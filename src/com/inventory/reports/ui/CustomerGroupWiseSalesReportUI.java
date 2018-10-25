package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.CustomerGroupDao;
import com.inventory.config.acct.model.CustomerGroupModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.SalesReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.ui.SalesNewUI;
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
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 10, 2014
 */

@SuppressWarnings("serial")
public class CustomerGroupWiseSalesReportUI extends SparkLogic {

	
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerGroupComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton showButton;

	private SCollectionContainer container;

	private long customerId;

	private UserManagementDao userDao;
	SHorizontalLayout popupContainer;
	CustomerGroupDao ledDao;
	CustomerDao custDao;

	SRadioButton filterTypeRadio;
	SRadioButton statusRadioButton;

	private Report report;

	SalesDao salDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_SALES_MAN = "Sales Man";
	static String TBC_SALE_NO = "Sale No";
	static String TBC_DATE = "Date";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_AMOUNT = "Amount";
	static String TBC_PAYMENT = "Payment Amount";
	static String TBC_PENDING = "Pending Amount";
	static String TBC_ITEMS = "Items";
	static String TBC_GROUP = "Group";

	private STable table;

	private String[] allColumns;
	private String[] visibleColumns;

	private WrappedSession session;
	private SettingsValuePojo sett;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		allColumns = new String[] { TBC_SN, TBC_ID,TBC_GROUP,TBC_CUSTOMER, TBC_SALE_NO, TBC_DATE,TBC_SALES_MAN,
				 TBC_ITEMS ,TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };
		visibleColumns = new String[] { TBC_SN, TBC_GROUP,TBC_CUSTOMER, TBC_SALE_NO, TBC_DATE,TBC_SALES_MAN,
				 TBC_ITEMS ,TBC_AMOUNT, TBC_PAYMENT, TBC_PENDING };

		ledDao = new CustomerGroupDao();
		salDao = new SalesDao();

		customerId = 0;
		report = new Report(getLoginID());
		userDao = new UserManagementDao();
		custDao=new CustomerDao();
		
		setSize(1200, 420);
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

			if (sett.isKEEP_DELETED_DATA())
				mainFormLayout.addComponent(statusRadioButton);
			

			customerGroupComboField = new SComboField(getPropertyName("group"),
					200);
			loadCustomerGroups(getOfficeID());
			mainFormLayout.addComponent(customerGroupComboField);


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

			table = new STable(null, 800, 275);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_SALES_MAN, String.class, null,getPropertyName("sales_man"), null, Align.CENTER);
			table.addContainerProperty(TBC_GROUP, String.class, null,getPropertyName("group"), null, Align.CENTER);
			table.addContainerProperty(TBC_SALE_NO, String.class, null,getPropertyName("invoice_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_ITEMS, String.class, null,getPropertyName("items"), null, Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("invoice_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PAYMENT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PENDING, Double.class, null,getPropertyName("pending_amount"), null, Align.RIGHT);
			

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.9);
			table.setColumnExpandRatio(TBC_SALES_MAN, 1f);
			table.setColumnExpandRatio(TBC_SALE_NO, 0.8f);
			table.setColumnExpandRatio(TBC_AMOUNT, 0.8f);
			table.setColumnExpandRatio(TBC_PAYMENT, 0.8f);
			table.setColumnExpandRatio(TBC_CUSTOMER, 2f);
			table.setColumnExpandRatio(TBC_ITEMS, 2f);
			table.setColumnExpandRatio(TBC_GROUP, 2f);
			

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));

			mainLay.addComponent(mainFormLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainPanel.setContent(mainLay);

			

			customerGroupComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							if (customerGroupComboField.getValue() != null
									&& !customerGroupComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerGroupComboField
										.getValue().toString());
							}
//							loadBillNo(customerId, toLong(officeComboField
//									.getValue().toString()));
						}
					});

//			filterTypeRadio.addListener(new Listener() {
//				@Override
//				public void componentEvent(Event event) {
//					loadBillNo(customerId, toLong(officeComboField.getValue()
//							.toString()));
//				}
//			});
//
//			statusRadioButton.addListener(new Listener() {
//				@Override
//				public void componentEvent(Event event) {
//					loadBillNo(customerId, toLong(officeComboField.getValue()
//							.toString()));
//				}
//			});
//
//			fromDateField.addListener(new Listener() {
//				@Override
//				public void componentEvent(Event event) {
//					loadBillNo(customerId, toLong(officeComboField.getValue()
//							.toString()));
//				}
//			});
//
//			toDateField.addListener(new Listener() {
//				@Override
//				public void componentEvent(Event event) {
//					loadBillNo(customerId, toLong(officeComboField.getValue()
//							.toString()));
//				}
//			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerGroups(toLong(officeComboField.getValue()
							.toString()));
