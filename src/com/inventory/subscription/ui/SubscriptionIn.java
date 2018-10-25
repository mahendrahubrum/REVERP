package com.inventory.subscription.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionExpenditureModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionInventoryDetailsModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
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
import com.webspark.Components.SFormLayout;
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

public class SubscriptionIn extends SparkLogic 
{
	private static final long serialVersionUID = 4933607204036137737L;
	SPanel mainPanel;
	SHorizontalLayout mainHorizontalLayout;
	SLabel label;
	SVerticalLayout mainLayout;
	SVerticalLayout tableLayout;
	SFormLayout form;
	SHorizontalLayout subscriptionNoLayout;
	SHorizontalLayout buttonLayout,imageLayout;
	SHorizontalLayout deadLayout;
	SComboField subscriptionCombo,subscriptionNoCombo,periodCombo,subscriberCombo,deadCombo;
	STextField deadField,amountField;
	STextArea detailsArea;
	SRadioButton accountRadio,rentRadio;
	SDateField deadDate,subscribedDate;
	SButton save,update,delete,createNew,print;
	SubscriptionConfigurationDao dao;
	SubscriptionCreationDao scdao;
	SubscriptionInDao sidao;
	STable table;
	SImage vehicleImage;
	String vehicleImageName = "";
	String dir;
	String odir;
	private Object[] allHeaders,visibleHeaders;
	static String TBC_PID = "Parent Id";
	static String TBC_EID = "Expendeture Id";
	static String TBC_SELECT = "";
	static String TBC_NAME = "Item Condition";
	static String TBC_DETAILS = "Details";
	RentalInvoiceBean bean;
	ValueChangeListener noListener,sListener;
	ValueChangeListener dlistener,cListener;
	
	
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI()
	{
		try
		{
			form=new SFormLayout();
			form.setSpacing(true);
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(420, 560);
			label=new SLabel(getPropertyName("rental_type"));
//			setSize(900, 530);
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionCreationDao();
			sidao=new SubscriptionInDao();
			dao=new SubscriptionConfigurationDao();
			imageLayout=new SHorizontalLayout();
			imageLayout.setSpacing(true);
			imageLayout.setMargin(true);
			dir = VaadinServlet.getCurrent().getServletContext().getRealPath("/")
					+ "VAADIN/themes/testappstheme/VehicleImages/";
			odir = VaadinServlet.getCurrent().getServletContext().getRealPath("/").trim()+"VAADIN/themes/testappstheme/Images/no_image.png".trim();
			vehicleImageName = "images/no_image.png";
			vehicleImage = new SImage(null, new ThemeResource(vehicleImageName));
			vehicleImage.setWidth("100");
			vehicleImage.setHeight("100");
			vehicleImage.setImmediate(true);
//			imageLayout.addComponent(vehicleImage);
			imageLayout.setStyleName("layout_border");
			mainHorizontalLayout=new SHorizontalLayout();
			mainHorizontalLayout.setSpacing(true);
			tableLayout=new SVerticalLayout();
			tableLayout.setSpacing(true);
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			subscriptionNoLayout=new SHorizontalLayout(getPropertyName("rent_in_out"));
			subscriptionNoLayout.setSpacing(true);
			
			deadLayout=new SHorizontalLayout(getPropertyName("rental_deadline"));
			deadLayout.setSpacing(true);
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
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
			subscriptionNoCombo=new SComboField(null, 175);
			subscriptionNoCombo.setRequired(true);
			reloadSubscriptionNoCombo((long)	0);
			subscriptionCombo=new SComboField(getPropertyName("rental_item"), 200);
			subscriptionCombo.setInputPrompt(getPropertyName("select"));
			subscriptionCombo.setRequired(true);
			reloadSubscriptionCombo(0);
			subscribedDate=new SDateField(getPropertyName("rental_date"), 200);
			subscribedDate.setRequired(true);
			subscribedDate.setImmediate(true);
			subscribedDate.setValue(getWorkingDate());
			deadField=new STextField(null, 75);
			deadField.setRequired(true);
			deadField.setInputPrompt(getPropertyName("period"));
			deadCombo=new SComboField(null, 125,SConstants.periodType,"key","value",true,getPropertyName("select"));
			deadDate=new SDateField(getPropertyName("rental_deadline_date"), 200);
			deadDate.setRequired(true);
			deadDate.setImmediate(true);
			deadDate.setValue(getWorkingDate());
			deadDate.setReadOnly(true);
			accountRadio = new SRadioButton(getPropertyName("account_type"), 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long) 2);
			accountRadio.setHorizontal(true);
			rentRadio = new SRadioButton(getPropertyName("rent_type"), 200,SConstants.specialRentalTypeList, "key", "value");
			rentRadio.setHorizontal(true);
			rentRadio.setValue((long)1);
			rentRadio.setReadOnly(true);
			subscriberCombo=new SComboField(getPropertyName("subscriber"), 200);
			subscriberCombo.setRequired(true);
			subscriberCombo.setInputPrompt(getPropertyName("select"));
			loadSubscriberIncome(0);
			amountField=new STextField(getPropertyName("rental_rate"), 200);
			amountField.setRequired(true);
			amountField.setInputPrompt(getPropertyName("rental_rate"));
			periodCombo=new SComboField(getPropertyName("period_type"), 200,SConstants.period,"key","value",true,getPropertyName("select"));
			detailsArea=new STextArea(getPropertyName("rental_details"), 200);
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
			subscriptionNoLayout.addComponent(subscriptionNoCombo);
			subscriptionNoLayout.addComponent(createNew);
			deadLayout.addComponent(deadField);
			deadLayout.addComponent(deadCombo);
			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(print);
			
			/*****************************************************************************************************///Adding to Main Layout
			form.addComponent(subscriptionNoLayout);
			form.addComponent(subscriptionCombo);
			form.addComponent(label);
			form.addComponent(subscribedDate);
			form.addComponent(deadLayout);
			form.addComponent(deadDate);
			form.addComponent(accountRadio);
			form.addComponent(rentRadio);
			form.addComponent(subscriberCombo);
			form.addComponent(amountField);
			form.addComponent(periodCombo);
			form.addComponent(detailsArea);
			form.addComponent(buttonLayout);
			rentRadio.setVisible(false);
			mainHorizontalLayout.addComponent(form);
			tableLayout.addComponent(imageLayout);
			tableLayout.addComponent(table);
			mainHorizontalLayout.addComponent(tableLayout);
			mainHorizontalLayout.setMargin(true);
			tableLayout.setVisible(false);
			mainPanel.setContent(mainHorizontalLayout);
			
			/*****************************************************************************************************///Listeners
			createNew.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try{
						subscriptionNoCombo.setValue((long)0);
						loadSubscriberIncome(0);
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
						subscriberCombo.removeAllItems();
						imageLayout.removeAllComponents();
						String fileName="";
						if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0")){
							mdl=sidao.getSubscriptionInModel(toLong(subscriptionNoCombo.getValue().toString()));
							if(mdl!=null){
								accountRadio.setReadOnly(false);
								rentRadio.setReadOnly(false);
								subscriptionCombo.removeValueChangeListener(sListener);
								subscriptionCombo.setValue(mdl.getSubscription().getId());
								if(mdl.getSubscription().getSpecial()==1) {
									label.setValue(getPropertyName("special_rental"));
								}
								else {
									label.setValue(getPropertyName("normal_rental"));
								}
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
								
//								long sid=toLong(subscriptionCombo.getValue().toString());
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
									setSize(420, 560);
									center();
									vehicleImage.setSource(new FileResource(new File("")));
									tableLayout.setVisible(false);
									rentRadio.setVisible(false);
									rentRadio.setReadOnly(true);
									loadSubscriberExpenditure(mdl.getSubscriber());
								}
								else if(type==(long)2){
									setSize(420, 560);
									center();
									vehicleImage.setSource(new FileResource(new File("")));
									tableLayout.setVisible(false);
									rentRadio.setVisible(false);
									rentRadio.setReadOnly(true);
									loadSubscriberIncome(mdl.getSubscriber());
								}
								else if(type==(long)3){
									tableLayout.setVisible(true);
									setSize(900, 560);
									center();
									rentRadio.setVisible(true);
									rentRadio.setReadOnly(false);
									long rentType=scdao.getRentType(mdl.getSubscription().getId(), getOfficeID());
									if(rentType==2){
										rentRadio.setValue((long)2);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome(mdl.getSubscriber());
									}
									else{
										rentRadio.setValue((long)1);
										rentRadio.setReadOnly(true);
										loadSubscriberTransportation(mdl.getSubscriber());
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
					}
				}
			};
			subscriptionNoCombo.addValueChangeListener(noListener);
			
			sListener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(subscriptionCombo.getValue()!=null && !subscriptionCombo.getValue().toString().equals("")){
							accountRadio.setReadOnly(false);
							rentRadio.setReadOnly(false);
							imageLayout.removeAllComponents();
							String fileName="";
							long sid=toLong(subscriptionCombo.getValue().toString());
							SubscriptionCreationModel mdl=scdao.getCreationModel(sid);
							try {
								String filename = scdao.getImageName(mdl.getId());
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
							table.removeAllItems();
							loadTable();
							if(mdl!=null){
								if(mdl.getAvailable()==0){
									accountRadio.setValue(mdl.getAccount_type());
									if(mdl.getSpecial()==1) {
										label.setValue(getPropertyName("special_rental"));
									}
									else {
										label.setValue(getPropertyName("normal_rental"));
									}
									long type=toLong(accountRadio.getValue().toString());
									if(type==1){
										setSize(420, 560);
										center();
										vehicleImage.setSource(new FileResource(new File("")));
										rentRadio.setVisible(false);
										tableLayout.setVisible(false);
										rentRadio.setReadOnly(true);
										loadSubscriberExpenditure((long)0);
									}
									else if(type==2){
										setSize(420, 560);
										center();
										vehicleImage.setSource(new FileResource(new File("")));
										rentRadio.setVisible(false);
										tableLayout.setVisible(false);
										rentRadio.setReadOnly(true);
										loadSubscriberIncome((long)0);
									}
									else if(type==3){
										rentRadio.setVisible(true);
										tableLayout.setVisible(true);
										setSize(900, 560);
										center();
										rentRadio.setReadOnly(false);
										long rentType=scdao.getRentType(sid, getOfficeID());
										if(rentType==2){
											rentRadio.setValue((long)2);
											rentRadio.setReadOnly(true);
											loadSubscriberIncome((long)0);
										}
										else{
											rentRadio.setValue((long)1);
											rentRadio.setReadOnly(true);
											loadSubscriberTransportation((long)0);
										}
										rentRadio.setReadOnly(true);
									}
									accountRadio.setReadOnly(true);
								}
								else{
									SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
					}
				}
			};
			subscriptionCombo.addValueChangeListener(sListener);
			
			dlistener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(isDate()){
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
							deadDate.setNewValue(cal.getTime());
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
						if(isDate()){
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
							deadDate.setNewValue(cal.getTime());
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
						SubscriptionInModel mdl;
						SubscriptionCreationModel scmdl;
						deadDate.setReadOnly(false);
						accountRadio.setReadOnly(false);
						rentRadio.setReadOnly(false);
						if(isValid())
						{
							long type=toLong(accountRadio.getValue().toString());
							long sd=toLong(subscriptionCombo.getValue().toString());
							long rentType=scdao.getRentType(sd, getOfficeID());
							mdl=new SubscriptionInModel();
							scmdl=scdao.getCreationModel(sd);
							if(scmdl!=null){
								if(scmdl.getAvailable()==0){
									if(rentType==3){
										scmdl.setAvailable(toLong("1"));
										mdl.setAvailable(toLong("1"));
									}
									else{
										scmdl.setAvailable(toLong("5"));
										mdl.setAvailable(toLong("0"));
									}
									mdl.setSubscription_no(getNextSequence("Subscription", getLoginID()));
									mdl.setSubscription(new SubscriptionCreationModel(sd));
									mdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(subscribedDate.getValue()));
									mdl.setQuantity(Integer.parseInt(deadField.getValue().toString()));
									mdl.setPeriod_type(Integer.parseInt(deadCombo.getValue().toString()));
									mdl.setClosing_date(CommonUtil.getSQLDateFromUtilDate(deadDate.getValue()));
									mdl.setAccount_type(type);
									mdl.setReturn_date(null);
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
//									if(childList!=null && childList.size()!=0)
										mdl.setSubscription_details_list(childList);
									long sid=sidao.save(mdl,scmdl);
									imageLayout.removeAllComponents();
									reloadSubscriptionNoCombo(sid);
									SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								}
								else{
									SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
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
						SubscriptionInModel mdl;
						SubscriptionCreationModel scmdl;
						deadDate.setReadOnly(false);
						accountRadio.setReadOnly(false);
						rentRadio.setReadOnly(false);
						if(isValid())
						{
							if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0"))
							{
								long id=toLong(subscriptionNoCombo.getValue().toString());
								long type=toLong(accountRadio.getValue().toString());
								mdl=sidao.getSubscriptionInModel(id);
								if(mdl!=null){
									if(mdl.getLock()!=1){
										if(mdl.getReturn_date()==null){
											long sd=mdl.getSubscription().getId();
											scmdl=scdao.getCreationModel(sd);
											long rentType=scdao.getRentType(sd, getOfficeID());
											if(scmdl!=null){
												if(scmdl.getAvailable()==2){
													SNotification.show(getPropertyName("cannot_update"),Type.WARNING_MESSAGE);
													reloadSubscriptionNoCombo(id);
												}
												else{
													if(rentType==3){
														scmdl.setAvailable(toLong("1"));
														mdl.setAvailable(toLong("1"));
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
													mdl.setSubscription_details_list(childList);
													long uid=sidao.update(mdl,scmdl);
													imageLayout.removeAllComponents();
													reloadSubscriptionNoCombo(uid);
													SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
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
								else{
									SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						deadDate.setReadOnly(true);
						accountRadio.setReadOnly(true);
						rentRadio.setReadOnly(true);
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
						final SubscriptionInModel mdl;
						final SubscriptionCreationModel scmdl;
						if(subscriptionNoCombo.getValue() != null && !subscriptionNoCombo.getValue().toString().equals("0")){
							long id=toLong(subscriptionNoCombo.getValue().toString());
							mdl=sidao.getSubscriptionInModel(id);
							if(mdl!=null){
								if(mdl.getLock()!=1){
									if(mdl.getReturn_date()==null){
										long sd=mdl.getSubscription().getId();
										scmdl=scdao.getCreationModel(sd);
										long rentType=scdao.getRentType(sd, getOfficeID());
										if(scmdl!=null){
											if(scmdl.getAvailable()==2){
												SNotification.show(getPropertyName("cannot_delete"),Type.WARNING_MESSAGE);
												reloadSubscriptionNoCombo(id);
											}
											else{
												if(rentType==3){
													scmdl.setAvailable(toLong("0"));
												}
												else{
													scmdl.setAvailable(toLong("0"));
												}
												ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
													public void onClose(ConfirmDialog dialog) {
														if (dialog.isConfirmed()) {
															try {
																
																sidao.delete(mdl,scmdl);
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
										}
									}
									else{
										SNotification.show(getPropertyName("rental_closed"),Type.WARNING_MESSAGE);
									}
								}
								else{
									SNotification.show(getPropertyName("payment_made"),Type.WARNING_MESSAGE);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
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
						setSize(420, 560);
						center();
						loadSubscriberExpenditure(0);
					}
					else if(type==(long)2){
						tableLayout.setVisible(false);
						vehicleImage.setSource(new FileResource(new File("")));
						setSize(420, 560);
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
											bean.setNo(mdl.getSubscription_no());
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
										bean.setNo(mdl.getSubscription_no());
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
			idList.addAll(sidao.getAllInSubscriptions(getOfficeID()));
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
	public void reloadSubscriptionCombo(long id)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.addAll(sidao.getAllSubscriptions(getOfficeID()));
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
			deadField.setValue("0");
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
		if(deadField.getValue().toString().equals("") || deadField.getValue()==null){
			valid=false;
		}
		else{
			if(toDouble(deadField.getValue().toString())<0){
				valid=false;
			}
			setRequiredError(deadField, null, false);
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
		rentRadio.setVisible(false);
		label.setValue("");
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
	
	
}
