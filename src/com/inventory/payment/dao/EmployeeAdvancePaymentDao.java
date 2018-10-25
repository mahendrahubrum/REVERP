package com.inventory.payment.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 22, 2013
 */

public class EmployeeAdvancePaymentDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4298177187345706539L;

	public List<Object> getPaymnetNo(long officeId) throws Exception {

		List<Object> list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.inventory.payment.model.EmployeeAdvancePaymentModel(id,cast(payment_id as string))" +
							" from EmployeeAdvancePaymentModel where office.id=:ofc and active=true order by id desc")
					.setParameter("ofc", officeId).list();

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

	public List getAllPurchaseNumbersForSupplier(long ofc_id, long supplierId)
			throws Exception {

		List<Object> list = null;
		try {
			begin();

			String condition = "";
			if (supplierId != 0) {
				condition = " and supplier.id=" + supplierId;
			}
			list = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseModel(id,cast(purchase_number as string) )"
									+ " from PurchaseModel where  office.id=:ofcId and active=true"
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
	
	public long getLedgerFromLogin(long empId) throws Exception{
		
		long ledg=0;
		try {
			begin();
			ledg= (Long) getSession().createQuery("select ledger.id from UserModel where loginId.id=:id").setParameter("id", empId).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return ledg;
	}
	
	
	public long save(EmployeeAdvancePaymentModel paymentModel, TransactionModel transaction)
			throws Exception {

		try {
			begin();
			getSession().save(transaction);
			flush();

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
	
	
	
	public EmployeeAdvancePaymentModel getEmployeeAdvancePaymentModel(long paymentId) throws Exception {
		EmployeeAdvancePaymentModel paymentModel = null;

		try {
			begin();
			paymentModel = (EmployeeAdvancePaymentModel) getSession().get(EmployeeAdvancePaymentModel.class,
					paymentId);
			commit();
		} catch (Exception e) {
			paymentModel = new EmployeeAdvancePaymentModel();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return paymentModel;
	}

	
	
	
	
	
	public void update(EmployeeAdvancePaymentModel paymentModel, TransactionModel transaction)
			throws Exception {

		try {
			begin();
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

			EmployeeAdvancePaymentModel paymentModel = (EmployeeAdvancePaymentModel) getSession().get(
					EmployeeAdvancePaymentModel.class, paymentId);

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
	
	public void cancel(long paymentId) throws Exception {
		try {
			begin();
			
			EmployeeAdvancePaymentModel paymentModel = (EmployeeAdvancePaymentModel) getSession().get(
					EmployeeAdvancePaymentModel.class, paymentId);
			
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
					"update EmployeeAdvancePaymentModel set active=false where id=:id")
					.setParameter("id", paymentModel.getId()).executeUpdate();
			
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
	
	
	public double getThisMonthPayed(long ledger_id, Date stdt, Date enddt) throws Exception {
		double total=0;
		try {
			begin();

			total=(Double) getSession().createQuery("select coalesce(sum(amount),0) from EmployeeAdvancePaymentModel where date between :stdt and :enddt and login_id=:act and active=true")
					.setLong("act", ledger_id).setDate("stdt", stdt).setDate("enddt", enddt).uniqueResult();
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return total;
		}
	}
	


}
