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
import com.inventory.config.stock.dao.SupplierQuotationDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.SupplierQuotationDetailsModel;
import com.inventory.config.stock.model.SupplierQuotationModel;
import com.inventory.config.stock.ui.SupplierQuotationUI;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesReturnNewUI;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
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
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.CountryDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.CountryModel;

/**
 * @author Jinshad P.T. Inventory Jan 21, 2014
 */
public class SupplierQuotationReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
	private SComboField itemsComboField;
	private SComboField countryComboField;
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

	private SRadioButton sortBy;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DID = "DID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_BILL = "Quotation No";
	static String TBC_CUSTOMER = "Supplier";
	static String TBC_COUNTRY = "Country";
	static String TBC_AMOUNT = "Rate";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DID,TBC_ITEM,TBC_BILL,TBC_DATE,TBC_CUSTOMER, TBC_COUNTRY, TBC_AMOUNT};
		visibleColumns = new Object[]  { TBC_SN,TBC_ITEM,TBC_BILL,TBC_DATE,TBC_CUSTOMER, TBC_COUNTRY, TBC_AMOUNT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DID, Long.class, null, TBC_DID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_BILL, Long.class, null,getPropertyName("quotation_no"), null, Align.LEFT);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
		table.addContainerProperty(TBC_COUNTRY, String.class, null,getPropertyName("country"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, String.class, null,getPropertyName("rate"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
		table.setColumnExpandRatio(TBC_ITEM, 1);
		table.setColumnExpandRatio(TBC_CUSTOMER, 2);
		table.setColumnExpandRatio(TBC_COUNTRY, 1);
		table.setColumnExpandRatio(TBC_AMOUNT, 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		
		
		customerId = 0;
		report = new Report(getLoginID());

		ledDao = new LedgerDao();

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

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);

			List countryList = new CountryDao().getCountry();
			if (countryList == null) {
				countryList = new ArrayList();
				countryList.add(0, new CountryModel(0, getPropertyName("all")));
			} else
				countryList.add(0, new CountryModel(0, getPropertyName("all")));

			countryComboField = new SComboField(getPropertyName("country"),
					200, countryList, "id", "name");
			countryComboField.setValue((long) 0);

			supplierComboField = new SComboField(getPropertyName("supplier"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(supplierComboField);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);
			mainFormLayout.addComponent(countryComboField);
			
			sortBy = new SRadioButton(getPropertyName("affect_type"), 200,
					Arrays.asList(new KeyValue(1,"Supplier"),new KeyValue(2,"Item")), "intKey", "value");
			sortBy.setHorizontal(true);
			sortBy.setValue(1);
			mainFormLayout.addComponent(sortBy);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
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

						List<Object> customerList;
							customerList = ledDao
									.getAllSuppliersWithLoginID((Long) officeComboField
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
						supplierComboField.setContainerDataSource(bic2);
						supplierComboField.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						// TODO Auto-generated catch block
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
							SupplierQuotationUI option=new SupplierQuotationUI();
							option.setCaption(getPropertyName("supplier_quotation"));
							option.getSupplierQuotationCombo().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							long did = (Long) itm.getItemProperty(TBC_DID).getValue();
							SupplierQuotationModel sale=new SupplierQuotationDao().getQuotationModel(id);
							SupplierQuotationDetailsModel det=new SupplierQuotationDao().getDetailModel(did);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("supplier_quotation")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("quotation_no"),sale.getQuotation_number()+""));
							form.addComponent(new SLabel(getPropertyName("supplier"),new UserManagementDao().getUserNameFromLoginID(sale.getLogin_id())));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							form.addComponent(new SLabel(getPropertyName("country"), new CountryDao().getCountryName(det.getCountryId())));
							form.addComponent(new SLabel(getPropertyName("paid_amount"),det.getRate()+" "+det.getCurrency().getCode()));
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

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							String items = "";

							List reportList;

							long itemID = 0, suplId = 0, country_id = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null
									&& !supplierComboField.getValue()
											.equals("")) {
								suplId = toLong(supplierComboField.getValue()
										.toString());
							}
							if (countryComboField.getValue() != null) {
								country_id = (Long) countryComboField
										.getValue();
							}
							reportList = new SupplierQuotationDao().getQuotationReport(
									(Long) officeComboField.getValue(),
									itemID,
									suplId,
									country_id,
									CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()));
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getStatus(),
											bean.getName(),
											bean.getNumber(),
											bean.getDt().toString(),
											bean.getClient_name(),
											bean.getCountry(),
											bean.getRate()+" "+bean.getCurrency()},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show("No Data Availabale",Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);

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

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							String items = "";

							List reportList;

							long itemID = 0, suplId = 0, country_id = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null
									&& !supplierComboField.getValue()
											.equals("")) {
								suplId = toLong(supplierComboField.getValue()
										.toString());
							}
							if (countryComboField.getValue() != null) {
								country_id = (Long) countryComboField
										.getValue();
							}
							reportList = new SupplierQuotationDao().getQuotationReport(
									(Long) officeComboField.getValue(),
									itemID,
									suplId,
									country_id,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()));

							if (reportList.size() > 0) {
								
								
								Collections.sort(reportList, new Comparator<ReportBean>() {
									@Override
									public int compare(final ReportBean object1,final ReportBean object2) {
										
										int result = 0;
										object2 .getDt() .compareTo(object2.getDt()); 
										
										 	if((Integer)sortBy.getValue()==2){
										 		result = object1.getName().compareTo(object2.getName());
										 		if (result == 0) 
										 			result=object2 .getDt() .compareTo(object2.getDt());
										 	}else{
										 		if(object1.getLogin()!=null&&object2.getLogin()!=null)
										 			result = object1.getLogin().compareTo(object2.getLogin());
										 		if (result == 0) 
										 			result=object2 .getDt() .compareTo(object2.getDt());
										 	}
									     return result;
									}
								});
								 
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("SupplierQuotation_Report");
								report.setReportFileName("Supplier Quotation Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("supplier_quotation_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("COUNTRY_LABEL", getPropertyName("country"));
								map.put("QUOTATION_LABEL", getPropertyName("quotation_no"));
								map.put("RATE_LABEL", getPropertyName("rate"));
								
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("supplier")+" : "
											+ supplierComboField
													.getItemCaption(supplierComboField
															.getValue()) + "\t";
								}
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
			supplierComboField.setContainerDataSource(custContainer);
			supplierComboField.setItemCaptionPropertyId("name");
			supplierComboField.setValue(0);
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
