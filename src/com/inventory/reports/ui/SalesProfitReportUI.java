package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesProfitReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Nov 22, 2013
 */
public class SalesProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField salesNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	LedgerDao ledDao;

	SalesProfitReportDao daoObj;
	private SettingsValuePojo sett;

	static String TBC_SN = "SN";
	static String TBC_SID = "SID";
	static String TBC_BILL = "Bill";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_DATE = "Date";
	static String TBC_PAMOUNT = "Purchase Amount";
	static String TBC_SAMOUNT = "Sale Amount";
	static String TBC_LOSS = "Profit";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	private WrappedSession session;

	
	
	@Override
	public SPanel getGUI() {
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null){
			sett = (SettingsValuePojo) session.getAttribute("settings");
			//profitCalculationType = ;
		}
			

		allColumns = new Object[] { TBC_SN, TBC_SID,TBC_BILL,TBC_CUSTOMER,TBC_DATE,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		visibleColumns = new Object[]{ TBC_SN, TBC_BILL,TBC_CUSTOMER,TBC_DATE,TBC_SAMOUNT, TBC_PAMOUNT,TBC_LOSS};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
		table.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("bill"), null, Align.CENTER);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_SAMOUNT, Double.class, null,getPropertyName("sale_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_PAMOUNT, Double.class, null,getPropertyName("purchase_amount"), null, Align.RIGHT);
		table.addContainerProperty(TBC_LOSS, Double.class, null,getPropertyName("profit"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 1);
		table.setColumnExpandRatio(TBC_CUSTOMER, (float) 2);
		table.setColumnExpandRatio(TBC_SAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_PAMOUNT, (float) 1.5);
		table.setColumnExpandRatio(TBC_LOSS, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		ledDao = new LedgerDao();

		daoObj = new SalesProfitReportDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1050, 375);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, getPropertyName("all"));

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(customerComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_bill_no"), 200, null, "id",
					"sales_number", false, getPropertyName("all"));
			mainFormLayout.addComponent(salesNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			mainPanel.setContent(mainHorizontal);

			organizationComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								SCollectionContainer bic = SCollectionContainer.setList(
										new OfficeDao()
												.getAllOfficeNamesUnderOrg((Long) organizationComboField
														.getValue()), "id");
								officeComboField.setContainerDataSource(bic);
								officeComboField
										.setItemCaptionPropertyId("name");

								Iterator it = officeComboField.getItemIds()
										.iterator();
								if (it.hasNext())
									officeComboField.setValue(it.next());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
							loadBillNo(customerId, toLong(officeComboField
									.getValue().toString()));
						}
					});

			fromDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});
			
			toDateField.addListener(new Listener() {

				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});
			
			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};
			
			final Action actionSales = new Action(getPropertyName("edit"));
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SalesUI option=new SalesUI();
							option.setCaption(getPropertyName("sales"));
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_SID).getValue());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionSales };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_SID).getValue();
							SalesModel mdl=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),mdl.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),mdl.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("sale_amount"),roundNumberToString((Double)item.getItemProperty(TBC_SAMOUNT).getValue())));
							form.addComponent(new SLabel(getPropertyName("purchase_amount"),roundNumberToString((Double)item.getItemProperty(TBC_PAMOUNT).getValue())));
							form.addComponent(new SLabel(getPropertyName("profit"),roundNumberToString((Double)item.getItemProperty(TBC_LOSS).getValue())));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
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
			
			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}

			organizationComboField.setValue(getOrganizationID());

			officeComboField.setValue(getOfficeID());

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList;

						long salesNo = 0;
						long custId = 0;

						if (salesNoComboField.getValue() != null
								&& !salesNoComboField.getValue().equals("")
								&& !salesNoComboField.getValue().toString()
										.equals("0")) {
							salesNo = toLong(salesNoComboField.getValue().toString());
						}
						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						reportList = daoObj.getSalesProfitDetails(
											salesNo,
											custId, 
											CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
											CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
											toLong(officeComboField.getValue().toString()),
											sett.getPROFIT_CALCULATION());
						
						if(reportList.size()>0){
							ReportBean bean=null;
							Iterator itr=reportList.iterator();
							while(itr.hasNext()){
								bean=(ReportBean)itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getId(),
										bean.getParticulars(),
										bean.getClient_name(),
										bean.getDate(),
										bean.getInwards(),
										bean.getOutwards(),
										bean.getProfit()},table.getItemIds().size()+1);
								
							}
						}
						else{
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
						table.setVisibleColumns(visibleColumns);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
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

						List<Object> reportList;

						long salesNo = 0;
						long custId = 0;

						if (salesNoComboField.getValue() != null
								&& !salesNoComboField.getValue().equals("")
								&& !salesNoComboField.getValue().toString()
										.equals("0")) {
							salesNo = toLong(salesNoComboField
									.getItemCaption(salesNoComboField
											.getValue()));
						}
						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						reportList = daoObj.getSalesProfitDetails(salesNo,
								custId, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
								toLong(officeComboField.getValue().toString()),
								sett.getPROFIT_CALCULATION());

						if (reportList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("SalesProfit_Report");
							report.setReportFileName("Sales Profit Report");

							String subHeader = "";
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("sales_profit_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("INWARDS_LABEL", getPropertyName("inwards"));
							map.put("OUTWARDS_LABEL", getPropertyName("outwards"));
							map.put("PROFIT_LABEL", getPropertyName("profit"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							if (customerComboField.getValue() != null)
								if (!customerComboField.getValue().toString()
										.equals("0"))
									subHeader += getPropertyName("customer")+" : "
											+ customerComboField
													.getItemCaption(customerComboField
															.getValue()) + "\t";

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
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

//	private int getProfitCalculationType() {
//		int profitCalculationType = 0;
//		session = getHttpSession();
//		if (session.getAttribute("settings") != null){
//			sett = (SettingsValuePojo) session.getAttribute("settings");
//			profitCalculationType = sett.getPROFIT_CALCULATION();
//		}
//		return profitCalculationType;
//	}

	private void loadBillNo(long customerId, long officeId) {
		List<Object> salesList = null;
		try {
			if (customerId != 0) {
				salesList = new SalesDao().getAllSalesNumbersForCustomer(
						officeId, customerId, CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), " and active=true");
			} else {
				salesList = daoObj
						.getAllSalesNumbersAsCommentWithInDates(officeId,
								CommonUtil.getSQLDateFromUtilDate(fromDateField
										.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));
			}
			SalesModel salesModel = new SalesModel();
			salesModel.setId(0);
			salesModel
					.setSales_number(getPropertyName("all"));
			if (salesList == null) {
				salesList = new ArrayList<Object>();
			}
			salesList.add(0, salesModel);
			container = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(container);
			salesNoComboField.setItemCaptionPropertyId("sales_number");
			salesNoComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadCustomerCombo(long officeId) {
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
