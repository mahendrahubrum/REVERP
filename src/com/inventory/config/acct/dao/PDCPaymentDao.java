package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.BankDetailsInvoiceMapModel;
import com.inventory.config.acct.model.PdcPaymentDetailsModel;
import com.inventory.config.acct.model.PdcPaymentModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.bean.PaymentInvoiceBean;
import com.inventory.sales.model.PaymentInvoiceMapModel;
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
 * @date 19-Oct-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class PDCPaymentDao extends SHibernate implements Serializable {
	
	
	@SuppressWarnings("rawtypes")
	public long save(PdcPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList) throws Exception {
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
			
			Iterator itr=mdl.getPdc_payment_list().iterator();
			while (itr.hasNext()) {
				PdcPaymentDetailsModel payDet = (PdcPaymentDetailsModel) itr.next();
				
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
																							SConstants.PDC_PAYMENT);
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
				mapMdl.setPayment_type(SConstants.PDC_PAYMENT);
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
	public List getPdcPaymentModelList(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.PdcPaymentModel(id,cast(id as string))" +
					" from PdcPaymentModel where office_id=:office order by id DESC")
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
	
	
	public PdcPaymentModel getPdcPaymentModel(long id) throws Exception {
		PdcPaymentModel mdl=null;
		try {
			begin();
			mdl=(PdcPaymentModel) getSession().get(PdcPaymentModel.class, id);
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
	
	
	public TransactionModel getTransactionModel(long id) throws Exception {
		TransactionModel mdl=null;
		try {
			begin();
			mdl=(TransactionModel) getSession().get(TransactionModel.class, id);
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
	
	
	public PdcPaymentDetailsModel getPdcPaymentDetailsModel(long id) throws Exception {
		PdcPaymentDetailsModel mdl=null;
		try {
			begin();
			mdl=(PdcPaymentDetailsModel) getSession().get(PdcPaymentDetailsModel.class, id);
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
	
	
	public long update(PdcPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList, boolean isPayment) throws Exception {
		long id=0;
		try {
			begin();
			id=updateValue(mdl, transaction, invoiceMapList, isPayment);
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
		return id;
	}
	
	
	public void updateReturn(PdcPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList, boolean isPayment) throws Exception {
		try {
			updateValue(mdl, transaction, invoiceMapList, isPayment);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void delete(long id) throws Exception {
		try {
			begin();
			PdcPaymentModel mdl=(PdcPaymentModel) getSession().get(PdcPaymentModel.class, id);
			List oldChildList=new ArrayList();
			List oldPurchaseMapList=new ArrayList();
			List oldSalesMapList=new ArrayList();
			oldChildList=getSession().createQuery("select b from PdcPaymentModel a join a.pdc_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
		
			Iterator detItr=oldChildList.iterator();
			while (detItr.hasNext()) {
				PdcPaymentDetailsModel payDet = (PdcPaymentDetailsModel) detItr.next();
				
				if(payDet.getType()==SConstants.PURCHASE){
					getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and type=:type and office_id=:office_id")
								.setParameter("paymentDetailsId", payDet.getId())
								.setParameter("paymentId", mdl.getId())
								.setParameter("office_id", mdl.getOffice_id())
								.setParameter("payment_type", SConstants.PDC_PAYMENT)
								.setParameter("type", SConstants.PURCHASE).executeUpdate();
				}
				else if(payDet.getType()==SConstants.SALES){
					getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and payment_type=:payment_type and type=:type and office_id=:office_id")
								.setParameter("paymentDetailsId", payDet.getId())
								.setParameter("paymentId", mdl.getId())
								.setParameter("office_id", mdl.getOffice_id())
								.setParameter("payment_type", SConstants.PDC_PAYMENT)
								.setParameter("type", SConstants.SALES).executeUpdate();
				}
				
				if(payDet.getPdc_child_id()!=0){
					getSession().createQuery("update PdcDetailsModel set status=:status, paymentDate=:date where id=:id")
								.setParameter("id", payDet.getPdc_child_id())
								.setParameter("date", CommonUtil.getSQLDateFromUtilDate(mdl.getDate()))
								.setParameter("status", SConstants.PDCStatus.ISSUED).executeUpdate();
					flush();
				}
				
				flush();
			}
			
			oldPurchaseMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
											.setParameter("id", mdl.getId())
											.setParameter("office", mdl.getOffice_id())
											.setParameter("payment_type", SConstants.PDC_PAYMENT)
											.setParameter("type", SConstants.PURCHASE).list();

			oldSalesMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
										.setParameter("id", mdl.getId())
										.setParameter("office", mdl.getOffice_id())
										.setParameter("payment_type", SConstants.PDC_PAYMENT)
										.setParameter("type", SConstants.SALES).list();

			Iterator mapItr=oldPurchaseMapList.iterator();
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
						.setParameter("payment_type", SConstants.PDC_PAYMENT)
						.setParameter("type", SConstants.PURCHASE).executeUpdate();
			flush();

			mapItr=oldSalesMapList.iterator();
			while (mapItr.hasNext()) {
				PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
				
				SalesModel pmdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()-map.getAmount();
				pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPayment_amount() - amount)>0) {
					
					pmdl.setPayment_done('N');
					pmdl.setStatus(SConstants.PARTIALLY_PAID);
				}
				else {
					pmdl.setPayment_done('Y');
					pmdl.setStatus(SConstants.FULLY_PAID);
				}
				getSession().update(pmdl);
				flush();
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
						.setParameter("payment_type", SConstants.PDC_PAYMENT)
						.setParameter("type", SConstants.SALES).executeUpdate();
			flush();


			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			if(transaction!=null){
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
			}
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
			PdcPaymentModel mdl=(PdcPaymentModel) getSession().get(PdcPaymentModel.class, id);
			
			List oldChildList=new ArrayList();
			oldChildList=getSession().createQuery("select b from PdcPaymentModel a join a.bank_account_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
		
			Iterator detItr=oldChildList.iterator();
			while (detItr.hasNext()) {
				PdcPaymentDetailsModel payDet = (PdcPaymentDetailsModel) detItr.next();
				
				getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and paymentId=:paymentId and type=:type and office_id=:office_id")
							.setParameter("paymentDetailsId", payDet.getId())
							.setParameter("paymentId", mdl.getId())
							.setParameter("office_id", mdl.getOffice_id())
							.setParameter("type", SConstants.PURCHASE).executeUpdate();
				
				flush();
			}
			
			TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, mdl.getTransactionId());
			List oldMapList=new ArrayList();
			
			oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
					.setParameter("id", mdl.getId())
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
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
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
			getSession().createQuery("update PdcPaymentModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	public long updateValue(PdcPaymentModel mdl,TransactionModel transaction, List<PaymentInvoiceBean> invoiceMapList, boolean isPayment) throws Exception{
		try {
			List oldChildList=new ArrayList();
			List oldChildIdList=new ArrayList();
			List transList=new ArrayList();
			List oldTransactionList=new ArrayList();
			List oldPurchaseMapList=new ArrayList();
			List oldSalesMapList=new ArrayList();
			
			oldChildList=getSession().createQuery("select b from PdcPaymentModel a join a.pdc_payment_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			transList = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransactionId()).list();
			
			oldPurchaseMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("payment_type", SConstants.PDC_PAYMENT)
									.setParameter("type", SConstants.PURCHASE).list();
			
			oldSalesMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", mdl.getId())
									.setParameter("office", mdl.getOffice_id())
									.setParameter("payment_type", SConstants.PDC_PAYMENT)
									.setParameter("type", SConstants.SALES).list();
			
			Iterator mapItr=oldPurchaseMapList.iterator();
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
									.setParameter("payment_type", SConstants.PDC_PAYMENT)
									.setParameter("type", SConstants.PURCHASE).executeUpdate();
			flush();
			
			mapItr=oldSalesMapList.iterator();
			while (mapItr.hasNext()) {
				PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
				
				SalesModel pmdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
				double amount=pmdl.getPaid_by_payment()-map.getAmount();
				pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
				
				if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
					(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
					 pmdl.getPayment_amount() - amount)>0) {
					
					pmdl.setPayment_done('N');
					pmdl.setStatus(SConstants.PARTIALLY_PAID);
				}
				else {
					pmdl.setPayment_done('Y');
					pmdl.setStatus(SConstants.FULLY_PAID);
				}
				getSession().update(pmdl);
				flush();
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
						.setParameter("id", mdl.getId())
						.setParameter("office", mdl.getOffice_id())
						.setParameter("payment_type", SConstants.PDC_PAYMENT)
						.setParameter("type", SConstants.SALES).executeUpdate();
			flush();
			
			Iterator detItr=oldChildList.iterator();
			while (detItr.hasNext()) {
				PdcPaymentDetailsModel payDet = (PdcPaymentDetailsModel) detItr.next();
				if(payDet.getPdc_child_id()!=0){
					getSession().createQuery("update PdcDetailsModel set status=:status, paymentDate=:date where id=:id")
								.setParameter("id", payDet.getPdc_child_id())
								.setParameter("date", CommonUtil.getSQLDateFromUtilDate(mdl.getDate()))
								.setParameter("status", SConstants.PDCStatus.ISSUED).executeUpdate();
				}
				flush();
				if(payDet.getType()==SConstants.PURCHASE){
					getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and type=:type and office_id=:office_id")
								.setParameter("paymentDetailsId", payDet.getId())
								.setParameter("paymentId", mdl.getId())
								.setParameter("office_id", mdl.getOffice_id())
								.setParameter("payment_type", SConstants.PDC_PAYMENT)
								.setParameter("type", SConstants.PURCHASE).executeUpdate();
				}
				else if(payDet.getType()==SConstants.SALES){
					getSession().createQuery("delete from BankDetailsInvoiceMapModel where paymentDetailsId=:paymentDetailsId and payment_type=:payment_type and paymentId=:paymentId and payment_type=:payment_type and type=:type and office_id=:office_id")
								.setParameter("paymentDetailsId", payDet.getId())
								.setParameter("paymentId", mdl.getId())
								.setParameter("office_id", mdl.getOffice_id())
								.setParameter("payment_type", SConstants.PDC_PAYMENT)
								.setParameter("type", SConstants.SALES).executeUpdate();
				}
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
			
			// Updating
			
			if(transaction.getTransaction_details_list()!=null){
				
				if(mdl.getTransactionId()!=0)
					getSession().update(transaction);
				else
					getSession().save(transaction);
				flush();
				
				Iterator<TransactionDetailsModel> titer = transaction.getTransaction_details_list().iterator();
				while (titer.hasNext()) {
					
					TransactionDetailsModel tdm = titer.next();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
							.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
							.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
					
					flush();
				}
				
				mdl.setTransactionId(transaction.getTransaction_id());
			}
			else{
				if(mdl.getTransactionId()!=0){
					getSession().delete(transaction);
					mdl.setTransactionId(0);
					flush();
				}
			}
			
			Iterator itr=mdl.getPdc_payment_list().iterator();
			while (itr.hasNext()) {
				PdcPaymentDetailsModel payDet = (PdcPaymentDetailsModel) itr.next();
				if(payDet.getId()!=0){
					if(oldChildIdList.contains(payDet.getId())){
						oldChildIdList.remove(payDet.getId());
					}
				}
			}
			getSession().clear();
			
			if(mdl.getId()!=0)
				getSession().update(mdl);
			else
				getSession().save(mdl);
			flush();
			
			itr=mdl.getPdc_payment_list().iterator();
			while (itr.hasNext()) {
				PdcPaymentDetailsModel payDet = (PdcPaymentDetailsModel) itr.next();
				List billList=new ArrayList();
				
				if(isPayment){
					
					if(payDet.getPdc_child_id()!=0){
						getSession().createQuery("update PdcDetailsModel set status=:status, paymentDate=:date where id=:id")
									.setParameter("id", payDet.getPdc_child_id())
									.setParameter("date", CommonUtil.getSQLDateFromUtilDate(mdl.getDate()))
									.setParameter("status", SConstants.PDCStatus.APPROVED).executeUpdate();
						flush();
					}
					
				}
				else{
					if(payDet.getStatus()==SConstants.PDCStatus.CANCELLED){
						if(payDet.getPdc_child_id()!=0){
							getSession().createQuery("update PdcDetailsModel set status=:status, paymentDate=:date where id=:id")
										.setParameter("id", payDet.getPdc_child_id())
										.setParameter("date", CommonUtil.getSQLDateFromUtilDate(mdl.getDate()))
										.setParameter("status", SConstants.PDCStatus.CANCELLED).executeUpdate();
							flush();
						}
					}
				}
				
				if(payDet.getStatus()==SConstants.PDCStatus.APPROVED){
					if(payDet.getType()==SConstants.PURCHASE){
						
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
																									SConstants.PDC_PAYMENT);
									getSession().save(map);
									flush();
								}
							}
						}
						
					}
					else if(payDet.getType()==SConstants.SALES){
						
						billList=Arrays.asList(payDet.getBill_no().split(","));
						if(billList.size()>0){
							Iterator it=billList.iterator();
							while (it.hasNext()) {
								String id=it.next().toString().trim();
								if(id.length()>0){
									long pid = Long.parseLong(id);
									BankDetailsInvoiceMapModel map=new BankDetailsInvoiceMapModel(SConstants.SALES, 
																									mdl.getOffice_id(),
																									mdl.getId(),
																									payDet.getId(),
																									pid,
																									SConstants.PDC_PAYMENT);
									getSession().save(map);
									flush();
								}
							}
						}
					}
				}
			}
			
			itr=invoiceMapList.iterator();
			while (itr.hasNext()) {
				PaymentInvoiceBean det = (PaymentInvoiceBean) itr.next();
				
				if(det.getType()==SConstants.PURCHASE){
					
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
					mapMdl.setPayment_type(SConstants.PDC_PAYMENT);
					getSession().save(mapMdl);
					
				}
				else if(det.getType()==SConstants.SALES){
					
					SalesModel pmdl=(SalesModel)getSession().get(SalesModel.class, det.getInvoiceId());
					double amount=pmdl.getPaid_by_payment()+det.getAmount();
					pmdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
					
					if(((pmdl.getAmount() - pmdl.getExpenseAmount()) +
						(pmdl.getExpenseAmount() - pmdl.getExpenseCreditAmount()) - 
						 pmdl.getPayment_amount() - amount)>0) {
						
						pmdl.setPayment_done('N');
						pmdl.setStatus(SConstants.PARTIALLY_PAID);
					}
					else {
						pmdl.setPayment_done('Y');
						pmdl.setStatus(SConstants.FULLY_PAID);
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
					mapMdl.setPayment_type(SConstants.PDC_PAYMENT);
					getSession().save(mapMdl);
				}
			}
			flush();
			
			if(oldChildIdList.size()>0){
				getSession().createQuery("delete from PdcPaymentDetailsModel where id in (:list)")
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
		return mdl.getId();
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getPdcList(Date start, long office, boolean isCreateNew) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			String cdn="";
			if(isCreateNew){
				cdn+=" and b.status="+SConstants.PDCStatus.ISSUED;
			}
			resultList=getSession().createQuery("select distinct a from PdcModel a join a.pdc_list b where a.office_id=:office and a.chequeDate <=:start "+cdn)
					.setParameter("office", office).setParameter("start", start).list();
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
	public List getPdcCount(Date start, long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select distinct b from PdcModel a join a.pdc_list b where a.office_id=:office and a.chequeDate <=:start  and b.status=:status")
					.setParameter("office", office).setParameter("status", SConstants.PDCStatus.ISSUED).setParameter("start", start).list();
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
