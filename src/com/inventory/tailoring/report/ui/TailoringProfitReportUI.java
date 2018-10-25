package com.inventory.tailoring.report.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.acct.ui.ExpendetureTransactionUI;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.payment.ui.TransportationPaymentsUI;
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
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.dao.TailoringSalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.model.TailoringSalesInventoryDetailsModel;
import com.inventory.sales.model.TailoringSalesModel;
import com.inventory.sales.ui.SalesReturnNewUI;
import com.inventory.sales.ui.TailoringSalesUI;
import com.inventory.tailoring.report.dao.TailoringProfitReportDao;
import com.inventory.transaction.model.TransactionDetailsModel;
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
import com.webspark.core.Report;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 23, 2014
 */
public class TailoringProfitReportUI extends SparkLogic {

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
	SHTMLLabel transportation, otherExpense, stockBal, netProfit;

	SButtonLink grosSalDetails, salesReturnDetails, salesReturnItemDetails,
			grossPurchaseDetails;
	SButtonLink purchaseReturnDetails, purchaseReturnItemDetails,
			transportationDetails, expenseDetails;
	SButtonLink stockDetails;
	SHorizontalLayout popupHor, popupContainer;
	SNativeButton closeBtn;
	TailoringProfitReportDao dao;
	SPopupView pop, subPop;

	STable table;

	Handler action1;
	CloseListener closeListener;

	ValueChangeListener valChangeLis;

	String current_details = "";
	
	SettingsValuePojo settings;
	WrappedSession session;
	
	SConfirmWithReview confirmBox;
	ReportReview review;

