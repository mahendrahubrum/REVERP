package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class SalesManWiseSupplierLedgerReportDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -652923603119519095L;
	private List<?> resultList=new ArrayList();
	
	public List getSupplieLedgerReport(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					" 'Purchase',date,amount,payment_amount, purchase_number) from PurchaseModel where date between :stdt and :enddt and supplier.id=:led " +
										"").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"'Receipt',date,0.0,payment_amount) from PaymentModel where date between :stdt and :enddt and " +
										"to_account_id=:led and type=:typ").setLong("led", ledger_id).
										setDate("stdt", start_date).setDate("enddt", end_date)
										.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean('Purch Return',date,  amount, -(payment_amount))"
					+ " from PurchaseReturnModel where status in (1,2) and " +
					"date between :stdt and :enddt  and supplier.id=:led")
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
	
	
	public List getNewSupplierLedgerReport(Date start_date, Date end_date, long office_id, 
			long ledger_id, boolean useToDt , long sales_man) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			String dat="a.date";
			if(useToDt)
				dat="a.toDate";
			
			begin();
			
			if(ledger_id==0) {
			
				try {
					resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"a.id,'Commission Sale',a.received_date,a.gross_sale,a.net_sale-a.commission,0.0, a.less_expense+a.commission, a.number) from CommissionSalesModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.received_date between :stdt and :enddt " +
							"and c.responsible_person=:rp ").setDate("stdt", start_date).setDate("enddt", end_date).setLong("rp", sales_man).list());
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Purchase',a.date,a.amount,a.payment_amount,a.purchase_number,c.name) from PurchaseModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.date between :stdt and :enddt and c.responsible_person=:rp and a.active=true " +
											"").setLong("rp", sales_man).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Payment',a.date,0.0,a.payment_amount,a.payment_id, c.name) from PaymentModel a, SupplierModel c where c.ledger.id=a.to_account_id and "+dat+" between :stdt and :enddt and " +
											" a.type=:typ and c.responsible_person=:rp and a.active=true").
											setDate("stdt", start_date).setDate("enddt", end_date).setLong("rp", sales_man)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(a.id,'Purch Return',a.date,  a.amount, -(a.payment_amount),a.debit_note_no, c.name)"
						+ " from PurchaseReturnModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.status in (1,2) and a.active=true and c.responsible_person=:rp and " +
						"a.date between :stdt and :enddt").setLong("rp", sales_man)
						.setParameter("stdt", start_date).setParameter("enddt", end_date)
						.list());
			}
			else {
				
				try {
					resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"a.id,'Commission Sale',a.received_date,a.gross_sale,a.net_sale-a.commission,0.0, a.less_expense+a.commission, a.number) from CommissionSalesModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.received_date between :stdt and :enddt " +
							"and a.supplier.id=:led" ).setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Purchase',a.date,a.amount,a.payment_amount,a.purchase_number,c.name) from PurchaseModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.date between :stdt and :enddt and a.supplier.id=:led and a.active=true "
											).setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Payment',a.date,0.0,a.payment_amount,a.payment_id, c.name) from PaymentModel a, SupplierModel c where c.ledger.id=a.to_account_id and "+dat+" between :stdt and :enddt and " +
											"a.to_account_id=:led and a.type=:typ and a.active=true").setLong("led", ledger_id).
											setDate("stdt", start_date).setDate("enddt", end_date)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).list());
				
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(a.id,'Purch Return',a.date,  a.amount, -(a.payment_amount),a.debit_note_no, c.name)"
						+ " from PurchaseReturnModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.status in (1,2) and a.active=true and " +
						"a.date between :stdt and :enddt  and a.supplier.id=:led")
						.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", ledger_id)
						.list());
				
			}
				
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
	
	
	
	public double getPurchaseOpeningBalance(Date date, long office_id, long ledger_id, boolean useToDt,long sales_man) throws Exception {
		double bal=0;
		try {
			
			String dat;
			
			begin();
			
			if(ledger_id==0) {
				
				dat="a.date";
				if(useToDt)
					dat="a.toDate";
			
				double comnSal=0;
				try {
					comnSal=(Double) getSession().createQuery("select coalesce(sum(a.net_sale-a.commission),0) from CommissionSalesModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.received_date<:stdt and c.responsible_person=:rp" +
												"").setLong("rp", sales_man).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				double purchase=(Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount),0) from PurchaseModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.date <:stdt and a.active=true and c.responsible_person=:rp")
						.setDate("stdt", date).setLong("rp", sales_man).uniqueResult();
				
				double payments=(Double) getSession().createQuery("select coalesce(sum(a.payment_amount),0) from PaymentModel a, SupplierModel c where c.ledger.id=a.to_account_id and "+dat+" <:stdt and " +
											" a.type=:typ and a.active=true and c.responsible_person=:rp").setDate("stdt", date)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).setLong("rp", sales_man).uniqueResult();
				
				double returns= (Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount),0) from PurchaseReturnModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.status in (1,2) and a.date <:stdt and a.active=true and c.responsible_person=:rp")
						.setParameter("stdt", date).setLong("rp", sales_man).uniqueResult();
				
				bal=purchase+comnSal-payments-returns;
				
			}
			else {
				
				dat="date";
				if(useToDt)
					dat="toDate";
				
				double comnSal=0;
				try {
					comnSal=(Double) getSession().createQuery("select coalesce(sum(net_sale-commission),0) from CommissionSalesModel where received_date<:stdt and supplier.id=:led " +
												"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				double purchase=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where date <:stdt and supplier.id=:led and active=true")
						.setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				
				double payments=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where "+dat+" <:stdt and " +
											"to_account_id=:led and type=:typ and active=true").setLong("led", ledger_id).setDate("stdt", date)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				double returns= (Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseReturnModel where status in (1,2) and date <:stdt and supplier.id=:led and active=true")
						.setParameter("stdt", date).setLong("led", ledger_id).uniqueResult();
				
				bal=purchase+comnSal-payments-returns;
				
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
	
	
	public double getPurchaseCurrentBalance(Date date, long office_id, long ledger_id,long sales_man) throws Exception {
		double bal=0;
		try {
			
			begin();
			
			if(ledger_id==0) {
				
				double comnSal=0;
				try {
					comnSal=(Double) getSession().createQuery("select coalesce(sum(a.net_sale-a.commission),0) from CommissionSalesModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.received_date<:stdt and c.responsible_person=:rp " +
												"").setLong("rp", sales_man).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				double purchase=(Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount),0) from PurchaseModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.active=true and c.responsible_person=:rp")
						.setLong("rp", sales_man).uniqueResult();
				
				
				double payments=(Double) getSession().createQuery("select coalesce(sum(a.payment_amount),0) from PaymentModel a, SupplierModel c where c.ledger.id=a.to_account_id and " +
											"a.type=:typ and a.active=true and c.responsible_person=:rp")
											.setLong("rp", sales_man).setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				
				double returns= (Double) getSession().createQuery("select coalesce(sum(a.amount-a.payment_amount),0) from PurchaseReturnModel a, SupplierModel c where c.ledger.id=a.supplier.id and a.status in (1,2) and a.active=true and c.responsible_person=:rp")
								.setLong("rp", sales_man).uniqueResult();
				
				bal=purchase+comnSal-payments-returns;
				
			}
			else {
				
				double comnSal=0;
				try {
					comnSal=(Double) getSession().createQuery("select coalesce(sum(net_sale-commission),0) from CommissionSalesModel where received_date<:stdt and supplier.id=:led " +
												"").setLong("led", ledger_id).setDate("stdt", date).uniqueResult();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				double purchase=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where supplier.id=:led and active=true")
						.setLong("led", ledger_id).uniqueResult();
				
				
				double payments=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where " +
											"to_account_id=:led and type=:typ and active=true").setLong("led", ledger_id)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				
				double returns= (Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseReturnModel where status in (1,2) and supplier.id=:led and active=true")
								.setLong("led", ledger_id).uniqueResult();
				
				bal=purchase+comnSal-payments-returns;
				
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
