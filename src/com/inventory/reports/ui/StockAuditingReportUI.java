package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.StockAuditingReportBean;
import com.inventory.reports.dao.StockAuditingReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 18, 2014
 */
public class StockAuditingReportUI extends SparkLogic {

	private static final long serialVersionUID = 3255729707774337087L;

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField salesNoComboField;
	private SComboField supplierComboField;
	private SComboField purchaseNoComboField;
	private SComboField itemComboField;
	private SReportChoiceField reportChoiceField;
	SHorizontalLayout popupContainer;
	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	private SCollectionContainer container;
	SRadioButton filterTypeRadio;

	LedgerDao ledDao;
	ItemDao itemDao;

	private Report report;

	SalesDao salDao;
	PurchaseDao purchDao;
	StockAuditingReportDao dao;

	static String TBC_PURCH_ID = "Purchase ID";
	static String TBC_SALE_ID = "Sales ID";
	static String TBC_PURCHASE_NO = "Purchase No";
	static String TBC_SALES_NO = "Sales No";
	static String TBC_ITEM = "Item";
	static String TBC_PURCHASE_DATE = "Purchase Date";
	static String TBC_SALES_DATE = "Sales Date";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_PURCH_QTY = "Purchase Qty";
	static String TBC_SALES_QTY = "Sales Qty";
	static String TBC_PURCH_UNIT_PRICE = "Purchase Price";
	static String TBC_SALES_UNIT_PRICE = "Sales Price";
	static String TBC_PROFIT = "Profit";

	private STable table;

	private String[] allColumns;
	private String[] visibleColumns;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		ledDao = new LedgerDao();
		salDao = new SalesDao();
		purchDao = new PurchaseDao();
		dao = new StockAuditingReportDao();
		itemDao = new ItemDao();

		report = new Report(getLoginID());

		setSize(1200, 500);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {

			allColumns = new String[] { TBC_ITEM, TBC_PURCH_ID,
					TBC_PURCHASE_NO, TBC_PURCHASE_DATE, TBC_SUPPLIER,
					TBC_PURCH_QTY, TBC_PURCH_UNIT_PRICE, TBC_SALE_ID,
					TBC_SALES_NO, TBC_SALES_DATE, TBC_CUSTOMER, TBC_SALES_QTY,
					TBC_SALES_UNIT_PRICE, TBC_PROFIT };
			visibleColumns = new String[] { TBC_ITEM, TBC_PURCHASE_NO,
					TBC_PURCHASE_DATE, TBC_SUPPLIER, TBC_PURCH_QTY,
					TBC_PURCH_UNIT_PRICE, TBC_SALES_NO, TBC_SALES_DATE,
					TBC_CUSTOMER, TBC_SALES_QTY, TBC_SALES_UNIT_PRICE,
					TBC_PROFIT };

			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			officeComboField.setValue(getOfficeID());
			fromDateField = new SDateField(getPropertyName("from_date"));
			fromDateField.setValue(getMonthStartDate());
			toDateField = new SDateField(getPropertyName("to_date"));
			toDateField.setValue(getWorkingDate());
			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);

			itemComboField = new SComboField(getPropertyName("item"), 200);
			itemComboField
					.setInputPrompt(getPropertyName("all"));
			mainFormLayout.addComponent(itemComboField);
			reloadItemCombo(getOfficeID());

