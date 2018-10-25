package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.IncomeTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.acct.ui.ExpendetureTransactionUI;
import com.inventory.config.acct.ui.IncomeTransactionUI;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.EmployeeAdvancePaymentDao;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.payment.ui.EmployeeAdvancePaymentsUI;
import com.inventory.payment.ui.TransportationPaymentsUI;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.payroll.ui.SalaryDisbursalNewUI;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseReturnDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.bean.ProfitReportBean;
import com.inventory.reports.dao.ProfitReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.sales.ui.SalesReturnNewUI;
import com.inventory.subscription.dao.RentalTransactionNewDao;
import com.inventory.subscription.model.RentalTransactionDetailsModel;
import com.inventory.subscription.model.RentalTransactionModel;
import com.inventory.subscription.ui.RentalTransactionNewUI;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.ReportReview;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SConfirmWithReview;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.LoginDao;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 27, 2013
 */
public class ProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -6316053747180409181L;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SButton generateButton, generateDetailedReport, showButton;

	private SOfficeComboField officeComboField;
	private Report report;
	private SReportChoiceField reportChoiceField;

	SGridLayout detailsGrid;

	SHTMLLabel grossSales, salesReturn, netSale, grossPurchase, purchaseReturn,
			netPurchase;
	SHTMLLabel transportation, otherExpense, stockBal, netProfit,otherIncome;

	SButtonLink grosSalDetails, salesReturnDetails, salesReturnItemDetails,
			grossPurchaseDetails;
	SButtonLink purchaseReturnDetails, purchaseReturnItemDetails,
			transportationDetails, expenseDetails, incomeDetails;
	SButtonLink stockDetails;
	SHorizontalLayout popupHor, popupContainer,popHor;
	SNativeButton closeButton;
	SNativeButton closeSubButton;
	ProfitReportDao dao;
	SPopupView pop, subPop,popup;

	STable table;
	STable subtable;

	Handler action1;
	CloseListener closeListener;

	ValueChangeListener valChangeLis;
	ValueChangeListener mainValueChangeListner;

	String current_details = "";
	int transaction_type=0;
	
	SettingsValuePojo settings;
	WrappedSession session;
	
	SConfirmWithReview confirmBox;
	ReportReview review;
	SPanel panel;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_DATE = "Date";
	static String TBC_NUMBER = "Number";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_AMOUNT = "Amount";
	static String TBC_REF = "Ref. No";
	static String TBC_COMMENT = "Comment";
	static String TBC_ITEM = "Item";
	static String TBC_UNIT = "Unit";
	static String TBC_RATE = "Rate";
	static String TBC_TYPE = "Type";
	static String TBC_GOOD = "Good Quantity";
	static String TBC_WASTE = "Waste Quantity";
	static String TBC_RETURN = "Return Quantity";
	static String TBC_STOCK = "Stock Quantity";
	static String TBC_ACCOUNT = "Account";
	static String TBC_PURCHASE = "Purchase Quantity";
	static String TBC_BALANCE = "Balance Quantity";

	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		try{
			
		setSize(1000, 350);

		report = new Report(getLoginID());
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		dao = new ProfitReportDao();

		popupHor = new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		popHor = new SHorizontalLayout();

		closeButton = new SNativeButton("X");
		closeSubButton = new SNativeButton("X");
		

		detailsGrid = new SGridLayout();
		detailsGrid.setColumns(6);
		detailsGrid.setRows(12);
		detailsGrid.setMargin(true);
		detailsGrid.setSpacing(true);

		grossSales = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0", 150);
		salesReturn = new SHTMLLabel(null,
				"<b style='font-size:14px;'> ( 0.0 ) ");
		netSale = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		grossPurchase = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		purchaseReturn = new SHTMLLabel(null,
				"<b style='font-size:14px;'> ( 0.0 )");
		netPurchase = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		transportation = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		otherExpense = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		otherIncome = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		stockBal = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");
		netProfit = new SHTMLLabel(null, "<b style='font-size:14px;'>0.0");

		grosSalDetails = new SButtonLink(getPropertyName("gross_sales_details"));
		salesReturnDetails = new SButtonLink(
				getPropertyName("sales_return_details"));
		salesReturnItemDetails = new SButtonLink(
				getPropertyName("item_wise_details"));
		grossPurchaseDetails = new SButtonLink(
				getPropertyName("gross_purchase_details"));
		purchaseReturnDetails = new SButtonLink(
				getPropertyName("purchase_return_details"));
		purchaseReturnItemDetails = new SButtonLink(
				getPropertyName("item_wise_details"));
		transportationDetails = new SButtonLink(
				getPropertyName("transportation_details"));
		expenseDetails = new SButtonLink(getPropertyName("expenditure_details"));
		incomeDetails = new SButtonLink(getPropertyName("income_details"));
		stockDetails = new SButtonLink(getPropertyName("stock_details"));

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>" + getPropertyName("gross_sale")
						+ " :", 180), 0, 0);
		detailsGrid.addComponent(grossSales, 1, 0);
		detailsGrid.addComponent(grosSalDetails, 2, 0);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("total_sales_return") + " :"), 0, 1);
		detailsGrid.addComponent(salesReturn, 1, 1);
		detailsGrid.addComponent(salesReturnDetails, 2, 1);
		detailsGrid.addComponent(salesReturnItemDetails, 3, 1);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>" + getPropertyName("net_sale")
						+ " :"), 0, 2);
		detailsGrid.addComponent(netSale, 1, 2);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("gross_purchase") + " :"), 0, 3);
		detailsGrid.addComponent(grossPurchase, 1, 3);
		detailsGrid.addComponent(grossPurchaseDetails, 2, 3);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("total_purchase_return") + " :"), 0,
				4);
		detailsGrid.addComponent(purchaseReturn, 1, 4);
		detailsGrid.addComponent(purchaseReturnDetails, 2, 4);
		detailsGrid.addComponent(purchaseReturnItemDetails, 3, 4);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>" + getPropertyName("net_purchase")
						+ " :"), 0, 5);
		detailsGrid.addComponent(netPurchase, 1, 5);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("transportation") + " :"), 0, 6);
		detailsGrid.addComponent(transportation, 1, 6);
		detailsGrid.addComponent(transportationDetails, 2, 6);

		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("other_expenses") + " :"), 0, 7);
		detailsGrid.addComponent(otherExpense, 1, 7);
		detailsGrid.addComponent(expenseDetails, 2, 7);
		
		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("other_income") + " :"), 0, 8);
		detailsGrid.addComponent(otherIncome, 1, 8);
		detailsGrid.addComponent(incomeDetails, 2, 8);
		

		if (settings.isSHOW_STOCK_IN_PROFIT_REPORT()) {
			detailsGrid.addComponent(new SHTMLLabel(null,
					"<b style='font-size:14px;'>"
							+ getPropertyName("stock_value") + " :</b>"), 0, 9);
			detailsGrid.addComponent(stockBal, 1, 9);
			detailsGrid.addComponent(stockDetails, 2, 9);
		}
		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("net_profit_loss") + " :</b>"), 0, 10);
		detailsGrid.addComponent(netProfit, 1, 10);

		panel= new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);
		mainFormLayout.setSpacing(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		showButton = new SButton(getPropertyName("show"));
		generateDetailedReport = new SButton(getPropertyName("detailed_report"));

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(reportChoiceField);
		SHorizontalLayout btnLay = new SHorizontalLayout(true, showButton, generateButton, generateDetailedReport);
		mainFormLayout.addComponent(btnLay);
		mainFormLayout.setComponentAlignment(btnLay, Alignment.MIDDLE_CENTER);

		review.addComponent(new SHorizontalLayout(popHor, mainFormLayout, popupHor, popupContainer, detailsGrid), "left: 0px; right: 0px; z-index:-1;");
		panel.setContent(review);
		
		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals("1")) {
					try {
						saveReview(getOptionId(),confirmBox.getTitle(),confirmBox.getComments()	,getLoginID(),report.getReportFile());
						SNotification.show("Review Saved");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				confirmBox.close();
				confirmBox.setTitle("");
				confirmBox.setComments("");
			}
			
		};
	
		confirmBox.setClickListener(confirmListener);
		
		review.setClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals(ReportReview.REVIEW)){
					if(generateReport())
						confirmBox.open();
				}
			}
		});

		
		grosSalDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Sale");
			}
		});

		
		salesReturnDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Sales Return");
			}
		});
		
		
		salesReturnItemDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Sales Return Item");
			}
		});

		
		grossPurchaseDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Purchase");
			}
		});
		
		
		purchaseReturnDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Purchase Return");
			}
		});
		
		
		purchaseReturnItemDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Purchase Return Item");
			}
		});
		

		transportationDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Transportation");
			}
		});
		
		
		expenseDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Expenses");
