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

@SuppressWarnings("serial")
public class SubscriptionPendingReportDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public List getPendingList(long ledger,Date start,Date end,long office) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			String condition="";
			if(ledger!=0)
				condition+=" and subscriber="+ledger;
			list=getSession().createQuery("from SubscriptionInModel where subscription_date between :start and :end" +
											" and subscription.subscription_type.officeId=:office"+condition+" order by id")
											.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			commit();
		}
		catch(Exception e) {
			rollback();
			flush();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return list;
	}
	
	public double getTotalPaid(long rental,Date start,Date end) throws Exception{
		double paid=0;
		try {
			begin();
			Object obj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date between :start and :end " +
					"and pay_credit=0 and subscription.id=:rental")
											.setParameter("start", start).setParameter("end", end).setParameter("rental", rental).uniqueResult();
			if(obj!=null)
				paid=(Double)obj;
			commit();
		}
		catch(Exception e) {
			rollback();
			flush();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return paid;
	}
	
	@SuppressWarnings("rawtypes")
	public double getTotalCredit(long rental,Date start,Date end) throws Exception{
		double credit=0;
		try {
			begin();
			Object obj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date between :start and :end" +
					" and pay_credit=1 and subscription.id=:rental")
											.setParameter("start", start).setParameter("end", end).setParameter("rental", rental).uniqueResult();
			if(obj!=null)
				credit=(Double)obj;
			List list=getSession().createQuery("from SubscriptionPaymentModel where payment_date " +
			 		"between :start and :end and pay_credit=0 and credit_transaction!=0 and subscription.id=:rental")
			 		.setParameter("start", start).setParameter("end", end).setParameter("rental", rental).list();
			 if(list.size()>0) {
				 Iterator it=list.iterator();
				 SubscriptionPaymentModel spmdl=null;
				 while (it.hasNext()) {
					spmdl=(SubscriptionPaymentModel)it.next();
					TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
					List transList=tran.getTransaction_details_list();
					Iterator tritr=transList.iterator();
					while (tritr.hasNext()) {
						TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
						credit+=det.getAmount();
					}
				}
			}
			commit();
		}
		catch(Exception e) {
			rollback();
			flush();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return credit;
	}
	
}
