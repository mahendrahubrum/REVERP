package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseInquiryDao;
import com.inventory.purchase.model.PurchaseInquiryModel;
import com.inventory.purchase.ui.PurchaseInquiryUI;
import com.inventory.reports.bean.ItemWiseReportBean;
import com.inventory.reports.dao.ItemWiseReportDao;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
/**
 * 
 * @author Muhammed shah
 * @date Nov 6, 2015
 * @Project REVERP
 */
public class ItemWisePurchaseInquiryReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

//	private SCollectionContainer container;
//	private SCollectionContainer custContainer;
//
//	private long customerId;

	private Report report;

	LedgerDao ledDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_QUANTITY = "Quantity";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_TOTAL = "Total";
	SHorizontalLayout popupContainer;
	SHorizontalLayout mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	private ItemWiseReportDao itemWiseReportDao;

	private OfficeDao officeDao;

	private ItemDao itemDao;

	private PurchaseInquiryDao purchaseInquiryDao;
	
	@SuppressWarnings({  "serial" })
	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_SUPPLIER,TBC_ITEM, TBC_QUANTITY,  TBC_TOTAL };
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE, TBC_SUPPLIER,TBC_ITEM,TBC_QUANTITY,  TBC_TOTAL };
		popupContainer = new SHorizontalLayout();
		mainHorizontal=new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);
		
		itemWiseReportDao = new ItemWiseReportDao();
		officeDao = new OfficeDao();
		itemDao = new ItemDao();
		purchaseInquiryDao = new PurchaseInquiryDao();
		
		table = new STable(null, 750, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_QUANTITY, String.class, null,getPropertyName("quantity"), null, Align.RIGHT);
		table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
		table.addContainerProperty(TBC_TOTAL, String.class, null,getPropertyName("total"), null, Align.RIGHT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.5);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
		table.setColumnExpandRatio(TBC_ITEM, 2);
		table.setColumnExpandRatio(TBC_QUANTITY, (float) 1);
		table.setColumnExpandRatio(TBC_SUPPLIER, 2);
		table.setColumnExpandRatio(TBC_TOTAL, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);

