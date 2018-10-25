package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.dao.CommissionPaymentDao;
import com.inventory.commissionsales.dao.CommissionPurchaseDao;
import com.inventory.commissionsales.dao.CommissionSalesNewDao;
import com.inventory.commissionsales.model.CommissionPaymentModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.commissionsales.model.CommissionSalesCustomerDetailsModel;
import com.inventory.commissionsales.model.CommissionSalesNewModel;
import com.inventory.config.acct.dao.BankAccountDepositDao;
import com.inventory.config.acct.dao.BankAccountPaymentDao;
import com.inventory.config.acct.dao.CashAccountDepositDao;
import com.inventory.config.acct.dao.CashAccountPaymentDao;
import com.inventory.config.acct.dao.CreditNoteDao;
import com.inventory.config.acct.dao.DebitNoteDao;
import com.inventory.config.acct.dao.ExpendetureTransactionDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.PDCPaymentDao;
import com.inventory.config.acct.model.BankAccountDepositDetailsModel;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentDetailsModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.CashAccountDepositDetailsModel;
import com.inventory.config.acct.model.CashAccountDepositModel;
import com.inventory.config.acct.model.CashAccountPaymentDetailsModel;
import com.inventory.config.acct.model.CashAccountPaymentModel;
import com.inventory.config.acct.model.CreditNoteDetailsModel;
import com.inventory.config.acct.model.CreditNoteModel;
import com.inventory.config.acct.model.DebitNoteDetailsModel;
import com.inventory.config.acct.model.DebitNoteModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.config.acct.model.PdcPaymentDetailsModel;
import com.inventory.config.acct.model.PdcPaymentModel;
import com.inventory.config.acct.ui.BankAccountDepositUI;
import com.inventory.config.acct.ui.BankAccountPaymentUI;
import com.inventory.config.acct.ui.CashAccountDepositUI;
import com.inventory.config.acct.ui.CashAccountPaymentUI;
import com.inventory.config.acct.ui.CreditNoteUI;
import com.inventory.config.acct.ui.DebitNoteUI;
import com.inventory.config.acct.ui.PDCPaymentUI;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalDetailsModel;
import com.inventory.journal.model.JournalModel;
import com.inventory.journal.ui.JournalNewUI;
import com.inventory.payment.dao.CashInvestmentDao;
import com.inventory.payment.dao.EmployeeAdvancePaymentDao;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.dao.TransportationPaymentDao;
import com.inventory.payment.model.CashInvestmentModel;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.payment.model.PaymentModel;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.payment.ui.CashInvestmentUI;
import com.inventory.payment.ui.EmployeeAdvancePaymentsUI;
import com.inventory.payroll.dao.CommissionSalaryDao;
import com.inventory.payroll.dao.SalaryDisbursalDao;
import com.inventory.payroll.model.CommissionSalaryModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.inventory.payroll.ui.CommissionSalaryUI;
import com.inventory.payroll.ui.SalaryDisbursalNewUI;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.dao.PurchaseReturnDao;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.LedgerViewDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesReturnDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.ui.SalesReturnUI;
import com.inventory.sales.ui.SalesUI;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.subscription.ui.AddExpenditure;
import com.inventory.subscription.ui.RentalPaymentsUI;
import com.inventory.subscription.ui.RentalTransactionNewUI;
import com.inventory.subscription.ui.SubscriptionPayment;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;
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
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DivisionModel;

/**
 * 
 * @author anil
 * @date 05-Nov-2015
 * @Project REVERP
 */
