package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
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
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 10, 2014
 */

@SuppressWarnings("serial")
public class SalesManWiseCollectionReportUI extends SparkLogic {

	
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField salesManComboField;
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
	private SalesReportDao dao;

	LedgerDao ledDao;

	SRadioButton filterTypeRadio;
	SRadioButton statusRadioButton;

	private Report report;

	SalesDao salDao;
	SHorizontalLayout popupContainer;
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALES_MAN = "Sales Man";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_DATE = "Date";
	static String TBC_PAYMENT_DATE = "Payment Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_PAID = "Paid Amount";
	static String TBC_STATUS = "Status";

	private STable table;

	private Object[] allColumns;
	private Object[] visibleColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;

	@Override
	public SPanel getGUI() {
		dao=new SalesReportDao();

		allColumns = new String[] { TBC_SN, TBC_ID,TBC_SALES_MAN, TBC_SALE_NO, TBC_DATE,TBC_PAYMENT_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_PAID ,TBC_STATUS };
		visibleColumns = new String[] { TBC_SN, TBC_SALES_MAN,TBC_SALE_NO, TBC_DATE,TBC_PAYMENT_DATE,
				TBC_CUSTOMER, TBC_AMOUNT,  TBC_PAID ,TBC_STATUS };
		popupContainer=new SHorizontalLayout();
		ledDao = new LedgerDao();
		salDao = new SalesDao();

		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(1180, 420);
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
					250, Arrays.asList(new KeyValue(0,getPropertyName("all")),new KeyValue(1,getPropertyName("paid")),new KeyValue(2,getPropertyName("not_paid"))), "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(0);

			statusRadioButton = new SRadioButton(getPropertyName("status"),
					250, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(
							1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);

			mainFormLayout.addComponent(filterTypeRadio);

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);
			
			
			
			salesManComboField=new SComboField(getPropertyName("sales_man"),200);
			salesManComboField.setInputPrompt(getPropertyName("all"));
			loadSalesMan();
			mainFormLayout.addComponent(salesManComboField);
			
			customerComboField = new SComboField(getPropertyName("customer"),
					200);
			loadCustomers(getOfficeID(),(long)0);
			mainFormLayout.addComponent(customerComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_bill_no"), 200, null, "id",
					"comments", false, getPropertyName("all"));
//			mainFormLayout.addComponent(salesNoComboField);

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

			table = new STable(null, 700, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_SALES_MAN, String.class, null,
					getPropertyName("sales_man"), null, Align.CENTER);
			table.addContainerProperty(TBC_SALE_NO, String.class, null,
					getPropertyName("sales_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_PAYMENT_DATE, String.class, null,
					getPropertyName("payment_date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PAID, Double.class, null,
					getPropertyName("paid_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_STATUS, String.class, null,
					getPropertyName("status"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_PAYMENT_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_SALES_MAN, 1f);
			table.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_PAID, 1f);
			table.setColumnExpandRatio(TBC_STATUS, 1f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainPanel.setContent(mainLay);

			salesNoComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					table.setColumnFooter(TBC_PAID, "0.0");
				}
			});
			
			salesManComboField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
//					loadBillNo(customerId, toLong(officeComboField
//							.getValue().toString()));
					if(salesManComboField.getValue()!=null)
						loadCustomers((Long)officeComboField.getValue(), (Long)salesManComboField.getValue());
					else
						loadCustomers((Long)officeComboField.getValue(), (long)0);
				}
			});

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerComboField
										.getValue().toString());
								
							}
