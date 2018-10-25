package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class WorkOrderReportDao extends SHibernate implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9090866037951008114L;


	public List<Object> getWODetails(long woNo, long contrId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> list = null;

		try {
			begin();
			
			String condition = "";
			if (woNo != 0) {
				condition += " and work_order_number=" + woNo;
			}
			if (contrId != 0) {
				condition += " and contractor.id=" + contrId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from WorkOrderModel where date>=:fromDate and date<=:toDate "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}

	
	public List getAllSalesNumbersAsComment(long officeId) throws Exception {
		List resultList=null;
		try {
			begin();
			String condition="";
			if (officeId != 0) {
				condition += " where office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.WorkOrderModel(id,cast(delivery_order_number as string) )"
									+ " from WorkOrderModel "+condition).list();
			commit();
		} catch (Exception e) {
			resultList=new ArrayList();
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
}
