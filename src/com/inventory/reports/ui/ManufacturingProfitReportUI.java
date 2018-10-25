package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ManufacturingDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.ManufacturingProfitBean;
import com.inventory.reports.dao.ManufacturingProfitReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
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
import com.webspark.uac.dao.OrganizationDao;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 3, 2014
 */

public class ManufacturingProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;
	SRadioButton radio;
	private SButton generateButton;

	private SCollectionContainer custContainer;

	private Report report;

	private ManufacturingDao dao;
	
	ManufacturingProfitReportDao mdao;
	ManufacturingProfitBean bean;
	private ItemDao itemDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM = "Item";
	static String TBC_DATE = "Date";
	static String TBC_BILL = "Bill";	
	static String TBC_PURCHASE = "Purchase";
	static String TBC_SALE = "Sale";
	static String TBC_EXPENSE = "Expense";
	static String TBC_PROFIT = "Profit";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns1;
	Object[] visibleColumns2;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DATE,TBC_ITEM,TBC_BILL,TBC_PURCHASE,TBC_SALE,TBC_EXPENSE,TBC_PROFIT};
		visibleColumns1 = new Object[] { TBC_SN,TBC_DATE,TBC_ITEM,TBC_BILL,TBC_PURCHASE,TBC_SALE,TBC_EXPENSE,TBC_PROFIT};
		visibleColumns2 = new Object[] { TBC_SN,TBC_ITEM,TBC_PURCHASE,TBC_SALE,TBC_EXPENSE,TBC_PROFIT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null, TBC_DATE, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,TBC_ITEM, null, Align.LEFT);
		table.addContainerProperty(TBC_BILL, Long.class, null, TBC_BILL, null,Align.CENTER);
		table.addContainerProperty(TBC_PURCHASE, Double.class, null, TBC_PURCHASE, null,Align.CENTER);
		table.addContainerProperty(TBC_SALE, Double.class, null, TBC_SALE, null,Align.CENTER);
		table.addContainerProperty(TBC_EXPENSE, Double.class, null, TBC_EXPENSE, null,Align.CENTER);
		table.addContainerProperty(TBC_PROFIT, Double.class, null, TBC_PROFIT, null,Align.CENTER);
		
		
		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, (float)2);
		table.setColumnExpandRatio(TBC_DATE, (float)1);
		table.setColumnExpandRatio(TBC_PURCHASE, (float)1);
		table.setColumnExpandRatio(TBC_SALE, (float)1);
		table.setColumnExpandRatio(TBC_EXPENSE, (float)1);
		table.setColumnExpandRatio(TBC_PROFIT, (float)1);
		
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns1);
		
		dao = new ManufacturingDao();
		mdao=new ManufacturingProfitReportDao();
		itemDao = new ItemDao();

		report = new Report(getLoginID());

		setSize(1050, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		// officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getMonthEndDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);
			List accountList = new ArrayList();
			accountList.add(new KeyValue((long) 1, "Sale Wise"));
			accountList.add(new KeyValue((long) 2, "Item Wise"));
			radio = new SRadioButton(null, 200, accountList, "key", "value");
			radio.setValue((long) 1);
			radio.setHorizontal(true);
			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(radio);
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

			mainPanel.setContent(mainHorizontal);

			radio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if((Long)radio.getValue()==1){
						table.setVisibleColumns(visibleColumns1);
					}
					else
						table.setVisibleColumns(visibleColumns2);
				}
			});
			
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
								e.printStackTrace();
							}
						}
					});

			officeComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								List itemsList = itemDao
										.getAllManufacturingItems((Long) officeComboField
												.getValue());
								ItemModel salesModel = new ItemModel(0,getPropertyName("all"));
								if (itemsList == null) {
									itemsList = new ArrayList<Object>();
								}
								itemsList.add(0, salesModel);

								SCollectionContainer bic1 = SCollectionContainer
										.setList(itemsList, "id");
								itemsComboField.setContainerDataSource(bic1);
								itemsComboField
										.setItemCaptionPropertyId("name");

							} catch (Exception e) {
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
			itemsComboField.setValue((long)0);
			
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
							if((Long)radio.getValue()==1){
								SalesNewUI sales = new SalesNewUI();
								sales.setCaption(getPropertyName("sale"));
								sales.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
								sales.center();
								getUI().getCurrent().addWindow(sales);
								sales.addCloseListener(closeListener);
							}
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
							if((Long)radio.getValue()==1){
								SalesModel sale=new SalesDao().getSale(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sale")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("sale"),sale.getSales_number()+""));
								form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
								form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
								form.addComponent(new SLabel(getPropertyName("payment"),sale.getPayment_amount() + ""));
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
							else{
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_manufacturing_profit")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("item"),itm.getItemProperty(TBC_ITEM).getValue().toString()));
								form.addComponent(new SLabel(getPropertyName("purchase"),itm.getItemProperty(TBC_PURCHASE).getValue().toString()));
								form.addComponent(new SLabel(getPropertyName("sales"),itm.getItemProperty(TBC_SALE).getValue().toString()));
								form.addComponent(new SLabel(getPropertyName("expense"),itm.getItemProperty(TBC_EXPENSE).getValue().toString()));
								form.addComponent(new SLabel(getPropertyName("profit"),itm.getItemProperty(TBC_PROFIT).getValue().toString()));
								form.setStyleName("grid_max_limit");
								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							List reportList = new ArrayList();
							List list;

							long itemID = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if((Long)radio.getValue()==1){
								reportList=mdao.getSalesWiseProfitReport(itemID,
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										(Long) officeComboField.getValue());
							}
							else{
								reportList=mdao.getItemWiseProfitReport(itemID,
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										(Long) officeComboField.getValue());
							}
							
							if(reportList.size()>0){
								ManufacturingProfitBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ManufacturingProfitBean)itr.next();
									if((Long)radio.getValue()==1){
										table.addItem(new Object[]{
												table.getItemIds().size()+1,
												bean.getNumber(),
												bean.getDate().toString(),
												bean.getItem(),
												bean.getId(),
												bean.getPurchase(),
												bean.getSale(),
												bean.getExpense(),
												(bean.getSale()-bean.getPurchase()-bean.getExpense())},table.getItemIds().size()+1);
									}
									else{
										table.addItem(new Object[]{
												table.getItemIds().size()+1,
												(long)0,
												"",
												bean.getItem(),
												(long)0,
												bean.getPurchase(),
												bean.getSale(),
												bean.getExpense(),
												(bean.getSale()-bean.getPurchase()-bean.getExpense())},table.getItemIds().size()+1);
									}
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							if((Long)radio.getValue()==1){
								table.setVisibleColumns(visibleColumns1);
							}
							else
								table.setVisibleColumns(visibleColumns2);
							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List reportList = new ArrayList();
							List list;

							long itemID = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if((Long)radio.getValue()==1){
								reportList=mdao.getSalesWiseProfitReport(itemID,
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										(Long) officeComboField.getValue());
							}
							else{
								reportList=mdao.getItemWiseProfitReport(itemID,
										CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
										(Long) officeComboField.getValue());
							}


							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								
								if((Long)radio.getValue()==1){
									report.setJrxmlFileName("ManufacturingProfitReport");
									report.setReportFileName("Manufacturing Profit Report");
									map.put("REPORT_TITLE_LABEL", getPropertyName("sales_wise_manufacturing_profit_report"));
								}
								else{
									report.setJrxmlFileName("ManufacturingItemProfitReport");
									report.setReportFileName("Manufacturing Profit Report");
									map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_manufacturing_profit_report"));
								}
								
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("BILL_LABEL", getPropertyName("bill"));
								map.put("PURCHASE_LABEL", getPropertyName("purchase"));
								map.put("SALE_LABEL", getPropertyName("sale"));
								map.put("EXPENSE_LABEL", getPropertyName("expense"));
								map.put("PROFIT_LABEL", getPropertyName("profit"));
								
								
								String subHeader = "";
								if (itemID != 0) {
									subHeader += getPropertyName("item")+" : "
											+ itemsComboField
													.getItemCaption(itemsComboField
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
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, null);

								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
