package com.inventory.reports.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.dao.CommissionSalesDao;
import com.inventory.commissionsales.dao.CustomerCommissionSalesDao;
import com.inventory.commissionsales.model.CommissionSalesCustomerDetailsModel;
import com.inventory.commissionsales.model.CommissionSalesModel;
import com.inventory.commissionsales.model.CustomerCommissionSalesModel;
import com.inventory.config.acct.dao.BankAccountDepositDao;
import com.inventory.config.acct.dao.BankAccountPaymentDao;
import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalModel;
import com.inventory.payment.dao.EmployeeAdvancePaymentDao;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.payment.model.PaymentModel;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseOrderDao;
import com.inventory.purchase.dao.PurchaseReturnDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseOrderModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.DayBookDao;
import com.inventory.sales.dao.DeliveryNoteDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.DeliveryNoteModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 10, 2013
 */
public class DayBookUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SHorizontalLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private DayBookDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField dateField;

	private SComboField organizationSelect;
	private SComboField officeSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_PARTICULARS = "Particulars";
	static String TBC_NUMBER = "Number";
	static String TBC_DATE = "Date";
	static String TBC_CHEQUE_DATE = "Cheq Date";
	static String TBC_CLIENT = "Client";
	static String TBC_AMOUNT = "Amount";
	static String TBC_REF_NO = "Ref. No.";
	static String TBC_COMMENTS = "Comments";

	SVerticalLayout mainLay;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SGridLayout ttlGrid;

	SHorizontalLayout popupContainer;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		ofcDao = new OfficeDao();
		ledDao = new LedgerDao();

		try {

			ttlGrid = new SGridLayout();
			ttlGrid.setColumns(5);
			ttlGrid.setRows(3);
			ttlGrid.setSpacing(true);

			allColumns = new String[] { TBC_SN, TBC_ID, TBC_PARTICULARS,
					TBC_NUMBER, TBC_DATE, TBC_CLIENT, TBC_AMOUNT,
					TBC_CHEQUE_DATE, TBC_REF_NO, TBC_COMMENTS };
			visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS,
					TBC_NUMBER, TBC_DATE, TBC_CLIENT, TBC_AMOUNT,
					TBC_CHEQUE_DATE, TBC_REF_NO, TBC_COMMENTS };

			popupContainer = new SHorizontalLayout();
			mainLay = new SVerticalLayout();

			setSize(1180, 610);
			reportType = new SNativeSelect(null, 60, SConstants.reportTypes,
					"intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,
					getPropertyName("particulars"), null, Align.CENTER);
			table.addContainerProperty(TBC_DATE, Date.class, null,
					getPropertyName("date"), null, Align.CENTER);
			table.addContainerProperty(TBC_NUMBER, String.class, null,
					getPropertyName("number"), null, Align.CENTER);
			table.addContainerProperty(TBC_CLIENT, String.class, null,
					getPropertyName("client"), null, Align.CENTER);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_REF_NO, String.class, null,
					getPropertyName("ref_no"), null, Align.CENTER);
			table.addContainerProperty(TBC_COMMENTS, String.class, null,
					getPropertyName("comments"), null, Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_DATE, String.class, null,
					getPropertyName("cheque_date"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_NUMBER, (float) 0.6);
			table.setColumnExpandRatio(TBC_CLIENT, 1);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_REF_NO, 1);
			table.setColumnExpandRatio(TBC_COMMENTS, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("1100");
			table.setHeight("410");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(null, 160,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(null, 160,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());

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

			formLayout = new SHorizontalLayout();
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new DayBookDao();

			dateField = new SDateField(null, 100, getDateFormat(),
					getWorkingDate());

			formLayout
					.addComponent(new SLabel(getPropertyName("organization")));
			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(new SLabel(getPropertyName("office")));
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(new SLabel(getPropertyName("date")));
			formLayout.addComponent(dateField);
			formLayout.addComponent(new SLabel(getPropertyName("report_type")));
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));

			buttonLayout.addComponent(showButton);
			buttonLayout.addComponent(generateButton);
			formLayout.addComponent(buttonLayout);
			formLayout.setSpacing(true);
			mainLay.addComponent(formLayout);
			mainLay.addComponent(popupContainer);
			mainLay.addComponent(table);
			mainLay.addComponent(ttlGrid);

			mainLay.setComponentAlignment(popupContainer,
					Alignment.MIDDLE_CENTER);
			mainLay.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
			mainLay.setComponentAlignment(ttlGrid, Alignment.MIDDLE_CENTER);
			mainLay.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
			mainLay.setMargin(true);

			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID)
									.getValue();
							if (itm.getItemProperty(TBC_PARTICULARS).getValue()
									.equals("Purchase")) {

								PurchaseModel objModel = new PurchaseDao()
										.getPurchaseModel(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Purchase</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("purchase_no"),
										objModel.getPurchase_no() + ""));
								form.addComponent(new SLabel(
										getPropertyName("supplier"), objModel
												.getSupplier().getName()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
//								form.addComponent(new SLabel(
//										getPropertyName("max_credit_period"),
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
										objModel.getPaymentAmount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getPurchase_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								PurchaseInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getPurchase_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (PurchaseInventoryDetailsModel) itmItr
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
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue()
									.equals(getPropertyName("purchase_order"))) {

								PurchaseOrderModel objModel = new PurchaseOrderDao()
										.getPurchaseOrderModel(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Purchase Order</u></h2>"));
								form.addComponent(new SLabel("PO No", objModel
										.getOrder_no() + ""));
								form.addComponent(new SLabel(
										getPropertyName("supplier"), objModel
												.getSupplier().getName()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
//								form.addComponent(new SLabel(
//										getPropertyName("approximate_amount"),
//										objModel.getApproximate_amount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getOrder_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								PurchaseInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getOrder_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (PurchaseInventoryDetailsModel) itmItr
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
										getPropertyName("ref_no"), objModel
												.getRef_no()));
								form.addComponent(new SLabel(
										getPropertyName("comment"), objModel
												.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Commission Sale")) {
								CommissionSalesModel objModel = new CommissionSalesDao()
										.getCommissionSales(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Commission Sales</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("commission_sales_no"),
										objModel.getNumber() + ""));
								form.addComponent(new SLabel(
										getPropertyName("received_date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getReceived_date())));
								form.addComponent(new SLabel(
										getPropertyName("issue_date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getIssue_date())));
								form.addComponent(new SLabel(
										getPropertyName("supplier"), objModel
												.getSupplier().getName()));
								form.addComponent(new SLabel(
										getPropertyName("container_no"),
										objModel.getContr_no()));
								form.addComponent(new SLabel(
										getPropertyName("gross_sale"), ""
												+ objModel.getGross_sale()));
								form.addComponent(new SLabel(
										getPropertyName("less_expenses"),
										objModel.getLess_expense() + ""));
								form.addComponent(new SLabel(
										getPropertyName("airport_charges"),
										objModel.getAirport_charges() + ""));
								form.addComponent(new SLabel(
										getPropertyName("auction"), objModel
												.getAuction() + ""));
								form.addComponent(new SLabel(
										getPropertyName("DPA_charges"),
										objModel.getDpa_charges() + ""));
								form.addComponent(new SLabel(
										getPropertyName("freight"), objModel
												.getFreight() + ""));
								form.addComponent(new SLabel(
										getPropertyName("pickup_charge"),
										objModel.getPickup_charge() + ""));
								form.addComponent(new SLabel(
										getPropertyName("port"), objModel
												.getPort() + ""));
								form.addComponent(new SLabel(
										getPropertyName("storage_charge"),
										objModel.getStorage_charge() + ""));
								form.addComponent(new SLabel(
										getPropertyName("unloading_charge"),
										objModel.getUnloading_charge() + ""));
								form.addComponent(new SLabel(
										getPropertyName("waste"), objModel
												.getWaste() + ""));
								form.addComponent(new SLabel(
										getPropertyName("commission"), objModel
												.getCommission() + ""));
								form.addComponent(new SLabel(
										getPropertyName("supplier_amount"),
										(objModel.getNet_sale() - objModel
												.getCommission()) + ""));
								form.addComponent(new SLabel(
										getPropertyName("details"), objModel
												.getDetails()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Supplier Payment")) {

								PaymentModel objModel = new PaymentDao()
										.getPaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Supplier Payment</u></h2>"));
								form.addComponent(new SLabel("Payment No. :",
										objModel.getPayment_id() + ""));
								LedgerModel supl = ledDao.getLedgeer(objModel
										.getTo_account_id());
								if (supl != null)
									form.addComponent(new SLabel(
											getPropertyName("supplier"), supl
													.getName()));

								LedgerModel frm = ledDao.getLedgeer(objModel
										.getFrom_account_id());
								if (frm != null)
									form.addComponent(new SLabel(
											getPropertyName("from_account"),
											frm.getName()));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("supplier_amount"),
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

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Purchase Return")) {
								PurchaseReturnModel objModel = new PurchaseReturnDao()
										.getPurchaseReturnModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Purchase Return</u></h2>"));
//								form.addComponent(new SLabel(
//										"Debit Note No. :", objModel
//												.getDebit_note_no() + ""));
								form.addComponent(new SLabel(
										getPropertyName("supplier"), objModel
												.getSupplier().getName()));
								form.addComponent(new SLabel(
										"Date :",
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("net_amount"), objModel
												.getAmount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("paid_amount"),
										objModel.getAmount()+ ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);

								int i = 1;
								PurchaseReturnInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getInventory_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (PurchaseReturnInventoryDetailsModel) itmItr
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

							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Sales Order")) {

								SalesOrderModel objModel = new SalesOrderDao()
										.getSalesOrderModel(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Sales Order</u></h2>"));
								form.addComponent(new SLabel("SO No. :",
										objModel.getOrder_no() + ""));
								form.addComponent(new SLabel("Customer :",
										objModel.getCustomer().getName()));
								form.addComponent(new SLabel(
										"Date :",
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("total_amount"),
										objModel.getAmount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getOrder_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								SalesInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getOrder_details_list().iterator();
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
										getPropertyName("ref_no"), objModel
												.getRef_no()));
								form.addComponent(new SLabel(
										getPropertyName("comment"), objModel
												.getComments()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Delivery Note")) {

								DeliveryNoteModel objModel = new DeliveryNoteDao()
										.getDeliveryNoteModel(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Delivery Note</u></h2>"));
								form.addComponent(new SLabel("DO No. :",
										objModel.getDeliveryNo()
												+ ""));
								form.addComponent(new SLabel(
										getPropertyName("employee"), new UserManagementDao()
											.getUser(objModel.getResponsible_employee()).getFirst_name()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("amount"), objModel
												.getAmount() + ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getDelivery_note_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								SalesInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getDelivery_note_details_list().iterator();
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
													.getDiscount()+ ""),
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
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Sale")) {

								SalesModel objModel = new SalesDao()
										.getSale(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Sale</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("sales_no"), objModel
												.getSales_number() + ""));
								form.addComponent(new SLabel(
										getPropertyName("customer"), objModel
												.getCustomer().getName()));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
//								form.addComponent(new SLabel(
//										getPropertyName("max_credit_period"),
//										objModel.getCredit_note()_period() + ""));

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
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
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
															- invObj.getDiscount()+ invObj
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
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue()
									.equals("Customer Commission Sale")) {
								CustomerCommissionSalesModel objModel = new CustomerCommissionSalesDao()
										.getSale(id);
								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Customer Commission Sales</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("sales_no"), objModel
												.getSales_no() + ""));
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
								grid.setRows(objModel.getDetails_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Customer"),
										1, 0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);
								int i = 1;
								CommissionSalesCustomerDetailsModel invObj;
								Iterator itmItr = objModel.getDetails_list()
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
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Customer Payment")) {

								PaymentModel objModel = new PaymentDao()
										.getPaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Customer Payment</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("receipt_no"), objModel
												.getPayment_id() + ""));
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

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Sales Return")) {
								SalesReturnModel objModel = new SalesReturnDao()
										.getSalesReturnModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Sales Return</u></h2>"));
								form.addComponent(new SLabel(
										"Return No. :", objModel
												.getReturn_no() + ""));
								form.addComponent(new SLabel(
										getPropertyName("customer"), objModel
												.getCustomer().getName()));
								form.addComponent(new SLabel(
										"Date :",
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("net_amount"), objModel
												.getAmount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("paid_amount"),
										objModel.getAmount()+ ""));

								SGridLayout grid = new SGridLayout(
										getPropertyName("item_details"));
								grid.setColumns(12);
								grid.setRows(objModel
										.getInventory_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null, "Item"), 1,
										0);
								grid.addComponent(new SLabel(null, "Qty"), 2, 0);
								grid.addComponent(new SLabel(null, "Unit"), 3,
										0);
//								grid.addComponent(
//										new SLabel(null, "Stock Qty"), 4, 0);
//								grid.addComponent(new SLabel(null,
//										"Purch. Rtn Qty"), 5, 0);
//								grid.addComponent(
//										new SLabel(null, "Waste Qty"), 6, 0);
								grid.addComponent(
										new SLabel(null, "Unit Price"), 4, 0);
								grid.addComponent(new SLabel(null, "Discount"),
										5, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										6, 0);
								grid.setSpacing(true);

								int i = 1;
								SalesReturnInventoryDetailsModel invObj;
								Iterator itmItr = objModel
										.getInventory_details_list().iterator();
								while (itmItr.hasNext()) {
									invObj = (SalesReturnInventoryDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getItem().getName()), 1, i);
									grid.addComponent(
											new SLabel(null, invObj
													.getStock_id() + ""),
											2, i);
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
															* (invObj
																	.getQunatity())
															- invObj.getDiscount() + invObj
																.getTaxAmount())
															+ ""), 9, i);
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

							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue()
									.equals("Transportation Payment")) {

								TransportationPaymentModel objModel = new TransportationPaymentDao()
										.getTransportationPaymentModel(id);

								SFormLayout form = new SFormLayout();
								if (objModel.getType() == 1) {
									form.addComponent(new SHTMLLabel(null,
											"<h2><u>Transportation Cash</u></h2>"));
								} else {
									form.addComponent(new SHTMLLabel(null,
											"<h2><u>Transportation Credit</u></h2>"));
								}

								form.addComponent(new SLabel(
										getPropertyName("transportation_no"),
										objModel.getId() + ""));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));

								form.addComponent(new SLabel(
										getPropertyName("transportation_amount"),
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

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue()
									.equals("Expenditure Transaction")) {

								PaymentDepositModel objModel = new ExpendetureTransactionDao()
										.getExpendetureTransaction(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Expenditure Transaction</u></h2>"));

								form.addComponent(new SLabel(
										getPropertyName("bill_no"), objModel
												.getBill_no() + ""));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								if (objModel.getCash_or_check() == 2)
									form.addComponent(new SLabel(
											getPropertyName("cash_cheque"),
											"Cheque"));
								else
									form.addComponent(new SLabel(
											getPropertyName("cash_cheque"),
											"Cash"));

								form.addComponent(new SLabel(
										getPropertyName("ref_no"), objModel
												.getRef_no()));

								form.addComponent(new SLabel(
										getPropertyName("memo"), objModel
												.getMemo()));

								SGridLayout grid = new SGridLayout(
										getPropertyName("transaction_details"));
								grid.setColumns(5);
								grid.setRows(objModel.getTransaction()
										.getTransaction_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null,
										"From Account"), 1, 0);
								grid.addComponent(
										new SLabel(null, "To Account"), 2, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										3, 0);
								grid.setSpacing(true);

								int i = 1;
								TransactionDetailsModel invObj;
								Iterator itmItr = objModel.getTransaction()
										.getTransaction_details_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (TransactionDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getFromAcct().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj
											.getToAcct().getName()), 2, i);
									grid.addComponent(
											new SLabel(null, invObj.getAmount()
													+ ""), 3, i);
									i++;
								}

								form.addComponent(grid);
								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Journal")) {

								JournalModel objModel = new JournalDao()
										.getJournalModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Journal</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("journal_no"), objModel
												.getId() + ""));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("ref_no"), objModel
												.getRef_no()));
								form.addComponent(new SLabel(
										getPropertyName("memo"), objModel
												.getBill_no()));

								SGridLayout grid = new SGridLayout(
										getPropertyName("transaction_details"));
								grid.setColumns(5);
								TransactionModel tranModel = new TransportationPaymentDao().getTransaction(objModel.getTransaction_id());
						//		TransactionModel tranModel = new trans
								grid.setRows(tranModel.getTransaction_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null,
										"From Account"), 1, 0);
								grid.addComponent(
										new SLabel(null, "To Account"), 2, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										3, 0);
								grid.setSpacing(true);

								int i = 1;
								TransactionDetailsModel invObj;
								Iterator itmItr = tranModel
										.getTransaction_details_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (TransactionDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getFromAcct().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj
											.getToAcct().getName()), 2, i);
									grid.addComponent(
											new SLabel(null, invObj.getAmount()
													+ ""), 3, i);
									i++;
								}

								form.addComponent(grid);
								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);

							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Income Transaction")) {

								PaymentDepositModel objModel = new ExpendetureTransactionDao()
										.getExpendetureTransaction(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Income Transactions</u></h2>"));

								form.addComponent(new SLabel(
										getPropertyName("bill_no"), objModel
												.getBill_no() + ""));
								form.addComponent(new SLabel(
										"Date :",
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("ref_no"), objModel
												.getRef_no()));
								form.addComponent(new SLabel(
										getPropertyName("comments"), objModel
												.getMemo()));

								SGridLayout grid = new SGridLayout(
										getPropertyName("transaction_details"));
								grid.setColumns(5);
								grid.setRows(objModel.getTransaction()
										.getTransaction_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null,
										"From Account"), 1, 0);
								grid.addComponent(
										new SLabel(null, "To Account"), 2, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										3, 0);
								grid.setSpacing(true);

								int i = 1;
								TransactionDetailsModel invObj;
								Iterator itmItr = objModel.getTransaction()
										.getTransaction_details_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (TransactionDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getFromAcct().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj
											.getToAcct().getName()), 2, i);
									grid.addComponent(
											new SLabel(null, invObj.getAmount()
													+ ""), 3, i);
									i++;
								}

								form.addComponent(grid);

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Bank Acct. Payment")) {

								BankAccountPaymentModel objModel = new BankAccountPaymentDao()
										.getBankAccountPaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Bank Account Payment</u></h2>"));

								form.addComponent(new SLabel(
										getPropertyName("no"), objModel.getId()
												+ ""));

								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
