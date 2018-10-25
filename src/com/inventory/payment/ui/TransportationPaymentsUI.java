package com.inventory.payment.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.TranspotationDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.dao.SalesManMapDao;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 22, 2013
 */

public class TransportationPaymentsUI extends SparkLogic {

	private static final long serialVersionUID = 8774283694107704131L;

	private SComboField paymentIdComboField;
	private SComboField transportationComboField;
	private SComboField fromAccountComboField;
	private SDateField dateField;
	private STextField amountField;
	private STextField discountField;
	private STextField paymentAmountField;
	private STextArea descriptionField;
	private SComboField currencyComboField;

	SComboField salesManCombo;
	
	private SDateField filterFromField;
	private SDateField filterToField;

	SRadioButton creditOrDebit;
	private SDateField chequeDateField;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private SButton cancelButton;

	// SListSelect workorderSelect;

	private SCollectionContainer container;

	private TransportationPaymentDao paymentDao;

	private SettingsValuePojo settings;

	private WrappedSession session;

	private long paymentId = 0;

	SButton createNewButton;
	private DocumentAttach docAttach;

	SRadioButton cashOrCheck;
	LedgerDao ledgerDao;

	private STextField billNoField;
	private STextField placeField;
	private STextField invoiceAmountField;
	
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
		
		try {
			salesManCombo = new SComboField(getPropertyName("sales_man"), 200,new SalesManMapDao().getUsers(getOfficeID(),SConstants.SALES_MAN), "id", "first_name");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		setSize(780, 520);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		ledgerDao = new LedgerDao();

		chequeDateField = new SDateField(getPropertyName("cheque_date"), 100,
				getDateFormat(), getWorkingDate());
		chequeDateField.setVisible(false);

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Transportation");

		cashOrCheck = new SRadioButton(getPropertyName("payment_type"), 200,
				SConstants.cashOrCheckList, "intKey", "value");
		cashOrCheck.setHorizontal(true);

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		paymentDao = new TransportationPaymentDao();

		cashOrCheck.setValue(1);

		docAttach = new DocumentAttach(SConstants.documentAttach.CHEQUE);
		docAttach.setVisible(false);

		// workorderSelect=new SListSelect("Work Order Number", 200, null, "id",
		// "comments");
		// workorderSelect.setImmediate(true);
		// workorderSelect.setMultiSelect(true);

		fromAccountComboField = new SComboField(getPropertyName("from_account"));

		loadAccountCombo();

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

		creditOrDebit = new SRadioButton("", 200, SConstants.creditOrDebit,
				"intKey", "value");
		creditOrDebit.setValue(2);
		creditOrDebit.setHorizontal(true);

		paymentIdComboField = new SComboField(null, 200);
		paymentIdComboField
				.setInputPrompt(getPropertyName("create_new"));
		loadPaymentNo(0);

		transportationComboField = new SComboField(
				getPropertyName("transportation"), 200);
		transportationComboField
				.setInputPrompt(getPropertyName("select"));
		loadTransportationCombo();

		filterFromField = new SDateField(getPropertyName("from_date"), 100,
				getDateFormat(), getWorkingDate());
		filterToField = new SDateField(getPropertyName("to_date"), 100,
				getDateFormat(), getWorkingDate());

		dateField = new SDateField(getPropertyName("date"), 100);
		dateField.setValue(getWorkingDate());
		amountField = new STextField(getPropertyName("transportation_amount"),
				200);
		amountField.setStyleName("textfield_align_right");
		amountField.setValue("0.00");
		discountField = new STextField(getPropertyName("discount"), 200);
		discountField.setStyleName("textfield_align_right");
		discountField.setValue("0.00");
		paymentAmountField = new STextField(
				(getPropertyName("payment_amount")), 200);
		paymentAmountField.setStyleName("textfield_align_right");
		paymentAmountField.setValue("0.00");
		descriptionField = new STextArea(getPropertyName("taxi_details"), 200,
				30);
		currencyComboField = new SComboField(getPropertyName("currency"), 200);
		loadCurrecny();

		billNoField = new STextField(getPropertyName("bill_no"), 200);
		placeField = new STextField(getPropertyName("place"), 200);
		invoiceAmountField = new STextField(getPropertyName("invoice_amount"),
				200);
		invoiceAmountField.setStyleName("textfield_align_right");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("payment_no"));
		salLisrLay.addComponent(paymentIdComboField);
		salLisrLay.addComponent(createNewButton);

