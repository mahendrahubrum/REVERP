package com.webspark.mailclient.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.tokenfield.DemoRoot.Contact;

import com.inventory.common.dao.CommonMethodsDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupDateField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.bean.EmailDetailsBean;
import com.webspark.mailclient.biz.GetMail;
import com.webspark.mailclient.dao.EmailConfigDao;
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
public class MyEmailsUI extends SparkLogic {

	private static final long serialVersionUID = -1830134231919939537L;

	private static final String TBL_ID = "ID";
	private static final String TBL_SINO = "#";
	private static final String TBL_FROM = "From / To";
	private static final String TBL_TITLE = "Title";
	private static final String TBL_DETAILS = "Details";
	private static final String TBL_DATETIME = "Date & Time";

	private STable table;
	String[] reqHeaders;

	String last_folder = "Inbox";

	private STextField titleField;
	private STextArea detailsField;

	private SPopupDateField datetimeField;
	private WrappedSession session;
	private MailDao daoObj;

	private SButton save;
	private SButton update;
	private SButton delete, replyButton, forwardButton, sendRply, sendFwd;
	private boolean syncFlag = false;

	private STextField subjectField;
	private RichTextArea mailArea, detailsTxtField;
	private SButton sendMailButton;
	private SFormLayout mailLayout;

	private STokenField toField, fwdAddresses;
	private SButtonLink attachLink;
	private FileDownloader downloader;
	private SDialogBox dialogBox;
	private SFileUpload fileUpload;
	private SFileUploder uploader;
	private SLabel attachLabel;
	private SButton removeAttachButton;

	private SMail mail;

	SLabel dateTimeLabel;

	SWindow showWindow;
	STextArea fromText, subjectText;

	SFormLayout rplyForm;

	SVerticalLayout iconLink;
	SHorizontalLayout mainHoriz, replyBtnLay;

	UserModel user = null;

	EmailConfigurationModel emainlConfig;

	private SimpleDateFormat df;

