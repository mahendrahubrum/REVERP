package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.onlineSales.dao.ImportOnlineSalesOrderDao;
import com.inventory.onlineSales.model.OnlineCustomerModel;
import com.inventory.onlineSales.model.OnlineSalesOrderDetailsModel;
import com.inventory.onlineSales.model.OnlineSalesOrderModel;
import com.inventory.reports.bean.SalesReportBean;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
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
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 29, 2014
 */
public class OnlineSalesOrderReportUI extends SparkLogic {

	private static final long serialVersionUID = -4799385370519917901L;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
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

	ImportOnlineSalesOrderDao ledDao;
	ItemDao itemDao;

	private Report report;

	@Override
	public SPanel getGUI() {

		ledDao = new ImportOnlineSalesOrderDao();
		itemDao = new ItemDao();

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

		try {
			List<Object> customerList = ledDao.getAllOnlineCustomers();
			OnlineCustomerModel ledgerModel = new OnlineCustomerModel();
			ledgerModel.setId(0);
			ledgerModel
					.setFirstName("---------------------ALL-------------------");
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(
					getPropertyName("online_customer"), 200, customerList,
					"id", "firstName", false, "ALL");
			mainFormLayout.addComponent(customerComboField);

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

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerComboField
										.getValue().toString());
							}
						}
					});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						OnlineSalesOrderModel salesModel = null;
						OnlineSalesOrderDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";
						OnlineCustomerModel cust;

						List reportList = new ArrayList<Object>();

						long custId = 0;

						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						List<Object> salesModelList = ledDao
								.getOnlineSalesOrderDetailsReport(
										custId,
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()));

						List<OnlineSalesOrderDetailsModel> detailsList;
						for (int i = 0; i < salesModelList.size(); i++) {
							salesModel = (OnlineSalesOrderModel) salesModelList
									.get(i);
							detailsList = ledDao
									.getOnlineSalesOrderDetails(salesModel
											.getId());
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += itemDao.getItem(
										inventoryDetailsModel.getItem())
										.getName()
										+ " ( Qty : "
										+ inventoryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}

							cust = ledDao.getOnlineCustomer(salesModel
									.getOnlineCustomer());
							if (cust != null) {
								reportBean = new SalesReportBean();
								reportBean.setCustomer(cust.getFirstName()
										+ " " + cust.getLastName());
								reportBean.setAmount(salesModel
										.getTotalAmount());
								reportBean.setDate(salesModel.getDate()
										.toString());
								reportBean.setItems(items);
								reportBean.setStatus(getStatus(salesModel
										.getStatus()));

								reportList.add(reportBean);
							}
						}

						if (reportList != null && reportList.size() > 0) {
							Collections.sort(reportList,
									new Comparator<SalesReportBean>() {
										@Override
										public int compare(
												final SalesReportBean object1,
												final SalesReportBean object2) {

											int result = object2.getDate()
													.compareTo(
															object1.getDate());
											return result;
										}

									});

							report.setJrxmlFileName("OnlineSalesOrderReport");
							report.setReportFileName("OnlineSalesOrderReport");
							report.setReportTitle(getPropertyName("online_sales_order_report"));
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("customer")+" : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}

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
							report.setOfficeName(getOfficeName());
							report.createReport(reportList, null);

							reportList.clear();
							salesModelList.clear();

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

	protected String getStatus(long status) {
		String stats;
		if (status == 1) {
			stats = "Sales Order Created";
		} else {
			stats = "Sales Order Approved";
		}
		return stats;
	}

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			custList = ledDao.getAllOnlineCustomers();
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			customerComboField.setContainerDataSource(custContainer);
			customerComboField.setItemCaptionPropertyId("firstName");
			customerComboField.setValue(0);
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
