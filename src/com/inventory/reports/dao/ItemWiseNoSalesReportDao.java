package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ItemWiseNoSalesReportDao extends SHibernate implements Serializable{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getNotSoldItems(long item,Date start,Date end,long office) throws Exception{
		List resultList=new ArrayList();
		List mapList=new ArrayList();
		try{
			begin();
			String condition="";
			if(item!=0)
				condition+=" and b.item.id="+item;
			List saleList=getSession().createQuery("select distinct(a) from SalesModel a join a.inventory_details_list b where a.date between :start and :end"+condition+" and a.office.id=:office order by a.date")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			System.out.println("Sales Count "+saleList.size());
			if(saleList.size()>0){
				Iterator saleitr=saleList.iterator();
				while (saleitr.hasNext()) {
					SalesModel sales = (SalesModel) saleitr.next();
					mapList.addAll(getSession().createQuery("select a.stockId from SalesStockMapModel a where a.stockId>0 and a.salesId="+sales.getId()).list());
				}
			}
			System.out.println("Map List Count "+mapList.size());
			resultList=getSession().createQuery("select a from ItemStockModel a where a.balance>0 and a.manufacturing_date<:end and a.id not in (:lst)")
					.setParameter("end", end).setParameterList("lst",mapList).list();
			System.out.println("Result List Count "+resultList.size());
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
		return resultList;
	}
	
	/*public String getLastSoldDate(long stkid) throws Exception{
		String date="";
		try{
			getSession().createQuery("select salesId from SalesStockMapModel where stockId=:stkid and id=( select max(id) from SalesStockMapModel where stockId=:stkid )")
				.setParameter("stkid", stkid).uniqueResult();
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return date;
	}*/
	
}
