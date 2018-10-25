package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.List;

import com.inventory.subscription.model.SubscriptionConfigurationModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SubscriptionConfigurationDao extends SHibernate implements Serializable
{
	public long save(SubscriptionConfigurationModel mdl) throws Exception
	{
		try
		{
			begin();
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
	
	public long update(SubscriptionConfigurationModel mdl) throws Exception
	{
		try
		{
			begin();
			getSession().update(mdl);
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
	
	public long delete(SubscriptionConfigurationModel mdl) throws Exception
	{
		try
		{
			begin();
			getSession().delete(mdl);
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
	
	@SuppressWarnings({ "rawtypes"})
	public List getAllSubscriptionTypes(long officeId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionConfigurationModel(id,name)"+
											" from SubscriptionConfigurationModel where officeId=:oid order by name").setParameter("oid", officeId).list();
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
	public List getSubscriptionTypes(long officeId,long type) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionConfigurationModel(id,name)"+
											" from SubscriptionConfigurationModel where officeId=:oid and account_type=:type order by name")
											.setParameter("oid", officeId).setParameter("type", type).list();
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
	
	public SubscriptionConfigurationModel getConfigurationModel(long id) throws Exception
	{
		SubscriptionConfigurationModel mdl = null;
		try
		{
			begin();
			mdl=(SubscriptionConfigurationModel) getSession().get(SubscriptionConfigurationModel.class, id);
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
}

