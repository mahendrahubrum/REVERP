package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseGRNDao;
import com.inventory.purchase.model.PurchaseGRNDetailsModel;
import com.inventory.purchase.model.PurchaseGRNModel;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.PurchaseReportBean;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.PurchaseReportDao;
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
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
//import com.inventory.purchase.model.PurchaseModel;

/**
 * @author Anil K P
 * 
 *         WebSpark
 * 
 *         Aug 8, 2013
 */
public class GRNReportUI extends SparkLogic {

	private static final long serialVersionUID = -4662121877669280864L;

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
	private SComboField purchaseNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;
	private SButton generateConsolidatedButton;

	private CollectionContainer container;
	private CollectionContainer suppContainer;

	private long supplierid;

	private Report report;

	//SRadioButton filterTypeRadio;

	LedgerDao ledDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_PURCHASE_NO = "Purchase No";
	static String TBC_DATE = "Date";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";

	private STable table;

	private Object[] allColumns;
	private Object[] visibleColumns;
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;

	SRadioButton statusRadioButton;
	SHorizontalLayout popupContainer;

	private WrappedSession session;
	private SettingsValuePojo sett;

	private HashMap<Long, String> currencyHashMap;

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();
		popupContainer=new SHorizontalLayout();
		
		allColumns = new String[] { TBC_SN, TBC_ID, TBC_SUPPLIER, TBC_AMOUNT};
		visibleColumns = new String[] { TBC_SN, TBC_SUPPLIER, TBC_AMOUNT};
		
		allSubColumns = new String[] { TBC_SN, TBC_ID, TBC_PURCHASE_NO, TBC_DATE,TBC_SUPPLIER, TBC_AMOUNT, TBC_ITEMS };
		visibleSubColumns = new String[] { TBC_SN, TBC_PURCHASE_NO, TBC_DATE,TBC_SUPPLIER, TBC_AMOUNT, TBC_ITEMS };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		
		supplierid = 0;
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

		/*	filterTypeRadio = new SRadioButton(getPropertyName("payment_type"),
					250, SConstants.filterTypeList, "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(0);*/

			statusRadioButton = new SRadioButton(getPropertyName("status"),
					250, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(
							1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);

		//	mainFormLayout.addComponent(filterTypeRadio);

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);

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