public class LedgerViewUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private LedgerViewDao daoObj;
	private LedgerDao ledgDao;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_INVOICE_ID = "Invoice Id";
	static String TBC_INVOICE= "Invoice No";
	static String TBC_DATE = "Date";
	static String TBC_TYPE_ID = "Type ID";
	static String TBC_TYPE = "Type";
	static String TBC_FROM = "From/TO";
	static String TBC_INWARD = "Inward";
	static String TBC_OUTWARD = "Outward";
	static String TBC_BALANCE = "Balance";
	static String TBC_COMMENTS = "Comments";
	
	String[] allColumns;
	String[] visibleColumns;
	
	private STable table;
	SHorizontalLayout popupContainer;
	
	SComboField divisionCombo;
	SComboField departmentCombo;
	int counter=0;
	
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			setSize(1100, 400);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");
			
			allColumns = new String[] { TBC_SN, TBC_ID,TBC_TYPE_ID,TBC_DATE, TBC_TYPE,TBC_INVOICE_ID,TBC_INVOICE,TBC_FROM, TBC_INWARD, TBC_OUTWARD,TBC_BALANCE,TBC_COMMENTS};
			visibleColumns = new String[] { TBC_SN, TBC_DATE, TBC_TYPE,TBC_INVOICE,TBC_FROM, TBC_INWARD, TBC_OUTWARD,TBC_BALANCE,TBC_COMMENTS};

			mainPanel = new SPanel();
			mainPanel.setSizeFull();
			popupContainer = new SHorizontalLayout();
			
			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");
			ledgertSelect = new SComboField(getPropertyName("ledger"), 200,
					null, "id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("select"));

			if (isSystemAdmin() || isSuperAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else {
					officeSelect.setEnabled(false);
				}
			}
			
			divisionCombo = new SComboField(getPropertyName("Division"),200, null,"id", "name", false, getPropertyName("select"));
			departmentCombo = new SComboField(getPropertyName("department"), 200, null,"id", "name", false, getPropertyName("select"));
			loadDepartments(getOrganizationID());
			loadDivisions(getOrganizationID());
			

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new LedgerViewDao();
			ledgDao=new LedgerDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());
			
			table = new STable(null, 700, 300);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_TYPE_ID, Integer.class, null, TBC_TYPE_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_INVOICE_ID, Long.class, null, TBC_INVOICE_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_INVOICE, String.class, null, getPropertyName("invoice_no"), null,
					Align.CENTER);
			table.addContainerProperty(TBC_COMMENTS, String.class, null, getPropertyName("comments"),
					null, Align.LEFT);
			table.addContainerProperty(TBC_TYPE, String.class, null, TBC_TYPE,
					null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, String.class, null,
					getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_FROM, String.class, null,
					getPropertyName("from_to_account"), null, Align.LEFT);
			table.addContainerProperty(TBC_INWARD, Double.class, null,
					getPropertyName("inward"), null, Align.RIGHT);
			table.addContainerProperty(TBC_OUTWARD, Double.class, null,
					getPropertyName("outward"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,
					getPropertyName("Balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
			table.setColumnExpandRatio(TBC_DATE, (float) 1.2f);
			table.setColumnExpandRatio(TBC_TYPE, (float) 2);
			table.setColumnExpandRatio(TBC_INVOICE, (float) 0.7);
			table.setColumnExpandRatio(TBC_FROM, (float)2);
			table.setColumnExpandRatio(TBC_OUTWARD, 1);
			table.setColumnExpandRatio(TBC_INWARD, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));
			
			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(divisionCombo);
			formLayout.addComponent(departmentCombo);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);
			
			SHorizontalLayout mainLay=new SHorizontalLayout();

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);
			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);
			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		table.addHeaderClickListener(new HeaderClickListener() {
//            public void headerClick(HeaderClickEvent event) {
//            	if(event.getPropertyId()==TBC_DATE){
//            		
//            		counter=counter+1;
//            		if(counter%2==0){
//            			table.sort(new Object[]{TBC_SN}, new boolean[]{false});
//            			table.markAsDirty();
//            		}
//            		else{
//            			table.sort(new Object[]{TBC_SN}, new boolean[]{true});
//            			table.markAsDirty();
//            		}
//            	}
//            }
//        });

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

		organizationSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(
							new OfficeDao()
									.getAllOfficeNamesUnderOrg((Long) organizationSelect
											.getValue()), "id");
					
					officeSelect.setContainerDataSource(bic);
					officeSelect.setItemCaptionPropertyId("name");
					
					loadDepartments((Long)organizationSelect.getValue());
					loadDivisions((Long)organizationSelect.getValue());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(ledgDao
							.getAllActiveLedgerNames((Long) officeSelect
									.getValue()), "id");
				} catch (Exception e) {
					e.printStackTrace();
				}
				ledgertSelect.setContainerDataSource(bic);
				ledgertSelect.setItemCaptionPropertyId("name");

			}
		});
		ledgertSelect.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				
				table.removeAllItems();
				
			}
		});

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());
		
		
		table.addListener(new Listener() {
			
			@Override
			public void componentEvent(Event event) {
				if(table.getValue()!=null){
					try {
						
					Item item=table.getItem(table.getValue());
					SFormLayout form = new SFormLayout();
					form.setStyleName("grid_max_limit");
					SPopupView pop = new SPopupView("", form);
					SGridLayout grid = new SGridLayout(
							getPropertyName("item_details"));
					Iterator itmItr ;
					int i = 1;
					
					long id=(Long) item.getItemProperty(TBC_ID).getValue();
					switch ((Integer) item.getItemProperty(TBC_TYPE_ID).getValue()) {
					case SConstants.SALES:
						SalesModel sales = new SalesDao().getSale(id);
						form.addComponent(new SHTMLLabel(null, "<h2><u>"
								+ getPropertyName("sale") + "</u></h2>"));
						form.addComponent(new SLabel(
								getPropertyName("sales_no"), sales
										.getSales_number() + ""));
						form.addComponent(new SLabel(getPropertyName("ref_no"),
								sales.getRef_no() + ""));
						form.addComponent(new SLabel(
								getPropertyName("customer"), sales
										.getCustomer().getName()));
						form.addComponent(new SLabel(getPropertyName("date"),
								CommonUtil.getUtilDateFromSQLDate(sales
										.getDate())));

						form.addComponent(new SLabel(
								getPropertyName("net_amount"), sales
										.getAmount() + ""));
						form.addComponent(new SLabel(
								getPropertyName("paid_amount"), sales
										.getPayment_amount() + ""));

						grid.setColumns(12);
						grid.setRows(sales.getInventory_details_list()
								.size() + 3);

						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("item")), 1, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("quantity")), 2, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("unit")), 3, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("rate")), 4, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("discount")), 5, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("amount")), 6, 0);
						grid.setSpacing(true);
						SalesInventoryDetailsModel salesChild;
						itmItr = sales.getInventory_details_list()
								.iterator();
						while (itmItr.hasNext()) {
							salesChild = (SalesInventoryDetailsModel) itmItr.next();

							grid.addComponent(new SLabel(null, i + ""), 0, i);
							grid.addComponent(new SLabel(null, salesChild.getItem()
									.getName()), 1, i);
							grid.addComponent(
									new SLabel(null, salesChild.getQunatity() + ""),
									2, i);
							grid.addComponent(new SLabel(null, salesChild.getUnit()
									.getSymbol()), 3, i);
							grid.addComponent(
									new SLabel(null, salesChild.getUnit_price()
											+ ""), 4, i);
							grid.addComponent(
									new SLabel(null, salesChild
											.getDiscount() + ""), 5, i);
							grid.addComponent(
									new SLabel(
											null,
											(salesChild.getUnit_price()
													* salesChild.getQunatity()
													- salesChild.getDiscount() + salesChild
														.getTaxAmount())
													+ ""), 6, i);
							i++;
						}

						form.addComponent(grid);
						form.addComponent(new SLabel(
								getPropertyName("comment"), sales
										.getComments()));

						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.PURCHASE:
						PurchaseModel purch = new PurchaseDao()
								.getPurchaseModel(id);
						
						form.addComponent(new SHTMLLabel(null, "<h2><u>"
								+ getPropertyName("purchase") + "</u></h2>"));
						form.addComponent(new SLabel(
								getPropertyName("purchase_no"), purch
										.getPurchase_no() + ""));
						form.addComponent(new SLabel(getPropertyName("ref_no"),
								purch.getRef_no()+ ""));
						form.addComponent(new SLabel(
								getPropertyName("supplier"), purch
										.getSupplier().getName()));
						form.addComponent(new SLabel(getPropertyName("date"),
								CommonUtil.getUtilDateFromSQLDate(purch
										.getDate())));


						form.addComponent(new SLabel(
								getPropertyName("net_amount"), purch
										.getAmount() + ""));
						form.addComponent(new SLabel(
								getPropertyName("paid_amount"), purch
										.getPaymentAmount() + ""));

						
						grid.setColumns(12);
						grid.setRows(purch.getPurchase_details_list()
								.size() + 3);

						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("item")), 1, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("quantity")), 2, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("unit")), 3, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("unit_price")), 4, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("discount")), 5, 0);
						grid.addComponent(new SLabel(null,
								getPropertyName("amount")), 6, 0);
						grid.setSpacing(true);
						
						PurchaseInventoryDetailsModel purchChild;
						itmItr = purch.getPurchase_details_list()
								.iterator();
						while (itmItr.hasNext()) {
							purchChild = (PurchaseInventoryDetailsModel) itmItr
									.next();

							grid.addComponent(new SLabel(null, i + ""), 0, i);
							grid.addComponent(new SLabel(null, purchChild.getItem()
									.getName()), 1, i);
							grid.addComponent(
									new SLabel(null, purchChild.getQunatity() + ""),
									2, i);
							grid.addComponent(new SLabel(null, purchChild.getUnit()
									.getSymbol()), 3, i);
							grid.addComponent(
									new SLabel(null, purchChild.getUnit_price()
											+ ""), 4, i);
							grid.addComponent(
									new SLabel(null, purchChild
											.getDiscount() + ""), 5, i);
							grid.addComponent(
									new SLabel(
											null,
											(purchChild.getUnit_price()
													* purchChild.getQunatity()
													- purchChild.getDiscount() + purchChild
														.getTaxAmount())
													+ ""), 6, i);
							i++;
						}

						form.addComponent(grid);
						form.addComponent(new SLabel(
								getPropertyName("comment"), purch
										.getComments()));
						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.JOURNAL:
							JournalModel jrnlMdl = new JournalDao()
									.getJournalModel(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Journal</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("journal_no"), jrnlMdl
											.getId() + ""));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(jrnlMdl
													.getDate())));
							form.addComponent(new SLabel(
									getPropertyName("ref_no"), jrnlMdl
											.getRef_no()));
							form.addComponent(new SLabel(
									getPropertyName("memo"), jrnlMdl.getRemarks()));

							grid.setColumns(5);
							grid.setRows(jrnlMdl.getJournal_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "From Account"),
									1, 0);
							grid.addComponent(new SLabel(null, "Type"),
									2, 0);
							grid.addComponent(new SLabel(null, "Amount"), 3, 0);
							grid.setSpacing(true);

							String type="";
							JournalDetailsModel jrnlTransDet;
							itmItr = jrnlMdl.getJournal_details_list().iterator();
							while (itmItr.hasNext()) {
								jrnlTransDet = (JournalDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, jrnlTransDet
										.getLedger().getName()), 1, i);
								if(jrnlTransDet.getTransaction_type()==SConstants.CR)
									type="CR";
								else
									type="DR";
								grid.addComponent(new SLabel(null, type), 2, i);
								grid.addComponent(
										new SLabel(null, jrnlTransDet.getAmount()
												+ ""), 3, i);
								i++;
							}

							form.addComponent(grid);

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.PURCHASE_RETURN:
							PurchaseReturnModel purchRtrn = new PurchaseReturnDao()
									.getPurchaseReturnModel(id);

							form.addComponent(new SHTMLLabel(null, "<h2><u>"
									+ getPropertyName("purchase_return")
									+ "</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("return_no"), purchRtrn
											.getReturn_no() + ""));
							form.addComponent(new SLabel(
									getPropertyName("supplier"), purchRtrn
											.getSupplier().getName()));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(purchRtrn
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("net_amount"), purchRtrn
											.getAmount() + ""));
							grid.setColumns(12);
							grid.setRows(purchRtrn.getInventory_details_list()
									.size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("item")), 1, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("unit")), 3, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("unit_price")), 4, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("discount")), 5, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("amount")), 6, 0);
							grid.setSpacing(true);

							PurchaseReturnInventoryDetailsModel purchRtrnChild;
							itmItr = purchRtrn
									.getInventory_details_list().iterator();
							while (itmItr.hasNext()) {
								purchRtrnChild = (PurchaseReturnInventoryDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, purchRtrnChild
										.getItem().getName()), 1, i);
								grid.addComponent(
										new SLabel(null, purchRtrnChild.getQunatity()
												+ ""), 2, i);
								grid.addComponent(new SLabel(null, purchRtrnChild
										.getUnit().getSymbol()), 3, i);
								grid.addComponent(
										new SLabel(null, purchRtrnChild.getUnit_price()
												+ ""), 4, i);
								grid.addComponent(
										new SLabel(null, purchRtrnChild
												.getDiscount() + ""), 5,
										i);
								grid.addComponent(
										new SLabel(
												null,
												(purchRtrnChild.getUnit_price()
														* purchRtrnChild.getQunatity()
														- purchRtrnChild.getDiscount() + purchRtrnChild
															.getTaxAmount())
														+ ""), 6, i);
								i++;
							}

							form.addComponent(grid);
							form.addComponent(new SLabel(
									getPropertyName("comment"), purchRtrn
											.getComments()));

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.SALES_RETURN:
							SalesReturnModel salRetrn = new SalesReturnDao()
									.getSalesReturnModel(id);

							form.addComponent(new SHTMLLabel(null, "<h2><u>"
									+ getPropertyName("sales_return")
									+ "</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("credit_note_no"), salRetrn
											.getReturn_no() + ""));
							form.addComponent(new SLabel(
									getPropertyName("customer"), salRetrn
											.getCustomer().getName()));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(salRetrn
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("net_amount"), salRetrn
											.getAmount() + ""));

							grid.setColumns(12);
							grid.setRows(salRetrn.getInventory_details_list()
									.size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("item")), 1, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("unit")), 3, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("rate")), 7, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("discount")), 8, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("amount")), 9, 0);
							grid.setSpacing(true);

							SalesReturnInventoryDetailsModel saleRtrnChild;
							itmItr = salRetrn
									.getInventory_details_list().iterator();
							while (itmItr.hasNext()) {
								saleRtrnChild = (SalesReturnInventoryDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, saleRtrnChild
										.getItem().getName()), 1, i);
								grid.addComponent(
										new SLabel(null, saleRtrnChild
												.getQunatity() + ""), 2,
										i);
								grid.addComponent(new SLabel(null, saleRtrnChild
										.getUnit().getSymbol()), 3, i);
								grid.addComponent(
										new SLabel(null, saleRtrnChild.getUnit_price()
												+ ""), 7, i);
								grid.addComponent(
										new SLabel(null, saleRtrnChild
												.getDiscount() + ""), 8,
										i);
								grid.addComponent(
										new SLabel(
												null,
												(saleRtrnChild.getUnit_price()
														* (saleRtrnChild
																.getQunatity())
														- saleRtrnChild.getDiscount() + saleRtrnChild
															.getTaxAmount())
														+ ""), 9, i);
								i++;
							}

							form.addComponent(grid);
							form.addComponent(new SLabel(
									getPropertyName("comment"), salRetrn
											.getComments()));

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.SUPPLIER_PAYMENTS:
							PaymentModel suppPay = new PaymentDao()
									.getPaymentModel(id);

							form.addComponent(new SHTMLLabel(null, "<h2><u>"
									+ getPropertyName("supplier_payment")
									+ "</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("payment_no"), suppPay
											.getPayment_id() + ""));
							LedgerModel supl = ledgDao.getLedgeer(suppPay
									.getTo_account_id());
							if (supl != null)
								form.addComponent(new SLabel(
										getPropertyName("supplier"), supl
												.getName()));

							LedgerModel frm = ledgDao.getLedgeer(suppPay
									.getFrom_account_id());
							if (frm != null)
								form.addComponent(new SLabel(
										getPropertyName("from_account"), frm
												.getName()));

							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(suppPay
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("supplier_amount"),
									suppPay.getSupplier_amount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("discount"), suppPay
											.getDiscount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("payment_amount"), suppPay
											.getPayment_amount() + ""));

							form.addComponent(new SLabel(
									getPropertyName("description"), suppPay
											.getDescription()));

							form.setWidth("400");

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.CUSTOMER_PAYMENTS:
							PaymentModel custPay = new PaymentDao()
									.getPaymentModel(id);
							form.addComponent(new SHTMLLabel(null, "<h2><u>"
									+ getPropertyName("customer_receipt")
									+ "</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("receipt_no"), custPay
											.getPayment_id() + ""));
							LedgerModel cust = ledgDao.getLedgeer(custPay
									.getFrom_account_id());
							if (cust != null)
								form.addComponent(new SLabel(
										getPropertyName("customer"), cust
												.getName()));

							LedgerModel toAcc = ledgDao.getLedgeer(custPay
									.getTo_account_id());
							if (toAcc != null)
								form.addComponent(new SLabel(
										getPropertyName("to_account"), toAcc
												.getName()));

							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(custPay
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("customer_amount"),
									custPay.getSupplier_amount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("discount"), custPay
											.getDiscount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("payment_amount"), custPay
											.getPayment_amount() + ""));

							form.addComponent(new SLabel(
									getPropertyName("description"), custPay
											.getDescription()));

							form.setWidth("400");

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.BANK_ACCOUNT_PAYMENTS:
							BankAccountPaymentModel bankPayMdl = new BankAccountPaymentDao()
									.getBankAccountPaymentModel(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Bank Account Payment</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("no"),
									bankPayMdl.getBill_no() + ""));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(bankPayMdl
													.getDate())));
