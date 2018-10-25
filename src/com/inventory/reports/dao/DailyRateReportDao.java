package com.inventory.reports.dao;

import java.sql.Date;
import java.util.List;

import com.webspark.dao.SHibernate;

public class DailyRateReportDao extends SHibernate {

	private static final long serialVersionUID = 6670662479044165686L;

	public List getDailyRateReport(long item_id, long cust_id, Date fromDate, Date toDate, long ofc_id)
			throws Exception {
		
		List resultList=null ;
		try {
			String criteria="";
			
			if(cust_id!=0)
				criteria+=" and a.customer_id="+cust_id;
			
			if(cust_id==0) {
				criteria+=" and a.office_id="+ofc_id;
			}
			
			if(item_id!=0) {
				criteria+=" and b.item="+item_id;
			}
			
			begin();
			
			resultList=getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean((select name from LedgerModel where id=a.customer_id), " +
																	"(select first_name from UserModel where loginId.id=a.login_id), " +
																	"a.date, " +
																	"(select name from ItemModel where id=b.item), " +
																	"(select symbol from UnitModel where id=b.unit)," +
																	" b.rate) from ItemDailyRateModel a join a.daily_rate_list b where a.date between :stdt and :enddt "+criteria+" order by a.customer_id")
					.setParameter("stdt", fromDate).setParameter("enddt", toDate).list();
			
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public List showDailyRateReport(long item_id, long cust_id, Date fromDate, Date toDate, long ofc_id)
			throws Exception {
		
		List resultList=null ;
		try {
			String criteria="";
			
			if(cust_id!=0)
				criteria+=" and a.customer_id="+cust_id;
			
			if(cust_id==0) {
				criteria+=" and a.office_id="+ofc_id;
			}
			
			if(item_id!=0) {
				criteria+=" and b.item="+item_id;
			}
			
			begin();
			
			resultList=getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean((select name from LedgerModel where id=a.customer_id), " +
																	"(select first_name from UserModel where loginId.id=a.login_id), " +
																	"a.date, " +
																	"(select name from ItemModel where id=b.item), " +
																	"(select symbol from UnitModel where id=b.unit)," +
																	" b.rate,a.customer_id) from ItemDailyRateModel a join a.daily_rate_list b where a.date between :stdt and :enddt "+criteria+" order by a.customer_id")
					.setParameter("stdt", fromDate).setParameter("enddt", toDate).list();
			
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return resultList;
	}


}
