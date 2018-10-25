package com.webspark.Components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.PaymentInvoiceDao;

/**
 * @author anil
 *
 */
/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
public class PaymentInvoicePanel extends SWindow{
	
	public SComboField ledgerCombo;
	public SDateField fromDateField;
	public SDateField toDateField;
	SListSelect invoiceSelect;
	public SCurrencyField netAmountField;
	PaymentInvoiceDao dao;
	SButton doneButton;
	
	WrappedSession session;
	SettingsValuePojo settings;
	
	boolean isCreateNew=true;
	
	int transactionType=0;
	
	public PaymentInvoicePanel(String caption) {
		try {
			setCaption("Bills");
			dao=new PaymentInvoiceDao();
			SFormLayout mainLayout=new  SFormLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			
			session = new SessionUtil().getHttpSession();
			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
			ledgerCombo=new SComboField("Ledger", 150,null,"id", "name", true, "Select");
			SHorizontalLayout dateLayout=new SHorizontalLayout();
			dateLayout.setSpacing(true);
			fromDateField = new SDateField("From Date", 100, session.getAttribute("date_format").toString(), (Date) session.getAttribute("working_date"));
			toDateField = new SDateField("To Date", 100, session.getAttribute("date_format").toString(), (Date) session.getAttribute("working_date"));
			dateLayout.addComponent(fromDateField);
			dateLayout.addComponent(toDateField);
			invoiceSelect = new SListSelect("Bill No", 200,null, "id", "comments");
			invoiceSelect.setImmediate(true);
			invoiceSelect.setMultiSelect(true);
			invoiceSelect.setNullSelectionAllowed(true);
			invoiceSelect.setHeight("100");
			netAmountField=new SCurrencyField("Total Bill Amount", 200, (Date) session.getAttribute("working_date"));
			netAmountField.currencySelect.setReadOnly(true);
			netAmountField.amountField.setReadOnly(true);
			netAmountField.rateButton.setVisible(false);
			netAmountField.setNewValue((Long) session.getAttribute("currency_id"), 0.0);
			netAmountField.setStyleName("textfield_align_right");
			
			doneButton=new SButton("Done");
			
			mainLayout.addComponent(ledgerCombo);
			mainLayout.addComponent(dateLayout);
			mainLayout.addComponent(invoiceSelect);
			mainLayout.addComponent(netAmountField);
			SGridLayout btnLayout=new SGridLayout(6,1);
			btnLayout.setSizeFull();
			btnLayout.addComponent(doneButton,3,0);
			mainLayout.addComponent(btnLayout);
			
			addShortcutListener(new ShortcutListener("Close", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					close();
				}
			});
			
			invoiceSelect.addValueChangeListener(new ValueChangeListener() {

				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent arg0) {
					try {
						if(invoiceSelect.getValue()!=null){
							Set set = (Set) invoiceSelect.getValue();
							if (set != null && set.size() > 0) {
								double amount = 0;
								Iterator itr = set.iterator();
								long cid=0;
								List<Object> removeList=new ArrayList<Object>();
								if(transactionType == SConstants.PURCHASE){
									while (itr.hasNext()) {
										Object obj=(Object)itr.next();
										PurchaseModel mdl = new PurchaseDao().getPurchaseModel((Long) obj);
										if(cid==0)
											cid=mdl.getNetCurrencyId().getId();
										if(cid!=mdl.getNetCurrencyId().getId()){
											removeList.add(obj);
											continue;
										}
										amount += (mdl.getAmount()- mdl.getExpenseAmount()) + 
												(mdl.getExpenseAmount()- mdl.getExpenseCreditAmount()) + mdl.getDebit_note() - mdl.getCredit_note() -
												mdl.getPaymentAmount()- mdl.getPaid_by_payment();
										
									}
								}
								else if(transactionType == SConstants.SALES){
									while (itr.hasNext()) {
										Object obj=(Object)itr.next();
										SalesModel mdl = new SalesDao().getSale((Long) obj);
										if(cid==0)
											cid=mdl.getNetCurrencyId().getId();
										if(cid!=mdl.getNetCurrencyId().getId()){
											removeList.add(obj);
											continue;
										}
										amount += (mdl.getAmount()- mdl.getExpenseAmount()) + 
												(mdl.getExpenseAmount()- mdl.getExpenseCreditAmount()) + mdl.getDebit_note() - mdl.getCredit_note() -
												mdl.getPayment_amount()- mdl.getPaid_by_payment();
										
									}
								}
								itr=removeList.iterator();
								while (itr.hasNext()) {
									try {
										invoiceSelect.removeItem(itr.next());
									} catch (Exception e) {
										
									}
								}
								netAmountField.setNewValue(cid, CommonUtil.roundNumber(amount));
								netAmountField.rateButton.setVisible(false);
							}
							else 
								netAmountField.setNewValue((Long) session.getAttribute("currency_id"), 0.0);
							netAmountField.rateButton.setVisible(false);
						}
						else 
							netAmountField.setNewValue((Long) session.getAttribute("currency_id"), 0.0);
						netAmountField.rateButton.setVisible(false);
					} catch (Exception e) {
						e.printStackTrace();
						netAmountField.rateButton.setVisible(false);
					}
				}

			});
			
			
			fromDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						loadBillNos(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			toDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						loadBillNos(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			doneButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					close();
				}
			});
			
			setContent(mainLayout);
			center();
			setModal(true);
			setCaption(caption);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void loadLedgers(long ledgerId, int type, Date startDate, Date endDate, HashSet<Long> hashSet, boolean isNew){
		try {
			ledgerCombo.setReadOnly(false);
			List ledgerList=new ArrayList();
			transactionType=type;
			if(type==SConstants.SALES){
				ledgerList=new LedgerDao().getAllCustomers();
			}
			else if(type==SConstants.PURCHASE){
				ledgerList=new LedgerDao().getAllSuppliers();
			}
			SCollectionContainer bic=SCollectionContainer.setList(ledgerList, "id");
			ledgerCombo.setContainerDataSource(bic);
			ledgerCombo.setItemCaptionPropertyId("name");
			ledgerCombo.setValue(ledgerId);
			ledgerCombo.setReadOnly(true);
			fromDateField.setValue(startDate);
			toDateField.setValue(endDate);
			isCreateNew=isNew;
			loadBillNos(hashSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void setFilterDates(Date startDate, Date endDate){
		fromDateField.setValue(startDate);
		toDateField.setValue(endDate);
	}
	
	
	@SuppressWarnings({ "rawtypes"})
	public void loadBillNos(HashSet<Long> hashSet){
		try {
			List billList=new ArrayList();
			if(ledgerCombo.getValue()!=null){
				if(transactionType == SConstants.PURCHASE){
					billList=dao.getAllPurchaseForSupplier((Long)ledgerCombo.getValue(),
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
															isCreateNew);
					CollectionContainer bic = CollectionContainer.fromBeans(billList,"id");
					invoiceSelect.setContainerDataSource(bic);
					invoiceSelect.setItemCaptionPropertyId("purchase_no");
					
					if(hashSet!=null){
						Iterator iter = hashSet.iterator();
						double amount=0;
						while (iter.hasNext()) {
							PurchaseModel mdl = new PurchaseDao().getPurchaseModel((Long) iter.next());
							amount += (mdl.getAmount()- mdl.getExpenseAmount()) + 
										(mdl.getExpenseAmount()- mdl.getExpenseCreditAmount()) + mdl.getDebit_note() - mdl.getCredit_note() -
										mdl.getPaymentAmount()- mdl.getPaid_by_payment();
						}
						invoiceSelect.setValue(hashSet);
						netAmountField.setNewValue(CommonUtil.roundNumber(amount));
						netAmountField.rateButton.setVisible(false);
					}
					else{
						invoiceSelect.setValue(null);
						netAmountField.setNewValue(0.0);
						netAmountField.rateButton.setVisible(false);
					}
				}
				else if(transactionType == SConstants.SALES){
					billList=dao.getAllSalesForCustomer((Long)ledgerCombo.getValue(),
														CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
														CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
														isCreateNew);
					CollectionContainer bic = CollectionContainer.fromBeans(billList,"id");
					invoiceSelect.setContainerDataSource(bic);
					invoiceSelect.setItemCaptionPropertyId("sales_number");
					
					if(hashSet!=null){
						Iterator iter = hashSet.iterator();
						double amount=0;
						while (iter.hasNext()) {
							SalesModel mdl = new SalesDao().getSale((Long) iter.next());
							amount += (mdl.getAmount()- mdl.getExpenseAmount()) + 
									(mdl.getExpenseAmount()- mdl.getExpenseCreditAmount()) + mdl.getDebit_note() - mdl.getCredit_note() -
									mdl.getPayment_amount()- mdl.getPaid_by_payment();
						}
						invoiceSelect.setValue(hashSet);
						netAmountField.setNewValue(CommonUtil.roundNumber(amount));
						netAmountField.rateButton.setVisible(false);
					}
					else{
						invoiceSelect.setValue(null);
						netAmountField.setNewValue(0.0);
						netAmountField.rateButton.setVisible(false);
					}
				}
				netAmountField.rateButton.setVisible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public String getBillNos(){
		String billNo="";
		try {
			if(invoiceSelect.getValue()!=null){
				Set set = (Set) invoiceSelect.getValue();
				if (set != null && set.size() > 0) {
					Iterator itr = set.iterator();
					while (itr.hasNext()) {
						billNo += (Long) itr.next()+", ";
					}
				}
			}
			else
				billNo="";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billNo;
	}
	
	
	public Date getFromDate(){
		return fromDateField.getValue();
	}
	
	
	
	
	public Date getToDate(){
		return toDateField.getValue();
	}

	
	public void resetAll(){
		ledgerCombo.setReadOnly(false);
		ledgerCombo.setValue(null);
		fromDateField.setValue((Date) session.getAttribute("working_date"));
		toDateField.setValue((Date) session.getAttribute("working_date"));
		invoiceSelect.removeAllItems();
		netAmountField.setNewValue((Long) session.getAttribute("currency_id"), 0.0);
	}
	
}
