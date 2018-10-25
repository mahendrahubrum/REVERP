package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.BankAccountDepositDao;
import com.inventory.config.acct.dao.BankAccountPaymentDao;
import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.IncomeTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.acct.ui.BankAccountDepositUI;
import com.inventory.config.acct.ui.BankAccountPaymentUI;
import com.inventory.config.acct.ui.ExpendetureTransactionUI;
import com.inventory.config.acct.ui.IncomeTransactionUI;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalModel;
import com.inventory.journal.ui.JournalUI;
import com.inventory.payment.dao.EmployeeAdvancePaymentDao;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.payment.model.PaymentModel;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.payment.ui.CustomerPaymentsUI;
import com.inventory.payment.ui.EmployeeAdvancePaymentsUI;
import com.inventory.payment.ui.SupplierPaymentsUI;
import com.inventory.payment.ui.TransportationPaymentsUI;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.CashFlowReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.subscription.ui.SubscriptionPayment;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
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
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Sep 12, 2014
 */
public class MoneyFlowReportUI extends SparkLogic {

	private static final long serialVersionUID = 5727068156020720793L;
	private SComboField organizationComboField;
	private SComboField officeComboField;

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

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	SHorizontalLayout popupContainer;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_PARTICULARS = "Particulars";
	static String TBC_DATE = "Date";
	static String TBC_INWARDS = "Inwards";
	static String TBC_OUTWARDS = "Outwards";

	SHorizontalLayout mainLay;
	STable table;
	String[] allColumns;
	String[] visibleColumns;
	CashFlowReportDao daoObj;

