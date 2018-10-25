package com.inventory.expenditureposting.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.expenditureposting.dao.ExpenditurePaymentSetupDao;
import com.inventory.expenditureposting.model.BatchExpenditurePaymentDetailsModel;
import com.inventory.expenditureposting.model.BatchExpenditurePaymentMasterModel;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupDetailsModel;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupModel;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T. WebSpark. Apr 7 2014
 */

public class ExpenditurePaymentsPayUI extends SparkLogic {

	private static final long serialVersionUID = 2561215323382808817L;

	SPanel panel = null;

	static String TSR_SN = "SN";

	static String TSJ_TYPE_ID = "Type ID";
	static String TSJ_TYPE_NAME = "Type Name";
	static String TSJ_FROM_LEDGER_ID = "From Account ID";
	static String TSJ_FROM_LEDGER_NAME = "From Account";
	static String TSJ_TO_LEDGER_ID = "To Account ID";
	static String TSJ_TO_LEDGER_NAME = "To Account";
	static String TSJ_REAL_AMOUNT = "Real Amount";
	static String TSJ_AMOUNT = "Amount";
	static String TSJ_NARRATION = "Narration";
	static final long EXPENSE = 4;

	STable accountDepositEntryTable;
	ExpenditurePaymentSetupDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout accountDepositAddGrid;
	SVerticalLayout stkrkVLay;
	STextField amountTextField, narration;

	SComboField batchExpPaymentNumbersCombo;
	SComboField accountDepositNumberList;

	SButton payButton, deleteBtn;

	SDateField date;

	LedgerDao ledgerDao;
	SNativeButton updateItemButton;
	
	SButton createNewButton;

	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub

		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		
		List lst = new ArrayList();
		lst.add(new KeyValue(1, "Expenditure"));
		lst.add(new KeyValue(2, "Salary Payment"));

		daoObj = new ExpenditurePaymentSetupDao();
		ledgerDao = new LedgerDao();

