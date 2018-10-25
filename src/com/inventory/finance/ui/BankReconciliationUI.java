package com.inventory.finance.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.BankAccountModel;
import com.inventory.config.acct.ui.BankAccountDepositUI;
import com.inventory.config.acct.ui.BankAccountPaymentUI;
import com.inventory.finance.bean.BankReconciliationBean;
import com.inventory.finance.dao.BankReconciliationDao;
import com.inventory.finance.model.BankRecociliationModel;
import com.inventory.reports.dao.LedgerViewDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.dao.LoginDao;
import com.webspark.uac.dao.OfficeDao;

public class BankReconciliationUI extends SparkLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6822996138582514333L;
	private SPanel panel;
	private SFormLayout formLayout;
	private SHorizontalLayout buttonLayout;
	// private SHorizontalLayout tableLayout;
	private SVerticalLayout mainVerticalLayout;
	private OfficeDao officeDao;
	private SComboField officeComboField;
	private SComboField bankAccountComboField;
	private BankAccountDao bankAccountDao;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SNativeSelect statusNativeSelectField;
	private SButton loadButton;
	private SButton resetButton;
	private STable table;

	private final static String TBC_SN = "SN";
	private final static String TBC_ID = "ID";
	private final static String TBC_DATE = "Date";
	private final static String TBC_BILL_NO = "Bill No";
	private final static String TBC_PARTICULARS = "Particulars";
	private final static String TBC_DR = "DR";
	private final static String TBC_CR = "CR";
	private final static String TBC_CHEQUE_NO = "Cheque No";
	private final static String TBC_CHEQUE_DATE = "Cheque Date";
	private final static String TBC_REMARKS = "Remarks";
	private final static String TBC_DIVISION = "Division";
	private final static String TBC_DEPT = "Dept.";
	private final static String TBC_TRAN_ID = "Transaction Id";
	private final static String TBC_TRAN_LEDGER_ID = "Transfer Ledger Id";
	private final static String TBC_INVOICE_ID = "Invoice Id";
	private final static String TBC_ACTION = "Action";
	private final static String TBC_CLEARING_DATE = "Clearing Date";
	private final static String TBC_COMMENTS = "Comments";
	// private final static String TBC_POPUP_VIEW = "Pop Up View";

	private Object[] visibleColumns;
	private Object[] allColumns;
	private BankReconciliationDao bankReconciliationDao;
	private SDateField clearingDateField;
	private SFormLayout popUpFormLayout;
	private SHorizontalLayout popUpButtonLayout;
	private SVerticalLayout popUpMainVerticalLayout;
	private STextArea commentsTextArea;
	private SButton popUpSaveButton;
	private SButton popupUpdateButton;
	private SButton popUpDeleteButton;
	private SPopupView popupView;
	private LedgerDao ledgerDao;
	private LoginDao loginDao;
	private SLabel openingBalanceLabel;
	private SLabel closingBalanceLabel;
	// private SHorizontalLayout balanceFieldLayout;
	private SButton bankDepositButton;
	private SButton bankPaymentButton;
	private LedgerViewDao ledgerViewDao;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		visibleColumns = new Object[] { TBC_SN, TBC_DATE, TBC_BILL_NO,
				TBC_PARTICULARS, TBC_DR, TBC_CR, TBC_CHEQUE_NO,
				TBC_CHEQUE_DATE, TBC_ACTION, TBC_REMARKS, TBC_DIVISION,
				TBC_DEPT };
		allColumns = new Object[] { TBC_ID, TBC_SN, TBC_DATE, TBC_BILL_NO,
				TBC_PARTICULARS, TBC_DR, TBC_CR, TBC_CHEQUE_NO,
				TBC_CHEQUE_DATE, TBC_ACTION, TBC_REMARKS, TBC_DIVISION,
				TBC_DEPT, TBC_TRAN_ID, TBC_TRAN_LEDGER_ID, TBC_INVOICE_ID,
				TBC_CLEARING_DATE, TBC_COMMENTS };

		setSize(1200, 600);
		panel = new SPanel();
		formLayout = new SFormLayout();
		buttonLayout = new SHorizontalLayout();
		// balanceFieldLayout = new SHorizontalLayout();
		mainVerticalLayout = new SVerticalLayout();
		mainVerticalLayout.setSpacing(true);

		formLayout.setMargin(true);
		buttonLayout.setSpacing(true);
		// balanceFieldLayout.setSpacing(true);
		// balanceFieldLayout.setMargin(true);
		// / balanceFieldLayout.setWidth("500");
		panel.setSizeFull();

		createPopupView();

		officeDao = new OfficeDao();
		bankAccountDao = new BankAccountDao();
		bankReconciliationDao = new BankReconciliationDao();
		ledgerDao = new LedgerDao();
		loginDao = new LoginDao();
		ledgerViewDao = new LedgerViewDao();
		// Transaction

		officeComboField = new SComboField(getPropertyName("office"), 200,
				getOfficeList(), "id", "name", true, getPropertyName("select"));
		officeComboField.setValue(getOfficeID());

		bankAccountComboField = new SComboField(
				getPropertyName("bank_account"), 350,
				getBankAccountList(getOfficeID()), "id", "name", true,
				getPropertyName("select"));

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getFinStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getFinEndDate());

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		statusNativeSelectField = new SNativeSelect(getPropertyName("status"),
				100, getStatusList(), "intKey", "value");
		statusNativeSelectField.setNullSelectionAllowed(false);
		statusNativeSelectField.setValue(0);

		loadButton = new SButton(getPropertyName("load"));
		// loadButton.setClickShortcut(KeyCode.SPACEBAR);
		resetButton = new SButton(getPropertyName("reset"));
		// resetButton.setClickShortcut(KeyCode.);
		bankDepositButton = new SButton();//getPropertyName("bank_account_deposit")
		bankDepositButton.setPrimaryStyleName("reconciliation_bank_deposit_btn_style");
		bankDepositButton.setDescription(getPropertyName("bank_account_deposit"));
		// resetButton.setClickShortcut(KeyCode.);
		bankPaymentButton = new SButton();
		bankPaymentButton.setPrimaryStyleName("reconciliation_bank_payment_btn_style");
		bankPaymentButton.setDescription(getPropertyName("bank_account_payment"));
		// resetButton.setClickShortcut(KeyCode.);

		openingBalanceLabel = new SLabel(null, 250);
		openingBalanceLabel.setVisible(false);
		openingBalanceLabel.setStyleName(Reindeer.LABEL_H2);

		closingBalanceLabel = new SLabel(null, 250);
		closingBalanceLabel.setVisible(false);
		closingBalanceLabel.setStyleName(Reindeer.LABEL_H2);

		table = new STable(null, 1100, 300);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
				Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
				Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,
				getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_BILL_NO, String.class, null,
				getPropertyName("bill_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_PARTICULARS, String.class, null,
				getPropertyName("particulars"), null, Align.LEFT);
		table.addContainerProperty(TBC_DR, String.class, null,
				getPropertyName("dr"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CR, String.class, null,
				getPropertyName("cr"), null, Align.RIGHT);
		table.addContainerProperty(TBC_CHEQUE_NO, String.class, null,
				getPropertyName("cheque_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_CHEQUE_DATE, String.class, null,
				getPropertyName("cheque_date"), null, Align.CENTER);
		table.addContainerProperty(TBC_ACTION, SButton.class, null,
				getPropertyName("action"), null, Align.CENTER);
		table.addContainerProperty(TBC_CLEARING_DATE, Date.class, null,
				getPropertyName("clearing_date"), null, Align.CENTER);
		table.addContainerProperty(TBC_COMMENTS, String.class, null,
				getPropertyName("comments"), null, Align.CENTER);
		/*
		 * table.addContainerProperty(TBC_POPUP_VIEW, SPopupView.class, null,
		 * getPropertyName("popup"), null, Align.CENTER);
		 */
		table.addContainerProperty(TBC_REMARKS, String.class, null,
				getPropertyName("remarks"), null, Align.LEFT);
		table.addContainerProperty(TBC_DIVISION, String.class, null,
				getPropertyName("division"), null, Align.LEFT);
		table.addContainerProperty(TBC_DEPT, String.class, null,
				getPropertyName("department"), null, Align.LEFT);
		table.addContainerProperty(TBC_TRAN_ID, Long.class, null,
				getPropertyName("tran_id"), null, Align.LEFT);
		table.addContainerProperty(TBC_TRAN_LEDGER_ID, Long.class, null,
				getPropertyName("tran_ledger_id"), null, Align.LEFT);
		table.addContainerProperty(TBC_INVOICE_ID, Long.class, null,
				getPropertyName("invoice_id"), null, Align.CENTER);

		table.setFooterVisible(true);
		table.setColumnFooter(TBC_PARTICULARS, getPropertyName("total"));

		table.setVisibleColumns(getVisibleColumns());

		table.setColumnExpandRatio(TBC_SN, 0.5f);
		table.setColumnExpandRatio(TBC_DATE, 1.9f);
		table.setColumnExpandRatio(TBC_BILL_NO, 1.5f);
		table.setColumnExpandRatio(TBC_PARTICULARS, 3f);
		table.setColumnExpandRatio(TBC_DR, 2f);
		table.setColumnExpandRatio(TBC_CR, 2f);
		table.setColumnExpandRatio(TBC_CHEQUE_NO, 2f);
		table.setColumnExpandRatio(TBC_CHEQUE_DATE, 2f);
		table.setColumnExpandRatio(TBC_ACTION, 2f);
		table.setColumnExpandRatio(TBC_REMARKS, 4f);
		table.setColumnExpandRatio(TBC_DIVISION, 2f);
		table.setColumnExpandRatio(TBC_DEPT, 2f);

		formLayout.addComponent(officeComboField);
		formLayout.addComponent(bankAccountComboField);
		formLayout.addComponent(dateHorizontalLayout);
		formLayout.addComponent(statusNativeSelectField);

		buttonLayout.addComponent(loadButton);
		buttonLayout.addComponent(resetButton);
		buttonLayout.addComponent(bankDepositButton);
		buttonLayout.addComponent(bankPaymentButton);
		buttonLayout.addComponent(openingBalanceLabel);
		buttonLayout.addComponent(closingBalanceLabel);

		/*
		 * balanceFieldLayout.addComponent(openingBalanceLabel);
		 * balanceFieldLayout.addComponent(closingBalanceLabel);
		 */

		mainVerticalLayout.addComponent(formLayout);
		mainVerticalLayout.addComponent(buttonLayout);
		mainVerticalLayout.addComponent(popupView);
		// mainVerticalLayout.addComponent(balanceFieldLayout);
		mainVerticalLayout.addComponent(table);
		mainVerticalLayout.setComponentAlignment(formLayout,
				Alignment.MIDDLE_CENTER);
		mainVerticalLayout.setComponentAlignment(buttonLayout,
				Alignment.MIDDLE_CENTER);
		mainVerticalLayout
				.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
		mainVerticalLayout.setComponentAlignment(popupView,
				Alignment.MIDDLE_RIGHT);

		panel.setContent(mainVerticalLayout);

		officeComboField.addValueChangeListener(new ValueChangeListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void valueChange(ValueChangeEvent event) {
				List list = getBankAccountList(toLong(officeComboField
						.getValue().toString()));
				if (list == null) {
					list = new ArrayList<Object>();
				}
				list.add(0, new BankAccountModel(0,
						"------------ Select ---------------"));
				bankAccountComboField
						.setContainerDataSource(SCollectionContainer.setList(
								list, "id"));
				bankAccountComboField.setItemCaptionPropertyId("name");
			}
		});

		resetButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					setComponentFieldsReadOnly(false);
					table.removeAllItems();
					table.setColumnFooter(TBC_DR, "0");
					table.setColumnFooter(TBC_CR, "0");
				}
			}
		});

		final ClickListener popupListener = new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				popupView.setPopupVisible(true);
				clearingDateField.focus();

				clearPopupFields();

				if (event.getButton().getId() != null) {
					Object itemId = getTableObjectId(event.getButton().getId());
					Item item = table.getItem(itemId);
					popUpSaveButton.setId(event.getButton().getId());
					popupUpdateButton.setId(event.getButton().getId());
					popUpDeleteButton.setId(event.getButton().getId());

					table.setValue(itemId);

					if (((Long) item.getItemProperty(TBC_ID).getValue()) == 0) {
						popUpSaveButton.setVisible(true);
						popupUpdateButton.setVisible(false);
						popUpDeleteButton.setVisible(false);
					} else {
						popUpSaveButton.setVisible(false);
						popupUpdateButton.setVisible(true);
						popUpDeleteButton.setVisible(true);

						clearingDateField.setValue((Date) item.getItemProperty(
								TBC_CLEARING_DATE).getValue());
						commentsTextArea.setValue(item
								.getItemProperty(TBC_COMMENTS).getValue()
								.toString());
					}
				}

			}

			private void clearPopupFields() {
				clearingDateField.setValue(getWorkingDate());
				commentsTextArea.setValue("");
			}
		};

		loadButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					table.removeAllItems();
					setComponentFieldsReadOnly(true);
					try {
						List<BankReconciliationBean> beanList = bankReconciliationDao.getBankReconciliationDetails(
								toLong(bankAccountComboField.getValue()
										.toString()), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
								toInt(statusNativeSelectField.getValue()
										.toString()));
						table.setVisibleColumns(allColumns);
						SButton actionButton = null;
						boolean isDataExist = false;
						double totalDr = 0;
						double totalCr = 0;

						for (BankReconciliationBean bean : beanList) {
							isDataExist = true;
							// System.out.println((table.getItemIds().size()+1)+" ====\n "+
							// bean.getId()+" ====\n "+
							// bean.getDate()+" ====\n "+
							// bean.getBillNo()+" ====\n "+
							// bean.getParticulars()+" ====\n "+
							// bean.getDr()+""+" ====\n "+
							// bean.getCr()+""+" ====\n "+
							// bean.getChequeNo()+""+" ====\n "+
							// bean.getChequeDate()+" ====\n "+
							// bean.getRemarks()+" ====\n "+
							// bean.getDivision()+" ====\n "+
							// bean.getDept()+" ====\n "+
							// bean.getTransactionId()+" ====\n "+
							// bean.getTransactionDetailId()+" ====\n "+
							// bean.getInvoiceId()+" ====\n ");
							//
							Object id = table.getItemIds().size() + 1;
							actionButton = new SButton();
							if (bean.getId() == 0) {
								actionButton
										.setPrimaryStyleName("reconciliation_cleared_btn_style");
								actionButton.setDescription("Clearing");
							} else {
								actionButton
										.setPrimaryStyleName("reconciliation_uncleared_btn_style");
								actionButton.setDescription("Unclearing");
							}
							// actionButton.setStyleName(style)
							actionButton.setId(id + "");
							actionButton.addClickListener(popupListener);

							totalDr += bean.getDr();
							totalCr += bean.getCr();

							table.addItem(
									new Object[] {
											bean.getId(),
											table.getItemIds().size() + 1,
											bean.getDateString(),
											bean.getBillNo(),
											bean.getParticulars(),
											(bean.getDr() == 0) ? " " : String
													.valueOf(bean.getDr()),
											(bean.getCr() == 0) ? " " : String
													.valueOf(bean.getCr()),
											bean.getChequeNo(),
											bean.getChequeDate(), actionButton,
											bean.getRemarks(),
											bean.getDivision(), bean.getDept(),
											bean.getTransactionId(),
											bean.getTransactionAccountId(),
											bean.getInvoiceId(),
											bean.getClearingDate(),
											bean.getComments() }, id);
						}
						table.setVisibleColumns(visibleColumns);
						table.setColumnFooter(TBC_DR, roundNumber(totalDr) + "");
						table.setColumnFooter(TBC_CR, roundNumber(totalCr) + "");
						double openingBal = bankReconciliationDao
								.getOpeningBalance(CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()),
										toLong(bankAccountComboField.getValue()
												.toString()));
					//	double closingBal = openingBal - totalDr + totalCr;
						double closingBal = ledgerViewDao
								.getLedgerBalance(CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										toLong(bankAccountComboField.getValue().toString()));
						openingBalanceLabel.setValue("Opening Bal : "
								+ ((openingBal > 0) ? openingBal
										+ " Dr" : Math.abs(openingBal)));
						closingBalanceLabel.setValue("Closing Bal : "
								+ ((closingBal > 0) ? closingBal
										+ " Dr" : Math.abs(closingBal)));

						if (!isDataExist) {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
							setComponentFieldsReadOnly(false);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});

		table.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null) {
					System.out.println(table.getValue());

					// Item item = table.getItem(table.getValue());

				}
			}
		});

		popUpSaveButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = getTableObjectId(event.getButton().getId());
				Item item = table.getItem(itemId);
				BankRecociliationModel model = new BankRecociliationModel();
				try {
					model.setLedger(ledgerDao
							.getLedgeer(toLong(bankAccountComboField.getValue()
									.toString())));
					model.setDate(getWorkingDate());
					model.setTransaction(bankReconciliationDao
							.getTransactionModel(toLong(item.getItemProperty(
									TBC_TRAN_ID).toString())));
					model.setClearing_date(CommonUtil
							.getSQLDateFromUtilDate(clearingDateField
									.getValue()));
					model.setComments(commentsTextArea.getValue());
					model.setLogin(loginDao.getLoginModel(getLoginID()));
					model.setInvoiceId(toLong(item.getItemProperty(
							TBC_INVOICE_ID).toString()));
					model.setTransafer_ledger(ledgerDao.getLedgeer(toLong(item
							.getItemProperty(TBC_TRAN_LEDGER_ID).toString())));
					if (item.getItemProperty(TBC_DR).toString().trim().length() != 0) {
						model.setAmount((-1)
								* toDouble(item.getItemProperty(TBC_DR)
										.toString().trim()));
					} else {
						model.setAmount(toDouble(item.getItemProperty(TBC_CR)
								.toString().trim()));
					}

					bankReconciliationDao.save(model);
					item.getItemProperty(TBC_CLEARING_DATE).setValue(
							clearingDateField.getValue());
					item.getItemProperty(TBC_COMMENTS).setValue(
							commentsTextArea.getValue());
					item.getItemProperty(TBC_ID).setValue(model.getId());
					if (toInt(statusNativeSelectField.getValue().toString()) == SConstants.BankReconciliationStatus.CLEARED) {
						table.removeItem(itemId);
					} else {
						((SButton) item.getItemProperty(TBC_ACTION).getValue())
								.setPrimaryStyleName("reconciliation_uncleared_btn_style");
					}

					// loadButton.click();

				} catch (Exception e) {
					e.printStackTrace();
				}

				popupView.setPopupVisible(false);

			}

		});

		popupUpdateButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = getTableObjectId(event.getButton().getId());
				Item item = table.getItem(itemId);

				try {
					BankRecociliationModel model = bankReconciliationDao
							.getBankRecociliationModel(toLong(item
									.getItemProperty(TBC_ID).getValue()
									.toString()));
					model.setDate(getWorkingDate());
					model.setClearing_date(CommonUtil
							.getSQLDateFromUtilDate(clearingDateField
									.getValue()));
					model.setComments(commentsTextArea.getValue());
					model.setLogin(loginDao.getLoginModel(getLoginID()));

					if (item.getItemProperty(TBC_DR).toString().trim().length() != 0) {
						model.setAmount((-1)
								* toDouble(item.getItemProperty(TBC_DR)
										.toString().trim()));
					} else {
						model.setAmount(toDouble(item.getItemProperty(TBC_CR)
								.toString().trim()));
					}
					bankReconciliationDao.update(model);

					item.getItemProperty(TBC_CLEARING_DATE).setValue(
							clearingDateField.getValue());
					item.getItemProperty(TBC_COMMENTS).setValue(
							commentsTextArea.getValue());
					item.getItemProperty(TBC_ID).setValue(model.getId());

					/*
					 * if(toInt(statusNativeSelectField.getValue().toString())
					 * == SConstants.BankReconciliationStatus.UNCLEARED){
					 * table.removeItem(itemId); } else {
					 * ((SButton)item.getItemProperty
					 * (TBC_ACTION)).setPrimaryStyleName
					 * ("reconciliation_cleared_btn_style"); }
					 */

					// loadButton.click();
				} catch (Exception e) {
					e.printStackTrace();
				}

				popupView.setPopupVisible(false);

			}

		});

		popUpDeleteButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = getTableObjectId(event.getButton().getId());
				Item item = table.getItem(itemId);

				try {
					bankReconciliationDao.delete(toLong(item
							.getItemProperty(TBC_ID).getValue().toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// setComponentFieldsReadOnly(false);
				// loadButton.click();
				// setComponentFieldsReadOnly(true);
				popupView.setPopupVisible(false);

				item.getItemProperty(TBC_ID).setValue(toLong("0"));

				if (toInt(statusNativeSelectField.getValue().toString()) == SConstants.BankReconciliationStatus.CLEARED) {
					table.removeItem(itemId);
				} else {
					((SButton) item.getItemProperty(TBC_ACTION).getValue())
							.setPrimaryStyleName("reconciliation_cleared_btn_style");
				}

			}

		});
		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				loadButton.click();
			}
		};
		bankDepositButton.addClickListener(new ClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void buttonClick(ClickEvent event) {
				BankAccountDepositUI bankDepositUI = new BankAccountDepositUI();
				bankDepositUI.setCaption(getPropertyName("bank_account_deposit"));
				bankDepositUI.center();
				getUI().getCurrent().addWindow(bankDepositUI);
				bankDepositUI.addCloseListener(closeListener);
			}

		});
		
		bankPaymentButton.addClickListener(new ClickListener() {

			@SuppressWarnings({ "static-access" })
			@Override
			public void buttonClick(ClickEvent event) {
				BankAccountPaymentUI bankPaymentUI = new BankAccountPaymentUI();
				bankPaymentUI.setCaption(getPropertyName("bank_account_payment"));
				bankPaymentUI.center();
				getUI().getCurrent().addWindow(bankPaymentUI);
				bankPaymentUI.addCloseListener(closeListener);
			}

		});
		return panel;
	}

	private Object getTableObjectId(String id) {
		@SuppressWarnings("rawtypes")
		Iterator itr = table.getItemIds().iterator();
		while (itr.hasNext()) {
			Object obj = itr.next();
			if (obj.toString().equals(id)) {
				return obj;
			}
		}
		return null;
	}

	private void createPopupView() {
		popUpFormLayout = new SFormLayout();
		popUpButtonLayout = new SHorizontalLayout();
		popUpMainVerticalLayout = new SVerticalLayout();

		popUpButtonLayout.setSpacing(true);

		clearingDateField = new SDateField(getPropertyName("clearing_date"));
		clearingDateField.setValue(getWorkingDate());

		commentsTextArea = new STextArea(getPropertyName("comments"), 200, 100);

		popUpSaveButton = new SButton(getPropertyName("save"), 70);
		popUpSaveButton.setStyleName("savebtnStyle");
		popUpSaveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		popUpSaveButton.setClickShortcut(KeyCode.ENTER);

		popupUpdateButton = new SButton(getPropertyName("update"), 80);
		popupUpdateButton
				.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		popupUpdateButton.setStyleName("updatebtnStyle");
		popupUpdateButton.setClickShortcut(KeyCode.PAGE_DOWN);

		popUpDeleteButton = new SButton(getPropertyName("delete"), 78);
		popUpDeleteButton
				.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		popUpDeleteButton.setStyleName("deletebtnStyle");
		popUpDeleteButton.setClickShortcut(KeyCode.DELETE);

		popUpFormLayout.addComponent(clearingDateField);
		popUpFormLayout.addComponent(commentsTextArea);

		popUpButtonLayout.addComponent(popUpSaveButton);
		popUpButtonLayout.addComponent(popupUpdateButton);
		popUpButtonLayout.addComponent(popUpDeleteButton);

		popUpMainVerticalLayout.addComponent(popUpFormLayout);
		popUpMainVerticalLayout.addComponent(popUpButtonLayout);

		popUpMainVerticalLayout.setComponentAlignment(popUpButtonLayout,
				Alignment.BOTTOM_CENTER);

		popupView = new SPopupView(null, popUpMainVerticalLayout);
		/*
		 * popupView.setWidth("250"); popupView.setHeight("100");
		 */
		popupView.setHideOnMouseOut(false);

		// mainVerticalLayout.addComponent(popupView);

	}

	private void setComponentFieldsReadOnly(boolean b) {
		officeComboField.setReadOnly(b);
		bankAccountComboField.setReadOnly(b);
		fromDateField.setReadOnly(b);
		toDateField.setReadOnly(b);
		statusNativeSelectField.setReadOnly(b);

		openingBalanceLabel.setVisible(b);
		closingBalanceLabel.setVisible(b);
	}

	private List<KeyValue> getStatusList() {
		return Arrays.asList(new KeyValue(
				SConstants.BankReconciliationStatus.ALL, "All"), new KeyValue(
				SConstants.BankReconciliationStatus.CLEARED, "Cleared"),
				new KeyValue(SConstants.BankReconciliationStatus.UNCLEARED,
						"Uncleared"));
	}

	@SuppressWarnings("rawtypes")
	private List getBankAccountList(long office_id) {
		try {
			return bankAccountDao
					.getAllActiveBankAccountNamesWithLedgerID(office_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private List getOfficeList() {
		try {
			return officeDao.getAllOfficeNamesUnderOrg(getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (officeComboField.getValue() == null
				|| officeComboField.getValue().equals("")) {
			setRequiredError(officeComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(officeComboField, null, false);
		}

		if (bankAccountComboField.getValue() == null
				|| bankAccountComboField.getValue().equals("")) {
			setRequiredError(bankAccountComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(bankAccountComboField, null, false);
		}

		if (fromDateField.getValue() == null
				|| fromDateField.getValue().equals("")) {
			setRequiredError(fromDateField, getPropertyName("invalid_data"),
					true);
			valid = false;
		} else {
			setRequiredError(fromDateField, null, false);
		}

		if (toDateField.getValue() == null || toDateField.getValue().equals("")) {
			setRequiredError(toDateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			setRequiredError(toDateField, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getVisibleColumns() {
		return visibleColumns;
	}

	public void setVisibleColumns(Object[] visibleColumns) {
		this.visibleColumns = visibleColumns;
	}

}
