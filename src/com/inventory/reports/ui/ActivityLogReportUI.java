package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.dao.ActivityLogReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Jinshad P.T. WebSpark.
 * @Date Mar 10 2014
 */

public class ActivityLogReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField employeeCombo;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	ActivityLogReportDao daoObj;

	@Override
	public SPanel getGUI() {

		daoObj = new ActivityLogReportDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(400, 300);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField(getPropertyName("from_date"), 150);
		toDateField = new SDateField(getPropertyName("to_date"), 150);

		fromDateField.setDateFormat("dd/MM/yyyy hh:mm aa");
		toDateField.setDateFormat("dd/MM/yyyy hh:mm aa");
		toDateField.setResolution(Resolution.SECOND);
		fromDateField.setResolution(Resolution.SECOND);

		fromDateField.setValue(getFormattedTime(getMonthStartDate()));
		toDateField.setValue(getFormattedTime(getWorkingDate()));

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			employeeCombo = new SComboField(getPropertyName("employee"), 200,
					null, "id", "name", false, getPropertyName("all"));

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(employeeCombo);
			mainFormLayout.addComponent(fromDateField);
			mainFormLayout.addComponent(toDateField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainPanel.setContent(mainFormLayout);

			organizationComboField
					.addValueChangeListener(new ValueChangeListener() {
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

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadEmployeeCombo(toLong(officeComboField.getValue()
							.toString()));
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

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						List<Object> reportList;
						long emp_id = 0;

						if (employeeCombo.getValue() != null
								&& !employeeCombo.getValue().toString()
										.equals("0")) {
							emp_id = (Long) employeeCombo.getValue();
						}
						
							boolean isAdmin=false;
							if(isSuperAdmin()||isSystemAdmin())
								isAdmin=true;

						reportList = daoObj.getActivityLogs(emp_id, CommonUtil
								.getTimestampFromUtilDate(fromDateField
										.getValue()), CommonUtil
								.getTimestampFromUtilDate(toDateField
										.getValue()), toLong(officeComboField
								.getValue().toString()), getLoginID(),isAdmin);

						if (reportList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("ActivityLog_Report");
							report.setReportFileName("Activity Log Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("activity_log_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("USER_LABEL", getPropertyName("user"));
							map.put("LOG_DETAILS_LABEL", getPropertyName("log_details"));

							String subHeader = "";

							if (employeeCombo.getValue() != null)
								if (!employeeCombo.getValue().toString()
										.equals("0"))
									subHeader += getPropertyName("employee")+" : "
											+ employeeCombo
													.getItemCaption(employeeCombo
															.getValue()) + "\t";

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
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);

							reportList.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
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

	protected void loadEmployeeCombo(long officeId) {
		List<Object> custList = null;
		try {

			if(isSuperAdmin()||isSystemAdmin())
				custList = new UserManagementDao().getAllUsersUnderOffice(
						officeId);
			else
				custList = new UserManagementDao().getEmployeesUnderUser(
						officeId, getLoginID());
			
			S_LoginModel ledgerModel = new S_LoginModel();
			ledgerModel.setId(0);
			ledgerModel
					.setLogin_name(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);

			custContainer = SCollectionContainer.setList(custList, "id");
			employeeCombo.setContainerDataSource(custContainer);
			employeeCombo.setItemCaptionPropertyId("login_name");
			employeeCombo.setValue(getLoginID());

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
