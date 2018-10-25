package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

public class SubscriptionListDao extends SHibernate implements Serializable {

	private static final long serialVersionUID = -1793177230062088532L;
	@SuppressWarnings({ "rawtypes" })
	public List getAllSubscriptions(long accountType,long subscriptionType,long office,Date start,Date end)throws Exception{
		List list=null;
		try
		{
			begin();
			String condition="";
			if(accountType!=0)
				condition+=" and account_type = "+accountType;
			if(subscriptionType!=0)
				condition+=" and id = "+subscriptionType;
			list=getSession().createQuery("from SubscriptionInModel where subscription_date between :start and :end and subscription.subscription_type.officeId=:office "+condition)
					.setParameter("office", office).setParameter("start", start).setParameter("end", end).list();
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
	
	@SuppressWarnings("rawtypes")
	public List getModel(long subscription)throws Exception{
		List list=new ArrayList();
		try
		{
			begin();
			list=(List)getSession().createQuery("from SubscriptionInModel where subscription.id=:subscription ").setParameter("subscription", subscription).list();
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
	
	@SuppressWarnings("rawtypes")
	public double getTotalCredit(long subscription,Date start,Date end)throws Exception{
		double credit=0;
		try
		{
			begin();
			List list=(List)getSession().createQuery("from SubscriptionPaymentModel where pay_credit=0 and credit_transaction!=0 and payment_date between :start and :end and subscription.id="+subscription)
					.setParameter("start", start).setParameter("end", end).list();
			if(list.size()>0) {
				SubscriptionPaymentModel mdl=null;
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					mdl=(SubscriptionPaymentModel)itr.next();
					if(mdl.getCredit_transaction()!=0) {
						TransactionModel model=(TransactionModel)getSession().get(TransactionModel.class, mdl.getCredit_transaction());
						TransactionDetailsModel detmdl=null;
						Iterator it=model.getTransaction_details_list().iterator();
						while (it.hasNext()) {
							detmdl=(TransactionDetailsModel)it.next();
							credit+=detmdl.getAmount();
						}
					}
				}
			}
			Object obj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where pay_credit=1 and payment_date between :start and :end and subscription.id="+subscription)
					.setParameter("start", start).setParameter("end", end).uniqueResult();
			if(obj!=null)
				credit+=(Double)obj;
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
		return credit;
	}
	
	@SuppressWarnings("rawtypes")
	public double getTotalCash(long subscription,Date start,Date end)throws Exception{
		double cash=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where pay_credit=0 and payment_date between :start and :end and subscription.id="+subscription)
					.setParameter("start", start).setParameter("end", end).uniqueResult();
			if(obj!=null)
				cash=(Double)obj;
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
		return cash;
	}
	
}
