package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionInventoryDetailsModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SubscriptionOutDao extends SHibernate implements Serializable{

	public long save(SubscriptionInModel mdl,SubscriptionCreationModel scmdl,SubscriptionInModel simdl) throws Exception
	{
		try
		{
			begin();
			getSession().update(scmdl);
			flush();
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
			getSession().createQuery("delete from SubscriptionPaymentModel where subscription.id="+mdl.getId()).executeUpdate();
			List old_notDeletedLst = getSession().createQuery("select b.id from SubscriptionInModel a join a.subscription_details_list b where a.id=" + mdl.getId()).list();
			getSession().update(scmdl);
			flush();
			SubscriptionInventoryDetailsModel detmdl;
			if(mdl.getSubscription_details_list()!=null && mdl.getSubscription_details_list().size()!=0){
				Iterator itr=mdl.getSubscription_details_list().iterator();
				while(itr.hasNext()){
					detmdl=(SubscriptionInventoryDetailsModel)itr.next();
					if(old_notDeletedLst.contains(detmdl.getId())){
						old_notDeletedLst.remove(detmdl.getId());
					}
				}
			}
			
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
			List old_notDeletedLst = getSession().createQuery(
					"select b.id from SubscriptionInModel a join a.subscription_details_list b "
							+ "where a.id=" + mdl.getId()).list();
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
					" from CustomerModel where subscription=:sid and ledger.office.id=:ofc order by name").setParameter("ofc",ofc).setParameter("sid",(long)1).list();
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
											" from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid and a.available=1 and a.return_date is null and" +
											" b.rent_status=3 order by subscription_no")
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
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllOutSubscriptions(long officeId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel" +
					"(a.id,concat(a.subscription_no,' ( ',b.name,' )'))"+
											" from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid and a.available=2 and" +
											" b.rent_status=3 and a.rent_in!=0 order by subscription_no")
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
											"rent_status=3 order by name")
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
	public Long getAvailablilty(long id,long office) throws Exception
	{	
		long status=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select available from SubscriptionAllocationModel " +
					"where id=:id and subscription.subscription_type.officeId=:office")
					.setParameter("office", office).setParameter("id", id).uniqueResult();
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
	
}
