package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.dao.SalesManMapDao;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.ExceptionReportDao;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.DeliveryNoteDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.DeliveryNoteDetailsModel;
import com.inventory.sales.model.DeliveryNoteModel;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

public class ExceptionReportUI extends SparkLogic {

	private static final long serialVersionUID = -5438735068299993910L;
	
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

	private UserManagementDao userDao;
	SHorizontalLayout popupContainer;
	SHorizontalLayout detailPopupContainer;
	LedgerDao ledDao;
	ItemSubGroupDao itemSubGrpDao;
	ExceptionReportDao exceptiondao;
	DeliveryNoteDao deliveryDao;

	SRadioButton filterTypeRadio;
	SRadioButton statusRadioButton;

	private Report report;

	SalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_SALES_MAN_ID = "Sales Man Id";
	static String TBC_SALES_MAN = "Sales Man";
	static String TBC_SALES_ID = "Sales ID";
	static String TBC_SALES_NO = "Sales No";
	static String TBC_DELIVERY_NOTE_ID = "Delivery Note Id";
	static String TBC_DELIVERY_NOTE_NO = "Delivery Note No";
	static String TBC_ITEM_ID = "Item Id";
	static String TBC_ITEM_CODE = "Item Code";
	static String TBC_ITEM_NAME = "Item";
	static String TBC_UNIT_ID = "Unit Id";
	static String TBC_UNIT = "Unit";
	static String TBC_STOCK_OUT = "Stock Out";
	static String TBC_STOCK_IN = "Stock In";
	static String TBC_SALES_QTY = "Sales";
	
	static String POP_TBC_SN = "SN";
//	static String POP_TBC_SALES_MAN = "Sales Man";
	static String POP_TBC_SALE_NO = "Sale No";
	static String POP_TBC_DATE = "Date";
//	static String POP_TBC_CUSTOMER = "Customer";
	static String POP_TBC_AMOUNT = "Amount";
	static String POP_TBC_PAYMENT = "Payment Amount";
	static String POP_TBC_PENDING = "Pending Amount";
	
	private STable popTable;
	private STable table;

	private String[] allColumns;
	private String[] visibleColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;
	
	SalesReportDao dao;

	private SComboField itemGrpComboField;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		allColumns = new String[] { TBC_SN,TBC_SALES_ID,TBC_SALES_MAN, TBC_SALES_NO,TBC_DELIVERY_NOTE_ID,
				TBC_DELIVERY_NOTE_NO,TBC_ITEM_ID ,TBC_ITEM_CODE, TBC_ITEM_NAME, TBC_UNIT_ID,TBC_UNIT,TBC_STOCK_OUT,
				TBC_STOCK_IN,TBC_SALES_QTY};
		visibleColumns = new String[] { TBC_SN, TBC_ITEM_CODE,TBC_ITEM_NAME,TBC_UNIT,TBC_SALES_NO,TBC_STOCK_OUT,
				TBC_SALES_QTY,TBC_STOCK_IN };

		detailPopupContainer=new SHorizontalLayout();
		ledDao = new LedgerDao();
		salDao = new SalesDao();
		dao=new SalesReportDao();
		itemSubGrpDao=new ItemSubGroupDao();
		exceptiondao=new ExceptionReportDao();
		deliveryDao=new DeliveryNoteDao();

		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(1100, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		SHorizontalLayout mainLay = new SHorizontalLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {

			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				sett = (SettingsValuePojo) session.getAttribute("settings");

			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao().getAllOfficeNamesUnderOrg(getOrganizationID()),
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
					250, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);

			//mainFormLayout.addComponent(filterTypeRadio);

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);
			
			customerComboField = new SComboField(getPropertyName("customer"),200);
			
