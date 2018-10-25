package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class LaundryCustomerLedgerReportDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6439037979804708220L;
	private List resultList=new ArrayList();
	
	public List getCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Sale',date,amount,payment_amount) from LaundrySalesModel where date between :stdt and :enddt and customer.id=:led " +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean('Sal Return',date,  amount, -(payment_amount))"
					+ " from SalesReturnModel where status=1 and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
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
	
	
	
	public List getNewCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale',date,amount,payment_amount) from LaundrySalesModel where date between :stdt and :enddt and customer.id=:led " +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Sal Return',date,  amount, -(payment_amount))"
					+ " from SalesReturnModel where status=1 and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
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
	
	
	public List getLaundryCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale',date,amount,payment_amount,sales_number) from LaundrySalesModel where date between :stdt and :enddt and customer.id=:led  and active=true" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Receipt',date,0.0,payment_amount, case when cash_or_check=2 then chequeNo else '' end) from PaymentModel where date between :stdt and :enddt and " +
										"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Sal Return',date,  amount, -(payment_amount))"
					+ " from SalesReturnModel where status=1 and active=true and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
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
