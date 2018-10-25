package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.management.dao.TaskComponentDao;
import com.inventory.management.dao.TasksDao;
import com.inventory.management.model.TaskComponentModel;
import com.inventory.management.model.TasksAssignedUsersModel;
import com.inventory.management.model.TasksModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.WrappedSession;
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
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class TaskManagementReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField createdBySelect;
	private SComboField assignedToSelect;

	private SComboField statusSelect;
	private SComboField componentSelect;

	private SNativeSelect reportType;

	TasksDao taskDao;
	TaskComponentDao tslCompDao;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_TITLE = "Title";
	static String TBC_CREATED = "Created By";
	static String TBC_TASK = "Tasks";
	static String TBC_ASSIGNED = "Assigned To";
	static String TBC_START = "Start Date";	
	static String TBC_END = "End Date";
	static String TBC_COMPLETE = "Completion Date";
	static String TBC_STATUS = "Status";
	static String TBC_DESC = "Description";

	private STable table;

	private Object[] allColumns;
	private Object[] visibleColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer,mainHorizontal;
	SButton showButton;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_TITLE,TBC_CREATED, TBC_TASK,TBC_ASSIGNED, TBC_START, TBC_END,TBC_COMPLETE,TBC_STATUS,TBC_DESC };
		visibleColumns = new Object[]   { TBC_SN, TBC_TITLE,TBC_CREATED, TBC_TASK,TBC_ASSIGNED, TBC_START, TBC_END,TBC_COMPLETE,TBC_STATUS };
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 700, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_TITLE, String.class, null, getPropertyName("title"), null,Align.CENTER);
		table.addContainerProperty(TBC_CREATED, String.class, null, getPropertyName("created_by"), null,Align.CENTER);
		table.addContainerProperty(TBC_TASK, String.class, null, getPropertyName("task"), null,Align.CENTER);
		table.addContainerProperty(TBC_ASSIGNED, String.class, null, getPropertyName("assigned_to"), null,Align.CENTER);
		table.addContainerProperty(TBC_START, String.class, null, getPropertyName("start_date"), null,Align.CENTER);
		table.addContainerProperty(TBC_END, String.class, null, getPropertyName("end_date"), null,Align.CENTER);
		table.addContainerProperty(TBC_COMPLETE, String.class, null, getPropertyName("completion"), null,Align.CENTER);
		table.addContainerProperty(TBC_STATUS, String.class, null, getPropertyName("status"), null,Align.CENTER);
		table.addContainerProperty(TBC_DESC, String.class, null, getPropertyName("description"), null,Align.CENTER);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_CREATED, (float) 1);
		table.setColumnExpandRatio(TBC_TASK, (float) 1.5);
		table.setColumnExpandRatio(TBC_TITLE, (float) 1);
		table.setColumnExpandRatio(TBC_ASSIGNED, (float) 1);
		table.setColumnExpandRatio(TBC_START, (float) 1);
		table.setColumnExpandRatio(TBC_END, (float) 1);
		table.setColumnExpandRatio(TBC_COMPLETE, (float) 1);
		table.setColumnExpandRatio(TBC_STATUS, (float) 1);
		table.setColumnExpandRatio(TBC_DESC, (float)0.1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);

		taskDao = new TasksDao();
		tslCompDao = new TaskComponentDao();

		try {

			setSize(1100, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			createdBySelect = new SComboField(getPropertyName("created_by"),
					200, null, "id", "name");

			assignedToSelect = new SComboField(
					getPropertyName(getPropertyName("assigned_to")), 200, null,
					"id", "name");

			statusSelect = new SComboField(getPropertyName("status"), 200,
					Arrays.asList(new KeyValue((long) 0, getPropertyName("all")), new KeyValue(
							(long) 1, getPropertyName("created")), new KeyValue((long) 2,
									getPropertyName("completed")), new KeyValue((long) 3,
											getPropertyName("partially_completed"))), "key", "value");
			statusSelect.setInputPrompt(getPropertyName("select"));
			statusSelect.setValue((long) 0);

			componentSelect = new SComboField(getPropertyName("components"),
					200);

			formLayout = new SFormLayout();
			 formLayout.setSpacing(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(createdBySelect);
			formLayout.addComponent(assignedToSelect);

			formLayout.addComponent(componentSelect);
			formLayout.addComponent(statusSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);
			mainHorizontal.addComponent(formLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*final CloseListener closeListener = new CloseListener() {
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
						AddTasksUI option=new AddTasksUI();
						option.setCaption("Add Tasks");
						option.getConnecComboField().setValue((Long) item.getItemProperty(TBC_ID).getValue());
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
		*/
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("task_management")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("title"),item.getItemProperty(TBC_TITLE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("created_by"),item.getItemProperty(TBC_CREATED).getValue().toString()));
						form.addComponent(new SHTMLLabel(getPropertyName("tasks"),item.getItemProperty(TBC_TASK).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("assigned_to"),item.getItemProperty(TBC_ASSIGNED).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("start_date"),item.getItemProperty(TBC_START).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("end_date"),item.getItemProperty(TBC_END).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("completion"),item.getItemProperty(TBC_COMPLETE).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("status"),item.getItemProperty(TBC_STATUS).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("description"),item.getItemProperty(TBC_DESC).getValue().toString()));
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

		organizationSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				SCollectionContainer bic1 = null;

				try {
					List lst1 = new ArrayList();
					lst1.add(new TaskComponentModel(0, getPropertyName("all")));
					lst1.addAll(tslCompDao
							.getComponentNames((Long) organizationSelect
									.getValue()));

					bic = SCollectionContainer.setList(lst1, "id");

					List lst2 = new ArrayList();
					lst2.add(new S_LoginModel(0, getPropertyName("all")));
					lst2.addAll(new UserManagementDao().getAllLoginsFromRole(
							false, (Long) organizationSelect.getValue()));
					bic1 = SCollectionContainer.setList(lst2, "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				componentSelect.setContainerDataSource(bic);
				componentSelect.setItemCaptionPropertyId("name");

				createdBySelect.setContainerDataSource(bic1);
				createdBySelect.setItemCaptionPropertyId("login_name");

				assignedToSelect.setContainerDataSource(bic1);
				assignedToSelect.setItemCaptionPropertyId("login_name");

				componentSelect.setValue((long) 0);
				createdBySelect.setValue((long) 0);
				assignedToSelect.setValue((long) 0);
			}
		});

		organizationSelect.setValue(getOrganizationID());

		if (isSuperAdmin() || isSystemAdmin()) {
			organizationSelect.setEnabled(true);
		}
		mainPanel.setContent(mainHorizontal);

		return mainPanel;
	}
	
	protected void showReport() {
		try {

			if (isValid()) {
				table.removeAllItems();
				table.setVisibleColumns(allColumns);
				List lst = taskDao.getAllTasksListAssignedToUser(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) createdBySelect.getValue(),
						(Long) assignedToSelect.getValue(),
						(Long) componentSelect.getValue(),
						(Long) statusSelect.getValue());

				List reportList = new ArrayList();
				String tasks = "", users = "";
				TasksModel obj;
				TasksAssignedUsersModel detObj;
				Iterator it2;
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					obj = (TasksModel) it.next();

					tasks = "";
					users = "";

					it2 = obj.getAssignedList().iterator();
					while (it2.hasNext()) {
						detObj = (TasksAssignedUsersModel) it2.next();
						users += detObj.getUser().getLogin_name() + " , ";
					}
					String status="";
					if(obj.getStatus()==1){
						status="Created";
					}
					else if(obj.getStatus()==2){
						status="Completed";
					}
					else if(obj.getStatus()==3){
						status="Partially Completed";
					}

					tasks = taskDao.getComponentDetails(obj.getId());
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							obj.getId(),
							obj.getTitle(),
							obj.getCreated_by().getLogin_name(),
							tasks,
							users,
							obj.getStart_time().toString(),
							obj.getEnd_time().toString(),
							obj.getActual_completion_time().toString(),
							status,obj.getDescription()},table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(visibleColumns);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	protected void generateReport() {
		try {

			if (isValid()) {

				List lst = taskDao.getAllTasksListAssignedToUser(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) createdBySelect.getValue(),
						(Long) assignedToSelect.getValue(),
						(Long) componentSelect.getValue(),
						(Long) statusSelect.getValue());

				List reportList = new ArrayList();

				/*
				 * if (lst.size() > 0) { Collections.sort(lst, new
				 * Comparator<AcctReportMainBean>() {
				 * 
				 * @Override public int compare(final AcctReportMainBean
				 * object1, final AcctReportMainBean object2) { return
				 * object1.getDate().compareTo( object2.getDate()); } }); }
				 */

				String tasks = "", users = "";
				TasksModel obj;
				TasksAssignedUsersModel detObj;
				Iterator it2;
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					obj = (TasksModel) it.next();

					tasks = "";
					users = "";

					it2 = obj.getAssignedList().iterator();
					while (it2.hasNext()) {
						detObj = (TasksAssignedUsersModel) it2.next();
						users += detObj.getUser().getLogin_name() + " , ";
					}

					tasks = taskDao.getComponentDetails(obj.getId());

					//Constructor 51
					reportList.add(new ReportBean(obj.getCreated_by()
							.getLogin_name(), obj.getTitle(), tasks, users, obj
							.getDate(), obj.getStart_time(), obj.getEnd_time(),
							obj.getActual_completion_time(), obj.getStatus(),obj.getDescription()));

				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
					params.put("Opening Balance", roundNumber(0));

					report.setJrxmlFileName("Task_Report");
					report.setReportFileName("Task Report");
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("task_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("CREATED_BY_LABEL", getPropertyName("created_by"));
					params.put("TITLE_LABEL", getPropertyName("title"));
					params.put("TASK_LABEL", getPropertyName("tasks"));
					params.put("ASSIGNED_TO_LABEL", getPropertyName("assigned_to"));
					params.put("START_DATE_LABEL", getPropertyName("start_date"));
					params.put("END_DATE_LABEL", getPropertyName("end_date"));
					params.put("COMPLETION_LABEL", getPropertyName("completion"));
					params.put("STATUS_LABEL", getPropertyName("status"));
					params.put("DESCRIPTION", getPropertyName("description"));
					
					
					
					report.setReportSubTitle(getPropertyName("from")+" : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ getPropertyName("to")+ " : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(createdBySelect
							.getItemCaption(createdBySelect.getValue()));
					report.createReport(reportList, params);

					reportList.clear();
					lst.clear();

				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (statusSelect.getValue() == null
				|| statusSelect.getValue().equals("")) {
			setRequiredError(statusSelect,
					getPropertyName("invalid_selection"), true);
			statusSelect.focus();
			ret = false;
		} else
			setRequiredError(statusSelect, null, false);

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
