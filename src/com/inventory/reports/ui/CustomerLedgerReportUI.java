package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.dao.CommissionSalesNewDao;
import com.inventory.commissionsales.model.CommissionSalesCustomerDetailsModel;
import com.inventory.commissionsales.model.CommissionSalesNewModel;
import com.inventory.commissionsales.ui.CustomerCommissionSalesUI;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.model.PaymentModel;
import com.inventory.payment.ui.CustomerPaymentsUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.CustomerLedgerReportDao;
import com.inventory.reports.dao.LedgerViewDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.sales.ui.SalesReturnNewUI;
import com.inventory.sales.ui.SalesUI;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.subscription.ui.SubscriptionPayment;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 11, 2013
 */
public class CustomerLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;
	private SButton generateConsolidatedButton;

	private Report report;

	private CustomerLedgerReportDao daoObj;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_INVOICE_NO = "Invoice No";
	static String TBC_TYPE = "type";
	static String TBC_DATE = "Date";
	static String TBC_SALE = "Sale";
	static String TBC_CASH = "Cash";
	static String TBC_RETURN = "Return";
	static String TBC_PERIOD_BAL = "Period Balance";
	static String TBC_BALANCE = "Balance";

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

			allColumns = new String[] { TBC_SN, TBC_ID,TBC_INVOICE_NO, TBC_TYPE, TBC_DATE,
					TBC_SALE, TBC_CASH, TBC_RETURN, TBC_PERIOD_BAL, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN,TBC_INVOICE_NO, TBC_DATE, TBC_SALE,
					TBC_CASH, TBC_RETURN, TBC_PERIOD_BAL, TBC_BALANCE };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1200, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_INVOICE_NO, String.class, null, getPropertyName("invoice_no"), null,
					Align.CENTER);
			table.addContainerProperty(TBC_TYPE, String.class, null, TBC_TYPE,
					null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_SALE, Double.class, null,
					getPropertyName("sale"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CASH, Double.class, null,
					getPropertyName("cash"), null, Align.RIGHT);
			table.addContainerProperty(TBC_RETURN, Double.class, null,
					getPropertyName("return"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PERIOD_BAL, Double.class, null,
					getPropertyName("period_balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_INVOICE_NO, (float) 0.6);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_SALE, (float) 0.7);
			table.setColumnExpandRatio(TBC_CASH, (float) 0.7);
			table.setColumnExpandRatio(TBC_RETURN, (float) 0.7);
			table.setColumnExpandRatio(TBC_PERIOD_BAL, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("730");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));
			calculateTableTotals();

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());
			ledgertSelect = new SComboField(getPropertyName("customer"), 200,
					ledDao.getAllCustomers((Long) officeSelect.getValue()),
					"id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("select"));

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

			daoObj = new CustomerLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(),getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			generateConsolidatedButton = new SButton(
					getPropertyName("consolidated_report"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(generateConsolidatedButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action(getPropertyName("edit"));

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());

						if (item.getItemProperty(TBC_TYPE).getValue()
								.equals("Sale")) {
							SalesUI purchase = new SalesUI();
							purchase.setCaption(getPropertyName("sales"));
							purchase.getSalesNumberList().setValue(
									(Long) item.getItemProperty(TBC_ID)
											.getValue());
							purchase.center();
							getUI().getCurrent().addWindow(purchase);
							purchase.addCloseListener(closeListener);

						} else if (item.getItemProperty(TBC_TYPE).getValue()
								.equals("Commission Sale")) {
							CustomerCommissionSalesUI commSales = new CustomerCommissionSalesUI();
							commSales.setCaption(getPropertyName("commission_sales"));
							commSales.getCommissionSaleNumberList().setValue(
									(Long) item.getItemProperty(TBC_ID)
											.getValue());
							commSales.center();
							getUI().getCurrent().addWindow(commSales);
							commSales.addCloseListener(closeListener);

						} else if (item.getItemProperty(TBC_TYPE).getValue()
								.equals("Receipt")) {
							CustomerPaymentsUI payment = new CustomerPaymentsUI();
							payment.setCaption(getPropertyName("customer_payment"));
							payment.getPaymentIdComboField().setValue(
									(Long) item.getItemProperty(TBC_ID)
											.getValue());
							payment.center();
							getUI().getCurrent().addWindow(payment);
							payment.addCloseListener(closeListener);

						} 
						else if (item.getItemProperty(TBC_TYPE).getValue()
								.equals("Subscription")) {
							SubscriptionPayment payment = new SubscriptionPayment();
							payment.setCaption(getPropertyName("rental"));
							SubscriptionPaymentModel mdl = null;
							try {
								mdl = new SubscriptionPaymentDao().getPaymentModel((Long) item.getItemProperty(TBC_ID).getValue());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							payment.loadAllCustomer(mdl);
							payment.center();
							getUI().getCurrent().addWindow(payment);
							payment.addCloseListener(closeListener);
						} 
						else {
							SalesReturnNewUI retrn = new SalesReturnNewUI();
							retrn.setCaption(getPropertyName("sales_return"));
							retrn.getSalesreturnNumberList().setValue(
									(Long) item.getItemProperty(TBC_ID)
											.getValue());
							retrn.center();
							getUI().getCurrent().addWindow(retrn);
							retrn.addCloseListener(closeListener);
						}

					}
				}

			});

			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID)
									.getValue();
							if (itm.getItemProperty(TBC_TYPE).getValue()
									.equals("Sale")) {

								SalesModel objModel = new SalesDao()
										.getSale(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>"+getPropertyName("sale")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("sales_no"),
										objModel.getSales_number() + ""));
//								form.addComponent(new SLabel(getPropertyName("ref_no"),
//										objModel.getVoucher_no() + ""));
								form.addComponent(new SLabel(getPropertyName("customer"),
										objModel.getCustomer().getName()));
								form.addComponent(new SLabel(getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
//								form.addComponent(new SLabel(
//										getPropertyName("max_cr_period"),
//										objModel.getCredit_period() + ""));

//								if (isShippingChargeEnable())
//									form.addComponent(new SLabel(
//											getPropertyName("shipping_charge"),
//											objModel.getShipping_charge() + ""));

								form.addComponent(new SLabel(
										getPropertyName("net_amount"), objModel
												.getAmount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("paid_amount"),
										objModel.getPayment_amount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,
										0);
								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,
										0);
								grid.addComponent(
										new SLabel(null, getPropertyName("rate")), 4, 0);
								grid.addComponent(new SLabel(null, getPropertyName("discount")),
										5, 0);
								grid.addComponent(new SLabel(null,getPropertyName("amount")),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								SalesInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getInventory_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (SalesInventoryDetailsModel) itmItr
											.next();

									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getItem().getName()), 1, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getQunatity() + ""), 2, i);
									grid.addComponent(new SLabel(null, invObj
											.getUnit().getSymbol()), 3, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getUnit_price() + ""), 4,
											i);
									grid.addComponent(
											new SLabel(null, invObj
													.getDiscount() + ""),
											5, i);
									grid.addComponent(
											new SLabel(
													null,
													(invObj.getUnit_price()
															* invObj.getQunatity()
															- invObj.getDiscount() + invObj
																.getTaxAmount())
															+ ""), 6, i);
									i++;
								}

								form.addComponent(grid);
								form.addComponent(new SLabel(
										getPropertyName("comment"), objModel
												.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_TYPE).getValue()
									.equals("Commission Sale")) {
								CommissionSalesNewModel objModel = new CommissionSalesNewDao()
										.getSale(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u"+getPropertyName("commission_sales")+"</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("sales_no"), objModel
												.getSales_number() + ""));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("amount"), objModel
												.getAmount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("customer_details"));
								grid.setColumns(12);
								grid.setRows(objModel.getCommission_sales_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, getPropertyName("customer")),
										1, 0);
								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,
										0);
								grid.addComponent(
										new SLabel(null, getPropertyName("rate")), 4, 0);
								grid.addComponent(new SLabel(null, getPropertyName("amount")),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								CommissionSalesCustomerDetailsModel invObj;
								Iterator itmItr = objModel.getCommission_sales_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (CommissionSalesCustomerDetailsModel) itmItr
											.next();

									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getCustomer().getName()), 1, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getQunatity() + ""), 2, i);
									grid.addComponent(new SLabel(null, invObj
											.getUnit().getSymbol()), 3, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getUnit_price() + ""), 4,
											i);
									grid.addComponent(
											new SLabel(
													null,
													(invObj.getUnit_price()
															* invObj.getQunatity()
															- invObj.getDiscount_amount() + invObj
																.getTax_amount())
															+ ""), 6, i);
									i++;
								}

								form.addComponent(grid);
								form.addComponent(new SLabel(
										getPropertyName("comment"), objModel
												.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_TYPE).getValue()
									.equals("Receipt")) {

								PaymentModel objModel = new PaymentDao()
										.getPaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>"+getPropertyName("customer_receipt")+"</u></h2>"));
								form.addComponent(new SLabel(getPropertyName("receipt_no"),
										objModel.getPayment_id() + ""));
								LedgerModel cust = ledDao.getLedgeer(objModel
										.getFrom_account_id());
								if (cust != null)
									form.addComponent(new SLabel(
											getPropertyName("customer"), cust
													.getName()));

								LedgerModel toAcc = ledDao.getLedgeer(objModel
										.getTo_account_id());
								if (toAcc != null)
									form.addComponent(new SLabel(
											getPropertyName("to_account"),
											toAcc.getName()));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("customer_amount"),
										objModel.getSupplier_amount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("discount"), objModel
												.getDiscount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("payment_amount"),
										objModel.getPayment_amount() + ""));

								form.addComponent(new SLabel(
										getPropertyName("description"),
										objModel.getDescription()));

								form.setWidth("400");

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							}
							else if (itm.getItemProperty(TBC_TYPE).getValue()
									.equals("Subscription")) {

								SubscriptionPaymentModel objModel = new SubscriptionPaymentDao().getPaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>"+getPropertyName("rental")+"</u></h2>"));
//								form.addComponent(new SLabel("Rental Paymn No. :",objModel.getPayment_id() + ""));
								LedgerModel cust = ledDao.getLedgeer(objModel.getFrom_account());
								if (cust != null)
									form.addComponent(new SLabel(getPropertyName("customer"), cust.getName()));

								LedgerModel toAcc = ledDao.getLedgeer(objModel.getTo_account());
								
								if (toAcc != null)
									form.addComponent(new SLabel(getPropertyName("to_account"),toAcc.getName()));

								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getPayment_date())));

