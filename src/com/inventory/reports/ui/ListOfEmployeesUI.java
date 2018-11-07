package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.inventory.reports.bean.ListOfEmployeesBeans;
import com.inventory.reports.dao.ListOfEmployeesDao;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.business.AddressBusiness;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DesignationDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DesignationModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 25, 2013
 */
public class ListOfEmployeesUI extends SparkLogic {

	private static final long serialVersionUID = 175059089871644354L;

	private SOfficeComboField officeComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private Report report;
	private SComboField designationComboField;
	private SComboField departmentComboField;

	@Override
	public SPanel getGUI() {
		setSize(400, 300);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		report = new Report(getLoginID());

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		try {

			officeComboField = new SOfficeComboField(getPropertyName("office"),
					200);

			List depList = new DepartmentDao()
					.getDepartments(getOrganizationID());
			DepartmentModel depModel = new DepartmentModel();
			depModel.setId(0);
			depModel.setName("----------------All----------------");
			depList.add(0, depModel);
			departmentComboField = new SComboField(
					getPropertyName("department"), 200, depList, "id", "name");
			departmentComboField.setValue((long) 0);

			List desigList = new DesignationDao().getlabels();
			DesignationModel desModel = new DesignationModel();
			desModel.setId(0);
			desModel.setName("---------------All---------------");
			desigList.add(0, desModel);
			designationComboField = new SComboField(
					getPropertyName("designation"), 200, desigList, "id",
					"name");
			designationComboField.setValue((long) 0);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));

			generateButton = new SButton(getPropertyName("generate"));

			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(departmentComboField);
			mainFormLayout.addComponent(designationComboField);
			mainFormLayout.addComponent(reportChoiceField);
			mainFormLayout.addComponent(generateButton);
			panel.setContent(mainFormLayout);

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						List reportList = new ArrayList();
						ListOfEmployeesBeans beans = null;
						UserModel model = null;
						String genderString = "";
						String maritalStatusString = "";
						KeyValue keyValue;
						Hashtable<Character, String> hashtable = new Hashtable<Character, String>();

						try {

							List list = new ListOfEmployeesDao().getAllUsers(
									toLong(officeComboField.getValue()
											.toString()),
									toLong(departmentComboField.getValue()
											.toString()),
									toLong(designationComboField.getValue()
											.toString()),getOrganizationID());

							if (list != null && list.size() > 0) {
								List stat;
								for (int i = 0; i < list.size(); i++) {
									model = (UserModel) list.get(i);

									if (model.getGender() == 'M') {
										genderString = "Male";
									} else {
										genderString = "Female";
									}
									stat = SConstants.maritalStatusOptions;
									for (int k = 0; k < stat.size(); k++) {
										keyValue = (KeyValue) stat.get(k);
										hashtable.put(keyValue.getCharKey(),
												keyValue.getValue());
									}

									maritalStatusString = hashtable.get(model
											.getMarital_status());
									
									String address="";
									if(model.getAddress()!=null)
										address=new AddressBusiness()
										.getAddressString(model.getAddress().getId());
									beans = new ListOfEmployeesBeans(
											model.getFirst_name()
													+ model.getMiddle_name()
													+ model.getLast_name(),
											model.getEmploy_code(),
											address,
											model.getDesignation().getName(),
											model.getDepartment().getName(),
											genderString,
											maritalStatusString,
											CommonUtil
													.formatDateToDDMMMYYYY(model
															.getBirth_date()),
											CommonUtil
													.formatDateToDDMMMYYYY(model
															.getJoining_date()));
									reportList.add(beans);
								}
								if (reportList.size() > 0) {
									report.setJrxmlFileName("EmployeeList");
									report.setReportFileName("EmployeeList");
									report.setReportTitle("Employee List");
									String subTitle = "";
									if (toLong(officeComboField.getValue()
											.toString()) != 0)
										subTitle += "Office : "
												+ officeComboField
														.getItemCaption(officeComboField
																.getValue())
												+ "\n";
									if (toLong(departmentComboField.getValue()
											.toString()) != 0)
										subTitle += "Department : "
												+ departmentComboField
														.getItemCaption(departmentComboField
																.getValue())
												+ "    \t   ";
									if (toLong(designationComboField.getValue()
											.toString()) != 0)
										subTitle += "Designation : "
												+ designationComboField
														.getItemCaption(designationComboField
																.getValue());
									report.setReportSubTitle(subTitle);
									report.setReportType(toInt(reportChoiceField
											.getValue().toString()));
									report.setIncludeHeader(true);
									report.setOfficeName(officeComboField
											.getItemCaption(officeComboField
													.getValue()));
									report.createReport(reportList, null);

									reportList.clear();
									list.clear();

								} else {
									SNotification
											.show(getPropertyName("no_data_available"),
													Type.WARNING_MESSAGE);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}

	@Override
	public Boolean isValid() {
		return true;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