//							if (bankPayMdl.getCash_or_check() == 2)
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"),
//										"Cheque"));
//							else
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"), "Cash"));

							form.addComponent(new SLabel(
									getPropertyName("ref_no"), bankPayMdl
											.getRef_no()));

							form.addComponent(new SLabel(
									getPropertyName("memo"), bankPayMdl.getMemo()));

							grid.setColumns(5);
//							grid.setRows(bankPayMdl.getTransaction()
//									.getTransaction_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "To Account"),
									1, 0);
							grid.addComponent(new SLabel(null, "Amount"),2, 0);
							grid.setSpacing(true);
							grid.addComponent(new SLabel(null, "Cheque No"), 3, 0);
							grid.addComponent(new SLabel(null, "Cheque Date"), 4, 0);

							BankAccountPaymentDetailsModel bankPaydet;
							itmItr = bankPayMdl.getBank_account_payment_list().iterator();
							while (itmItr.hasNext()) {
								bankPaydet = (BankAccountPaymentDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, bankPaydet
										.getAccount().getName()), 1, i);
								grid.addComponent(
										new SLabel(null, bankPaydet.getAmount()
												+ ""), 2, i);
								grid.addComponent(
										new SLabel(null, bankPaydet.getChequeNo()
												+ ""), 3, i);
								if(bankPaydet.getCash_or_check()==SConstants.bank_account.CHEQUE||
										bankPaydet.getCash_or_check()==SConstants.bank_account.SUPPLIER)
									grid.addComponent(
											new SLabel(null, bankPaydet.getChequeDate()
													+ ""), 4, i);
								else
									grid.addComponent(
											new SLabel(null, ""
													+ ""), 4, i);
								i++;
							}

							form.addComponent(grid);

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.BANK_ACCOUNT_DEPOSITS:
							BankAccountDepositModel bankDepMdl = new BankAccountDepositDao()
									.getBankAccountDepositModel(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Bank Account Deposit</u></h2>"));

							form.addComponent(new SLabel(getPropertyName("no"),
									bankDepMdl.getBill_no() + ""));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(bankDepMdl
													.getDate())));
//							if (bankDepMdl.getCash_or_check() == 2)
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"),
//										"Cheque"));
//							else
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"), "Cash"));

							form.addComponent(new SLabel(
									getPropertyName("ref_no"), bankDepMdl
											.getRef_no()));
							form.addComponent(new SLabel(
									getPropertyName("details"), bankDepMdl.getMemo()));

							grid.setColumns(5);
							grid.setRows(bankDepMdl.getBank_account_deposit_list()
									.size() + 3);
							grid.setSpacing(true);
							
							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "From Account"),	1, 0);
							grid.addComponent(new SLabel(null, "Amount"), 2, 0);
							grid.addComponent(new SLabel(null, "Cheque No"), 3, 0);
							grid.addComponent(new SLabel(null, "Cheque Date"), 4, 0);

							BankAccountDepositDetailsModel bankDepdet;
							itmItr = bankDepMdl.getBank_account_deposit_list().iterator();
							while (itmItr.hasNext()) {
								bankDepdet = (BankAccountDepositDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, bankDepdet
										.getAccount().getName()), 1, i);
								grid.addComponent(
										new SLabel(null, bankDepdet.getAmount()
												+ ""), 2, i);
								grid.addComponent(
										new SLabel(null, bankDepdet.getChequeNo()
												+ ""), 3, i);
								if(bankDepdet.getCash_or_check()==SConstants.bank_account.CHEQUE||
										bankDepdet.getCash_or_check()==SConstants.bank_account.CUSTOMER)
									grid.addComponent(
											new SLabel(null, bankDepdet.getChequeDate()
													+ ""), 4, i);
								else
									grid.addComponent(
											new SLabel(null, ""
													+ ""), 4, i);
								i++;
							}

							form.addComponent(grid);

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.CASH_ACCOUNT_DEPOSITS:
						CashAccountDepositModel cashDepMdl = new CashAccountDepositDao()
						.getCashAccountDepositModel(id);
						
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>Cash Account Deposit</u></h2>"));
						
						form.addComponent(new SLabel(getPropertyName("no"),
								cashDepMdl.getBill_no() + ""));
						form.addComponent(new SLabel(
								getPropertyName("date"), CommonUtil
								.getUtilDateFromSQLDate(cashDepMdl
										.getDate())));
