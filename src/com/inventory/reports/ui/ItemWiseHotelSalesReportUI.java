package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hotel.service.dao.HotelSalesDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.dao.SalesReportDao;
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
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 30-Sep-2015
 */
public class ItemWiseHotelSalesReportUI extends SparkLogic {

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

	private SButton generateButton;

	private Report report;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_QUANTITY = "Qty";
	static String TBC_RATE = "Rate";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_TOTAL = "Total Qty";
	static String TBC_CURRENT = "Current Stock";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;
	
	HotelSalesDao saleDao;
	
	@SuppressWarnings({ "deprecation", "serial" })
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_ITEM, TBC_QUANTITY};
		visibleColumns = new Object[]  { TBC_SN, TBC_ITEM, TBC_QUANTITY};
		allSubColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_ITEM, TBC_QUANTITY,TBC_RATE, TBC_CUSTOMER, TBC_TOTAL,TBC_CURRENT };
		visibleSubColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_ITEM, TBC_QUANTITY,TBC_RATE, TBC_CUSTOMER, TBC_TOTAL,TBC_CURRENT };
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		mainHorizontal=new SHorizontalLayout();
		mainHorizontal.setSpacing(true);
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		
		saleDao=new HotelSalesDao();
		report = new Report(getLoginID());

		setSize(1000, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
//		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		// officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		try {
			table = new STable(null, 650, 250);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
			table.addContainerProperty(TBC_QUANTITY, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
			table.setColumnExpandRatio(TBC_ITEM, 2);
			table.setColumnExpandRatio(TBC_QUANTITY, (float) 0.5);
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setVisibleColumns(visibleColumns);
			
			
			subTable = new STable(null, 750, 250);
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_QUANTITY, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("rate"), null, Align.RIGHT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_TOTAL, String.class, null,getPropertyName("total"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CURRENT, String.class, null,getPropertyName("current_balance"), null, Align.LEFT);

			subTable.setColumnExpandRatio(TBC_SN, (float) 0.3);
			subTable.setColumnExpandRatio(TBC_DATE, (float) 0.8);
			subTable.setColumnExpandRatio(TBC_ITEM, 2);
			subTable.setColumnExpandRatio(TBC_QUANTITY, (float) 0.5);
			subTable.setColumnExpandRatio(TBC_RATE, (float) 0.6);
			subTable.setColumnExpandRatio(TBC_CUSTOMER, 2);
			subTable.setColumnExpandRatio(TBC_TOTAL, (float) 1);
			subTable.setColumnExpandRatio(TBC_CURRENT, (float) 1);
			subTable.setSelectable(true);
			subTable.setMultiSelect(false);
			subTable.setVisibleColumns(visibleSubColumns);
			
			
			organizationComboField = new SComboField(getPropertyName("organization"), 200,new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(popHor);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			mainPanel.setContent(mainHorizontal);

			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});
			
			organizationComboField
					.addListener(new Property.ValueChangeListener() {
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

			officeComboField.addListener(new Property.ValueChangeListener() {
				@SuppressWarnings("unchecked")
				public void valueChange(ValueChangeEvent event) {
					try {

						List itemsList = new ItemDao()
								.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
										.getValue());
						ItemModel salesModel = new ItemModel(0,getPropertyName("all"));
						if (itemsList == null) {
							itemsList = new ArrayList<Object>();
						}
						itemsList.add(0, salesModel);

						SCollectionContainer bic1 = SCollectionContainer
								.setList(itemsList, "id");
						itemsComboField.setContainerDataSource(bic1);
						itemsComboField.setItemCaptionPropertyId("name");

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

			
			final Action actionDelete = new Action("Edit");
			
			
			
			subTable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (subTable.getValue() != null) {
							item = subTable.getItem(subTable.getValue());
							SalesNewUI option=new SalesNewUI();
							option.setCaption(getPropertyName("sales"));
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							popUp.setPopupVisible(false);
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
					return new Action[] { actionDelete };
				}
			});
			
			
			
			subTable.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item item = subTable.getItem(subTable.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							SalesModel sale=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
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
								Item itm=table.getItem(table.getValue());
								long iem = (Long) itm.getItemProperty(TBC_ID).getValue();
								if(invObj.getItem().getId()!=iem)
									continue;
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity())+ ""), 6, i);
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
							List reportList;

							reportList =saleDao.showItemWiseSalesDetails(
															id,
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
															(Long) officeComboField.getValue());
							subTable.removeAllItems();
							subTable.setVisibleColumns(allSubColumns);
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									subTable.addItem(new Object[]{
											subTable.getItemIds().size()+1,
											bean.getId(),
											bean.getDt().toString(),
											bean.getItem_name(),
											bean.getQuantity(),
											bean.getRate(),
											bean.getClient_name(),
											(bean.getTotal()+" "+bean.getUnit()),
											bean.getDescription()+" "+bean.getUnit()},subTable.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Sales Details",725), closeBtn), subTable));

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
			
			
			
			showButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						if (officeComboField.getValue() != null) {

							List reportList;

							long itemID = 0;
							table.setVisibleColumns(allColumns);
							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}


							reportList = saleDao.showItemWiseSalesDetailsConsolidated(
																itemID,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																(Long) officeComboField.getValue());

							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getName(),
											roundNumber(bean.getAmount())},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							
							table.setVisibleColumns(visibleColumns);
							setRequiredError(officeComboField, null, false);
						} 
						else {
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

							List reportList;

							long itemID = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}


							reportList =saleDao
									.getItemWiseSalesDetails(
											itemID,
											CommonUtil
													.getSQLDateFromUtilDate(fromDateField
															.getValue()),
											CommonUtil
													.getSQLDateFromUtilDate(toDateField
															.getValue()),
											(Long) officeComboField.getValue());

							if (reportList.size() > 0) {

								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("ItemWiseSales_Report");
								report.setReportFileName("ItemWise Sales Report");
//								report.setReportTitle("ItemWise Sales Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_sales_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
								map.put("SALES_NO_LABEL", getPropertyName("sales_no"));
								map.put("RATE_LABEL", getPropertyName("rate"));
								map.put("TOTAL_QUANTITY_LABEL", getPropertyName("total_quantity"));
								map.put("CURRENT_STOCK_LABEL", getPropertyName("current_stock"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								
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
								report.createReport(reportList, map);

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
