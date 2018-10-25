package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.CustomerGroupDao;
import com.inventory.config.acct.dao.PaymentTermsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.PaymentTermsModel;
import com.inventory.rent.dao.RentTypeDao;
import com.inventory.rent.model.RentTypeModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.LocationMap;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
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
import com.webspark.common.location.GoogleResponse;
import com.webspark.common.location.LocationFinder;
import com.webspark.common.location.Result;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SEncryption;
import com.webspark.common.util.SMail;
import com.webspark.dao.AddressDao;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.LoginDao;
import com.webspark.mailclient.ui.ComposeMailUI;
import com.webspark.mailclient.ui.ShowEmailsUI;
import com.webspark.model.AddressModel;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_UserRoleModel;

public class AddCustomer extends SparkLogic {

	private static final long serialVersionUID = 1946552886842774009L;

	long id;

	SPanel mainPanel;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SWindow sendWindow, showWindow;
	SDialogBox mapWindow;

	private SButton sendMailButton, showMails;

	SComboField customerListCombo;
	STextField customerNameField;
	// SComboField groupCombo;
	SComboField statusCombo;
	SComboField responsibleEmployeeCombo;
	SComboField groupCombo;
	SAddressField address1Field;
	SHTMLLabel warningLabel;

	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;
	SButton locate;

	LocationMap locateMap;
	
	STextField customerCodeTextField;
	SNativeSelect currency;
	SNativeSelect salesType;
	SNativeSelect payment_terms;
	STextField credit_limitTextField;
	STextField max_credit_periodTextField;
	STextArea description;

	List list;
	CustomerDao objDao = new CustomerDao();

	LoginDao loginDao;

	private SCheckBox enableLogin,subscription;
	private STextField userNameField;
	private SPasswordField passwordField;
	private SPasswordField confirmPasswordField;
	private SCheckBox sendMailBox;
	private SFormLayout loginLay;
	private UserManagementDao userDao;

	private SMail smail;

	SButton createNewButton;
	