//							if (cashDepMdl.getCash_or_check() == 2)
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"),
//										"Cheque"));
//							else
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"), "Cash"));
						
						form.addComponent(new SLabel(
								getPropertyName("ref_no"), cashDepMdl
								.getRef_no()));
						form.addComponent(new SLabel(
								getPropertyName("memo"), cashDepMdl.getMemo()));
						
						grid.setColumns(5);
						grid.setRows(cashDepMdl.getCash_account_deposit_list()
								.size() + 3);
						
						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null, "From Account"),
								1, 0);
						grid.addComponent(new SLabel(null, "Amount"), 2, 0);
						grid.setSpacing(true);
						
						CashAccountDepositDetailsModel cashDepDet;
						itmItr = cashDepMdl.getCash_account_deposit_list().iterator();
						while (itmItr.hasNext()) {
							cashDepDet = (CashAccountDepositDetailsModel) itmItr
									.next();
							grid.addComponent(new SLabel(null, i + ""), 0,
									i);
							grid.addComponent(new SLabel(null, cashDepDet
									.getAccount().getName()), 1, i);
							grid.addComponent(
									new SLabel(null, cashDepDet.getAmount()
											+ ""), 2, i);
							i++;
						}
						
						form.addComponent(grid);
						
						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.CASH_ACCOUNT_PAYMENTS:
						CashAccountPaymentModel cashPayModel = new CashAccountPaymentDao()
						.getCashAccountPaymentModel(id);
						
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>Cash Account Payments</u></h2>"));
						
						form.addComponent(new SLabel(getPropertyName("no"),
								cashPayModel.getBill_no() + ""));
						form.addComponent(new SLabel(
								getPropertyName("date"), CommonUtil
								.getUtilDateFromSQLDate(cashPayModel
										.getDate())));
//							if (cashPayModel.getCash_or_check() == 2)
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"),
//										"Cheque"));
//							else
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"), "Cash"));
						
						form.addComponent(new SLabel(
								getPropertyName("ref_no"), cashPayModel
								.getRef_no()));
						form.addComponent(new SLabel(
								getPropertyName("memo"), cashPayModel.getMemo()));
						
						grid.setColumns(5);
						grid.setRows(cashPayModel.getCash_account_payment_list()
								.size() + 3);
						
						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null, "To Account"),
								1, 0);
						grid.addComponent(new SLabel(null, "Amount"), 2, 0);
						grid.setSpacing(true);
						
						CashAccountPaymentDetailsModel cashPayDet;
						itmItr = cashPayModel.getCash_account_payment_list().iterator();
						while (itmItr.hasNext()) {
							cashPayDet = (CashAccountPaymentDetailsModel) itmItr
									.next();
							grid.addComponent(new SLabel(null, i + ""), 0,
									i);
							grid.addComponent(new SLabel(null, cashPayDet
									.getAccount().getName()), 1, i);
							grid.addComponent(
									new SLabel(null, cashPayDet.getAmount()
											+ ""), 2, i);
							i++;
						}
						
						form.addComponent(grid);
						
						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
						
						
					case SConstants.CREDIT_NOTE:
						CreditNoteModel credMdl = new CreditNoteDao()
						.getCreditNoteModel(id);
						
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>Credit Note</u></h2>"));
						
						form.addComponent(new SLabel(getPropertyName("no"),
								credMdl.getBill_no() + ""));
						form.addComponent(new SLabel(
								getPropertyName("date"), CommonUtil
								.getUtilDateFromSQLDate(credMdl
										.getDate())));
//							if (cashDepMdl.getCash_or_check() == 2)
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"),
//										"Cheque"));
//							else
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"), "Cash"));
						
						form.addComponent(new SLabel(
								getPropertyName("ref_no"), credMdl
								.getRef_no()));
						form.addComponent(new SLabel(
								getPropertyName("memo"), credMdl.getMemo()));
						
						grid.setColumns(5);
						grid.setRows(credMdl.getCredit_note_list()
								.size() + 3);
						
						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null, "From Account"),
								1, 0);
						grid.addComponent(new SLabel(null, "Amount"), 2, 0);
						grid.setSpacing(true);
						
						CreditNoteDetailsModel credDetMdl;
						itmItr = credMdl.getCredit_note_list().iterator();
						while (itmItr.hasNext()) {
							credDetMdl = (CreditNoteDetailsModel) itmItr
									.next();
							grid.addComponent(new SLabel(null, i + ""), 0,
									i);
							grid.addComponent(new SLabel(null, credDetMdl
									.getAccount().getName()), 1, i);
							grid.addComponent(
									new SLabel(null, credDetMdl.getAmount()
											+ ""), 2, i);
							i++;
						}
						
						form.addComponent(grid);
						
						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.DEBIT_NOTE:
						DebitNoteModel debMdl = new DebitNoteDao().getDebitNoteModel(id);
						
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>Debit Note</u></h2>"));
						
						form.addComponent(new SLabel(getPropertyName("no"),
								debMdl.getBill_no() + ""));
						form.addComponent(new SLabel(
								getPropertyName("date"), CommonUtil
								.getUtilDateFromSQLDate(debMdl
										.getDate())));
