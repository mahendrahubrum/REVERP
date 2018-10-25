package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SubscriptionInDao extends SHibernate implements Serializable{

	public long save(SubscriptionInModel mdl,SubscriptionCreationModel scmdl) throws Exception
	{
		try
		{
			begin();
			getSession().update(scmdl);
			flush();
			getSession().save(mdl);
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl.getId();
	}
	
	public long update(SubscriptionInModel mdl,SubscriptionCreationModel scmdl) throws Exception
	{
		try
		{
			begin();
//			getSession().createSQLQuery("DELETE FROM `i_subscription_inventory_details` WHERE id not in(Select item_details_id from rent_inv_link)").executeUpdate();
			List old_notDeletedLst = getSession().createQuery(
					"select b.id from SubscriptionInModel a join a.subscription_details_list b "
							+ "where a.id=" + mdl.getId()).list();
			getSession().update(scmdl);
			flush();
			
			getSession().update(mdl);
			flush();
			
			try {
				if(old_notDeletedLst.size()>0)
				getSession()
				.createQuery(
						"delete from SubscriptionInventoryDetailsModel where id in (:lst)")
				.setParameterList("lst", old_notDeletedLst)
				.executeUpdate();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl.getId();
	}
	
	public void delete(SubscriptionInModel mdl,SubscriptionCreationModel scmdl) throws Exception
	{
		try
		{
			begin();
			SubscriptionPaymentModel spmdl=null;
			
			List list=getSession().createQuery("from SubscriptionPaymentModel where subscription.id="+mdl.getId()).list();
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					spmdl=(SubscriptionPaymentModel)itr.next();
					TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, spmdl.getTransaction_id());
					Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
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
					getSession().delete(spmdl);
					flush();
				}
			}
			
			List old_notDeletedLst = getSession().createQuery("select b.id from SubscriptionInModel a join a.subscription_details_list b where a.id=" + mdl.getId()).list();
			getSession().update(scmdl);
			flush();
			getSession().delete(mdl);
			flush();
			try {
				if(old_notDeletedLst.size()>0)
				getSession()
				.createQuery(
						"delete from SubscriptionInventoryDetailsModel where id in (:lst)")
				.setParameterList("lst", old_notDeletedLst)
				.executeUpdate();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllExpenditureSubscriptions(long ofc) throws Exception{
		List list=null;
		try{
			begin();
			list=getSession().createQuery("select new com.inventory.config.acct.model.SupplierModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"+
					" from SupplierModel where subscription=:sid and ledger.office.id=:ofc order by name").setParameter("ofc",ofc).setParameter("sid",(long)1).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return list;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllIncomeSubscriptions(long ofc) throws Exception{
		List list=null;
		try{
			begin();
			list=getSession().createQuery("select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"+
					" from CustomerModel where subscription=:sid and ledger.office.id=:ofc order by name")
					.setParameter("sid",(long)1).setParameter("ofc",ofc).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return list;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllTransportationSubscriptions(long officeId) throws Exception{
		List list=null;
		try{
			begin();
			list=getSession().createQuery("select new com.inventory.config.acct.model.TranspotationModel(ledger.id, concat(name, ' [ ' , code,' ] '))"+
					" from TranspotationModel where ledger.office.id=:oid and subscription=1 order by name").setParameter("oid",officeId).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllInSubscriptions(long officeId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel" +
					"(a.id,concat(a.subscription_no,' ( ',b.name,' )'))"+
											" from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid " +
											"and a.available!=2 order by subscription_no")
											.setParameter("oid", officeId).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return idLIst;	
	}
	
	public SubscriptionInModel getSubscriptionInModel(long id) throws Exception
	{
		SubscriptionInModel mdl = null;
		try
		{
			begin();
			mdl=(SubscriptionInModel) getSession().get(SubscriptionInModel.class, id);
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllSubscriptions(long officeId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionCreationModel(id,name)"+
											" from SubscriptionCreationModel where subscription_type.officeId=:oid and " +
											"available!=2 order by name")
											.setParameter("oid", officeId).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return idLIst;	
	}
	
	@SuppressWarnings({ })
	public Long getAvailablilty(long id,long type) throws Exception
	{	
		long status=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select available from SubscriptionInModel " +
					"where id=:id and subscription.rent_status=:typ")
					.setParameter("typ", type).setParameter("id", id).uniqueResult();
			if(obj!=null)
				status=(Long)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return status;	
	}
	
	@SuppressWarnings({ })
	public Long getRentType(long in,long office) throws Exception
	{	
		long status=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select subscription.rent_status from SubscriptionInModel where id=:id and " +
					"subscription.subscription_type.officeId=:office")
					.setParameter("office", office).setParameter("id", in).uniqueResult();
			if(obj!=null)
				status=(Long)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return status;	
	}

	@SuppressWarnings({ "rawtypes" })
	public List getAllInSubscriptionsExceedingClosing(long officeId,Date date) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select a from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid " +
											"and a.return_date is NULL and a.closing_date<:date order by subscription_no")
											.setParameter("oid", officeId).setParameter("date", date).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return idLIst;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public SubscriptionInModel getSubscription(long subscriber,long account) throws Exception{
		SubscriptionInModel mdl = null;
		try{
			begin();
			Object obj=getSession().createQuery("from SubscriptionInModel where id=( select max(id) from SubscriptionInModel where account_type="+account+" and subscriber="+subscriber+")").uniqueResult();
			if(obj!=null)
				mdl=(SubscriptionInModel)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getPendingSubscription(long ofc) throws Exception{
		List list = null;
		try{
			begin();
			list=getSession().createQuery("from SubscriptionInModel where subscription.subscription_type.officeId=:ofc " +
					"and return_date is NULL and subscription.subscription_type.status=1 and subscription.special!=1").setParameter("ofc", ofc).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return list;	
	}
	
	
}
