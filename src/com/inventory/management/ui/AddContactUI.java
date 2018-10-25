package com.inventory.management.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.PrivilageSetupDao;
import com.inventory.management.dao.ContactDao;
import com.inventory.management.model.ContactCategoryModel;
import com.inventory.management.model.ContactModel;
import com.inventory.management.model.MailModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 30, 2013
 */
public class AddContactUI extends SparkLogic {

	private static final long serialVersionUID = -5031646742764410240L;

	private static final String TBL_SLNO = "#";
	private static final String TBL_ID = "Id";
	private static final String TBL_DATE = "Date";
	private static final String TBL_SUBJECT = "Subject";
	private static final String TBL_CONTENT = "Content";

	private SComboField contactComboField;

	private STextField contactTextField;
	private STextField contactPersonTextField;
	private STextField mobileTextField;
	private STextField locationTextField;
	private STextArea addressTextArea;
	private STextField emailField;
	private STextField webField;
	private STextField faxField;
	private SComboField categoryComboField;

	SRadioButton criteriaRadio;
	SRadioButton typeRadio;
	SDateField date;

	private SFormLayout formLayout;
	private SHorizontalLayout horizontalLayout;
	private SHorizontalLayout mainLayout;
	private SFormLayout tableFormLayout;

	private SPanel mainPanel;

	private SButton saveButton;
	private SButton editButton;
	private SButton deleteButton;
	private SButton updateButton;
	private SButton cancelButton;

	private SCollectionContainer bic;
	private ContactDao objDao;

	private SButton createNewButton;

	private SButton newMailButton;

	private STable emailTable;
	private STextField subjectField;
	private RichTextArea mailArea;
	private SButton sendMailButton;
	private SFormLayout mailLayout;
	private SFormLayout popupLayout;
	private SFormLayout popConentLayout;

	private SDialogBox dialogBox;
	private SimpleDateFormat format;

	private SLabel subjectLabel;
	private SLabel dateLabel;
	private SLabel contentLabel;

	private SFileUpload fileUpload;
	private SFileUploder uploader;
	private SLabel attachLabel;
	private SButton removeAttachButton;

	SComboField createdByUser;

	private SMail mail;

	private STokenField toField;
	private SButtonLink attachLink;
	private FileDownloader downloader;

	private Object[] allHeaders;
	private Object[] reqHeaders;

	private SimpleDateFormat df;

