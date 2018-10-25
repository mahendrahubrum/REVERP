package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.subscription.bean.SubscriptionListBean;
import com.inventory.subscription.dao.SubscriptionConfigurationDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionListDao;
import com.inventory.subscription.model.SubscriptionConfigurationModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane.ScalableIconUIResource;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

public class SubscriptionList extends SparkLogic{

	private static final long serialVersionUID = 2095443223456184667L;
	SPanel mainPanel;
	SFormLayout mainLayout;
	SComboField accountCombo,subscriptionTypeCombo,periodCombo;
	private SReportChoiceField reportChoiceField;
	SButton generate;
	private Report report;
	SubscriptionConfigurationDao scdao;
	SubscriptionCreationDao sdao;
	SubscriptionConfigurationModel scmdl;
	SDateField fromDate, toDate;
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@Override
	public SPanel getGUI() {
		try
		{
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(400, 280);
			report = new Report(getLoginID());
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionConfigurationDao();
			sdao=new SubscriptionCreationDao();
			scmdl=new SubscriptionConfigurationModel();
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			accountCombo=new SComboField(getPropertyName("account_type"), 200);
			accountCombo.setInputPrompt(getPropertyName("select"));
			subscriptionTypeCombo=new SComboField(getPropertyName("rental_item"), 200);
			subscriptionTypeCombo.setInputPrompt(getPropertyName("select"));
			periodCombo=new SComboField(getPropertyName("period"), 200,SConstants.allPeriod,"key","value",true,"-----------------Select-----------------");
			periodCombo.setValue((long)0);
			fromDate = new SDateField(getPropertyName("from_date"), 150,getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,getDateFormat(),getWorkingDate());
			reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));
			generate=new SButton(getPropertyName("generate"));
			
			/*****************************************************************************************************///Adding to Main Layout
			mainLayout.addComponent(accountCombo);
			mainLayout.addComponent(subscriptionTypeCombo);
			mainLayout.addComponent(fromDate);
			mainLayout.addComponent(toDate);
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
							SubscriptionListBean bean=null;
							SubscriptionCreationModel mdl=null;
							SubscriptionInModel simdl=null;
							LedgerModel lmdl;
							List lis=new ArrayList();
							List list=new SubscriptionListDao().
									getAllSubscriptions(
									toLong(accountCombo.getValue().toString()),
									toLong(subscriptionTypeCombo.getValue().toString()),getOfficeID(),
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
							if(list!=null && list.size()>0){
								for(int i=0;i<list.size();i++){
									simdl=(SubscriptionInModel)list.get(i);
									double cash=0,credit=0;
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
									String date="";
									if(simdl.getReturn_date()!=null)
										date=asString(simdl.getReturn_date());
									else
										date="";
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
									cash=new SubscriptionListDao().getTotalCash(simdl.getId(),
											CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
											CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
									credit=new SubscriptionListDao().getTotalCredit(simdl.getId(),
											CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
											CommonUtil.getSQLDateFromUtilDate(toDate.getValue())); 
									bean=new SubscriptionListBean(
											asString(accountCombo.getItemCaption(simdl.getAccount_type())),
											simdl.getSubscription().getName(),
											date, 
											asString(simdl.getSubscription_date()),
											type, 
											asString(new LedgerDao().getLedgerNameFromID(simdl.getSubscriber())), 
											asString(simdl.getRate()), 
											period,
											cash,
											credit);
									reportList.add(bean);
									
								}
								if(reportList.size()>0){
									report.setJrxmlFileName("SubscriptionList");
									report.setReportFileName("SubscriptionList");
									report.setReportTitle("Rental List");
									
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
									SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
								}
							}
							else {
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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
//		if(accountCombo.getValue()==null){
//			valid=false;
//			setRequiredError(accountCombo, getPropertyName("invalid_selection"), true);
//		}
//		else{
//			setRequiredError(accountCombo, null, false);
//		}
		if(subscriptionTypeCombo.getValue()==null){
			valid=false;
			setRequiredError(subscriptionTypeCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscriptionTypeCombo, null, false);
		}
		if(fromDate.getValue()==null){
			valid=false;
			setRequiredError(fromDate, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(fromDate, null, false);
		}
		if(toDate.getValue()==null){
			valid=false;
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
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
