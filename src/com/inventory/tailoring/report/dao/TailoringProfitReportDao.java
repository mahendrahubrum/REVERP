package com.inventory.tailoring.report.dao;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 23, 2014
 */


public class TailoringProfitReportDao extends SHibernate implements Serializable{
	
	public double getTotalSalesAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
					.createQuery(
							"select coalesce(sum(amount)) from TailoringSalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public double getTotalSalesReturnAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
					.createQuery(
							"select coalesce(sum(amount)) from SalesReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public double getTotalPurchaseAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob  =  getSession()
					.createQuery(
							"select coalesce(sum(amount)) from PurchaseModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public List getDetailsList(long officeId,Date fromDate, Date toDate) throws Exception {
		List result=new ArrayList();
		try {
			
			begin();
			
			result.addAll(getSession().createQuery(
					"select new com.inventory.reports.bean.AcctReportMainBean(" +
			"3,id,'Sale',cast(sales_number as string), date, customer.name, amount,'',comments) from TailoringSalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
							.setParameter("fromDate", fromDate).setLong("ofc", officeId)
							.setParameter("toDate", toDate).list());
	
			result.addAll(getSession().createQuery(
					"select new com.inventory.reports.bean.AcctReportMainBean(" +
			"4,id,'Sales Return',cast(credit_note_no as string),date,customer.name , amount,ref_no,comments) from SalesReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
							.setParameter("fromDate", fromDate).setLong("ofc", officeId)
							.setParameter("toDate", toDate).list());
			
			result.addAll(getSession().createQuery(
							"select new com.inventory.reports.bean.AcctReportMainBean(" +
					"1,id,'Purchase',cast(purchase_number as string), date, supplier.name, amount,'',comments ) from PurchaseModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).list());
			
			result.addAll(getSession().createQuery(
							"select new com.inventory.reports.bean.AcctReportMainBean(" +
					"2,id,'Purchase Return',cast(debit_note_no as string),date,supplier.name , amount,refNo,comments) from PurchaseReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).list());
			
			result.addAll(getSession().createQuery(
							"select new com.inventory.reports.bean.AcctReportMainBean(" +
					"5,id,'Transportation Payment',cast(payment_id as string),date,(select name from LedgerModel where id=transportation_id),payment_amount, '',description) from TransportationPaymentModel where date between :fromDate and :toDate and office.id=:ofc and active=true and type=2")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).list());
			
			result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"6,a.id,'Expenditure Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo) from PaymentDepositModel a join a.transaction.transaction_details_list b" +
					" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date between :fromDate and :toDate group by a.id")
					.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return result;
		}
	}
	
	
	public double getTotalPurchaseReturnAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
					.createQuery(
							"select coalesce(sum(amount)) from PurchaseReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public double getTotalTransportationAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
					.createQuery(
							"select coalesce(sum(payment_amount)) from TransportationPaymentModel where date between :fromDate and :toDate and office.id=:ofc and active=true and type=2")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public double getTotalExpentitureTransactionAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob  =  getSession()
                    .createQuery(
                            "select sum(b.amount) from TransactionModel a  "
                                            + " join a.transaction_details_list b where a.office.id=:ofc and a.transaction_type=:type and a.date between :frmDt and :toDt")
            .setParameter("ofc", officeId).setParameter("type", SConstants.EXPENDETURE_TRANSACTION)
            .setParameter("frmDt", fromDate)
            .setParameter("toDt", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public double getOfStockAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
                    .createQuery("select sum(balance*rate) from ItemStockModel where item.office.id=:ofc and balance>0 and date_time between :frmDt and :toDt")
            .setParameter("ofc", officeId).setParameter("frmDt", new Timestamp(fromDate.getTime()))
            .setParameter("toDt", new Timestamp(toDate.getTime())).uniqueResult();
			
			if(ob!=null)
				total=(Double) ob;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return total;
		}
	}
	
	
	public List getDetailsFromType(long officeId,Date fromDate, Date toDate, String type) throws Exception {
		List result=new ArrayList();
		try {
			
			begin();
			
			if(type.equals("Sale")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"3,id,'Sale',cast(sales_number as string), date, customer.name, amount,'',comments) from TailoringSalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
								.setParameter("fromDate", fromDate).setLong("ofc", officeId)
								.setParameter("toDate", toDate).list());
			}
			else if(type.equals("Sales Return")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"4,id,'Sales Return',cast(credit_note_no as string),date,customer.name , amount,ref_no,comments) from SalesReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
								.setParameter("fromDate", fromDate).setLong("ofc", officeId)
								.setParameter("toDate", toDate).list());
			}
			
			else if(type.equals("Sales Return Item")) {
				
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Sales Return',cast(a.credit_note_no as string),a.date,a.customer.name , b.item.name, b.good_stock/(b.quantity_in_basic_unit/b.balance),b.waste_quantity/(b.quantity_in_basic_unit/b.balance)," +
						"b.returned_quantity/(b.quantity_in_basic_unit/b.balance), b.stock_quantity/(b.quantity_in_basic_unit/b.balance),b.unit.symbol,b.unit_price,a.ref_no,a.comments) from SalesReturnModel a" +
						" join a.inventory_details_list b where a.date between :fromDate and :toDate and a.office.id=:ofc and a.active=true")
								.setParameter("fromDate", fromDate).setLong("ofc", officeId)
								.setParameter("toDate", toDate).list());
				
			}
			
			
			else if(type.equals("Purchase")) {
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"1,id,'Purchase',cast(purchase_number as string), date, supplier.name, amount,'',comments ) from PurchaseModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
										.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).list());
			}
			else if(type.equals("Purchase Return")) {
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"2,id,'Purchase Return',cast(debit_note_no as string),date,supplier.name , amount,refNo,comments) from PurchaseReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
										.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).list());
			}
			
			else if(type.equals("Purchase Return Item")) {
				
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Purchase Return',cast(a.debit_note_no as string),a.date,a.supplier.name, b.item.name,b.qunatity,b.unit.symbol, b.unit_price,a.refNo,a.comments) " +
						"from PurchaseReturnModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and " +
						"a.office.id=:ofc and a.active=true").setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).list());
			}
			
			
			else if(type.equals("Transportation")) {
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"5,id,'Transportation Payment',cast(payment_id as string),date,(select name from LedgerModel where id=transportation_id),payment_amount, '',description) from TransportationPaymentModel where date between :fromDate and :toDate and office.id=:ofc and active=true and type=2")
										.setParameter("fromDate", fromDate).setLong("ofc", officeId)
										.setParameter("toDate", toDate).list());
			}
			else if(type.equals("Expenses")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"6,a.id,'Expenditure Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo) from PaymentDepositModel a join a.transaction.transaction_details_list b" +
						" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date between :fromDate and :toDate group by a.id")
						.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
			}
			else if(type.equals("Stock")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"item.name,quantity,balance,item.unit.symbol,rate)" +
						" from ItemStockModel " +
						" where item.office.id=:ofcid and date_time between :fromDate and :toDate and balance>0 order by item.name ,date_time desc")
						.setLong("ofcid", officeId).setParameter("fromDate", new Timestamp(fromDate.getTime())).setParameter("toDate",  new Timestamp(toDate.getTime())).list());
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return result;
		}
	}
	
}
