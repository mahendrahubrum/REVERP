package com.inventory.journal.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.journal.bean.JournalBean;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalDetailsModel;
import com.inventory.journal.model.JournalModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SSelectionField;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.model.DepartmentModel;

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
public class JournalNewUI extends SparkLogic {

	SVerticalLayout mainLayout;
	SPanel mainPanel;
	
	SGridLayout topGrid;
	SComboField journalCombo;
	SDateField dateField;
	STextField referenceNoField;
	
	SettingsValuePojo settings;
	WrappedSession session;
	
	STable table;
	
	private static String TBC_SN="SN";
	private static String TBC_ACCOUNT_ID="Id";
	private static String TBC_ACCOUNT="Account";
	private static String TBC_DEPARTMENT_ID="Department Id";
	private static String TBC_DEPARTMENT="Department";
	private static String TBC_DIVISION_ID="Division Id";
	private static String TBC_DIVISION="Division";
	private static String TBC_TYPE="Type";
	private static String TBC_DEBIT="Debit Amount";
	private static String TBC_CREDIT="Credit Amount";
	private static String TBC_CID="Currency Id";
	private static String TBC_CURRENCY="Currency";
	private static String TBC_CONV_RATE="Conv Rate";
	private static String TBC_BASE_CURRENCY_VALUE="Base Currency";
	private static String TBC_BILL="Bill No";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	STextField differenceField;
	
	SNativeSelect transactionTypeSelect;
	SComboField ledgerCombo;
	SComboField departmentCombo;
	SSelectionField divisionCombo;
	SCurrencyField amountField;
	SCurrencyField convertedField;
	STextField billNoField;
	
	SButton createNewButton;
	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;
	SButton addItemButton;
	SButton updateItemButton;
	
