package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.reports.bean.RateComparisonReportBean;
import com.inventory.reports.dao.RateComparisonReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 2, 2014
 */
public class RateComparisonReportUI extends SparkLogic {

	private static final long serialVersionUID = 1467075418323093877L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private SDateField fromDate, toDate;

	private SReportChoiceField reportType;

	private SOfficeComboField officeSelect;

	private RateComparisonReportDao dao;
	private SComboField employSelect;
	private SComboField supplierSelect;
	private SCollectionContainer c;
	private UserManagementDao userDao;
	private SupplierDao supplierDao;
	
	static String TBC_SN = "SN";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_UNIT = "Unit";
	static String TBC_SRATE = "Supplier Rate";
	static String TBC_ERATE = "Employee Rate";
	static String TBC_SUPPLIER = "Supplier";
	static String TBC_EMPLOYEE = "Employee";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@Override
	public SPanel getGUI() {
		setSize(1050, 350);
		allColumns = new Object[] { TBC_SN, TBC_DATE,TBC_ITEM,TBC_UNIT,TBC_SUPPLIER,TBC_SRATE,TBC_EMPLOYEE,TBC_ERATE};
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_ITEM,TBC_UNIT,TBC_SUPPLIER,TBC_SRATE,TBC_EMPLOYEE,TBC_ERATE};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
		table.addContainerProperty(TBC_SUPPLIER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
		table.addContainerProperty(TBC_SRATE, Double.class, null,getPropertyName("supplier_rate"), null, Align.LEFT);
		table.addContainerProperty(TBC_EMPLOYEE, String.class, null,getPropertyName("employee"), null, Align.LEFT);
		table.addContainerProperty(TBC_ERATE, Double.class, null,getPropertyName("employee_rate"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 1);
		table.setColumnExpandRatio(TBC_ITEM, 2);
		table.setColumnExpandRatio(TBC_SUPPLIER, (float) 1.5);
		table.setColumnExpandRatio(TBC_EMPLOYEE, (float) 1.5);
		table.setColumnExpandRatio(TBC_SRATE, (float) 1);
		table.setColumnExpandRatio(TBC_ERATE, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		dao = new RateComparisonReportDao();
		userDao = new UserManagementDao();
		supplierDao = new SupplierDao();
		report = new Report(getLoginID());

		officeSelect = new SOfficeComboField(getPropertyName("office"), 200);
		fromDate = new SDateField(getPropertyName("from_date"), 100,
				getDateFormat(), getMonthStartDate());
		toDate = new SDateField(getPropertyName("to_date"), 100,
				getDateFormat(), getWorkingDate());

		employSelect = new SComboField(getPropertyName("employee"), 200);
		loadEmployees((Long) officeSelect.getValue());

		supplierSelect = new SComboField(getPropertyName("supplier"), 200);
		loadSuppliers((Long) officeSelect.getValue());

		reportType = new SReportChoiceField(getPropertyName("export_to"));

		formLayout = new SFormLayout();
		formLayout.setSpacing(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		formLayout.addComponent(officeSelect);
		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(employSelect);
		formLayout.addComponent(supplierSelect);
		formLayout.addComponent(reportType);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);

		mainHorizontal.addComponent(formLayout);
		mainHorizontal.addComponent(table);
		mainHorizontal.addComponent(popupContainer);
		mainHorizontal.setMargin(true);
		officeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				loadEmployees((Long) officeSelect.getValue());
				loadSuppliers((Long) officeSelect.getValue());
			}
		});

		showButton.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					table.removeAllItems();
					table.setVisibleColumns(allColumns);
					boolean[] sort=new boolean[]{true};
					ItemUnitMangementModel unitMangementModel = null;
					List reportList = new ArrayList();
					// List quotlist=null;
					List supList = null;
					List empList = null;
					List removeList = new ArrayList();

					RateComparisonReportBean bean = null;
					RateComparisonReportBean empBean = null;

