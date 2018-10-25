package com.inventory.payment.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.EmployeeAdvancePaymentDao;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.DocumentAttach;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author anil
 * @date 23-Nov-2015
 * @Project REVERP
 */

public class EmployeeAdvancePaymentsUI extends SparkLogic {

	private static final long serialVersionUID = 8774283694107704131L;

	private SComboField paymentIdComboField;
	private SComboField employeComboField;
	// private SComboField toAccountComboField;
	private SDateField dateField;
	private SDateField chequeDateField;
	private STextField chequeNoField;
//	private STextField amountField;
//	private STextField discountField;
	private SCurrencyField paymentAmountField;
	private STextArea descriptionField;
//	private SComboField currencyComboField;
	private SComboField fromAccountComboField;

	// SRadioButton creditOrDebit;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private SButton cancelButton;

	// SListSelect workorderSelect;

	private SCollectionContainer container;

	private EmployeeAdvancePaymentDao paymentDao;
	UserManagementDao userDao;
	BankAccountDao bankDao;

	private SettingsValuePojo settings;

	private WrappedSession session;

	private DocumentAttach attach;

	SButton createNewButton;

	SRadioButton cashOrCheck;

	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		
		setSize(830, 450);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		try {
			
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("add_bank_acct"));

		cashOrCheck = new SRadioButton(getPropertyName("payment_type"), 200,
				SConstants.cashOrCheckList, "intKey", "value");
		cashOrCheck.setHorizontal(true);

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		paymentDao = new EmployeeAdvancePaymentDao();
		userDao=new UserManagementDao();
		bankDao=new BankAccountDao();

		fromAccountComboField = new SComboField(getPropertyName("from_account"), 200);
		fromAccountComboField
				.setInputPrompt(getPropertyName("select"));
//		loadAccountCombo();

		// workorderSelect=new SListSelect("Work Order Number", 200, null, "id",
		// "comments");
		// workorderSelect.setImmediate(true);
		// workorderSelect.setMultiSelect(true);

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SFormLayout contractorFormLayout = new SFormLayout();

		SFormLayout amountFormLayout = new SFormLayout();
		amountFormLayout.setMargin(true);

		SGridLayout gridLayout = new SGridLayout();
		gridLayout.setColumns(2);
		gridLayout.setRows(1);
		gridLayout.setSpacing(true);
		gridLayout.setStyleName("master_border");

		SGridLayout buttonGridLayout = new SGridLayout();
		buttonGridLayout.setColumns(8);
		buttonGridLayout.setRows(1);
		buttonGridLayout.setSpacing(true);
		buttonGridLayout.setSizeFull();

		SHorizontalLayout buttonFormLayout = new SHorizontalLayout();
		buttonFormLayout.setSpacing(true);

		// creditOrDebit=new SRadioButton("", 200, SConstants.creditOrDebit,
		// "intKey", "value");
		// creditOrDebit.setValue(1);

		paymentIdComboField = new SComboField(null, 200);
		paymentIdComboField
				.setInputPrompt(getPropertyName("create_new"));
		loadPaymentNo(0);

		
			List list = null;
			if (settings.isSHOW_ALL_EMPLOYEES_ON_PAYROLL())
				list = userDao.getUsersFromOrganization(getOrganizationID());
			else
				list = userDao
						.getUsersFromOffice(getOfficeID());

			employeComboField = new SComboField(
					getPropertyName("employee"), 200, list, "id", "first_name");
	
		employeComboField
				.setInputPrompt(getPropertyName("select"));

