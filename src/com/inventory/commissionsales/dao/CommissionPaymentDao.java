package com.inventory.commissionsales.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

public class CommissionPaymentDao extends SHibernate{

	public List getAllPaymentNos(long officeID) throws Exception {
			List resultList;
			try {
				begin();
				resultList = getSession()
						.createQuery(
								"select new com.inventory.commissionsales.model.CommissionPaymentModel(id, cast(number as string))"
										+ " from CommissionPaymentModel where office.id=:ofc order by id desc")
						.setParameter("ofc", officeID).list();
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

	public CommissionPaymentModel getPaymentModel(long id) throws Exception {
		CommissionPaymentModel mdl=null;
		try {
			begin();
			mdl=(CommissionPaymentModel) getSession().get(CommissionPaymentModel.class, id);
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

	public long save(CommissionPaymentModel obj,TransactionModel transaction) throws Exception {
		try {

			begin();
			
			getSession().save(transaction);
			
			flush();
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			obj.setTransaction_id(transaction.getTransaction_id());
			
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return obj.getId();
	}

	public void update(CommissionPaymentModel objModel,	TransactionModel transaction) throws Exception {
		try {
			
			begin();
			
			List old_AcctnotDeletedLst = new ArrayList();
			List AcctDetLst = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id()).list();
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = AcctDetLst.iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery(
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
				
				old_AcctnotDeletedLst.add(tr.getId());
				
			}
			
			getSession().update(transaction);
			
			flush();
			
			Iterator<TransactionDetailsModel> aciter1 = transaction
					.getTransaction_details_list().iterator();
			while (aciter1.hasNext()) {
				tr = aciter1.next();

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
			
			
			getSession().update(objModel);
			flush();
			
			getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
				.setParameterList("lst", (Collection) old_AcctnotDeletedLst).executeUpdate();
			
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
	
	public void delete(long id) throws Exception {
		try {
			begin();
			CommissionPaymentModel obj = (CommissionPaymentModel) getSession()
					.get(CommissionPaymentModel.class, id);
			
			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			getSession().delete(transObj);
			
			flush();
			
			getSession().delete(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
		
	}

	public double getTotalSalesAmountOfPurchase(long purchaseId) throws Exception {
		
		double total=0;
		
		try {
			begin();
			Object mdl=getSession().createQuery("select coalesce(sum(amount),0) from CommissionSalesNewModel" +
					" where purchase_id=:pid").setParameter("pid", purchaseId).uniqueResult();
			commit();
			if(mdl!=null)
				total=(Double) mdl;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return total;
	}

}