//				showDetails("Expenses",0);
			}
		});
		
		
		incomeDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Income");
			}
		});
		
		
		stockDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showMainDetails("Stock");
			}
		});

		
		closeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				pop.setPopupVisible(false);
			}
		});
		
		
		closeSubButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				popup.setPopupVisible(false);
			}
		});

		
		final Action actionEdit = new Action("Edit");

		
		action1 = new Action.Handler() {
			@Override
			public Action[] getActions(final Object target, final Object sender) {
				return new Action[] { actionEdit };
			}

			@Override
			public void handleAction(final Action action, final Object sender,
					final Object target) {
				if (table.getValue() != null)
					openOption((Long) table.getValue(),transaction_type);

			}
		};

		
		closeListener = new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				pop.setPopupVisible(true);
				popup.setPopupVisible(true);
			}
		};

		
		showButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {


					ProfitReportDao dao = new ProfitReportDao();

					try {

						double purchase = roundNumber(dao
								.getTotalPurchaseAmount((Long) officeComboField
										.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						double purchase_return = roundNumber(dao.getTotalPurchaseReturnAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						double sales = roundNumber(dao.getTotalSalesAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						double sales_return = roundNumber(dao.getTotalSalesReturnAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						double transp = roundNumber(dao.getTotalTransportationAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						double expendetures = roundNumber(dao.getTotalExpentitureTransactionAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));
						
						double income = roundNumber(dao.getTotalIncomeTransactionAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						double of_stock = roundNumber(dao.getOfStockAmount(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						grossSales.setValue("<b style='font-size:14px;'>"
								+ sales);
						salesReturn.setValue("<b style='font-size:14px;'> ( "
								+ sales_return + " )");
						netSale.setValue("<b style='font-size:14px;'>"
								+ roundNumber(sales - sales_return));
						grossPurchase.setValue("<b style='font-size:14px;'>"
								+ purchase);
						purchaseReturn
								.setValue("<b style='font-size:14px;'> ( "
										+ purchase_return + " )");
						netPurchase.setValue("<b style='font-size:14px;'>"
								+ roundNumber(purchase - purchase_return));
						transportation.setValue("<b style='font-size:14px;'>"
								+ transp);
						otherExpense.setValue("<b style='font-size:14px;'>"
								+ expendetures);
						otherIncome.setValue("<b style='font-size:14px;'>"
								+ income);
						stockBal.setValue("<b style='font-size:14px;'>"
								+ of_stock);
						netProfit.setValue("<b style='font-size:14px;'>"
								+ roundNumber((sales - sales_return)
										- (purchase - purchase_return) - transp
										- expendetures + income));

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		
		valChangeLis = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null)
					showSubDetails((Long) table.getValue(),transaction_type);
			}
		};
		
		
		mainValueChangeListner = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (subtable.getValue() != null)
					loadTable();
			}
		};
		
		
		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
				}
			}
		});

		
		generateDetailedReport.addClickListener(new ClickListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					HashMap<String, Object> map = new HashMap<String, Object>();

					List reportList = new ArrayList();
					ProfitReportDao dao = new ProfitReportDao();

					try {

						reportList.addAll(dao.getDetailsList(
								(Long) officeComboField.getValue(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue())));

						map.put("REPORT_TITLE_LABEL", getPropertyName("profit_details_report"));
						map.put("SL_NO_LABEL", getPropertyName("sl_no"));
						map.put("DATE_LABEL", getPropertyName("date"));
						map.put("PARTICULAR_LABEL", getPropertyName("particulars"));
						map.put("NUMBER_LABEL", getPropertyName("number"));
						map.put("CLIENT_LABEL", getPropertyName("client"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						map.put("REFERENCE_NO_LABEL", getPropertyName("ref_no"));
						map.put("COMMENT_LABEL", getPropertyName("comment"));
						
						
						report.setJrxmlFileName("ProfitDetailedReport");
						report.setReportFileName("Profit Details Report");
						String subTitle = getPropertyName("from")+" : "
								+ CommonUtil
										.formatDateToDDMMMYYYY(fromDateField
												.getValue())
								+ "\t "+getPropertyName("to")+" : "
								+ CommonUtil.formatDateToDDMMMYYYY(toDateField
										.getValue());

						report.setReportSubTitle(subTitle);
						report.setReportType(toInt(reportChoiceField.getValue()
								.toString()));
						report.setIncludeHeader(true);
						report.setIncludeFooter(false);
						report.setOfficeName(officeComboField
								.getItemCaption(officeComboField.getValue()));
						report.createReport(reportList, map);

						reportList.clear();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return panel;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected boolean generateReport() {
		boolean flag=false;

		HashMap<String, Object> map = new HashMap<String, Object>();

		List reportList = new ArrayList();

		try {

			double purchase = roundNumber(dao
					.getTotalPurchaseAmount((Long) officeComboField
							.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			double purchase_return = roundNumber(dao.getTotalPurchaseReturnAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			double sales = roundNumber(dao.getTotalSalesAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			double sales_return = roundNumber(dao.getTotalSalesReturnAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			double transportation = roundNumber(dao.getTotalTransportationAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			double expendetures = roundNumber(dao.getTotalExpentitureTransactionAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));
			
			double income = roundNumber(dao.getTotalIncomeTransactionAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			double of_stock = roundNumber(dao.getOfStockAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			reportList.add(new ProfitReportBean(roundNumber(purchase),
					roundNumber(purchase_return),roundNumber( sales), roundNumber(sales_return),
					roundNumber(transportation), roundNumber(expendetures),roundNumber(income),
					roundNumber((sales - sales_return)
							- (purchase - purchase_return)
							- transportation - expendetures+income),
					of_stock));
			
			map.put("SHOWSTOCK", settings.isSHOW_STOCK_IN_PROFIT_REPORT());
			
			map.put("REPORT_TITLE_LABEL", getPropertyName("profit_report"));
			map.put("GROSS_SALES_LABEL", getPropertyName("gross_sales"));
			map.put("TOTAL_SALES_RETURN_LABEL", getPropertyName("total_sales_return"));
			map.put("NET_SALES_LABEL", getPropertyName("net_sales"));
			map.put("GROSS_PURCHASE_LABEL", getPropertyName("gross_purchase"));
			map.put("TOTAL_PURCHASE_RETURN_LABEL", getPropertyName("total_purchase_return"));
			map.put("NET_PURCHASE_LABEL", getPropertyName("net_purchase"));
			map.put("TRANSPORTATION_LABEL", getPropertyName("transportation"));
			map.put("OTHER_EXPENDETURES_LABEL", getPropertyName("other_expendetures"));
			map.put("OTHER_INCOME_LABEL", getPropertyName("other_income"));
			map.put("STOCK_VALUE_LABEL", getPropertyName("stock_value"));
			map.put("NET_PROFIT_LOSS_LABEL", getPropertyName("net_profit_loss"));
			
			report.setJrxmlFileName("Profit_Report");
			report.setReportFileName("Profit_Report");
			report.setReportTitle("Profit Report");
			String subTitle = getPropertyName("from")+" : "
					+ CommonUtil
							.formatDateToDDMMMYYYY(fromDateField
									.getValue())
					+ "\t "+getPropertyName("to")+" : "
					+ CommonUtil.formatDateToDDMMMYYYY(toDateField
							.getValue());

			report.setReportSubTitle(subTitle);
			report.setReportType(toInt(reportChoiceField.getValue()
					.toString()));
			report.setIncludeHeader(true);
			report.setIncludeFooter(false);
			report.setOfficeName(officeComboField
					.getItemCaption(officeComboField.getValue()));
			report.createReport(reportList, map);

			reportList.clear();
			
			flag=true;

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return flag;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void showMainDetails(String type) {
		try {

			current_details = type;

			popHor.removeAllComponents();

			if (type.equals("Sale")) {

				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

				subtable.setVisibleColumns(new Object[] { TBC_CUSTOMER, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type);
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					
					ttl += roundNumber(objIn.getAmount());
				}
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);
				subtable.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
				popup = new SPopupView("",new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
												"<h2><u style='margin-left: 40px;'>Sales Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);

			} 
			else if (type.equals("Sales Return")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				
				subtable.setVisibleColumns(new Object[] {TBC_CUSTOMER, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					
					ttl += roundNumber(objIn.getAmount());
				}
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
				subtable.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Sales Return Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
			}
			else if (type.equals("Sales Return Item")) {

				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_GOOD, Double.class, null,getPropertyName("good_stock"), null, Align.RIGHT);
				subtable.addContainerProperty(TBC_WASTE, Double.class, null,getPropertyName("waste_qty"), null, Align.RIGHT);
				subtable.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return_qty"), null, Align.RIGHT);
				subtable.addContainerProperty(TBC_STOCK, Double.class, null,getPropertyName("stock_qty"), null, Align.RIGHT);
				
				subtable.setVisibleColumns(new Object[] { TBC_CUSTOMER, TBC_GOOD, TBC_WASTE, TBC_RETURN, TBC_STOCK});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();

					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getGood()), 
													roundNumber(objIn.getWaste()), roundNumber(objIn.getReturn_qty()), 
													roundNumber(objIn.getStock_qty()) },objIn.getId());

				}
				subtable.setFooterVisible(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Item-wise Sales Return Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);

			} else if (type.equals("Purchase")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

				subtable.setVisibleColumns(new Object[] { TBC_SUPPLIER, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					
					ttl += roundNumber(objIn.getAmount());
				}
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_SUPPLIER, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_AMOUNT, (float) 0.5);
				subtable.setColumnExpandRatio(TBC_SUPPLIER, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Purchase Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
			} else if (type.equals("Purchase Return")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

				subtable.setVisibleColumns(new Object[] { TBC_SUPPLIER, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					ttl += roundNumber(objIn.getAmount());
				}

				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_SUPPLIER, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_AMOUNT, (float) 0.5);
				subtable.setColumnExpandRatio(TBC_SUPPLIER, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Purchase Return Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
			} else if (type.equals("Purchase Return Item")) {

				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return_qty"), null, Align.RIGHT);

				subtable.setVisibleColumns(new Object[] {TBC_ITEM, TBC_RETURN});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
				}

				subtable.setFooterVisible(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnExpandRatio(TBC_ITEM, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Item Wise Purchase Return Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);

			} else if (type.equals("Transportation")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_ACCOUNT, String.class, null,getPropertyName("account_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

				subtable.setVisibleColumns(new Object[] {TBC_ACCOUNT, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					
					ttl += roundNumber(objIn.getAmount());
				}
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_ACCOUNT, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_ACCOUNT, (float) 1);
				subtable.setColumnExpandRatio(TBC_AMOUNT, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Transportation Details :",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
			} 
			else if (type.equals("Expenses")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_SN, Integer.class, null,TBC_SN, null, Align.LEFT);
				subtable.addContainerProperty(TBC_ACCOUNT, String.class, null,getPropertyName("account_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);

				subtable.setVisibleColumns(new Object[] {TBC_SN, TBC_ACCOUNT, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					subtable.addItem(new Object[] { objIn.getTransaction_type(), objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					
					ttl += roundNumber(objIn.getAmount());
				}
				subtable.setVisibleColumns(new Object[] {TBC_ACCOUNT, TBC_AMOUNT});
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_ACCOUNT, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_ACCOUNT, (float) 1);
				subtable.setColumnExpandRatio(TBC_AMOUNT, (float) 1);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Expense Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
				
			}
			else if (type.equals("Income")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_ACCOUNT, String.class, null,getPropertyName("account_name"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				subtable.setVisibleColumns(new Object[] { TBC_ACCOUNT, TBC_AMOUNT});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getAmount()) },objIn.getId());
					
					ttl += roundNumber(objIn.getAmount());
				}
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_ACCOUNT, getPropertyName("total"));
				subtable.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				subtable.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				subtable.setColumnExpandRatio(TBC_ACCOUNT, (float) 1);
				subtable.setColumnExpandRatio(TBC_COMMENT, (float) 1.5);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Income Details :",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
				
			}
			else if (type.equals("Stock")) {
				subtable = new STable();
				subtable.setWidth("750");
				subtable.setHeight("400");

				subtable.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_PURCHASE, Double.class, null,getPropertyName("purchase_qty"), null, Align.LEFT);
				subtable.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance_qty"), null, Align.LEFT);
				subtable.setVisibleColumns(new Object[] { TBC_ITEM, TBC_PURCHASE, TBC_BALANCE});

				List list = dao.getMainDetailsFromType((Long) officeComboField.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														type);
				
				Iterator it = list.iterator();
				double total=0,qty=0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					subtable.addItem(new Object[] { objIn.getName(), roundNumber(objIn.getQuantity()), roundNumber(objIn.getBalance()) },objIn.getId());
					qty+=roundNumber(objIn.getQuantity());
					total+=roundNumber(objIn.getBalance());
				}
				subtable.setFooterVisible(true);
				subtable.setSelectable(true);
				subtable.addValueChangeListener(mainValueChangeListner);
				subtable.setColumnFooter(TBC_ITEM, getPropertyName("total"));
				subtable.setColumnFooter(TBC_BALANCE, asString(roundNumber(total)));
				subtable.setColumnFooter(TBC_PURCHASE, asString(roundNumber(qty)));

				subtable.setColumnExpandRatio(TBC_BALANCE, (float) 1);
				subtable.setColumnExpandRatio(TBC_PURCHASE, (float) 1);
				subtable.setColumnExpandRatio(TBC_ITEM, (float) 3);

				popup = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Stock Details",
												725), closeSubButton), subtable));

				popHor.addComponent(popup);
				popup.setPopupVisible(true);
				popup.setHideOnMouseOut(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void showDetails(String type, long id, int ttype) {
		try {

			current_details = type;
			transaction_type = ttype;
			popupHor.removeAllComponents();

			if (type.equals("Sale")) {

				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_TYPE, String.class, null, getPropertyName("type"), null, Align.CENTER);
				table.addContainerProperty(TBC_DATE, Date.class, null, getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new Object[] {TBC_TYPE, TBC_DATE, TBC_NUMBER, TBC_CUSTOMER, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] {objIn.getParticulars() ,objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER, TBC_CUSTOMER, TBC_AMOUNT, TBC_REF, TBC_COMMENT });
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Sales Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);

			} 
			else if (type.equals("Sales Return")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);
				
				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER,TBC_CUSTOMER, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] { objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				table.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Sales Return Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			}
			else if (type.equals("Sales Return Item")) {

				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
				table.addContainerProperty(TBC_GOOD, Double.class, null,getPropertyName("good_stock"), null, Align.RIGHT);
				table.addContainerProperty(TBC_WASTE, Double.class, null,getPropertyName("waste_qty"), null, Align.RIGHT);
				table.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return_qty"), null, Align.RIGHT);
				table.addContainerProperty(TBC_STOCK, Double.class, null,getPropertyName("stock_qty"), null, Align.RIGHT);
				table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.RIGHT);
				table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("price"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);
				
				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER, TBC_CUSTOMER, TBC_ITEM, TBC_GOOD, TBC_WASTE, TBC_RETURN,
						TBC_STOCK, TBC_UNIT, TBC_RATE, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
					table.addItem(
							new Object[] { objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getItem(),objIn.getGood(),
									objIn.getWaste(), objIn.getReturned(),
									objIn.getStock_qty(), objIn.getUnit(),
									objIn.getPrice(), objIn.getRef_no(),
									objIn.getComments() }, i);
				}
				table.setFooterVisible(true);

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_CUSTOMER, (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Item-wise Sales Return Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);

			} else if (type.equals("Purchase")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER,
						TBC_SUPPLIER, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] { objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				table.setColumnFooter(TBC_SUPPLIER, getPropertyName("total"));
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_SUPPLIER, (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Purchase Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			} else if (type.equals("Purchase Return")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER,
						TBC_SUPPLIER, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] { objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}

				table.setFooterVisible(true);
				table.setSelectable(true);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				table.setColumnFooter(TBC_SUPPLIER, getPropertyName("total"));
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_SUPPLIER, (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Purchase Return Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			} else if (type.equals("Purchase Return Item")) {

				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
				table.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return_qty"), null, Align.RIGHT);
				table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.RIGHT);
				table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("price"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER,TBC_SUPPLIER, TBC_ITEM, TBC_RETURN, TBC_UNIT, TBC_RATE,
						TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
					table.addItem(
							new Object[] { objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getItem(),
									objIn.getReturned(), objIn.getUnit(),
									objIn.getPrice(), objIn.getRef_no(),
									objIn.getComments() }, i);
				}

				table.setFooterVisible(true);
				// table.setColumnFooter(TBC_SUPPLIER, "Total :");
				// table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_SUPPLIER, (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Item Wise Purchase Return Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);

			} else if (type.equals("Transportation")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");
				table.addContainerProperty(TBC_TYPE, String.class, null, getPropertyName("type"), null, Align.CENTER);
				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_ACCOUNT, String.class, null,getPropertyName("account_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new Object[] { TBC_TYPE,TBC_DATE, TBC_NUMBER,TBC_ACCOUNT, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] { objIn.getParticulars(),objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setVisibleColumns(new Object[] { TBC_DATE, TBC_NUMBER,TBC_ACCOUNT, TBC_AMOUNT, TBC_REF, TBC_COMMENT });
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				table.setColumnFooter(TBC_ACCOUNT, "Total :");
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_ACCOUNT, (float) 1);
				table.setColumnExpandRatio(TBC_COMMENT, (float) 1.5);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Transportation Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			} 
			else if (type.equals("Expenses")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_SN, Integer.class, null,TBC_SN, null, Align.CENTER);
				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_ACCOUNT, String.class, null,getPropertyName("account_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new Object[] {TBC_SN, TBC_DATE, TBC_NUMBER,TBC_ACCOUNT, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] {objIn.getTransaction_type(), objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setVisibleColumns(new Object[] {TBC_DATE, TBC_NUMBER,TBC_ACCOUNT, TBC_AMOUNT, TBC_REF, TBC_COMMENT });
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				table.setColumnFooter(TBC_ACCOUNT, getPropertyName("total"));
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_ACCOUNT, (float) 1);
				table.setColumnExpandRatio(TBC_COMMENT, (float) 1.5);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Expense Details",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
				
			}
			else if (type.equals("Income")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_TYPE, String.class, null,getPropertyName("type"), null, Align.CENTER);
				table.addContainerProperty(TBC_DATE, Date.class, null,getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty(TBC_NUMBER, String.class, null,getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty(TBC_ACCOUNT, String.class, null,getPropertyName("account_name"), null, Align.LEFT);
				table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty(TBC_REF, String.class, null,getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty(TBC_COMMENT, String.class, null,getPropertyName("comments"), null, Align.LEFT);
				table.setVisibleColumns(new Object[] { TBC_TYPE, TBC_DATE, TBC_NUMBER,TBC_ACCOUNT, TBC_AMOUNT, TBC_REF, TBC_COMMENT });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				double ttl = 0;
				Iterator it = list.iterator();
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					table.addItem(
							new Object[] { objIn.getParticulars(), objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.addValueChangeListener(valChangeLis);
				table.addActionHandler(action1);
				table.setColumnFooter(TBC_ACCOUNT, getPropertyName("total"));
				table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(ttl)));

				table.setColumnExpandRatio(TBC_DATE, (float) 0.5);
				table.setColumnExpandRatio(TBC_ACCOUNT, (float) 1);
				table.setColumnExpandRatio(TBC_COMMENT, (float) 1.5);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Income Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
				
			}
			else if (type.equals("Stock")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
				table.addContainerProperty(TBC_PURCHASE, Double.class, null,getPropertyName("purchase_qty"), null, Align.LEFT);
				table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance_qty"), null, Align.LEFT);
				table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
				table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("rate"), null, Align.RIGHT);
				table.setVisibleColumns(new Object[] { TBC_ITEM, TBC_PURCHASE, TBC_BALANCE, TBC_UNIT,TBC_RATE });

				List list = dao.getDetailsFromType((Long) officeComboField.getValue(),
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
													type, id, ttype);
				Iterator it = list.iterator();
				int i = 0;
//				double total=0,qty=0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
					table.addItem(
							new Object[] { objIn.getItem(), objIn.getPurchase_qty(),
									objIn.getCurrent_qty(), objIn.getUnit(),objIn.getRate() },
							i);
//					qty+=objIn.getCurrent_qty();
//					total+=objIn.getRate();
				}
				table.setFooterVisible(true);
//				table.addActionHandler(action1);
				
				table.setColumnFooter(TBC_ITEM, getPropertyName("total"));
//				table.setColumnFooter(TBC_BALANCE, asString(roundNumber(qty)));
//				table.setColumnFooter(TBC_RATE, asString(roundNumber(total)));

				table.setColumnExpandRatio(TBC_RATE, (float) 1);
				table.setColumnExpandRatio(TBC_UNIT, (float) 1);
				table.setColumnExpandRatio(TBC_BALANCE, (float) 1);
				table.setColumnExpandRatio(TBC_PURCHASE, (float) 1);
				table.setColumnExpandRatio(TBC_ITEM, (float) 3);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Stock Details :",
												970), closeButton), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void showSubDetails(long id, int transaction_type) {
		try {

			popupContainer.removeAllComponents();

			if (current_details.equals("Sale")) {
				
				if(table.getValue()!=null){
					Item item=table.getItem(table.getValue());
					
					String type=item.getItemProperty(TBC_TYPE).getValue().toString();
					
					SFormLayout form = new SFormLayout();
					SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
					grid.setColumns(12);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, TBC_ITEM), 1, 0);
					grid.addComponent(new SLabel(null, "Qty"), 2, 0);
					grid.addComponent(new SLabel(null, TBC_UNIT), 3, 0);
					grid.addComponent(new SLabel(null, TBC_RATE), 4, 0);
					grid.addComponent(new SLabel(null, "Discount"), 5, 0);
					grid.addComponent(new SLabel(null, TBC_AMOUNT), 6, 0);
					grid.setSpacing(true);
					
					if(type.equalsIgnoreCase("Sale")){
						SalesModel objModel = new SalesDao().getSale(id);
						form.addComponent(new SHTMLLabel(null, "<h2><u>Sale</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("sales_no"),objModel.getSales_number() + ""));
						form.addComponent(new SLabel(getPropertyName("customer"),objModel.getCustomer().getName()));
						form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
//						form.addComponent(new SLabel(getPropertyName("max_credit_period"), objModel.getCredit_period() + ""));
		
//						if (isShippingChargeEnable())
//							form.addComponent(new SLabel(getPropertyName("shipping_charge"), objModel.getShipping_charge() + ""));
						form.addComponent(new SLabel(getPropertyName("net_amount"),objModel.getAmount() + ""));
						form.addComponent(new SLabel(getPropertyName("paid_amount"),objModel.getPayment_amount() + ""));
						
						grid.setRows(objModel.getInventory_details_list().size() + 3);
						int i = 1;
						SalesInventoryDetailsModel invObj;
						Iterator itmItr = objModel.getInventory_details_list()
								.iterator();
						while (itmItr.hasNext()) {
							invObj = (SalesInventoryDetailsModel) itmItr.next();
		
							grid.addComponent(new SLabel(null, i + ""), 0, i);
							grid.addComponent(new SLabel(null, invObj.getItem()
									.getName()), 1, i);
							grid.addComponent(new SLabel(null, invObj.getQunatity()
									+ ""), 2, i);
							grid.addComponent(new SLabel(null, invObj.getUnit()
									.getSymbol()), 3, i);
							grid.addComponent(new SLabel(null, invObj.getUnit_price()
									+ ""), 4, i);
							grid.addComponent(
									new SLabel(null, invObj.getDiscount() + ""),
									5, i);
							grid.addComponent(
									new SLabel(null, (invObj.getUnit_price()
											* invObj.getQunatity()
											- invObj.getDiscount() + invObj
												.getTaxAmount()) + ""), 6, i);
							i++;
						}
		
						form.addComponent(grid);
						form.addComponent(new SLabel(getPropertyName("comment"),
								objModel.getComments()));
					}
					else if(type.equalsIgnoreCase("Rental")){
						
						RentalTransactionModel objModel = new RentalTransactionNewDao().getRentalTransactionModel(id);
						form.addComponent(new SHTMLLabel(null, "<h2><u>Rental</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("rental_no"),objModel.getSales_number() + ""));
						form.addComponent(new SLabel(getPropertyName("customer"),objModel.getCustomer().getName()));
						form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
						form.addComponent(new SLabel(getPropertyName("max_credit_period"), objModel.getCredit_period() + ""));
		
						if (isShippingChargeEnable())
							form.addComponent(new SLabel(getPropertyName("shipping_charge"), objModel.getShipping_charge() + ""));
						form.addComponent(new SLabel(getPropertyName("net_amount"),objModel.getAmount() + ""));
						form.addComponent(new SLabel(getPropertyName("paid_amount"),objModel.getPayment_amount() + ""));
						
						grid.setRows(objModel.getInventory_details_list().size() + 3);
						int i = 1;
						RentalTransactionDetailsModel invObj;
						Iterator itmItr = objModel.getInventory_details_list()
								.iterator();
						while (itmItr.hasNext()) {
							invObj = (RentalTransactionDetailsModel) itmItr.next();
		
							grid.addComponent(new SLabel(null, i + ""), 0, i);
							grid.addComponent(new SLabel(null, invObj.getRental().getName()), 1, i);
							grid.addComponent(new SLabel(null, invObj.getQunatity()+ ""), 2, i);
							grid.addComponent(new SLabel(null, "No"), 3, i);
							grid.addComponent(new SLabel(null, invObj.getUnit_price()+ ""), 4, i);
							grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
							grid.addComponent(new SLabel(null, (invObj.getUnit_price()
											* invObj.getQunatity()
											- invObj.getDiscount_amount() + invObj
												.getTax_amount()) + ""), 6, i);
							i++;
						}
						form.addComponent(grid);
						form.addComponent(new SLabel(getPropertyName("comment"),objModel.getComments()));
					} 
	
					form.setStyleName("grid_max_limit");
	
					popupContainer.removeAllComponents();
					subPop = new SPopupView("", form);
					popupContainer.addComponent(subPop);
					subPop.setPopupVisible(true);
					subPop.setHideOnMouseOut(false);
				}
			} else if (current_details.equals("Sales Return")) {

				SalesReturnModel objModel = new SalesReturnDao()
						.getSalesReturnModel(id);

				SFormLayout form = new SFormLayout();
				form.addComponent(new SHTMLLabel(null,
						"<h2><u>Sales Return</u></h2>"));
//				form.addComponent(new SLabel(getPropertyName("credit_note_no"),
//						objModel.getCredit_note_no() + ""));
				form.addComponent(new SLabel(getPropertyName("customer"),
						objModel.getCustomer().getName()));
				form.addComponent(new SLabel(getPropertyName("date"),
						CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));

				form.addComponent(new SLabel(getPropertyName("net_amount"),
						objModel.getAmount() + ""));
//				form.addComponent(new SLabel(getPropertyName("paid_amount"),
//						objModel.getPayment_amount() + ""));

				SGridLayout grid = new SGridLayout(
						getPropertyName("item_details"));
				grid.setColumns(12);
				grid.setRows(objModel.getInventory_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, TBC_ITEM), 1, 0);
				grid.addComponent(new SLabel(null, TBC_GOOD), 2, 0);
				grid.addComponent(new SLabel(null, TBC_UNIT), 3, 0);
				grid.addComponent(new SLabel(null, TBC_STOCK), 4, 0);
				grid.addComponent(new SLabel(null, "Purch. Rtn Qty"), 5, 0);
				grid.addComponent(new SLabel(null, TBC_WASTE), 6, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 7, 0);
				grid.addComponent(new SLabel(null, "Discount"), 8, 0);
				grid.addComponent(new SLabel(null, TBC_AMOUNT), 9, 0);
				grid.setSpacing(true);

				int i = 1;
				SalesReturnInventoryDetailsModel invObj;
				Iterator itmItr = objModel.getInventory_details_list()
						.iterator();
//				while (itmItr.hasNext()) {
//					invObj = (SalesReturnInventoryDetailsModel) itmItr.next();
//					grid.addComponent(new SLabel(null, i + ""), 0, i);
//					grid.addComponent(new SLabel(null, invObj.getItem()
//							.getName()), 1, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getGood_stock() + ""),
//							2, i);
//					grid.addComponent(new SLabel(null, invObj.getUnit()
//							.getSymbol()), 3, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getStock_quantity() + ""),
//							4, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getReturned_quantity() + ""),
//							5, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getWaste_quantity() + ""),
//							6, i);
//					grid.addComponent(new SLabel(null, invObj.getUnit_price()
//							+ ""), 7, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getDiscount_amount() + ""),
//							8, i);
//					grid.addComponent(
//							new SLabel(
//									null,
//									(invObj.getUnit_price()
//											* (invObj.getStock_quantity()
//													+ invObj.getReturned_quantity() + invObj
//														.getWaste_quantity()+invObj.getGood_stock())
//											- invObj.getDiscount_amount() + invObj
//												.getTax_amount())
//											+ ""), 9, i);
//					i++;
//				}

				form.addComponent(grid);
				form.addComponent(new SLabel(getPropertyName("comment"),
						objModel.getComments()));

				form.setStyleName("grid_max_limit");

				popupContainer.removeAllComponents();
				subPop = new SPopupView("", form);
				popupContainer.addComponent(subPop);
				subPop.setPopupVisible(true);
				subPop.setHideOnMouseOut(false);
				
			} else if (current_details.equals("Purchase")) {
				PurchaseModel objModel = new PurchaseDao().getPurchaseModel(id);
				SFormLayout form = new SFormLayout();
				form.addComponent(new SHTMLLabel(null,
						"<h2><u>Purchase</u></h2>"));
				form.addComponent(new SLabel(getPropertyName("purchase_no"),
						objModel.getPurchase_no() + ""));
				form.addComponent(new SLabel(getPropertyName("supplier"),
						objModel.getSupplier().getName()));
				form.addComponent(new SLabel(getPropertyName("date"),
						CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
//				form.addComponent(new SLabel(
//						getPropertyName("max_credit_period"), objModel
//								.getCredit_period() + ""));

//				if (isShippingChargeEnable())
//					form.addComponent(new SLabel(
//							getPropertyName("shipping_charge"), objModel
//									.getShipping_charge() + ""));

				form.addComponent(new SLabel(getPropertyName("net_amount"),
						objModel.getAmount() + ""));
//				form.addComponent(new SLabel(getPropertyName("paid_amount"),
//						objModel.getPayment_amount() + ""));

				SGridLayout grid = new SGridLayout(
						getPropertyName("item_details"));
				grid.setColumns(12);
				grid.setRows(objModel.getPurchase_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, TBC_ITEM), 1, 0);
				grid.addComponent(new SLabel(null, "Qty"), 2, 0);
				grid.addComponent(new SLabel(null, TBC_UNIT), 3, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 4, 0);
				grid.addComponent(new SLabel(null, "Discount"), 5, 0);
				grid.addComponent(new SLabel(null, TBC_AMOUNT), 6, 0);
				grid.setSpacing(true);
				int i = 1;
				PurchaseInventoryDetailsModel invObj;
				Iterator itmItr = objModel.getPurchase_details_list()
						.iterator();
				while (itmItr.hasNext()) {
					invObj = (PurchaseInventoryDetailsModel) itmItr.next();

					grid.addComponent(new SLabel(null, i + ""), 0, i);
					grid.addComponent(new SLabel(null, invObj.getItem()
							.getName()), 1, i);
					grid.addComponent(new SLabel(null, invObj.getQunatity()
							+ ""), 2, i);
					grid.addComponent(new SLabel(null, invObj.getUnit()
							.getSymbol()), 3, i);
					grid.addComponent(new SLabel(null, invObj.getUnit_price()
							+ ""), 4, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getDiscount_amount() + ""),
//							5, i);
//					grid.addComponent(
//							new SLabel(null, (invObj.getUnit_price()
//									* invObj.getQunatity()
//									- invObj.getDiscount_amount() + invObj
//										.getTax_amount()) + ""), 6, i);
					i++;
				}

				form.addComponent(grid);
				form.addComponent(new SLabel(getPropertyName("comment"),
						objModel.getComments()));

				form.setStyleName("grid_max_limit");

				popupContainer.removeAllComponents();
				subPop = new SPopupView("", form);
				popupContainer.addComponent(subPop);
				subPop.setPopupVisible(true);
				subPop.setHideOnMouseOut(false);
				
			} else if (current_details.equals("Purchase Return")) {
				PurchaseReturnModel objModel = new PurchaseReturnDao()
						.getPurchaseReturnModel(id);

				SFormLayout form = new SFormLayout();
				form.addComponent(new SHTMLLabel(null,
						"<h2><u>Purchase Return</u></h2>"));
//				form.addComponent(new SLabel(getPropertyName("debit_note_no"),
//						objModel.getDebit_note_no() + ""));
				form.addComponent(new SLabel(getPropertyName("supplier"),
						objModel.getSupplier().getName()));
				form.addComponent(new SLabel(getPropertyName("date"),
						CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));

				form.addComponent(new SLabel(getPropertyName("net_amount"),
						objModel.getAmount() + ""));
//				form.addComponent(new SLabel(getPropertyName("paid_amount"),
//						objModel.getPayment_amount() + ""));

				SGridLayout grid = new SGridLayout(
						getPropertyName("item_details"));
				grid.setColumns(12);
				grid.setRows(objModel.getInventory_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, TBC_ITEM), 1, 0);
				grid.addComponent(new SLabel(null, "Qty"), 2, 0);
				grid.addComponent(new SLabel(null, TBC_UNIT), 3, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 4, 0);
				grid.addComponent(new SLabel(null, "Discount"), 5, 0);
				grid.addComponent(new SLabel(null, TBC_AMOUNT), 6, 0);
				grid.setSpacing(true);

				int i = 1;
				PurchaseReturnInventoryDetailsModel invObj;
				Iterator itmItr = objModel.getInventory_details_list()
						.iterator();
				while (itmItr.hasNext()) {
					invObj = (PurchaseReturnInventoryDetailsModel) itmItr
							.next();
					grid.addComponent(new SLabel(null, i + ""), 0, i);
					grid.addComponent(new SLabel(null, invObj.getItem()
							.getName()), 1, i);
					grid.addComponent(new SLabel(null, invObj.getQunatity()
							+ ""), 2, i);
					grid.addComponent(new SLabel(null, invObj.getUnit()
							.getSymbol()), 3, i);
					grid.addComponent(new SLabel(null, invObj.getUnit_price()
							+ ""), 4, i);
//					grid.addComponent(
//							new SLabel(null, invObj.getDiscount_amount() + ""),
//							5, i);
//					grid.addComponent(
//							new SLabel(null, (invObj.getUnit_price()
//									* invObj.getQunatity()
//									- invObj.getDiscount_amount() + invObj
//										.getTax_amount()) + ""), 6, i);
					i++;
				}

				form.addComponent(grid);
				form.addComponent(new SLabel("Comment :", objModel
						.getComments()));

				form.setStyleName("grid_max_limit");

				popupContainer.removeAllComponents();
				subPop = new SPopupView("", form);
				popupContainer.addComponent(subPop);
				subPop.setPopupVisible(true);
				subPop.setHideOnMouseOut(false);
				
			} else if (current_details.equals("Transportation")) {
				
				if(table.getValue()!=null){
					Item item=table.getItem(table.getValue());
					String type=item.getItemProperty(TBC_TYPE).getValue().toString();
					
					SFormLayout form = new SFormLayout();
					SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
					grid.setColumns(12);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, TBC_ITEM), 1, 0);
					grid.addComponent(new SLabel(null, "Qty"), 2, 0);
					grid.addComponent(new SLabel(null, TBC_UNIT), 3, 0);
					grid.addComponent(new SLabel(null, TBC_RATE), 4, 0);
					grid.addComponent(new SLabel(null, "Discount"), 5, 0);
					grid.addComponent(new SLabel(null, TBC_AMOUNT), 6, 0);
					grid.setSpacing(true);
					
					if(type.equalsIgnoreCase("Transportation Payment")){
						TransportationPaymentModel objModel = new TransportationPaymentDao().getTransportationPaymentModel(id);
						if (objModel != null) {

							
							if (objModel.getType() == 1) {
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Transportation Cash</u></h2>"));
							} else {
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Transportation Credit</u></h2>"));
							}

							form.addComponent(new SLabel(
									getPropertyName("transportation_no"), objModel
											.getId() + ""));
							String tansp = new LedgerDao().getLedgerNameFromID(objModel
									.getTransportation_id());
							if (tansp != null)
								form.addComponent(new SLabel(
										getPropertyName("transportation"), tansp));

							// LedgerModel
							// toAcc=ledDao.getLedgeer(objModel.getTo_account_id());
							// if(toAcc!=null)
							// form.addComponent(new
							// SLabel("To Account :",toAcc.getName()));

							form.addComponent(new SLabel(getPropertyName("date"),
									CommonUtil.getUtilDateFromSQLDate(objModel
											.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("transportation_amount"), objModel
											.getSupplier_amount() + ""));
							form.addComponent(new SLabel(getPropertyName("discount"),
									objModel.getDiscount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("payment_amount"), objModel
											.getPayment_amount() + ""));

							form.addComponent(new SLabel(
									getPropertyName("description"), objModel
											.getDescription()));
							form.setWidth("400");
						}
					}
					else if(type.equalsIgnoreCase("Rental")){
						RentalTransactionModel objModel = new RentalTransactionNewDao().getRentalTransactionModel(id);
						form.addComponent(new SHTMLLabel(null, "<h2><u>Rental</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("sales_no"),objModel.getSales_number() + ""));
						form.addComponent(new SLabel(getPropertyName("customer"),objModel.getCustomer().getName()));
						form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
						form.addComponent(new SLabel(getPropertyName("max_credit_period"), objModel.getCredit_period() + ""));
		
						if (isShippingChargeEnable())
							form.addComponent(new SLabel(getPropertyName("shipping_charge"), objModel.getShipping_charge() + ""));
						form.addComponent(new SLabel(getPropertyName("net_amount"),objModel.getAmount() + ""));
						form.addComponent(new SLabel(getPropertyName("paid_amount"),objModel.getPayment_amount() + ""));
						
						grid.setRows(objModel.getInventory_details_list().size() + 3);
						int i = 1;
						RentalTransactionDetailsModel invObj;
						Iterator itmItr = objModel.getInventory_details_list()
								.iterator();
						while (itmItr.hasNext()) {
							invObj = (RentalTransactionDetailsModel) itmItr.next();
		
							grid.addComponent(new SLabel(null, i + ""), 0, i);
							grid.addComponent(new SLabel(null, invObj.getRental().getName()), 1, i);
							grid.addComponent(new SLabel(null, invObj.getQunatity()+ ""), 2, i);
							grid.addComponent(new SLabel(null, "No"), 3, i);
							grid.addComponent(new SLabel(null, invObj.getUnit_price()+ ""), 4, i);
							grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
							grid.addComponent(new SLabel(null, (invObj.getUnit_price()
											* invObj.getQunatity()
											- invObj.getDiscount_amount() + invObj
												.getTax_amount()) + ""), 6, i);
							i++;
						}
						form.addComponent(grid);
						form.addComponent(new SLabel(getPropertyName("comment"),objModel.getComments()));
					}
					
					form.setStyleName("grid_max_limit");

					popupContainer.removeAllComponents();
					subPop = new SPopupView("", form);
					popupContainer.addComponent(subPop);
					subPop.setPopupVisible(true);
					subPop.setHideOnMouseOut(false);
				}
				
			} 
			else if (current_details.equals("Expenses")) {
				
				SFormLayout form = new SFormLayout();
				SGridLayout grid = new SGridLayout(getPropertyName("transaction_details"));
				if(transaction_type==SConstants.EXPENDETURE_TRANSACTION){
					
					PaymentDepositModel objModel = new ExpendetureTransactionDao().getExpendetureTransaction(id);
					form.addComponent(new SHTMLLabel(null,"<h2><u>Expenditure Transaction</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("bill_no"),objModel.getBill_no() + ""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.formatDateToDDMMYYYY(objModel.getDate())));
					if (objModel.getCash_or_check() == 2)
						form.addComponent(new SLabel(getPropertyName("cash_cheque"), "Cheque"));
					else
						form.addComponent(new SLabel(getPropertyName("cash_cheque"), "Cash"));

					form.addComponent(new SLabel(getPropertyName("ref_no"),objModel.getRef_no()));
					form.addComponent(new SLabel(getPropertyName("memo"), objModel.getMemo()));
					
					grid.setColumns(5);
					grid.setRows(objModel.getTransaction().getTransaction_details_list().size() + 3);
					grid.addComponent(new SLabel(null, "#"), 0, 0);
					grid.addComponent(new SLabel(null, getPropertyName("from_account")), 1, 0);
					grid.addComponent(new SLabel(null, getPropertyName("to_account")), 2, 0);
					grid.addComponent(new SLabel(null, getPropertyName("amount")), 3, 0);
					grid.setSpacing(true);
					int i = 1;
					TransactionDetailsModel invObj;
					Iterator itmItr = objModel.getTransaction().getTransaction_details_list().iterator();
					while (itmItr.hasNext()) {
						invObj = (TransactionDetailsModel) itmItr.next();
						grid.addComponent(new SLabel(null, i + ""), 0, i);
						grid.addComponent(new SLabel(null, invObj.getFromAcct().getName()), 1, i);
						grid.addComponent(new SLabel(null, invObj.getToAcct().getName()), 2, i);
						grid.addComponent(new SLabel(null, invObj.getAmount() + ""), 3, i);
						i++;
					}
					form.addComponent(grid);
					form.setStyleName("grid_max_limit");
				}
				
				if(transaction_type==SConstants.PAYROLL_PAYMENTS){
					
					TransactionModel transaction=dao.getTransaction(id);
					SalaryDisbursalNewModel salary=dao.getSalaryDisbursalNewModel(transaction.getTransaction_id());
					form.addComponent(new SHTMLLabel(null,"<h2><u>Salary Disbursal</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("employee"),salary.getEmploy().getFirst_name()));
					form.addComponent(new SLabel(getPropertyName("disbursal_date"),CommonUtil.formatDateToDDMMYYYY(salary.getDispursal_date())));
					Calendar cal=getCalendar();
					cal.setTime(salary.getMonth());
					form.addComponent(new SLabel(getPropertyName("month"),CommonUtil.getMonthName(cal.get(Calendar.MONTH))));
					form.addComponent(new SLabel(getPropertyName("amount"),CommonUtil.roundNumber(salary.getPaid_amount())+""));
				}
				
				if(transaction_type==SConstants.EMPLOYEE_ADVANCE_PAYMENTS){
					
					EmployeeAdvancePaymentModel advance=new EmployeeAdvancePaymentDao().getEmployeeAdvancePaymentModel(id);
					form.addComponent(new SHTMLLabel(null,"<h2><u>Employee Advance Payment</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("employee"),new LoginDao().getUserFromLoginId(advance.getLogin_id()).getFirst_name()));
					form.addComponent(new SLabel(getPropertyName("payment_id"),advance.getPayment_id() + ""));
//					form.addComponent(new SLabel(getPropertyName("amount"),CommonUtil.roundNumber(advance.getPayment_amount()) + ""));
					form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.formatDateToDDMMYYYY(advance.getDate())));
					if (advance.getCash_or_check() == 2)
						form.addComponent(new SLabel(getPropertyName("cash_cheque"), "Cheque"));
					else
						form.addComponent(new SLabel(getPropertyName("cash_cheque"), "Cash"));
					form.addComponent(new SLabel(getPropertyName("description"),advance.getDescription()));
					
				}
				popupContainer.removeAllComponents();
				subPop = new SPopupView("", form);
				popupContainer.addComponent(subPop);
				subPop.setPopupVisible(true);
				subPop.setHideOnMouseOut(false);
			}
			
			else if (current_details.equals("Income")) {
				PaymentDepositModel objModel = new IncomeTransactionDao().getIncomeTransaction(id);

				SFormLayout form = new SFormLayout();
				form.addComponent(new SHTMLLabel(null,"<h2><u>Income Transaction</u></h2>"));

				form.addComponent(new SLabel(getPropertyName("bill_no"),objModel.getBill_no() + ""));

				form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
				if (objModel.getCash_or_check() == 2)
					form.addComponent(new SLabel(getPropertyName("cash_cheque"), "Cheque"));
				else
					form.addComponent(new SLabel(getPropertyName("cash_cheque"), "Cash"));

				form.addComponent(new SLabel(getPropertyName("ref_no"),objModel.getRef_no()));

				form.addComponent(new SLabel(getPropertyName("memo"), objModel.getMemo()));

				SGridLayout grid = new SGridLayout(getPropertyName("transaction_details"));
				grid.setColumns(5);
				grid.setRows(objModel.getTransaction().getTransaction_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, getPropertyName("from_account")), 1, 0);
				grid.addComponent(new SLabel(null, getPropertyName("to_account")), 2, 0);
				grid.addComponent(new SLabel(null, getPropertyName("amount")), 3, 0);
				grid.setSpacing(true);

				int i = 1;
				TransactionDetailsModel invObj;
				Iterator itmItr = objModel.getTransaction()
						.getTransaction_details_list().iterator();
				while (itmItr.hasNext()) {
					invObj = (TransactionDetailsModel) itmItr.next();
					grid.addComponent(new SLabel(null, i + ""), 0, i);
					grid.addComponent(new SLabel(null, invObj.getFromAcct()
							.getName()), 1, i);
					grid.addComponent(new SLabel(null, invObj.getToAcct()
							.getName()), 2, i);
					grid.addComponent(
							new SLabel(null, invObj.getAmount() + ""), 3, i);
					i++;
				}

				form.addComponent(grid);
				form.setStyleName("grid_max_limit");

				popupContainer.removeAllComponents();
				subPop = new SPopupView("", form);
				popupContainer.addComponent(subPop);
				subPop.setPopupVisible(true);
				subPop.setHideOnMouseOut(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("static-access")
	public void openOption(long id,int transaction_type) {
		try {

			if (current_details.equals("Sale")) {
				if(table.getValue()!=null){
					Item item=table.getItem(table.getValue());
					String type=item.getItemProperty(TBC_TYPE).getValue().toString();
					if(type.equalsIgnoreCase("Sale")){
						SalesNewUI sale = new SalesNewUI();
						sale.setCaption("Sales");
						sale.getSalesNumberList().setValue(id);
						sale.center();
						getUI().getCurrent().addWindow(sale);
						sale.addCloseListener(closeListener);
						sale.setModal(true);
					}
					else if(type.equalsIgnoreCase("Rental")){
						RentalTransactionNewUI sale = new RentalTransactionNewUI();
						sale.setCaption("Rental");
						sale.getSalesNumberList().setValue(id);
						sale.center();
						getUI().getCurrent().addWindow(sale);
						sale.addCloseListener(closeListener);
						sale.setModal(true);
					}
				}
			} else if (current_details.equals("Sales Return")) {
				SalesReturnNewUI retrn = new SalesReturnNewUI();
				retrn.setCaption("Sales Return");
				retrn.getSalesreturnNumberList().setValue(id);
				retrn.center();
				getUI().getCurrent().addWindow(retrn);
				retrn.addCloseListener(closeListener);
				retrn.setModal(true);
			} else if (current_details.equals("Purchase")) {
				PurchaseUI purchase = new PurchaseUI();
				purchase.setCaption("Purchase");
				purchase.getPurchaseNumberList().setValue(id);
				purchase.center();
				getUI().getCurrent().addWindow(purchase);
				purchase.addCloseListener(closeListener);
				purchase.setModal(true);
			} else if (current_details.equals("Purchase Return")) {
				PurchaseReturnUI retrn = new PurchaseReturnUI();
				retrn.setCaption("Purchase Return");
//				retrn.getDebitNoteNoComboField().setValue(id);
				retrn.center();
				getUI().getCurrent().addWindow(retrn);
				retrn.addCloseListener(closeListener);
				retrn.setModal(true);
			} else if (current_details.equals("Transportation")) {
				if(table.getValue()!=null){
					Item item=table.getItem(table.getValue());
					String type=item.getItemProperty(TBC_TYPE).getValue().toString();
					if(type.equalsIgnoreCase("Transportation Payment")){
						TransportationPaymentsUI trans = new TransportationPaymentsUI();
						trans.setCaption("Transportation Payment");
						trans.getPaymentIdComboField().setValue(id);
						trans.center();
						getUI().getCurrent().addWindow(trans);
						trans.addCloseListener(closeListener);
						trans.setModal(true);
					}
					else if(type.equalsIgnoreCase("Rental")){
						RentalTransactionNewUI sale = new RentalTransactionNewUI();
						sale.setCaption("Rental");
						sale.getSalesNumberList().setValue(id);
						sale.center();
						getUI().getCurrent().addWindow(sale);
						sale.addCloseListener(closeListener);
						sale.setModal(true);
					}
				}
					
				
			} 
			else if (current_details.equals("Expenses")) {
				
				if(transaction_type==SConstants.EXPENDETURE_TRANSACTION){
					PaymentDepositModel objModel = new ExpendetureTransactionDao().getExpendetureTransaction(id);
					
					ExpendetureTransactionUI expend = new ExpendetureTransactionUI();
					expend.setCaption("Expenditure Transactions");
					expend.loadData(objModel.getId());
					expend.center();
					getUI().getCurrent().addWindow(expend);
					expend.addCloseListener(closeListener);
					expend.setModal(true);
					
				}
				
				if(transaction_type==SConstants.PAYROLL_PAYMENTS){
					TransactionModel transaction=dao.getTransaction(id);
					SalaryDisbursalNewModel salary=dao.getSalaryDisbursalNewModel(transaction.getTransaction_id());	
					
					SalaryDisbursalNewUI expend = new SalaryDisbursalNewUI();
					expend.setCaption("Salary Disbursal");
					Calendar cal=getCalendar();
					cal.setTime(salary.getMonth());
					expend.forMonthDateField.setValue(cal.getTime());
					expend.center();
					getUI().getCurrent().addWindow(expend);
					expend.addCloseListener(closeListener);
					expend.setModal(true);
					
				}
				
				if(transaction_type==SConstants.EMPLOYEE_ADVANCE_PAYMENTS){
					EmployeeAdvancePaymentModel advance=new EmployeeAdvancePaymentDao().getEmployeeAdvancePaymentModel(id);
					EmployeeAdvancePaymentsUI expend = new EmployeeAdvancePaymentsUI();
					expend.setCaption("Employee Advance Payment");
					expend.loadPaymentNo(advance.getId());
					expend.center();
					getUI().getCurrent().addWindow(expend);
					expend.addCloseListener(closeListener);
					expend.setModal(true);
				}
				
			}
			else if (current_details.equals("Income")) {
				IncomeTransactionUI expend = new IncomeTransactionUI();
				expend.setCaption("Income Transaction");
				expend.loadData(id);
				expend.center();
				getUI().getCurrent().addWindow(expend);
				expend.addCloseListener(closeListener);
				expend.setModal(true);
			}
			subPop.setPopupVisible(false);
			pop.setPopupVisible(false);
			popup.setPopupVisible(false);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void loadTable(){
		try {
			if(subtable.getValue()!=null){
				if (current_details.equals("Sale")) {
					
					showDetails("Sale", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Sales Return")) {
					
					showDetails("Sales Return", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Sales Return Item")) {

					showDetails("Sales Return Item", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Purchase")) {
					
					showDetails("Purchase", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Purchase Return")) {
					
					showDetails("Purchase Return", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Purchase Return Item")) {

					showDetails("Purchase Return Item", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Transportation")) {

					showDetails("Transportation", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Expenses")) {
					
					Item item=subtable.getItem(subtable.getValue());
					int ttype=(Integer)item.getItemProperty(TBC_SN).getValue();
					
					showDetails("Expenses", (Long) subtable.getValue(), ttype);
					
				} else if (current_details.equals("Income")) {
					
					showDetails("Income", (Long) subtable.getValue(), 0);
					
				} else if (current_details.equals("Stock")) {
				
					showDetails("Stock", (Long) subtable.getValue(), 0);
					
				}
				
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Boolean isValid() {

		fromDateField.setComponentError(null);
		if (fromDateField.getValue().after(toDateField.getValue())) {
			setRequiredError(fromDateField,
					getPropertyName("invalid_selection"), true);
			return false;
		}
		return true;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
