package com.inventory.tailoring.report.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class TailoringCustomerLedgerReportDao extends SHibernate implements Serializable{
	
	/**
	 * @author Jinshad P.T.
	 * 
	 *         Dec 22, 2014
	 */
	private static final long serialVersionUID = 6439037979804708220L;
	private List resultList=new ArrayList();
	
	
	@SuppressWarnings("unchecked")
	public List getCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale',date,amount,payment_amount,sales_number) from TailoringSalesModel where date between :stdt and :enddt and customer.id=:led and active=true" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and active=true and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Sal Return',date,  amount, -(payment_amount))"
					+ " from SalesReturnModel where active=true and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
					.list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Subscription',payment_date,0.0,amount_paid) from SubscriptionPaymentModel where payment_date between :stdt and :enddt and " +
										"from_account=:led").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.list());
			
			commit();
			
			return resultList;
			
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
	
	
	
	public double getSalesOpeningBalance(Date date, long office_id, long ledger_id) throws Exception {
		double bal=0;
		try {
			
			begin();
			double comnSale=0;
			try {
				comnSale=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) from CustomerCommissionSalesModel a join a.details_list b where a.date<:stdt and b.customer.id=:led" +
											"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			double saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from TailoringSalesModel where date <:stdt and customer.id=:led and active=true " +
										"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
			
			double payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <:stdt and " +
										"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
										setDate("stdt", date).setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
			
			double returns=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
					+ " from SalesReturnModel where date <:stdt  and customer.id=:led and active=true")
					.setParameter("stdt", date).setLong("led", ledger_id).uniqueResult();
			
			payment+=(Double) getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date <:stdt and " +
					"from_account=:led ").setLong("led", ledger_id).
					setDate("stdt", date).uniqueResult();
			
			commit();
			
			bal=saleAmt+comnSale-payment-returns;
			
			return bal;
			
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
	
	
	public double getSalesCurrentBalance(Date date, long office_id, long ledger_id) throws Exception {
		double bal=0;
		try {
			
			begin();
			
			double comnSale=0;
			try {
				comnSale=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) from CustomerCommissionSalesModel a join a.details_list b where a.date<:stdt and b.customer.id=:led" +
											"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
			} catch (Exception e) {
			}
			
			double saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from TailoringSalesModel where customer.id=:led and active=true " +
										"").setLong("led", ledger_id).uniqueResult();
			
			double payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where " +
										"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
										setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
			
			double returns=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
					+ " from SalesReturnModel where customer.id=:led and active=true")
					.setLong("led", ledger_id).uniqueResult();
			
			commit();
			
			bal=saleAmt+comnSale-payment-returns;
			
			return bal;
			
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
	
	
	
	
	
	public double getOpeningBalance(Date start_date, long ledger_id) throws Exception {
		
		double op_bal=0;
		
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			Object objDr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
					"b.toAcct.id =:led)").setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			Object objCr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
					"b.fromAcct.id =:led)").setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			
//			Object objCr1=getSession().createQuery("select a from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
//					"b.fromAcct.id =:led)").setLong("led", ledger_id).setDate("stdt", start_date).list();
			
			
			if(objDr!=null)
				op_bal+=(Double)objDr;
			
			if(objCr!=null)
				op_bal-=(Double)objCr;
			
			
			commit();
			
			return op_bal;
			
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
