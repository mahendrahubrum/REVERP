package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.payment.ui.SupplierPaymentsUI;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.LedgerViewDao;
import com.inventory.reports.dao.SupplierLedgerReportDao;
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
import com.webspark.Components.SCheckBox;
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
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Anil K P.
 * 
 *         Dec 10, 2013
 */
public class SupplierLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton generateConsolidatedButton;
	private SButton showButton;

	private Report report;

	SCheckBox usePaymentEndDate;

	private SupplierLedgerReportDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_TYPE = "type";
	static String TBC_DATE = "Date";
	static String TBC_SALE = "Purchase";
	static String TBC_EXPENSE = "Expense";
	static String TBC_CASH = "Cash";
	static String TBC_RETURN = "Return";
	static String TBC_PERIOD_BAL = "Period Balance";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		ofcDao = new OfficeDao();
		ledDao = new LedgerDao();

		try {

			usePaymentEndDate = new SCheckBox(
					getPropertyName("use_date_payment"));

			allColumns = new String[] { TBC_SN, TBC_ID, TBC_TYPE, TBC_DATE,
					TBC_SALE, TBC_EXPENSE, TBC_CASH, TBC_RETURN,
					TBC_PERIOD_BAL, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_DATE, TBC_SALE,
					TBC_EXPENSE, TBC_CASH, TBC_RETURN, TBC_PERIOD_BAL,
					TBC_BALANCE };

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
			table.addContainerProperty(TBC_TYPE, String.class, null,
					getPropertyName("type"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_SALE, Double.class, null,
					getPropertyName("purchase"), null, Align.RIGHT);
			table.addContainerProperty(TBC_EXPENSE, Double.class, null,
					getPropertyName("expenses"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CASH, Double.class, null,
					getPropertyName("cash"), null, Align.RIGHT);
			table.addContainerProperty(TBC_RETURN, Double.class, null,
					getPropertyName("return"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PERIOD_BAL, Double.class, null,
					getPropertyName("period_balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_SALE, 1);
			table.setColumnExpandRatio(TBC_EXPENSE, 1);
			table.setColumnExpandRatio(TBC_CASH, 1);
			table.setColumnExpandRatio(TBC_RETURN, 1);
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
			ledgertSelect = new SComboField(getPropertyName("supplier"), 200,
					ledDao.getAllSuppliers((Long) officeSelect.getValue()),
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

			daoObj = new SupplierLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(usePaymentEndDate);
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
								.equals("Purchase")) {
							PurchaseUI purchase = new PurchaseUI();
							purchase.setCaption(getPropertyName("purchase"));
							purchase.getPurchaseNumberList().setValue(
									(Long) item.getItemProperty(TBC_ID)
											.getValue());
							purchase.center();
							getUI().getCurrent().addWindow(purchase);
							purchase.addCloseListener(closeListener);

						} else if (item.getItemProperty(TBC_TYPE).getValue()
								.equals("Commission Sale")) {
//							CommissionPayment commSales = new CommissionPayment();
//							commSales.setCaption(getPropertyName("commission_sales"));
//							commSales.getCommissionSalesCombo().setValue(
//									(Long) item.getItemProperty(TBC_ID)
//											.getValue());
//							commSales.center();
//							getUI().getCurrent().addWindow(commSales);
//							commSales.addCloseListener(closeListener);

						} else if (item.getItemProperty(TBC_TYPE).getValue()
								.equals("Payment")) {
							SupplierPaymentsUI payment = new SupplierPaymentsUI();
							payment.setCaption(getPropertyName("supplier_payment"));
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
							payment.loadAllSupplier(mdl);
							payment.center();
							getUI().getCurrent().addWindow(payment);
							payment.addCloseListener(closeListener);
						} 
						else {
							PurchaseReturnUI retrn = new PurchaseReturnUI();
							retrn.setCaption(getPropertyName("purchase_return"));
//							retrn.getDebitNoteNoComboField().setValue(
//									(Long) item.getItemProperty(TBC_ID)
//											.getValue());
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

//						if (table.getValue() != null) {
//							Item itm = table.getItem(table.getValue());
//							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
//							if (itm.getItemProperty(TBC_TYPE).getValue()
//									.equals("Purchase")) {
//
//								PurchaseModel objModel = new PurchaseDao()
//										.getPurchase(id);
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,
//										"<h2><u>"+getPropertyName("purchase")+"</u></h2>"));
//								form.addComponent(new SLabel(
//										getPropertyName("purchase_no"),
//										objModel.getPurchase_number() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("ref_no"),
//										objModel.getPurchase_bill_number() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("supplier"), objModel
//												.getSupplier().getName()));
//								form.addComponent(new SLabel(
//										getPropertyName("date"),
//										CommonUtil
//												.getUtilDateFromSQLDate(objModel
//														.getDate())));
//								form.addComponent(new SLabel(
//										getPropertyName("max_credit_period"),
//										objModel.getCredit_period() + ""));
//
//								if (isShippingChargeEnable())
//									form.addComponent(new SLabel(
//											getPropertyName("shipping_charge"),
//											objModel.getShipping_charge() + ""));
//
//								form.addComponent(new SLabel(
//										getPropertyName("net_amount"), objModel
//												.getAmount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("paid_amount"),
//										objModel.getPayment_amount() + ""));
//
//								SGridLayout grid = new SGridLayout(
//										getPropertyName("item_details"));
//								grid.setColumns(12);
//								grid.setRows(objModel
//										.getInventory_details_list().size() + 3);
//
//								grid.addComponent(new SLabel(null, "#"), 0, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,
//										0);
//								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,
//										0);
//								grid.addComponent(
//										new SLabel(null, getPropertyName("unit_price")), 4, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("discount")),
//										5, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("amount")),
//										6, 0);
//								grid.setSpacing(true);
//								int i = 1;
//								PurchaseInventoryDetailsModel invObj;
//								Iterator itmItr = objModel
//										.getInventory_details_list().iterator();
//								while (itmItr.hasNext()) {
//									invObj = (PurchaseInventoryDetailsModel) itmItr
//											.next();
//
//									grid.addComponent(new SLabel(null, i + ""),
//											0, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getItem().getName()), 1, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getQunatity() + ""), 2, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getUnit().getSymbol()), 3, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getUnit_price() + ""), 4,
//											i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getDiscount_amount() + ""),
//											5, i);
//									grid.addComponent(
//											new SLabel(
//													null,
//													(invObj.getUnit_price()
//															* invObj.getQunatity()
//															- invObj.getDiscount_amount() + invObj
//																.getTax_amount())
//															+ ""), 6, i);
//									i++;
//								}
//
//								form.addComponent(grid);
//								form.addComponent(new SLabel(
//										getPropertyName("comment"), objModel
//												.getComments()));
//
//								form.setStyleName("grid_max_limit");
//
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} else if (itm.getItemProperty(TBC_TYPE).getValue()
//									.equals("Commission Sale")) {
//								CommissionPaymentModel objModel = new CommissionPaymentDao()
//										.getPaymentModel(id);
//
//								CommissionPurchaseModel pmdl=new CommissionPurchaseDao().getPurchase(objModel.getPurchaseId());
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,
//										"<h2><u>"+getPropertyName("commission_sales")+"</u></h2>"));
//								form.addComponent(new SLabel(
//										getPropertyName("payment_no"),
//										objModel.getNumber() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("purchase_no"),
//										pmdl.getNumber() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("date"),
//										CommonUtil
//												.getUtilDateFromSQLDate(objModel
//														.getDate())));
//								form.addComponent(new SLabel(
//										getPropertyName("supplier"), pmdl
//												.getSupplier().getName()));
//								form.addComponent(new SLabel(
//										getPropertyName("gross_sale"), ""
//												+ objModel.getGross_sale()));
//								form.addComponent(new SLabel(
//										getPropertyName("less_expenses"),
//										objModel.getLess_expense() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("airport_charges"),
//										objModel.getAirport_charges() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("auction"), objModel
//												.getAuction() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("DPA_charges"),
//										objModel.getDpa_charges() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("freight"), objModel
//												.getFreight() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("pickup_charge"),
//										objModel.getPickup_charge() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("port"), objModel
//												.getPort() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("storage_charge"),
//										objModel.getStorage_charge() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("unloading_charge"),
//										objModel.getUnloading_charge() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("waste"), objModel
//												.getWaste() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("commission"), objModel
//												.getCommission() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("supplier_amount"),
//										(objModel.getNet_sale() - objModel
//												.getCommission()) + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("details"), objModel
//												.getDetails()));
//
//								form.setWidth("400");
//
//								form.setStyleName("grid_max_limit");
//
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//							} else if (itm.getItemProperty(TBC_TYPE).getValue()
//									.equals("Payment")) {
//
//								PaymentModel objModel = new PaymentDao()
//										.getPaymentModel(id);
//
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,
//										"<h2><u>"+getPropertyName("supplier_payment")+"</u></h2>"));
//								form.addComponent(new SLabel(
//										getPropertyName("payment_no"), objModel
//												.getPayment_id() + ""));
//								LedgerModel supl = ledDao.getLedgeer(objModel
//										.getTo_account_id());
//								if (supl != null)
//									form.addComponent(new SLabel(
//											getPropertyName("supplier"), supl
//													.getName()));
//
//								LedgerModel frm = ledDao.getLedgeer(objModel
//										.getFrom_account_id());
//								if (frm != null)
//									form.addComponent(new SLabel(
//											getPropertyName("from_account"),
//											frm.getName()));
//
//								form.addComponent(new SLabel(
//										getPropertyName("date"),
//										CommonUtil
//												.getUtilDateFromSQLDate(objModel
//														.getDate())));
//
//								form.addComponent(new SLabel(
//										getPropertyName("supplier_amount"),
//										objModel.getSupplier_amount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("discount"), objModel
//												.getDiscount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("payment_amount"),
//										objModel.getPayment_amount() + ""));
//
//								form.addComponent(new SLabel(
//										getPropertyName("description"),
//										objModel.getDescription()));
//
//								form.setWidth("400");
//
//								form.setStyleName("grid_max_limit");
//
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//
//							} 
//							else if (itm.getItemProperty(TBC_TYPE).getValue()
//									.equals("Subscription")) {
//
//								SubscriptionPaymentModel objModel = new SubscriptionPaymentDao().getPaymentModel(id);
//
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,
//										"<h2><u>"+getPropertyName("rental")+"</u></h2>"));
////								form.addComponent(new SLabel("Rental Paymn No. :",objModel.getPayment_id() + ""));
//								LedgerModel cust = ledDao.getLedgeer(objModel.getFrom_account());
//								if (cust != null)
//									form.addComponent(new SLabel(getPropertyName("customer"), cust.getName()));
//
//								LedgerModel toAcc = ledDao.getLedgeer(objModel.getTo_account());
//								
//								if (toAcc != null)
//									form.addComponent(new SLabel(getPropertyName("to_account"),toAcc.getName()));
//
//								form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(objModel.getPayment_date())));
//
////								form.addComponent(new SLabel(getPropertyName("customer_amount"),objModel.getAmount_paid() + ""));
////								form.addComponent(new SLabel(
////										getPropertyName("discount"), objModel
////												.getDiscount() + ""));
//								form.addComponent(new SLabel(getPropertyName("payment_amount"),objModel.getAmount_paid() + ""));
//
////								form.addComponent(new SLabel(getPropertyName("description"),objModel.getDescription()));
//
//								form.setWidth("400");
//
//								form.setStyleName("grid_max_limit");
//
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//
//							}
//							else {
//								PurchaseReturnModel objModel = new PurchaseReturnDao()
//										.getPurchaseReturnModel(id);
//
//								SFormLayout form = new SFormLayout();
//								form.addComponent(new SHTMLLabel(null,
//										"<h2><u>"+getPropertyName("purchase_return")+"</u></h2>"));
//								form.addComponent(new SLabel(
//										getPropertyName("debit_note_no"),
//										objModel.getDebit_note_no() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("supplier"), objModel
//												.getSupplier().getName()));
//								form.addComponent(new SLabel(
//										getPropertyName("date"),
//										CommonUtil
//												.getUtilDateFromSQLDate(objModel
//														.getDate())));
//
//								form.addComponent(new SLabel(
//										getPropertyName("net_amount"), objModel
//												.getAmount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("paid_amount"),
//										objModel.getPayment_amount() + ""));
//
//								SGridLayout grid = new SGridLayout(
//										getPropertyName("item_details"));
//								grid.setColumns(12);
//								grid.setRows(objModel
//										.getInventory_details_list().size() + 3);
//
//								grid.addComponent(new SLabel(null, "#"), 0, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("item")), 1,
//										0);
//								grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,
//										0);
//								grid.addComponent(
//										new SLabel(null, getPropertyName("unit_price")), 4, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("discount")),
//										5, 0);
//								grid.addComponent(new SLabel(null, getPropertyName("amount")),
//										6, 0);
//								grid.setSpacing(true);
//
//								int i = 1;
//								PurchaseReturnInventoryDetailsModel invObj;
//								Iterator itmItr = objModel
//										.getInventory_details_list().iterator();
//								while (itmItr.hasNext()) {
//									invObj = (PurchaseReturnInventoryDetailsModel) itmItr
//											.next();
//									grid.addComponent(new SLabel(null, i + ""),
//											0, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getItem().getName()), 1, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getQunatity() + ""), 2, i);
//									grid.addComponent(new SLabel(null, invObj
//											.getUnit().getSymbol()), 3, i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getUnit_price() + ""), 4,
//											i);
//									grid.addComponent(
//											new SLabel(null, invObj
//													.getDiscount_amount() + ""),
//											5, i);
//									grid.addComponent(
//											new SLabel(
//													null,
//													(invObj.getUnit_price()
//															* invObj.getQunatity()
//															- invObj.getDiscount_amount() + invObj
//																.getTax_amount())
//															+ ""), 6, i);
//									i++;
//								}
//
//								form.addComponent(grid);
//								form.addComponent(new SLabel(
//										getPropertyName("comment"), objModel
//												.getComments()));
//
//								form.setStyleName("grid_max_limit");
//
//								popupContainer.removeAllComponents();
//								SPopupView pop = new SPopupView("", form);
//								popupContainer.addComponent(pop);
//								pop.setPopupVisible(true);
//								pop.setHideOnMouseOut(false);
//
//							}
//
//						}

					} catch (Exception e) {
						e.printStackTrace();
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
						generateConsolidatedReport();
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

			organizationSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
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

			officeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							SCollectionContainer bic = null;
							try {
								bic = SCollectionContainer.setList(ledDao
										.getAllSuppliers((Long) officeSelect
												.getValue()), "id");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ledgertSelect.setContainerDataSource(bic);
							ledgertSelect.setItemCaptionPropertyId("name");

						}
					});

			ledgertSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
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

				List lst = daoObj.getNewSupplierLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						getOfficeID(), (Long) ledgertSelect.getValue(),
						usePaymentEndDate.getValue());

				List reportList = new ArrayList();

				double opening_bal = daoObj.getPurchaseOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue(),
						usePaymentEndDate.getValue());

//				opening_bal += ledger.getOpening_balance();

				// opening_bal=-opening_bal;

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
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						if (obj.getParticulars().equals("Purch Return")) {
							bal -= (obj.getAmount() + obj.getPayed());
							periodBal -= (obj.getAmount() + obj.getPayed());
							obj.setBalance(bal);
						} else {
							bal += obj.getAmount() - obj.getPayed();
							periodBal += obj.getAmount() - obj.getPayed();
							obj.setBalance(bal);
						}

						if (obj.getParticulars().equals("Purch Return")) {
							if (obj.getPayed() == 0)
								obj.setPayed(0);
							table.addItem(new Object[] { ct + 1, obj.getId(),
									obj.getParticulars(), obj.getDate(), 0.0,
									0.0, obj.getPayed(), obj.getAmount(),
									periodBal, bal }, ct);
						} else if (obj.getParticulars().equals("Payment")) {
							table.addItem(new Object[] { ct + 1, obj.getId(),
									obj.getParticulars(), obj.getDate(), 0.0,
									0.0, obj.getPayed(), 0.0, periodBal, bal },
									ct);
						} else if (obj.getParticulars().equals(
								"Commission Sale")) {
							table.addItem(
									new Object[] { ct + 1, obj.getId(),
											obj.getParticulars(),
											obj.getDate(), obj.getGross_sale(),
											obj.getExpence(), obj.getPayed(),
											0.0, periodBal, bal }, ct);
						} else {
							table.addItem(
									new Object[] { ct + 1, obj.getId(),
											obj.getParticulars(),
											obj.getDate(), obj.getAmount(),
											0.0, obj.getPayed(), 0.0,
											periodBal, bal }, ct);
						}

						ct++;

					}
					table.setVisibleColumns(visibleColumns);
				} else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}

				calculateTableTotals();

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

				List lst = daoObj.getNewSupplierLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue(),
						usePaymentEndDate.getValue());

				List reportList = new ArrayList();

				double opening_bal = daoObj.getPurchaseOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue(),
						usePaymentEndDate.getValue());

				double current_bal = daoObj.getPurchaseCurrentBalance(
						CommonUtil.getCurrentSQLDate(),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());

				double ledger_bal = new LedgerViewDao().getLedgerBalance(
						getWorkingDate(), (Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				// opening_bal=-opening_bal;

				// if(opening_bal!=0)
				// reportList.add(new AcctReportMainBean("Opening Balance",
				// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue())
				// , 0, opening_bal));

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

					if (obj.getParticulars().equals("Purch Return")) {
						bal -= obj.getAmount();
						periodBal -= obj.getAmount();
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);
					} else {
						bal += obj.getAmount() - obj.getPayed();
						periodBal += obj.getAmount() - obj.getPayed();
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
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("supplier_ledger_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("PURCHASE_NO_LABEL", getPropertyName("purchase_no"));
					params.put("PURCHASE_LABEL", getPropertyName("purchase"));
					params.put("EXPENSE_COMMISSION_LABEL", getPropertyName("expense_commission"));
					params.put("CASH_LABEL", getPropertyName("cash"));	
					params.put("RETURN_LABEL", getPropertyName("return"));
					params.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
					params.put("BALANCE_LABEL", getPropertyName("balance"));
					params.put("TOTAL_LABEL", getPropertyName("total"));
					
					
					report.setJrxmlFileName("SupplierLedgerReport");
					report.setReportFileName("Supplier Ledger Report");
					report.setReportSubTitle(getPropertyName("from")+" : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+getPropertyName("to")+" : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));

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

	protected void generateConsolidatedReport() {
		try {

			LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List lst = daoObj.getNewSupplierLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue(),
						usePaymentEndDate.getValue());

				List reportList = new ArrayList();

				double opening_bal = daoObj.getPurchaseOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue(),
						usePaymentEndDate.getValue());

				double current_bal = daoObj.getPurchaseCurrentBalance(
						CommonUtil.getCurrentSQLDate(),
						(Long) officeSelect.getValue(),
						(Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				double ledger_bal = new LedgerViewDao().getLedgerBalance(
						getWorkingDate(), (Long) ledgertSelect.getValue());

				// opening_bal=-opening_bal;

				// if(opening_bal!=0)
				// reportList.add(new AcctReportMainBean("Opening Balance",
				// CommonUtil.getSQLDateFromUtilDate(fromDate.getValue())
				// , 0, opening_bal));

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

					if (obj.getParticulars().equals("Purch Return")) {
						bal -= obj.getAmount();
						periodBal -= obj.getAmount();
						obj.setBalance(bal);
						obj.setPeriod_balance(periodBal);

					} else {
						bal += obj.getAmount() - obj.getPayed();
						periodBal += obj.getAmount() - obj.getPayed();
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
					params.put("Organization", ledger.getOffice()
							.getOrganization().getName());
//					params.put("Address", new AddressDao()
//							.getAddressString(ledger.getAddress().getId()));

					
					params.put("name_label", getPropertyName("name"));
					params.put("office_label", getPropertyName("office"));
					params.put("opening_balance_label", getPropertyName("opening_balance"));
					params.put("address_label", getPropertyName("address"));
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("supplier_ledger_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("PURCHASE_NO_LABEL", getPropertyName("purchase_no"));
					params.put("DESCRIPTION_LABEL", getPropertyName("description"));
					params.put("DEBIT_LABEL", getPropertyName("debit"));
					params.put("CREDIT_LABEL", getPropertyName("credit"));
					params.put("PERIOD_BALANCE_LABEL", getPropertyName("period_balance"));
					params.put("BALANCE_LABEL", getPropertyName("balance"));
					
					params.put("PURCHASE_LABEL", getPropertyName("purchase"));
					params.put("PURCHASE_RETURN_LABEL", getPropertyName("purchase_return"));
					params.put("PAYMENT_LABEL", getPropertyName("payment"));	
					
					
					report.setJrxmlFileName("SupplierLedgerReportNew");
					report.setReportFileName("SupplierLedgerReportNew");
					report.setReportSubTitle(getPropertyName("from")+" : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ getPropertyName("to")+" : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));

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

	public void calculateTableTotals() {
		Iterator it = table.getItemIds().iterator();
		Item itm;
		double sal_ttl = 0, cash_ttl = 0, ret_ttl = 0, expns = 0, per_bal = 0, bal = 0;
		while (it.hasNext()) {
			itm = table.getItem(it.next());
			sal_ttl += (Double) itm.getItemProperty(TBC_SALE).getValue();
			cash_ttl += (Double) itm.getItemProperty(TBC_CASH).getValue();
			ret_ttl += (Double) itm.getItemProperty(TBC_RETURN).getValue();
			expns += (Double) itm.getItemProperty(TBC_EXPENSE).getValue();

			per_bal = (Double) itm.getItemProperty(TBC_PERIOD_BAL).getValue();
			bal = (Double) itm.getItemProperty(TBC_BALANCE).getValue();
		}
		table.setColumnFooter(TBC_SALE, asString(roundNumber(sal_ttl)));
		table.setColumnFooter(TBC_CASH, asString(roundNumber(cash_ttl)));
		table.setColumnFooter(TBC_RETURN, asString(roundNumber(ret_ttl)));
		table.setColumnFooter(TBC_EXPENSE, asString(roundNumber(expns)));
		table.setColumnFooter(TBC_PERIOD_BAL, asString(roundNumber(per_bal)));
		table.setColumnFooter(TBC_BALANCE, asString(roundNumber(bal)));
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