	WrappedSession session;
	SettingsValuePojo settings;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(1200, 640);
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		if(settings.getCUSTOMER_GROUP()!=0){
		
		objDao = new CustomerDao();
		userDao = new UserManagementDao();

		smail = new SMail();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {
			loginDao = new LoginDao();
			
			sendMailButton = new SButton(getPropertyName("send_mail"));
			sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
			sendMailButton.setStyleName("deletebtnStyle");

			showMails = new SButton(getPropertyName("show_mails"));
			showMails.setIcon(new ThemeResource("icons/sendmail.png"));
			showMails.setStyleName("deletebtnStyle");

			customerCodeTextField = new STextField(
					getPropertyName("customer_code"), 250);
			currency = new SNativeSelect(getPropertyName("customer_currency"),
					250, new CurrencyManagementDao().getlabels(), "id", "name");
			salesType = new SNativeSelect(getPropertyName("sales_type"), 250,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");
			Iterator itt = salesType.getItemIds().iterator();
			if (itt.hasNext())
				salesType.setValue(itt.next());

			payment_terms = new SNativeSelect(getPropertyName("payment_terms"),
					250,
					new PaymentTermsDao()
							.getAllActivePaymentTerms(getOrganizationID()),
					"id", "name");

			credit_limitTextField = new STextField(
					getPropertyName("credit_limit"), 250);
			max_credit_periodTextField = new STextField(
					getPropertyName("max_credit_period"), 250, "0");
			description = new STextArea(getPropertyName("description"), 250, 30);
			List rentList = new RentTypeDao()
					.getAllActiveSalesTypeNames(getOfficeID());
			rentList.add(0, new RentTypeModel(0, "None"));

			/*
			 * customerCodeTextField currency gstnoTextField salesType
			 * discount_percentageTextField
			 * prompt_payment_discount_percentTextField payment_terms
			 * credit_limitTextField description
			 */

			credit_limitTextField = new STextField(
					getPropertyName("credit_limit"), 250);
			max_credit_periodTextField = new STextField(
					getPropertyName("max_credit_period"), 250, "0");
			description = new STextArea(getPropertyName("description"), 250, 30);
			hLayout = new SHorizontalLayout();
			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			// SGridLayout grid=new SGridLayout();
			// grid.setColumns(6);
			// grid.setSizeFull();

//			form.setSizeFull();

			address1Field = new SAddressField(1);
			warningLabel=new SHTMLLabel(null,"<font color='#0000FF'>"+getPropertyName("locate_customer_map")+"</font>");
			
			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			locate = new SButton(getPropertyName("locate_customer"));

			mapWindow = new SDialogBox(getPropertyName("locate_customer"), 800, 500);
			mapWindow.center();
			mapWindow.setResizable(false);
			mapWindow.setModal(true);
			mapWindow.setCloseShortcut(KeyCode.ESCAPE);
			
			
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			
			buttonLayout.addComponent(sendMailButton);
			buttonLayout.addComponent(showMails);
			buttonLayout.setSpacing(true);

			sendMailButton.setVisible(false);
			showMails.setVisible(false);

			deleteButton.setVisible(false);
			updateButton.setVisible(false);

			list = objDao.getAllCustomersNames(getOfficeID());
			CustomerModel og = new CustomerModel();
			og.setId(0);
			og.setName(getPropertyName("create_new"));
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			customerListCombo = new SComboField(null, 250, list, "id", "name");
			customerListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 250,SConstants.statuses.status,"key","value");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			responsibleEmployeeCombo = new SComboField(getPropertyName("sales_man"), 250,
					new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()), "id", "first_name");
			
			groupCombo = new SComboField(
					getPropertyName("group"), 250,
					new CustomerGroupDao().getAllCustomerGroups(getOfficeID()), "id", "name");
			groupCombo.setInputPrompt(getPropertyName("select"));

			// groupCombo = new SComboField("Group", 250, new
			// GroupDao().getAllGroupsNames(getOrganizationID()), "id", "name"
			// , true, "Select");

			customerNameField = new STextField(
					getPropertyName("customer_name"), 250, "");

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("customer"));
			salLisrLay.addComponent(customerListCombo);
			salLisrLay.addComponent(createNewButton);

			form.addComponent(salLisrLay);
			form.addComponent(customerNameField);
			form.addComponent(customerCodeTextField);
			form.addComponent(groupCombo);
			form.addComponent(statusCombo);
			form.addComponent(responsibleEmployeeCombo);

			// form.addComponent(address1Field);

			form.addComponent(currency);
			form.addComponent(salesType);
			form.addComponent(payment_terms);
			form.addComponent(credit_limitTextField);
			form.addComponent(max_credit_periodTextField);
			form.addComponent(description);

			enableLogin = new SCheckBox(getPropertyName("enable_login"));
			subscription=new SCheckBox(getPropertyName("enable_rental"));
			userNameField = new STextField(getPropertyName("login_name"), 150);
			passwordField = new SPasswordField(getPropertyName("password"), 150);
			confirmPasswordField = new SPasswordField(
					getPropertyName("reenter_password"), 150);
			sendMailBox = new SCheckBox(getPropertyName("send_mail"));
			loginLay = new SFormLayout();
			loginLay.addComponent(userNameField);
			loginLay.addComponent(passwordField);
			loginLay.addComponent(confirmPasswordField);
			loginLay.addComponent(sendMailBox);
			loginLay.setVisible(false);

			// form.setWidth("400");

			// form.addComponent(buttonLayout);
			SVerticalLayout addressLayout = new SVerticalLayout();
			addressLayout.setSpacing(true);
//			addressLayout.addComponent(subscription);
			addressLayout.addComponent(locate);
			addressLayout.addComponent(warningLabel);
			locate.setVisible(false);
			
			
			setRequiredError(warningLabel, "", true);
			
			
			addressLayout.addComponent(enableLogin);
			addressLayout.addComponent(loginLay);
//			addressLayout.setSizeFull();
			hLayout.addComponent(form);
			hLayout.addComponent(addressLayout);
			
