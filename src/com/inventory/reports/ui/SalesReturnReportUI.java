package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.PurchaseReportBean;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.bean.SalesReturnReportBean;
import com.inventory.reports.dao.SalesReturnReportDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesReturnUI;
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
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 13, 2013
 */
public class SalesReturnReportUI extends SparkLogic {

	private static final long serialVersionUID = 4527388475958727643L;

//	private String PROMPT_ALL = getPropertyName("all");

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField itemComboField;
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
	private long itemId;
	private long officeId;

	private Report report;

	LedgerDao ledDao;
	ItemDao itmDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALE_NO = "CreditNote No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";

	private STable table;

	private Object[] allColumns;
	private Object[] visibleColumns;
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

		ledDao = new LedgerDao();
		itmDao = new ItemDao();
		popupContainer = new SHorizontalLayout();
		allColumns = new String[] { TBC_SN, TBC_ID, TBC_CUSTOMER, TBC_AMOUNT};
		visibleColumns = new String[] { TBC_SN, TBC_CUSTOMER, TBC_AMOUNT};
		
		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleSubColumns = new String[] { TBC_SN, TBC_SALE_NO, TBC_DATE, TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		customerId = 0;
		itemId = 0;
		officeId = getOfficeID();
		report = new Report(getLoginID());

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

			List<Object> itemList = itmDao
					.getAllActiveItemsWithAppendingItemCode(getOfficeID());
			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName(getPropertyName("all"));
			if (itemList == null) {
				itemList = new ArrayList<Object>();
			}
			itemList.add(0, itemModel);

			itemComboField = new SComboField(getPropertyName("item"), 200,
					itemList, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemComboField);
			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			showButton = new SButton(getPropertyName("show"));
			generateConsolidatedButton = new SButton(getPropertyName("consolidated_report"));
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
			subTable.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("sales_return_no"), null, Align.CENTER);
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
			
			
			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
							table.setColumnFooter(TBC_AMOUNT, "0.0");
							customerId = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerComboField
										.getValue().toString());
							}
						}
					});

			itemComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					itemId = 0;
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					if (itemComboField.getValue() != null
							&& !itemComboField.getValue().toString()
									.equals("0")) {
						itemId = toLong(itemComboField.getValue().toString());
					}
				}
			});
			fromDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					officeId = toLong(officeComboField.getValue().toString());
					loadCustomerCombo();
					reloadItemCombo();
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						List<Object> reportList = new ArrayList<Object>();
						SalesReturnModel returnModel = null;
						SalesReturnInventoryDetailsModel detailsModel = null;
						SalesReturnReportBean bean = null;
						String items = "";

						List<Object> list = new SalesReturnReportDao()
								.getReturnDetails(
										customerId,
										itemId,
										officeId,
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						
						
						
						
						List<SalesReturnInventoryDetailsModel> detailsList;
						double amount;
						double paymentAmount;
						for (int i = 0; i < list.size(); i++) {
							noData = false;
							returnModel = (SalesReturnModel) list.get(i);
							detailsList = returnModel
									.getInventory_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								detailsModel = detailsList.get(k);
								if (k != 0) {
									items += " , ";
								}
								items += detailsModel.getItem().getName()
										/*+ "(GRV Stock : "
										+ detailsModel.getQuantity_saled()*/
										+ " , Returned to supplier : "
										+ detailsModel.getQunatity()
									/*	+ ", Waste Qty : "
										+ detailsModel.getWaste_quantity()*/
										+ ")"
										+ detailsModel.getUnit().getSymbol();
							}
							
							amount = returnModel.getAmount() / returnModel.getConversionRate();
							if(returnModel.getNetCurrencyId().getId() == getCurrencyID()){
								bean = new SalesReturnReportBean(CommonUtil
										.getUtilDateFromSQLDate(
												returnModel.getDate()).toString(),
										returnModel.getCustomer().getName(),
										returnModel.getOffice().getName(), items,
										returnModel
												.getAmount(), returnModel.getPaid_by_payment(),returnModel.getReturn_no()	);
								bean.setCurrency(getCurrencyDescription(getCurrencyID()));
								bean.setPaymentAmountCurrency(getCurrencyDescription(getCurrencyID()));			
							} else {
								amount = returnModel.getAmount() / returnModel.getConversionRate();
								paymentAmount = returnModel.getPaid_by_payment() / returnModel.getConversionRate();
								bean = new SalesReturnReportBean(CommonUtil
										.getUtilDateFromSQLDate(
												returnModel.getDate()).toString(),
										returnModel.getCustomer().getName(),
										returnModel.getOffice().getName(), items,
										roundNumber(amount),
										roundNumber(paymentAmount), 
										 returnModel.getReturn_no()	);	
								bean.setCurrency(getCurrencyDescription(getCurrencyID())+
										" ("+returnModel.getAmount()+" "+returnModel.getNetCurrencyId().getSymbol()+")");
								bean.setPaymentAmountCurrency(getCurrencyDescription(getCurrencyID())+
										" ("+returnModel.getPaid_by_payment()+" "+returnModel.getNetCurrencyId().getSymbol()+")");
							}

							
							reportList.add(bean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Sales_Return_Report");
							report.setReportFileName("SalesReturnReport");
//							report.setReportTitle("Sales Return Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_return_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("CREDIT_NOTE_LABEL", getPropertyName("sales_return_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (itemId != 0) {
								subHeader += getPropertyName("item")+" : "
										+ itemComboField
												.getItemCaption(itemComboField
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
							list.clear();

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

					try {

						Object[] row;
						List<Object> repList = new SalesReturnReportDao()
												.showReturnDetails(customerId,
																	itemId,
																	officeId,
																	CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																	CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));

						table.setVisibleColumns(allColumns);
						table.removeAllItems();
						table.setColumnFooter(TBC_AMOUNT, "0.0");
						double ttl = 0;
						if (repList != null && repList.size() > 0) {
							for (int i = 0; i < repList.size(); i++) {
								SalesReportBean srbean = (SalesReportBean) repList.get(i);
								
								row = new Object[] {
										i + 1,
										srbean.getCustomerId(),
										srbean.getCustomer(),
										roundNumber(srbean.getAmount())+" "+getCurrencyDescription(getCurrencyID())};
								table.addItem(row, i + 1);
								ttl += roundNumber(srbean.getAmount());
							}
							table.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));
						}
						else
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						table.setVisibleColumns(visibleColumns);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			
			generateConsolidatedButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						List<Object> reportList = new SalesReturnReportDao()
												.showReturnDetails(customerId,
																	itemId,
																	officeId,
																	CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																	CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));

						if (reportList.size() > 0) {
							for(Object obj : reportList){
								SalesReportBean bean = (SalesReportBean) obj;
								bean.setCurrency(getCurrencyDescription(getCurrencyID()));
							}
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Consolidated_3_Report");
							report.setReportFileName("ConsolidatedReport");
//							report.setReportTitle("Sales Return Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_return_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (itemId != 0) {
								subHeader += getPropertyName("item")+" : "
										+ itemComboField
												.getItemCaption(itemComboField
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
						}
						else
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						
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
							SalesReturnUI sales = new SalesReturnUI();
							sales.setCaption(getPropertyName("sales_return"));
							sales.getSalesOrderNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							popUp.setPopupVisible(false);
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
			
			
			subTable.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesReturnModel sale=new SalesReturnDao().getSalesReturnModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales_return")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_return"),sale.getReturn_no()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					//		form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_note_no() + ""));
							double amount = sale.getAmount() / sale.getConversionRate();
							double paymentAmount = sale.getPaid_by_payment() / sale.getConversionRate();
							if(sale.getNetCurrencyId().getId() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(sale.getAmount())+" "+getCurrencyDescription(getCurrencyID())));		
								form.addComponent(new SLabel(getPropertyName("paid_amount"), 
										roundNumber(sale.getPaid_by_payment())+" "+getCurrencyDescription(getCurrencyID())));		
								//form.addComponent(new SLabel(getPropertyName("paid_amount"),sale.getPaid_by_payment()+ ""));
							} else {
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())
										+" ("+roundNumber(sale.getAmount())+" "+getCurrencyDescription(sale.getNetCurrencyId().getId())+")"));	
								form.addComponent(new SLabel(getPropertyName("paid_amount"), 
										roundNumber(paymentAmount)+" "+getCurrencyDescription(getCurrencyID())
										+" ("+roundNumber(sale.getPaid_by_payment())+" "+getCurrencyDescription(sale.getNetCurrencyId().getId())+")"));	
							}
							
						//	form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
							
							SGridLayout grid = new SGridLayout(getPropertyName("details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
						//	grid.addComponent(new SLabel(null, getPropertyName("good_stock")), 2, 0);
						//	grid.addComponent(new SLabel(null, getPropertyName("GRV_stock")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("returned_qty")), 4, 0);
						//	grid.addComponent(new SLabel(null, getPropertyName("waste_quantity")),	5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")),	6, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),7, 0);
							grid.setSpacing(true);
							
							int i = 1;
							SalesReturnInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(SalesReturnInventoryDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
						//		grid.addComponent(new SLabel(null, invObj.getGood_stock() + ""), 2, i);
							//	grid.addComponent(new SLabel(null, invObj.getStock_quantity()+""), 3, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 4,	i);
							//	grid.addComponent(new SLabel(null, invObj.getWaste_quantity() + ""),5, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol() + ""),6, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																- invObj.getDiscount()
																+ invObj.getTaxAmount())+ " "+getCurrencyDescription(getCurrencyID())), 7, i);
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
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							Object[] row;
							SalesReturnModel returnModel = null;
							SalesReturnInventoryDetailsModel detailsModel = null;
							SalesReturnReportBean bean = null;
							String items = "";
							subTable.setVisibleColumns(allSubColumns);
							subTable.removeAllItems();
							subTable.setColumnFooter(TBC_AMOUNT, "0.0");
							double convQty;
							double ttl = 0;
							List<Object> repList=new SalesReturnReportDao()
														.getReturnDetails(
																id,
																itemId,
																officeId,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
							if (repList != null && repList.size() > 0) {
								List<SalesReturnInventoryDetailsModel> detailsList;
								double amount;
								for (int i = 0; i < repList.size(); i++) {
									returnModel = (SalesReturnModel) repList.get(i);
									detailsList = returnModel
											.getInventory_details_list();
									items = "";
									for (int k = 0; k < detailsList.size(); k++) {
										detailsModel = detailsList.get(k);
										if (k != 0) {
											items += ", ";
										}
										convQty = (detailsModel.getQunatity());
										items += detailsModel.getItem().getName()+" - "
										/*		+ "("+getPropertyName("good_stock")+" : "
												+ detailsModel.getGood_stock()/convQty
												+ " ,"+getPropertyName("GRV_stock")+" : "
												+ detailsModel.getStock_quantity()/convQty
												+ " , "*/+getPropertyName("returned_qty")+" : "
												+ detailsModel
														.getQuantity_in_basic_unit()/*
												+ ", "+getPropertyName("waste_quantity")+" : "
												+ detailsModel.getWaste_quantity()/convQty
												+ ")"*/
												+ detailsModel.getUnit()
														.getSymbol();
									}
									amount = returnModel.getAmount() / returnModel.getConversionRate();
									if(returnModel.getNetCurrencyId().getId() == getCurrencyID()){
										row = new Object[] {
												i + 1,
												returnModel.getId(),
												returnModel.getReturn_no() + "",
												CommonUtil
														.getUtilFromSQLDate(returnModel
																.getDate())
														+ "",
												returnModel.getCustomer().getName(),
												roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID()), items };
									} else {
										row = new Object[] {
												i + 1,
												returnModel.getId(),
												returnModel.getReturn_no() + "",
												CommonUtil
														.getUtilFromSQLDate(returnModel
																.getDate())
														+ "",
												returnModel.getCustomer().getName(),
												roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())+
												"("+returnModel.getAmount()+" "+getCurrencyDescription(returnModel.getNetCurrencyId().getId())+")", items };
									}
									
									subTable.addItem(row, i + 1);
									ttl += amount;
								}
								subTable.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));
							}
							else
								SNotification.show(getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);

							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Sales Return Details",725), closeBtn), subTable));

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
	protected void reloadItemCombo() {
		try {

			List itemList = itmDao.getAllActiveItemsWithAppendingItemCode(
					officeId, 0, 0);

			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName(getPropertyName("all"));
			if (itemList == null)
				itemList = new ArrayList();

			itemList.add(0, itemModel);

			itemComboField.setInputPrompt(getPropertyName("all"));

			container = SCollectionContainer.setList(itemList, "id");
			itemComboField.setContainerDataSource(container);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(0);

			table.removeAllItems();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadCustomerCombo() {
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