		dateField = new SDateField(getPropertyName("date"), 100);
		dateField.setValue(getWorkingDate());
		chequeDateField = new SDateField(getPropertyName("cheque_date"), 100);
		chequeDateField.setValue(getWorkingDate());
		chequeNoField = new STextField(
				getPropertyName("cheque_no"), 200);
		chequeDateField.setVisible(false);
		chequeNoField.setVisible(false);
//		amountField = new STextField(
//				getPropertyName("employee_advance_payment"), 200);
//		amountField.setStyleName("textfield_align_right");
//		amountField.setValue("0.00");
//		discountField = new STextField(getPropertyName("deductions"), 200);
//		discountField.setStyleName("textfield_align_right");
//		discountField.setValue("0.00");
		paymentAmountField = new SCurrencyField(getPropertyName("payment_amount"),150,getWorkingDate());
		paymentAmountField.setValue(0);
		descriptionField = new STextArea(getPropertyName("details"), 200, 50);
//		currencyComboField = new SComboField(getPropertyName("currency"), 200);
//		loadCurrecny();

		attach = new DocumentAttach(SConstants.documentAttach.CHEQUE);

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("payment_no"));
		salLisrLay.addComponent(paymentIdComboField);
		salLisrLay.addComponent(createNewButton);
		contractorFormLayout.addComponent(salLisrLay);

		contractorFormLayout.addComponent(employeComboField);
		contractorFormLayout.addComponent(cashOrCheck);
		contractorFormLayout.addComponent(fromAccountComboField);
		contractorFormLayout.addComponent(chequeNoField);
		contractorFormLayout.addComponent(chequeDateField);
		

		// workorderSelect.setHeight("80");

		amountFormLayout.addComponent(dateField);
//		amountFormLayout.addComponent(amountField);
//		amountFormLayout.addComponent(discountField);
		amountFormLayout.addComponent(paymentAmountField);
		amountFormLayout.addComponent(descriptionField);
		amountFormLayout.addComponent(attach);
