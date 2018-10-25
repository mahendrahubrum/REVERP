package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.SalesManWiseReportBean;
import com.inventory.reports.dao.SalesManWiseReportDao;
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
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 19, 2015
 */

@SuppressWarnings("serial")
public class SalesManWiseReportUI extends SparkLogic {

	
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField salesManComboField;
	private SComboField customerComboField;
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
	SPopupView popUp, subPopUp;
	SNativeButton closeBtn;

	private UserManagementDao userDao;
	SHorizontalLayout popupContainer;
	SHorizontalLayout popHor;
	LedgerDao ledDao;
	
	SalesManWiseReportDao dao;

	SRadioButton filterTypeRadio;

	private Report report;

	SalesDao salDao;
	boolean isPrint=false;
	List<Object> reportList;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALES_MAN = "Sales Man";
	static String TBC_FROM = "From Id";
	static String TBC_FROM_SHOW = "From";
	static String TBC_TO = "To Id";
	static String TBC_TO_SHOW = "To";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_PAYMENT = "Payment Amount";
	static String TBC_PENDING = "Pending Amount";
	static String TBC_ITEMS = "Items";

	private STable table,subtable;

	private Object[] allColumns;
	private Object[] visibleColumns;
	
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;
	
	Calendar toCalendar;
	Calendar fromCalendar;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		popHor=new SHorizontalLayout();
		reportList = new ArrayList<Object>();
		
