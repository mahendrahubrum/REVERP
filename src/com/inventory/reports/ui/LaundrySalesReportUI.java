package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.LaundrySalesDetailsModel;
import com.inventory.sales.model.LaundrySalesModel;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Feb 19 2014
 */
public class LaundrySalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SOfficeComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SComboField salesNoComboField;
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

		setSize(385, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(dateHorizontalLayout);

		filterTypeRadio = new SRadioButton(getPropertyName("payment_type"),
				250, SConstants.filterTypeList, "intKey", "value");
		filterTypeRadio.setStyleName("radio_horizontal");
		filterTypeRadio.setValue(0);

		mainFormLayout.addComponent(filterTypeRadio);

		try {
			List<Object> customerList = ledDao.getAllCustomersWithoutCode(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(getPropertyName("customer"),
					200, customerList, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(customerComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_bill_no"), 200, null, "id",
					"comments", false, getPropertyName("all"));
			mainFormLayout.addComponent(salesNoComboField);

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
							loadBillNo(customerId, toLong(officeComboField
									.getValue().toString()));
						}
					});

			filterTypeRadio.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			fromDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			toDateField.addListener(new Listener() {
				@Override
				public void componentEvent(Event event) {
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
					loadBillNo(customerId, toLong(officeComboField.getValue()
							.toString()));
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						LaundrySalesModel salesModel = null;
						LaundrySalesDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList = new ArrayList<Object>();

						long salesNo = 0;
						long custId = 0;
						String salesPerson = "";

						if (salesNoComboField.getValue() != null
								&& !salesNoComboField.getValue().equals("")
								&& !salesNoComboField.getValue().toString()
										.equals("0")) {
							salesNo = toLong(salesNoComboField
									.getItemCaption(salesNoComboField
											.getValue()));
						}
						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}

						String condition1 = "";

						if ((Integer) filterTypeRadio.getValue() == 1) {
							condition1 = " and payment_amount=amount ";
						} else if ((Integer) filterTypeRadio.getValue() == 2) {
							condition1 = " and payment_amount<amount ";
						}

						List<Object> salesModelList = new SalesReportDao()
								.getLaundrySalesDetails(
										salesNo,
										custId,
										CommonUtil
												.getSQLDateFromUtilDate(fromDateField
														.getValue()),
										CommonUtil
												.getSQLDateFromUtilDate(toDateField
														.getValue()),
										toLong(officeComboField.getValue()
												.toString()), condition1);
						double qty;
						List<LaundrySalesDetailsModel> detailsList;
						for (int i = 0; i < salesModelList.size(); i++) {
							noData = false;
							salesPerson = "";
							salesModel = (LaundrySalesModel) salesModelList
									.get(i);
							detailsList = salesModel.getDetails_list();
							items = "";
							qty=0;
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inventoryDetailsModel.getQuantity()
										+ " , Rate : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
								
								qty+=inventoryDetailsModel.getQuantity();
							}

							salesPerson = userDao.getUserFromLogin(
									salesModel.getSales_person())
									.getFirst_name();

							reportBean = new SalesReportBean(salesModel
									.getDate().toString(), salesModel
									.getCustomer().getName(), String
									.valueOf(salesModel.getSales_number()),
									salesModel.getOffice().getName(), items,
									salesModel.getAmount(), salesPerson,salesModel.getRoom_no(),qty);
							reportList.add(reportBean);

						}

						if (!noData) {
							report.setJrxmlFileName("LaundrySales_Report");
							report.setReportFileName("SalesReport");
							report.setReportTitle("Sales Report");
							String subHeader = "";
							if (customerId != 0) {
								subHeader += ""
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
//							if (salesNo != 0) {
//								subHeader += "Sales No : "
//										+ salesNoComboField
//												.getItemCaption(salesNoComboField
//														.getValue());
//							}
							
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("IMAGE_PATH", VaadinServlet.getCurrent().getServletContext()
									.getRealPath("/").toString());
							
							
							params.put( "date_intervel", "From : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t To : "+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue()));

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, params);

							reportList.clear();
							salesModelList.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show("Error Happened . Msg : "+e,
								Type.ERROR_MESSAGE);
					}
				}
			});
			
			loadBillNo(0, getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	private void loadBillNo(long customerId, long officeId) {
		List<Object> salesList = null;
		try {

			String condition1 = "";

			if ((Integer) filterTypeRadio.getValue() == 1) {
				condition1 = " and payment_amount=amount ";
			} else if ((Integer) filterTypeRadio.getValue() == 2) {
				condition1 = " and payment_amount<amount ";
			}
			condition1 += " and active=true";
			if (customerId != 0) {
//				salesList = salDao.getAllLaundrySalesNumbersForCustomer(officeId,
//						customerId, CommonUtil
//								.getSQLDateFromUtilDate(fromDateField
//										.getValue()),
//						CommonUtil.getSQLDateFromUtilDate(toDateField
//								.getValue()), condition1);
			} else {
//				salesList = salDao.getAllLaundrySalesNumbersByDate(officeId,
//						CommonUtil.getSQLDateFromUtilDate(fromDateField
//								.getValue()),
//						CommonUtil.getSQLDateFromUtilDate(toDateField
//								.getValue()), condition1);
			}

			SalesModel salesModel = new SalesModel();
			salesModel.setId(0);
			salesModel
					.setComments("---------------------ALL-------------------");
			if (salesList == null) {
				salesList = new ArrayList<Object>();
			}
			salesList.add(0, salesModel);
			container = SCollectionContainer.setList(salesList, "id");
			salesNoComboField.setContainerDataSource(container);
			salesNoComboField.setItemCaptionPropertyId("comments");
			salesNoComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			if (officeId != 0) {
				custList = ledDao.getAllCustomersWithoutCode(officeId);
			} else {
				custList = ledDao.getAllCustomers();
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			customerComboField.setContainerDataSource(custContainer);
			customerComboField.setItemCaptionPropertyId("name");
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
