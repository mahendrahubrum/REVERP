package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class RentalReportDao extends SHibernate implements Serializable {

	public List getRentalReport(long customer,long office,Date start,Date end,long account)throws Exception{
		List list=new ArrayList();
		try{
			String cndn="";
			begin();
			if(customer!=0)
				cndn+=" and customer.id="+customer;
			
			if(account==1){
				cndn+=" and rent_type="+2;
			}
			else{
				cndn+=" and rent_type="+1;
			}
			
			list=getSession().createQuery(" from RentalTransactionModel where office.id=:office and date between :start and :end  "+cndn+" order by id")
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
