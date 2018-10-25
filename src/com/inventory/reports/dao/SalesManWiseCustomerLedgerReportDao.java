package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class SalesManWiseCustomerLedgerReportDao extends SHibernate implements Serializable{
	
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
	
	
	
	public List getCustomerLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id,
			long sales_man) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			String dat=" a.date ";
			
			begin();
			
			if(ledger_id==0) {
				try {
					resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"a.id,'Commission Sale',a.date,coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) ,0.0) from " +
							"CommissionSalesNewModel a, CustomerModel c join a.details_list b where a.date between :stdt and :enddt and " +
							" c.ledger.id=b.customer.id and c.responsible_person=:rp group by a.id" +
												"").setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date).list());
				} catch (Exception e) {
				}
				// Contsructor #17
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Sale',a.date,a.amount,a.payment_amount,a.sales_number,a.customer.name) from SalesModel a, CustomerModel c where a.date between :stdt and :enddt and " +
						" a.active=true and c.ledger.id=a.customer.id and c.responsible_person=:rp ")
						.setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Receipt',a.date,0.0,a.payment_amount,a.payment_id, c.name) from PaymentModel a, CustomerModel c where "+dat+" between :stdt and :enddt and a.active=true and " +
											" a.type=:typ and c.ledger.id=a.from_account_id and c.responsible_person=:rp").setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date)
											.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(a.id,'Sal Return',a.date,  a.amount, -(a.payment_amount),a.credit_note_no, c.name)"
						+ " from SalesReturnModel a, CustomerModel c where a.active=true and " +
						"a.date between :stdt and :enddt  and c.ledger.id=a.customer.id and c.responsible_person=:rp ").setLong("rp", sales_man)
						.setParameter("stdt", start_date).setParameter("enddt", end_date).list());
				
			}
			else {
				
				
				try {
					resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"a.id,'Commission Sale',a.date,coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) ,0.0,b.customer.name) from CommissionSalesNewModel a join a.details_list b where a.date between :stdt and :enddt and b.customer.id=:led group by a.id" +
												"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
				} catch (Exception e) {
				}
				// Contsructor #17
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Sale',a.date,a.amount,a.payment_amount,a.sales_number, a.customer.name) from SalesModel a where a.date between :stdt and :enddt and a.customer.id=:led and a.active=true" +
											"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Receipt',a.date,0.0,a.payment_amount,a.payment_id,c.name) from PaymentModel a, CustomerModel c where a.date between :stdt and :enddt and a.active=true and " +
											"a.from_account_id=:led and c.ledger.id=a.from_account_id and type=:typ").setLong("led", ledger_id).
											setDate("stdt", start_date).setDate("enddt", end_date)
											.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(a.id,'Sal Return',a.date,  a.amount, -(a.payment_amount),a.credit_note_no,a.customer.name)"
						+ " from SalesReturnModel a where a.active=true and " +
						"a.date between :stdt and :enddt  and a.customer.id=:led")
						.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
						.list());
				
			}
			
			
			
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
			
			commit();
			
			return resultList;
			
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
	public List getCustomerConsolidatedLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id,
			long sales_man) throws Exception {
		try {
			
			resultList=new ArrayList();
			
//			String dat="a.date";
			
			begin();
			
//			if(ledger_id==0) {
//				
//				try {
//					resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//							"c.ledger.id,'Commission Sale',coalesce(sum(a.amount),0) ,sum(a.payment_amount),c.name) from " +
//							"CommissionSalesNewModel a, CustomerModel c join a.commission_sales_list b where a.date between :stdt and :enddt and " +
//							" c.ledger.id=a.customer.id and c.responsible_person=:rp group by c.id" +
//							"").setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date).list());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				// Contsructor #25
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//						"c.ledger.id,'Sale',sum(a.amount),sum(a.payment_amount),a.customer.name) from SalesModel a, CustomerModel c where a.date between :stdt and :enddt and " +
//						" a.active=true and c.ledger.id=a.customer.id and c.responsible_person=:rp group by c.id ")
//						.setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date).list());
//				
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//						"c.ledger.id,'Receipt',0.0,sum(a.payment_amount), c.name) from PaymentModel a, CustomerModel c where "+dat+" between :stdt and :enddt and a.active=true and " +
//						" a.type=:typ and c.ledger.id=a.from_account_id and c.responsible_person=:rp  group by c.id ").setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date)
//						.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
//				
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean" +
//						"(c.ledger.id,'Sal Return', sum(a.amount), sum(-(a.payment_amount)), c.name)"
//						+ " from SalesReturnModel a, CustomerModel c where a.active=true and " +
//						"a.date between :stdt and :enddt  and c.ledger.id=a.customer.id and c.responsible_person=:rp  group by c.id ").setLong("rp", sales_man)
//						.setParameter("stdt", start_date).setParameter("enddt", end_date).list());
//				
//			}
//			else {
				
				
				// Contsructor #25
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//						"a.customer.id,'Sale',sum(a.amount),sum(a.payment_amount), a.customer.name) from SalesModel a where a.date " +
//						"between :stdt and :enddt and a.customer.id=:led and a.active=true group by a.customer.id")
//						.setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//						"c.ledger.id,''Receipt'','0.0,sum(a.payment_amount),c.name) from PaymentModel a, CustomerModel " +
//						"c where a.date between :stdt and :enddt and a.active=true and " +
//						"a.from_account_id=:led and c.ledger.id=a.from_account_id and type=:typ  group by c.id").setLong("led", ledger_id).
//						setDate("stdt", start_date).setDate("enddt", end_date)
//						.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
				
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean" +
//						"(c.ledger.id, 'Sal Return', sum(a.amount), sum(-(a.payment_amount)),a.customer.name)"
//						+ " from SalesReturnModel a where a.active=true and " +
//						"a.date between :stdt and :enddt  and a.customer.id=:led group by c.id")
//						.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
//						.list());
//			}
			String cnd="";
			if(ledger_id!=0)
				cnd+=" and id="+ledger_id;
			LedgerModel ledg=null;
			Iterator iter=getSession().createQuery("from LedgerModel where office.id=:ofc "+cnd).setParameter("ofc", office_id).list().iterator();
			
			while (iter.hasNext()) {
				ledg = (LedgerModel) iter.next();
			
			
			// Contsructor #25
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.customer.id,'Sale',sum(a.amount),sum(a.payment_amount+a.paid_by_payment), a.customer.name) from SalesModel a where a.date " +
						"between :stdt and :enddt and a.customer.id=:led and a.active=true group by a.customer.id")
						.setLong("led", ledg.getId()).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"b.account.id,'Receipt',0.0,sum(b.amount),'') " +
//					"from CashAccountDepositModel a join a.cash_account_deposit_list b where a.date between :stdt and :enddt and a.active=true and " +
//										"b.account.id=:led").setLong("led",  ledg.getId())
//										.setDate("stdt", start_date).setDate("enddt", end_date).list());
//			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"b.account.id,'Receipt',0.0,sum(b.amount),'') " +
//					"from BankAccountDepositModel a join a.bank_account_deposit_list b where a.date between :stdt and :enddt and a.active=true and " +
//										"b.account.id=:led").setLong("led",  ledg.getId())
//										.setDate("stdt", start_date).setDate("enddt", end_date).list());
//			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"b.account.id,'Receipt',0.0,sum(b.amount),'')" +
//					" from PdcPaymentModel a join a.pdc_payment_list b where a.date between :stdt and :enddt and a.active=true and " +
//										"b.from_id=:led").setLong("led",  ledg.getId())
//										.setDate("stdt", start_date).setDate("enddt", end_date).list());
			try {
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"customer.id,'Sale Return',sum(amount),0.0, customer.name)"
					+ " from SalesReturnModel where active=true and " +
					"date between :stdt and :enddt  and customer.id=:led")
					.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led",  ledg.getId())
					.list());
			} catch (Exception e) {}
