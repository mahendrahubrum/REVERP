package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.subscription.bean.ConsolidatedSubscriptionLedgerReportBean;
import com.inventory.subscription.dao.ConsolidatedSubscriptionLedgerReportDao;
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
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 *         Feb 11, 2014
 */
public class ConsolidatedSubscriptionLedgerReport extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;
	
	private SComboField organizationCombo;
	private SComboField officeCombo;
	private SRadioButton accountRadio;
	SDateField fromDate, toDate;
	private SNativeSelect reportType;
	private SButton generateButton;
	private SButton showButton;
	private Report report;
	
	private ConsolidatedSubscriptionLedgerReportDao dao;

	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_PARTICULARS = "Customer";
	static String TBC_OPENING = "Opening";
	static String TBC_CASH = "Cash";
	static String TBC_CREDIT = "Credit";
	static String TBC_BALANCE = "Balance";
	static String TBC_CUR_BALANCE = "Balance On "+ CommonUtil.formatDateToDDMMYYYY(new Date());
	SHorizontalLayout mainLay;
	STable table;
	Object[] allColumns;
	Object[] visibleColumns;
	SHorizontalLayout popupContainer;
	OfficeDao ofcDao;
	LedgerDao ledDao;

	@SuppressWarnings({ "deprecation", "serial" })
	@Override
	public SPanel getGUI() {

		try {
			dao=new ConsolidatedSubscriptionLedgerReportDao();
			ofcDao = new OfficeDao();
			ledDao = new LedgerDao();

			allColumns = new String[] {TBC_SN, TBC_ID,TBC_PARTICULARS, TBC_OPENING, TBC_CASH, TBC_CREDIT, TBC_BALANCE, TBC_CUR_BALANCE };
			visibleColumns = new String[] {TBC_SN, TBC_PARTICULARS, TBC_OPENING, TBC_CASH, TBC_CREDIT, TBC_BALANCE, TBC_CUR_BALANCE };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1200, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null,getPropertyName("id"), null, Align.LEFT);
			table.addContainerProperty(TBC_PARTICULARS, String.class, null,getPropertyName("customer"), null, Align.LEFT);
			table.addContainerProperty(TBC_OPENING, Double.class, null,getPropertyName("opening"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CASH, Double.class, null,getPropertyName("cash"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CREDIT, Double.class, null,getPropertyName("credit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CUR_BALANCE, Double.class, null,getPropertyName("balance_on"), null, Align.RIGHT);
			

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_PARTICULARS, 2);
			table.setColumnExpandRatio(TBC_CREDIT, 1);
			table.setColumnExpandRatio(TBC_CASH, 1);
			table.setColumnExpandRatio(TBC_OPENING, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);
			table.setColumnExpandRatio(TBC_CUR_BALANCE, (float) 1.3);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("830");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_PARTICULARS, getPropertyName("total"));
			calculateTableTotals();

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationCombo = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationCombo.setValue(getOrganizationID());
			officeCombo = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationCombo
							.getValue()), "id", "name");
			officeCombo.setValue(getOfficeID());
			accountRadio = new SRadioButton(getPropertyName("type"),100,SConstants.rentalList,"key","value");
			accountRadio.setValue((long)2);
			if (isSuperAdmin() || isSystemAdmin()) {
				organizationCombo.setEnabled(true);
				officeCombo.setEnabled(true);
			} 
			else {
				organizationCombo.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeCombo.setEnabled(true);
				} else
					officeCombo.setEnabled(false);
			}
			formLayout = new SFormLayout();
			formLayout.setSpacing(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			fromDate = new SDateField(getPropertyName("from_date"), 150,getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationCombo);
			formLayout.addComponent(officeCombo);
			formLayout.addComponent(accountRadio);
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
							form.addComponent(new SLabel(accountRadio.getItemCaption(accountRadio.getValue()),itm.getItemProperty(TBC_PARTICULARS).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("opening_balance"),itm.getItemProperty(TBC_OPENING).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("cash"),itm.getItemProperty(TBC_CASH).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("credit"),itm.getItemProperty(TBC_CREDIT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("current_balance"),itm.getItemProperty(TBC_CUR_BALANCE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("balance"),itm.getItemProperty(TBC_BALANCE).getValue().toString()));
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
			
			final Action actionDelete = new Action(getPropertyName("edit"));
			
			
			table.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
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
			
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if((Long)accountRadio.getValue()==2){
						table.setColumnHeader(TBC_PARTICULARS, getPropertyName("customer"));
					}
					else if((Long)accountRadio.getValue()==3){
						table.setColumnHeader(TBC_PARTICULARS, getPropertyName("transportation_supplier"));
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

			organizationCombo.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(ofcDao.getAllOfficeNamesUnderOrg((Long) organizationCombo.getValue()), "id");
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					officeCombo.setContainerDataSource(bic);
					officeCombo.setItemCaptionPropertyId("name");
				}
			});

			officeCombo.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					
				}
			});

			accountRadio.addListener(new Property.ValueChangeListener() {
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

	@SuppressWarnings("rawtypes")
	protected void showReport() {
		try {

			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			calculateTableTotals();
			if (isValid()) {
				List resultList=dao.getConsolidatedLedgerReport((Long)accountRadio.getValue(), 
																(Long)officeCombo.getValue(), 
																CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
																CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
				if(resultList.size()>0) {
					ConsolidatedSubscriptionLedgerReportBean bean;
					Iterator itr=resultList.iterator();
					while (itr.hasNext()) {
						bean=(ConsolidatedSubscriptionLedgerReportBean)itr.next();
						table.addItem(new Object[] {
								table.getItemIds().size()+1,
								bean.getLedgerId(),
								bean.getName(),
								bean.getOpening(),
								bean.getCash(),
								bean.getCredit(),
								bean.getBalance(),
								bean.getCurrent()},table.getItemIds().size()+1);
					}
				}
				else {
					SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
				}
			} 
			calculateTableTotals();
			table.setVisibleColumns(visibleColumns);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	protected void generateReport() {
		try {

			if (isValid()) {
				List reportList = new ArrayList();
				reportList=dao.getConsolidatedLedgerReport((Long)accountRadio.getValue(), 
															(Long)officeCombo.getValue(), 
															CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
															CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
				if(reportList.size()>0) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("FromDate", fromDate.getValue().toString());
					params.put("ToDate", CommonUtil.formatDateToDDMMYYYY(toDate.getValue()));
					params.put("TODAY",CommonUtil.formatDateToDDMMYYYY(new Date()));
					if((Long)accountRadio.getValue()==2){
						params.put("PART_HEAD", "Customer");
					}
					else if((Long)accountRadio.getValue()==3){
						params.put("PART_HEAD", "Transporation Supplier");
					}
					params.put("LedgerName", "");
					params.put("Balance", 0.0);
					params.put("OpeningBalance", 0.0);
					params.put("Office", officeCombo.getItemCaption(officeCombo.getValue()));
					params.put("Organization", organizationCombo.getItemCaption(organizationCombo.getValue()));
					report.setJrxmlFileName("ConsolidatedSubscriptionLedgerReport");
					report.setReportFileName("Consolidated Rental Ledger Report");
					report.setReportTitle("Consolidated Rental Ledger Report");
					report.setReportSubTitle("From  : "
							+ CommonUtil.formatDateToCommonFormat(fromDate
									.getValue())
							+ "   To  : "
							+ CommonUtil.formatDateToCommonFormat(toDate
									.getValue()));
					report.setIncludeHeader(true);
					report.setReportType((Integer) reportType.getValue());
					report.setOfficeName(officeCombo.getItemCaption(officeCombo.getValue()));
					report.createReport(reportList, params);
					reportList.clear();
				}
				else {
					SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (accountRadio.getValue() == null || accountRadio.getValue().equals("")) {
			setRequiredError(accountRadio, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(accountRadio, null, false);

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),true);
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
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
		return (comboField.getValue() != null && !comboField.getValue().toString().equals("0") && !comboField.getValue().equals(""));
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
			cash_ttl += (Double) itm.getItemProperty(TBC_CASH).getValue();
			bal1 += (Double) itm.getItemProperty(TBC_BALANCE).getValue();
			bal2 += (Double) itm.getItemProperty(TBC_CUR_BALANCE).getValue();
		}
		table.setColumnFooter(TBC_OPENING, asString(roundNumber(open)));
		table.setColumnFooter(TBC_CREDIT, asString(roundNumber(sal_ttl)));
		table.setColumnFooter(TBC_CASH, asString(roundNumber(cash_ttl)));
		table.setColumnFooter(TBC_BALANCE, asString(roundNumber(bal1)));
		table.setColumnFooter(TBC_CUR_BALANCE, asString(roundNumber(bal2)));
	}

}
