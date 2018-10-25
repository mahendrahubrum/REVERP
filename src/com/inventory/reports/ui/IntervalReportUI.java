package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.haijian.ExcelExporter;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.IntervalReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 *         Feb 11, 2014
 */

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 20, 2015
 */

public class IntervalReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private IntervalReportDao daoObj;


	SDateField fromDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SRadioButton customerOrSupplier;

	STextField intervalDays, no_ofIntervals;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_PARTICULARS = "Customer";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	STable table;

	Object[] allColumns;
	Object[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	ArrayList<String> visibleColumnsList;

	ExcelExporter excelExporter;

	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes", "serial" })
	@Override
	public SPanel getGUI() {

		try {

			intervalDays = new STextField(getPropertyName("interval_days"),
					150, "15");
			no_ofIntervals = new STextField(getPropertyName("no_intervals"),
					150, "5");

			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();

			allColumns = new String[] { TBC_SN, TBC_PARTICULARS, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS,
					TBC_BALANCE };

			visibleColumnsList = new ArrayList<String>(Arrays.asList(TBC_SN,
					TBC_PARTICULARS, TBC_BALANCE));

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1200, 500);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnWidth(TBC_SN, 30);
			table.setColumnWidth(TBC_BALANCE, 140);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("820");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());
			List rentList = new ArrayList();
			rentList.add(new KeyValue((long) 1, getPropertyName("customers")));
			rentList.add(new KeyValue((long) 2, getPropertyName("suppliers")));
			rentList.add(new KeyValue((long) 3, getPropertyName("transportation")));
			customerOrSupplier = new SRadioButton(getPropertyName("type"),100,rentList,"key","value");
			customerOrSupplier.setValue((long)1);

			customerOrSupplier.setValue("Customers");

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new IntervalReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(customerOrSupplier);

			formLayout.addComponent(fromDate);
			formLayout.addComponent(intervalDays);
			formLayout.addComponent(no_ofIntervals);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			excelExporter = new ExcelExporter(table);
			excelExporter.setCaption(getPropertyName("export_excel"));

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(showButton);
			buttonLayout.addComponent(excelExporter);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(table.getValue()!=null){
							int count=1;
							Item item=table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("period_wise_ledger")+"</u></h2>"));
							form.addComponent(new SLabel(customerOrSupplier.getItemCaption(customerOrSupplier.getValue()),item.getItemProperty(TBC_PARTICULARS).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("balance_on")+CommonUtil.formatDateToDDMMYYYY(fromDate.getValue()),item.getItemProperty(TBC_BALANCE).getValue().toString()));
							for(int i=3;i<visibleColumnsList.size();i++){
								form.addComponent(new SLabel(getPropertyName("balance_after")+toDouble(intervalDays.getValue().toString())*count+" Days",item.getItemProperty(visibleColumnsList.get(i)).getValue().toString()));
								count++;
							}
							
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
			
			customerOrSupplier.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if((Long)customerOrSupplier.getValue()==1){
						table.setColumnHeader(TBC_PARTICULARS, getPropertyName("customer"));
					}
					else if((Long)customerOrSupplier.getValue()==2){
						table.setColumnHeader(TBC_PARTICULARS, getPropertyName("supplier"));
					}
					else{
						table.setColumnHeader(TBC_PARTICULARS, getPropertyName("transportation"));
					}
						
				}
			});
			
			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						showReport();
					}
				}
			});

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						generateReport();
					}
				}
			});

			organizationSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
										.getValue()), "id");
					} catch (Exception e) {
						e.printStackTrace();
					}
					officeSelect.setContainerDataSource(bic);
					officeSelect.setItemCaptionPropertyId("name");

				}
			});

			officeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

						}
					});

			customerOrSupplier
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
						}
					});

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	@SuppressWarnings("rawtypes")
	protected void showReport() {
		try {

			table.removeAllItems();

			if (isValid()) {

				List lst = null;

				removeContainerProperties();

				table.setColumnHeader(
						TBC_BALANCE,
						getPropertyName("balacne_on ")
								+ CommonUtil.formatDateToDDMMMYYYY(fromDate
										.getValue()));

				if ((Long)customerOrSupplier.getValue()==1) {

					lst = daoObj.getCustomerLedgerReport(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							toInt(intervalDays.getValue()),
							toInt(no_ofIntervals.getValue()), getOfficeID());

					table.setVisibleColumns(allColumns);

					int ct = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {

						if (ct == 0) {
							int val = 0;
							for (int i = 0; i < (toInt(no_ofIntervals.getValue())-1); i++) {
								val += toInt(intervalDays.getValue());
								table.addContainerProperty(1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"), Double.class, 0, 1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"), null, Align.RIGHT);
								visibleColumnsList.add(1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"));
							}
							table.addContainerProperty(getPropertyName("after")+" "+val+" "+ getPropertyName("days"), Double.class, 0, getPropertyName("after")+" "+val+" "+ getPropertyName("days"), null, Align.RIGHT);
							visibleColumnsList.add(getPropertyName("after")+" "+val+" "+ getPropertyName("days"));
							table.setVisibleColumns((Object[]) visibleColumnsList
									.toArray(new Object[visibleColumnsList
											.size()]));

						}

						obj = (AcctReportMainBean) it.next();

						Object[] objs = new Object[visibleColumnsList.size()];

						objs[0] = ct + 1;
						objs[1] = obj.getParticulars();
						objs[2] = obj.getAmount();

						for (int i = 3; i < visibleColumnsList.size(); i++) {
							objs[i] = obj.getSubList().get(i - 3);
						}

						table.addItem(objs, ct);

						ct++;

					}

					table.setVisibleColumns((Object[]) visibleColumnsList
							.toArray(new Object[visibleColumnsList.size()]));

				} else if ((Long)customerOrSupplier.getValue()==2) {

					lst = daoObj.getSupplierLedgerReport(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							toInt(intervalDays.getValue()),
							toInt(no_ofIntervals.getValue()), getOfficeID());

					table.setVisibleColumns(allColumns);

					int ct = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (ct == 0) {
							int val = 0;
							for (int i = 0; i < (toInt(no_ofIntervals.getValue())-1); i++) {
								val += toInt(intervalDays.getValue());
								table.addContainerProperty(1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"), Double.class, 0, 1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"), null, Align.RIGHT);
								visibleColumnsList.add(1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"));
							}
							table.addContainerProperty(getPropertyName("after")+" "+val+" "+ getPropertyName("days"), Double.class, 0, getPropertyName("after")+" "+val+" "+ getPropertyName("days"), null, Align.RIGHT);
							visibleColumnsList.add(getPropertyName("after")+" "+val+" "+ getPropertyName("days"));
							table.setVisibleColumns((Object[]) visibleColumnsList.toArray(new Object[visibleColumnsList.size()]));
						}

						Object[] objs = new Object[visibleColumnsList.size()];

						objs[0] = ct + 1;
						objs[1] = obj.getParticulars();
						objs[2] = obj.getAmount();

						for (int i = 3; i < visibleColumnsList.size(); i++) {
							objs[i] = "" + obj.getSubList().get(i - 3);
						}

						table.addItem(objs, ct);
						ct++;

					}

					table.setVisibleColumns((Object[]) visibleColumnsList
							.toArray(new Object[visibleColumnsList.size()]));

				} else {

					lst = daoObj.getTransportationReport(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							toInt(intervalDays.getValue()),
							toInt(no_ofIntervals.getValue()), getOfficeID());

					table.setVisibleColumns(allColumns);

					int ct = 0;
					double bal = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (ct == 0) {
							int val = 0;
							for (int i = 0; i < (toInt(no_ofIntervals.getValue())-1); i++) {
								val += toInt(intervalDays.getValue());
								table.addContainerProperty(1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"), Double.class, 0, 1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"), null, Align.RIGHT);
								visibleColumnsList.add(1+(i*toInt(intervalDays.getValue()))+"-"+val+" "+ getPropertyName("days"));
							}
							table.addContainerProperty(getPropertyName("after")+" "+val+" "+ getPropertyName("days"), Double.class, 0, getPropertyName("after")+" "+val+" "+ getPropertyName("days"), null, Align.RIGHT);
							visibleColumnsList.add(getPropertyName("after")+" "+val+" "+ getPropertyName("days"));
							table.setVisibleColumns((Object[]) visibleColumnsList
									.toArray(new Object[visibleColumnsList
											.size()]));
						}

						Object[] objs = new Object[visibleColumnsList.size()];
						objs[0] = ct + 1;
						objs[1] = obj.getParticulars();
						objs[2] = obj.getAmount();

						for (int i = 3; i < visibleColumnsList.size(); i++) {
							objs[i] = "" + obj.getSubList().get(i - 3);
						}

						table.addItem(objs, ct);
						ct++;

					}

					table.setVisibleColumns((Object[]) visibleColumnsList
							.toArray(new Object[visibleColumnsList.size()]));

				}

				buttonLayout.removeComponent(excelExporter);
				excelExporter = new ExcelExporter(table);
				buttonLayout.addComponent(excelExporter);
				excelExporter.setCaption("Export to Excel");

				lst.clear();

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void generateReport() {
		try {

			if (isValid()) {

				List lst = null;
				List reportList = new ArrayList();
				if (customerOrSupplier.getValue().equals("Customers")) {

					reportList = daoObj.getCustomerLedgerReport(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							toInt(intervalDays.getValue()),
							toInt(no_ofIntervals.getValue()), getOfficeID());

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("TODAY",
								CommonUtil.formatDateToDDMMYYYY(new Date()));
						params.put("PART_HEAD", "Customer");

						params.put("LedgerName", "");
						params.put("Balance", 0.0);
						params.put("OpeningBalance", 0.0);
						params.put("Office", officeSelect
								.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						report.setJrxmlFileName("ConsolidatedLedgerReport");
						report.setReportFileName("Consolidated Ledger Report");
						report.setReportTitle("Consolidated Ledger Report");
						report.setReportSubTitle("Date  : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue()));
						report.setIncludeHeader(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);

						reportList.clear();
						lst.clear();

					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				} else if (customerOrSupplier.getValue().equals("Suppliers")) {
					reportList = daoObj.getSupplierLedgerReport(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							toInt(intervalDays.getValue()),
							toInt(no_ofIntervals.getValue()), getOfficeID());

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("TODAY",
								CommonUtil.formatDateToDDMMYYYY(new Date()));
						params.put("PART_HEAD", "Supplier");
						params.put("LedgerName", "");
						params.put("Balance", 0.0);
						params.put("OpeningBalance", 0.0);
						params.put("Office", officeSelect
								.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						report.setJrxmlFileName("ConsolidatedLedgerReport");
						report.setReportFileName("Consolidated Ledger Report");
						report.setReportTitle("Consolidated Ledger Report");
						report.setReportSubTitle("Date  : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue()));
						report.setIncludeHeader(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);

						reportList.clear();
						lst.clear();

					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				} else {
					reportList = daoObj.getTransportationReport(CommonUtil
							.getSQLDateFromUtilDate(fromDate.getValue()),
							toInt(intervalDays.getValue()),
							toInt(no_ofIntervals.getValue()), getOfficeID());

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("TODAY",
								CommonUtil.formatDateToDDMMYYYY(new Date()));
						params.put("PART_HEAD", "Supplier");
						params.put("LedgerName", "");
						params.put("Balance", 0.0);
						params.put("OpeningBalance", 0.0);
						params.put("Office", officeSelect
								.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						report.setJrxmlFileName("ConsolidatedLedgerReportTransportation");
						report.setReportFileName("Consolidated Ledger Report");
						report.setReportTitle("Consolidated Ledger Report");
						report.setReportSubTitle("Date  : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue()));
						report.setIncludeHeader(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);

						reportList.clear();
						lst.clear();

					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (customerOrSupplier.getValue() == null
				|| customerOrSupplier.getValue().equals("")) {
			setRequiredError(customerOrSupplier,
					getPropertyName("invalid_selection"), true);
			customerOrSupplier.focus();
			ret = false;
		} else
			setRequiredError(customerOrSupplier, null, false);

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		try {
			if (toInt(intervalDays.getValue()) < 0) {
				setRequiredError(intervalDays, getPropertyName("invalid_data"),
						true);
				intervalDays.focus();
				ret = false;
			} else
				setRequiredError(intervalDays, null, false);
		} catch (Exception e) {
			setRequiredError(intervalDays, getPropertyName("invalid_data"),
					true);
			intervalDays.focus();
			ret = false;
			// TODO: handle exception
		}

		try {
			if (toInt(no_ofIntervals.getValue()) < 0
					|| toInt(no_ofIntervals.getValue()) > 10) {
				setRequiredError(no_ofIntervals,
						getPropertyName("invalid_data"), true);
				no_ofIntervals.focus();
				ret = false;
			} else
				setRequiredError(no_ofIntervals, null, false);
		} catch (Exception e) {
			setRequiredError(no_ofIntervals, getPropertyName("invalid_data"),
					true);
			no_ofIntervals.focus();
			ret = false;
			// TODO: handle exception
		}

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;
	}

	public void removeContainerProperties() {

		String refId;
		for (int i = 3; i < visibleColumnsList.size(); i++) {
			refId = visibleColumnsList.get(i);
			table.removeContainerProperty(refId);
			visibleColumnsList.remove(i);
			i--;
		}

	}

}
