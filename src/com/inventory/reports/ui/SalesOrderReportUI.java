package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesOrderDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.ui.SalesOrderUI;
import com.vaadin.data.Item;
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
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         May 2, 2014
 */
public class SalesOrderReportUI extends SparkLogic {

	private static final long serialVersionUID = -3884017067913406712L;

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField salesNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private UserManagementDao userDao;

	LedgerDao ledDao;

	private Report report;

	SalesOrderDao salDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALE_NO = "Sale Order No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";
	SHorizontalLayout popupContainer;
	private STable table;

	private Object[] allColumns;
	private Object[] visibleColumns;

	private HashMap<Long, String> currencyHashMap;

	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();
		salDao = new SalesOrderDao();

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_SALE_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleColumns = new String[] { TBC_SN, TBC_SALE_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		popupContainer = new SHorizontalLayout();
		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(1100, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		SHorizontalLayout mainLay = new SHorizontalLayout();

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {

			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			fromDateField = new SDateField(getPropertyName("from_date"));
			fromDateField.setValue(getMonthStartDate());
			fromDateField.setImmediate(true);
			toDateField = new SDateField(getPropertyName("to_date"));
			toDateField.setValue(getWorkingDate());
			toDateField.setImmediate(true);
			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);

			List<Object> customerList = ledDao.getAllCustomers(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(getPropertyName("customer"),
					200, customerList, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_order_no"), 200, null, "id",
					"order_no", false, getPropertyName("all"));
			mainFormLayout.addComponent(salesNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 670, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_SALE_NO, String.class, null,
					getPropertyName("sales_order_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, String.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_ITEMS, String.class, null,
					getPropertyName("items"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_SALE_NO, 1.2f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_ITEMS, 2f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);
			

			mainPanel.setContent(mainLay);

			loadBillNo((long) 0, getOfficeID());

			salesNoComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							table.removeAllItems();
							table.setColumnFooter(TBC_AMOUNT, "0.0");
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerComboField
										.getValue().toString());
							}
							loadBillNo(customerId, toLong(officeComboField
									.getValue().toString()));
						}
					});

			fromDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesOrderModel salesModel = null;
						SalesOrderDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";
						String salesPerson = "";

						List<Object> reportList = new ArrayList<Object>();
						List<Object> salesModelList = getSalesOrderReport();

						List<SalesOrderDetailsModel> detailsList;
						double amount;
						for (int i = 0; i < salesModelList.size(); i++) {
							noData = false;
							salesPerson = "";
							salesModel = (SalesOrderModel) salesModelList
									.get(i);
							detailsList = salesModel
									.getOrder_details_list();
							
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inventoryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}