		payButton = new SButton(getPropertyName("pay"), 70);
		payButton.setStyleName("savebtnStyle");
		payButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		deleteBtn = new SButton(getPropertyName("Delete"), 70);
		deleteBtn.setStyleName("savebtnStyle");
		deleteBtn.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateItemButton = new SNativeButton(getPropertyName("Update"));

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(payButton);
		mainButtonLayout.addComponent(deleteBtn);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(1000, 500);
		try {

			amountTextField = new STextField(null, 120);
			amountTextField.setStyleName("textfield_align_right");
			narration = new STextField(null, 150);
			
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Add New Receipt");

			accountDepositEntryTable = new STable(null, 800, 160);

			accountDepositNumberList = new SComboField(null, 200,
					daoObj.getAllSetups(getOfficeID()), "id", "group_name");
			batchExpPaymentNumbersCombo = new SComboField(null, 200);

			List actList = null;
			if ((Boolean) getHttpSession().getAttribute(
					"expendeture_acct_cash_only") == true) {
				actList = ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						getSettings().getCASH_GROUP());
			} else {
				actList = new ArrayList();
				actList.addAll(ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						getSettings().getCASH_GROUP()));
			}

			date = new SDateField(null, 100, getDateFormat(), getWorkingDate());

			accountDepositAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			accountDepositEntryTable.setSizeFull();
			accountDepositEntryTable.setSelectable(true);

			accountDepositEntryTable.setWidth("900px");
			accountDepositEntryTable.setHeight("200px");

			accountDepositEntryTable.addContainerProperty(TSR_SN,
					Integer.class, null, "#", null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_FROM_LEDGER_ID,
					Long.class, null, TSJ_FROM_LEDGER_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_FROM_LEDGER_NAME,
					String.class, null, getPropertyName("from_account"), null,
					Align.LEFT);
			accountDepositEntryTable.addContainerProperty(TSJ_TO_LEDGER_ID,
					Long.class, null, TSJ_TO_LEDGER_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_TO_LEDGER_NAME,
					String.class, null, getPropertyName("to_account"), null,
					Align.LEFT);

			accountDepositEntryTable.addContainerProperty(TSJ_TYPE_ID,
					Integer.class, null, TSJ_TYPE_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_TYPE_NAME,
					String.class, null, getPropertyName("type_name"), null,
					Align.LEFT);
			accountDepositEntryTable.addContainerProperty(TSJ_AMOUNT,
					Double.class, null, getPropertyName("amount"), null,
					Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_REAL_AMOUNT,
					Double.class, null, TSJ_REAL_AMOUNT, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_NARRATION,
					String.class, null, getPropertyName("narration"), null,
					Align.CENTER);

			accountDepositEntryTable.setColumnExpandRatio(TSR_SN, (float) .5);
			accountDepositEntryTable.setColumnExpandRatio(TSJ_FROM_LEDGER_NAME,
					2);
			accountDepositEntryTable.setColumnExpandRatio(TSJ_AMOUNT,
					(float) 1.5);

			accountDepositAddGrid.setColumns(8);
			accountDepositAddGrid.setRows(2);
			accountDepositEntryTable.setMultiSelect(false);

			accountDepositAddGrid.setStyleName("journal_adding_grid");

			accountDepositAddGrid.setSpacing(true);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("payment_no")), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(batchExpPaymentNumbersCombo);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("group")), 3, 0);
			masterDetailsGrid.addComponent(accountDepositNumberList, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					5, 0);
			masterDetailsGrid.addComponent(date, 6, 0);

			accountDepositAddGrid.addComponent(new SLabel(null,
					getPropertyName("amount")));
			accountDepositAddGrid.addComponent(amountTextField);
			accountDepositAddGrid.addComponent(new SLabel(null,
					getPropertyName("narration")));
			accountDepositAddGrid.addComponent(narration);
			accountDepositAddGrid.addComponent(updateItemButton);

			masterDetailsGrid.setSpacing(true);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			accountDepositAddGrid.setSpacing(true);
			accountDepositAddGrid.setVisible(false);

			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(accountDepositEntryTable);

			stkrkVLay.addComponent(accountDepositAddGrid);
			stkrkVLay.setComponentAlignment(accountDepositEntryTable,
					Alignment.MIDDLE_CENTER);
			stkrkVLay.setComponentAlignment(accountDepositAddGrid,
					Alignment.MIDDLE_CENTER);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			// stkrkVLay.setComponentAlignment(accountDepositAddGrid,
			// Alignment.BOTTOM_CENTER);

			accountDepositEntryTable.setVisibleColumns(new String[] { TSR_SN,
					TSJ_TYPE_NAME, TSJ_FROM_LEDGER_NAME, TSJ_TO_LEDGER_NAME,
					TSJ_AMOUNT, TSJ_NARRATION });

			accountDepositEntryTable.setFooterVisible(true);
			accountDepositEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(0)));
			accountDepositEntryTable.setColumnFooter(TSJ_FROM_LEDGER_NAME,
					getPropertyName("total"));

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(stkrkVLay);

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			panel.setContent(windowNotif);
			

			accountDepositEntryTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					accountDepositEntryTable.setValue(null);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadData(0);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)batchExpPaymentNumbersCombo.getValue(),confirmBox.getUserID());
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
						if(batchExpPaymentNumbersCombo.getValue()!=null && !batchExpPaymentNumbersCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)batchExpPaymentNumbersCombo.getValue(),
									"Batch Expenditure Transactions : No. "+batchExpPaymentNumbersCombo.getItemCaption(batchExpPaymentNumbersCombo.getValue()));
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
						if(batchExpPaymentNumbersCombo.getValue()!=null && !batchExpPaymentNumbersCombo.getValue().toString().equals("0")) {
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
			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					batchExpPaymentNumbersCombo.setValue((long) 0);
				}
			});

			accountDepositEntryTable
					.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(ValueChangeEvent event) {

							if (accountDepositEntryTable.getValue() != null) {

								Item item = accountDepositEntryTable
										.getItem(accountDepositEntryTable
												.getValue());
								
								amountTextField
										.setValue(asString(item
												.getItemProperty(TSJ_AMOUNT)
												.getValue()));
								narration.setValue(asString(item
										.getItemProperty(TSJ_NARRATION)
										.getValue()));

								if (batchExpPaymentNumbersCombo.getValue() == null
										|| batchExpPaymentNumbersCombo
												.getValue().toString()
												.equals("0")) {
									updateItemButton.setVisible(true);
									accountDepositAddGrid.setVisible(true);
								} else{
									updateItemButton.setVisible(false);
									accountDepositAddGrid.setVisible(false);
								}

								amountTextField.focus();
							} else {
								updateItemButton.setVisible(false);
								accountDepositAddGrid.setVisible(false);
								amountTextField.setValue("0");
								narration.setValue("");
							}
						}
					});

			batchExpPaymentNumbersCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {
								accountDepositEntryTable.removeAllItems();
								if (batchExpPaymentNumbersCombo.getValue() != null
										&& !batchExpPaymentNumbersCombo
												.getValue().toString()
												.equals("0")) {

									BatchExpenditurePaymentMasterModel objModel = daoObj
											.getBatchHistory((Long) batchExpPaymentNumbersCombo
													.getValue());

									date.setValue(objModel.getDate());
									accountDepositNumberList.setValue(objModel
											.getGroup_id());
									accountDepositEntryTable.removeAllItems();
									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN, TSJ_TYPE_ID,
													TSJ_TYPE_NAME,
													TSJ_FROM_LEDGER_ID,
													TSJ_FROM_LEDGER_NAME,
													TSJ_TO_LEDGER_ID,
													TSJ_TO_LEDGER_NAME,
													TSJ_REAL_AMOUNT,
													TSJ_AMOUNT, TSJ_NARRATION });

									String type = "";
									int ct = 0;
									Iterator it = objModel.getDetails_list()
											.iterator();
									while (it.hasNext()) {
										BatchExpenditurePaymentDetailsModel det = (BatchExpenditurePaymentDetailsModel) it
												.next();
										ct++;
										if (det.getType() == 1)
											type = "Expenditure";
										else
											type = "Salary Payment";

										accountDepositEntryTable.addItem(
												new Object[] {
														ct,
														det.getType(),
														type,
														det.getFrom_account()
																.getId(),
														det.getFrom_account()
																.getName(),
														det.getTo_account()
																.getId(),
														det.getTo_account()
																.getName(),
														det.getReal_amount(),
														det.getAmount(),
														det.getComments() }, ct);

									}

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN, TSJ_TYPE_NAME,
													TSJ_FROM_LEDGER_NAME,
													TSJ_TO_LEDGER_NAME,
													TSJ_AMOUNT, TSJ_NARRATION });

									payButton.setVisible(false);
									deleteBtn.setVisible(true);

								} else {
									accountDepositNumberList.setValue(null);
									payButton.setVisible(true);
									deleteBtn.setVisible(false);
								}
								calculateTotals();

							} catch (Exception e) {
							}

						}
					});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(accountDepositEntryTable, null, false);
						if (isAddingValid()) {

							Item item = accountDepositEntryTable
									.getItem(accountDepositEntryTable
											.getValue());

							item.getItemProperty(TSJ_AMOUNT).setValue(
									toDouble(amountTextField.getValue()));
							item.getItemProperty(TSJ_NARRATION).setValue(
									narration.getValue());

							updateItemButton.setVisible(false);
							narration.setValue("");
							amountTextField.setValue("");
							accountDepositEntryTable.setValue(null);

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			payButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							List expList = new ArrayList();
							Hashtable<EmployeeAdvancePaymentModel, TransactionModel> advncSalList = new Hashtable<EmployeeAdvancePaymentModel, TransactionModel>();

							Iterator it = accountDepositEntryTable.getItemIds()
									.iterator();

							BatchExpenditurePaymentMasterModel objMdl = new BatchExpenditurePaymentMasterModel();
							objMdl.setGroup_id((Long) accountDepositNumberList
									.getValue());
							objMdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setOffice_id(getOfficeID());
							objMdl.setNumber(getNextSequence(
									"Batch_Exp_Transactions", getLoginID()));
							List<BatchExpenditurePaymentDetailsModel> detList = new ArrayList<BatchExpenditurePaymentDetailsModel>();
							BatchExpenditurePaymentDetailsModel detObj = null;
							Iterator it1 = accountDepositEntryTable
									.getItemIds().iterator();
							while (it1.hasNext()) {
								Item item = accountDepositEntryTable
										.getItem(it1.next());
								detObj = new BatchExpenditurePaymentDetailsModel();
								int amtType = 1;
								detObj.setAmount((Double) item.getItemProperty(
										TSJ_AMOUNT).getValue());
								detObj.setReal_amount((Double) item
										.getItemProperty(TSJ_REAL_AMOUNT)
										.getValue());
								detObj.setComments((String) item
										.getItemProperty(TSJ_NARRATION)
										.getValue());
								detObj.setFrom_account(new LedgerModel(
										(Long) item.getItemProperty(
												TSJ_FROM_LEDGER_ID).getValue()));
								detObj.setTo_account(new LedgerModel(
										(Long) item.getItemProperty(
												TSJ_TO_LEDGER_ID).getValue()));
								detObj.setType((Integer) item.getItemProperty(
										TSJ_TYPE_ID).getValue());
								detList.add(detObj);
							}
							objMdl.setDetails_list(detList);

							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it
										.next());

								if ((Integer) item.getItemProperty(TSJ_TYPE_ID)
										.getValue() == 1) {

									PaymentDepositModel objPayMdl = new PaymentDepositModel();

									objPayMdl.setDate(CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue()));
									objPayMdl.setLogin_id(getLoginID());
									objPayMdl.setOffice_id(getOfficeID());
									objPayMdl.setRef_no("");
									objPayMdl.setMemo((String) item
											.getItemProperty(TSJ_NARRATION)
											.getValue());
									objPayMdl.setStatus(1);
									objPayMdl
											.setType(SConstants.EXPENDETURE_TRANSACTION);
									objPayMdl.setBill_no(getNextSequence(
											"Expense_Transaction_No",
											getLoginID()));

									FinTransaction tran = new FinTransaction();
									tran.addTransactionWithNarration(
											1,
											(Long) item.getItemProperty(
													TSJ_FROM_LEDGER_ID)
													.getValue(),
											(Long) item.getItemProperty(
													TSJ_TO_LEDGER_ID)
													.getValue(),
											(Double) item.getItemProperty(
													TSJ_AMOUNT).getValue(),
											(String) item.getItemProperty(
													TSJ_NARRATION).getValue());
									objPayMdl.setTransaction(tran
											.getTransaction(
													SConstants.EXPENDETURE_TRANSACTION,
													CommonUtil
															.getSQLDateFromUtilDate(date
																	.getValue())));
									expList.add(objPayMdl);
								} else {

									EmployeeAdvancePaymentModel paymentModel = new EmployeeAdvancePaymentModel();
									paymentModel
											.setPayment_id(getNextSequence(
													"EmployeeAdvancePayment_Payment_Id",
													getLoginID()));
									paymentModel.setCurrency(new CurrencyModel(
											getCurrencyID()));
									paymentModel.setDate(CommonUtil
											.getSQLDateFromUtilDate(date
													.getValue()));
									paymentModel.setDescription((String) item
											.getItemProperty(TSJ_NARRATION)
											.getValue());
									paymentModel
											.setAccount_id((Long) item
													.getItemProperty(
															TSJ_FROM_LEDGER_ID)
													.getValue());
									paymentModel.setLogin_id(daoObj
											.getLoginIDFromLledger((Long) item
													.getItemProperty(
															TSJ_TO_LEDGER_ID)
													.getValue()));
									paymentModel.setOffice(new S_OfficeModel(
											getOfficeID()));
									paymentModel
											.setAmount((Double) item
													.getItemProperty(TSJ_AMOUNT)
													.getValue());
									paymentModel.setAmount((Double) item
											.getItemProperty(TSJ_AMOUNT)
											.getValue());
									paymentModel.setType(1);

									FinTransaction transaction = new FinTransaction();
									transaction
											.addTransaction(
													SConstants.DR,
													(Long) item
															.getItemProperty(
																	TSJ_FROM_LEDGER_ID)
															.getValue(),
													(Long) item
															.getItemProperty(
																	TSJ_TO_LEDGER_ID)
															.getValue(),
													roundNumber((Double) item
															.getItemProperty(
																	TSJ_AMOUNT)
															.getValue()));

									advncSalList.put(
											paymentModel,
											transaction
													.getTransaction(
															SConstants.EMPLOYEE_ADVANCE_PAYMENTS,
															CommonUtil
																	.getSQLDateFromUtilDate(date
																			.getValue())));

								}
							}

							long id = daoObj.payExpenses(expList, advncSalList,
									objMdl);
							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
							loadData(id);

							saveActivity(getOptionId(),
									"Expenditure Transactions Done");

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification
								.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
					}
				}
			});

			deleteBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					ConfirmDialog.show(getUI(), "Are you sure?",
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {

											BatchExpenditurePaymentMasterModel objModel = daoObj
													.getBatchHistory((Long) batchExpPaymentNumbersCombo
															.getValue());

											String[] pays = objModel
													.getExp_transaction_ids()
													.split(";");
											if (pays.length > 0) {
												String[] expIDs = pays[0]
														.trim().split(",");
												String[] advncIDs = pays[1]
														.trim().split(",");

												daoObj.deleteExpenses(expIDs,
														advncIDs, objModel);

												Notification
														.show(getPropertyName("Success"),
																getPropertyName("save_success"),
																Type.WARNING_MESSAGE);
												loadData(0);

											}

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
			});

			accountDepositNumberList
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {
								accountDepositEntryTable.removeAllItems();
								if (accountDepositNumberList.getValue() != null
										&& !accountDepositNumberList.getValue()
												.toString().equals("0")) {

									ExpenditurePaymentSetupModel objModel = daoObj
											.getSetup((Long) accountDepositNumberList
													.getValue());

									date.setValue(getWorkingDate());

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN, TSJ_TYPE_ID,
													TSJ_TYPE_NAME,
													TSJ_FROM_LEDGER_ID,
													TSJ_FROM_LEDGER_NAME,
													TSJ_TO_LEDGER_ID,
													TSJ_TO_LEDGER_NAME,
													TSJ_REAL_AMOUNT,
													TSJ_AMOUNT, TSJ_NARRATION });

									String type = "";
									int ct = 0;
									Iterator it = objModel.getDetails_list()
											.iterator();
									while (it.hasNext()) {
										ExpenditurePaymentSetupDetailsModel det = (ExpenditurePaymentSetupDetailsModel) it
												.next();
										ct++;
										if (det.getType() == 1)
											type = "Expenditure";
										else
											type = "Salary Payment";

										accountDepositEntryTable.addItem(
												new Object[] {
														ct,
														det.getType(),
														type,
														det.getFrom_account()
																.getId(),
														det.getFrom_account()
																.getName(),
														det.getTo_account()
																.getId(),
														det.getTo_account()
																.getName(),
														det.getAmount(),
														det.getAmount(),
														det.getComments() }, ct);

									}

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN, TSJ_TYPE_NAME,
													TSJ_FROM_LEDGER_NAME,
													TSJ_TO_LEDGER_NAME,
													TSJ_AMOUNT, TSJ_NARRATION });

								} else {
								}
								calculateTotals();

							} catch (Exception e) {
							}

						}
					});


			loadData(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return panel;
	}

	public void loadData(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new BatchExpenditurePaymentMasterModel(0, "NEW"));
			list.addAll(daoObj.getAllPaymentHistories(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			batchExpPaymentNumbersCombo.setContainerDataSource(bic);
			batchExpPaymentNumbersCombo
					.setItemCaptionPropertyId("exp_transaction_ids");
			batchExpPaymentNumbersCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public void deleteItem() {
		try {

			if (accountDepositEntryTable.getValue() != null) {

				Collection selectedItems = (Collection) accountDepositEntryTable
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					accountDepositEntryTable.removeItem(it1.next());
				}

				int SN = 0;
				Iterator it = accountDepositEntryTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					Item newitem = accountDepositEntryTable
							.getItem((Integer) it.next());

					newitem.getItemProperty(TSR_SN).setValue(SN);

				}
				calculateTotals();
			}
			accountDepositEntryTable.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void calculateTotals() {
		try {

			double amtttl = 0;

			Iterator it = accountDepositEntryTable.getItemIds().iterator();
			while (it.hasNext()) {
				Item item = accountDepositEntryTable.getItem(it.next());

				amtttl += (Double) item.getItemProperty(TSJ_AMOUNT).getValue();
			}

			accountDepositEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(amtttl)));

		} catch (Exception e) {
			// TODO: handle exception
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public Boolean isAddingValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (amountTextField.getValue() == null
				|| amountTextField.getValue().equals("")) {
			setRequiredError(amountTextField, getPropertyName("invalid_data"),
					true);
			amountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(amountTextField.getValue()) <= 0) {
					setRequiredError(amountTextField,
							getPropertyName("invalid_data"), true);
					amountTextField.focus();
					ret = false;
				} else
					setRequiredError(amountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(amountTextField,
						getPropertyName("invalid_data"), true);
				amountTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		return ret;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_data"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		boolean g = true || false;

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	public SComboField getBatchExpPaymentNumbersCombo() {
		return batchExpPaymentNumbersCombo;
	}

	public void setBatchExpPaymentNumbersCombo(
			SComboField batchExpPaymentNumbersCombo) {
		this.batchExpPaymentNumbersCombo = batchExpPaymentNumbersCombo;
	}

	@Override
	public SComboField getBillNoFiled() {
		return batchExpPaymentNumbersCombo;
	}

}
