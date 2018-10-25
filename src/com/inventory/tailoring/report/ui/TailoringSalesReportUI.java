package com.inventory.tailoring.report.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.sales.dao.TailoringSalesDao;
import com.inventory.sales.model.TailoringSalesInventoryDetailsModel;
import com.inventory.sales.model.TailoringSalesModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.sales.ui.TailoringSalesUI;
import com.inventory.tailoring.report.dao.TailoringSalesReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 22, 2014
 */
public class TailoringSalesReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField officeComboField;
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
	private SButton showButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private UserManagementDao userDao;

	LedgerDao ledDao;

	SRadioButton filterTypeRadio;
	SRadioButton statusRadioButton;

	private Report report;

	TailoringSalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_ITEMS = "Items";

	private STable table;

	private String[] allColumns;
	private String[] visibleColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;
	SHorizontalLayout popupContainer;
	@Override
	public SPanel getGUI() {

		allColumns = new String[] { TBC_SN, TBC_ID, TBC_SALE_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };
		visibleColumns = new String[] { TBC_SN, TBC_SALE_NO, TBC_DATE,
				TBC_CUSTOMER, TBC_AMOUNT, TBC_ITEMS };

		ledDao = new LedgerDao();
		salDao = new TailoringSalesDao();
		popupContainer = new SHorizontalLayout();
		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();

		setSize(1100, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		SHorizontalLayout mainLay = new SHorizontalLayout();

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		try {

			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				sett = (SettingsValuePojo) session.getAttribute("settings");

			officeComboField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			fromDateField = new SDateField(getPropertyName("from_date"));
			fromDateField.setValue(getMonthStartDate());
			fromDateField.setImmediate(true);
			toDateField = new SDateField(getPropertyName("to_date"));
			toDateField.setValue(getWorkingDate());
			toDateField.setImmediate(true);
			dateHorizontalLayout.addComponent(fromDateField);
			dateHorizontalLayout.addComponent(toDateField);
			mainFormLayout.addComponent(officeComboField);
			mainFormLayout.addComponent(dateHorizontalLayout);

			filterTypeRadio = new SRadioButton(getPropertyName("payment_type"),
					250, SConstants.filterTypeList, "intKey", "value");
			filterTypeRadio.setStyleName("radio_horizontal");
			filterTypeRadio.setValue(0);

			statusRadioButton = new SRadioButton(getPropertyName("status"),
					250, Arrays.asList(new KeyValue(0, "Active"), new KeyValue(
							1, "Cancelled")), "intKey", "value");
			statusRadioButton.setStyleName("radio_horizontal");
			statusRadioButton.setValue(0);

			mainFormLayout.addComponent(filterTypeRadio);

			mainFormLayout.addComponent(statusRadioButton);

			List<Object> customerList = ledDao.getAllCustomers(getOfficeID());
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			customerComboField = new SComboField(getPropertyName("customer"),
					200, customerList, "id", "name", false, "ALL");
			mainFormLayout.addComponent(customerComboField);

			salesNoComboField = new SComboField(
					getPropertyName("sales_bill_no"), 200, null, "id",
					"comments", false, "ALL");
			mainFormLayout.addComponent(salesNoComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			table = new STable(null, 670, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_SALE_NO, String.class, null,
					getPropertyName("sales_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,
					getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_ITEMS, String.class, null,
					getPropertyName("items"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 1.2f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_ITEMS, 2f);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainPanel.setContent(mainLay);

			salesNoComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
				}
			});

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

			statusRadioButton.addListener(new Listener() {
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
						TailoringSalesModel salesModel = null;
						TailoringSalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";
						String salesPerson = "";

						List<Object> reportList = new ArrayList<Object>();
						List<Object> salesModelList = getSalesReportList();

						List<TailoringSalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < salesModelList.size(); i++) {
							noData = false;
							salesPerson = "";
							salesModel = (TailoringSalesModel) salesModelList.get(i);
							detailsList = salesModel
									.getTailoring_inventory_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inventoryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}
							
							salesPerson = userDao.getUserNameFromLoginID(
									salesModel.getSales_person());

							reportBean = new SalesReportBean(CommonUtil
									.getUtilDateFromSQLDate(
											salesModel.getDate()).toString(),
									salesModel.getCustomer().getName(), String
											.valueOf(salesModel
													.getSales_number()),
									salesModel.getOffice().getName(), items,
									salesModel.getAmount(), salesPerson,
									salesModel.getPayment_amount()+salesModel.getPaid_by_payment());
							reportList.add(reportBean);

						}

						if (!noData) {
							report.setJrxmlFileName("TailoringSales_Report");
							report.setReportFileName("SalesReport");
							report.setReportTitle("Sales Report");
							String subHeader = "";
							if (customerId != 0) {
								subHeader += "Customer : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue()) + "\t";
							}
							if (salesNoComboField.getValue() != null
									&& !salesNoComboField.getValue().toString()
											.equals("0")) {
								subHeader += "Sales No : "
										+ salesNoComboField
												.getItemCaption(salesNoComboField
														.getValue());
							}

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
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, null);

							reportList.clear();
							salesModelList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					TailoringSalesModel salesModel = null;
					TailoringSalesInventoryDetailsModel inventoryDetailsModel = null;
					String items = "";
					Object[] row;

					List repList = getSalesReportList();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
					if (repList != null && repList.size() > 0) {
						List<TailoringSalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < repList.size(); i++) {
							salesModel = (TailoringSalesModel) repList.get(i);
							detailsList = salesModel
									.getTailoring_inventory_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( Qty : "
										+ inventoryDetailsModel.getQunatity()
										+ " , Rate : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}

							row = new Object[] {
									i + 1,
									salesModel.getId(),
									salesModel.getSales_number() + "",
									CommonUtil.getUtilFromSQLDate(salesModel
											.getDate()) + "",
									salesModel.getCustomer().getName(),
									salesModel.getAmount(), items };
							table.addItem(row, i + 1);

							ttl += salesModel.getAmount();

						}
						table.setColumnFooter(TBC_AMOUNT,
								asString(roundNumber(ttl)));

					} else
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
				}
			});

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action("Edit");
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							TailoringSalesUI sales = new TailoringSalesUI();
							sales.setCaption("Sales");
							sales.loadSale((Long) item.getItemProperty(TBC_ID).getValue());
							sales.center();
							getUI().getCurrent().addWindow(sales);
							sales.addCloseListener(closeListener);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionDelete };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							TailoringSalesModel sale=new TailoringSalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>Sale</u></h2>"));
							form.addComponent(new SLabel("Sales No",sale.getSales_number()+""));
							form.addComponent(new SLabel("Customer",sale.getCustomer().getName()));
							form.addComponent(new SLabel("Date",CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							form.addComponent(new SLabel("Max Credit Period",sale.getCredit_period() + ""));
							if (isShippingChargeEnable())
								form.addComponent(new SLabel("Shipping Charge",sale.getShipping_charge() + ""));
							form.addComponent(new SLabel("Net Amount",sale.getAmount() + ""));
							form.addComponent(new SLabel("Paid Amount",sale.getPayment_amount() + ""));
							SGridLayout grid = new SGridLayout("Item Details");
							grid.setColumns(12);
							grid.setRows(sale
									.getTailoring_inventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "Item"), 1,0);
							grid.addComponent(new SLabel(null, "Qty"), 2, 0);
							grid.addComponent(new SLabel(null, "Unit"), 3,0);
							grid.addComponent(new SLabel(null, "Unit Price"), 4, 0);
							grid.addComponent(new SLabel(null, "Discount"),	5, 0);
							grid.addComponent(new SLabel(null, "Amount"),6, 0);
							grid.setSpacing(true);
							
							int i = 1;
							TailoringSalesInventoryDetailsModel invObj;
							Iterator itr = sale.getTailoring_inventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(TailoringSalesInventoryDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																	+ invObj.getTax_amount())+ ""), 6, i);
								i++;
							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
							form.setStyleName("grid_max_limit");
							popupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});

			officeComboField.setValue(getOfficeID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	protected List<Object> getSalesReportList() {
		long salesNo = 0;
		long custId = 0;

		if (salesNoComboField.getValue() != null
				&& !salesNoComboField.getValue().equals("")
				&& !salesNoComboField.getValue().toString().equals("0")) {
			salesNo = toLong(salesNoComboField.getValue().toString());
		}
		if (customerComboField.getValue() != null
				&& !customerComboField.getValue().equals("")) {
			custId = toLong(customerComboField.getValue().toString());
		}

		String condition1 = "";

		if ((Integer) filterTypeRadio.getValue() == 1) {
			condition1 = " and (payment_amount+paid_by_payment)>=amount ";
		} else if ((Integer) filterTypeRadio.getValue() == 2) {
			condition1 = " and (payment_amount+paid_by_payment)<amount ";
		}

		if ((Integer) statusRadioButton.getValue() == 0) {
			condition1 += " and active=true ";
		} else if ((Integer) statusRadioButton.getValue() == 1) {
			condition1 += " and active=false ";
		}

		List<Object> salesModelList = null;
		try {
			salesModelList = new TailoringSalesReportDao()
					.getSalesDetails(salesNo, custId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), toLong(officeComboField
									.getValue().toString()), condition1,
							getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
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

			if ((Integer) statusRadioButton.getValue() == 0) {
				condition1 += " and active=true ";
			} else if ((Integer) statusRadioButton.getValue() == 1) {
				condition1 += " and active=false ";
			}
			
			

			if (customerId != 0) {
				condition1 += " and customer.id="+customerId;
				
				salesList = salDao.getAllSalesNumbersByDate(officeId,
						CommonUtil
								.getSQLDateFromUtilDate(fromDateField
										.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), condition1);
			} else {
				salesList = salDao.getAllSalesNumbersByDate(officeId,
						CommonUtil.getSQLDateFromUtilDate(fromDateField
								.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDateField
								.getValue()), condition1);
			}

			TailoringSalesModel salesModel = new TailoringSalesModel();
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

			table.removeAllItems();
			table.setColumnFooter(TBC_AMOUNT, "0.0");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			if (officeId != 0) {
				custList = ledDao.getAllCustomers(officeId);
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