//					loadBillNo(customerId, toLong(officeComboField.getValue()
//							.toString()));
					
				}
			});

			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";
						String salesPerson = "";

						List<Object> reportList = new ArrayList<Object>();
						List<Object> salesModelList = getSalesReportList();

						List<SalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < salesModelList.size(); i++) {
							noData = false;
							salesPerson = "";
							salesModel = (SalesModel) salesModelList.get(i);
							detailsList = salesModel
									.getInventory_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( "+getPropertyName("quantity")+" : "
										+ inventoryDetailsModel.getQunatity()
										+ " , "+getPropertyName("rate")+" : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}

							salesPerson = userDao.getUserFromLogin(
									salesModel.getResponsible_employee())
									.getFirst_name();

							reportBean = new SalesReportBean(CommonUtil.getUtilDateFromSQLDate(salesModel.getDate()).toString(),
															salesModel.getCustomer().getName(), 
															String.valueOf(salesModel.getSales_number()),
															salesModel.getOffice().getName(), 
															items,
															roundNumber(salesModel.getAmount()), 
															salesPerson,
															roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()),
															roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()))
															,custDao.getCustomerGroupNameFromLedger(salesModel.getCustomer().getId()));
							reportList.add(reportBean);

						}

						if (!noData) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("CustomerGroupWiseSalesReport");
							report.setReportFileName("CustomerGroupWiseSalesReport");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("customer_group_wise_sales_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("SALES_MAN_LABEL", getPropertyName("sales_man"));
							map.put("INVOICE_NO_LABEL", getPropertyName("invoice_no"));
							map.put("CUSTOMER_LABEL", getPropertyName("customer"));
							map.put("CUSTOMER_GROUP_LABEL", getPropertyName("group"));
							map.put("PAID_AMOUNT_LABEL", getPropertyName("amount_paid"));
							map.put("PENDING_AMOUNT_LABEL", getPropertyName("pending_amount"));
							map.put("INVOICE_AMOUNT_LABEL", getPropertyName("invoice_amount"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							
							String subHeader = "";
							if (customerId != 0) {
								subHeader += getPropertyName("group")+" : "
										+ customerGroupComboField
												.getItemCaption(customerGroupComboField
														.getValue()) + "\t";
							}

							subHeader += " \t "+getPropertyName("from")+" : "
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
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);

							reportList.clear();
							salesModelList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					SalesModel salesModel = null;
					SalesInventoryDetailsModel inventoryDetailsModel = null;
					String items = "";
					String group = "";
					Object[] row;

					try{
					List repList = getSalesReportList();

					table.setVisibleColumns(allColumns);
					table.removeAllItems();
					table.setColumnFooter(TBC_AMOUNT, "0.0");
					double ttl = 0;
					if (repList != null && repList.size() > 0) {
						List<SalesInventoryDetailsModel> detailsList;
						for (int i = 0; i < repList.size(); i++) {
							salesModel = (SalesModel) repList.get(i);
							detailsList = salesModel
									.getInventory_details_list();
							items = "";
							for (int k = 0; k < detailsList.size(); k++) {
								inventoryDetailsModel = detailsList.get(k);
								if (k != 0) {
									items += ", ";
								}
								items += inventoryDetailsModel.getItem()
										.getName()
										+ " ( "+getPropertyName("quantity")+" : "
										+ inventoryDetailsModel.getQunatity()
										+ " , "+getPropertyName("rate")+" : "
										+ inventoryDetailsModel.getUnit_price()
										+ " ) ";
							}
							
							group=custDao.getCustomerGroupNameFromLedger(salesModel.getCustomer().getId());

							try {
								row = new Object[] {
										i + 1,
										salesModel.getId(),group,salesModel.getCustomer().getName(),salesModel.getSales_number() + "",
										salesModel.getDate().toString(),userDao.getUserFromLogin(salesModel.getResponsible_employee()).getFirst_name(),									
										items,	roundNumber(salesModel.getAmount()),
										roundNumber(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()),
										roundNumber(salesModel.getAmount()-(salesModel.getPayment_amount()+salesModel.getPaid_by_payment()))};
								
								table.addItem(row, i + 1);
								ttl += salesModel.getAmount();
							} catch (Exception e) {
							}

						}
						table.setColumnFooter(TBC_AMOUNT,
								asString(roundNumber(ttl)));

					} else
						SNotification.show(getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);

					table.setVisibleColumns(visibleColumns);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action(getPropertyName("edit"));
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SalesNewUI sales = new SalesNewUI();
							sales.setCaption(getPropertyName("sales"));
							sales.getSalesNumberList().setValue(
									(Long) item.getItemProperty(TBC_ID).getValue());
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
							SalesModel sale=new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("sales")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("sales_no"),sale.getSales_number()+""));
							form.addComponent(new SLabel(getPropertyName("customer"),sale.getCustomer().getName()));
							form.addComponent(new SLabel(getPropertyName("group"),custDao.getCustomerGroupNameFromLedger(sale.getCustomer().getId())));
							form.addComponent(new SLabel(getPropertyName("sales_man"),userDao.getUserFromLogin(sale.getResponsible_employee()).getFirst_name()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
//							form.addComponent(new SLabel(getPropertyName("max_credit_period"),sale.getCredit_period() + ""));
//							if (isShippingChargeEnable())
//								form.addComponent(new SLabel(getPropertyName("shipping_charge"),sale.getShipping_charge() + ""));
							form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + ""));
							form.addComponent(new SLabel(getPropertyName("amount_paid"),sale.getPayment_amount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("unit_price")), 4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("discount")),	5, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),6, 0);
							grid.setSpacing(true);
							
							int i = 1;
							SalesInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(SalesInventoryDetailsModel)itr.next();
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
								grid.addComponent(new SLabel(null, invObj.getDiscount() + ""),5, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
																	- invObj.getDiscount() 
																	+ invObj.getTaxAmount())+ ""), 6, i);
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
		long custId = 0;

		if (customerGroupComboField.getValue() != null
				&& !customerGroupComboField.getValue().equals("")) {
			custId = toLong(customerGroupComboField.getValue().toString());
		}

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

		List<Object> salesModelList = null;
		try {
			salesModelList = new SalesReportDao()
					.getCustomerGroupWiseSalesDetails(custId, getOrganizationID(),toLong(officeComboField.getValue().toString())
							, CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()), condition1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesModelList;
	}

