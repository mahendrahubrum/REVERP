package com.inventory.reports.ui;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.LedgerViewDao;
import com.inventory.reports.dao.SalarySlipDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author anil
 *
 * 10-Nov-2016
 *
 * WebSpark
 */
public class SalarySlipReportUI extends SparkLogic {

	private static final long serialVersionUID = 5789415331022919925L;
	
	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	UserManagementDao userDao;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			setSize(350, 310);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");

			ledgertSelect = new SComboField(getPropertyName("employee"), 200,
					null, "id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("select"));

			if (getRoleID() == (long) 1) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else if (getRoleID() == (long) 2) {
				// organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			userDao=new UserManagementDao();
			
			fromDate = new SDateField(getPropertyName("month"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			buttonLayout.addComponent(generateButton);
			formLayout.addComponent(buttonLayout);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				try {
					bic = SCollectionContainer.setList(
							new OfficeDao()
									.getAllOfficeNamesUnderOrg((Long) organizationSelect
											.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				officeSelect.setContainerDataSource(bic);
				officeSelect.setItemCaptionPropertyId("name");

			}
		});

		officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderAllOffice((Long) officeSelect
									.getValue(),(Long)organizationSelect.getValue()), "id");
				} catch (Exception e) {
					e.printStackTrace();
				}
				ledgertSelect.setContainerDataSource(bic);
				ledgertSelect.setItemCaptionPropertyId("first_name");

			}
		});

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());

		mainPanel.setContent(formLayout);

		return mainPanel;
	}

	@SuppressWarnings("unchecked")
	protected void generateReport() {
		try {

			List reportList;

			UserModel ledger = userDao
					.getUser((Long) ledgertSelect.getValue());

			if (isValid()) {
				
				Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(fromDate.getValue());
				cal.set(cal.DAY_OF_MONTH, 1);
				
				Date frmDate=cal.getTime();
				
				cal.set(cal.DAY_OF_MONTH,cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
				
				Date toDate=cal.getTime();

				reportList = new SalarySlipDao().getSalarySlip((Long) ledgertSelect.getValue(),CommonUtil.getSQLDateFromUtilDate(frmDate)
						,CommonUtil.getSQLDateFromUtilDate(toDate));

				if (reportList != null && reportList.size() > 0) {

					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("LedgerName", ledger.getFirst_name());
					params.put("Office", ledger.getLoginId().getOffice().getName());

					report.setJrxmlFileName("SalarySlip");
					report.setReportFileName("Salary Slip");
					report.setReportTitle(getPropertyName("Salary Slip"));
					report.setReportSubTitle("Employee : "+ledgertSelect.getItemCaption(ledgertSelect.getValue())+"\n"+getPropertyName("Month")+" : "+CommonUtil.getMonthName(fromDate.getValue().getMonth()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, params);

					reportList.clear();

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

		if (ledgertSelect.getValue() == null
				|| ledgertSelect.getValue().equals("")) {
			setRequiredError(ledgertSelect,
					getPropertyName("invalid_selection"), true);
			ledgertSelect.focus();
			ret = false;
		} else
			setRequiredError(ledgertSelect, null, false);

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

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
