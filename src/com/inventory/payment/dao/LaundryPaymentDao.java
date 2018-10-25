package com.inventory.payment.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.payment.model.PaymentModel;
import com.inventory.sales.model.PaymentInvoiceMapModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 23, 2013
 */
public class LaundryPaymentDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8819778150730753985L;

	public List<Object> getPaymnetNo(long officeId, int type) throws Exception {

		List<Object> list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.inventory.payment.model.PaymentModel(id,cast(payment_id as string)) from PaymentModel where office.id=:ofc and type=:type and active =true order by id desc")
					.setParameter("ofc", officeId).setParameter("type", type)
					.list();

			commit();
		} catch (Exception e) {
			list = new ArrayList<Object>();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return list;
	}

	public List getAllPurchaseNumbersForSupplier(long ofc_id, long supplierId,
			boolean isCreateNew) throws Exception {

		List<Object> list = null;
		try {
			begin();

			String condition = "";
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (isCreateNew)
				condition += " and payment_done='N'";

			list = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseModel(id,cast(purchase_number as string) )"
									+ " from PurchaseModel where  office.id=:ofcId and active=true and status in (2,3)"
									+ condition).setParameter("ofcId", ofc_id)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return list;
		}
	}

	public long save(PaymentModel paymentModel, TransactionModel transaction)
			throws Exception {

		try {
			begin();
			getSession().save(transaction);

			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			paymentModel.setTransaction_id(transaction.getTransaction_id());

			getSession().save(paymentModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return paymentModel.getId();
		}
	}


	// Added By Jinshad

	public long save(PaymentModel paymentModel, TransactionModel transaction,
			Set<Long> options_selected) throws Exception {
		try {
			begin();
			getSession().save(transaction);

			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			paymentModel.setTransaction_id(transaction.getTransaction_id());

			getSession().save(paymentModel);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return paymentModel.getId();
		}
	}

	public long saveCustomerPayment(PaymentModel paymentModel,
			TransactionModel transaction, Set<Long> options_selected)
			throws Exception {
		try {
			begin();
			
			getSession().save(transaction);

			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}
			
			paymentModel.setTransaction_id(transaction.getTransaction_id());
			
			getSession().save(paymentModel);
			
			Iterator<Long> it = options_selected.iterator();
			double tot_amt=paymentModel.getPayment_amount();
			long id;
			while (it.hasNext()) {
				id=it.next();
				double bal_amt=(Double) getSession().createQuery("select amount-payment_amount-paid_by_payment from LaundrySalesModel where id=:id")
						.setLong("id", id).uniqueResult();
				
				if(tot_amt>bal_amt) {
					getSession().createQuery("update LaundrySalesModel set payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", bal_amt).executeUpdate();
					tot_amt=CommonUtil.roundNumber(tot_amt-bal_amt);
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(), SConstants.CUSTOMER_PAYMENTS, bal_amt));
					
				}
				else if(tot_amt==bal_amt) {
					getSession().createQuery("update LaundrySalesModel set payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
					.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
				
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(),SConstants.CUSTOMER_PAYMENTS, tot_amt));
					
					tot_amt=0;
				}
				else {
					getSession().createQuery("update LaundrySalesModel set paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(), SConstants.CUSTOMER_PAYMENTS, tot_amt));
					
					tot_amt=0;
					
				}
				
				if(tot_amt<=0)
					break;
				
			}

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return paymentModel.getId();
		}
	}

	public long saveSupplierPayment(PaymentModel paymentModel,
			TransactionModel transaction, Set<Long> options_selected)
			throws Exception {
		try {
			begin();
			
			getSession().save(transaction);

			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			paymentModel.setTransaction_id(transaction.getTransaction_id());

			getSession().save(paymentModel);

			
			Iterator<Long> it = options_selected.iterator();
			double tot_amt=paymentModel.getPayment_amount();
			long id;
			while (it.hasNext()) {
				id=it.next();
				double bal_amt=(Double) getSession().createQuery("select amount-payment_amount-paid_by_payment from PurchaseModel where id=:id")
						.setLong("id", id).uniqueResult();
				
				if(tot_amt>bal_amt) {
					getSession().createQuery("update PurchaseModel set payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", bal_amt).executeUpdate();
					tot_amt=CommonUtil.roundNumber(tot_amt-bal_amt);
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(), SConstants.SUPPLIER_PAYMENTS, bal_amt));
					
				}
				else if(tot_amt==bal_amt) {
					getSession().createQuery("update PurchaseModel set  payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
					.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
				
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(), SConstants.SUPPLIER_PAYMENTS, tot_amt));
				
					tot_amt=0;
				}
				else {
					getSession().createQuery("update PurchaseModel set paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(), SConstants.SUPPLIER_PAYMENTS, tot_amt));
					
					tot_amt=0;
					
				}
				
				if(tot_amt<=0)
					break;
				
			}
			
			
			/*Iterator<Long> it = options_selected.iterator();
			long purchaseId=0;
			while (it.hasNext()) {
				purchaseId=it.next();
				getSession()
						.createQuery(
								"update PurchaseModel set payment_done='Y' where id=:id")
						.setLong("id", purchaseId).executeUpdate();
				try{
					getSession().save(new PaymentInvoiceMapModel(purchaseId, paymentModel.getId(),SConstants.SUPPLIER_PAYMENTS));
				}catch(Exception e){
				}
			}*/

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return paymentModel.getId();
		}
	}

	public PaymentModel getPaymentModel(long paymentId) throws Exception {
		PaymentModel paymentModel = null;

		try {
			begin();
			paymentModel = (PaymentModel) getSession().get(PaymentModel.class,
					paymentId);
			commit();
		} catch (Exception e) {
			paymentModel = new PaymentModel();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return paymentModel;
	}

	public void update(PaymentModel paymentModel, TransactionModel transaction)
			throws Exception {

		try {
			begin();
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			getSession().update(transaction);

			Iterator<TransactionDetailsModel> iter = transaction
					.getTransaction_details_list().iterator();
			while (iter.hasNext()) {
				tr = iter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			getSession().update(paymentModel);
			

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	// Added By Jinsahd

	public void updateCustomerPayment(PaymentModel paymentModel,
			TransactionModel transaction, Set<Long> options_selected)
			throws Exception {
		
		try {
			begin();
			
			List oldPSMaps = getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay",paymentModel.getId()).setInteger("typ", SConstants.CUSTOMER_PAYMENTS).list();
			
			PaymentInvoiceMapModel psmObj;
			Iterator itr1=oldPSMaps.iterator();
			while (itr1.hasNext()) {
				psmObj=(PaymentInvoiceMapModel) itr1.next();
				if(psmObj.getAmount()>0) {
					getSession().createQuery("update LaundrySalesModel set payment_done='N',paid_by_payment=paid_by_payment-:amt where id=:id")
						.setLong("id", psmObj.getInvoiceId()).setDouble("amt", psmObj.getAmount()).executeUpdate();
				}
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:pay and type=:typ").setInteger("typ", SConstants.CUSTOMER_PAYMENTS).setLong("pay", paymentModel.getId()).executeUpdate();
			

			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			getSession().update(transaction);

			Iterator<TransactionDetailsModel> iter = transaction
					.getTransaction_details_list().iterator();
			while (iter.hasNext()) {
				tr = iter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
				
			}
			
			getSession().update(paymentModel);
			
			flush();
			
			double tot_amt=paymentModel.getPayment_amount();
			long id;
			Iterator<Long> it = options_selected.iterator();
			while (it.hasNext()) {
				id=it.next();
				double bal_amt=(Double) getSession().createQuery("select amount-payment_amount-paid_by_payment from LaundrySalesModel where id=:id")
									.setLong("id", id).uniqueResult();
				
				if(tot_amt>bal_amt) {
					getSession().createQuery("update LaundrySalesModel set payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", bal_amt).executeUpdate();
					tot_amt=CommonUtil.roundNumber(tot_amt-bal_amt);
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(),SConstants.CUSTOMER_PAYMENTS, bal_amt));
				}
				else if(tot_amt==bal_amt) {
					getSession().createQuery("update LaundrySalesModel set payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
					.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
				
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(),SConstants.CUSTOMER_PAYMENTS, tot_amt));
					
					tot_amt=0;
				}
				else {
					getSession().createQuery("update LaundrySalesModel set paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(),SConstants.CUSTOMER_PAYMENTS, tot_amt));
					
					tot_amt=0;
				}
				
				if(tot_amt<=0)
					break;
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void updateSupplierPayment(PaymentModel paymentModel,
			TransactionModel transaction, Set<Long> options_selected)
			throws Exception {

		try {
			begin();

			List oldPSMaps = getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay",paymentModel.getId()).setInteger("typ", SConstants.SUPPLIER_PAYMENTS).list();
			
			PaymentInvoiceMapModel psmObj;
			Iterator itr1=oldPSMaps.iterator();
			while (itr1.hasNext()) {
				psmObj=(PaymentInvoiceMapModel) itr1.next();
				if(psmObj.getAmount()>0) {
					getSession().createQuery("update PurchaseModel set payment_done='N',paid_by_payment=paid_by_payment-:amt where id=:id")
						.setLong("id", psmObj.getInvoiceId()).setDouble("amt", psmObj.getAmount()).executeUpdate();
				}
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay", paymentModel.getId()).setInteger("typ", SConstants.SUPPLIER_PAYMENTS).executeUpdate();
			
			
			
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			getSession().update(transaction);

			Iterator<TransactionDetailsModel> iter = transaction
					.getTransaction_details_list().iterator();
			while (iter.hasNext()) {
				tr = iter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			getSession().update(paymentModel);
			
			flush();
			
			double tot_amt=paymentModel.getPayment_amount();
			long id;
			Iterator<Long> it = options_selected.iterator();
			while (it.hasNext()) {
				id=it.next();
				double bal_amt=(Double) getSession().createQuery("select amount-payment_amount-paid_by_payment from PurchaseModel where id=:id")
									.setLong("id", id).uniqueResult();
				
				if(tot_amt>bal_amt) {
					getSession().createQuery("update PurchaseModel set payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", bal_amt).executeUpdate();
					tot_amt=CommonUtil.roundNumber(tot_amt-bal_amt);
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(),SConstants.SUPPLIER_PAYMENTS, bal_amt));
				}
				else if(tot_amt==bal_amt) {
					getSession().createQuery("update PurchaseModel set  payment_done='Y',paid_by_payment=paid_by_payment+:amt where id=:id")
					.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
				
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(), SConstants.SUPPLIER_PAYMENTS, tot_amt));
				
					tot_amt=0;
				}
				else {
					getSession().createQuery("update PurchaseModel set paid_by_payment=paid_by_payment+:amt where id=:id")
						.setLong("id", id).setDouble("amt", tot_amt).executeUpdate();
					
					getSession().save(new PaymentInvoiceMapModel(id, paymentModel.getId(),SConstants.SUPPLIER_PAYMENTS, tot_amt));
					
					tot_amt=0;
				}
				
				if(tot_amt<=0)
					break;
			}

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}


	// Added By Jinsahd
	public void deleteCustomerPayment(long paymentId) throws Exception {
		try {
			begin();

			List oldPSMaps = getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay",paymentId).setInteger("typ", SConstants.CUSTOMER_PAYMENTS).list();
			
			PaymentInvoiceMapModel psmObj;
			Iterator itr1=oldPSMaps.iterator();
			while (itr1.hasNext()) {
				psmObj=(PaymentInvoiceMapModel) itr1.next();
				if(psmObj.getAmount()>0) {
					getSession().createQuery("update LaundrySalesModel set payment_done='N',paid_by_payment=paid_by_payment-:amt where id=:id")
						.setLong("id", psmObj.getInvoiceId()).setDouble("amt", psmObj.getAmount()).executeUpdate();
				}
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:pay and type=:typ").setInteger("typ", SConstants.CUSTOMER_PAYMENTS).setLong("pay", paymentId).executeUpdate();
			

			PaymentModel paymentModel = (PaymentModel) getSession().get(
					PaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id")
				.setLong("id", paymentModel.getId()).executeUpdate();
			
			getSession().delete(transObj);
			getSession().delete(paymentModel);

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void deleteSupplierPayment(long paymentId) throws Exception {
		try {
			begin();

			List oldPSMaps = getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay",paymentId).setInteger("typ", SConstants.SUPPLIER_PAYMENTS).list();
			
			PaymentInvoiceMapModel psmObj;
			Iterator itr1=oldPSMaps.iterator();
			while (itr1.hasNext()) {
				psmObj=(PaymentInvoiceMapModel) itr1.next();
				if(psmObj.getAmount()>0) {
					getSession().createQuery("update PurchaseModel set payment_done='N',paid_by_payment=paid_by_payment-:amt where id=:id")
						.setLong("id", psmObj.getInvoiceId()).setDouble("amt", psmObj.getAmount()).executeUpdate();
				}
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay", paymentId).setInteger("typ", SConstants.SUPPLIER_PAYMENTS).executeUpdate();
			
			
			
			
			PaymentModel paymentModel = (PaymentModel) getSession().get(
					PaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id")
				.setLong("id", paymentModel.getId()).executeUpdate();

			getSession().delete(transObj);
			getSession().delete(paymentModel);

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void delete(long paymentId) throws Exception {
		try {
			begin();

			PaymentModel paymentModel = (PaymentModel) getSession().get(
					PaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);
			getSession().delete(paymentModel);

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void cancelCustomerPayment(long paymentId) throws Exception {
		try {
			begin();

			List oldPSMaps = getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay",paymentId).setInteger("typ", SConstants.CUSTOMER_PAYMENTS).list();
			
			PaymentInvoiceMapModel psmObj;
			Iterator itr1=oldPSMaps.iterator();
			while (itr1.hasNext()) {
				psmObj=(PaymentInvoiceMapModel) itr1.next();
				if(psmObj.getAmount()>0) {
					getSession().createQuery("update LaundrySalesModel set payment_done='N',paid_by_payment=paid_by_payment-:amt where id=:id")
						.setLong("id", psmObj.getInvoiceId()).setDouble("amt", psmObj.getAmount()).executeUpdate();
				}
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:pay and type=:typ").setInteger("typ", SConstants.CUSTOMER_PAYMENTS).setLong("pay", paymentId).executeUpdate();
			

			PaymentModel paymentModel = (PaymentModel) getSession().get(
					PaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);
			getSession()
					.createQuery(
							"update PaymentModel set active=false where id=:id")
					.setParameter("id", paymentId).executeUpdate();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void cancelSupplierPayment(long paymentId) throws Exception {
		try {
			
			begin();
			
			List oldPSMaps = getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay",paymentId).setInteger("typ", SConstants.SUPPLIER_PAYMENTS).list();
			
			PaymentInvoiceMapModel psmObj;
			Iterator itr1=oldPSMaps.iterator();
			while (itr1.hasNext()) {
				psmObj=(PaymentInvoiceMapModel) itr1.next();
				if(psmObj.getAmount()>0) {
					getSession().createQuery("update PurchaseModel set payment_done='N',paid_by_payment=paid_by_payment-:amt where id=:id")
						.setLong("id", psmObj.getInvoiceId()).setDouble("amt", psmObj.getAmount()).executeUpdate();
				}
			}
			getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:pay and type=:typ")
					.setLong("pay", paymentId).setInteger("typ", SConstants.SUPPLIER_PAYMENTS).executeUpdate();
			
			

			PaymentModel paymentModel = (PaymentModel) getSession().get(
					PaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);
			getSession()
					.createQuery(
							"update PaymentModel set active=false where id=:id")
					.setParameter("id", paymentId).executeUpdate();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void cancel(long paymentId) throws Exception {
		try {
			begin();

			PaymentModel paymentModel = (PaymentModel) getSession().get(
					PaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);
			getSession()
					.createQuery(
							"update PaymentModel set active=false where id=:id")
					.setParameter("id", paymentId).executeUpdate();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public TransactionModel getTransaction(long id) throws Exception {
		TransactionModel tran = null;
		try {
			begin();
			tran = (TransactionModel) getSession().get(TransactionModel.class,
					id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return tran;
		}
	}

	public double getCustomerAllPayedAmt(long custID, Date fromDt, Date toDt) throws Exception {
		double paidAmt = 0;
		
		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select sum(payment_amount) from PaymentModel where from_account_id=:cust and active=true and date between:frmdt and :todt and type="
									+ SConstants.CUSTOMER_PAYMENTS).setParameter("frmdt", fromDt).setParameter("todt", toDt)
					.setLong("cust", custID).uniqueResult();
			commit();
			
			if (obj != null)
				paidAmt = (Double) obj;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return paidAmt;
	}
	
	
	
	

	public double getPurchaseReturnAmount(Long ledgerId, Date fromDate, Date toDate) throws Exception {
		double paidAmt = 0;

		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select sum(amount-payment_amount) from PurchaseReturnModel where supplier.id=:supp and active=true and date between :from and :to")
					.setLong("supp", ledgerId).setParameter("from", fromDate).setParameter("to", toDate).uniqueResult();
			commit();

			if (obj != null)
				paidAmt = (Double) obj;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return paidAmt;
	}

	public double getSalesReturnAmount(Long ledgerId, Date fromDate, Date toDate) throws Exception {
		double paidAmt = 0;

		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select sum(amount-payment_amount) from SalesReturnModel where customer.id=:cust and active=true and date between :from and :to")
					.setLong("cust", ledgerId).setParameter("from", fromDate).setParameter("to", toDate).uniqueResult();
			commit();

			if (obj != null)
				paidAmt = (Double) obj;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return paidAmt;
	}

}