	@Override
	public SPanel getGUI() {

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Create new");

		try {

			List testList = null;

			if (isSuperAdmin()) {
				testList = new UserManagementDao().getAllLoginNames();
			} else if (isSystemAdmin()) {
				testList = new UserManagementDao()
						.getAllLoginNamesWithoutSparkAdmin();
			} else {
				testList = new UserManagementDao()
						.getAllLoginsForOrg(getOrganizationID());
			}

			boolean avail = true;
			if (!isSuperAdmin() && !isSystemAdmin()) {
				avail = new PrivilageSetupDao().isOptionsAvailToUser(
						getOfficeID(),
						SConstants.privilegeTypes.ADD_CONTACT_FOR_OTHERS,
						getLoginID());
			} else
				avail = true;

			createdByUser = new SComboField(getPropertyName("created_user"),
					300, testList, "id", "login_name");

			createdByUser.setValue(getUserID());

			if (!avail)
				createdByUser.setVisible(false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		criteriaRadio = new SRadioButton(null, 200, Arrays.asList(new KeyValue(
				(int) 0, getPropertyName("all")), new KeyValue((int) 1,getPropertyName("supplier")),
				new KeyValue((int) 2, getPropertyName("customer"))), "intKey", "value");

		typeRadio = new SRadioButton(getPropertyName("type"), 200,
				Arrays.asList(new KeyValue((int) 1, getPropertyName("supplier")), new KeyValue(
						(int) 2, getPropertyName("customer"))), "intKey", "value");
		criteriaRadio.setHorizontal(true);
		typeRadio.setHorizontal(true);

		format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		df = new SimpleDateFormat("ddMMyyHHmmss");
		mail = new SMail();

		setSize(1050, 650);

		objDao = new ContactDao();

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		tableFormLayout = new SFormLayout();
		tableFormLayout.setSizeFull();
		tableFormLayout.setMargin(true);
		tableFormLayout.setSpacing(true);

		mainLayout = new SHorizontalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		horizontalLayout = new SHorizontalLayout();
		horizontalLayout.setMargin(true);
		horizontalLayout.setSpacing(true);

		formLayout = new SFormLayout();
		formLayout.setSizeFull();
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		mailLayout = new SFormLayout();
		mailLayout.setSizeFull();
		mailLayout.setMargin(true);
		mailLayout.setSpacing(true);

		popConentLayout = new SFormLayout();
		popConentLayout.setSizeFull();
		popConentLayout.setMargin(true);
		popConentLayout.setSpacing(true);

		popupLayout = new SFormLayout();

		contactComboField = new SComboField(null, 300, null, "id", "name");
		contactComboField
				.setInputPrompt(getPropertyName("create_new"));

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("contact"));
		salLisrLay.addComponent(contactComboField);
		salLisrLay.addComponent(createNewButton);

		contactTextField = new STextField(getPropertyName("company_name"), 300);
		contactPersonTextField = new STextField(
				getPropertyName("contact_person"), 300);

		mobileTextField = new STextField(getPropertyName("mobile"), 300);

		locationTextField = new STextField(getPropertyName("location"), 300);

		emailField = new STextField(getPropertyName("email"), 300);
		faxField = new STextField(getPropertyName("fax"), 300);
		webField = new STextField(getPropertyName("web"), 300);

		date = new SDateField(getPropertyName("date"), 150, getDateFormat(),
				getWorkingDate());

		addressTextArea = new STextArea(getPropertyName("address"));
		addressTextArea.setWidth("300px");
		addressTextArea.setHeight("50px");

		categoryComboField = new SComboField(getPropertyName("category"), 300,
				null, "value", "name");
		categoryComboField
				.setInputPrompt(getPropertyName("select"));
		categoryComboField.setWidth("300px");

		formLayout.addComponent(criteriaRadio);
		formLayout.addComponent(salLisrLay);
		formLayout.addComponent(contactTextField);
		formLayout.addComponent(contactPersonTextField);

		formLayout.addComponent(typeRadio);
		formLayout.addComponent(categoryComboField);
		formLayout.addComponent(date);
		formLayout.addComponent(createdByUser);
		formLayout.addComponent(mobileTextField);
		formLayout.addComponent(locationTextField);
		formLayout.addComponent(addressTextArea);
		formLayout.addComponent(emailField);
		formLayout.addComponent(webField);
		formLayout.addComponent(faxField);

		saveButton = new SButton(getPropertyName("save"));
		editButton = new SButton(getPropertyName("edit"));
		deleteButton = new SButton(getPropertyName("delete"));
		updateButton = new SButton(getPropertyName("update"));
		cancelButton = new SButton(getPropertyName("cancel"));

		editButton.setVisible(false);
		deleteButton.setVisible(false);
		updateButton.setVisible(false);
		cancelButton.setVisible(false);

		horizontalLayout.addComponent(saveButton);
		horizontalLayout.addComponent(editButton);
		horizontalLayout.addComponent(deleteButton);
		horizontalLayout.addComponent(updateButton);
		horizontalLayout.addComponent(cancelButton);

		formLayout.addComponent(horizontalLayout);

		// Email part starts here

		try {

			allHeaders = new Object[] { TBL_SLNO, TBL_ID, TBL_DATE,
					TBL_SUBJECT, TBL_CONTENT };
			reqHeaders = new Object[] { TBL_SLNO, TBL_DATE, TBL_SUBJECT,
					TBL_CONTENT };

			emailTable = new STable(null, 500, 350);
			emailTable.addContainerProperty(TBL_SLNO, Integer.class, null,
					TBL_SLNO, null, Align.CENTER);
			emailTable.addContainerProperty(TBL_ID, Long.class, null, TBL_ID,
					null, Align.CENTER);
			emailTable.addContainerProperty(TBL_DATE, String.class, null,
					getPropertyName("date"), null, Align.CENTER);
			emailTable.addContainerProperty(TBL_SUBJECT, String.class, null,
					getPropertyName("subject"), null, Align.LEFT);
			emailTable.addContainerProperty(TBL_CONTENT, String.class, null,
					getPropertyName("content"), null, Align.LEFT);

			emailTable.setSelectable(true);
			emailTable.setColumnWidth(TBL_SLNO, 20);
			emailTable.setColumnWidth(TBL_DATE, 120);
			emailTable.setColumnWidth(TBL_SUBJECT, 80);
			emailTable.setColumnWidth(TBL_CONTENT, 225);

			emailTable.setVisibleColumns(reqHeaders);

			newMailButton = new SButton(getPropertyName("new_mail"));
			newMailButton.setIcon(new ThemeResource("icons/newmail.png"));
			newMailButton.setStyleName("deletebtnStyle");
			sendMailButton = new SButton("Send Mail");
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");

			dialogBox = new SDialogBox("New Mail", 800, 700);
			dialogBox.setModal(true);

			toField = new STokenField("To");
			toField.setInputPrompt("Select");
			toField.setStyleName(STokenField.STYLE_TOKENFIELD);
			toField.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);
			toField.setWidth("650");
			toField.setInputWidth("150");
			loadToAddress();

			subjectField = new STextField("Subject", 600);
			subjectField.setMaxLength(550);
			mailArea = new RichTextArea();
			mailArea.setCaption(null);
			mailArea.setWidth("600px");
			mailArea.setHeight("350px");

			SHorizontalLayout attachLayout = new SHorizontalLayout();
			attachLayout.setSpacing(true);
			uploader = new SFileUploder();
			fileUpload = new SFileUpload(null, uploader);
			fileUpload.setImmediate(true);
			fileUpload.markAsDirty();
			fileUpload.setButtonCaption("Attach a file");
			attachLabel = new SLabel();
			removeAttachButton = new SButton(
					getPropertyName("remove_attachment"));

			attachLayout.addComponent(fileUpload);
			attachLayout.addComponent(removeAttachButton);
			attachLayout.addComponent(attachLabel);

			mailLayout.addComponent(toField);
			mailLayout.addComponent(subjectField);
			mailLayout.addComponent(mailArea);
			mailLayout.addComponent(attachLayout);
			mailLayout.addComponent(sendMailButton);

			dialogBox.setContent(mailLayout);

			tableFormLayout.addComponent(popupLayout);
			tableFormLayout.addComponent(emailTable);
			tableFormLayout.addComponent(newMailButton);

			subjectLabel = new SLabel("Subject : ");
			dateLabel = new SLabel("Date : ");
			contentLabel = new SLabel("Content : ");
			contentLabel.setContentMode(ContentMode.HTML);
			attachLink = new SButtonLink("Download Attachment");
			attachLink.setVisible(false);

			downloader = new FileDownloader(new FileResource(new File("")));
			downloader.extend(attachLink);

			popConentLayout.addComponent(dateLabel);
			popConentLayout.addComponent(subjectLabel);
			popConentLayout.addComponent(contentLabel);
			popConentLayout.addComponent(attachLink);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Mail ends here

		mainLayout.addComponent(formLayout);
		mainLayout.addComponent(tableFormLayout);

		mainPanel.setContent(mainLayout);

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

		newMailButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (contactComboField.getValue() != null
						&& !contactComboField.getValue().equals("")
						&& !contactComboField.getValue().toString().equals("0")
						&& isValidEmail(emailField.getValue())) {
					getUI().addWindow(dialogBox);
					subjectField.setValue("");
					mailArea.setValue("");
					removeAttachButton.click();
					emailField.setComponentError(null);
					toField.setValue(null);
					loadToAddress();
				} else {
					setRequiredError(emailField, "Invalid email", true);
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
					MailModel mailModel = null;
					List mailList = new ArrayList();
					boolean isSale = false;

					Set<Long> mailSet = (Set<Long>) toField.getValue();

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

						Iterator it = mailSet.iterator();
						int i = 0;
						long contactId = 0;
						while (it.hasNext()) {
							try {
								contactId = (Long) it.next();
								emailAddr = new InternetAddress(objDao
										.getEmailId(contactId));
								ads[i] = emailAddr;

								i++;

								mailModel = new MailModel();
								mailModel.setContact_id(contactId);
								mailModel.setContent(mailArea.getValue()
												.toString());
								mailModel.setDate(CommonUtil
										.getCurrentDateTime());
								mailModel.setSend_by(getLoginID());
								mailModel.setStatus(1);
								mailModel.setSubject(subjectField.getValue());
								mailModel.setAttachment(fileName);

								mailList.add(mailModel);

							} catch (AddressException e) {
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						try {

							if (toInt(typeRadio.getValue().toString()) == 1)
								isSale = false;
							else
								isSale = true;

							mail.sendSparkMail(ads,
									mailArea.getValue(),
									subjectField.getValue(), file, isSale);

							try {
								objDao.saveMail(mailList);

								if (file != null && file.exists()) {
									String dir = VaadinServlet.getCurrent()
											.getServletContext()
											.getRealPath("/")
											+ "VAADIN/themes/testappstheme/Docs/Attachments/"
											+ fileName;
									System.out.println("File     " + dir);
									FileUtils.copyFile(file, new File(dir));
								}

								SNotification.show("Mail sent successfully",
										Type.WARNING_MESSAGE);
								getUI().removeWindow(dialogBox);
								loadEmailTable((Long) contactComboField
										.getValue());
							} catch (Exception e) {
								SNotification.show("Mail sending failed",
										Type.ERROR_MESSAGE);
								e.printStackTrace();
								sendMailButton.setEnabled(true);
							}
						} catch (Exception e1) {
							SNotification.show("Mail sending failed",
									Type.ERROR_MESSAGE);
							sendMailButton.setEnabled(true);
						}
					} else {
						setRequiredError(toField, "Select email ids", true);
					}
				} else {
					setRequiredError(mailArea, "Enter contents", true);
				}
			}
		});

