package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hotel.config.dao.TableDao;
import com.hotel.config.model.TableModel;
import com.hotel.service.dao.HotelSalesDao;
import com.hotel.service.model.HotelSalesInventoryDetailsModel;
import com.hotel.service.model.HotelSalesModel;
import com.hotel.service.ui.HotelSalesUI;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
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
 *         WebSpark.
 * 
 *         Aug 12, 2013
 */
public class HotelSalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -7310717636742098456L;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField tableComboField;
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

	private long tableId;

	private UserManagementDao userDao;

	TableDao ledDao;

	SRadioButton filterTypeRadio;

	private Report report;

	HotelSalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_TABLE = "Table No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";


	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer;
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;
	
	TableDao tabDao;
	
	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_SALE_NO, TBC_DATE,TBC_TABLE, TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleSubColumns = new String[] { TBC_SN, TBC_SALE_NO, TBC_DATE,TBC_TABLE, TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		
		ledDao = new TableDao();
		salDao = new HotelSalesDao();
		popupContainer = new SHorizontalLayout();
		
		tabDao=new TableDao();
		tableId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(1200, 380);
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
			filterTypeRadio.setVisible(false);


			mainFormLayout.addComponent(filterTypeRadio);


			tableComboField = new SComboField(getPropertyName("Table"),
					200, null, "id", "tableNo", false, getPropertyName("all"));
			loadCustomerCombo(getOfficeID());
			mainFormLayout.addComponent(tableComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_bill_no"), 200, null, "id",
					"comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(salesNoComboField);

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


			subTable = new STable(null, 750, 250);
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("sales_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_TABLE, String.class, null,getPropertyName("Table No"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);
			
			subTable.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			subTable.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
			subTable.setColumnExpandRatio(TBC_TABLE, 0.8f);
			subTable.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			subTable.setColumnExpandRatio(TBC_AMOUNT, 1);
			subTable.setColumnExpandRatio(TBC_ITEMS, 2f);
			
			subTable.setVisibleColumns(visibleSubColumns);
			subTable.setSelectable(true);

			subTable.setFooterVisible(true);
			subTable.setColumnFooter(TBC_DATE, getPropertyName("total"));
			
			mainLay.addComponent(popHor);
			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(subTable);
			
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
					subTable.removeAllItems();
					subTable.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			
			tableComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					tableId = 0;
					if (tableComboField.getValue() != null
							&& !tableComboField.getValue()
									.toString().equals("0")) {
						tableId = toLong(tableComboField
								.getValue().toString());
					}
					loadBillNo(tableId, toLong(officeComboField
							.getValue().toString()));
				}
			});

			
			filterTypeRadio.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(tableId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			

			
			fromDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(tableId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(tableId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
					loadBillNo(tableId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						HotelSalesModel hotelSalesModel = null;
						HotelSalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";
						String salesPerson = "";
						
						long salesNo = 0;
						long tblId = 0;

						if (salesNoComboField.getValue() != null && !salesNoComboField.getValue().equals("") && !salesNoComboField.getValue().toString().equals("0")) {
							salesNo = toLong(salesNoComboField.getValue().toString());
						}
						if (tableComboField.getValue() != null && !tableComboField.getValue().equals("")) {
							tblId = toLong(tableComboField.getValue().toString());
						}

						List<Object> reportList = new ArrayList<Object>();
						List<Object> hotelSalesModelList = salDao.getSalesDetailsReport(salesNo,tblId,CommonUtil.getSQLDateFromUtilDate(fromDateField
								.getValue()),CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),getOfficeID());

						List<HotelSalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < hotelSalesModelList.size(); i++) {
							noData = false;
							salesPerson = "";
							hotelSalesModel = (HotelSalesModel) hotelSalesModelList.get(i);
							detailsList = hotelSalesModel
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

							salesPerson = userDao.getUser(
									hotelSalesModel.getSales_person())
									.getFirst_name();

							reportBean = new SalesReportBean(CommonUtil
									.getUtilDateFromSQLDate(
											hotelSalesModel.getDate()).toString(),
									hotelSalesModel.getCustomer(), String
											.valueOf(hotelSalesModel
													.getSales_number()),
									hotelSalesModel.getOffice().getName(), items,
									hotelSalesModel.getAmount(), salesPerson,
									hotelSalesModel.getPayment_amount());
							
							String tableNo="";
							if(hotelSalesModel.getTableId()!=0){
								tableNo=tabDao.getTable(hotelSalesModel.getTableId()).getTableNo();
							}else
								tableNo="Take away";
							reportBean.setTableNo(tableNo);
							reportList.add(reportBean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("HotelSalesReport");
							report.setReportFileName("HotelSalesReport");
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
							map.put("TABLE_LABEL", getPropertyName("Table No"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							String subHeader = "";
							if (tableId != 0) {
								subHeader += getPropertyName("customer")+" : "+ tableComboField
												.getItemCaption(tableComboField.getValue()) + "\t";
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
							hotelSalesModelList.clear();

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

					try {

					boolean noData = true;
					HotelSalesModel hotelSalesModel = null;
					HotelSalesInventoryDetailsModel inventoryDetailsModel = null;
					SalesReportBean reportBean = null;
					String items = "";
					String salesPerson = "";
					
					long salesNo = 0;
					long tblId = 0;

					if (salesNoComboField.getValue() != null && !salesNoComboField.getValue().equals("") && !salesNoComboField.getValue().toString().equals("0")) {
						salesNo = toLong(salesNoComboField.getValue().toString());
					}
					if (tableComboField.getValue() != null && !tableComboField.getValue().equals("")) {
						tblId = toLong(tableComboField.getValue().toString());
					}

					List<Object> hotelSalesModelList = salDao.getSalesDetailsReport(salesNo,tblId,CommonUtil.getSQLDateFromUtilDate(fromDateField
							.getValue()),CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),getOfficeID());

					List<HotelSalesInventoryDetailsModel> detailsList;
					if(hotelSalesModelList!=null&&hotelSalesModelList.size()>0){
						subTable.setVisibleColumns(allSubColumns);
						subTable.removeAllItems();
						subTable.setColumnFooter(TBC_AMOUNT, "0.0");
						double ttl=0;
						
					for (int i = 0; i < hotelSalesModelList.size(); i++) {
						noData = false;
						salesPerson = "";
						hotelSalesModel = (HotelSalesModel) hotelSalesModelList.get(i);
						detailsList = hotelSalesModel
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

						salesPerson = userDao.getUser(
								hotelSalesModel.getSales_person())
								.getFirst_name();
						
						String tableNo="";
						if(hotelSalesModel.getTableId()!=0){
							tableNo=tabDao.getTable(hotelSalesModel.getTableId()).getTableNo();
						}else
							tableNo="Take away";
						
						row = new Object[] {
								i + 1,
								hotelSalesModel.getId(),
								hotelSalesModel.getSales_number() + "",
								CommonUtil.getUtilFromSQLDate(hotelSalesModel
										.getDate()) + "",tableNo,
										hotelSalesModel.getCustomer(),
										hotelSalesModel.getAmount(), items };
						subTable.addItem(row, i + 1);

						ttl += hotelSalesModel.getAmount();

					}
					subTable.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));
					

					} else {
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
					
					subTable.setVisibleColumns(visibleSubColumns);
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

			
			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
			subTable.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (subTable.getValue() != null) {
							Item item = subTable.getItem(subTable.getValue());
							HotelSalesUI sales = new HotelSalesUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getBillNoFiled().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
							HotelSalesModel sale=new HotelSalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer()));
							form.addComponent(new SLabel(getPropertyName("Table No"),itm.getItemProperty(TBC_TABLE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
							if (isShippingChargeEnable())
								form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
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
							HotelSalesInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(HotelSalesInventoryDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
								grid.addComponent(new SLabel(null, invObj.getDiscount_amount() + ""),5, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																	- invObj.getDiscount_amount() 
																	+ invObj.getTax_amount())+ ""), 6, i);
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
			
			
			officeComboField.setValue(getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	
	protected List<Object> getSalesReportList() {
		long salesNo = 0;
		long custId = 0;

		if (salesNoComboField.getValue() != null && !salesNoComboField.getValue().equals("") && !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}
		if (tableComboField.getValue() != null && !tableComboField.getValue().equals("")) {
			custId = toLong(tableComboField.getValue().toString());
		}
		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and payment_amount=amount ";
		} 
		else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and payment_amount<amount ";
		}
		

		List<Object> HotelSalesModelList = null;
		try {
			HotelSalesModelList = new SalesReportDao()
					.getSalesDetails(salesNo, custId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HotelSalesModelList;
	}
	
	
	
	private void loadBillNo(long tableId, long officeId) {
		List<Object> salesList = null;
		try {

			String condition1 = "";

			if ((Integer) filterTypeRadio.getValue() == 1) {
				condition1 = " and payment_amount=amount ";
			} else if ((Integer) filterTypeRadio.getValue() == 2) {
				condition1 = " and payment_amount<amount ";
			}

			if (tableId != 0) {
				salesList = salDao.getAllSalesNumbersOfTable(officeId,
						tableId, CommonUtil
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

			HotelSalesModel HotelSalesModel = new HotelSalesModel();
			HotelSalesModel.setId(0);
			HotelSalesModel.setComments(getPropertyName("all"));
			if (salesList == null) {
				salesList = new ArrayList<Object>();
			}
			salesList.add(0, HotelSalesModel);
			container = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(container);
			salesNoComboField.setItemCaptionPropertyId("comments");
			salesNoComboField.setValue(0);

			subTable.removeAllItems();
			subTable.setColumnFooter(TBC_AMOUNT, "0.0");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			custList = ledDao.getAllTables(officeId);
			TableModel ledgerModel = new TableModel();
			ledgerModel.setId(0);
			ledgerModel.setTableNo(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			tableComboField.setContainerDataSource(custContainer);
			tableComboField.setItemCaptionPropertyId("tableNo");
			tableComboField.setValue(0);
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
