package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.ItemWiseNoSalesReportBean;
import com.inventory.reports.dao.ItemWiseNoSalesReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
 * @author Jinshad P.T. Inventory Nov 20, 2013
 */
public class ItemWiseNoSalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout,mainHorizontal;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	
	ItemWiseNoSalesReportDao dao;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	LedgerDao ledDao;
	
	static String TBC_SN = "SN";
	static String TBC_ITEM = "Item";
	static String TBC_CLOSING_STOCK = "Closing Stock";
	static String TBC_SUPPLIER = "Supplier";
//	static String TBC_DATE = "Last Sold Date";
	SHorizontalLayout popupContainer;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ITEM, TBC_CLOSING_STOCK, TBC_SUPPLIER};
		visibleColumns = new Object[] { TBC_SN, TBC_ITEM, TBC_CLOSING_STOCK, TBC_SUPPLIER};
		popupContainer = new SHorizontalLayout();
		ledDao = new LedgerDao();
		customerId = 0;
		report = new Report(getLoginID());

		dao=new ItemWiseNoSalesReportDao();
		
		setSize(1000, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainHorizontal=new SHorizontalLayout();
		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
//		mainFormLayout.setMargin(true);

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
			table = new STable(null, 650, 250);
			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
			table.addContainerProperty(TBC_CLOSING_STOCK, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
			table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
//			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("last_sold_date"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
//			table.setColumnExpandRatio(TBC_DATE, (float) 1);
			table.setColumnExpandRatio(TBC_ITEM, 2);
			table.setColumnExpandRatio(TBC_CLOSING_STOCK, (float) 1);
			table.setColumnExpandRatio(TBC_SUPPLIER, 2);
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setVisibleColumns(visibleColumns);
			
			organizationComboField = new SComboField(getPropertyName("organization"), 200,new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);
			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);
			customerComboField = new SComboField(getPropertyName("customer"),200);

//			mainFormLayout.addComponent(customerComboField);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			showButton = new SButton(getPropertyName("show"));
			showButton.setClickShortcut(KeyCode.ENTER);

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
				public void valueChange(ValueChangeEvent event) {
					try {

						List itemsList = new ItemDao()
								.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
										.getValue());
						ItemModel salesModel = new ItemModel(0,"---------------------ALL-------------------");
						if (itemsList == null) {
							itemsList = new ArrayList<Object>();
						}
						itemsList.add(0, salesModel);

						SCollectionContainer bic1 = SCollectionContainer
								.setList(itemsList, "id");
						itemsComboField.setContainerDataSource(bic1);
						itemsComboField.setItemCaptionPropertyId("name");

						List<Object> customerList = ledDao
								.getAllCustomers((Long) officeComboField
										.getValue());
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel.setName("---------------------ALL-------------------");
						if (customerList == null) {
							customerList = new ArrayList<Object>();
						}
						customerList.add(0, ledgerModel);

						SCollectionContainer bic2 = SCollectionContainer
								.setList(customerList, "id");
						customerComboField.setContainerDataSource(bic2);
						customerComboField.setItemCaptionPropertyId("name");

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
			
			showButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							List<Object> reportList;
							
							reportList = dao.getNotSoldItems((Long)itemsComboField.getValue(), 
																CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
																(Long) officeComboField.getValue());

							if(reportList.size()>0){
								ItemStockModel stkmdl=null;
								PurchaseModel pur=null;
								Iterator<Object> itr=reportList.iterator();
								while(itr.hasNext()){
									String name="";
									stkmdl=(ItemStockModel)itr.next();
									pur=new PurchaseDao().getPurchaseModel(stkmdl.getPurchase_id());
									if(pur!=null)
										name=pur.getSupplier().getName();
									else
										name="";
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											stkmdl.getItem().getName(),
											stkmdl.getBalance(),
											name},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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

			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("stock")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("supplier"),item.getItemProperty(TBC_SUPPLIER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("closing_stock"),item.getItemProperty(TBC_CLOSING_STOCK).getValue().toString()));
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
			
			organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());
			customerComboField.setValue((long)0);
			itemsComboField.setValue((long)0);

			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						ItemWiseNoSalesReportBean bean;
						if (officeComboField.getValue() != null) {
							List<Object> reportList = new ArrayList<Object>();
							List resultList=null;
							resultList = dao.getNotSoldItems((Long)itemsComboField.getValue(), 
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
									(Long) officeComboField.getValue());
							if(resultList.size()>0){
								ItemStockModel stkmdl=null;
								PurchaseModel pur=null;
								Iterator<Object> itr=resultList.iterator();
								while(itr.hasNext()){
									String name="";
									stkmdl=(ItemStockModel)itr.next();
									pur=new PurchaseDao().getPurchaseModel(stkmdl.getPurchase_id());
									if(pur!=null)
										name=pur.getSupplier().getName();
									else
										name="";
									bean=new ItemWiseNoSalesReportBean(stkmdl.getItem().getName(), name, stkmdl.getBalance());
									reportList.add(bean);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}

							if (reportList.size() > 0) {
								report.setJrxmlFileName("ItemWiseNoSalesReport");
								report.setReportFileName("Item Wise No Sales Report");
								report.setReportTitle("Item Wise No Sales Report");
								String subHeader = "";
								subHeader += "\n From : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t To : "
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
			ledgerModel.setName("---------------------ALL-------------------");
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