//							if (cashPayModel.getCash_or_check() == 2)
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"),
//										"Cheque"));
//							else
//								form.addComponent(new SLabel(
//										getPropertyName("cash_cheque"), "Cash"));
						
						form.addComponent(new SLabel(
								getPropertyName("ref_no"), debMdl
								.getRef_no()));
						form.addComponent(new SLabel(
								getPropertyName("memo"), debMdl.getMemo()));
						
						grid.setColumns(5);
						grid.setRows(debMdl.getDebit_note_list()
								.size() + 3);
						
						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null, "To Account"),
								1, 0);
						grid.addComponent(new SLabel(null, "Amount"), 2, 0);
						grid.setSpacing(true);
						
						DebitNoteDetailsModel debDetMdl;
						itmItr = debMdl.getDebit_note_list().iterator();
						while (itmItr.hasNext()) {
							debDetMdl = (DebitNoteDetailsModel) itmItr
									.next();
							grid.addComponent(new SLabel(null, i + ""), 0,
									i);
							grid.addComponent(new SLabel(null, debDetMdl
									.getAccount().getName()), 1, i);
							grid.addComponent(
									new SLabel(null, debDetMdl.getAmount()
											+ ""), 2, i);
							i++;
						}
						
						form.addComponent(grid);
						
						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
						
					case SConstants.PDC_PAYMENT:
						PdcPaymentModel pdcPay = new PDCPaymentDao().getPdcPaymentModel(id);
						
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>PDC Payment</u></h2>"));
						
						form.addComponent(new SLabel(getPropertyName("no"),
								pdcPay.getBill_no() + ""));
						
						form.addComponent(new SLabel(
								getPropertyName("date"), CommonUtil
								.getUtilDateFromSQLDate(pdcPay
										.getDate())));
						
						form.addComponent(new SLabel(
								getPropertyName("memo"), pdcPay.getMemo()));
						
						grid.setColumns(5);
						grid.setRows(pdcPay.getPdc_payment_list()
								.size() + 3);
						
						grid.addComponent(new SLabel(null, "#"), 0, 0);
						grid.addComponent(new SLabel(null, "From Account"),
								1, 0);
						grid.addComponent(new SLabel(null, "TO Account"),
								2, 0);
						grid.addComponent(new SLabel(null, "Amount"), 3, 0);
						grid.setSpacing(true);
						
						PdcPaymentDetailsModel pdcPayDet;
						itmItr = pdcPay.getPdc_payment_list().iterator();
						while (itmItr.hasNext()) {
							pdcPayDet = (PdcPaymentDetailsModel) itmItr
									.next();
							grid.addComponent(new SLabel(null, i + ""), 0,
									i);
							grid.addComponent(new SLabel(null, ledgDao.getLedgerNameFromID(pdcPayDet
									.getFrom_id())), 1, i);
							grid.addComponent(new SLabel(null, ledgDao.getLedgerNameFromID(pdcPayDet
									.getTo_id())), 2, i);
							grid.addComponent(
									new SLabel(null, pdcPayDet.getAmount()
											+ ""), 3, i);
							i++;
						}
						
						form.addComponent(grid);
						
						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
						
					case SConstants.EXPENDETURE_TRANSACTION:
							PaymentDepositModel expTrans = new ExpendetureTransactionDao()
									.getExpendetureTransaction(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Expenditure Transaction</u></h2>"));

							form.addComponent(new SLabel(
									getPropertyName("bill_no"), expTrans
											.getBill_no() + ""));

							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(expTrans
													.getDate())));
							if (expTrans.getCash_or_check() == 2)
								form.addComponent(new SLabel(
										getPropertyName("cash_cheque"),
										"Cheque"));
							else
								form.addComponent(new SLabel(
										getPropertyName("cash_cheque"), "Cash"));

							form.addComponent(new SLabel(
									getPropertyName("ref_no"), expTrans
											.getRef_no()));

							form.addComponent(new SLabel(
									getPropertyName("memo"), expTrans.getMemo()));

							grid.setColumns(5);
							grid.setRows(expTrans.getTransaction()
									.getTransaction_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "From Account"),
									1, 0);
							grid.addComponent(new SLabel(null, "To Account"),
									2, 0);
							grid.addComponent(new SLabel(null, "Amount"), 3, 0);
							grid.setSpacing(true);

							TransactionDetailsModel expTransDet;
							itmItr = expTrans.getTransaction()
									.getTransaction_details_list().iterator();
							while (itmItr.hasNext()) {
								expTransDet = (TransactionDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, expTransDet
										.getFromAcct().getName()), 1, i);
								grid.addComponent(new SLabel(null, expTransDet
										.getToAcct().getName()), 2, i);
								grid.addComponent(
										new SLabel(null, expTransDet.getAmount()
												+ ""), 3, i);
								i++;
							}

							form.addComponent(grid);

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.INCOME_TRANSACTION:
							PaymentDepositModel incomeTrans = new ExpendetureTransactionDao()
									.getExpendetureTransaction(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Income Transactions</u></h2>"));

							form.addComponent(new SLabel(
									getPropertyName("bill_no"), incomeTrans
											.getBill_no() + ""));
							form.addComponent(new SLabel("Date :", CommonUtil
									.getUtilDateFromSQLDate(incomeTrans.getDate())));
							form.addComponent(new SLabel(
									getPropertyName("ref_no"), incomeTrans
											.getRef_no()));
							form.addComponent(new SLabel(
									getPropertyName("comments"), incomeTrans
											.getMemo()));

							grid.setColumns(5);
							grid.setRows(incomeTrans.getTransaction()
									.getTransaction_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "From Account"),
									1, 0);
							grid.addComponent(new SLabel(null, "To Account"),
									2, 0);
							grid.addComponent(new SLabel(null, "Amount"), 3, 0);
							grid.setSpacing(true);

							TransactionDetailsModel incomeTransDet;
							itmItr = incomeTrans.getTransaction()
									.getTransaction_details_list().iterator();
							while (itmItr.hasNext()) {
								incomeTransDet = (TransactionDetailsModel) itmItr
										.next();
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, incomeTransDet
										.getFromAcct().getName()), 1, i);
								grid.addComponent(new SLabel(null, incomeTransDet
										.getToAcct().getName()), 2, i);
								grid.addComponent(
										new SLabel(null, incomeTransDet.getAmount()
												+ ""), 3, i);
								i++;
							}

							form.addComponent(grid);

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
//					case SConstants.CONTRACTOR_PAYMENTS:
//						ContractorPaymentsUI contrPay = new ContractorPaymentsUI();
//						contrPay.setCaption(getPropertyName("contractor payments"));
//						contrPay.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID).getValue());
//						contrPay.center();
//						contrPay.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(contrPay);
//						break;
					case SConstants.TRANSPORTATION_PAYMENTS:
							TransportationPaymentModel transPayMdl = new TransportationPaymentDao()
									.getTransportationPaymentModel(id);

							if (transPayMdl.getType() == 1) {
								form.addComponent(new SHTMLLabel(
										null,
										"<h2><u>"
												+ getPropertyName("transportation_cash")
												+ "</u></h2>"));
							} else {
								form.addComponent(new SHTMLLabel(
										null,
										"<h2><u>"
												+ getPropertyName("transportation_credit")
												+ "</u></h2>"));
							}

							form.addComponent(new SLabel(
									getPropertyName("bill_no"), transPayMdl
											.getPayment_id() + ""));

							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(transPayMdl
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("transportation_amount"),
									transPayMdl.getSupplier_amount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("discount"), transPayMdl
											.getDiscount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("payment_amount"), transPayMdl
											.getPayment_amount() + ""));

							form.addComponent(new SLabel(
									getPropertyName("description"), transPayMdl
											.getDescription()));
							form.addComponent(new SLabel(
									getPropertyName("place"), transPayMdl
											.getPlace()));
							form.addComponent(new SLabel(
									getPropertyName("invoice_amount"), transPayMdl
											.getInvoiceAmount() + ""));

							form.setWidth("400");

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.EMPLOYEE_ADVANCE_PAYMENTS:
							EmployeeAdvancePaymentModel empAdvMdl = new EmployeeAdvancePaymentDao()
									.getEmployeeAdvancePaymentModel(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Employee Advance Payment</u></h2>"));
							form.addComponent(new SLabel("Payment No. :",
									empAdvMdl.getPayment_id() + ""));
							form.addComponent(new SLabel(
									getPropertyName("employee"),
									new UserManagementDao()
											.getUserNameFromLoginID(empAdvMdl
													.getLogin_id())));
							form.addComponent(new SLabel(
									getPropertyName("from_account"), ledgDao
											.getLedgerNameFromID(empAdvMdl
													.getAccount_id())));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(empAdvMdl
													.getDate())));
							form.addComponent(new SLabel(
									getPropertyName("transportation_amount"),
									empAdvMdl.getAmount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("amount"),
									empAdvMdl.getAmount() + ""));
							form.addComponent(new SLabel(
									getPropertyName("description"), empAdvMdl
											.getDescription()));

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
							break;
						case SConstants.PAYROLL_PAYMENTS:
							SalaryDisbursalModel salDisb = new SalaryDisbursalDao().getSalaryDisbursalModel(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Salary Disbursal</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("no"),
									salDisb.getId() + ""));
//							form.addComponent(new SLabel(
//									getPropertyName("employee"), salDisb
//											.getEmploy().getFirst_name()));
//							form.addComponent(new SLabel(
//									getPropertyName("salary_month"), CommonUtil
//											.getUtilDateFromSQLDate(salDisb
//													.getMonth())));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(salDisb
													.getDispursal_date())));
