package com.inventory.journal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.journal.dao.JournalDao;
import com.inventory.journal.model.JournalModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 *
 * Jul 11, 2013
 */
public class JournalOriginalConcept extends SparkLogic {

	SPanel panel=null;
	
	static String TSR_SN="SN";
	static String TSJ_LEDGER_ID="Account ID";
	static String TSJ_LEDGER_NAME="Account Name";
	static String TSJ_DEBIT_VALUE="Debit";
	static String TSJ_CREDIT_VALUE="Credit";
	static String TSJ_CR_OR_DR="Amt Type";
	
	STable journalEntryTable;
	JournalDao daoObj;
	
	SGridLayout masterDetailsGrid;
	SGridLayout stkrakmapGrid;
	SVerticalLayout stkrkVLay;
	
	SComboField journalNumberList;
	SComboField ledgerSelect;
	SNativeSelect amountTypeSelect;
	
	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	SNativeButton saveJournal;
	STextField amountTextField;
	SDateField date;
	
	STextField refNoTextField;
	STextArea memoTextArea;
	
	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		
		daoObj=new JournalDao();
		
		addItemButton=new SNativeButton("Add");
		updateItemButton=new SNativeButton("Update");
		saveJournal=new SNativeButton("Save");
		
		date=new SDateField();
		refNoTextField=new STextField();
		memoTextArea=new STextArea();
		