//								if (objModel.getCash_or_check() == 2)
//									form.addComponent(new SLabel(
//											getPropertyName("cash_cheque"),
//											"Cheque"));
//								else
//									form.addComponent(new SLabel(
//											getPropertyName("cash_cheque"),
//											"Cash"));

								form.addComponent(new SLabel(
										getPropertyName("ref_no"), objModel
												.getRef_no()));

								form.addComponent(new SLabel(
										getPropertyName("memo"), objModel
												.getMemo()));

								SGridLayout grid = new SGridLayout(
										getPropertyName("transaction_details"));
								grid.setColumns(5);
								
								TransactionModel tranModel = new TransportationPaymentDao().getTransaction(objModel.getTransactionId());
								
								grid.setRows(tranModel.getTransaction_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null,
										"From Account"), 1, 0);
								grid.addComponent(
										new SLabel(null, "To Account"), 2, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										3, 0);
								grid.setSpacing(true);

								int i = 1;
								TransactionDetailsModel invObj;
								Iterator itmItr = tranModel
										.getTransaction_details_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (TransactionDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getFromAcct().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj
											.getToAcct().getName()), 2, i);
									grid.addComponent(
											new SLabel(null, invObj.getAmount()
													+ ""), 3, i);
									i++;
								}

								form.addComponent(grid);

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Bank Acct. Deposit")) {

								BankAccountDepositModel objModel = new BankAccountDepositDao()
										.getBankAccountDepositModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Bank Account Deposit</u></h2>"));

								form.addComponent(new SLabel(
										getPropertyName("no"), objModel.getId()
												+ ""));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
