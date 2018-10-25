package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.dao.CashFlowReportDao;
import com.inventory.subscription.bean.SubscriberLedgerBean;
import com.inventory.subscription.bean.SubscriptionPendingReportBean;
import com.inventory.subscription.dao.SubscriberLedgerReportDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.dao.SubscriptionPendingReportDao;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.subscription.ui.SubscriptionPayment;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
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
 *         Dec 10, 2013
 */
public class SubscriptionPendingReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;
	SRadioButton accountRadio;

	SubscriptionPendingReportDao dao;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_CUSTOMER = "Customer";
	static String TBC_INCOME = "Income";
	static String TBC_SDATE = "Rental Date";
	static String TBC_RDATE = "Return Date";
	static String TBC_TO_PAY = "To Pay";
	static String TBC_AMOUNT = "Amount Paid";
	static String TBC_CREDIT = "Credit";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	STable table;

	Object[] allColumns;

	SHorizontalLayout popupContainer;

	@SuppressWarnings({ "serial", "unchecked" })
	@Override
	public SPanel getGUI() {

		ofcDao = new OfficeDao();
		ledDao = new LedgerDao();

		try {
			dao=new SubscriptionPendingReportDao();
			allColumns = new Object[] { TBC_SN, TBC_CUSTOMER,TBC_INCOME,TBC_SDATE,TBC_RDATE,TBC_TO_PAY,TBC_AMOUNT, TBC_CREDIT,TBC_BALANCE };
			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();
			accountRadio = new SRadioButton(getPropertyName("subscriber"), 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long) 2);
			accountRadio.setHorizontal(true);
			setSize(1200, 400);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.CENTER);
			table.addContainerProperty(TBC_INCOME, String.class, null,getPropertyName("income"), null, Align.CENTER);
			table.addContainerProperty(TBC_SDATE, String.class, null,getPropertyName("rental_date"), null, Align.CENTER);
			table.addContainerProperty(TBC_RDATE, String.class, null,getPropertyName("return_date"), null, Align.CENTER);
			table.addContainerProperty(TBC_TO_PAY, Double.class, null,getPropertyName("to_pay"), null, Align.RIGHT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CREDIT, Double.class, null,getPropertyName("credit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_SDATE, (float) 1);
			table.setColumnExpandRatio(TBC_RDATE, (float) 1);
			table.setColumnExpandRatio(TBC_CUSTOMER ,( float) 1.5);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_TO_PAY, 1);
			table.setColumnExpandRatio(TBC_CREDIT, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);

			table.setVisibleColumns(allColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("730");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_BALANCE , "0.0");
			table.setColumnFooter(TBC_AMOUNT, "0.0");
			table.setColumnFooter(TBC_CREDIT, "0.0");
			table.setColumnFooter(TBC_CUSTOMER, getPropertyName("total"));

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(getPropertyName("organization"), 200,new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());
			ledgertSelect = new SComboField(getPropertyName("customer"), 200,ledDao.getAllSuppliers((Long) officeSelect.getValue()),"id", "name");
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
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), getWorkingDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(accountRadio);
			formLayout.addComponent(ledgertSelect);
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

			loadSubscriberIncome(0,(Long)officeSelect.getValue());
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if((Long)accountRadio.getValue()==1){
							loadSubscriberExpenditure(0,(Long)officeSelect.getValue());
							table.setColumnHeader(TBC_CUSTOMER, getPropertyName("expendeture"));
							table.removeAllItems();
						}
						else if((Long)accountRadio.getValue()==2){
							loadSubscriberIncome(0,(Long)officeSelect.getValue());
							table.setColumnHeader(TBC_CUSTOMER, getPropertyName("customer"));
							table.setColumnHeader(TBC_INCOME, getPropertyName("income"));
							ledgertSelect.setCaption(getPropertyName("customer"));
							table.removeAllItems();
						}
						else{
							loadSubscriberTransportation(0,(Long)officeSelect.getValue());
							table.setColumnHeader(TBC_CUSTOMER, getPropertyName("transportation_supplier"));
							table.setColumnHeader(TBC_INCOME,getPropertyName( "vehicle"));
							ledgertSelect.setCaption(getPropertyName("transportation_supplier"));
							table.removeAllItems();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<center><h2><u>"+getPropertyName("subscriber_ledger")+"</u></h2></center>"));
							form.addComponent(new SLabel(accountRadio.getItemCaption((Long)accountRadio.getValue()), new LedgerDao().getLedgerNameFromID((Long)ledgertSelect.getValue())));
							form.addComponent(new SLabel(getPropertyName("customer"),item.getItemProperty(TBC_CUSTOMER).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("rental_item"),item.getItemProperty(TBC_INCOME).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("rental_date"),item.getItemProperty(TBC_SDATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("return_date"),item.getItemProperty(TBC_RDATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("paid_amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("credit"),item.getItemProperty(TBC_CREDIT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("balance"),item.getItemProperty(TBC_BALANCE).getValue().toString()));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
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
								bic = SCollectionContainer.setList(ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect.getValue()), "id");
							} catch (Exception e) {
								e.printStackTrace();
							}
							officeSelect.setContainerDataSource(bic);
							officeSelect.setItemCaptionPropertyId("name");
						}
					});

			officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							
							try {
								accountRadio.setValue(null);
								accountRadio.setValue((long)2);
							} 
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			ledgertSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							table.removeAllItems();
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
			List reportList=dao.getPendingList((Long)ledgertSelect.getValue(),
												CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
												CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
												(Long)officeSelect.getValue());
			if(reportList.size()>0) {
				Iterator itr=reportList.iterator();
				SubscriptionInModel simdl=null;
				while (itr.hasNext()) {
					simdl=(SubscriptionInModel)itr.next();
					double total=0,paid=0,credit=0,balance=0;
					paid=dao.getTotalPaid(simdl.getId(),
							CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
					credit=dao.getTotalCredit(simdl.getId(),
							CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
					if(simdl.getSubscription().getSpecial()!=1) {
						total=calculateSubscriptionAmount(calculateDifferenceInDays(simdl), simdl.getRate(), simdl.getPeriod());
						balance=total-paid;
					}
					else {
						total=0;
						balance=0;
					}
					String rdate="";
					if(simdl.getReturn_date()!=null)
						rdate=simdl.getReturn_date().toString();
					else
						rdate="";
					table.addItem(new Object[] {
							table.getItemIds().size()+1,
							new LedgerDao().getLedgerNameFromID(simdl.getSubscriber()),
							simdl.getSubscription().getName(),
							simdl.getSubscription_date().toString(),
							rdate,
							total,
							paid,
							credit,
							balance},table.getItemIds().size()+1);
				}
			}
			else {
				SNotification.show("No Data Available",Type.WARNING_MESSAGE);
			}
			table.setVisibleColumns(allColumns);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void generateReport() {
		try {
			List reportList=new ArrayList();
			
			List resultList=dao.getPendingList((Long)ledgertSelect.getValue(),
												CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
												CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
												(Long)officeSelect.getValue());
			
			if(resultList.size()>0) {
				Iterator itr=resultList.iterator();
				SubscriptionInModel simdl=null;
				while (itr.hasNext()) {
					simdl=(SubscriptionInModel)itr.next();
					double total=0,paid=0,credit=0,balance=0;
					
					paid=dao.getTotalPaid(simdl.getId(),
											CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
											CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
					credit=dao.getTotalCredit(simdl.getId(),
							CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
					if(simdl.getSubscription().getSpecial()!=1) {
						total=calculateSubscriptionAmount(calculateDifferenceInDays(simdl), simdl.getRate(), simdl.getPeriod());
						balance=total-paid;
					}
						
					else {
						total=0;
						balance=0;
					}
						
					String rdate="";
					if(simdl.getReturn_date()!=null)
						rdate=simdl.getReturn_date().toString();
					else
						rdate="";
					reportList.add(new SubscriptionPendingReportBean(new LedgerDao().getLedgerNameFromID(simdl.getSubscriber()),
																	simdl.getSubscription().getName(),
																	simdl.getSubscription_date().toString(),
																	rdate,
																	total,
																	paid,
																	credit,
																	balance));
				}
			}
			
			if(reportList.size()>0){
				HashMap<String, Object> params = new HashMap<String, Object>();
				report.setJrxmlFileName("SubscriptionPendingReport");
				report.setReportFileName("Rental Pending Report");
				report.setReportTitle("Rental Pending Report");
				if((Long)accountRadio.getValue()==2){
					params.put("CUSTOMER", "Customer");
					params.put("RENTAL", "Income");
				}
				else if((Long)accountRadio.getValue()==3){
					params.put("CUSTOMER", "Supplier");
					params.put("RENTAL", "Vehicle");
				}
				
				String subTitle = "";
				params.put("subscriber", new LedgerDao().getLedgerNameFromID((Long)ledgertSelect.getValue()));
				params.put("FromDate", CommonUtil.formatDateToDDMMYYYY(fromDate.getValue()));
				params.put("ToDate", CommonUtil.formatDateToDDMMYYYY(toDate.getValue()));
				subTitle += getOfficeName();
				report.setReportSubTitle(subTitle);
				report.setReportType(toInt(reportType.getValue().toString()));
				report.setIncludeHeader(true);
				report.setOfficeName(getOfficeName());
				report.createReport(reportList, params);
				reportList.clear();
			}
			else{
				SNotification.show("No Data Available",Type.WARNING_MESSAGE);
			}
		} 
		catch (Exception e) {
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberExpenditure(long id,long ofc){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllExpenditureSubscriptions(ofc));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			ledgertSelect.setContainerDataSource(bic);
			ledgertSelect.setItemCaptionPropertyId("name");
			ledgertSelect.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberIncome(long id,long ofc){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.add(0,new LedgerModel(0, "All"));
			list.addAll(dao.getAllIncomeSubscriptions(ofc));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			ledgertSelect.setContainerDataSource(bic);
			ledgertSelect.setItemCaptionPropertyId("name");
			ledgertSelect.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberTransportation(long id,long ofc){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.add(0,new LedgerModel(0, "All"));
			list.addAll(dao.getAllTransportationSubscriptions(ofc));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			ledgertSelect.setContainerDataSource(bic);
			ledgertSelect.setItemCaptionPropertyId("name");
			ledgertSelect.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int calculateDifferenceInDays(SubscriptionInModel simdl){
		int days=0;
		Date date=null;
		Calendar calendar=Calendar.getInstance();
		long fromTime,toTime;
		try{
			date=simdl.getSubscription_date();
			calendar.setTime(date);
			fromTime=date.getTime();
			if(simdl.getReturn_date()!=null) {
				if(toDate.getValue().getTime()>simdl.getReturn_date().getTime()) {
					toTime=simdl.getReturn_date().getTime();
				}
				else {
					toTime=toDate.getValue().getTime();
				}
			}
			else {
				toTime=toDate.getValue().getTime();
			}
			days=(int)(((toTime-fromTime)/(24*60*60*1000)));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return days;
	}

//	public Date findIssueDate(long sid){
//		Date date=null;
//		SubscriptionInModel simdl;
//		try{
//			simdl=new SubscriptionInDao().getSubscriptionInModel(sid);
//			date=simdl.getSubscription_date();
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		return date;
//	}
	
	public double calculateSubscriptionAmount(int diffDays,double rate,long per){
		double amount=0;
		int period=Integer.parseInt(asString(per));
		try{
			switch (period) {
			case 1: amount=diffDays*rate;
					break;
					
			case 2: amount=(diffDays/7)*rate;
					break;		

			case 3: amount=(diffDays/30)*rate;
					break;
					
			case 4: amount=(diffDays/365)*rate;
					break;		
			default:
					break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return amount;
	}
}