//			try {
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"a.id,'Debit Note',a.date,sum(b.amount),0.0,a.bill_no,a.ref_no) from " +
//					" DebitNoteModel a join a.debit_note_list b where a.date between :stdt and :enddt and a.active=true and " +
//										"a.ledger.id=:led group by a.id").setLong("led", ledger_id)
//										.setDate("stdt", start_date).setDate("enddt", end_date).list());
//			} catch (Exception e) {}
//			try {
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"a.id,'Credit Note',a.date,0.0,sum(b.amount),a.bill_no,a.ref_no) from" +
//					" CreditNoteModel a join a.credit_note_list b where a.date between :stdt and :enddt and a.active=true and " +
//					"a.ledger.id=:led group by a.id").setLong("led", ledger_id)
//					.setDate("stdt", start_date).setDate("enddt", end_date).list());
//			
//			} catch (Exception e) {}
			
			
			}
			
			commit();
			
			return resultList;
			
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
	
	
	
	public double getSalesOpeningBalance(Date date, long office_id, long ledger_id, long sales_man) throws Exception {
		double bal=0;
		try {
			
			begin();
			double comnSale=0;
			
			String dat=" a.date ";
			
			if(ledger_id==0) {
				
				try {
					comnSale=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) from " +
							"CustomerCommissionSalesModel a, CustomerModel c join a.details_list b where a.date<:stdt and c.ledger.id=b.customer.id and c.responsible_person=:rp" +
												"").setLong("rp", sales_man).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {}
				
				double saleAmt=(Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount-a.paid_by_payment),0) from SalesModel a, " +
						"CustomerModel c where a.date <:stdt  and c.ledger.id=a.customer.id and c.responsible_person=:rp and a.active=true " +
											"").setLong("rp", sales_man).setDate("stdt", date).uniqueResult();
				
