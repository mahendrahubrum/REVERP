package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class IntervalReportDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6439037979804708220L;
	private List resultList=new ArrayList();
	
	public List getCustomerLedgerReport(Date date,int interval, int noOfintrvls, long office_id) throws Exception {
		try {
			List<Double> intrvalList;
			Calendar cal=Calendar.getInstance();
			Date start_date;
			AcctReportMainBean rptObj;
			boolean valid=false;
			
			resultList=new ArrayList();
			
			begin();
			
			
				
			Iterator itr = getSession().createQuery(
							"select new com.inventory.config.acct.model.LedgerModel(a.ledger.id, a.name)"
									+ " from CustomerModel a where a.ledger.status=:val and  a.ledger.office.id=:ofc order by a.name")
									.setLong("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			LedgerModel obj;
			double sale=0, cash=0, ret=0, todayBal=0, openingBal=0;
			while(itr.hasNext()) {
				
				cal.setTime(date);
				start_date=date;
				
				obj=(LedgerModel) itr.next();
				
				sale=0; cash=0; ret=0;todayBal=0;openingBal=0;
				
				sale=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from SalesModel where date <=:stdt and active=true and customer.id=:led " +
											"").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();
				
				cash=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <=:stdt and " +
											"from_account_id=:led and type=:typ and active=true").setLong("led", obj.getId()).
											setDate("stdt", start_date)
											.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
				
				ret=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
						+ " from SalesReturnModel where status=1 and active=true and " +
						"date <=:stdt and customer.id=:led")
						.setParameter("stdt", start_date).setLong("led", obj.getId())
						.uniqueResult();
				
				valid=false;
				if(sale!=0 || cash!=0 || ret!=0)
					valid=true;
				
				rptObj=new AcctReportMainBean(obj.getName(), sale, cash, ret, sale-cash-ret, null);
				sale=0;cash=0;ret=0;
				intrvalList=new ArrayList<Double>();
				for (int i = 0; i < noOfintrvls; i++) {
					cal.add(Calendar.DAY_OF_MONTH, interval);
					start_date=new Date(cal.getTime().getTime());
					
					sale=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from SalesModel where date <=:stdt and customer.id=:led and active=true " +
							"").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();

					cash=(Double) getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <=:stdt and " +
												"from_account_id=:led and type=:typ and active=true").setLong("led", obj.getId()).
												setDate("stdt", start_date)
												.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).uniqueResult();
					
					ret=(Double) getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
							+ " from SalesReturnModel where status=1 and active=true and " +
							"date <=:stdt and customer.id=:led")
							.setParameter("stdt", start_date).setLong("led", obj.getId())
							.uniqueResult();
					
					intrvalList.add(sale-cash-ret);
					if(sale!=0 || cash!=0 || ret!=0)
						valid=true;
					
				}
				rptObj.setSubList(intrvalList);
				
				
				if(valid)
					resultList.add(rptObj);

				
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
			return resultList;
		}
	}
	
	
	public List getSupplierLedgerReport(Date date,int interval, int noOfintrvls, long office_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			List<Double> intrvalList;
			Calendar cal=Calendar.getInstance();
			Date start_date;
			AcctReportMainBean rptObj;
			boolean valid=false;
			
			
			begin();
			
			Iterator itr = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
									+ " from SupplierModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			
			LedgerModel obj;
			double purchase=0, cash=0, ret=0, todayBal=0,openingBal=0;
			while(itr.hasNext()) {
				
				cal.setTime(date);
				start_date=date;
				
				obj=(LedgerModel) itr.next();
				
				purchase=0; cash=0; ret=0;todayBal=0;openingBal=0;
			
				
				purchase = (Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where date <=:stdt and supplier.id=:led and active=true " +
										"").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();
			
				cash=(Double)getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <=:stdt and " +
											"to_account_id=:led and type=:typ and active=true").setLong("led", obj.getId()).
											setDate("stdt", start_date)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				ret=(Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
						+ " from PurchaseReturnModel where status=1 and date <=:stdt and supplier.id=:led and active=true")
						.setParameter("stdt", start_date).setLong("led", obj.getId())
						.uniqueResult();
				
				valid=false;
				if(purchase!=0 || cash!=0 || ret!=0)
					valid=true;
				
				rptObj=new AcctReportMainBean(obj.getName(), purchase, cash, ret, purchase-cash-ret, null);
				purchase=0;cash=0;ret=0;
				intrvalList=new ArrayList<Double>();
				for (int i = 0; i < noOfintrvls; i++) {
					cal.add(Calendar.DAY_OF_MONTH, interval);
					start_date=new Date(cal.getTime().getTime());
					
					purchase = (Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where date <=:stdt and supplier.id=:led and active=true " +
							"").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();

				
					cash=(Double)getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date <=:stdt and active=true and " +
												"to_account_id=:led and type=:typ").setLong("led", obj.getId()).
												setDate("stdt", start_date)
												.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
					
					
					ret=(Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
							+ " from PurchaseReturnModel where status=1 and date <=:stdt and supplier.id=:led and active=true")
							.setParameter("stdt", start_date).setLong("led", obj.getId())
							.uniqueResult();
					
					
					
					intrvalList.add(purchase-cash-ret);
					if(purchase!=0 || cash!=0 || ret!=0)
						valid=true;
					
				}
				rptObj.setSubList(intrvalList);
				
				
				if(valid)
					resultList.add(rptObj);
				
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
			return resultList;
		}
	}
	
	
	
	public List getTransportationReport(Date date,int interval, int noOfintrvls, long office_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			List<Double> intrvalList;
			Calendar cal=Calendar.getInstance();
			Date start_date;
			AcctReportMainBean rptObj;
			boolean valid=false;
			
			begin();
			
			
			Iterator itr = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
									+ " from TranspotationModel where ledger.office.id=:ofc and ledger.status=:val and active=true order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			
			LedgerModel obj;
			double credit=0, cash=0, ret=0, todayBal=0,openingBal=0;
			while(itr.hasNext()) {
				
				cal.setTime(date);
				start_date=date;
				
				obj=(LedgerModel) itr.next();
				
				credit=0; cash=0; ret=0;todayBal=0;openingBal=0;
				
				
				credit=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
											"b.fromAcct.id=:led").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();
				
				
				cash= (Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
											"b.toAcct.id=:led").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();
				
				
				valid=false;
				if(credit!=0 || cash!=0)
					valid=true;
				
				
				
				rptObj=new AcctReportMainBean(obj.getName(), credit, cash, 0, credit-cash, null);
				credit=0;cash=0;ret=0;
				intrvalList=new ArrayList<Double>();
				for (int i = 0; i < noOfintrvls; i++) {
					cal.add(Calendar.DAY_OF_MONTH, interval);
					start_date=new Date(cal.getTime().getTime());
					
					credit=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
												"b.fromAcct.id=:led").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();


					cash= (Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
												"b.toAcct.id=:led").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();

					
					intrvalList.add(credit-cash);
					if(credit!=0 || cash!=0)
						valid=true;
					
				}
				rptObj.setSubList(intrvalList);
				
				
				if(valid)
					resultList.add(rptObj);
				
				
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
			return resultList;
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