//							form.addComponent(new SLabel(
//									getPropertyName("advance_paid_amount"),
//									salDisb.getAdvance_payed() + ""));
//							form.addComponent(new SLabel(
//									getPropertyName("salary_amount"), salDisb
//											.getTotal_salary() + ""));

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.INVESTMENT:
						CashInvestmentModel cashInv=new CashInvestmentDao().getCashInvestmentModel(id);
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>"+getPropertyName("investment")+"</u></h2>"));
						form.addComponent(new SLabel(
								getPropertyName("no"),
								cashInv.getInvestment_no() + ""));
						form.addComponent(new SLabel(
								getPropertyName("date"),
								CommonUtil
										.getUtilDateFromSQLDate(cashInv.getDate())));
						form.addComponent(new SLabel(
								getPropertyName("amount"), cashInv
										.getAmount()+""));
						form.addComponent(new SLabel(
								getPropertyName("description"), cashInv
								.getDescription()+""));

						form.setWidth("400");

						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.COMMISSION_PURCHASE:
						CommissionPurchaseModel pmdl=new CommissionPurchaseDao().getPurchase(id);
						form.addComponent(new SHTMLLabel(null,
								"<h2><u>"+getPropertyName("commission_sales")+"</u></h2>"));
						form.addComponent(new SLabel(
								getPropertyName("purchase_no"),
								pmdl.getNumber() + ""));
						form.addComponent(new SLabel(
								getPropertyName("date"),
								CommonUtil
										.getUtilDateFromSQLDate(pmdl.getIssue_date())));
						form.addComponent(new SLabel(
								getPropertyName("supplier"), pmdl
										.getSupplier().getName()));

						form.setWidth("400");

						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.COMMISSION_SALES:
							CommissionSalesNewModel commSale = new CommissionSalesNewDao()
									.getSale(id);
							form.addComponent(new SHTMLLabel(null, "<h2><u"
									+ getPropertyName("commission_sales")
									+ "</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("sales_no"), commSale
											.getSales_number() + ""));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(commSale
													.getDate())));
							form.addComponent(new SLabel(
									getPropertyName("amount"), commSale
											.getAmount() + ""));

							grid.setColumns(12);
							grid.setRows(commSale.getCommission_sales_list()
									.size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("customer")), 1, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("unit")), 3, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("rate")), 4, 0);
							grid.addComponent(new SLabel(null,
									getPropertyName("amount")), 6, 0);
							grid.setSpacing(true);
							CommissionSalesCustomerDetailsModel commSaleDet;
							itmItr = commSale
									.getCommission_sales_list().iterator();
							while (itmItr.hasNext()) {
								commSaleDet = (CommissionSalesCustomerDetailsModel) itmItr
										.next();

								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, commSaleDet
										.getCustomer().getName()), 1, i);
								grid.addComponent(
										new SLabel(null, commSaleDet.getQunatity()
												+ ""), 2, i);
								grid.addComponent(new SLabel(null, commSaleDet
										.getUnit().getSymbol()), 3, i);
								grid.addComponent(
										new SLabel(null, commSaleDet.getUnit_price()
												+ ""), 4, i);
								grid.addComponent(
										new SLabel(
												null,
												(commSaleDet.getUnit_price()
														* commSaleDet.getQunatity()
														- commSaleDet.getDiscount_amount() + commSaleDet
															.getTax_amount())
														+ ""), 6, i);
								i++;
							}

							form.addComponent(grid);
							form.addComponent(new SLabel(
									getPropertyName("comment"), commSale
											.getComments()));


							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
					case SConstants.SUBSCRIPTION_PAYMENTS:
						SubscriptionPaymentModel subscrPay = new SubscriptionPaymentDao().getPaymentModel(id);

						form.addComponent(new SHTMLLabel(null,
								"<h2><u>"+getPropertyName("rental")+"</u></h2>"));
						LedgerModel frmLedg = ledgDao.getLedgeer(subscrPay.getFrom_account());
						if (frmLedg != null)
							form.addComponent(new SLabel(getPropertyName("customer"), frmLedg.getName()));

						LedgerModel toAccLedg = ledgDao.getLedgeer(subscrPay.getTo_account());
						
						if (toAccLedg != null)
							form.addComponent(new SLabel(getPropertyName("to_account"),toAccLedg.getName()));

						form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(subscrPay.getPayment_date())));
						form.addComponent(new SLabel(getPropertyName("payment_amount"),subscrPay.getAmount_paid() + ""));
						form.setWidth("400");

						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;
					case SConstants.COMMISSION_PAYMENTS:
						CommissionPaymentModel commPay = new CommissionPaymentDao().getPaymentModel(id);

						form.addComponent(new SHTMLLabel(null,
								"<h2><u>"+getPropertyName("commission payments")+"</u></h2>"));
						LedgerModel custLedg = ledgDao.getLedgeer(commPay.getSupplierId());
						if (custLedg != null)
							form.addComponent(new SLabel(getPropertyName("customer"), custLedg.getName()));

						LedgerModel frmAccLedg = ledgDao.getLedgeer(commPay.getFromAccount());
						
						if (frmAccLedg != null)
							form.addComponent(new SLabel(getPropertyName("to_account"),frmAccLedg.getName()));

						form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(commPay.getDate())));
						form.setWidth("400");

						popupContainer.removeAllComponents();
						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);
						break;

					case SConstants.COMMISSION_SALARY:
							CommissionSalaryModel commSalMdl = new CommissionSalaryDao()
									.getCommissionSalaryModel(id);

							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Commission Salary</u></h2>"));
							form.addComponent(new SLabel("Payment No. :",
									commSalMdl.getPayment_number() + ""));
							form.addComponent(new SLabel(
									getPropertyName("employee"),
									commSalMdl.getEmployee().getFirst_name()));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(commSalMdl
													.getDate())));
							form.addComponent(new SLabel(
									getPropertyName("month"),
									CommonUtil.getMonthName(commSalMdl.getMonth().getMonth()) + ""));
							form.addComponent(new SLabel(
									getPropertyName("payment_amount"),
									commSalMdl.getPaid_amount() + ""));

							popupContainer.removeAllComponents();
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						break;
						