//				double payment=(Double) getSession().createQuery("select coalesce(sum(a.payment_amount),0) from PaymentModel a, CustomerModel c where "+dat+" <:stdt and " +
//											"c.ledger.id=a.from_account_id and c.responsible_person=:rp and a.type=:typ and a.active=true").setLong("rp", sales_man).
//											setDate("stdt", date).setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
				
				double returns=(Double) getSession().createQuery("select coalesce(sum(a.amount),0)"
						+ " from SalesReturnModel a, CustomerModel c where a.date <:stdt  and c.ledger.id=a.customer.id and c.responsible_person=:rp and a.active=true")
						.setParameter("stdt", date).setLong("rp", sales_man).uniqueResult();
				
				bal=saleAmt+comnSale-returns;
				
			} else {
			
				try {
					comnSale=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) from CustomerCommissionSalesModel a join a.details_list b where a.date<:stdt and b.customer.id=:led" +
												"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {}
				
				double saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount-paid_by_payment),0) from SalesModel where date <:stdt and customer.id=:led and active=true " +
											"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				
//				double payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <:stdt and " +
//											"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
//											setDate("stdt", date).setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
				
				double returns=(Double) getSession().createQuery("select coalesce(sum(amount),0)"
						+ " from SalesReturnModel where date <:stdt  and customer.id=:led and active=true")
						.setParameter("stdt", date).setLong("led", ledger_id).uniqueResult();
				
				bal=saleAmt+comnSale-returns;
			}
			
			commit();
			
			
			
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
	
	
	
	
	public double getSalesCurrentBalance(Date date, long office_id, long ledger_id, long sales_man) throws Exception {
		double bal=0;
		try {
			
			begin();
			
			double comnSale=0;
			
			if(ledger_id==0) {
				
				try {
					
					comnSale=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) from " +
							"CustomerCommissionSalesModel a, CustomerModel c join a.details_list b where a.date<:stdt and c.ledger.id=b.customer.id and c.responsible_person=:rp" +
												"").setLong("rp", sales_man).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				double saleAmt=(Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount),0) from " +
						"SalesModel  a, CustomerModel c where c.ledger.id=a.customer.id and c.responsible_person=:rp and a.active=true " +
											"").setLong("rp", sales_man).uniqueResult();
				
				double payment=(Double) getSession().createQuery("select coalesce(sum(a.payment_amount),0) from PaymentModel a, CustomerModel c where " +
											"c.ledger.id=a.from_account_id and c.responsible_person=:rp and a.type=:typ and a.active=true").setLong("rp", sales_man).
											setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
				
				double returns=(Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount),0)"
						+ " from SalesReturnModel a, CustomerModel c where c.ledger.id=a.customer.id and c.responsible_person=:rp and a.active=true")
						.setLong("rp", sales_man).uniqueResult();
				
				bal=saleAmt+comnSale-payment-returns;
				
			} else {
				
				try {
					comnSale=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price+b.tax_amount-b.discount_amount),0) from CustomerCommissionSalesModel a join a.details_list b where a.date<:stdt and b.customer.id=:led" +
												"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				double saleAmt=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from SalesModel where customer.id=:led and active=true " +
											"").setLong("led", ledger_id).uniqueResult();
				
				double payment=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where " +
											"from_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).
											setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
				
				double returns=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
						+ " from SalesReturnModel where customer.id=:led and active=true")
						.setLong("led", ledger_id).uniqueResult();
				
				bal=saleAmt+comnSale-payment-returns;
				
			}
			commit();
			
			
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
