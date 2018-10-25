package com.inventory.commissionsales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CustomerCommissionSalesModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class CustomerCommissionSalesDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5849037844831132390L;
	List resultList = new ArrayList();

	public long save(CustomerCommissionSalesModel obj, TransactionModel transaction) throws Exception {

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
			throw e;
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	public CustomerCommissionSalesModel getSale(long order_id) throws Exception {
		CustomerCommissionSalesModel po=null;
		try {
			begin();
			po = (CustomerCommissionSalesModel) getSession().get(CustomerCommissionSalesModel.class, order_id);
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
			return po;
		}
	}
	
	
	public void update(CustomerCommissionSalesModel obj, TransactionModel transaction) throws Exception {
		try {
			
			begin();
			
			List old_AcctnotDeletedLst = new ArrayList();
			List AcctDetLst = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id()).list();
				
			Object objLst=getSession().createQuery("select b.id from CustomerCommissionSalesModel a join a.details_list b " +
					"where a.id="+obj.getId()).list();
			
			
			
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

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				
				flush();
			}
			
			
			getSession().update(obj);
			flush();
			
			getSession().createQuery("delete from CommissionSalesCustomerDetailsModel where id in (:lst)")
			.setParameterList("lst", (Collection) objLst).executeUpdate();
			
			getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
			.setParameterList("lst", (Collection) old_AcctnotDeletedLst).executeUpdate();
				
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
		}
	}
	
	
	public void delete(long id) throws Exception {
		try {
			begin();
			
			CustomerCommissionSalesModel obj=(CustomerCommissionSalesModel) getSession().get(CustomerCommissionSalesModel.class, id);
			
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
	
	
	public List getAllPurchaseOrderNumbersAsRefNo(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.commissionsales.model.CustomerCommissionSalesModel(id,cast(sales_no as string) )" +
					" from CustomerCommissionSalesModel where office.id=:ofc order by sales_no desc").setParameter("ofc", ofc_id).list();
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
	
	
	public List getAllPurchaseOrders(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.commissionsales.model.CustomerCommissionSalesModel(id,concat(sales_no, '  - ', supplier.name, ' - ',date)) " +
					" from CustomerCommissionSalesModel where office.id=:ofc").setParameter("ofc", ofc_id).list();
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
	
	
	
	
}
