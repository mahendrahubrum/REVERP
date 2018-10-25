package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class CustomerLedgerReportDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6439037979804708220L;
	private List resultList=new ArrayList();
	
	/*public List getCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Sale',date,amount,payment_amount) from SalesModel where date between :stdt and :enddt and customer.id=:led " +
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
	}*/
	
	
	
	@SuppressWarnings("unchecked")
	public List getCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			
			begin();
			
			try {
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Commission Sale',a.date,coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) ,0.0,a.sales_no) from CustomerCommissionSalesModel a join a.details_list b where a.date between :stdt and :enddt and b.customer.id=:led group by a.id" +
											"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			} catch (Exception e) {
				System.out.println(e);
				// TODO: handle exception
			}
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale',date,amount,payment_amount,sales_number) from SalesModel where date between :stdt and :enddt and customer.id=:led and active=true" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Rental',date,amount,payment_amount,sales_number) from RentalTransactionModel where date between :stdt and :enddt and customer.id=:led and active=true and rent_type=2" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and active=true and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Rental Receipt',date,0.0,payment_amount) from RentalPaymentModel where date between :stdt and :enddt and active=true and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.RENTAL_PAYMENTS).list());
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Sal Return',date,  amount, -(payment_amount))"
					+ " from SalesReturnModel where active=true and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
					.list());*/
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Sal Return',date,  amount, -(paid_by_payment))"
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
			
			double saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from SalesModel where date <:stdt and customer.id=:led and active=true " +
										"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
			
			double rentalAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from RentalTransactionModel where date <:stdt and customer.id=:led and active=true and rent_type=2" +
					"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
			
			double payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <:stdt and " +
										"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
										setDate("stdt", date).setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
			double renatlPayment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from RentalPaymentModel where date <:stdt and " +
					"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
					setDate("stdt", date).setParameter("typ", SConstants.RENTAL_PAYMENTS).uniqueResult();
			
			double returns=(Double) getSession().createQuery("select coalesce(sum(amount-paid_by_payment),0)"
					+ " from SalesReturnModel where date <:stdt  and customer.id=:led and active=true")
					.setParameter("stdt", date).setLong("led", ledger_id).uniqueResult();
			
			payment+=(Double) getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date <:stdt and " +
					"from_account=:led ").setLong("led", ledger_id).
					setDate("stdt", date).uniqueResult();
			
			commit();
			
			bal=saleAmt+rentalAmt+comnSale-payment-renatlPayment-returns;
			
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
				// TODO: handle exception
			}
			
			double saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from SalesModel where customer.id=:led and active=true " +
										"").setLong("led", ledger_id).uniqueResult();
			
			double cashDeposit=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from CashAccountDepositModel a join a.cash_account_deposit_list b  where " +
					"b.account.id=:led and a.active=true").setLong("led", ledger_id).uniqueResult();
			
			double bankDeposit=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from BankAccountDepositModel a join a.bank_account_deposit_list b  where " +
					"b.account.id=:led and a.active=true").setLong("led", ledger_id).uniqueResult();
			
			double pdcPayment=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from PdcPaymentModel a join a.pdc_payment_list b where " +
					"b.from_id=:led and a.active=true").setLong("led", ledger_id).uniqueResult();
			
			/*double payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where " +
										"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
										setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();*/
			
			double returns=(Double) getSession().createQuery("select coalesce(sum(amount-paid_by_payment),0)"
					+ " from SalesReturnModel where customer.id=:led and active=true")
					.setLong("led", ledger_id).uniqueResult();
			
			
			commit();
			
			bal=saleAmt+comnSale-cashDeposit-bankDeposit-pdcPayment-returns;
			
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
			
			Object dr=getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Dr',a.date,a.amount,0, customer.name) from RentalTransactionModel a where a.date <:stdt  and " +
										"a.customer.id=:led and a.rent_type=1").setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			
			Object cr=getSession().createQuery("select sum(a.payment_amount) from RentalPaymentModel a where a.date <:stdt  and " +
										"a.from_account_id=:led and a.type=:typ").setParameter("typ", SConstants.RENTAL_PAYMENTS).setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			
			
			if(objDr!=null)
				op_bal+=(Double)objDr;
			
			if(objCr!=null)
				op_bal-=(Double)objCr;
			
			if(dr!=null)
				op_bal+=(Double)dr;
			
			if(cr!=null)
				op_bal-=(Double)cr;
			
			
			commit();
			
			return op_bal;
			
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
	
	@SuppressWarnings("unchecked")
	public List getNewCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			try {
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Commission Sale',a.date,coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) ,0.0,a.sales_no,'') from CustomerCommissionSalesModel a join a.details_list b where a.date between :stdt and :enddt and b.customer.id=:led group by a.id" +
											"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			} catch (Exception e) {
				System.out.println(e);
				// TODO: handle exception
			}
			
			// Contsructor #33
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale',date,amount,payment_amount,sales_number,ref_no) from SalesModel where date between :stdt and :enddt and customer.id=:led and active=true" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"b.id,'Cash Receipt',a.date,0.0,b.amount,b.bill_no,'') from CashAccountDepositModel a join a.cash_account_deposit_list b where a.date between :stdt and :enddt and a.active=true and " +
										"b.account.id=:led").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"b.id,concat('Bank-',b.chequeNo,' ',b.chequeDate),a.date,0.0,b.amount,b.bill_no,'') from BankAccountDepositModel a join a.bank_account_deposit_list b where a.date between :stdt and :enddt and a.active=true and " +
										"b.account.id=:led").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"b.id,'PDC Payment',a.date,0.0,b.amount,b.bill_no,'') from PdcPaymentModel a join a.pdc_payment_list b where a.date between :stdt and :enddt and a.active=true and " +
										"b.from_id=:led").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			try {
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale Return',date,0.0,  amount, return_no,ref_no)"
					+ " from SalesReturnModel where active=true and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
					.list());
			} catch (Exception e) {}
			try {
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Debit Note',a.date,sum(b.amount),0.0,a.bill_no,a.ref_no) from DebitNoteModel a join a.debit_note_list b where a.date between :stdt and :enddt and a.active=true and " +
										"a.ledger.id=:led group by a.id").setLong("led", ledger_id)
										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			} catch (Exception e) {}
			try {
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Credit Note',a.date,0.0,sum(b.amount),a.bill_no,a.ref_no) from CreditNoteModel a join a.credit_note_list b where a.date between :stdt and :enddt and a.active=true and " +
					"a.ledger.id=:led group by a.id").setLong("led", ledger_id)
					.setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			} catch (Exception e) {}
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Rental',date,amount,payment_amount,sales_number) from RentalTransactionModel where date between :stdt and :enddt and customer.id=:led and active=true and rent_type=2" +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());*/
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and active=true and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());*/
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Rental Receipt',date,0.0,payment_amount) from RentalPaymentModel where date between :stdt and :enddt and active=true and " +
										"from_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.RENTAL_PAYMENTS).list());*/
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Sal Return',date,  amount, -(payment_amount))"
					+ " from SalesReturnModel where active=true and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
					.list());*/
			
			/*resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Subscription',payment_date,0.0,amount_paid) from SubscriptionPaymentModel where payment_date between :stdt and :enddt and " +
										"from_account=:led").setLong("led", ledger_id).
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





	public String getSalesOrderNoFromSales(long id) throws Exception {
		String orderNo="";
		try {
			begin();
			List list =getSession().createQuery("select b.order_id from SalesModel a join a.inventory_details_list b where a.id=:id").setParameter("id", id).list();
			HashSet set=new HashSet(list);
			Iterator iter=set.iterator();
			long orderId=0;
			while (iter.hasNext()) {
				orderId = (Long) iter.next();
				if(orderId!=0){
					orderNo+=getSession().createQuery("select order_no from SalesOrderModel where id=:id").setParameter("id", orderId).uniqueResult()+" , ";
				}
			}
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return orderNo;
	}
	
	
	
	
	

}