		SHorizontalLayout dateLay = new SHorizontalLayout();
		dateLay.addComponent(filterFromField);
		dateLay.addComponent(filterToField);

		contractorFormLayout.addComponent(salLisrLay);
		contractorFormLayout.addComponent(transportationComboField);
		contractorFormLayout.addComponent(salesManCombo);
		contractorFormLayout.addComponent(descriptionField);
		contractorFormLayout.addComponent(dateLay);
		contractorFormLayout.addComponent(billNoField);
		contractorFormLayout.addComponent(placeField);
		contractorFormLayout.addComponent(invoiceAmountField);

		// workorderSelect.setHeight("80");

		amountFormLayout.addComponent(creditOrDebit);
		amountFormLayout.addComponent(cashOrCheck);
		amountFormLayout.addComponent(fromAccountComboField);
		amountFormLayout.addComponent(chequeDateField);
		amountFormLayout.addComponent(dateField);
		amountFormLayout.addComponent(amountField);
		amountFormLayout.addComponent(discountField);
		amountFormLayout.addComponent(paymentAmountField);
		amountFormLayout.addComponent(currencyComboField);
		amountFormLayout.addComponent(docAttach);

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

		chequeDateField.setValue(getWorkingDate());

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
								"Transportation Payment : No. "+paymentIdComboField.getItemCaption(paymentIdComboField.getValue()));
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
		
		transportationComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							if (transportationComboField.getValue() != null) {
								transportationComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
										+ ledgerDao
												.getLedgerCurrentBalance((Long) transportationComboField
														.getValue()) + "</i>");
							} else
								transportationComboField.setDescription(null);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		createNewButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				paymentIdComboField.setValue((long) 0);
			}
		});

		creditOrDebit.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (creditOrDebit.getValue().toString().equals("1")) {
					cashOrCheck.setVisible(true);
					cashOrCheck.setValue((int) 2);
					cashOrCheck.setValue((int) 1);
					fromAccountComboField.setVisible(true);
				} else {
					cashOrCheck.setVisible(false);
					cashOrCheck.setValue((int) 2);
					cashOrCheck.setValue((int) 1);
					fromAccountComboField.setVisible(false);
				}
			}
		});

		cashOrCheck.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				if (cashOrCheck.getValue().toString().equals("2")) {
					fromAccountComboField.setVisible(true);
					chequeDateField.setVisible(true);
					docAttach.setVisible(true);
					chequeDateField.setValue(getWorkingDate());
				} else {
					// fromAccountComboField.setVisible(false);
					chequeDateField.setVisible(false);
					docAttach.setVisible(false);
				}

				loadAccountCombo();
			}
		});

		creditOrDebit.setValue((int) 1);
		cashOrCheck.setValue((int) 1);

		saveButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {
						int typ = (Integer) creditOrDebit.getValue();
						int cashORcheck = (Integer) cashOrCheck.getValue();

						long transp_id = toLong(transportationComboField
								.getValue().toString());

						long cash = getSettings().getCASH_ACCOUNT();
//						long liability = getSettings().getLIABILITY_ACCOUNT();

						long bank_orCash_id = 0;
						if (fromAccountComboField.getValue() != null)
							bank_orCash_id = (Long) fromAccountComboField
									.getValue();

						if (typ == 1) {
							if (bank_orCash_id != 0)
								cash = bank_orCash_id;
						}

						TransportationPaymentModel paymentModel = new TransportationPaymentModel();
						paymentModel.setPayment_id(getNextSequence(
								"Transportation_Payment_Id", getLoginID()));
						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel.setDiscount(toDouble(discountField
								.getValue()));
						paymentModel.setFrom_account_id(bank_orCash_id);
						paymentModel.setTransportation_id(transp_id);
						paymentModel.setCheque_date(CommonUtil
								.getSQLDateFromUtilDate(chequeDateField
										.getValue()));
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel
								.setPayment_amount(toDouble(paymentAmountField
										.getValue().toString()));
						paymentModel.setSupplier_amount(toDouble(amountField
								.getValue().toString()));
						paymentModel.setType(typ);
						paymentModel.setActive(true);
						paymentModel.setCash_or_check((Integer) cashOrCheck
								.getValue());
						paymentModel
								.setInvoiceAmount(toDouble(invoiceAmountField
										.getValue().toString()));
						paymentModel.setBillNo(billNoField.getValue());
						paymentModel.setPlace(placeField.getValue());

						paymentModel.setFrom_date(CommonUtil
								.getSQLDateFromUtilDate(filterFromField
										.getValue()));
						if(salesManCombo.getValue()!=null && !salesManCombo.getValue().toString().equals(""))
							paymentModel.setSales_person((Long)salesManCombo.getValue());
						else
							paymentModel.setSales_person(0);
						
						paymentModel.setTo_date(CommonUtil
								.getSQLDateFromUtilDate(filterToField
										.getValue()));

						FinTransaction transaction = new FinTransaction();
						if (paymentModel.getType() == 1) {
							transaction.addTransaction(SConstants.DR, cash,
									transp_id,
									roundNumber(toDouble(paymentAmountField
											.getValue().toString())));
						} else {
//							transaction.addTransaction(SConstants.DR,
//									transp_id, liability,
//									roundNumber(toDouble(paymentAmountField
//											.getValue().toString())));
						}

						paymentId = paymentDao.save(paymentModel, transaction
								.getTransaction(
										SConstants.TRANSPORTATION_PAYMENTS,
										paymentModel.getDate()));

						docAttach.saveDocument(paymentId, getOfficeID(),
								SConstants.TRANSPORTATION_PAYMENTS);

						saveActivity(
								getOptionId(),
								"Transportation Payment Saved. Bill No : "
										+ paymentModel.getPayment_id()
										+ ", Transportation Acct. : "
										+ transportationComboField
												.getItemCaption(transportationComboField
														.getValue())
										+ ", Payment Amount : "
										+ roundNumber(toDouble(paymentAmountField
												.getValue())),paymentModel.getId());

						loadPaymentNo(paymentId);

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
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
						int cashORcheck = (Integer) cashOrCheck.getValue();
						int typ = (Integer) creditOrDebit.getValue();
						long transp_id = toLong(transportationComboField
								.getValue().toString());

						long cash = getSettings().getCASH_ACCOUNT();
//						long liability = getSettings().getLIABILITY_ACCOUNT();

						TransportationPaymentModel paymentModel = paymentDao
								.getTransportationPaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						long bank_orCash_id = 0;
						if (fromAccountComboField.getValue() != null)
							bank_orCash_id = (Long) fromAccountComboField
									.getValue();

						if (typ == 1) {
							if (bank_orCash_id != 0)
								cash = bank_orCash_id;
						}

						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setCheque_date(CommonUtil
								.getSQLDateFromUtilDate(chequeDateField
										.getValue()));
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel.setDiscount(toDouble(discountField
								.getValue()));
						paymentModel.setFrom_account_id(bank_orCash_id);
						paymentModel.setTransportation_id(transp_id);
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel
								.setPayment_amount(toDouble(paymentAmountField
										.getValue().toString()));
						paymentModel.setSupplier_amount(toDouble(amountField
								.getValue().toString()));
						paymentModel.setFrom_date(CommonUtil
								.getSQLDateFromUtilDate(filterFromField
										.getValue()));
						if(salesManCombo.getValue()!=null && !salesManCombo.getValue().toString().equals(""))
							paymentModel.setSales_person((Long)salesManCombo.getValue());
						else
							paymentModel.setSales_person(0);
						paymentModel.setTo_date(CommonUtil
								.getSQLDateFromUtilDate(filterToField
										.getValue()));
						paymentModel.setType(typ);
						paymentModel.setActive(true);
						paymentModel.setCash_or_check((Integer) cashOrCheck
								.getValue());
						paymentModel
								.setInvoiceAmount(toDouble(invoiceAmountField
										.getValue().toString()));
						paymentModel.setBillNo(billNoField.getValue());
						paymentModel.setPlace(placeField.getValue());

						FinTransaction transaction = new FinTransaction();
						if (paymentModel.getType() == 1) {
							transaction.addTransaction(SConstants.DR, cash,
									transp_id,
									roundNumber(toDouble(paymentAmountField
											.getValue().toString())));
						} else {
//							transaction.addTransaction(SConstants.DR,
//									transp_id, liability,
//									roundNumber(toDouble(paymentAmountField
//											.getValue().toString())));
						}

						TransactionModel tran = paymentDao
								.getTransaction(paymentModel
										.getTransaction_id());
						tran.setTransaction_details_list(transaction
								.getChildList());
						tran.setDate(paymentModel.getDate());
						tran.setLogin_id(getLoginID());

						paymentDao.update(paymentModel, tran);

						docAttach.saveDocument(toLong(paymentIdComboField
								.getValue().toString()), getOfficeID(),
								SConstants.TRANSPORTATION_PAYMENTS);

						saveActivity(
								getOptionId(),
								"Transportation Payment Updated. Bill No : "
										+ paymentIdComboField
												.getItemCaption(paymentIdComboField
														.getValue())
										+ ", Transportation Acct. : "
										+ transportationComboField
												.getItemCaption(transportationComboField
														.getValue())
										+ ", Payment Amount : "
										+ roundNumber(toDouble(paymentAmountField
												.getValue())),(Long)paymentIdComboField
												.getValue());

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("update_success"),
								Type.WARNING_MESSAGE);

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
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.delete(toLong(paymentIdComboField
														.getValue().toString()));

										docAttach
												.deleteDocument(
														toLong(paymentIdComboField
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.TRANSPORTATION_PAYMENTS);

										saveActivity(
												getOptionId(),
												"Transportation Payment Deleted. Bill No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Transportation Acct. : "
														+ transportationComboField
																.getItemCaption(transportationComboField
																		.getValue())
														+ ", Payment Amount : "
														+ roundNumber(toDouble(paymentAmountField
																.getValue())),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);
										SNotification.show("Success",
												"Deleted Successfully",
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

		cancelButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.cancel(toLong(paymentIdComboField
														.getValue().toString()));

										docAttach
												.deleteDocument(
														toLong(paymentIdComboField
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.TRANSPORTATION_PAYMENTS);

										saveActivity(
												getOptionId(),
												"Transportation Payment Deleted. Bill No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Transportation Acct. : "
														+ transportationComboField
																.getItemCaption(transportationComboField
																		.getValue())
														+ ", Payment Amount : "
														+ roundNumber(toDouble(paymentAmountField
																.getValue())),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);
										SNotification.show("Success",
												"Deleted Successfully",
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

					if (paymentIdComboField.getValue() != null
							&& !paymentIdComboField.getValue().toString()
									.equals("0")) {
						TransportationPaymentModel model = paymentDao
								.getTransportationPaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						if (model != null) {

							creditOrDebit.setValue(model.getType());
							cashOrCheck.setValue(model.getCash_or_check());
							transportationComboField.setValue(model
									.getTransportation_id());
							descriptionField.setValue(model.getDescription());
							dateField.setValue(model.getDate());
							amountField.setValue(String.valueOf(model
									.getSupplier_amount()));
							discountField.setValue(String.valueOf(model
									.getDiscount()));
							paymentAmountField.setValue(String.valueOf(model
									.getPayment_amount()));
							currencyComboField.setValue(model.getCurrency()
									.getId());
							fromAccountComboField.setValue(model
									.getFrom_account_id());

							if(model.getSales_person()!=0)
								salesManCombo.setValue(model.getSales_person());
							else
								salesManCombo.setValue(null);
							
							filterFromField.setValue(model.getFrom_date());
							filterToField.setValue(model.getTo_date());

							billNoField.setValue(model.getBillNo());
							placeField.setValue(model.getPlace());
							invoiceAmountField.setValue(model
									.getInvoiceAmount() + "");

							chequeDateField.setValue(model.getCheque_date());

							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);

							docAttach.loadDocument(
									(Long) paymentIdComboField.getValue(),
									getOfficeID(),
									SConstants.TRANSPORTATION_PAYMENTS);

						}
					} else {

						creditOrDebit.setValue((int) 1);
						cashOrCheck.setValue((int) 1);
						salesManCombo.setValue(null);
						transportationComboField.setValue(null);
						descriptionField.setValue("");
						dateField.setValue(getWorkingDate());
						amountField.setValue("0.00");
						discountField.setValue("0.00");
						paymentAmountField.setValue("0.00");
						currencyComboField.setValue(getCurrencyID());
						chequeDateField.setValue(getWorkingDate());

						billNoField.setValue("");
						placeField.setValue("");
						invoiceAmountField.setValue("");

						filterFromField.setValue(getWorkingDate());
						filterToField.setValue(getWorkingDate());

						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						creditOrDebit.setValue(2);
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

		amountField.setImmediate(true);

		amountField.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					calculateNetPrice();
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("Error..!!",
							"Error Message :" + e.getCause(),
							Type.ERROR_MESSAGE);
				}

			}
		});

		discountField.setImmediate(true);

		discountField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							calculateNetPrice();
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("error"),
									Type.ERROR_MESSAGE);
						}

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
		return panel;
	}

	private void loadAccountCombo() {
		List<Object> list = null;
		try {
			List groupList = new ArrayList();
			if ((Integer) cashOrCheck.getValue() == 1)
				groupList.add(settings.getCASH_GROUP());
			else
				groupList.add(settings.getCASH_GROUP());
			list = new LedgerDao()
					.getAllLedgersUnderGroupAndSubGroupsFromGroupList(
							getOfficeID(), getOrganizationID(), groupList);
			SCollectionContainer suppContainer = SCollectionContainer.setList(
					list, "id");
			fromAccountComboField.setContainerDataSource(suppContainer);
			fromAccountComboField.setItemCaptionPropertyId("name");
			fromAccountComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateNetPrice() {
		double custAmt = 0, payAmt = 0, disc = 0;

		try {
			custAmt = Double.parseDouble(amountField.getValue());
			disc = Double.parseDouble(discountField.getValue());
		} catch (Exception e) {
		}
		paymentAmountField.setNewValue(asString(custAmt - disc));
	}

	public void loadPaymentNo(long paymentId) {
		List<Object> list = new ArrayList<Object>();
		try {
			TransportationPaymentModel paymentModel = new TransportationPaymentModel();
			paymentModel.setId(0);
			paymentModel
					.setDescription("---------------Create New---------------");
			list.add(0, paymentModel);
			list.addAll(paymentDao.getPaymnetNo(getOfficeID()));
			container = SCollectionContainer.setList(list, "id");
			paymentIdComboField.setContainerDataSource(container);
			paymentIdComboField.setItemCaptionPropertyId("description");
			if (paymentId > 0) {
				paymentIdComboField.setValue(paymentId);
			} else {
				paymentIdComboField.setValue(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadCurrecny() {
		List<Object> list = null;
		try {
			list = new CurrencyManagementDao().getlabels();
			container = SCollectionContainer.setList(list, "id");
			currencyComboField.setContainerDataSource(container);
			currencyComboField.setItemCaptionPropertyId("name");
			currencyComboField.setValue(getCurrencyID());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void loadTransportationCombo() {
		List<Object> suppList = null;
		try {
			suppList = new TranspotationDao()
					.getAllActiveTranspotationNamesWithLedgerID(getOfficeID());
			container = SCollectionContainer.setList(suppList, "id");
			transportationComboField.setContainerDataSource(container);
			transportationComboField.setItemCaptionPropertyId("name");
			transportationComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		clearErrorMessages();

		boolean valid = true;
		if (transportationComboField.getValue() == null
				|| transportationComboField.getValue().equals("")) {
			setRequiredError(transportationComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
		else
			setRequiredError(transportationComboField,null, false);
		
		if (!isValidAmount(amountField)) {
			valid = false;
		}
		if (!isValidAmount(discountField)) {
			valid = false;
		}
		if (!isValidAmount(paymentAmountField)) {
			valid = false;
		}

		if (paymentAmountField.getValue() == null
				|| paymentAmountField.getValue().equals("")) {
			setRequiredError(paymentAmountField, "Enter a valid amount", true);
			valid = false;
		} else {
			try {
				if (Double
						.parseDouble(paymentAmountField.getValue().toString()) <= 0) {
					setRequiredError(paymentAmountField,
							"Enter a valid amount", true);
					valid = false;
				}
			} catch (Exception e) {
				setRequiredError(paymentAmountField,
						getPropertyName("enter_valid_amount"), true);
				e.printStackTrace();
				valid = false;
			}
		}

		if (invoiceAmountField.getValue() == null
				|| invoiceAmountField.getValue().equals("")) {
			setRequiredError(invoiceAmountField,
					getPropertyName("invalid_amount"), true);
			valid = false;
		} else {
			try {
				if (toDouble(invoiceAmountField.getValue().toString()) <= 0) {
					setRequiredError(invoiceAmountField,
							getPropertyName("invalid_amount"), true);
					valid = false;
				}
			} catch (Exception e) {
				setRequiredError(invoiceAmountField,
						getPropertyName("invalid_amount"), true);
				valid = false;
			}
		}

		int cashORcheck = (Integer) cashOrCheck.getValue();
		int typ = (Integer) creditOrDebit.getValue();
		if (typ == 1) {
			if (fromAccountComboField.getValue() == null
					|| fromAccountComboField.getValue().equals("")) {
				setRequiredError(fromAccountComboField,
						getPropertyName("select_account"), true);
				valid = false;
			} else
				setRequiredError(fromAccountComboField, "", false);

			if (cashORcheck == 2) {
				if (chequeDateField.getValue() == null
						|| chequeDateField.getValue().equals("")) {
					setRequiredError(chequeDateField,
							getPropertyName("select_account"), true);
					valid = false;
				} else
					setRequiredError(chequeDateField, "", false);
			}
		}

		return valid;
	}

	private void clearErrorMessages() {
		transportationComboField.setComponentError(null);
		salesManCombo.setComponentError(null);
		amountField.setComponentError(null);
		discountField.setComponentError(null);
		paymentAmountField.setComponentError(null);
		invoiceAmountField.setComponentError(null);
	}

	private boolean isValidAmount(STextField field) {
		boolean valid = true;
		if (field.getValue() == null || field.getValue().equals("")) {
			setRequiredError(field, getPropertyName("enter_valid_amount"), true);
			valid = false;
		} else {
			try {
				Double.parseDouble(field.getValue().toString());
				valid = true;
			} catch (Exception e) {
				setRequiredError(field, getPropertyName("enter_valid_amount"),
						true);
				e.printStackTrace();
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public SComboField getPaymentIdComboField() {
		return paymentIdComboField;
	}

	public void setPaymentIdComboField(SComboField paymentIdComboField) {
		this.paymentIdComboField = paymentIdComboField;
	}

	@Override
	public SComboField getBillNoFiled() {
		return paymentIdComboField;
	}
}
