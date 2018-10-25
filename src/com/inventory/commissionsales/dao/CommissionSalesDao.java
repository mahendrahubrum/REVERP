package com.inventory.commissionsales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionSalesModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class CommissionSalesDao extends SHibernate implements Serializable{

	List resultList = new ArrayList();

	public long save(CommissionSalesModel obj, TransactionModel transaction) throws Exception {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	
	public void update(CommissionSalesModel obj, TransactionModel transaction) throws Exception {
		
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
			
			
			getSession().update(obj);
			flush();
			
			getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
				.setParameterList("lst", (Collection) old_AcctnotDeletedLst).executeUpdate();
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			CommissionSalesModel obj = (CommissionSalesModel) getSession()
					.get(CommissionSalesModel.class, id);
			
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
			// TODO Auto-generated catch block
		}
		flush();
		close();
	}

	public List getAllSales() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(CommissionSalesModel.class)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getAllActiveSalesNames(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.commissionsales.model.CommissionSalesModel(id, cast(number as string))"
									+ " from CommissionSalesModel where status=:val and office.id=:ofc")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.SALES_TYPE_ACTIVE)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}

	public CommissionSalesModel getCommissionSales(long id) throws Exception {
		CommissionSalesModel mod = null;
		try {
			begin();
			mod = (CommissionSalesModel) getSession().get(CommissionSalesModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}

}
