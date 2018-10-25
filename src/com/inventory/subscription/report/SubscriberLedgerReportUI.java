package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.reports.dao.CashFlowReportDao;
import com.inventory.subscription.bean.SubscriberLedgerBean;
import com.inventory.subscription.dao.SubscriberLedgerReportDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
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
public class SubscriberLedgerReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;
	private SButton showButton;

	private Report report;
	SRadioButton accountRadio;

	SubscriberLedgerReportDao dao;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	static String TBC_SN = "#";
	static String TBC_ID = "ID";
	static String TBC_SID = "SID";
	static String TBC_DATE = "Date";
	static String TBC_TYPE = "Income";
	static String TBC_AMOUNT = "Paid Amount";
	static String TBC_CREDIT = "Credit";
	static String TBC_PERIOD_BAL = "Period Balance";
	static String TBC_BALANCE = "Balance";

	SHorizontalLayout mainLay;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	STable table;

	Object[] allColumns;
	Object[] visibleColumns;

	SHorizontalLayout popupContainer;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		ofcDao = new OfficeDao();
		ledDao = new LedgerDao();

		try {
			dao=new SubscriberLedgerReportDao();
			allColumns = new Object[] { TBC_SN, TBC_ID, TBC_SID,TBC_DATE,TBC_TYPE,TBC_AMOUNT, TBC_CREDIT,TBC_PERIOD_BAL, TBC_BALANCE };
			visibleColumns = new Object[]{ TBC_SN, TBC_DATE,TBC_TYPE,TBC_AMOUNT,TBC_CREDIT, TBC_PERIOD_BAL, TBC_BALANCE };

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();
			accountRadio = new SRadioButton(getPropertyName("customer"), 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long) 2);
			accountRadio.setHorizontal(true);
			setSize(1200, 400);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_SID, Long.class, null, TBC_SID, null,Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_TYPE, String.class, null,getPropertyName("income"), null, Align.CENTER);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,getPropertyName("paid_amount"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CREDIT, Double.class, null,getPropertyName("credit"), null, Align.RIGHT);
			table.addContainerProperty(TBC_PERIOD_BAL, Double.class, null,getPropertyName("period_balance"), null, Align.RIGHT);
			table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("balance"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 0.7);
			table.setColumnExpandRatio(TBC_TYPE, 2);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			table.setColumnExpandRatio(TBC_CREDIT, 1);
			table.setColumnExpandRatio(TBC_PERIOD_BAL, 1);
			table.setColumnExpandRatio(TBC_BALANCE, 1);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("730");

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_PERIOD_BAL,"0.0");
			table.setColumnFooter(TBC_BALANCE , "0.0");
			table.setColumnFooter(TBC_AMOUNT, "0.0");
			table.setColumnFooter(TBC_CREDIT, "0.0");
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));

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
			formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			fromDate = new SDateField(getPropertyName("from_date"), 150,getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,getDateFormat(), getWorkingDate());

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
							table.setColumnHeader(TBC_TYPE, getPropertyName("expendeture"));
							table.removeAllItems();
						}
						else if((Long)accountRadio.getValue()==2){
							loadSubscriberIncome(0,(Long)officeSelect.getValue());
							table.setColumnHeader(TBC_TYPE, getPropertyName("income"));
							ledgertSelect.setCaption(getPropertyName("customer"));
							table.removeAllItems();
						}
						else{
							loadSubscriberTransportation(0,(Long)officeSelect.getValue());
							table.setColumnHeader(TBC_TYPE, getPropertyName("vehicle"));
							ledgertSelect.setCaption(getPropertyName("transportation_supplier"));
							table.removeAllItems();
						}
					}
					catch(Exception e){
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

//			table.addActionHandler(new Action.Handler() {
//				@Override
//				public Action[] getActions(final Object target,
//						final Object sender) {
//					return new Action[] { actionDelete };
//				}
//
//				@SuppressWarnings("static-access")
//				@Override
//				public void handleAction(final Action action,
//						final Object sender, final Object target) {
//					try{
//						if (table.getValue() != null) {
//							Item item = table.getItem(table.getValue());
//							SubscriptionPayment option = new SubscriptionPayment();
//							option.setCaption("Rental Payment");
//							SubscriptionPaymentModel mdl=new CashFlowReportDao().getSubscriptionPaymentModel((Long) item.getItemProperty(TBC_ID).getValue());
//							SubscriptionInModel simdl=new SubscriptionPaymentDao().getInModel(mdl.getId());
//							option.loadAccountCombo(simdl.getAccount_type());
//							if(simdl.getAvailable()==1){
//								option.loadrentCombo(1);
//								option.reloadSubscriptionInCombo(simdl.getId(),simdl.getAccount_type());
//							}
//							else if(simdl.getAvailable()==2){
//								option.loadrentCombo(2);
//								option.reloadSubscriptionOutCombo(simdl.getId(),simdl.getAccount_type());
//							}
//							else{
//								option.loadrentCombo(1);
//								option.reloadSubscriptionInCombo(simdl.getId(),simdl.getAccount_type());
//							}
//							option.center();
//							getUI().getCurrent().addWindow(option);
//							option.addCloseListener(closeListener);
//						}
//					}
//					catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//
//			});

			table.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<center><h2><u>"+getPropertyName("subscriber_ledger")+"</u></h2></center>"));
							form.addComponent(new SLabel(accountRadio.getItemCaption((Long)accountRadio.getValue()), new LedgerDao().getLedgerNameFromID((Long)ledgertSelect.getValue())));
							form.addComponent(new SLabel(getPropertyName("rental_item"),item.getItemProperty(TBC_TYPE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("payment_date"),item.getItemProperty(TBC_DATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("paid_amount"),item.getItemProperty(TBC_AMOUNT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("credit_amount"),item.getItemProperty(TBC_CREDIT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("period_balance"),item.getItemProperty(TBC_PERIOD_BAL).getValue().toString()));
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

	protected void showReport() {
		try {
			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			table.setColumnFooter(TBC_PERIOD_BAL,"0.0");
			table.setColumnFooter(TBC_BALANCE , "0.0");
			table.setColumnFooter(TBC_AMOUNT, "0.0");
			table.setColumnFooter(TBC_CREDIT, "0.0");
			double openingBalance=0;
			List resultList=dao.getLedgerReport((Long)ledgertSelect.getValue(),
												CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
												CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
												(Long)officeSelect.getValue());
			openingBalance=dao.getOpeningBalance((Long)ledgertSelect.getValue(),
												CommonUtil.getSQLDateFromUtilDate(new Date(fromDate.getValue().getTime()-(24*60*60))),
												(Long)officeSelect.getValue());
			if(resultList.size()>0){
				SubscriptionPaymentModel spmdl=null;
				Iterator itr=resultList.iterator();
				double period=0,balance=0,total=0;
				balance=openingBalance;
				while (itr.hasNext()) {
					spmdl= (SubscriptionPaymentModel) itr.next();
					String name="";	
					double credit=0,paid=0;
					if(spmdl.getPay_credit()==0){
						if(spmdl.getCredit_transaction()!=0){
							paid=spmdl.getAmount_paid();
							name=dao.getSubscriptionName(spmdl.getId());
							TransactionDetailsModel tdm;
							TransactionModel transaction=new SubscriptionPaymentDao().getTransaction(spmdl.getCredit_transaction());
							for(int i=0;i<transaction.getTransaction_details_list().size();i++){
								tdm=(TransactionDetailsModel)transaction.getTransaction_details_list().get(i);
								credit+=tdm.getAmount();
							}
						}
						else {
							credit=0;
							name="Cash Payment for "+dao.getSubscriptionName(spmdl.getId());
							paid=spmdl.getAmount_paid();
						}
						
					}
					else if(spmdl.getPay_credit()==1){
						name="Credit Payment for "+dao.getSubscriptionName(spmdl.getId());
						credit=spmdl.getAmount_paid();
						paid=0;
					}
					balance+=(paid-credit);
					period+=(paid-credit);
					table.addItem(new Object[]{
							table.getItemIds().size()+1,
							spmdl.getId(),
							dao.getSubscriptionInId(spmdl.getId()),
							spmdl.getPayment_date().toString(),
							name,
							paid,
							credit,
							period,
							balance},table.getItemIds().size()+1);
					total+=credit;
				}
				table.setColumnFooter(TBC_PERIOD_BAL, period+"");
				table.setColumnFooter(TBC_BALANCE , balance+"");
				table.setColumnFooter(TBC_AMOUNT, (period+total)+"");
				table.setColumnFooter(TBC_CREDIT, total+"");
			}
			else{
				SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
			}
			table.setVisibleColumns(visibleColumns);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void generateReport() {
		try {
			List reportList=new ArrayList();
			SubscriberLedgerBean bean;
			List resultList=dao.getLedgerReport((Long)ledgertSelect.getValue(),
					CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()), 
					CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
					(Long)officeSelect.getValue());
			double openingBalance=dao.getOpeningBalance((Long)ledgertSelect.getValue(),
										CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
										(Long)officeSelect.getValue());
			if(resultList.size()>0){
				SubscriptionPaymentModel spmdl=null;
				Iterator itr=resultList.iterator();
				double period=0,balance=0,total=0;
				balance=openingBalance;
				while (itr.hasNext()) {
					double credit=0,paid=0;
					spmdl= (SubscriptionPaymentModel) itr.next();
					String name="";	
					if(spmdl.getPay_credit()==0){
						if(spmdl.getCredit_transaction()!=0){
							paid=spmdl.getAmount_paid();
							name=dao.getSubscriptionName(spmdl.getId());
							TransactionDetailsModel tdm;
							TransactionModel transaction=new SubscriptionPaymentDao().getTransaction(spmdl.getCredit_transaction());
							for(int i=0;i<transaction.getTransaction_details_list().size();i++){
								tdm=(TransactionDetailsModel)transaction.getTransaction_details_list().get(i);
								credit+=tdm.getAmount();
							}
						}
						else {
							credit=0;
							name="Cash Payment for "+dao.getSubscriptionName(spmdl.getId());
							paid=spmdl.getAmount_paid();
						}
					}
					else if(spmdl.getPay_credit()==1){
						name="Credit Payment for "+dao.getSubscriptionName(spmdl.getId());
						credit=spmdl.getAmount_paid();
						paid=0;
					}
					balance+=(paid-credit);
					period+=(paid-credit);
					bean=new SubscriberLedgerBean(spmdl.getId(), 
												spmdl.getPayment_date().toString(), 
												name,
												paid, 
												credit,
												period, 
												balance);
					reportList.add(bean);
				}
			}
			if(reportList.size()>0){
				HashMap<String, Object> params = new HashMap<String, Object>();
				report.setJrxmlFileName("SubscriberLedgerReport");
				report.setReportFileName("SubscriberLedgerReport");
				report.setReportTitle("Subscriber Ledger Report");
				String subTitle = "";
				params.put("OPENING", openingBalance);
				params.put("subscriber", new LedgerDao().getLedgerNameFromID((Long)ledgertSelect.getValue()));
				params.put("FromDate", CommonUtil.formatDateToDDMMYYYY(fromDate.getValue()));
				params.put("ToDate", CommonUtil.formatDateToDDMMYYYY(toDate.getValue()));
				subTitle += "of Office : "+getOfficeName();
				report.setReportSubTitle(subTitle);
				report.setReportType(toInt(reportType.getValue().toString()));
				report.setIncludeHeader(true);
				report.setOfficeName(getOfficeName());
				report.createReport(reportList, params);
				reportList.clear();
			}
			else{
				SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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

}