	@Override
	public SPanel getGUI() {

		setSize(1000, 350);

		report = new Report(getLoginID());
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		dao = new TailoringProfitReportDao();

		popupHor = new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();

		closeBtn = new SNativeButton("X");

		detailsGrid = new SGridLayout();
		detailsGrid.setColumns(6);
		detailsGrid.setRows(10);
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

		if (settings.isSHOW_STOCK_IN_PROFIT_REPORT()) {
			detailsGrid.addComponent(new SHTMLLabel(null,
					"<b style='font-size:14px;'>"
							+ getPropertyName("stock_value") + " :</b>"), 0, 8);
			detailsGrid.addComponent(stockBal, 1, 8);
			detailsGrid.addComponent(stockDetails, 2, 8);
		}
		detailsGrid.addComponent(new SHTMLLabel(null,
				"<b style='font-size:14px;'>"
						+ getPropertyName("net_profit_loss") + " :</b>"), 0, 9);
		detailsGrid.addComponent(netProfit, 1, 9);

		SPanel panel = new SPanel();
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
		SHorizontalLayout btnLay = new SHorizontalLayout(true, showButton,
				generateButton, generateDetailedReport);
		mainFormLayout.addComponent(btnLay);
		mainFormLayout.setComponentAlignment(btnLay, Alignment.MIDDLE_CENTER);

		review.addComponent(new SHorizontalLayout(mainFormLayout, popupHor,
				popupContainer, detailsGrid), "left: 0px; right: 0px; z-index:-1;");
		panel.setContent(review);
		
		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
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
				showDetails("Sale");
			}
		});

		salesReturnDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Sales Return");
			}
		});
		
		salesReturnItemDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Sales Return Item");
			}
		});

		grossPurchaseDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Purchase");
			}
		});
		
		purchaseReturnDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Purchase Return");
			}
		});
		
		purchaseReturnItemDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Purchase Return Item");
			}
		});
		transportationDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Transportation");
			}
		});
		
		expenseDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Expenses");
			}
		});
		
		stockDetails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showDetails("Stock");
			}
		});

		closeBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				pop.setPopupVisible(false);
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
				if (table.getValue() != null) {
					pop.setPopupVisible(false);
					openOption((Long) table.getValue());
				}

			}
		};

		closeListener = new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				pop.setPopupVisible(true);
			}
		};

		showButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					HashMap<String, Object> map = new HashMap<String, Object>();

					List reportList = new ArrayList();

					try {

						double purchase = roundNumber(dao.getTotalPurchaseAmount((Long) officeComboField
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
						stockBal.setValue("<b style='font-size:14px;'>"
								+ of_stock);
						netProfit.setValue("<b style='font-size:14px;'>"
								+ roundNumber((sales - sales_return)
										- (purchase - purchase_return) - transp
										- expendetures));

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		valChangeLis = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (table.getValue() != null)
					showSubDetails((Long) table.getValue());
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
			@SuppressWarnings("unchecked")
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

						report.setJrxmlFileName("ProfitDetailedReport");
						report.setReportFileName("Profit Details Report");
						report.setReportTitle("Profit Details Report");
						String subTitle = "From : "
								+ CommonUtil
										.formatDateToDDMMMYYYY(fromDateField
												.getValue())
								+ "\t To : "
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

		return panel;
	}

	@SuppressWarnings("unchecked")
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

			double of_stock = roundNumber(dao.getOfStockAmount(
					(Long) officeComboField.getValue(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue())));

			reportList.add(new ProfitReportBean(purchase,
					purchase_return, sales, sales_return,
					transportation, expendetures,
					roundNumber((sales - sales_return)
							- (purchase - purchase_return)
							- transportation - expendetures),
					of_stock));
			
			map.put("SHOWSTOCK", settings.isSHOW_STOCK_IN_PROFIT_REPORT());

			report.setJrxmlFileName("Profit_Report");
			report.setReportFileName("Profit_Report");
			report.setReportTitle("Profit Report");
			String subTitle = "From : "
					+ CommonUtil
							.formatDateToDDMMMYYYY(fromDateField
									.getValue())
					+ "\t To : "
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

	public void showDetails(String type) {
		try {

			current_details = type;

			popupHor.removeAllComponents();

			if (type.equals("Sale")) {

				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Customer Name", String.class, null,
						getPropertyName("customer_name"), null, Align.LEFT);
				table.addContainerProperty("Amount", Double.class, null,
						getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Customer Name", "Amount", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
					table.addItem(
							new Object[] { objIn.getDate(), objIn.getNo(),
									objIn.getClient_name(), objIn.getAmount(),
									objIn.getRef_no(), objIn.getComments() },
							objIn.getId());
					ttl += objIn.getAmount();
				}
				table.setFooterVisible(true);
				table.setSelectable(true);
				table.setColumnFooter("Customer Name", getPropertyName("total"));
				table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Customer Name", (float) 1);
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
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);

			} 
			else if (type.equals("Sales Return")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Customer Name", String.class, null,
						getPropertyName("customer_name"), null, Align.LEFT);
				table.addContainerProperty("Amount", Double.class, null,
						getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Customer Name", "Amount", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
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
				table.setColumnFooter("Customer Name", getPropertyName("total"));
				table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Customer Name", (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Sales Return Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			}
			else if (type.equals("Sales Return Item")) {

				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Customer Name", String.class, null,
						getPropertyName("customer_name"), null, Align.LEFT);
				table.addContainerProperty("Item", String.class, null,
						getPropertyName("item"), null, Align.LEFT);
				table.addContainerProperty("Good Stock", Double.class, null,
						getPropertyName("good_stock"), null, Align.RIGHT);
				table.addContainerProperty("Waste Qty", Double.class, null,
						getPropertyName("waste_qty"), null, Align.RIGHT);
				table.addContainerProperty("Return Qty", Double.class, null,
						getPropertyName("return_qty"), null, Align.RIGHT);
				table.addContainerProperty("Stock Qty", Double.class, null,
						getPropertyName("stock_qty"), null, Align.RIGHT);
				table.addContainerProperty("Unit", String.class, null,
						getPropertyName("unit"), null, Align.RIGHT);
				table.addContainerProperty("Price", Double.class, null,
						getPropertyName("price"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Customer Name", "Item","Good Stock", "Waste Qty", "Return Qty",
						"Stock Qty", "Unit", "Price", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
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
					ttl += objIn.getAmount();
				}
				table.setFooterVisible(true);
				// table.setColumnFooter("Customer Name", "Total :");
				// table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Customer Name", (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Item-wise Sales Return Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);

			} else if (type.equals("Purchase")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Supplier Name", String.class, null,
						getPropertyName("supplier_name"), null, Align.LEFT);
				table.addContainerProperty("Amount", Double.class, null,
						getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Supplier Name", "Amount", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
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
				table.setColumnFooter("Supplier Name", getPropertyName("total"));
				table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Supplier Name", (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Purchase Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			} else if (type.equals("Purchase Return")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Supplier Name", String.class, null,
						getPropertyName("supplier_name"), null, Align.LEFT);
				table.addContainerProperty("Amount", Double.class, null,
						getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Supplier Name", "Amount", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
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
				table.setColumnFooter("Supplier Name", getPropertyName("total"));
				table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Supplier Name", (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Purchase Return Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			} else if (type.equals("Purchase Return Item")) {

				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Supplier Name", String.class, null,
						getPropertyName("supplier_name"), null, Align.LEFT);
				table.addContainerProperty("Item", String.class, null,
						getPropertyName("item"), null, Align.LEFT);
				table.addContainerProperty("Return Qty", Double.class, null,
						getPropertyName("return_qty"), null, Align.RIGHT);
				table.addContainerProperty("Unit", String.class, null,
						getPropertyName("unit"), null, Align.RIGHT);
				table.addContainerProperty("Price", Double.class, null,
						getPropertyName("price"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Supplier Name", "Item", "Return Qty", "Unit", "Price",
						"Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
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
				// table.setColumnFooter("Supplier Name", "Total :");
				// table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Supplier Name", (float) 1);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Item Wise Purchase Return Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);

			} else if (type.equals("Transportation")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Account Name", String.class, null,
						getPropertyName("account_name"), null, Align.LEFT);
				table.addContainerProperty("Amount", Double.class, null,
						getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Account Name", "Amount", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
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
				table.setColumnFooter("Account Name", "Total :");
				table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Account Name", (float) 1);
				table.setColumnExpandRatio("Comments", (float) 1.5);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Transportation Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			} else if (type.equals("Expenses")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Date", Date.class, null,
						getPropertyName("date"), null, Align.CENTER);
				table.addContainerProperty("Number", String.class, null,
						getPropertyName("number"), null, Align.LEFT);
				table.addContainerProperty("Account Name", String.class, null,
						getPropertyName("account_name"), null, Align.LEFT);
				table.addContainerProperty("Amount", Double.class, null,
						getPropertyName("amount"), null, Align.RIGHT);
				table.addContainerProperty("Ref. No.", String.class, null,
						getPropertyName("ref_no"), null, Align.LEFT);
				table.addContainerProperty("Comments", String.class, null,
						getPropertyName("comments"), null, Align.LEFT);

				table.setVisibleColumns(new String[] { "Date", "Number",
						"Account Name", "Amount", "Ref. No.", "Comments" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
				double ttl = 0;
				Iterator it = list.iterator();
				int i = 0;
				AcctReportMainBean objIn;
				while (it.hasNext()) {
					objIn = (AcctReportMainBean) it.next();
					i++;
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
				table.setColumnFooter("Account Name", getPropertyName("total"));
				table.setColumnFooter("Amount", asString(roundNumber(ttl)));

				table.setColumnExpandRatio("Date", (float) 0.5);
				table.setColumnExpandRatio("Account Name", (float) 1);
				table.setColumnExpandRatio("Comments", (float) 1.5);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Expense Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
				
			}else if (type.equals("Stock")) {
				table = new STable();
				table.setWidth("1000");
				table.setHeight("400");

				table.addContainerProperty("Item", String.class, null,
						getPropertyName("item"), null, Align.LEFT);
				table.addContainerProperty("Purchase Qty", Double.class, null,
						getPropertyName("purchase_qty"), null, Align.LEFT);
				table.addContainerProperty("Balance Qty", Double.class, null,
						getPropertyName("balance_qty"), null, Align.LEFT);
				table.addContainerProperty("Unit", String.class, null,
						getPropertyName("unit"), null, Align.LEFT);
				table.addContainerProperty("Rate", Double.class, null,
						getPropertyName("rate"), null, Align.RIGHT);

				table.setVisibleColumns(new String[] { "Item", "Purchase Qty",
						"Balance Qty", "Unit","Rate" });

				List list = dao.getDetailsFromType((Long) officeComboField
						.getValue(), CommonUtil
						.getSQLDateFromUtilDate(fromDateField.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), type);
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
//				table.setSelectable(true);
//				table.addValueChangeListener(valChangeLis);
//				table.addActionHandler(action1);
				
				table.setColumnFooter("Item", getPropertyName("total"));
//				table.setColumnFooter("Balance Qty", asString(roundNumber(qty)));
//				table.setColumnFooter("Rate", asString(roundNumber(total)));

				table.setColumnExpandRatio("Rate", (float) 1);
				table.setColumnExpandRatio("Unit", (float) 1);
				table.setColumnExpandRatio("Balance Qty", (float) 1);
				table.setColumnExpandRatio("Purchase Qty", (float) 1);
				table.setColumnExpandRatio("Item", (float) 3);

				pop = new SPopupView(
						"",
						new SVerticalLayout(
								true,
								new SHorizontalLayout(
										new SHTMLLabel(
												null,
												"<h2><u style='margin-left: 40px;'>Stock Details :",
												970), closeBtn), table));

				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
				pop.setHideOnMouseOut(false);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void showSubDetails(long id) {
		try {
			
			popupContainer.removeAllComponents();

			if (current_details.equals("Sale")) {

				TailoringSalesModel objModel = new TailoringSalesDao().getSale(id);
				SFormLayout form = new SFormLayout();
				form.addComponent(new SHTMLLabel(null, "<h2><u>Sale</u></h2>"));
				form.addComponent(new SLabel(getPropertyName("sales_no"),
						objModel.getSales_number() + ""));
				form.addComponent(new SLabel(getPropertyName("customer"),
						objModel.getCustomer().getName()));
				form.addComponent(new SLabel(getPropertyName("date"),
						CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
				form.addComponent(new SLabel(
						getPropertyName("max_credit_period"), objModel
								.getCredit_period() + ""));

				if (isShippingChargeEnable())
					form.addComponent(new SLabel(
							getPropertyName("shipping_charge"), objModel
									.getShipping_charge() + ""));

				form.addComponent(new SLabel(getPropertyName("net_amount"),
						objModel.getAmount() + ""));
				form.addComponent(new SLabel(getPropertyName("paid_amount"),
						objModel.getPayment_amount() + ""));

				SGridLayout grid = new SGridLayout(
						getPropertyName("item_details"));
				grid.setColumns(12);
				grid.setRows(objModel.getTailoring_inventory_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, "Item"), 1, 0);
				grid.addComponent(new SLabel(null, "Qty"), 2, 0);
				grid.addComponent(new SLabel(null, "Unit"), 3, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 4, 0);
				grid.addComponent(new SLabel(null, "Discount"), 5, 0);
				grid.addComponent(new SLabel(null, "Amount"), 6, 0);
				grid.setSpacing(true);
				int i = 1;
				TailoringSalesInventoryDetailsModel invObj;
				Iterator itmItr = objModel.getTailoring_inventory_details_list()
						.iterator();
				while (itmItr.hasNext()) {
					invObj = (TailoringSalesInventoryDetailsModel) itmItr.next();

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
							new SLabel(null, (invObj.getUnit_price()
									* invObj.getQunatity()
									+ invObj
										.getTax_amount()) + ""), 6, i);
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
				grid.addComponent(new SLabel(null, "Item"), 1, 0);
				grid.addComponent(new SLabel(null, "Good Stock"), 2, 0);
				grid.addComponent(new SLabel(null, "Unit"), 3, 0);
				grid.addComponent(new SLabel(null, "Stock Qty"), 4, 0);
				grid.addComponent(new SLabel(null, "Purch. Rtn Qty"), 5, 0);
				grid.addComponent(new SLabel(null, "Waste Qty"), 6, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 7, 0);
				grid.addComponent(new SLabel(null, "Discount"), 8, 0);
				grid.addComponent(new SLabel(null, "Amount"), 9, 0);
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
//				grid.setRows(objModel.getInventory_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, "Item"), 1, 0);
				grid.addComponent(new SLabel(null, "Qty"), 2, 0);
				grid.addComponent(new SLabel(null, "Unit"), 3, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 4, 0);
				grid.addComponent(new SLabel(null, "Discount"), 5, 0);
				grid.addComponent(new SLabel(null, "Amount"), 6, 0);
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
				grid.addComponent(new SLabel(null, "Item"), 1, 0);
				grid.addComponent(new SLabel(null, "Qty"), 2, 0);
				grid.addComponent(new SLabel(null, "Unit"), 3, 0);
				grid.addComponent(new SLabel(null, "Unit Price"), 4, 0);
				grid.addComponent(new SLabel(null, "Discount"), 5, 0);
				grid.addComponent(new SLabel(null, "Amount"), 6, 0);
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
				TransportationPaymentModel objModel = new TransportationPaymentDao()
						.getTransportationPaymentModel(id);

				if (objModel != null) {

					SFormLayout form = new SFormLayout();
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

					form.setStyleName("grid_max_limit");

					popupContainer.removeAllComponents();
					subPop = new SPopupView("", form);
					popupContainer.addComponent(subPop);
					subPop.setPopupVisible(true);
					subPop.setHideOnMouseOut(false);

				}
			} else if (current_details.equals("Expenses")) {
				PaymentDepositModel objModel = new ExpendetureTransactionDao()
						.getExpendetureTransaction(id);

				SFormLayout form = new SFormLayout();
				form.addComponent(new SHTMLLabel(null,
						"<h2><u>Expenditure Transaction</u></h2>"));

				form.addComponent(new SLabel(getPropertyName("bill_no"),
						objModel.getBill_no() + ""));

				form.addComponent(new SLabel(getPropertyName("date"),
						CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
				if (objModel.getCash_or_check() == 2)
					form.addComponent(new SLabel(
							getPropertyName("cash_cheque"), "Cheque"));
				else
					form.addComponent(new SLabel(
							getPropertyName("cash_cheque"), "Cash"));

				form.addComponent(new SLabel(getPropertyName("ref_no"),
						objModel.getRef_no()));

				form.addComponent(new SLabel(getPropertyName("memo"), objModel
						.getMemo()));

				SGridLayout grid = new SGridLayout(
						getPropertyName("transaction_details"));
				grid.setColumns(5);
				grid.setRows(objModel.getTransaction()
						.getTransaction_details_list().size() + 3);

				grid.addComponent(new SLabel(null, "#"), 0, 0);
				grid.addComponent(new SLabel(null, "From Account"), 1, 0);
				grid.addComponent(new SLabel(null, "To Account"), 2, 0);
				grid.addComponent(new SLabel(null, "Amount"), 3, 0);
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
			// TODO: handle exception
		}
	}

	public void openOption(long id) {
		try {

			if (current_details.equals("Sale")) {
				TailoringSalesUI sale = new TailoringSalesUI();
				sale.setCaption("Sales");
				sale.loadSale(id);
				sale.center();
				getUI().getCurrent().addWindow(sale);
				sale.addCloseListener(closeListener);
				sale.setModal(true);
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
				TransportationPaymentsUI trans = new TransportationPaymentsUI();
				trans.setCaption("Transportation Payment");
				trans.getPaymentIdComboField().setValue(id);
				trans.center();
				getUI().getCurrent().addWindow(trans);
				trans.addCloseListener(closeListener);
				trans.setModal(true);
			} else if (current_details.equals("Expenses")) {
				ExpendetureTransactionUI expend = new ExpendetureTransactionUI();
				expend.setCaption("Expenditure Transactions");
				expend.loadData(id);
				expend.center();
				getUI().getCurrent().addWindow(expend);
				expend.addCloseListener(closeListener);
				expend.setModal(true);
			}

			subPop.setPopupVisible(false);
			pop.setPopupVisible(false);

		} catch (Exception e) {
			// TODO: handle exception
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
