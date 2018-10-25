package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.TranspotationDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.dao.SalesManMapDao;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.CustomerSupplierLedgerReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 *         Feb 11, 2014
 */
public class AllCustomerSupplierLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;

	private CustomerSupplierLedgerReportDao daoObj;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField salesManSelect;
	private SComboField customerSelect;
	private SRadioButton customerOrSupplier;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_PARTICULARS = "Customer";
	static String TBC_CREDIT = "Credit";
	static String TBC_DEBIT = "Debit";
	static String TBC_RETURN = "Return";
	static String TBC_BALANCE = "Balance";
	static String TBC_OPENING = "Opening";
	static String TBC_CUR_BALANCE = "Balance On ";

	SHorizontalLayout mainLay;

	STable table;

	Object[] allColumns;
	Object[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	LedgerDao ledDao;
	TranspotationDao transDao;

	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes", "serial" })
	@Override
	public SPanel getGUI() {

		try {

			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();
			transDao=new TranspotationDao();

			
			
			allColumns = new String[] { TBC_SN, TBC_ID, TBC_PARTICULARS, TBC_CREDIT, TBC_DEBIT, TBC_RETURN, TBC_BALANCE, TBC_CUR_BALANCE, TBC_OPENING };
			visibleColumns = new String[] { TBC_SN, TBC_PARTICULARS, TBC_OPENING, TBC_CREDIT, TBC_DEBIT, TBC_RETURN, TBC_BALANCE, TBC_CUR_BALANCE };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1200, 430);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 980, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,getPropertyName("customer"), null, Align.CENTER);
			table.addContainerProperty(TBC_CREDIT, Double.class, null,getPropertyName("credit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_DEBIT, Double.class, null,getPropertyName("debit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CUR_BALANCE, Double.class, null,getPropertyName("balance_on")+" "+CommonUtil.formatDateToDDMMYYYY(getWorkingDate()), null, Align.RIGHT);
			table.addContainerProperty(TBC_OPENING, Double.class, null,getPropertyName("opening"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_CREDIT, 1);
			table.setColumnExpandRatio(TBC_DEBIT, 1);
			table.setColumnExpandRatio(TBC_RETURN, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);
			table.setColumnExpandRatio(TBC_OPENING, 1);
			table.setColumnExpandRatio(TBC_CUR_BALANCE, (float) 1.3);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("810");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_PARTICULARS, getPropertyName("total"));
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
			
			salesManSelect = new SComboField(getPropertyName("sales_man"), 200,null
					, "id",
					"first_name");
			salesManSelect.setInputPrompt(getPropertyName("select"));
			loadSalesMan();
			
			customerSelect=new SComboField("Customer",200);
			
			List rentList = new ArrayList();
			rentList.add(new KeyValue((long) 1, getPropertyName("customers")));
			rentList.add(new KeyValue((long) 2, getPropertyName("suppliers")));
//			rentList.add(new KeyValue((long) 3, getPropertyName("general")));
			customerOrSupplier = new SRadioButton(getPropertyName("type"),100,rentList,"key","value");
			customerOrSupplier.setImmediate(true);
			
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

			daoObj = new CustomerSupplierLedgerReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(salesManSelect);
			formLayout.addComponent(customerOrSupplier);
			formLayout.addComponent(customerSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(generateButton);
			buttonLayout.addComponent(showButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("consolidated_ledger")+"</u></h2>"));
							form.addComponent(new SLabel(customerOrSupplier.getItemCaption(customerOrSupplier.getValue()),itm.getItemProperty(TBC_PARTICULARS).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("opening_balance"),roundNumber((Double)itm.getItemProperty(TBC_OPENING).getValue())+""));
							form.addComponent(new SLabel(getPropertyName("credit"),roundNumber((Double)itm.getItemProperty(TBC_CREDIT).getValue())+""));
							form.addComponent(new SLabel(getPropertyName("cash"),roundNumber((Double)itm.getItemProperty(TBC_DEBIT).getValue())+""));
							form.addComponent(new SLabel(getPropertyName("return"),roundNumber((Double)itm.getItemProperty(TBC_RETURN).getValue())+""));
							form.addComponent(new SLabel(getPropertyName("current_balance"),roundNumber((Double)itm.getItemProperty(TBC_CUR_BALANCE).getValue())+""));
							form.addComponent(new SLabel(getPropertyName("balance"),roundNumber((Double)itm.getItemProperty(TBC_BALANCE).getValue())+""));
							form.addComponent(new SLabel(getPropertyName("date"),toDate.getValue().toString()));
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
			
			salesManSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					loadLedgers();
				}
			});
			
			customerOrSupplier.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadLedgers();
				}
			});
			customerOrSupplier.setValue((long)1);
			
			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
						
						showReport();
				}
			});

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
						generateReport();
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
						e.printStackTrace();
					}
					officeSelect.setContainerDataSource(bic);
					officeSelect.setItemCaptionPropertyId("name");

				}
			});

			officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					loadSalesMan();
				}
			});

			customerOrSupplier.addValueChangeListener(new Property.ValueChangeListener() {
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

	protected void loadSalesMan() {
		try {
			List list=new SalesManMapDao().getUsers(
					(Long) officeSelect.getValue(), 0);
			
			list.add(0,new UserModel((long)0,"All"));
			
			SCollectionContainer	custContainer = SCollectionContainer.setList(list, "id");
			salesManSelect.setContainerDataSource(custContainer);
			salesManSelect.setItemCaptionPropertyId("first_name");
			salesManSelect.setValue((long)0);
		} catch (Exception e) {
		}
		
	}

	protected void loadLedgers() {
		try{
			List list=new ArrayList();
			if((Long)customerOrSupplier.getValue()==1){
				table.setColumnHeader(TBC_PARTICULARS, getPropertyName("customer"));
				if (salesManSelect.getValue() != null&&(Long)salesManSelect.getValue() !=0) 
					list.addAll(ledDao.getAllCustomersUnderSalesMan(getOfficeID(),(Long)salesManSelect.getValue()));
				else
					list.addAll(ledDao.getAllCustomers(getOfficeID()));
				list.add(0,new LedgerModel((long)0,"----- "+getPropertyName("all")+" -----"));
				customerSelect.setCaption(getPropertyName("customer"));
			}
			else if((Long)customerOrSupplier.getValue()==2){
				table.setColumnHeader(TBC_PARTICULARS, getPropertyName("supplier"));
				if (salesManSelect.getValue() != null&&(Long)salesManSelect.getValue() !=0) 
					list.addAll(ledDao.getAllSuppliersUnderSalesMan(getOfficeID(),(Long)salesManSelect.getValue()));
				else
					list.addAll(ledDao.getAllSuppliers(getOfficeID()));
				list.add(0,new LedgerModel((long)0,"----- "+getPropertyName("all")+" -----"));
				customerSelect.setCaption(getPropertyName("supplier"));
			}
			customerSelect.setContainerDataSource(SCollectionContainer.setList(list, "id"));
			customerSelect.setItemCaptionPropertyId("name");
			customerSelect.setValue((long)0);
			
			}catch(Exception e){
				e.printStackTrace();
			}
	}

	@SuppressWarnings("rawtypes")
	protected void showReport() {
		try {

			table.removeAllItems();
			calculateTableTotals();

			if (isValid()) {

				List lst = null;

				long salesMan=0;
				if(salesManSelect.getValue()!=null)
					salesMan=(Long)salesManSelect.getValue();
				
				if ((Long)customerOrSupplier.getValue()==1) {

					lst = daoObj.getCustomerLedgerReport(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
														CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
														(Long) officeSelect.getValue(),(Long)customerSelect.getValue(),salesMan);

					
					if(lst!=null && lst.size()>0){
					table.setVisibleColumns(allColumns);

					int ct = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();
						
						/*double opening_bal = 0;
						
						opening_bal=new LedgerDao().getOpeningBalance(
								CommonUtil.getSQLDateFromUtilDate(fromDate
										.getValue()), obj.getId());*/
						
						table.addItem(new Object[] { ct + 1, 
												obj.getId(),
												obj.getParticulars(),
												roundNumber(obj.getAmount()), 
												roundNumber(obj.getPayed()),
												roundNumber(obj.getReturned()), 
												roundNumber(obj.getBalance()),
												roundNumber(obj.getBalance()+obj.getOpening_balance()),
												roundNumber(obj.getOpening_balance()) }, ct);
						ct++;
					}

					table.setVisibleColumns(visibleColumns);
					} 
					else 
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);

				} else if ((Long)customerOrSupplier.getValue()==2) {

					lst = daoObj.getSupplierLedgerReport(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
														CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
														(Long) officeSelect.getValue(),(Long)customerSelect.getValue());

					if(lst!=null&&lst.size()>0){
					table.setVisibleColumns(allColumns);
					int ct = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();
						
						/*double opening_bal = 0;
						opening_bal=new LedgerDao().getOpeningBalanceOfSupplier(
								CommonUtil.getSQLDateFromUtilDate(fromDate
										.getValue()), obj.getId());*/
						
						table.addItem(new Object[] { ct + 1, 
													obj.getCustomer(),
													obj.getParticulars(),
													roundNumber(obj.getAmount()),
													roundNumber(obj.getPayed()),
													roundNumber(obj.getReturned()),
													roundNumber(obj.getBalance()),
													roundNumber(obj.getBalance()+obj.getOpening_balance()),
													roundNumber(obj.getOpening_balance()) }, ct);
						ct++;

					}

					table.setVisibleColumns(visibleColumns);
					} 
					else 
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
					
				} else {

					lst = daoObj
							.getTransportationReport(
									CommonUtil.getSQLDateFromUtilDate(fromDate
											.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()),
									(Long) officeSelect.getValue(),(Long)customerSelect.getValue());
					if(lst!=null&&lst.size()>0){
						
					table.setVisibleColumns(allColumns);

					int ct = 0;
					AcctReportMainBean obj;
					Iterator it = lst.iterator();
					while (it.hasNext()) {
						obj = (AcctReportMainBean) it.next();

						table.addItem(
								new Object[] { ct + 1, obj.getParticulars(),
										obj.getAmount(), obj.getPayed(),
										obj.getReturned(), obj.getBalance(),
										obj.getCurrent_balance(),
										obj.getOpening_balance() }, ct);

						ct++;

					}

					table.setVisibleColumns(visibleColumns);
					
				} else {
					SNotification.show(
							getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}

				}

				calculateTableTotals();

				lst.clear();

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	protected void generateReport() {
		try {

			if (isValid()) {

				List reportList = new ArrayList();
				long salesMan=0;
				if(salesManSelect.getValue()!=null)
					salesMan=(Long)salesManSelect.getValue();
				
				if ((Long)customerOrSupplier.getValue()==1) {

					reportList = daoObj.getCustomerLedgerReport(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
																(Long) officeSelect.getValue(),(Long)customerSelect.getValue(),salesMan);

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("ToDate", CommonUtil.formatDateToDDMMYYYY(toDate.getValue()));
						params.put("TODAY",CommonUtil.formatDateToDDMMYYYY(new Date()));
						params.put("PART_HEAD", getPropertyName("customer"));
						params.put("LedgerName", "");
						params.put("Balance", 0.0);
						params.put("OpeningBalance", 0.0);
						params.put("Office", officeSelect.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect.getItemCaption(organizationSelect.getValue()));

						params.put("REPORT_TITLE_LABEL", getPropertyName("consolidated_ledger_report"));
						params.put("SL_NO_LABEL", getPropertyName("sl_no"));
						params.put("OPENING_BALANCE_LABEL", getPropertyName("opening_balance"));
						params.put("CREDIT_LABEL", getPropertyName("credit"));
						params.put("CASH_LABEL", getPropertyName("cash"));
						params.put("RETURN_LABEL", getPropertyName("return"));
						params.put("BALANCE_SELECTED_PERIOD_LABEL", getPropertyName("balance_on_selected_period"));
						params.put("BALANCE_AS_ON", getPropertyName("balance_as_on"));
						params.put("TOTAL_LABEL", getPropertyName("total"));
						params.put("office_label", getPropertyName("office"));
						params.put("organization_label", getPropertyName("organization"));
						
						
						
						report.setJrxmlFileName("ConsolidatedLedgerReport");
						report.setReportFileName("Consolidated Ledger Report");
						String sub=getPropertyName("from")+" : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue())
								+ "   "+getPropertyName("to")+"  : "
								+ CommonUtil.formatDateToCommonFormat(toDate
										.getValue());
						if((Long)salesManSelect.getValue()!=0)
							sub+="\t Salesman : "+salesManSelect.getItemCaption(salesManSelect.getValue());
						report.setReportSubTitle(sub);
						report.setIncludeHeader(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);

						reportList.clear();

					} else {
						SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
					}
				} else if ((Long)customerOrSupplier.getValue()==2) {
					reportList = daoObj
							.getSupplierLedgerReport(
									CommonUtil.getSQLDateFromUtilDate(fromDate
											.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()),
									(Long) officeSelect.getValue(),(Long)customerSelect.getValue());

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("ToDate", CommonUtil
								.formatDateToDDMMYYYY(toDate.getValue()));
						params.put("TODAY",
								CommonUtil.formatDateToDDMMYYYY(new Date()));
						params.put("PART_HEAD", getPropertyName("supplier"));
						params.put("LedgerName", "");
						params.put("Balance", 0.0);
						params.put("OpeningBalance", 0.0);
						params.put("Office", officeSelect
								.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						
						params.put("REPORT_TITLE_LABEL", getPropertyName("consolidated_ledger_report"));
						params.put("SL_NO_LABEL", getPropertyName("sl_no"));
						params.put("OPENING_BALANCE_LABEL", getPropertyName("opening_balance"));
						params.put("CREDIT_LABEL", getPropertyName("credit"));
						params.put("CASH_LABEL", getPropertyName("cash"));
						params.put("RETURN_LABEL", getPropertyName("return"));
						params.put("BALANCE_SELECTED_PERIOD_LABEL", getPropertyName("balance_on_selected_period"));
						params.put("BALANCE_AS_ON", getPropertyName("balance_as_on"));
						params.put("TOTAL_LABEL", getPropertyName("total"));
						params.put("office_label", getPropertyName("office"));
						params.put("organization_label", getPropertyName("organization"));
						
						report.setJrxmlFileName("ConsolidatedLedgerReport");
						report.setReportFileName("Consolidated Ledger Report");
						
						report.setReportSubTitle(getPropertyName("from")+" : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue())
								+ "   "+getPropertyName("to")+"  : "
								+ CommonUtil.formatDateToCommonFormat(toDate
										.getValue()));
						report.setIncludeHeader(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);

						reportList.clear();

					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				} else {
					reportList = daoObj
							.getTransportationReport(
									CommonUtil.getSQLDateFromUtilDate(fromDate
											.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDate
													.getValue()),
									(Long) officeSelect.getValue(),(Long)customerSelect.getValue());

					if (reportList != null && reportList.size() > 0) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("FromDate", fromDate.getValue().toString());
						params.put("ToDate", CommonUtil
								.formatDateToDDMMYYYY(toDate.getValue()));
						params.put("TODAY",
								CommonUtil.formatDateToDDMMYYYY(new Date()));
						params.put("PART_HEAD", getPropertyName("supplier"));
						params.put("LedgerName", "");
						params.put("Balance", 0.0);
						params.put("OpeningBalance", 0.0);
						params.put("Office", officeSelect
								.getItemCaption(officeSelect.getValue()));
						params.put("Organization", organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						params.put("REPORT_TITLE_LABEL", getPropertyName("consolidated_ledger_report"));
						params.put("SL_NO_LABEL", getPropertyName("sl_no"));
						params.put("OPENING_BALANCE_LABEL", getPropertyName("opening_balance"));
						params.put("CREDIT_LABEL", getPropertyName("credit"));
						params.put("CASH_LABEL", getPropertyName("cash"));
						params.put("RETURN_LABEL", getPropertyName("return"));
						params.put("BALANCE_SELECTED_PERIOD_LABEL", getPropertyName("balance_on_selected_period"));
						params.put("BALANCE_AS_ON", getPropertyName("balance_as_on"));
						params.put("TOTAL_LABEL", getPropertyName("total"));
						params.put("office_label", getPropertyName("office"));
						params.put("organization_label", getPropertyName("organization"));
						
						
						report.setJrxmlFileName("ConsolidatedLedgerReportTransportation");
						report.setReportFileName("Consolidated Ledger Report");
						
						report.setReportSubTitle(getPropertyName("from")+" : "
								+ CommonUtil.formatDateToCommonFormat(fromDate
										.getValue())
								+ "   "+getPropertyName("to")+"  : "
								+ CommonUtil.formatDateToCommonFormat(toDate
										.getValue()));
						report.setIncludeHeader(true);
						report.setReportType((Integer) reportType.getValue());
						report.setOfficeName(officeSelect
								.getItemCaption(officeSelect.getValue()));
						report.createReport(reportList, params);

						reportList.clear();

					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (customerOrSupplier.getValue() == null
				|| customerOrSupplier.getValue().equals("")) {
			setRequiredError(customerOrSupplier,
					getPropertyName("invalid_selection"), true);
			customerOrSupplier.focus();
			ret = false;
		} else
			setRequiredError(customerOrSupplier, null, false);

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

	public void calculateTableTotals() {
		Iterator it = table.getItemIds().iterator();
		Item itm;
		double sal_ttl = 0, cash_ttl = 0, ret_ttl = 0, open = 0, bal1 = 0, bal2 = 0;
		while (it.hasNext()) {
			itm = table.getItem(it.next());
			open += (Double) itm.getItemProperty(TBC_OPENING).getValue();
			sal_ttl += (Double) itm.getItemProperty(TBC_CREDIT).getValue();
			cash_ttl += (Double) itm.getItemProperty(TBC_DEBIT).getValue();
			ret_ttl += (Double) itm.getItemProperty(TBC_RETURN).getValue();
			bal1 += (Double) itm.getItemProperty(TBC_BALANCE).getValue();
			bal2 += (Double) itm.getItemProperty(TBC_CUR_BALANCE).getValue();
		}
		table.setColumnFooter(TBC_OPENING, asString(roundNumber(open)));
		table.setColumnFooter(TBC_CREDIT, asString(roundNumber(sal_ttl)));
		table.setColumnFooter(TBC_DEBIT, asString(roundNumber(cash_ttl)));
		table.setColumnFooter(TBC_RETURN, asString(roundNumber(ret_ttl)));
		table.setColumnFooter(TBC_BALANCE, asString(roundNumber(bal1)));
		table.setColumnFooter(TBC_CUR_BALANCE, asString(roundNumber(bal2)));
	}

}
