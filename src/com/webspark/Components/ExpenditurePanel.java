package com.webspark.Components;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.ClearingAgentDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.webspark.bean.ExpenseBean;
import com.webspark.bean.ExpenseTransactionBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.CurrencyManagementDao;

/**
 * @author anil
 *
 */

/**
 * 
 * @author sangeeth
 *
 */
public class ExpenditurePanel extends SWindow{

	private static final long serialVersionUID = 615975969145383704L;
	
	SNativeSelect transactionTypeSelect;
	SRadioButton ledgerRadio;
	public SCurrencyField amountField;
	SComboField ledgerCombo;
	SButton addButton;
	SButton updateButton;
	WrappedSession session;
	SettingsValuePojo settings;
	STable table;
	SHorizontalLayout hlay;
	
	private static String TBC_ID = "Id";
	private static String TBC_CLEARING_AGENT = "Clearing Agent";
	private static String TBC_TYPE_ID="Type Id";
	private static String TBC_TYPE="Type";
	private static String TBC_LEDGER = "Ledger";
	private static String TBC_CID="Currency Id";
	private static String TBC_CURRENCY="Currency";
	private static String TBC_CONV_RATE="Conv Rate";
	private static String TBC_BASE_CURRENCY_VALUE="Base Currency";
	private static String TBC_AMOUNT = "Amount";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	LedgerDao ledgDao;
	