			List<Object> supplierList = ledDao.getAllSuppliers(getOfficeID());
			LedgerModel suppModel = new LedgerModel();
			suppModel.setId(0);
			suppModel.setName("---------------------ALL-------------------");
			if (supplierList == null) {
				supplierList = new ArrayList<Object>();
			}
			supplierList.add(0, suppModel);
			supplierComboField = new SComboField(getPropertyName("supplier"),
					200, supplierList, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(supplierComboField);

			purchaseNoComboField = new SComboField(
					getPropertyName("purchase_bill_no"), 200, null, "id",
					"comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(purchaseNoComboField);

			List customerList = ledDao.getAllCustomers(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (customerList == null) {
				customerList = new ArrayList();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(getPropertyName("customer"),
					200, customerList, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_bill_no"), 200, null, "id",
					"comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(salesNoComboField);

			filterTypeRadio = new SRadioButton(getPropertyName("type"), 200,
					Arrays.asList(new KeyValue(1, getPropertyName("sale_based")), new KeyValue(
							2, getPropertyName("purchase_based"))), "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(2);

			table = new STable(null, 850, 400);

			table.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);
			table.addContainerProperty(TBC_PURCH_ID, Long.class, null,
					TBC_PURCH_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_PURCHASE_NO, String.class, null,
					getPropertyName("purchase_no"), null, Align.LEFT);
			table.addContainerProperty(TBC_PURCHASE_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_SUPPLIER, String.class, null,
					getPropertyName("supplier"), null, Align.LEFT);
			table.addContainerProperty(TBC_PURCH_QTY, Double.class, null,
					getPropertyName("purchase_qty"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PURCH_UNIT_PRICE, Double.class,
					null, getPropertyName("purchase_price"), null, Align.RIGHT);

			table.addContainerProperty(TBC_SALE_ID, Long.class, null,
					TBC_SALE_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_SALES_NO, String.class, null,
					getPropertyName("sales_no"), null, Align.LEFT);
			table.addContainerProperty(TBC_SALES_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_SALES_QTY, Double.class, null,
					getPropertyName("sales_qty"), null, Align.RIGHT);
			table.addContainerProperty(TBC_SALES_UNIT_PRICE, Double.class,
					null, getPropertyName("price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PROFIT, Double.class, null,
					getPropertyName("profit"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_ITEM, (float) 1.5);
			table.setColumnExpandRatio(TBC_PURCHASE_NO, (float) 0.8);
			table.setColumnExpandRatio(TBC_SALES_NO, (float) 0.8);
			table.setColumnExpandRatio(TBC_PURCHASE_DATE, (float) 1.1);
			table.setColumnExpandRatio(TBC_SALES_DATE, (float) 1.1);
			table.setColumnExpandRatio(TBC_SUPPLIER, 1.4f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1.4f);
			table.setColumnExpandRatio(TBC_PURCH_QTY, 0.35f);
			table.setColumnExpandRatio(TBC_SALES_QTY, 0.35f);
			table.setColumnExpandRatio(TBC_PURCH_UNIT_PRICE, 0.6f);
			table.setColumnExpandRatio(TBC_SALES_UNIT_PRICE, 0.8f);
			table.setColumnExpandRatio(TBC_PROFIT, 1f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_PURCHASE_NO, getPropertyName("total"));
			table.setColumnFooter(TBC_PROFIT, "0.0");

			mainFormLayout.addComponent(filterTypeRadio);

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

			SHorizontalLayout mainLay = new SHorizontalLayout();
			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);
			

			mainPanel.setContent(mainLay);

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							loadBillNo((Long) customerComboField.getValue(),
									toLong(officeComboField.getValue()
											.toString()));

							table.removeAllItems();
							table.setColumnFooter(TBC_PROFIT, "0.0");

						}
					});
			supplierComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							loadPurchaseBillNo((Long) supplierComboField
									.getValue(), toLong(officeComboField
									.getValue().toString()));

							table.removeAllItems();
							table.setColumnFooter(TBC_PROFIT, "0.0");

						}
					});

			fromDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo((Long) customerComboField.getValue(),
							toLong(officeComboField.getValue().toString()));
					loadPurchaseBillNo((Long) supplierComboField.getValue(),
							toLong(officeComboField.getValue().toString()));
					table.removeAllItems();
					table.setColumnFooter(TBC_PROFIT, "0.0");
				}
			});

			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo((Long) customerComboField.getValue(),
							toLong(officeComboField.getValue().toString()));
					loadPurchaseBillNo((Long) supplierComboField.getValue(),
							toLong(officeComboField.getValue().toString()));
					table.removeAllItems();
					table.setColumnFooter(TBC_PROFIT, "0.0");
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {

					reloadItemCombo(toLong(officeComboField.getValue()
							.toString()));
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
					loadSupplierCombo(toLong(officeComboField.getValue()
							.toString()));

					loadBillNo((Long) customerComboField.getValue(),
							toLong(officeComboField.getValue().toString()));
					loadPurchaseBillNo((Long) supplierComboField.getValue(),
							toLong(officeComboField.getValue().toString()));
					table.removeAllItems();
					table.setColumnFooter(TBC_PROFIT, "0.0");
				}
			});
			filterTypeRadio.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_PROFIT, "0.0");
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						long salesNo = 0;
						long custId = 0;
						long purchaseNo = 0;
						long supplierId = 0;
						long itemId = 0;

						if (salesNoComboField.getValue() != null
								&& !salesNoComboField.getValue().equals("")) {
							salesNo = toLong(salesNoComboField.getValue()
									.toString());
						}
						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						if (purchaseNoComboField.getValue() != null
								&& !purchaseNoComboField.getValue().equals("")) {
							purchaseNo = toLong(purchaseNoComboField.getValue()
									.toString());
						}
						if (supplierComboField.getValue() != null
								&& !supplierComboField.getValue().equals("")) {
							supplierId = toLong(supplierComboField.getValue()
									.toString());
						}
						if (itemComboField.getValue() != null
								&& !itemComboField.getValue().equals("")) {
							itemId = toLong(itemComboField.getValue()
									.toString());
						}

						List list = null;

						if (toInt(filterTypeRadio.getValue().toString()) == 1) {
							list = dao.getStockAuditingReport(
									itemId,
									custId,
									salesNo,
									supplierId,
									purchaseNo,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							report.setJrxmlFileName("StockAuditingReport");
						} else {
							list = dao.getPurchaseStockAuditingReport(
									itemId,
									custId,
									salesNo,
									supplierId,
									purchaseNo,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							report.setJrxmlFileName("StockAuditingReportPurchase");
						}

						if (list != null && list.size() > 0) {

							Collections.sort(list,
									new Comparator<StockAuditingReportBean>() {
										@Override
										public int compare(
												final StockAuditingReportBean object1,
												final StockAuditingReportBean object2) {
											return object2
													.getPurchaseNo()
													.compareTo(
															object1.getPurchaseNo());
										}
									});

							report.setReportFileName("StockAuditingReport");
							report.setReportTitle("Stock Auditing Report");
							String subHeader = "\n From : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t To : "
									+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue());

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(list, null);

							list.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						table.removeAllItems();

						long salesNo = 0;
						long custId = 0;
						long purchaseNo = 0;
						long supplierId = 0;
						long itemId = 0;

						if (salesNoComboField.getValue() != null
								&& !salesNoComboField.getValue().equals("")) {
							salesNo = toLong(salesNoComboField.getValue()
									.toString());
						}
						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						if (purchaseNoComboField.getValue() != null
								&& !purchaseNoComboField.getValue().equals("")) {
							purchaseNo = toLong(purchaseNoComboField.getValue()
									.toString());
						}
						if (supplierComboField.getValue() != null
								&& !supplierComboField.getValue().equals("")) {
							supplierId = toLong(supplierComboField.getValue()
									.toString());
						}
						if (itemComboField.getValue() != null
								&& !itemComboField.getValue().equals("")) {
							itemId = toLong(itemComboField.getValue()
									.toString());
						}

						List list = null;

						if (toInt(filterTypeRadio.getValue().toString()) == 1) {
							list = dao.getStockAuditingReport(
									itemId,
									custId,
									salesNo,
									supplierId,
									purchaseNo,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							report.setJrxmlFileName("StockAuditingReport");
						} else {
							list = dao.getPurchaseStockAuditingReport(
									itemId,
									custId,
									salesNo,
									supplierId,
									purchaseNo,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							report.setJrxmlFileName("StockAuditingReportPurchase");
						}

						if (list != null && list.size() > 0) {

							Collections.sort(list,
									new Comparator<StockAuditingReportBean>() {
										@Override
										public int compare(
												final StockAuditingReportBean object1,
												final StockAuditingReportBean object2) {
											return object2
													.getPurchaseNo()
													.compareTo(
															object1.getPurchaseNo());
										}
									});

							StockAuditingReportBean stkBean;
							Iterator iter = list.iterator();
							Object[] row;
							int i = 0;
							double profit = 0;
							table.setVisibleColumns(allColumns);
							while (iter.hasNext()) {
								i++;
								stkBean = (StockAuditingReportBean) iter.next();
								row = new Object[] { stkBean.getItem(),
										stkBean.getPurchaseId(),
										stkBean.getPurchaseNo(),
										stkBean.getPurchaseDate(),
										stkBean.getSupplier(),
										stkBean.getPurchaseQuantity(),
										stkBean.getPurchaseRate(),
										stkBean.getSaleId(),
										stkBean.getSalesNo(),
										stkBean.getSalesDate(),
										stkBean.getCustomer(),
										stkBean.getSalesQuantity(),
										stkBean.getSalesRate(),
										stkBean.getProfit() };
								table.addItem(row, i);
								profit += stkBean.getProfit();

							}
							table.setVisibleColumns(visibleColumns);
							table.setColumnFooter(TBC_PROFIT,
									roundNumber(profit) + "");
							list.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			final Action actionSales = new Action("Sales");
			final Action actionPurchase = new Action("Purchase");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionPurchase, actionSales };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());

						if (action.equals(actionSales)) {
							SalesNewUI purchase = new SalesNewUI();
							purchase.setCaption("Sales");
							purchase.getSalesNumberList().setValue(
									(Long) item.getItemProperty(TBC_SALE_ID)
											.getValue());
							purchase.center();
							getUI().getCurrent().addWindow(purchase);
							// purchase.addCloseListener(closeListener);

						} else {
							PurchaseUI retrn = new PurchaseUI();
							retrn.setCaption("Purchase");
							retrn.getPurchaseNumberList().setValue(
									(Long) item.getItemProperty(TBC_PURCH_ID)
											.getValue());
							retrn.center();
							getUI().getCurrent().addWindow(retrn);
							// retrn.addCloseListener(closeListener);
						}

					}
				}

			});

			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>Stock Auditing</u></h2>"));
							form.addComponent(new SLabel("Purchase No ",item.getItemProperty(TBC_PURCHASE_NO).getValue().toString()));
							form.addComponent(new SLabel("Item",item.getItemProperty(TBC_ITEM).getValue().toString()));
							form.addComponent(new SLabel("Supplier",item.getItemProperty(TBC_SUPPLIER).getValue().toString()));
							form.addComponent(new SLabel("Purchased Qty",item.getItemProperty(TBC_PURCH_QTY).getValue().toString()));
							form.addComponent(new SLabel("Purchase Price",item.getItemProperty(TBC_PURCH_UNIT_PRICE).getValue().toString()));
							form.addComponent(new SLabel("Customer",item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel("Sales Qty",item.getItemProperty(TBC_SALES_QTY).getValue().toString()));
							form.addComponent(new SLabel("Sales Price",item.getItemProperty(TBC_SALES_UNIT_PRICE).getValue().toString()));
							form.addComponent(new SLabel("Profit",item.getItemProperty(TBC_PROFIT).getValue().toString()));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			officeComboField.setValue(getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	private void loadBillNo(Long customerId, long officeId) {
		List<Object> salesList = null;
		try {

			String condition1 = " and active=true";

			if (customerId != null && customerId != 0) {
				salesList = salDao.getAllSalesNumbersForCustomer(officeId,
						customerId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), condition1);
			} else {
				salesList = salDao.getAllSalesNumbersByDate(officeId,
						CommonUtil.getSQLDateFromUtilDate(fromDateField
								.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), condition1);
			}

			SalesModel salesModel = new SalesModel();
			salesModel.setId(0);
			salesModel
					.setComments("---------------------ALL-------------------");
			if (salesList == null) {
				salesList = new ArrayList<Object>();
			}
			salesList.add(0, salesModel);
			container = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(container);
			salesNoComboField.setItemCaptionPropertyId("comments");
			salesNoComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			if (officeId != 0) {
				custList = ledDao.getAllCustomers(officeId);
			} else {
				custList = ledDao.getAllCustomers();
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			SCollectionContainer custContainer = SCollectionContainer.setList(
					custList, "id");
			customerComboField.setContainerDataSource(custContainer);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadPurchaseBillNo(Long supplierId, long officeId) {
		List purchaseBillList = null;
		try {

			String condition1 = " and active=true";

			if (supplierId != null && supplierId != 0) {
				purchaseBillList = purchDao.getAllPurchaseNumbersForSupplier(
						officeId, supplierId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), condition1);
			} else {
				purchaseBillList = purchDao.getAllPurchaseNumbersFromDate(
						officeId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), condition1);
			}

			PurchaseModel purchaseModel = new PurchaseModel();
			purchaseModel.setId(0);
			purchaseModel
					.setComments("---------------------ALL-------------------");
			if (purchaseBillList == null) {
				purchaseBillList = new ArrayList<Object>();
			}
			purchaseBillList.add(0, purchaseModel);
			container = SCollectionContainer.setList(purchaseBillList, "id");
			purchaseNoComboField.setContainerDataSource(container);
			purchaseNoComboField.setItemCaptionPropertyId("comments");
			purchaseNoComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void loadSupplierCombo(long officeId) {
		List<Object> suppList = null;
		try {
			if (officeId != 0) {
				suppList = ledDao.getAllSuppliers(officeId);
			} else {
				suppList = ledDao.getAllSuppliersFromOrgId(getOrganizationID());
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (suppList == null) {
				suppList = new ArrayList<Object>();
			}
			suppList.add(0, ledgerModel);
			SCollectionContainer suppContainer = SCollectionContainer.setList(
					suppList, "id");
			supplierComboField.setContainerDataSource(suppContainer);
			supplierComboField.setItemCaptionPropertyId("name");
			supplierComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reloadItemCombo(long officeId) {
		try {

			List itemList = itemDao
					.getAllActiveItemsWithAppendingItemCode(officeId);

			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName("-----------------ALL------------------");
			if (itemList == null)
				itemList = new ArrayList();

			itemList.add(0, itemModel);

			itemComboField
					.setInputPrompt("-----------------ALL------------------");

			SCollectionContainer itemContainer = SCollectionContainer.setList(
					itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(0);

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

}