//							loadBillNo(customerId, toLong(officeComboField
//									.getValue().toString()));
						}
					});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadSalesMan();
					loadCustomers(toLong(officeComboField.getValue()
							.toString()),0);
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
						
						if(salesModelList!=null&&salesModelList.size()>0){
							noData = false;
						List<SalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < salesModelList.size(); i++) {
							
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
										+ " ( "+getPropertyName("quantity")+" : "
										+ inventoryDetailsModel.getQunatity()
										+ " , "+getPropertyName("rate")+" : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}
							if(salesModel.getResponsible_employee()!=0)
								salesPerson = userDao.getUserFromLogin(salesModel.getResponsible_employee()).getFirst_name();
							else
								salesPerson="";
							reportBean = new SalesReportBean(CommonUtil.formatDateToCommonFormat(CommonUtil
									.getUtilFromSQLDate(salesModel.getDate())),
									salesModel.getCustomer().getName(), String
											.valueOf(salesModel
													.getSales_number()),
									salesModel.getOffice().getName(), items,
									salesModel.getAmount(), salesPerson,
									salesModel.getPayment_amount());
							
							if(salesModel.getPayment_amount()!=0){
								reportBean.setPaymentDate(CommonUtil.formatDateToCommonFormat(salesModel.getDate()));
							}else{
								reportBean.setPaymentDate(dao.getPaymentDateOfSales(salesModel.getId()));
							}
							
							
							if(salesModel.getPayment_done()=='Y'||salesModel.getPayment_amount()!=0||salesModel.getPaid_by_payment()!=0){
								reportBean.setStatus(getPropertyName("paid"));
								if(salesModel.getPaid_by_payment()!=0)
									reportBean.setPayment_amt(salesModel.getPaid_by_payment()+salesModel.getPayment_amount());
								else
									reportBean.setPayment_amt(salesModel.getAmount());
							}
							else{
								reportBean.setStatus(getPropertyName("not_paid"));
								reportBean.setPayment_amt(0);
							}
							reportList.add(reportBean);

						}
						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("SalesManWiseCollectionReport");
							report.setReportFileName("SalesManWiseCollectionReport");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_man_wise_collection_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("BILL_DATE_LABEL", getPropertyName("bill_date"));
							map.put("PAYMENT_DATE_LABEL", getPropertyName("payment_date"));
							map.put("SALES_MAN_LABEL", getPropertyName("sales_man"));
							map.put("INVOICE_NO_LABEL", getPropertyName("invoice_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("PAID_AMOUNT_LABEL", getPropertyName("amount_paid"));
							map.put("BALANCE_AMOUNT_LABEL", getPropertyName("balance_amount"));
							map.put("STATUS_LABEL", getPropertyName("ststus"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (salesNoComboField.getValue() != null
									&& !salesNoComboField.getValue().toString()
											.equals("0")) {
								subHeader += getPropertyName("sales_no")+" : "
										+ salesNoComboField
												.getItemCaption(salesNoComboField
														.getValue());
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

				@Override
				public void buttonClick(ClickEvent event) {
					SalesModel salesModel = null;
					SalesInventoryDetailsModel inventoryDetailsModel = null;
					Object[] row;

					List repList = getSalesReportList();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					table.setColumnFooter(TBC_PAID, "0.0");
					String status="";
					double paidAmount=0; 
					double ttl = 0;
					double paidTtl = 0;
					if (repList != null && repList.size() > 0) {
						List<SalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < repList.size(); i++) {
							salesModel = (SalesModel) repList.get(i);

							try {
								if(salesModel.getPayment_done()=='Y'||salesModel.getPayment_amount()!=0||salesModel.getPaid_by_payment()!=0){
									status=getPropertyName("paid");
									if(salesModel.getPaid_by_payment()!=0)
										paidAmount=salesModel.getPaid_by_payment()+salesModel.getPayment_amount();
									else
										paidAmount=salesModel.getAmount();
								}
								else{
									status=getPropertyName("not_paid");
									paidAmount=0;
								}
								String dateString="";
								String salesPerson = "";
								if(salesModel.getResponsible_employee()!=0)
									salesPerson = userDao.getUserFromLogin(salesModel.getResponsible_employee()).getFirst_name();
								else
									salesPerson="";
								if(salesModel.getPayment_amount()!=0){
									dateString=CommonUtil.formatDateToCommonFormat(salesModel.getDate());
								}else{
									dateString=dao.getPaymentDateOfSales(salesModel.getId());
								}
								
								row = new Object[] {
										i + 1,
										salesModel.getId(),salesPerson,
										salesModel.getSales_number() + "",CommonUtil.formatDateToCommonDateTimeFormat(
										CommonUtil.getUtilFromSQLDate(salesModel
												.getDate())),dateString,
										salesModel.getCustomer().getName(),
										salesModel.getAmount(),paidAmount, status };
								
								table.addItem(row, i + 1);
								ttl += salesModel.getAmount();
								paidTtl += paidAmount;
							} catch (Exception e) {
							}

						}
						table.setColumnFooter(TBC_AMOUNT,
								asString(roundNumber(ttl)));
						table.setColumnFooter(TBC_PAID,
								asString(roundNumber(paidTtl)));

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
							SalesNewUI sales = new SalesNewUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getSalesNumberList().setValue(
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
							SalesModel sale=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("sales_man"),itm.getItemProperty(TBC_SALES_MAN).getValue().toString()));
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

	private void loadCustomers(long officeID, long salesMan) {
		List<Object> customerList;
		try {
			if (salesMan != 0)
				customerList = ledDao.getAllCustomersUnderSalesMan(getOfficeID(),salesMan);
			else
				customerList = ledDao.getAllCustomers(getOfficeID());

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

	private void loadSalesMan() {
		try {

			List saList = new ArrayList();
			saList.add(0, new UserModel(0,getPropertyName("all")));
			saList.addAll(new SalesManMapDao().getUsers(
					(Long) officeComboField.getValue(), 0));
			SCollectionContainer con=SCollectionContainer.setList(saList, "id");
			salesManComboField.setContainerDataSource(con);
			salesManComboField.setItemCaptionPropertyId("first_name");
			salesManComboField.setValue(null);
			
		} catch (Exception e) {
		}

	}

	protected List<Object> getSalesReportList() {
		long salesNo = 0;
		long custId = 0;
		long salesMan = 0;
		

		if (salesNoComboField.getValue() != null
				&& !salesNoComboField.getValue().equals("")
				&& !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}
		if (salesManComboField.getValue() != null
				&& !salesManComboField.getValue().equals("")) {
			salesMan = toLong(salesManComboField.getValue().toString());
		}
		if (customerComboField.getValue() != null
				&& !customerComboField.getValue().equals("")) {
			custId = toLong(customerComboField.getValue().toString());
		}
		
		List idStringList=new ArrayList();
		try {
			idStringList = dao.getSalesNoFromPayment(CommonUtil
					.getSQLDateFromUtilDate(fromDateField.getValue()),CommonUtil.getSQLDateFromUtilDate(toDateField
							.getValue()), toLong(officeComboField
									.getValue().toString()),custId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			
			condition1 = " and (payment_done='Y' or payment_amount!=0 or paid_by_payment!=0)";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and payment_done='N' and payment_amount=0 and paid_by_payment=0";
		}

		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and active=false ";
		}

		List<Object> salesModelList = null;
		
		try {
			salesModelList = dao
					.getSalesManWiseCollectionDetails(salesNo, custId,  toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID(),salesMan,idStringList,(Integer) filterTypeRadio.getValue(),CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
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