			salesManComboField=new SComboField(getPropertyName("sales_man"),200);
			salesManComboField.setInputPrompt(getPropertyName("all"));
			loadSalesMan();
			itemGrpComboField = new SComboField(getPropertyName("item_sub_grp"),200,
					null,"id","name",true,getPropertyName("select"));
			loadItemSubGrp();
			reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));
			
			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			
			mainFormLayout.addComponent(salesManComboField);
			mainFormLayout.addComponent(itemGrpComboField);
			mainFormLayout.addComponent(reportChoiceField);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 700, 275);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_SALES_MAN_ID, Long.class, null,TBC_SALES_MAN_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_SALES_MAN, String.class, null,TBC_SALES_MAN, null, Align.CENTER);
			table.addContainerProperty(TBC_SALES_ID, Long.class, null,TBC_SALES_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_SALES_NO, String.class, null,TBC_SALES_NO, null, Align.LEFT);
			table.addContainerProperty(TBC_DELIVERY_NOTE_ID, Long.class, null,TBC_DELIVERY_NOTE_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DELIVERY_NOTE_NO, String.class, null,TBC_DELIVERY_NOTE_NO, null, Align.LEFT);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_CODE, String.class, null,TBC_ITEM_CODE, null, Align.LEFT);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,TBC_ITEM_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,TBC_UNIT, null, Align.CENTER);
			table.addContainerProperty(TBC_STOCK_OUT, Double.class, null,TBC_STOCK_OUT, null, Align.RIGHT);
			table.addContainerProperty(TBC_STOCK_IN, Double.class, null,TBC_STOCK_IN, null, Align.RIGHT);
			table.addContainerProperty(TBC_SALES_QTY, Double.class, null,TBC_SALES_QTY, null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ITEM_CODE, 1f);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 1.2f);
			table.setColumnExpandRatio(TBC_UNIT, 1f);
			table.setColumnExpandRatio(TBC_SALES_NO, 1f);
			table.setColumnExpandRatio(TBC_STOCK_OUT, 1f);
			table.setColumnExpandRatio(TBC_SALES_QTY, 1f);
			table.setColumnExpandRatio(TBC_STOCK_IN, 1f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, getPropertyName("total"));

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);
			mainLay.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLay);
			
			popTable = new STable(null, 1000, 400);
			popTable.setSelectable(true);
			
			popTable.addContainerProperty(POP_TBC_SN, Integer.class, null, "#", null,Align.CENTER);
