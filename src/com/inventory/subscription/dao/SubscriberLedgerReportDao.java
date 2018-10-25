package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SubscriberLedgerReportDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public List getLedgerReport(long subscriber,Date start,Date end,long office)throws Exception{
		List list=new ArrayList();
		try{
			begin();
			/*list=getSession().createQuery("from SubscriptionPaymentModel where (from_account=:ledger or to_account=:ledger)" +
					" and payment_date between :start and :end and pay_credit=0 and subscription.subscription.subscription_type.officeId=:office")
					.setParameter("ledger", subscriber).setParameter("start", start).setParameter("end", end).setParameter("office", office)
					.list();*/
			list=getSession().createQuery("from SubscriptionPaymentModel where (from_account=:ledger or to_account=:ledger)" +
					" and payment_date between :start and :end and subscription.subscription.subscription_type.officeId=:office order by payment_date")
					.setParameter("ledger", subscriber).setParameter("start", start).setParameter("end", end).setParameter("office", office)
					.list();
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
	
	public double getOpeningBalance(long subscriber,Date start,long office) throws Exception{
		double balance=0,credit=0,total=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select coalesce(sum(a.amount_paid),0) from SubscriptionPaymentModel a where a.payment_date<:start " +
					" and a.amount_paid>0 and a.pay_credit=0 and a.subscription.subscription.subscription_type.officeId=:ofc and (a.from_account=:ledger or a.to_account=:ledger)")
					.setParameter("ledger", subscriber).setParameter("start", start).setParameter("ofc", office).uniqueResult();
			
			if(obj!=null){
				balance=(Double)obj;
			}
			
			Object obj1=getSession().createQuery("select coalesce(sum(a.amount_paid),0) from SubscriptionPaymentModel a where a.payment_date<:start " +
					" and a.amount_paid>1 and a.pay_credit=1 and a.subscription.subscription.subscription_type.officeId=:ofc and (a.from_account=:ledger or a.to_account=:ledger)")
					.setParameter("ledger", subscriber).setParameter("start", start).setParameter("ofc", office).uniqueResult();
			
			if(obj1!=null){
				credit=(Double)obj1;
			}
			total=(balance-credit);
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
		return total;
	}
	
	public String getSubscriptionName(long paymentId) throws Exception{
		String name="";
		try
		{
			begin();
			Object obj=getSession().createQuery("select  a.subscription.subscription.name from SubscriptionPaymentModel a where " +
					" a.id=:id")
					.setParameter("id", paymentId).uniqueResult();
			if(obj!=null){
				name=(String)obj;
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
		return name;
	}
	
	public long getSubscriptionInId(long paymentId) throws Exception{
		long id=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select  a.subscription.id from SubscriptionPaymentModel a where " +
					"a.pay_credit=0 and a.id=:id")
					.setParameter("id", paymentId).uniqueResult();
			if(obj!=null){
				id=(Long)obj;
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
		return id;
	}
	
	
}
