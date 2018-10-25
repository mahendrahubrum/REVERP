package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.subscription.bean.SubscriptionTransactionBean;
import com.inventory.subscription.dao.SubscriptionConfigurationDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.dao.SubscriptionTransactionReportDao;
import com.inventory.subscription.model.SubscriptionConfigurationModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
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
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

public class SubscriptionTransactionReport extends SparkLogic{

	private static final long serialVersionUID = 2095443223456184667L;
	SPanel mainPanel;
	SFormLayout mainLayout;
	SComboField accountCombo,subscriptionTypeCombo,periodCombo;
	private SReportChoiceField reportChoiceField;
	SButton generate;
	private SButton show;

	private Report report;
	SubscriptionConfigurationDao scdao;
	SubscriptionCreationDao sdao;
	SubscriptionConfigurationModel scmdl;
	SDateField fromDate, toDate;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_RENTAL = "Rental";
	static String TBC_SUBSCRIBER = "Subscriber";
	static String TBC_CASH = "Cash";
	static String TBC_CREDIT = "Credit";
	static String TBC_RATE = "Rate";
	
	STable table;

	Object[] allColumns;
	Object[] visibleColumns;
	SHorizontalLayout mainLay;
	SHorizontalLayout popupContainer;
	private SComboField organizationSelect;
	private SComboField officeSelect;
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@Override
	public SPanel getGUI() {
		try
		{
			allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_SUBSCRIBER,TBC_RENTAL,TBC_RATE,TBC_CASH,TBC_CREDIT };
			visibleColumns = new Object[] { TBC_SN, TBC_DATE,TBC_SUBSCRIBER,TBC_RENTAL,TBC_RATE,TBC_CASH,TBC_CREDIT };
			
			table = new STable(null, 650, 250);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
			table.addContainerProperty(TBC_SUBSCRIBER, String.class, null,getPropertyName("subscriber"), null, Align.CENTER);
			table.addContainerProperty(TBC_RENTAL, String.class, null,getPropertyName("rental"), null, Align.CENTER);
			table.addContainerProperty(TBC_RATE, Double.class, null,getPropertyName("rate"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CASH, Double.class, null,getPropertyName("cash"), null, Align.RIGHT);
			table.addContainerProperty(TBC_CREDIT, Double.class, null,getPropertyName("credit"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_DATE, (float) 1);
			table.setColumnExpandRatio(TBC_SUBSCRIBER,(float) 1.5);
			table.setColumnExpandRatio(TBC_RENTAL, 2);
			table.setColumnExpandRatio(TBC_CREDIT, 1);
			table.setColumnExpandRatio(TBC_CASH, 1);
			table.setVisibleColumns(visibleColumns);
//			table.setSizeFull();
			table.setSelectable(true);
			table.setColumnFooter(TBC_RENTAL, "Total");
			table.setColumnFooter(TBC_CASH, "0.0");
			table.setColumnFooter(TBC_CREDIT, "0.0");
			
			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,
					new OfficeDao().getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(1010, 400);
			report = new Report(getLoginID());
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionConfigurationDao();
			sdao=new SubscriptionCreationDao();
			scmdl=new SubscriptionConfigurationModel();
			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
//			mainLayout.setMargin(true);
			accountCombo=new SComboField(getPropertyName("account_type"), 200);
			accountCombo.setInputPrompt(getPropertyName("select"));
			subscriptionTypeCombo=new SComboField(getPropertyName("rental_item"), 200);
			subscriptionTypeCombo.setInputPrompt(getPropertyName("select"));
			periodCombo=new SComboField(getPropertyName("period"), 200,SConstants.allPeriod,"key","value",true,getPropertyName("select"));
			periodCombo.setValue((long)0);
			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getWorkingDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(),getWorkingDate());
			reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));
			generate=new SButton(getPropertyName("generate"));
			show=new SButton(getPropertyName("show"));
			SHorizontalLayout lay=new SHorizontalLayout();
			lay.addComponent(show);
			lay.addComponent(generate);
			/*****************************************************************************************************///Adding to Main Layout
			mainLayout.addComponent(organizationSelect);
			mainLayout.addComponent(officeSelect);
			mainLayout.addComponent(accountCombo);
			mainLayout.addComponent(subscriptionTypeCombo);
			mainLayout.addComponent(fromDate);
			mainLayout.addComponent(toDate);
			mainLayout.addComponent(reportChoiceField);
			mainLayout.addComponent(lay);
//			mainLayout.setComponentAlignment(accountCombo, Alignment.MIDDLE_CENTER);
//			mainLayout.setComponentAlignment(subscriptionTypeCombo, Alignment.MIDDLE_CENTER);
////			mainLayout.setComponentAlignment(periodCombo, Alignment.MIDDLE_CENTER);
//			mainLayout.setComponentAlignment(reportChoiceField, Alignment.MIDDLE_CENTER);
//			mainLayout.setComponentAlignment(generate, Alignment.MIDDLE_CENTER);
			mainLay.addComponent(mainLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);
			mainLay.setMargin(true);
			mainPanel.setContent(mainLay);
			
