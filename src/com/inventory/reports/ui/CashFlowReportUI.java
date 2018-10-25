package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.BankAccountDepositDao;
import com.inventory.config.acct.dao.BankAccountPaymentDao;
import com.inventory.config.acct.dao.CashAccountDepositDao;
import com.inventory.config.acct.dao.CashAccountPaymentDao;
import com.inventory.config.acct.dao.CreditNoteDao;
import com.inventory.config.acct.dao.DebitNoteDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.PDCDao;
import com.inventory.config.acct.dao.PDCPaymentDao;
import com.inventory.config.acct.model.BankAccountDepositDetailsModel;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentDetailsModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.CashAccountDepositDetailsModel;
import com.inventory.config.acct.model.CashAccountDepositModel;
import com.inventory.config.acct.model.CashAccountPaymentDetailsModel;
import com.inventory.config.acct.model.CashAccountPaymentModel;
import com.inventory.config.acct.model.CreditNoteDetailsModel;
import com.inventory.config.acct.model.CreditNoteModel;
import com.inventory.config.acct.model.DebitNoteDetailsModel;
import com.inventory.config.acct.model.DebitNoteModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PdcDetailsModel;
import com.inventory.config.acct.model.PdcPaymentDetailsModel;
import com.inventory.config.acct.model.PdcPaymentModel;
import com.inventory.config.acct.ui.BankAccountDepositUI;
import com.inventory.config.acct.ui.BankAccountPaymentUI;
import com.inventory.config.acct.ui.CashAccountDepositUI;
import com.inventory.config.acct.ui.CashAccountPaymentUI;
import com.inventory.config.acct.ui.CreditNoteUI;
import com.inventory.config.acct.ui.DebitNoteUI;
import com.inventory.config.acct.ui.PDCPaymentUI;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalDetailsModel;
import com.inventory.journal.model.JournalModel;
import com.inventory.journal.ui.JournalNewUI;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseReturnDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.CashFlowReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesReturnUI;
import com.inventory.sales.ui.SalesUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T. Inventory Nov 21, 2013
 */
