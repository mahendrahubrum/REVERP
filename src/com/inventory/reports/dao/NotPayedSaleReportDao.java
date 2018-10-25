package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;


/**
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Oct 2, 2013
 */
public class NotPayedSaleReportDao extends SHibernate implements Serializable{
	
	private static final long serialVersionUID = 8656961481811739178L;
	
	
	public List<Object> getAllSalesDetailsForCustomer(long ledgerId,Date fromDate, Date last_payable_date, long office_id, long org_id) throws Exception {
		List resultList=new ArrayList();
		try {
			String condn="";
			if(ledgerId!=0)
				condn+=" and customer.id="+ledgerId;
			else if(office_id!=0)
				condn+=" and office.id="+office_id;
			else
				condn+=" and office.organization.id="+org_id;
			
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.sales.model.SalesModel(payment_amount,amount,paid_by_payment)"
									+ " from SalesModel where customer.id=:custId and date between :fromDate and :lastdate"+condn +
									" and payment_done!='Y' and status!=1 and active=true")
					.setParameter("custId", ledgerId)
					.setParameter("fromDate", fromDate)
					.setParameter("lastdate", last_payable_date).list();
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
		return resultList;
	}
	
	
	
	public List<Object> getAllPurchaseDetailsForSupplier(long ledgerId,Date fromDate, Date last_payable_date, long office_id, long org_id) throws Exception {
		List resultList=new ArrayList();
		try {
			String condn="";
			if(ledgerId!=0)
				condn+=" and supplier.id="+ledgerId;
			else if(office_id!=0)
				condn+=" and office.id="+office_id;
			else
				condn+=" and office.organization.id="+org_id;
			
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.purchase.model.PurchaseModel(payment_amount,amount,paid_by_payment)"
									+ " from PurchaseModel where date between :fromDate and :lastdate"+condn +
									" and payment_done!='Y' and status!=1 and active=true")
					.setParameter("fromDate", fromDate)
					.setParameter("lastdate", last_payable_date).list();
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
		return resultList;
	}
	
	
	
	public List<Object> getDetailedNotPaidSales(long ledgerId,
			Date fromDate, Date last_payable_date, long office_id, long org_id) throws Exception {
		List resultList=new ArrayList();
		try {
			
			String condn="";
			
			if(ledgerId!=0)
				condn+=" and customer.id="+ledgerId;
			else if(office_id!=0)
				condn+=" and office.id="+office_id;
			else
				condn+=" and office.organization.id="+org_id;
			
			begin();
			
			resultList = getSession()
					.createQuery("select new com.inventory.sales.model.SalesModel(sales_number, date, payment_amount,amount, credit_period, paid_by_payment)"
									+ " from SalesModel where date between :fromDate and :lastdate"+condn +
									" and payment_done!='Y' and status!=1 and active=true")
					.setParameter("fromDate", fromDate)
					.setParameter("lastdate", last_payable_date).list();
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
		return resultList;
	}
	
	
	
	
	public List<Object> getDetailedNotPaidPurchase(long ledgerId,
			Date fromDate, Date last_payable_date, long office_id, long org_id) throws Exception {
		List resultList=new ArrayList();
		try {
			
			String condn="";
			
			if(ledgerId!=0)
				condn+=" and supplier.id="+ledgerId;
			else if(office_id!=0)
				condn+=" and office.id="+office_id;
			else
				condn+=" and office.organization.id="+org_id;
			
			begin();
			
			resultList = getSession()
					.createQuery("select new com.inventory.purchase.model.PurchaseModel(purchase_number, date, payment_amount,amount, credit_period, paid_by_payment)"
									+ " from PurchaseModel where date between :fromDate and :lastdate"+condn +
									" and payment_done!='Y' and status!=1 and active=true")
					.setParameter("fromDate", fromDate)
					.setParameter("lastdate", last_payable_date).list();
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
		return resultList;
	}
	
	
	
	public List<Object> getSalesDetailsForCustomer(long ledgerId, Date start, Date end, long office_id) throws Exception {
		List resultList=new ArrayList();
		try {
			String condn="";
			if(ledgerId!=0)
				condn+=" and customer.id="+ledgerId;
			if(office_id!=0)
				condn+=" and office.id="+office_id;
			begin();
			
			resultList=getSession().createQuery("from SalesModel where date between :fromDate and :lastdate"+condn +
									" and payment_done!='Y' and status!=1 and active=true")
									.setParameter("fromDate", start)
									.setParameter("lastdate", end).list();
			
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
		return resultList;
	}
	
	
	public List<Object> getPurchaseDetailsForSupplier(long ledgerId, Date start, Date end, long office_id) throws Exception {
		List resultList=new ArrayList();
		try {
			String condn="";
			if(ledgerId!=0)
				condn+=" and supplier.id="+ledgerId;
			if(office_id!=0)
				condn+=" and office.id="+office_id;
			begin();
			
			resultList=getSession().createQuery("from PurchaseModel where date between :fromDate and :lastdate"+condn +
									" and payment_done!='Y' and status!=1 and active=true")
									.setParameter("fromDate", start)
									.setParameter("lastdate", end).list();
			
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
		return resultList;
	}

}