	 @SuppressWarnings("serial")
	public ExpenditurePanel(String caption) {
		try {
			SVerticalLayout ver=new  SVerticalLayout();
			ver.setMargin(true);
			ledgDao=new LedgerDao();
			setResizable(false);
			allHeaders=new Object[]{TBC_ID, TBC_CLEARING_AGENT, TBC_TYPE_ID, TBC_TYPE, TBC_LEDGER, 
					TBC_CID, TBC_CURRENCY, TBC_CONV_RATE, TBC_BASE_CURRENCY_VALUE, TBC_AMOUNT};
			requiredHeaders=new Object[]{ TBC_TYPE, TBC_LEDGER, TBC_CURRENCY, TBC_AMOUNT };
				
			session = new SessionUtil().getHttpSession();
			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
			hlay=new SHorizontalLayout();
			
			transactionTypeSelect = new SNativeSelect(null, 60, SConstants.transactionType,"intKey", "value");
			transactionTypeSelect.setValue(SConstants.DR);
			ledgerRadio = new SRadioButton(null, 100, SConstants.ledgerType,"key", "value");
			ledgerRadio.setValue(SConstants.GENERAL);
			
			
			amountField=new SCurrencyField(null,75, (Date) session.getAttribute("working_date"));
			amountField.currencySelect.setVisible(false);
			amountField.rateButton.setVisible(false);
			amountField.setStyleName("textfield_align_right");
			amountField.setValue((Long) session.getAttribute("currency_id"), 0);
			
			ledgerCombo=new SComboField(null, 150, 
										new LedgerDao().getAllActiveLedgerNamesExcluding(
												Long.parseLong(session.getAttribute("office_id").toString()),
												SConstants.LEDGER_ADDED_INDIRECTLY, 
												settings.getCLEARING_AGENT_GROUP()),
										"id", "name");
			ledgerCombo.setInputPrompt("Select");
			addButton=new SButton("Add");
			updateButton=new SButton("Update");
			updateButton.setVisible(false);
			
			table=new STable(null,550,150);
			
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CLEARING_AGENT, Boolean.class, null, TBC_CLEARING_AGENT, null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE_ID, Integer.class, null, TBC_TYPE_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE, String.class, null, TBC_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBC_LEDGER, String.class, null, TBC_LEDGER, null, Align.LEFT);
			table.addContainerProperty(TBC_CID, Long.class, null, TBC_CID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY, String.class, null, TBC_CURRENCY, null, Align.LEFT);
			table.addContainerProperty(TBC_CONV_RATE, Double.class, null,TBC_CONV_RATE, null, Align.RIGHT);
			table.addContainerProperty(TBC_BASE_CURRENCY_VALUE, Double.class, null,TBC_BASE_CURRENCY_VALUE, null, Align.RIGHT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null,TBC_AMOUNT, null, Align.RIGHT);
			table.setSelectable(true);
			table.setFooterVisible(true);
			
			table.setColumnFooter(TBC_TYPE, "Total");
			table.setColumnFooter(TBC_AMOUNT, "0.0");
			table.setVisibleColumns(requiredHeaders);
			
			
			hlay.addComponent(ledgerRadio);
			hlay.addComponent(transactionTypeSelect);
			hlay.addComponent(ledgerCombo);
			hlay.addComponent(ledgerCombo);
			hlay.addComponent(amountField);
			hlay.addComponent(addButton);
			hlay.addComponent(updateButton);
			
			ver.addComponent(table);
			ver.addComponent(hlay);
			ver.setSpacing(true);
			hlay.setSpacing(true);
			
			setContent(ver);
			center();
			setModal(true);
			setCaption(caption);
			
			
			addShortcutListener(new ShortcutListener("Submit Item", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
	
					if (addButton.isVisible())
						addButton.click();
					else
						updateButton.click();
				}
			});
			
			
			ledgerRadio.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						List list=new ArrayList();
						if(ledgerRadio.getValue()!=null){
							if((Long)ledgerRadio.getValue()==SConstants.GENERAL){
								transactionTypeSelect.setReadOnly(false);
								transactionTypeSelect.setValue(SConstants.DR);
								list=new LedgerDao().getAllActiveLedgerNamesExcluding(
										Long.parseLong(session.getAttribute("office_id").toString()),
										SConstants.LEDGER_ADDED_INDIRECTLY, 
										settings.getCLEARING_AGENT_GROUP());
								SCollectionContainer bic=SCollectionContainer.setList(list, "id");
								ledgerCombo.setContainerDataSource(bic);
								ledgerCombo.setItemCaptionPropertyId("name");
								ledgerCombo.setValue(null);
							}
							else{
								transactionTypeSelect.setValue(SConstants.CR);
								transactionTypeSelect.setReadOnly(true);
								list=new ClearingAgentDao().getAllActiveClearingAgentNamesWithLedgerID(Long.parseLong(session.getAttribute("office_id").toString()));
								SCollectionContainer bic=SCollectionContainer.setList(list, "id");
								ledgerCombo.setContainerDataSource(bic);
								ledgerCombo.setItemCaptionPropertyId("name");
								ledgerCombo.setValue(null);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			addButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.setVisibleColumns(allHeaders);
						if(valid()&&isNotAdded()){
							
							boolean isClearingAgent=false;
							
							if((Long)ledgerRadio.getValue()==SConstants.CLEARING_AGENT){
								isClearingAgent = true;
							}
							else{
								isClearingAgent = false;
							}
							
							table.addItem(new Object[] {
									Long.parseLong(ledgerCombo.getValue().toString()),
									isClearingAgent,
									(Integer)transactionTypeSelect.getValue(),
									transactionTypeSelect.getItemCaption(transactionTypeSelect.getValue()),
									ledgerCombo.getItemCaption(ledgerCombo.getValue()),
									amountField.getCurrency(),
									new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getSymbol(),
									CommonUtil.roundNumber(amountField.getConversionRate()),
									CommonUtil.roundNumber(amountField.getValue()/amountField.getConversionRate()),
									CommonUtil.roundNumber(amountField.getValue())}, table.getItemIds().size()+1);
							table.setValue(null);
							ledgerRadio.setValue(SConstants.GENERAL);
							transactionTypeSelect.setNewValue(SConstants.DR);
							ledgerCombo.setValue(null);
							amountField.setValue(0);
							amountField.rateButton.setVisible(false);
							addButton.setVisible(true);
							updateButton.setVisible(false);
							calculateTotal();
						}
						table.setVisibleColumns(requiredHeaders);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(table.getValue()!=null){
							Item item = table.getItem(table.getValue());
							if((Boolean)item.getItemProperty(TBC_CLEARING_AGENT).getValue()){
								ledgerRadio.setValue(SConstants.CLEARING_AGENT);
							}
							else{
								ledgerRadio.setValue(SConstants.GENERAL);
							}
							transactionTypeSelect.setNewValue(item.getItemProperty(TBC_TYPE_ID).getValue());
							ledgerCombo.setValue(item.getItemProperty(TBC_ID).getValue());
							amountField.setValue((Double)item.getItemProperty(TBC_AMOUNT).getValue());
							amountField.rateButton.setVisible(false);
							addButton.setVisible(false);
							updateButton.setVisible(true);
						}
						else{
							ledgerRadio.setValue(SConstants.GENERAL);
							transactionTypeSelect.setNewValue(SConstants.DR);
							ledgerCombo.setValue(null);
							amountField.setValue(0);
							amountField.rateButton.setVisible(false);
							addButton.setVisible(true);
							updateButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			updateButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						table.setVisibleColumns(allHeaders);
						if(valid()) {
							Item item = table.getItem(table.getValue());
							boolean isClearingAgent=false;
							
							if((Long)ledgerRadio.getValue()==SConstants.CLEARING_AGENT){
								isClearingAgent = true;
							}
							else{
								isClearingAgent = false;
							}
							item.getItemProperty(TBC_ID).setValue(Long.parseLong(ledgerCombo.getValue().toString()));
							item.getItemProperty(TBC_CLEARING_AGENT).setValue(isClearingAgent);
							item.getItemProperty(TBC_TYPE_ID).setValue((Integer)transactionTypeSelect.getValue());
							item.getItemProperty(TBC_TYPE).setValue(transactionTypeSelect.getItemCaption(transactionTypeSelect.getValue()));
							item.getItemProperty(TBC_LEDGER).setValue(ledgerCombo.getItemCaption(ledgerCombo.getValue()));
							item.getItemProperty(TBC_CID).setValue(amountField.getCurrency());
							item.getItemProperty(TBC_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getSymbol());
							item.getItemProperty(TBC_CONV_RATE).setValue(CommonUtil.roundNumber(amountField.getConversionRate()));
							item.getItemProperty(TBC_BASE_CURRENCY_VALUE).setValue(CommonUtil.roundNumber(amountField.getValue()/amountField.getConversionRate()));
							item.getItemProperty(TBC_AMOUNT).setValue(CommonUtil.roundNumber(amountField.getValue()));
							
							table.setValue(null);
							ledgerRadio.setValue(SConstants.GENERAL);
							transactionTypeSelect.setNewValue(SConstants.DR);
							ledgerCombo.setValue(null);
							amountField.setValue(0);
							amountField.rateButton.setVisible(false);
							addButton.setVisible(true);
							updateButton.setVisible(false);
							calculateTotal();
						}
						table.setVisibleColumns(requiredHeaders);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			final Action actionDeleteStock = new Action("Delete");
	
			
			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					
					return new Action[] { actionDeleteStock };
				}
	
				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					if(table.getValue()!=null){
						table.removeItem(table.getValue());
					}
					calculateTotal();
				}
			});
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 
	@SuppressWarnings("rawtypes")
	protected void calculateTotal() {
		Object [] header={TBC_TYPE_ID};
		boolean [] ordering={false};
		table.sort(header, ordering);
		Iterator it = table.getItemIds().iterator();
		Item item;
		double totalCredit=0;
		double totalDebit=0;
		while (it.hasNext()) {
			item = table.getItem(it.next());
			int type=(Integer)item.getItemProperty(TBC_TYPE_ID).getValue();
			if(type==SConstants.DR){
				totalDebit+=(Double)item.getItemProperty(TBC_AMOUNT).getValue();
			}
			else{
				totalCredit+=(Double)item.getItemProperty(TBC_AMOUNT).getValue();
			}
		}
		table.setColumnFooter(TBC_AMOUNT,CommonUtil.roundNumber(totalDebit-totalCredit)+ "");
	}

	
	public void clearAll(){
		table.removeAllItems();
		calculateTotal();
	}

	
	protected boolean valid() {
		boolean valid=true;
		if(amountField.getValue()<=0){
			valid=false;
			setRequiredError(amountField, "Invalid Data", true);
		}
		else
			setRequiredError(amountField, null, false);
		
		if (ledgerCombo.getValue() == null || ledgerCombo.getValue().equals("")) {
			setRequiredError(ledgerCombo,("invalid_selection"), true);
			ledgerCombo.focus();
			valid = false;
		} else
			setRequiredError(ledgerCombo, null, false);
		
		return valid;
	}
	
	
	public void setCurrency(long cid){
		amountField.currencySelect.setValue(cid);
		amountField.rateButton.setVisible(false);
	}
	
	
	@SuppressWarnings("rawtypes")
	protected boolean isNotAdded() {
		Iterator it = table.getItemIds().iterator();
		Item item;
		long val=Long.parseLong(ledgerCombo.getValue().toString());
		while (it.hasNext()) {
			item = table.getItem(it.next());
			if(val==Long.parseLong(item.getItemProperty(TBC_ID).getValue().toString())){
				setRequiredError(ledgerCombo,("invalid_selection"), true);
				return false;
			}
		}
		return true;
	}
	
	
	public void setRequiredError(AbstractComponent component,
			String fieldNameToDisplay, boolean enable) {
		if (enable) {
			component.setComponentError(new SUserError(
					"<i style='font-size: 13px;'>" + fieldNameToDisplay,
					ContentMode.HTML, ErrorLevel.CRITICAL));
		} else
			component.setComponentError(null);
	}
	
	
	public double getAmount() {
		return Double.parseDouble(table.getColumnFooter(TBC_AMOUNT).toString());		
	}
	
	
	@SuppressWarnings("rawtypes")
	public double getDebitAmount() {
		Iterator it = table.getItemIds().iterator();
		double totalDebit=0;
		while (it.hasNext()) {
			Item item = table.getItem(it.next());
			int type=(Integer)item.getItemProperty(TBC_TYPE_ID).getValue();
			if(type==SConstants.DR){
				totalDebit+=(Double)item.getItemProperty(TBC_AMOUNT).getValue();
			}
		}
		return CommonUtil.roundNumber(totalDebit);		
	}
	
	
	@SuppressWarnings("rawtypes")
	public double getConversionrate() {
		Iterator it = table.getItemIds().iterator();
		double rate=0;
		while (it.hasNext()) {
			Item item = table.getItem(it.next());
			rate=(Double)item.getItemProperty(TBC_CONV_RATE).getValue();
		}
		return CommonUtil.roundNumber(rate);		
	}
	
	
	@SuppressWarnings("rawtypes")
	public double getCreditAmount() {
		Iterator it = table.getItemIds().iterator();
		double totalCredit=0;
		while (it.hasNext()) {
			Item item = table.getItem(it.next());
			int type=(Integer)item.getItemProperty(TBC_TYPE_ID).getValue();
			if(type==SConstants.CR){
				totalCredit+=(Double)item.getItemProperty(TBC_AMOUNT).getValue();
			}
		}
		return CommonUtil.roundNumber(totalCredit);		
	}
	
	
	@SuppressWarnings({ "rawtypes"})
	public List getValue() {
		List<ExpenseBean> resultList = new ArrayList<ExpenseBean>();
		if(getAmount()>=0){
			Iterator itr=table.getItemIds().iterator();
			while (itr.hasNext()) {
				Item item = table.getItem(itr.next());
				ExpenseBean bean= new ExpenseBean();
				bean.setLedger((Long)item.getItemProperty(TBC_ID).getValue());
				bean.setClearingAgent((Boolean)item.getItemProperty(TBC_CLEARING_AGENT).getValue());
				bean.setTransactionType((Integer)item.getItemProperty(TBC_TYPE_ID).getValue());
				bean.setCurrencyId((Long)item.getItemProperty(TBC_CID).getValue());
				bean.setConversionRate(CommonUtil.roundNumber((Double)item.getItemProperty(TBC_CONV_RATE).getValue()));
				bean.setAmount(CommonUtil.roundNumber((Double)item.getItemProperty(TBC_AMOUNT).getValue()));
				resultList.add(bean);
			}
		}
		return resultList;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getTransactionList(){
		List<ExpenseTransactionBean> resultList = new ArrayList<ExpenseTransactionBean>();
		try {
			List<Long> creditList = new ArrayList<Long>();
			Hashtable<Long, ExpenseBean> hashCredit = new Hashtable<Long, ExpenseBean>();
			double debitAmount=0,totalCredit=0,balanceAmount=0;
			
			if(getAmount()>=0){
				Iterator itr=table.getItemIds().iterator();
				while (itr.hasNext()) {
					Item item = table.getItem(itr.next());
					
					if((Integer)item.getItemProperty(TBC_TYPE_ID).getValue()==SConstants.CR) {
						creditList.add((Long)item.getItemProperty(TBC_ID).getValue());
						hashCredit.put((Long)item.getItemProperty(TBC_ID).getValue(), 
								new ExpenseBean((Long)item.getItemProperty(TBC_CID).getValue(),
														CommonUtil.roundNumber((Double)item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue()), 
														CommonUtil.roundNumber((Double)item.getItemProperty(TBC_CONV_RATE).getValue())));
					}
				}
				
				List<Long> tempList = new ArrayList<Long>();
				itr=table.getItemIds().iterator();
				ExpenseTransactionBean transBean=null;
				while (itr.hasNext()) {
					Item item = table.getItem(itr.next());
					
					
					int type=(Integer)item.getItemProperty(TBC_TYPE_ID).getValue();
					long account=0;
					double paymentAmount=0;
					if(type == SConstants.CR){
						continue;
					}
					else if(type == SConstants.DR){
						debitAmount=CommonUtil.roundNumber((Double)item.getItemProperty(TBC_BASE_CURRENCY_VALUE).getValue());
						
						balanceAmount=debitAmount;
						
//						System.out.println("Debit Amount Parent "+debitAmount);
						
						if(creditList.size()>0){
							
							if(tempList.size()<creditList.size()) {
								
								for(int i=0;i<creditList.size();i++){
									
									if(debitAmount<=0)
										break;
//									System.out.println("Debit Amount Child "+debitAmount);
									
									account=creditList.get(i);
									
									if(tempList.contains(account))
										continue;
									
									ExpenseBean bean=hashCredit.get(account);
									
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
									balanceAmount-=paymentAmount;
									
//									System.out.println("Payment Amount "+paymentAmount);
									
									transBean=new ExpenseTransactionBean((Long)item.getItemProperty(TBC_ID).getValue(), 
															account, 
															bean.getCurrencyId(),
															CommonUtil.roundNumber(bean.getConversionRate()),
															CommonUtil.roundNumber(paymentAmount));
									
									resultList.add(transBean);
									debitAmount-= paymentAmount;
									if(totalCredit==0)
										tempList.add(account);
									
									if(tempList.size()>=creditList.size()){
										transBean=new ExpenseTransactionBean((Long)item.getItemProperty(TBC_ID).getValue(), 
																(long)0, 
																(Long)item.getItemProperty(TBC_CID).getValue(),
																CommonUtil.roundNumber((Double)item.getItemProperty(TBC_CONV_RATE).getValue()),
																CommonUtil.roundNumber(balanceAmount));
//										System.out.println("Balance Amount 1 "+balanceAmount);
										resultList.add(transBean);
									}
								}
							}
							else{
								transBean=new ExpenseTransactionBean((Long)item.getItemProperty(TBC_ID).getValue(), 
														(long)0, 
														(Long)item.getItemProperty(TBC_CID).getValue(),
														CommonUtil.roundNumber((Double)item.getItemProperty(TBC_CONV_RATE).getValue()),
														CommonUtil.roundNumber(debitAmount));
//								System.out.println("Balance Amount 2 "+balanceAmount);
								resultList.add(transBean);
							}
						}
						else{
							transBean=new ExpenseTransactionBean((Long)item.getItemProperty(TBC_ID).getValue(), 
														(long)0, 
														(Long)item.getItemProperty(TBC_CID).getValue(),
														CommonUtil.roundNumber((Double)item.getItemProperty(TBC_CONV_RATE).getValue()),
														CommonUtil.roundNumber(debitAmount));
//							System.out.println("Balance Amount 3 "+balanceAmount);
							resultList.add(transBean);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reloadConvertionRate() {
		try {
			Iterator itr = table.getItemIds().iterator();
			while (itr.hasNext()) {
				Item item = table.getItem(itr.next());
				amountField.setValue((Double)item.getItemProperty(TBC_AMOUNT).getValue());
				item.getItemProperty(TBC_CURRENCY).setValue(new CurrencyManagementDao().getselecteditem(amountField.getCurrency()).getSymbol());
				item.getItemProperty(TBC_CID).setValue(amountField.getCurrency());
				item.getItemProperty(TBC_CONV_RATE).setValue(CommonUtil.roundNumber(amountField.getConversionRate()));
				item.getItemProperty(TBC_BASE_CURRENCY_VALUE).setValue(CommonUtil.roundNumber(amountField.getValue()/amountField.getConversionRate()));
			}
			amountField.setValue(0);
			amountField.rateButton.setVisible(false);
			calculateTotal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("rawtypes")
	public void loadTable(List list) {
		try {
			table.setVisibleColumns(allHeaders);
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				ExpenseBean bean = (ExpenseBean) itr.next();
				table.addItem(new Object[]{
						bean.getLedger(),
						bean.isClearingAgent(),
						bean.getTransactionType(),
						transactionTypeSelect.getItemCaption(bean.getTransactionType()),
						new LedgerDao().getLedgeer(bean.getLedger()).getName(),
						bean.getCurrencyId(),
						new CurrencyManagementDao().getselecteditem(bean.getCurrencyId()).getSymbol(),
						CommonUtil.roundNumber(bean.getConversionRate()),
						CommonUtil.roundNumber(bean.getAmount()/bean.getConversionRate()),
						CommonUtil.roundNumber(bean.getAmount())},table.getItemIds().size()+1);
			}
			calculateTotal();
			amountField.rateButton.setVisible(false);
			table.setVisibleColumns(requiredHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void clear() {
		table.removeAllItems();
		calculateTotal();
	}
	
}
