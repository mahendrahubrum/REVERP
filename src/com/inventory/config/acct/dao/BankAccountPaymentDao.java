package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.BankAccountPaymentDetailsModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.BankDetailsInvoiceMapModel;
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
public class BankAccountPaymentDao extends SHibernate implements Serializable {
	
	
	@SuppressWarnings("rawtypes")
	public long save(BankAccountPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
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
			
			Iterator itr=mdl.getBank_account_payment_list().iterator();
			while (itr.hasNext()) {
				BankAccountPaymentDetailsModel payDet = (BankAccountPaymentDetailsModel) itr.next();
				List billList=new ArrayList();
				billList=Arrays.asList(payDet.getBill_no().split(","));
				if(billList.size()>0){
					Iterator it=billList.iterator();
					while (it.hasNext()) {
						String id=it.next().toString().trim();
						if(id.length()>0){
							long pid = Long.parseLong(id);
							BankDetailsInvoiceMapModel map=new BankDetailsInvoiceMapModel(SConstants.PURCHASE, 
																							mdl.getOffice_id(),
																							mdl.getId(),
																							payDet.getId(),
																							pid,
																							SConstants.BANK_ACCOUNT_PAYMENTS);
							getSession().save(map);
							flush();
						}
					}
				}
			}
			
			itr=invoiceMapList.iterator();
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
				mapMdl.setCheque(det.isCheque());
				mapMdl.setPayment_type(SConstants.BANK_ACCOUNT_PAYMENTS);
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
	public List getBankAccountPaymentModelList(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.BankAccountPaymentModel(id,bill_no)" +
					" from BankAccountPaymentModel where office_id=:office order by id DESC")
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
	
	
	public BankAccountPaymentModel getBankAccountPaymentModel(long id) throws Exception {
		BankAccountPaymentModel mdl=null;
		try {
			begin();
			mdl=(BankAccountPaymentModel) getSession().get(BankAccountPaymentModel.class, id);
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
	
	
	public BankAccountPaymentDetailsModel getBankAccountPaymentDetailsModel(long id) throws Exception {
		BankAccountPaymentDetailsModel mdl=null;
		try {
			begin();
			mdl=(BankAccountPaymentDetailsModel) getSession().get(BankAccountPaymentDetailsModel.class, id);
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
	
	
	public void update(BankAccountPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
		try {
			begin();
			updateValue(mdl, transaction, invoiceMapList);
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
	
	
	public void updateReturn(BankAccountPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
		try {
			updateValue(mdl, transaction, invoiceMapList);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void delete(long id) throws Exception {
		try {
			begin();
			BankAccountPaymentModel mdl=(BankAccountPaymentModel) getSession().get(BankAccountPaymentModel.class, id);
			List oldChildList=new ArrayList();
			oldChildList=getSession().createQuery("select b from BankAccountPaymentModel a join a.bank_account_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
		
			Iterator detItr=oldChildList.iterator();
			while (detItr.hasNext()) {
				BankAccountPaymentDetailsModel payDet = (BankAccountPaymentDetailsModel) detItr.next();
				
				getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and type=:type and office_id=:office_id")
							.setParameter("paymentDetailsId", payDet.getId())
							.setParameter("paymentId", mdl.getId())
							.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
							.setParameter("office_id", mdl.getOffice_id())
							.setParameter("type", SConstants.PURCHASE).executeUpdate();
				
				flush();
			}
			
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and type=:type and payment_type=:payment_type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
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
						.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
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
			BankAccountPaymentModel mdl=(BankAccountPaymentModel) getSession().get(BankAccountPaymentModel.class, id);
			
			List oldChildList=new ArrayList();
			oldChildList=getSession().createQuery("select b from BankAccountPaymentModel a join a.bank_account_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
		
			Iterator detItr=oldChildList.iterator();
			while (detItr.hasNext()) {
				BankAccountPaymentDetailsModel payDet = (BankAccountPaymentDetailsModel) detItr.next();
				
				getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and type=:type and office_id=:office_id")
							.setParameter("paymentDetailsId", payDet.getId())
							.setParameter("paymentId", mdl.getId())
							.setParameter("office_id", mdl.getOffice_id())
							.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
							.setParameter("type", SConstants.PURCHASE).executeUpdate();
				
				flush();
			}
			
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and type=:type and payment_type=:payment_type and office_id=:office")
					.setParameter("id", mdl.getId())
					.setParameter("office", mdl.getOffice_id())
					.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
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
						.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
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
			getSession().createQuery("update BankAccountPaymentModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateValue(BankAccountPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception{
		try {
			List oldChildList=new ArrayList();
			List oldChildIdList=new ArrayList();
			List transList=new ArrayList();
			List oldTransactionList=new ArrayList();
			List oldMapList=new ArrayList();
			
			oldChildList=getSession().createQuery("select b from BankAccountPaymentModel a join a.bank_account_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			transList = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransactionId()).list();
			
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
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
									.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
									.setParameter("type", SConstants.PURCHASE).executeUpdate();
			flush();
			
			Iterator detItr=oldChildList.iterator();
			while (detItr.hasNext()) {
				BankAccountPaymentDetailsModel payDet = (BankAccountPaymentDetailsModel) detItr.next();
				
				getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and type=:type and office_id=:office_id")
							.setParameter("paymentDetailsId", payDet.getId())
							.setParameter("paymentId", mdl.getId())
							.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
							.setParameter("office_id", mdl.getOffice_id())
							.setParameter("type", SConstants.PURCHASE).executeUpdate();
				
				flush();
				oldChildIdList.add(payDet.getId());
			}
			
			
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
			
			Iterator itr=mdl.getBank_account_payment_list().iterator();
			while (itr.hasNext()) {
				BankAccountPaymentDetailsModel payDet = (BankAccountPaymentDetailsModel) itr.next();
				if(payDet.getId()!=0){
					if(oldChildIdList.contains(payDet.getId())){
						oldChildIdList.remove(payDet.getId());
					}
				}
			}
			getSession().clear();
			getSession().update(mdl);
			flush();
			itr=mdl.getBank_account_payment_list().iterator();
			while (itr.hasNext()) {
				BankAccountPaymentDetailsModel payDet = (BankAccountPaymentDetailsModel) itr.next();
//				System.out.println("New Child Id "+payDet.getId());
				List billList=new ArrayList();
				billList=Arrays.asList(payDet.getBill_no().split(","));
				if(billList.size()>0){
					Iterator it=billList.iterator();
					while (it.hasNext()) {
						String id=it.next().toString().trim();
						if(id.length()>0){
							long pid = Long.parseLong(id);
							BankDetailsInvoiceMapModel map=new BankDetailsInvoiceMapModel(SConstants.PURCHASE, 
																							mdl.getOffice_id(),
																							mdl.getId(),
																							payDet.getId(),
																							pid,
																							SConstants.BANK_ACCOUNT_PAYMENTS);
							getSession().save(map);
							flush();
						}
					}
				}
			}
			
			itr=invoiceMapList.iterator();
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
				mapMdl.setCheque(det.isCheque());
				mapMdl.setPayment_type(SConstants.BANK_ACCOUNT_PAYMENTS);
				getSession().save(mapMdl);
			}
			flush();
			if(oldChildIdList.size()>0){
				getSession().createQuery("delete from BankAccountPaymentDetailsModel where id in (:list)")
							.setParameterList("list", oldChildIdList).executeUpdate();
				flush();
			}
			
			if(oldTransactionList.size()>0){
				getSession().createQuery("delete from TransactionDetailsModel where id in (:list)")
							.setParameterList("list", (Collection) oldTransactionList).executeUpdate();
				flush();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
