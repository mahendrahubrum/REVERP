package com.inventory.reports.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinServlet;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.mailclient.dao.MailDao;
import com.webspark.mailclient.model.MyMailsModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Mar 31 2014
 */

public class EmailReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField emailIdComboField;
	// private SComboField salesNoComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private UserManagementDao userDao;

	LedgerDao ledDao;

	SRadioButton filterTypeRadio;

	private Report report;

	SalesDao salDao;

	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();
		salDao = new SalesDao();

		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(380, 330);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		mainFormLayout.addComponent(dateHorizontalLayout);

		filterTypeRadio = new SRadioButton(getPropertyName("payment_type"),
				250, SConstants.emailFoldersList, "intKey", "value");
		filterTypeRadio.setStyleName("radio_horizontal");
		filterTypeRadio.setValue(0);

		mainFormLayout.addComponent(filterTypeRadio);

		try {
			List<Object> emailsList = new CommonMethodsDao()
					.getAllEmailsAsKeyValueObject(getLoginID());
			KeyValue ledgerModel = new KeyValue("ALL", "ALL");
			if (emailsList == null) {
				emailsList = new ArrayList<Object>();
			}
			emailsList.add(0, ledgerModel);
			emailIdComboField = new SComboField(getPropertyName("email"), 200,
					emailsList, "stringKey", "value", false, "ALL");
			mainFormLayout.addComponent(emailIdComboField);

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

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();

						long salesNo = 0;
						long custId = 0;
						String salesPerson = "";

						String email = "ALL";
						long folder_id = (Integer) filterTypeRadio.getValue();
						if (emailIdComboField.getValue() != null)
							email = (String) emailIdComboField.getValue();

						List<MyMailsModel> lst = new MailDao().getEmailReport(
								getLoginID(), email, folder_id, CommonUtil
										.getTimestampFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getTimestampFromUtilDate(toDateField
												.getValue()));

						for (MyMailsModel obj : lst) {
							obj.setDetails(reaDetailsFile(getLoginID() + "_1_"
									+ obj.getFolder_id() + "_"
									+ obj.getMail_number() + ".txt"));
							reportList.add(obj);
						}

						if (reportList.size() > 0) {
							report.setJrxmlFileName("Email_Report");
							report.setReportFileName("Email Report");
							report.setReportTitle("Email Report");
							String subHeader = "";

							subHeader += "\n From : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t To : "
									+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue());

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName("");
							report.createReport(reportList, null);

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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public String reaDetailsFile(String fileName) {
		String details = "";
		try {
			FileInputStream fisTargetFile = new FileInputStream(new File(
					VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")
							+ "Mails/" + fileName));

			details = IOUtils.toString(fisTargetFile, "UTF-8");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return details;
	}

}
