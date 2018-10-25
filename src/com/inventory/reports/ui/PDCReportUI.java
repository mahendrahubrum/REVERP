package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.PDCDao;
import com.inventory.config.acct.model.PdcModel;
import com.inventory.config.acct.ui.PDCUI;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.PdcReportBean;
import com.inventory.reports.dao.PDCReportDao;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
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
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author sangeeth
 * @date 21-Oct-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class PDCReportUI extends SparkLogic {

	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField pdcCombo;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;
	private SButton generateConsolidatedButton;


	LedgerDao ledDao;

	private Report report;

	PDCReportDao dao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_PDC_NO = "PDC No";
	static String TBC_CHEQUE_DATE = "Cheque Date";
	static String TBC_ISSUE_DATE = "Issue Date";
	static String TBC_BANK = "Bank";
	static String TBC_CHEQUE_NO = "Cheque No";
	static String TBC_STATUS = "PDC Status";
	static String TBC_AMOUNT = "Amount";
	static String TBC_CURRENCY = "Currency";

	private STable table;
	

	private Object[] allColumns;
	private Object[] visibleColumns;
	

	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer;
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;
	
	
	@Override
	public SPanel getGUI() {

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_PDC_NO, TBC_BANK, TBC_CHEQUE_DATE};
		visibleColumns = new String[] { TBC_SN, TBC_PDC_NO, TBC_BANK, TBC_CHEQUE_DATE};
		
		allSubColumns = new String[] { TBC_SN, TBC_PDC_NO, TBC_CHEQUE_DATE, TBC_ISSUE_DATE, TBC_CHEQUE_NO, TBC_STATUS, TBC_AMOUNT, TBC_CURRENCY };
		visibleSubColumns = new String[] { TBC_SN, TBC_PDC_NO, TBC_CHEQUE_DATE, TBC_ISSUE_DATE, TBC_CHEQUE_NO, TBC_STATUS, TBC_AMOUNT, TBC_CURRENCY};
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		
		ledDao = new LedgerDao();
		dao = new PDCReportDao();
		popupContainer = new SHorizontalLayout();
		
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

			pdcCombo = new SComboField(getPropertyName("pdc"), 200, null, "id","comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(pdcCombo);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);
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
			table.addContainerProperty(TBC_PDC_NO, String.class, null, getPropertyName("pdc_no"), null,Align.CENTER);
			table.addContainerProperty(TBC_BANK, String.class, null,getPropertyName("bank"), null, Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_DATE, String.class, null,getPropertyName("cheque_date"), null, Align.RIGHT);
			
			table.setColumnExpandRatio(TBC_PDC_NO, 0.5f);
			table.setColumnExpandRatio(TBC_BANK, 1f);
			table.setColumnExpandRatio(TBC_CHEQUE_DATE, 1f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			subTable = new STable(null, 750, 250);
			
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_PDC_NO, String.class, null, TBC_PDC_NO, null,Align.CENTER);
			subTable.addContainerProperty(TBC_CHEQUE_DATE, String.class, null,getPropertyName("cheque_date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_ISSUE_DATE, String.class, null,getPropertyName("issue_date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CHEQUE_NO, String.class, null,getPropertyName("cheque_no"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_STATUS, String.class, null,TBC_STATUS, null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_CURRENCY, String.class, null,getPropertyName("currency"), null, Align.LEFT);
			
			subTable.setColumnExpandRatio(TBC_CHEQUE_DATE, 1f);
			subTable.setColumnExpandRatio(TBC_ISSUE_DATE, 1f);
			subTable.setColumnExpandRatio(TBC_PDC_NO, 0.5f);
			subTable.setColumnExpandRatio(TBC_AMOUNT, 1f);
			subTable.setColumnExpandRatio(TBC_STATUS, 1f);
			subTable.setColumnExpandRatio(TBC_CURRENCY, 1f);
			
			subTable.setVisibleColumns(visibleSubColumns);
			subTable.setSelectable(true);

			subTable.setFooterVisible(true);
			subTable.setColumnFooter(TBC_CHEQUE_DATE, getPropertyName("total"));
			
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
			
			
			pdcCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

			
			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadBillNo(toLong(officeComboField.getValue().toString()));
				}
			});

			
			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						List<Object> reportList = new ArrayList<Object>();
						reportList=dao.getPdcChildReport((Long)pdcCombo.getValue(), 
														(Long)officeComboField.getValue(), 
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						if(reportList.size()>0){
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("PDC_Report");
							report.setReportFileName("PDC Report");
							S_OfficeModel office=new OfficeDao().getOffice((Long)officeComboField.getValue());
							map.put("REPORT_TITLE_LABEL", getPropertyName("pdc_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("SALES_NO_LABEL", getPropertyName("pdc_no"));
							map.put("INVOICE_DATE_LABEL", getPropertyName("cheque_date"));
							map.put("CUSTOMER_LABEL", getPropertyName("bank"));
							map.put("SALES_MAN_LABEL", getPropertyName("issue_date"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("cheque_no"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							map.put("CURRENCY_LABEL", office.getCurrency().getCode());
							map.put("STATUS_LABEL", getPropertyName("status"));
							
							
							String subHeader = "";
							if (pdcCombo.getValue() != null && !pdcCombo.getValue().toString().equals("0")) {
								subHeader += getPropertyName("pdc_no")+" : "+ pdcCombo.getItemCaption(pdcCombo.getValue());
							}
							subHeader += "\n"+getPropertyName("from")+" : "
									+ CommonUtil.formatDateToDDMMYYYY(fromDateField.getValue())
									+ "\t "+getPropertyName("to")+" : "
									+ CommonUtil.formatDateToDDMMYYYY(toDateField.getValue());
							report.setReportTitle(getPropertyName("pdc_report"));
							report.setReportSubTitle(subHeader);
							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField.getValue().toString()));
							report.setOfficeName(officeComboField.getItemCaption(officeComboField.getValue()));
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

			
			generateConsolidatedButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						List<Object> reportList = new ArrayList<Object>();
						reportList=dao.getPdcReport((Long)pdcCombo.getValue(), 
														(Long)officeComboField.getValue(), 
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						if(reportList.size()>0){
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("PDC_Consolidated_Report");
							report.setReportFileName("PDC_Consolidated_Report");
							S_OfficeModel office=new OfficeDao().getOffice((Long)officeComboField.getValue());
							map.put("REPORT_TITLE_LABEL", getPropertyName("pdc_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("SALES_NO_LABEL", getPropertyName("pdc_no"));
							map.put("INVOICE_DATE_LABEL", getPropertyName("cheque_date"));
							map.put("CUSTOMER_LABEL", getPropertyName("bank"));
							map.put("SALES_MAN_LABEL", getPropertyName("issue_date"));
							map.put("PAYMENT_AMOUNT_LABEL", getPropertyName("cheque_no"));
							map.put("AMOUNT_LABEL", getPropertyName("amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							map.put("CURRENCY_LABEL", office.getCurrency().getCode());
							map.put("STATUS_LABEL", getPropertyName("status"));
							
							
							String subHeader = "";
							if (pdcCombo.getValue() != null && !pdcCombo.getValue().toString().equals("0")) {
								subHeader += getPropertyName("pdc_no")+" : "+ pdcCombo.getItemCaption(pdcCombo.getValue());
							}
							subHeader += "\n"+getPropertyName("from")+" : "
									+ CommonUtil.formatDateToDDMMYYYY(fromDateField.getValue())
									+ "\t "+getPropertyName("to")+" : "
									+ CommonUtil.formatDateToDDMMYYYY(toDateField.getValue());

							report.setReportSubTitle(subHeader);
							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField.getValue().toString()));
							report.setOfficeName(officeComboField.getItemCaption(officeComboField.getValue()));
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

				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						List resultList=new ArrayList();
						table.setVisibleColumns(allColumns);
						table.removeAllItems();
						resultList=dao.getPdcReport((Long)pdcCombo.getValue(), 
													(Long)officeComboField.getValue(), 
													CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
													CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
						if(resultList.size()>0){
							Iterator itr=resultList.iterator();
							while (itr.hasNext()) {
								PdcReportBean bean = (PdcReportBean) itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getId(),
										bean.getBill(),
										bean.getBank(),
										CommonUtil.formatDateToDDMMYYYY(bean.getChequeDate())},table.getItemIds().size()+1);
							}
						}
						else
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						table.setVisibleColumns(visibleColumns);
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
			
			
			table.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							PDCUI sales=new PDCUI();
							sales.setCaption(getPropertyName("pdc"));
							sales.loadData((Long) item.getItemProperty(TBC_ID).getValue());
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
							SalesModel sale=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
//							form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
//							if (isShippingChargeEnable())
//								form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
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
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							List resultList=new ArrayList();
							subTable.setVisibleColumns(allSubColumns);
							subTable.removeAllItems();
							subTable.setColumnFooter(TBC_AMOUNT, "0.0");
							Item itm = table.getItem(table.getValue());
							resultList=dao.getPdcChildReport((Long) itm.getItemProperty(TBC_ID).getValue(), 
															(Long)officeComboField.getValue(), 
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
							double total=0;
							if(resultList.size()>0){
								Iterator itr=resultList.iterator();
								while (itr.hasNext()) {
									PdcReportBean bean = (PdcReportBean) itr.next();
									subTable.addItem(new Object[]{
											subTable.getItemIds().size()+1,
											bean.getBill(),
											CommonUtil.formatDateToDDMMYYYY(bean.getChequeDate()),
											CommonUtil.formatDateToDDMMYYYY(bean.getIssueDate()),
											bean.getChequeNo(),
											getStatus(bean.getStatus()),
											roundNumber(bean.getAmount()),
											bean.getCurrency()},subTable.getItemIds().size()+1);
									total+=(bean.getAmount()/bean.getConvRate());
								}
							}
							else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							S_OfficeModel office=new OfficeDao().getOffice((Long)officeComboField.getValue());
							subTable.setColumnFooter(TBC_AMOUNT,roundNumber(total)+" "+office.getCurrency().getCode());
							subTable.setVisibleColumns(visibleSubColumns);
							
							popUp = new SPopupView( "",new SVerticalLayout(true,
									new SHorizontalLayout(new SHTMLLabel(null, "<h2><u style='margin-left: 40px;'>Pdc Details",725), closeBtn), subTable));
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

	
	protected List<Object> getSalesReportList() {
		long salesNo = 0;
		long custId = 0;

		if (pdcCombo.getValue() != null && !pdcCombo.getValue().equals("") && !pdcCombo.getValue().toString().equals("0")) {
			salesNo = toLong(pdcCombo.getValue().toString());
		}
		String condition1 = "";

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
	
	
	protected List<Object> getSalesReportSubList(long id) {
		long salesNo = 0;

		if (pdcCombo.getValue() != null && !pdcCombo.getValue().equals("") && !pdcCombo.getValue().toString().equals("0")) {
			salesNo = toLong(pdcCombo.getValue().toString());
		}
		String condition1 = "";

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesDetails(salesNo, id, CommonUtil
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
	
	
	protected List<Object> showSalesReportList() {
		long salesNo = 0;
		long custId = 0;

		if (pdcCombo.getValue() != null
				&& !pdcCombo.getValue().equals("")
				&& !pdcCombo.getValue().toString().equals("0")) {
			salesNo = toLong(pdcCombo.getValue().toString());
		}
		String condition1 = "";

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getSalesDetailsConsolidated(salesNo, custId, CommonUtil
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
	private void loadBillNo(long officeId) {
		List salesList = new ArrayList();
		try {
			salesList.add(new PdcModel(0, getPropertyName("all")));
			salesList.addAll(new PDCDao().getPdcModelList(officeId));
			SCollectionContainer container; container = SCollectionContainer.setList(salesList, "id");
			pdcCombo.setContainerDataSource(container);
			pdcCombo.setItemCaptionPropertyId("bill_no");
			pdcCombo.setValue((long)0);
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
		return null;
	}

	
	public String getStatus(int stat){
		String status="";
		switch (stat) {
		
			case 1:
					status="Issued";
					break;
					
			case 2:
					status="Approved";
					break;
				
			case 3:
					status="Cancelled";
					break;

			default:
					break;
		}
		return status;
	}

	
}
