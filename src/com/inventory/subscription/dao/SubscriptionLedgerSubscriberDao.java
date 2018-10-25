package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.webspark.dao.SHibernate;

public class SubscriptionLedgerSubscriberDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = 6190651947000088562L;
	@SuppressWarnings({ "rawtypes" })
	public List getSubscriptionPayments(long subscription,Date start,Date end,long ofc) throws Exception{
		List list=null;
		try
		{
			begin();
			String condition="";
			if(subscription!=0)
				condition+=" and a.subscription.subscription.id = "+subscription;
			
			list=getSession().createQuery("from SubscriptionPaymentModel a where a.payment_date between :start and :end and " +
					"a.subscription.subscription.subscription_type.officeId=:ofc and a.pay_credit=0 "+condition)
					.setParameter("ofc", ofc).setParameter("start", start).setParameter("end", end).list();
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
	
	public double getOpeningBalancePayment(long subscription,Date start,long ofc) throws Exception{
		double balance=0;
		try
		{
			begin();
			String condition="";
			if(subscription!=0)
				condition+=" and a.subscription.subscription.id = "+subscription;
			
			Object obj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel a where a.payment_date<:start " +
					" and a.pay_credit=0 and a.subscription.subscription.subscription_type.officeId=:ofc "+condition)
					.setParameter("start", start).setParameter("ofc", ofc).uniqueResult();
			if(obj!=null){
				balance=(Double)obj;
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
		return balance;
	}
	
	public long getAccountType(SubscriptionPaymentModel mdl) throws Exception{
		long id=0;
		try
		{
			begin();
			String condition="";
			Object obj=getSession().createQuery("select subscription.account_type from SubscriptionPaymentModel where pay_credit=0 and id="+mdl.getId()).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
	public long getRentStatus(SubscriptionPaymentModel mdl) throws Exception{
		long id=0;
		try
		{
			begin();
			String condition="";
			Object obj=getSession().createQuery("select subscription.subscription.rent_status from SubscriptionPaymentModel where pay_credit=0 and id="+mdl.getId()).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
	public long getVehicleStatus(SubscriptionPaymentModel mdl) throws Exception{
		long id=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select subscription.available from SubscriptionPaymentModel where pay_credit=0 and id="+mdl.getId()).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
	public long getSubscriber(SubscriptionPaymentModel mdl) throws Exception{
		long id=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select subscription.subscriber from SubscriptionPaymentModel where pay_credit=0 and id="+mdl.getId()).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
	public long getAccount(long scid) throws Exception{
		long id=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select account_type from SubscriptionCreationModel where id="+scid).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
	public String getSubscriptionName(SubscriptionPaymentModel mdl) throws Exception{
		String name=null;
		try
		{
			begin();
			Object obj=getSession().createQuery("select subscription.subscription.name from SubscriptionPaymentModel where pay_credit=0 and id="+mdl.getId()).uniqueResult();
			if(obj!=null)
				name=(String)obj;
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
		return name;
	}
	
	public Double getTotalPaid(long subscription,Date date/*,Date closing*/) throws Exception{
		double balance=0;
		try
		{
			begin();
			String condition="";
			String cdn="";
			if(subscription!=0)
				condition+=" and a.subscription.id = "+subscription;
			/*if(closing!=null)
				cdn+=" or payment_date<="+closing;*/
			Object obj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel a where a.pay_credit=0 and a.payment_date<=:date "+cdn+condition)
					.setParameter("date", date).setParameter("date", date).uniqueResult();
			if(obj!=null){
				balance=(Double)obj;
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
		return balance;
	}
	
}