		panel=new SPanel();
		
		
		setSize(700, 400);
		try {
			
			updateItemButton.setVisible(false);
			
			journalEntryTable=new STable(null, 600,200);
			
			List list=new ArrayList();
			list.add(new JournalModel(0, "----Create New-----"));
			list.addAll(daoObj.getJournalModelList(getOfficeID()));
			
			journalNumberList=new SComboField(null, 200, list, "id", "bill_no",true, "Create New");
			
		
			stkrakmapGrid=new SGridLayout();
			stkrkVLay=new SVerticalLayout();
			
			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(1);
			
			journalEntryTable.setSizeFull();
			journalEntryTable.setSelectable(true);
			journalEntryTable.setMultiSelect(true);
			
			journalEntryTable.setWidth("660px");
			journalEntryTable.setHeight("180px");
			
			journalEntryTable.addContainerProperty(TSR_SN, Integer.class, null,"#", null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_LEDGER_ID, Long.class, null,TSJ_LEDGER_ID , null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_LEDGER_NAME, String.class, null,TSJ_LEDGER_NAME , null, Align.LEFT);
			journalEntryTable.addContainerProperty(TSJ_DEBIT_VALUE, Double.class, null,TSJ_DEBIT_VALUE , null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_CREDIT_VALUE, Double.class, null,TSJ_CREDIT_VALUE , null, Align.CENTER);
			journalEntryTable.addContainerProperty(TSJ_CR_OR_DR, String.class, null,TSJ_CR_OR_DR , null, Align.LEFT);
			
			journalEntryTable.setColumnExpandRatio(TSR_SN,(float).5);
			journalEntryTable.setColumnExpandRatio(TSJ_CR_OR_DR,2);
			journalEntryTable.setColumnExpandRatio(TSJ_LEDGER_NAME,1);
			
			stkrakmapGrid.setColumns(8);
			stkrakmapGrid.setRows(2);
			
			amountTypeSelect=new SNativeSelect("Amount Type", 50, SConstants.amountTypes , "stringKey", "value");
			amountTypeSelect.setValue("DR");
			ledgerSelect=new SComboField("Account", 200, new LedgerDao().getAllActiveLedgerNames(getOfficeID()), "id", "name",true, "Select");
			
			amountTextField=new STextField("Quantity", 80);
//			stkrakmapGrid.addComponent();
			stkrakmapGrid.addComponent(ledgerSelect);
			stkrakmapGrid.addComponent(amountTypeSelect);
			stkrakmapGrid.addComponent(amountTextField);
			stkrakmapGrid.addComponent(addItemButton);
			stkrakmapGrid.addComponent(updateItemButton);
			
			amountTextField.setStyleName("textfield_align_right");
			
			stkrakmapGrid.setComponentAlignment(addItemButton, Alignment.BOTTOM_RIGHT);
			stkrakmapGrid.setComponentAlignment(updateItemButton, Alignment.BOTTOM_RIGHT);
			
			stkrakmapGrid.setSpacing(true);
			
			masterDetailsGrid.addComponent(new SLabel("Journal ID :"), 1, 0);
			masterDetailsGrid.addComponent(journalNumberList, 2, 0);
			masterDetailsGrid.addComponent(new SLabel("Date :"), 6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			
			masterDetailsGrid.addComponent(new SLabel("Ref. No. :"), 3, 0);
			masterDetailsGrid.addComponent(refNoTextField, 4, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
			
			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);
			
			masterDetailsGrid.setStyleName("master_border");
			
			stkrkVLay.addComponent(masterDetailsGrid);
			
			stkrkVLay.addComponent(journalEntryTable);
			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);
			
			stkrkVLay.addComponent(stkrakmapGrid);
			stkrkVLay.addComponent(saveJournal);
			stkrkVLay.setComponentAlignment(saveJournal, Alignment.BOTTOM_CENTER);
			
			
			
			
			journalEntryTable.setVisibleColumns(new String[] {TSR_SN, TSJ_LEDGER_NAME, TSJ_DEBIT_VALUE,TSJ_CREDIT_VALUE});
			
			panel.setContent(stkrkVLay);
			
			ledgerSelect.focus();
			
			journalEntryTable.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					
					
					Collection selectedItems=null;
					
					if(journalEntryTable.getValue()!=null) {
						selectedItems=(Collection) journalEntryTable.getValue();
					}
					
					if(selectedItems!=null && selectedItems.size()==1) {
					
						Item item=journalEntryTable.getItem(selectedItems.iterator().next());
						
						ledgerSelect.setValue(item.getItemProperty(TSJ_LEDGER_ID).getValue());
						
						double credit=0,debit=0;
						if(item.getItemProperty(TSJ_CR_OR_DR).getValue().toString().equals("DR")) {
							amountTextField.setValue(asString(item.getItemProperty(TSJ_DEBIT_VALUE).getValue()));
						}
						else {
							amountTextField.setValue(asString(item.getItemProperty(TSJ_CREDIT_VALUE).getValue()));
						}
						
						amountTypeSelect.setValue(item.getItemProperty(TSJ_CR_OR_DR).getValue());
						
						updateItemButton.setVisible(true);
						addItemButton.setVisible(false);
						
						ledgerSelect.focus();
					}
					else {
						updateItemButton.setVisible(false);
						addItemButton.setVisible(true);
						ledgerSelect.setValue(null);
						amountTextField.setValue("");
						ledgerSelect.focus();
					}
				}
			});
			
			
			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(journalEntryTable, null,false);
						if(isAddingValid()) {
							
							journalEntryTable.setVisibleColumns(new String[] {TSR_SN,TSJ_LEDGER_ID,TSJ_LEDGER_NAME,TSJ_DEBIT_VALUE,TSJ_CREDIT_VALUE,TSJ_CR_OR_DR});
							
							
							Collection selectedItems=null;
							
							if(journalEntryTable.getValue()!=null){
								selectedItems=(Collection) journalEntryTable.getValue();
							}
							
							int sel_id=(Integer) selectedItems.iterator().next();
							Item item=journalEntryTable.getItem(sel_id);
							
							item.getItemProperty(TSJ_LEDGER_ID).setValue(ledgerSelect.getValue());
							item.getItemProperty(TSJ_LEDGER_NAME).setValue(ledgerSelect.getItemCaption(ledgerSelect.getValue()));
							
							double credit=0,debit=0;
							if(amountTypeSelect.getValue().toString().equals("DR")){
								debit=toDouble(amountTextField.getValue());
							}
							else {
								credit=toDouble(amountTextField.getValue());
							}
							
							item.getItemProperty(TSJ_DEBIT_VALUE).setValue(debit);
							item.getItemProperty(TSJ_CREDIT_VALUE).setValue(credit);
							
							item.getItemProperty(TSJ_CR_OR_DR).setValue(amountTypeSelect.getValue().toString());
							
							
							journalEntryTable.setVisibleColumns(new String[] {TSR_SN, TSJ_LEDGER_NAME, TSJ_DEBIT_VALUE,TSJ_CREDIT_VALUE});
							
							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);
							
							journalEntryTable.setValue(null);
							
							calculateTotals();
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});
			
			
			
			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(journalEntryTable, null,false);
						if(isAddingValid()) {
							
							boolean exist=false;
							int exist_id=0;
							double total_qty=0;
							journalEntryTable.setVisibleColumns(new String[] {TSR_SN,TSJ_LEDGER_ID,TSJ_LEDGER_NAME,TSJ_DEBIT_VALUE,TSJ_CREDIT_VALUE,TSJ_CR_OR_DR});
							
							int id=0, ct=journalEntryTable.getItemIds().size();
							Iterator it1=journalEntryTable.getItemIds().iterator();
							while (it1.hasNext()) {
								id=(Integer) it1.next();
							}
							
			    			id++;
							ct++;
							
							double credit=0,debit=0;
							if(amountTypeSelect.getValue().toString().equals("DR")){
								debit=toDouble(amountTextField.getValue());
							}
							else {
								credit=toDouble(amountTextField.getValue());
							}
							
							journalEntryTable.addItem(new Object[] {ct,(Long)ledgerSelect.getValue(), ledgerSelect.getItemCaption(ledgerSelect.getValue()), 
									debit, credit,amountTypeSelect.getValue().toString()}, id);
							
							
							journalEntryTable.setVisibleColumns(new String[] {TSR_SN, TSJ_LEDGER_NAME, TSJ_DEBIT_VALUE,TSJ_CREDIT_VALUE});
							
							
							ledgerSelect.setValue(null);
							amountTextField.setValue("");
							ledgerSelect.focus();
							
							
							calculateTotals();
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});
			
			
			
			saveJournal.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						
						if(isValid()) {
							
							JournalModel objMdl=new JournalModel();
							
							objMdl.setDate(CommonUtil.getSQLDateFromUtilDate(date.getValue()));
							objMdl.setLogin_id(getLoginID());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setRef_no(refNoTextField.getValue());
							
						
							Iterator it=journalEntryTable.getItemIds().iterator();
							while (it.hasNext()) {
								Item item=journalEntryTable.getItem(it.next());
								
								
								
							}
							
//							daoObj.saveJounal(objMdl);
							
							Notification.show("Success", "Mapped Successfully..!",
			                        Type.WARNING_MESSAGE);
							
							/*Object temp=stockSelect.getValue();
							
							journalEntryTable.removeAllItems();
							if((Long) modeSelect.getValue()==3) {
								stockSelect.setContainerDataSource(SCollectionContainer.setList(
										daoObj.getPendingAvailableStocksList(), "id"));
								stockSelect.setItemCaptionPropertyId("details");
							}
							else if((Long) modeSelect.getValue()==2) {
								stockSelect.setContainerDataSource(SCollectionContainer.setList(
										daoObj.getArrangedAvailabeStocksList(), "id"));
								stockSelect.setItemCaptionPropertyId("details");
							}
							
							stockSelect.setValue(temp);*/
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});
			
			
			journalEntryTable.addShortcutListener(new ShortcutListener("Clear entereded and edited data and Add new", ShortcutAction.KeyCode.ESCAPE, null) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	journalEntryTable.setValue(null);
		        }
		    });
			
			journalEntryTable.addShortcutListener(new ShortcutListener("Submit Item", ShortcutAction.KeyCode.ENTER, null) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	if(addItemButton.isVisible())
		        		addItemButton.click();
		        	else
		        		updateItemButton.click();
		        }
		    });
			
			
			final Action actionDeleteStock = new Action("Delete");

	        journalEntryTable.addActionHandler(new Action.Handler() {
	            @Override
	            public Action[] getActions(final Object target, final Object sender) {
	            	 return new Action[] { actionDeleteStock };
	            }

	            @Override
	            public void handleAction(final Action action, final Object sender,
	                    final Object target) {
	            	deleteItem();
	            }

	        });
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return panel;
	}
	
	
	
	public void deleteItem(){
		try {
			
			if(journalEntryTable.getValue()!=null){
				
				Collection selectedItems=(Collection) journalEntryTable.getValue();
				Iterator it1=selectedItems.iterator();
				while(it1.hasNext()){
					journalEntryTable.removeItem(it1.next());
				}
				
				
				int SN=0;
				Iterator it=journalEntryTable.getItemIds().iterator();
    			while (it.hasNext()) {
    				SN++;
    				
    				Item newitem=journalEntryTable.getItem((Integer) it.next());
					
					newitem.getItemProperty( TSR_SN).setValue(SN);
    				
				}
			}
			journalEntryTable.focus();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public Boolean isAddingValid() {
		// TODO Auto-generated method stub
		boolean ret=true;
		
		if(amountTextField.getValue()==null || amountTextField.getValue().equals("")){
			setRequiredError(amountTextField, "Enter an Amount",true);
			amountTextField.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(amountTextField.getValue())<0){
					setRequiredError(amountTextField, "Enter a valid amount",true);
					amountTextField.focus();
					ret=false;
				}
				else
					setRequiredError(amountTextField, null,false);
			} catch (Exception e) {
				setRequiredError(amountTextField, "Enter a valid amount",true);
				amountTextField.focus();
				ret=false;
				// TODO: handle exception
			}
		}
		
		if(amountTypeSelect.getValue()==null || amountTypeSelect.getValue().equals("")){
			setRequiredError(amountTypeSelect, "Select an Amount Type",true);
			amountTypeSelect.focus();
			ret=false;
		}
		else
			setRequiredError(amountTypeSelect, null,false);
		
		if(ledgerSelect.getValue()==null || ledgerSelect.getValue().equals("")){
			setRequiredError(ledgerSelect, "Select a Account",true);
			ledgerSelect.focus();
			ret=false;
		}
		else
			setRequiredError(ledgerSelect, null,false);
		
		
		return ret;
	}
	
	
	
	public void calculateTotals(){
		try {
			
			double crttl=0, drttl=0;
			
			Iterator it=journalEntryTable.getItemIds().iterator();
			while (it.hasNext()) {
				Item item=journalEntryTable.getItem(it.next());
				
				crttl+=(Double)item.getItemProperty( TSJ_CREDIT_VALUE).getValue();
				drttl+=(Double)item.getItemProperty( TSJ_DEBIT_VALUE).getValue();
			}
			
			journalEntryTable.setColumnFooter(TSJ_CREDIT_VALUE, asString(roundNumber(crttl)));
			journalEntryTable.setColumnFooter(TSJ_DEBIT_VALUE, asString(roundNumber(drttl)));
	        
			
		} catch (Exception e) {
			// TODO: handle exception
			Notification.show("Error..!!", "Error Message from Method calculateTotal() :"+e.getCause(),
                    Type.ERROR_MESSAGE);
		}
	}
	
	

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret=true;
		
		if(journalEntryTable.getItemIds().size()<=0){
			setRequiredError( journalEntryTable, "Add some items",true);
			ledgerSelect.focus();
			ret=false;
		}
		else
			setRequiredError( journalEntryTable, null,false);
		
		
		if(journalEntryTable.getColumnFooter(TSJ_CREDIT_VALUE)!=journalEntryTable.getColumnFooter(TSJ_DEBIT_VALUE)) {
			setRequiredError( journalEntryTable, "Credit total and Dbit total must be equal..!",true);
			ledgerSelect.focus();
			ret=false;
		}
		else
			setRequiredError( journalEntryTable, null,false);
		
		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
