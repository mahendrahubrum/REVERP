package com.inventory.journal.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.journal.model.JournalModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author sangeeth
 *
 */
@SuppressWarnings("serial")
public class JournalDao extends SHibernate implements Serializable{
	
//	List resultList=new ArrayList();
	
	public long saveJounal(JournalModel obj, TransactionModel mdl) throws Exception {
		try {
			begin();
			getSession().save(mdl);
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = mdl.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			obj.setTransaction_id(mdl.getTransaction_id());
			getSession().save(obj);
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
		return obj.getId();
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getJournalModelList(long office_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.journal.model.JournalModel(id,bill_no)" +
					" from JournalModel where office_id=:ofcid order by id desc").setLong("ofcid", office_id).list();
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
	
	
	public long getTransactionIdFromJournal(long id) throws Exception {
		long transId=0;
		try {
			begin();
			transId=(Long) getSession().createQuery("select transaction_id from JournalModel where id=:id")
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
		return transId;
	}
	
	
	public JournalModel getJournalModel(long id) throws Exception {
		JournalModel obj=null;
		try {
			begin();
			obj=(JournalModel) getSession().get(JournalModel.class, id);
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
		return obj;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateJounal(JournalModel obj, TransactionModel mdl) throws Exception {
		try {
			begin();
			List childList=new ArrayList();
			List oldTransList=new ArrayList();
			List transList=new ArrayList();
			childList=getSession().createQuery("select b.id from JournalModel a join a.journal_details_list b where a.id=:id")
									.setParameter("id", obj.getId()).list();
			
			oldTransList=getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b " +
					"where a.id=:id").setParameter("id", mdl.getTransaction_id()).list();
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = oldTransList.iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
				transList.add(tr.getId());
			}
			
			getSession().update(mdl);
			
			Iterator<TransactionDetailsModel> itr = mdl.getTransaction_details_list().iterator();
			while (itr.hasNext()) {
				tr = itr.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			getSession().update(obj);
			
			flush();
			if(transList.size()>0)
				getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) transList).executeUpdate();
			
			if(childList.size()>0)
				getSession().createQuery("delete from JournalDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) childList).executeUpdate();
			
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
	
	
	public void deleteJounal(long journelId) throws Exception {
		try {
			begin();
			JournalModel jm=(JournalModel) getSession().get(JournalModel.class, journelId);
			
			TransactionModel trans=(TransactionModel) getSession().get(TransactionModel.class, jm.getTransaction_id());
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = trans.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			getSession().delete(trans);
			getSession().delete(jm);
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
	
	
	
	
	
	
	
	
	
	
}
