package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.List;

import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SubscriptionPendingDao extends SHibernate implements Serializable  {

	@SuppressWarnings({ "rawtypes" })
	public List getAllSubscriptions(long accountType,long subscription,long office)throws Exception{
		List list=null;
		try
		{
			begin();
			String condition="";
			if(accountType!=0)
				condition+=" and a.account_type = "+accountType;
			if(subscription!=0)
				condition+=" and a.subscription.id = "+subscription;
			list=getSession().createQuery("from SubscriptionInModel a where subscription.subscription_type.officeId=:office and return_date is NULL "+condition)
					.setParameter("office", office).list();
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
