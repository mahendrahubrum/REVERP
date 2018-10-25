package com.inventory.subscription.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.subscription.bean.RentalInvoiceBean;
import com.inventory.subscription.dao.ExpenditureDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.dao.SubscriptionLedgerSubscriberDao;
import com.inventory.subscription.dao.SubscriptionOutDao;
import com.inventory.subscription.dao.SubscriptionPaymentDao;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionExpenditureModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionInventoryDetailsModel;
import com.inventory.subscription.model.SubscriptionPaymentDetailsModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
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
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;

public class SubscriptionPayment extends SparkLogic{

	private static final long serialVersionUID = -6759183441175493614L;
	SPanel mainPanel;
	SFormLayout form;
	SVerticalLayout mainLayout;
	SVerticalLayout main;
	SHorizontalLayout mainHorizontalLayout;
	SHorizontalLayout buttonLayout;
	SComboField subscriptionCombo,fromAccountCombo,subscriberCombo;
	SRadioButton accountRadio,cashChequeRadio,rentRadio;
	STextField amountField,paymentField,chequeNumberField;
	STextField creditField;
	STextField id,tid,fid,cid;
	SCheckBox returnCheck;
	SDateField dateField,chequeDate,returnDate;
	SButton payment,update,delete,print;
	SubscriptionCreationDao scdao;
	SubscriptionInDao sidao;
	SubscriptionOutDao sodao;
	SubscriptionPaymentDao dao;
	Date issueDate=null;
	long period=0;
	int diffDays=0;
	STable table;
	double totalAmount=0,totalCredit=0,totalPayment=0;
	
	STable dtable;
	SImage vehicleImage;
	String vehicleImageName = "";
	String dir;
	String odir;
	SHorizontalLayout imageLayout;
	SVerticalLayout tableLayout;
	private Object[] allDHeaders,visibleDHeaders;
	static String TBL_PID = "Parent Id";
	static String TBL_EID = "Expendeture Id";
	static String TBL_SELECT = "";
	static String TBL_NAME = "Item Condition";
	static String TBL_DETAILS = "Details";
	static String TBL_REMARK = "Remarks";
	
	private Object[] allHeaders,visibleHeaders;
	static String TBC_SN = "#";
	static String TBC_ID = "Id";
	static String TBC_DUE = "Due Amount";
	static String TBC_PAYMENT = "Paid Amount";
	static String TBC_CASH_CHEQUE = "Cash or Cheque";
	static String TBC_CHEQUEDATE = "Cheque Date";
	static String TBC_CHEQUENO = "Cheque Number";
	static String TBC_FROMACCOUNT = "From Account";
	static String TBC_PDATE = "Payment Date";
	static String TBC_SDATE = "Subscription Date";
	static String TBC_TID = "Transaction ID";
	static String TBC_FID = "Final Transaction ID";
	static String TBC_CID = "Credit Transaction ID";
	SettingsValuePojo settings;

	WrappedSession session;
	@SuppressWarnings({ "rawtypes", "unchecked", "serial", "deprecation" })
	@Override
	public SPanel getGUI() {
		try{
			mainPanel=new SPanel();
//			mainPanel.setSizeFull();
			setSize(1100, 480);
			form=new SFormLayout();
			form.setSpacing(true);
			center();
			session = getHttpSession();

			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionCreationDao();
			sidao=new SubscriptionInDao();
			sodao=new SubscriptionOutDao();
			dao=new SubscriptionPaymentDao();
			
			imageLayout=new SHorizontalLayout();
			dir = VaadinServlet.getCurrent().getServletContext().getRealPath("/")
					+ "VAADIN/themes/testappstheme/VehicleImages/";
			vehicleImageName = "VehicleImages/blank.png";
			odir = VaadinServlet.getCurrent().getServletContext().getRealPath("/").trim()+"VAADIN/themes/testappstheme/Images/no_image.png".trim();
//			vehicleImage = new SImage(null, new ThemeResource(vehicleImageName));
//			vehicleImage.setWidth("50");
//			vehicleImage.setHeight("50");
//			vehicleImage.setImmediate(true);
//			imageLayout.addComponent(vehicleImage);
			imageLayout.setStyleName("layout_border");
			tableLayout=new SVerticalLayout();
			tableLayout.setSpacing(true);
			allHeaders=new Object[] {TBC_SN,TBC_ID,TBC_DUE,TBC_PAYMENT,TBC_CASH_CHEQUE,TBC_CHEQUEDATE,TBC_CHEQUENO,TBC_FROMACCOUNT,TBC_PDATE,TBC_SDATE,TBC_TID,TBC_FID,TBC_CID};
			visibleHeaders=new Object[] {TBC_SN,TBC_PAYMENT,TBC_PDATE};
			allDHeaders = new String[] {TBL_PID, TBL_EID, TBL_SELECT,TBL_NAME,TBL_DETAILS,TBL_REMARK};
			visibleDHeaders = new String[] {TBL_SELECT,TBL_NAME,TBL_DETAILS,TBL_REMARK};
			
			dtable = new STable(null, 670, 175);
			dtable.addContainerProperty(TBL_PID, Long.class, null, TBL_PID, null, Align.CENTER);
			dtable.addContainerProperty(TBL_EID, Long.class, null, TBL_EID, null, Align.CENTER);
			dtable.addContainerProperty(TBL_SELECT, SCheckBox.class, null, TBL_SELECT, null, Align.CENTER);
			dtable.addContainerProperty(TBL_NAME, String.class, null, getPropertyName("item_condition"), null, Align.LEFT);
			dtable.addContainerProperty(TBL_DETAILS, String.class, null, getPropertyName("details"), null, Align.LEFT);
			dtable.addContainerProperty(TBL_REMARK, STextField.class, null, getPropertyName("remark"), null, Align.LEFT);
			dtable.setColumnExpandRatio(TBL_SELECT, (float)0.2);
			dtable.setColumnExpandRatio(TBL_NAME, (float)2);
			dtable.setColumnExpandRatio(TBL_DETAILS, (float)2);
			dtable.setColumnExpandRatio(TBL_REMARK, (float)2);
			dtable.setVisibleColumns(visibleDHeaders);
			dtable.setSelectable(true);	
			dtable.setImmediate(true);
			loadTable();
			main=new SVerticalLayout();
			mainHorizontalLayout=new SHorizontalLayout();
			mainLayout=new SVerticalLayout();
			table=new STable("Last Payments",670,175);
			mainLayout.setSpacing(true);
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			mainPanel.setContent(mainLayout);
			subscriptionCombo=new SComboField(getPropertyName("rental"), 200);
			subscriptionCombo.setRequired(true);
			subscriptionCombo.setInputPrompt(getPropertyName("select"));
			subscriberCombo=new SComboField(getPropertyName("subscriber"), 200);
			subscriberCombo.setInputPrompt(getPropertyName("select"));
			dateField=new SDateField(getPropertyName("payment_date"), 200);
			dateField.setRequired(true);
			dateField.setImmediate(true);
			dateField.setValue(getWorkingDate());
			amountField=new STextField(getPropertyName("amount_due"), 200);
			amountField.setInputPrompt(getPropertyName("amount_due"));
			amountField.setReadOnly(true);
			accountRadio = new SRadioButton(getPropertyName("account_type"), 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long) 2);
			accountRadio.setRequired(true);
			accountRadio.setHorizontal(true);
			List rentList = new ArrayList();
			rentList.add(new KeyValue((long) 1, getPropertyName("rent_in_out")));
			rentList.add(new KeyValue((long) 2, getPropertyName("rent_in_to_out")));
			rentRadio = new SRadioButton(getPropertyName("rent_type"), 200,rentList, "key", "value");
			rentRadio.setHorizontal(true);
			rentRadio.setValue((long)1);
			cashChequeRadio = new SRadioButton(getPropertyName("cash_cheque"), 200,SConstants.cashOrCheckList, "intKey", "value");
			cashChequeRadio.setHorizontal(true);
			cashChequeRadio.setValue(1);
			fromAccountCombo=new SComboField(getPropertyName("from_account"), 200);
			fromAccountCombo.setRequired(true);
			fromAccountCombo.setInputPrompt(getPropertyName("select"));
			chequeDate=new SDateField(getPropertyName("cheque_date"), 200);
			chequeDate.setRequired(true);
			chequeDate.setImmediate(true);
			chequeDate.setValue(getWorkingDate());
			returnDate=new SDateField(getPropertyName("return_date"), 200);
			returnDate.setRequired(true);
			returnDate.setImmediate(true);
			returnDate.setReadOnly(true);
			returnCheck=new SCheckBox(getPropertyName("return_item"), false);
			paymentField=new STextField(getPropertyName("payment_amount"), 200);
			paymentField.setRequired(true);
			paymentField.setInputPrompt(getPropertyName("payment_amount"));
			chequeNumberField=new STextField(getPropertyName("cheque_no"), 200);
			chequeNumberField.setRequired(true);
			chequeNumberField.setInputPrompt(getPropertyName("cheque_no"));
			creditField=new STextField(getPropertyName("credit_amount"), 200);
			creditField.setInputPrompt(getPropertyName("credit_amount"));
			creditField.setValue("0");
			creditField.setVisible(false);
			id=new STextField();
			id.setVisible(false);
			tid=new STextField();
			tid.setVisible(false);
			fid=new STextField();
			fid.setVisible(false);
			cid=new STextField();
			cid.setVisible(false);
			payment=new SButton(getPropertyName("make_payment"));
			update=new SButton(getPropertyName("update"));
			update.setVisible(false);
			delete=new SButton(getPropertyName("delete"));
			delete.setVisible(false);
			print=new SButton(getPropertyName("print"));
			print.setVisible(true);
			reloadSubscriptionInCombo((long)0,(long)2);
			loadSubscriberIncome(0);
			subscriptionCombo.setValue(null);
			loadFromAccountCombo();
			/*****************************************************************************************************///Adding to Layout
			chequeDate.setVisible(false);
			chequeNumberField.setVisible(false);
			returnDate.setVisible(false);
			rentRadio.setVisible(false);
			buttonLayout.addComponent(payment);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(print);
			buttonLayout.addComponent(id);
			buttonLayout.addComponent(tid);
			buttonLayout.addComponent(fid);
			buttonLayout.addComponent(cid);
			buttonLayout.setComponentAlignment(payment, Alignment.MIDDLE_CENTER);
			buttonLayout.setComponentAlignment(update, Alignment.MIDDLE_CENTER);
			buttonLayout.setComponentAlignment(delete, Alignment.MIDDLE_CENTER);
			buttonLayout.setComponentAlignment(print, Alignment.MIDDLE_CENTER);
			/*****************************************************************************************************///Adding to Main Layout
			form.addComponent(accountRadio);
			form.addComponent(rentRadio);
			form.addComponent(subscriberCombo);
			form.addComponent(subscriptionCombo);
			form.addComponent(amountField);
			form.addComponent(creditField);
			form.addComponent(cashChequeRadio);
			form.addComponent(dateField);
			form.addComponent(fromAccountCombo);
			form.addComponent(chequeDate);
			form.addComponent(chequeNumberField);
			form.addComponent(paymentField);
			form.addComponent(returnCheck);
			form.addComponent(returnDate);
			form.addComponent(imageLayout);
			
			mainHorizontalLayout.addComponent(form);
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DUE, Double.class, null, TBC_DUE, null, Align.CENTER);
			table.addContainerProperty(TBC_PAYMENT, Double.class, null, getPropertyName("paid_amount"), null, Align.CENTER);
			table.addContainerProperty(TBC_CASH_CHEQUE, Integer.class, null, TBC_CASH_CHEQUE, null, Align.CENTER);
			table.addContainerProperty(TBC_CHEQUEDATE, Date.class, null, TBC_CHEQUEDATE, null, Align.CENTER);
			table.addContainerProperty(TBC_CHEQUENO, String.class, null, TBC_CHEQUENO, null, Align.CENTER);
			table.addContainerProperty(TBC_FROMACCOUNT, Long.class, null, TBC_FROMACCOUNT, null, Align.CENTER);
			table.addContainerProperty(TBC_PDATE, Date.class, null, getPropertyName("payment_date"), null, Align.CENTER);
			table.addContainerProperty(TBC_SDATE, Date.class, null, TBC_SDATE, null, Align.CENTER);
			table.addContainerProperty(TBC_TID, Long.class, null, TBC_TID, null, Align.CENTER);
			table.addContainerProperty(TBC_FID, Long.class, null, TBC_FID, null, Align.CENTER);
			table.addContainerProperty(TBC_CID, Long.class, null, TBC_CID, null, Align.CENTER);
			table.setVisibleColumns(visibleHeaders);
			table.setSelectable(true);
			table.setColumnExpandRatio(TBC_SN, (float) 0.4);
			table.setColumnExpandRatio(TBC_ID, (float) 0.4);
			table.setColumnExpandRatio(TBC_DUE, 1);
			table.setColumnExpandRatio(TBC_PAYMENT,1);
			table.setColumnExpandRatio(TBC_CASH_CHEQUE,1);
			table.setColumnExpandRatio(TBC_CHEQUEDATE,1);
			table.setColumnExpandRatio(TBC_CHEQUENO,1);
			table.setColumnExpandRatio(TBC_FROMACCOUNT,1);
			table.setColumnExpandRatio(TBC_PDATE,1);
			table.setColumnExpandRatio(TBC_SDATE,1);
			table.setColumnExpandRatio(TBC_TID,1);
			dtable.setVisible(false);
			imageLayout.setVisible(false);
			tableLayout.addComponent(table);
			tableLayout.addComponent(dtable);
			mainHorizontalLayout.addComponent(tableLayout);
			mainHorizontalLayout.setSpacing(true);
			main.addComponent(mainHorizontalLayout);
			main.addComponent(buttonLayout);
			main.setComponentAlignment(mainHorizontalLayout, Alignment.MIDDLE_CENTER);
			main.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			main.setSpacing(true);
			main.setMargin(true);
			main.setSizeFull();
			mainPanel.setContent(main);
	
			cashChequeRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						long value=toLong(cashChequeRadio.getValue().toString());
						if(value==1){
							chequeDate.setVisible(false);
							chequeDate.setValue(getWorkingDate());
							chequeNumberField.setVisible(false);
							chequeNumberField.setValue("");
						}
						else{
							chequeDate.setVisible(true);
							chequeNumberField.setVisible(true);
						}
						loadFromAccountCombo();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			rentRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						imageLayout.removeAllComponents();
						long rent=toLong(rentRadio.getValue().toString());
						if(rent==1){
							reloadSubscriptionInCombo((long)0, (long)3);
							loadSubscriberTransportationRentInOrOut(0);
							vehicleImage.setSource(new FileResource(new File(odir)));
							resetAll();
						}
						else{
							reloadSubscriptionOutCombo((long)0, (long)3);
							loadSubscriberIncome(0);
							vehicleImage.setSource(new FileResource(new File(odir)));
							resetAll();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						imageLayout.removeAllComponents();
						long accountType=toLong(accountRadio.getValue().toString());
						if(accountType==1){
							setSize(1100, 480);
							center();
							rentRadio.setVisible(false);
							imageLayout.setVisible(false);
							dtable.setVisible(false);
							reloadSubscriptionInCombo((long)0, accountType);
							loadSubscriberExpenditure(0);
							resetAll();
						}
						else if(accountType==2){
							setSize(1100, 480);
							center();
							rentRadio.setVisible(false);
							imageLayout.setVisible(false);
							dtable.setVisible(false);
							reloadSubscriptionInCombo((long)0, accountType);
							loadSubscriberIncome(0);
							resetAll();
						}
						else if(accountType==3){
							rentRadio.setVisible(true);
							setSize(1100, 580);
							center();
							long rent=toLong(rentRadio.getValue().toString());
							if(rent==1){
								reloadSubscriptionInCombo((long)0, (long)3);
								loadSubscriberTransportationRentInOrOut(0);
								resetAll();
							}
							else if(rent==2){
								reloadSubscriptionOutCombo((long)0, (long)3);
								loadSubscriberIncome(0);
								resetAll();
							}
							imageLayout.setVisible(true);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			subscriberCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(subscriberCombo.getValue()!=null && !subscriberCombo.getValue().toString().equals("0")){
							SubscriptionInModel mdl=sidao.getSubscription((Long)subscriberCombo.getValue(),(Long)accountRadio.getValue());
							if(mdl!=null){
								subscriptionCombo.setValue(mdl.getId());
							}
							else{
								subscriptionCombo.setValue(null);
								resetAll();
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			subscriptionCombo.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) { 
					try{
						payment.setEnabled(true);
						update.setEnabled(true);
						delete.setEnabled(true);
						print.setEnabled(true);
						print.setVisible(true);
						creditField.setVisible(false);
						totalAmount=0;
						totalCredit=0;
						totalPayment=0;
						SubscriptionCreationModel scmdl;
						SubscriptionInModel simdl;
						SubscriptionPaymentModel mdl;
						table.removeAllItems();
						imageLayout.removeAllComponents();
						List list=new ArrayList();
						String fileName="";
						if(subscriptionCombo.getValue()!=null && !subscriptionCombo.getValue().toString().equals("0")){
							long sid=toLong(subscriptionCombo.getValue().toString());
							double rate=0,amountDue=0,previousDue=0,previousPaid=0;
							if(sid!=0){
								simdl=sidao.getSubscriptionInModel(sid);
								list=dao.getAllTransactions(sid);
								if(simdl!=null){
									scmdl=scdao.getCreationModel(simdl.getSubscription().getId());
									if(simdl.getAccount_type()==(long)3){
										imageLayout.setVisible(true);
									}
									else{
										imageLayout.setVisible(false);
									}
									subscriberCombo.setValue(simdl.getSubscriber());
									period=getPeriod(sid);
									issueDate=findIssueDate(sid);
									diffDays=calculateDifferenceInDays(sid);
									previousDue=getPreviousDue(sid);
									previousPaid=getTotalPaid(sid);
									rate=simdl.getRate();
									amountDue=calculateSubscriptionAmount(diffDays, rate, period)-previousPaid;
									if(simdl.getSubscription().getSpecial()!=1) {
										creditField.setVisible(false);
										amountField.setNewValue(asString(amountDue));
										paymentField.setValue(asString(roundNumber(Math.abs(amountDue))));
									}
									else {
										creditField.setVisible(true);
										amountField.setNewValue(asString(0));
										paymentField.setValue(asString(0));
									}
									if(list.size()>0){
										for(int i=0;i<list.size();i++){
											long id=0;
											mdl=(SubscriptionPaymentModel)list.get(i);
											table.setVisibleColumns(allHeaders);
											if(dao.getCreditPaymentModel(mdl.getId())!=null) {
												id=dao.getCreditPaymentModel(mdl.getId()).getId();
											}
											else {
												id=0;
											}
											table.addItem(
													new Object[] {
															table.getItemIds().size() + 1,
															mdl.getId(),
															mdl.getAmount_due(),
															mdl.getAmount_paid(),
															mdl.getCash_cheque(),
															mdl.getCheque_date(),
															mdl.getCheque_number(),
															mdl.getFrom_account(),
															mdl.getPayment_date(),
															mdl.getSubscription_date(),
															mdl.getTransaction_id(),
															mdl.getCredit_transaction(),
															id},
													table.getItemIds().size() + 1);
										}
										table.setVisibleColumns(visibleHeaders);
									}
									try {
										String filename = simdl.getSubscription().getImage();
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
									if(simdl.getSubscription_details_list()!=null && simdl.getSubscription_details_list().size()!=0){
										Iterator it=simdl.getSubscription_details_list().iterator();
										SubscriptionInventoryDetailsModel detmdl;
										dtable.removeAllItems();
										dtable.setVisibleColumns(allDHeaders);
										String details;
										while(it.hasNext()){
											detmdl=(SubscriptionInventoryDetailsModel)it.next();
											if(detmdl.getDetails().trim().length()>0)
												details=detmdl.getDetails();
											else
												details="";
											dtable.addItem(new Object[]{
													simdl.getId(),
													detmdl.getExpenditure().getId(),
													new SCheckBox(null, true),
													detmdl.getExpenditure().getName(),
													details,
													new STextField(null,"")
											},dtable.getItemIds().size()+1);
										}
										dtable.setVisibleColumns(visibleDHeaders);
									}
									else{
										dtable.removeAllItems();
									}
									loadTable();
									if(simdl.getReturn_date()!=null){
										returnCheck.setValue(true);
										SubscriptionPaymentModel spmdl=dao.getModel(sid);
										if(spmdl.getSubscription_payment_list()!=null && spmdl.getSubscription_payment_list().size()!=0){
											Iterator it=spmdl.getSubscription_payment_list().iterator();
											SubscriptionPaymentDetailsModel detmdl;
											dtable.removeAllItems();
											dtable.setVisibleColumns(allDHeaders);
											while(it.hasNext()){
												detmdl=(SubscriptionPaymentDetailsModel)it.next();
												dtable.addItem(new Object[]{
														spmdl.getId(),
														detmdl.getExpenditure().getId(),
														new SCheckBox(null, true),
														detmdl.getExpenditure().getName(),
														detmdl.getDetails(),
														new STextField(null,detmdl.getRemarks())
												},dtable.getItemIds().size()+1);
											}
											dtable.setVisibleColumns(visibleDHeaders);
										}
										else{
											dtable.removeAllItems();
										}
										loadTable();
										returnDate.setNewValue(simdl.getReturn_date());
										amountField.setNewValue(asString(0));
										paymentField.setValue(asString(0));
										Notification.show(getPropertyName("rental_closed"),Type.WARNING_MESSAGE);
										if(scmdl.getAvailable()==2){ // Check from here is issue.. and do the same for table selection
											payment.setEnabled(false);
											update.setEnabled(false);
											delete.setEnabled(false);
											print.setEnabled(true);
										}
										else if(scmdl.getAvailable()==1){
											if(dao.getCount(toLong(subscriptionCombo.getValue().toString()))>(long)1){
												if(sid==scdao.getGreatestID(sid,getOfficeID())){
													payment.setEnabled(true);
													update.setEnabled(true);
													delete.setEnabled(true);
													print.setEnabled(true);
												}
												else{
													payment.setEnabled(false);
													update.setEnabled(false);
													delete.setEnabled(false);
													print.setEnabled(true);
												}
											}
											else{
												payment.setEnabled(true);
												update.setEnabled(true);
												delete.setEnabled(true);
												print.setEnabled(true);
											}
										}
										else {
											if(dao.getCount(toLong(subscriptionCombo.getValue().toString()))>(long)1){
												if(sid==scdao.getGreatestID(sid,getOfficeID())){
													payment.setEnabled(true);
													update.setEnabled(true);
													delete.setEnabled(true);
													print.setEnabled(true);
												}
												else{
													payment.setEnabled(false);
													update.setEnabled(false);
													delete.setEnabled(false);
													print.setEnabled(true);
												}
											}
											else{
												payment.setEnabled(true);
												update.setEnabled(true);
												delete.setEnabled(true);
												print.setEnabled(true);
											}
										}
									}
									else{
										returnCheck.setValue(false);
										returnDate.setNewValue(null);
										if(simdl.getSubscription().getSpecial()!=1) {
											amountField.setNewValue(asString(amountDue));
											paymentField.setValue(asString(roundNumber(Math.abs(amountDue))));
										}
										else {
											amountField.setNewValue(asString(0));
											paymentField.setValue(asString(0));
										}
									}
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						payment.setEnabled(false);
						
					}
				}
			});
			
			returnCheck.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(returnCheck.getValue()==true){
							creditField.setVisible(false);
							rentRadio.setVisible(true);
							returnDate.setReadOnly(false);
							totalAmount=0;
							totalCredit=0;
							totalPayment=0;
							payment.setCaption(getPropertyName("pay_close"));
							SubscriptionInModel mdl;
							SubscriptionPaymentModel spmdl;
							Calendar cal = Calendar.getInstance();
							double amountDue=0,previousDue=0,totalPaid=0;
							if(subscriptionCombo.getValue()!=null && !subscriptionCombo.getValue().toString().equals("")){
								long sid=toLong(subscriptionCombo.getValue().toString());
								mdl=sidao.getSubscriptionInModel(sid);
								if(mdl.getAccount_type()==(long)3){
									dtable.setVisible(true);
								}
								else{
									dtable.setVisible(false);
								}
								long per=mdl.getPeriod();
								double rat=mdl.getRate();
								previousDue=getPreviousDue(sid);
								totalPaid=getTotalPaid(sid);
								Date date=mdl.getSubscription_date();
								cal.setTime(date);
								int days=calculateEligibleDifferenceInDays(per, date);
								if(days>1) {
									cal.add(Calendar.DATE, days);
									returnDate.setValue(cal.getTime());
								}
								else {
									returnDate.setValue(cal.getTime());
								}
								if(days>1)
									totalAmount=calculateSubscriptionAmount(days, rat, per);
								else
									totalAmount=calculateSubscriptionAmount(1, rat, per);
								totalCredit=dao.getAllCreditPayment(mdl);
								totalPayment=totalPaid;
								amountDue=totalAmount-totalPaid;
								if(mdl.getSubscription().getSpecial()!=1) {
									creditField.setVisible(false);
									amountField.setNewValue(asString(amountDue));
									paymentField.setValue(asString(Math.abs(amountDue)));
								}
								else {
									creditField.setVisible(true);
									creditField.setValue("0");
									amountField.setNewValue("0");
									paymentField.setValue("0");
								}
								
							}
							returnDate.setReadOnly(true);
						}
						else{
							rentRadio.setVisible(false);
							dtable.setVisible(false);
							payment.setCaption(getPropertyName("make_payment"));
							double amountDue=0,previousDue=0,previousPaid=0;
							SubscriptionInModel mdl;
							if(subscriptionCombo.getValue()!=null && !subscriptionCombo.getValue().toString().equals("")){
								long sid=toLong(subscriptionCombo.getValue().toString());
								mdl=sidao.getSubscriptionInModel(sid);
								long per=mdl.getPeriod();
								double rat=mdl.getRate();
								previousDue=getPreviousDue(sid);
								previousPaid=getTotalPaid(sid);
								int days=calculateDifferenceInDays(sid);
								returnDate.setNewValue(null);
								amountDue=calculateSubscriptionAmount(days, rat, per)-previousPaid;
								if(mdl.getSubscription().getSpecial()!=1) {
									amountField.setNewValue(asString(amountDue));
									paymentField.setValue(asString(Math.abs(amountDue)));
								}
								else {
									amountField.setNewValue("0");
									paymentField.setValue("0");
								}
							}
							returnDate.setReadOnly(true);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						returnDate.setReadOnly(true);
					}
				}
			});
			
			chequeDate.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						chequeDate.setComponentError(null);
						Calendar calender=Calendar.getInstance();
						Calendar cal=Calendar.getInstance();
						cal.setTime(chequeDate.getValue());
						if(cal.get(Calendar.DATE)<calender.get(Calendar.DATE) ||
								cal.get(Calendar.MONTH) <calender.get(Calendar.MONTH)  ||
								cal.get(Calendar.YEAR) <calender.get(Calendar.YEAR) ){
							setRequiredError(chequeDate, getPropertyName("invalid_selection"), true);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			payment.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try{
							paymentField.setRequiredError(null);
							if(toDouble(paymentField.getValue().toString())>=0){
								boolean check=false;
								returnDate.setReadOnly(false);
								chequeNumberField.setComponentError(null);
								SubscriptionPaymentModel spmdl=new SubscriptionPaymentModel();
								SubscriptionCreationModel scmdl = null;
								SubscriptionInModel simdl = null;
								FinTransaction trans=new FinTransaction();
								FinTransaction trans1=new FinTransaction();
								FinTransaction trans2=new FinTransaction();
								amountField.setReadOnly(false);
								Date paymentDate=null,subscriptionDate=null,chequeDate=null;
								long fromAccountId=0,toAccountId=0,subscriber = 0,from=0,to=0;
								int creditDebit=0;
								long accountType=toLong(accountRadio.getValue().toString());
								long sid=toLong(subscriptionCombo.getValue().toString());
								double dueAmount=toDouble(amountField.getValue().toString());
								int cashCheque=toInt(cashChequeRadio.getValue().toString());
								long fromAccount=toLong(fromAccountCombo.getValue().toString());
								double paymentAmount=toDouble(paymentField.getValue().toString());
								double balanceAmount=dueAmount-paymentAmount;
								String chequeNumber=null;
								Calendar calendar=Calendar.getInstance();
								long rentType=sidao.getRentType(sid, getOfficeID());
								long type=sidao.getAvailablilty(sid, rentType);
								long inout=0;
								if(sid!=0){
									simdl=sidao.getSubscriptionInModel(sid);
									if(simdl!=null){
										subscriber=simdl.getSubscriber();
										scmdl=scdao.getCreationModel(simdl.getSubscription().getId());
										period=getPeriod(sid);
										issueDate=findIssueDate(sid);
										calendar.setTime(issueDate);
										diffDays=calculateDifferenceInDays(sid);
										paymentDate=dateField.getValue();
										subscriptionDate=simdl.getSubscription_date();
										long rent=toLong(rentRadio.getValue().toString());
										if(cashCheque==1){
											chequeNumber="";
											chequeDate=getWorkingDate();
										}
										else{
											chequeNumber=chequeNumberField.getValue().toString();
											chequeDate=SubscriptionPayment.this.chequeDate.getValue();
										}
										if(accountType==1){
											fromAccountId=fromAccount;
											toAccountId=subscriber;
//											if(returnCheck.getValue()==true){
												from=subscriber;
//												to=settings.getCASH_PAYABLE_ACCOUNT();
//											}
											inout=1;
											creditDebit=SConstants.DR;
										}
										else if(accountType==2){
											fromAccountId=subscriber;
											toAccountId=fromAccount;
//											if(returnCheck.getValue()==true){
//												from=settings.getCASH_RECEIVABLE_ACCOUNT();
												to=subscriber;
//											}
											inout=0;
											creditDebit=SConstants.CR;
										}
										else if(accountType==3){
											if(rentType==1){
												fromAccountId=fromAccount;
												toAccountId=subscriber;
//												if(returnCheck.getValue()==true){
													from=subscriber;
//													to=settings.getCASH_PAYABLE_ACCOUNT();
//												}
												inout=1;
												creditDebit=SConstants.DR;
											}
											else if(rentType==2){
												fromAccountId=subscriber;
												toAccountId=fromAccount;
//												if(returnCheck.getValue()==true){
//													from=settings.getCASH_RECEIVABLE_ACCOUNT();
													to=subscriber;
//												}
												inout=0;
												creditDebit=SConstants.CR;
											}
											else{
												if(type==1){
													fromAccountId=fromAccount;
													toAccountId=subscriber;
//													if(returnCheck.getValue()==true){
														from=subscriber;
//														to=settings.getCASH_PAYABLE_ACCOUNT();
//													}
													inout=1;
													creditDebit=SConstants.DR;
												}
												else if(type==2) {
													fromAccountId=subscriber;
													toAccountId=fromAccount;
//													if(returnCheck.getValue()==true){
//														from=settings.getCASH_RECEIVABLE_ACCOUNT();
														to=subscriber;
//													}
													inout=0;
													creditDebit=SConstants.CR;
												}
											}
										}
										spmdl.setSubscription(new SubscriptionInModel(sid));
										spmdl.setPayment_date(CommonUtil.getSQLDateFromUtilDate(paymentDate));
										spmdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(subscriptionDate));
										spmdl.setCash_cheque(cashCheque);
										spmdl.setFrom_account(fromAccountId);
										spmdl.setTo_account(toAccountId);
										spmdl.setCheque_date(CommonUtil.getSQLDateFromUtilDate(chequeDate));
										spmdl.setCheque_number(chequeNumber);
										spmdl.setType(inout);
										spmdl.setPay_credit(0);
										spmdl.setAmount_due(roundNumber(dueAmount));
										spmdl.setAmount_paid(roundNumber(paymentAmount));
										if(returnCheck.getValue()==true){
//											if(dueAmount<0){
//												spmdl.setAmount_paid(roundNumber(balanceAmount));
//											}
//											else{
//												spmdl.setAmount_paid(roundNumber(paymentAmount));
//											}
											SubscriptionPaymentDetailsModel detmdl;
											Item item;
											List<SubscriptionPaymentDetailsModel> childList=new ArrayList<SubscriptionPaymentDetailsModel>();
											Iterator it = dtable.getItemIds().iterator();
											while(it.hasNext()){
												item=dtable.getItem(it.next());
												if(((SCheckBox)item.getItemProperty(TBL_SELECT).getValue()).getValue()) {
													detmdl=new SubscriptionPaymentDetailsModel();
													detmdl.setExpenditure(new SubscriptionExpenditureModel(toLong(item.getItemProperty(TBL_EID).getValue().toString())));
													detmdl.setDetails(item.getItemProperty(TBL_DETAILS).getValue().toString());
													detmdl.setRemarks(item.getItemProperty(TBL_REMARK).getValue().toString());
													childList.add(detmdl);
												}
											}
											if(childList!=null && childList.size()!=0)
												spmdl.setSubscription_payment_list(childList);
											check=true;
										
										}
										else{
											spmdl.setAmount_paid(roundNumber(paymentAmount));
										}
										
										
										if(simdl.getRent_in()==0){             	// Normal Case
											if(simdl.getAvailable()==0){       	// Except Transportation Rent Both
												if(returnCheck.getValue()==true){
													simdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
													spmdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
													simdl.setLock(toLong("0"));
													simdl.setAvailable(toLong("0"));
													scmdl.setAvailable(toLong("0"));
												}
												else{
													simdl.setReturn_date(null);
													spmdl.setReturn_date(null);
													simdl.setLock(toLong("1"));
													simdl.setAvailable(toLong("0"));
													scmdl.setAvailable(toLong("5"));
												}
												trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
												if(returnCheck.getValue()==true){
													trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
													if(simdl.getSubscription().getSpecial()==1) {
														trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
														dao.savePaymentCredit(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
													else {
														dao.save(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
												}
												else{
													if(simdl.getSubscription().getSpecial()==1) {
														trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
														dao.savePaymentCredit(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
													else {
														dao.save(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
												}
												Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
											} 									// End except Transportation Rent Both
											else if(simdl.getAvailable()==1){	// Rent In or Rent Out of Rent Both
												if(scmdl.getAvailable()==2){     // Locked Case of Rent Both
													if(returnCheck.getValue()==true){
														Notification.show(getPropertyName("unable_close"),Type.WARNING_MESSAGE);
													}
													else{
														simdl.setReturn_date(null);
														spmdl.setReturn_date(null);
														simdl.setLock(toLong("1"));
														simdl.setAvailable(toLong("1"));
														scmdl.setAvailable(toLong("2"));
														trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
														if(returnCheck.getValue()==true){
															trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
															if(simdl.getSubscription().getSpecial()==1) {
																trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
																dao.savePaymentCredit(spmdl,
																		trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		simdl,
																		scmdl,check);
															}
															else {
																dao.save(spmdl,
																		trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		simdl,
																		scmdl,check);
															}
														}
														else{
															if(simdl.getSubscription().getSpecial()==1) {
																trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
																dao.savePaymentCredit(spmdl,
																		trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																		CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																		CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		simdl,
																		scmdl,check);
															}
															else {
																dao.save(spmdl,
																		trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																		CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																		simdl,
																		scmdl,check);
															}
														}
														Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
													}
												}								// End Locked Case of Rent Both
												else if(scmdl.getAvailable()==1){							// Unlocked Case of Rent Both
													if(returnCheck.getValue()==true){
														simdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
														spmdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
														simdl.setLock(toLong("0"));
														simdl.setAvailable(toLong("1"));
														scmdl.setAvailable(toLong("0")); ///// Check here if issue
													}
													else{
														simdl.setReturn_date(null);
														spmdl.setReturn_date(null);
														simdl.setLock(toLong("1"));
														simdl.setAvailable(toLong("1"));
														scmdl.setAvailable(toLong("1"));
													}
													trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
													if(returnCheck.getValue()==true){
														trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
														if(simdl.getSubscription().getSpecial()==1) {
															trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
															dao.savePaymentCredit(spmdl,
																	trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	simdl,
																	scmdl,check);
														}
														else {
															dao.save(spmdl,
																	trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	simdl,
																	scmdl,check);
														}
													}
													else{
														if(simdl.getSubscription().getSpecial()==1) {
															trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
															dao.savePaymentCredit(spmdl,
																	trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																	CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																	CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	simdl,
																	scmdl,check);
														}
														else {
															dao.save(spmdl,
																	trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																	CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																	simdl,
																	scmdl,check);
														}
													}
													Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
												}									// End Unlocked Case of Rent Both
											}										// End Rent In or Rent Out of Rent Both
										}									// End Normal Case
										else{								// Rent Out Of Rent Both
											if(simdl.getAvailable()==2){
												if(returnCheck.getValue()==true){
													simdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
													spmdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
													simdl.setLock(toLong("0"));
													simdl.setAvailable(toLong("2"));
													scmdl.setAvailable(toLong("1"));
												}
												else{
													simdl.setReturn_date(null);
													spmdl.setReturn_date(null);
													simdl.setLock(toLong("1"));
													simdl.setAvailable(toLong("2"));
													scmdl.setAvailable(toLong("2"));
												}
												trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
												if(returnCheck.getValue()==true){
													trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
													if(simdl.getSubscription().getSpecial()==1) {
														trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
														dao.savePaymentCredit(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
													else {
														dao.save(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans1.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
												}
												else{
													if(simdl.getSubscription().getSpecial()==1) {
														trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
														dao.savePaymentCredit(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																trans2.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
													else {
														dao.save(spmdl,
																trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																CommonUtil.getSQLDateFromUtilDate(paymentDate)),
																simdl,
																scmdl,check);
													}
												}
												Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
											}
										}															// End Rent Out Of Rent Both
										if(rent==1){
											reloadSubscriptionInCombo(sid, accountType);
										}
										else{
											reloadSubscriptionOutCombo(sid, accountType);
										}
									}																// end if(simdl!=null)
								}																	// end if (sid)
								else{
									setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
								}
								amountField.setReadOnly(true);
								returnDate.setReadOnly(true);
							}
							else{
								setRequiredError(paymentField, getPropertyName("invalid_data"), true);
							}
						}
						catch(Exception e){
							SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
							e.printStackTrace();
							amountField.setReadOnly(true);
							returnDate.setReadOnly(true);
						}
					}
				}
			});
			
			update.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(isValid()){
						try{
							if(toDouble(paymentField.getValue().toString())>=0){
								boolean check=false;
								returnDate.setReadOnly(false);
								List list=new ArrayList();
								SubscriptionPaymentModel spmdl;
								SubscriptionCreationModel scmdl = null;
								SubscriptionInModel simdl;
								SubscriptionInModel smdl = null;
								chequeNumberField.setComponentError(null);
								amountField.setReadOnly(false);
								String chequeNumber=null;
								double previousPaid=0,previousDue=0,newAmount;
								Date paymentDate=null,subscriptionDate=null,chequeDate=null;
								long fromAccountId=0,toAccountId=0,subscriber = 0,paymentId=0,from=0,to=0;
								int creditDebit=0;
								long accountType=toLong(accountRadio.getValue().toString());
								long sid=toLong(subscriptionCombo.getValue().toString());
								double dueAmount=toDouble(amountField.getValue().toString());
								int cashCheque=toInt(cashChequeRadio.getValue().toString());
								long fromAccount=toLong(fromAccountCombo.getValue().toString());
								double paymentAmount=toDouble(paymentField.getValue().toString());
								long rentType=sidao.getRentType(sid, getOfficeID());
								long type=sidao.getAvailablilty(sid, rentType);
								long inout=0;
								if(sid!=0){
									simdl=sidao.getSubscriptionInModel(sid);
									if(simdl!=null){
										subscriber=simdl.getSubscriber();
										scmdl=scdao.getCreationModel(simdl.getSubscription().getId());
										list=dao.getAllTransactions(sid);
										if(list.size()>0){
											SubscriptionPaymentModel mdl;
											for(int i=0;i<list.size();i++){
												mdl=(SubscriptionPaymentModel)list.get(i);
												if(i==0){
													previousPaid=mdl.getAmount_paid();
													previousDue=mdl.getAmount_due();
												}
											}
										}
										newAmount=previousDue+previousPaid;
										/*if(returnCheck.getValue()==true){
											if(dueAmount<0){
												paymentAmount=paymentAmount+dueAmount;
											}
										}*/
										double balanceAmount=dueAmount-paymentAmount;
										long rent=toLong(rentRadio.getValue().toString());
										if(cashCheque==1){
											chequeNumber="";
											chequeDate=getWorkingDate();
										}
										else{
											chequeNumber=chequeNumberField.getValue().toString();
											chequeDate=SubscriptionPayment.this.chequeDate.getValue();
										}
										if(accountType==1){
											fromAccountId=fromAccount;
											toAccountId=subscriber;
//											if(returnCheck.getValue()==true){
												from=subscriber;
//												to=settings.getCASH_PAYABLE_ACCOUNT();
//											}
											inout=1;
											creditDebit=SConstants.DR;
										}
										else if(accountType==2){
											fromAccountId=subscriber;
											toAccountId=fromAccount;
//											if(returnCheck.getValue()==true){
//												from=settings.getCASH_RECEIVABLE_ACCOUNT();
												to=subscriber;
//											}
											inout=0;
											creditDebit=SConstants.CR;
										}
										else if(accountType==3){
											if(rentType==1){
												fromAccountId=fromAccount;
												toAccountId=subscriber;
//												if(returnCheck.getValue()==true){
													from=subscriber;
//													to=settings.getCASH_PAYABLE_ACCOUNT();
//												}
												inout=1;
												creditDebit=SConstants.DR;
											}
											else if(rentType==2){
												fromAccountId=subscriber;
												toAccountId=fromAccount;
//												if(returnCheck.getValue()==true){
//													from=settings.getCASH_RECEIVABLE_ACCOUNT();
													to=subscriber;
//												}
												inout=0;
												creditDebit=SConstants.CR;
											}
											else{
												if(type==1){
													fromAccountId=fromAccount;
													toAccountId=subscriber;
//													if(returnCheck.getValue()==true){
														from=subscriber;
//														to=settings.getCASH_PAYABLE_ACCOUNT();
//													}
													inout=1;
													creditDebit=SConstants.DR;
												}
												else if(type==2) {
													fromAccountId=subscriber;
													toAccountId=fromAccount;
//													if(returnCheck.getValue()==true){
//														from=settings.getCASHC_RECEIVABLE_ACCOUNT();
														to=subscriber;
//													}
													inout=0;
													creditDebit=SConstants.CR;
												}
											}
										}
										spmdl=dao.getPaymentModel(toLong(id.getValue().toString()));
										if(spmdl!=null){
											spmdl.setCash_cheque(cashCheque);
											spmdl.setFrom_account(fromAccountId);
											spmdl.setTo_account(toAccountId);
											spmdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(simdl.getSubscription_date()));
											spmdl.setPayment_date(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
											spmdl.setCheque_date(CommonUtil.getSQLDateFromUtilDate(chequeDate));
											spmdl.setCheque_number(chequeNumber);
											spmdl.setPay_credit(0);
											spmdl.setType(inout);
//											if(returnCheck.getValue()==true){
												spmdl.setAmount_due(roundNumber(dueAmount));
//											}
//											else{
//												spmdl.setAmount_due(roundNumber(balanceAmount));
//											}
											spmdl.setAmount_paid(roundNumber(paymentAmount));
											if(returnCheck.getValue()==true){
												SubscriptionPaymentDetailsModel detmdl;
												Item item;
												List<SubscriptionPaymentDetailsModel> childList=new ArrayList<SubscriptionPaymentDetailsModel>();
												Iterator it = dtable.getItemIds().iterator();
												while(it.hasNext()){
													item=dtable.getItem(it.next());
													if(((SCheckBox)item.getItemProperty(TBL_SELECT).getValue()).getValue()) {
														detmdl=new SubscriptionPaymentDetailsModel();
														detmdl.setExpenditure(new SubscriptionExpenditureModel(toLong(item.getItemProperty(TBL_EID).getValue().toString())));
														detmdl.setDetails(item.getItemProperty(TBL_DETAILS).getValue().toString());
														detmdl.setRemarks(item.getItemProperty(TBL_REMARK).getValue().toString());
														childList.add(detmdl);
													}
												}
												if(childList!=null && childList.size()!=0)
													spmdl.setSubscription_payment_list(childList);
												check=true;
											}
											else{
												spmdl.setAmount_paid(roundNumber(paymentAmount));
											}
											
											if(simdl.getRent_in()==0){             	// Normal Case
												if(simdl.getAvailable()==0){       	// Except Transportation Rent Both
													if(returnCheck.getValue()==true){
														simdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
														spmdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
														simdl.setLock(toLong("0"));
														simdl.setAvailable(toLong("0"));
														scmdl.setAvailable(toLong("0"));
													}
													else{
														simdl.setReturn_date(null);
														spmdl.setReturn_date(null);
														simdl.setLock(toLong("1"));
														simdl.setAvailable(toLong("0"));
														scmdl.setAvailable(toLong("5"));
													}
													FinTransaction trans = new FinTransaction();
													trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
													TransactionModel tran=dao.getTransaction(toLong(tid.getValue().toString()));
													tran.setTransaction_details_list(trans.getChildList());
													if(returnCheck.getValue()==true){
														FinTransaction trans1 = new FinTransaction();
														trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
														TransactionModel trn=dao.getTransaction(toLong(fid.getValue().toString()));
														trn.setTransaction_details_list(trans1.getChildList());
														if(simdl.getSubscription().getSpecial()==1) {
															FinTransaction trans2 = new FinTransaction();
															trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
															TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
															credit.setTransaction_details_list(trans2.getChildList());
															dao.updatePaymentCredit(spmdl,tran,trn,credit,simdl,scmdl,check);
														}
														else {
															dao.update(spmdl,tran,trn,simdl,scmdl,check);
														}
													}
													else{
														if(simdl.getSubscription().getSpecial()==1) {
															FinTransaction trans2 = new FinTransaction();
															trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
															TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
															credit.setTransaction_details_list(trans2.getChildList());
															dao.updatePaymentCredit(spmdl,tran,credit,simdl,scmdl,check);
														}
														else {
															dao.update(spmdl,tran,simdl,scmdl,check);
														}
														
													}
													Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
												} 									// End except Transportation Rent Both
												else if(simdl.getAvailable()==1){	// Rent In or Rent Out of Rent Both
													if(scmdl.getAvailable()==2){     // Locked Case of Rent Both
														if(returnCheck.getValue()==true){
															Notification.show(getPropertyName("unable_close"),Type.WARNING_MESSAGE);
														}
														else{
															simdl.setReturn_date(null);
															spmdl.setReturn_date(null);
															simdl.setLock(toLong("1"));
															simdl.setAvailable(toLong("1"));
															scmdl.setAvailable(toLong("2"));
															FinTransaction trans = new FinTransaction();
															trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
															TransactionModel tran=dao.getTransaction(toLong(tid.getValue().toString()));
															tran.setTransaction_details_list(trans.getChildList());
															if(returnCheck.getValue()==true){
																FinTransaction trans1 = new FinTransaction();
																trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
																TransactionModel trn=dao.getTransaction(toLong(fid.getValue().toString()));
																trn.setTransaction_details_list(trans1.getChildList());
																if(simdl.getSubscription().getSpecial()==1) {
																	FinTransaction trans2 = new FinTransaction();
																	trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
																	TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
																	credit.setTransaction_details_list(trans2.getChildList());
																	dao.updatePaymentCredit(spmdl,tran,trn,credit,simdl,scmdl,check);
																}
																else {
																	dao.update(spmdl,tran,trn,simdl,scmdl,check);
																}
															}
															else{
																if(simdl.getSubscription().getSpecial()==1) {
																	FinTransaction trans2 = new FinTransaction();
																	trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
																	TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
																	credit.setTransaction_details_list(trans2.getChildList());
																	dao.updatePaymentCredit(spmdl,tran,credit,simdl,scmdl,check);
																}
																else {
																	dao.update(spmdl,tran,simdl,scmdl,check);
																}
																
															}
															Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
														}
													}								// End Locked Case of Rent Both
													else if(scmdl.getAvailable()==1){							// Unlocked Case of Rent Both
														if(returnCheck.getValue()==true){
															simdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
															spmdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
															simdl.setLock(toLong("0"));
															simdl.setAvailable(toLong("1"));
															scmdl.setAvailable(toLong("0"));
														}
														else{
															simdl.setReturn_date(null);
															spmdl.setReturn_date(null);
															simdl.setLock(toLong("1"));
															simdl.setAvailable(toLong("1"));
															scmdl.setAvailable(toLong("1"));
														}
														
														FinTransaction trans = new FinTransaction();
														trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
														TransactionModel tran=dao.getTransaction(toLong(tid.getValue().toString()));
														tran.setTransaction_details_list(trans.getChildList());
														if(returnCheck.getValue()==true){
															FinTransaction trans1 = new FinTransaction();
															trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
															TransactionModel trn=dao.getTransaction(toLong(fid.getValue().toString()));
															trn.setTransaction_details_list(trans1.getChildList());
															if(simdl.getSubscription().getSpecial()==1) {
																FinTransaction trans2 = new FinTransaction();
																trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
																TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
																credit.setTransaction_details_list(trans2.getChildList());
																dao.updatePaymentCredit(spmdl,tran,trn,credit,simdl,scmdl,check);
															}
															else {
																dao.update(spmdl,tran,trn,simdl,scmdl,check);
															}
														}
														else{
															if(simdl.getSubscription().getSpecial()==1) {
																FinTransaction trans2 = new FinTransaction();
																trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
																TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
																credit.setTransaction_details_list(trans2.getChildList());
																dao.updatePaymentCredit(spmdl,tran,credit,simdl,scmdl,check);
															}
															else {
																dao.update(spmdl,tran,simdl,scmdl,check);
															}
														}
														Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
													}									// End Unlocked Case of Rent Both
												}										// End Rent In or Rent Out of Rent Both
											}									// End Normal Case
											else{								// Rent Out Of Rent Both
												if(simdl.getAvailable()==2){
													if(returnCheck.getValue()==true){
														simdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
														spmdl.setReturn_date(CommonUtil.getSQLDateFromUtilDate(returnDate.getValue()));
														simdl.setLock(toLong("0"));
														simdl.setAvailable(toLong("2"));
														scmdl.setAvailable(toLong("1"));
													}
													else{
														simdl.setReturn_date(null);
														spmdl.setReturn_date(null);
														simdl.setLock(toLong("1"));
														simdl.setAvailable(toLong("2"));
														scmdl.setAvailable(toLong("2"));
													}
													
													FinTransaction trans = new FinTransaction();
													trans.addTransaction(creditDebit, fromAccountId, toAccountId, roundNumber(paymentAmount));
													TransactionModel tran=dao.getTransaction(toLong(tid.getValue().toString()));
													tran.setTransaction_details_list(trans.getChildList());
													if(returnCheck.getValue()==true){
														FinTransaction trans1 = new FinTransaction();
														trans1.addTransaction(creditDebit, from, to, roundNumber(Math.abs(totalAmount-totalCredit)));
														TransactionModel trn=dao.getTransaction(toLong(fid.getValue().toString()));
														trn.setTransaction_details_list(trans1.getChildList());
														if(simdl.getSubscription().getSpecial()==1) {
															FinTransaction trans2 = new FinTransaction();
															trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
															TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
															credit.setTransaction_details_list(trans2.getChildList());
															dao.updatePaymentCredit(spmdl,tran,trn,credit,simdl,scmdl,check);
														}
														else {
															dao.update(spmdl,tran,trn,simdl,scmdl,check);
														}
													}
													else{
														if(simdl.getSubscription().getSpecial()==1) {
															FinTransaction trans2 = new FinTransaction();
															trans2.addTransaction(creditDebit, from, to, roundNumber(toDouble(creditField.getValue().toString())));
															TransactionModel credit=dao.getTransaction(toLong(cid.getValue().toString()));
															credit.setTransaction_details_list(trans2.getChildList());
															dao.updatePaymentCredit(spmdl,tran,credit,simdl,scmdl,check);
														}
														else {
															dao.update(spmdl,tran,simdl,scmdl,check);
														}
													}
													Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
												}
											}
										}
										if(rent==1){
											reloadSubscriptionInCombo(sid, accountType);
										}
										else{
											reloadSubscriptionOutCombo(sid, accountType);
										}
										
									}							// End if(simdl!=null)
								}                            // End if(sid!=0)
								amountField.setReadOnly(true);
								payment.setVisible(true);
								update.setVisible(false);
								delete.setVisible(false);
								print.setVisible(true);
							}
							else{
								setRequiredError(paymentField, getPropertyName("invalid_data"), true);
							}
						}
						catch(Exception e){
							SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
							e.printStackTrace();
							amountField.setReadOnly(true);
							returnDate.setReadOnly(true);
						}
					}
				}
			});
			
			delete.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						final SubscriptionPaymentModel spmdl;
						final SubscriptionCreationModel scmdl;
						final SubscriptionInModel simdl;
						amountField.setReadOnly(false);
						returnDate.setReadOnly(false);
						final long accountType=toLong(accountRadio.getValue().toString());
						final long sid=toLong(subscriptionCombo.getValue().toString());
						simdl=sidao.getSubscriptionInModel(sid);
						scmdl=scdao.getCreationModel(simdl.getSubscription().getId());
						spmdl=dao.getPaymentModel(toLong(id.getValue().toString()));
						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										if(returnCheck.getValue()==true){
											simdl.setReturn_date(null);
											spmdl.setReturn_date(null);
											if(simdl.getRent_in()!=0){
												scmdl.setAvailable(toLong("2"));
												simdl.setAvailable(toLong("2"));
												simdl.setLock(toLong("1"));
												List list=(List)table.getItemIds();
												if(list!=null && list.size()>1){
													simdl.setLock(toLong("1"));
												}
												else{
													simdl.setLock(toLong("0"));
												}
											}
											else{
												if(simdl.getAvailable()==1){
													scmdl.setAvailable(toLong("1"));
													simdl.setAvailable(toLong("1"));
												}
												else if(simdl.getAvailable()==2){
													scmdl.setAvailable(toLong("2"));
													simdl.setAvailable(toLong("2"));
													simdl.setLock(toLong("1"));
												}
												else {
													scmdl.setAvailable(toLong("5"));
													simdl.setAvailable(toLong("0"));
												}
												List list=(List)table.getItemIds();
												if(list!=null && list.size()>1){
													simdl.setLock(toLong("1"));
												}
												else{
													simdl.setLock(toLong("0"));
												}
											}
											
										}
										else{
											simdl.setReturn_date(null);
											spmdl.setReturn_date(null);
											if(simdl.getRent_in()!=0){
												scmdl.setAvailable(toLong("2"));
												simdl.setAvailable(toLong("2"));
												simdl.setLock(toLong("1"));
												List list=(List)table.getItemIds();
												if(list!=null && list.size()>1){
													simdl.setLock(toLong("1"));
												}
												else{
													simdl.setLock(toLong("0"));
												}
											}
											else{
												if(simdl.getAvailable()==1){
													scmdl.setAvailable(toLong("1"));
													simdl.setAvailable(toLong("1"));
												}
												else if(simdl.getAvailable()==2){
													scmdl.setAvailable(toLong("2"));
													simdl.setAvailable(toLong("2"));
													simdl.setLock(toLong("1"));
												}
												else {
													scmdl.setAvailable(toLong("5"));
													simdl.setAvailable(toLong("0"));
												}
												
												List list=(List)table.getItemIds();
												if(list!=null && list.size()>1){
													simdl.setLock(toLong("1"));
												}
												else{
													simdl.setLock(toLong("0"));
												}
											}
										}
										dao.deleteSupplierPayment(spmdl.getId(),simdl,scmdl);
										SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
										long rent=toLong(rentRadio.getValue().toString());
										if(rent==1){
											reloadSubscriptionInCombo(sid, accountType);
										}
										else{
											reloadSubscriptionOutCombo(sid, accountType);
										}
									} 
									catch (Exception e) {
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
	        			        } 
	        			    }
	        			});
						amountField.setReadOnly(true);
						returnDate.setReadOnly(true);
						payment.setVisible(true);
						update.setVisible(false);
						delete.setVisible(false);
						print.setVisible(true);
					}
					catch(Exception e){
						amountField.setReadOnly(true);
						returnDate.setReadOnly(true);
						SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});
			
			table.addListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(table.getValue()!=null){
							List list=(List)table.getItemIds();
							long sid=toLong(subscriptionCombo.getValue().toString());
							SubscriptionInModel simdl;
							SubscriptionCreationModel scmdl;
							long accountType=toLong(accountRadio.getValue().toString());
							Item item = table.getItem(table.getValue());
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							simdl=sidao.getSubscriptionInModel(sid);
							scmdl=scdao.getCreationModel(simdl.getSubscription().getId());
							if(Integer.parseInt(item.getItemProperty(TBC_SN).getValue().toString())==1){
								amountField.setNewValue(asString(toDouble(item.getItemProperty(TBC_DUE).getValue().toString())+toDouble(item.getItemProperty(TBC_PAYMENT).getValue().toString())));
								paymentField.setValue(item.getItemProperty(TBC_PAYMENT).getValue().toString());
								cashChequeRadio.setValue(Integer.parseInt(item.getItemProperty(TBC_CASH_CHEQUE).getValue().toString()));
								chequeNumberField.setValue(item.getItemProperty(TBC_CHEQUENO).getValue().toString());
								fromAccountCombo.setValue(toLong(item.getItemProperty(TBC_FROMACCOUNT).getValue().toString()));
								dateField.setValue((Date)item.getItemProperty(TBC_PDATE).getValue());
								id.setValue(item.getItemProperty(TBC_ID).getValue().toString());
								tid.setValue(item.getItemProperty(TBC_TID).getValue().toString());
								fid.setValue(item.getItemProperty(TBC_FID).getValue().toString());
								cid.setValue(item.getItemProperty(TBC_CID).getValue().toString());
								String date=item.getItemProperty(TBC_CHEQUEDATE).getValue().toString();
//								System.out.println("Date "+date);
								chequeDate.setValue(formatter.parse(date));
								payment.setVisible(false);
								update.setVisible(true);
								delete.setVisible(true);
								print.setVisible(false);
							}
							else{
								SNotification.show(getPropertyName("last_payment"),Type.WARNING_MESSAGE);
								long rent=toLong(rentRadio.getValue().toString());
								if(rent==1){
									reloadSubscriptionInCombo(sid, accountType);
								}
								else{
									reloadSubscriptionOutCombo(sid, accountType);
								}
								cashChequeRadio.setValue(1);
								payment.setVisible(true);
								update.setVisible(false);
								delete.setVisible(false);
								print.setVisible(true);
							}
							if(simdl.getReturn_date()!=null){
								returnCheck.setValue(true);
								returnDate.setNewValue(simdl.getReturn_date());
								amountField.setNewValue(asString(0));
								paymentField.setValue(asString(0));
								Notification.show(getPropertyName("rental_closed"),Type.WARNING_MESSAGE);
								if(scmdl.getAvailable()==2){
									payment.setEnabled(false);
									update.setEnabled(false);
									delete.setEnabled(false);
									print.setEnabled(true);
								}
								else if(scmdl.getAvailable()==1){
									if(dao.getCount(toLong(subscriptionCombo.getValue().toString()))>(long)1){
										if(sid==scdao.getGreatestID(sid,getOfficeID())){
											payment.setEnabled(true);
											update.setEnabled(true);
											delete.setEnabled(true);
											print.setEnabled(true);
										}
										else{
											payment.setEnabled(false);
											update.setEnabled(false);
											delete.setEnabled(false);
											print.setEnabled(true);
										}
									}
									else{
										payment.setEnabled(true);
										update.setEnabled(true);
										delete.setEnabled(true);
										print.setEnabled(true);
									}
								}
								else {
									if(dao.getCount(toLong(subscriptionCombo.getValue().toString()))>(long)1){
										if(sid==scdao.getGreatestID(sid,getOfficeID())){
											payment.setEnabled(true);
											update.setEnabled(true);
											delete.setEnabled(true);
											print.setEnabled(true);
										}
										else{
											payment.setEnabled(false);
											update.setEnabled(false);
											delete.setEnabled(false);
											print.setEnabled(true);
										}
									}
									else{
										payment.setEnabled(true);
										update.setEnabled(true);
										delete.setEnabled(true);
										print.setEnabled(true);
									}
								}
								
							}
						}
						else{
							payment.setVisible(true);
							update.setVisible(false);
							delete.setVisible(false);
							print.setVisible(true);
						}
					}
					catch(Exception e){
						
					}
					
				}
			});
			
			print.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0")){
							long sid=toLong(subscriptionCombo.getValue().toString());
							SubscriptionInModel mdl=sidao.getSubscriptionInModel(sid);
							SubscriptionPaymentModel spmdl=null;/*=dao.getModel(sid);*/
							SubscriptionPaymentDetailsModel dmdl=null;
							List resultList=new ArrayList();
							HashMap<String, Object> params = new HashMap<String, Object>();
							Report report = new Report(getLoginID());
							RentalInvoiceBean bean=null;
							if(mdl!=null){
								if(mdl.getAccount_type()==3){
									List list=dao.getLastTransactions(sid);
									if(list.size()!=0){
										Iterator itr=list.iterator();
										while(itr.hasNext()){
											spmdl=(SubscriptionPaymentModel)itr.next();
											bean=new RentalInvoiceBean();
											bean.setSubscriber(new LedgerDao().getLedgerNameFromID(mdl.getSubscriber()));
											bean.setDate(CommonUtil.formatDateToDDMMYYYY(spmdl.getPayment_date()));
											bean.setAmount(spmdl.getAmount_paid());
											bean.setTotal(new SubscriptionLedgerSubscriberDao().getTotalPaid(sid, spmdl.getPayment_date()));
											resultList.add(bean);
										}
									}
									else{
										SNotification.show(getPropertyName("no_data_available"), Type.WARNING_MESSAGE);
									}
									if (resultList != null && resultList.size() > 0){
										String subTitle = "";
										subTitle += "Office : "+getOfficeName();
										report.setJrxmlFileName("TransportationCashReceipt");
										report.setReportFileName("Transportation Cash Receipt");
										report.setReportTitle("Cash Receipt");
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
		catch(Exception e){
			e.printStackTrace();
		}
		return mainPanel;
	}

	public void loadAccountCombo(long id){
		accountRadio.setValue(id);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadTable(){
		try{
			List list=new  ArrayList();
			long id=0;
			SubscriptionExpenditureModel mdl;
			list.addAll(new ExpenditureDao().getAllExpenditures(getOfficeID()));
			dtable.setVisibleColumns(allDHeaders);
			List lst=new ArrayList();
			Iterator it=dtable.getItemIds().iterator();
			while(it.hasNext()){
				Item item=dtable.getItem(it.next());
				lst.add(toLong(item.getItemProperty(TBL_EID).getValue().toString()));
			}
			if(list!=null && list.size()!=0){
				Iterator itr=list.iterator();
				while(itr.hasNext()){
					mdl=(SubscriptionExpenditureModel)itr.next();
					if(lst.contains(mdl.getId())){
						continue;
					}
					dtable.addItem(new Object[]{
							toLong("0"),
							mdl.getId(),
							new SCheckBox(null,false),
							mdl.getName(),
							"",
							new STextField(null,"")
					},dtable.getItemIds().size()+1);
				}
			}
			dtable.setVisibleColumns(visibleDHeaders);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadrentCombo(long id){
		rentRadio.setValue(id);
	}
	
	public void loadSubscription(long id){
		subscriptionCombo.setValue(id);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionInCombo(long id,long type)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.addAll(dao.getAllInSubscriptions(getOfficeID(),type));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionCombo.setContainerDataSource(bic);
			subscriptionCombo.setItemCaptionPropertyId("details");
			subscriptionCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionOutCombo(long id,long type)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.addAll(dao.getAllOutSubscriptions(getOfficeID(),type));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionCombo.setContainerDataSource(bic);
			subscriptionCombo.setItemCaptionPropertyId("details");
			subscriptionCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadFromAccountCombo() {
		List<Object> list = null;
		try{
			fromAccountCombo.removeAllItems();
			List groupList = new ArrayList();
			if ((Integer) cashChequeRadio.getValue() == 1)
				groupList.add(settings.getCASH_GROUP());
			else
//				groupList.add(SConstants.BANK_ACCOUNT_GROUP_ID);
			list = new LedgerDao().getAllLedgersUnderGroupAndSubGroupsFromGroupList(
							getOfficeID(), 
							getOrganizationID(), 
							groupList);
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			fromAccountCombo.setContainerDataSource(bic);
			fromAccountCombo.setItemCaptionPropertyId("name");
			fromAccountCombo.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int calculateDifferenceInDays(long sid){
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return days;
	}
	
	public int calculateEligibleDifferenceInDays(long per,Date dat){
		int days=0;
		int actualDays=0;
		long fromTime,toTime;
		try{
			fromTime=dat.getTime();
			toTime=new Date().getTime();
			days=(int)(((toTime-fromTime)/(24*60*60*1000))+1);
			int period=Integer.parseInt(asString(per));
			switch (period) {
				case 1: actualDays=days;
						break;
				case 2: if(days%7==0)
							actualDays=days;
						else
							actualDays=((days/7)*7)+7;
						break;		

				case 3: if(days%30==0)
							actualDays=days;
						else
							actualDays=((days/30)*30)+30;
						break;
					
				case 4: if(days%365==0)
							actualDays=days;
						else
							actualDays=((days/365)*365)+365;
						break;		
				default:
						break;
			}
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return actualDays;
	}
	
	@SuppressWarnings("rawtypes")
	public double getPreviousDue(long sid){
		double due=0;
		List list=new ArrayList();
		SubscriptionPaymentModel mdl;
		try{
			list=dao.getAllTransactions(sid);
			if(list.size()>0){
				for(int i=0;i<list.size();i++){
					mdl=(SubscriptionPaymentModel)list.get(i);
					if(i==0){
						due=mdl.getAmount_due();
					}
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
	
	@SuppressWarnings("rawtypes")
	public double getTotalPreviousPaid(long sid){
		double due=0;
		List list=new ArrayList();
		SubscriptionPaymentModel mdl;
		try{
			list=dao.getAllTransactions(sid);
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
	
	@SuppressWarnings("rawtypes")
	public double getTotalPaid(long sid){
		double due=0;
		List list=new ArrayList();
		SubscriptionPaymentModel mdl;
		try{
			list=dao.getAllTransactions(sid);
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
	
	@SuppressWarnings("rawtypes")
	public Date findIssueDate(long sid){
		Date date=null;
		List list=new ArrayList();
		SubscriptionInModel simdl;
		SubscriptionPaymentModel mdl;
		try{
			simdl=sidao.getSubscriptionInModel(sid);
			list=dao.getAllTransactions(sid);
			if(list.size()>0){
				for(int i=0;i<list.size();i++){
					mdl=(SubscriptionPaymentModel)list.get(i);
					if(i==0){
						date=mdl.getSubscription_date();
					}
				}
			}
			else{
				date=simdl.getSubscription_date();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return date;
	}
	
	public long getPeriod(long sid){
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
	
	public double calculateSubscriptionAmountDue(int diffDays,double rate,long per){
		double amount=0;
		int period=Integer.parseInt(asString(per));
		try{
			switch (period) {
			case 1: if(diffDays>=1){
						amount=rate;
					}
					break;
			case 2: if(diffDays>=7){
						if(diffDays%7==0)
							amount=rate;
					}
					else
						amount=0;
					break;		

			case 3: if(diffDays>=30){
						if(diffDays%30==0)
							amount=rate;
					}
					else
						amount=0;
					break;
					
			case 4: if(diffDays>=365){
						if(diffDays%365==0)
							amount=rate;
					}
					else
						amount=0;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberExpenditure(long id){
		try{
			subscriberCombo.removeAllItems();
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
			subscriberCombo.removeAllItems();
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
			subscriberCombo.removeAllItems();
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSubscriberTransportationRentInOrOut(long id){
		try{
			subscriberCombo.removeAllItems();
			SubscriptionInDao dao=new SubscriptionInDao();
			List list=new ArrayList();
			list.addAll(dao.getAllTransportationSubscriptions(getOfficeID()));
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
		
		if(fromAccountCombo.getValue()==null || fromAccountCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(fromAccountCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(fromAccountCombo, null, false);
		}
		if (paymentField.getValue() == null || paymentField.getValue().equals("")) {
			paymentField.setValue("0");
		} 
		else {
			try {
				if (toDouble(paymentField.getValue().toString()) < 0) {
					paymentField.setValue(""+Math.abs(toDouble(paymentField.getValue().toString())));
				}
			} 
			catch (Exception e) {
				paymentField.setValue("0");
			}
		}
		if(dateField.getValue()==null)
			dateField.setValue(getWorkingDate());
		if(dateField.getValue().getTime()>new Date().getTime())
			dateField.setValue(getWorkingDate());
		
		if(chequeDate.isVisible()){
			if(chequeDate.getValue()==null){
				setRequiredError(chequeDate, getPropertyName("invalid_selection"), true);
				valid=false;
			}
			else{
				setRequiredError(chequeDate, null, false);
			}
		}
		
		if(chequeNumberField.isVisible()){
			if (chequeNumberField.getValue() == null
					|| chequeNumberField.getValue().equals("")) {
				setRequiredError(chequeNumberField,	getPropertyName("invalid_data"), true);
				valid = false;
			} else {
				try {
					if (toDouble(chequeNumberField.getValue()) < 0) {
						setRequiredError(chequeNumberField,getPropertyName("invalid_data"), true);
						valid = false;
					} 
					else
						setRequiredError(chequeNumberField, null, false);
				} catch (Exception e) {
					setRequiredError(chequeNumberField,getPropertyName("invalid_data"), true);
					valid = false;
				}
			}
		}
		if(creditField.isVisible()){
			if (creditField.getValue() == null
					|| creditField.getValue().equals("")) {
				setRequiredError(creditField,	getPropertyName("invalid_data"), true);
				valid = false;
			} else {
				try {
					if (toDouble(creditField.getValue()) < 0) {
						setRequiredError(creditField,getPropertyName("invalid_data"), true);
						valid = false;
					} 
					else
						setRequiredError(creditField, null, false);
				} catch (Exception e) {
					setRequiredError(creditField,getPropertyName("invalid_data"), true);
					valid = false;
				}
			}
		}
		if(returnCheck.getValue()==true){
			amountField.setReadOnly(false);
			try{
				
			}
			catch(Exception e){
				setRequiredError(paymentField,getPropertyName("invalid_data"), true);
				amountField.setReadOnly(true);
			}
			amountField.setReadOnly(true);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	public void resetAll(){
		subscriptionCombo.setValue(null);
		fromAccountCombo.setValue(null);
		amountField.setNewValue("0");
		chequeNumberField.setValue("");
		paymentField.setValue("0");
		id.setValue("");
		tid.setValue("");
		fid.setValue("");
		cid.setValue("");
		creditField.setValue("0");
		cashChequeRadio.setValue((long)1);
		dateField.setValue(getWorkingDate());		
		chequeDate.setValue(getWorkingDate());
		table.removeAllItems();
		payment.setVisible(true);
		update.setVisible(false);
		delete.setVisible(false);
		print.setVisible(false);
	}

	public void removeAllErrors(){
		subscriptionCombo.setComponentError(null);
		fromAccountCombo.setComponentError(null);
		amountField.setComponentError(null);
		chequeNumberField.setComponentError(null);
		paymentField.setComponentError(null);
		cashChequeRadio.setComponentError(null);
		dateField.setComponentError(null);		
		creditField.setComponentError(null);
		chequeDate.setComponentError(null);
		id.setComponentError(null);
		tid.setComponentError(null);
		table.removeAllItems();
	}

	public void loadAllCustomer(SubscriptionPaymentModel mdl){
		try{
			SubscriptionInModel imdl=dao.getInModel(mdl.getId());
			accountRadio.setValue(imdl.getAccount_type());
			if(imdl.getAccount_type()==3){
				if(imdl.getSubscription().getRent_status()==1){
					rentRadio.setValue((long)1);
				}
				else if(imdl.getSubscription().getRent_status()==2){
					rentRadio.setValue((long)1);
				}
				else if(imdl.getSubscription().getRent_status()==3){
					if(imdl.getAvailable()==1){
						rentRadio.setValue((long)1);
					}
					else if(imdl.getAvailable()==2){
						rentRadio.setValue((long)2);
					}
				}
			}
			subscriptionCombo.setValue(imdl.getId());
			fromAccountCombo.setValue(mdl.getTo_account());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadAllSupplier(SubscriptionPaymentModel mdl){
		try{
			SubscriptionInModel imdl=dao.getInModel(mdl.getId());
			accountRadio.setValue(imdl.getAccount_type());
			if(imdl.getAccount_type()==3){
				if(imdl.getSubscription().getRent_status()==1){
					rentRadio.setValue((long)1);
				}
				else if(imdl.getSubscription().getRent_status()==2){
					rentRadio.setValue((long)1);
				}
				else if(imdl.getSubscription().getRent_status()==3){
					if(imdl.getAvailable()==1){
						rentRadio.setValue((long)1);
					}
					else if(imdl.getAvailable()==2){
						rentRadio.setValue((long)2);
					}
				}
			}
			subscriptionCombo.setValue(imdl.getId());
			fromAccountCombo.setValue(mdl.getFrom_account());
		}
		catch(Exception e){
			e.printStackTrace();
		}
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

}