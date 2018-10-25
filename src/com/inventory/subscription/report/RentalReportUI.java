package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.subscription.dao.RentalReportDao;
import com.inventory.subscription.dao.RentalTransactionNewDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.RentalTransactionDetailsModel;
import com.inventory.subscription.model.RentalTransactionModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.ui.RentalTransactionNewUI;
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
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 27, 2015
 */

@SuppressWarnings("serial")
public class RentalReportUI extends SparkLogic {

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField salesNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;
	SRadioButton accountRadio;
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
	RentalReportDao dao;

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

	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer;
	
	@Override
	public SPanel getGUI() {

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_SALE_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleColumns = new String[] { TBC_SN, TBC_SALE_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		ledDao = new LedgerDao();
		salDao = new SalesDao();
		popupContainer = new SHorizontalLayout();
		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();
		dao=new RentalReportDao();
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
			accountRadio = new SRadioButton(getPropertyName("subscriber"), 200, SConstants.accountList, "key", "value");
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

//			mainFormLayout.addComponent(filterTypeRadio);

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
			customerComboField = new SComboField(getPropertyName("customer"),200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(accountRadio);
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

			table = new STable(null, 670, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_SALE_NO, String.class, null,
					getPropertyName("sales_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_ITEMS, String.class, null,
					getPropertyName("items"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
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
			officeComboField.setValue(getOfficeID());
			accountRadio.setValue((long)1);
			loadSubscriberIncome(0,(Long)officeComboField.getValue());
			
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(accountRadio.getValue()!=null){
							if(toLong(accountRadio.getValue().toString())==1){
								loadSubscriberIncome(0,(Long)officeComboField.getValue());
								customerComboField.setCaption(getPropertyName("customer"));
								table.setColumnHeader(TBC_CUSTOMER, getPropertyName("customer"));
							}
							else{
								loadSubscriberTransportation(0,(Long)officeComboField.getValue());
								customerComboField.setCaption(getPropertyName("transportation"));
								table.setColumnHeader(TBC_CUSTOMER, getPropertyName("transportation"));
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					accountRadio.setValue(null);
					accountRadio.setValue((long)1);
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesReportBean reportBean = null;
						String salesPerson = "";

						List<Object> reportList = new ArrayList<Object>();

						List resultList=dao.getRentalReport((Long)customerComboField.getValue(), 
								(Long)officeComboField.getValue(),
								CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()), 
								(Long)accountRadio.getValue());
						
						if(resultList.size()>0){
							table.setVisibleColumns(allColumns);
							Iterator lsitr=resultList.iterator();
							double ttl = 0;
							while (lsitr.hasNext()) {
								String items="";
								RentalTransactionModel mdl = (RentalTransactionModel) lsitr.next();
								Iterator tbitr=mdl.getInventory_details_list().iterator();
								while (tbitr.hasNext()) {
									RentalTransactionDetailsModel det = (RentalTransactionDetailsModel) tbitr.next();
									items+=det.getRental().getName()+" ("+getPropertyName("rate")+"- "+det.getUnit_price()+", "
											+getPropertyName("quantity")+"- "+det.getQunatity()+")<br> ";
								}
								reportBean = new SalesReportBean(CommonUtil.getUtilDateFromSQLDate(mdl.getDate()).toString(),
																mdl.getCustomer().getName(), String.valueOf(mdl.getSales_number()),
																mdl.getOffice().getName(), 
																items,
																mdl.getAmount(), 
																salesPerson,
																mdl.getPayment_amount());
								
								reportList.add(reportBean);
								
							}
						}

						if (reportList.size()>0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Rental_Report");
							report.setReportFileName("Rental Report");
//							report.setReportTitle("Sales Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("rental_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("INVOICE_DATE_LABEL", getPropertyName("date"));
							map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("SALES_MAN_LABEL", getPropertyName("sales_man"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							
							
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
					
					try{
						
						List resultList=dao.getRentalReport((Long)customerComboField.getValue(), 
															(Long)officeComboField.getValue(),
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()), 
															(Long)accountRadio.getValue());
						table.setColumnFooter(TBC_AMOUNT, "0.0");
						table.removeAllItems();
						if(resultList.size()>0){
							table.setVisibleColumns(allColumns);
							Iterator lsitr=resultList.iterator();
							double ttl = 0;
							while (lsitr.hasNext()) {
								String items="";
								RentalTransactionModel mdl = (RentalTransactionModel) lsitr.next();
								Iterator tbitr=mdl.getInventory_details_list().iterator();
								while (tbitr.hasNext()) {
									RentalTransactionDetailsModel det = (RentalTransactionDetailsModel) tbitr.next();
									items+=det.getRental().getName()+" ("+getPropertyName("rate")+"- "+det.getUnit_price()+", "
											+getPropertyName("quantity")+"- "+det.getQunatity()+"), ";
								}
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										mdl.getId(),
										mdl.getSales_number()+"",
										mdl.getDate().toString(),
										mdl.getCustomer().getName(),
										mdl.getAmount(),
										items},table.getItemIds().size()+1);
								ttl += mdl.getAmount();
								
							}
							table.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));
						}
						else
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						table.setVisibleColumns(visibleColumns);
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
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
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							RentalTransactionNewUI sales = new RentalTransactionNewUI();
							sales.setCaption(getPropertyName("rental"));
							sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
							RentalTransactionModel sale=new RentalTransactionNewDao().getRentalTransactionModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
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
							grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
							grid.setSpacing(true);
							
							int i = 1;
							RentalTransactionDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(RentalTransactionDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getRental().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
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


		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	protected List<Object> getSalesReportList() {
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

		String condition1 = "";

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

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesDetails(salesNo, custId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadBillNo(long customerId, long officeId,long account) {
		List<Object> salesList =new ArrayList();
		try {
			salesList.add(0, new RentalTransactionModel(0, getPropertyName("all")));
//			salesList.addAll(dao.getAllRentalTransactions(	customerId, 
//															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
//															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()), 
//															officeId, account));
			SCollectionContainer bic = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(bic);
			salesNoComboField.setItemCaptionPropertyId("comments");
			salesNoComboField.setValue(0);
			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
		} 
		catch (Exception e) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberIncome(long id, long office){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.add(0, new SubscriptionCreationModel(0, getPropertyName("all")));
			list.addAll(dao.getAllIncomeSubscriptions(office));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			customerComboField.setContainerDataSource(bic);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberTransportation(long id, long office){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.add(0, new SubscriptionCreationModel(0, getPropertyName("all")));
			list.addAll(dao.getAllTransportationSubscriptions(office));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			customerComboField.setContainerDataSource(bic);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
