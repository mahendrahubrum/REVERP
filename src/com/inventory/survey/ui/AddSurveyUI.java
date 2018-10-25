package com.inventory.survey.ui;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.survey.dao.SurveyDao;
import com.inventory.survey.model.SurveyModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SMail;
import com.webspark.dao.LoginDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

public class AddSurveyUI extends SparkLogic {

	private static final long serialVersionUID = 1946552886842774009L;

	long id;

	SPanel mainPanel;

	// SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SGridLayout form, form2;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField surveyIDList;

	SDateField date;

	STextField emailTextField;
	STextField mobileTextField;
	STextField telephoneTextField;
	STextField faxTextField;
	STextField contactPersonTextField;
	STextField companyTextField;
	STextField activityTextField;

	STextField accountingSoftwareTextField;
	STextField chequeWriterTextField;
	STextField remindersTextField;
	STextField websiteTextField;
	STextField cctvTextField;
	STextField timeAttendanceTextField;
	STextField bulkEmailTextField;
	STextField bulkSmsTextField;
	STextField computerTextField;
	STextField printerTextField;
	STextField annualMaintenanceTextField;

	SNativeSelect flyerSelect;

	STextArea description;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	SurveyDao objDao;

	LoginDao loginDao;

	private SCheckBox loginBox;
	private STextField loginField;
	private SPasswordField passwrdField;
	private SPasswordField passwrdCnfField;
	private SCheckBox sendMailBox;
	private SFormLayout loginLay;
	private UserManagementDao userDao;

	private SMail smail;

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		loginDao = new LoginDao();

		mainPanel = new SPanel();

		date = new SDateField(getPropertyName("date"), 100, getDateFormat(),
				getWorkingDate());

		setSize(1000, 575);
		objDao = new SurveyDao();

		smail = new SMail();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Customer");

		try {

			emailTextField = new STextField(getPropertyName("email"), 300);
			mobileTextField = new STextField(getPropertyName("ad_mobile"), 300);
			telephoneTextField = new STextField(getPropertyName("ad_phone"),
					300);
			faxTextField = new STextField(getPropertyName("ad_fax"), 300);
			contactPersonTextField = new STextField(
					getPropertyName("contact_person"), 300);
			companyTextField = new STextField(getPropertyName("company"), 300);
			activityTextField = new STextField(getPropertyName("activity"), 300);

			accountingSoftwareTextField = new STextField(
					getPropertyName("acc_software"), 300);
			chequeWriterTextField = new STextField(
					getPropertyName("check_writer"), 300);
			remindersTextField = new STextField(getPropertyName("reminders"),
					300);
			websiteTextField = new STextField(getPropertyName("website"), 300);
			cctvTextField = new STextField(getPropertyName("cctv"), 300);
			timeAttendanceTextField = new STextField(
					getPropertyName("time_attendence"), 300);
			bulkEmailTextField = new STextField(getPropertyName("bulk_email"),
					300);
			bulkSmsTextField = new STextField(getPropertyName("bulk_sms"), 300);
			computerTextField = new STextField(getPropertyName("computer"), 300);
			printerTextField = new STextField(getPropertyName("printer"), 300);
			annualMaintenanceTextField = new STextField(
					getPropertyName("annual_maint_contract"), 300);

			flyerSelect = new SNativeSelect(getPropertyName("flyer"), 300);
			flyerSelect.addItem("Flyer");

			description = new STextArea(getPropertyName("description"), 300, 40);

			vLayout = new SVerticalLayout();
			vLayout.setMargin(true);
			form = new SGridLayout();
			form.setColumns(3);
			form2 = new SGridLayout();
			form2.setColumns(3);
			buttonLayout = new HorizontalLayout();

			save = new SButton(getPropertyName("Save"));
			edit = new SButton(getPropertyName("Edit"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));
			cancel = new SButton(getPropertyName("Cancel"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(edit);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(cancel);
			buttonLayout.setSpacing(true);

			edit.setVisible(false);
			delete.setVisible(false);
			update.setVisible(false);
			cancel.setVisible(false);

			list = objDao.getAllSurveys(getOfficeID());
			SurveyModel og = new SurveyModel();
			og.setId(0);
			og.setDescription("New");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			surveyIDList = new SComboField(null, 250);
			surveyIDList.setInputPrompt("New");
			loadOptions(0);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("survey_no"));
			salLisrLay.addComponent(surveyIDList);
			salLisrLay.addComponent(createNewButton);

			form.setMargin(true);
			form.setSpacing(true);
			form2.setMargin(true);
			form2.setSpacing(true);

			form.addComponent(emailTextField);
			form.addComponent(mobileTextField);
			form.addComponent(telephoneTextField);
			form.addComponent(faxTextField);
			form.addComponent(contactPersonTextField);
			form.addComponent(companyTextField);
			form.addComponent(activityTextField);
			form.addComponent(date);

			form2.addComponent(accountingSoftwareTextField);
			form2.addComponent(chequeWriterTextField);
			form2.addComponent(remindersTextField);
			form2.addComponent(websiteTextField);
			form2.addComponent(cctvTextField);
			form2.addComponent(timeAttendanceTextField);
			form2.addComponent(bulkEmailTextField);
			form2.addComponent(bulkSmsTextField);
			form2.addComponent(computerTextField);
			form2.addComponent(printerTextField);
			form2.addComponent(annualMaintenanceTextField);
			form2.addComponent(flyerSelect);
			form2.addComponent(description);

			loginBox = new SCheckBox(getPropertyName("enable_login"));
			loginField = new STextField(getPropertyName("login_name"), 150);
			passwrdField = new SPasswordField(getPropertyName("password"), 150);
			passwrdCnfField = new SPasswordField(
					getPropertyName("reenter_password"), 150);
			sendMailBox = new SCheckBox(getPropertyName("send_mail"));

			loginLay = new SFormLayout();
			loginLay.addComponent(loginField);
			loginLay.addComponent(passwrdField);
			loginLay.addComponent(passwrdCnfField);
			loginLay.addComponent(sendMailBox);
			loginLay.setVisible(false);

			// hLayout.addComponent(form);
			// hLayout.addComponent(form2);

			// hLayout.setMargin(true);
			// hLayout.setSpacing(true);
			vLayout.setSizeFull();
			vLayout.addComponent(salLisrLay);
			// vLayout.addComponent(hLayout);
			vLayout.addComponent(form);
			vLayout.addComponent(form2);

			vLayout.addComponent(buttonLayout);
			vLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
			// form.addComponent(grid);

			mainPanel.setContent(vLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					surveyIDList.setValue((long) 0);
				}
			});