	@Override
	public SPanel getGUI() {
		setSize(1100, 580);

		showWindow = new SWindow(null, 1100, 640);
		showWindow.setCloseShortcut(KeyCode.ESCAPE);

		SPanel panel = new SPanel();

		mail = new SMail();

		dateTimeLabel = new SLabel(getPropertyName("date_n_time"));

		replyButton = new SButton(getPropertyName("reply"));
		forwardButton = new SButton(getPropertyName("forward"));
		sendRply = new SButton(getPropertyName("send"));
		sendFwd = new SButton(getPropertyName("forward_mail"));

		replyBtnLay = new SHorizontalLayout(replyButton, forwardButton,
				sendRply, sendFwd);
		sendRply.setVisible(false);
		sendFwd.setVisible(false);
		replyButton.setVisible(true);
		forwardButton.setVisible(true);

		df = new SimpleDateFormat("ddMMYYHHmmss");

		session = getHttpSession();
		daoObj = new MailDao();

		mainHoriz = new SHorizontalLayout();

		iconLink = new SVerticalLayout();

		iconLink.setWidth("150");
		iconLink.setStyleName("testBtnNew");
		iconLink.setSpacing(true);

		SFormLayout composeBtn = new SFormLayout();
		composeBtn.addComponent(new SLabel(null, getPropertyName("compose"),"btn_caption_new"));
		composeBtn.setStyleName("testBtnNew");
		composeBtn.setId("Compose");
		iconLink.addComponent(composeBtn);

		SFormLayout syncBtn = new SFormLayout();
		syncBtn.addComponent(new SLabel(null, getPropertyName("sync"),"btn_caption_new"));
		syncBtn.setWidth("150");
		syncBtn.setStyleName("testBtnNew");
		syncBtn.setId("Sync");
		iconLink.addComponent(syncBtn);

		SFormLayout homeBtn = new SFormLayout();
		homeBtn.addComponent(new SLabel(null, getPropertyName("inbox"),
				"btn_caption_new"));
		homeBtn.setWidth("150");
		homeBtn.setStyleName("selectedBtnNew");
		homeBtn.setId("Inbox");
		iconLink.addComponent(homeBtn);

		SFormLayout btn1 = new SFormLayout();
		btn1.addComponent(new SLabel(null, getPropertyName("drafts"),
				"btn_caption_new"));
		btn1.setWidth("150");
		btn1.setStyleName("testBtnNew");
		btn1.setId("Drafts");
		iconLink.addComponent(btn1);

		SFormLayout sendItm = new SFormLayout();
		sendItm.addComponent(new SLabel(null, getPropertyName("sent_mail"),
				"btn_caption_new"));
		sendItm.setWidth("150");
		sendItm.setStyleName("testBtnNew");
		sendItm.setId("Sent Mail");
		iconLink.addComponent(sendItm);

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		SVerticalLayout verticalLayout = new SVerticalLayout();
		verticalLayout.setMargin(true);
		verticalLayout.setSpacing(true);

		final SFormLayout personalLayout = new SFormLayout();
		personalLayout.setStyleName("layout_border");

		reqHeaders = new String[] { TBL_SINO, TBL_TITLE, TBL_DETAILS,
				TBL_DATETIME };

		table = new STable(null, 400, 400);
		table.addContainerProperty(TBL_ID, Long.class, null, TBL_ID,
				null, Align.CENTER);
		table.addContainerProperty(TBL_SINO, Integer.class, null, TBL_SINO,
				null, Align.CENTER);
		table.addContainerProperty(TBL_FROM, String.class, null,
				getPropertyName("from/to"), null, Align.CENTER);
		table.addContainerProperty(TBL_TITLE, String.class, null,
				getPropertyName("title"), null, Align.LEFT);
		table.addContainerProperty(TBL_DETAILS, String.class, null,
				getPropertyName("details"), null, Align.LEFT);
		table.addContainerProperty(TBL_DATETIME, Timestamp.class, null,
				getPropertyName("date_n_time"), null, Align.CENTER);
		table.setSelectable(true);
		table.setMultiSelect(true);

		table.setColumnExpandRatio(TBL_SINO, (float) 0.3);
		table.setColumnExpandRatio(TBL_TITLE, (float) 1);
		table.setColumnExpandRatio(TBL_DETAILS, 2);
		table.setColumnExpandRatio(TBL_DATETIME, (float) 1.5);

		table.setWidth("1120");
		table.setHeight("550");

		try {

			user = new UserManagementDao().getUser(getLoginID());

			sendMailButton = new SButton(getPropertyName("send_mail"));
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");

			dialogBox = new SDialogBox(getPropertyName("new_mail"), 800, 700);
			dialogBox.setModal(true);

			toField = new STokenField("To") {
				@SuppressWarnings("unchecked")
				protected void onTokenInput(Object tokenId) {
					Set<Object> set = (Set<Object>) getValue();
					Contact c = new Contact("", tokenId.toString());
					if (set != null && set.contains(c)) {
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
			toField.setWidth("650");
			toField.setInputWidth("150");

			SCollectionContainer bic = SCollectionContainer.setList(new CommonMethodsDao().getEmailsAsKeyValueObject(getLoginID(),getOfficeID()),"stringKey");
			toField.setContainerDataSource(bic);
			toField.setTokenCaptionPropertyId("value");

			fwdAddresses = new STokenField(getPropertyName("to")) {
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
					fwdAddresses.setComponentError(null);
				}
			};

			fwdAddresses.setInputPrompt("Select");
			fwdAddresses.setStyleName(STokenField.STYLE_TOKENFIELD);
			fwdAddresses.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);
			fwdAddresses.setWidth("650");
			fwdAddresses.setInputWidth("150");
			fwdAddresses.setContainerDataSource(bic);
			fwdAddresses.setTokenCaptionPropertyId("value");
			fwdAddresses.setInvalidAllowed(true);

			subjectField = new STextField(getPropertyName("subject"), 600);
			subjectField.setMaxLength(550);
			mailArea = new RichTextArea();
			mailArea.setCaption(null);
			mailArea.setWidth("600px");
			mailArea.setHeight("350px");

			detailsTxtField = new RichTextArea();
			detailsTxtField.setCaption(getPropertyName("details"));
			detailsTxtField.setWidth("980px");
			detailsTxtField.setHeight("400px");

			fromText = new STextArea(getPropertyName("from/to"), 980, 40);
			subjectText = new STextArea(getPropertyName("subject"), 980, 40);

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

			dialogBox.setContent(mailLayout);

			SLabel subjectLabel = new SLabel(getPropertyName("subject") + " : ");
			SLabel dateLabel = new SLabel(getPropertyName("date") + " : ");
			SLabel contentLabel = new SLabel(getPropertyName("content") + " : ");
			contentLabel.setContentMode(ContentMode.HTML);
			attachLink = new SButtonLink(getPropertyName("download"));
			attachLink.setVisible(false);

			downloader = new FileDownloader(new FileResource(new File("")));
			downloader.extend(attachLink);

			titleField = new STextField(getPropertyName("title"), 200);
			detailsField = new STextArea(getPropertyName("details"), 200);
			datetimeField = new SPopupDateField(getPropertyName("date_n_time"));
			datetimeField.setValue(new Date());

			personalLayout.addComponent(titleField);
			personalLayout.addComponent(detailsField);
			personalLayout.addComponent(datetimeField);

			save = new SButton(getPropertyName("Save"), "ENTER");
			update = new SButton(getPropertyName("Update"), "ENTER");
			update.setVisible(false);
			delete = new SButton(getPropertyName("Delete"), "ALT+DEL");
			delete.setVisible(false);
			delete.setClickShortcut(KeyCode.DELETE, ModifierKey.ALT);

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);

			SHorizontalLayout hor = new SHorizontalLayout();
			hor.addComponent(table);
			hor.setSpacing(true);
			hor.setComponentAlignment(table, Alignment.MIDDLE_RIGHT);

			verticalLayout.addComponent(hor);
			verticalLayout.addComponent(buttonLayout);
			verticalLayout.setComponentAlignment(buttonLayout,
					Alignment.MIDDLE_LEFT);
			verticalLayout.setComponentAlignment(hor, Alignment.MIDDLE_CENTER);

			buttonLayout.setStyleName("addtask_btnLayout");

			mainHoriz.addComponent(iconLink);
			mainHoriz.addComponent(table);

			panel.setContent(mainHoriz);

			verticalLayout.setStyleName("common_page_style_todolist");
			verticalLayout.setWidth("1300");

			emainlConfig = new EmailConfigDao()
					.getEmailConfiguration(getLoginID());
			if (emainlConfig != null) {
				loadList("Inbox");
			} else {
				SNotification.show(getPropertyName("mail_config_msg"),
						Type.ERROR_MESSAGE);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		fwdAddresses.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub

			}
		});