		dao=new SalesManWiseReportDao();
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_SALES_MAN, TBC_FROM, TBC_FROM_SHOW, TBC_TO, TBC_TO_SHOW, TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
		visibleColumns = new Object[] { TBC_SN, TBC_SALES_MAN, TBC_FROM_SHOW, TBC_TO_SHOW, TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
		
		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_SALES_MAN, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_ITEMS ,TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
		visibleSubColumns = new String[] { TBC_SN, TBC_SALES_MAN, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_ITEMS,TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };

		
		closeBtn = new SNativeButton("X");
		ledDao = new LedgerDao();
		salDao = new SalesDao();

		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(1100, 420);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		toCalendar=getCalendar();
		fromCalendar=getCalendar();
		
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

			filterTypeRadio = new SRadioButton(null, 250, SConstants.filterDayList, "key", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue((long)1);

			mainFormLayout.addComponent(filterTypeRadio);

			
			salesManComboField=new SComboField(getPropertyName("sales_man"),200);
			salesManComboField.setInputPrompt(getPropertyName("all"));
			loadSalesMan();
			
			mainFormLayout.addComponent(salesManComboField);
			
			

			customerComboField = new SComboField(getPropertyName("customer"),200);
			loadCustomers(getOfficeID(),(long)0);
			mainFormLayout.addComponent(customerComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 700, 275);
			subtable = new STable(null, 900, 250);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
			table.addContainerProperty(TBC_FROM, Date.class, null,getPropertyName("from"), null, Align.CENTER);
			table.addContainerProperty(TBC_FROM_SHOW, String.class, null,getPropertyName("from_date"), null, Align.LEFT);
			table.addContainerProperty(TBC_TO, Date.class, null,getPropertyName("to"), null, Align.CENTER);
			table.addContainerProperty(TBC_TO_SHOW, String.class, null,getPropertyName("to_date"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			
			
			subtable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subtable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subtable.addContainerProperty(TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
			subtable.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("invoice_no"), null, Align.LEFT);
			subtable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subtable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			subtable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);
			subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
			subtable.addContainerProperty(TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			subtable.addContainerProperty(TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			

			table.setColumnExpandRatio(TBC_SALES_MAN, 1f);
			table.setColumnExpandRatio(TBC_FROM_SHOW, 0.5f);
			table.setColumnExpandRatio(TBC_TO_SHOW, 0.5f);
			table.setColumnExpandRatio(TBC_AMOUNT, 0.55f);
			table.setColumnExpandRatio(TBC_PAYMENT, 0.55f);
			table.setColumnExpandRatio(TBC_PENDING, 0.55f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);
			table.setFooterVisible(true);
			table.setColumnFooter(TBC_SALES_MAN, getPropertyName("total"));
			
			subtable.setColumnExpandRatio(TBC_SALES_MAN, 1f);
			subtable.setColumnExpandRatio(TBC_SALE_NO, 0.45f);
			subtable.setColumnExpandRatio(TBC_DATE, 0.5f);
			subtable.setColumnExpandRatio(TBC_CUSTOMER, 1f);
			subtable.setColumnExpandRatio(TBC_ITEMS, 1.5f);
			subtable.setColumnExpandRatio(TBC_AMOUNT, 0.75f);
			subtable.setColumnExpandRatio(TBC_PAYMENT, 0.75f);
			subtable.setColumnExpandRatio(TBC_PENDING, 0.75f);
			
			subtable.setVisibleColumns(visibleSubColumns);
			subtable.setSelectable(true);
			subtable.setFooterVisible(true);
			subtable.setColumnFooter(TBC_SALES_MAN, getPropertyName("total"));

			mainLay.addComponent(popHor);
			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);
			

			mainPanel.setContent(mainLay);

			
			salesManComboField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					
					if(salesManComboField.getValue()!=null)
						loadCustomers(toLong(officeComboField.getValue()
							.toString()),(Long)salesManComboField.getValue());
					else
						loadCustomers(toLong(officeComboField.getValue()
								.toString()),(long)0);
					
				}
			});

			
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
				@Override
				public void componentEvent(Event event) {
					
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
					loadSalesMan();
					loadCustomers(toLong(officeComboField.getValue()
							.toString()),(long)0);
					
				}
			});

			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if(isValid()) {
							isPrint=true;
							getSalesManWiseDetails();
						}

						if (reportList.size()>0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("SalesManWiseSaleReport");
							report.setReportFileName("SalesManWiseSaleReport");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_man_wise_sales_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("SALES_MAN_LABEL", getPropertyName("sales_man"));
							map.put("INVOICE_NO_LABEL", getPropertyName("from_date"));
							map.put("CUSTOMER_LABEL", getPropertyName("to_date"));
							map.put("PAID_AMOUNT_LABEL", getPropertyName("amount_paid"));
							map.put("PENDING_AMOUNT_LABEL", getPropertyName("pending_amount"));
							map.put("INVOICE_AMOUNT_LABEL", getPropertyName("invoice_amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (salesManComboField.getValue() != null
									&& !salesManComboField.getValue().toString()
									.equals("0")) {
								subHeader += getPropertyName("sales_man")+" : "
										+ salesManComboField
										.getItemCaption(salesManComboField
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

						} else {
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});
			
			
			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					
					if(isValid()) {
						isPrint=false;
						getSalesManWiseDetails();
					}
				}
			});

			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(table.getValue()!=null){
							subtable.removeAllItems();
							Item item=table.getItem(table.getValue());
							long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
							Date start=(Date)item.getItemProperty(TBC_FROM).getValue();
							Date end=(Date)item.getItemProperty(TBC_TO).getValue();
							List detialList=dao.getSalesMansSales(	id,
																	(Long)officeComboField.getValue(),
																	(Long)customerComboField.getValue(),
																	CommonUtil.getSQLDateFromUtilDate(start),
																	CommonUtil.getSQLDateFromUtilDate(end));
							subtable.setVisibleColumns(allSubColumns);
							if(detialList.size()>0){
								Iterator itr=detialList.iterator();
								while (itr.hasNext()) {
									SalesModel mdl=(SalesModel)itr.next();
									String items="";
									Iterator it=mdl.getInventory_details_list().iterator();
									String name=new UserManagementDao().getUserNameFromLoginID(mdl.getResponsible_employee());
									while (it.hasNext()) {
										SalesInventoryDetailsModel det=(SalesInventoryDetailsModel)it.next();
										items+=det.getItem().getName()+"(Quantity : "+roundNumber(det.getQunatity())+", Rate : "+roundNumber(det.getQunatity())+"), ";
									}
									subtable.addItem(new Object[]{
											subtable.getItemIds().size()+1,
											mdl.getId(),
											name,
											mdl.getSales_number()+"",
											CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
											mdl.getCustomer().getName(),
											items,
											roundNumber(mdl.getAmount()),
											roundNumber(mdl.getPayment_amount()- mdl.getPaid_by_payment()),
											roundNumber(mdl.getAmount()- mdl.getPayment_amount()- mdl.getPaid_by_payment()),
											
									},subtable.getItemIds().size()+1);
								}
							}
							Iterator itr=subtable.getItemIds().iterator();
							double amount=0,paid=0,bal=0;
							while (itr.hasNext()) {
								Item itm = subtable.getItem(itr.next());
								amount+=roundNumber(toDouble(itm.getItemProperty(TBC_AMOUNT).getValue().toString()));
								paid+=roundNumber(toDouble(itm.getItemProperty(TBC_PAYMENT).getValue().toString()));
								bal+=roundNumber(toDouble(itm.getItemProperty(TBC_PENDING).getValue().toString()));
							}
							subtable.setColumnFooter(TBC_AMOUNT, roundNumber(amount)+"");
							subtable.setColumnFooter(TBC_PAYMENT, roundNumber(paid)+"");
							subtable.setColumnFooter(TBC_PENDING, roundNumber(bal)+"");
							subtable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView("",new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(
									null,"<h2><u style='margin-left: 40px;'>Sales Man Wise Sales",
									875), closeBtn), subtable));
							
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
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			
			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
			subtable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (subtable.getValue() != null) {
							Item item = subtable.getItem(subtable.getValue());
							SalesNewUI sales = new SalesNewUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							sales.center();
							popUp.setVisible(false);
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
			
			
			subtable.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subtable.getValue() != null) {
							Item itm = subtable.getItem(subtable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesModel sale=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("sales_man"),userDao.getUserFromLogin(sale.getResponsible_employee()).getFirst_name()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
							form.addComponent(new SLabel(getPropertyName("amount_paid"),sale.getPayment_amount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("unit_price")), 4, 0);
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
							popupContainer.removeAllComponents();
							subPopUp = new SPopupView("", form);
							popupContainer.addComponent(subPopUp);
							subPopUp.setPopupVisible(true);
							subPopUp.setHideOnMouseOut(false);
						}
						else
							subPopUp.setPopupVisible(false);
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

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadSalesMan() {
		try {

			List saList = new ArrayList();
			saList.add(0, new UserModel(0,getPropertyName("all")));
			saList.addAll(new SalesManMapDao().getUsers((Long) officeComboField.getValue(), 0));
			SCollectionContainer con=SCollectionContainer.setList(saList, "id");
			salesManComboField.setContainerDataSource(con);
			salesManComboField.setItemCaptionPropertyId("first_name");
			salesManComboField.setValue((long)0);
			
		} catch (Exception e) {
		}

	}

	
	@SuppressWarnings("rawtypes")
	public List getSalesManWiseDetails(){
		reportList.clear();
		if(!isPrint){
			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
			table.setColumnFooter(TBC_PAYMENT, "0.0");
			table.setColumnFooter(TBC_PENDING, "0.0");
		}
		List resultList = new ArrayList();
		try{
			Calendar startCalendar=getCalendar(); 
			Calendar endCalendar=getCalendar();
			
			int diffdays=(int) ((toDateField.getValue().getTime()-fromDateField.getValue().getTime())/(60*60*24*1000))+1;
			
			long type=toLong(filterTypeRadio.getValue().toString());
			
			List salesManList=dao.getSalesManList((Long)officeComboField.getValue(), (Long)salesManComboField.getValue());
			table.setVisibleColumns(allColumns);
			if(salesManList.size()>0){
				
				Iterator it=salesManList.iterator();
				while (it.hasNext()) {
					
					UserModel user=(UserModel)it.next();
					
					startCalendar.setTime(fromDateField.getValue());
					endCalendar.setTime(fromDateField.getValue());
					int days=diffdays;
					for(int i=0;i<diffdays;i++){

						if(days<=0)
							break;
						
						endCalendar.setTime(startCalendar.getTime());
						int monthend=startCalendar.getActualMaximum(Calendar.DATE)-startCalendar.get(Calendar.DATE);
						
						if(type==(long)1) {
							endCalendar.setTime(startCalendar.getTime());
						}
						else if(type==(long)2) {
							if(days>=6) {
								endCalendar.add(Calendar.DATE, 6);
							}
							else {
								endCalendar.add(Calendar.DATE, days-1);
							}
						}
						else if(type==(long)3) {
							if(days>=monthend){
								endCalendar.add(Calendar.DATE, monthend);
							}
							else{
								endCalendar.setTime(toCalendar.getTime());
							}
						}
						
						if(endCalendar.getTime().compareTo(toCalendar.getTime())>0)
							break;
						
						List detailList=dao.getSalesManWiseReport(user.getLoginId().getId(),
																	(Long)customerComboField.getValue(),
																	CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()),
																	CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime()),
																	(Long)officeComboField.getValue());
						if(detailList.size()>0){
							Iterator itr=detailList.iterator();
							while (itr.hasNext()) {
								SalesManWiseReportBean bean = (SalesManWiseReportBean) itr.next();
								if(bean.getAmount()>0){
									if(!isPrint){
										table.addItem(new Object[]{
												table.getItemIds().size()+1,
												bean.getId(),
												bean.getSales_man(),
												startCalendar.getTime(),
												CommonUtil.formatDateToDDMMYYYY(startCalendar.getTime()),
												endCalendar.getTime(),
												CommonUtil.formatDateToDDMMYYYY(endCalendar.getTime()),
												roundNumber(bean.getAmount()),
												roundNumber(bean.getPaid()),
												roundNumber(bean.getBalance())},table.getItemIds().size()+1);
									}
									else{
										reportList.add(new SalesManWiseReportBean(bean.getSales_man(), 
														CommonUtil.formatDateToDDMMYYYY(startCalendar.getTime()),
														CommonUtil.formatDateToDDMMYYYY(endCalendar.getTime()),
														roundNumber(bean.getAmount()),
														roundNumber(bean.getPaid()),
														roundNumber(bean.getBalance())));
									}
								}
							}
						}
						
						if(type==(long)1) {
							startCalendar.add(Calendar.DATE, 1);
							days-=1;
						}
						else if(type==(long)2) {
							startCalendar.add(Calendar.DATE, 7);
							days-=7;
						}
						else if(type==(long)3) {
							startCalendar.add(Calendar.DATE, monthend+1);
							days-=(monthend+1);
						}
					}
					
				}
				if(!isPrint){
					if(!salesManComboField.getValue().toString().equals("0")){
						Iterator itr=table.getItemIds().iterator();
						double amount=0,paid=0,bal=0;
						while (itr.hasNext()) {
							Item item = table.getItem(itr.next());
							amount+=roundNumber(toDouble(item.getItemProperty(TBC_AMOUNT).getValue().toString()));
							paid+=roundNumber(toDouble(item.getItemProperty(TBC_PAYMENT).getValue().toString()));
							bal+=roundNumber(toDouble(item.getItemProperty(TBC_PENDING).getValue().toString()));
						}
						table.setColumnFooter(TBC_AMOUNT, roundNumber(amount)+"");
						table.setColumnFooter(TBC_PAYMENT, roundNumber(paid)+"");
						table.setColumnFooter(TBC_PENDING, roundNumber(bal)+"");
					}
					if(table.getItemIds().size()<=0)
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
					
				}
				
			}
			table.setVisibleColumns(visibleColumns);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return resultList;
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadCustomers(long officeID, long salesMan) {
		List<Object> customerList;
		try {
			if (salesMan != 0)
				customerList = ledDao.getAllCustomersUnderSalesMan(officeID,salesMan);
			else
				customerList = ledDao.getAllCustomers(officeID);

			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			
			customerComboField.setContainerDataSource(SCollectionContainer.setList(customerList, "id"));
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue((long)0);
			
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
		if(salesManComboField.getValue()==null || salesManComboField.getValue().toString().equals("")) {
			salesManComboField.setValue((long)0);
		}
		if(officeComboField.getValue()==null || officeComboField.getValue().toString().equals("")) {
			setRequiredError(officeComboField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(officeComboField, null, false);
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