			purchaseNoComboField = new SComboField(
					getPropertyName("grn_bill_no"), 200, null, "id",
					"grn_no", false, getPropertyName("all"));
			mainFormLayout.addComponent(purchaseNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateConsolidatedButton = new SButton(getPropertyName("consolidated_report"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(generateConsolidatedButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(generateConsolidatedButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 670, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("amount"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SUPPLIER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_SUPPLIER, getPropertyName("total"));
			
			
			
			subTable = new STable(null, 750, 250);

			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_PURCHASE_NO, String.class, null,getPropertyName("purchase_no"), null, Align.CENTER);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("amount"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);

			subTable.setColumnExpandRatio(TBC_SN, (float) 0.4);
			subTable.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			subTable.setColumnExpandRatio(TBC_PURCHASE_NO, 0.9f);
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
			
			
			purchaseNoComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			
	/*		filterTypeRadio.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});
*/
			
			statusRadioButton.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			supplierComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					supplierid = 0;
					if (supplierComboField.getValue() != null
							&& !supplierComboField.getValue()
									.toString().equals("0")) {
						supplierid = toLong(supplierComboField
								.getValue().toString());
					}
					loadBillNo(supplierid, toLong(officeComboField
							.getValue().toString()));
				}
			});

			
			fromDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			
			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadSupplierCombo(toLong(officeComboField.getValue()
							.toString()));
					loadBillNo(supplierid, toLong(officeComboField.getValue()
							.toString()));
				}
			});
			
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						PurchaseGRNModel purchaseModel = null;
						PurchaseGRNDetailsModel inventoryDetailsModel = null;
						PurchaseReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();
						List<Object> purchaseModelList = getPurchaseReportList();
						double amount;
						for (int i = 0; i < purchaseModelList.size(); i++) {
							noData = false;
							purchaseModel = (PurchaseGRNModel) purchaseModelList
									.get(i);
							List<PurchaseGRNDetailsModel> detailsList = purchaseModel
									.getGrn_details_list();
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
							amount = purchaseModel.getAmount() / purchaseModel.getConversionRate();
							if(purchaseModel.getCurrencyId() == getCurrencyID()){
								reportBean = new PurchaseReportBean(
										CommonUtil.getUtilDateFromSQLDate(
												purchaseModel.getDate()).toString(),
										purchaseModel.getSupplier().getName(),
										purchaseModel.getGrn_no(),
										purchaseModel.getOffice().getName(), items,
										purchaseModel.getAmount(), 0);
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID()));								
							} else {
								reportBean = new PurchaseReportBean(
										CommonUtil.getUtilDateFromSQLDate(
												purchaseModel.getDate()).toString(),
										purchaseModel.getSupplier().getName(),
										purchaseModel.getGrn_no(),
										purchaseModel.getOffice().getName(), items,
										roundNumber(amount), 0);
								reportBean.setCurrency(getCurrencyDescription(getCurrencyID())+
										" ("+purchaseModel.getAmount()+" "+getCurrencyDescription(purchaseModel.getCurrencyId())+")");
							}
							
							reportList.add(reportBean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Purchase_Report");
							report.setReportFileName("grn_report");
							report.setReportTitle(getPropertyName("grn_report"));
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("grn_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
							map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("payment_amount"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							
							String subHeader = "";
							if (supplierid != 0) {
								subHeader += getPropertyName("supplier")+" : "
										+ supplierComboField
												.getItemCaption(supplierComboField
														.getValue()) + "\n";
							} else {
								subHeader += getPropertyName("supplier")+" : "+
														getPropertyName("all")+"\n";
							}
							if (purchaseNoComboField.getValue() != null
									&& !purchaseNoComboField.getValue().equals(
											"")
									&& !purchaseNoComboField.getValue()
											.toString().equals("0")) {
								subHeader += getPropertyName("grn_no")+" : "
										+ purchaseNoComboField
												.getItemCaption(purchaseNoComboField
														.getValue());
							} else {
								subHeader += getPropertyName("grn_no")+" : "+
										getPropertyName("all");
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
							purchaseModelList.clear();

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

				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					Object[] row;

					List repList = showPurchaseReportList();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
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

					}
					else
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
				}
			});
			
			
			
			generateConsolidatedButton.addClickListener(new ClickListener() {

				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					List reportList = showPurchaseReportList();
					if (reportList.size() > 0) {
						for(Object obj : reportList){
							SalesReportBean bean = (SalesReportBean) obj;
							bean.setCurrency(getCurrencyDescription(getCurrencyID()));
						}
						HashMap<String, Object> map = new HashMap<String, Object>();
						report.setJrxmlFileName("Consolidated_3_Report");
						report.setReportFileName("ConsolidatedReport");
//						report.setReportTitle("Purchase Report");
						
						map.put("REPORT_TITLE_LABEL", getPropertyName("grn_report"));
						map.put("CUSTOMER_LABEL", getPropertyName("supplier"));
						map.put("AMOUNT_LABEL", getPropertyName("amount"));
						map.put("TOTAL_LABEL", getPropertyName("total"));
						
						String subHeader = "";
						if (supplierid != 0) {
							subHeader += getPropertyName("supplier")+" : "+ supplierComboField.getItemCaption(supplierComboField.getValue()) + "\t";
						} else {
							subHeader += getPropertyName("supplier")+" : All \t";
						}
						if (purchaseNoComboField.getValue() != null && !purchaseNoComboField.getValue().equals("") 
								&& !purchaseNoComboField.getValue().toString().equals("0")) {
							subHeader += getPropertyName("purchase_no")+" : "+ purchaseNoComboField.getItemCaption(purchaseNoComboField
													.getValue());
						}else{
							subHeader += getPropertyName("purchase_no")+" : All";
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
							PurchaseUI purchase = new PurchaseUI();
							purchase.setCaption(getPropertyName("purchase"));
							purchase.getPurchaseNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							PurchaseGRNModel objModel = new PurchaseGRNDao().getPurchaseGRNModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("grn_no"),objModel.getGrn_no() + ""));
							form.addComponent(new SLabel(getPropertyName("ref_no"),
									objModel.getRef_no()/*Purchase_bill_number()*/ + ""));
							form.addComponent(new SLabel(getPropertyName("supplier"), objModel.getSupplier().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getDate())));
							//form.addComponent(new SLabel(getPropertyName("max_credit_period"),objModel.getCredit_period() + ""));
//							if (isShippingChargeEnable())
//								form.addComponent(new SLabel(getPropertyName("shipping_charge"),objModel.getShipping_charge() + ""));
							double amount = objModel.getAmount() / objModel.getConversionRate();
							if(objModel.getCurrencyId() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(objModel.getAmount())+" "+getCurrencyDescription(getCurrencyID())));								
							} else {
								form.addComponent(new SLabel(getPropertyName("net_amount"), 
										roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())
										+" ("+roundNumber(objModel.getAmount())+" "+getCurrencyDescription(objModel.getCurrencyId())+")"));									
							}
						//	form.addComponent(new SLabel(getPropertyName("net_amount"), objModel.getAmount() + ""));
					//		form.addComponent(new SLabel(getPropertyName("paid_amount"),objModel.getPaymentAmount() + ""));
	
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(objModel.getGrn_details_list().size() + 3);
							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
						//	grid.addComponent(new SLabel(null, getPropertyName("discount")),5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
							grid.setSpacing(true);
							int i = 1;
							PurchaseGRNDetailsModel invObj;
							Iterator itmItr = objModel.getGrn_details_list().iterator();
							while (itmItr.hasNext()) {
								invObj = (PurchaseGRNDetailsModel) itmItr.next();
								grid.addComponent(new SLabel(null, i + ""),0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,i);
					//			grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price()* invObj.getQunatity())+ " "+
					getCurrencyDescription(getCurrencyID())), 6, i);
								i++;
							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), objModel.getComments()));
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
							LedgerModel ledger=new LedgerDao().getLedgeer(id);
							PurchaseGRNModel purchaseModel = null;
							PurchaseGRNDetailsModel inventoryDetailsModel = null;
							String items = "";
							Object[] row;
							List reportList=getPurchaseReportSubList(id);
							subTable.setVisibleColumns(allSubColumns);
							subTable.removeAllItems();
							subTable.setColumnFooter(TBC_AMOUNT, "0.0");
							double ttl = 0;
							
							if (reportList != null && reportList.size() > 0) {
								List<PurchaseGRNDetailsModel> detailsList;
								double amount;
								for (int i = 0; i < reportList.size(); i++) {
									purchaseModel = (PurchaseGRNModel) reportList.get(i);
									detailsList = purchaseModel.getGrn_details_list();
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
									if(purchaseModel.getCurrencyId() == getCurrencyID()){
										row = new Object[] {
												i + 1,
												purchaseModel.getId(),
												purchaseModel.getGrn_no() + "",
												CommonUtil.getUtilFromSQLDate(purchaseModel
														.getDate()) + "",
												purchaseModel.getSupplier().getName(),
												purchaseModel.getAmount()+" "+getCurrencyDescription(getCurrencyID()), items };
										
									} else {
										row = new Object[] {
												i + 1,
												purchaseModel.getId(),
												purchaseModel.getGrn_no() + "",
												CommonUtil.getUtilFromSQLDate(purchaseModel
														.getDate()) + "",
												purchaseModel.getSupplier().getName(),
												roundNumber(amount)+" "+getCurrencyDescription(getCurrencyID())
												+" ("+roundNumber(purchaseModel.getAmount())+" "+getCurrencyDescription(purchaseModel.getCurrencyId())+")", items };
										
									}
									
									subTable.addItem(row, i + 1);
									ttl += roundNumber(amount);
								}
								subTable.setColumnFooter(TBC_AMOUNT,asString(roundNumber(ttl)));

							}else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Purchase Details",725), closeBtn), subTable));

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
			supplierComboField.setValue((long)0);
			purchaseNoComboField.setValue((long)0);

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
	
	protected List<Object> getPurchaseReportList() {
		long purchaseNo = 0;
		long supplierId = 0;

		if (purchaseNoComboField.getValue() != null
				&& !purchaseNoComboField.getValue().equals("")
				&& !purchaseNoComboField.getValue().toString().equals("0")) {
			purchaseNo = toLong(purchaseNoComboField.getValue().toString());
		}
		if (supplierComboField.getValue() != null
				&& !supplierComboField.getValue().equals("")) {
			supplierId = toLong(supplierComboField.getValue().toString());
		}

		String condition1 = "";
/*
		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and payment_amount=amount ";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and payment_amount<amount ";
		}*/
		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and active=false ";
		}

		List<Object> purchaseModelList = null;
		try {
			purchaseModelList = new PurchaseReportDao()
					.getPurchaseGRNDetails(purchaseNo, supplierId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()),
							getOrganizationID(), condition1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseModelList;
	}
	
	
	protected List<Object> getPurchaseReportSubList(long id) {
		long purchaseNo = 0;

		if (purchaseNoComboField.getValue() != null
				&& !purchaseNoComboField.getValue().equals("")
				&& !purchaseNoComboField.getValue().toString().equals("0")) {
			purchaseNo = toLong(purchaseNoComboField.getValue().toString());
		}

		String condition1 = "";

	/*	if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and payment_amount=amount ";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and payment_amount<amount ";
		}*/
		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and active=false ";
		}

		List<Object> purchaseModelList = null;
		try {
			purchaseModelList = new PurchaseReportDao()
					.getPurchaseGRNDetails(purchaseNo, id, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()),
							getOrganizationID(), condition1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseModelList;
	}
	
	
	protected List<Object> showPurchaseReportList() {
		long purchaseNo = 0;
		long supplierId = 0;

		if (purchaseNoComboField.getValue() != null
				&& !purchaseNoComboField.getValue().equals("")
				&& !purchaseNoComboField.getValue().toString().equals("0")) {
			purchaseNo = toLong(purchaseNoComboField.getValue().toString());
		}
		if (supplierComboField.getValue() != null
				&& !supplierComboField.getValue().equals("")) {
			supplierId = toLong(supplierComboField.getValue().toString());
		}

		String condition1 = "";

	/*	if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and payment_amount=amount ";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and payment_amount<amount ";
		}*/
		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and active=false ";
		}

		List<Object> purchaseModelList = null;
		try {
			purchaseModelList = new PurchaseReportDao()
					.getPurchaseGRNDetailsConsolidated(purchaseNo, supplierId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()),
							getOrganizationID(), condition1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseModelList;
	}

	
	@SuppressWarnings("unchecked")
	private void loadBillNo(long supplierId, long officeId) {
		List<Object> purchaseBillList = null;
		try {

			String condition1 = "";

		/*	if ((Integer) filterTypeRadio.getValue() == 1) {
				condition1 = " and paymentAmount=amount ";
			} else if ((Integer) filterTypeRadio.getValue() == 2) {
				condition1 = " and paymentAmount<amount ";
			}*/

			if ((Integer) statusRadioButton.getValue() == 0) {
				condition1 += " and active=true ";
			} else if ((Integer) statusRadioButton.getValue() == 1) {
				condition1 += " and active=false ";
			}

			if (supplierId != 0) {
				purchaseBillList = new PurchaseDao()
						.getAllPurchaseGRNNumbersForSupplier(officeId, supplierId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()), condition1);
			} else {
				purchaseBillList = new PurchaseDao()
						.getAllPurchaseGRNNumbersFromDate(officeId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDateField
										.getValue()), condition1);
			}

			PurchaseGRNModel purchaseModel = new PurchaseGRNModel();
			purchaseModel.setId(0);
			purchaseModel
					.setGrn_no(getPropertyName("all"));
			if (purchaseBillList == null) {
				purchaseBillList = new ArrayList<Object>();
			}
			purchaseBillList.add(0, purchaseModel);
			container = CollectionContainer.fromBeans(purchaseBillList, "id");
			purchaseNoComboField.setContainerDataSource(container);
			purchaseNoComboField.setItemCaptionPropertyId("grn_no");
			purchaseNoComboField.setValue(0);

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	protected void loadSupplierCombo(long officeId) {
		List<Object> suppList = null;
		try {
			if (officeId != 0) {
				suppList = ledDao.getAllSuppliers(officeId);
			} else {
				suppList = ledDao.getAllSuppliersFromOrgId(getOrganizationID());
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (suppList == null) {
				suppList = new ArrayList<Object>();
			}
			suppList.add(0, ledgerModel);
			suppContainer = CollectionContainer.fromBeans(suppList, "id");
			supplierComboField.setContainerDataSource(suppContainer);
			supplierComboField.setItemCaptionPropertyId("name");
			supplierComboField.setValue(0);

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");
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
		// TODO Auto-generated method stub
		return null;
	}

}
