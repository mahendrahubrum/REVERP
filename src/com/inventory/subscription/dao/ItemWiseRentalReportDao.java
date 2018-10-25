package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ItemWiseRentalReportDao extends SHibernate implements Serializable {

	public List getItemWiseRentalReport(long office,long ledger,long rental,Date start,Date end)throws Exception{
		List list=new ArrayList();
		try{
			String cndn="";
			begin();
			if(ledger!=0)
				cndn+=" and a.customer.id="+ledger;
			if(rental!=0)
				cndn+=" and b.rental.id="+rental;
			list=getSession().createQuery("select new com.inventory.subscription.bean." +
										"ItemWiseRentalReportBean(a.id,cast(a.date as string),b.rental.name,a.customer.name," +
										"b.qunatity,b.unit_price,a.amount,a.sales_number) from RentalTransactionModel a join a.inventory_details_list b " +
										"where a.office.id=:office and a.date between :start and :end "+cndn+" order by b.rental.name")
										.setParameter("office", office).setParameter("start", start).setParameter("end", end).list();
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
	
}
