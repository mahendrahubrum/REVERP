package com.inventory.management.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.PrivilageSetupDao;
import com.inventory.management.dao.ContactDao;
import com.inventory.management.model.ContactCategoryModel;
import com.inventory.management.model.ContactModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.mailclient.ui.ComposeMailUI;
import com.webspark.mailclient.ui.ShowEmailsUI;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 30, 2013
 */
public class AddContactUsingUserMailUI extends SparkLogic {

	private static final long serialVersionUID = -5031646742764410240L;

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

	SWindow sendWindow, showWindow;

	private SButton showMails;

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

	private SButton sendMailButton;

	private STextField subjectField;
	private RichTextArea mailArea;
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

			createdByUser = new SComboField("Created User :", 300, testList,
					"id", "login_name");

			createdByUser.setValue(getUserID());

			if (!avail)
				createdByUser.setVisible(false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		criteriaRadio = new SRadioButton(null, 200, Arrays.asList(new KeyValue(
				(int) 0, "ALL"), new KeyValue((int) 1, "Supplier"),
				new KeyValue((int) 2, "Customer")), "intKey", "value");

		typeRadio = new SRadioButton("Type", 200, Arrays.asList(new KeyValue(
				(int) 1, "Supplier"), new KeyValue((int) 2, "Customer")),
				"intKey", "value");
		criteriaRadio.setHorizontal(true);
		typeRadio.setHorizontal(true);

		format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		df = new SimpleDateFormat("ddMMYYHHmmss");
		mail = new SMail();

		setSize(800, 650);

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
				.setInputPrompt("------------------- Create New -------------------");

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
				.setInputPrompt("------------------- Select -------------------");
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

		sendMailButton = new SButton(getPropertyName("new_mail"));
		sendMailButton.setIcon(new ThemeResource("icons/newmail.png"));
		sendMailButton.setStyleName("deletebtnStyle");

		showMails = new SButton(getPropertyName("show_mails"));
		showMails.setIcon(new ThemeResource("icons/sendmail.png"));
		showMails.setStyleName("deletebtnStyle");

		horizontalLayout.addComponent(saveButton);
		horizontalLayout.addComponent(editButton);
		horizontalLayout.addComponent(deleteButton);
		horizontalLayout.addComponent(updateButton);
		horizontalLayout.addComponent(cancelButton);
		horizontalLayout.addComponent(sendMailButton);
		horizontalLayout.addComponent(showMails);

		formLayout.addComponent(horizontalLayout);

		// Email part starts here

		try {

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

			sendMailButton.setVisible(false);
			showMails.setVisible(false);

			dialogBox.setContent(mailLayout);

			tableFormLayout.addComponent(popupLayout);

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

		sendMailButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidEmail(emailField.getValue())) {
					try {
						sendWindow = new ComposeMailUI(emailField.getValue());
						sendWindow.center();
						sendWindow.setModal(true);
						getUI().addWindow(sendWindow);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					SNotification.show(getPropertyName("invalid_email"),
							Type.WARNING_MESSAGE);
				}
			}
		});

		showMails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValidEmail(emailField.getValue())) {
					try {
						showWindow = new ShowEmailsUI(emailField.getValue());
						showWindow.center();
						showWindow.setModal(true);
						getUI().addWindow(showWindow);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					SNotification.show(getPropertyName("invalid_email"),
							Type.WARNING_MESSAGE);
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

						setReadOnlyAll();

						sendMailButton.setVisible(true);
						showMails.setVisible(true);

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

						sendMailButton.setVisible(false);
						showMails.setVisible(false);
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

	@Override
	public Boolean getHelp() {
		return null;
	}

}
