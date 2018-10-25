package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.finance.dao.PaymentModeDao;
import com.inventory.finance.model.PaymentModeModel;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
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
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
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
 *         Aug 12, 2013
 */
public class SalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

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
	private SButton generateConsolidatedButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private UserManagementDao userDao;

	LedgerDao ledDao;

	SRadioButton filterTypeRadio;
	SRadioButton statusRadioButton;

	private Report report;

	SalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";

	private STable table;
	

	private Object[] allColumns;
	private Object[] visibleColumns;
	SNativeSelect paymentModeSelect;
	

	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer;
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;

	private HashMap<Long, String> currencyHashMap;
	
	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_CUSTOMER, TBC_AMOUNT};
		visibleColumns = new String[] { TBC_SN, TBC_CUSTOMER, TBC_AMOUNT};
		
		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleSubColumns = new String[] { TBC_SN, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		
		ledDao = new LedgerDao();
		salDao = new SalesDao();
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

			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				sett = (SettingsValuePojo) session.getAttribute("settings");

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

			filterTypeRadio = new SRadioButton(getPropertyName("payment_type"),
					250, SConstants.filterTypeList, "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(0);

			statusRadioButton = new SRadioButton(getPropertyName("status"),
					250, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(
							1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);
			
			List payModeList=new PaymentModeDao().getAllPaymentModeList(getOfficeID());
			payModeList.add(0,new PaymentModeModel((long)0,"ALL"));
			paymentModeSelect = new SNativeSelect(getPropertyName("payment_mode"),
					200, payModeList, "id", "description");
			paymentModeSelect.setValue((long)0);

			mainFormLayout.addComponent(filterTypeRadio);
			mainFormLayout.addComponent(paymentModeSelect);

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);

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
					getPropertyName("sales_bill_no"), 200, null, "id",
					"comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(salesNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateConsolidatedButton = new SButton(getPropertyName("consolidated_report"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			
			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.addComponent(generateConsolidatedButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(generateConsolidatedButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 670, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("amount"), null, Align.RIGHT);
			
			table.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_ITEMS, 2f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));

			subTable = new STable(null, 750, 250);
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("sales_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("amount"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);
			
			subTable.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			subTable.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
			subTable.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			subTable.setColumnExpandRatio(TBC_AMOUNT, 1);
			subTable.setColumnExpandRatio(TBC_ITEMS, 2f);
			
			subTable.setVisibleColumns(visibleSubColumns);
			subTable.setSelectable(true);

			subTable.setFooterVisible(true);
			subTable.setColumnFooter(TBC_DATE, getPropertyName("total"));
			
			mainLay.addComponent(popHor);
			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);
			

			mainPanel.setContent(mainLay);

			
			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});
			
			
			salesNoComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			
			customerComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					customerId = 0;
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

			
			filterTypeRadio.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			statusRadioButton.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
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
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";
						String salesPerson = "";

						List<Object> reportList = new ArrayList<Object>();
						List<Object> salesModelList = getSalesReportList();

						List<SalesInventoryDetailsModel> detailsList;
						double amount;
						for (int i = 0; i < salesModelList.size(); i++) {
							noData = false;
							salesPerson = "";
							salesModel = (SalesModel) salesModelList.get(i);
							detailsList = salesModel
									.getInventory_details_list();
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

							salesPerson = userDao.getUser(salesModel.getResponsible_employee())
									.getFirst_name();
							
							amount = salesModel.getAmount() / salesModel.getConversionRate();
							if(salesModel.getCurrency_id() == getCurrencyID()){
								reportBean = new SalesReportBean(CommonUtil
										.getUtilDateFromSQLDate(
												salesModel.getDate()).toString(),
										salesModel.getCustomer().getName(), String
												.valueOf(salesModel
														.getSales_number()),
										salesModel.getOffice().getName(), items,
										salesModel.getAmount(), salesPerson,
										salesModel.getPayment_amount());
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID()));					
								reportBean.setRoom_no(getCurrencyDescription(getCurrencyID()));					
											
							} else {
								reportBean = new SalesReportBean(CommonUtil
										.getUtilDateFromSQLDate(
												salesModel.getDate()).toString(),
										salesModel.getCustomer().getName(), String
												.valueOf(salesModel
														.getSales_number()),
										salesModel.getOffice().getName(), items,
										roundNumber(amount), salesPerson,
										roundNumber(salesModel.getPayment_amount()/salesModel.getConversionRate()));
								
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID())+
										" ("+salesModel.getAmount()+" "+getCurrencyDescription(salesModel.getCurrency_id())+")");
								reportBean.setRoom_no(getCurrencyDescription(getCurrencyID())+
										" ("+roundNumber(salesModel.getPayment_amount())+" "+getCurrencyDescription(salesModel.getCurrency_id())+")");
							}

							reportList.add(reportBean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Sales_Report");
							report.setReportFileName("SalesReport");
//							report.setReportTitle("Sales Report");
							//Consolidated_3_Report
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("INVOICE_DATE_LABEL", getPropertyName("invoice_date"));
							map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("SALES_MAN_LABEL", getPropertyName("sales_man"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "+ customerComboField
												.getItemCaption(customerComboField.getValue()) + "\t";
							}
							if (salesNoComboField.getValue() != null
									&& !salesNoComboField.getValue().toString()
											.equals("0")) {
								subHeader += getPropertyName("sales_no")+" : "
										+ salesNoComboField
												.getItemCaption(salesNoComboField
														.getValue());
							}

							subHeader += "\n"+getPropertyName("from")+" : "
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
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					Object[] row;

					List repList = showSalesReportList();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
					double amount;
					if (repList != null && repList.size() > 0) {
						for (int i = 0; i < repList.size(); i++) {
							
							SalesReportBean bean=(SalesReportBean)repList.get(i);
							
							
							row = new Object[] {
									i + 1,
									bean.getCustomerId(),
									bean.getCustomer(),
									roundNumber(bean.getAmount())+" "+getCurrencyDescription(getCurrencyID())};
							table.addItem(row, i + 1);

							ttl += roundNumber(bean.getAmount());
							

						}
						table.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));

					} else
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
				}
			});
			
			
			generateConsolidatedButton.addClickListener(new ClickListener() {

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					List reportList = showSalesReportList();
					if (reportList.size() > 0) {
						for(Object obj : reportList){
							SalesReportBean bean = (SalesReportBean) obj;
							bean.setCurrency(getCurrencyDescription(getCurrencyID()));
						}
						HashMap<String, Object> map = new HashMap<String, Object>();
						report.setJrxmlFileName("Consolidated_3_Report");
						report.setReportFileName("ConsolidatedReport");
						//Consolidated_3_Report
						
						map.put("REPORT_TITLE_LABEL", getPropertyName("sales_report"));
						map.put("SL_NO_LABEL", getPropertyName("sl_no"));
						map.put("CUSTOMER_LABEL", getPropertyName("customer"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						map.put("TOTAL_LABEL", getPropertyName("total"));
						
						
						String subHeader = "";
						if (customerId != 0) {
							subHeader += getPropertyName("customer")+" : "+ customerComboField
											.getItemCaption(customerComboField.getValue()) + "\t";
						}
						if (salesNoComboField.getValue() != null && !salesNoComboField.getValue().toString().equals("0")) {
							subHeader += getPropertyName("sales_no")+" : "+ salesNoComboField.getItemCaption(salesNoComboField.getValue());
						}

						subHeader += "\n"+getPropertyName("from")+" : "+ CommonUtil.formatDateToDDMMYYYY(fromDateField.getValue())
								+ "\t "+getPropertyName("to")+" : "+ CommonUtil.formatDateToDDMMYYYY(toDateField.getValue());
						report.setReportSubTitle(subHeader);
						report.setIncludeHeader(true);
						report.setIncludeFooter(false);
						report.setReportType(toInt(reportChoiceField.getValue().toString()));
						report.setOfficeName(officeComboField.getItemCaption(officeComboField.getValue()));
						report.createReport(reportList, map);
						reportList.clear();
					} 
					else
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
				}
			});
			

			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			
			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
			subTable.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (subTable.getValue() != null) {
							Item item = subTable.getItem(subTable.getValue());
							SalesUI sales = new SalesUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							sales.center();
							popUp.setPopupVisible(false);
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
			
			
			subTable.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesModel sale=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
//							form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
//							if (isShippingChargeEnable())
//								form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
							if(sale.getCurrency_id() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + " "+getCurrencyDescription(getCurrencyID())));
								form.addComponent(new SLabel(getPropertyName("paid_amount"),sale.getPayment_amount() + " "+getCurrencyDescription(getCurrencyID())));
							} else{
								double amount = sale.getAmount() / sale.getConversionRate();
								double paymentAmount = sale.getPayment_amount() / sale.getConversionRate();
								form.addComponent(new SLabel(getPropertyName("net_amount"),
										roundNumber(amount) + " "+getCurrencyDescription(getCurrencyID())+
										"("+sale.getAmount()+" "+getCurrencyDescription(sale.getCurrency_id())+")"));
								form.addComponent(new SLabel(getPropertyName("paid_amount"),
										roundNumber(paymentAmount) + " "+getCurrencyDescription(getCurrencyID())+
										"("+sale.getPayment_amount()+" "+getCurrencyDescription(sale.getCurrency_id())+")"));								
							}
							
							
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

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
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																	- invObj.getDiscount() 
																	+ invObj.getTaxAmount())+ " "+getCurrencyDescription(getCurrencyID())), 6, i);
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
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							LedgerModel ledger=new LedgerDao().getLedgeer(id);
							SalesModel salesModel = null;
							SalesInventoryDetailsModel inventoryDetailsModel = null;
							String items = "";
							Object[] row;
							List reportList=getSalesReportSubList(id);
							subTable.setVisibleColumns(allSubColumns);
							subTable.removeAllItems();
							subTable.setColumnFooter(TBC_AMOUNT, "0.0");
							double ttl = 0;
							double amount;
							if (reportList != null && reportList.size() > 0) {
								List<SalesInventoryDetailsModel> detailsList;
								for (int i = 0; i < reportList.size(); i++) {
									
									salesModel = (SalesModel) reportList.get(i);
									detailsList = salesModel.getInventory_details_list();
									items = "";
									for (int k = 0; k < detailsList.size(); k++) {
										inventoryDetailsModel = detailsList.get(k);
										if (k != 0) {
											items += ", ";
										}
										items += inventoryDetailsModel.getItem()
												.getName()
												+ " ("+getPropertyName("quantity")+" : "
												+ inventoryDetailsModel.getQunatity()
												+ " , "+getPropertyName("rate")+" : "
												+ inventoryDetailsModel.getUnit_price()
												+ " ) ";
									}
									amount = salesModel.getAmount() / salesModel.getConversionRate();
									if(salesModel.getCurrency_id() == getCurrencyID()){
										row = new Object[] {
												i + 1,
												salesModel.getId(),
												salesModel.getSales_number() + "",
												CommonUtil.getUtilFromSQLDate(salesModel
														.getDate()) + "",
												salesModel.getCustomer().getName(),
												salesModel.getAmount() +" "+getCurrencyDescription(getCurrencyID()), items };
										
									}else{
										row = new Object[] {
												i + 1,
												salesModel.getId(),
												salesModel.getSales_number() + "",
												CommonUtil.getUtilFromSQLDate(salesModel
														.getDate()) + "",
												salesModel.getCustomer().getName(),
												amount +" "+getCurrencyDescription(getCurrencyID())+
												"("+salesModel.getAmount()+" "+getCurrencyDescription(salesModel.getCurrency_id())+")", items };
									}
									
									subTable.addItem(row, i + 1);
									ttl += amount;

								}
								subTable.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));
							}
							else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Sales Details",725), closeBtn), subTable));

							popHor.addComponent(popUp);
							popUp.setPopupVisible(true);
							popUp.setHideOnMouseOut(false);
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

	
	protected List<Object> getSalesReportList() {
		long salesNo = 0;
		long custId = 0;
		long modeId=(Long)paymentModeSelect.getValue();;

		if (salesNoComboField.getValue() != null && !salesNoComboField.getValue().equals("") && !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}
		if (customerComboField.getValue() != null && !customerComboField.getValue().equals("")) {
			custId = toLong(customerComboField.getValue().toString());
		}
		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and a.payment_amount=a.amount ";
			
		} 
		else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and a.payment_amount<a.amount ";
			modeId=0;
		}
		
		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and a.active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and a.active=false ";
		}
		
		

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesDetailsWithMode(salesNo, custId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID(),modeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
	}
	
	
	protected List<Object> getSalesReportSubList(long id) {
		long salesNo = 0;
		long modeId=(Long)paymentModeSelect.getValue();
		
		if (salesNoComboField.getValue() != null && !salesNoComboField.getValue().equals("") && !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}
		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and a.payment_amount=a.amount ";
		} 
		else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and a.payment_amount<a.amount ";
			modeId=0;
		}
		
		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and a.active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and a.active=false ";
		}

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesDetailsWithMode(salesNo, id, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID(),modeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
	}
	
	
	protected List<Object> showSalesReportList() {
		long salesNo = 0;
		long custId = 0;
		long modeId=(Long)paymentModeSelect.getValue();
		
		if (salesNoComboField.getValue() != null
				&& !salesNoComboField.getValue().equals("")
				&& !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}
		if (customerComboField.getValue() != null
				&& !customerComboField.getValue().equals("")) {
			custId = toLong(customerComboField.getValue().toString());
		}

		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and a.payment_amount=a.amount ";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and a.payment_amount<a.amount ";
			modeId=0;
		}

		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and a.active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and a.active=false ";
		}

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesDetailsConsolidatedWithMode(salesNo, custId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID(),modeId);
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

	@SuppressWarnings("unchecked")
	private void loadBillNo(long customerId, long officeId) {
		List<Object> salesList = null;
		try {

			String condition1 = "";

			if ((Integer) filterTypeRadio.getValue() == 1) {
				condition1 = " and payment_amount=a.amount ";
			} else if ((Integer) filterTypeRadio.getValue() == 2) {
				condition1 = " and payment_amount<a.amount ";
			}

			if ((Integer) statusRadioButton.getValue() == 0) {
				condition1 += " and active=true ";
			} else if ((Integer) statusRadioButton.getValue() == 1) {
				condition1 += " and active=false ";
			}

			if (customerId != 0) {
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
			salesModel.setSales_number(getPropertyName("all"));
			if (salesList == null) {
				salesList = new ArrayList<Object>();
			}
			salesList.add(0, salesModel);
			container = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(container);
			salesNoComboField.setItemCaptionPropertyId("sales_number");
			salesNoComboField.setValue(0);

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
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
