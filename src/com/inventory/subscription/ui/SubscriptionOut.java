package com.inventory.subscription.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.subscription.bean.RentalInvoiceBean;
import com.inventory.subscription.dao.ExpenditureDao;
import com.inventory.subscription.dao.SubscriptionConfigurationDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.dao.SubscriptionOutDao;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionExpenditureModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionInventoryDetailsModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

public class SubscriptionOut extends SparkLogic 
{
	private static final long serialVersionUID = 4933607204036137737L;
	SPanel mainPanel;
	SHorizontalLayout mainHorizontalLayout,imageLayout;
	SVerticalLayout mainLayout;
	SVerticalLayout tableLayout;
	SHorizontalLayout subscriptionNoLayout,subscriptionLayout,subscriptionDateLayout,accountLayout,rentLayout;
	SHorizontalLayout amountLayout,periodLayout,detailsLayout,buttonLayout,subscriberLayout;
	SHorizontalLayout deadLayout,dateLayout,rentOutLayout;
	SComboField subscriptionCombo,subscriptionNoCombo,periodCombo,subscriberCombo,deadCombo,rentOutCombo;
	STextField deadField,amountField;
	STextArea detailsArea;
	SRadioButton accountRadio,rentRadio;
	SDateField deadDate,subscribedDate;
	SButton save,update,delete,createNew,print;
	SubscriptionConfigurationDao dao;
	SubscriptionCreationDao scdao;
	SubscriptionOutDao sodao;
	SubscriptionInDao sidao;
	ValueChangeListener noListener,sListener;
	ValueChangeListener dlistener,cListener,rListener;
	STable table;
	SImage vehicleImage;
	String vehicleImageName = "";
	String dir,odir;
	private Object[] allHeaders,visibleHeaders;
	static String TBC_PID = "Parent Id";
	static String TBC_EID = "Expendeture Id";
	static String TBC_SELECT = "";
	static String TBC_NAME = "Item Condition";
	static String TBC_DETAILS = "Details";
	RentalInvoiceBean bean;
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI()
	{
		try
		{
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
//			setSize(420, 560);
			setSize(900, 560);
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionCreationDao();
			sodao=new SubscriptionOutDao();
			sidao=new SubscriptionInDao();
			dao=new SubscriptionConfigurationDao();
			imageLayout=new SHorizontalLayout();
			imageLayout.setSpacing(true);
			imageLayout.setMargin(true);
			dir = VaadinServlet.getCurrent().getServletContext().getRealPath("/")
					+ "VAADIN/themes/testappstheme/VehicleImages/";
			vehicleImageName = "VehicleImages/blank.png";
			odir = VaadinServlet.getCurrent().getServletContext().getRealPath("/").trim()+"VAADIN/themes/testappstheme/Images/no_image.png".trim();
//			vehicleImage = new SImage(null, new ThemeResource(vehicleImageName));
//			vehicleImage.setWidth("100");
//			vehicleImage.setHeight("100");
//			vehicleImage.setImmediate(true);
//			imageLayout.addComponent(vehicleImage);
			imageLayout.setStyleName("layout_border");
			mainHorizontalLayout=new SHorizontalLayout();
			mainHorizontalLayout.setSpacing(true);
			tableLayout=new SVerticalLayout();
			tableLayout.setSpacing(true);
			allHeaders = new Object[] {TBC_PID, TBC_EID, TBC_SELECT,TBC_NAME,TBC_DETAILS};
			visibleHeaders = new Object[] {TBC_SELECT,TBC_NAME,TBC_DETAILS};
			table = new STable(null, 475, 350);
			table.addContainerProperty(TBC_PID, Long.class, null, TBC_PID, null, Align.CENTER);
			table.addContainerProperty(TBC_EID, Long.class, null, TBC_EID, null, Align.CENTER);
			table.addContainerProperty(TBC_SELECT, SCheckBox.class, null, TBC_SELECT, null, Align.CENTER);
			table.addContainerProperty(TBC_NAME, String.class, null, getPropertyName("item_condition"), null, Align.LEFT);
			table.addContainerProperty(TBC_DETAILS, STextField.class, null, getPropertyName("details"), null, Align.LEFT);
			table.setColumnExpandRatio(TBC_SELECT, (float)0.2);
			table.setColumnExpandRatio(TBC_NAME, (float)2);
			table.setColumnExpandRatio(TBC_DETAILS, (float)3);
			table.setVisibleColumns(visibleHeaders);
			table.setSelectable(true);	
			table.setImmediate(true);
			
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			subscriptionNoLayout=new SHorizontalLayout();
			subscriptionNoLayout.setSpacing(true);
			rentOutLayout=new SHorizontalLayout();
			rentOutLayout.setSpacing(true);
			subscriptionLayout=new SHorizontalLayout();
			subscriptionLayout.setSpacing(true);
			subscriptionDateLayout=new SHorizontalLayout();
			subscriptionDateLayout.setSpacing(true);
			deadLayout=new SHorizontalLayout();
			deadLayout.setSpacing(true);
			dateLayout=new SHorizontalLayout();
			dateLayout.setSpacing(true);
			accountLayout=new SHorizontalLayout();
			accountLayout.setSpacing(true);
			rentLayout=new SHorizontalLayout();
			rentLayout.setSpacing(true);
			subscriberLayout=new SHorizontalLayout();
			subscriberLayout.setSpacing(true);
			amountLayout=new SHorizontalLayout();
			amountLayout.setSpacing(true);
			periodLayout=new SHorizontalLayout();
			periodLayout.setSpacing(true);
			detailsLayout=new SHorizontalLayout();
			detailsLayout.setSpacing(true);
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			subscriptionNoCombo=new SComboField(null, 175);
			subscriptionNoCombo.setRequired(true);
			reloadSubscriptionNoCombo((long)0);
			rentOutCombo=new SComboField(null, 200);
			rentOutCombo.setInputPrompt(getPropertyName("select"));
			rentOutCombo.setRequired(true);
			reloadSubscriptionOut((long)0);
			subscriptionCombo=new SComboField(null, 200);
			subscriptionCombo.setInputPrompt(getPropertyName("select"));
			subscriptionCombo.setRequired(true);
			reloadSubscriptionCombo(0);
			subscribedDate=new SDateField(null, 200);
			subscribedDate.setRequired(true);
			subscribedDate.setImmediate(true);
			subscribedDate.setValue(getWorkingDate());
			deadField=new STextField(null, 75);
			deadField.setRequired(true);
			deadField.setInputPrompt(getPropertyName("period"));
			deadCombo=new SComboField(null, 125,SConstants.periodType,"key","value",true,getPropertyName("select"));
			deadDate=new SDateField(null, 200);
			deadDate.setRequired(true);
			deadDate.setImmediate(true);
			deadDate.setValue(getWorkingDate());
			deadDate.setReadOnly(true);
			accountRadio = new SRadioButton(null, 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long) 2);
			accountRadio.setHorizontal(true);
			rentRadio = new SRadioButton(null, 200,SConstants.specialRentalTypeList, "key", "value");
			rentRadio.setHorizontal(true);
			rentRadio.setValue((long)1);
			rentRadio.setReadOnly(true);
			subscriberCombo=new SComboField(null, 200);
			subscriberCombo.setRequired(true);
			subscriberCombo.setInputPrompt(getPropertyName("select"));
			loadSubscriberIncome(0);
			amountField=new STextField(null, 200);
			amountField.setRequired(true);
			amountField.setInputPrompt(getPropertyName("rental_rate"));
			periodCombo=new SComboField(null, 200,SConstants.period,"key","value",true,getPropertyName("select"));
			detailsArea=new STextArea(null, 200);
			detailsArea.setInputPrompt(getPropertyName("rental_details"));
			save=new SButton(getPropertyName("save"));
			update=new SButton(getPropertyName("update"));
			update.setVisible(false);
			delete=new SButton(getPropertyName("delete"));
			delete.setVisible(false);
			print=new SButton(getPropertyName("print"));
			print.setVisible(false);
			createNew=new SButton();
			createNew.setStyleName("createNewBtnStyle");
			createNew.setDescription(getPropertyName("create_new"));
			
			/*****************************************************************************************************///Adding to Layout
			subscriptionNoLayout.addComponent(new SLabel(getPropertyName("rent_in_to_out"), 110));
			subscriptionNoLayout.addComponent(subscriptionNoCombo);
			subscriptionNoLayout.addComponent(createNew);
			rentOutLayout.addComponent(new SLabel(getPropertyName("rent_in_out"), 110));
			rentOutLayout.addComponent(rentOutCombo);
			subscriptionLayout.addComponent(new SLabel(getPropertyName("rental_item"), 110));
			subscriptionLayout.addComponent(subscriptionCombo);
			subscriptionDateLayout.addComponent(new SLabel(getPropertyName("rental_date"), 110));
			subscriptionDateLayout.addComponent(subscribedDate);
			deadLayout.addComponent(new SLabel(getPropertyName("rental_deadline"), 110));
			deadLayout.addComponent(deadField);
			deadLayout.addComponent(deadCombo);
			dateLayout.addComponent(new SLabel(getPropertyName("rental_deadline_date"), 110));
			dateLayout.addComponent(deadDate);
			accountLayout.addComponent(new SLabel(getPropertyName("account_type"),110));
			accountLayout.addComponent(accountRadio);
			rentLayout.addComponent(new SLabel(getPropertyName("rent_type"), 110));
			rentLayout.addComponent(rentRadio);
			subscriberLayout.addComponent(new SLabel(getPropertyName("subscriber"), 110));
			subscriberLayout.addComponent(subscriberCombo);
			amountLayout.addComponent(new SLabel(getPropertyName("rental_rate"), 110));
			amountLayout.addComponent(amountField);
			periodLayout.addComponent(new SLabel(getPropertyName("period_type"), 110));
			periodLayout.addComponent(periodCombo);
			detailsLayout.addComponent(new SLabel(getPropertyName("rental_details"), 110));
			detailsLayout.addComponent(detailsArea);
			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(print);
			
			/*****************************************************************************************************///Adding to Main Layout
			mainLayout.addComponent(subscriptionNoLayout);
			mainLayout.addComponent(rentOutLayout);
			mainLayout.addComponent(subscriptionLayout);
			mainLayout.addComponent(subscriptionDateLayout);
			mainLayout.addComponent(deadLayout);
			mainLayout.addComponent(dateLayout);
			mainLayout.addComponent(accountLayout);
			mainLayout.addComponent(rentLayout);
			mainLayout.addComponent(subscriberLayout);
			mainLayout.addComponent(amountLayout);
			mainLayout.addComponent(periodLayout);
			mainLayout.addComponent(detailsLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(subscriptionNoLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(rentOutLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(subscriptionLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(subscriptionDateLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(deadLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(dateLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(accountLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(rentLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(subscriberLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(amountLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(periodLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(detailsLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			rentLayout.setVisible(false);
			mainHorizontalLayout.addComponent(mainLayout);
			tableLayout.addComponent(imageLayout);
			tableLayout.addComponent(table);
			mainHorizontalLayout.addComponent(tableLayout);
//			tableLayout.setVisible(false);
			mainPanel.setContent(mainHorizontalLayout);
			
			/*****************************************************************************************************///Listeners
			createNew.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try{
						subscriptionNoCombo.setValue((long)0);
						loadSubscriberIncome((long)0);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			noListener=new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					try{
						removeAllErrors();
						save.setVisible(false);
						update.setVisible(true);
						print.setVisible(true);
						delete.setVisible(true);
						SubscriptionInModel mdl;
						String fileName="";
						imageLayout.removeAllComponents();
						subscriberCombo.removeAllItems();
						if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0")){
							mdl=sodao.getSubscriptionInModel(toLong(subscriptionNoCombo.getValue().toString()));
							if(mdl!=null){
								accountRadio.setReadOnly(false);
								rentRadio.setReadOnly(false);
								subscriptionCombo.setReadOnly(false);
								rentOutCombo.removeValueChangeListener(rListener);
								rentOutCombo.setValue(mdl.getRent_in());
								rentOutCombo.addValueChangeListener(rListener);
								subscriptionCombo.removeValueChangeListener(sListener);
								subscriptionCombo.setValue(mdl.getSubscription().getId());
								subscriptionCombo.setReadOnly(true);
								subscriptionCombo.addValueChangeListener(sListener);
								subscribedDate.removeValueChangeListener(dlistener);
								subscribedDate.setValue(mdl.getSubscription_date());
								subscribedDate.addValueChangeListener(dlistener);
								deadField.setValue(mdl.getQuantity()+"");
								deadCombo.removeValueChangeListener(cListener);
								deadCombo.setValue((long)mdl.getPeriod_type());
								deadCombo.addValueChangeListener(cListener);
								accountRadio.setValue(mdl.getAccount_type());
								deadDate.setNewValue(mdl.getClosing_date());
								try {
									String filename = mdl.getSubscription().getImage();
									if(filename!=null){
										if(filename.length()!=0) {
											String[] fileArray = filename.split(",");
											for (int i = 0; i < fileArray.length; i++) {
													fileName=VaadinServlet.getCurrent().getServletContext().getRealPath("/")
															+ "VAADIN/themes/testappstheme/VehicleImages/"
															+ fileArray[i].trim();
													vehicleImage = new SImage(null, new FileResource(new File(fileName)));
													vehicleImage.setWidth("50");
													vehicleImage.setHeight("50");
													vehicleImage.setImmediate(true);
													imageLayout.addComponent(vehicleImage);
											}
										}
										else {
											vehicleImage = new SImage(null, new FileResource(new File(odir)));
											vehicleImage.setWidth("50");
											vehicleImage.setHeight("50");
											vehicleImage.setImmediate(true);
											imageLayout.addComponent(vehicleImage);
										}
									}
								} 
								catch (Exception e) {
									e.printStackTrace();
								}
								if(mdl.getSubscription_details_list()!=null && mdl.getSubscription_details_list().size()!=0){
									Iterator it=mdl.getSubscription_details_list().iterator();
									SubscriptionInventoryDetailsModel detmdl;
									table.removeAllItems();
									table.setVisibleColumns(allHeaders);
									String details;
									while(it.hasNext()){
										detmdl=(SubscriptionInventoryDetailsModel)it.next();
										if(detmdl.getDetails().trim().length()>0)
											details=detmdl.getDetails();
										else
											details="";
										table.addItem(new Object[]{
												mdl.getId(),
												detmdl.getExpenditure().getId(),
												new SCheckBox(null, true),
												detmdl.getExpenditure().getName(),
												new STextField(null,250 ,details)
										},table.getItemIds().size()+1);
									}
									table.setVisibleColumns(visibleHeaders);
								}
								else{
									table.removeAllItems();
								}
								loadTable();
								
								
								long type=toLong(accountRadio.getValue().toString());
								if(type==(long)1){
									setSize(900, 560);
									rentLayout.setVisible(false);
									rentRadio.setReadOnly(true);
									loadSubscriberExpenditure(mdl.getSubscriber());
								}
								else if(type==(long)2){
									setSize(900, 560);
									rentLayout.setVisible(false);
									rentRadio.setReadOnly(true);
									loadSubscriberIncome(mdl.getSubscriber());
								}
								else if(type==(long)3){
									setSize(900, 560);
									rentLayout.setVisible(true);
									rentRadio.setReadOnly(false);
									long rentType=scdao.getRentType(mdl.getSubscription().getId(), getOfficeID());
									if(rentType==2){
										rentRadio.setValue((long)2);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome(mdl.getSubscriber());
									}
									else{
										rentRadio.setValue((long)2);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome(mdl.getSubscriber());
									}
									rentRadio.setReadOnly(true);
								}
								accountRadio.setReadOnly(true);
								amountField.setValue(asString(mdl.getRate()));
								periodCombo.setValue(mdl.getPeriod());
								detailsArea.setValue(mdl.getDetails());
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else{
							resetAll();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(true);
					}
				}
			};
			subscriptionNoCombo.addValueChangeListener(noListener);
			
			rListener=new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					try{
						removeAllErrors();
						SubscriptionInModel mdl;
						String fileName="";
						subscriberCombo.removeAllItems();
						imageLayout.removeAllComponents();
						if(rentOutCombo.getValue() != null && !rentOutCombo.getValue().toString().equals("0")){
							mdl=sidao.getSubscriptionInModel(toLong(rentOutCombo.getValue().toString()));
							if(mdl!=null){
								accountRadio.setReadOnly(false);
								rentRadio.setReadOnly(false);
								subscriptionCombo.setReadOnly(false);
								subscriptionCombo.removeValueChangeListener(sListener);
								subscriptionCombo.setValue(mdl.getSubscription().getId());
								subscriptionCombo.setReadOnly(true);
								subscriptionCombo.addValueChangeListener(sListener);
								subscribedDate.removeValueChangeListener(dlistener);
								subscribedDate.setValue(mdl.getSubscription_date());
								subscribedDate.addValueChangeListener(dlistener);
								accountRadio.setValue(mdl.getAccount_type());
								long sid=toLong(subscriptionCombo.getValue().toString());
								SubscriptionCreationModel cmdl=scdao.getCreationModel(sid);
								if(cmdl.getAvailable()==1){
									if(mdl.getSubscription_details_list()!=null && mdl.getSubscription_details_list().size()!=0){
										Iterator it=mdl.getSubscription_details_list().iterator();
										SubscriptionInventoryDetailsModel detmdl;
										table.removeAllItems();
										table.setVisibleColumns(allHeaders);
										String details;
										while(it.hasNext()){
											detmdl=(SubscriptionInventoryDetailsModel)it.next();
											if(detmdl.getDetails().trim().length()>0)
												details=detmdl.getDetails();
											else
												details="";
											table.addItem(new Object[]{
													mdl.getId(),
													detmdl.getExpenditure().getId(),
													new SCheckBox(null, true),
													detmdl.getExpenditure().getName(),
													new STextField(null,250 ,details)
											},table.getItemIds().size()+1);
										}
										table.setVisibleColumns(visibleHeaders);
									}
									else{
										table.removeAllItems();
									}
									loadTable();
									long type=toLong(accountRadio.getValue().toString());
									if(type==(long)1){
										setSize(900, 560);
										rentLayout.setVisible(false);
										rentRadio.setReadOnly(true);
										loadSubscriberExpenditure(mdl.getSubscriber());
									}
									else if(type==(long)2){
										setSize(900, 560);
										rentLayout.setVisible(false);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome(mdl.getSubscriber());
									}
									else if(type==(long)3){
										setSize(900, 590);
										rentLayout.setVisible(true);
										rentRadio.setReadOnly(false);
										long rentType=scdao.getRentType(mdl.getSubscription().getId(), getOfficeID());
										if(rentType==2){
											rentRadio.setValue((long)2);
											rentRadio.setReadOnly(true);
											loadSubscriberIncome(mdl.getSubscriber());
										}
										else{
											rentRadio.setValue((long)2);
											rentRadio.setReadOnly(true);
											loadSubscriberIncome(mdl.getSubscriber());
										}
										rentRadio.setReadOnly(true);
									}
									accountRadio.setReadOnly(true);
								}
								else{
									SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
								}
								try {
									String filename = scdao.getImageName(cmdl.getId());
									if(filename!=null){
										if(filename.length()!=0) {
											String[] fileArray = filename.split(",");
											for (int i = 0; i < fileArray.length; i++) {
													fileName=VaadinServlet.getCurrent().getServletContext().getRealPath("/")
															+ "VAADIN/themes/testappstheme/VehicleImages/"
															+ fileArray[i].trim();
													vehicleImage = new SImage(null, new FileResource(new File(fileName)));
													vehicleImage.setWidth("50");
													vehicleImage.setHeight("50");
													vehicleImage.setImmediate(true);
													imageLayout.addComponent(vehicleImage);
											}
										}
										else {
											vehicleImage = new SImage(null, new FileResource(new File(odir)));
											vehicleImage.setWidth("50");
											vehicleImage.setHeight("50");
											vehicleImage.setImmediate(true);
											imageLayout.addComponent(vehicleImage);
										}
									}
								} 
								catch (Exception e) {
									e.printStackTrace();
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(true);
					}
				}
			};
			rentOutCombo.addValueChangeListener(rListener);
			
			sListener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						String fileName="";
						if(subscriptionCombo.getValue()!=null && !subscriptionCombo.getValue().toString().equals("")){
							accountRadio.setReadOnly(false);
							rentRadio.setReadOnly(false);
							subscriptionCombo.setReadOnly(false);
							imageLayout.removeAllComponents();
							long sid=toLong(subscriptionCombo.getValue().toString());
							SubscriptionCreationModel mdl=scdao.getCreationModel(sid);
							try {
								String filename = scdao.getImageName(mdl.getId());
								if(fileName!=null){
									String[] fileArray = filename.split(",");
									for (int i = 0; i < fileArray.length; i++) {
											fileName=VaadinServlet.getCurrent().getServletContext().getRealPath("/")
													+ "VAADIN/themes/testappstheme/VehicleImages/"
													+ fileArray[i].trim();
											vehicleImage = new SImage(null, new FileResource(new File(fileName)));
											vehicleImage.setWidth("50");
											vehicleImage.setHeight("50");
											vehicleImage.setImmediate(true);
											imageLayout.addComponent(vehicleImage);
									}
								}
							} 
							catch (Exception e) {
								e.printStackTrace();
							}
							if(mdl!=null){
								accountRadio.setValue(mdl.getAccount_type());
								long type=toLong(accountRadio.getValue().toString());
								if(type==1){
									setSize(900, 560);
									rentLayout.setVisible(false);
									rentRadio.setReadOnly(true);
									loadSubscriberExpenditure((long)0);
								}
								else if(type==2){
									setSize(900, 560);
									rentLayout.setVisible(false);
									rentRadio.setReadOnly(true);
									loadSubscriberIncome((long)0);
								}
								else if(type==3){
									setSize(900, 590);
									rentLayout.setVisible(true);
									rentRadio.setReadOnly(false);
									long rentType=scdao.getRentType(sid, getOfficeID());
									if(rentType==2){
										rentRadio.setValue((long)2);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome((long)0);
									}
									else{
										rentRadio.setValue((long)2);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome((long)0);
									}
									rentRadio.setReadOnly(true);
								}
								accountRadio.setReadOnly(true);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(false);
					}
				}
			};
			subscriptionCombo.addValueChangeListener(sListener);
			
			dlistener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						deadDate.setComponentError(null);
						subscribedDate.setComponentError(null);
						if(isDate()){
							SubscriptionInModel mdl;
							Date date;
							Date fDate;
							deadDate.setReadOnly(false);
							int no=Integer.parseInt(deadField.getValue().toString());
							int period=Integer.parseInt(deadCombo.getValue().toString());
							int interval=0;
							Calendar cal=Calendar.getInstance();
							cal.setTime(subscribedDate.getValue());
							deadDate.setNewValue(cal.getTime());
							if(period==1){
								interval=no*1;
							}
							else if(period==2){
								interval=no*7;
							}
							else if(period==3){
								interval=no*30;
							}
							else if(period==4){
								interval=no*365;
							}
							cal.add(Calendar.DATE, interval);
							fDate=cal.getTime();
							if(rentOutCombo.getValue() != null && !rentOutCombo.getValue().toString().equals("0")){
								long id=toLong(rentOutCombo.getValue().toString());
								mdl=sidao.getSubscriptionInModel(id);
								if(mdl!=null){
									date=CommonUtil.getUtilFromSQLDate(mdl.getClosing_date());
//									System.out.println("Reached Here 222222 "+date);
									if(mdl.getSubscription_date().getTime()>subscribedDate.getValue().getTime()){
										setRequiredError(subscribedDate, getPropertyName("invalid_selection"), true);
									}
									if(date.getTime()>=fDate.getTime()){
										deadDate.setNewValue(fDate);
									}
									else{
										setRequiredError(deadDate, getPropertyName("invalid_selection"), true);
									}
								}
							}
							else{
								deadDate.setNewValue(fDate);
							}
							deadDate.setReadOnly(true);
						}
					}
					catch(Exception e){
						deadDate.setReadOnly(true);
						e.printStackTrace();
					}
				}
			};
			subscribedDate.addValueChangeListener(dlistener);
			
			cListener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						deadDate.setComponentError(null);
						subscribedDate.setComponentError(null);
						if(isDate()){
							SubscriptionInModel mdl;
							Date date;
							Date fDate;
							deadDate.setReadOnly(false);
							int no=Integer.parseInt(deadField.getValue().toString());
							int period=Integer.parseInt(deadCombo.getValue().toString());
							int interval=0;
							Calendar cal=Calendar.getInstance();
							cal.setTime(subscribedDate.getValue());
							deadDate.setNewValue(cal.getTime());
							if(period==1){
								interval=no*1;
							}
							else if(period==2){
								interval=no*7;
							}
							else if(period==3){
								interval=no*30;
							}
							else if(period==4){
								interval=no*365;
							}
							cal.add(Calendar.DATE, interval);
							fDate=cal.getTime();
							if(rentOutCombo.getValue() != null && !rentOutCombo.getValue().toString().equals("0")){
								long id=toLong(rentOutCombo.getValue().toString());
								mdl=sidao.getSubscriptionInModel(id);
								if(mdl!=null){
									date=CommonUtil.getUtilFromSQLDate(mdl.getClosing_date());
//									System.out.println("Reached Here 222222 "+date);
									if(date.getTime()>=fDate.getTime()){
										deadDate.setNewValue(fDate);
									}
									else{
										setRequiredError(deadDate, getPropertyName("invalid_selection"), true);
									}
								}
							}
							else{
								deadDate.setNewValue(fDate);
							}
							deadDate.setReadOnly(true);
						}
					}
					catch(Exception e){
						deadDate.setReadOnly(true);
						e.printStackTrace();
					}
				}
			};
			deadCombo.addValueChangeListener(cListener);
			
			save.addClickListener(new ClickListener() 
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try
					{
						removeAllErrors();
						SubscriptionInModel mdl,simdl;
						SubscriptionCreationModel scmdl;
						deadDate.setReadOnly(false);
						accountRadio.setReadOnly(false);
						rentRadio.setReadOnly(false);
						subscriptionCombo.setReadOnly(false);
						if(isValid())
						{
							long id=toLong(rentOutCombo.getValue().toString());
							long type=toLong(accountRadio.getValue().toString());
							simdl=sidao.getSubscriptionInModel(id);
							long sd=simdl.getSubscription().getId();
							long rentType=scdao.getRentType(sd, getOfficeID());
							mdl=new SubscriptionInModel();
							scmdl=scdao.getCreationModel(sd);
							if(scmdl!=null){
								if(scmdl.getAvailable()==1){
									if(rentType==3){
										scmdl.setAvailable(toLong("2"));
//										simdl.setAvailable(toLong("2"));
										mdl.setAvailable(toLong("2"));
									}
									else{
										scmdl.setAvailable(toLong("5"));
										mdl.setAvailable(toLong("0"));
									}
									mdl.setRent_in(id);
									mdl.setSubscription_no(getNextSequence("Subscription", getLoginID()));
									mdl.setSubscription(new SubscriptionCreationModel(sd));
									mdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(subscribedDate.getValue()));
									mdl.setQuantity(Integer.parseInt(deadField.getValue().toString()));
									mdl.setPeriod_type(Integer.parseInt(deadCombo.getValue().toString()));
									mdl.setClosing_date(CommonUtil.getSQLDateFromUtilDate(deadDate.getValue()));
									mdl.setAccount_type(type);
									mdl.setLock(toLong("0"));
									mdl.setSubscriber(toLong(subscriberCombo.getValue().toString()));
									mdl.setRate(toDouble(amountField.getValue().toString()));
									mdl.setPeriod(toLong(periodCombo.getValue().toString()));
									mdl.setDetails(detailsArea.getValue());
									SubscriptionInventoryDetailsModel detmdl;
									Item item;
									List<SubscriptionInventoryDetailsModel> childList=new ArrayList<SubscriptionInventoryDetailsModel>();
									Iterator it = table.getItemIds().iterator();
									while(it.hasNext()){
										item=table.getItem(it.next());
										if(((SCheckBox)item.getItemProperty(TBC_SELECT).getValue()).getValue()) {
											detmdl=new SubscriptionInventoryDetailsModel();
											detmdl.setExpenditure(new SubscriptionExpenditureModel(toLong(item.getItemProperty(TBC_EID).getValue().toString())));
											detmdl.setDetails(item.getItemProperty(TBC_DETAILS).getValue().toString());
											childList.add(detmdl);
										}
									}
									if(childList!=null && childList.size()!=0)
										mdl.setSubscription_details_list(childList);
									long sid=sodao.save(mdl,scmdl,simdl);
									imageLayout.removeAllComponents();
									reloadSubscriptionNoCombo(sid);
									SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								}
								else{
									SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
								}
							}
						}
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(true);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(true);
					}
				}
			});
			
			update.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try
					{
						removeAllErrors();
						SubscriptionInModel mdl,simdl;
						SubscriptionCreationModel scmdl;
						deadDate.setReadOnly(false);
						accountRadio.setReadOnly(false);
						rentRadio.setReadOnly(false);
						subscriptionCombo.setReadOnly(false);
						if(isValid())
						{
							if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0"))
							{
								long id=toLong(subscriptionNoCombo.getValue().toString());
								long sid=toLong(rentOutCombo.getValue().toString());
								long type=toLong(accountRadio.getValue().toString());
								mdl=sodao.getSubscriptionInModel(id);
								simdl=sidao.getSubscriptionInModel(sid);
								if(mdl!=null){
									if(mdl.getLock()!=1){
										if(mdl.getReturn_date()==null){
											long sd=mdl.getSubscription().getId();
											scmdl=scdao.getCreationModel(sd);
											long rentType=scdao.getRentType(sd, getOfficeID());
											if(scmdl!=null){
												if(scmdl.getAvailable()==2){
													if(rentType==3){
														scmdl.setAvailable(toLong("2"));
//														simdl.setAvailable(toLong("2"));
														mdl.setAvailable(toLong("2"));
													}
													else{
														scmdl.setAvailable(toLong("5"));
														mdl.setAvailable(toLong("0"));
													}
													mdl.setSubscription(new SubscriptionCreationModel(sd));
													mdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(subscribedDate.getValue()));
													mdl.setQuantity(Integer.parseInt(deadField.getValue().toString()));
													mdl.setPeriod_type(Integer.parseInt(deadCombo.getValue().toString()));
													mdl.setClosing_date(CommonUtil.getSQLDateFromUtilDate(deadDate.getValue()));
													mdl.setAccount_type(type);
													mdl.setSubscriber(toLong(subscriberCombo.getValue().toString()));
													mdl.setRate(toDouble(amountField.getValue().toString()));
													mdl.setPeriod(toLong(periodCombo.getValue().toString()));
													mdl.setDetails(detailsArea.getValue());
													mdl.setLock(toLong("0"));
													SubscriptionInventoryDetailsModel detmdl;
													Item item;
													List<SubscriptionInventoryDetailsModel> childList=new ArrayList<SubscriptionInventoryDetailsModel>();
													Iterator it = table.getItemIds().iterator();
													while(it.hasNext()){
														item=table.getItem(it.next());
														if(((SCheckBox)item.getItemProperty(TBC_SELECT).getValue()).getValue()) {
															detmdl=new SubscriptionInventoryDetailsModel();
															detmdl.setExpenditure(new SubscriptionExpenditureModel(toLong(item.getItemProperty(TBC_EID).getValue().toString())));
															detmdl.setDetails(item.getItemProperty(TBC_DETAILS).getValue().toString());
															childList.add(detmdl);
														}
													}
													if(childList!=null && childList.size()!=0)
														mdl.setSubscription_details_list(childList);
													long uid=sodao.update(mdl,scmdl);
													imageLayout.removeAllComponents();
													reloadSubscriptionNoCombo(uid);
													SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
												}
												else{
													SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
												}
											}
										}
										else{
											SNotification.show(getPropertyName("rental_closed"),Type.WARNING_MESSAGE);
											reloadSubscriptionNoCombo(id);
										}
									}
									else{
										SNotification.show(getPropertyName("payment_made"),Type.WARNING_MESSAGE);
										reloadSubscriptionNoCombo(id);
									}
									
								}
							}
							else
							{
								setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
							}
						}
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(true);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
						subscriptionCombo.setReadOnly(true);
					}
				}
			});
			
			delete.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event){
					try
					{
						removeAllErrors();
						final SubscriptionInModel mdl,simdl;
						final SubscriptionCreationModel scmdl;
						if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0")){
							long id=toLong(subscriptionNoCombo.getValue().toString());
							long sid=toLong(rentOutCombo.getValue().toString());
							mdl=sodao.getSubscriptionInModel(id);
							simdl=sidao.getSubscriptionInModel(sid);
							if(mdl!=null){
								if(mdl.getLock()!=1){
//									if(mdl.getReturn_date()==null){
										long sd=mdl.getSubscription().getId();
										scmdl=scdao.getCreationModel(sd);
										long rentType=scdao.getRentType(sd, getOfficeID());
										if(scmdl!=null){
											if(rentType==3){
												scmdl.setAvailable(toLong("1"));
												simdl.setAvailable(toLong("1"));
											}
											else{
												scmdl.setAvailable(toLong("0"));
											}
											ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
												public void onClose(ConfirmDialog dialog) {
													if (dialog.isConfirmed()) {
														try {
															sodao.delete(mdl,scmdl);
															SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
															reloadSubscriptionNoCombo((long)0);
														} 
														catch (Exception e) {
															Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
															e.printStackTrace();
														}
						        			        } 
						        			    }
						        			});
										}
//									}
//									else{
//										SNotification.show("Not Available",Type.WARNING_MESSAGE);
//									}
								}
								else{
									SNotification.show(getPropertyName("payment_made"),Type.WARNING_MESSAGE);
								}
								
							}
						}
						else
						{
							setRequiredError(subscriptionNoCombo, getPropertyName("invalid_selection"), true);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					subscriberCombo.removeAllItems();
					long type=toLong(accountRadio.getValue().toString());
					if(type==(long)1){
						tableLayout.setVisible(false);
						vehicleImage.setSource(new FileResource(new File("")));
						setSize(900, 560);
						center();
						loadSubscriberExpenditure(0);
					}
					else if(type==(long)2){
						tableLayout.setVisible(false);
						vehicleImage.setSource(new FileResource(new File("")));
						setSize(900, 560);
						center();
						loadSubscriberIncome(0);
					}
					else if(type==(long)3){
						tableLayout.setVisible(true);
						setSize(900, 560);
						center();
						loadSubscriberTransportation(0);
					}
					
				}
			});
			
			print.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0")){
							long sid=toLong(subscriptionNoCombo.getValue().toString());
							SubscriptionInModel mdl=sidao.getSubscriptionInModel(sid);
							SubscriptionInventoryDetailsModel dmdl=null;
							List resultList=new ArrayList();
							HashMap<String, Object> params = new HashMap<String, Object>();
							Report report = new Report(getLoginID());
							if(mdl!=null){
								if(mdl.getAccount_type()==3){
									String period = null;
									if(mdl.getPeriod()==(long)1){
										period="Daily";
									}
									else if(mdl.getPeriod()==(long)2){
										period="Weekly";
									}
									else if(mdl.getPeriod()==(long)3){
										period="Monthly";
									}
									else if(mdl.getPeriod()==(long)4){
										period="Yearly";
									}
									if(mdl.getSubscription_details_list().size()>0){
										Iterator it=mdl.getSubscription_details_list().iterator();
										while(it.hasNext()){
											dmdl=(SubscriptionInventoryDetailsModel)it.next();
											bean=new RentalInvoiceBean(dmdl.getDetails(),dmdl.getExpenditure().getName());
											bean.setName(mdl.getSubscription().getName());
											bean.setDate(CommonUtil.formatDateToDDMMYYYY(mdl.getSubscription_date()));
											bean.setReturnDate(CommonUtil.formatDateToDDMMYYYY(mdl.getClosing_date()));
											bean.setSubscriber(new LedgerDao().getLedgerNameFromID(mdl.getSubscriber()));
											bean.setPeriod(period);
											bean.setRate(mdl.getRate());
											resultList.add(bean);
										}
									}
									else{
										bean=new RentalInvoiceBean();
										bean.setName(mdl.getSubscription().getName());
										bean.setDate(CommonUtil.formatDateToDDMMYYYY(mdl.getSubscription_date()));
										bean.setReturnDate(CommonUtil.formatDateToDDMMYYYY(mdl.getClosing_date()));
										bean.setSubscriber(new LedgerDao().getLedgerNameFromID(mdl.getSubscriber()));
										bean.setPeriod(period);
										bean.setRate(mdl.getRate());
										resultList.add(bean);
									}
									if (resultList != null && resultList.size() > 0){
										String subTitle = "";
										subTitle += "Office : "+getOfficeName();
										report.setJrxmlFileName("TransportationRentalInvoice");
										report.setReportFileName("Transportation Rental");
										report.setReportTitle("Rental Invoice");
										if(mdl.getSubscription().getImage().length()!=0){
											try{
												String filename = mdl.getSubscription().getImage();
												String[] fileArray = filename.split(",");
												String dir=VaadinServlet.getCurrent().getServletContext().getRealPath("/")
														+ "VAADIN/themes/testappstheme/VehicleImages/".trim();
												for(int i=0;i<fileArray.length;i++){
													if(new File(dir+fileArray[i].trim()).exists()){
														switch(i)
														{
															case 0:	params.put("Image1", dir+fileArray[i].trim());
																	continue;
															case 1:	params.put("Image2", dir+fileArray[i].trim());
																	continue;
															case 2:	params.put("Image3", dir+fileArray[i].trim());
																	continue;
															case 3:	params.put("Image4", dir+fileArray[i].trim());
																	continue;
														}
													}
													else{
														continue;
													}
															
												}
											}
											catch(Exception e){
												e.printStackTrace();
											}
										}
										report.setIncludeHeader(true);
										report.setOfficeName(getOfficeName());
										report.setReportSubTitle(subTitle);
										report.setReportType(Report.PDF);
										report.createReport(resultList, params);
										report.print();
									}
								}
								else{
									SNotification.show(getPropertyName("service_not_available"), Type.WARNING_MESSAGE);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"), Type.WARNING_MESSAGE);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
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

	/*****************************************************************************************************///Methods
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadTable(){
		try{
			List list=new  ArrayList();
			SubscriptionExpenditureModel mdl;
			list.addAll(new ExpenditureDao().getAllExpenditures(getOfficeID()));
			table.setVisibleColumns(allHeaders);
			List lst=new ArrayList();
			Iterator it=table.getItemIds().iterator();
			while(it.hasNext()){
				Item item=table.getItem(it.next());
				lst.add(toLong(item.getItemProperty(TBC_EID).getValue().toString()));
			}
			if(list!=null && list.size()!=0){
				Iterator itr=list.iterator();
				while(itr.hasNext()){
					mdl=(SubscriptionExpenditureModel)itr.next();
					if(lst.contains(mdl.getId())){
						continue;
					}
					table.addItem(new Object[]{
							toLong("0"),
							mdl.getId(),
							new SCheckBox(null,false),
							mdl.getName(),
							new STextField(null,250,"")
					},table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(visibleHeaders);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionNoCombo(long id)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.add(0,new SubscriptionInModel(0, "-----------Create New-----------"));
			idList.addAll(sodao.getAllOutSubscriptions(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionNoCombo.setContainerDataSource(bic);
			subscriptionNoCombo.setItemCaptionPropertyId("details");
			subscriptionNoCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionOut(long id)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.addAll(sodao.getAllInSubscriptions(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			rentOutCombo.setContainerDataSource(bic);
			rentOutCombo.setItemCaptionPropertyId("details");
			rentOutCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionCombo(long id)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.addAll(sodao.getAllSubscriptions(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionCombo.setContainerDataSource(bic);
			subscriptionCombo.setItemCaptionPropertyId("name");
			subscriptionCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberExpenditure(long id){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllExpenditureSubscriptions(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			subscriberCombo.setContainerDataSource(bic);
			subscriberCombo.setItemCaptionPropertyId("name");
			subscriberCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberIncome(long id){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllIncomeSubscriptions(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			subscriberCombo.setContainerDataSource(bic);
			subscriberCombo.setItemCaptionPropertyId("name");
			subscriberCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberTransportation(long id){
		try{
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllTransportationSubscriptions(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			subscriberCombo.setContainerDataSource(bic);
			subscriberCombo.setItemCaptionPropertyId("name");
			subscriberCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		
		if(rentOutCombo.getValue() == null || rentOutCombo.getValue().toString().equals("0") || rentOutCombo.getValue().toString().equals("")){
			valid=false;
			setRequiredError(rentOutCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(rentOutCombo, null, false);
		}
		if(subscriptionCombo.getValue() == null || subscriptionCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscriptionCombo, null, false);
		}
		if(subscribedDate.getValue() == null){
			valid=false;
			setRequiredError(subscribedDate, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscribedDate, null, false);
		}
		if (deadField.getValue() == null || deadField.getValue().equals("")) {
			amountField.setValue("0");
		} 
		else {
			try {
				if (toDouble(deadField.getValue().toString()) <= 0) {
					deadField.setValue("0");
				} 
			} 
			catch (Exception e) {
				deadField.setValue("0");
			}
		}
		if(deadCombo.getValue() == null || deadCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(deadCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(deadCombo, null, false);
		}
		deadDate.setReadOnly(true);
		if(deadDate.getValue() == null){
			valid=false;
			setRequiredError(deadDate, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(deadDate, null, false);
		}
		deadDate.setReadOnly(false);
		if(subscriberCombo.getValue() == null || subscriberCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(subscriberCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscriberCombo, null, false);
		}
		
		if (amountField.getValue() == null || amountField.getValue().equals("")) {
			amountField.setValue("0");
		} 
		else {
			try {
				if (toDouble(amountField.getValue().toString()) <= 0) {
					amountField.setValue("0");
				} 
			} 
			catch (Exception e) {
				amountField.setValue("0");
			}
		}
		if(periodCombo.getValue() == null || periodCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(periodCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(periodCombo, null, false);
		}
		
		if(isDate()){
			SubscriptionInModel mdl = null;
			Date date;
			Date fDate;
			deadDate.setReadOnly(false);
			int no=Integer.parseInt(deadField.getValue().toString());
			int period=Integer.parseInt(deadCombo.getValue().toString());
			int interval=0;
			Calendar cal=Calendar.getInstance();
			cal.setTime(subscribedDate.getValue());
			deadDate.setNewValue(cal.getTime());
			if(period==1){
				interval=no*1;
			}
			else if(period==2){
				interval=no*7;
			}
			else if(period==3){
				interval=no*30;
			}
			else if(period==4){
				interval=no*365;
			}
			cal.add(Calendar.DATE, interval);
			fDate=cal.getTime();
			if(rentOutCombo.getValue() != null && !rentOutCombo.getValue().toString().equals("0")){
				long id=toLong(rentOutCombo.getValue().toString());
				try{
					mdl=sidao.getSubscriptionInModel(id);
				}
				catch(Exception e){
				}
				if(mdl!=null){
					date=CommonUtil.getUtilFromSQLDate(mdl.getClosing_date());
					if(mdl.getSubscription_date().getTime()>subscribedDate.getValue().getTime()){
						valid=false;
						setRequiredError(subscribedDate, getPropertyName("invalid_selection"), true);
					}
					else if(date.getTime()>=fDate.getTime()){
						
					}
					else{
						valid=false;
						setRequiredError(deadDate, getPropertyName("invalid_selection"), true);
					}
				}
			}
			else{
				if(subscribedDate.getValue().getTime()>fDate.getTime()){
					valid=false;
					setRequiredError(deadDate, getPropertyName("invalid_selection"), true);
				}
			}
			deadDate.setReadOnly(true);
		}
		
		return valid;
	}

	protected String getFileName(long id) {
		String fileName="";
		try {
			String filename = scdao.getImageName(id);
			String[] fileArray = filename.split(",");

			for (int i = 0; i < fileArray.length; i++) {
				if(i==0){
					fileName=VaadinServlet.getCurrent().getServletContext().getRealPath("/")
							+ "VAADIN/themes/testappstheme/VehicleImages/"
							+ fileArray[i].trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	public boolean isDate(){
		boolean valid=true;
		if (deadField.getValue() == null || deadField.getValue().equals("")) {
			amountField.setValue("0");
		} 
		else {
			try {
				if (toDouble(deadField.getValue().toString()) <= 0) {
					deadField.setValue("0");
				} 
			} 
			catch (Exception e) {
				deadField.setValue("0");
			}
		}
		if(deadCombo.getValue() == null || deadCombo.getValue().toString().equals("0") ||deadCombo.getValue().toString().equals("")){
			valid=false;
		}
		else{
			setRequiredError(deadCombo, null, false);
		}
		return valid;
	}
	
	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAllErrors(){
		subscriptionNoCombo.setComponentError(null);
		rentOutCombo.setComponentError(null);
		subscriptionCombo.setComponentError(null);
		subscribedDate.setComponentError(null);
		deadField.setComponentError(null);
		deadCombo.setComponentError(null);
		deadDate.setComponentError(null);
		subscriberCombo.setComponentError(null);
		amountField.setComponentError(null);
		periodCombo.setComponentError(null);
		detailsArea.setComponentError(null);
	}
	
	public void resetAll(){
		accountRadio.setReadOnly(false);
		rentRadio.setReadOnly(false);
		rentLayout.setVisible(false);
		subscriptionCombo.setReadOnly(false);
		rentOutCombo.setValue(null);
		subscriptionCombo.removeValueChangeListener(sListener);
		subscriptionCombo.setValue(null);
		subscriptionCombo.addValueChangeListener(sListener);
		subscribedDate.removeValueChangeListener(dlistener);
		subscribedDate.setValue(getWorkingDate());
		subscribedDate.addValueChangeListener(dlistener);
		deadField.setValue("");
		deadCombo.removeValueChangeListener(cListener);
		deadCombo.setValue(null);
		deadCombo.addValueChangeListener(cListener);
		deadDate.setNewValue(getWorkingDate());
		accountRadio.setValue((long)2);
		rentRadio.setValue((long)1);
		subscriberCombo.setValue(null);
		loadSubscriberExpenditure((long)0);
		amountField.setValue("");
		periodCombo.setValue(null);
		detailsArea.setValue("");
		save.setVisible(true);
		delete.setVisible(false);
		update.setVisible(false);
		print.setVisible(false);
	}
	
	public boolean isDateValid(){
		boolean valid=true;
		try{
			if(isDate()){
			}
		}
		catch(Exception e){
			deadDate.setReadOnly(true);
			e.printStackTrace();
		}
		return valid;
	}
	
}