					List itemList = dao.getItems(
							(Long) officeSelect.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));

					if (itemList != null && itemList.size() > 0) {
						Iterator itr = itemList.iterator();

						while (itr.hasNext()) {

							unitMangementModel = (ItemUnitMangementModel) itr
									.next();

							supList = dao.getComparisonSupplierReport(
									(Long) officeSelect.getValue(),
									unitMangementModel, 
									(Long) supplierSelect.getValue(), 
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
							empList = dao.getComparisonEmplReport(
									(Long) officeSelect.getValue(),
									unitMangementModel, 
									(Long) employSelect.getValue(), 
									(Long) supplierSelect.getValue(), 
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
									CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));

							for (int i = 0; i < supList.size(); i++) {
								bean = (RateComparisonReportBean) supList
										.get(i);

								for (int e = 0; e < empList.size(); e++) {
									empBean = (RateComparisonReportBean) empList
											.get(e);
									if (bean.getDate()
											.equals(empBean.getDate())
											&& bean.getItem().equals(
													empBean.getItem())
											&& bean.getUnit().equals(
													empBean.getUnit())) {
										bean.setEmployee(empBean.getEmployee());
										bean.setEmployeeRate(empBean
												.getEmployeeRate());
										removeList.add(empBean);
										break;
									}
								}
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										bean.getDate().toString(),
										bean.getItem(),
										bean.getUnit(),
										bean.getSupplier(),
										bean.getSupplierRate(),
										bean.getEmployee(),
										bean.getEmployeeRate()},table.getItemIds().size()+1);
							}
							table.sort(new Object[]{TBC_DATE}, sort);
							empList.removeAll(removeList);
							table.setVisibleColumns(visibleColumns);
						}

					}
					else {
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("rate")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("item"),item.getItemProperty(TBC_ITEM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("unit"),item.getItemProperty(TBC_UNIT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("supplier"),item.getItemProperty(TBC_SUPPLIER).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("supplier_rate"),item.getItemProperty(TBC_SRATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("employee"),item.getItemProperty(TBC_EMPLOYEE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("employee_rate"),item.getItemProperty(TBC_ERATE).getValue().toString()));
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

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try {

					ItemUnitMangementModel unitMangementModel = null;
					List reportList = new ArrayList();
					// List quotlist=null;
					List supList = null;
					List empList = null;
					List removeList = new ArrayList();

					RateComparisonReportBean bean = null;
					RateComparisonReportBean empBean = null;

					List itemList = dao.getItems(
							(Long) officeSelect.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDate
									.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDate.getValue()));

					if (itemList != null && itemList.size() > 0) {
						Iterator itr = itemList.iterator();

						while (itr.hasNext()) {

							unitMangementModel = (ItemUnitMangementModel) itr
									.next();

							supList = dao.getComparisonSupplierReport(
									(Long) officeSelect.getValue(),
									unitMangementModel, (Long) supplierSelect
											.getValue(), CommonUtil
											.getSQLDateFromUtilDate(fromDate
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()));
							empList = dao.getComparisonEmplReport(
									(Long) officeSelect.getValue(),
									unitMangementModel, (Long) employSelect
											.getValue(), (Long) supplierSelect
											.getValue(), CommonUtil
											.getSQLDateFromUtilDate(fromDate
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()));

							for (int i = 0; i < supList.size(); i++) {
								bean = (RateComparisonReportBean) supList
										.get(i);

								for (int e = 0; e < empList.size(); e++) {
									empBean = (RateComparisonReportBean) empList
											.get(e);
									if (bean.getDate()
											.equals(empBean.getDate())
											&& bean.getItem().equals(
													empBean.getItem())
											&& bean.getUnit().equals(
													empBean.getUnit())) {
										bean.setEmployee(empBean.getEmployee());
										bean.setEmployeeRate(empBean
												.getEmployeeRate());
										removeList.add(empBean);
										break;
									}

								}
								reportList.add(bean);
							}

							empList.removeAll(removeList);

							for (int e = 0; e < empList.size(); e++) {
								empBean = (RateComparisonReportBean) empList
										.get(e);
								reportList.add(empBean);
							}

						}

					}

					if (reportList != null && reportList.size() > 0) {

						Collections.sort(reportList,
								new Comparator<RateComparisonReportBean>() {

									@Override
									public int compare(
											RateComparisonReportBean bean1,
											RateComparisonReportBean bean2) {
										return bean1.getDate().compareTo(
												bean2.getDate());
									}
								});
						HashMap<String, Object> map = new HashMap<String, Object>();
						report.setJrxmlFileName("RateComparisonReport");
						report.setReportFileName("RateComparisonReport");

						map.put("REPORT_TITLE_LABEL", getPropertyName("rate_comparison_report"));
						map.put("SL_NO_LABEL", getPropertyName("sl_no"));
						map.put("DATE_LABEL", getPropertyName("date"));
						map.put("ITEM_LABEL", getPropertyName("item"));
						map.put("UNIT_LABEL", getPropertyName("unit"));
						map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
						map.put("SUPPLIER_RATE_LABEL", getPropertyName("supplier_rate"));
						map.put("EMPLOYEE_LABEL", getPropertyName("employee"));
						map.put("EMPLOYEE_RATE_LABEL", getPropertyName("employee_rate"));
						map.put("ITEM_LABEL", getPropertyName("item"));
						
						

						report.setIncludeHeader(true);
						report.setReportSubTitle(getPropertyName("from")+" : "
								+ CommonUtil.formatDateToDDMMMYYYY(fromDate
										.getValue())
								+ "   "+getPropertyName("to")+"  : "
								+ CommonUtil.formatDateToDDMMMYYYY(toDate
										.getValue()));
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
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
		
		mainPanel.setContent(mainHorizontal);

		return mainPanel;
	}

	private void loadEmployees(long officeID) {
		try {
			List lst = new ArrayList();
			lst.add(new UserModel(0, getPropertyName("all")));
			lst.addAll(userDao.getUsersWithLoginId(officeID));

			c = SCollectionContainer.setList(lst, "id");
			employSelect.setContainerDataSource(c);
			employSelect.setItemCaptionPropertyId("first_name");
			employSelect.setValue((long) 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadSuppliers(long officeID) {
		try {
			List lst = new ArrayList();
			lst.add(new SupplierModel(0, getPropertyName("all")));
			lst.addAll(supplierDao.getAllSupplierNamesList(officeID));

			c = SCollectionContainer.setList(lst, "id");
			supplierSelect.setContainerDataSource(c);
			supplierSelect.setItemCaptionPropertyId("name");
			supplierSelect.setValue((long) 0);

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
