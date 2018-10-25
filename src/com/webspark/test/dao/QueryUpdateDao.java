package com.webspark.test.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class QueryUpdateDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6439037979804708220L;
	private List resultList=new ArrayList();
	
	
	
	public void updateCustomerLedgerBalance(Date date, long office_id) throws Exception {
		
		long ledger_id=0;
		try {
			
			begin();
			double bal=0;
			double saleAmt=0,payment=0,returns=0;
			
			Iterator itr1 = getSession()
					.createQuery(
							"select ledger.id from CustomerModel where ledger.office.id=:ofc and ledger.status=:val")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			
			while (itr1.hasNext()) {
				ledger_id=(Long) itr1.next();
				
				bal=0; saleAmt=0; payment=0; returns=0;
				
				saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from SalesModel where date <=:stdt and customer.id=:led " +
						"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();

				payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <=:stdt and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", date).setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
				
				returns=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
					+ " from SalesReturnModel where status=1 and date <=:stdt  and customer.id=:led")
					.setParameter("stdt", date).setLong("led", ledger_id).uniqueResult();
				
				bal=saleAmt-payment-returns;
				
				getSession().createQuery("update LedgerModel set current_balance=:bal where id=:id")
						.setLong("id", ledger_id).setDouble("bal", bal).executeUpdate();
				
				flush();
				
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
	}
	
	
	
	public void updateSupplierLedgerBalance(Date date, long office_id) throws Exception {
		try {
			
			begin();
			
			long ledger_id=0;
			double bal=0;
			double purchase=0,payment=0,returns=0;
			
			Iterator itr1 = getSession()
					.createQuery("select ledger.id from SupplierModel where ledger.office.id=:ofc and ledger.status=:val")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			
			while (itr1.hasNext()) {
				ledger_id=(Long) itr1.next();
				
				bal=0; purchase=0; payment=0; returns=0;
				
				purchase=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where date <=:stdt and supplier.id=:led")
						.setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				
				payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <=:stdt and " +
						"to_account_id=:led and type=:typ").setLong("led", ledger_id).setDate("stdt", date)
						.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();

				returns=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
					+ " from SalesReturnModel where status=1 and date <=:stdt  and customer.id=:led")
					.setParameter("stdt", date).setLong("led", ledger_id).uniqueResult();
				
				bal=purchase-payment-returns;
				
				getSession().createQuery("update LedgerModel set current_balance=:bal where id=:id")
						.setLong("id", ledger_id).setDouble("bal", -bal).executeUpdate();
				
				
				flush();
				
			}
			
			
			commit();
			
		} catch (Exception e) {
			// TODO: handle exception
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
	}
	
	
	
	
	
	

}
