package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */

public class ContractorLedgerReportDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8008091043487506867L;
	private List resultList=new ArrayList();
	
	@SuppressWarnings("unchecked")
	public List getCustomerLedgerReport(Date start_date, Date end_date, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Work Order',date,amount,0.0) from WorkOrderModel where date between :stdt and :enddt and contractor.id=:led "
										).setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CONTRACTOR_PAYMENTS).list());
			
			
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
	
	@SuppressWarnings("unchecked")
	public List showCustomerLedgerReport(Date start_date, Date end_date, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean" +
					"('Work Order',date,amount,0.0,id) from WorkOrderModel where date between :stdt and :enddt and contractor.id=:led "
										).setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean" +
					"('Receipt',date,0.0,payment_amount,id) from PaymentModel where date between :stdt and :enddt and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CONTRACTOR_PAYMENTS).list());
			
			
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
			
			begin();
			
			Object objttl1=getSession().createQuery("select sum(amount) from WorkOrderModel where date<:stdt and contractor.id=:led "
										).setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			
			
			Object objttl2=getSession().createQuery("select sum(payment_amount) from PaymentModel where date<:stdt and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setParameter("typ", SConstants.CONTRACTOR_PAYMENTS).uniqueResult();
			
			if(objttl1!=null)
				op_bal+=(Double)objttl1;
			
			if(objttl2!=null)
				op_bal-=(Double)objttl2;
			
			
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
