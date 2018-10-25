package com.webspark.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PaymentInvoiceDao extends SHibernate implements Serializable {

	
	public long isPaymentPendingForSupplier(long supplier) throws Exception {
		long count=0;
		try {
			begin();
			count = (Long)getSession().createQuery("select count(id) from PurchaseModel where supplier.id=:supplier " +
									" and payment_status in (1,2,3) and active=true order by id desc")
					.setParameter("supplier", supplier).uniqueResult();
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
		return count;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> getAllPurchaseForSupplier(long supplier, Date fromDate, Date toDate, boolean isCreateNew) throws Exception {
		List resultList = new ArrayList();
		try {
			String criteria="";
			if(isCreateNew)
				criteria=" and payment_done='N' ";
			
			begin();
			
			resultList = getSession().createQuery("select new com.inventory.purchase.model.PurchaseModel(id, " +
												"concat(purchase_no, '  - ', date,'(',(amount - expenseAmount) + " +
												"(expenseAmount - expenseCreditAmount) + debit_note- credit_note- " +
												" paymentAmount - paid_by_payment,')', ' ', netCurrencyId.code))" +
												" from PurchaseModel where supplier.id=:supplier and date between :fromDate and :toDate" +
												" and payment_status in (1,2,3) and active=true "+criteria+" order by id desc")
					.setParameter("supplier", supplier).setParameter("fromDate", fromDate).setParameter("toDate", toDate).list();
			
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
	
	
	public long isPaymentPendingForCustomer(long customer) throws Exception {
		long count=0;
		try {
			begin();
			count = (Long)getSession().createQuery("select count(id) from SalesModel where customer.id=:customer " +
									" and status in (1,2,3) and active=true order by id desc")
					.setParameter("customer", customer).uniqueResult();
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
		return count;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> getAllSalesForCustomer(long customer, Date fromDate, Date toDate, boolean isCreateNew) throws Exception {
		List resultList = new ArrayList();
		try {
			String criteria="";
			if(isCreateNew)
				criteria=" and payment_done='N' ";
			
			begin();
			
			resultList = getSession().createQuery("select new com.inventory.sales.model.SalesModel(id, " +
					"concat(sales_number, '  - ', date,'(',(amount-expenseAmount)+(expenseAmount-expenseCreditAmount)+debit_note-credit_note-payment_amount-paid_by_payment,')', ' ', netCurrencyId.code ))"
									+ " from SalesModel where customer.id=:customer and date between :fromDate and :toDate" +
									" and status in (1,2,3) and active=true "+criteria+" order by id desc")
					.setParameter("customer", customer).setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			
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
	
}
