package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.PaymentTermsDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.model.PaymentTermsModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SEncryption;
import com.webspark.common.util.SMail;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.mailclient.ui.ComposeMailUI;
import com.webspark.mailclient.ui.ShowEmailsUI;
import com.webspark.model.AddressModel;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_UserRoleModel;

public class AddSupplier extends SparkLogic {

	private static final long serialVersionUID = -6589343450720444589L;

	long id;

	SPanel mainPanel;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField supplierListCombo;
	STextField supplierNameField;
	SComboField statusCombo;
	
	SComboField responsibleEmployeeCombo;

	SAddressField address1Field;
	
	SWindow sendWindow, showWindow;
	
	private SButton sendMailButton, showMails;

	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;

	STextField supplierCodeTextField;
	SNativeSelect currency;
	STextField bank_nameTextField;
	STextField websiteTextField;
	SNativeSelect payment_terms;
	STextField credit_limitTextField;
	STextField credit_periodTextField;
	STextArea description;

	STextField contact_person;
	STextField contact_person_fax;
	STextField contact_person_email;

	private SCheckBox enableLogin,subscription;
	private STextField userNameField;
	private STextField vatNumberField;
	private SPasswordField passwordField;
	private SPasswordField confirmPasswordField;
	private SCheckBox sendMailBox;
	private UserManagementDao userDao;

	private SMail smail;

	WrappedSession session;
	SettingsValuePojo settings;
	
	
	SButton createNewButton;

