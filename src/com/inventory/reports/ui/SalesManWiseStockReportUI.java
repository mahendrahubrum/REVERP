package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.SalesManWiseReportBean;
import com.inventory.reports.dao.SalesManWiseReportDao;
import com.inventory.sales.dao.SalesDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * June 26, 2015
 */

@SuppressWarnings("serial")
public class SalesManWiseStockReportUI extends SparkLogic {

	
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField salesManComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	SPopupView popUp, subPopUp;
	SNativeButton closeBtn;

	SHorizontalLayout popupContainer;
	SHorizontalLayout popHor;
	LedgerDao ledDao;
	
	SalesManWiseReportDao dao;

	private Report report;

	SalesDao salDao;
	List<Object> reportList;

	static String TBC_SN = "SN";
	static String TBC_ITEM = "Item";
	static String TBC_SALES_MAN = "Sales Man";
	static String TBC_UNIT = "Unit";
	static String TBC_PURCHASE = "Purchase";
	static String TBC_SALES = "Sales";
	static String TBC_PURCHASE_RETURN = "Purchase Return";
	static String TBC_SALES_RETURN = "Sales Return";
	static String TBC_BALANCE = "Balance";

	private STable table/*,subtable*/;

	private Object[] allColumns;
	private Object[] visibleColumns;
	
//	private Object[] allSubColumns;
//	private Object[] visibleSubColumns;

	Calendar toCalendar;
	Calendar fromCalendar;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		popHor=new SHorizontalLayout();
		reportList = new ArrayList<Object>();
		
		dao=new SalesManWiseReportDao();
		allColumns = new Object[] {TBC_SN ,TBC_ITEM ,TBC_UNIT, TBC_PURCHASE, TBC_PURCHASE_RETURN , TBC_SALES , TBC_SALES_RETURN, TBC_BALANCE};
		visibleColumns = new Object[] {TBC_SN  ,TBC_ITEM, TBC_UNIT,TBC_PURCHASE,  TBC_PURCHASE_RETURN ,TBC_SALES , TBC_SALES_RETURN, TBC_BALANCE};
		
//		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_SALES_MAN, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_ITEMS ,TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
//		visibleSubColumns = new String[] { TBC_SN, TBC_SALES_MAN, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_ITEMS,TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };

		
		closeBtn = new SNativeButton("X");
		ledDao = new LedgerDao();
		salDao = new SalesDao();

		report = new Report(getLoginID());
//		userDao = new UserManagementDao();

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

			salesManComboField=new SComboField(getPropertyName("sales_man"),200);
			salesManComboField.setInputPrompt(getPropertyName("select"));
			loadSalesMan();
			