		emailTable.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				Item itm = (Item) emailTable.getItem(emailTable.getValue());
				if (itm != null) {
					dateLabel.setValue(itm.getItemProperty(TBL_DATE).getValue()
							.toString());
					subjectLabel.setValue(itm.getItemProperty(TBL_SUBJECT)
							.getValue().toString());
					contentLabel.setValue(itm.getItemProperty(TBL_CONTENT)
							.getValue().toString());

					File file;
					try {
						file = new File(
								VaadinServlet.getCurrent().getServletContext()
										.getRealPath("/")
										+ "VAADIN/themes/testappstheme/Docs/Attachments/"
										+ objDao.getEmailAttachment((toLong(itm
												.getItemProperty(TBL_ID)
												.getValue().toString()))));

						if (file != null && file.exists() && file.isFile()) {
							downloader
									.setFileDownloadResource(new FileResource(
											file));
							attachLink.setVisible(true);
						} else {
							downloader
									.setFileDownloadResource(new FileResource(
											new File("")));
							attachLink.setVisible(false);
						}
					} catch (Exception e) {
						downloader.setFileDownloadResource(new FileResource(
								new File("")));
						attachLink.setVisible(false);
					}

					SPopupView pop = new SPopupView(null, popConentLayout);
					popupLayout.removeAllComponents();
					popupLayout.addComponent(pop);
					pop.setPopupVisible(true);
				} else {
					dateLabel.setValue("");
					subjectLabel.setValue("");
					contentLabel.setValue("");
				}
			}
		});

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				contactComboField.setValue((long) 0);
			}
		});

		criteriaRadio.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					loadOptions(0);

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		typeRadio.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (typeRadio.getValue() != null) {
						bic = SCollectionContainer.setList(objDao
								.getCategories((Integer) typeRadio.getValue(),
										getOrganizationID()), "id");
						categoryComboField.setContainerDataSource(bic);
						categoryComboField.setItemCaptionPropertyId("name");

					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		saveButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {

					if (contactComboField.getValue() == null
							|| contactComboField.getValue().toString()
									.equals("0")) {

						if (isValid()) {

							ContactModel objModel = new ContactModel();

							objModel.setName(contactTextField.getValue());
							objModel.setContact_person(contactPersonTextField
									.getValue());

							objModel.setAddress(addressTextArea.getValue());
							objModel.setCategory(new ContactCategoryModel(Long
									.parseLong(categoryComboField.getValue()
											.toString())));
							objModel.setMobile(mobileTextField.getValue());
							objModel.setAddress(addressTextArea.getValue());
							objModel.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objModel.setLocation(locationTextField.getValue());
							objModel.setEmail(emailField.getValue());
							objModel.setFax(faxField.getValue());
							objModel.setWebsite(webField.getValue());
							objModel.setStatus(1);
							objModel.setType((Integer) typeRadio.getValue());

							if (createdByUser.getValue() != null)
								objModel.setLogin(new S_LoginModel(
										(Long) createdByUser.getValue()));
							else
								objModel.setLogin(new S_LoginModel(getLoginID()));

							objModel.setAdded_by(getLoginID());

							try {

								setWritableAll();
								long id = objDao.save(objModel);
								loadOptions(id);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								setReadOnlyAll();

								saveButton.setVisible(false);
								editButton.setVisible(true);
								deleteButton.setVisible(true);
								updateButton.setVisible(false);
								cancelButton.setVisible(false);

							} catch (Exception e) {
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		editButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					saveButton.setVisible(false);
					editButton.setVisible(false);
					deleteButton.setVisible(false);
					updateButton.setVisible(true);
					cancelButton.setVisible(true);
					setWritableAll();

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		cancelButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					saveButton.setVisible(false);
					editButton.setVisible(true);
					deleteButton.setVisible(true);
					updateButton.setVisible(false);
					cancelButton.setVisible(false);
					loadOptions(Long.parseLong(contactComboField.getValue()
							.toString()));

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		deleteButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											long id = Long
													.parseLong(contactComboField
															.getValue()
															.toString());
											objDao.delete(id);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

											loadOptions(0);

											saveButton.setVisible(true);
											editButton.setVisible(false);
											deleteButton.setVisible(false);
											updateButton.setVisible(false);
											cancelButton.setVisible(false);

										} catch (Exception e) {
											Notification.show(
													getPropertyName("error"),
													Type.WARNING_MESSAGE);
											e.printStackTrace();
										}

									}
								}
							});

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		updateButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if (contactComboField.getValue() != null) {

						if (isValid()) {

							ContactModel objModel = objDao
									.getContact((Long) contactComboField
											.getValue());

							objModel.setName(contactTextField.getValue());
							objModel.setContact_person(contactPersonTextField
									.getValue());
							objModel.setAddress(addressTextArea.getValue());
							objModel.setCategory(new ContactCategoryModel(Long
									.parseLong(categoryComboField.getValue()
											.toString())));
							objModel.setMobile(mobileTextField.getValue());
							objModel.setAddress(addressTextArea.getValue());
							objModel.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objModel.setLocation(locationTextField.getValue());
							objModel.setEmail(emailField.getValue());
							objModel.setFax(faxField.getValue());
							objModel.setWebsite(webField.getValue());
							objModel.setType((Integer) typeRadio.getValue());

							if (createdByUser.getValue() != null)
								objModel.setLogin(new S_LoginModel(
										(Long) createdByUser.getValue()));
							else
								objModel.setLogin(new S_LoginModel(getLoginID()));

							objModel.setAdded_by(getLoginID());

							try {
								objDao.update(objModel);
								loadOptions(objModel.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);

								saveButton.setVisible(false);
								editButton.setVisible(true);
								deleteButton.setVisible(true);
								updateButton.setVisible(false);
								cancelButton.setVisible(false);

							} catch (Exception e) {
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
		//
		// addShortcutListener(new ShortcutListener("Add New contact",
		// ShortcutAction.KeyCode.N,
		// new int[] { ShortcutAction.ModifierKey.ALT }) {
		// @Override
		// public void handleAction(Object sender, Object target) {
		// loadOptions(0);
		// }
		// });

		/*
		 * addShortcutListener(new ShortcutListener("Save",
		 * ShortcutAction.KeyCode.ENTER, null) {
		 * 
		 * @Override public void handleAction(Object sender, Object target) { if
		 * (saveButton.isVisible()) saveButton.click(); else
		 * updateButton.click(); } });
		 */

		contactComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {

					emailField.setComponentError(null);
					if (contactComboField.getValue() != null
							&& !contactComboField.getValue().toString()
									.equals("0")) {

						saveButton.setVisible(false);
						editButton.setVisible(true);
						deleteButton.setVisible(true);
						updateButton.setVisible(false);
						cancelButton.setVisible(false);

						ContactModel objModel = objDao
								.getContact((Long) contactComboField.getValue());

						setWritableAll();

						date.setValue(CommonUtil.getUtilFromSQLDate(objModel
								.getDate()));
						locationTextField.setValue(objModel.getLocation());
						typeRadio.setValue(objModel.getType());
						contactTextField.setValue(objModel.getName());
						contactPersonTextField.setValue(objModel
								.getContact_person());
						mobileTextField.setValue(objModel.getMobile());
						addressTextArea.setValue(objModel.getAddress());
						faxField.setValue(objModel.getFax());
						emailField.setValue(objModel.getEmail());
						webField.setValue(objModel.getWebsite());
						categoryComboField.setValue(objModel.getCategory()
								.getId());

						createdByUser.setValue(objModel.getLogin().getId());

						loadEmailTable((Long) contactComboField.getValue());
						setReadOnlyAll();

					} else {
						saveButton.setVisible(true);
						editButton.setVisible(false);
						deleteButton.setVisible(false);
						updateButton.setVisible(false);
						cancelButton.setVisible(false);

						setWritableAll();
						// organizationComboField.select(getOrganizationID());
						contactTextField.setValue("");
						contactPersonTextField.setValue("");
						mobileTextField.setValue("");
						addressTextArea.setValue("");
						categoryComboField.setValue(1);
						locationTextField.setValue("");
						faxField.setValue("");
						emailField.setValue("");
						webField.setValue("");

						loadEmailTable(0);

					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		criteriaRadio.setValue(0);
		typeRadio.setValue(1);

		return mainPanel;

	}

	private void loadToAddress() {
		try {
			SCollectionContainer bic = SCollectionContainer.setList(
					objDao.getAllEmailIds(getLoginID()), "id");
			toField.setContainerDataSource(bic);
			toField.setTokenCaptionPropertyId("name");

			Set<Long> usersSet = new HashSet<Long>();
			usersSet.add((Long) contactComboField.getValue());
			toField.setNewValue(usersSet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void loadEmailTable(long contactId) {
		try {
			emailTable.setVisibleColumns(allHeaders);
			emailTable.removeAllItems();
			MailModel mailModel = null;
			List mailList = objDao.getSentMails(contactId);
			Iterator itr = mailList.iterator();
			int index = 1;
			Object[] row = null;

			while (itr.hasNext()) {
				mailModel = (MailModel) itr.next();
				row = new Object[] { index, mailModel.getId(),
						format.format(mailModel.getDate()),
						mailModel.getSubject(), mailModel.getContent() };
				emailTable.addItem(row, index);
				index++;
			}
			emailTable.setVisibleColumns(reqHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean isValidEmail(String value) {
		boolean ret = true;
		if (value == null || value.equals("")) {
			ret = false;
		} else {
			try {
				InternetAddress emailAddr = new InternetAddress(value);
				emailAddr.validate();
			} catch (Exception ex) {
				ret = false;
			}
		}
		return ret;
	}

	public void loadOptions(long id) {
		List testList;
		try {

			testList = objDao.getAllContacts(getLoginID(),
					(Integer) criteriaRadio.getValue());
			ContactModel model = new ContactModel();
			model.setId(0);
			model.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, model);

			contactComboField
					.setInputPrompt("------------------- Create New -------------------");

			bic = SCollectionContainer.setList(testList, "id");
			contactComboField.setContainerDataSource(bic);
			contactComboField.setItemCaptionPropertyId("name");

			contactComboField.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void setWritableAll() {
		contactTextField.setReadOnly(false);
		contactPersonTextField.setReadOnly(false);
		mobileTextField.setReadOnly(false);
		addressTextArea.setReadOnly(false);
		categoryComboField.setReadOnly(false);
		emailField.setReadOnly(false);
		faxField.setReadOnly(false);
		webField.setReadOnly(false);
		date.setReadOnly(false);
		locationTextField.setReadOnly(false);
		typeRadio.setReadOnly(false);
		createdByUser.setReadOnly(false);
	}

	protected void setReadOnlyAll() {
		contactTextField.setReadOnly(true);
		contactPersonTextField.setReadOnly(true);
		mobileTextField.setReadOnly(true);
		addressTextArea.setReadOnly(true);
		categoryComboField.setReadOnly(true);
		emailField.setReadOnly(true);
		faxField.setReadOnly(true);
		webField.setReadOnly(true);
		date.setReadOnly(true);
		locationTextField.setReadOnly(true);
		typeRadio.setReadOnly(true);
		createdByUser.setReadOnly(true);
	}

	@Override
	public Boolean isValid() {

		boolean flag = true;

		if (mobileTextField.getValue() == null
				|| mobileTextField.getValue().equals("")) {
			setRequiredError(mobileTextField, getPropertyName("enter_mobile"),
					true);
			flag = false;
		} else {
			try {
				toDouble(mobileTextField.getValue());
				mobileTextField.setComponentError(null);
			} catch (Exception e) {
				setRequiredError(mobileTextField,
						getPropertyName("enter_valid_mobile"), true);
				flag = false;
			}
		}

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("select_date"), true);
			flag = false;
		} else
			date.setComponentError(null);

		if (categoryComboField.getValue() == null
				|| categoryComboField.getValue().equals("")) {
			setRequiredError(categoryComboField,
					getPropertyName("select_category"), true);
			flag = false;
		} else
			categoryComboField.setComponentError(null);

		if (contactTextField.getValue() == null
				|| contactTextField.getValue().equals("")) {
			setRequiredError(contactTextField,
					getPropertyName("enter_contact_name"), true);
			flag = false;
		} else
			contactTextField.setComponentError(null);

		if (!isValidEmail(emailField.getValue())) {
			setRequiredError(emailField, getPropertyName("enter_email"), true);
			flag = false;
		} else
			emailField.setComponentError(null);

		return flag;
	}
	
	
	
	public SComboField getContactComboField() {
		return contactComboField;
	}

	public void setContactComboField(SComboField contactComboField) {
		this.contactComboField = contactComboField;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
