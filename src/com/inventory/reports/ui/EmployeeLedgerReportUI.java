package com.inventory.reports.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.LedgerViewDao;
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

/**
 * @author Jinshad P.T.
 * 
 *         Nov 25, 2013
 */
public class EmployeeLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private LedgerViewDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

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

			daoObj = new LedgerViewDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
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

		organizationSelect.addListener(new Property.ValueChangeListener() {
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

		officeSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(new UserManagementDao()
							.getUsersWithLedgerID((Long) officeSelect
									.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

			LedgerModel ledger = new LedgerDao()
					.getLedgeer((Long) ledgertSelect.getValue());

			if (isValid()) {

				reportList = daoObj.getLedgerView(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						getOfficeID(), (Long) ledgertSelect.getValue(),0,0);

				if (reportList != null && reportList.size() > 0) {

					Collections.sort(reportList,
							new Comparator<AcctReportMainBean>() {
								@Override
								public int compare(
										final AcctReportMainBean object1,
										final AcctReportMainBean object2) {
									return object2.getDate().compareTo(
											object1.getDate());
								}
							});

					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
					params.put("LedgerName", ledger.getName());
					params.put("Balance",
							roundNumber(ledger.getCurrent_balance()));
					params.put("Office", ledger.getOffice().getName());
					params.put("Organization", ledger.getOffice()
							.getOrganization().getName());

					report.setJrxmlFileName("EmployeeLedgerReport");
					report.setReportFileName("Employee Ledger Report");
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("employee_ledger_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("TRANSACTION_TYPE_LABEL", getPropertyName("transaction_type"));
					params.put("CREDIT_LABEL", getPropertyName("credit"));
					params.put("CASH_LABEL", getPropertyName("cash"));
					params.put("TOTAL_LABEL", getPropertyName("total"));
					params.put("name_label", getPropertyName("name"));
					params.put("organization_label", getPropertyName("organization"));
					params.put("balance_label", getPropertyName("balance"));
					params.put("office_label", getPropertyName("office"));
					
					report.setReportSubTitle(getPropertyName("from")+" : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ getPropertyName("to")+" : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));
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