	@Override
	public SPanel getGUI() {
		customerId = 0;
		report = new Report(getLoginID());

		daoObj = new CashFlowReportDao();

		mainLay = new SHorizontalLayout();

		setSize(1200, 400);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		popupContainer = new SHorizontalLayout();

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_PARTICULARS, TBC_DATE,
				TBC_INWARDS, TBC_OUTWARDS };
		visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS, TBC_DATE,
				TBC_INWARDS, TBC_OUTWARDS };

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

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,
					getPropertyName("particulars"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_INWARDS, Double.class, null,
					getPropertyName("inwards"), null, Align.RIGHT);
			table.addContainerProperty(TBC_OUTWARDS, Double.class, null,
					getPropertyName("outwards"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 1);
			table.setColumnExpandRatio(TBC_PARTICULARS, 2);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("700");
			table.setHeight("300");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_PARTICULARS, getPropertyName("total"));

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

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
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainPanel.setContent(mainLay);

			mainLay.setSpacing(true);
			mainLay.setMargin(true);

			organizationComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								SCollectionContainer bic = SCollectionContainer.setList(
										new OfficeDao()
												.getAllOfficeNamesUnderOrg((Long) organizationComboField
														.getValue()), "id");
								officeComboField.setContainerDataSource(bic);
								officeComboField
										.setItemCaptionPropertyId("name");

								Iterator it = officeComboField.getItemIds()
										.iterator();
								if (it.hasNext())
									officeComboField.setValue(it.next());
							} catch (Exception e) {
								// TODO Auto-generated catch block
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
			
//			table.addActionHandler(new Handler() {
//				
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					try{
//						if (table.getValue() != null) {
//							Item itm = table.getItem(table.getValue());
//							long id = (Long) itm.getItemProperty(TBC_ID)
//									.getValue();
//
//							if (itm.getItemProperty(TBC_PARTICULARS).getValue()
//									.equals("Sales")) {
//								SalesNewUI option = new SalesNewUI();
//								option.setCaption(getPropertyName("sales"));
//								option.loadSale((Long) itm.getItemProperty(
//										TBC_ID).getValue());
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Purchase")) {
//								PurchaseUI option = new PurchaseUI();
//								option.setCaption(getPropertyName("purchase"));
//								option.loadPurchase((Long) itm.getItemProperty(
//										TBC_ID).getValue());
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Customer Payment")) {
//								CustomerPaymentsUI option = new CustomerPaymentsUI();
//								option.setCaption(getPropertyName("customer_payments"));
//								option.getPaymentIdComboField().setValue(
//										(Long) itm.getItemProperty(TBC_ID)
//												.getValue());
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Supplier Payment")) {
//								SupplierPaymentsUI option = new SupplierPaymentsUI();
//								option.setCaption(getPropertyName("supplier_payments"));
//								option.getPaymentIdComboField().setValue(
//										(Long) itm.getItemProperty(TBC_ID)
//												.getValue());
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Expenditure")) {
//								ExpendetureTransactionUI option = new ExpendetureTransactionUI();
//								option.setCaption(getPropertyName("expenditure_transactions"));
//								option.loadData(daoObj
//										.getPaymentIdFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue()));
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Income")) {
//								IncomeTransactionUI option = new IncomeTransactionUI();
//								option.setCaption(getPropertyName("income_transactions"));
//								option.loadData(daoObj
//										.getPaymentIdFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue()));
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue()
//									.equals("Bank Account Withdrawal")) {
//								BankAccountPaymentUI option = new BankAccountPaymentUI();
//								option.setCaption(getPropertyName("bank_account_withdrawal"));
//								option.loadData(daoObj
//										.getBankActPayIdFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue()));
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Bank Account Deposit")) {
//								BankAccountDepositUI option = new BankAccountDepositUI();
//								option.setCaption(getPropertyName("bank_account_deposit"));
//								option.loadData(daoObj
//										.getBankActDepositIdFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue()));
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue()
//									.equals("Transportation Payments")) {
//								TransportationPaymentsUI option = new TransportationPaymentsUI();
//								option.setCaption(getPropertyName("transportation_payments"));
//								option.loadPaymentNo((Long) itm
//										.getItemProperty(TBC_ID).getValue());
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							} else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue()
//									.equals("Employee Advance Payments")) {
//								EmployeeAdvancePaymentsUI option = new EmployeeAdvancePaymentsUI();
//								option.setCaption(getPropertyName("employee_advance_payments"));
//								option.loadPaymentNo(daoObj
//										.getEmplAdvPayIdFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue()));
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							}else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue()
//									.equals("Journal")) {
//								JournalUI option = new JournalUI();
//								option.setCaption(getPropertyName("journal"));
//								option.loadJournal(daoObj
//										.getJournalIdFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue()));
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue()
//									.equals("Subscription")) {
//								SubscriptionPayment option = new SubscriptionPayment();
//								option.setCaption(getPropertyName("rental_payment"));
//								SubscriptionPaymentModel mdl=daoObj.getSubscriptionPaymentModel((Long) itm.getItemProperty(TBC_ID).getValue());
//								SubscriptionInModel simdl=new SubscriptionPaymentDao().getInModel(mdl.getId());
//								option.loadAccountCombo(simdl.getAccount_type());
//								if(simdl.getAvailable()==1){
//									option.loadrentCombo(1);
//									option.reloadSubscriptionInCombo(simdl.getId(),simdl.getAccount_type());
//								}
//								else if(simdl.getAvailable()==2){
//									option.loadrentCombo(2);
//									option.reloadSubscriptionOutCombo(simdl.getId(),simdl.getAccount_type());
//								}
//								else{
//									option.loadrentCombo(1);
//									option.reloadSubscriptionInCombo(simdl.getId(),simdl.getAccount_type());
//								}
//								option.center();
//								getUI().getCurrent().addWindow(option);
//								option.addCloseListener(closeListener);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Salary Payments")) {
//								SalaryDisbursalNewModel objModel = daoObj
//										.getDisbursalFromTransID((Long) itm
//												.getItemProperty(TBC_ID)
//												.getValue());
//
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,
//										"<h2><u>"+getPropertyName("salary_disbursal")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("no")+" :", objModel
//										.getId() + ""));
//								form.addComponent(new SLabel(getPropertyName("employee")+" :",
//										objModel.getEmploy().getFirst_name()));
//								form.addComponent(new SLabel(
//										getPropertyName("month")+" :",
//										CommonUtil
//												.getUtilDateFromSQLDate(objModel
//														.getMonth())));
//								form.addComponent(new SLabel(
//										getPropertyName("date")+" :",
//										CommonUtil
//												.getUtilDateFromSQLDate(objModel
//														.getDispursal_date())));
//								form.addComponent(new SLabel(
//										getPropertyName("advance_amnt")+" :", objModel
//												.getAdvance_payed() + ""));
//								form.addComponent(new SLabel(getPropertyName("salary_amount")+" :",
//										objModel.getTotal_salary() + ""));
//
//								form.setStyleName("grid_max_limit");
//
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//						}
//					}
//					catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//				
//				@Override
//				public Action[] getActions(Object target, Object sender) {
//					return new Action[] { actionDelete };
//				}
//			});			
			
			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

