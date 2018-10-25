package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 *   Inventory
 *   Nov 21, 2013
 */
public class PaymentReportDao extends SHibernate implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5019482696995632704L;

		
	public List<Object> getSupplierPaymnetReport(long officeId, long supl_id, Date frmDt, Date toDt,boolean active) throws Exception {
		
		List<Object> list = null;
		try {
			
			String criteria="";
			if(supl_id!=0)
				criteria=" and a.to_account_id="+supl_id;
			
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(a.date,a.to_account_id, (select b.name from LedgerModel b where b.id=a.to_account_id), " +
							"(select c.name from LedgerModel c where c.id=a.from_account_id),a.payment_amount,a.currency.code, a.description,cast(a.cheque_date as string),a.cash_or_check,a.id,cast(a.date as string),concat(a.fromDate ,' - ', a.toDate))" +
							" from PaymentModel a where a.office.id=:ofc and a.type=:type and a.date between :frm and :to  and a.active=:act "+criteria+" order by a.date desc,a.payment_id desc")
					.setParameter("ofc", officeId).setParameter("type", SConstants.SUPPLIER_PAYMENTS)
					.setParameter("frm", frmDt).setParameter("to", toDt).setParameter("act", active).list();

			commit();
		} catch (Exception e) {
			list = new ArrayList<Object>();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	
	public List<Object> getCustomerReceiptReport(long officeId, long cust_id, Date frmDt, Date toDt,boolean active) throws Exception {
		
		List<Object> list = null;
		try {
			
			String criteria="";
			if(cust_id!=0)
				criteria=" and a.from_account_id="+cust_id;
			
			begin();
			list = getSession()
					.createQuery("select new com.webspark.bean.ReportBean(a.date,a.from_account_id, (select b.name from LedgerModel b where b.id=a.from_account_id)," +
							" (select c.name from LedgerModel c where c.id=a.to_account_id),a.payment_amount,a.currency.code, a.description,cast(a.cheque_date as string),a.cash_or_check,a.id,cast(a.date as string),concat(a.fromDate ,' - ', a.toDate))" +
							" from PaymentModel a where a.office.id=:ofc and a.type=:type and a.date between :frm and :to and a.active=:act "+criteria+" order by a.to_account_id")
					.setParameter("ofc", officeId).setParameter("type", SConstants.CUSTOMER_PAYMENTS)
					.setParameter("frm", frmDt).setParameter("to", toDt).setParameter("act", active).list();
			
			commit();
		} catch (Exception e) {
			list = new ArrayList<Object>();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	
	public List<Object> getTransportationPaymentReport(long officeId, long cust_id, Date frmDt, Date toDt) throws Exception {
		
		List<Object> list = null;
		try {
			
			String criteria="";
			if(cust_id!=0)
				criteria=" and a.transportation_id="+cust_id;
			
			begin();
			
			list = getSession().createQuery("select new com.webspark.bean.ReportBean(a.date,a.id, (select b.name from LedgerModel b where b.id=a.transportation_id), " +
							"'',a.payment_amount,a.currency.code, a.description, a.type,cast(a.payment_id as string),cast(a.cheque_date as string),cast(a.date as string),concat(a.from_date ,' - ', a.to_date)) " +
							"from TransportationPaymentModel a where a.office.id=:ofc and a.date between :frm and :to and a.active=true "+criteria+" order by a.date desc, a.payment_id desc")
					.setParameter("ofc", officeId).setParameter("frm", frmDt).setParameter("to", toDt).list();
			
			commit();
		} catch (Exception e) {
			list = new ArrayList<Object>();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	
	
}