			mainFormLayout.addComponent(salesManComboField);
			
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
//			subtable = new STable(null, 900, 250);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ITEM, String.class, null, TBC_ITEM, null,Align.LEFT);
			table.addContainerProperty(TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
			table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
			table.addContainerProperty(TBC_PURCHASE, Double.class, null,getPropertyName("purchase_quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_PURCHASE_RETURN, Double.class, null,getPropertyName("purchase_return_quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_SALES, Double.class, null,getPropertyName("sales_quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_SALES_RETURN, Double.class, null,getPropertyName("sales_return_quantity"), null, Align.CENTER);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.CENTER);
			
			
//			subtable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
//			subtable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
//			subtable.addContainerProperty(TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
//			subtable.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("invoice_no"), null, Align.LEFT);
//			subtable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
//			subtable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
//			subtable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);
//			subtable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
//			subtable.addContainerProperty(TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
//			subtable.addContainerProperty(TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			

			table.setColumnExpandRatio(TBC_ITEM, 1.5f);
			table.setColumnExpandRatio(TBC_UNIT, 0.5f);
			table.setColumnExpandRatio(TBC_PURCHASE, 1f);
			table.setColumnExpandRatio(TBC_PURCHASE_RETURN, 1.5f);
			table.setColumnExpandRatio(TBC_SALES, 1f);
			table.setColumnExpandRatio(TBC_SALES_RETURN, 1.5f);
			table.setColumnExpandRatio(TBC_BALANCE, 1f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);
			table.setFooterVisible(true);
//			table.setColumnFooter(TBC_ITEM, getPropertyName("total"));
			
//			subtable.setColumnExpandRatio(TBC_SALES_MAN, 1f);
//			subtable.setColumnExpandRatio(TBC_SALE_NO, 0.45f);
//			subtable.setColumnExpandRatio(TBC_DATE, 0.5f);
//			subtable.setColumnExpandRatio(TBC_CUSTOMER, 1f);
//			subtable.setColumnExpandRatio(TBC_ITEMS, 1.5f);
//			subtable.setColumnExpandRatio(TBC_AMOUNT, 0.75f);
//			subtable.setColumnExpandRatio(TBC_PAYMENT, 0.75f);
//			subtable.setColumnExpandRatio(TBC_PENDING, 0.75f);
			
//			subtable.setVisibleColumns(visibleSubColumns);
//			subtable.setSelectable(true);
//			subtable.setFooterVisible(true);
//			subtable.setColumnFooter(TBC_SALES_MAN, getPropertyName("total"));

			mainLay.addComponent(popHor);
			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);
			

			mainPanel.setContent(mainLay);

			
			salesManComboField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					
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
				}
			});

			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

