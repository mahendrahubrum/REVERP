package com.inventory.survey.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.survey.dao.SurveyDao;
import com.inventory.survey.model.SurveyModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 11, 2013
 */
public class SurveyReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private SurveyDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField createdUserSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_EMAIL = "Email";
	static String TBC_MOBILE = "Mobile";
	static String TBC_TELEPHONE = "Telephone";
	static String TBC_CONTACT_PERSON = "Contact Person";
	static String TBC_COMPANY = "Company";
	static String TBC_ACTIVITY = "Activity";
	static String TBC_DATE = "Date";
	static String TBC_DESCRIPTION = "Description";

	SHorizontalLayout mainLay;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();

			allColumns = new String[] { TBC_SN, TBC_EMAIL, TBC_MOBILE,
					TBC_TELEPHONE, TBC_CONTACT_PERSON, TBC_COMPANY,
					TBC_ACTIVITY, TBC_DATE, TBC_DESCRIPTION };
			visibleColumns = new String[] { TBC_SN, TBC_TELEPHONE,
					TBC_CONTACT_PERSON, TBC_COMPANY, TBC_ACTIVITY, TBC_DATE,
					TBC_DESCRIPTION };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1180, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_EMAIL, String.class, null,
					getPropertyName("email"), null, Align.LEFT);
			table.addContainerProperty(TBC_MOBILE, String.class, null,
					getPropertyName("mobile"), null, Align.LEFT);
			table.addContainerProperty(TBC_TELEPHONE, String.class, null,
					getPropertyName("telephone"), null, Align.LEFT);
			table.addContainerProperty(TBC_CONTACT_PERSON, String.class, null,
					getPropertyName("contact_person"), null, Align.LEFT);
			table.addContainerProperty(TBC_COMPANY, String.class, null,
					getPropertyName("company"), null, Align.LEFT);
			table.addContainerProperty(TBC_ACTIVITY, String.class, null,
					getPropertyName("activity"), null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, java.sql.Date.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_DESCRIPTION, String.class, null,
					getPropertyName("description"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_TELEPHONE, (float) 0.7);
			table.setColumnExpandRatio(TBC_CONTACT_PERSON, 1);
			table.setColumnExpandRatio(TBC_COMPANY, 1);
			table.setColumnExpandRatio(TBC_ACTIVITY, 1);
			table.setColumnExpandRatio(TBC_DATE, 1);
			table.setColumnExpandRatio(TBC_DESCRIPTION, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("800");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200);

			createdUserSelect = new SComboField(
					getPropertyName("created_user"), 200);
			createdUserSelect.setInputPrompt("-------------Select-----------");

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

			daoObj = new SurveyDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), new Date(getFinStartDate().getTime()));
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), new Date(getFinEndDate().getTime()));

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(createdUserSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

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

						List lst = new ArrayList();
						lst.add(new S_LoginModel(0, "ALL"));
						lst.addAll(new UserManagementDao()
								.getAllLoginsFromOffice((Long) officeSelect
										.getValue()));

						bic = SCollectionContainer.setList(lst, "id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					createdUserSelect.setContainerDataSource(bic);
					createdUserSelect.setItemCaptionPropertyId("login_name");
					createdUserSelect.setValue((long) 0);

				}
			});

			organizationSelect.setValue(getOrganizationID());
			officeSelect.setValue(getOfficeID());

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();

			if (isValid()) {

				long createdUser = 0;
				if (createdUserSelect.getValue() != null)
					createdUser = (Long) createdUserSelect.getValue();

				List lst = daoObj.getSurveyReport(
						(Long) officeSelect.getValue(),
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						createdUser);

				table.setVisibleColumns(allColumns);
				// TBC_SN, TBC_EMAIL, TBC_MOBILE, TBC_TELEPHONE,
				// TBC_CONTACT_PERSON, TBC_COMPANY, TBC_ACTIVITY,TBC_DATE,
				// TBC_DESCRIPTION
				int ct = 1;
				SurveyModel obj;
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					obj = (SurveyModel) it.next();

					table.addItem(
							new Object[] { ct, obj.getEmail(), obj.getMobile(),
									obj.getTelephone(),
									obj.getContact_person(), obj.getCompany(),
									obj.getActivity(), obj.getDate(),
									obj.getDescription() }, obj.getId());

					ct++;

				}

				table.setVisibleColumns(visibleColumns);

				lst.clear();
			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void generateReport() {
		try {

			if (isValid()) {

				long createdUser = 0;
				if (createdUserSelect.getValue() != null)
					createdUser = (Long) createdUserSelect.getValue();

				List reportList = daoObj.getSurveyReport(
						(Long) officeSelect.getValue(),
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						createdUser);

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());

					params.put("LedgerName", createdUserSelect
							.getItemCaption(createdUserSelect.getValue()));

					params.put("Office", officeSelect
							.getItemCaption(officeSelect.getValue()));
					params.put("Organization", organizationSelect
							.getItemCaption(organizationSelect.getValue()));

					report.setJrxmlFileName("SurveyReport");
					report.setReportFileName("Survey Ledger Report");
					report.setReportTitle("Survey Ledger Report");
					report.setReportSubTitle("From  : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ "   To  : "
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

		// if (createdUserSelect.getValue() == null
		// || createdUserSelect.getValue().equals("")) {
		// setRequiredError(createdUserSelect, "Select a Ledger", true);
		// createdUserSelect.focus();
		// ret = false;
		// } else
		// setRequiredError(createdUserSelect, null, false);

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