//			popTable.addContainerProperty(POP_TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null,Align.CENTER);
			popTable.addContainerProperty(POP_TBC_SALE_NO, String.class, null,getPropertyName("sales_no"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
//			popTable.addContainerProperty(POP_TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
			popTable.addContainerProperty(POP_TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
			popTable.addContainerProperty(POP_TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			popTable.addContainerProperty(POP_TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			
			popTable.setFooterVisible(true);
			popTable.setColumnFooter(POP_TBC_SALE_NO, getPropertyName("total"));

			
			/*salesManComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(salesManComboField.getValue()!=null)
						loadCustomers(toLong(officeComboField.getValue()
							.toString()),(Long)salesManComboField.getValue());
					else
						loadCustomers(toLong(officeComboField.getValue()
								.toString()),(long)0);
					
				}
			});*/

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadSalesMan();
				}
			});

			generateButton.addClickListener(new ClickListener() {
				@SuppressWarnings("unused")
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						long itemSubGrp=0;
						SalesModel salesModel = null;
						String salesPerson = "";
						List reportList=new ArrayList();
						
						if (itemGrpComboField.getValue() != null && !itemGrpComboField.getValue().equals("0")) {
							itemSubGrp = toLong(itemGrpComboField.getValue().toString());
						}

						List<Object> repList = getSalesReportList();
						if (repList != null && repList.size() > 0) {
							Iterator itr1=repList.iterator();
							while(itr1.hasNext()){
								SalesModel salMdl=(SalesModel) itr1.next();
								List salesDetailList=salMdl.getInventory_details_list();
								if(salesDetailList.size()>0){
									Iterator itr2=salesDetailList.iterator();
									while(itr2.hasNext()){
										SalesInventoryDetailsModel detMdl=(SalesInventoryDetailsModel) itr2.next();
										
										if(itemSubGrp!=0){
											if(detMdl.getItem().getSub_group().getId()==itemSubGrp){
												DeliveryNoteDetailsModel noteMdl=deliveryDao.getDeliveryNoteDetailsModel(detMdl.getDelivery_child_id());
												
												if(noteMdl!=null){
													SalesReportBean reportBean=new SalesReportBean();
													
													reportBean.setSalesNo(salMdl.getSales_number());
													reportBean.setId(detMdl.getId());
													
													reportBean.setItemId(noteMdl.getItem().getId());
													reportBean.setItemCode(noteMdl.getItem().getItem_code());
													reportBean.setItems(noteMdl.getItem().getName());
													
													reportBean.setDeliveryNoteId(detMdl.getDelivery_child_id());
													reportBean.setDeliveryNoteNo(new DeliveryNoteModel(detMdl.getDelivery_id()).getDeliveryNo());
													
													reportBean.setUnitId(noteMdl.getUnit().getId());
													reportBean.setUnit(noteMdl.getUnit().getSymbol());
													
													reportBean.setStockOut(noteMdl.getQunatity());
													reportBean.setQuantity(detMdl.getQunatity());
													
													reportList.add(reportBean);
												}
											}
										}else{
											DeliveryNoteDetailsModel noteMdl=deliveryDao.getDeliveryNoteDetailsModel(detMdl.getDelivery_child_id());
											
											if(noteMdl!=null){

												SalesReportBean reportBean=new SalesReportBean();
												
												reportBean.setSalesNo(salMdl.getSales_number());
												reportBean.setId(detMdl.getId());
												
												reportBean.setItemId(noteMdl.getItem().getId());
												reportBean.setItemCode(noteMdl.getItem().getItem_code());
												reportBean.setItems(noteMdl.getItem().getName());
												
												reportBean.setDeliveryNoteId(detMdl.getDelivery_child_id());
												reportBean.setDeliveryNoteNo(new DeliveryNoteModel(detMdl.getDelivery_id()).getDeliveryNo());
												
												reportBean.setUnitId(noteMdl.getUnit().getId());
												reportBean.setUnit(noteMdl.getUnit().getSymbol());
												
												reportBean.setStockOut(noteMdl.getQunatity());
												reportBean.setQuantity(detMdl.getQunatity());
												
												reportList.add(reportBean);
											}
										}
									}
								}
							}
						}
						List reportList1=new ArrayList();
						
						if(reportList.size()>0){
							
							Iterator it=reportList.iterator();
							int ct=1;
							while(it.hasNext()){
								SalesReportBean bean1=(SalesReportBean) it.next();
								double balance=bean1.getStockOut();
								
								Iterator it1=reportList.iterator();
								int intCt=1;
								while(it1.hasNext() && intCt<=ct){
									intCt=intCt+1;
									SalesReportBean bean2=(SalesReportBean) it1.next();
									if(bean1.getDeliveryNoteId()==bean2.getDeliveryNoteId() && bean1.getItemId()==bean2.getItemId()){
										balance=balance-bean2.getQuantity();
									}
								}
								SalesReportBean reportBean=new SalesReportBean();
								
								reportBean.setItemId(bean1.getItemId());
								reportBean.setItemCode(bean1.getItemCode());
								reportBean.setItems(bean1.getItems());
								reportBean.setDeliveryNoteId(bean1.getDeliveryNoteId());
								reportBean.setDeliveryNoteNo(bean1.getDeliveryNoteNo());
								reportBean.setSalesNo(bean1.getSalesNo());
								reportBean.setUnit(bean1.getUnit());
								reportBean.setStockOut(bean1.getStockOut());
								reportBean.setStockIn(balance);
								reportBean.setQuantity(bean1.getQuantity());
								
								reportList1.add(reportBean);
								ct=ct+1;
							}
						}
						
						System.out.println("reportList1 size---"+reportList1);

						if (reportList1!=null && reportList1.size()>0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("ExceptionReportForSales");
							report.setReportFileName("ExceptionReportForSales");
							report.setReportTitle("Exception Report");
							
							map.put("REPORT_TITLE_LABEL", "Exception Report");
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("STOCK_IN_LABEL", getPropertyName("stock_in"));
							map.put("STOCK_OUT_LABEL", getPropertyName("stock_out"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
							map.put("SALES_QUANTITY_LABEL", getPropertyName("sales"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							
							
							
							String subHeader = "";
							
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
									+ "\t\t "+getPropertyName("to")+" : "
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
							report.createReport(reportList1, map);

							reportList1.clear();

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
					long itemSubGrp=0;
					try {
						table.removeAllItems();
						if(isValid()){
							if (itemGrpComboField.getValue() != null && !itemGrpComboField.getValue().equals("")) {
								itemSubGrp = toLong(itemGrpComboField.getValue().toString());
							}
							List reportList=new ArrayList();

							List repList = getSalesReportList();
							List balanceList=new ArrayList();
							
							if (repList != null && repList.size() > 0) {
								table.setVisibleColumns(allColumns);
								Iterator itr1=repList.iterator();
								while(itr1.hasNext()){
									SalesModel salMdl=(SalesModel) itr1.next();
									List salesDetailList=salMdl.getInventory_details_list();
									if(salesDetailList.size()>0){
										Iterator itr2=salesDetailList.iterator();
										while(itr2.hasNext()){
											SalesInventoryDetailsModel detMdl=(SalesInventoryDetailsModel) itr2.next();
											
											if(itemSubGrp!=0){
												if(detMdl.getItem().getSub_group().getId()==itemSubGrp){
													DeliveryNoteDetailsModel noteMdl=deliveryDao.getDeliveryNoteDetailsModel(detMdl.getDelivery_child_id());
													
													if(noteMdl!=null){
														SalesReportBean reportBean=new SalesReportBean();
														
														reportBean.setSalesNo(salMdl.getSales_number());
														reportBean.setId(detMdl.getId());
														
														reportBean.setItemId(noteMdl.getItem().getId());
														reportBean.setItemCode(noteMdl.getItem().getItem_code());
														reportBean.setItems(noteMdl.getItem().getName());
														
														reportBean.setDeliveryNoteId(detMdl.getDelivery_child_id());
														reportBean.setDeliveryNoteNo(new DeliveryNoteModel(detMdl.getDelivery_id()).getDeliveryNo());
														
														reportBean.setUnitId(noteMdl.getUnit().getId());
														reportBean.setUnit(noteMdl.getUnit().getSymbol());
														
														reportBean.setStockOut(noteMdl.getQunatity());
														reportBean.setQuantity(detMdl.getQunatity());
														
														reportList.add(reportBean);
													}
												}
											}else{
												DeliveryNoteDetailsModel noteMdl=deliveryDao.getDeliveryNoteDetailsModel(detMdl.getDelivery_child_id());
												
												if(noteMdl!=null){

													SalesReportBean reportBean=new SalesReportBean();
													
													reportBean.setSalesNo(salMdl.getSales_number());
													reportBean.setId(detMdl.getId());
													
													reportBean.setItemId(noteMdl.getItem().getId());
													reportBean.setItemCode(noteMdl.getItem().getItem_code());
													reportBean.setItems(noteMdl.getItem().getName());
													
													reportBean.setDeliveryNoteId(detMdl.getDelivery_child_id());
													reportBean.setDeliveryNoteNo(new DeliveryNoteModel(detMdl.getDelivery_id()).getDeliveryNo());
													
													reportBean.setUnitId(noteMdl.getUnit().getId());
													reportBean.setUnit(noteMdl.getUnit().getSymbol());
													
													reportBean.setStockOut(noteMdl.getQunatity());
													reportBean.setQuantity(detMdl.getQunatity());
													
													reportList.add(reportBean);
												}
											}
										}
									}
								}
								if(reportList.size()>0){
									
									Iterator it=reportList.iterator();
									int ct=1;
									while(it.hasNext()){
										SalesReportBean bean1=(SalesReportBean) it.next();
										double balance=bean1.getStockOut();
										
										Iterator it1=reportList.iterator();
										int intCt=1;
										while(it1.hasNext() && intCt<=ct){
											intCt=intCt+1;
											SalesReportBean bean2=(SalesReportBean) it1.next();
											if(bean1.getDeliveryNoteId()==bean2.getDeliveryNoteId() && bean1.getItemId()==bean2.getItemId()){
												balance=balance-bean2.getQuantity();
											}
										}
										
										table.addItem(new Object[]{ table.getItemIds().size()+1,
												bean1.getId(),
												"",
												bean1.getSalesNo(),
												bean1.getDeliveryNoteId(),
												bean1.getDeliveryNoteNo(),
												bean1.getItemId(),
												bean1.getItemCode(),
												bean1.getItems(),
												bean1.getUnitId(),
												bean1.getUnit(),
												bean1.getStockOut(),
												balance,
												bean1.getQuantity()
										},table.getItemIds().size()+1);
										
										ct=ct+1;
									}
								}
								table.setVisibleColumns(visibleColumns);
							}else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							
						}
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
			
			/*table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							popupContainer.removeAllComponents();
							SFormLayout form=new SFormLayout();
							form.addComponent(detailPopupContainer);
							form.addComponent(new SHTMLLabel(null,"<b>Sales Man : "+itm.getItemProperty(TBC_SALES_MAN).getValue().toString()+"</b>"));
							form.addComponent(new SHTMLLabel(null,"<b>Customer : "+itm.getItemProperty(TBC_CUSTOMER).getValue().toString()+"</b>"));
							form.addComponent(popTable);
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
							
							popTable.removeAllItems();
							popTable.setColumnFooter(POP_TBC_AMOUNT,
									asString(roundNumber(0)));
							popTable.setColumnFooter(POP_TBC_PAYMENT,
								asString(roundNumber(0)));
							popTable.setColumnFooter(POP_TBC_PENDING,
								asString(roundNumber(0)));
							
							String condition1="";
							if ((Integer) filterTypeRadio.getValue() == 1) {
								condition1 = " and payment_amount=amount ";
							} else if ((Integer) filterTypeRadio.getValue() == 2) {
								condition1 = " and payment_amount<amount ";
							}

							if ((Integer) statusRadioButton.getValue() == 0) {
								condition1 += " and active=true ";
							} else if ((Integer) statusRadioButton.getValue() == 1) {
								condition1 += " and active=false ";
							}

							List<Object> salesModelList =dao
									.getSalesManWiseSalesDetails(0, (Long) itm.getItemProperty(TBC_CUSTOMER_ID).getValue(), CommonUtil
											.getSQLDateFromUtilDate(fromDateField.getValue()),
											CommonUtil.getSQLDateFromUtilDate(toDateField
													.getValue()), toLong(officeComboField
													.getValue().toString()), condition1,
											getOrganizationID(),(Long) itm.getItemProperty(TBC_SALES_MAN_ID).getValue());
							Object[] row;
							SalesModel salesModel;
							double totalAmount=0;
							double totalPaid=0;
							double totalPending=0;
							for (int i = 0; i < salesModelList.size(); i++) {
								salesModel = (SalesModel) salesModelList.get(i);
								row = new Object[] {
										i + 1,salesModel.getSales_number() + "",
										salesModel.getDate().toString(),									
										roundNumber(salesModel.getAmount()),
										roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()),
										roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()))};
								
								popTable.addItem(row, salesModel.getId());
								totalAmount+=roundNumber(salesModel.getAmount());
								totalPaid+=roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment());
								totalPending+=roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()));
								
							}
							popTable.setColumnFooter(POP_TBC_AMOUNT,
										asString(roundNumber(totalAmount)));
							popTable.setColumnFooter(POP_TBC_PAYMENT,
									asString(roundNumber(totalPaid)));
							popTable.setColumnFooter(POP_TBC_PENDING,
									asString(roundNumber(totalPending)));
							
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			popTable.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (popTable.getValue() != null) {
//							Item itm = popTable.getItem(popTable.getValue());
//							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesModel sale=salDao.getSale((Long)popTable.getValue());
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
							detailPopupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							detailPopupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			final Action actionEdit = new Action(getPropertyName("edit"));
			
			popTable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (popTable.getValue() != null) {
							Item item = popTable.getItem(popTable.getValue());
							SalesNewUI sales = new SalesNewUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getSalesNumberList().setValue(
									(Long) popTable.getValue());
							sales.center();
							getUI().getCurrent().addWindow(sales);
							sales.addCloseListener(closeListener);
							popupContainer.removeAllComponents();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionEdit };
				}
			});*/


			officeComboField.setValue(getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	@SuppressWarnings({ "unchecked"})
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

	protected List<Object> getSalesReportList() {
		long custId = 0;
		long salesMan = 0;
		long itemSubGrp=0;

		if (salesManComboField.getValue() != null
				&& !salesManComboField.getValue().equals("")) {
			salesMan = toLong(salesManComboField.getValue().toString());
		}
		
		if (itemGrpComboField.getValue() != null && !itemGrpComboField.getValue().equals("")) {
			itemSubGrp = toLong(itemGrpComboField.getValue().toString());
		}
		
		List<Object> salesModelList = null;
		try {
			
			 salesModelList=exceptiondao.getSalesManWiseSalesDetails(itemSubGrp, 
					 CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
					 CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
					 toLong(officeComboField.getValue().toString()),
					 salesMan);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
	}
	
	private void loadItemSubGrp() {
		List itemList;
		try {
			itemList = itemSubGrpDao.getAllActiveItemSubGroupsNames(getOrganizationID());
			
			ItemSubGroupModel itmMdl = new ItemSubGroupModel();
			itmMdl.setId(0);
			itmMdl.setName(getPropertyName("all"));
			if (itemList == null) {
				itemList = new ArrayList<Object>();
			}
			itemList.add(0, itmMdl);
			
			itemGrpComboField.setContainerDataSource(SCollectionContainer.setList(itemList, "id"));
			itemGrpComboField.setItemCaptionPropertyId("name");
			itemGrpComboField.setValue((long)0);
			
		} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public Boolean isValid() {
		boolean ret = true;

		if (itemGrpComboField.getValue() == null || itemGrpComboField.getValue().equals("")) {
			setRequiredError(itemGrpComboField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(itemGrpComboField, null, false);
		
		if (salesManComboField.getValue() == null || salesManComboField.getValue().equals("")) {
			setRequiredError(salesManComboField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(salesManComboField, null, false);
		
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