//						case SConstants.RENT_PAYMENTS:
//						RentalPaymentsUI rentPay = new RentalPaymentsUI();
//						rentPay.setCaption(getPropertyName("Rental Payment"));
//						rentPay.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID).getValue());
//						rentPay.center();
//						rentPay.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(rentPay);
//						break;
//						case SConstants.TRANSPORTATION_EXPENDITUE:
//						AddExpenditure transExp = new AddExpenditure();
//						transExp.setCaption(getPropertyName("transportation expense"));
//						transExp.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID).getValue());
//						transExp.center();
//						transExp.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(transExp);
//						break;
//					case SConstants.RENTAL_TRANSACTION:
//						RentalTransactionNewUI rentlTrans = new RentalTransactionNewUI();
//						rentlTrans
//								.setCaption(getPropertyName("Rental Transaction"));
//						rentlTrans.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID).getValue());
//						rentlTrans.center();
//						rentlTrans.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(rentlTrans);
//						break;
//					case SConstants.RENTAL_PAYMENTS:
//						RentalPaymentsUI rentlPay = new RentalPaymentsUI();
//						rentlPay.setCaption(getPropertyName("Rental Payments"));
//						rentlPay.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID).getValue());
//						rentlPay.center();
//						rentlPay.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(rentlPay);
//						break;
						
					default:
						break;
					}
					} catch (Exception e) {
						e.printStackTrace();
					}
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

					switch ((Integer)item.getItemProperty(TBC_TYPE_ID).getValue()) {
					
					case SConstants.SALES:
						SalesUI sales = new SalesUI();
						sales.setCaption(getPropertyName("sales"));
						sales.getSalesNumberList().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						sales.center();
						getUI().getCurrent().addWindow(sales);
						sales.addCloseListener(closeListener);
						break;
					case SConstants.PURCHASE:
						PurchaseUI purchase = new PurchaseUI();
						purchase.setCaption(getPropertyName("purchase"));
						purchase.getPurchaseNumberList().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						purchase.center();
						getUI().getCurrent().addWindow(purchase);
						purchase.addCloseListener(closeListener);
						break;
					case SConstants.JOURNAL:
						JournalNewUI journal = new JournalNewUI();
						journal.setCaption(getPropertyName("joirnal"));
						journal.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						journal.center();
						getUI().getCurrent().addWindow(journal);
						journal.addCloseListener(closeListener);
						break;
					case SConstants.PURCHASE_RETURN:
						PurchaseReturnUI purchRetrn = new PurchaseReturnUI();
						purchRetrn.setCaption(getPropertyName("purchase return"));
						purchRetrn.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						purchRetrn.center();
						getUI().getCurrent().addWindow(purchRetrn);
						purchRetrn.addCloseListener(closeListener);
						break;
					case SConstants.SALES_RETURN:
						SalesReturnUI salesRetrn = new SalesReturnUI();
						salesRetrn.setCaption(getPropertyName("sales return"));
						salesRetrn.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						salesRetrn.center();
						getUI().getCurrent().addWindow(salesRetrn);
						salesRetrn.addCloseListener(closeListener);
						break;
					case SConstants.BANK_ACCOUNT_PAYMENTS:
						BankAccountPaymentUI bankPay = new BankAccountPaymentUI();
						bankPay.setCaption(getPropertyName("bank account payments"));
						bankPay.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						bankPay.center();
						getUI().getCurrent().addWindow(bankPay);
						bankPay.addCloseListener(closeListener);
						break;
					case SConstants.BANK_ACCOUNT_DEPOSITS:
						BankAccountDepositUI bankDep = new BankAccountDepositUI();
						bankDep.setCaption(getPropertyName("bank account deposit"));
						bankDep.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						bankDep.center();
						getUI().getCurrent().addWindow(bankDep);
						bankDep.addCloseListener(closeListener);
						break;
					case SConstants.EMPLOYEE_ADVANCE_PAYMENTS:
						EmployeeAdvancePaymentsUI emplPay = new EmployeeAdvancePaymentsUI();
						emplPay.setCaption(getPropertyName("employee advance payments"));
						emplPay.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						emplPay.center();
						emplPay.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(emplPay);
						break;
					case SConstants.PAYROLL_PAYMENTS:
						SalaryDisbursalNewUI salDis = new SalaryDisbursalNewUI();
						salDis.setCaption(getPropertyName("salary disbursal"));
						salDis.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						salDis.center();
						salDis.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(salDis);
						break;
					case SConstants.INVESTMENT:
						CashInvestmentUI cashInv = new CashInvestmentUI();
						cashInv.setCaption(getPropertyName("Cash Investment"));
						cashInv.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						cashInv.center();
						cashInv.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(cashInv);
						break;
//					case SConstants.COMMISSION_PURCHASE:
//						CommissionPurchaseUI commPurch = new CommissionPurchaseUI();
//						commPurch.setCaption(getPropertyName("Commission purchase"));
//						commPurch.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID)
//										.getValue());
//						commPurch.center();
//						commPurch.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(commPurch);
//						break;
//					case SConstants.COMMISSION_SALES:
//						CommissionSalesNewUI comSales = new CommissionSalesNewUI();
//						comSales.setCaption(getPropertyName("Commission sales"));
//						comSales.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID)
//										.getValue());
//						comSales.center();
//						comSales.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(comSales);
//						break;
					case SConstants.RENT_PAYMENTS:
						RentalPaymentsUI rentPay = new RentalPaymentsUI();
						rentPay.setCaption(getPropertyName("Rental Payment"));
						rentPay.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						rentPay.center();
						rentPay.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(rentPay);
						break;
					case SConstants.SUBSCRIPTION_PAYMENTS:
						SubscriptionPayment subsPay = new SubscriptionPayment();
						subsPay.setCaption(getPropertyName("Subscription Payment"));
						subsPay.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						subsPay.center();
						subsPay.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(subsPay);
						break;
//					case SConstants.COMMISSION_PAYMENTS:
//						CommissionPayment commPay = new CommissionPayment();
//						commPay.setCaption(getPropertyName("Commission Payment"));
//						commPay.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID)
//										.getValue());
//						commPay.center();
//						commPay.addCloseListener(closeListener);
//						getUI().getCurrent().addWindow(commPay);
//						break;
					case SConstants.TRANSPORTATION_EXPENDITUE:
						AddExpenditure transExp = new AddExpenditure();
						transExp.setCaption(getPropertyName("transportation expense"));
						transExp.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						transExp.center();
						transExp.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(transExp);
						break;
					case SConstants.RENTAL_TRANSACTION:
						RentalTransactionNewUI rentlTrans = new RentalTransactionNewUI();
						rentlTrans.setCaption(getPropertyName("Rental Transaction"));
						rentlTrans.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						rentlTrans.center();
						rentlTrans.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(rentlTrans);
						break;
					case SConstants.RENTAL_PAYMENTS:
						RentalPaymentsUI rentlPay = new RentalPaymentsUI();
						rentlPay.setCaption(getPropertyName("Rental Payments"));
						rentlPay.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						rentlPay.center();
						rentlPay.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(rentlPay);
						break;
					case SConstants.COMMISSION_SALARY:
						CommissionSalaryUI commSal = new CommissionSalaryUI();
						commSal.setCaption(getPropertyName("Commission Salary"));
						commSal.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
										.getValue());
						commSal.center();
						commSal.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(commSal);
						break;
					case SConstants.CASH_ACCOUNT_DEPOSITS:
						CashAccountDepositUI cashDep = new CashAccountDepositUI();
						cashDep.setCaption(getPropertyName("Cash Deposit"));
						cashDep.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
								.getValue());
						cashDep.center();
						cashDep.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(cashDep);
						break;
					case SConstants.CASH_ACCOUNT_PAYMENTS:
						CashAccountPaymentUI cashAccPay = new CashAccountPaymentUI();
						cashAccPay.setCaption(getPropertyName("Cash Payment"));
						cashAccPay.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
								.getValue());
						cashAccPay.center();
						cashAccPay.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(cashAccPay);
						break;
						
					case SConstants.CREDIT_NOTE:
						CreditNoteUI cred = new CreditNoteUI();
						cred.setCaption(getPropertyName("Credit Note"));
						cred.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
								.getValue());
						cred.center();
						cred.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(cred);
						break;
					case SConstants.DEBIT_NOTE:
						DebitNoteUI debNote = new DebitNoteUI();
						debNote.setCaption(getPropertyName("Debit Note"));
						debNote.getBillNoFiled().setValue(
								(Long) item.getItemProperty(TBC_ID)
								.getValue());
						debNote.center();
						debNote.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(debNote);
						break;
						
					case SConstants.PDC_PAYMENT:
						PDCPaymentUI pdcPay = new PDCPaymentUI();
						pdcPay.setCaption(getPropertyName("PDC Payment"));