	STextArea remarksArea;
	JournalDao dao;
	Date previousDate;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		setSize(825, 575);
		previousDate=new Date();
		previousDate=getWorkingDate();
		allHeaders=new Object[]{TBC_SN, TBC_ACCOUNT_ID, TBC_ACCOUNT, TBC_DEPARTMENT_ID, TBC_DEPARTMENT, TBC_DIVISION_ID, TBC_DIVISION, TBC_TYPE, 
								TBC_DEBIT, TBC_CREDIT, TBC_CID, TBC_CURRENCY, TBC_CONV_RATE, TBC_BASE_CURRENCY_VALUE, TBC_BILL };
		dao=new JournalDao();
		try {
			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
			
			requiredHeaders=new Object[]{TBC_SN, TBC_ACCOUNT, TBC_DEPARTMENT, TBC_DIVISION, TBC_DEBIT, TBC_CREDIT, TBC_CURRENCY, TBC_BILL };
			
			List<Object> tempList = new ArrayList<Object>();
			Collections.addAll(tempList, requiredHeaders);
			
			if(!settings.isDEPARTMENT_ENABLED()){
				tempList.remove(TBC_DEPARTMENT);
			}
			
			if(!settings.isDIVISION_ENABLED()){
				tempList.remove(TBC_DIVISION);
			}
			requiredHeaders = tempList.toArray(new String[tempList.size()]);
			
			topGrid=new SGridLayout(8, 4);
			topGrid.setSpacing(true);
			journalCombo=new SComboField(null, 200, null, "id", "name", false, "Create new");
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			
			dateField=new SDateField(null, 100, getDateFormat());
			dateField.setImmediate(true);
			referenceNoField=new STextField(null, 200);
			topGrid.addComponent(new SLabel("Journal"), 1, 0);
			topGrid.addComponent(new SHorizontalLayout(journalCombo,createNewButton), 2, 0);
			topGrid.addComponent(new SLabel("Ref. No"), 3, 0);
			topGrid.addComponent(referenceNoField, 4, 0);
			topGrid.addComponent(new SLabel("Journal Date"), 5, 0);
			topGrid.addComponent(dateField, 6, 0);
			
			table=new STable(null, 775, 200);
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.LEFT);
			table.addContainerProperty(TBC_ACCOUNT_ID, Long.class, null, TBC_ACCOUNT_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_ACCOUNT, String.class, null, TBC_ACCOUNT, null, Align.LEFT);
			table.addContainerProperty(TBC_DEPARTMENT_ID, Long.class, null, TBC_DEPARTMENT_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_DEPARTMENT, String.class, null, TBC_DEPARTMENT, null, Align.LEFT);
			table.addContainerProperty(TBC_DIVISION_ID, Long.class, null, TBC_DIVISION_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_DIVISION, String.class, null, TBC_DIVISION, null, Align.LEFT);
			table.addContainerProperty(TBC_TYPE, Integer.class, null, TBC_TYPE, null, Align.LEFT);
			table.addContainerProperty(TBC_DEBIT, Double.class, null, TBC_DEBIT, null, Align.LEFT);
			table.addContainerProperty(TBC_CREDIT, Double.class, null, TBC_CREDIT, null, Align.LEFT);
			table.addContainerProperty(TBC_CID, Long.class, null, TBC_CID, null, Align.LEFT);
			table.addContainerProperty(TBC_CURRENCY, String.class, null, TBC_CURRENCY, null, Align.LEFT);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null, TBC_CONV_RATE, null, Align.LEFT);
			table.addContainerProperty(TBC_BASE_CURRENCY_VALUE, Double.class, null, TBC_BASE_CURRENCY_VALUE, null, Align.LEFT);
			table.addContainerProperty(TBC_BILL, String.class, null, TBC_BILL, null, Align.LEFT);
			
			table.setColumnExpandRatio(TBC_SN, 0.5f);
			table.setColumnExpandRatio(TBC_ACCOUNT, 1.5f);
			table.setColumnExpandRatio(TBC_DEPARTMENT, 1f);
			table.setColumnExpandRatio(TBC_DIVISION, 1f);
			table.setColumnExpandRatio(TBC_DEBIT, 1f);
			table.setColumnExpandRatio(TBC_CREDIT, 1f);
			table.setColumnExpandRatio(TBC_CURRENCY, 0.75f);
			table.setColumnExpandRatio(TBC_BILL, 1.25f);
			table.setVisibleColumns(requiredHeaders);
			table.setSelectable(true);
			table.setFooterVisible(false);
			
			differenceField=new STextField(null, 200);
			differenceField.setReadOnly(true);
			getDifference();
			SHorizontalLayout itemLayout=new SHorizontalLayout();
			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			itemLayout.setStyleName("po_border");
			transactionTypeSelect = new SNativeSelect(getPropertyName("type"), 50, SConstants.transactionType,"intKey", "value",true);
			transactionTypeSelect.setValue(1);
			ledgerCombo=new SComboField("Account", 150, new LedgerDao().getAllActiveLedgerNames(getOfficeID()), "id", "name", true, getPropertyName("select"));

			List departmentList=new ArrayList();
			departmentList.add(0, new DepartmentModel(0, "None"));
			departmentList.addAll(new DepartmentDao().getDepartments(getOrganizationID()));
			divisionCombo = new SSelectionField(getPropertyName("Division"),getPropertyName("none"),200, 400);
			divisionCombo.setContainerData(new DivisionDao().getDivisionsHierarchy(getOrganizationID()));
			departmentCombo = new SComboField(getPropertyName("Department"), 100, departmentList,"id", "name", false, getPropertyName("select"));
			departmentCombo.setValue((long)0);
			
			amountField=new SCurrencyField("Amount",75, getWorkingDate());
			convertedField = new SCurrencyField(null, 75,getWorkingDate());
			billNoField=new STextField("Bill No", 150);
			
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			
			itemLayout.addComponent(transactionTypeSelect);
			itemLayout.addComponent(ledgerCombo);
			if(settings.isDEPARTMENT_ENABLED())
				itemLayout.addComponent(departmentCombo);
			if(settings.isDIVISION_ENABLED())
				itemLayout.addComponent(divisionCombo);
			itemLayout.addComponent(amountField);
			itemLayout.addComponent(convertedField);
			itemLayout.addComponent(billNoField);
			itemLayout.addComponent(addItemButton);
			itemLayout.addComponent(updateItemButton);
			itemLayout.setComponentAlignment(addItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setComponentAlignment(updateItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setComponentAlignment(convertedField, Alignment.BOTTOM_CENTER);
			convertedField.currencySelect.setReadOnly(true);
			convertedField.amountField.setReadOnly(true);
			convertedField.setVisible(false);
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			
			remarksArea=new STextArea("Remarks", 450,30);
			
			SHorizontalLayout differenceLayout=new SHorizontalLayout();
			differenceLayout.setSpacing(true);
			differenceLayout.addComponent(new SLabel("Difference"));
			differenceLayout.addComponent(differenceField);
			
			mainLayout.addComponent(topGrid);
			mainLayout.addComponent(table);
			mainLayout.addComponent(differenceLayout);
			mainLayout.addComponent(itemLayout);
			mainLayout.addComponent(remarksArea);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			loadJournal(0);
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						journalCombo.setValue((long)0);
						journalCombo.setValue(null);
					} catch (ReadOnlyException e) {
						e.printStackTrace();
					}
				}
			});

			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(dateField.getValue()!=null){
						if(previousDate.getTime()!=dateField.getValue().getTime()){
							ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are You Sure ? Update Currency Rate & Journal Data Accordingly.",new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (!dialog.isConfirmed()) {
										dateField.setValue(previousDate);
									}
									previousDate=dateField.getValue();
									amountField.setCurrencyDate(previousDate);
								}
							});
						}
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			amountField.currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(amountField.currencySelect.getValue()!=null){
						if((Long)amountField.currencySelect.getValue()!=getCurrencyID()){
							convertedField.setVisible(true);
						}
						else{
							convertedField.setVisible(false);
						}
						convertedField.setNewValue(roundNumber(amountField.getValue()/amountField.getConversionRate()));
					}
				}
			});
			
			
			amountField.amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					convertedField.setNewValue(roundNumber(amountField.getValue()/amountField.getConversionRate()));	
				}
			});
			amountField.setNewValue(getCurrencyID(), 0);
			
			
			addItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							table.setVisibleColumns(allHeaders);
							double debit=0,credit=0;
							long departmentId=0,divisionId=0;
							String department="",division="";
							Iterator itr=table.getItemIds().iterator();
							List<Long> idList=new ArrayList<Long>();
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								idList.add(toLong(item.getItemProperty(TBC_ACCOUNT_ID).getValue().toString()));
							}
							if((Integer)transactionTypeSelect.getValue()==1){
								credit=amountField.getValue();
							}
							else if((Integer)transactionTypeSelect.getValue()==2){
								debit=amountField.getValue();
							}
							departmentId=(Long)departmentCombo.getValue();
							department=departmentCombo.getItemCaption((Long)departmentCombo.getValue());
							divisionId=(Long)divisionCombo.getValue();
							division=divisionCombo.getItemCaption();
							if(!idList.contains((Long)ledgerCombo.getValue())){
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(Long)ledgerCombo.getValue(),
										ledgerCombo.getItemCaption((Long)ledgerCombo.getValue()),
										departmentId,
										department,
										divisionId,
										division,
										(Integer)transactionTypeSelect.getValue(),
										roundNumber(debit),
										roundNumber(credit),
										amountField.getCurrency(),
										new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getSymbol(),
										roundNumber(amountField.getConversionRate()),
										roundNumber(amountField.getValue()/amountField.getConversionRate()),
										billNoField.getValue()},table.getItemIds().size()+1);
								getDifference();
								ledgerCombo.setValue(null);
								departmentCombo.setValue((long)0);
								divisionCombo.setValue(null);
								transactionTypeSelect.setValue(1);
								amountField.setValue(getCurrencyID(), 0);
								billNoField.setNewValue("");
							}
							else{
								Notification.show(getPropertyName("ledger_added_earlier"), Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(requiredHeaders);
							addItemButton.setVisible(true);
							updateItemButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						Item item=table.getItem(table.getValue());
						double amount=0;
						if((Integer)item.getItemProperty(TBC_TYPE).getValue()==1){
							amount=toDouble(item.getItemProperty(TBC_CREDIT).getValue().toString());
						}
						else if((Integer)item.getItemProperty(TBC_TYPE).getValue()==2){
							amount=toDouble(item.getItemProperty(TBC_DEBIT).getValue().toString());
						}
						ledgerCombo.setValue((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue());
						departmentCombo.setValue((Long)item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
						divisionCombo.setValue((Long)item.getItemProperty(TBC_DIVISION_ID).getValue());
						transactionTypeSelect.setValue((Integer)item.getItemProperty(TBC_TYPE).getValue());
						amountField.setValue((Long)item.getItemProperty(TBC_CID).getValue(), roundNumber(amount));
						billNoField.setNewValue(item.getItemProperty(TBC_BILL).getValue().toString());
						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
					}
					else{
						ledgerCombo.setValue(null);
						departmentCombo.setValue((long)0);
						divisionCombo.setValue(null);
						transactionTypeSelect.setValue(1);
						amountField.setValue(getCurrencyID(), 0);
						billNoField.setNewValue("");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
					}
				}
			});
			
			
			updateItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							if(table.getValue()!=null){
								Item item=table.getItem(table.getValue());
								double debit=0,credit=0;
								long departmentId=0,divisionId=0;
								String department="",division="";
								Iterator itr=table.getItemIds().iterator();
								List<Long> idList=new ArrayList<Long>();
								while (itr.hasNext()) {
									Item nitem = table.getItem(itr.next());
									idList.add(toLong(nitem.getItemProperty(TBC_ACCOUNT_ID).getValue().toString()));
								}
								idList.remove(toLong(item.getItemProperty(TBC_ACCOUNT_ID).getValue().toString()));
								if((Integer)transactionTypeSelect.getValue()==1){
									credit=amountField.getValue();
								}
								else if((Integer)transactionTypeSelect.getValue()==2){
									debit=amountField.getValue();
								}
								if(settings.isDEPARTMENT_ENABLED()){
									departmentId=(Long)departmentCombo.getValue();
									department=departmentCombo.getItemCaption((Long)departmentCombo.getValue());
								}
								else{
									departmentId=0;
									department="";
								}
								if(settings.isDIVISION_ENABLED()){
									divisionId=(Long)divisionCombo.getValue();
									division=divisionCombo.getItemCaption();
								}
								else{
									divisionId=0;
									division="";
								}
								if(!idList.contains((Long)ledgerCombo.getValue())){
									item.getItemProperty(TBC_ACCOUNT_ID).setValue((Long)ledgerCombo.getValue());
									item.getItemProperty(TBC_ACCOUNT).setValue(ledgerCombo.getItemCaption((Long)ledgerCombo.getValue()));
									item.getItemProperty(TBC_DEPARTMENT_ID).setValue(departmentId);
									item.getItemProperty(TBC_DEPARTMENT).setValue(department);
									item.getItemProperty(TBC_DIVISION_ID).setValue(divisionId);
									item.getItemProperty(TBC_DIVISION).setValue(division);
									item.getItemProperty(TBC_TYPE).setValue((Integer)transactionTypeSelect.getValue());
									item.getItemProperty(TBC_DEBIT).setValue(roundNumber(debit));
									item.getItemProperty(TBC_CREDIT).setValue(roundNumber(credit));
									item.getItemProperty(TBC_CID).setValue(amountField.getCurrency());
									item.getItemProperty(TBC_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getSymbol());
									item.getItemProperty(TBC_CONV_RATE).setValue(roundNumber(amountField.getConversionRate()));
									item.getItemProperty(TBC_BASE_CURRENCY_VALUE).setValue(roundNumber(amountField.getValue()/amountField.getConversionRate()));
									item.getItemProperty(TBC_BILL).setValue(billNoField.getValue());
									table.setValue(null);
									getDifference();
								}
								else{
									Notification.show(getPropertyName("ledger_added_earlier"), Type.WARNING_MESSAGE);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							if(journalCombo.getValue()==null || journalCombo.getValue().toString().equals("0")) {
								List<JournalDetailsModel> journalList = new ArrayList<JournalDetailsModel>();
								Hashtable<Long, JournalBean> hashCredit = new Hashtable<Long, JournalBean>();
								List<Long> creditList = new ArrayList<Long>();
								double debitAmount=0,totalCredit=0;
								
								JournalModel mdl=new JournalModel();
								FinTransaction transaction=new FinTransaction();
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setRemarks(remarksArea.getValue());
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setBill_no(getNextSequence("Journal No", getLoginID())+"");
								mdl.setActive(true);
								Iterator itr=table.getItemIds().iterator();
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									double amount=0;
									JournalDetailsModel det=new JournalDetailsModel();
									det.setLedger(new LedgerModel((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue()));
									det.setDepartment_id((Long)item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivision_id((Long)item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setTransaction_type((Integer)item.getItemProperty(TBC_TYPE).getValue());
									if((Integer)item.getItemProperty(TBC_TYPE).getValue()==1){
										amount=toDouble(item.getItemProperty(TBC_CREDIT).getValue().toString());
										hashCredit.put((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue(), 
												new JournalBean((Long)item.getItemProperty(TBC_CID).getValue(), 
														roundNumber(toDouble(item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue().toString())), 
														roundNumber(toDouble(item.getItemProperty(TBC_CONV_RATE).getValue().toString()))));
										creditList.add((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue());
									}
									else if((Integer)item.getItemProperty(TBC_TYPE).getValue()==2){
										amount=toDouble(item.getItemProperty(TBC_DEBIT).getValue().toString());
									}
									det.setAmount(roundNumber(amount));
									det.setCurrencyId((Long)item.getItemProperty(TBC_CID).getValue());
									det.setConversionRate(roundNumber(toDouble(item.getItemProperty(TBC_CONV_RATE).getValue().toString())));
									det.setBill_no(item.getItemProperty(TBC_BILL).getValue().toString());
									journalList.add(det);
								}
								mdl.setJournal_details_list(journalList);
								List<Long> tempList = new ArrayList<Long>();
								itr=table.getItemIds().iterator();
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									int type=(Integer)item.getItemProperty(TBC_TYPE).getValue();
									long account=0;
									double paymentAmount=0;
									if(type==1){
										continue;
									}
									else if(type==2){
										debitAmount=roundNumber(toDouble(item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue().toString()));
										
										for(int i=0;i<creditList.size();i++){
											
											if(debitAmount<=0)
												break;
											
											account=creditList.get(i);
											
											if(tempList.contains(account))
												continue;
											
											JournalBean bean=hashCredit.get(account);
											double amount=bean.getAmount();
											amount-=totalCredit;
											if(debitAmount > amount){
												paymentAmount=amount;
												totalCredit=0;
											}
											else if(amount > debitAmount){
												totalCredit+=debitAmount;
												paymentAmount=debitAmount;
											}
											else{
												totalCredit=0;
												paymentAmount=debitAmount;
											}
											
											transaction.addTransaction(SConstants.CR,
																					(Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue(),
																					account,
																					roundNumber(paymentAmount),
																					"",
																					bean.getCurrency(),
																					roundNumber(bean.getConv_rate())
																					,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																					(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
											debitAmount-= paymentAmount;
											if(totalCredit==0)
												tempList.add(account);
										}
									}
								}
								TransactionModel transMdl= transaction.getTransaction(SConstants.JOURNAL, 
													CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								long id=dao.saveJounal(mdl, transMdl);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),"Journal Inquiry Created. Inquiry No : "+ id);
								loadJournal(id);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			journalCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						resetItems();
						if(journalCombo.getValue()!=null && !journalCombo.getValue().toString().equals("0")) {
							JournalModel mdl=dao.getJournalModel((Long)journalCombo.getValue());
							referenceNoField.setValue(mdl.getRef_no());
							previousDate=mdl.getDate();
							dateField.setValue(mdl.getDate());
							remarksArea.setValue(mdl.getRemarks());
							table.setVisibleColumns(allHeaders);
							Iterator itr=mdl.getJournal_details_list().iterator();
							while (itr.hasNext()) {
								JournalDetailsModel det = (JournalDetailsModel) itr.next();
								String department="",division="",currency="";
								double credit=0,debit=0;
								if(det.getDepartment_id()!=0)
									department=new DepartmentDao().getDepartment(det.getDepartment_id()).getName();
								else
									department="None";
								if(det.getDivision_id()!=0)
									division=new DivisionDao().getDivision(det.getDivision_id()).getName();
								else
									division="None";
								if(det.getTransaction_type()==1){
									credit=det.getAmount();
								}
								else if(det.getTransaction_type()==2){
									debit=det.getAmount();
								}
								if(det.getCurrencyId()!=0)
									currency=new CurrencyManagementDao().getselecteditem(det.getCurrencyId()).getSymbol();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										det.getLedger().getId(),
										det.getLedger().getName(),
										det.getDepartment_id(),
										department,
										det.getDivision_id(),
										division,
										det.getTransaction_type(),
										roundNumber(debit),
										roundNumber(credit),
										det.getCurrencyId(),
										currency,
										roundNumber(det.getConversionRate()),
										roundNumber(det.getAmount()/det.getConversionRate()),
										det.getBill_no()},table.getItemIds().size()+1);
							}
							table.setVisibleColumns(requiredHeaders);
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		
			
			updateButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							if(journalCombo.getValue()!=null && !journalCombo.getValue().toString().equals("0")) {
								List<JournalDetailsModel> journalList = new ArrayList<JournalDetailsModel>();
								Hashtable<Long, JournalBean> hashCredit = new Hashtable<Long, JournalBean>();
								List<Long> creditList = new ArrayList<Long>();
								double debitAmount=0,totalCredit=0;
								
								JournalModel mdl=dao.getJournalModel((Long)journalCombo.getValue());
								FinTransaction transaction=new FinTransaction();
								mdl.setRef_no(referenceNoField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setRemarks(remarksArea.getValue());
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								Iterator itr=table.getItemIds().iterator();
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									double amount=0;
									JournalDetailsModel det=new JournalDetailsModel();
									det.setLedger(new LedgerModel((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue()));
									det.setDepartment_id((Long)item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivision_id((Long)item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setTransaction_type((Integer)item.getItemProperty(TBC_TYPE).getValue());
									if((Integer)item.getItemProperty(TBC_TYPE).getValue()==1){
										amount=toDouble(item.getItemProperty(TBC_CREDIT).getValue().toString());
										hashCredit.put((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue(), 
												new JournalBean((Long)item.getItemProperty(TBC_CID).getValue(), 
														roundNumber(toDouble(item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue().toString())), 
														roundNumber(toDouble(item.getItemProperty(TBC_CONV_RATE).getValue().toString()))));
										creditList.add((Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue());
									}
									else if((Integer)item.getItemProperty(TBC_TYPE).getValue()==2){
										amount=toDouble(item.getItemProperty(TBC_DEBIT).getValue().toString());
									}
									det.setAmount(roundNumber(amount));
									det.setCurrencyId((Long)item.getItemProperty(TBC_CID).getValue());
									det.setConversionRate(roundNumber(toDouble(item.getItemProperty(TBC_CONV_RATE).getValue().toString())));
									det.setBill_no(item.getItemProperty(TBC_BILL).getValue().toString());
									journalList.add(det);
								}
								mdl.setJournal_details_list(journalList);
								List<Long> tempList = new ArrayList<Long>();
								itr=table.getItemIds().iterator();
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									int type=(Integer)item.getItemProperty(TBC_TYPE).getValue();
									long account=0;
									double paymentAmount=0;
									if(type==1){
										continue;
									}
									else if(type==2){
										debitAmount=roundNumber(toDouble(item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue().toString()));
										
										for(int i=0;i<creditList.size();i++){
											
											if(debitAmount<=0)
												break;
											
											account=creditList.get(i);
											
											if(tempList.contains(account))
												continue;
											
											JournalBean bean=hashCredit.get(account);
											double amount=bean.getAmount();
											amount-=totalCredit;
											if(debitAmount > amount){
												paymentAmount=amount;
												totalCredit=0;
											}
											else if(amount > debitAmount){
												totalCredit+=debitAmount;
												paymentAmount=debitAmount;
											}
											else{
												totalCredit=0;
												paymentAmount=debitAmount;
											}
											
											transaction.addTransaction(SConstants.CR,
																					(Long)item.getItemProperty(TBC_ACCOUNT_ID).getValue(),
																					account,
																					roundNumber(paymentAmount),
																					"",
																					bean.getCurrency(),
																					roundNumber(bean.getConv_rate())
																					,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																					(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
											debitAmount-= paymentAmount;
											if(totalCredit==0)
												tempList.add(account);
										}
									}
								}
								TransactionModel trObj=transaction.getTransaction(SConstants.JOURNAL, 
																				CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								trObj.setTransaction_id(dao.getTransactionIdFromJournal(mdl.getId()));
								mdl.setTransaction_id(trObj.getTransaction_id());
								
								dao.updateJounal(mdl,trObj);
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),"Journal Updated. Journal No : "+ mdl.getId());
								loadJournal(mdl.getId());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							if(journalCombo.getValue()!=null && !journalCombo.getValue().toString().equals("0")) {
								ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												dao.deleteJounal((Long) journalCombo.getValue());
												Notification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
												saveActivity(getOptionId(),"Journal Deleted. Journal No : "+ journalCombo.getItemCaption((Long) journalCombo.getValue()));
												loadJournal(0);
											} catch (Exception e) {
												e.printStackTrace();
												Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
											}
										}
									}
								});
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Add",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});
			
			
			final Action actionDelete = new Action("Delete");

			
			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					return new Action[] { actionDelete };
				}
				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					deleteItem();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadJournal(long id) {
		try {
			List list = new ArrayList();
			list.add(new JournalModel(0, "----Create New-----"));
			list.addAll(dao.getJournalModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			journalCombo.setContainerDataSource(bic);
			journalCombo.setItemCaptionPropertyId("bill_no");
			journalCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	
	public void resetItems(){
		table.removeAllItems();
		table.setValue(null);
		referenceNoField.setValue("");
		previousDate=getWorkingDate();
		dateField.setValue(getWorkingDate());
		remarksArea.setValue("");
		differenceField.setNewValue("0.0");
		saveButton.setVisible(true);
		updateButton.setVisible(false);
		deleteButton.setVisible(false);
	}
	
	
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				table.removeItem(table.getValue());
				getDifference();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getDifference(){
		Object [] header={TBC_TYPE};
		boolean [] ordering={false};
		table.sort(header, ordering);
		
		Iterator itr=table.getItemIds().iterator();
		double credit=0,debit=0;
		int SN = 0;
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			SN++;
			item.getItemProperty(TBC_SN).setValue(SN);
			int type=(Integer)item.getItemProperty(TBC_TYPE).getValue();
			if(type==1){
				credit+=(Double)item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue();
			}
			else if(type==2){
				debit+=(Double)item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue();
			}
		}
		differenceField.setNewValue(roundNumber(debit-credit)+"");
	}
	
	
	public Boolean isAddingValid() {
		boolean valid=true;
		
		if(transactionTypeSelect.getValue()==null || transactionTypeSelect.getValue().toString().equals("")){
			valid=false;
			setRequiredError(transactionTypeSelect, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(transactionTypeSelect, null, false);
		
		if(ledgerCombo.getValue()==null || ledgerCombo.getValue().toString().equals("")){
			valid=false;
			setRequiredError(ledgerCombo, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(ledgerCombo, null, false);
		
		if(dateField.getValue()==null || dateField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(dateField, null, false);
		
		if(settings.isDEPARTMENT_ENABLED()){
			if(departmentCombo.getValue()==null || departmentCombo.getValue().toString().equals("")){
				valid=false;
				setRequiredError(departmentCombo, getPropertyName("invalid_selection"), true);
			}
			else
				setRequiredError(departmentCombo, null, false);
		}

		if(dateField.getValue()!=null){
			if(!amountField.isFieldValid(dateField.getValue()))
				valid=false;
		}
		
		if(amountField.getValue()<=0){
			valid=false;
			setRequiredError(amountField, getPropertyName("invalid_data"), true);
		}
		else
			setRequiredError(amountField, null, false);
		
		return valid;
	}
	
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		
		if(table.getItemIds().size()<=0){
			valid=false;
			setRequiredError(table, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(table, null, false);
		
		if(dateField.getValue()==null || dateField.getValue().toString().equals("")){
			valid=false;
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(dateField, null, false);
		
		try {
			if (toDouble(differenceField.getValue()) != 0) {
				setRequiredError(differenceField, getPropertyName("debit_credit_not_matching"), true);
				valid = false;
			} 
			else
				setRequiredError(differenceField, null, false);
		} catch (Exception e) {
			setRequiredError(differenceField, getPropertyName("debit_credit_not_matching"), true);
			valid = false;
		}
		
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}
	
}
