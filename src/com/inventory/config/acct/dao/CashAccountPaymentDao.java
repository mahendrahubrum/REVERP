package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.CashAccountPaymentModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.bean.PaymentInvoiceBean;
import com.inventory.sales.model.PaymentInvoiceMapModel;
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
public class CashAccountPaymentDao extends SHibernate implements Serializable {
	
	
	@SuppressWarnings("rawtypes")
	public long save(CashAccountPaymentModel mdl, TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
		try {
			begin();
			getSession().save(transaction);
			flush();
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
				
				PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, det.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()+det.getAmount();
				pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPaymentAmount() - amount)>0) {
					
					pmdl.setPayment_done('N');
					pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
				}
				else {
					pmdl.setPayment_done('Y');
					pmdl.setPayment_status(SConstants.FULLY_PAID);
				}
				getSession().update(pmdl);
				flush();
				
				PaymentInvoiceMapModel mapMdl=new PaymentInvoiceMapModel();
				mapMdl.setType(det.getType());
				mapMdl.setInvoiceId(det.getInvoiceId());
				mapMdl.setPaymentId(mdl.getId());
				mapMdl.setAmount(CommonUtil.roundNumber(det.getAmount()));
				mapMdl.setOffice_id(det.getOffice());
				mapMdl.setCurrencyId(new CurrencyModel(det.getCurrencyId()));
				mapMdl.setConversionRate(CommonUtil.roundNumber(det.getConversionRate()));
				mapMdl.setPayment_type(SConstants.CASH_ACCOUNT_PAYMENTS);
				getSession().save(mapMdl);
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
	public List getCashAccountPaymentModelList(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.CashAccountPaymentModel(id,bill_no)" +
					" from CashAccountPaymentModel where office_id=:office order by id DESC")
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
	
	
	public CashAccountPaymentModel getCashAccountPaymentModel(long id) throws Exception {
		CashAccountPaymentModel mdl=null;
		try {
			begin();
			mdl=(CashAccountPaymentModel) getSession().get(CashAccountPaymentModel.class, id);
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
	public void update(CashAccountPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
		try {
			begin();
			
			List oldChildList=new ArrayList();
			List transList=new ArrayList();
			List oldTransactionList=new ArrayList();
			List oldMapList=new ArrayList();
			
			oldChildList=getSession().createQuery("select b.id from CashAccountPaymentModel a join a.cash_account_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			transList = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransactionId()).list();
			
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
									.setParameter("office", mdl.getOffice_id())
									.setParameter("type", SConstants.PURCHASE).list();
			
			
			Iterator mapItr=oldMapList.iterator();
			while (mapItr.hasNext()) {
				PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
				
				PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()-map.getAmount();
				pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPaymentAmount() - amount)>0) {
					
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
			
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
						.setParameter("office", mdl.getOffice_id())
						.setParameter("type", SConstants.PURCHASE).executeUpdate();
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
				
				PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, det.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()+det.getAmount();
				pmdl.setAmount(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPaymentAmount() - amount)>0) {
					
					pmdl.setPayment_done('N');
					pmdl.setPayment_status(SConstants.PARTIALLY_PAID);
				}
				else {
					pmdl.setPayment_done('Y');
					pmdl.setPayment_status(SConstants.FULLY_PAID);
				}
				getSession().update(pmdl);
				flush();
				
				PaymentInvoiceMapModel mapMdl=new PaymentInvoiceMapModel();
				mapMdl.setType(det.getType());
				mapMdl.setInvoiceId(det.getInvoiceId());
				mapMdl.setPaymentId(mdl.getId());
				mapMdl.setAmount(CommonUtil.roundNumber(det.getAmount()));
				mapMdl.setOffice_id(det.getOffice());
				mapMdl.setCurrencyId(new CurrencyModel(det.getCurrencyId()));
				mapMdl.setConversionRate(CommonUtil.roundNumber(det.getConversionRate()));
				mapMdl.setPayment_type(SConstants.CASH_ACCOUNT_PAYMENTS);
				getSession().save(mapMdl);
			}
			flush();
			
			if(oldChildList.size()>0){
				getSession().createQuery("delete from CashAccountPaymentDetailsModel where id in (:list)")
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
			CashAccountPaymentModel mdl=(CashAccountPaymentModel) getSession().get(CashAccountPaymentModel.class, id);
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
									.setParameter("type", SConstants.PURCHASE).list();


			Iterator mapItr=oldMapList.iterator();
			while (mapItr.hasNext()) {
				PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
				
				PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()-map.getAmount();
				pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPaymentAmount() - amount)>0) {
					
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
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
						.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
						.setParameter("type", SConstants.PURCHASE).executeUpdate();
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
			CashAccountPaymentModel mdl=(CashAccountPaymentModel) getSession().get(CashAccountPaymentModel.class, id);
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
									.setParameter("type", SConstants.PURCHASE).list();


			Iterator mapItr=oldMapList.iterator();
			while (mapItr.hasNext()) {
				PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
				
				PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()-map.getAmount();
				pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPaymentAmount() - amount)>0) {
					
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
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
						.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
						.setParameter("type", SConstants.PURCHASE).executeUpdate();
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
			getSession().createQuery("update CashAccountPaymentModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
			resultList=getSession().createQuery("from PaymentInvoiceMapModel where office_id=:office and type=:type and invoiceId=:invoice")
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

	@SuppressWarnings("rawtypes")
	public List getAllCreditDebitList(long office, int type, long invoice, int cusSup) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and supplier_customer=:cusSup and invoiceId=:invoice")
						.setParameter("office", office).setParameter("type", type).setParameter("cusSup", cusSup).setParameter("invoice", invoice).list();
			
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
	
}
