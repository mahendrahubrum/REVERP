package com.inventory.subscription.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.subscription.bean.SubscriptionLedgerBean;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionLedgerSubscriberDao;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

public class SubscriptionLedgerNew extends SparkLogic 
{

	private static final long serialVersionUID = 559155820379197224L;
	SPanel mainPanel;
	private Report report;
	SFormLayout mainLayout;
	SComboField accountCombo,subscriptionCombo;
	SButton generate;
	private SReportChoiceField reportChoiceField;
	SubscriptionCreationDao scdao;
	SubscriptionPaymentDao spdao;
	SDateField fromDate, toDate;
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	@Override
	public SPanel getGUI() {
		try{
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(400, 280);
			report = new Report(getLoginID());
			
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionCreationDao();
			spdao=new SubscriptionPaymentDao();
			mainLayout=new SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			
			accountCombo=new SComboField(getPropertyName("account_type"), 200);
			accountCombo.setInputPrompt(getPropertyName("select"));
			subscriptionCombo=new SComboField(getPropertyName("rental"), 200);
			subscriptionCombo.setInputPrompt(getPropertyName("select"));
			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(),getWorkingDate());
			reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));
			generate=new SButton(getPropertyName("generate"));
			
			mainLayout.addComponent(accountCombo);
			mainLayout.addComponent(subscriptionCombo);
			mainLayout.addComponent(fromDate);
			mainLayout.addComponent(toDate);
			mainLayout.addComponent(reportChoiceField);
			mainLayout.addComponent(generate);
			mainLayout.setComponentAlignment(accountCombo, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(subscriptionCombo, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(reportChoiceField, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(generate, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			SCollectionContainer accountContainer=SCollectionContainer.setList(SConstants.rentalList, "key");
			accountCombo.setContainerDataSource(accountContainer);
			accountCombo.setItemCaptionPropertyId("value");
			accountCombo.setValue((long)0);
			subscriptionCombo.removeAllItems();
			List typeList=new ArrayList();
			typeList.addAll(scdao.getAllSubscriptions(getOfficeID(),getLoginID()));
			SCollectionContainer typeContainer=SCollectionContainer.setList(typeList, "id");
			subscriptionCombo.setContainerDataSource(typeContainer);
			subscriptionCombo.setItemCaptionPropertyId("name");
			subscriptionCombo.setValue(null);
			subscriptionCombo.setInputPrompt(getPropertyName("select"));
			accountCombo.setInputPrompt(getPropertyName("select"));
		
			accountCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						long type=toLong(accountCombo.getValue().toString());
						if(type!=0){
							subscriptionCombo.removeAllItems();
							reloadSubscriptionCombo(0,type);
						}
						else{
							subscriptionCombo.removeAllItems();
							List typeList=new ArrayList();
							typeList.addAll(scdao.getAllSubscriptions(getOfficeID(),getLoginID()));
							SCollectionContainer typeContainer=SCollectionContainer.setList(typeList, "id");
							subscriptionCombo.setContainerDataSource(typeContainer);
							subscriptionCombo.setItemCaptionPropertyId("name");
							subscriptionCombo.setValue(null);
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
					accountCombo.setRequiredError(null);
					subscriptionCombo.setRequiredError(null);
					if(isValid()){
						try{
							double openingBalance=0;
							List resultList=new ArrayList();
							SubscriptionLedgerBean bean=null;
							SubscriptionPaymentModel mdl;
							List list=new SubscriptionLedgerSubscriberDao().getSubscriptionPayments(
									toLong(subscriptionCombo.getValue().toString()),
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
									CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),getOfficeID());
							openingBalance=new SubscriptionLedgerSubscriberDao().getOpeningBalancePayment(
									toLong(subscriptionCombo.getValue().toString()),
									CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),getOfficeID());
							if(list!=null && list.size()>0){
								double balance=0,period=0;
								balance=openingBalance;
								for(int i=0;i<list.size();i++){
									mdl=(SubscriptionPaymentModel)list.get(i);
									long id=new SubscriptionLedgerSubscriberDao().getAccountType(mdl);
									long rentId=new SubscriptionLedgerSubscriberDao().getRentStatus(mdl);
									long vehicle=new SubscriptionLedgerSubscriberDao().getVehicleStatus(mdl);
									String rent="";
									if(id==1){
										rent="Expendeture";
									}
									else if(id==2){
										rent="Income";
									}
									else if(id==3){
										if(rentId==1){
											rent="Rent In";
										}
										else if(rentId==2){
											rent="Rent Out";
										}
										else if(rentId==3){
											if(vehicle==1){
												rent="Rent In";
											}
											else if(vehicle==2){
												rent="Rent Out";
											}
										}
									}
									balance+=mdl.getAmount_paid();
									period+=mdl.getAmount_paid();
									String cash="";
									if(mdl.getCash_cheque()==1){
										cash="Cash";
									}
									else{
										cash="Cheque";
									}
									bean=new SubscriptionLedgerBean(
											asString(new SubscriptionLedgerSubscriberDao().getSubscriptionName(mdl)),
											asString(CommonUtil.formatDateToDDMMMYYYY(mdl.getPayment_date())),
											rent,
											mdl.getAmount_paid(),
											period,
											balance,cash);
									resultList.add(bean);
								}
								if(resultList.size()>0){
									HashMap<String, Object> params = new HashMap<String, Object>();
									report.setJrxmlFileName("SubscriptionLedgerReport");
									report.setReportFileName("SubscriptionLedgerReport");
									report.setReportTitle("Rental Ledger Report");
									String subTitle = "";
									long id=new SubscriptionLedgerSubscriberDao().getAccount((Long)subscriptionCombo.getValue());
									if(id==1){
										subTitle += "Expendeture Rental Report ";
									}
									else if(id==2){
										subTitle += "Income Rental Report ";
									}
									else{
										subTitle += "Transportation Rental Report ";
									}
									params.put("OPENING", openingBalance);
									params.put("FromDate", CommonUtil.formatDateToDDMMYYYY(fromDate.getValue()));
									params.put("ToDate", CommonUtil.formatDateToDDMMYYYY(toDate.getValue()));
									subTitle += "of Office : "+getOfficeName();
									report.setReportSubTitle(subTitle);
									report.setReportType(toInt(reportChoiceField
											.getValue().toString()));
									report.setIncludeHeader(true);
									report.setOfficeName(getOfficeName());
									report.createReport(resultList, params);
									resultList.clear();
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
		catch(Exception e){
			e.printStackTrace();
		}
		return mainPanel;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reloadSubscriptionCombo(long id,long type)
	{
		List idList=null;
		try{
			subscriptionCombo.removeAllItems();
			idList = new ArrayList();
			SubscriptionCreationDao dao=new SubscriptionCreationDao();
			idList.addAll(dao.getSubscriptions(type,getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionCombo.setContainerDataSource(bic);
			subscriptionCombo.setItemCaptionPropertyId("name");
			subscriptionCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(subscriptionCombo.getValue()==null || subscriptionCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscriptionCombo, null, false);
		}
		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			valid = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			toDate.focus();
			valid = false;
		} else
			setRequiredError(toDate, null, false);
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