//		amountFormLayout.addComponent(currencyComboField);

		gridLayout.addComponent(contractorFormLayout);
		gridLayout.addComponent(amountFormLayout);

		saveButton = new SButton(getPropertyName("save"));
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		updateButton = new SButton(getPropertyName("update"));
		updateButton.setVisible(false);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		deleteButton = new SButton(getPropertyName("delete"));
		deleteButton.setVisible(false);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		cancelButton = new SButton(getPropertyName("cancel"));
		cancelButton.setVisible(false);
		cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		cancelButton.setStyleName("deletebtnStyle");

		buttonFormLayout.addComponent(saveButton);
		buttonFormLayout.addComponent(updateButton);
		if (settings.isKEEP_DELETED_DATA())
			buttonFormLayout.addComponent(cancelButton);
		else
			buttonFormLayout.addComponent(deleteButton);
		buttonGridLayout.addComponent(buttonFormLayout, 4, 0);

		mainFormLayout.addComponent(gridLayout);
		mainFormLayout.addComponent(buttonGridLayout);
		
		
		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getButton().getId().equals("1")) {
					try {
						saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)paymentIdComboField.getValue(),confirmBox.getUserID());
						Notification.show("Success",
								"Session Saved Successfully..!",
								Type.WARNING_MESSAGE);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				confirmBox.close();
			}
		};
		
		confirmBox.setClickListener(confirmListener);
		
		ClickListener clickListnr=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
					if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")) {
						saveSessionActivity(getOptionId(), (Long)paymentIdComboField.getValue(),
								"Employee Advance Payment : No. "+paymentIdComboField.getItemCaption(paymentIdComboField.getValue()));
						Notification.show("Success",
								"Session Saved Successfully..!",
								Type.WARNING_MESSAGE);
					}
					else
						Notification.show("Select an Invoice..!",
								"Select an Invoice for save in session",
								Type.HUMANIZED_MESSAGE);
				}
				else if(event.getButton().getId().equals(windowNotif.REPORT_ISSUE)) {
					if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")) {
						confirmBox.open();
					}
					else
						Notification.show("Select an Invoice..!", "Select an Invoice for Save in session",
								Type.HUMANIZED_MESSAGE);
				}
				else {
					try {
						helpPopup=new SHelpPopupView(getOptionId());
						popupLay.removeAllComponents();
						popupLay.addComponent(helpPopup);
						helpPopup.setPopupVisible(true);
						helpPopup.setHideOnMouseOut(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
		windowNotif.setClickListener(clickListnr);
		

		cashOrCheck.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent arg0) {
				try {
					if ((Integer) cashOrCheck.getValue() == 1){
						chequeDateField.setVisible(false);
						chequeNoField.setVisible(false);
						attach.setVisible(false);
					}else{
						attach.setVisible(true);
						chequeDateField.setVisible(true);
						chequeNoField.setVisible(true);
					}
					loadAccountCombo();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cashOrCheck.setValue((int) 1);
		createNewButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				paymentIdComboField.setValue((long) 0);
			}
		});

		
		employeComboField
				.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						employeComboField.setComponentError(null);

						Date today = CommonUtil.getCurrentSQLDate();
						Date first = Date
								.valueOf(today.toString().split("-")[0] + "-"
										+ today.toString().split("-")[1]
										+ "-01");

						if (employeComboField.getValue() != null) {
							try {
								employeComboField.setDescription("<i class='ledger_bal_style'>Total Advance Payed on this month : "
										+ paymentDao
												.getThisMonthPayed(
														(Long) employeComboField
																.getValue(),
														first, today) + "</i>");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				});

		
		saveButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					
					if(settings.getSALARY_ADVANCE_ACCOUNT()!=0){
					if (isValid()) {
						// int typ=(Integer) creditOrDebit.getValue();
						// long acct= getSettings().getSALARY_PAYMENT_ACCOUNT();

						/*
						 * if(typ==1) {
						 * liability_or_cash=getSettings().getSALARY_PAYMENT_ACCOUNT
						 * (); } else {
						 * liability_or_cash=getSettings().getLIABILITY_ACCOUNT
						 * (); }
						 */

						EmployeeAdvancePaymentModel paymentModel = new EmployeeAdvancePaymentModel();
						paymentModel.setPayment_id(getNextSequence(
								"EmployeeAdvancePayment_Payment_Id",
								getLoginID()));
						paymentModel
								.setCurrency(new CurrencyModel(paymentAmountField.getCurrency()));
						paymentModel.setConversionRate(paymentAmountField.getConversionRate());
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setChequeDate(CommonUtil
								.getSQLDateFromUtilDate(chequeDateField.getValue()));
						paymentModel.setChequeNo(chequeNoField.getValue());
						paymentModel.setDescription(descriptionField.getValue());
//						paymentModel.setDiscount(toDouble(discountField
//								.getValue()));
						paymentModel.setAccount_id((Long) fromAccountComboField
								.getValue());
						paymentModel
								.setLogin_id(getLoginID());
						paymentModel.setUser(new UserModel(toLong(employeComboField.getValue().toString())));
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
//						paymentModel
//								.setPayment_amount(toDouble(paymentAmountField
//										.getValue().toString()));
						paymentModel.setCash_or_check((Integer) cashOrCheck
								.getValue());
						paymentModel.setAmount(paymentAmountField.getValue());
						paymentModel.setType(1);
						paymentModel.setActive(true);

						FinTransaction transaction = new FinTransaction();
							transaction.addTransaction(SConstants.DR,
									(Long) fromAccountComboField.getValue(),
									getSettings().getSALARY_ADVANCE_ACCOUNT(),
									roundNumber(paymentAmountField
											.getValue()),"",paymentAmountField.getCurrency(),paymentAmountField.getConversionRate());
						paymentDao.save(
								paymentModel,
								transaction
										.getTransaction(
												SConstants.EMPLOYEE_ADVANCE_PAYMENTS,
												CommonUtil
														.getSQLDateFromUtilDate(dateField
																.getValue())));
						attach.saveDocument(paymentModel.getId(), getOfficeID(),
								SConstants.EMPLOYEE_ADVANCE_PAYMENTS);

						saveActivity(
								getOptionId(),
								"Employee Advance Payment Saved. Payment No : "
										+ paymentModel.getPayment_id()
										+ ", Employee : "
										+ employeComboField
												.getItemCaption(employeComboField
														.getValue())
										+ ", Payment Amount : "
										+ roundNumber(paymentAmountField
												.getValue()),paymentModel.getId());

						loadPaymentNo(paymentModel.getId());

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
					}
					
					}else{
						SNotification.show(getPropertyName("account_settings_not_set_salary_advance"),Type.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});

		
		updateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				try {
					if (isValid()) {

						EmployeeAdvancePaymentModel paymentModel = paymentDao.getEmployeeAdvancePaymentModel(toLong(paymentIdComboField.getValue().toString()));

						if(paymentModel.getSalary_id()==0){
							paymentModel.setCurrency(new CurrencyModel(paymentAmountField.getCurrency()));
							paymentModel.setConversionRate(paymentAmountField.getConversionRate());
							paymentModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							paymentModel.setDescription(descriptionField.getValue());
							paymentModel.setChequeDate(CommonUtil
									.getSQLDateFromUtilDate(chequeDateField.getValue()));
							paymentModel.setChequeNo(chequeNoField.getValue());
							paymentModel.setCash_or_check((Integer)cashOrCheck.getValue());
//							paymentModel.setDiscount(toDouble(discountField.getValue()));
							paymentModel.setAccount_id((Long) fromAccountComboField.getValue());
							paymentModel.setLogin_id(getLoginID());
							paymentModel.setUser(new UserModel(toLong(employeComboField.getValue().toString())));
							paymentModel.setOffice(new S_OfficeModel(getOfficeID()));
							paymentModel.setAmount(paymentAmountField.getValue());
//							paymentModel.setAmount(toDouble(amountField.getValue().toString()));
							paymentModel.setType(1);
							paymentModel.setActive(true);

							FinTransaction transaction = new FinTransaction();
							transaction.addTransaction(SConstants.DR,
									(Long) fromAccountComboField.getValue(),
									getSettings().getSALARY_ADVANCE_ACCOUNT(),
									roundNumber(paymentAmountField
											.getValue()),"",paymentAmountField.getCurrency(),paymentAmountField.getConversionRate());

							TransactionModel tran = paymentDao
									.getTransaction(paymentModel
											.getTransaction_id());
							tran.setTransaction_details_list(transaction
									.getChildList());
							tran.setDate(paymentModel.getDate());
							tran.setLogin_id(getLoginID());

							paymentDao.update(paymentModel, tran);
							attach.saveDocument(
									(Long) paymentIdComboField.getValue(),
									getOfficeID(),
									SConstants.EMPLOYEE_ADVANCE_PAYMENTS);

							saveActivity(
									getOptionId(),
									"Employee Advance Payment Updated. Payment No : "
											+ paymentModel.getPayment_id()
											+ ", Employee : "
											+ employeComboField
													.getItemCaption(employeComboField
															.getValue())
											+ ", Payment Amount : "
											+ roundNumber(paymentAmountField
													.getValue()),paymentModel.getId());

							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);

							SNotification.show(getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
						}
						else{
							SNotification.show(getPropertyName("data_used_salary_disbursal"));
						}
					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		
		deleteButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")){
					ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {

						@Override
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								try {
									EmployeeAdvancePaymentModel paymentModel = paymentDao.getEmployeeAdvancePaymentModel(toLong(paymentIdComboField.getValue().toString()));
									if(paymentModel.getSalary_id()==0){
										paymentDao.delete(toLong(paymentIdComboField.getValue().toString()));
										attach.deleteDocument(toLong(paymentIdComboField.getValue().toString()),
												getOfficeID(),
												SConstants.EMPLOYEE_ADVANCE_PAYMENTS);
		
										saveActivity(getOptionId(),
												"Employee Advance Payment Deleted. Payment No : "
														+ paymentIdComboField.getItemCaption(paymentIdComboField.getValue())
														+ ", Employee : "
														+ employeComboField.getItemCaption(employeComboField.getValue())
														+ ", Payment Amount : "
														+ roundNumber(paymentAmountField
																.getValue()),(Long)paymentIdComboField
																.getValue());
		
										loadPaymentNo(0);
										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);
		
										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);
										cancelButton.setVisible(false);
									}
									else{
										SNotification.show(getPropertyName("data_used_salary_disbursal"));
									}
		
								}
								catch (Exception e) {
									SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		});
	
		
		cancelButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), "Are you sure?",
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.cancel(toLong(paymentIdComboField
														.getValue().toString()));
										attach.deleteDocument(
												toLong(paymentIdComboField
														.getValue().toString()),
												getOfficeID(),
												SConstants.EMPLOYEE_ADVANCE_PAYMENTS);

										saveActivity(
												getOptionId(),
												"Employee Advance Payment Deleted. Payment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Employee : "
														+ employeComboField
																.getItemCaption(employeComboField
																		.getValue())
														+ ", Payment Amount : "
														+ roundNumber(paymentAmountField
																.getValue()),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);
										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);
										cancelButton.setVisible(false);

									} catch (Exception e) {
										SNotification.show(
												getPropertyName("error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		
		paymentIdComboField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				try {
					attach.clear();

					if (paymentIdComboField.getValue() != null
							&& !paymentIdComboField.getValue().toString()
									.equals("0")) {
						EmployeeAdvancePaymentModel model = paymentDao
								.getEmployeeAdvancePaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						if (model != null) {
							cashOrCheck.setValue(model.getCash_or_check());
							fromAccountComboField.setValue(model
									.getAccount_id());
							employeComboField.setValue(model
									.getUser().getId());
							descriptionField.setValue(model.getDescription());
							dateField.setValue(model.getDate());
							paymentAmountField.setValue(model.getCurrency().getId(),model
									.getAmount());
							chequeDateField.setValue(model.getChequeDate());
							chequeNoField.setValue(model.getChequeNo());
//							discountField.setValue(String.valueOf(model
//									.getDiscount()));
//							paymentAmountField.setValue(String.valueOf(model
//									.getPayment_amount()));
//							currencyComboField.setValue(model.getCurrency()
//									.getId());

							// creditOrDebit.setValue(model.getType());
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);

							attach.loadDocument(
									(Long) paymentIdComboField.getValue(),
									getOfficeID(),
									SConstants.EMPLOYEE_ADVANCE_PAYMENTS);

						}
					} else {
						cashOrCheck.setValue((int) 1);
						employeComboField.setValue(null);
						descriptionField.setValue("");
						dateField.setValue(getWorkingDate());
//						amountField.setValue("0.00");
//						discountField.setValue("0.00");
						paymentAmountField.setValue(getCurrencyID(),0);
//						currencyComboField.setValue(getCurrencyID());
						chequeDateField.setValue(getWorkingDate());
						chequeNoField.setValue("");

						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						// creditOrDebit.setValue(1);
					}

					if (!isFinYearBackEntry()) {
						saveButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						if (paymentIdComboField.getValue() == null
								|| paymentIdComboField.getValue().toString()
										.equals("0")) {
							Notification.show(
									getPropertyName("warning_transaction"),
									Type.WARNING_MESSAGE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
//		amountField.setImmediate(true);

		
//		amountField.addListener(new Property.ValueChangeListener() {
//			public void valueChange(ValueChangeEvent event) {
//				try {
//					calculateNetPrice();
//				} catch (Exception e) {
//					e.printStackTrace();
//					Notification.show("Error..!!",
//							"Error Message :" + e.getCause(),
//							Type.ERROR_MESSAGE);
//				}
//
//			}
//		});

		
//		discountField.setImmediate(true);
//
//		
//		discountField.addListener(new Property.ValueChangeListener() {
//			public void valueChange(ValueChangeEvent event) {
//				try {
//					calculateNetPrice();
//				} catch (Exception e) {
//					e.printStackTrace();
//					Notification.show(getPropertyName("error"),
//							Type.ERROR_MESSAGE);
//				}
//
//			}
//		});
		
		addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {

				if (saveButton.isVisible())
					saveButton.click();
				else
					updateButton.click();
			}
		});

		if (!isFinYearBackEntry()) {
			saveButton.setVisible(false);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			cancelButton.setVisible(false);
			Notification.show(getPropertyName("warning_transaction"),
					Type.WARNING_MESSAGE);
		}

		SVerticalLayout hLayout=new SVerticalLayout();
		hLayout.addComponent(popupLay);
		hLayout.addComponent(mainFormLayout);

		windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
		panel.setContent(windowNotif);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return panel;
	}

	
//	public void calculateNetPrice() {
//		double custAmt = 0, payAmt = 0, disc = 0;
//
//		try {
//			custAmt = Double.parseDouble(amountField.getValue());
//			disc = Double.parseDouble(discountField.getValue());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		paymentAmountField.setNewValue(asString(custAmt - disc));
//	}

	
	@SuppressWarnings("unchecked")
	private void loadAccountCombo() {
		List<Object> list = null;
		try {
			if ((Integer) cashOrCheck.getValue() == 1){
				list = new LedgerDao().
						getAllActiveLedgerNamesOfGroup(getOfficeID(),
								SConstants.LEDGER_ADDED_DIRECTLY,  
								settings.getCASH_GROUP());
			}else
				list = bankDao.getAllActiveBankAccountNamesWithLedgerID(
					getOfficeID());
			
			SCollectionContainer suppContainer = SCollectionContainer.setList(
					list, "id");
			fromAccountComboField.setContainerDataSource(suppContainer);
			fromAccountComboField.setItemCaptionPropertyId("name");
			fromAccountComboField.setValue(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void loadPaymentNo(long paymentId) {
		List<Object> list = new ArrayList<Object>();
		try {
			EmployeeAdvancePaymentModel paymentModel = new EmployeeAdvancePaymentModel();
			paymentModel.setId(0);
			paymentModel.setDescription("---------------Create New---------------");
			list.add(0, paymentModel);
			list.addAll(paymentDao.getPaymnetNo(getOfficeID()));
			container = SCollectionContainer.setList(list, "id");
			paymentIdComboField.setContainerDataSource(container);
			paymentIdComboField.setItemCaptionPropertyId("description");
			paymentIdComboField.setValue(paymentId);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
//	private void loadCurrecny() {
//		List<Object> list = null;
//		try {
//			list = new CurrencyManagementDao().getlabels();
//			container = SCollectionContainer.setList(list, "id");
//			currencyComboField.setContainerDataSource(container);
//			currencyComboField.setItemCaptionPropertyId("name");
//			currencyComboField.setValue(getCurrencyID());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	
	@Override
	public Boolean isValid() {
		clearErrorMessages();
		boolean valid = true;
		if (employeComboField.getValue() == null
				|| employeComboField.getValue().equals("")) {
			setRequiredError(employeComboField,
					"Select an Employee", true);
			valid = false;
		}
		if (fromAccountComboField.getValue() == null
				|| fromAccountComboField.getValue().equals("")) {
			setRequiredError(fromAccountComboField,
					"Select an account", true);
			valid = false;
		}
		
		if (paymentAmountField.getValue()<=0) {
			setRequiredError(paymentAmountField,getPropertyName("invalid_data"), true);
			valid = false;
		}


//		if (!isValidAmount(amountField)) {
//			valid = false;
//		}
//		if (!isValidAmount(discountField)) {
//			valid = false;
//		}
		return valid;
	}

	
	private void clearErrorMessages() {
		employeComboField.setComponentError(null);
		fromAccountComboField.setComponentError(null);
//		amountField.setComponentError(null);
//		discountField.setComponentError(null);
		paymentAmountField.setComponentError(null);
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}
	
	
	@Override
	public SComboField getBillNoFiled() {
		return paymentIdComboField;
	}

}