//						pdcPay.getBillNoFiled().setValue(
//								(Long) item.getItemProperty(TBC_ID)
//								.getValue());
						pdcPay.center();
						pdcPay.addCloseListener(closeListener);
						getUI().getCurrent().addWindow(pdcPay);
						break;
					default:
						break;
					}
				}
			}
			});
		return mainPanel;
	}

	protected void generateReport() {
		try {

			List reportList = new ArrayList();

			LedgerModel ledger =ledgDao
					.getLedgeer((Long) ledgertSelect.getValue());

			if (isValid()) {

				reportList = daoObj.getLedgerView(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						getOfficeID(), (Long) ledgertSelect.getValue(),(Long) departmentCombo.getValue(),(Long) divisionCombo.getValue());

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
				
				double opening_bal = daoObj.getOpeningBalance(
								CommonUtil.getSQLDateFromUtilDate(fromDate
										.getValue()), (Long) ledgertSelect
										.getValue());

//				List resList=new ArrayList();

				double cr_ttl = 0, dr_ttl = 0,balance=opening_bal;
				AcctReportMainBean obj;
				double inward=0,outward=0;
				Iterator it = reportList.iterator();
				while (it.hasNext()) {
					obj = (AcctReportMainBean) it.next();
					inward=0;
					outward=0;
					if (obj.getAmount_type().equals("Cr")) {
						cr_ttl += obj.getAmount();
						inward=0;
						outward=obj.getAmount();
						
					} else {
						dr_ttl += obj.getAmount();
						outward=0;
						inward=obj.getAmount();
					}
					balance+=inward-outward;
					obj.setBalance(Math.abs(balance));
					obj.setDispaly_name(getTransactionTypeName(obj.getTransaction_type()));
				}

				if (reportList != null && reportList.size() > 0) {


					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue());
					params.put("ToDate", toDate.getValue());
					params.put("ToDateString", CommonUtil.formatDateToCommonFormat(toDate.getValue()));
					params.put("LedgerName", ledger.getName());
					params.put("Opening Balance", roundNumber(Math.abs(opening_bal)));
					params.put("Current Balance",
							roundNumber(Math.abs(ledger.getCurrent_balance())));
					params.put("Balance as on",Math.abs(
							roundNumber(opening_bal)+roundNumber(dr_ttl)-roundNumber(cr_ttl)));
					params.put("Office", ledger.getOffice().getName());
					params.put("Organization", ledger.getOffice()
							.getOrganization().getName());
					params.put("inward_total", Math.abs(dr_ttl));
					
					params.put("NAME_LABEL", getPropertyName("name")+" :");
					params.put("OPENING_BALANCE_LABEL", getPropertyName("opening_balance")+" :");
					params.put("BALANCE_AS_ON_LABEL", getPropertyName("balance_as_on")+" :");
					params.put("OFFICE_LABEL", getPropertyName("office")+" :");
					params.put("ORGANIZATION_LABEL", getPropertyName("organization")+" :");
					params.put("CURRENT_BALANCE_LABEL", getPropertyName("current_balance")+" :");
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("TRANSACTION_TYPE_LABEL", getPropertyName("transaction_type"));
					params.put("INVOICE_NO_LABEL", getPropertyName("invoice_no"));
					params.put("FROM_OR_TO_LABEL", getPropertyName("from_or_to"));
					params.put("INWARDS_LABEL", getPropertyName("inwards"));
					params.put("OUTWARDS_LABEL", getPropertyName("outwards"));
					params.put("BALANCE_LABEL", getPropertyName("balance"));
					params.put("COMMENTS_LABEL", getPropertyName("comments"));
					params.put("TOTAL_LABEL", getPropertyName("total"));
					
					
					
					params.put("outward_total", Math.abs(cr_ttl));
					report.setJrxmlFileName("LedgerView");
					report.setReportFileName("Ledger View");
					report.setReportTitle("Statement Of Account - "+ledgertSelect.getItemCaption(ledgertSelect.getValue()));
					report.setReportSubTitle(getPropertyName("from")+"  : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ getPropertyName("to")+"  : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeSelect
							.getItemCaption(officeSelect.getValue()));
					report.createReport(reportList, params);

					reportList.clear();

				} else {
					SNotification.show(getPropertyName("no_data _available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void showReport() {
		try {

			table.removeAllItems();
			table.setVisibleColumns(allColumns);

			LedgerModel ledger = ledgDao.getLedgeer((Long) ledgertSelect
					.getValue());

			if (isValid()) {

				List reportList = daoObj.getLedgerView(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						getOfficeID(), (Long) ledgertSelect.getValue(),(Long) departmentCombo.getValue(),(Long) divisionCombo.getValue());


				double opening_bal = /*ledger.getOpening_balance()+*/
						daoObj.getOpeningBalance(
								CommonUtil.getSQLDateFromUtilDate(fromDate
										.getValue()), (Long) ledgertSelect
										.getValue());

				if (reportList.size() > 0) {
					
//					if(counter%2==0)
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
//					else
//						Collections.sort(reportList,
//								new Comparator<AcctReportMainBean>() {
//									@Override
//									public int compare(
//											final AcctReportMainBean object1,
//											final AcctReportMainBean object2) {
//										return object2.getDate().compareTo(
//												object1.getDate());
//									}
//								});
				AcctReportMainBean obj;
				int ct = 0;
				long id=0;
				double inward=0,outward=0,balance=opening_bal;
				String accountName="";
				Iterator it = reportList.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();
						inward=0;
						outward=0;
						accountName= obj.getFrom_or_to();;
						if(obj.getAmount_type().equals("Cr")){
							inward=0;
							outward=obj.getAmount();
						}else{
							inward=obj.getAmount();
							outward=0;
						}
						
						balance+=inward-outward;
//						id=daoObj.getIdFromTransaction(obj.getId(),obj.getTransaction_type());
						
						table.addItem(new Object[] { ct + 1, obj.getInvoiceId(),obj.getTransaction_type(),
							CommonUtil.formatDateToDDMMYYYY(obj.getDate()),	getTransactionTypeName(obj.getTransaction_type()),obj.getInvoiceId(),obj.getBill_no(),accountName,inward,outward,Math.abs(balance),obj.getComments() }, ct);

						ct++;

					} 
				reportList.clear();
			}else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
			}
		}
			calculateTableTotals();
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.setVisibleColumns(visibleColumns);
	}
	
	private void loadDepartments(long orgId){
		try {
		List departmentList=new ArrayList();
		departmentList.add(0, new DepartmentModel((long)0, getPropertyName("all")));
		departmentList.addAll(new DepartmentDao().getDepartments(orgId));
		
		SCollectionContainer dep=SCollectionContainer.setList(departmentList
				, "id");
		departmentCombo.setContainerDataSource(dep);
		departmentCombo.setItemCaptionPropertyId("name");
		departmentCombo.setValue((long)0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void loadDivisions(long orgId){
		try {
		List divisionList=new ArrayList();
		divisionList.add(0, new DivisionModel((long)0, getPropertyName("all")));
		divisionList.addAll(new DivisionDao().getDivisions(orgId));
		
		SCollectionContainer div=SCollectionContainer.setList(divisionList, "id");
		divisionCombo.setContainerDataSource(div);
		divisionCombo.setItemCaptionPropertyId("name");
		divisionCombo.setValue((long)0);
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	private String getTransactionTypeName(int transaction_type) {
		String str="";
		switch (transaction_type) {
		
		case SConstants.SALES:
			str="SALES";
			break;
		case SConstants.PURCHASE:
			str="PURCHASE";
			break;
		case SConstants.JOURNAL:
			str="JOURNAL";
			break;
		case SConstants.PURCHASE_RETURN:
			str="PURCHASE RETURN";
			break;
		case SConstants.SALES_RETURN:
			str="SALES RETURN";
			break;
		case SConstants.SUPPLIER_PAYMENTS:
			str="SUPPLIER PAYMENTS";
			break;
		case SConstants.CUSTOMER_PAYMENTS:
			str="CUSTOMER PAYMENTS";
			break;
		case SConstants.BANK_ACCOUNT_PAYMENTS:
			str="BANK ACCOUNT PAYMENTS";
			break;
		case SConstants.BANK_ACCOUNT_DEPOSITS:
			str="BANK ACCOUNT DEPOSITS";
			break;
		case SConstants.EXPENDETURE_TRANSACTION:
			str="EXPENDETURE TRANSACTION";
			break;
		case SConstants.INCOME_TRANSACTION:
			str="INCOME TRANSACTION";
			break;
		case SConstants.CONTRACTOR_PAYMENTS:
			str="CONTRACTOR PAYMENTS";
			break;
		case SConstants.TRANSPORTATION_PAYMENTS:
			str="TRANSPORTATION PAYMENTS";
			break;
		case SConstants.EMPLOYEE_ADVANCE_PAYMENTS:
			str="EMPLOYEE ADVANCE PAYMENTS";
			break;
		case SConstants.PAYROLL_PAYMENTS:
			str="PAYROLL PAYMENTS";
			break;
		case SConstants.INVESTMENT:
			str="INVESTMENT";
			break;
		case SConstants.COMMISSION_PURCHASE:
			str="COMMISSION PURCHASE";
			break;
		case SConstants.COMMISSION_SALES:
			str="COMMISSION SALES";
			break;
		case SConstants.RENT_PAYMENTS:
			str="RENT PAYMENTS";
			break;
		case SConstants.SUBSCRIPTION_PAYMENTS:
			str="SUBSCRIPTION PAYMENTS";
			break;
		case SConstants.COMMISSION_PAYMENTS:
			str="COMMISSION PAYMENTS";
			break;
		case SConstants.TRANSPORTATION_EXPENDITUE:
			str="TRANSPORTATION EXPENDITUE";
			break;
		case SConstants.RENTAL_TRANSACTION:
			str="RENTAL TRANSACTION";
			break;
		case SConstants.RENTAL_PAYMENTS:
			str="RENTAL PAYMENTS";
			break;
		case SConstants.COMMISSION_SALARY:
			str="COMMISSION SALARY";
			break;
		case SConstants.CASH_ACCOUNT_DEPOSITS:
			str="CASH DEPOSITS";
			break;
		case SConstants.CASH_ACCOUNT_PAYMENTS:
			str="CASH PAYMENTS";
			break;
		case SConstants.CREDIT_NOTE:
			str="CREDIT NOTE";
			break;
		case SConstants.DEBIT_NOTE:
			str="DEBIT NOTE";
			break;
		case SConstants.PDC_PAYMENT:
			str="PDC PAYMENT";
			break;
		default:
			str="";
			break;
		}
		return str;
	}

	private void calculateTableTotals() {
		Iterator it = table.getItemIds().iterator();
		Item itm;

		double sal_ttl = 0, cash_ttl = 0;
		while (it.hasNext()) {
			itm = table.getItem(it.next());
			sal_ttl += (Double) itm.getItemProperty(TBC_INWARD).getValue();
			cash_ttl += (Double) itm.getItemProperty(TBC_OUTWARD).getValue();
		}
		table.setColumnFooter(TBC_INWARD, asString(roundNumber(sal_ttl)));
		table.setColumnFooter(TBC_OUTWARD, asString(roundNumber(cash_ttl)));
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

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