						if(isValid()) {
							
							long salseManId=0;
							SalesManWiseReportBean bean=null;
							SalesManWiseReportBean mainBean=new SalesManWiseReportBean(0,0,0,0,0);
							List itemList;
							List resList=new ArrayList();
							
							if(salesManComboField.getValue()!=null&&!salesManComboField.getValue().equals("")){
								salseManId=toLong(salesManComboField.getValue().toString());
							}
							try {
								reportList = dao.getSalesManWiseStockReport(
										(Long) officeComboField.getValue(), salseManId,
										CommonUtil.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));
							
							if(reportList!=null&&reportList.size()>0){
//								Iterator itr=reportList.iterator();
//								while (itr.hasNext()) {
//									mainBean=new SalesManWiseReportBean(0,0,0,0,0);
//									itemList=(List) itr.next();
//									Iterator iter=itemList.iterator();
//									while (iter.hasNext()) {
//										bean=(SalesManWiseReportBean) iter.next();
//										if(bean.getType()==1){
//											mainBean.setPurchaseQuantity(bean.getPurchaseQuantity());
//										}
//										else if(bean.getType()==2){
//											mainBean.setPurchaseReturnQuantity(bean.getPurchaseReturnQuantity());
//										}
//										else if(bean.getType()==3){
//											mainBean.setSalesQuantity(bean.getSalesQuantity());
//										}
//										else{
//											mainBean.setSalesReturnQuantity(bean.getSalesReturnQuantity());
//										}
//									}
//									
//									if(bean!=null){
//									mainBean.setId(bean.getId());
//									mainBean.setUnit(bean.getUnit());
//									mainBean.setSales_man(bean.getSales_man());
//									mainBean.setItem(bean.getItem());
//									mainBean.setBalance(mainBean.getPurchaseQuantity()+mainBean.getSalesReturnQuantity()
//											-mainBean.getPurchaseReturnQuantity()-mainBean.getSalesQuantity());
//									resList.add(mainBean);
//									bean=null;
//									}
//								}
//							}
//							if(resList.size()>0){
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("SalesManWiseStockReport");
							report.setReportFileName("SalesManWiseStockReport");
							
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("PURCHASE_QTY_LABEL", getPropertyName("purchase_quantity"));
							map.put("PURCHASE_RETURN_QTY_LABEL", getPropertyName("purchase_return_quantity"));
							map.put("SALES_QTY_LABEL", getPropertyName("sales_quantity"));
							map.put("SALES_RETURN_QTY_LABEL", getPropertyName("sales_return_quantity"));
							map.put("BALANCE_LABEL", getPropertyName("balance"));
							
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

//							reportList.clear();
//							resList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
							} catch (Exception e) {
								e.printStackTrace();
							}
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
						
						reportList.clear();
						table.removeAllItems();
						
						long salseManId=0;
						int slNo=1;
						SalesManWiseReportBean mainBean=null;
						
						
						if(salesManComboField.getValue()!=null&&!salesManComboField.getValue().equals("")){
							salseManId=toLong(salesManComboField.getValue().toString());
						}
						try {
							reportList = dao.getSalesManWiseStockReport(
									(Long) officeComboField.getValue(), salseManId,
									CommonUtil.getSQLDateFromUtilDate(fromDateField
											.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()));
						
						
						if(reportList!=null&&reportList.size()>0){
							Iterator itr=reportList.iterator();
							while (itr.hasNext()) {
								mainBean=(SalesManWiseReportBean) itr.next();
									table.addItem(new Object[]{slNo,mainBean.getItem(),mainBean.getUnit(),mainBean.getPurchaseQuantity(),
											mainBean.getPurchaseReturnQuantity(),mainBean.getSalesQuantity(),mainBean.getSalesReturnQuantity(),mainBean.getBalance()}
										, table.getItemIds().size()+1);
									slNo++;
								}
								
						}else
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
//			showButton.addClickListener(new ClickListener() {
//				
//				@Override
//				public void buttonClick(ClickEvent event) {
//					
//					if(isValid()) {
//						
//						reportList.clear();
//						table.removeAllItems();
////						table.setColumnFooter(TBC_PURCHASE, "0.0");
////						table.setColumnFooter(TBC_PURCHASE_RETURN, "0.0");
////						table.setColumnFooter(TBC_SALES, "0.0");
////						table.setColumnFooter(TBC_SALES_RETURN, "0.0");
//						
//						long salseManId=0;
//						int slNo=1;
//						SalesManWiseReportBean bean=null;
//						SalesManWiseReportBean mainBean=new SalesManWiseReportBean(0,0,0,0,0);
//						
//						List itemList;
//						
//						if(salesManComboField.getValue()!=null&&!salesManComboField.getValue().equals("")){
//							salseManId=toLong(salesManComboField.getValue().toString());
//						}
//						try {
//							reportList = dao.getSalesManWiseStockReport(
//									(Long) officeComboField.getValue(), salseManId,
//									CommonUtil.getSQLDateFromUtilDate(fromDateField
//											.getValue()), CommonUtil
//											.getSQLDateFromUtilDate(toDateField
//													.getValue()));
//							
//							
//							if(reportList!=null&&reportList.size()>0){
//								Iterator itr=reportList.iterator();
//								while (itr.hasNext()) {
//									mainBean=new SalesManWiseReportBean(0,0,0,0,0);
//									itemList=(List) itr.next();
//									Iterator iter=itemList.iterator();
//									while (iter.hasNext()) {
//										bean=(SalesManWiseReportBean) iter.next();
//										if(bean.getType()==1){
//											mainBean.setPurchaseQuantity(bean.getPurchaseQuantity());
//										}
//										else if(bean.getType()==2){
//											mainBean.setPurchaseReturnQuantity(bean.getPurchaseReturnQuantity());
//										}
//										else if(bean.getType()==3){
//											mainBean.setSalesQuantity(bean.getSalesQuantity());
//										}
//										else{
//											mainBean.setSalesReturnQuantity(bean.getSalesReturnQuantity());
//										}
//									}
//									
//									if(bean!=null){
//										mainBean.setId(bean.getId());
//										mainBean.setUnit(bean.getUnit());
//										mainBean.setSales_man(bean.getSales_man());
//										mainBean.setItem(bean.getItem());
//										mainBean.setBalance(mainBean.getPurchaseQuantity()+mainBean.getSalesReturnQuantity()
//												-mainBean.getPurchaseReturnQuantity()-mainBean.getSalesQuantity());
//										table.addItem(new Object[]{slNo,mainBean.getItem(),mainBean.getUnit(),mainBean.getPurchaseQuantity(),
//												mainBean.getPurchaseReturnQuantity(),mainBean.getSalesQuantity(),mainBean.getSalesReturnQuantity(),mainBean.getBalance()}
//										, table.getItemIds().size()+1);
//										
////								purchase+=roundNumber(mainBean.getPurchaseQuantity());
////								purchaseReturn+=roundNumber(mainBean.getPurchaseReturnQuantity());
////								sales+=roundNumber(mainBean.getSalesQuantity());
////								salesReturn+=roundNumber(mainBean.getSalesReturnQuantity());
//										bean=null;
//										slNo++;
//									}
//								}
////							table.setColumnFooter(TBC_PURCHASE, roundNumber(purchase)+"");
////							table.setColumnFooter(TBC_PURCHASE_RETURN, roundNumber(purchaseReturn)+"");
////							table.setColumnFooter(TBC_SALES, roundNumber(sales)+"");
////							table.setColumnFooter(TBC_SALES_RETURN, roundNumber(salesReturn)+"");
//							}else
//								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
//							
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			});

			
//			table.addValueChangeListener(new ValueChangeListener() {
//				
//				@SuppressWarnings("rawtypes")
//				@Override
//				public void valueChange(ValueChangeEvent event) {
//					try{
//						if(table.getValue()!=null){
//							subtable.removeAllItems();
//							Item item=table.getItem(table.getValue());
//							long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
//							Date start=(Date)item.getItemProperty(TBC_FROM).getValue();
//							Date end=(Date)item.getItemProperty(TBC_TO).getValue();
//							List detialList=dao.getSalesMansSales(	id,
//																	(Long)officeComboField.getValue(),
//																	(Long)customerComboField.getValue(),
//																	CommonUtil.getSQLDateFromUtilDate(start),
//																	CommonUtil.getSQLDateFromUtilDate(end));
//							subtable.setVisibleColumns(allSubColumns);
//							if(detialList.size()>0){
//								Iterator itr=detialList.iterator();
//								while (itr.hasNext()) {
//									SalesModel mdl=(SalesModel)itr.next();
//									String items="";
//									Iterator it=mdl.getInventory_details_list().iterator();
//									String name=new UserManagementDao().getUserNameFromLoginID(mdl.getResponsible_person());
//									while (it.hasNext()) {
//										SalesInventoryDetailsModel det=(SalesInventoryDetailsModel)it.next();
//										items+=det.getItem().getName()+"(Quantity : "+roundNumber(det.getQunatity())+", Rate : "+roundNumber(det.getQunatity())+"), ";
//									}
//									subtable.addItem(new Object[]{
//											subtable.getItemIds().size()+1,
//											mdl.getId(),
//											name,
//											mdl.getSales_number()+"",
//											CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
//											mdl.getCustomer().getName(),
//											items,
//											roundNumber(mdl.getAmount()),
//											roundNumber(mdl.getPayment_amount()- mdl.getPaid_by_payment()),
//											roundNumber(mdl.getAmount()- mdl.getPayment_amount()- mdl.getPaid_by_payment()),
//											
//									},subtable.getItemIds().size()+1);
//								}
//							}
//							Iterator itr=subtable.getItemIds().iterator();
//							double amount=0,paid=0,bal=0;
//							while (itr.hasNext()) {
//								Item itm = subtable.getItem(itr.next());
//								amount+=roundNumber(toDouble(itm.getItemProperty(TBC_AMOUNT).getValue().toString()));
//								paid+=roundNumber(toDouble(itm.getItemProperty(TBC_PAYMENT).getValue().toString()));
//								bal+=roundNumber(toDouble(itm.getItemProperty(TBC_PENDING).getValue().toString()));
//							}
//							subtable.setColumnFooter(TBC_AMOUNT, roundNumber(amount)+"");
//							subtable.setColumnFooter(TBC_PAYMENT, roundNumber(paid)+"");
//							subtable.setColumnFooter(TBC_PENDING, roundNumber(bal)+"");
//							subtable.setVisibleColumns(visibleSubColumns);
//							popUp = new SPopupView("",new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(
//									null,"<h2><u style='margin-left: 40px;'>Sales Man Wise Sales",
//									875), closeBtn), subtable));
//							
//							popHor.addComponent(popUp);
//							popUp.setPopupVisible(true);
//							popUp.setHideOnMouseOut(false);
//						}
//					}
//					catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			});
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			
//			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
//			subtable.addActionHandler(new Handler() {
//				
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					try{
//						if (subtable.getValue() != null) {
//							Item item = subtable.getItem(subtable.getValue());
//							SalesNewUI sales = new SalesNewUI();
//							sales.setCaption(getPropertyName("sales"));
//							sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
//							sales.center();
//							popUp.setVisible(false);
//							getUI().getCurrent().addWindow(sales);
//							sales.addCloseListener(closeListener);
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
			
			
//			subtable.addValueChangeListener(new ValueChangeListener() {
//				
//				@SuppressWarnings("rawtypes")
//				@Override
//				public void valueChange(ValueChangeEvent event) {
//					try{
//						if (subtable.getValue() != null) {
//							Item itm = subtable.getItem(subtable.getValue());
//							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
//							SalesModel sale=new SalesDao().getSale(id);
//							SFormLayout form = new SFormLayout();
//							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
//							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
//							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
//							form.addComponent(new SLabel(getPropertyName("sales_man"),userDao.getUserFromLogin(sale.getResponsible_person()).getFirst_name()));
//							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
//							form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
//							if (isShippingChargeEnable())
//								form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
//							form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
//							form.addComponent(new SLabel(getPropertyName("amount_paid"),sale.getPayment_amount() + ""));
//							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
//							grid.setColumns(12);
//							grid.setRows(sale
//									.getInventory_details_list().size() + 3);
//
//							grid.addComponent(new SLabel(null, "#"), 0, 0);
//							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
//							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
//							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
//							grid.addComponent(new SLabel(null, getPropertyName("unit_price")), 4, 0);
//							grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
//							grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
//							grid.setSpacing(true);
//							
//							int i = 1;
//							SalesInventoryDetailsModel invObj;
//							Iterator itr = sale.getInventory_details_list().iterator();
//							while(itr.hasNext()){
//								invObj=(SalesInventoryDetailsModel)itr.next();
//								grid.addComponent(new SLabel(null, i + ""),	0, i);
//								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
//								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
//								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
//								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
//								grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
//								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
//																	- invObj.getDiscount_amount() 
//																	+ invObj.getTax_amount())+ ""), 6, i);
//								i++;
//							}
//							form.addComponent(grid);
//							form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
//							form.setStyleName("grid_max_limit");
//							popupContainer.removeAllComponents();
//							subPopUp = new SPopupView("", form);
//							popupContainer.addComponent(subPopUp);
//							subPopUp.setPopupVisible(true);
//							subPopUp.setHideOnMouseOut(false);
//						}
//						else
//							subPopUp.setPopupVisible(false);
//					}
//					catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			});

			
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
//			saList.add(0, new UserModel(0,getPropertyName("all")));
			saList.addAll(new SalesManMapDao().getUsers((Long) officeComboField.getValue(), 0));
			SCollectionContainer con=SCollectionContainer.setList(saList, "id");
			salesManComboField.setContainerDataSource(con);
			salesManComboField.setItemCaptionPropertyId("first_name");
			salesManComboField.setValue(null);
			
		} catch (Exception e) {
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
		
		if(salesManComboField.getValue()==null || salesManComboField.getValue().equals("")) {
			setRequiredError(salesManComboField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(salesManComboField, null, false);
		
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