//								form.addComponent(new SLabel(getPropertyName("customer_amount"),objModel.getAmount_paid() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("discount"), objModel
//												.getDiscount() + ""));
								form.addComponent(new SLabel(getPropertyName("payment_amount"),objModel.getAmount_paid() + ""));

//								form.addComponent(new SLabel(getPropertyName("description"),objModel.getDescription()));

								form.setWidth("400");

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							}
							else {
								SalesReturnModel objModel = new SalesReturnDao()
										.getSalesReturnModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>"+getPropertyName("sales_return")+"</u></h2>"));
//								form.addComponent(new SLabel(
//										getPropertyName("credit_note_no"), objModel
//												.getCredit_note_no() + ""));
								form.addComponent(new SLabel(
										getPropertyName("customer"), objModel
												.getCustomer().getName()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("net_amount"), objModel
												.getAmount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("paid_amount"),
//										objModel.getPayment_amount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,
										0);
								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,
										0);
								grid.addComponent(
										new SLabel(null, getPropertyName("stock_quantity")), 4, 0);
								grid.addComponent(new SLabel(null,
										getPropertyName("purchase_return_qty")), 5, 0);
								grid.addComponent(
										new SLabel(null, getPropertyName("waste_qty")), 6, 0);
								grid.addComponent(
										new SLabel(null, getPropertyName("rate")), 7, 0);
								grid.addComponent(new SLabel(null, getPropertyName("discount")),
										8, 0);
								grid.addComponent(new SLabel(null, getPropertyName("amount")),
										9, 0);
								grid.setSpacing(true);

								int i = 1;
								SalesReturnInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getInventory_details_list().iterator();
//								while (itmItr.hasNext()) {
//									invObj = (SalesReturnInventoryDetailsModel) itmItr
//											.next();
//									grid.addComponent(new SLabel(null, i + ""),
//											0, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getItem().getName()), 1, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getStock_quantity() + ""),
//											2, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getUnit().getSymbol()), 3, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getStock_quantity() + ""),
//											4, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getReturned_quantity()
//													+ ""), 5, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getWaste_quantity() + ""),
//											6, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getUnit_price() + ""), 7,
//											i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getDiscount_amount() + ""),
//											8, i);
//									grid.addComponent(
//											new SLabel(
//													null,
//													(invObj.getUnit_price()
//															* (invObj
//																	.getStock_quantity()
//																	+ invObj.getReturned_quantity() + invObj
//																		.getWaste_quantity())
//															- invObj.getDiscount_amount() + invObj
//																.getTax_amount())
//															+ ""), 9, i);
//									i++;
//								}

								form.addComponent(grid);
								form.addComponent(new SLabel(getPropertyName("comment"),
										objModel.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

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

			generateConsolidatedButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						generateConsolidatedReportReport();
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
						bic = SCollectionContainer.setList(
								ledDao.getAllCustomers((Long) officeSelect
										.getValue()), "id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ledgertSelect.setContainerDataSource(bic);
					ledgertSelect.setItemCaptionPropertyId("name");

				}
			});

			ledgertSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					table.removeAllItems();
					calculateTableTotals();
				}
			});

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();
			calculateTableTotals();

			LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List lst = daoObj.getCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long)officeSelect.getValue(), (Long) ledgertSelect.getValue());

				double opening_bal = daoObj.getSalesOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());


				if (lst != null && lst.size() > 0) {
					Collections.sort(lst, new Comparator<AcctReportMainBean>() {
						@Override
						public int compare(final AcctReportMainBean object1,
								final AcctReportMainBean object2) {
							return object1.getDate().compareTo(
									object2.getDate());
						}
					});

					table.setVisibleColumns(allColumns);

					int ct = 0;
					double bal = opening_bal, periodBal = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					String invNo="";
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();
						invNo="";
						if(obj.getNumber()!=0)
							invNo=asString(obj.getNumber());

						if (obj.getParticulars().equals("Sal Return")) {
							bal -= (obj.getAmount() + obj.getPayed());
							// obj.setBalance(bal);
							periodBal -= (obj.getAmount() + obj.getPayed());
						} else {
							bal += obj.getAmount() - obj.getPayed();
							// obj.setBalance(bal);
							periodBal += obj.getAmount() - obj.getPayed();
						}

						if (obj.getParticulars().equals("Sal Return")) {
							if (obj.getPayed() == 0)
								obj.setPayed(0);
							table.addItem(new Object[] { ct + 1, obj.getId(),invNo,
									obj.getParticulars(), obj.getDate(), 0.0,
									obj.getPayed(), obj.getAmount(), periodBal,
									bal }, ct);
						} else if (obj.getParticulars().equals("Receipt")) {
							table.addItem(new Object[] { ct + 1, obj.getId(),invNo,
									obj.getParticulars(), obj.getDate(), 0.0,
									obj.getPayed(), 0.0, periodBal, bal }, ct);
						} else {
							table.addItem(
									new Object[] { ct + 1, obj.getId(),invNo,
											obj.getParticulars(),
											obj.getDate(), obj.getAmount(),
											obj.getPayed(), 0.0, periodBal, bal },
									ct);
						}

						ct++;

					}

					table.setVisibleColumns(visibleColumns);

					calculateTableTotals();

					lst.clear();
				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void generateReport() {
		try {

			LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List lst = daoObj.getCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long)officeSelect.getValue(), (Long) ledgertSelect.getValue());

				List reportList = new ArrayList();

				double opening_bal = daoObj.getSalesOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				double current_bal = daoObj.getSalesCurrentBalance(
						CommonUtil.getCurrentSQLDate(),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());

				if (lst.size() > 0) {
					Collections.sort(lst, new Comparator<AcctReportMainBean>() {
						@Override
						public int compare(final AcctReportMainBean object1,
								final AcctReportMainBean object2) {
							return object1.getDate().compareTo(
									object2.getDate());
						}
					});
				}

				double ledger_bal = new LedgerViewDao().getLedgerBalance(
						getWorkingDate(), (Long) ledgertSelect.getValue());

				double bal = opening_bal, periodBal = 0;
				AcctReportMainBean obj;
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					obj = (AcctReportMainBean) it.next();

					if (obj.getParticulars().equals("Sal Return")) {
						bal -= obj.getAmount();
						periodBal -= obj.getAmount();
						if (obj.getPayed() == -0) {
							obj.setPayed(0);
						}
						periodBal -= obj.getPayed();
						bal -= obj.getPayed();
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);
					} else {
						periodBal += obj.getAmount() - obj.getPayed();
						bal += obj.getAmount() - obj.getPayed();
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);
					}
					reportList.add(obj);
				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
					params.put("LedgerName", ledger.getName());

					params.put("Balance", roundNumber(current_bal));
					params.put("OpeningBalance", roundNumber(opening_bal));
					params.put("Office", ledger.getOffice().getName());
					params.put("Organization", ledger.getOffice().getOrganization().getName());

					params.put("name_label", getPropertyName("name"));
					params.put("office_label", getPropertyName("office"));
					params.put("opening_balance_label", getPropertyName("opening_balance"));
					params.put("organization_label", getPropertyName("organization"));
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("customer_ledger_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("INVOICE_NO_LABEL", getPropertyName("invoice_no"));
					params.put("SALES_LABEL", getPropertyName("sales"));
					params.put("EXPENSE_COMMISSION_LABEL", getPropertyName("expense_commission"));
					params.put("CASH_LABEL", getPropertyName("cash"));	
					params.put("RETURN_LABEL", getPropertyName("return"));
					params.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
					params.put("BALANCE_PAY_LABEL", getPropertyName("balance_pay"));
					params.put("TOTAL_LABEL", getPropertyName("total"));
					
					params.put(
							"bal_to_pay_label",
							getPropertyName("balance_pay_on")
									+ CommonUtil
											.formatDateToDDMMMYYYY(getWorkingDate()));
					params.put(
							"ledg_bal_lab",
							getPropertyName("ledger_balance_on")
									+ CommonUtil
											.formatDateToDDMMMYYYY(getWorkingDate()));
					params.put("ledger_balance", ledger_bal);

					report.setJrxmlFileName("CustomerLedgerReport");
					report.setReportFileName("Customer Ledger Report");
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
					lst.clear();

				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void generateConsolidatedReportReport() {
		try {

			LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List lst = daoObj.getCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long)officeSelect.getValue(), (Long) ledgertSelect.getValue());

				List reportList = new ArrayList();

				double opening_bal = daoObj.getSalesOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());

				double ledger_bal = new LedgerViewDao().getLedgerBalance(
						getWorkingDate(), (Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				double current_bal = daoObj.getSalesCurrentBalance(
						CommonUtil.getCurrentSQLDate(),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());

				if (lst.size() > 0) {
					Collections.sort(lst, new Comparator<AcctReportMainBean>() {
						@Override
						public int compare(final AcctReportMainBean object1,
								final AcctReportMainBean object2) {
							return object1.getDate().compareTo(
									object2.getDate());
						}
					});
				}

				double bal = opening_bal, periodBal = 0;
				AcctReportMainBean obj;
				Iterator it = lst.iterator();
				while (it.hasNext()) {
					obj = (AcctReportMainBean) it.next();

					if (obj.getParticulars().equals("Sal Return")) {
						bal -= obj.getAmount();
						periodBal -= obj.getAmount();
						if (obj.getPayed() == -0) {
							obj.setPayed(0);
						}
						periodBal -= obj.getPayed();
						bal -= obj.getPayed();
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);
					} else {
						periodBal += obj.getAmount() - obj.getPayed();
						bal += obj.getAmount() - obj.getPayed();
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);
					}
					reportList.add(obj);
				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
					params.put("LedgerName", ledger.getName());

					params.put("Balance", roundNumber(current_bal));
					params.put("OpeningBalance", roundNumber(opening_bal));
					params.put("Office", ledger.getOffice().getName());
//					params.put("Address", new AddressDao()
//							.getAddressString(ledger.getAddress().getId()));

					
					params.put("name_label", getPropertyName("name"));
					params.put("office_label", getPropertyName("office"));
					params.put("opening_balance_label", getPropertyName("opening_balance"));
					params.put("address_label", getPropertyName("address"));
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("customer_ledger_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("INVOICE_NO_LABEL", getPropertyName("invoice_no"));
					params.put("DESCRIPTION_LABEL", getPropertyName("description"));
					params.put("DEBIT_LABEL", getPropertyName("debit"));
					params.put("CREDIT_LABEL", getPropertyName("credit"));
					params.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
					params.put("BALANCE_PAY_LABEL", getPropertyName("balance_pay"));
					params.put("SALES_LABEL", getPropertyName("sales"));
					params.put("SALES_RETURN_LABEL", getPropertyName("sales_return"));
					params.put("PAYMENT_LABEL", getPropertyName("payment"));
					
					params.put(
							"bal_to_pay_label",
							getPropertyName("balance_pay_on")
									+ CommonUtil
											.formatDateToDDMMMYYYY(getWorkingDate()));
					params.put(
							"ledg_bal_lab",
							getPropertyName("ledger_balance_on")
									+ CommonUtil
											.formatDateToDDMMMYYYY(getWorkingDate()));
					params.put("ledger_balance", ledger_bal);

					report.setJrxmlFileName("CustomerLedgerReportNew");
					report.setReportFileName("CustomerLedgerReportNew");
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
					lst.clear();

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

	public void calculateTableTotals() {
		Iterator it = table.getItemIds().iterator();
		Item itm;

		double sal_ttl = 0, cash_ttl = 0, ret_ttl = 0, per_bal = 0, bal = 0;
		while (it.hasNext()) {
			itm = table.getItem(it.next());
			sal_ttl += (Double) itm.getItemProperty(TBC_SALE).getValue();
			cash_ttl += (Double) itm.getItemProperty(TBC_CASH).getValue();
			ret_ttl += (Double) itm.getItemProperty(TBC_RETURN).getValue();

			per_bal = (Double) itm.getItemProperty(TBC_PERIOD_BAL).getValue();
			bal = (Double) itm.getItemProperty(TBC_BALANCE).getValue();
		}
		table.setColumnFooter(TBC_SALE, asString(roundNumber(sal_ttl)));
		table.setColumnFooter(TBC_CASH, asString(roundNumber(cash_ttl)));
		table.setColumnFooter(TBC_RETURN, asString(roundNumber(ret_ttl)));
		table.setColumnFooter(TBC_PERIOD_BAL, asString(roundNumber(per_bal)));
		table.setColumnFooter(TBC_BALANCE, asString(roundNumber(bal)));

	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
