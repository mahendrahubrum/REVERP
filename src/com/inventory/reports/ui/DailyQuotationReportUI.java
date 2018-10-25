package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.ui.DailyQuotationUI;
import com.inventory.reports.bean.DailyQuotationBean;
import com.inventory.reports.dao.DailyQuotationReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
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
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.CountryDao;
import com.webspark.uac.model.CountryModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 18, 2013
 */
public class DailyQuotationReportUI extends SparkLogic {

	private static final long serialVersionUID = -3605069202090520021L;
	private SOfficeComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
	private SComboField itemComboField;
	private SComboField countryComboField;
	private SReportChoiceField reportChoiceField;
	private Report report;
	private SComboField userComboField;
	private SButton generateButton;

	private SCollectionContainer userContainer;
	private SCollectionContainer supplierContainer;
	private SCollectionContainer itemContainer;

	private DailyQuotationReportDao dao;
	
	private SRadioButton sortBy;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_EMPLOYEE = "Employee";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_ITEM = "Item";
	static String TBC_COUNTRY = "Country";
	static String TBC_UNIT = "Unit";
	static String TBC_RATE = "Rate";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DATE ,TBC_EMPLOYEE,TBC_SUPPLIER, TBC_ITEM,TBC_COUNTRY,TBC_UNIT,TBC_RATE};
		visibleColumns = new Object[] { TBC_SN,TBC_DATE ,TBC_EMPLOYEE,TBC_SUPPLIER, TBC_ITEM,TBC_COUNTRY,TBC_UNIT,TBC_RATE};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"), null,Align.CENTER);
		table.addContainerProperty(TBC_EMPLOYEE, String.class, null,getPropertyName("employee"), null, Align.LEFT);
		table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_COUNTRY, String.class, null,getPropertyName("country"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
		table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("rate"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, (float)1);
		table.setColumnExpandRatio(TBC_EMPLOYEE, (float) 1.5);
		table.setColumnExpandRatio(TBC_SUPPLIER, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		SPanel mainPanel;

		SFormLayout mainFormLayout;
		SHorizontalLayout dateHorizontalLayout;
		SHorizontalLayout buttonHorizontalLayout;

		report = new Report(getLoginID());

		setSize(1050, 410);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		dao = new DailyQuotationReportDao();

		try {
			officeComboField = new SOfficeComboField(getPropertyName("office"),
					200, getOrganizationID());

			userComboField = new SComboField(getPropertyName("employee"), 200);
			userComboField.setInputPrompt(getPropertyName("all"));
			loadUsers((Long) officeComboField.getValue());

			supplierComboField = new SComboField(getPropertyName("supplier"),
					200);
			supplierComboField
					.setInputPrompt(getPropertyName("all"));
			loadSuppliers((Long) officeComboField.getValue());

			itemComboField = new SComboField(getPropertyName("item"), 200);
			itemComboField.setInputPrompt(getPropertyName("all"));
			loadItems((Long) officeComboField.getValue());
			
			List countryList = new CountryDao().getCountry();
			if (countryList == null) {
				countryList = new ArrayList();
				countryList.add(0, new CountryModel(0, getPropertyName("all")));
			} else
				countryList.add(0, new CountryModel(0, getPropertyName("all")));

			countryComboField = new SComboField(getPropertyName("country"),
					200, countryList, "id", "name");
			countryComboField.setValue((long) 0);

			fromDateField = new SDateField(getPropertyName("from_date"), 100,
					getDateFormat(), getMonthStartDate());
			toDateField = new SDateField(getPropertyName("to_date"), 100,
					getDateFormat(), getWorkingDate());
			
			sortBy = new SRadioButton(getPropertyName("affect_type"), 200,
					Arrays.asList(new KeyValue(1,getPropertyName("item")),new KeyValue(2,getPropertyName("employee"))), "intKey", "value");
			sortBy.setHorizontal(true);
			sortBy.setValue(1);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);

			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(userComboField);
			mainFormLayout.addComponent(supplierComboField);
			mainFormLayout.addComponent(itemComboField);
			mainFormLayout.addComponent(countryComboField);
			mainFormLayout.addComponent(sortBy);
			mainFormLayout.addComponent(dateHorizontalLayout);
			mainFormLayout.addComponent(reportChoiceField);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			
			mainPanel.setContent(mainHorizontal);

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (officeComboField.getValue() != null) {
						loadUsers((Long) officeComboField.getValue());
						loadSuppliers((Long) officeComboField.getValue());
						loadItems((Long) officeComboField.getValue());
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
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							DailyQuotationUI option=new DailyQuotationUI();
							option.setCaption(getPropertyName("daily_quotation"));
							option.getUserComboField().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("daily_quotation")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("employee"),itm.getItemProperty(TBC_EMPLOYEE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("supplier"),itm.getItemProperty(TBC_SUPPLIER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),itm.getItemProperty(TBC_DATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("item"),itm.getItemProperty(TBC_ITEM).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("country"),itm.getItemProperty(TBC_COUNTRY).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("unit"),itm.getItemProperty(TBC_UNIT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("rate"),itm.getItemProperty(TBC_RATE).getValue().toString()));
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
			
			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					// if (isValid()) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						long supplierId = 0;
						long itemId = 0;
						long employeeId = 0;
						long officeId = 0;
						long countryId = 0;
						if (supplierComboField.getValue() != null
								&& !supplierComboField.getValue().equals(""))
							supplierId = (Long) supplierComboField.getValue();
						if (itemComboField.getValue() != null
								&& !itemComboField.getValue().equals(""))
							itemId = (Long) itemComboField.getValue();
						if (userComboField.getValue() != null
								&& !userComboField.getValue().equals(""))
							employeeId = (Long) userComboField.getValue();
						if (officeComboField.getValue() != null
								&& !officeComboField.getValue().equals(""))
							officeId = (Long) officeComboField.getValue();
						
						if (countryComboField.getValue() != null) 
							countryId = (Long) countryComboField
									.getValue();

						List reportList = dao.showAllQuotations(
								getOrganizationID(), officeId, employeeId,
								supplierId, itemId, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),countryId,(Integer)sortBy.getValue());

						if(reportList.size()>0){
							DailyQuotationBean bean=null;
							Iterator itr=reportList.iterator();
							while (itr.hasNext()) {
								bean=(DailyQuotationBean)itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getLogin(),
										bean.getDate().toString(),
										bean.getEmployee(),
										bean.getSupplier(),
										bean.getItems(),
										bean.getCountry(),
										bean.getUnit(),
										bean.getRate()},table.getItemIds().size()+1);
							}
						}
						else{
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
						table.setVisibleColumns(visibleColumns);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// }
			});

			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					// if (isValid()) {
					try {

						long supplierId = 0;
						long itemId = 0;
						long employeeId = 0;
						long officeId = 0;
						long countryId = 0;
						if (supplierComboField.getValue() != null
								&& !supplierComboField.getValue().equals(""))
							supplierId = (Long) supplierComboField.getValue();
						if (itemComboField.getValue() != null
								&& !itemComboField.getValue().equals(""))
							itemId = (Long) itemComboField.getValue();
						if (userComboField.getValue() != null
								&& !userComboField.getValue().equals(""))
							employeeId = (Long) userComboField.getValue();
						if (officeComboField.getValue() != null
								&& !officeComboField.getValue().equals(""))
							officeId = (Long) officeComboField.getValue();
						
						if (countryComboField.getValue() != null) 
							countryId = (Long) countryComboField
									.getValue();

						List reportList = dao.getAllQuotations(
								getOrganizationID(), officeId, employeeId,
								supplierId, itemId, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),countryId);

						if (reportList.size() > 0) {
							
							Collections.sort(reportList, new Comparator<DailyQuotationBean>() {
								@Override
								public int compare(final DailyQuotationBean object1,final DailyQuotationBean object2) {
									
									int result = 0;
									
									 	if((Integer)sortBy.getValue()==1){
									 		result = object1.getItems().compareTo(object2.getItems());
									 		if (result == 0) 
									 			result=object2 .getDate() .compareTo(object2.getDate());
									 	}else{
									 		if(object1.getEmployee()!=null&&object2.getEmployee()!=null)
									 			result = object1.getEmployee().compareTo(object2.getEmployee());
									 		if (result == 0) 
									 			result=object2 .getDate() .compareTo(object2.getDate());
									 	}
								     return result;
								}
							});
							
							report.setJrxmlFileName("DailyQuotationReport");
							report.setReportFileName("DailyQuotationReport");
							
							HashMap<String, Object> map = new HashMap<String, Object>();
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("daily_quotation"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("EMPLOYEE_LABEL", getPropertyName("employee"));
							map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("COUNTRY_LABEL", getPropertyName("country"));
							map.put("UNIT_LABEL", getPropertyName("unit"));
							map.put("RATE_LABEL", getPropertyName("rate"));
							
							
							String subTitle = "";

							if (userComboField.getValue() != null
									&& !userComboField.getValue().equals("")
									&& !userComboField.getValue().toString()
											.equals("0")) {
								subTitle += getPropertyName("user")+" : "
										+ userComboField
												.getItemCaption(userComboField
														.getValue()) + "\n";
							}
							subTitle += getPropertyName("from")
									+ CommonUtil
											.formatDateToDDMMMYYYY(fromDateField
													.getValue())
									+ getPropertyName("to")
									+ CommonUtil
											.formatDateToDDMMMYYYY(toDateField
													.getValue());
							report.setReportSubTitle(subTitle);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);
						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// }
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	private void loadItems(Long officeId) {
		try {
			if (itemComboField != null) {
				List itemList = dao
						.getAllActiveItemsWithAppendingItemCode(officeId);
				if (itemList == null)
					itemList = new ArrayList();
				ItemModel itmMdl = new ItemModel();
				itmMdl.setId((long) 0);
				itmMdl.setName(getPropertyName("all"));
				itemList.add(0, itmMdl);

				itemContainer = SCollectionContainer.setList(itemList, "id");
				itemComboField.setContainerDataSource(itemContainer);
				itemComboField.setItemCaptionPropertyId("name");
				itemComboField.setNewValue((long)0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadUsers(Long officeId) {
		try {
			if (userComboField != null) {
				List usrList = dao.getUsersWithFullNameAndCode(officeId);
				if (usrList == null)
					usrList = new ArrayList();

				UserModel mdl = new UserModel();
				mdl.setId(0);
				mdl.setFirst_name(getPropertyName("all"));
				usrList.add(0, mdl);

				userContainer = SCollectionContainer.setList(usrList, "id");
				userComboField.setReadOnly(false);
				userComboField.setContainerDataSource(userContainer);
				userComboField.setItemCaptionPropertyId("first_name");
				userComboField.setNewValue((long)0);
//				if (!isSuperAdmin() && !isSystemAdmin() && !isSemiAdmin()) {
//					userComboField.setReadOnly(true);
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadSuppliers(Long officeId) {
		try {
			if (supplierComboField != null) {
				List suppList = dao.getAllActiveSuppliers(officeId);
				if (suppList == null)
					suppList = new ArrayList();
				SupplierModel supMdl = new SupplierModel();
				supMdl.setId((long) 0);
				supMdl.setName(getPropertyName("all"));
				suppList.add(0, supMdl);

				supplierContainer = SCollectionContainer
						.setList(suppList, "id");
				supplierComboField.setContainerDataSource(supplierContainer);
				supplierComboField.setItemCaptionPropertyId("name");
				supplierComboField.setNewValue((long)0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;

		userComboField.setComponentError(null);

		if (userComboField.getValue() == null
				|| userComboField.getValue().equals("")) {
			setRequiredError(userComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}

		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