	SupplierDao objDao = new SupplierDao();

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mainPanel.setSizeFull();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(1200, 640);
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		if(settings.getSUPPLIER_GROUP()!=0){

		objDao = new SupplierDao();
		userDao = new UserManagementDao();

		smail = new SMail();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {
			
			sendMailButton = new SButton(getPropertyName("send_mail"));
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");
			
			showMails= new SButton(getPropertyName("show_mails"));
			showMails.setIcon(new ThemeResource("icons/sendmail.png"));
			showMails.setStyleName("deletebtnStyle");

			supplierCodeTextField = new STextField(getPropertyName("supplier_code"), 250);
			currency = new SNativeSelect(getPropertyName("supplier_currency"), 150,
					new CurrencyManagementDao().getlabels(), "id", "name");
//			tax_group = new SNativeSelect(getPropertyName("tax_group"), 150,
//					new TaxGroupDao().getAllActiveTaxGroups(), "id", "name");
			bank_nameTextField = new STextField(getPropertyName("bank_name"), 250);
			websiteTextField = new STextField(getPropertyName("website"), 250);

			List ledgers = new LedgerDao()
					.getAllActiveLedgerNames(getOfficeID());

			contact_person = new STextField(getPropertyName("contact_person"), 250);
			contact_person_fax = new STextField(getPropertyName("contact_person_mobile"), 250);
			contact_person_email = new STextField(getPropertyName("contact_person_email"), 250);

			payment_terms = new SNativeSelect(getPropertyName("payment_terms"), 150,
					new PaymentTermsDao()
							.getAllActivePaymentTerms(getOrganizationID()),
					"id", "name");

			credit_limitTextField = new STextField(getPropertyName("credit_limit"), 150);
			credit_periodTextField = new STextField(getPropertyName("credit_period"), 150);
			description = new STextArea(getPropertyName("description"), 250,50);
			vatNumberField = new STextField(getPropertyName("vat_num_label"), 150);
			
			responsibleEmployeeCombo = new SComboField(getPropertyName("sales_man"), 250,
					new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()), "id", "first_name");
			responsibleEmployeeCombo.setInputPrompt(getPropertyName("select"));

			hLayout = new SHorizontalLayout();
			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			address1Field = new SAddressField(1);

			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			buttonLayout.addComponent(sendMailButton);
			buttonLayout.addComponent(showMails);
			buttonLayout.setSpacing(true);

			deleteButton.setVisible(false);
			updateButton.setVisible(false);

			supplierListCombo = new SComboField(null, 250, null, "id", "name", false, getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 150, SConstants.statuses.status, "key", "value");
			statusCombo.setInputPrompt(getPropertyName("select"));
			// groupCombo = new SComboField("Group", 250, new
			// GroupDao().getAllGroupsNames(getOrganizationID()), "id", "name"
			// , true, "Select");

			supplierNameField = new STextField(getPropertyName("supplier_name"), 250);


			SHorizontalLayout salLisrLay = new SHorizontalLayout(getPropertyName("supplier"));
			salLisrLay.addComponent(supplierListCombo);
			salLisrLay.addComponent(createNewButton);

			form.addComponent(salLisrLay);
			form.addComponent(supplierNameField);
			form.addComponent(supplierCodeTextField);
			// form.addComponent(groupCombo);
			// form.addComponent(address1Field);

			form.addComponent(bank_nameTextField);
			form.addComponent(websiteTextField);
			
			form.addComponent(responsibleEmployeeCombo);
			
			form.addComponent(contact_person);
			form.addComponent(contact_person_fax);
			form.addComponent(contact_person_email);
			form.addComponent(description);

			// form.setWidth("400");

			// form.addComponent(buttonLayout);
			enableLogin = new SCheckBox(getPropertyName("enable_login"));
			subscription=new SCheckBox(getPropertyName("enable_subscription"));
			userNameField = new STextField(getPropertyName("login_name"), 150);
			passwordField = new SPasswordField(getPropertyName("password"), 150);
			confirmPasswordField = new SPasswordField(getPropertyName("reenter_password"), 150);
			sendMailBox = new SCheckBox(getPropertyName("send_mail"));
			
			final SFormLayout accountLayout = new SFormLayout();
			accountLayout.setSizeFull();
			
			accountLayout.addComponent(credit_limitTextField);
			accountLayout.addComponent(credit_periodTextField);
			accountLayout.addComponent(payment_terms);
			accountLayout.addComponent(currency);
//			accountLayout.addComponent(tax_group);
			accountLayout.addComponent(statusCombo);
			accountLayout.addComponent(vatNumberField);
//			accountLayout.addComponent(subscription);
			accountLayout.addComponent(enableLogin);
			accountLayout.addComponent(userNameField);
			accountLayout.addComponent(passwordField);
			accountLayout.addComponent(confirmPasswordField);
			accountLayout.addComponent(sendMailBox);
			userNameField.setVisible(false);
			passwordField.setVisible(false);
			confirmPasswordField.setVisible(false);
			sendMailBox.setVisible(false);
			
			sendMailButton.setVisible(false);
			showMails.setVisible(false);
			
			
			SVerticalLayout addressLayout = new SVerticalLayout();
			addressLayout.setSizeFull();
			addressLayout.addComponent(address1Field);
		
			hLayout.addComponent(form);
			hLayout.addComponent(accountLayout);
			hLayout.addComponent(addressLayout);

			address1Field.setCaption(null);
			hLayout.setMargin(true);
			hLayout.setSpacing(true);

			vLayout.addComponent(hLayout);
			vLayout.addComponent(buttonLayout);

			vLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);
			mainPanel.setContent(vLayout);
			
			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});

			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});

			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					supplierListCombo.setValue((long) 0);
				}
			});

			
			enableLogin.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					
					if(enableLogin.getValue()!=null){
						if(enableLogin.getValue()){
							userNameField.setVisible(true);
							passwordField.setVisible(true);
							confirmPasswordField.setVisible(true);
							sendMailBox.setVisible(true);
						}
						else{
							userNameField.setVisible(false);
							passwordField.setVisible(false);
							confirmPasswordField.setVisible(false);
							sendMailBox.setVisible(false);
						}
					}
					else{
						userNameField.setVisible(false);
						passwordField.setVisible(false);
						confirmPasswordField.setVisible(false);
						sendMailBox.setVisible(false);
						userNameField.setValue("");
						passwordField.setValue("");
						confirmPasswordField.setValue("");
						sendMailBox.setValue(false);
					}
				}
			});
			
			
			sendMailButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValidEmail(address1Field.getEmailTextField().getValue())) {
						try {
							sendWindow=new ComposeMailUI(address1Field.getEmailTextField().getValue());
							sendWindow.center();
							sendWindow.setModal(true);
							getUI().addWindow(sendWindow);
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						SNotification.show(getPropertyName("invalid_email"),
								getPropertyName("invalid_email"),
								Type.WARNING_MESSAGE);
					}
				}
			});
			
			
			showMails.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValidEmail(address1Field.getEmailTextField().getValue())) {
						try {
							showWindow=new ShowEmailsUI(address1Field.getEmailTextField().getValue());
							showWindow.center();
							showWindow.setModal(true);
							getUI().addWindow(showWindow);
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						SNotification.show(getPropertyName("invalid_email"),
								getPropertyName("invalid_email"),
								Type.WARNING_MESSAGE);
					}
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (supplierListCombo.getValue() == null || supplierListCombo.getValue().toString().equals("0")) {
							boolean notExists=isNotExist();
	        				if(isValid() && notExists){
								if (!objDao.isCodeExists(getOfficeID(), supplierCodeTextField.getValue(), 0)) {

									AddressModel mdl= address1Field.getAddress();
									
									LedgerModel objModel = new LedgerModel();
									objModel.setName(supplierNameField.getValue());
									objModel.setGroup(new GroupModel(settings.getSUPPLIER_GROUP()));
									objModel.setCurrent_balance(0);
									objModel.setStatus((Long) statusCombo.getValue());
									objModel.setOffice(new S_OfficeModel(getOfficeID()));
									objModel.setType(SConstants.LEDGER_ADDED_INDIRECTLY);

									SupplierModel supplier = new SupplierModel();

									supplier.setName(supplierNameField.getValue());
									supplier.setSupplier_code(supplierCodeTextField.getValue());
									supplier.setBank_name(bank_nameTextField.getValue());
									supplier.setWebsite(websiteTextField.getValue());
									supplier.setResponsible_person((Long) responsibleEmployeeCombo.getValue());
									supplier.setContact_person(contact_person.getValue());
									supplier.setContact_person_fax(contact_person_fax.getValue());
									supplier.setContact_person_email(contact_person_email.getValue());
									supplier.setDescription(description.getValue());
									supplier.setCredit_limit(toDouble(credit_limitTextField.getValue()));
									supplier.setCredit_period(toInt(credit_periodTextField.getValue()));
									supplier.setPayment_terms(new PaymentTermsModel((Long) payment_terms.getValue()));
									supplier.setSupplier_currency(new CurrencyModel((Long) currency.getValue()));
									if(subscription.getValue())
										supplier.setSubscription(toLong("1"));
									else
										supplier.setSubscription(toLong("0"));
									supplier.setStatus((Long) statusCombo.getValue());
									supplier.setLedger(objModel);
									supplier.setLoginEnabled(enableLogin.getValue());
									supplier.setAddress(mdl);
									supplier.setVatNumber(vatNumberField.getValue());
									
									S_LoginModel loginModel = null;
									if (enableLogin.getValue()) {
										loginModel = new S_LoginModel();
										loginModel.setLogin_name(userNameField.getValue());
										loginModel.setPassword(SEncryption.encrypt(confirmPasswordField.getValue()));
										loginModel.setOffice(new S_OfficeModel(getOfficeID()));
										loginModel.setUserType(new S_UserRoleModel(SConstants.ROLE_SUPPLIER));
										loginModel.setStatus(0);
										supplier.setLogin(loginModel);
									}
									else
										supplier.setLogin(null);
									
									try {
										id = objDao.save(supplier, loginModel);
										if (sendMailBox.getValue()) {
											sendMail(supplier.getAddress().getEmail());
										}
										loadOptions(id);
										Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
									} catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
									setRequiredError(supplierCodeTextField,null, false);
								} 
								else {
									setRequiredError(supplierCodeTextField, getPropertyName("code_exist"), true);
								}
							} else if(!notExists){
								setRequiredError(userNameField,getPropertyName("username_exist"), true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			supplierListCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (supplierListCombo.getValue() != null && !supplierListCombo.getValue().toString().equals("0")) {
							saveButton.setVisible(false);
							deleteButton.setVisible(true);
							updateButton.setVisible(true);

							SupplierModel supplier = objDao.getSupplier((Long) supplierListCombo.getValue());

							supplierNameField.setValue(supplier.getName());
							supplierCodeTextField.setValue(supplier.getSupplier_code());
							session.setAttribute("oldCode", supplier.getSupplier_code());
							bank_nameTextField.setValue(supplier.getBank_name());
							websiteTextField.setValue(supplier.getWebsite());
							responsibleEmployeeCombo.setValue(supplier.getResponsible_person());
							contact_person.setValue(supplier.getContact_person());
							contact_person_fax.setValue(supplier.getContact_person_fax());
							contact_person_email.setValue(supplier.getContact_person_email());
							description.setValue(supplier.getDescription());
							credit_limitTextField.setValue(""+supplier.getCredit_limit());
							credit_periodTextField.setValue(asString(supplier.getCredit_period()));
							payment_terms.setValue(supplier.getPayment_terms().getId());
							currency.setValue(supplier.getSupplier_currency().getId());
							statusCombo.setValue(supplier.getStatus());
							if(supplier.getSubscription()==0)
								subscription.setValue(false);
							else if(supplier.getSubscription()==1)
								subscription.setValue(true);
							address1Field.loadAddress(supplier.getAddress().getId());
							enableLogin.setValue(supplier.isLoginEnabled());
							sendMailBox.setValue(false);
							vatNumberField.setValue(supplier.getVatNumber());
							if(supplier.getLogin()!=null){
								userNameField.setValue(supplier.getLogin().getLogin_name());
								session.setAttribute("userName", supplier.getLogin().getLogin_name());
								passwordField.setValue(SEncryption.decrypt(supplier.getLogin().getPassword()));
								confirmPasswordField.setValue(SEncryption.decrypt(supplier.getLogin().getPassword()));
							}
							sendMailButton.setVisible(true);
							showMails.setVisible(true);
						}
						else{
							saveButton.setVisible(true);
							deleteButton.setVisible(false);
							updateButton.setVisible(false);
							sendMailButton.setVisible(false);
							showMails.setVisible(false);
							
							supplierNameField.setValue("");
							supplierCodeTextField.setValue("");
							bank_nameTextField.setValue("");
							websiteTextField.setValue("");
							responsibleEmployeeCombo.setValue(null);
							contact_person.setValue("");
							contact_person_fax.setValue("");
							contact_person_email.setValue("");
							description.setValue("");
							credit_limitTextField.setValue("0");
							credit_periodTextField.setValue("0");
							payment_terms.setValue(null);
							currency.setValue(getCurrencyID());
							statusCombo.setValue((long)1);
							subscription.setValue(false);
							address1Field.clearAll();
							address1Field.getCountryComboField().setValue(getCountryID());
							enableLogin.setValue(false);
							sendMailBox.setValue(false);
							userNameField.setValue("");
							passwordField.setValue("");
							confirmPasswordField.setValue("");
							sendMailButton.setVisible(false);
							showMails.setVisible(false);
							session.setAttribute("userName", null);
							session.setAttribute("oldCode", null);
						}
						removeErrorMsgs();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			deleteButton.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										id = (Long) supplierListCombo.getValue();
										objDao.delete(id);
										Notification.show(getPropertyName("deleted_success"), Type.WARNING_MESSAGE);
										loadOptions(0);
									} catch (Exception e) {
										Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			updateButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (supplierListCombo.getValue() != null) {

							boolean notExist = true;
							if (enableLogin.getValue()) {
								if(session.getAttribute("userName")!=null){
									if (session.getAttribute("userName").toString().equals(userNameField.getValue())) {
										notExist = true;
									} else {
										notExist = isNotExist();
									}
								}
							}
							
							if (isValid() && notExist) {

								if (!objDao.isCodeExists(getOfficeID(), supplierCodeTextField.getValue(), (Long) supplierListCombo.getValue())) {

									SupplierModel supplier = objDao.getSupplier((Long) supplierListCombo.getValue());
									LedgerModel objModel = supplier.getLedger();
									AddressModel addr = address1Field.getAddress();
									addr.setId(supplier.getAddress().getId());

									objModel.setName(supplierNameField.getValue());
									objModel.setGroup(new GroupModel(settings.getSUPPLIER_GROUP()));
									objModel.setStatus((Long) statusCombo.getValue());
									objModel.setOffice(new S_OfficeModel(getOfficeID()));
									objModel.setType(SConstants.LEDGER_ADDED_INDIRECTLY);

									supplier.setName(supplierNameField.getValue());
									supplier.setSupplier_code(supplierCodeTextField.getValue());
									supplier.setBank_name(bank_nameTextField.getValue());
									supplier.setWebsite(websiteTextField.getValue());
									supplier.setResponsible_person((Long) responsibleEmployeeCombo.getValue());
									supplier.setContact_person(contact_person.getValue());
									supplier.setContact_person_fax(contact_person_fax.getValue());
									supplier.setContact_person_email(contact_person_email.getValue());
									supplier.setDescription(description.getValue());
									supplier.setCredit_limit(toDouble(credit_limitTextField.getValue()));
									supplier.setCredit_period(toInt(credit_periodTextField.getValue()));
									supplier.setPayment_terms(new PaymentTermsModel((Long) payment_terms.getValue()));
									supplier.setSupplier_currency(new CurrencyModel((Long) currency.getValue()));
									supplier.setAddress(addr);
									if(subscription.getValue())
										supplier.setSubscription(toLong("1"));
									else
										supplier.setSubscription(toLong("0"));
									supplier.setStatus((Long) statusCombo.getValue());
									supplier.setLedger(objModel);
									supplier.setLoginEnabled(enableLogin.getValue());
									supplier.setVatNumber(vatNumberField.getValue());
									
									S_LoginModel loginModel=null;
									if(supplier.getLogin()!=null){
										loginModel=supplier.getLogin();
										if(enableLogin.getValue()){
											loginModel.setLogin_name(userNameField.getValue());
											loginModel.setPassword(SEncryption.encrypt(passwordField.getValue()));
											loginModel.setOffice(new S_OfficeModel(getOfficeID()));
											loginModel.setUserType(new S_UserRoleModel(SConstants.ROLE_SUPPLIER));
											loginModel.setStatus(0);
											supplier.setLogin(loginModel);
										}
										else{
											loginModel.setStatus(1);
											supplier.setLogin(loginModel);
										}
									}
									else{
										if(enableLogin.getValue()){
											loginModel=new S_LoginModel();
											loginModel.setLogin_name(userNameField.getValue());
											loginModel.setPassword(SEncryption.encrypt(passwordField.getValue()));
											loginModel.setOffice(new S_OfficeModel(getOfficeID()));
											loginModel.setUserType(new S_UserRoleModel(SConstants.ROLE_SUPPLIER));
											loginModel.setStatus(0);
											supplier.setLogin(loginModel);
										}
										else{
											supplier.setLogin(null);
										}
									}
									try {
										
										objDao.update(supplier,loginModel);
										if (sendMailBox.getValue()) {
											sendMail(supplier.getAddress().getEmail());
										}
										loadOptions(supplier.getId());
										Notification.show(getPropertyName("update_success"), Type.WARNING_MESSAGE);
									} catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
									setRequiredError(supplierCodeTextField,null, false);
								} else {
									setRequiredError(supplierCodeTextField,getPropertyName("code_exist"),true);
								}
							}else if(!notExist){
								setRequiredError(userNameField,getPropertyName("username_exist"), true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});
			
			loadOptions(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		}else{
			SNotification.show("Set the supplier group in account settings",Type.ERROR_MESSAGE);
		}
		return mainPanel;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadOptions(long id) {
		List testList=new ArrayList();
		try {
			testList = objDao.getAllSuppliersNames(getOfficeID());
			testList.add(0, new SupplierModel(0, getPropertyName("create_new")));
			CollectionContainer bic = CollectionContainer.fromBeans(testList, "id");
			supplierListCombo.setContainerDataSource(bic);
			supplierListCombo.setItemCaptionPropertyId("name");
			supplierListCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (!address1Field.isValid()) {
			ret = false;
		}

		if (credit_periodTextField.getValue() == null || credit_periodTextField.getValue().equals("")) {
			setRequiredError(credit_periodTextField, getPropertyName("invalid_data"), true);
			ret = false;
		} else {
			try {
				if (toInt(credit_periodTextField.getValue()) < 0) {
					setRequiredError(credit_periodTextField, getPropertyName("invalid_data"), true);
					ret = false;
				} else
					setRequiredError(credit_periodTextField, null, false);
			} catch (Exception e) {
				setRequiredError(credit_periodTextField, getPropertyName("invalid_data"), true);
				ret = false;
			}
		}

		if (credit_limitTextField.getValue() == null
				|| credit_limitTextField.getValue().equals("")) {
			setRequiredError(credit_limitTextField, getPropertyName("invalid_data"), true);
			ret = false;
		} else {
			try {
				if (toDouble(credit_limitTextField.getValue()) < 0) {
					setRequiredError(credit_limitTextField,
							getPropertyName("invalid_data"), true);
					ret = false;
				} else
					setRequiredError(credit_limitTextField, null, false);
			} catch (Exception e) {
				setRequiredError(credit_limitTextField, getPropertyName("invalid_data"),
						true);
				ret = false;
			}
		}

		if (payment_terms.getValue() == null
				|| payment_terms.getValue().equals("")) {
			setRequiredError(payment_terms, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(payment_terms, null, false);
		
		
		if (currency.getValue() == null || currency.getValue().equals("")) {
			setRequiredError(currency, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(currency, null, false);
		
		if(responsibleEmployeeCombo.getValue()==null || responsibleEmployeeCombo.getValue().equals("")){
			setRequiredError(responsibleEmployeeCombo, getPropertyName("invalid_selection"),true);
			ret=false;
		}
		else
			setRequiredError(responsibleEmployeeCombo, null,false);

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (supplierCodeTextField.getValue() == null
				|| supplierCodeTextField.getValue().equals("")) {
			setRequiredError(supplierCodeTextField, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(supplierCodeTextField, null, false);

		if (supplierNameField.getValue() == null
				|| supplierNameField.getValue().equals("")) {
			setRequiredError(supplierNameField, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(supplierNameField, null, false);
		
		if(enableLogin.getValue()){
			if(userNameField.getValue()==null || userNameField.getValue().equals("")){
				setRequiredError(userNameField, getPropertyName("invalid_data"),true);
				ret=false;
			}else
				setRequiredError(userNameField, null,false);
			
			if(passwordField.getValue()==null || passwordField.getValue().equals("")){
				setRequiredError(passwordField, getPropertyName("invalid_data"),true);
				ret=false;
			}else
				setRequiredError(passwordField, null,false);
			
			if(!passwordField.getValue().toString().equals(confirmPasswordField.getValue().toString())){
				setRequiredError(confirmPasswordField, getPropertyName("password_mismatch"),true);
				ret=false;
			}else
				setRequiredError(confirmPasswordField, null,false);
			
			if(sendMailBox.getValue()&&(address1Field.getEmailTextField().getValue()==null||address1Field.getEmailTextField().getValue().equals(""))){
				ret=false;
				setRequiredError(address1Field.getEmailTextField(),  getPropertyName("invalid_email"), true);
			}else
				setRequiredError(address1Field.getEmailTextField(),null, false);
		}

		return ret;
	}

	public void removeErrorMsgs() {
		address1Field.getCountryComboField().setComponentError(null);
		credit_limitTextField.setComponentError(null);
		credit_periodTextField.setComponentError(null);
		payment_terms.setComponentError(null);
		currency.setComponentError(null);
		statusCombo.setComponentError(null);
		supplierNameField.setComponentError(null);
		supplierCodeTextField.setComponentError(null);
		responsibleEmployeeCombo.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	private void sendMail(String email) {
		try {
			InternetAddress emailAddr = null;
			try {
				emailAddr = new InternetAddress(email);
			} catch (AddressException e) {
				e.printStackTrace();
			}
			SMail mail = new SMail();
			Address[] ads = new Address[1];
			ads[0] = emailAddr;

			smail.sendSparkMail(ads, "Your user name is : " + userNameField.getValue()
					+ " and your password is : " + passwordField.getValue(), 
					"Moaza saif Registration",null,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isNotExist() {
		try {
			if (userDao.isAlreadyExist(userNameField.getValue())) {
				return false;
			}
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
}