//								if (objModel.getCash_or_check() == 2)
//									form.addComponent(new SLabel(
//											getPropertyName("cash_cheque"),
//											"Cheque"));
//								else
//									form.addComponent(new SLabel(
//											getPropertyName("cash_cheque"),
//											"Cash"));

								form.addComponent(new SLabel(
										getPropertyName("ref_no"), objModel
												.getRef_no()));
								form.addComponent(new SLabel(
										getPropertyName("memo"), objModel
												.getMemo()));

								SGridLayout grid = new SGridLayout(
										getPropertyName("transaction_details"));
								grid.setColumns(5);
								
								TransactionModel tranModel = new TransportationPaymentDao().getTransaction(objModel.getTransactionId());
								
								grid.setRows(tranModel
										.getTransaction_details_list().size() + 3);

								grid.addComponent(new SLabel(null, "#"), 0, 0);
								grid.addComponent(new SLabel(null,
										"From Account"), 1, 0);
								grid.addComponent(
										new SLabel(null, "To Account"), 2, 0);
								grid.addComponent(new SLabel(null, "Amount"),
										3, 0);
								grid.setSpacing(true);

								int i = 1;
								TransactionDetailsModel invObj;
								Iterator itmItr = tranModel
										.getTransaction_details_list()
										.iterator();
								while (itmItr.hasNext()) {
									invObj = (TransactionDetailsModel) itmItr
											.next();
									grid.addComponent(new SLabel(null, i + ""),
											0, i);
									grid.addComponent(new SLabel(null, invObj
											.getFromAcct().getName()), 1, i);
									grid.addComponent(new SLabel(null, invObj
											.getToAcct().getName()), 2, i);
									grid.addComponent(
											new SLabel(null, invObj.getAmount()
													+ ""), 3, i);
									i++;
								}

								form.addComponent(grid);

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue()
									.equals("Employee Advance Payment")) {

								EmployeeAdvancePaymentModel objModel = new EmployeeAdvancePaymentDao()
										.getEmployeeAdvancePaymentModel(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Employee Advance Payment</u></h2>"));
								form.addComponent(new SLabel("Payment No. :",
										objModel.getPayment_id() + ""));
								form.addComponent(new SLabel(
										getPropertyName("employee"),
										new UserManagementDao()
												.getUserNameFromLoginID(objModel
														.getLogin_id())));
								form.addComponent(new SLabel(
										getPropertyName("from_account"), ledDao
												.getLedgerNameFromID(objModel
														.getAccount_id())));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDate())));
								form.addComponent(new SLabel(
										getPropertyName("transportation_amount"),
										objModel.getAmount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("deductions"), objModel
//												.getDiscount() + ""));
//								form.addComponent(new SLabel(
//										getPropertyName("payment_amount"),
//										objModel.getPayment_amount() + ""));
								form.addComponent(new SLabel(
										getPropertyName("description"),
										objModel.getDescription()));

								form.setStyleName("grid_max_limit");

								popupContainer.removeAllComponents();
								SPopupView pop = new SPopupView("", form);
								popupContainer.addComponent(pop);
								pop.setPopupVisible(true);
								pop.setHideOnMouseOut(false);
							} else if (itm.getItemProperty(TBC_PARTICULARS)
									.getValue().equals("Salary Disbursal")) {

								SalaryDisbursalNewModel objModel = daoObj
										.getSalaryDesbursal(id);

								SFormLayout form = new SFormLayout();
								form.addComponent(new SHTMLLabel(null,
										"<h2><u>Salary Disbursal</u></h2>"));
								form.addComponent(new SLabel(
										getPropertyName("no"), objModel.getId()
												+ ""));
								form.addComponent(new SLabel(
										getPropertyName("employee"), objModel
												.getEmploy().getFirst_name()));
								form.addComponent(new SLabel(
										getPropertyName("salary_month"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getMonth())));
								form.addComponent(new SLabel(
										getPropertyName("date"),
										CommonUtil
												.getUtilDateFromSQLDate(objModel
														.getDispursal_date())));
								form.addComponent(new SLabel(
										getPropertyName("advance_paid_amount"),
										objModel.getAdvance_payed() + ""));
								form.addComponent(new SLabel(
										getPropertyName("salary_amount"),
										objModel.getTotal_salary() + ""));

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

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						generateReport();
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
							if(officeSelect.getItemIds()!=null)
								officeSelect.setValue(officeSelect.getItemIds().iterator().next());
						}
					});

			officeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

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

			if (isValid()) {

				List lst = daoObj
						.getDayBook(CommonUtil.getSQLDateFromUtilDate(dateField
								.getValue()), (Long) officeSelect.getValue(),
								(Long) organizationSelect.getValue());

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

					double ttl_purch = 0, ttl_sup_pay = 0, ttl_sal = 0, ttl_cust_pay = 0, ttl_sal_rtn = 0, ttl_pur_rtn = 0, ttl_expnd = 0, ttl_transp = 0, ttl_emp_adv = 0, ttl_sal_disb = 0;

					int ct = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						table.addItem(
								new Object[] { ct + 1, obj.getId(),
										obj.getParticulars(), obj.getNo(),
										obj.getDate(), obj.getClient_name(),
										obj.getAmount(), obj.getCheque_date(),
										obj.getRef_no(), obj.getComments() },
								ct);

						ct++;

						if (obj.getParticulars().equals("Purchase")) {
							ttl_purch += obj.getAmount();
						} else if (obj.getParticulars().equals(
								"Supplier Payment")) {
							ttl_sup_pay += obj.getAmount();
						} else if (obj.getParticulars().equals(
								"Purchase Return")) {
							ttl_pur_rtn += obj.getAmount();
						} else if (obj.getParticulars().equals("Sale")) {
							ttl_sal += obj.getAmount();
						} else if (obj.getParticulars().equals(
								"Customer Payment")) {
							ttl_cust_pay += obj.getAmount();
						} else if (obj.getParticulars().equals("Sales Return")) {
							ttl_sal_rtn += obj.getAmount();
						}

						else if (obj.getParticulars().equals(
								"Expenditure Transaction")) {
							ttl_expnd += obj.getAmount();
						} else if (obj.getParticulars().equals(
								"Transportation Payment")) {
							ttl_transp += obj.getAmount();
						} else if (obj.getParticulars().equals(
								"Employee Advance Payment")) {
							ttl_emp_adv += obj.getAmount();
						} else if (obj.getParticulars().equals(
								"Salary Disbursal")) {
							ttl_sal_disb += obj.getAmount();
						}
					}
					table.setVisibleColumns(visibleColumns);

					ttlGrid.removeAllComponents();
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Total Purchases :  "
									+ ttl_purch));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Tot. Suplier Payments :  "
									+ ttl_sup_pay));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Tot. Purchase Return :  "
									+ ttl_pur_rtn));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Tot. Sales :  " + ttl_sal));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Tot. Customer Payments :  "
									+ ttl_cust_pay));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Total Sales Returns :  "
									+ ttl_sal_rtn));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Total Expnd. Transactions :  "
									+ ttl_expnd));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Total Transp. Payments :  "
									+ ttl_transp));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Total Employ. Adv. Pays :  "
									+ ttl_emp_adv));
					ttlGrid.addComponent(new SHTMLLabel(null,
							"<b>&nbsp;&nbsp;&nbsp;Total Salary Disbursals :  "
									+ ttl_sal_disb));

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

			if (isValid()) {

				List reportList = daoObj
						.getDayBook(CommonUtil.getSQLDateFromUtilDate(dateField
								.getValue()), (Long) officeSelect.getValue(),
								(Long) organizationSelect.getValue());

				if (reportList.size() > 0) {
					Collections.sort(reportList,
							new Comparator<AcctReportMainBean>() {
								@Override
								public int compare(
										final AcctReportMainBean object1,
										final AcctReportMainBean object2) {
									return object1.getDate().compareTo(
											object2.getDate());
								}
							});
				}

				double ttl_purch = 0, ttl_sup_pay = 0, ttl_sal = 0, ttl_cust_pay = 0, ttl_sal_rtn = 0, ttl_pur_rtn = 0, ttl_expnd = 0, ttl_transp = 0, ttl_emp_adv = 0, ttl_sal_disb = 0;
				AcctReportMainBean obj;
				Iterator it = reportList.iterator();
				while (it.hasNext()) {
					obj = (AcctReportMainBean) it.next();

					if (obj.getParticulars().equals("Purchase")) {
						ttl_purch += obj.getAmount();
					} else if (obj.getParticulars().equals("Supplier Payment")) {
						ttl_sup_pay += obj.getAmount();
					} else if (obj.getParticulars().equals("Purchase Return")) {
						ttl_pur_rtn += obj.getAmount();
					} else if (obj.getParticulars().equals("Sale")) {
						ttl_sal += obj.getAmount();
					} else if (obj.getParticulars().equals("Customer Payment")) {
						ttl_cust_pay += obj.getAmount();
					} else if (obj.getParticulars().equals("Sales Return")) {
						ttl_sal_rtn += obj.getAmount();
					}

					else if (obj.getParticulars().equals(
							"Expenditure Transaction")) {
						ttl_expnd += obj.getAmount();
					} else if (obj.getParticulars().equals(
							"Transportation Payment")) {
						ttl_transp += obj.getAmount();
					} else if (obj.getParticulars().equals(
							"Employee Advance Payment")) {
						ttl_emp_adv += obj.getAmount();
					} else if (obj.getParticulars().equals("Salary Disbursal")) {
						ttl_sal_disb += obj.getAmount();
					}
				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", dateField.getValue().toString());
					params.put("Office", officeSelect
							.getItemCaption(officeSelect.getValue()));
					params.put("Organization", organizationSelect
							.getItemCaption(organizationSelect.getValue()));

					params.put("ttl_purch", ttl_purch);
					params.put("ttl_sup_pay", ttl_sup_pay);
					params.put("ttl_sal", ttl_sal);
					params.put("ttl_cust_pay", ttl_cust_pay);
					params.put("ttl_sal_rtn", ttl_sal_rtn);
					params.put("ttl_pur_rtn", ttl_pur_rtn);
					params.put("ttl_expnd", ttl_expnd);
					params.put("ttl_transp", ttl_transp);
					params.put("ttl_emp_adv", ttl_emp_adv);
					params.put("ttl_sal_disb", ttl_sal_disb);

					report.setJrxmlFileName("DayBook");
					report.setReportFileName("Day Book");
					report.setReportTitle("Day Book");
					report.setReportSubTitle("Date  : "
							+ CommonUtil.formatDateToCommonFormat(dateField
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

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			dateField.focus();
			ret = false;
		} else
			setRequiredError(dateField, null, false);

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
