package com.inventory.reports.ui;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.MonthWiseSaleBean;
import com.inventory.reports.dao.MonthWiseSalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
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
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 18, 2015
 */

public class MonthWiseSalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;
	
	Calendar toCalendar;
	Calendar fromCalendar;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private UserManagementDao userDao;

	LedgerDao ledDao;

	SRadioButton filterTypeRadio;

	private Report report;

	SalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_TYPE_ID = "TT";
	static String TBC_TYPE = "Type";
	static String TBC_MONTH = "Month";
	static String TBC_FROM = "From Date";
	static String TBC_TO = "To Date";
	static String TBC_AMOUNT = "Amount";
	
	static String TBC_ID = "Id";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_ITEMS = "Items";
	

	private STable table;
	private STable subTable;

	private Object[] allColumns;
	private Object[] visibleColumns;
	
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	
	SPopupView popUp, subPopUp;
	SNativeButton closeBtn;
	SHorizontalLayout popupHor;
	MonthWiseSalesReportDao dao;
	
	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer;
	boolean isPrint=false;
	
	List<Object> reportList;
	
	
	@SuppressWarnings({ "serial", "unchecked" })
	@Override
	public SPanel getGUI() {

		dao=new MonthWiseSalesReportDao();
		reportList = new ArrayList<Object>();
		allColumns = new Object[] { TBC_SN, TBC_TYPE_ID, TBC_TYPE, TBC_MONTH, TBC_FROM, TBC_TO, TBC_AMOUNT};
		allSubColumns = new Object[] { TBC_SN, TBC_TYPE_ID, TBC_ID, TBC_SALE_NO, TBC_DATE,TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleColumns = new Object[] { TBC_SN, TBC_TYPE, TBC_MONTH, TBC_AMOUNT};
		visibleSubColumns = new Object[] { TBC_SN, TBC_SALE_NO, TBC_DATE,TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		closeBtn = new SNativeButton("X");
		toCalendar=getCalendar();
		fromCalendar=getCalendar();
		popupHor=new SHorizontalLayout();
		
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
			officeComboField.setValue(getOfficeID());
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

			filterTypeRadio = new SRadioButton(null,250, SConstants.saleOrPurchaseList, "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(1);

			mainFormLayout.addComponent(filterTypeRadio);

			List<Object> customerList = ledDao.getAllCustomers((Long)officeComboField.getValue());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(getPropertyName("customer"),200, customerList, "id", "name", false, getPropertyName("all"));
			customerComboField.setValue((long)0);
			mainFormLayout.addComponent(customerComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 670, 250);
			subTable= new STable(null, 750, 250);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_TYPE_ID, Integer.class, null, TBC_TYPE_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_TYPE, String.class, null, getPropertyName("type"), null,Align.CENTER);
			table.addContainerProperty(TBC_MONTH, String.class, null,getPropertyName("month"), null, Align.CENTER);
			table.addContainerProperty(TBC_FROM, Date.class, null,getPropertyName("from_date"), null, Align.LEFT);
			table.addContainerProperty(TBC_TO, Date.class, null,getPropertyName("to_date"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
			table.setColumnExpandRatio(TBC_MONTH, 1f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1f);
			
			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_MONTH, getPropertyName("total"));
			
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_TYPE_ID, Integer.class, null, TBC_TYPE_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("sales_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);
			
			subTable.setVisibleColumns(visibleSubColumns);
			subTable.setSelectable(true);

			subTable.setFooterVisible(true);
			subTable.setColumnFooter(TBC_SALE_NO, getPropertyName("total"));

			mainLay.addComponent(popupHor);
			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);

			mainPanel.setContent(mainLay);

			
			
			customerComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					customerId = 0;
					if (customerComboField.getValue() != null && !customerComboField.getValue().toString().equals("0")) {
						customerId = toLong(customerComboField.getValue().toString());
					}
				}
			});

			
			
			filterTypeRadio.addListener(new Listener() {
				@SuppressWarnings("rawtypes")
				@Override
				public void componentEvent(Event event) {
					try{
						List customerList=new ArrayList();
						if(Integer.parseInt(filterTypeRadio.getValue().toString())==1){
							customerList = ledDao.getAllCustomers((Long)officeComboField.getValue());
							customerList.add(0,new LedgerModel(0, getPropertyName("all")));
							customerComboField.setCaption(getPropertyName("customer"));
						}
						else{
							customerList = ledDao.getAllSuppliers((Long)officeComboField.getValue());
							customerList.add(0,new LedgerModel(0, getPropertyName("all")));
							customerComboField.setCaption(getPropertyName("supplier"));
						}
						SCollectionContainer bic=SCollectionContainer.setList(customerList, "id");
						customerComboField.setContainerDataSource(bic);
						customerComboField.setItemCaptionPropertyId("name");
						customerComboField.setValue((long)0);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			fromDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					fromCalendar.setTime(fromDateField.getValue());
				}
			});

			
			
			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					toCalendar.setTime(toDateField.getValue());
				}
			});

			
			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue().toString()));
				}
			});
			
			
			
			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});

			
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if(isValid()){
							isPrint=true;
							getMonthlyDetails();
							if(reportList.size()>0){
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("MonthWiseSalesReport");
								report.setReportFileName("Month Wise Sales Report");
								
								if(Integer.parseInt(filterTypeRadio.getValue().toString())==1){
									map.put("REPORT_TITLE_LABEL", getPropertyName("sales_report"));
								}
								else{
									map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_report"));
								}
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("INVOICE_DATE_LABEL", getPropertyName("invoice_date"));
								map.put("SALES_NO_LABEL", getPropertyName("type"));
								map.put("CUSTOMER_LABEL", getPropertyName("month"));
								map.put("SALES_MAN_LABEL", getPropertyName("sales_man"));
								map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
								map.put("AMOUNT_LABEL", getPropertyName("amount"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "+ customerComboField
													.getItemCaption(customerComboField.getValue()) + "\t";
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
								report.setReportType(toInt(reportChoiceField.getValue().toString()));
								report.setOfficeName(officeComboField.getItemCaption(officeComboField.getValue()));
								report.createReport(reportList, map);
								reportList.clear();
							}
						}
						else {
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			
			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					
					try{
						if(isValid()){
							isPrint=false;
							getMonthlyDetails();
						}
					}
					catch(Exception e){
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
			
			
			
			subTable.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							if(Integer.parseInt(itm.getItemProperty(TBC_TYPE_ID).getValue().toString())==SConstants.SALES) {
								SalesNewUI sales = new SalesNewUI();
								sales.setCaption(getPropertyName("sales"));
								sales.getSalesNumberList().setValue((Long) itm.getItemProperty(TBC_ID).getValue());
								sales.center();
								popUp.setVisible(false);
								getUI().getCurrent().addWindow(sales);
								sales.addCloseListener(closeListener);
							}
							else if(Integer.parseInt(itm.getItemProperty(TBC_TYPE_ID).getValue().toString())==SConstants.PURCHASE){
								PurchaseUI purchase = new PurchaseUI();
								purchase.setCaption(getPropertyName("purchase"));
								purchase.getPurchaseNumberList().setValue((Long) itm.getItemProperty(TBC_ID).getValue());
								purchase.center();
								popUp.setVisible(false);
								getUI().getCurrent().addWindow(purchase);
								purchase.addCloseListener(closeListener);
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
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							subTable.removeAllItems();
							List detailList=new ArrayList();
							String head="";
							if(Integer.parseInt(itm.getItemProperty(TBC_TYPE_ID).getValue().toString())==SConstants.SALES){
								detailList=dao.getDetailedMonthlySalesDetails((Long)customerComboField.getValue(),
										CommonUtil.getSQLDateFromUtilDate((Date)itm.getItemProperty(TBC_FROM).getValue()),
										CommonUtil.getSQLDateFromUtilDate((Date)itm.getItemProperty(TBC_TO).getValue()),
										(Long)officeComboField.getValue());
								head=getPropertyName("sales");
							}
							else if(Integer.parseInt(itm.getItemProperty(TBC_TYPE_ID).getValue().toString())==SConstants.PURCHASE){
								detailList=dao.getDetailedMonthlyPurchaseDetails((Long)customerComboField.getValue(),
										CommonUtil.getSQLDateFromUtilDate((Date)itm.getItemProperty(TBC_FROM).getValue()),
										CommonUtil.getSQLDateFromUtilDate((Date)itm.getItemProperty(TBC_TO).getValue()),
										(Long)officeComboField.getValue());
								head=getPropertyName("purchase");
							}
							if(detailList.size()>0){
								subTable.setVisibleColumns(allSubColumns);
								Iterator itr=detailList.iterator();
								while (itr.hasNext()) {
									MonthWiseSaleBean bean=(MonthWiseSaleBean)itr.next();
									subTable.addItem(new Object[]{
											subTable.getItemIds().size()+1,
											bean.getType(),
											bean.getId(),
											bean.getSales(),
											bean.getDate(),
											bean.getCustomer(),
											roundNumber(bean.getAmount()),
											bean.getItem()},subTable.getItemIds().size()+1);
								}
							}
							Iterator it=subTable.getItemIds().iterator();
							double amount=0;
							while (it.hasNext()) {
								Item item=subTable.getItem(it.next());
								amount+=roundNumber(toDouble(item.getItemProperty(TBC_AMOUNT).getValue().toString()));
							}
							subTable.setColumnFooter(TBC_AMOUNT, ""+roundNumber(amount));
							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView("",new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(
															null,"<h2><u style='margin-left: 40px;'>"+head,
															725), closeBtn), subTable));
							popupHor.addComponent(popUp);
							popUp.setPopupVisible(true);
							popUp.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			subTable.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							SFormLayout form = new SFormLayout();
							if(Integer.parseInt(itm.getItemProperty(TBC_TYPE_ID).getValue().toString())==SConstants.SALES) {
								
								SalesModel sale=new SalesDao().getSale(toLong(itm.getItemProperty(TBC_ID).getValue().toString()));
								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
								form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
								form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
								form.addComponent(new SLabel(getPropertyName("paid_amount"),sale.getPayment_amount() + ""));
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
																		+ invObj.getTaxAmount())+ ""), 6, i);
									i++;
								}
								form.addComponent(grid);
								form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
								form.setStyleName("grid_max_limit");
								
							}
							else if(Integer.parseInt(itm.getItemProperty(TBC_TYPE_ID).getValue().toString())==SConstants.PURCHASE){

								PurchaseModel objModel = new PurchaseDao().getPurchaseModel(toLong(itm.getItemProperty(TBC_ID).getValue().toString()));
								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("purchase_no"),objModel.getPurchase_no() + ""));
								form.addComponent(new SLabel(getPropertyName("ref_no"),
										objModel.getRef_no() + ""));
								form.addComponent(new SLabel(getPropertyName("supplier"), objModel.getSupplier().getName()));
								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
								form.addComponent(new SLabel(getPropertyName("net_amount"), objModel.getAmount() + ""));
								form.addComponent(new SLabel(getPropertyName("paid_amount"),objModel.getPaymentAmount() + ""));
		
								SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel.getPurchase_details_list().size() + 3);
								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
								grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
								grid.addComponent(new SLabel(null, getPropertyName("discount")),5, 0);
								grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
								grid.setSpacing(true);
								int i = 1;
								PurchaseInventoryDetailsModel invObj;
								Iterator itmItr = objModel.getPurchase_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (PurchaseInventoryDetailsModel) itmItr.next();
									grid.addComponent(new SLabel(null, i + ""),0, i);
									grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
									grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
									grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,i);
									grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
									grid.addComponent(new SLabel(null,(invObj.getUnit_price()* invObj.getQunatity()- 
																		invObj.getDiscount() + 
																		invObj.getTaxAmount())+ ""), 6, i);
									i++;
								}
								form.addComponent(grid);
								form.addComponent(new SLabel(getPropertyName("comment"), objModel.getComments()));
								form.setStyleName("grid_max_limit");
							}
							subPopUp = new SPopupView("", form);
							popupContainer.removeAllComponents();
							popupContainer.addComponent(subPopUp);
							subPopUp.setPopupVisible(true);
							subPopUp.setHideOnMouseOut(false);
						}
						else{
							subPopUp.setPopupVisible(false);
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

	
	
	@SuppressWarnings({ "rawtypes"})
	private List getMonthlyDetails() {
		reportList.clear();
		if(!isPrint)
			table.removeAllItems();
		List resultList = new ArrayList();
		try{
			Calendar monthStartCalendar=getCalendar(); 
			Calendar variableCalendar=getCalendar();
			int dayend=fromCalendar.getActualMaximum(Calendar.DATE)-fromCalendar.get(Calendar.DATE);
			monthStartCalendar.setTime(fromDateField.getValue());
			variableCalendar.setTime(fromCalendar.getTime());
			variableCalendar.add(Calendar.DATE, dayend);
			int monthDifference=calculateMonthDifference(fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.YEAR),
					toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.YEAR));
			for(int i=0; i<monthDifference; i++){
				
				int startInterval=monthStartCalendar.getActualMaximum(Calendar.DATE)-monthStartCalendar.get(Calendar.DATE)+1;
				
				if(i!=0){
					variableCalendar.add(Calendar.DATE, monthStartCalendar.getActualMaximum(Calendar.DATE));
				}
				if(	(variableCalendar.get(Calendar.MONTH)==toCalendar.get(Calendar.MONTH)) && 
					(variableCalendar.get(Calendar.YEAR)==toCalendar.get(Calendar.YEAR))) {
					variableCalendar.setTime(toCalendar.getTime());
				}
				List detailList=new ArrayList();
				if(Integer.parseInt(filterTypeRadio.getValue().toString())==1){
					detailList=dao.getMonthlySaleDetails(	(Long)customerComboField.getValue(),
															CommonUtil.getSQLDateFromUtilDate(monthStartCalendar.getTime()),
															CommonUtil.getSQLDateFromUtilDate(variableCalendar.getTime()),
															(Long)officeComboField.getValue());
				}
				else{
					detailList=dao.getMonthlyPurchaseDetails(	(Long)customerComboField.getValue(),
															CommonUtil.getSQLDateFromUtilDate(monthStartCalendar.getTime()),
															CommonUtil.getSQLDateFromUtilDate(variableCalendar.getTime()),
															(Long)officeComboField.getValue());
				}
				if(detailList.size()>0){
					table.setVisibleColumns(allColumns);
					Iterator itr=detailList.iterator();
					while (itr.hasNext()) {
						MonthWiseSaleBean bean=(MonthWiseSaleBean)itr.next();
						if(!isPrint){
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									bean.getType(),
									bean.getParticular(),
									getMonth(monthStartCalendar.get(Calendar.MONTH))+" "+monthStartCalendar.get(Calendar.YEAR),
									monthStartCalendar.getTime(),
									variableCalendar.getTime(),
									roundNumber(bean.getAmount())},table.getItemIds().size()+1);
						}
						else{
							reportList.add(new MonthWiseSaleBean( bean.getParticular(), roundNumber(bean.getAmount()), getMonth(monthStartCalendar.get(Calendar.MONTH))+" "+monthStartCalendar.get(Calendar.YEAR)));
						}
					}
					table.setVisibleColumns(visibleColumns);
				}
				monthStartCalendar.add(Calendar.DATE, startInterval);
			}
			
			if(!isPrint){
				Iterator it=table.getItemIds().iterator();
				double amount=0;
				while (it.hasNext()) {
					Item item=table.getItem(it.next());
					amount+=roundNumber(toDouble(item.getItemProperty(TBC_AMOUNT).getValue().toString()));
				}
				table.setColumnFooter(TBC_AMOUNT, ""+amount);
				if(table.getItemIds().size()<=0)
					SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return resultList;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(fromDateField.getValue()==null) {
			setRequiredError(fromDateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(fromDateField, null, false);
		if(toDateField.getValue()==null) {
			setRequiredError(toDateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(toDateField, null, false);
		if(fromDateField.getValue().compareTo(toDateField.getValue())>0) {
			setRequiredError(fromDateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(fromDateField, null, false);
		if(customerComboField.getValue()==null || customerComboField.getValue().toString().equals("")) {
			customerComboField.setValue((long)0);
		}
		
		return valid;
	}

	
	
	public int calculateMonthDifference(int smonth,int syear,int tmonth,int tyear){
		int diff=0,diffyear=0;
		try{
			if(syear==tyear){
				diff=(tmonth-smonth)+1;
			}
			else{
				diffyear=tyear-syear;
				if(diffyear>1){
					diff=(13-smonth)+(tmonth)+((diffyear-1)*12);
				}
				else{
					diff=(13-smonth)+(tmonth);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diff;
	}
	
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	
	String getMonth(int m) {
		
	    String month = null;
	    DateFormatSymbols dfs = new DateFormatSymbols();
	    String[] months = dfs.getMonths();
	    if (m >= 0 && m <= 11 ) {
	        month = months[m];
	    }
	    return month;
	}
	
}
