package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.dao.SalaryDisbursalDao;
import com.inventory.payroll.model.CommissionSalaryModel;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.payroll.ui.CommissionSalaryUI;
import com.inventory.payroll.ui.SalaryDisbursalNewUI;
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

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 23, 2015
 */

public class CommissionSalaryReportUI extends SparkLogic {

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
	static String TBC_DATE = "Date";
	static String TBC_AMOUNT = "Amount";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] {TBC_SN, TBC_ID, TBC_EMPLOYEE, TBC_DATE,TBC_AMOUNT};
		visibleColumns = new Object[] {TBC_SN, TBC_EMPLOYEE, TBC_DATE, TBC_AMOUNT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_EMPLOYEE, String.class, null,getPropertyName("employee"), null, Align.LEFT);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("from_date"), null, Align.LEFT);
		table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("amount"), null, Align.LEFT);
		
		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_EMPLOYEE, (float) 2);
		table.setColumnExpandRatio(TBC_DATE, (float) 1);
		table.setColumnExpandRatio(TBC_AMOUNT, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		//table.setVisibleColumns(visibleColumns);
		
		setSize(975, 350);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		officeSelect = new SOfficeComboField(getPropertyName("office"), 150);

		try {
			List lst = new ArrayList();
			lst.add(new UserModel(0, getPropertyName("all")));
			lst.addAll(new UserManagementDao()
					.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()));
			employSelect = new SComboField(getPropertyName("employee"), 100,
					lst, "id", "first_name", false, getPropertyName("select"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		employSelect.setValue(employSelect.getItemIds().iterator().next());

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
		
		table.addActionHandler(new Handler() {
			
			@SuppressWarnings("static-access")
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						CommissionSalaryUI purchase = new CommissionSalaryUI();
						purchase.setCaption(getPropertyName("commission_salary"));
						purchase.getCommissonCombo().setValue((Long)item.getItemProperty(TBC_ID).getValue());
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
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("commission_salary")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("employee"),item.getItemProperty(TBC_EMPLOYEE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("from_date"),item.getItemProperty(TBC_DATE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
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

				List list = daoObj.getCommissionSalaryDisbursal(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) employSelect.getValue());
				if(list.size()>0){
					AcctReportMainBean mainObj = null;
					Iterator it = list.iterator();
					CommissionSalaryModel sal;
					while (it.hasNext()) {
						sal = (CommissionSalaryModel) it.next();
						table.addItem(new Object[]{
								table.getItemIds().size()+1,
								sal.getId(),
								sal.getEmployee().getFirst_name(),
								sal.getDate().toString(),
								CommonUtil.roundNumber(sal.getSalary())},table.getItemIds().size()+1);
//						mainObj.setType(type.charAt(0));
					}
				}
				else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
				list.clear();
			}
			//table.setVisibleColumns(visibleColumns);
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

				List list = daoObj.getCommissionSalaryDisbursal(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) employSelect.getValue());
				
				AcctReportMainBean mainObj = null;
				Iterator it = list.iterator();

				CommissionSalaryModel sal;
				while (it.hasNext()) {

					sal = (CommissionSalaryModel) it.next();

					mainObj = new AcctReportMainBean();
					mainObj.setName(sal.getEmployee().getFirst_name());
					mainObj.setAmount(CommonUtil.roundNumber(sal.getSalary()));
					mainObj.setDate(sal.getDate());
					reportList.add(mainObj);

				}

				list.clear();
			}

			if (reportList.size() > 0) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("FromDate", fromDate.getValue().toString());
				params.put("ToDate", toDate.getValue().toString());

				report.setJrxmlFileName("CommissionSalaryReport");
				report.setReportFileName("Commission Salary Report");

				if (officeSelect.isVisible())
					params.put("REPORT_TITLE_LABEL", getPropertyName("commission_salary_report")+" - "+ officeSelect.getItemCaption(officeSelect.getValue()));
				else
					params.put("REPORT_TITLE_LABEL", getPropertyName("commission_salary_report"));

				
				params.put("SL_NO_LABEL", getPropertyName("sl_no"));
				params.put("EMPLOYEE_LABEL", getPropertyName("employee"));
				params.put("DISBURSAL_LABEL", getPropertyName("date"));
				params.put("AMOUNT_LABEL", getPropertyName("amount"));
				params.put("TOTAL_LABEL", getPropertyName("total"));
				
				
				report.setIncludeHeader(true);
				report.setReportSubTitle(getPropertyName("from")+" : " + fromDate.getValue()
						+ getPropertyName("to")+" : " + toDate.getValue());
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
