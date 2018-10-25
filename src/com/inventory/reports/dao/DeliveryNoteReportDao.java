package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 28, 2013
 */
public class DeliveryNoteReportDao extends SHibernate implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7624431272022732854L;


	@SuppressWarnings("unchecked")
	public List<Object> getSalesDetailsCustomer(long salesNo, long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = "";
			if (salesNo != 0) {
				condition += " and id=" + salesNo;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from DeliveryNoteModel where date>=:fromDate and date<=:toDate and active=true"
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
	
	@SuppressWarnings("unchecked")
	public List<Object> getSalesDetailsSalesMan(long salesNo, long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = "";
			if (salesNo != 0) {
				condition += " and id=" + salesNo;
			}
			if (custId != 0) {
				condition += " and responsible_employee=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from DeliveryNoteModel where date>=:fromDate and date<=:toDate and active=true"
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
				condition += " where office.id=" + officeId+" and active=true";
			}
			else
				condition += " where active=true";
			
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.DeliveryNoteModel(id,deliveryNo )"
									+ " from DeliveryNoteModel "+condition).list();
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
	
	public List getAllDNNumbersForCustomer(long officeId, long cust_id,Date from_date, Date to_date) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(a.id, a.deliveryNo) from DeliveryNoteModel a" +
					" where a.office.id = :office_id" +
					" and a.customer.id = :cust_id" +
					" and date between :from_date and :to_date" +
					" order by a.id")
						.setParameter("office_id", officeId)
						.setParameter("cust_id", cust_id)
						.setParameter("from_date", from_date)
						.setParameter("to_date", to_date).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public List getAllDNNumbersForSalesMan(long officeId, long salesman,Date from_date, Date to_date) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(a.id, a.deliveryNo) from DeliveryNoteModel a" +
					" where a.office.id = :office_id" +
					" and a.responsible_employee = :salesman" +
					" and date between :from_date and :to_date" +
					" order by a.id")
					.setParameter("office_id", officeId)
					.setParameter("salesman", salesman)
					.setParameter("from_date", from_date)
					.setParameter("to_date", to_date).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return list;
	}
}
