package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.CashAccountPaymentModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class SupplierLedgerReportDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -652923603119519095L;
	private List<?> resultList=new ArrayList();
	
	@SuppressWarnings("unchecked")
	public List getSupplieLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					" 'Purchase',date,amount,payment_amount, purchase_number) from PurchaseModel where date between :stdt and :enddt and supplier.id=:led and office.id=:ofc" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).setLong("ofc", office_id).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and " +
										"to_account_id=:led and type=:typ  and office.id=:ofc").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).setLong("ofc", office_id).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean('Purch Return',date,  amount, -(payment_amount))"
					+ " from PurchaseReturnModel where status in (1,2) and " +
					"date between :stdt and :enddt  and supplier.id=:led   and office.id=:ofc").setLong("ofc", office_id)
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
					.list());
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Subscription',payment_date,0.0,amount_paid,id) from SubscriptionPaymentModel where payment_date between :stdt and :enddt and " +
										"from_account=:led").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.list());
			*/
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
	public List getNewSupplierLedgerReport(Date start_date, Date end_date, long office_id, 
			long ledger_id, boolean useToDt ) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			String dat="date";
			if(useToDt)
				dat="toDate";
			
			begin();
			
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Purchase',date,amount,payment_amount,purchase_number) from PurchaseModel where date between :stdt and :enddt and supplier.id=:led and office.id=:ofc" +
					" and active=true ").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).setLong("ofc", office_id).list());
			*/
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Purchase',date,amount,paymentAmount,purchase_no) from PurchaseModel where date between :stdt and :enddt and supplier.id=:led and office.id=:ofc" +
					" and active=true ").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).setLong("ofc", office_id).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Purchase Return',date,  amount, -(paid_by_payment))"
					+ " from PurchaseReturnModel where active=true and " +
					"date between :stdt and :enddt  and supplier.id=:led  and office.id=:ofc").setLong("ofc", office_id)
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
					.list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"b.id,'Cash Account Payment',a.date,0.0,b.amount,b.bill_no) from CashAccountPaymentModel a join a.cash_account_payment_list b where a.date between :stdt and :enddt and a.active=true and " +
										"b.account.id=:led").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"b.id,'Bank',a.date,0.0,b.amount,b.bill_no) from BankAccountPaymentModel a join a.bank_account_payment_list b where a.date between :stdt and :enddt and a.active=true and " +
										"b.account.id=:led").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"b.id,'PDC Payment',a.date,0.0,b.amount,b.bill_no) from PdcPaymentModel a join a.pdc_payment_list b where a.date between :stdt and :enddt and a.active=true and " +
										"b.to_id=:led").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			try {
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Debit Note',a.date,0.0,sum(b.amount),a.bill_no,a.ref_no) from DebitNoteModel a join a.debit_note_list b where a.date between :stdt and :enddt and a.active=true and " +
											"a.ledger.id=:led group by a.id").setLong("led", ledger_id)
											.setDate("stdt", start_date).setDate("enddt", end_date).list());
				} catch (Exception e) {}
				try {
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Credit Note',a.date,sum(b.amount),0.0,a.bill_no,a.ref_no) from CreditNoteModel a join a.credit_note_list b where a.date between :stdt and :enddt and a.active=true and " +
						"a.ledger.id=:led group by a.id").setLong("led", ledger_id)
						.setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				} catch (Exception e) {}
			
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Payment',date,0.0,payment_amount) from PaymentModel where "+dat+" between :stdt and :enddt and " +
										"to_account_id=:led and type=:typ and active=true  and office.id=:ofc").setLong("ofc", office_id).setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).list());*/
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Subscription',payment_date,0.0,amount_paid) from SubscriptionPaymentModel where payment_date between :stdt and :enddt and " +
										"to_account=:led").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.list());*/
			
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
	
	
	
	public double getPurchaseOpeningBalance(Date date, long office_id, long ledger_id, boolean useToDt) throws Exception {
		double bal=0;
		try {
			
			String dat="date";
			if(useToDt)
				dat="toDate";
			
			begin();
			
			double comnSal=0;
			try {
				comnSal=(Double) getSession().createQuery("select coalesce(sum(net_sale-commission),0) from CommissionSalesModel where received_date<:stdt and supplier.id=:led " +
											"  and office.id=:ofc").setLong("led", ledger_id).setDate("stdt", date).setLong("ofc", office_id).uniqueResult();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			double purchase=(Double) getSession().createQuery("select coalesce(sum(amount-paymentAmount),0) from PurchaseModel where date <:stdt and supplier.id=:led and active=true  and office.id=:ofc")
					.setLong("led", ledger_id).setDate("stdt", date).setLong("ofc", office_id).uniqueResult();
			
			double payments=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where "+dat+" <:stdt and " +
										"to_account_id=:led and type=:typ and active=true   and office.id=:ofc").setLong("led", ledger_id).setDate("stdt", date)
										.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).setLong("ofc", office_id).uniqueResult();
			
			/*double returns= (Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseReturnModel where status in (1,2) and date <:stdt and supplier.id=:led and active=true  and office.id=:ofc")
					.setParameter("stdt", date).setLong("led", ledger_id).setLong("ofc", office_id).uniqueResult();*/
			
			double returns= (Double) getSession().createQuery("select coalesce(sum(amount-paid_by_payment),0) from PurchaseReturnModel where status in (1,2) and date <:stdt and supplier.id=:led and active=true  and office.id=:ofc")
					.setParameter("stdt", date).setLong("led", ledger_id).setLong("ofc", office_id).uniqueResult();
			
			payments+=(Double) getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date <:stdt and " +
					"to_account=:led ").setLong("led", ledger_id).
					setDate("stdt", date).uniqueResult();
			
			commit();
			
			bal=purchase+comnSal-payments-returns;
			
			return bal;
			
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
	
	
	public double getPurchaseCurrentBalance(Date date, long office_id, long ledger_id) throws Exception {
		double bal=0;
		try {
			
			
			begin();
			
			double comnSal=0;
			try {
				comnSal=(Double) getSession().createQuery("select coalesce(sum(net_sale-commission),0) from CommissionSalesModel where received_date<:stdt and supplier.id=:led " +
											" and office.id=:ofc").setLong("led", ledger_id).setDate("stdt", date).setLong("ofc", office_id).uniqueResult();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			/*double purchase=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where supplier.id=:led and active=true and office.id=:ofc")
					.setLong("led", ledger_id).setLong("ofc", office_id).uniqueResult();*/
			
			double purchase=(Double) getSession().createQuery("select coalesce(sum(amount-paymentAmount),0) from PurchaseModel where supplier.id=:led and active=true and office.id=:ofc")
					.setLong("led", ledger_id).setLong("ofc", office_id).uniqueResult();
			
			
			double cashPayment=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from CashAccountPaymentModel a join a.cash_account_payment_list b  where " +
					"b.account.id=:led and a.active=true").setLong("led", ledger_id).uniqueResult();
			
			double bankPayment=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from BankAccountPaymentModel a join a.bank_account_payment_list b  where " +
					"b.account.id=:led and a.active=true").setLong("led", ledger_id).uniqueResult();
			
			double pdcPayment=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from PdcPaymentModel a join a.pdc_payment_list b where " +
					"b.to_id=:led and a.active=true").setLong("led", ledger_id).uniqueResult();
			
			/*double payments=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where " +
										"to_account_id=:led and type=:typ and active=true and office.id=:ofc").setLong("led", ledger_id)
										.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).setLong("ofc", office_id).uniqueResult();*/
			
			/*double returns= (Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseReturnModel where status in (1,2) and supplier.id=:led and active=true and office.id=:ofc")
							.setLong("led", ledger_id).setLong("ofc", office_id).uniqueResult();*/
			
			double returns= (Double) getSession().createQuery("select coalesce(sum(amount-paid_by_payment),0) from PurchaseReturnModel where  supplier.id=:led and active=true and office.id=:ofc")
					.setLong("led", ledger_id).setLong("ofc", office_id).uniqueResult();
			
			
			commit();
			
			bal=purchase+comnSal-cashPayment-bankPayment-pdcPayment-returns;
			
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
