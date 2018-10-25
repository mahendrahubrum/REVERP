package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SubscriptionTransactionReportDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public List getTransactionReport(long rental,Date start,Date end,long office) throws Exception{
		List resultList=new ArrayList();
		try {
			String cdn="";
			begin();
			if(rental!=0) {
				cdn+=" and a.subscription.subscription.id="+rental;
			}
			resultList=getSession().createQuery("from SubscriptionPaymentModel a where a.payment_date between :start and :end " +
					"and a.subscription.subscription.subscription_type.officeId=:office "+cdn+" order by a.payment_date")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		return resultList;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getExpenditure(long rental,Date start,Date end,long office) throws Exception{
		List resultList=new ArrayList();
		try {
			String cdn="";
			String cndn="";
			begin();
			if(rental!=0) {
				cndn+=" and a.subscription="+rental;
			}
			resultList.addAll(getSession().createQuery("from PaymentDepositModel a where a.type=:type and a.date between :start and :end and a.office_id=:office"+cndn+" order by a.date")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("type",Long.parseLong(SConstants.RENTAL_EXPENDETURE+"")).list());
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		return resultList;
	}
	
	
}
