package com.inventory.reports.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.bean.UnderCreditPeriodBean;
import com.inventory.reports.dao.NotPayedSaleReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;

/**
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Oct 2, 2013
 */
public class CustomersUnderCreditLimitReportUI extends SparkLogic {

	private static final long serialVersionUID = 7557911380966377544L;

	private static final String PROMPT_ALL = "----------------ALL--------------";

	private SReportChoiceField choiceField;
	private SOfficeComboField officeComboField;
	private SButton generateButton;

	private NotPayedSaleReportDao objDao;

	private Report report;

	private SRadioButton typeButton;

	CustomerDao custDao;

	@Override
	public SPanel getGUI() {

		custDao = new CustomerDao();

		setSize(320, 250);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);
		mainFormLayout.setSpacing(true);

		objDao = new NotPayedSaleReportDao();
		report = new Report(getLoginID());

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		typeButton = new SRadioButton("", 200, Arrays.asList(new KeyValue(1,
				getPropertyName("under_credit_limit")),
				new KeyValue(2, getPropertyName("exceeded_credit_limit"))), "intKey", "value");
		typeButton.select(1);
		List groupList;
		try {
			groupList = custDao.getAllCustomersNames(getOfficeID());
		} catch (Exception e) {
			groupList = new ArrayList();
			e.printStackTrace();
		}
		CustomerModel custModel = new CustomerModel();
		custModel.setId(0);
		custModel.setName(PROMPT_ALL);
		groupList.add(0, custModel);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		choiceField = new SReportChoiceField(getPropertyName("export_to"));

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(typeButton);
		mainFormLayout.addComponent(choiceField);
		mainFormLayout.addComponent(generateButton);

		panel.setContent(mainFormLayout);

		officeComboField.setValue(getOfficeID());

		// if(getRoleID()>2){
		// officeComboField.setReadOnly(true);
		// }

		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {

						List reportList = new ArrayList();

						if (toInt(typeButton.getValue().toString()) == 1) {
							List list = custDao
									.getCustomersCreditDetails((Long) officeComboField
											.getValue());

							AcctReportMainBean details;
							Iterator it = list.iterator();
							int i = 0;
							CustomerModel obj;
							while (it.hasNext()) {
								obj = (CustomerModel) it.next();

								details = new AcctReportMainBean();

								details.setName(obj.getName());
								details.setTotal(obj.getCredit_limit());
								details.setPayed(Math.abs(obj.getLedger()
										.getCurrent_balance()));
								details.setParticulars(obj.getAddress().getPhone()
										+ "   "
										+ obj.getAddress()
												.getMobile());
								reportList.add(details);

							}

							if (reportList.size() > 0) {
								HashMap<String, Object> params = new HashMap<String, Object>();
								params.put("FromDate", new java.util.Date());
								params.put("ToDate", new java.util.Date());

								report.setJrxmlFileName("CustomersUnderCreditLimitReport");
								report.setReportFileName("Customers Under Credit Limit");

								report.setReportTitle("Customers Under Credit Limit");

								report.setIncludeHeader(true);
								report.setReportSubTitle("Customers Under Credit Limit");
								report.setReportType((Integer) choiceField
										.getValue());
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, params);

								reportList.clear();
								list.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

						} else {

							List list = custDao
									.getAllCustomersNamesList((Long) officeComboField
											.getValue());

							Iterator it = list.iterator();

							UnderCreditPeriodBean bean = null;
							int i = 0;
							CustomerModel obj;
							Calendar cal;
							List list2;
							String salesNos = " ";
							double payed = 0, total = 0;
							Iterator it2;
							SalesModel salObj;
							while (it.hasNext()) {

								obj = (CustomerModel) it.next();

								cal = Calendar.getInstance();
								cal.setTime(new java.util.Date());

								cal.add(Calendar.DAY_OF_MONTH,
										-obj.getMax_credit_period());

								list2 = new SalesDao()
										.getAllSalesDetailsForCustomer(obj
												.getLedger().getId(),
												getFinStartDate(), new Date(cal
														.getTime().getTime()));

								if (list2.size() > 0) {

									salesNos = " ";
									payed = 0;
									total = 0;
									it2 = list2.iterator();
									while (it2.hasNext()) {
										salObj = (SalesModel) it2.next();

										salesNos += " "
												+ salObj.getSales_number()
												+ " ,";
										payed += salObj.getPayment_amount();
										total += salObj.getAmount();

									}
									bean = new UnderCreditPeriodBean(obj
											.getName(), salesNos.substring(0,
											salesNos.length() - 1), total,
											total - payed);
									i++;
									reportList.add(bean);
								}

							}

							if (reportList.size() > 0) {
								HashMap<String, Object> params = new HashMap<String, Object>();

								report.setJrxmlFileName("CustomersUnderCreditPeriodReport");
								report.setReportFileName("CustomersUnderCreditPeriodReport");

								report.setReportTitle("Customers Exceeded Credit Period");

								report.setIncludeHeader(true);
								report.setReportSubTitle("");
								report.setReportType((Integer) choiceField
										.getValue());
								report.createReport(reportList, params);

								reportList.clear();
								list.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

						}

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
		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