//		customerId = 0;
		report = new Report(getLoginID());

		setSize(1200, 360);
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

			supplierComboField = new SComboField(getPropertyName("supplier"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(supplierComboField);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.SPACEBAR);

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
					.addValueChangeListener(new ValueChangeListener() {
						@SuppressWarnings("rawtypes")
						public void valueChange(ValueChangeEvent event) {

							try {

								SCollectionContainer bic = SCollectionContainer.setList(
										officeDao
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

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void valueChange(ValueChangeEvent event) {
					try {

						List itemsList = itemDao
								.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
										.getValue());
						ItemModel itemModel = new ItemModel(0,
								 "------- "+getPropertyName("all")+" ---------");
						if (itemsList == null) {
							itemsList = new ArrayList<Object>();
						}
						itemsList.add(0, itemModel);

						SCollectionContainer bic1 = SCollectionContainer
								.setList(itemsList, "id");
						itemsComboField.setContainerDataSource(bic1);
						itemsComboField.setItemCaptionPropertyId("name");
						itemsComboField.setValue((long)0);

						List<Object> customerList = ledDao
								.getAllSuppliers((Long) officeComboField
										.getValue());
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel.setName("------- "+getPropertyName("all")+" ---------");
						if (customerList == null) {
							customerList = new ArrayList<Object>();
						}
						customerList.add(0, ledgerModel);

						SCollectionContainer bic2 = SCollectionContainer
								.setList(customerList, "id");
						supplierComboField.setContainerDataSource(bic2);
						supplierComboField.setItemCaptionPropertyId("name");
						supplierComboField.setValue((long)0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
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
				
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {
					//		boolean noData = true;
//							SalesModel salesModel = null;
//							SalesInventoryDetailsModel inventoryDetailsModel = null;
//							SalesReportBean reportBean = null;
//							String items = "";
							List<Object> reportList;
							long itemID = 0;
							long supplierId = 0;
							long officeId = 0;

							if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("") && !itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null && !supplierComboField.getValue().equals("")) {
								supplierId = toLong(supplierComboField.getValue().toString());
							}
							if (officeComboField.getValue() != null && !officeComboField.getValue().equals("")) {
								officeId = toLong(officeComboField.getValue().toString());
							}
							
							reportList = itemWiseReportDao.getItemwisePurchaseInquiryDetails(officeId, 
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
									supplierId, itemID, "");
							if(reportList.size()>0){
								ItemWiseReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean = (ItemWiseReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											CommonUtil.formatDateToDDMMYYYY(bean.getDate()),
											bean.getSupplierOrCustomer(),
											bean.getItem(),
											bean.getQuantity()+" "+bean.getUnit(),
											(bean.getTotal()+" "+bean.getUnit())},
											
											table.getItemIds().size()+1);
								}
							}
							else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
							
							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}
					} 
					catch (Exception e) {
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
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							PurchaseInquiryUI option=new PurchaseInquiryUI();
							option.setCaption(getPropertyName("purchase_inquiry"));
							option.getPurchaseOrderNumberList().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
							PurchaseInquiryModel mdl=purchaseInquiryDao.getPurchaseInquiryModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase_inquiry")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("inquiry_no"),mdl.getInquiry_no()+""));
							form.addComponent(new SLabel(getPropertyName("supplier"),item.getItemProperty(TBC_SUPPLIER).getValue().toString()));
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

				@SuppressWarnings({ "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

						/*	boolean noData = true;
							SalesModel salesModel = null;
							SalesInventoryDetailsModel inventoryDetailsModel = null;
							SalesReportBean reportBean = null;
							String items = "";*/

							List<Object> reportList;

							long itemID = 0;
							long supplierId = 0;
							long officeId = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null
									&& !supplierComboField.getValue()
											.equals("")) {
								supplierId = toLong(supplierComboField.getValue()
										.toString());
							}
							if (officeComboField.getValue() != null
									&& !officeComboField.getValue()
											.equals("")) {
								officeId = toLong(officeComboField.getValue()
										.toString());
							}
							reportList = itemWiseReportDao.getItemwisePurchaseInquiryDetails(officeId, 
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
									supplierId, itemID, "");
							if(reportList.size()>0){
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("ItemWiseReport");
								report.setReportFileName("ItemWise Purchase Inquiry Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_purchase_inquiry_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								map.put("SUPPLIER_OR_CUSTOMER_LABEL", getPropertyName("supplier"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								
								
								
								String subHeader = "";
//								if (supplierId != 0) {
//									subHeader += getPropertyName("supplier")+" : "
//											+ supplierComboField
//													.getItemCaption(supplierComboField
//															.getValue()) + "\t";
//								}
//								if (itemID != 0) {
//									subHeader += getPropertyName("item")+" : "
//											+ itemsComboField
//													.getItemCaption(itemsComboField
//															.getValue());
//								}

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
							else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
							

//							if (reportList.size() > 0) {
//								
//
//							} else {
//								SNotification.show(
//										getPropertyName("no_data_available"),
//										Type.WARNING_MESSAGE);
//							}
//
//							setRequiredError(officeComboField, null, false);
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

	/*
	 * private void loadBillNo(long customerId, long officeId) { List<Object>
	 * salesList = null; try { if (customerId != 0) { salesList = new SalesDao()
	 * .getAllSalesNumbersForSupplier(officeId, customerId,
	 * CommonUtil.getSQLDateFromUtilDate(fromDateField .getValue()), CommonUtil
	 * .getSQLDateFromUtilDate(toDateField .getValue())); } else { salesList =
	 * new SalesReportDao() .getAllSalesNumbersAsComment(officeId); } SalesModel
	 * salesModel = new SalesModel(); salesModel.setId(0); salesModel
	 * .setComments("---------------------ALL-------------------"); if
	 * (salesList == null) { salesList = new ArrayList<Object>(); }
	 * salesList.add(0, salesModel); container =
	 * SCollectionContainer.setList(salesList, "id");
	 * itemsComboField.setContainerDataSource(container);
	 * itemsComboField.setItemCaptionPropertyId("comments");
	 * itemsComboField.setValue(0); } catch (Exception e) { e.printStackTrace();
	 * } }
	 */

//	protected void loadCustomerCombo(long officeId) {
//		List<Object> custList = null;
//		try {
//			if (officeId != 0) {
//				custList = new LedgerDao().getAllCustomers(officeId);
//			} else {
//				custList = new LedgerDao().getAllCustomers();
//			}
//			LedgerModel ledgerModel = new LedgerModel();
//			ledgerModel.setId(0);
//			ledgerModel.setName( getPropertyName("all"));
//			if (custList == null) {
//				custList = new ArrayList<Object>();
//			}
//			custList.add(0, ledgerModel);
//			custContainer = SCollectionContainer.setList(custList, "id");
//			supplierComboField.setContainerDataSource(custContainer);
//			supplierComboField.setItemCaptionPropertyId("name");
//			supplierComboField.setValue(0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
