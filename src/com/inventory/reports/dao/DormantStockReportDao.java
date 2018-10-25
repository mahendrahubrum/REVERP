package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 27, 2014
 */
public class DormantStockReportDao extends SHibernate{

	private List resultList;

	public List getDormantStockReport(Date date,
			int interval, int noOfintrvls, long officeID,long itemId) throws Exception {
		

		try {
			List intrvalList;
			Calendar cal=Calendar.getInstance();
			Date start_date;
			Date prev_date;
			AcctReportMainBean rptObj;
			boolean valid=false;
			
			resultList=new ArrayList();
			
			begin();
			
			String cond="";
			if(itemId!=0)
				cond+=" and id="+itemId;
			
			Iterator itr = getSession().createQuery(
							" from ItemModel  where status=:val and  office.id=:ofc "+cond+" order by name")
									.setLong("ofc", officeID).setParameter("val", SConstants.statuses.ITEM_ACTIVE).list().iterator();
			ItemModel obj;
			double todayBal=0;
			while(itr.hasNext()) {
				
				cal.setTime(date);
				start_date=date;
				prev_date=date;
				
				obj=(ItemModel) itr.next();
				
				todayBal=0;
				
				rptObj=new AcctReportMainBean();
				rptObj.setName(obj.getName());
				
				intrvalList=new ArrayList();
				for (int i = 0; i < noOfintrvls; i++) {
					cal.add(Calendar.DAY_OF_MONTH, -interval);
					start_date=new Date(cal.getTime().getTime());
					
					todayBal=(Double) getSession()
							.createQuery("select coalesce(sum(balance),0) from ItemStockModel where manufacturing_date between :stdt and :enddt and item.id=:led " +
												"").setLong("led", obj.getId()).setDate("stdt", start_date).setDate("enddt", prev_date).uniqueResult();

					
					intrvalList.add(todayBal);
					
					prev_date=start_date;
				}
				rptObj.setSubList(intrvalList);
				
				resultList.add(rptObj);
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
	}

}
