package com.inventory.payroll.ui;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.dao.EmployeeWorkingTimeDao;
import com.inventory.payroll.model.EmployeeWorkingTimeModel;
import com.vaadin.data.Item;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 6, 2013
 */
public class EmployeeWorkingTimeUI extends SparkLogic {

	private static final long serialVersionUID = -5506860089168699476L;

	private SPanel mainPanel;
	private SFormLayout mainFormLayout;

	private InlineDateField monthField;
	private STable employeeSTable;

	private static final String TBL_EMP_ID = "Emp Id";
	private static final String TBL_EMP_NAME = "Employee";
	private static final String TBL_SALARY_TYPE_ID = "Salary Type Id";
	private static final String TBL_SALARY_TYPE = "Salary Type";
	private static final String TBL_WORKING_TIME = "Worked Days";

	private String allHeaders[];
	private String reqHeaders[];

	private SButton saveButton;

	private EmployeeWorkingTimeDao dao;
	private SettingsValuePojo settings;
	private WrappedSession session;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		setSize(670, 470);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		dao = new EmployeeWorkingTimeDao();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		SHorizontalLayout buttHorizontalLayout = new SHorizontalLayout();
		buttHorizontalLayout.setSpacing(true);

		mainPanel.setContent(mainFormLayout);

		monthField = new InlineDateField(getPropertyName("month"));
		monthField.setResolution(Resolution.MONTH);
		monthField.setImmediate(true);
		Calendar cal = Calendar.getInstance();
		cal.setTime(getWorkingDate());
		cal.set(Calendar.DATE, 1);
		monthField.setValue(cal.getTime());

		allHeaders = new String[] { TBL_EMP_ID, TBL_EMP_NAME,
				TBL_SALARY_TYPE_ID, TBL_SALARY_TYPE, TBL_WORKING_TIME };

		reqHeaders = new String[] { TBL_EMP_NAME, TBL_SALARY_TYPE,
				TBL_WORKING_TIME };

		employeeSTable = new STable(null, 550, 300);
		employeeSTable.addContainerProperty(TBL_EMP_ID, Long.class, null,
				TBL_EMP_ID, null, Align.CENTER);
		employeeSTable.addContainerProperty(TBL_EMP_NAME, String.class, null,
				getPropertyName("employee"), null, Align.LEFT);
		employeeSTable.addContainerProperty(TBL_SALARY_TYPE_ID, Integer.class,
				null, TBL_SALARY_TYPE_ID, null, Align.CENTER);
		employeeSTable.addContainerProperty(TBL_SALARY_TYPE, String.class,
				null, getPropertyName("salary_type"), null, Align.LEFT);
		employeeSTable.addContainerProperty(TBL_WORKING_TIME, STextField.class,
				null, getPropertyName("worked_days"), null, Align.CENTER);

		loadData();

		mainFormLayout.addComponent(monthField);
		mainFormLayout.addComponent(employeeSTable);

		saveButton = new SButton(getPropertyName("Save"));

		buttHorizontalLayout.addComponent(saveButton);
		mainFormLayout.addComponent(buttHorizontalLayout);

		monthField.addListener(new Listener() {

			@Override
			public void componentEvent(Event event) {
				loadData();

			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					EmployeeWorkingTimeModel model = null;
					Item item = null;
					boolean flag = true;
					Vector<EmployeeWorkingTimeModel> vector = new Vector<EmployeeWorkingTimeModel>();

					Iterator itr = employeeSTable.getItemIds().iterator();
					while (itr.hasNext()) {
						item = employeeSTable.getItem(itr.next());
						model = new EmployeeWorkingTimeModel();

						model.setMonth(CommonUtil
								.getSQLDateFromUtilDate(monthField.getValue()));
						model.setEmployee(new UserModel(toLong(item
								.getItemProperty(TBL_EMP_ID).toString())));
						model.setWorking_time(toDouble(((STextField) item
								.getItemProperty(TBL_WORKING_TIME).getValue())
								.getValue().toString()));
						vector.add(model);

					}

					try {
						dao.save(vector);
						Notification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
					} catch (Exception e) {
						e.printStackTrace();
						Notification
								.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);

					}

				}
			}
		});

		return mainPanel;
	}

	private void loadData() {
		try {
			employeeSTable.setVisibleColumns(allHeaders);
			employeeSTable.removeAllItems();

			List list = null;
			if (settings.isSHOW_ALL_EMPLOYEES_ON_PAYROLL())
				list = dao.getEmployees(getOrganizationID());
			else
				list = dao.getEmployeesUnderOffice(getOfficeID());

			UserModel model = null;
			Object row[] = null;

			STextField field = null;
			KeyValue kv = null;
			Iterator itr = SConstants.payroll.salaryTypes.iterator();
			String type = "";
			double workingTime = 0;

			for (int i = 0; i < list.size(); i++) {

				model = (UserModel) list.get(i);
				while (itr.hasNext()) {
					kv = (KeyValue) itr.next();
					if (kv.getIntKey() == model.getSalary_type()) {
						type = kv.getValue();
						break;
					}
					type = "";
				}

				workingTime = dao.getWorkingTime(CommonUtil
						.getSQLDateFromUtilDate(monthField.getValue()), model
						.getId());

				field = new STextField();
				field.setValue(workingTime + "");
				row = new Object[] {
						model.getId(),
						model.getFirst_name() + " " + model.getMiddle_name()
								+ " " + model.getLast_name(),
						model.getSalary_type(), type, field };
				employeeSTable.addItem(row, i + 1);
			}
			employeeSTable.setVisibleColumns(reqHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		boolean flag = true;
		employeeSTable.setComponentError(null);
		Iterator itr = employeeSTable.getItemIds().iterator();
		Item item = null;
		while (itr.hasNext()) {
			item = employeeSTable.getItem(itr.next());

			if (((STextField) item.getItemProperty(TBL_WORKING_TIME).getValue())
					.getValue() == null) {
				flag = false;
				setRequiredError(employeeSTable, getPropertyName("invalid_msg")
						+ item.getItemProperty(TBL_EMP_NAME).getValue(), true);
				break;
			} else {
				try {
					if (toDouble(((STextField) item.getItemProperty(
							TBL_WORKING_TIME).getValue()).getValue().toString()) > 30) {
						setRequiredError(employeeSTable,
								getPropertyName("invalid_data_msg")
										+ item.getItemProperty(TBL_EMP_NAME)
												.getValue(), true);
						flag = false;
						break;
					}
				} catch (Exception e) {
					setRequiredError(employeeSTable,
							getPropertyName("invalid_msg")
									+ item.getItemProperty(TBL_EMP_NAME)
											.getValue(), true);
					flag = false;
					break;
				}
			}

		}

		if (employeeSTable.getItemIds().size() <= 0) {
			flag = false;
			setRequiredError(employeeSTable, getPropertyName("invalid_data"),
					true);
		}

		return flag;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
