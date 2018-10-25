package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.subscription.model.RentalTransactionModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class RentalTransactionNewDao  extends SHibernate implements Serializable{

	public long save(RentalTransactionModel obj, TransactionModel transaction)
			throws Exception {
		
		try {
			
			begin();
			
			// Transaction Related
			
			getSession().save(transaction);
			
			flush();

			TransactionDetailsModel tr;

			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			
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
		}
		return obj.getId();
	}
	
	@SuppressWarnings("unchecked")
	public void update(RentalTransactionModel newobj, TransactionModel transaction) throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery("select b.id from RentalTransactionModel a join a.inventory_details_list b where a.id=" + newobj.getId()).list();

			// Delete

			List old_AcctnotDeletedLst = new ArrayList();

			List AcctDetLst = getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id="+ transaction.getTransaction_id()).list();
			
			TransactionDetailsModel tr;
			
			Iterator<TransactionDetailsModel> aciter = AcctDetLst.iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

				old_AcctnotDeletedLst.add(tr.getId());

			}

			// Transaction Related

			getSession().update(transaction);
			
			flush();

			Iterator<TransactionDetailsModel> aciter1 = transaction.getTransaction_details_list().iterator();
			while (aciter1.hasNext()) {
				tr = aciter1.next();

				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}
			
			// Transaction Related
			
			getSession().update(newobj);
			flush();

			getSession()
					.createQuery(
							"delete from RentalTransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

			getSession()
					.createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
					.executeUpdate();

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
	}
	
	public void delete(long id) throws Exception {
		try {
			begin();
			RentalTransactionModel obj = (RentalTransactionModel) getSession().get(RentalTransactionModel.class, id);

			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
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
			
			flush();

			// Transaction Related

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
	
	public List getAllRentalTransactions(long office)throws Exception{
		
		List list=new ArrayList();
		try{
			begin();
			list=getSession().createQuery("select new com.inventory.subscription.model.RentalTransactionModel(id,concat(sales_number,' - [',customer.name,']')) " +
					"from RentalTransactionModel where office.id="+office).list();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	public RentalTransactionModel getRentalTransactionModel(long id)throws Exception {
		RentalTransactionModel mdl=null;
		try {
			
			begin();
			mdl=(RentalTransactionModel)getSession().get(RentalTransactionModel.class, id);
			commit();
		}
		catch (Exception e) {
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
	
	public TransactionModel getTransaction(long id) throws Exception {
		TransactionModel tran = null;
		try {
			begin();
			tran = (TransactionModel) getSession().get(TransactionModel.class, id);
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
		return tran;
	}
	
}
