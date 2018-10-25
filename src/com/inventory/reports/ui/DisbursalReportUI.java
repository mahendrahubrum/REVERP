package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.dao.SalaryDisbursalDao;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.inventory.payroll.ui.SalaryDisbursalUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.DisbursalReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 6, 2013
 */
public class DisbursalReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private DisbursalReportDao daoObj;

	SDateField fromDate, toDate;

	private SNativeSelect reportType;

	SOfficeComboField officeSelect;

	SComboField employSelect;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_EMPLOYEE = "Employee";
	static String TBC_DDATE = "Disbursal Date";
	static String TBC_FROM = "From Date";
	static String TBC_TO = "To Date";
	static String TBC_ADVANCE = "Advance";
	static String TBC_AMOUNT = "Amount";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] {TBC_SN, TBC_ID, TBC_EMPLOYEE,TBC_DDATE, TBC_FROM,TBC_TO,TBC_ADVANCE,TBC_AMOUNT};
		visibleColumns = new Object[] {TBC_SN, TBC_EMPLOYEE,TBC_DDATE, TBC_FROM,TBC_TO,TBC_ADVANCE,TBC_AMOUNT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_EMPLOYEE, String.class, null,getPropertyName("employee"), null, Align.LEFT);
		table.addContainerProperty(TBC_DDATE, String.class, null,getPropertyName("disbursal_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_FROM, String.class, null,getPropertyName("from_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_TO, String.class, null,getPropertyName("to_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.LEFT);
		table.addContainerProperty(TBC_ADVANCE, Double.class, null,getPropertyName("advance_amount"), null, Align.LEFT);
		
		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_EMPLOYEE, (float) 2);
		table.setColumnExpandRatio(TBC_DDATE, 1);
		table.setColumnExpandRatio(TBC_FROM, (float) 1);
		table.setColumnExpandRatio(TBC_TO, (float) 1);
		table.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
		table.setColumnExpandRatio(TBC_ADVANCE, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		setSize(975, 350);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		officeSelect = new SOfficeComboField(getPropertyName("office"), 150);

		employSelect = new SComboField(getPropertyName("employee"), 150);
		loadEmployees(getOfficeID());
		reportType = new SNativeSelect(getPropertyName("report_type"), 100,
				SConstants.reportTypes, "intKey", "value");

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		 formLayout.setSpacing(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		daoObj = new DisbursalReportDao();

		fromDate = new SDateField(getPropertyName("from_date"), 150,
				getDateFormat(), getMonthStartDate());
		toDate = new SDateField(getPropertyName("to_date"), 150,
				getDateFormat(), getWorkingDate());

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(officeSelect);
		formLayout.addComponent(employSelect);

		formLayout.addComponent(reportType);
		reportType.setValue(0);

//		employSelect.setValue(employSelect.getItemIds().iterator().next());

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);
		mainHorizontal.addComponent(formLayout);
		mainHorizontal.addComponent(table);
		mainHorizontal.addComponent(popupContainer);
		mainHorizontal.setMargin(true);
		
		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				showButton.click();
			}
		};
		
		final Action actionDelete = new Action(getPropertyName("edit"));
		
		officeSelect.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				loadEmployees((Long)officeSelect.getValue());	
			}
		});
		
		table.addActionHandler(new Handler() {
			
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						SalaryDisbursalUI purchase = new SalaryDisbursalUI();
						purchase.setCaption(getPropertyName("salary_disbursal"));
						SalaryDisbursalModel mdl=new SalaryDisbursalDao().getSalaryDisbursalModel((Long)item.getItemProperty(TBC_ID).getValue());
				//		purchase.getForMonthDateField().setValue(CommonUtil.getUtilFromSQLDate(mdl.getMonth()));
						purchase.center();
						getUI().getCurrent().addWindow(purchase);
						purchase.addCloseListener(closeListener);
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
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("salary_disbursal")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("employee"),item.getItemProperty(TBC_EMPLOYEE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("disbursal_date"),item.getItemProperty(TBC_DDATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("from_date"),item.getItemProperty(TBC_FROM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("to_date"),item.getItemProperty(TBC_TO).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("advance_amount"),item.getItemProperty(TBC_ADVANCE).getValue().toString()));
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
				if (isValid()) {
					generateReport();
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

		mainPanel.setContent(mainHorizontal);

		return mainPanel;
	}
	
	private void loadEmployees(long officeID) {
		try {
			List lst = new ArrayList();
			lst.add(0,new UserModel((long)0, getPropertyName("all")));
			lst.addAll(new UserManagementDao()
					.getUsersWithFullNameAndCodeUnderAllOffice(officeID,getOrganizationID()));
			
			SCollectionContainer con=SCollectionContainer.setList(lst, "id");
			employSelect.setContainerDataSource(con);
			employSelect.setItemCaptionPropertyId("first_name");
			employSelect.setValue((long)0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void showReport() {
		try {

			List reportList = new ArrayList();
			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			if (isValid()) {
				
				Calendar cal = Calendar.getInstance();
				Iterator itr = SConstants.payroll.salaryTypes.iterator();
				String type = "";
				KeyValue kv = null;

				List list = daoObj.getDisbursal(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) employSelect.getValue(), getOrganizationID());
				if(list.size()>0){
					AcctReportMainBean mainObj = null;
					Iterator it = list.iterator();
					SalaryDisbursalModel sal;
					while (it.hasNext()) {

						sal = (SalaryDisbursalModel) it.next();
						while (itr.hasNext()) {
							kv = (KeyValue) itr.next();
							if (kv.getIntKey() == sal.getUser().getSalary_type()) {
								type = kv.getValue();
								break;
							}
							type = "";
						}
						cal.setTime(sal.getDispursal_date());
						cal.set(Calendar.DATE, cal.getMaximum(Calendar.DATE));
						cal.add(Calendar.DATE, -1);
						double advance=daoObj.getTotalAdvancePayment(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
								(Long) officeSelect.getValue(),
								sal.getUser().getLoginId().getId(), getOrganizationID());
						
						table.addItem(new Object[]{
								table.getItemIds().size()+1,
								sal.getId(),
								sal.getUser().getFirst_name(),
								sal.getDispursal_date().toString(),
								sal.getFrom_date().toString(),
								sal.getTo_date().toString(),
								roundNumber(advance),
								CommonUtil.roundNumber(sal.getPayroll())},table.getItemIds().size()+1);
//						mainObj.setType(type.charAt(0));
					}
				}
				else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
				list.clear();
			}
			table.setVisibleColumns(visibleColumns);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void generateReport() {
		try {

			List reportList = new ArrayList();

			if (isValid()) {

				Calendar cal = Calendar.getInstance();
				Iterator itr = SConstants.payroll.salaryTypes.iterator();
				String type = "";
				KeyValue kv = null;

				List list = daoObj.getDisbursal(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) employSelect.getValue(), getOrganizationID());
				
				AcctReportMainBean mainObj = null;
				Iterator it = list.iterator();

				SalaryDisbursalModel sal;
				while (it.hasNext()) {

					sal = (SalaryDisbursalModel) it.next();

					double advance=daoObj.getTotalAdvancePayment(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
							(Long) officeSelect.getValue(),
							sal.getUser().getLoginId().getId(), getOrganizationID());
					
					mainObj = new AcctReportMainBean();
					mainObj.setName(sal.getUser().getFirst_name());
					mainObj.setAmount(CommonUtil.roundNumber(sal
							.getPayroll()));
					mainObj.setDate(sal.getDispursal_date());
					mainObj.setFrom_date(sal.getFrom_date());
					mainObj.setBalance(roundNumber(advance));
					cal.setTime(sal.getFrom_date());

					cal.set(Calendar.DATE, cal.getMaximum(Calendar.DATE));
					cal.add(Calendar.DATE, -1);
					mainObj.setTo_date(cal.getTime());

					while (itr.hasNext()) {
						kv = (KeyValue) itr.next();
						if (kv.getIntKey() == sal.getUser().getSalary_type()) {
							type = kv.getValue();
							break;
						}
						type = "";
					}
					mainObj.setType(type.charAt(0));
					reportList.add(mainObj);

				}

				list.clear();
			}

			if (reportList.size() > 0) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("FromDate", fromDate.getValue().toString());
				params.put("ToDate", toDate.getValue().toString());

				report.setJrxmlFileName("DisbursalReport");
				report.setReportFileName("Disbursal Report");

				if (officeSelect.isVisible())
					params.put("REPORT_TITLE_LABEL", getPropertyName("disbursal_report")+" -"+ officeSelect.getItemCaption(officeSelect.getValue()));
				else
					params.put("REPORT_TITLE_LABEL", getPropertyName("disbursal_report"));

				
				params.put("SL_NO_LABEL", getPropertyName("sl_no"));
				params.put("EMPLOYEE_LABEL", getPropertyName("employee"));
				params.put("DISBURSAL_LABEL", getPropertyName("disbursal_date"));
				params.put("SALARY_LABEL", getPropertyName("salary_for"));
				params.put("FROM_DATE_LABEL", getPropertyName("from_date"));
				params.put("TO_DATE_LABEL", getPropertyName("to_date"));
				params.put("AMOUNT_LABEL", getPropertyName("amount"));
				params.put("ADVANCE_LABEL", getPropertyName("advance"));
				params.put("SALARY1_LABEL", getPropertyName("salary"));
				params.put("TOTAL_LABEL", getPropertyName("total"));
				
				
				report.setIncludeHeader(true);
				report.setReportSubTitle(getPropertyName("from")+" : " + CommonUtil.formatDateToCommonDateTimeFormat(fromDate.getValue())
						+ getPropertyName("to")+" : " + CommonUtil.formatDateToCommonDateTimeFormat(toDate.getValue()));
				report.setReportType((Integer) reportType.getValue());
				report.setOfficeName(officeSelect.getItemCaption(officeSelect
						.getValue()));
				report.createReport(reportList, params);

				reportList.clear();

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			toDate.focus();
			ret = false;
		} else
			setRequiredError(toDate, null, false);

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

}
