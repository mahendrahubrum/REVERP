package com.inventory.reports.ui;

import java.util.ArrayList;
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
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.ui.SalesOrderNewUI;
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
 * @author Jinshad P.T. Inventory Dec 30, 2013
 */
public class ItemWiseCustomerSalesOrderReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

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

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;
	
	LedgerDao ledDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_QUANTITY = "Qty";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_TOTAL = "Total";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	

	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_ITEM, TBC_QUANTITY, TBC_CUSTOMER, TBC_TOTAL };
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_ITEM, TBC_QUANTITY, TBC_CUSTOMER, TBC_TOTAL };
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_QUANTITY, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBC_TOTAL, String.class, null,getPropertyName("total"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
		table.setColumnExpandRatio(TBC_ITEM, 2);
		table.setColumnExpandRatio(TBC_QUANTITY, (float) 0.5);
		table.setColumnExpandRatio(TBC_CUSTOMER, 2);
		table.setColumnExpandRatio(TBC_TOTAL, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		ledDao = new LedgerDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1050, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

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
		// mainFormLayout.addComponent(officeComboField);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			customerComboField = new SComboField(getPropertyName("customer"),
					200);

			mainFormLayout.addComponent(customerComboField);
			
			List itemGroupList = new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID());
			itemGroupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
			
			itemGroupCombo = new SComboField(getPropertyName("item_group"), 200, itemGroupList, "id", "name", true, getPropertyName("all"));
			itemGroupCombo.setValue((long)0);
			mainFormLayout.addComponent(itemGroupCombo);

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
								e.printStackTrace();
							}
						}
					});

			officeComboField.addValueChangeListener(new Property.ValueChangeListener() {
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
						customerComboField.setItemCaptionPropertyId("name");

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
			
			itemGroupCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					reloadItemCombo();
				}
			});
			
			
			customerComboField.setValue((long)0);
			itemsComboField.setValue((long)0);
			

			showButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						if (officeComboField.getValue() != null) {

							List<Object> reportList;

							long itemID = 0;
							long custId = 0;
							table.setVisibleColumns(allColumns);
							if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("") && !itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (customerComboField.getValue() != null && !customerComboField.getValue().equals("")) {
								custId = toLong(customerComboField.getValue().toString());
							}
							reportList = new SalesReportDao().showItemWiseSalesOrderDetails(
																							itemID,
																							custId,
																							CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																							CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																							(Long) officeComboField.getValue(),(Long) itemGroupCombo.getValue());

							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getDt().toString(),
											bean.getItem_name(),
											bean.getQuantity(),
											bean.getClient_name(),
											(bean.getTotal()+" "+bean.getUnit())},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show("No Data Available",Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
							setRequiredError(officeComboField, null, false);
						} 
						else {
							setRequiredError(officeComboField,getPropertyName("invalid_selection"), true);
						}
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
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							SalesOrderNewUI option=new SalesOrderNewUI();
							option.setCaption(getPropertyName("sales_order"));
							option.getSalesNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							SalesOrderModel mdl=new SalesOrderDao().getSalesOrderModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales_order")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_order_no"),mdl.getOrder_no()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("quantity"),item.getItemProperty(TBC_QUANTITY).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("total"),item.getItemProperty(TBC_TOTAL).getValue().toString()));
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
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List<Object> reportList;

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
							reportList = new SalesReportDao().
												getItemWiseCustomerSalesOrderDetails(
														itemID,
														custId,
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														(Long) officeComboField.getValue(),(Long) itemGroupCombo.getValue());

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								
								report.setJrxmlFileName("ItemWiseCustomerSalesOrder_Report");
								report.setReportFileName("ItemWise Customer Sales Order Report");
//								report.setReportTitle("ItemWise Customer Sales Order Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_customer_sales_order_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								map.put("CUSTOMER_LABEL", getPropertyName("customer"));
								map.put("STATUS_LABEL", getPropertyName("status"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
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
															.getValue());
								}
								if (itemID != 0) {
									subHeader += getPropertyName("customer")+" : "
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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
