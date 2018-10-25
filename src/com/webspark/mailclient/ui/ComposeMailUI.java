package com.webspark.mailclient.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.vaadin.tokenfield.DemoRoot.Contact;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.dao.MailDao;
import com.webspark.mailclient.model.EmailConfigurationModel;
import com.webspark.mailclient.model.MyMailsModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date 10/03/2014
 */
public class ComposeMailUI extends SparkLogic {

	private static final long serialVersionUID = -1830134231919939537L;

	private WrappedSession session;
	private MailDao daoObj;

	private STextField subjectField;
	private RichTextArea mailArea;
	private SButton sendMailButton;
	private SFormLayout mailLayout;

	private STokenField toField;
	private SButtonLink attachLink;
	private FileDownloader downloader;
	private SFileUpload fileUpload;
	private SFileUploder uploader;
	private SLabel attachLabel;
	private SButton removeAttachButton;

	private SMail mail;

	SHorizontalLayout mainHoriz;

	UserModel user = null;

	SettingsValuePojo settings;

	EmailConfigurationModel emainlConfig;

	private SimpleDateFormat df;

	public ComposeMailUI() {
		setSize(1100, 600);

		SPanel panel = new SPanel();

		mail = new SMail();

		df = new SimpleDateFormat("ddMMYYHHmmss");

		session = getHttpSession();
		daoObj = new MailDao();

		mainHoriz = new SHorizontalLayout();

		try {

			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");

			user = new UserManagementDao().getUser(getLoginID());

			sendMailButton = new SButton(getPropertyName("send_mail"));
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");

			toField = new STokenField(getPropertyName("to")) {
				protected void onTokenInput(Object tokenId) {
					Set<Object> set = (Set<Object>) getValue();
					Contact c = new Contact("", tokenId.toString());
					if (set != null && set.contains(c)) {
						// duplicate
					} else {
						try {
							new InternetAddress(tokenId.toString()).validate();
							addToken(tokenId);
						} catch (Exception e) {
							SNotification.show(getPropertyName("invalid_data"),
									Type.TRAY_NOTIFICATION);
							// TODO: handle exception
						}
					}
				}
			};
			toField.setInputPrompt("Select");
			toField.setStyleName(STokenField.STYLE_TOKENFIELD);
			toField.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);
			toField.setWidth("970");
			toField.setInputWidth("200");

			SCollectionContainer bic = SCollectionContainer.setList(new CommonMethodsDao().getEmailsAsKeyValueObject(getLoginID(),getOfficeID()),"stringKey");
			toField.setContainerDataSource(bic);
			toField.setTokenCaptionPropertyId("value");

			subjectField = new STextField(getPropertyName("subject"), 970);
			subjectField.setMaxLength(550);
			mailArea = new RichTextArea();
			mailArea.setCaption(null);
			mailArea.setWidth("970px");
			mailArea.setHeight("350px");

			SHorizontalLayout attachLayout = new SHorizontalLayout();
			attachLayout.setSpacing(true);
			uploader = new SFileUploder();
			fileUpload = new SFileUpload(null, uploader);
			fileUpload.setImmediate(true);
			fileUpload.markAsDirty();
			fileUpload.setButtonCaption(getPropertyName("attach_a_file"));
			attachLabel = new SLabel();
			removeAttachButton = new SButton(
					getPropertyName("remove_attachment"));

			mailLayout = new SFormLayout();
			mailLayout.setSizeFull();
			mailLayout.setMargin(true);
			mailLayout.setSpacing(true);

			attachLayout.addComponent(fileUpload);
			attachLayout.addComponent(removeAttachButton);
			attachLayout.addComponent(attachLabel);
			mailLayout.addComponent(toField);
			mailLayout.addComponent(subjectField);
			mailLayout.addComponent(mailArea);
			mailLayout.addComponent(attachLayout);
			mailLayout.addComponent(sendMailButton);

			SLabel subjectLabel = new SLabel(getPropertyName("subject") + " :");
			SLabel dateLabel = new SLabel(getPropertyName("date") + " :");
			SLabel contentLabel = new SLabel(getPropertyName("content") + " :");
			contentLabel.setContentMode(ContentMode.HTML);
			attachLink = new SButtonLink(getPropertyName("download"));
			attachLink.setVisible(false);

			downloader = new FileDownloader(new FileResource(new File("")));
			downloader.extend(attachLink);

			setContent(mailLayout);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		fileUpload.addListener(new Listener() {

			@Override
			public void componentEvent(Event event) {
				if (uploader.getFile() != null) {
					attachLabel.setValue(uploader.getFile().getName());
				} else
					attachLabel.setValue("");
			}
		});

		removeAttachButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (uploader.getFile() != null) {
					attachLabel.setValue("");
					uploader.deleteFile();
				}
			}
		});

		sendMailButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				toField.setComponentError(null);
				mailArea.setComponentError(null);

				if (mailArea.getValue() != null
						&& mailArea.getValue().toString().trim().length() != 0) {

					InternetAddress emailAddr = null;
					List mailList = new ArrayList();
					boolean isSale = false;

					Set mailSet = (Set) toField.getValue();

					File file = null;
					String fileName = "";
					if (uploader.getFile() != null) {
						file = uploader.getFile();
						fileName = String.valueOf(
								df.format(CommonUtil.getCurrentDateTime()))
								.trim()
								+ file.getName().replaceAll(" ", "");

						if (fileName.length() > 499) {
							fileName = fileName.substring((fileName.length() - 498));
						}
					}

					if (mailSet != null && mailSet.size() > 0) {

						Address[] ads = new Address[mailSet.size()];
						String toMail = "";
						Iterator it = mailSet.iterator();
						int i = 0;
						long contactId = 0;
						while (it.hasNext()) {
							try {
								emailAddr = new InternetAddress(it.next()
										.toString());
								ads[i] = emailAddr;
								toMail += emailAddr.toString() + ",";
								i++;

							} catch (AddressException e) {
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						try {

							// if(settings.isUSE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL())
							// {
							// mail.sendMailFromSystemMail(ads,
							// CommonUtil.removeHtml(mailArea.getValue()),
							// subjectField.getValue(), file,"");
							// } else {
							mail.sendMailWithFromAddress(ads,
									mailArea.getValue(),
									subjectField.getValue(), file, getLoginID());
							// }

							new MyMailsModel(getLoginID(), 1, 2, 0, toMail,
									subjectField.getValue(), CommonUtil
											.getCurrentDateTime(), false, false);

							try {

								if (file != null && file.exists()) {
									String dir = VaadinServlet.getCurrent()
											.getServletContext()
											.getRealPath("/")
											+ "VAADIN/themes/testappstheme/Docs/Attachments/"
											+ fileName;
									System.out.println("File     " + dir);
									FileUtils.copyFile(file, new File(dir));
								}

								SNotification
										.show(getPropertyName("mail_sent_successfully"),
												Type.WARNING_MESSAGE);

								new MyEmailsUI().syncronizeMail("Sent");

							} catch (Exception e) {
								SNotification.show(
										getPropertyName("mail_sent_failed"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
								sendMailButton.setEnabled(true);
							}
						} catch (Exception e1) {
							SNotification.show(
									getPropertyName("mail_sent_failed"),
									Type.ERROR_MESSAGE);
							sendMailButton.setEnabled(true);
						}
					} else {
						setRequiredError(toField,
								getPropertyName("invalid_selection"), true);
					}
				} else {
					setRequiredError(mailArea, getPropertyName("invalid_data"),
							true);
				}
			}
		});

	}

	public ComposeMailUI(String email) {
		setSize(1100, 600);

		SPanel panel = new SPanel();

		mail = new SMail();

		df = new SimpleDateFormat("ddMMYYHHmmss");

		session = getHttpSession();
		daoObj = new MailDao();

		mainHoriz = new SHorizontalLayout();
		setCaption("New Email");

		try {

			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");

			user = new UserManagementDao().getUser(getLoginID());

			sendMailButton = new SButton(getPropertyName("send_mail"));
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");

			toField = new STokenField(getPropertyName("to")) {
				protected void onTokenInput(Object tokenId) {
					Set<Object> set = (Set<Object>) getValue();
					Contact c = new Contact("", tokenId.toString());
					if (set != null && set.contains(c)) {
						// duplicate
					} else {
						try {
							new InternetAddress(tokenId.toString()).validate();
							addToken(tokenId);
						} catch (Exception e) {
							SNotification.show(getPropertyName("invalid_data"),
									Type.TRAY_NOTIFICATION);
							// TODO: handle exception
						}
					}
				}
			};
			toField.setInputPrompt("Select");
			toField.setStyleName(STokenField.STYLE_TOKENFIELD);
			toField.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);
			toField.setWidth("970");
			toField.setInputWidth("200");

			SCollectionContainer bic = SCollectionContainer.setList(
					new CommonMethodsDao()
							.getAllEmailsAsKeyValueObject(getLoginID()),
					"stringKey");
			toField.setContainerDataSource(bic);
			toField.setTokenCaptionPropertyId("value");

			toField.addToken(email);

			subjectField = new STextField(getPropertyName("subject"), 970);
			subjectField.setMaxLength(550);
			mailArea = new RichTextArea();
			mailArea.setCaption(null);
			mailArea.setWidth("970px");
			mailArea.setHeight("350px");

			SHorizontalLayout attachLayout = new SHorizontalLayout();
			attachLayout.setSpacing(true);
			uploader = new SFileUploder();
			fileUpload = new SFileUpload(null, uploader);
			fileUpload.setImmediate(true);
			fileUpload.markAsDirty();
			fileUpload.setButtonCaption(getPropertyName("attach_a_file"));
			attachLabel = new SLabel();
			removeAttachButton = new SButton(
					getPropertyName("remove_attachment"));

			mailLayout = new SFormLayout();
			mailLayout.setSizeFull();
			mailLayout.setMargin(true);
			mailLayout.setSpacing(true);

			attachLayout.addComponent(fileUpload);
			attachLayout.addComponent(removeAttachButton);
			attachLayout.addComponent(attachLabel);
			mailLayout.addComponent(toField);
			mailLayout.addComponent(subjectField);
			mailLayout.addComponent(mailArea);
			mailLayout.addComponent(attachLayout);
			mailLayout.addComponent(sendMailButton);

			SLabel subjectLabel = new SLabel(getPropertyName("subject") + " :");
			SLabel dateLabel = new SLabel(getPropertyName("date") + " :");
			SLabel contentLabel = new SLabel(getPropertyName("content") + " :");
			contentLabel.setContentMode(ContentMode.HTML);
			attachLink = new SButtonLink(getPropertyName("download"));
			attachLink.setVisible(false);

			downloader = new FileDownloader(new FileResource(new File("")));
			downloader.extend(attachLink);

			setContent(mailLayout);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		fileUpload.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				if (uploader.getFile() != null) {
					attachLabel.setValue(uploader.getFile().getName());
				} else
					attachLabel.setValue("");
			}
		});

		removeAttachButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (uploader.getFile() != null) {
					attachLabel.setValue("");
					uploader.deleteFile();
				}
			}
		});

		sendMailButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				toField.setComponentError(null);
				mailArea.setComponentError(null);

				if (mailArea.getValue() != null
						&& mailArea.getValue().toString().trim().length() != 0) {

					InternetAddress emailAddr = null;
					List mailList = new ArrayList();
					boolean isSale = false;

					Set mailSet = (Set) toField.getValue();

					File file = null;
					String fileName = "";
					if (uploader.getFile() != null) {
						file = uploader.getFile();
						fileName = String.valueOf(
								df.format(CommonUtil.getCurrentDateTime()))
								.trim()
								+ file.getName().replaceAll(" ", "");

						if (fileName.length() > 499) {
							fileName = fileName.substring((fileName.length() - 498));
						}
					}

					if (mailSet != null && mailSet.size() > 0) {

						Address[] ads = new Address[mailSet.size()];
						String toMail = "";
						Iterator it = mailSet.iterator();
						int i = 0;
						long contactId = 0;
						while (it.hasNext()) {
							try {
								emailAddr = new InternetAddress(it.next()
										.toString());
								ads[i] = emailAddr;
								toMail += emailAddr.toString() + ",";
								i++;

							} catch (AddressException e) {
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						try {

							if (settings
									.isUSE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL()) {
								mail.sendMailFromSystemMail(ads, mailArea.getValue(),
										subjectField.getValue(), file, "");
							} else {
								mail.sendMailWithFromAddress(ads, mailArea.getValue(),
										subjectField.getValue(), file,
										getLoginID());
							}
							
							try {
								
								if (file != null && file.exists()) {
									String dir = VaadinServlet.getCurrent()
											.getServletContext()
											.getRealPath("/")
											+ "VAADIN/themes/testappstheme/Docs/Attachments/"
											+ fileName;
									System.out.println("File     " + dir);
									FileUtils.copyFile(file, new File(dir));
								}

								SNotification
										.show(getPropertyName("mail_sent_successfully"),
												Type.WARNING_MESSAGE);

								new MyEmailsUI().syncronizeMail("Sent");

								closeWindow();

							} catch (Exception e) {
								SNotification.show(
										getPropertyName("mail_sent_failed"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
								sendMailButton.setEnabled(true);
							}
						} catch (Exception e1) {
							SNotification.show(
									getPropertyName("mail_sent_failed"),
									Type.ERROR_MESSAGE);
							sendMailButton.setEnabled(true);
						}
					} else {
						setRequiredError(toField,
								getPropertyName("invalid_selection"), true);
					}
				} else {
					setRequiredError(mailArea, getPropertyName("invalid_data"),
							true);
				}
			}
		});

	}

	public void closeWindow() {
		this.close();
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
