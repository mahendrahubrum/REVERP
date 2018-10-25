package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesReturnUI;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jul 22, 2014
 */
public class ItemWiseSalesReturnReportUI extends SparkLogic {

	private static final long serialVersionUID = 5345270248467029146L;
	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField itemGroupCombo ;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton generateConsolidatedButton;

	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	LedgerDao ledDao;

	private SNativeSelect stockType;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_BILL = "Bill";
	static String TBC_CUSTOMER = "Customer";
//	static String TBC_GOOD = "Good Qty";
//	static String TBC_WASTE = "Waste Qty";
	static String TBC_RETURN = "Return Qty";
	static String TBC_AMOUNT = "Amount";
	
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
	private HashMap<Long, String> currencyHashMap;
	
	@Override
	public SPanel getGUI() {
		
		try {
		
			allColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,/*,TBC_GOOD,TBC_WASTE,*/TBC_RETURN};
			visibleColumns = new Object[]  { TBC_SN, TBC_ITEM,/*,TBC_GOOD,TBC_WASTE,*/TBC_RETURN};
			
			allSubColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_BILL,TBC_DATE,TBC_CUSTOMER, /*TBC_GOOD,TBC_WASTE,*/TBC_RETURN, TBC_AMOUNT};
			visibleSubColumns = new Object[]  { TBC_SN, TBC_ITEM,TBC_BILL,TBC_DATE,TBC_CUSTOMER, /*TBC_GOOD,TBC_WASTE,*/TBC_RETURN, TBC_AMOUNT};
			popHor = new SHorizontalLayout();
			closeBtn = new SNativeButton("X");
			
			mainHorizontal=new SHorizontalLayout();
			popupContainer = new SHorizontalLayout();
			showButton=new SButton(getPropertyName("show"));
			
			table = new STable(null, 650, 250);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		/*	table.addContainerProperty(TBC_GOOD, Double.class, null,getPropertyName("good_qty"), null, Align.LEFT);
			table.addContainerProperty(TBC_WASTE, Double.class, null,getPropertyName("waste_qty"), null, Align.LEFT);*/
			table.addContainerProperty(TBC_RETURN, String.class, null,getPropertyName("return_qty"), null, Align.LEFT);
	
			table.setColumnExpandRatio(TBC_ITEM, 1);
//			table.setColumnExpandRatio(TBC_GOOD, 1);
//			table.setColumnExpandRatio(TBC_WASTE, 1);
			table.setColumnExpandRatio(TBC_RETURN, 1);
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setVisibleColumns(visibleColumns);
			
			subTable = new STable(null, 750, 250);
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			subTable.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("bill"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
//			subTable.addContainerProperty(TBC_GOOD, Double.class, null,getPropertyName("good_qty"), null, Align.LEFT);
//			subTable.addContainerProperty(TBC_WASTE, Double.class, null,getPropertyName("waste_qty"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_RETURN, String.class, null,getPropertyName("return_qty"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("amount"), null, Align.LEFT);
	
			subTable.setColumnExpandRatio(TBC_SN, (float) 0.3);
			subTable.setColumnExpandRatio(TBC_DATE, (float) 0.5);
			subTable.setColumnExpandRatio(TBC_ITEM, 1);
			subTable.setColumnExpandRatio(TBC_CUSTOMER, 2);
//			subTable.setColumnExpandRatio(TBC_GOOD, 1);
//			subTable.setColumnExpandRatio(TBC_WASTE, 1);
			subTable.setColumnExpandRatio(TBC_RETURN, 1);
			subTable.setColumnExpandRatio(TBC_AMOUNT, 1);
			subTable.setSelectable(true);
			subTable.setMultiSelect(false);
			subTable.setVisibleColumns(visibleSubColumns);
			
			
			ledDao = new LedgerDao();
	
			customerId = 0;
			report = new Report(getLoginID());
	
			setSize(1000, 380);
			mainPanel = new SPanel();
			mainPanel.setSizeFull();
	
			mainFormLayout = new SFormLayout();
			mainFormLayout.setSpacing(true);
			mainFormLayout.setMargin(true);
	
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

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			customerComboField = new SComboField(getPropertyName("customer"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);
			
			List itemGroupList = new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID());
			itemGroupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
			
			itemGroupCombo = new SComboField(getPropertyName("item_group"), 200, itemGroupList, "id", "name", true, getPropertyName("all"));
			itemGroupCombo.setValue((long)0);
			mainFormLayout.addComponent(itemGroupCombo);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);
			