		final Action actionDelete = new Action("Delete");

		table.addActionHandler(new Action.Handler() {
			@Override
			public Action[] getActions(final Object target, final Object sender) {
				// if(deleteItemButton.isVisible())
				// deleteItemButton.click();
				return new Action[] { actionDelete };
			}

			@Override
			public void handleAction(final Action action, final Object sender,
					final Object target) {
				deleteItem();
			}

		});

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

							mail.sendMailWithFromAddress(ads,
									mailArea.getValue(),
									subjectField.getValue(), file, getLoginID());

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

								syncronizeMail("Sent");

								SNotification
										.show(getPropertyName("mail_sent_successfully"),
												Type.TRAY_NOTIFICATION);
								getUI().removeWindow(dialogBox);
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

		iconLink.addLayoutClickListener(new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				// TODO Auto-generated method stub

				if (event.getChildComponent().getId().equals("Sync")) {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											syncronizeMail();

										} catch (Exception e) {
											e.printStackTrace();
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
										}
									}
								}
							});

					loadList(last_folder);
					event.getChildComponent().setStyleName("testBtnNew");
				} else if (event.getChildComponent().getId().equals("Compose")) {
					getUI().addWindow(dialogBox);
					subjectField.setValue("");
					mailArea.setValue("");
					removeAttachButton.click();
					toField.setValue(null);
				} else {
					loadList(event.getChildComponent().getId());
					last_folder = event.getChildComponent().getId();
				}

				clearSelectButtons();

				event.getChildComponent().setStyleName("selectedBtnNew");

			}
		});

		replyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				try {
					fwdAddresses.setValue(null);
					ArrayList emailIdList = new ArrayList();
					Pattern p = Pattern.compile(
							"\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",
							Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(fromText.getValue());
					while (m.find()) {
						if (m.group().length() > 3)
							fwdAddresses.addToken(m.group());
					}

					removeAttachButton.click();

				} catch (Exception e) {
					Notification.show(getPropertyName("Error"),
							getPropertyName("issue_occured") + e.getCause(),
							Type.ERROR_MESSAGE);
					// TODO: handle exception
				}

				fwdAddresses.setVisible(true);
				replyButton.setVisible(false);
				forwardButton.setVisible(false);
				sendRply.setVisible(true);
				sendFwd.setVisible(false);

				if (last_folder.equals("Sent Mail"))
					detailsTxtField.setValue("<br/><br/><br/><br/><br/> On "
							+ dateTimeLabel.getValue() + ", "
							+ emainlConfig.getUsername() + " wrote: <br/>"
							+ detailsTxtField.getValue());
				else
					detailsTxtField.setValue("<br/><br/><br/><br/><br/> On "
							+ dateTimeLabel.getValue() + ", "
							+ fromText.getValue() + " wrote: <br/>"
							+ detailsTxtField.getValue());

				subjectText.setValue("Re: " + subjectText.getValue());

			}
		});

		forwardButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				fwdAddresses.setVisible(true);
				replyButton.setVisible(false);
				forwardButton.setVisible(false);
				sendRply.setVisible(true);
				sendFwd.setVisible(false);
				fwdAddresses.setValue(null);
				if (last_folder.equals("Sent Mail"))
					detailsTxtField
							.setValue("---------- Forwarded message ---------- <br/>From: "
									+ emainlConfig.getUsername()
									+ "</br>Date: "
									+ dateTimeLabel.getValue()
									+ "<br/>Subject: "
									+ subjectText.getValue()
									+ "<br/>To: "
									+ fromText.getValue()
									+ "<br/><br/>" + detailsTxtField.getValue());
				else
					detailsTxtField
							.setValue("---------- Forwarded message ---------- <br/>From: "
									+ fromText.getValue()
									+ "</br>Date: "
									+ dateTimeLabel.getValue()
									+ "<br/>Subject: "
									+ subjectText.getValue()
									+ "<br/>To: "
									+ emainlConfig.getUsername()
									+ "<br/><br/>" + detailsTxtField.getValue());

				subjectText.setValue("Fwd: " + subjectText.getValue());

			}
		});

		ClickListener rplyLsnr = new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				fwdAddresses.setComponentError(null);
				detailsTxtField.setComponentError(null);

				if (detailsTxtField.getValue() != null
						&& detailsTxtField.getValue().toString().trim()
								.length() != 0) {

					InternetAddress emailAddr = null;
					List mailList = new ArrayList();
					boolean isSale = false;

					Set mailSet = (Set) fwdAddresses.getValue();

					File file = null;
					String fileName = "";
					if (uploader.getFile() != null) {
						file = uploader.getFile();
						fileName = String.valueOf(
								df.format(CommonUtil.getCurrentDateTime()))
								.trim()
								+ file.getName().replaceAll(" ", "");

						if (fileName.length() > 499) {
							fileName = fileName
									.substring((fileName.length() - 498));
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
							System.out.println("Mail Det :"
									+ detailsTxtField.getValue());
							mail.sendMailWithFromAddress(ads,
									detailsTxtField.getValue(),
									subjectText.getValue(), file, getLoginID());

							new MyMailsModel(getLoginID(), 1, 2, 0, toMail,
									subjectText.getValue(),
									CommonUtil.getCurrentDateTime(), false,
									false);

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
												Type.TRAY_NOTIFICATION);
								getUI().removeWindow(dialogBox);

								syncronizeMail("Sent");

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
						setRequiredError(fwdAddresses,
								getPropertyName("invalid_selection"), true);
					}
				} else {
					setRequiredError(detailsTxtField,
							getPropertyName("invalid_data"), true);
				}
			}
		};
		sendRply.addClickListener(rplyLsnr);
		sendFwd.addClickListener(rplyLsnr);
		
		table.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearError();
				if (table.getValue() != null && !table.getValue().equals("")) {
					try {
						
						Collection selectedItems = (Collection) table
								.getValue();
						
						if (selectedItems.size() == 1) {
							
							Object obj=selectedItems.iterator().next();
							
							OpenMail(obj);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					titleField.setValue("");
					detailsField.setValue("");
					datetimeField.setValue(new Date());

					save.setVisible(true);
					update.setVisible(false);
					delete.setVisible(false);
				}

				showWindow.focus();
			}

			
		});

		mainHoriz.addLayoutClickListener(new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				// TODO Auto-generated method stub

				if (!syncFlag) {
					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											syncronizeMail();
											syncFlag = true;
										} catch (Exception e) {
											e.printStackTrace();
											Notification
													.show(getPropertyName("Error"),
															getPropertyName("issue_occured")
																	+ e.getCause(),
															Type.ERROR_MESSAGE);
										}
									} else {
										syncFlag = true;
									}
								}
							});
				}

			}
		});

		return panel;
	}

	GetMail emailObj;

	private void loadList(String option) {
		try {

			last_folder = option;

			emailObj = new GetMail(getLoginID());

			List list = null;
			reqHeaders = new String[] { TBL_SINO, TBL_FROM, TBL_TITLE,
					TBL_DETAILS, TBL_DATETIME };
			table.setVisibleColumns(new String[] {TBL_ID, TBL_SINO, TBL_FROM, TBL_TITLE,
					TBL_DETAILS, TBL_DATETIME });
			table.removeAllItems();

			int folder_id = 0;
			if (option.equals("Inbox")) {
				list = daoObj.getEmails(getLoginID(), 1, 1);
				folder_id = 1;
			} else if (option.equals("Sent Mail")) {
				list = daoObj.getEmails(getLoginID(), 1, 2);
				folder_id = 2;
			} else if (option.equals("Drafts")) {
				list = daoObj.getEmails(getLoginID(), 1, 3);
				folder_id = 3;
			}

			String details = "";
			int ct = 1;
			Iterator it = list.iterator();
			while (it.hasNext()) {
				MyMailsModel objModel = (MyMailsModel) it.next();
				details = "";

				details = reaDetailsFile(getLoginID() + "_1_" + folder_id + "_"
						+ objModel.getMail_number() + ".txt");

				table.addItem(
						new Object[] {objModel.getId(), ct, objModel.getEmails(),
								objModel.getSubject(), details,
								objModel.getDate_time() }, objModel.getId());

				ct++;
			}
			table.setVisibleColumns(reqHeaders);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void syncronizeMail() {
		try {

			emailObj = new GetMail(getLoginID());

			List list = null;
			table.removeAllItems();

			List mailSaveList = new ArrayList();

			long last_no = daoObj.getLastMailNumber(getLoginID(), 1, 1);
			Iterator it = emailObj.getInBoxMails(getLoginID(), last_no)
					.iterator();
			while (it.hasNext()) {
				EmailDetailsBean objModel = (EmailDetailsBean) it.next();
				mailSaveList.add(new MyMailsModel(getLoginID(), 1, 1, objModel
						.getMessage_no(), objModel.getFrom(), objModel
						.getSubject(), CommonUtil
						.getTimestampFromUtilDate(objModel.getDate()), false,
						true));

				createDetailsFile(objModel.getDetails(), getLoginID() + "_1_1_"
						+ objModel.getMessage_no() + ".txt");
			}

			it = null;
			last_no = daoObj.getLastMailNumber(getLoginID(), 1, 2);
			it = emailObj.getOutBoxMails(getLoginID(), last_no).iterator();
			while (it.hasNext()) {
				EmailDetailsBean objModel = (EmailDetailsBean) it.next();
				mailSaveList.add(new MyMailsModel(getLoginID(), 1, 2, objModel
						.getMessage_no(), objModel.getFrom(), objModel
						.getSubject(), CommonUtil
						.getTimestampFromUtilDate(objModel.getDate()), false,
						false));
				createDetailsFile(objModel.getDetails(), getLoginID() + "_1_2_"
						+ objModel.getMessage_no() + ".txt");
			}

			it = null;
			last_no = daoObj.getLastMailNumber(getLoginID(), 1, 3);
			it = emailObj.getDraftMails(getLoginID(), last_no).iterator();
			while (it.hasNext()) {
				EmailDetailsBean objModel = (EmailDetailsBean) it.next();
				mailSaveList.add(new MyMailsModel(getLoginID(), 1, 3, objModel
						.getMessage_no(), objModel.getFrom(), objModel
						.getSubject(), CommonUtil
						.getTimestampFromUtilDate(objModel.getDate()), false,
						false));
				createDetailsFile(objModel.getDetails(), getLoginID() + "_1_3_"
						+ objModel.getMessage_no() + ".txt");
			}

			daoObj.save(mailSaveList);

			loadList(last_folder);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void syncronizeMail(String folder) {
		try {

			emailObj = new GetMail(getLoginID());

			List mailSaveList = new ArrayList();

			Iterator it = null;
			long last_no = 0;
			if (folder.equals("Inbox")) {
				last_no = daoObj.getLastMailNumber(getLoginID(), 1, 1);
				it = emailObj.getInBoxMails(getLoginID(), last_no).iterator();
				while (it.hasNext()) {
					EmailDetailsBean objModel = (EmailDetailsBean) it.next();
					mailSaveList.add(new MyMailsModel(getLoginID(), 1, 1,
							objModel.getMessage_no(), objModel.getFrom(),
							objModel.getSubject(), CommonUtil
									.getTimestampFromUtilDate(objModel
											.getDate()), false, true));

					createDetailsFile(objModel.getDetails(), getLoginID()
							+ "_1_1_" + objModel.getMessage_no() + ".txt");
				}
			} else if (folder.equals("Sent")) {
				it = null;
				last_no = daoObj.getLastMailNumber(getLoginID(), 1, 2);
				it = emailObj.getOutBoxMails(getLoginID(), last_no).iterator();
				while (it.hasNext()) {
					EmailDetailsBean objModel = (EmailDetailsBean) it.next();
					mailSaveList.add(new MyMailsModel(getLoginID(), 1, 2,
							objModel.getMessage_no(), objModel.getFrom(),
							objModel.getSubject(), CommonUtil
									.getTimestampFromUtilDate(objModel
											.getDate()), false, false));
					createDetailsFile(objModel.getDetails(), getLoginID()
							+ "_1_2_" + objModel.getMessage_no() + ".txt");
				}
			} else {
				it = null;
				last_no = daoObj.getLastMailNumber(getLoginID(), 1, 3);
				it = emailObj.getDraftMails(getLoginID(), last_no).iterator();
				while (it.hasNext()) {
					EmailDetailsBean objModel = (EmailDetailsBean) it.next();
					mailSaveList.add(new MyMailsModel(getLoginID(), 1, 3,
							objModel.getMessage_no(), objModel.getFrom(),
							objModel.getSubject(), CommonUtil
									.getTimestampFromUtilDate(objModel
											.getDate()), false, false));
					createDetailsFile(objModel.getDetails(), getLoginID()
							+ "_1_3_" + objModel.getMessage_no() + ".txt");
				}
			}

			daoObj.save(mailSaveList);

			loadList(last_folder);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		clearError();
		boolean valid = true;

		if (datetimeField.getValue() == null
				|| datetimeField.getValue().equals("")) {
			setRequiredError(datetimeField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else
			setRequiredError(datetimeField, null, false);

		if (titleField.getValue() == null || titleField.getValue().equals("")) {
			setRequiredError(titleField, getPropertyName("invalid_data"), true);
			valid = false;
		} else
			setRequiredError(titleField, null, false);

		return valid;
	}

	private void clearError() {
		titleField.setComponentError(null);
		detailsField.setComponentError(null);
	}

	public void clearSelectButtons() {
		int a = iconLink.getComponentCount();
		for (int i = 0; i < a; i++) {
			iconLink.getComponent(i).setStyleName("testBtnNew");
		}
	}

	public void createDetailsFile(String details, String fileName) {
		try {
			File file = new File(VaadinServlet.getCurrent().getServletContext()
					.getRealPath("/")
					+ "Mails/" + fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(details);
			output.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String reaDetailsFile(String fileName) {
		String details = "";
		try {
			FileInputStream fisTargetFile = new FileInputStream(new File(
					VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")
							+ "Mails/" + fileName));

			details = IOUtils.toString(fisTargetFile, "UTF-8");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return details;
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										Collection selectedItems = (Collection) table
												.getValue();

										if (selectedItems.size() > 0) {
											daoObj.deleteMails(selectedItems);
										}

										Notification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);
										loadList(last_folder);

									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(
												getPropertyName("Error"),
												Type.ERROR_MESSAGE);
									}
								}
							}
						});

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public void OpenMail(Object obj)  {
		
		try {
			
		if (last_folder.equals("Drafts")) {

			Item item = table.getItem(obj);

			fromText.setNewValue(item.getItemProperty(
					TBL_FROM).getValue()
					+ "");
			subjectText.setValue(item.getItemProperty(
					TBL_TITLE).getValue()
					+ "");
			detailsTxtField.setValue(item.getItemProperty(
					TBL_DETAILS).getValue()
					+ "");
			
			removeAttachButton.click();
			dateTimeLabel.setValue(item.getItemProperty(
					TBL_DATETIME).getValue()
					+ "");
			rplyForm = new SFormLayout(dateTimeLabel,
					fwdAddresses, fromText, subjectText,
					detailsTxtField, new SHorizontalLayout(
							fileUpload, removeAttachButton,
							attachLabel), replyBtnLay);
			showWindow.setContent(rplyForm);
			fwdAddresses.setVisible(false);
			
			fromText.setReadOnly(true);
			
			getUI().addWindow(showWindow);
			showWindow.setModal(true);
			replyButton.setVisible(true);
			forwardButton.setVisible(true);
			replyButton.setVisible(false);
			forwardButton.setVisible(false);
			sendRply.setVisible(true);
			sendFwd.setVisible(false);
			
		} else {
			
			
			Item item = table.getItem(obj);
			
			fromText.setNewValue(item.getItemProperty(
					TBL_FROM).getValue()
					+ "");
			subjectText.setValue(item.getItemProperty(
					TBL_TITLE).getValue()
					+ "");
			detailsTxtField.setValue(item.getItemProperty(
					TBL_DETAILS).getValue()
					+ "");

			removeAttachButton.click();
			dateTimeLabel.setValue(item.getItemProperty(
					TBL_DATETIME).getValue()
					+ "");
			rplyForm = new SFormLayout(dateTimeLabel,
					fwdAddresses, fromText, subjectText,
					detailsTxtField, new SHorizontalLayout(
							fileUpload, removeAttachButton,
							attachLabel), replyBtnLay);
			showWindow.setContent(rplyForm);
			fwdAddresses.setVisible(false);
			
			fromText.setReadOnly(true);

			getUI().addWindow(showWindow);
			showWindow.setModal(true);
			
			daoObj.markAsReaded((Long) obj);

			replyButton.setVisible(true);
			forwardButton.setVisible(true);

			replyButton.setVisible(true);
			forwardButton.setVisible(true);
			sendRply.setVisible(false);
			sendFwd.setVisible(false);
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setMail(Object obj)  {
		
		Set<Long> val=new HashSet<Long>();
		val.add(Long.parseLong((String)obj));
		
		table.setValue(val);
	}

}
