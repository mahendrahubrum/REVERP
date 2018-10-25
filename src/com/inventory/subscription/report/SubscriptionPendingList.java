package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.subscription.bean.SubscriptionPendingBean;
import com.inventory.subscription.dao.SubscriptionConfigurationDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.dao.SubscriptionOutDao;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.dao.SubscriptionPendingDao;
import com.inventory.subscription.model.SubscriptionConfigurationModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

public class SubscriptionPendingList extends SparkLogic{

	private static final long serialVersionUID = 2095443223456184667L;
	SPanel mainPanel;
	SFormLayout mainLayout;
	SComboField accountCombo,subscriptionTypeCombo,periodCombo;
	private SReportChoiceField reportChoiceField;
	SButton generate;
	private Report report;
	SubscriptionConfigurationDao scdao;
	SubscriptionCreationDao sdao;
	SubscriptionInDao sidao;
	SubscriptionOutDao sodao;
	SubscriptionConfigurationModel scmdl;
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@Override
	public SPanel getGUI() {
		try
		{
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(400, 250);
			report = new Report(getLoginID());
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionConfigurationDao();
			sdao=new SubscriptionCreationDao();
			sidao=new SubscriptionInDao();
			sodao=new SubscriptionOutDao();
			scmdl=new SubscriptionConfigurationModel();
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			accountCombo=new SComboField("Account Type", 200);
			accountCombo.setInputPrompt("-----------------Select-----------------");
			subscriptionTypeCombo=new SComboField("Subscription Type", 200);
			subscriptionTypeCombo.setInputPrompt("-----------------Select-----------------");
			periodCombo=new SComboField("Subscription Period", 200,SConstants.allPeriod,"key","value",true,"-----------------Select-----------------");
			periodCombo.setValue((long)0);
			reportChoiceField = new SReportChoiceField("Export To");
			generate=new SButton("Generate");
			
			/*****************************************************************************************************///Adding to Main Layout
			mainLayout.addComponent(accountCombo);
			mainLayout.addComponent(subscriptionTypeCombo);
			mainLayout.addComponent(reportChoiceField);
			mainLayout.addComponent(generate);
			mainLayout.setComponentAlignment(accountCombo, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(subscriptionTypeCombo, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(reportChoiceField, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(generate, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
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
							typeList.addAll(sdao.getAllSubscriptions(getOfficeID(),getLoginID()));
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
			
			
			generate.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try{
							String period;
							List reportList=new ArrayList();
							SubscriptionPendingBean bean=null;
							SubscriptionCreationModel mdl=null;
							SubscriptionInModel simdl=null;
							LedgerModel lmdl;
							List lis=new ArrayList();
							List list=new SubscriptionPendingDao().
									getAllSubscriptions(
									toLong(accountCombo.getValue().toString()),
									toLong(subscriptionTypeCombo.getValue().toString()),getOfficeID());
							System.out.println("List Size "+list.size());
							if(list!=null && list.size()>0){
								
								for(int i=0;i<list.size();i++){
									
									simdl=(SubscriptionInModel)list.get(i);
									if(simdl.getPeriod()==1){
										period="Daily";
									}
									else if(simdl.getPeriod()==2){
										period="Weekly";
									}
									else if(simdl.getPeriod()==3){
										period="Monthly";
									}
									else{
										period="Yearly";
									}
									String type="";
									if(simdl.getAccount_type()==1){
										type="Expendeture";
									}
									else if(simdl.getAccount_type()==2){
										type="Income";
									}
									else if(simdl.getAccount_type()==3){
										type="Transportation";
									}
									double rate=simdl.getRate();
									long per=getPeriod(simdl.getId());
									Date issueDate=findIssueDate(simdl.getId());
									int diffDays=calculateDifferenceInDays(simdl.getId());
									double previousPaid=getTotalPaid(simdl.getId());
									double amountDue=0;
									if(simdl.getSubscription().getSpecial()!=1) {
										amountDue=calculateSubscriptionAmount(diffDays, rate, per)-previousPaid;
									}
									else {
										amountDue=0;
									}
									bean=new SubscriptionPendingBean(
											type,
											simdl.getSubscription().getName(),
											asString(simdl.getSubscription_date()),
											asString(new LedgerDao().getLedgerNameFromID(simdl.getSubscriber())),
											period, 
											rate,
											previousPaid,
											amountDue);
									reportList.add(bean);
								}
								if(reportList.size()>0){
									report.setJrxmlFileName("SubscriptionPendingList");
									report.setReportFileName("SubscriptionPendingList");
									report.setReportTitle("Rental Pending List");
									
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
			idList.addAll(dao.getSubscriptions(type,getOfficeID()));
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
		if(periodCombo.getValue()==null){
			valid=false;
			setRequiredError(periodCombo, "Invalid Selection", true);
		}
		else{
			setRequiredError(periodCombo, null, false);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	private int calculateDifferenceInDays(long sid){
		int days=0;
		Date date=null;
		Calendar calendar=Calendar.getInstance();
		long fromTime,toTime;
		try{
			date=findIssueDate(sid);
			calendar.setTime(date);
			fromTime=date.getTime();
			toTime=new Date().getTime();
			days=(int)(((toTime-fromTime)/(24*60*60*1000)));
//			System.out.println("Days Here "+days);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return days;
	}
	
	@SuppressWarnings("rawtypes")
	private Date findIssueDate(long sid){
		Date date=null;
		List list=new ArrayList();
		SubscriptionInModel simdl;
		SubscriptionPaymentModel mdl;
		try{
			simdl=sidao.getSubscriptionInModel(sid);
			date=simdl.getSubscription_date();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return date;
	}
	
	private long getPeriod(long sid){
		long period=0;
		try{
			SubscriptionInModel mdl=sidao.getSubscriptionInModel(sid);
			if(mdl!=null){
				period=mdl.getPeriod();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return period;
	}
	
	private double calculateSubscriptionAmount(int diffDays,double rate,long per){
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
	
	@SuppressWarnings("rawtypes")
	private double getTotalPaid(long sid){
		double due=0;
		List list=new ArrayList();
		SubscriptionPaymentModel mdl;
		try{
			list=new SubscriptionPaymentDao().getAllTransactions(sid);
			if(list.size()>0){
				for(int i=0;i<list.size();i++){
					mdl=(SubscriptionPaymentModel)list.get(i);
					due+=mdl.getAmount_paid();
				}
			}
			else{
				due=0;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return due;
	}
	
}