public class CashFlowReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;
	private SComboField ledgerComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	// private SComboField customerComboField;
	// private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton, showButton;

	SettingsValuePojo settings;

	private Report report;

	SHorizontalLayout popupContainer;

	static String TBC_SN = "SN";
	static String TBC_TYPE = "Type";
	static String TBC_ID = "ID";
	static String TBC_PARTICULARS = "Particulars";
	static String TBC_FROM = "From";
	static String TBC_TO = "To";
	static String TBC_DATE = "Date";
	static String TBC_INWARDS = "Inwards";
	static String TBC_OUTWARDS = "Outwards";
	static String TBC_CONV_RATE = "Conv Rate";
	static String TBC_CURRENCY = "Currency";
	static String TBC_PER_BAL = "Period Bal";
	static String TBC_BAL = "Balance";

	SHorizontalLayout mainLay;
	STable table;
	Object[] allColumns;
	Object[] visibleColumns;
	CashFlowReportDao daoObj;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		report = new Report(getLoginID());

		daoObj = new CashFlowReportDao();

		mainLay = new SHorizontalLayout();

		setSize(1300, 400);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		popupContainer = new SHorizontalLayout();

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_TYPE, TBC_PARTICULARS,TBC_FROM,TBC_TO, TBC_DATE,
				TBC_INWARDS, TBC_OUTWARDS, TBC_CONV_RATE, TBC_CURRENCY, TBC_PER_BAL,TBC_BAL };
		visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS,TBC_FROM,TBC_TO, TBC_DATE,
				TBC_INWARDS, TBC_OUTWARDS, TBC_CURRENCY, TBC_PER_BAL, TBC_BAL };

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		// officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		try {
			
			settings=(SettingsValuePojo) getHttpSession().getAttribute("settings");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_TYPE, Integer.class, null, TBC_TYPE, null,Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,getPropertyName("particulars"), null, Align.LEFT);
			table.addContainerProperty(TBC_FROM, String.class, null,getPropertyName("from"), null, Align.LEFT);
			table.addContainerProperty(TBC_TO, String.class, null,getPropertyName("to"), null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.CENTER);
			table.addContainerProperty(TBC_INWARDS, Double.class, null,getPropertyName("inwards"), null, Align.RIGHT);
			table.addContainerProperty(TBC_OUTWARDS, Double.class, null,getPropertyName("outwards"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null,TBC_CONV_RATE, null, Align.RIGHT);
			table.addContainerProperty(TBC_CURRENCY, String.class, null,getPropertyName("currency"), null, Align.LEFT);
			table.addContainerProperty(TBC_PER_BAL, Double.class, null,getPropertyName("period_balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BAL, Double.class, null,getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, 0.5f);
			table.setColumnExpandRatio(TBC_PARTICULARS, 2f);
			table.setColumnExpandRatio(TBC_FROM, 2f);
			table.setColumnExpandRatio(TBC_TO, 2f);
			table.setColumnExpandRatio(TBC_DATE, 1f);
			table.setColumnExpandRatio(TBC_INWARDS, 1f);
			table.setColumnExpandRatio(TBC_OUTWARDS, 1f);
			table.setColumnExpandRatio(TBC_CURRENCY, 1f);
			table.setColumnExpandRatio(TBC_PER_BAL, 1f);
			table.setColumnExpandRatio(TBC_BAL, 1f);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("950");
			table.setHeight("300");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_PARTICULARS, getPropertyName("total"));

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);
			ledgerComboField = new SComboField(getPropertyName("ledger"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(ledgerComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			showButton = new SButton(getPropertyName("show"));

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);

			mainPanel.setContent(mainLay);

			mainLay.setSpacing(true);
			mainLay.setMargin(true);

			organizationComboField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {
						SCollectionContainer bic = SCollectionContainer.setList(
								new OfficeDao().getAllOfficeNamesUnderOrg((Long) organizationComboField.getValue()), "id");
						officeComboField.setContainerDataSource(bic);
						officeComboField.setItemCaptionPropertyId("name");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			officeComboField.addValueChangeListener(new Property.ValueChangeListener() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public void valueChange(ValueChangeEvent event) {
					try {
						List list=new ArrayList();
						list.add(new LedgerModel(0, getPropertyName("all")));
						list.addAll(new LedgerDao().getAllActiveLedgerNamesOfGroup((Long)officeComboField.getValue(),
																					SConstants.LEDGER_ADDED_DIRECTLY, 
																					settings.getCASH_GROUP()));
						SCollectionContainer bic = SCollectionContainer.setList(list, "id");
						ledgerComboField.setContainerDataSource(bic);
						ledgerComboField.setItemCaptionPropertyId("name");
						ledgerComboField.setValue((long)0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			
			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
			table.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							
							long id=daoObj.getIdFromTransaction((Integer)item.getItemProperty(TBC_TYPE).getValue(), (Long) item.getItemProperty(TBC_ID).getValue());
							if(id!=0){
								switch ((Integer)item.getItemProperty(TBC_TYPE).getValue()) {	
								
									case SConstants.SALES:
														SalesUI sales=new SalesUI();
														sales.setCaption(getPropertyName("sales"));
//														sales.loadOptions(id);
														sales.center();
														getUI().getCurrent().addWindow(sales);
														sales.addCloseListener(closeListener);
														break;
										
									case SConstants.PURCHASE:
														PurchaseUI purchase = new PurchaseUI();
														purchase.setCaption(getPropertyName("purchase"));
														purchase.loadOptions(id);
														purchase.center();
														getUI().getCurrent().addWindow(purchase);
														purchase.addCloseListener(closeListener);
														break;
														
									case SConstants.JOURNAL:
														JournalNewUI journal = new JournalNewUI();
														journal.setCaption(getPropertyName("journal"));
														journal.loadJournal(id);
														journal.center();
														getUI().getCurrent().addWindow(journal);
														journal.addCloseListener(closeListener);
														break;
														
									case SConstants.PURCHASE_RETURN:
														PurchaseReturnUI purchaseReturn = new PurchaseReturnUI();
														purchaseReturn.setCaption(getPropertyName("purchase_return"));
														purchaseReturn.loadOptions(id);
														purchaseReturn.center();
														getUI().getCurrent().addWindow(purchaseReturn);
														purchaseReturn.addCloseListener(closeListener);
														break;
														
									case SConstants.SALES_RETURN:
														SalesReturnUI salesReturn = new SalesReturnUI();
														salesReturn.setCaption(getPropertyName("sales_return"));
														salesReturn.loadOptions(id);
														salesReturn.center();
														getUI().getCurrent().addWindow(salesReturn);
														salesReturn.addCloseListener(closeListener);
														break;
						
									case SConstants.BANK_ACCOUNT_PAYMENTS:
														BankAccountPaymentUI bankPayment = new BankAccountPaymentUI();
														bankPayment.setCaption(getPropertyName("bank_account_payment"));
														bankPayment.loadData(id);
														bankPayment.center();
														getUI().getCurrent().addWindow(bankPayment);
														bankPayment.addCloseListener(closeListener);
														break;
														
									case SConstants.BANK_ACCOUNT_DEPOSITS:
														BankAccountDepositUI bankDeposit = new BankAccountDepositUI();
														bankDeposit.setCaption(getPropertyName("bank_account_deposit"));
														bankDeposit.loadData(id);
														bankDeposit.center();
														getUI().getCurrent().addWindow(bankDeposit);
														bankDeposit.addCloseListener(closeListener);
														break;
						
									case SConstants.CASH_ACCOUNT_DEPOSITS:
														CashAccountDepositUI cashDeposit = new CashAccountDepositUI();
														cashDeposit.setCaption(getPropertyName("cash_account_deposit"));
														cashDeposit.loadData(id);
														cashDeposit.center();
														getUI().getCurrent().addWindow(cashDeposit);
														cashDeposit.addCloseListener(closeListener);
														break;
						
									case SConstants.CASH_ACCOUNT_PAYMENTS:
														CashAccountPaymentUI cashPayment = new CashAccountPaymentUI();
														cashPayment.setCaption(getPropertyName("cash_account_payment"));
														cashPayment.loadData(id);
														cashPayment.center();
														getUI().getCurrent().addWindow(cashPayment);
														cashPayment.addCloseListener(closeListener);
														break;
						
									case SConstants.CREDIT_NOTE:
														CreditNoteUI credit = new CreditNoteUI();
														credit.setCaption(getPropertyName("credit_note"));
														credit.loadData(id);
														credit.center();
														getUI().getCurrent().addWindow(credit);
														credit.addCloseListener(closeListener);
														break;
						
									case SConstants.DEBIT_NOTE:
														DebitNoteUI debit = new DebitNoteUI();
														debit.setCaption(getPropertyName("debit_note"));
														debit.loadData(id);
														debit.center();
														getUI().getCurrent().addWindow(debit);
														debit.addCloseListener(closeListener);
														break;
						
									case SConstants.PDC_PAYMENT:
														PDCPaymentUI pdcPayment = new PDCPaymentUI();
														pdcPayment.setCaption(getPropertyName("pdc_payment"));
														pdcPayment.loadData(id);
														pdcPayment.center();
														getUI().getCurrent().addWindow(pdcPayment);
														pdcPayment.addCloseListener(closeListener);
														break;
						
									default:
														break;
							
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionDelete };
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							showDetails((Integer)item.getItemProperty(TBC_TYPE).getValue(), (Long)item.getItemProperty(TBC_ID).getValue());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			
			showButton.addClickListener(new ClickListener() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						table.removeAllItems();
						officeComboField.setComponentError(null);
						table.setColumnFooter(TBC_INWARDS, "0");
						table.setColumnFooter(TBC_OUTWARDS, "0");
						if (officeComboField.getValue() != null) {
							
							List resultList=new ArrayList();
							resultList = daoObj.getCashFlowReport((Long) organizationComboField.getValue(),
																(Long) officeComboField.getValue(),
																(Long) ledgerComboField.getValue(),0,0,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																settings);
							
							if (resultList != null && resultList.size() > 0) {
								Collections.sort(resultList,new Comparator<AcctReportMainBean>() {
									@Override
									public int compare(final AcctReportMainBean object1, final AcctReportMainBean object2) {
										return object1.getDate().compareTo(object2.getDate());
									}
								});
								double bal = 0, periodBal = 0;
								try {
									bal=daoObj.getCashFlowOpeningBalance((Long) officeComboField.getValue(),
																		(Long) ledgerComboField.getValue(),
																		CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																		settings);
								} catch (Exception e) {
									bal=0;
								}
								table.setVisibleColumns(allColumns);
								AcctReportMainBean bean;
								double total_inw = 0, ttl_out = 0;
								Iterator itr = resultList.iterator();
								while (itr.hasNext()) {
									bean = (AcctReportMainBean) itr.next();
									double inAmount=0;
									double outAmount=0;
									
									if(bean.getAmount_type().equalsIgnoreCase("CR")){
										outAmount=bean.getAmount();
										ttl_out+=bean.getAmount();
										bal-=bean.getAmount();
										periodBal-=bean.getAmount();
									}
									else if(bean.getAmount_type().equalsIgnoreCase("DR")){
										inAmount=bean.getAmount();
										total_inw+=bean.getAmount();
										bal+=bean.getAmount();
										periodBal+=bean.getAmount();
									}
									
									table.addItem(new Object[] {table.getItemIds().size()+1,
																bean.getId(),
																bean.getTransaction_type(),
																getTransactionType(bean.getTransaction_type()),
																bean.getFrom_or_to(),
																bean.getName(),
																CommonUtil.formatDateToDDMMYYYY(bean.getDate()),
																roundNumber(inAmount*bean.getRate()),
																roundNumber(outAmount*bean.getRate()),
																roundNumber(bean.getRate()),
																new CurrencyManagementDao().getselecteditem(bean.getCurrencyId()).getCode(),
																roundNumber(periodBal),
																roundNumber(bal) }, table.getItemIds().size()+1);
								}
								table.setVisibleColumns(visibleColumns);
								table.setColumnFooter(TBC_INWARDS, roundNumber(total_inw)+" "+new OfficeDao().getOffice((Long) officeComboField.getValue()).getCurrency().getCode());
								table.setColumnFooter(TBC_OUTWARDS, roundNumber(ttl_out)+" "+new OfficeDao().getOffice((Long) officeComboField.getValue()).getCurrency().getCode());
								resultList.clear();
							} 
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else {
							setRequiredError(officeComboField, getPropertyName("invalid_selection"),true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			generateButton.addClickListener(new ClickListener() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						
						if (officeComboField.getValue() != null) {

							List reportList;
							List reportMainList=new ArrayList();

							reportList = daoObj.getCashFlowReport((Long) organizationComboField.getValue(),
																(Long) officeComboField.getValue(),
																(Long) ledgerComboField.getValue(),0,0,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																settings);

							if (reportList.size() > 0) {
								Collections.sort(reportList,new Comparator<AcctReportMainBean>() {
									@Override
									public int compare(final AcctReportMainBean object1, final AcctReportMainBean object2) {
										return object1.getDate().compareTo(object2.getDate());
									}
								});
							}
							
							
							double opening_balance=0,closing_balance=0;
							if (reportList.size() > 0) {
								try {
									opening_balance=daoObj.getCashFlowOpeningBalance((Long) officeComboField.getValue(),
																		(Long) ledgerComboField.getValue(),
																		CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																		settings);
								} catch (Exception e) {
									opening_balance=0;
								}
								double bal = opening_balance, periodBal = 0;
								
								AcctReportMainBean bean;
								Iterator itr = reportList.iterator();
								while (itr.hasNext()) {
									bean = (AcctReportMainBean) itr.next();
									double inAmount=0;
									double outAmount=0;
									
									if(bean.getAmount_type().equalsIgnoreCase("CR")){
										outAmount=bean.getAmount();
										bal-=bean.getAmount();
										periodBal-=bean.getAmount();
									}
									else if(bean.getAmount_type().equalsIgnoreCase("DR")){
										inAmount=bean.getAmount();
										bal+=bean.getAmount();
										periodBal+=bean.getAmount();
									}
									// Contsructor #28
									AcctReportMainBean mainBean=new AcctReportMainBean(getTransactionType(bean.getTransaction_type()),
																						bean.getFrom_or_to(),
																						bean.getName(), 
																						CommonUtil.formatDateToDDMMYYYY(bean.getDate()),
																						roundNumber(inAmount*bean.getRate()),
																						roundNumber(outAmount*bean.getRate()), 
																						roundNumber(bean.getRate()),
																						new CurrencyManagementDao().getselecteditem(bean.getCurrencyId()).getCode(),
																						roundNumber(periodBal),
																						roundNumber(bal));
									reportMainList.add(mainBean);
								}
							}
							if (reportMainList.size() > 0) {
								report.setJrxmlFileName("CashFlow_Report");
								report.setReportFileName("Cash Flow Report");
								
								
								
								String subHeader = "";
								
								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t "+getPropertyName("to")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDateField
														.getValue());
								
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("opening_balance", roundNumber(opening_balance));
								map.put("closing_balance", roundNumber(closing_balance));
								map.put("opening_balance_label", getPropertyName("opening_balance"));
								map.put("closing_balance_label", getPropertyName("closing_balance"));
								map.put("REPORT_TITLE_LABEL", getPropertyName("cash_flow_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("CURRENCY_LABEL", getPropertyName("currency"));
								map.put("CURRENCY", new OfficeDao().getOffice((Long) officeComboField.getValue()).getCurrency().getCode());
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("PARTICULAR_LABEL", getPropertyName("particular"));
								map.put("FROM_ACCOUNT_LABEL", getPropertyName("from_account"));
								map.put("TO_ACCOUNT_LABEL", getPropertyName("to_account"));
								map.put("INWARDS_LABEL", getPropertyName("inwards"));
								map.put("OUTWARDS_LABEL", getPropertyName("outwards"));
								map.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
								map.put("BALANCE_LABEL", getPropertyName("balance"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportMainList, map);

								reportMainList.clear();
								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		
			
			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}

			organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	
	@SuppressWarnings("rawtypes")
	public void showDetails(int type,long tid) {
		
		try {
			long id=daoObj.getIdFromTransaction(type, tid);
			if(id!=0){
				if (type==SConstants.SALES) {
					
					SalesModel sale=new SalesDao().getSale(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
					form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber((sale.getAmount()-sale.getExpenseAmount())+
							(sale.getExpenseAmount()-sale.getExpenseCreditAmount()))+ " "+new CurrencyManagementDao().getselecteditem(sale.getCurrency_id()).getCode()));
					form.addComponent(new SLabel(getPropertyName("amount_paid"), roundNumber(sale.getPayment_amount()) + ""));
					SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
					grid.setColumns(12);
					grid.setRows(sale.getInventory_details_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
					grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
					grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
					grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
					grid.setSpacing(true);
					
					int i = 1;
					SalesInventoryDetailsModel invObj;
					Iterator itr = sale.getInventory_details_list().iterator();
					while(itr.hasNext()){
						invObj=(SalesInventoryDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
						grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
						grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
						grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
						grid.addComponent(new SLabel(null, roundNumber(invObj.getUnit_price() * invObj.getQunatity()
															- invObj.getDiscount() 
															+ invObj.getTaxAmount())+ ""), 6, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				} 
				else if (type==SConstants.PURCHASE) {
					PurchaseModel sale = new PurchaseDao().getPurchaseModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("purchase_no"),sale.getPurchase_no()+""));
					form.addComponent(new SLabel(getPropertyName("supplier"),sale.getSupplier().getName()));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber((sale.getAmount()-sale.getExpenseAmount())+
							(sale.getExpenseAmount()-sale.getExpenseCreditAmount()))+ " "+new CurrencyManagementDao().getselecteditem(sale.getCurrency_id()).getCode()));
					form.addComponent(new SLabel(getPropertyName("amount_paid"), roundNumber(sale.getPaymentAmount()) + ""));
					SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
					grid.setColumns(12);
					grid.setRows(sale.getPurchase_details_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
					grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
					grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
					grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
					grid.setSpacing(true);
					
					int i = 1;
					PurchaseInventoryDetailsModel invObj;
					Iterator itr = sale.getPurchase_details_list().iterator();
					while(itr.hasNext()){
						invObj=(PurchaseInventoryDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
						grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
						grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
						grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
						grid.addComponent(new SLabel(null, roundNumber(invObj.getUnit_price() * invObj.getQunatity()
															- invObj.getDiscount() 
															+ invObj.getTaxAmount())+ ""), 6, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				} 
				
				else if (type==SConstants.JOURNAL) {
					
					JournalModel sale = new JournalDao().getJournalModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("journal")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getJournal_details_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					JournalDetailsModel invObj;
					Iterator itr = sale.getJournal_details_list().iterator();
					while(itr.hasNext()){
						invObj=(JournalDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getLedger().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getRemarks()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				} 
				
				else if (type==SConstants.PURCHASE_RETURN) {
					
					PurchaseReturnModel sale=new PurchaseReturnDao().getPurchaseReturnModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase_return")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("return_no"),sale.getReturn_no()+""));
					form.addComponent(new SLabel(getPropertyName("supplier"),sale.getSupplier().getName()));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber((sale.getAmount()-sale.getExpenseAmount())+
							(sale.getExpenseAmount()-sale.getExpenseCreditAmount()))+ " "+new CurrencyManagementDao().getselecteditem(sale.getNetCurrencyId().getId()).getCode()));
					SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
					grid.setColumns(12);
					grid.setRows(sale.getInventory_details_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
					grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
					grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
					grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
					grid.setSpacing(true);
					
					int i = 1;
					PurchaseReturnInventoryDetailsModel invObj;
					Iterator itr = sale.getInventory_details_list().iterator();
					while(itr.hasNext()){
						invObj=(PurchaseReturnInventoryDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
						grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
						grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
						grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
						grid.addComponent(new SLabel(null, roundNumber(invObj.getUnit_price() * invObj.getQunatity()
															- invObj.getDiscount() 
															+ invObj.getTaxAmount())+ ""), 6, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				} 
				else if (type==SConstants.SALES_RETURN) {
					
					SalesReturnModel sale=new SalesReturnDao().getSalesReturnModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales_return")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("return_no"),sale.getReturn_no()+""));
					form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					form.addComponent(new SLabel(getPropertyName("net_amount"),roundNumber((sale.getAmount()-sale.getExpenseAmount())+
							(sale.getExpenseAmount()-sale.getExpenseCreditAmount()))+ " "+new CurrencyManagementDao().getselecteditem(sale.getNetCurrencyId().getId()).getCode()));
					SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
					grid.setColumns(12);
					grid.setRows(sale.getInventory_details_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
					grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
					grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
					grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
					grid.setSpacing(true);
					
					int i = 1;
					SalesReturnInventoryDetailsModel invObj;
					Iterator itr = sale.getInventory_details_list().iterator();
					while(itr.hasNext()){
						invObj=(SalesReturnInventoryDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
						grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
						grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
						grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
						grid.addComponent(new SLabel(null, roundNumber(invObj.getUnit_price() * invObj.getQunatity()
															- invObj.getDiscount() 
															+ invObj.getTaxAmount())+ ""), 6, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.BANK_ACCOUNT_PAYMENTS) {
					
					BankAccountPaymentModel sale = new BankAccountPaymentDao().getBankAccountPaymentModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("bank_account_payment")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getBank_account_payment_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					BankAccountPaymentDetailsModel invObj;
					Iterator itr = sale.getBank_account_payment_list().iterator();
					while(itr.hasNext()){
						invObj=(BankAccountPaymentDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId().getId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.BANK_ACCOUNT_DEPOSITS) {
					
					BankAccountDepositModel sale = new BankAccountDepositDao().getBankAccountDepositModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("bank_account_deposit")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getBank_account_deposit_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					BankAccountDepositDetailsModel invObj;
					Iterator itr = sale.getBank_account_deposit_list().iterator();
					while(itr.hasNext()){
						invObj=(BankAccountDepositDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId().getId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.CASH_ACCOUNT_PAYMENTS) {
					
					CashAccountPaymentModel sale = new CashAccountPaymentDao().getCashAccountPaymentModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("cash_account_payment")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getCash_account_payment_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					CashAccountPaymentDetailsModel invObj;
					Iterator itr = sale.getCash_account_payment_list().iterator();
					while(itr.hasNext()){
						invObj=(CashAccountPaymentDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.CASH_ACCOUNT_DEPOSITS) {
					
					CashAccountDepositModel sale = new CashAccountDepositDao().getCashAccountDepositModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("cash_account_deposit")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getCash_account_deposit_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					CashAccountDepositDetailsModel invObj;
					Iterator itr = sale.getCash_account_deposit_list().iterator();
					while(itr.hasNext()){
						invObj=(CashAccountDepositDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.CREDIT_NOTE) {
					
					CreditNoteModel sale = new CreditNoteDao().getCreditNoteModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("credit_note")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("account"),sale.getLedger().getName()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getCredit_note_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					CreditNoteDetailsModel invObj;
					Iterator itr = sale.getCredit_note_list().iterator();
					while(itr.hasNext()){
						invObj=(CreditNoteDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.DEBIT_NOTE) {
					
					DebitNoteModel sale = new DebitNoteDao().getDebitNoteModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("debit_note")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("account"),sale.getLedger().getName()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getDebit_note_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					DebitNoteDetailsModel invObj;
					Iterator itr = sale.getDebit_note_list().iterator();
					while(itr.hasNext()){
						invObj=(DebitNoteDetailsModel)itr.next();
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, invObj.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
				else if (type==SConstants.PDC_PAYMENT) {
					
					PdcPaymentModel sale = new PDCPaymentDao().getPdcPaymentModel(id);
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("pdc_payment")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getBill_no()+""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					SGridLayout grid = new SGridLayout(getPropertyName("payment_details"));
					grid.setColumns(12);
					grid.setRows(sale.getPdc_payment_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("account")), 1,0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 2, 0);
					grid.setSpacing(true);
					
					int i = 1;
					PdcPaymentDetailsModel invObj;
					Iterator itr = sale.getPdc_payment_list().iterator();
					while(itr.hasNext()){
						invObj=(PdcPaymentDetailsModel)itr.next();
						PdcDetailsModel det=new PDCDao().getPdcDetailsModel(invObj.getPdc_child_id());
						grid.addComponent(new SLabel(null, i + ""),	0, i);
						grid.addComponent(new SLabel(null, det.getAccount().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() +" " +new CurrencyManagementDao().getselecteditem(invObj.getCurrencyId().getId()).getCode()), 2, i);
						i++;
					}
					form.addComponent(grid);
					form.addComponent(new SLabel(getPropertyName("comment"), sale.getMemo()));
					form.setStyleName("grid_max_limit");
					popupContainer.removeAllComponents();
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Boolean isValid() {
		return null;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	public String getTransactionType(int type){
		String transactionType="";
		switch (type) {
						case SConstants.SALES:
											transactionType="Sales";
											break;
											
						case SConstants.PURCHASE:
											transactionType="Purchase";
											break;
											
						case SConstants.JOURNAL:
											transactionType="Journal";
											break;
											
						case SConstants.PURCHASE_RETURN:
											transactionType="Purchase Return";
											break;
											
						case SConstants.SALES_RETURN:
											transactionType="Sales return";
											break;

						case SConstants.BANK_ACCOUNT_PAYMENTS:
											transactionType="Bank Account Payment";
											break;
											
						case SConstants.BANK_ACCOUNT_DEPOSITS:
											transactionType="Bank Account Deposit";
											break;

						case SConstants.CASH_ACCOUNT_DEPOSITS:
											transactionType="Cash Account Deposit";
											break;

						case SConstants.CASH_ACCOUNT_PAYMENTS:
											transactionType="Cash Account Payment";
											break;

						case SConstants.CREDIT_NOTE:
											transactionType="Credit Note";
											break;

						case SConstants.DEBIT_NOTE:
											transactionType="Debit Note";
											break;


						case SConstants.PDC_PAYMENT:
											transactionType="PDC Payment";
											break;

						default:
											transactionType="";
											break;
		}
		return transactionType;
	}
	
}