			SVerticalLayout addLay = new SVerticalLayout();
			addLay.setSpacing(true);
			
			addLay.addComponent(address1Field);
			
			hLayout.addComponent(addLay);
			

			address1Field.setCaption(null);
			hLayout.setMargin(true);
			hLayout.setSpacing(true);
//			vLayout.setSizeFull();
			vLayout.addComponent(hLayout);
			vLayout.addComponent(buttonLayout);
			vLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
			// form.addComponent(grid);

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
					customerListCombo.setValue((long) 0);
				}
			});

			
			enableLogin.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					
					if(enableLogin.getValue()!=null){
						if(enableLogin.getValue()){
							loginLay.setVisible(true);
						}
						else{
							loginLay.setVisible(false);
						}
					}
					else{
						loginLay.setVisible(false);
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
					if (isValidEmail(address1Field.getEmailTextField()
							.getValue())) {
						try {
							sendWindow = new ComposeMailUI(address1Field
									.getEmailTextField().getValue());
							sendWindow.center();
							sendWindow.setModal(true);
							getUI().addWindow(sendWindow);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						SNotification.show(getPropertyName("invalid_email"),
								getPropertyName("invalid_email"),
								Type.WARNING_MESSAGE);
					}
				}
			});

			
			showMails.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValidEmail(address1Field.getEmailTextField()
							.getValue())) {
						try {
							showWindow = new ShowEmailsUI(address1Field
									.getEmailTextField().getValue());
							showWindow.center();
							showWindow.setModal(true);
							getUI().addWindow(showWindow);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						SNotification.show(getPropertyName("invalid_email"),
								getPropertyName("invalid_email"),
								Type.WARNING_MESSAGE);
					}
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (customerListCombo.getValue() == null || customerListCombo.getValue().toString().equals("0")) {

							boolean notExists = isNotExist();

							if (isValid() && notExists) {

								if (!objDao.isCodeExists(getOfficeID(),customerCodeTextField.getValue(), 0)) {

									LedgerModel objModel = new LedgerModel();
									objModel.setName(customerNameField.getValue());
									objModel.setGroup(new GroupModel(settings.getCUSTOMER_GROUP()));
									objModel.setCurrent_balance(0);
									objModel.setStatus((Long) statusCombo.getValue());
									objModel.setOffice(new S_OfficeModel(getOfficeID()));
									objModel.setType(SConstants.LEDGER_ADDED_INDIRECTLY);

									CustomerModel customer = new CustomerModel();

									customer.setName(customerNameField.getValue());
									customer.setCustomer_code(customerCodeTextField.getValue());
									customer.setStatus((Long)statusCombo.getValue());
									customer.setResponsible_person((Long) responsibleEmployeeCombo.getValue());
									customer.setCustomer_currency(new CurrencyModel((Long) currency.getValue()));
									customer.setSales_type((Long) salesType.getValue());
									customer.setPayment_terms(new PaymentTermsModel((Long) payment_terms.getValue()));
									customer.setCredit_limit(roundNumber(toDouble(credit_limitTextField.getValue())));
									customer.setMax_credit_period(toInt(max_credit_periodTextField.getValue()));
									customer.setDescription(description.getValue());
									if(subscription.getValue())
										customer.setSubscription(toLong("1"));
									else
										customer.setSubscription(toLong("0"));
									customer.setCustomerGroupId((Long) groupCombo.getValue());
									customer.setAddress(address1Field.getAddress());
									customer.setLedger(objModel);
									customer.setLoginEnabled(enableLogin.getValue());

									S_LoginModel loginModel = null;

									if (enableLogin.getValue()) {
										loginModel = new S_LoginModel();
										loginModel.setLogin_name(userNameField.getValue());
										loginModel.setPassword(SEncryption.encrypt(confirmPasswordField.getValue()));
										loginModel.setOffice(new S_OfficeModel(getOfficeID()));
										loginModel.setUserType(new S_UserRoleModel(SConstants.ROLE_CUSTOMER));
										loginModel.setStatus(0);
									}
									else
										customer.setLogin(null);

									try {
										id = objDao.save(customer, loginModel);

										if (sendMailBox.getValue()) {
											sendMail(customer.getAddress().getEmail());
										}
										loadOptions(id);
										Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);

									} catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
									setRequiredError(customerCodeTextField,null, false);
								} else
									setRequiredError(customerCodeTextField,getPropertyName("code_exist"), true);

							} else if (!notExists) {
								setRequiredError(userNameField,getPropertyName("username_exist"), true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
			
			
			customerListCombo.addValueChangeListener(new ValueChangeListener() {

				@SuppressWarnings("rawtypes")
				public void valueChange(ValueChangeEvent event) {

					try {
						if (customerListCombo.getValue() != null && !customerListCombo.getValue().toString().equals("0")) {
							saveButton.setVisible(false);
							deleteButton.setVisible(true);
							updateButton.setVisible(true);
							locate.setVisible(true);
							CustomerModel customerModel = objDao.getCustomer((Long) customerListCombo.getValue());
							LedgerModel objModel = customerModel.getLedger();
							customerNameField.setValue(objModel.getName());
							customerCodeTextField.setValue(customerModel.getCustomer_code());
							session.setAttribute("oldCode", customerModel.getCustomer_code());
							groupCombo.setNewValue(customerModel.getCustomerGroupId());
							statusCombo.setValue(customerModel.getStatus());
							responsibleEmployeeCombo.setValue(customerModel.getResponsible_person());
							currency.setValue(customerModel.getCustomer_currency().getId());
							salesType.setValue(customerModel.getSales_type());
							payment_terms.setValue(customerModel.getPayment_terms().getId());
							credit_limitTextField.setValue(""+ customerModel.getCredit_limit());
							max_credit_periodTextField.setValue(""+ customerModel.getMax_credit_period());
							description.setValue(""+ customerModel.getDescription());
							address1Field.loadAddress(customerModel.getAddress().getId());
							if(customerModel.getSubscription()==0)
								subscription.setValue(false);
							else if(customerModel.getSubscription()==1)
								subscription.setValue(true);
							enableLogin.setValue(customerModel.isLoginEnabled());
							if (customerModel.getLogin() != null) {
								userNameField.setValue(customerModel.getLogin().getLogin_name());
								passwordField.setValue(SEncryption.decrypt(customerModel.getLogin().getPassword()));
								confirmPasswordField.setValue(SEncryption.decrypt(customerModel.getLogin().getPassword()));
								session.setAttribute("userName", customerModel.getLogin().getLogin_name());
							} 
							sendMailButton.setVisible(true);
							showMails.setVisible(true);
						}
						else{
							saveButton.setVisible(true);
							deleteButton.setVisible(false);
							updateButton.setVisible(false);
							locate.setVisible(false);
							
							customerNameField.setValue("");
							customerCodeTextField.setValue("");
							groupCombo.setNewValue(null);
							statusCombo.setValue((long)1);
							responsibleEmployeeCombo.setValue(null);
							currency.setValue(getCurrencyID());
							Iterator itt = salesType.getItemIds().iterator();
							if (itt.hasNext())
								salesType.setValue(itt.next());
							payment_terms.setValue(null);
							credit_limitTextField.setValue("0");
							max_credit_periodTextField.setValue("0");
							description.setValue("");
							address1Field.clearAll();
							address1Field.getCountryComboField().setValue(getCountryID());
							subscription.setValue(false);
							enableLogin.setValue(false);
							userNameField.setValue("");
							passwordField.setValue("");
							confirmPasswordField.setValue("");
							sendMailBox.setValue(false);
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
			
			
			deleteButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										id = (Long) customerListCombo.getValue();
										objDao.delete(id);
										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
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
						if (customerListCombo.getValue() != null) {

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

								if (!objDao.isCodeExists(getOfficeID(), customerCodeTextField.getValue(), (Long) customerListCombo.getValue())) {

									CustomerModel customer = objDao.getCustomer((Long) customerListCombo.getValue());
									LedgerModel objModel = customer.getLedger();
									AddressModel addr = address1Field.getAddress();
									addr.setId(customer.getAddress().getId());
									
									
									objModel.setName(customerNameField.getValue());
									objModel.setGroup(new GroupModel(settings.getCUSTOMER_GROUP()));
									objModel.setStatus((Long) statusCombo.getValue());
									objModel.setOffice(new S_OfficeModel(getOfficeID()));
									objModel.setType(SConstants.LEDGER_ADDED_INDIRECTLY);
									

									customer.setName(customerNameField.getValue());
									customer.setCustomer_code(customerCodeTextField.getValue());
									customer.setStatus((Long)statusCombo.getValue());
									customer.setResponsible_person((Long) responsibleEmployeeCombo.getValue());
									customer.setCustomer_currency(new CurrencyModel((Long) currency.getValue()));
									customer.setSales_type((Long) salesType.getValue());
									customer.setPayment_terms(new PaymentTermsModel((Long) payment_terms.getValue()));
									customer.setCredit_limit(roundNumber(toDouble(credit_limitTextField.getValue())));
									customer.setMax_credit_period(toInt(max_credit_periodTextField.getValue()));
									customer.setDescription(description.getValue());
									if(subscription.getValue())
										customer.setSubscription(toLong("1"));
									else
										customer.setSubscription(toLong("0"));
									customer.setCustomerGroupId((Long) groupCombo.getValue());
									customer.setAddress(addr);
									customer.setLedger(objModel);
									customer.setLoginEnabled(enableLogin.getValue());

									S_LoginModel loginModel = null;
									
									if(customer.getLogin()!=null){
										loginModel=customer.getLogin();
										if(enableLogin.getValue()){
											loginModel.setLogin_name(userNameField.getValue());
											loginModel.setPassword(SEncryption.encrypt(passwordField.getValue()));
											loginModel.setOffice(new S_OfficeModel(getOfficeID()));
											loginModel.setUserType(new S_UserRoleModel(SConstants.ROLE_SUPPLIER));
											loginModel.setStatus(0);
											customer.setLogin(loginModel);
										}
										else{
											loginModel.setStatus(1);
											customer.setLogin(loginModel);
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
											customer.setLogin(loginModel);
										}
										else{
											customer.setLogin(null);
										}
									}
									try {
										objDao.update(customer,loginModel);
										if (sendMailBox.getValue()) {
											sendMail(customer.getAddress().getEmail());
										}
										loadOptions(customer.getId());
										Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									} catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
									setRequiredError(customerCodeTextField,null, false);
								} else {
									setRequiredError(customerCodeTextField,getPropertyName("code_exist"), true);
								}
							} else if (!notExist) {
								setRequiredError(userNameField,getPropertyName("username_exist"), true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			locate.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						CustomerModel customer=new CustomerDao().getCustomer((Long)customerListCombo.getValue());
						S_OfficeModel office=new OfficeDao().getOffice(getOfficeID());
						String customerAddress="";
						String officeAddress="";
						double lat1=0,lat2=0,long1=0,long2=0;
						
						if(customer!=null){
							if(customer.getAddress()!=null)
								customerAddress=new AddressDao().getLocationAddressString(customer.getAddress().getId());
							if(customerAddress.length()>0){
								GoogleResponse response = new LocationFinder().getLatLongFromAddress(customerAddress);
								if(response!=null){
									if(response.getStatus().equals("OK")){
										for(Result result : response.getResults()){
											lat1=toDouble(result.getGeometry().getLocation().getLat());
											long1=toDouble(result.getGeometry().getLocation().getLng());
										}
									}
									else{
									   SNotification.show(getPropertyName("cannot_locate"), Type.TRAY_NOTIFICATION);
									}
								}
								else{
									SNotification.show(getPropertyName("cannot_locate"), Type.TRAY_NOTIFICATION);
								}
							}
							
						}
						
						if(office!=null){
							if(office.getAddress()!=null)
								officeAddress=new AddressDao().getLocationAddressString(office.getAddress().getId());
							if(officeAddress.length()>0){
								GoogleResponse response = new LocationFinder().getLatLongFromAddress(officeAddress);
								if(response!=null){
									if(response.getStatus().equals("OK")){
									   for(Result result : response.getResults()){
										   lat2=toDouble(result.getGeometry().getLocation().getLat());
										   long2=toDouble(result.getGeometry().getLocation().getLng());
									   }
									}
									else{
									   SNotification.show(getPropertyName("cannot_locate"), Type.TRAY_NOTIFICATION);
									}
								}
								else{
									SNotification.show(getPropertyName("cannot_locate"), Type.TRAY_NOTIFICATION);
								}
							}
						}
						if(lat1!=0 && lat2!=0 && long1!=0 && long2!=0){
							locateMap=new LocationMap(lat1, long1, customer.getName(), lat2, long2, getOfficeName());
							mapWindow.setContent(locateMap);
							getUI().getCurrent().addWindow(mapWindow);
						}
						else if(lat1!=0 && long1!=0){
							locateMap=new LocationMap(lat1, long1, customer.getName());
							mapWindow.setContent(locateMap);
							getUI().getCurrent().addWindow(mapWindow);
						}
						else{
							SNotification.show(getPropertyName("cannot_locate"), Type.WARNING_MESSAGE);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			loadOptions(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		}else{
			SNotification.show("Set the customer group in account settings",Type.ERROR_MESSAGE);
		}
		return mainPanel;

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

			smail.sendSparkMail(
					ads,
					"Your user name is : " + userNameField.getValue()
							+ " and your password is : "
							+ passwordField.getValue(),
					"Moaza saif Registration", null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isNotExist() {
		try {

			if (userDao.isAlreadyExist(userNameField.getValue())) {
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllCustomersNames(getOfficeID());
			CustomerModel sop = new CustomerModel();
			sop.setId(0);
			sop.setName(getPropertyName("create_new"));
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			customerListCombo.setContainerDataSource(bic);
			customerListCombo.setItemCaptionPropertyId("name");

			customerListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (!address1Field.isValid()) {
			ret = false;
		}

		if (max_credit_periodTextField.getValue() == null
				|| max_credit_periodTextField.getValue().equals("")) {
			setRequiredError(max_credit_periodTextField,
					getPropertyName("invalid_data"), true);
			max_credit_periodTextField.focus();
			ret = false;
		} else {
			try {
				if (toInt(max_credit_periodTextField.getValue()) < 0) {
					setRequiredError(max_credit_periodTextField,
							getPropertyName("invalid_data"), true);
					max_credit_periodTextField.focus();
					ret = false;
				} else
					setRequiredError(max_credit_periodTextField, null, false);
			} catch (Exception e) {
				setRequiredError(max_credit_periodTextField,
						getPropertyName("invalid_data"), true);
				max_credit_periodTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (credit_limitTextField.getValue() == null
				|| credit_limitTextField.getValue().equals("")) {
			setRequiredError(credit_limitTextField,
					getPropertyName("invalid_data"), true);
			credit_limitTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(credit_limitTextField.getValue()) < 0) {
					setRequiredError(credit_limitTextField,
							getPropertyName("invalid_data"), true);
					credit_limitTextField.focus();
					ret = false;
				} else
					setRequiredError(credit_limitTextField, null, false);
			} catch (Exception e) {
				setRequiredError(credit_limitTextField,
						getPropertyName("invalid_data"), true);
				credit_limitTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (payment_terms.getValue() == null
				|| payment_terms.getValue().equals("")) {
			setRequiredError(payment_terms,
					getPropertyName("invalid_selection"), true);
			payment_terms.focus();
			ret = false;
		} else
			setRequiredError(payment_terms, null, false);

		if (salesType.getValue() == null || salesType.getValue().equals("")) {
			setRequiredError(salesType, getPropertyName("invalid_selection"),
					true);
			salesType.focus();
			ret = false;
		} else
			setRequiredError(salesType, null, false);
		
		if (groupCombo.getValue() == null || groupCombo.getValue().equals("")) {
			setRequiredError(groupCombo, getPropertyName("invalid_selection"),
					true);
			groupCombo.focus();
			ret = false;
		} else
			setRequiredError(groupCombo, null, false);

		if (currency.getValue() == null || currency.getValue().equals("")) {
			setRequiredError(currency, getPropertyName("invalid_selection"),
					true);
			currency.focus();
			ret = false;
		} else
			setRequiredError(currency, null, false);

		if (responsibleEmployeeCombo.getValue() == null
				|| responsibleEmployeeCombo.getValue().equals("")) {
			setRequiredError(responsibleEmployeeCombo,
					getPropertyName("invalid_selection"), true);
			responsibleEmployeeCombo.focus();
			ret = false;
		} else
			setRequiredError(responsibleEmployeeCombo, null, false);

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		/*
		 * if(groupCombo.getValue()==null || groupCombo.getValue().equals("")){
		 * setRequiredError(groupCombo, "Select a Group",true);
		 * groupCombo.focus(); ret=false; } else setRequiredError(groupCombo,
		 * null,false);
		 */

		if (customerNameField.getValue() == null
				|| customerNameField.getValue().equals("")) {
			setRequiredError(customerNameField,
					getPropertyName("invalid_data"), true);
			customerNameField.focus();
			ret = false;
		} else
			setRequiredError(customerNameField, null, false);

		if (customerCodeTextField.getValue() == null
				|| customerCodeTextField.getValue().equals("")) {
			setRequiredError(customerCodeTextField,
					getPropertyName("invalid_data"), true);
			customerCodeTextField.focus();
			ret = false;
		} else
			setRequiredError(customerCodeTextField, null, false);

		if (enableLogin.getValue()) {
			if (userNameField.getValue() == null
					|| userNameField.getValue().equals("")) {
				setRequiredError(userNameField, getPropertyName("invalid_data"),
						true);
				ret = false;
			} else
				setRequiredError(userNameField, null, false);

			if (passwordField.getValue() == null
					|| passwordField.getValue().equals("")) {
				setRequiredError(passwordField, getPropertyName("invalid_data"),
						true);
				ret = false;
			} else
				setRequiredError(passwordField, null, false);

			if (!passwordField.getValue().toString()
					.equals(confirmPasswordField.getValue().toString())) {
				setRequiredError(confirmPasswordField,
						getPropertyName("password_mismatch"), true);
				ret = false;
			} else
				setRequiredError(confirmPasswordField, null, false);

			if (sendMailBox.getValue()
					&& (address1Field.getEmailTextField().getValue() == null || address1Field
							.getEmailTextField().getValue().equals(""))) {
				ret = false;
				setRequiredError(address1Field.getEmailTextField(),
						getPropertyName("invalid_email"), true);
			} else
				setRequiredError(address1Field.getEmailTextField(), null, false);
		}

		return ret;
	}

	public void removeErrorMsgs() {
		address1Field.getCountryComboField().setComponentError(null);
		max_credit_periodTextField.setComponentError(null);
		credit_limitTextField.setComponentError(null);
		payment_terms.setComponentError(null);
		currency.setComponentError(null);
		salesType.setComponentError(null);
		statusCombo.setComponentError(null);
		responsibleEmployeeCombo.setComponentError(null);
		customerNameField.setComponentError(null);
		customerCodeTextField.setComponentError(null);
	}

	public Boolean getHelp() {
		return null;
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