//	private void loadBillNo(long customerId, long officeId) {
//		List<Object> salesList = null;
//		try {
//
//			String condition1 = "";
//
//			if ((Integer) filterTypeRadio.getValue() == 1) {
//				condition1 = " and payment_amount=amount ";
//			} else if ((Integer) filterTypeRadio.getValue() == 2) {
//				condition1 = " and payment_amount<amount ";
//			}
//
//			if ((Integer) statusRadioButton.getValue() == 0) {
//				condition1 += " and active=true ";
//			} else if ((Integer) statusRadioButton.getValue() == 1) {
//				condition1 += " and active=false ";
//			}
//			
//
//			if (customerId != 0) {
//				salesList = salDao.getAllSalesNumbersForCustomer(officeId,
//						customerId, CommonUtil
//								.getSQLDateFromUtilDate(fromDateField
//										.getValue()),
//						CommonUtil.getSQLDateFromUtilDate(toDateField
//								.getValue()), condition1);
//			} else {
//				salesList = salDao.getAllSalesNumbersByDate(officeId,
//						CommonUtil.getSQLDateFromUtilDate(fromDateField
//								.getValue()),
//						CommonUtil.getSQLDateFromUtilDate(toDateField
//								.getValue()), condition1);
//			}
//
//			SalesModel salesModel = new SalesModel();
//			salesModel.setId(0);
//			salesModel
//					.setComments(getPropertyName("all"));
//			if (salesList == null) {
//				salesList = new ArrayList<Object>();
//			}
//			salesList.add(0, salesModel);
//			container = SCollectionContainer.setList(salesList, "id");
//
//			table.removeAllItems();
//			table.setColumnFooter(TBC_AMOUNT, "0.0");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private void loadCustomerGroups(long officeID) {
		List<Object> customerList;
		try {
			customerList = ledDao.getAllCustomerGroups(officeID);

			CustomerGroupModel ledgerModel = new CustomerGroupModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (customerList == null) {
				customerList = new ArrayList<Object>();
			}
			customerList.add(0, ledgerModel);
			
			customerGroupComboField.setContainerDataSource(SCollectionContainer.setList(customerList, "id"));
			customerGroupComboField.setItemCaptionPropertyId("name");
			customerGroupComboField.setValue((long)0);
			
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
