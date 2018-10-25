package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.ContractorDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.payment.dao.PaymentDao;
import com.inventory.payment.model.PaymentModel;
import com.inventory.payment.ui.ContractorPaymentsUI;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.ContractorLedgerReportDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.WorkOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.WorkOrderModel;
import com.inventory.sales.ui.SalesNewUI;
import com.inventory.sales.ui.WorkOrderUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
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

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class ContractorsLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout,mainHorizontalLayout;
	private SFormLayout formLayout;
	private SVerticalLayout mainLayout;
	

	private SButton generateButton,show;

	private Report report;

	private ContractorLedgerReportDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;
	ContractorDao contrDao;
	OfficeDao ofcDao;
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_WORK = "Work Order";
	static String TBC_CASH = "Cash";
	static String TBC_BALANCE = "Balance";
	STable table;
	private String[] allColumns;
	private String[] visibleColumns;
	SHorizontalLayout popupContainer;
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {
			allColumns = new String[] { TBC_SN, TBC_ID, TBC_DATE,
					TBC_WORK, TBC_CASH, TBC_BALANCE };
			visibleColumns = new String[] { TBC_SN, TBC_DATE,
					TBC_WORK, TBC_CASH, TBC_BALANCE };
			contrDao = new ContractorDao();
			ofcDao = new OfficeDao();
			popupContainer = new SHorizontalLayout();
			setSize(1000, 350);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");
			table = new STable(null, 600, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_WORK, Double.class, null,getPropertyName("work_order"), null, Align.LEFT);
			table.addContainerProperty(TBC_CASH, Double.class, null,getPropertyName("cash"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.LEFT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_WORK, 1);
			table.setColumnExpandRatio(TBC_CASH, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSelectable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));
			
			

			
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
			ledgertSelect = new SComboField(
					getPropertyName("contractor"),
					200,
					contrDao.getAllActiveContractorNamesWithLedgerID((Long) officeSelect
							.getValue()), "id", "name");
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
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainHorizontalLayout=new SHorizontalLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new ContractorLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			show=new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(show);
			formLayout.addComponent(buttonLayout);
			mainHorizontalLayout.addComponent(formLayout);
			mainHorizontalLayout.addComponent(table);
			mainHorizontalLayout.addComponent(popupContainer);
			mainLayout.addComponent(mainHorizontalLayout);
			mainLayout.setMargin(true);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				show.click();
			}
		};

		final Action actionDelete = new Action("Edit");
		
		table.addActionHandler(new Handler() {
			
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				try{
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						double cash=(Double)item.getItemProperty(TBC_CASH).getValue();
						double work=(Double)item.getItemProperty(TBC_WORK).getValue();
						if(cash!=0){
							ContractorPaymentsUI option=new ContractorPaymentsUI();
							option.setCaption("Contractor Payment");
							option.loadPaymentNo((Long)item.getItemProperty(TBC_ID).getValue());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
						if(work!=0){
							WorkOrderUI option=new WorkOrderUI();
							option.setCaption(getPropertyName("work_order"));
							option.loadWorkOrders((Long)item.getItemProperty(TBC_ID).getValue());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
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
						Item item = table.getItem(table.getValue());
						double cash=(Double)item.getItemProperty(TBC_CASH).getValue();
						double work=(Double)item.getItemProperty(TBC_WORK).getValue();
						long id = (Long) item.getItemProperty(TBC_ID).getValue();
						if(cash!=0){
							PaymentModel mdl=new PaymentDao().getPaymentModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("contractor_payment")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("payment_no"),mdl.getPayment_id()+""));
							form.addComponent(new SLabel(getPropertyName("contractor"),new LedgerDao().getLedgerNameFromID(mdl.getFrom_account_id())));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("details"),mdl.getDescription()));
							form.addComponent(new SLabel(getPropertyName("amount"),mdl.getPayment_amount()+ ""));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
						if(work!=0){
							WorkOrderModel mdl=new WorkOrderDao().getWorkOrder(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("worK_order")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("work_order"),mdl.getWork_order_number()+""));
							form.addComponent(new SLabel(getPropertyName("contractor"),mdl.getContractor().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("net_amount"),mdl.getAmount() + ""));
							form.addComponent(new SLabel(getPropertyName("details"),mdl.getComments() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
//							grid.setRows(mdl.getInventory_details_list().size() + 3);
							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("quantity")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")), 3,0);
							grid.addComponent(new SLabel(null, getPropertyName("rate")), 4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),5, 0);
							grid.setSpacing(true);
							int i = 1;
							SalesInventoryDetailsModel invObj;
//							Iterator itr = mdl.getInventory_details_list().iterator();
//							while(itr.hasNext()){
//								invObj=(SalesInventoryDetailsModel)itr.next();
//								grid.addComponent(new SLabel(null, i + ""),	0, i);
//								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
//								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2, i);
//								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol()), 3, i);
//								grid.addComponent(new SLabel(null, invObj.getUnit_price() + ""), 4,	i);
//								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity()
//																	- invObj.getDiscount_amount() 
//																	+ invObj.getTax_amount())+ ""), 5, i);
//								i++;
//							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), mdl.getComments()));
							form.setStyleName("grid_max_limit");
							popupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					}
				}
				catch(Exception e){
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

		show.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					showReport();
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
							contrDao.getAllActiveContractorNamesWithLedgerID((Long) officeSelect
									.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ledgertSelect.setContainerDataSource(bic);
				ledgertSelect.setItemCaptionPropertyId("name");

			}
		});

		mainPanel.setContent(mainLayout);

		return mainPanel;
	}

	protected void generateReport() {
		try {

			LedgerModel ledger = new LedgerDao()
					.getLedgeer((Long) ledgertSelect.getValue());

			if (isValid()) {

				List lst = daoObj.getCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) ledgertSelect.getValue());
				List reportList = new ArrayList();

				double opening_bal = daoObj.getOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

				if (opening_bal != 0)
					reportList.add(new AcctReportMainBean("Opening Balance",
							CommonUtil.getSQLDateFromUtilDate(fromDate
									.getValue()), 0, opening_bal));

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

				double bal = opening_bal;
				Iterator it = lst.iterator();
				AcctReportMainBean obj;
				while (it.hasNext()) {
					obj = (AcctReportMainBean) it.next();

					bal += obj.getAmount() - obj.getPayed();
					obj.setBalance(bal);
					reportList.add(obj);
				}

				if (reportList != null && reportList.size() > 0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", toDate.getValue().toString());
					params.put("LedgerName", ledger.getName());
					params.put("Balance",
							roundNumber(ledger.getCurrent_balance()));
					params.put("Office", ledger.getOffice().getName());
					params.put("Organization", ledger.getOffice()
							.getOrganization().getName());

										
					report.setJrxmlFileName("ContractorLedgerReport");
					report.setReportFileName("Contractor Ledger Report");
					
					params.put("REPORT_TITLE_LABEL", getPropertyName("contractor_ledger_report"));
					params.put("SL_NO_LABEL", getPropertyName("sl_no"));
					params.put("DATE_LABEL", getPropertyName("date"));
					params.put("WORK_ORDER_LABEL", getPropertyName("work_order"));
					params.put("CASH_LABEL", getPropertyName("cash"));
					params.put("BALANCE_LABEL", getPropertyName("balance"));
					params.put("name_label", getPropertyName("name"));
					params.put("organization_label", getPropertyName("organization"));
					params.put("balance_label", getPropertyName("balance"));
					params.put("office_label", getPropertyName("office"));
					
					
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
	
	@SuppressWarnings("unchecked")
	protected void showReport() {
		try {
			table.removeAllItems();
			LedgerModel ledger = new LedgerDao()
					.getLedgeer((Long) ledgertSelect.getValue());

			if (isValid()) {

				List lst = daoObj.showCustomerLedgerReport(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) ledgertSelect.getValue());
				double opening_bal = daoObj.getOpeningBalance(
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						(Long) ledgertSelect.getValue());

//				opening_bal += ledger.getOpening_balance();

//				opening_bal += ledger.getOpening_balance();
				table.setVisibleColumns(allColumns);
				if (opening_bal != 0)
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							(long)0,
							fromDate.getValue().toString(),
							"Opening Balance",
							(double)0,
							opening_bal},table.getItemIds().size()+1);
				
				if (lst.size() > 0) {
					Collections.sort(lst, new Comparator<AcctReportMainBean>() {
						@Override
						public int compare(final AcctReportMainBean object1,
								final AcctReportMainBean object2) {
							return object1.getDate().compareTo(
									object2.getDate());
						}
					});
					
					double bal = opening_bal;
					Iterator it = lst.iterator();
					AcctReportMainBean obj;
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();
						bal += obj.getAmount() - obj.getPayed();
						obj.setBalance(bal);
						if(obj.getParticulars().equals("Work Order")){
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									obj.getNumber(),
									obj.getDate().toString(),
									obj.getAmount(),
									(double)0,
									obj.getBalance()},table.getItemIds().size()+1);
						}
						else{
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									obj.getNumber(),
									obj.getDate().toString(),
									(double)0,
									obj.getPayed(),
									obj.getBalance()},table.getItemIds().size()+1);
						}
						
					}
				}
				else {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
				table.setVisibleColumns(visibleColumns);
				
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

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