							salesPerson = userDao.getUser(
									salesModel.getResponsible_employee())
									.getFirst_name();
							amount = salesModel.getAmount() / salesModel.getConversionRate();
							if(salesModel.getCurrencyId() == getCurrencyID()){
								reportBean = new SalesReportBean(salesModel
										.getDate().toString(), salesModel
										.getCustomer().getName(),
										String.valueOf(salesModel
												.getOrder_no()),
										salesModel.getOffice().getName(), items,
										salesModel.getAmount(), items);
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID()));
							} else {
								reportBean = new SalesReportBean(salesModel
										.getDate().toString(), salesModel
										.getCustomer().getName(),
										String.valueOf(salesModel
												.getOrder_no()),
										salesModel.getOffice().getName(), items,
										roundNumber(amount), items);
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID())
										+" ("+salesModel.getAmount()+" "+getCurrencyDescription(salesModel.getCurrencyId())+")");
							}
							
							reportList.add(reportBean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							
							report.setJrxmlFileName("SalesOrderReport");
							report.setReportFileName("SalesOrderReport");
//							report.setReportTitle("Sales Order Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_order_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("SALES_ORDER_NO_LABEL", getPropertyName("sales_order_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("CREATED_BY_LABEL", getPropertyName("items"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (salesNoComboField.getValue() != null
									&& !salesNoComboField.getValue().equals("")
									&& !salesNoComboField.getValue().toString()
											.equals("0")) {
								subHeader += getPropertyName("sales_no")+" : "
										+ salesNoComboField
												.getItemCaption(salesNoComboField
														.getValue());
							}

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
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);

							reportList.clear();
							salesModelList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					SalesOrderModel salesModel = null;
					SalesOrderDetailsModel inventoryDetailsModel = null;
					String items = "";
					Object[] row;

					List repList = getSalesOrderReport();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
					double amount;
					if (repList != null && repList.size() > 0) {
						List<SalesOrderDetailsModel> detailsList;
						for (int i = 0; i < repList.size(); i++) {
							salesModel = (SalesOrderModel) repList.get(i);
							detailsList = salesModel.getOrder_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inventoryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}
							amount = salesModel.getAmount() / salesModel.getConversionRate();
							if(salesModel.getCurrencyId() == getCurrencyID()){
								row = new Object[] {
										i + 1,
										salesModel.getId(),
										salesModel.getOrder_no() + "",
										CommonUtil
												.formatDateToCommonDateTimeFormat(salesModel
														.getDate()),
										salesModel.getCustomer().getName(),
										salesModel.getAmount()+" "+getCurrencyDescription(getCurrencyID()), items };
							} else {
								row = new Object[] {
										i + 1,
										salesModel.getId(),
										salesModel.getOrder_no() + "",
										CommonUtil
												.formatDateToCommonDateTimeFormat(salesModel
														.getDate()),
										salesModel.getCustomer().getName(),
										roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())
										+"("+salesModel.getAmount() +" "+getCurrencyDescription(salesModel.getCurrencyId())+")", items };
							}
							
							table.addItem(row, i + 1);
							ttl += amount;
						}
						table.setColumnFooter(TBC_AMOUNT,
								asString(roundNumber(ttl)));

					} else
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
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
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SalesOrderUI sales = new SalesOrderUI();
							sales.setCaption(getPropertyName("sales_order"));
							sales.getsalesOrderNumberList().setValue(
									(Long) item.getItemProperty(TBC_ID).getValue());
							sales.center();
							getUI().getCurrent().addWindow(sales);
							sales.addCloseListener(closeListener);
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
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesOrderModel mdl=new SalesOrderDao().getSalesOrderModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales_order")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_order_no"),mdl.getOrder_no()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),itm.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(mdl.getDate())));
							
							if(mdl.getCurrencyId() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"),mdl.getAmount() + " "+getCurrencyDescription(getCurrencyID())));
							}else{
								double amount = mdl.getAmount() / mdl.getConversionRate();
								form.addComponent(new SLabel(getPropertyName("net_amount"),
										roundNumber(amount) + " "+getCurrencyDescription(getCurrencyID())
										+" ("+mdl.getAmount()+" "+getCurrencyDescription(mdl.getCurrencyId())+")"));
							}
							
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(mdl.getOrder_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),	5, 0);
							grid.setSpacing(true);
							
							int i = 1;
							SalesOrderDetailsModel invObj;
							Iterator itr = mdl.getOrder_details_list().iterator();
							while(itr.hasNext()){
								invObj=(SalesOrderDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																	/*- invObj.getDiscount_amount() 
																	+ invObj.getTax_amount()*/)+ " "+getCurrencyDescription(getCurrencyID())), 5, i);
								i++;
							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), mdl.getComments()));
							form.setStyleName("grid_max_limit");
							popupContainer.removeAllComponents();
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

	protected List<Object> getSalesOrderReport() {
		long salesNo = 0;
		long custId = 0;

		if (salesNoComboField.getValue() != null
				&& !salesNoComboField.getValue().equals("")
				&& !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}

		if (customerComboField.getValue() != null
				&& !customerComboField.getValue().equals("")) {
			custId = toLong(customerComboField.getValue().toString());
		}

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesOrderDetails(salesNo, custId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return salesModelList;
	}
	private String getCurrencyDescription(long currencyId) {
		if(currencyHashMap == null){
			currencyHashMap = new HashMap<Long, String>();
			try {
				List list = new CurrencyManagementDao().getCurrencySymbol();
				Iterator<CurrencyModel> itr = list.iterator();
				while(itr.hasNext()){
					CurrencyModel model = itr.next();
					currencyHashMap.put(model.getId(), model.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return currencyHashMap.get(currencyId);
	}
	private void loadBillNo(long customerId, long officeId) {
		List salesList = null;
		try {

			if (customerId != 0) {
				salesList = salDao
						.getAllSalesOrdersForCustomer(officeId, customerId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));
			} else {
				salesList = salDao
						.getAllSalesOrderNumbersByDate(officeId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
								.getSQLDateFromUtilDate(toDateField.getValue()));
			}

			SalesOrderModel salesModel = new SalesOrderModel();
			salesModel.setId(0);
			salesModel.setOrder_no(getPropertyName("all"));
			if (salesList == null) {
				salesList = new ArrayList();
			}
			salesList.add(0, salesModel);
			container = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(container);
			salesNoComboField.setItemCaptionPropertyId("order_no");
			salesNoComboField.setValue(0);

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
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
			ledgerModel.setName(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			customerComboField.setContainerDataSource(custContainer);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(0);

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
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