			/*****************************************************************************************************///Listeners
			List accountList=new ArrayList();
			accountList.add(new KeyValue((long) 0, "------------------All------------------"));
			accountList.addAll(SConstants.rentalList);
			SCollectionContainer accountContainer=SCollectionContainer.setList(accountList, "key");
			accountCombo.setContainerDataSource(accountContainer);
			accountCombo.setItemCaptionPropertyId("value");
			accountCombo.setValue((long)0);
			
			subscriptionTypeCombo.removeAllItems();
			List typeList=new ArrayList();
			typeList.add(0, new SubscriptionCreationModel(0, "------------------All------------------"));
			typeList.addAll(sdao.getAllSubscriptions(getOfficeID(),getLoginID()));
			SCollectionContainer typeContainer=SCollectionContainer.setList(typeList, "id");
			subscriptionTypeCombo.setContainerDataSource(typeContainer);
			subscriptionTypeCombo.setItemCaptionPropertyId("name");
			subscriptionTypeCombo.setValue((long)0);

			organizationSelect
			.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								new OfficeDao().getAllOfficeNamesUnderOrg((Long) organizationSelect
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
						accountCombo.setValue(null);
						accountCombo.setValue((long)0); 
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			accountCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						long type=toLong(accountCombo.getValue().toString());
						if(type!=0){
							subscriptionTypeCombo.removeAllItems();
							reloadSubscriptionTypeCombo(0,type);
							periodCombo.setValue((long)0);
						}
						else{
							subscriptionTypeCombo.removeAllItems();
							List typeList=new ArrayList();
							typeList.add(0, new SubscriptionCreationModel(0, "------------------All------------------"));
							typeList.addAll(sdao.getAllSubscriptions((Long)officeSelect.getValue(),getLoginID()));
							SCollectionContainer typeContainer=SCollectionContainer.setList(typeList, "id");
							subscriptionTypeCombo.setContainerDataSource(typeContainer);
							subscriptionTypeCombo.setItemCaptionPropertyId("name");
							subscriptionTypeCombo.setValue((long)0);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			show.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try{
							table.removeAllItems();
							table.setVisibleColumns(allColumns);
							table.setColumnFooter(TBC_CASH, "0.0");
							table.setColumnFooter(TBC_CREDIT, "0.0");
							SubscriptionInModel simdl=null;
							SubscriptionPaymentModel spmdl=null;
							PaymentDepositModel pdmdl=null;
							List lis=new ArrayList();
							double t_cash=0,t_credit=0;
							List list=new SubscriptionTransactionReportDao().
										getTransactionReport((Long)subscriptionTypeCombo.getValue(), 
										CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
										(Long)officeSelect.getValue());
							List payList=new SubscriptionTransactionReportDao().
										getExpenditure((Long)subscriptionTypeCombo.getValue(), 
										CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
										(Long)officeSelect.getValue());
										
							if(list!=null && list.size()>0){
								for(int i=0;i<list.size();i++){
									String name="";
									spmdl=(SubscriptionPaymentModel)list.get(i);
									simdl=(SubscriptionInModel)new SubscriptionPaymentDao().getAllInModel(spmdl.getId());
									double cash=0,credit=0;
									if(spmdl.getPay_credit()==0) {
										if(spmdl.getCredit_transaction()!=0){
											name=simdl.getSubscription().getName();
											cash=spmdl.getAmount_paid();
											TransactionDetailsModel tdm;
											TransactionModel transaction=new SubscriptionPaymentDao().getTransaction(spmdl.getCredit_transaction());
											for(int j=0;i<transaction.getTransaction_details_list().size();j++){
												tdm=(TransactionDetailsModel)transaction.getTransaction_details_list().get(j);
												credit+=tdm.getAmount();
											}
										}
										else {
											name="Cash Payment for "+simdl.getSubscription().getName();
											credit=0;
											cash=spmdl.getAmount_paid();
										}
									}
									else if(spmdl.getPay_credit()==1) {
										name="Credit Payment for "+simdl.getSubscription().getName();
										cash=0;
										credit=spmdl.getAmount_paid();
									}
									table.addItem(new Object[] {
											table.getItemIds().size()+1,
											(long)0,
											spmdl.getPayment_date().toString(),
											asString(new LedgerDao().getLedgerNameFromID(simdl.getSubscriber())),
											name,
											simdl.getRate(),
											cash,
											credit},table.getItemIds().size()+1);
								}
							}
							if(payList!=null && payList.size()>0) {
								TransactionDetailsModel tdm=null;
								for(int i=0;i<payList.size();i++) {
									pdmdl=(PaymentDepositModel)payList.get(i);
									TransactionModel model=(TransactionModel)new SubscriptionPaymentDao().getTransaction(pdmdl.getTransaction().getTransaction_id());
									Iterator titr=model.getTransaction_details_list().iterator();
									while (titr.hasNext()) {
										tdm=(TransactionDetailsModel)titr.next();
										table.addItem(new Object[] {
												table.getItemIds().size()+1,
												(long)0,
												model.getDate().toString(),
												"Expenditure Transaction",
												tdm.getToAcct().getName()+" expense for "+tdm.getNarration(),
												(double)0,
												tdm.getAmount(),
												(double)0},table.getItemIds().size()+1);
									}
								}
							}
							if(list.size()==0 && payList.size()==0){
								SNotification.show("No Data Available",Type.WARNING_MESSAGE);
							}
							Iterator tbitr=table.getItemIds().iterator();
							while (tbitr.hasNext()) {
								Item item=table.getItem(tbitr.next());
								t_cash+=toDouble(item.getItemProperty(TBC_CASH).getValue().toString());
								t_credit+=toDouble(item.getItemProperty(TBC_CREDIT).getValue().toString());
							}
							table.setVisibleColumns(visibleColumns);
							table.setColumnFooter(TBC_CASH, t_cash+"");
							table.setColumnFooter(TBC_CREDIT, t_credit+"");
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			});
			
			
			generate.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try{
							String period;
							List reportList=new ArrayList();
							SubscriptionTransactionBean bean=null;
							SubscriptionInModel simdl=null;
							SubscriptionPaymentModel spmdl=null;
							PaymentDepositModel pdmdl=null;
							List lis=new ArrayList();
							List list=new SubscriptionTransactionReportDao().
										getTransactionReport((Long)subscriptionTypeCombo.getValue(), 
										CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
										CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
										(Long)officeSelect.getValue());
							List payList=new SubscriptionTransactionReportDao().
									getExpenditure((Long)subscriptionTypeCombo.getValue(), 
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
									(Long)officeSelect.getValue());
							
							System.out.println("List Size "+list.size());
							if(list!=null && list.size()>0){
								for(int i=0;i<list.size();i++){
									String name="";
									spmdl=(SubscriptionPaymentModel)list.get(i);
									simdl=(SubscriptionInModel)new SubscriptionPaymentDao().getAllInModel(spmdl.getId());
									double cash=0,credit=0;
									if(spmdl.getPay_credit()==0) {
										if(spmdl.getCredit_transaction()!=0){
											name=simdl.getSubscription().getName();
											cash=spmdl.getAmount_paid();
											TransactionDetailsModel tdm;
											TransactionModel transaction=new SubscriptionPaymentDao().getTransaction(spmdl.getCredit_transaction());
											for(int j=0;i<transaction.getTransaction_details_list().size();j++){
												tdm=(TransactionDetailsModel)transaction.getTransaction_details_list().get(j);
												credit+=tdm.getAmount();
											}
										}
										else {
											name="Cash Payment for "+simdl.getSubscription().getName();
											credit=0;
											cash=spmdl.getAmount_paid();
										}
									}
									else if(spmdl.getPay_credit()==1) {
										name="Credit Payment for "+simdl.getSubscription().getName();
										cash=0;
										credit=spmdl.getAmount_paid();
									}
									bean=new SubscriptionTransactionBean(spmdl.getPayment_date().toString(),
																		asString(new LedgerDao().getLedgerNameFromID(simdl.getSubscriber())),
																		name,
																		simdl.getRate(), cash, credit);
									reportList.add(bean);
								}
							}
							if(payList!=null && payList.size()>0) {
								TransactionDetailsModel tdm=null;
								for(int i=0;i<payList.size();i++) {
									pdmdl=(PaymentDepositModel)payList.get(i);
									TransactionModel model=(TransactionModel)new SubscriptionPaymentDao().getTransaction(pdmdl.getTransaction().getTransaction_id());
									Iterator titr=model.getTransaction_details_list().iterator();
									while (titr.hasNext()) {
										tdm=(TransactionDetailsModel)titr.next();
										bean=new SubscriptionTransactionBean(
												model.getDate().toString(), 
												"Expenditure Transaction", 
												tdm.getToAcct().getName()+" expense for "+tdm.getNarration(),
												(double)0,tdm.getAmount(), (double)0);
										reportList.add(bean);
									}
								}
							}
							if(reportList.size()>0){
								report.setJrxmlFileName("SubscriptionTransactionReport");
								report.setReportFileName("Rental Transaction Report");
								report.setReportTitle("Rental Transaction Report");
								
								String subTitle = "";
									subTitle += "Office : "+getOfficeName(); 
								report.setReportSubTitle(subTitle);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setIncludeHeader(true);
								report.setOfficeName(getOfficeName());
								report.createReport(reportList, null);
								reportList.clear();
								list.clear();
							}
							else {
								SNotification.show("No Data Available",Type.WARNING_MESSAGE);
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainPanel;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reloadSubscriptionTypeCombo(long id,long type)
	{
		List idList=null;
		try{
			subscriptionTypeCombo.removeAllItems();
			idList = new ArrayList();
			SubscriptionCreationDao dao=new SubscriptionCreationDao();
			idList.addAll(dao.getSubscriptions(type,(Long)officeSelect.getValue()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionTypeCombo.setContainerDataSource(bic);
			subscriptionTypeCombo.setItemCaptionPropertyId("name");
			subscriptionTypeCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(accountCombo.getValue()==null){
			valid=false;
			setRequiredError(accountCombo, "Invalid Selection", true);
		}
		else{
			setRequiredError(accountCombo, null, false);
		}
		if(subscriptionTypeCombo.getValue()==null){
			valid=false;
			setRequiredError(subscriptionTypeCombo, "Invalid Selection", true);
		}
		else{
			setRequiredError(subscriptionTypeCombo, null, false);
		}
		if(fromDate.getValue()==null){
			valid=false;
			setRequiredError(fromDate, "Invalid Selection", true);
		}
		else{
			setRequiredError(fromDate, null, false);
		}
		if(toDate.getValue()==null){
			valid=false;
			setRequiredError(toDate, "Invalid Selection", true);
		}
		else{
			setRequiredError(toDate, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