			loginBox.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (loginBox.getValue())
						loginLay.setVisible(true);
					else
						loginLay.setVisible(false);
					loginField.setValue("");
					passwrdField.setValue("");
					passwrdCnfField.setValue("");
					sendMailBox.setValue(false);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (surveyIDList.getValue() == null
								|| surveyIDList.getValue().toString()
										.equals("0")) {

							if (isValid()) {

								SurveyModel objModel = new SurveyModel();

								objModel.setEmail(emailTextField.getValue());
								objModel.setMobile(mobileTextField.getValue());
								objModel.setTelephone(telephoneTextField
										.getValue());
								objModel.setFax(faxTextField.getValue());
								objModel.setContact_person(contactPersonTextField
										.getValue());
								objModel.setCompany(companyTextField.getValue());
								objModel.setActivity(activityTextField
										.getValue());
								objModel.setAccounting_software(accountingSoftwareTextField
										.getValue());
								objModel.setCheque_writer(chequeWriterTextField
										.getValue());
								objModel.setReminders(remindersTextField
										.getValue());
								objModel.setWebsite(websiteTextField.getValue());
								objModel.setCctv(cctvTextField.getValue());
								objModel.setTime_attendance(timeAttendanceTextField
										.getValue());
								objModel.setBulk_email(bulkEmailTextField
										.getValue());
								objModel.setBulk_sms(bulkSmsTextField
										.getValue());
								objModel.setComputer(computerTextField
										.getValue());
								objModel.setPrinter(printerTextField.getValue());
								objModel.setAnnual_maintanance_contact(annualMaintenanceTextField
										.getValue());
								objModel.setDescription(description.getValue());

								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setFlyer(flyerSelect.getValue()
										.toString());
								objModel.setDate(CommonUtil
										.getSQLDateFromUtilDate(date.getValue()));
								objModel.setLogin_id(getLoginID());

								try {
									id = objDao.save(objModel);

									loadOptions(id);

									Notification.show(
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

								} catch (Exception e) {
									// TODO Auto-generated catch block
									Notification.show(getPropertyName("Error"),
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

			surveyIDList.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {
						if (surveyIDList.getValue() != null
								&& !surveyIDList.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							SurveyModel objModel = objDao
									.getSurvey((Long) surveyIDList.getValue());

							emailTextField.setValue(objModel.getEmail());
							mobileTextField.setValue(objModel.getMobile());
							telephoneTextField.setValue(objModel.getTelephone());
							faxTextField.setValue(objModel.getFax());
							contactPersonTextField.setValue(objModel
									.getContact_person());
							companyTextField.setValue(objModel.getCompany());
							activityTextField.setValue(objModel.getActivity());

							accountingSoftwareTextField.setValue(objModel
									.getAccounting_software());
							chequeWriterTextField.setValue(objModel
									.getCheque_writer());
							remindersTextField.setValue(objModel.getReminders());
							websiteTextField.setValue(objModel.getWebsite());
							cctvTextField.setValue(objModel.getCctv());
							timeAttendanceTextField.setValue(objModel
									.getTime_attendance());
							bulkEmailTextField.setValue(objModel
									.getBulk_email());
							bulkSmsTextField.setValue(objModel.getBulk_sms());
							computerTextField.setValue(objModel.getComputer());
							printerTextField.setValue(objModel.getPrinter());
							annualMaintenanceTextField.setValue(objModel
									.getAnnual_maintanance_contact());

							description.setValue(objModel.getDescription());
							flyerSelect.setValue(objModel.getFlyer());
							date.setValue(objModel.getDate());

							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();

							emailTextField.setValue("");
							mobileTextField.setValue("");
							telephoneTextField.setValue("");
							faxTextField.setValue("");
							contactPersonTextField.setValue("");
							companyTextField.setValue("");
							activityTextField.setValue("");

							accountingSoftwareTextField.setValue("");
							chequeWriterTextField.setValue("");
							remindersTextField.setValue("");
							websiteTextField.setValue("");
							cctvTextField.setValue("");
							timeAttendanceTextField.setValue("");
							bulkEmailTextField.setValue("");
							bulkSmsTextField.setValue("");
							computerTextField.setValue("");
							printerTextField.setValue("");
							annualMaintenanceTextField.setValue("");
							date.setValue(getWorkingDate());
							description.setValue("");

						}
						removeErrorMsgs();

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			edit.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(true);
						cancel.setVisible(true);

						setWritableAll();

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			cancel.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);
						loadOptions(Long.parseLong(surveyIDList.getValue()
								.toString()));

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) surveyIDList
														.getValue();
												objDao.delete(id);

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
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

			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (surveyIDList.getValue() != null) {

							if (isValid()) {

								SurveyModel objModel = objDao
										.getSurvey((Long) surveyIDList
												.getValue());

								objModel.setEmail(emailTextField.getValue());
								objModel.setMobile(mobileTextField.getValue());
								objModel.setTelephone(telephoneTextField
										.getValue());
								objModel.setFax(faxTextField.getValue());
								objModel.setContact_person(contactPersonTextField
										.getValue());
								objModel.setCompany(companyTextField.getValue());
								objModel.setActivity(activityTextField
										.getValue());
								objModel.setAccounting_software(accountingSoftwareTextField
										.getValue());
								objModel.setCheque_writer(chequeWriterTextField
										.getValue());
								objModel.setReminders(remindersTextField
										.getValue());
								objModel.setWebsite(websiteTextField.getValue());
								objModel.setCctv(cctvTextField.getValue());
								objModel.setTime_attendance(timeAttendanceTextField
										.getValue());
								objModel.setBulk_email(bulkEmailTextField
										.getValue());
								objModel.setBulk_sms(bulkSmsTextField
										.getValue());
								objModel.setComputer(computerTextField
										.getValue());
								objModel.setPrinter(printerTextField.getValue());
								objModel.setAnnual_maintanance_contact(annualMaintenanceTextField
										.getValue());
								objModel.setDescription(description.getValue());

								objModel.setFlyer(flyerSelect.getValue()
										.toString());
								objModel.setDate(CommonUtil
										.getSQLDateFromUtilDate(date.getValue()));

								try {
									objDao.update(objModel);

									loadOptions(objModel.getId());

									Notification.show(
											getPropertyName("update_success"),
											Type.WARNING_MESSAGE);
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
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

			// addShortcutListener(new ShortcutListener("Add New Purchase",
			// ShortcutAction.KeyCode.N, new int[] {
			// ShortcutAction.ModifierKey.ALT}) {
			// @Override
			// public void handleAction(Object sender, Object target) {
			// loadOptions(0);
			// }
			// });
			//
			//
			// addShortcutListener(new ShortcutListener("Save",
			// ShortcutAction.KeyCode.ENTER, null) {
			// @Override
			// public void handleAction(Object sender, Object target) {
			// if (save.isVisible())
			// save.click();
			// else
			// update.click();
			// }
			// });
			//
			setDefaultValues();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;

	}

	public void setReadOnlyAll() {
		description.setReadOnly(true);
		emailTextField.setReadOnly(true);
		mobileTextField.setReadOnly(true);
		telephoneTextField.setReadOnly(true);
		faxTextField.setReadOnly(true);
		contactPersonTextField.setReadOnly(true);
		companyTextField.setReadOnly(true);
		activityTextField.setReadOnly(true);

		accountingSoftwareTextField.setReadOnly(true);
		chequeWriterTextField.setReadOnly(true);
		remindersTextField.setReadOnly(true);
		websiteTextField.setReadOnly(true);
		cctvTextField.setReadOnly(true);
		timeAttendanceTextField.setReadOnly(true);
		bulkEmailTextField.setReadOnly(true);
		bulkSmsTextField.setReadOnly(true);
		computerTextField.setReadOnly(true);
		printerTextField.setReadOnly(true);
		annualMaintenanceTextField.setReadOnly(true);
		flyerSelect.setReadOnly(true);
		date.setReadOnly(true);
	}

	public void setWritableAll() {
		description.setReadOnly(false);
		emailTextField.setReadOnly(false);
		mobileTextField.setReadOnly(false);
		telephoneTextField.setReadOnly(false);
		faxTextField.setReadOnly(false);
		contactPersonTextField.setReadOnly(false);
		companyTextField.setReadOnly(false);
		activityTextField.setReadOnly(false);

		accountingSoftwareTextField.setReadOnly(false);
		chequeWriterTextField.setReadOnly(false);
		remindersTextField.setReadOnly(false);
		websiteTextField.setReadOnly(false);
		cctvTextField.setReadOnly(false);
		timeAttendanceTextField.setReadOnly(false);
		bulkEmailTextField.setReadOnly(false);
		bulkSmsTextField.setReadOnly(false);
		computerTextField.setReadOnly(false);
		printerTextField.setReadOnly(false);
		annualMaintenanceTextField.setReadOnly(false);
		flyerSelect.setReadOnly(false);
		date.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllSurveys(getOfficeID());

			SurveyModel sop = new SurveyModel();
			sop.setId(0);
			sop.setDescription("New");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			surveyIDList.setContainerDataSource(bic);
			surveyIDList.setItemCaptionPropertyId("description");

			surveyIDList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (date.getValue() == null) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			date.setComponentError(null);

		if (emailTextField.getValue() != null
				&& !emailTextField.getValue().equals("")) {
			try {
				InternetAddress emailAddr = new InternetAddress(
						emailTextField.getValue());
				emailAddr.validate();
				setRequiredError(emailTextField, null, false);
			} catch (Exception ex) {
				setRequiredError(emailTextField,
						getPropertyName("invalid_data"), true);
				emailTextField.focus();
				ret = false;
			}
		}

		if (contactPersonTextField.getValue() == null
				|| contactPersonTextField.getValue().equals("")) {
			setRequiredError(contactPersonTextField,
					getPropertyName("invalid_data"), true);
			contactPersonTextField.focus();
			ret = false;
		} else
			setRequiredError(contactPersonTextField, null, false);

		if (companyTextField.getValue() == null
				|| companyTextField.getValue().equals("")) {
			setRequiredError(companyTextField, getPropertyName("invalid_data"),
					true);
			companyTextField.focus();
			ret = false;
		} else
			setRequiredError(companyTextField, null, false);

		if (flyerSelect.getValue() == null || flyerSelect.getValue().equals("")) {
			setRequiredError(flyerSelect, getPropertyName("invalid_selection"),
					true);
			flyerSelect.focus();
			ret = false;
		} else
			setRequiredError(flyerSelect, null, false);

		return ret;
	}

	public void removeErrorMsgs() {
		emailTextField.setComponentError(null);
		mobileTextField.setComponentError(null);
		telephoneTextField.setComponentError(null);
		faxTextField.setComponentError(null);
		contactPersonTextField.setComponentError(null);
		companyTextField.setComponentError(null);
		activityTextField.setComponentError(null);

		accountingSoftwareTextField.setComponentError(null);
		chequeWriterTextField.setComponentError(null);
		remindersTextField.setComponentError(null);
		websiteTextField.setComponentError(null);
		cctvTextField.setComponentError(null);
		timeAttendanceTextField.setComponentError(null);
		bulkEmailTextField.setComponentError(null);
		bulkSmsTextField.setComponentError(null);
		computerTextField.setComponentError(null);
		printerTextField.setComponentError(null);
		annualMaintenanceTextField.setComponentError(null);
		date.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaultValues() {
	}

}