//					try {
//
//						if (table.getValue() != null) {
//							Item itm = table.getItem(table.getValue());
//							long id = (Long) itm.getItemProperty(TBC_ID)
//									.getValue();
//
//							if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Sales")) {
//								SalesModel sale=new SalesDao().getSale(id);
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
//								form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
//								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
////								form.addComponent(new SLabel(getPropertyName("max_cr_period"),sale.getCredit_period() + ""));
////								if (isShippingChargeEnable())
////									form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
//								form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
//								form.addComponent(new SLabel(getPropertyName("amount_paid"),sale.getPayment_amount() + ""));
//								SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
//								grid.setColumns(12);
//								grid.setRows(sale
//										.getInventory_details_list().size() + 3);
//
//								grid.addComponent(new SLabel(null, "#"), 0, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
//								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
//								grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
//								grid.setSpacing(true);
//								
//								int i = 1;
//								SalesInventoryDetailsModel invObj;
//								Iterator itr = sale.getInventory_details_list().iterator();
//								while(itr.hasNext()){
//									invObj=(SalesInventoryDetailsModel)itr.next();
//									grid.addComponent(new SLabel(null, i + ""),	0, i);
//									grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
//									grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
//									grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
//									grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
//									grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
//									grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
//																		- invObj.getDiscount_amount() 
//																		+ invObj.getTax_amount())+ ""), 6, i);
//									i++;
//								}
//								form.addComponent(grid);
//								form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} 
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Purchase")) {
//								PurchaseModel objModel = new PurchaseDao().getPurchase(id);
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("purchase_no"),objModel.getPurchase_number() + ""));
//								form.addComponent(new SLabel(getPropertyName(getPropertyName("ref_no")),
//										objModel.getPurchase_bill_number() + ""));
//								form.addComponent(new SLabel(getPropertyName("supplier"), objModel.getSupplier().getName()));
//								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
//								form.addComponent(new SLabel(getPropertyName("max_credit_period"),objModel.getCredit_period() + ""));
//		
//								if (isShippingChargeEnable())
//									form.addComponent(new SLabel(getPropertyName("shipping_charge"),objModel.getShipping_charge() + ""));
//								form.addComponent(new SLabel(getPropertyName("net_amount"), objModel.getAmount() + ""));
//								form.addComponent(new SLabel(getPropertyName("paid_amount"),objModel.getPayment_amount() + ""));
//								SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
//								grid.setColumns(12);
//								grid.setRows(objModel.getInventory_details_list().size() + 3);
//								grid.addComponent(new SLabel(null, "#"), 0, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
//								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
//								grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
//								grid.setSpacing(true);
//								int i = 1;
//								PurchaseInventoryDetailsModel invObj;
//								Iterator itmItr = objModel
//										.getInventory_details_list().iterator();
//								while (itmItr.hasNext()) {
//									invObj = (PurchaseInventoryDetailsModel) itmItr
//											.next();
//		
//									grid.addComponent(new SLabel(null, i + ""),
//											0, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getItem().getName()), 1, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getQunatity() + ""), 2, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getUnit().getSymbol()), 3, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getUnit_price() + ""), 4,
//											i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getDiscount_amount() + ""),
//											5, i);
//									grid.addComponent(
//											new SLabel(
//													null,
//													(invObj.getUnit_price()
//															* invObj.getQunatity()
//															- invObj.getDiscount_amount() + invObj
//																.getTax_amount())
//															+ ""), 6, i);
//									i++;
//								}
//		
//								form.addComponent(grid);
//								form.addComponent(new SLabel(
//										getPropertyName("comment"), objModel
//												.getComments()));
//		
//								form.setStyleName("grid_max_limit");
//		
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} 
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Customer Payment")) {
//								PaymentModel mdl=new PaymentDao().getPaymentModel((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("customer_payments")+"</u></h2>"));
//								TransactionModel tr=new SalesDao().getTransaction(mdl.getTransaction_id());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SLabel(getPropertyName("payment_no"),mdl.getPayment_id()+""));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("payment"),mdl.getPayment_amount()+""));
//								form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
//								form.addComponent(new SLabel(getPropertyName("details"), mdl.getDescription()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} 
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Supplier Payment")) {
//								PaymentModel mdl=new PaymentDao().getPaymentModel((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("supplier_payments")+"</u></h2>"));
//								TransactionModel tr=new SalesDao().getTransaction(mdl.getTransaction_id());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SLabel(getPropertyName("payment_no"),mdl.getPayment_id()+""));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("payment"),mdl.getPayment_amount()+""));
//								form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
//								form.addComponent(new SLabel(getPropertyName("details"), mdl.getDescription()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Expenditure")) {
//								PaymentDepositModel mdl=new ExpendetureTransactionDao().getExpendetureTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("expenditure_transactions")+"</u></h2>"));
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("amount"),itm.getItemProperty(TBC_OUTWARDS).getValue().toString()));
//								form.addComponent(new SLabel(getPropertyName("date"),itm.getItemProperty(TBC_DATE).getValue().toString()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Income")) {
//								PaymentDepositModel mdl=new IncomeTransactionDao().getIncomeTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("income")+"</u></h2>"));
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("payment"),itm.getItemProperty(TBC_INWARDS).getValue().toString()+""));
//								form.addComponent(new SLabel(getPropertyName("date"),itm.getItemProperty(TBC_DATE).getValue().toString()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Bank Account Withdrawal")) {
//								BankAccountPaymentModel mdl=new BankAccountPaymentDao().getBankAcctPayment((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("bank_account_withdrawal")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("date"),itm.getItemProperty(TBC_DATE).getValue().toString()));
//								form.addComponent(new SLabel(getPropertyName("amount"),itm.getItemProperty(TBC_INWARDS).getValue().toString()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} 
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Bank Account Deposit")) {
//								BankAccountDepositModel mdl=new BankAccountDepositDao().getBankAcctDeposit((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("bank_account_deposit")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("date"),itm.getItemProperty(TBC_DATE).getValue().toString()));
//								form.addComponent(new SLabel(getPropertyName("amount"),itm.getItemProperty(TBC_OUTWARDS).getValue().toString()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} 
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Transportation Payments")) {
//								TransportationPaymentModel mdl=new TransportationPaymentDao().getTransportationPaymentModel
//										((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionModel tr=new SalesDao().getTransaction(mdl.getTransaction_id());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("transportation_payments")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("payment_no"),mdl.getBillNo()));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("amount"),itm.getItemProperty(TBC_OUTWARDS).getValue().toString()));
//								form.addComponent(new SLabel(getPropertyName("date"),itm.getItemProperty(TBC_DATE).getValue().toString()));
//								form.addComponent(new SLabel(getPropertyName("details"), mdl.getDescription()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Employee Advance Payments")) {
//								EmployeeAdvancePaymentModel mdl=new EmployeeAdvancePaymentDao().getEmployeeAdvancePaymentModel
//										((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("employee_advance_payments")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("amount"),det.getAmount()+""));
//								form.addComponent(new SLabel(getPropertyName("date"),tr.getDate().toString()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Journal")) {
//								JournalModel mdl=new JournalDao().getJounal((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("journal")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("amount"),det.getAmount()+""));
//								form.addComponent(new SLabel(getPropertyName("date"),tr.getDate().toString()));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS).getValue().equals("Subscription")) {
//								SubscriptionPaymentModel mdl=daoObj.getSubscriptionPaymentModel((Long) itm.getItemProperty(TBC_ID).getValue());
//								SubscriptionInModel simdl=new SubscriptionPaymentDao().getInModel(mdl.getId());
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("rental_payment")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("employee"),new LedgerDao().getLedgerNameFromID(simdl.getSubscriber())));
//								form.addComponent(new SLabel(getPropertyName("amount"),simdl.getRate()+""));
//								form.addComponent(new SLabel(getPropertyName("rental_date"),simdl.getSubscription_date().toString()));
//								form.addComponent(new SLabel(getPropertyName("payment_date"), mdl.getPayment_date().toString()));
//								form.addComponent(new SLabel(getPropertyName("payment"),mdl.getAmount_paid()+""));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//							else if (itm.getItemProperty(TBC_PARTICULARS)
//									.getValue().equals("Salary Payments")) {
//								SalaryDisbursalNewModel objModel = daoObj.getDisbursalFromTransID((Long) itm.getItemProperty(TBC_ID).getValue());
//								SFormLayout form = new SFormLayout();
//								TransactionModel tr=new SalesDao().getTransaction((Long) itm.getItemProperty(TBC_ID).getValue());
//								TransactionDetailsModel det=tr.getTransaction_details_list().get(0);
//								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("salary_disbursal")+"</u></h2>"));
//								form.addComponent(new SLabel(getPropertyName("from_account"),det.getFromAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("to_account"),det.getToAcct().getName()));
//								form.addComponent(new SLabel(getPropertyName("month"),CommonUtil.getUtilDateFromSQLDate(tr.getDate())));
//								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(tr.getDate())));
//								form.addComponent(new SLabel(getPropertyName("amount"),det.getAmount()+ ""));
//								form.setStyleName("grid_max_limit");
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							}
//
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}

				}
			});

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();

						table.setColumnFooter(TBC_INWARDS, "0");
						table.setColumnFooter(TBC_OUTWARDS, "0");
						if (officeComboField.getValue() != null) {

							List<Object> reportList;

							long clientId = 0;
							boolean active = true;

							List lst = reportList = daoObj.getMoneyFlowReport(
									(Long) officeComboField.getValue(),
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),getSettings());

							table.setVisibleColumns(allColumns);
							int ct = 0;
							reportList = new ArrayList<Object>();
							ReportBean obj;
							double total_inw = 0, ttl_out = 0;
							Iterator it = lst.iterator();
							while (it.hasNext()) {
								obj = (ReportBean) it.next();

								ttl_out += obj.getOutwards();
								total_inw += obj.getInwards();
								// TBC_SN, TBC_ID, TBC_PARTICULARS,
								// TBC_DATE,TBC_INWARDS,TBC_OUTWARDS
								table.addItem(
										new Object[] {
												ct + 1,
												obj.getId(),
												obj.getParticulars(),
												CommonUtil
														.formatDateToDDMMYYYY(obj
																.getTrn_date()),
												obj.getInwards(),
												obj.getOutwards() }, ct);
								ct++;
								reportList.add(obj);
							}

							table.setVisibleColumns(visibleColumns);

							table.setColumnFooter(TBC_INWARDS,
									asString(total_inw));
							table.setColumnFooter(TBC_OUTWARDS,
									asString(ttl_out));

						} else {
							setRequiredError(officeComboField, null,
									true);
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

			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							boolean noData = true;
							SalesModel salesModel = null;
							SalesInventoryDetailsModel inventoryDetailsModel = null;
							SalesReportBean reportBean = null;
							String items = "";

							List reportList;

							long itemID = 0;
							long custId = 0;

							reportList = daoObj.getMoneyFlowReport(
									(Long) officeComboField.getValue(),
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),getSettings());

							if (reportList.size() > 0) {
								Collections.sort(reportList,
										new Comparator<ReportBean>() {
											@Override
											public int compare(
													final ReportBean object1,
													final ReportBean object2) {
												return object1
														.getTrn_date()
														.compareTo(
																object2.getTrn_date());
											}
										});
							}

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("MoneyFlow_Report");
								report.setReportFileName("Money Flow Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("money_flow_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("PARTICULARS_LABEL", getPropertyName("particulars"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("FROM_ACCOUNT_LABEL", getPropertyName("from_account"));
								map.put("TO_ACCOUNT_LABEL", getPropertyName("to_account"));
								map.put("PAYMENT_TYPE_LABEL", getPropertyName("payment_type"));
								map.put("INWARDS_LABEL", getPropertyName("inwards"));
								map.put("OUTWARDS_LABEL", getPropertyName("outwards"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								String subHeader = "";

								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t "+getPropertyName("to")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDateField
														.getValue());

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, map);

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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
