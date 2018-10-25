package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.DebitCreditInvoiceMapModel;
import com.inventory.config.acct.model.DebitNoteModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.bean.PaymentInvoiceBean;
import com.inventory.sales.model.SalesModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.CurrencyModel;

/**
 * @author Jinshad P.T.
 *
 * Jul 11, 2013
 */

/**
 * 
 * @author sangeeth
 *
 */
@SuppressWarnings("serial")
public class DebitNoteDao extends SHibernate implements Serializable {
	
	
	@SuppressWarnings("rawtypes")
	public long save(DebitNoteModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
		try {
			begin();
			getSession().save(transaction);
			
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tdm;
			while (aciter.hasNext()) {
				
				tdm = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
				
				flush();
			}
			
			mdl.setTransactionId(transaction.getTransaction_id());
			getSession().save(mdl);
			flush();
			
			Iterator itr=invoiceMapList.iterator();
			while (itr.hasNext()) {
				PaymentInvoiceBean det = (PaymentInvoiceBean) itr.next();
				if(det.getInvoiceId()!=0){
				double amount=0;
				if(det.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
					PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, det.getInvoiceId());
					amount=pmdl.getDebit_note()+det.getAmount();
					pmdl.setDebit_note(CommonUtil.roundNumber(amount));
					
					if(((pmdl.getAmount() - pmdl.getExpenseAmount()) + (pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) + amount 
							- pmdl.getPaymentAmount() - pmdl.getCredit_note() - pmdl.getPaid_by_payment())>0) {

							pmdl.setPayment_done('N');
							pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							pmdl.setPayment_done('Y');
							pmdl.setPayment_status(SConstants.FULLY_PAID);
							
						}
					getSession().update(pmdl);
					flush();
				}
				
				else if(det.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
					
					SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, det.getInvoiceId());
					amount=smdl.getDebit_note()+det.getAmount();
					smdl.setDebit_note(CommonUtil.roundNumber(amount));
					if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
							- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {

							smdl.setPayment_done('N');
							smdl.setStatus(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							smdl.setPayment_done('Y');
							smdl.setStatus(SConstants.FULLY_PAID);
							
						}
					getSession().update(smdl);
					flush();
				}
				
				DebitCreditInvoiceMapModel mapMdl=new DebitCreditInvoiceMapModel();
				mapMdl.setType(det.getType());
				mapMdl.setSupplier_customer(det.getSupplier_customer());
				mapMdl.setInvoiceId(det.getInvoiceId());
				mapMdl.setPaymentId(mdl.getId());
				mapMdl.setAmount(CommonUtil.roundNumber(det.getAmount()));
				mapMdl.setOffice_id(det.getOffice());
				mapMdl.setCurrencyId(new CurrencyModel(det.getCurrencyId()));
				mapMdl.setConversionRate(CommonUtil.roundNumber(det.getConversionRate()));
				getSession().save(mapMdl);
			}
			}
			flush();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return mdl.getId();
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getDebitNoteModelList(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.DebitNoteModel(id,cast(id as string))" +
					" from DebitNoteModel where office_id=:office order by id DESC")
						.setLong("office", office).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	public DebitNoteModel getDebitNoteModel(long id) throws Exception {
		DebitNoteModel mdl=null;
		try {
			begin();
			mdl=(DebitNoteModel) getSession().get(DebitNoteModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return mdl;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(DebitNoteModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
		try {
			begin();
			
			List oldChildList=new ArrayList();
			List transList=new ArrayList();
			List oldTransactionList=new ArrayList();
			List oldMapList=new ArrayList();
			
			oldChildList=getSession().createQuery("select b.id from DebitNoteModel a join a.debit_note_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			transList = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransactionId()).list();
			
			oldMapList=getSession().createQuery("from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
			Iterator mapItr=oldMapList.iterator();
			while (mapItr.hasNext()) {
				DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) mapItr.next();
				if(map!=null&&map.getInvoiceId()!=0){
				double amount=0;
				if(map.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
					PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
					if(pmdl!=null){
					amount=pmdl.getDebit_note() - map.getAmount();
					pmdl.setDebit_note(CommonUtil.roundNumber(amount));
					if(((pmdl.getAmount() - pmdl.getExpenseAmount()) + (pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) + amount 
							- pmdl.getPaymentAmount() - pmdl.getCredit_note() - pmdl.getPaid_by_payment())>0) {

							pmdl.setPayment_done('N');
							pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							pmdl.setPayment_done('Y');
							pmdl.setPayment_status(SConstants.FULLY_PAID);
							
						}
					getSession().update(pmdl);
					flush();
					}
				}
				
				else if(map.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
					
					SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
					if(smdl!=null){
					amount=smdl.getDebit_note()-map.getAmount();
					smdl.setDebit_note(CommonUtil.roundNumber(amount));
					if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
							- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {

							smdl.setPayment_done('N');
							smdl.setStatus(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							smdl.setPayment_done('Y');
							smdl.setStatus(SConstants.FULLY_PAID);
							
						}
					getSession().update(smdl);
					flush();
					}
				}
				}
			}
			
			getSession().createQuery("delete from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("type", SConstants.creditDebitNote.DEBIT).executeUpdate();
			flush();
			
			
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			while (aciter.hasNext()) {
				
				TransactionDetailsModel tdm = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
				
				oldTransactionList.add(tdm.getId());

			}
			
			getSession().update(transaction);
			
			Iterator<TransactionDetailsModel> titer = transaction.getTransaction_details_list().iterator();
			while (titer.hasNext()) {
				
				TransactionDetailsModel tdm = titer.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
				
				flush();
			}
			
			getSession().update(mdl);
			flush();
			
			Iterator itr=invoiceMapList.iterator();
			while (itr.hasNext()) {
				PaymentInvoiceBean det = (PaymentInvoiceBean) itr.next();
				if(det.getInvoiceId()!=0){
				double amount=0;
				if(det.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
					PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, det.getInvoiceId());
					if(pmdl!=null){
					amount=pmdl.getDebit_note()+det.getAmount();
					pmdl.setDebit_note(CommonUtil.roundNumber(amount));
					
					if(((pmdl.getAmount() - pmdl.getExpenseAmount()) + (pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) + amount 
							- pmdl.getPaymentAmount() - pmdl.getCredit_note() - pmdl.getPaid_by_payment())>0) {

							pmdl.setPayment_done('N');
							pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							pmdl.setPayment_done('Y');
							pmdl.setPayment_status(SConstants.FULLY_PAID);
							
						}
					getSession().update(pmdl);
					flush();
					}
				}
				
				else if(det.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
					
					SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, det.getInvoiceId());
					if(smdl!=null){
					amount=smdl.getDebit_note()+det.getAmount();
					smdl.setDebit_note(CommonUtil.roundNumber(amount));
					if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
							- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {

							smdl.setPayment_done('N');
							smdl.setStatus(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							smdl.setPayment_done('Y');
							smdl.setStatus(SConstants.FULLY_PAID);
							
						}
					getSession().update(smdl);
					flush();
					}
				}
				
				DebitCreditInvoiceMapModel mapMdl=new DebitCreditInvoiceMapModel();
				mapMdl.setType(det.getType());
				mapMdl.setSupplier_customer(det.getSupplier_customer());
				mapMdl.setInvoiceId(det.getInvoiceId());
				mapMdl.setPaymentId(mdl.getId());
				mapMdl.setAmount(CommonUtil.roundNumber(det.getAmount()));
				mapMdl.setOffice_id(det.getOffice());
				mapMdl.setCurrencyId(new CurrencyModel(det.getCurrencyId()));
				mapMdl.setConversionRate(CommonUtil.roundNumber(det.getConversionRate()));
				getSession().save(mapMdl);
			}
			}
			flush();
			if(oldChildList.size()>0){
				getSession().createQuery("delete from DebitNoteDetailsModel where id in (:list)")
							.setParameterList("list", oldChildList).executeUpdate();
			}
			
			if(oldTransactionList.size()>0){
				getSession().createQuery("delete from TransactionDetailsModel where id in (:list)")
							.setParameterList("list", (Collection) oldTransactionList).executeUpdate();	
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void delete(long id) throws Exception {
		try {
			begin();
			DebitNoteModel mdl=(DebitNoteModel) getSession().get(DebitNoteModel.class, id);
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			oldMapList=getSession().createQuery("from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
			Iterator mapItr=oldMapList.iterator();
			while (mapItr.hasNext()) {
				DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) mapItr.next();
				if(map!=null&&map.getInvoiceId()!=0){
				double amount=0;
				if(map.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
					PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
					if(pmdl!=null){
					amount=pmdl.getDebit_note()-map.getAmount();
					pmdl.setDebit_note(CommonUtil.roundNumber(amount));
					
					if(((pmdl.getAmount() - pmdl.getExpenseAmount()) + (pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) + amount 
							- pmdl.getPaymentAmount() - pmdl.getCredit_note() - pmdl.getPaid_by_payment())>0) {

							pmdl.setPayment_done('N');
							pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							pmdl.setPayment_done('Y');
							pmdl.setPayment_status(SConstants.FULLY_PAID);
							
						}
					getSession().update(pmdl);
					flush();
					}
				}
				
				else if(map.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
					
					SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
					if(smdl!=null){
					amount=smdl.getDebit_note()-map.getAmount();
					smdl.setDebit_note(CommonUtil.roundNumber(amount));
					if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
							- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {

							smdl.setPayment_done('N');
							smdl.setStatus(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							smdl.setPayment_done('Y');
							smdl.setStatus(SConstants.FULLY_PAID);
							
						}
					getSession().update(smdl);
					flush();
					}
				}
				}
			}
			getSession().createQuery("delete from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
						.setParameter("type", SConstants.creditDebitNote.DEBIT).executeUpdate();
			flush();


			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
			
				TransactionDetailsModel tdm = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
				
				flush();
			
			}
			getSession().delete(transaction);
			flush();
			getSession().delete(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void cancel(long id) throws Exception {
		try {
			begin();
			DebitNoteModel mdl=(DebitNoteModel) getSession().get(DebitNoteModel.class, id);
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			
			oldMapList=getSession().createQuery("from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
					.setParameter("id", mdl.getId())
					.setParameter("office", mdl.getOffice_id())
					.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
			Iterator mapItr=oldMapList.iterator();
			while (mapItr.hasNext()) {
				DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) mapItr.next();
				if(map!=null&&map.getInvoiceId()!=0){
				double amount=0;
				if(map.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
					PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
					if(pmdl!=null){
					amount=pmdl.getDebit_note()-map.getAmount();
					pmdl.setDebit_note(CommonUtil.roundNumber(amount));
					
					if(((pmdl.getAmount() - pmdl.getExpenseAmount()) + (pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) + amount 
							- pmdl.getPaymentAmount() - pmdl.getCredit_note() - pmdl.getPaid_by_payment())>0) {

							pmdl.setPayment_done('N');
							pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							pmdl.setPayment_done('Y');
							pmdl.setPayment_status(SConstants.FULLY_PAID);
							
						}
					getSession().update(pmdl);
					flush();
					}
				}
				
				else if(map.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
					
					SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
					if(smdl!=null){
					amount=smdl.getDebit_note()-map.getAmount();
					smdl.setDebit_note(CommonUtil.roundNumber(amount));
					if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
							- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {

							smdl.setPayment_done('N');
							smdl.setStatus(SConstants.PARTIALLY_PAID);
							
						}
						else {
							
							smdl.setPayment_done('Y');
							smdl.setStatus(SConstants.FULLY_PAID);
							
						}
					getSession().update(smdl);
					flush();
				}
				}
				}
			}
			getSession().createQuery("delete from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
						.setParameter("type", SConstants.creditDebitNote.DEBIT).executeUpdate();
			flush();


			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
			
				TransactionDetailsModel tdm = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
						.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
				
				flush();
			
			}
			getSession().delete(transaction);
			flush();
			getSession().createQuery("update DebitNoteModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}


	@SuppressWarnings("rawtypes")
	public List getAllPaymentList(long office, int type, long invoice) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and invoiceId=:invoice")
						.setParameter("office", office).setParameter("type", type).setParameter("invoice", invoice).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}

	public String getBillNo(int supplier_customer, long id) throws Exception {
		
		String no="";
		try {
			begin();
			if(supplier_customer==1)
				no=(String) getSession().createQuery("select purchase_no from PurchaseModel where id=:id")
						.setParameter("id", id).uniqueResult();
			else
				no=(String) getSession().createQuery("select sales_number from SalesModel where id=:id")
					.setParameter("id", id).uniqueResult();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return no;
	}
	
	/*
	@SuppressWarnings("rawtypes")
	public List getAllCreditDebitList(long office, int type, long invoice) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and invoiceId=:invoice")
						.setParameter("office", office).setParameter("type", type).setParameter("invoice", invoice).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}*/
	
}
