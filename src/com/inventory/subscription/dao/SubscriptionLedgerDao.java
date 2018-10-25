package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class SubscriptionLedgerDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = 6190651947000088562L;
	@SuppressWarnings({ "rawtypes" })
	public List getAllSubscriptionPayments(long accountType,long subscription) throws Exception{
		List list=null;
		try
		{
			begin();
			String condition="";
			long count=0;
			Object obj=getSession().createQuery("select count(distinct a.subscription.id) from SubscriptionPaymentModel a").uniqueResult();
			if(obj!=null)
				count=(Long)obj;
			System.out.println("Count "+count);
			if(accountType!=0)
				condition+=" and a.subscription.account_type = "+accountType;
			if(subscription!=0)
				condition+=" and a.subscription.id = "+subscription;
				list=getSession().createQuery("select new com.inventory.subscription.bean.SubscriptionLedgerBean(" +
						"a.subscription.subscription.name," +
						"cast(a.subscription_date as string)," +
						"(select name from LedgerModel where id=a.subscription.subscriber)," +
						"0.0," +
						"0.0" +
						") " +
						" from SubscriptionPaymentModel a where 1=1 and a.pay_credit=0 and a.subscription.id=" +
						"(select distinct a.subscription.id from SubscriptionPaymentModel a where a.pay_credit=0 and "+condition+")").list();
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
