package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseReturnDao;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.reports.bean.PurchaseReturnReportBean;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.bean.SalesReturnReportBean;
import com.inventory.reports.dao.PurchaseReturnReportDao;
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
 *         Aug 12, 2013
 */
public class PurchaseReturnReportUI extends SparkLogic {

	private static final long serialVersionUID = -8351296419366914744L;

//	private static final String PROMPT_ALL = "---------------------ALL-------------------";

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
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
	private SCollectionContainer itemContainer;

	private long supplierid;
	private long itemId;
	private long officeId;

	private Report report;

	ItemDao itmDao;
	LedgerDao ledDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_RETURN_NO = "DebitNote No";
	static String TBC_DATE = "Date";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";
	SHorizontalLayout popupContainer;
	private STable table;

	private Object[] allColumns;
	private Object[] visibleColumns;
	
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

		popupContainer=new SHorizontalLayout();
		itmDao = new ItemDao();
		ledDao = new LedgerDao();
		supplierid = 0;
		itemId = 0;
		officeId = getOfficeID();
		report = new Report(getLoginID());

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_SUPPLIER, TBC_AMOUNT};
		visibleColumns = new String[] { TBC_SN, TBC_SUPPLIER, TBC_AMOUNT};
		
		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_RETURN_NO, TBC_DATE, TBC_SUPPLIER, TBC_AMOUNT, TBC_ITEMS };
		visibleSubColumns = new String[] { TBC_SN, TBC_RETURN_NO, TBC_DATE, TBC_SUPPLIER, TBC_AMOUNT, TBC_ITEMS };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
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

			List<Object> supplierList = ledDao.getAllSuppliers(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (supplierList == null) {
				supplierList = new ArrayList<Object>();
			}
			supplierList.add(0, ledgerModel);
			supplierComboField = new SComboField(getPropertyName("supplier"),
					200, supplierList, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(supplierComboField);

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

		//	generateButton = new SButton(getPropertyName("generate"));
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

			table = new STable(null, 650, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_SUPPLIER, String.class, null, getPropertyName("supplier"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, String.class, null, getPropertyName("amount"), null, Align.RIGHT);
			table.setColumnExpandRatio(TBC_SUPPLIER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_SUPPLIER, getPropertyName("total"));
			
			
			subTable = new STable(null, 750, 250);

			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_RETURN_NO, String.class, null,getPropertyName("debit_note_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("amount"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);

			subTable.setColumnExpandRatio(TBC_SN, (float) 0.4);
			subTable.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			subTable.setColumnExpandRatio(TBC_RETURN_NO, 0.9f);
			subTable.setColumnExpandRatio(TBC_SUPPLIER, 1.2f);
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
			
			supplierComboField
					.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(ValueChangeEvent event) {
							supplierid = 0;
							table.removeAllItems();
							table.setColumnFooter(TBC_AMOUNT, "0.0");
							if (supplierComboField.getValue() != null
									&& !supplierComboField.getValue()
											.toString().equals("0")) {
								supplierid = toLong(supplierComboField
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

			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					officeId = toLong(officeComboField.getValue().toString());
					loadSupplierCombo();
					reloadItemCombo(officeId);
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

			
			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					boolean noData = true;
					List<Object> reportList = new ArrayList<Object>();
					PurchaseReturnModel returnModel = null;
					PurchaseReturnInventoryDetailsModel detailsModel = null;
					PurchaseReturnReportBean bean = null;
					String items = "";

					try {

						List<Object> list = new PurchaseReturnReportDao()
								.getReturnDetails(
										supplierid,
										itemId,
										officeId,
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));

						List<PurchaseReturnInventoryDetailsModel> detailsList;
						double amount;
						for (int i = 0; i < list.size(); i++) {
							noData = false;
							returnModel = (PurchaseReturnModel) list.get(i);
							detailsList = returnModel
									.getInventory_details_list();

							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								detailsModel = detailsList.get(k);
								if (k != 0) {
									items += " , ";
								}
								items += detailsModel.getItem().getName()
										+ "(Qty : "
										+ detailsModel.getQunatity() + ")";
							}
							amount = returnModel.getAmount() / returnModel.getConversionRate();
							if(returnModel.getNetCurrencyId().getId() == getCurrencyID()){
								bean = new PurchaseReturnReportBean(CommonUtil
										.getUtilDateFromSQLDate(
												returnModel.getDate()).toString(),
										returnModel.getSupplier().getName(),
										returnModel.getOffice().getName(), items,
										returnModel.getAmount(), returnModel
												.getAmount(), returnModel
												.getReturn_no());
								bean.setCurrency(getCurrencyDescription(getCurrencyID()));
										
							} else {
								amount = returnModel.getAmount() / returnModel.getConversionRate();
								bean = new PurchaseReturnReportBean(CommonUtil
										.getUtilDateFromSQLDate(
												returnModel.getDate()).toString(),
										returnModel.getSupplier().getName(),
										returnModel.getOffice().getName(), items,
										roundNumber(amount),
										roundNumber(amount), returnModel
												.getReturn_no());
								
								bean.setCurrency(getCurrencyDescription(getCurrencyID())+
										" ("+returnModel.getAmount()+" "+returnModel.getNetCurrencyId().getSymbol()+")");
								
							}
							
							reportList.add(bean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Purchase_Return_Report");
							report.setReportFileName("PurchaseReturnReport");
//							report.setReportTitle("Purchase Return Report");
							String subHeader = "";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_return_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("DEBIT_NOTE_NO_LABEL", getPropertyName("debit_note_no"));
							map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							if (supplierid != 0) {
								subHeader += getPropertyName("supplier")+" : "
										+ supplierComboField
												.getItemCaption(supplierComboField
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
							SNotification.show(
									getPropertyName("no_data_available"),
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
					Object[] row;

					List<Object> repList = null;
					try {
						repList = new PurchaseReturnReportDao()
								.showReturnDetails(
										supplierid,
										itemId,
										officeId,
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));
					} catch (Exception e) {
						e.printStackTrace();
					}

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
					if (repList != null && repList.size() > 0) {
						List<PurchaseReturnInventoryDetailsModel> detailsList;
						for (int i = 0; i < repList.size(); i++) {
							SalesReportBean bean = (SalesReportBean) repList.get(i);

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
						SNotification.show("No data available",Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
				}
			});
			
			
			
			generateConsolidatedButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						List<Object> reportList = new PurchaseReturnReportDao().showReturnDetails(
										supplierid,
										itemId,
										officeId,
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						if (reportList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Consolidated_3_Report");
							report.setReportFileName("ConsolidatedReport");
//							report.setReportTitle("Purchase Return Report");
							String subHeader = "";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("purchase_return_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							if (supplierid != 0) {
								subHeader += getPropertyName("supplier")+" : "
										+ supplierComboField
												.getItemCaption(supplierComboField
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
						} else
							SNotification.show("No data available",Type.WARNING_MESSAGE);
						
						
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

			
			final Action actionDelete = new Action("Edit");
			
			
			subTable.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (subTable.getValue() != null) {
							Item item = subTable.getItem(subTable.getValue());
							PurchaseReturnUI purchase = new PurchaseReturnUI();
							purchase.setCaption("Purchase Return");
							purchase.getBillNoFiled().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							popUp.setPopupVisible(false);
							purchase.center();
							getUI().getCurrent().addWindow(purchase);
							purchase.addCloseListener(closeListener);
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
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							PurchaseReturnModel sale=new PurchaseReturnDao().getPurchaseReturnModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase_return")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("supplier"),sale.getSupplier().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
					//		form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getDebit_note_no()+ ""));
							
							double amount = sale.getAmount() / sale.getConversionRate();
							if(sale.getNetCurrencyId().getId() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(sale.getAmount())+" "+getCurrencyDescription(getCurrencyID())));		
//								form.addComponent(new SLabel(getPropertyName("paid_amount"), 
//										roundNumber(sale.getPaid_by_payment())+" "+getCurrencyDescription(getCurrencyID())));		
								//form.addComponent(new SLabel(getPropertyName("paid_amount"),sale.getPaid_by_payment()+ ""));
							} else {
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())
										+" ("+roundNumber(sale.getAmount())+" "+getCurrencyDescription(sale.getNetCurrencyId().getId())+")"));	
//								form.addComponent(new SLabel(getPropertyName("paid_amount"), 
//										roundNumber(paymentAmount)+" "+getCurrencyDescription(getCurrencyID())
//										+" ("+roundNumber(sale.getPaid_by_payment())+" "+getCurrencyDescription(sale.getNetCurrencyId().getId())+")"));	
							}
				//			form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
					//		form.addComponent(new SLabel(getPropertyName("paid_amount"),sale.getPayment_amount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("returned_qty")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")),	3, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),4, 0);
							grid.setSpacing(true);
							
							int i = 1;
							PurchaseReturnInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(PurchaseReturnInventoryDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol() + ""),3, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity() 
																- invObj.getDiscount()
																+ invObj.getTaxAmount())+ ""), 4, i);
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
							PurchaseReturnModel purchaseModel = null;
							PurchaseReturnInventoryDetailsModel inventoryDetailsModel = null;
							String items = "";
							Object[] row;

							List<Object> repList = new PurchaseReturnReportDao().getReturnDetails(
																id,
																itemId,
																officeId,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
							subTable.setVisibleColumns(allSubColumns);
							subTable.removeAllItems();
							subTable.setColumnFooter(TBC_AMOUNT, "0.0");
							double ttl = 0;
							double amount;
							if (repList != null && repList.size() > 0) {
								List<PurchaseReturnInventoryDetailsModel> detailsList;
								for (int i = 0; i < repList.size(); i++) {
									purchaseModel = (PurchaseReturnModel) repList
											.get(i);
									detailsList = purchaseModel
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
									amount = purchaseModel.getAmount() / purchaseModel.getConversionRate();
									if(purchaseModel.getNetCurrencyId().getId() == getCurrencyID()){
										row = new Object[] {
												i + 1,
												purchaseModel.getId(),
												purchaseModel.getReturn_no() + "",
												CommonUtil.getUtilFromSQLDate(purchaseModel
														.getDate()) + "",
												purchaseModel.getSupplier().getName(),
												roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID()), items };										
									} else {
										row = new Object[] {
												i + 1,
												purchaseModel.getId(),
												purchaseModel.getReturn_no() + "",
												CommonUtil.getUtilFromSQLDate(purchaseModel
														.getDate()) + "",
												purchaseModel.getSupplier().getName(),
												roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())+
												"("+purchaseModel.getAmount()+" "+getCurrencyDescription(purchaseModel.getNetCurrencyId().getId())+")", items };					
									
									}
									
									subTable.addItem(row, i + 1);
									ttl += roundNumber(amount);
								}
								subTable.setColumnFooter(TBC_AMOUNT,
										asString(roundNumber(ttl)));

							} 
							else
								SNotification.show("No data available",Type.WARNING_MESSAGE);

							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Purchase Return Details",725), closeBtn), subTable));

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
	
	protected void loadSupplierCombo() {
		List<Object> suppList = null;
		try {
			if (officeId != 0) {
				suppList = ledDao.getAllSuppliers(officeId);
			} else {
				suppList = ledDao.getAllSuppliers();
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (suppList == null) {
				suppList = new ArrayList<Object>();
			}
			suppList.add(0, ledgerModel);
			container = SCollectionContainer.setList(suppList, "id");
			supplierComboField.setContainerDataSource(container);
			supplierComboField.setItemCaptionPropertyId("name");
			supplierComboField.setValue(0);

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void reloadItemCombo(long officeId) {
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

			itemContainer = SCollectionContainer.setList(itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(0);

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
