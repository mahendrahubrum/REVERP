package com.webspark.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.CommonUtil;
import com.webspark.model.AddressModel;

/**
 * @author Anil
 * 
 *         Jun 7, 2013
 */
public class HomePageDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -3686640884363647030L;

	public long getTotalSalesCount(Date fromDate,Date toDate,long officeId) throws Exception {
		long count =0;
		try {
			begin();
			count=(Long) getSession().createQuery("select coalesce(count(id),0) from SalesModel" +
					" where  date between :frm and :to and office.id=:ofc").setParameter("ofc", officeId).setParameter("frm", fromDate).setParameter("to", toDate).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return count;
	}
	public long getTotalSalesReturnCount(Date fromDate,Date toDate,long officeId) throws Exception {
		long count =0;
		try {
			begin();
			count=(Long) getSession().createQuery("select coalesce(count(id),0) from SalesReturnModel" +
					" where  date between :frm and :to and office.id=:ofc").setParameter("ofc", officeId).setParameter("frm", fromDate).setParameter("to", toDate).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return count;
	}
	public long getTotalPurchaseCount(Date fromDate,Date toDate,long officeId) throws Exception {
		long count =0;
		try {
			begin();
			count=(Long) getSession().createQuery("select coalesce(count(id),0) from PurchaseModel" +
					" where  date between :frm and :to and office.id=:ofc").setParameter("ofc", officeId).setParameter("frm", fromDate).setParameter("to", toDate).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return count;
	}
	public long getTotalPurchaseReturnCount(Date fromDate,Date toDate,long officeId) throws Exception {
		long count =0;
		try {
			begin();
			count=(Long) getSession().createQuery("select coalesce(count(id),0) from PurchaseReturnModel" +
					" where  date between :frm and :to and office.id=:ofc").setParameter("ofc", officeId).setParameter("frm", fromDate).setParameter("to", toDate).uniqueResult();
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return count;
	}
}
