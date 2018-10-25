package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.reports.bean.ProfitReportBean;
import com.inventory.reports.dao.ProfitReportDao;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

/**
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Feb 19 2014
 */
public class LaundryProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -6316053747180409181L;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SButton generateButton;

	private SOfficeComboField officeComboField;
	private Report report;
	private SReportChoiceField reportChoiceField;

	@Override
	public SPanel getGUI() {
		setSize(320, 220);

		report = new Report(getLoginID());

		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);
		mainFormLayout.setSpacing(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(generateButton);
		mainFormLayout.setComponentAlignment(generateButton,
				Alignment.MIDDLE_CENTER);

		panel.setContent(mainFormLayout);

		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					HashMap<String, Object> map = new HashMap<String, Object>();

					List reportList = new ArrayList();
					ProfitReportDao dao = new ProfitReportDao();

					try {
						double purchase = dao.getTotalPurchaseAmount(
								getOfficeID(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));

						double purchase_return = dao
								.getTotalPurchaseReturnAmount(
										getOfficeID(),
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));

						double sales = dao.getLaundryTotalSalesAmount(
								getOfficeID(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));

						double sales_return = dao.getTotalSalesReturnAmount(
								getOfficeID(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));

						double transportation = dao
								.getTotalTransportationAmount(
										getOfficeID(),
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));

						double expendetures = dao.getTotalExpentitureTransactionAmount(
								getOfficeID(), CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()));

						// map.put("PURCHASE", dao.getAllTransactions(
						// SConstants.PURCHASE, getOfficeID(), CommonUtil
						// .getSQLDateFromUtilDate(fromDateField
						// .getValue()), CommonUtil
						// .getSQLDateFromUtilDate(toDateField
						// .getValue())));
						// map.put("SALES", dao.getAllTransactions(
						// SConstants.SALES, getOfficeID(), CommonUtil
						// .getSQLDateFromUtilDate(fromDateField
						// .getValue()), CommonUtil
						// .getSQLDateFromUtilDate(toDateField
						// .getValue())));

						reportList.add(new ProfitReportBean(purchase,
								purchase_return, sales, sales_return,
								transportation, expendetures,
								(sales - sales_return)
										- (purchase - purchase_return)
										- transportation - expendetures));

						report.setJrxmlFileName("Profit_Report");
						report.setReportFileName("Profit_Report");
						report.setReportTitle("Profit Report");
						String subTitle = "From : "
								+ CommonUtil
										.formatDateToDDMMMYYYY(fromDateField
												.getValue())
								+ "\t To : "
								+ CommonUtil.formatDateToDDMMMYYYY(toDateField
										.getValue());

						report.setReportSubTitle(subTitle);
						report.setReportType(toInt(reportChoiceField.getValue()
								.toString()));
						report.setIncludeHeader(true);
						report.setIncludeFooter(false);
						report.setOfficeName(officeComboField
								.getItemCaption(officeComboField.getValue()));
						report.createReport(reportList, map);

						reportList.clear();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		return panel;
	}

	@Override
	public Boolean isValid() {

		fromDateField.setComponentError(null);
		if (fromDateField.getValue().after(toDateField.getValue())) {
			setRequiredError(fromDateField, "Invalid Date Selection", true);
			return false;
		}
		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