			stockType = new SNativeSelect(getPropertyName("type"), 200,
					Arrays.asList(new KeyValue(0, getPropertyName("all")), new KeyValue(1,
							getPropertyName("good_stock")), new KeyValue(2, getPropertyName("GRV_stock")),
							new KeyValue(3, getPropertyName("purchase_returned")), new KeyValue(
									4, getPropertyName("waste"))), "intKey", "value");
			stockType.setValue(0);
	//		mainFormLayout.addComponent(stockType);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			generateConsolidatedButton = new SButton(getPropertyName("consolidated_report"));

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.addComponent(generateConsolidatedButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(generateConsolidatedButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(popHor);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainPanel.setContent(mainHorizontal);

			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
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
								reloadItemCombo();

								List<Object> customerList = ledDao
										.getAllCustomers((Long) officeComboField
												.getValue());
								LedgerModel ledgerModel = new LedgerModel();
								ledgerModel.setId(0);
								ledgerModel
										.setName(getPropertyName("all"));
								if (customerList == null) {
									customerList = new ArrayList<Object>();
								}
								customerList.add(0, ledgerModel);

								SCollectionContainer bic2 = SCollectionContainer
										.setList(customerList, "id");
								customerComboField.setContainerDataSource(bic2);
								customerComboField
										.setItemCaptionPropertyId("name");

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
			
			itemGroupCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					reloadItemCombo();
				}
			});

			final Action actionDelete = new Action(getPropertyName("edit"));
			
			subTable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (subTable.getValue() != null) {
							item = subTable.getItem(subTable.getValue());
							SalesReturnUI option=new SalesReturnUI();
							option.setCaption(getPropertyName("sales_return"));
							option.getSalesOrderNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
							Item itm = subTable.getItem(subTable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							SalesReturnModel sale=new SalesReturnDao().getSalesReturnModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales_return")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("bill_no"),sale.getReturn_no()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							//form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_note_no() + ""));
							form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
						//	form.addComponent(new SLabel(getPropertyName("paid_amount"),sale.getPaid_by_payment() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
//							grid.addComponent(new SLabel(null, getPropertyName("good_stock")), 2, 0);
//							grid.addComponent(new SLabel(null, getPropertyName("GRV_stock")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("return")), 4, 0);
//							grid.addComponent(new SLabel(null, getPropertyName("waste_qty")),	5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")),	6, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),7, 0);
							grid.setSpacing(true);
							
							int i = 1;
							SalesReturnInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(SalesReturnInventoryDetailsModel)itr.next();
								Item itim=table.getItem(table.getValue());
								long iem = (Long) itim.getItemProperty(TBC_ID).getValue();
								if(invObj.getItem().getId()!=iem)
									continue;
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
//								grid.addComponent(new SLabel(null, invObj.getGood_stock() + ""), 2, i);
//								grid.addComponent(new SLabel(null, invObj.getStock_quantity()+""), 3, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 4,	i);
//								grid.addComponent(new SLabel(null, invObj.getWaste_quantity() + ""),5, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol() + ""),6, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity() 
																- invObj.getDiscount()
																+ invObj.getTaxAmount())+ ""), 7, i);
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
							long custId = 0;
							List reportList= new SalesReportDao().getItemWiseSalesReturnDetails(
																	id,
																	custId,
																	CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																	CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																	(Long) officeComboField.getValue(),
																	toInt(stockType.getValue().toString()),(Long) itemGroupCombo.getValue());
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
											bean.getItem_name(),
											bean.getPaymentNo(),
											bean.getDt().toString(),
											bean.getClient_name(),
											bean.getReturnQty()+" "+bean.getUnit(),
											roundNumber(bean.getReturnQty() * bean.getRate())+" "+getCurrencyDescription(getCurrencyID())},subTable.getItemIds().size()+1);
									
								}
							}
							else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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
			

			showButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							List reportList;

							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("") && !itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (customerComboField.getValue() != null && !customerComboField.getValue() .equals("")) {
								custId = toLong(customerComboField.getValue().toString());
							}

							reportList = new SalesReportDao().showItemWiseSalesReturnDetails(
																itemID,
																custId,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																(Long) officeComboField.getValue(),toInt(stockType.getValue().toString()),(Long) itemGroupCombo.getValue());
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
								bean=(ReportBean)itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getId(),
										bean.getName(),									
										bean.getReturnQty()+" "+bean.getUnit()},table.getItemIds().size()+1);
								}
							}
							else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							table.setVisibleColumns(visibleColumns);
							
							
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			generateConsolidatedButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (officeComboField.getValue() != null) {


							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("") && !itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (customerComboField.getValue() != null && !customerComboField.getValue() .equals("")) {
								custId = toLong(customerComboField.getValue().toString());
							}

							List reportList = new SalesReportDao().showItemWiseSalesReturnDetails(
																itemID,
																custId,
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																(Long) officeComboField.getValue(),toInt(stockType.getValue().toString()),(Long) itemGroupCombo.getValue());
							if(reportList.size()>0){
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("Consolidated_4_Report");
								report.setReportFileName("ConsolidatedReport");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_sales_return_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("ITEM_LABEL", getPropertyName("item"));
//								map.put("GOOD_STOCK_LABEL", getPropertyName("good_quantity"));
//								map.put("WASTE_QUANTITY_LABEL", getPropertyName("waste_quantity"));
								map.put("RETURN_QUANTITY_LABEL", getPropertyName("return_quantity"));
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ customerComboField
													.getItemCaption(customerComboField
															.getValue()) + "\t";
								}
								if (toLong(itemGroupCombo.getValue().toString()) != 0) {
									subHeader += getPropertyName("group")
											+ " : "
											+ itemGroupCombo
													.getItemCaption(itemGroupCombo
															.getValue())+"\t ";
								}
								
								if (itemID != 0) {
									subHeader +=getPropertyName("item")+ " : "
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
							}
							else
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
					}
					catch (Exception e) {
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

			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List reportList;

							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}

							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.equals("")) {
								custId = toLong(customerComboField.getValue()
										.toString());
							}

							reportList = new SalesReportDao().getItemWiseSalesReturnDetails(
									itemID,
									custId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue(),toInt(stockType.getValue().toString()),(Long) itemGroupCombo.getValue());

							if (reportList!=null&&reportList.size() > 0) {

								Collections.sort(reportList,
										new Comparator<ReportBean>() {
											@Override
											public int compare(
													final ReportBean object1,
													final ReportBean object2) {

												int result = object2
														.getDt()
														.compareTo(
																object1.getDt());
												if (result == 0) {
													result = object1
															.getItem_name()
															.toLowerCase()
															.compareTo(
																	object2.getItem_name()
																			.toLowerCase());
												}
												return result;
											}

										});
								HashMap<String, Object> map = new HashMap<String, Object>();
								if(toInt(stockType.getValue().toString())!=0){
									report.setJrxmlFileName("ItemWiseSalesReturnReportTypeWise");
								}else{
									report.setJrxmlFileName("ItemWiseSalesReturnReport");
								}
								
								//Consolidated_4_Report
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_sales_return_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
//								map.put("GOOD_STOCK_LABEL", getPropertyName("good_stock"));
//								map.put("GOOD_STOCK_VALUE_LABEL", getPropertyName("good_stock_value"));
								map.put("GRV_QUANTITY_LABEL", getPropertyName("grv_quantity"));
//								map.put("GRV_STOCK_VALUE_LABEL", getPropertyName("grv_stock_value"));
//								map.put("WASTE_QUANTITY_LABEL", getPropertyName("waste_quantity"));
//								map.put("WASTE_STOCK_VALUE_LABEL", getPropertyName("waste_stock_value"));
								map.put("PURCHASE_RETURN_QUANTITY_LABEL", getPropertyName("return_quantity"));
//								map.put("PURCHASE_RETURN_VALUE_LABEL", getPropertyName("purchase_return_value"));
//								map.put("TOTAL_LABEL", getPropertyName("total"));
//								map.put("GRV_STOCK_LABEL", getPropertyName("GRV_stock"));
//								map.put("PURCHASE_RETURNED_LABEL", getPropertyName("purchase_returned"));
//								map.put("VALUE_LABEL", getPropertyName("value"));
								
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("customer")+" : "
											+ customerComboField
													.getItemCaption(customerComboField
															.getValue()) + "\t";
								}
								
								if (toLong(itemGroupCombo.getValue().toString()) != 0) {
									subHeader += getPropertyName("group")
											+ " : "
											+ itemGroupCombo
													.getItemCaption(itemGroupCombo
															.getValue())+"\t";
								}
								if (itemID != 0) {
									subHeader +=getPropertyName("item")+ " : "
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

	private void reloadItemCombo() {
		try {

			List itemList = new ItemDao().getAllActiveItemsWithAppendingItemCode((Long) officeComboField.getValue(),
					false, (Long)itemGroupCombo.getValue());
			ItemModel salesModel = new ItemModel(0, getPropertyName("all"));
			if (itemList == null) {
				itemList = new ArrayList<Object>();
			}
			itemList.add(0, salesModel);

			SCollectionContainer bic1 = SCollectionContainer.setList(itemList,
					"id");
			itemsComboField.setContainerDataSource(bic1);
			itemsComboField.setItemCaptionPropertyId("name");
			itemsComboField.setValue((long)0);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
