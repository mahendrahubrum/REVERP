package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.acct.model.TranspotationModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 28, 2013
 */

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 26, 2015
 */

@SuppressWarnings("serial")
public class ProfitReportDao extends SHibernate implements Serializable{
	
	
	
	
	public double getTotalSalesAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
					.createQuery(
							"select coalesce(sum(amount)) from SalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
									.setParameter("fromDate", fromDate).setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			
			Object obj3  =  getSession()
                    .createQuery(
                            "select sum(amount) from RentalTransactionModel where office.id=:ofc and date between :frmDt and :toDt and rent_type=2")
            .setParameter("ofc", officeId)
            .setParameter("frmDt", fromDate)
            .setParameter("toDt", toDate).uniqueResult();
			if(obj3!=null)
				total+=(Double) obj3;
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return total;
	}

	
	public double getLaundryTotalSalesAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob =  getSession()
					.createQuery(
							"select coalesce(sum(amount)) from LaundrySalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
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
		}
		return total;
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
		}
		return total;
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
		}
		return total;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getDetailsList(long officeId,Date fromDate, Date toDate) throws Exception {
		List result=new ArrayList();
		try {
			
			begin();
			
			result.addAll(getSession().createQuery(
					"select new com.inventory.reports.bean.AcctReportMainBean(" +
			"3,id,'Sale',cast(sales_number as string), date, customer.name, amount,'',comments) from SalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true")
							.setParameter("fromDate", fromDate).setLong("ofc", officeId)
							.setParameter("toDate", toDate).list());
			
			result.addAll(getSession().createQuery(
					"select new com.inventory.reports.bean.AcctReportMainBean(" +
			"3,id,'Rental',cast(sales_number as string), date, customer.name, amount,'',comments) from RentalTransactionModel where date between :fromDate and :toDate and office.id=:ofc and active=true and rent_type=2")
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
			
			result.addAll(getSession().createQuery(
					"select new com.inventory.reports.bean.AcctReportMainBean(" +
			"3,id,'Rental',cast(sales_number as string), date, customer.name, amount,'',comments) from RentalTransactionModel where date between :fromDate and :toDate and office.id=:ofc and active=true and rent_type=1")
							.setParameter("fromDate", fromDate).setLong("ofc", officeId)
							.setParameter("toDate", toDate).list());
			
			result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"6,a.id,'Expenditure Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo) from PaymentDepositModel a join a.transaction.transaction_details_list b" +
					" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date between :fromDate and :toDate group by a.id")
					.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
			
			result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"6,a.id,'Income Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo) from PaymentDepositModel a join a.transaction.transaction_details_list b" +
					" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date between :fromDate and :toDate group by a.id")
					.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("type", SConstants.INCOME_TRANSACTION).list());
			
			
			result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"6,a.id,'Salary Disbursal','',a.date,b.toAcct.name, sum(b.amount), '','') from TransactionModel a join a.transaction_details_list b" +
					" where a.status=1 and a.office.id=:ofcid and a.transaction_type =:type and a.date between :fromDate and :toDate group by a.id")
					.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("type", SConstants.PAYROLL_PAYMENTS).list());
			
			result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"6,a.id,'Employee Advance Payments','',a.date,(select b.ledger.name from UserModel b where b.loginId.id=a.login_id and b.ledger.office.id=:ofcid), sum(a.payment_amount), '','') from EmployeeAdvancePaymentModel a " +
					" where a.office.id=:ofcid and a.date between :fromDate and :toDate group by a.id")
					.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setParameter("toDate", toDate).list());
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return result;
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
		}
		return total;
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
			
			Object obj1  =  getSession()
                    .createQuery(
                            "select sum(amount) from RentalTransactionModel where office.id=:ofc and date between :frmDt and :toDt and rent_type=1")
            .setParameter("ofc", officeId)
            .setParameter("frmDt", fromDate)
            .setParameter("toDt", toDate).uniqueResult();
			if(obj1!=null)
				total+=(Double) obj1;
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return total;
	}
	
	
	public double getTotalExpentitureTransactionAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob  =  getSession()
                    .createQuery(
                            "select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.office.id=:ofc and a.transaction_type=:type and a.date between :frmDt and :toDt")
            .setParameter("ofc", officeId).setParameter("type", SConstants.EXPENDETURE_TRANSACTION)
            .setParameter("frmDt", fromDate)
            .setParameter("toDt", toDate).uniqueResult();
			if(ob!=null)
				total=(Double) ob;
			
			Object obj1  =  getSession()
                    .createQuery(
                            "select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.office.id=:ofc and a.transaction_type=:type and a.date between :frmDt and :toDt")
            .setParameter("ofc", officeId).setParameter("type", SConstants.PAYROLL_PAYMENTS)
            .setParameter("frmDt", fromDate)
            .setParameter("toDt", toDate).uniqueResult();
			if(obj1!=null)
				total+=(Double) obj1;
			Object obj2  =  getSession()
                    .createQuery(
                            "select sum(payment_amount) from EmployeeAdvancePaymentModel where office.id=:ofc and date between :frmDt and :toDt")
            .setParameter("ofc", officeId)
            .setParameter("frmDt", fromDate)
            .setParameter("toDt", toDate).uniqueResult();
			if(obj2!=null)
				total+=(Double) obj2;
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return total;
	}
	
	
	public double getTotalIncomeTransactionAmount(long officeId,Date fromDate, Date toDate) throws Exception {
		double total=0;
		try {
			begin();
			Object ob  =  getSession()
                    .createQuery(
                            "select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.office.id=:ofc and a.transaction_type=:type and a.date between :frmDt and :toDt")
            .setParameter("ofc", officeId).setParameter("type", SConstants.INCOME_TRANSACTION)
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
		}
		return total;
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
		}
		return total;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getDetailsFromType(long officeId,Date fromDate, Date toDate, String type, long id, int ttype) throws Exception {
		List result=new ArrayList();
		try {
			
			begin();
			
			if(type.equals("Sale")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"3,id,'Sale',cast(sales_number as string), date, customer.name, amount,'',comments) from SalesModel where date " +
						"between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:id")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setLong("id", id)
								.setParameter("toDate", toDate).list());
				
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"3,id,'Rental',cast(sales_number as string), date, customer.name, amount,'',comments) from RentalTransactionModel where date " +
						"between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:id and rent_type=2")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setLong("id", id)
								.setParameter("toDate", toDate).list());
				
			}
			else if(type.equals("Sales Return")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"4,id,'Sales Return',cast(credit_note_no as string),date,customer.name , amount,ref_no,comments) " +
						"from SalesReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:id")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setLong("id", id)
								.setParameter("toDate", toDate).list());
			}
			
			else if(type.equals("Sales Return Item")) {
				
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Sales Return',cast(a.credit_note_no as string),a.date,a.customer.name , b.item.name, b.good_stock/(b.quantity_in_basic_unit/b.balance),b.waste_quantity/(b.quantity_in_basic_unit/b.balance)," +
						"b.returned_quantity/(b.quantity_in_basic_unit/b.balance), b.stock_quantity/(b.quantity_in_basic_unit/b.balance),b.unit.symbol,b.unit_price,a.ref_no,a.comments) from SalesReturnModel a" +
						" join a.inventory_details_list b where a.date between :fromDate and :toDate and a.office.id=:ofc and a.active=true and b.item.id=:id")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setLong("id", id)
								.setParameter("toDate", toDate).list());
				
			}
			
			
			else if(type.equals("Purchase")) {
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"1,id,'Purchase',cast(purchase_number as string), date, supplier.name, amount,'',comments ) from PurchaseModel where " +
						"date between :fromDate and :toDate and office.id=:ofc and active=true and supplier.id=:id")
										.setParameter("fromDate", fromDate)
										.setLong("ofc", officeId)
										.setLong("id", id)
									.setParameter("toDate", toDate).list());
			}
			else if(type.equals("Purchase Return")) {
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"2,id,'Purchase Return',cast(debit_note_no as string),date,supplier.name , amount,refNo,comments) from " +
						"PurchaseReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true and supplier.id=:id")
										.setParameter("fromDate", fromDate)
										.setLong("ofc", officeId)
										.setLong("id", id)
									.setParameter("toDate", toDate).list());
			}
			
			else if(type.equals("Purchase Return Item")) {
				
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Purchase Return',cast(a.debit_note_no as string),a.date,a.supplier.name, b.item.name,b.qunatity,b.unit.symbol, b.unit_price,a.refNo,a.comments) " +
						"from PurchaseReturnModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and " +
						"a.office.id=:ofc and a.active=true and b.item.id=:id")
						.setParameter("fromDate", fromDate)
						.setLong("ofc", officeId)
						.setLong("id", id)
						.setParameter("toDate", toDate).list());
			}
			
			
			else if(type.equals("Transportation")) {
				result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(" +
						"5,id,'Transportation Payment',cast(payment_id as string),date,(select name from LedgerModel where id=transportation_id)," +
						"payment_amount, '',description) from TransportationPaymentModel where date between :fromDate and :toDate and " +
						"office.id=:ofc and active=true and type=2 and transportation_id=:id")
										.setParameter("fromDate", fromDate)
										.setLong("ofc", officeId)
										.setLong("id", id)
										.setParameter("toDate", toDate).list());
				
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"5,id,'Rental',cast(sales_number as string), date, customer.name, amount,'',comments) from RentalTransactionModel where date " +
						"between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:id and rent_type=1")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setLong("id", id)
								.setParameter("toDate", toDate).list());
				System.out.println("Size "+result.size());
			}
			else if(type.equals("Expenses")) {
				
				if(ttype==SConstants.EXPENDETURE_TRANSACTION){

					result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"a.transaction.transaction_type,a.id,'Expenditure Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo) " +
							"from PaymentDepositModel a join a.transaction.transaction_details_list b" +
							" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
							"a.date between :fromDate and :toDate and b.toAcct.id=:id group by a.id")
							.setLong("ofcid", officeId)
							.setParameter("fromDate", fromDate)
							.setLong("id", id)
							.setParameter("toDate", toDate)
							.setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
					
				}
				
				if(ttype==SConstants.PAYROLL_PAYMENTS){
				
					result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"a.transaction_type,a.id,'Salary Disbursal','',a.date,b.toAcct.name, sum(b.amount), '','Salary Disbursal') from TransactionModel a " +
							"join a.transaction_details_list b where a.status=1 and a.office.id=:ofcid and a.transaction_type =:type and " +
							"a.date between :fromDate and :toDate and b.toAcct.id=:id  group by a.id")
							.setLong("ofcid", officeId)
							.setParameter("fromDate", fromDate)
							.setLong("id", id).setParameter("toDate", toDate)
							.setLong("type", SConstants.PAYROLL_PAYMENTS).list());
					
				}
				
				if(ttype==SConstants.EMPLOYEE_ADVANCE_PAYMENTS){
					
					result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
							"(select c.transaction_type from TransactionModel c where c.transaction_id=a.transaction_id ),a.id,'Advance Payments','',a.date,(select b.ledger.name from UserModel b where b.loginId.id=a.login_id and " +
							"b.ledger.office.id=:ofcid), sum(a.payment_amount),'','Advance Payments') " +
							"from EmployeeAdvancePaymentModel a where a.office.id=:ofcid and a.date between :fromDate and :toDate" +
							" and a.login_id=:id group by a.id")
							.setLong("ofcid", officeId)
							.setParameter("fromDate", fromDate)
							.setLong("id", id)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate).list());
					
				}
				
				
				
			}
			else if(type.equals("Income")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"6,a.id,'Income Transaction',cast(a.bill_no as string),a.date,b.fromAcct.name, sum(b.amount), a.ref_no,a.memo)" +
						" from PaymentDepositModel a join a.transaction.transaction_details_list b" +
						" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type " +
						"and a.date between :fromDate and :toDate and b.fromAcct.id=:id group by a.id")
						.setLong("ofcid", officeId).setParameter("fromDate", fromDate).setLong("id", id).setParameter("toDate", toDate).setLong("type", SConstants.INCOME_TRANSACTION).list());
				
			}
			
			else if(type.equals("Stock")) {
				result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"item.name,quantity,balance,item.unit.symbol,rate)" +
						" from ItemStockModel " +
						" where item.office.id=:ofcid and item.id=:id date_time between :fromDate and :toDate and balance>0 order by item.name ,date_time desc")
						.setLong("ofcid", officeId).setLong("id", id).setParameter("fromDate", new Timestamp(fromDate.getTime())).setParameter("toDate",  new Timestamp(toDate.getTime())).list());
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
		}
		return result;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getMainDetailsFromType(long officeId,Date fromDate, Date toDate, String type) throws Exception {
		List result=new ArrayList();
		try {
			
			begin();
			
			if(type.equals("Sale")) {
				List customerList=getSession().createQuery("from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
						.setParameter("ofc", officeId)
						.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
				
				if(customerList.size()>0){
					Iterator cusitr=customerList.iterator();
					while (cusitr.hasNext()) {
						CustomerModel cust=(CustomerModel)cusitr.next();
						double amount=0;
						List list=getSession().createQuery("from SalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger")
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setLong("ofc", officeId)
								.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							amount+=(Double)getSession().createQuery("select coalesce(sum(amount),0) from SalesModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger")
									.setParameter("fromDate", fromDate)
									.setParameter("ledger", cust.getLedger().getId())
									.setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
						
						List list1=getSession().createQuery("from RentalTransactionModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger and rent_type=2")
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setLong("ofc", officeId)
								.setParameter("toDate", toDate).list();
						
						if(list1.size()>0)
							amount+=(Double)getSession().createQuery("select coalesce(sum(amount),0) from RentalTransactionModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger and rent_type=2")
									.setParameter("fromDate", fromDate)
									.setParameter("ledger", cust.getLedger().getId())
									.setLong("ofc", officeId)
									.setParameter("toDate", toDate).uniqueResult();
						
						result.add(new AcctReportMainBean(cust.getLedger().getId(), cust.getLedger().getName(), CommonUtil.roundNumber(amount)));
					}
				}
			}
			else if(type.equals("Sales Return")) {
				
				List customerList=getSession().createQuery("from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
						.setParameter("ofc", officeId)
						.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
				
				if(customerList.size()>0){
					Iterator cusitr=customerList.iterator();
					while (cusitr.hasNext()) {
						CustomerModel cust=(CustomerModel)cusitr.next();
						
						List list=getSession().createQuery("from SalesReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(customer.id, customer.name , coalesce(sum(amount),0))" +
								" from SalesReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger")
										.setParameter("fromDate", fromDate)
										.setLong("ofc", officeId)
										.setParameter("ledger", cust.getLedger().getId())
										.setParameter("toDate", toDate).list());
					}
				}
			}
			
			else if(type.equals("Sales Return Item")) {
				
				List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE).list();
				
				if(itemsList.size()>0){
					Iterator itr=itemsList.iterator();
					while (itr.hasNext()) {
						ItemModel itemObj = (ItemModel) itr.next();
						
						List list=getSession().createQuery("from SalesReturnModel a join a.inventory_details_list b where a.date between :fromDate " +
								"and :toDate and a.office.id=:ofc and a.active=true and b.item.id=:item")
									.setParameter("fromDate", fromDate)
									.setLong("item", itemObj.getId())
									.setLong("ofc", officeId)
									.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
								"b.item.id, b.item.name, " +
								"coalesce(sum(b.good_stock/(b.quantity_in_basic_unit/b.balance)),0)," +
								"coalesce(sum(b.waste_quantity/(b.quantity_in_basic_unit/b.balance)),0)," +
								"coalesce(sum(b.returned_quantity/(b.quantity_in_basic_unit/b.balance)),0)," +
								"coalesce(sum(b.stock_quantity/(b.quantity_in_basic_unit/b.balance)),0))" +
								" from SalesReturnModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and a.office.id=:ofc " +
								"and a.active=true and b.item.id=:item")
										.setParameter("fromDate", fromDate)
										.setLong("item", itemObj.getId())
										.setLong("ofc", officeId)
										.setParameter("toDate", toDate).list());
					}
				}
			}
			
			else if(type.equals("Purchase")) {
				
				List suppList=getSession().createQuery("from SupplierModel where ledger.office.id=:ofc and ledger.status=:val order by name")
						.setParameter("ofc", officeId)
						.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
				
				if(suppList.size()>0){
					Iterator supitr=suppList.iterator();
					while (supitr.hasNext()) {
						SupplierModel cust=(SupplierModel)supitr.next();
						
						List list=getSession().createQuery(
								"from PurchaseModel where date between :fromDate and :toDate and office.id=:ofc and active=true and supplier.id=:ledger")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(supplier.id, supplier.name, coalesce(sum(amount),0)) " +
								"from PurchaseModel where date between :fromDate and :toDate and office.id=:ofc and active=true and supplier.id=:ledger")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
						
					}
					
				}
			}
			else if(type.equals("Purchase Return")) {
				
				List suppList=getSession().createQuery("from SupplierModel where ledger.office.id=:ofc and ledger.status=:val order by name")
						.setParameter("ofc", officeId)
						.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
				
				if(suppList.size()>0){
					Iterator supitr=suppList.iterator();
					while (supitr.hasNext()) {
						SupplierModel cust=(SupplierModel)supitr.next();
						
						List list=getSession().createQuery(
								"from PurchaseReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true and supplier.id=:ledger")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(supplier.id, supplier.name ,coalesce(sum(amount),0)) " +
								"from PurchaseReturnModel where date between :fromDate and :toDate and office.id=:ofc and active=true and supplier.id=:ledger")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
					}
				}
			}
			
			else if(type.equals("Purchase Return Item")) {
				
				List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE).list();
				if(itemsList.size()>0){
					Iterator itr=itemsList.iterator();
					while (itr.hasNext()) {
						ItemModel itemObj = (ItemModel) itr.next();
						
						List list=getSession().createQuery(
								"from PurchaseReturnModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and " +
								"a.office.id=:ofc and a.active=true and b.item.id=:item")
									.setParameter("fromDate", fromDate)
									.setLong("ofc", officeId)
									.setLong("item", itemObj.getId())
									.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							result.addAll(getSession().createQuery(
								"select new com.inventory.reports.bean.AcctReportMainBean(b.item.id, b.item.name, coalesce(sum(b.qunatity),0)) " +
								"from PurchaseReturnModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and " +
								"a.office.id=:ofc and a.active=true and b.item.id=:item")
									.setParameter("fromDate", fromDate)
									.setLong("ofc", officeId)
									.setLong("item", itemObj.getId())
									.setParameter("toDate", toDate).list());
					}
				}
			}
			
			
			else if(type.equals("Transportation")) {
				
				List transList=getSession().createQuery("from TranspotationModel where ledger.office.id=:ofc and ledger.status=:val order by name")
						.setParameter("ofc", officeId)
						.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
				if(transList.size()>0){
					Iterator transitr=transList.iterator();
					
					while (transitr.hasNext()) {
						TranspotationModel trans=(TranspotationModel)transitr.next();
						double amount=0;
						List list=getSession().createQuery(
								"from TransportationPaymentModel where date between :fromDate and :toDate and office.id=:ofc and active=true and type=2 and transportation_id=:ledger")
										.setParameter("fromDate", fromDate)
										.setLong("ofc", officeId)
										.setLong("ledger", trans.getLedger().getId())
										.setParameter("toDate", toDate).list();
						
						if(list.size()>0)
							amount+=(Double)getSession().createQuery(
								"select coalesce(sum(payment_amount),0) from TransportationPaymentModel where date between :fromDate and :toDate and office.id=:ofc and active=true and type=2 and transportation_id=:ledger")
										.setParameter("fromDate", fromDate)
										.setLong("ofc", officeId)
										.setLong("ledger", trans.getLedger().getId())
										.setParameter("toDate", toDate).uniqueResult();
						
						List list1=getSession().createQuery(
								"from RentalTransactionModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger and rent_type=1")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", trans.getLedger().getId())
								.setParameter("toDate", toDate).list();
						
						if(list1.size()>0)
							amount+=(Double)getSession().createQuery(
								"select coalesce(sum(amount),0) from RentalTransactionModel where date between :fromDate and :toDate and office.id=:ofc and active=true and customer.id=:ledger and rent_type=1")
								.setParameter("fromDate", fromDate)
								.setLong("ofc", officeId)
								.setParameter("ledger", trans.getLedger().getId())
								.setParameter("toDate", toDate).uniqueResult();
						result.add(new AcctReportMainBean(trans.getLedger().getId(), trans.getLedger().getName(), CommonUtil.roundNumber(amount)));
					}
				}
			}
			else if(type.equals("Expenses")) {
				
				List list= getSession().createQuery("from LedgerModel where group.account_class_id=:expense and office_id=:ofcid and type=:typ ")
						.setLong("ofcid", officeId)
						.setInteger("typ", SConstants.LEDGER_ADDED_DIRECTLY)
						.setParameter("expense", Long.parseLong("4")).list();
				if(list.size()>0){
					Iterator it=list.iterator();
					while (it.hasNext()) {
						LedgerModel ledger = (LedgerModel) it.next();
						
						List lst=getSession().createQuery("from PaymentDepositModel a join a.transaction.transaction_details_list b where a.status=1 and a.office_id=:ofcid and a.active=true " +
								"and a.type =:type and a.date between :fromDate and :toDate and b.toAcct.id=:ledger")
								.setLong("ofcid", officeId)
								.setLong("ledger", ledger.getId())
								.setParameter("fromDate", fromDate)
								.setParameter("toDate", toDate)
								.setLong("type", SConstants.EXPENDETURE_TRANSACTION).list();
						
						if(lst.size()>0)
							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(a.transaction.transaction_type,b.toAcct.id, b.toAcct.name,  coalesce(sum(b.amount),0)) " +
								"from PaymentDepositModel a join a.transaction.transaction_details_list b where a.status=1 and a.office_id=:ofcid and a.active=true " +
								"and a.type =:type and a.date between :fromDate and :toDate and b.toAcct.id=:ledger")
								.setLong("ofcid", officeId)
								.setLong("ledger", ledger.getId())
								.setParameter("fromDate", fromDate)
								.setParameter("toDate", toDate)
								.setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
						
					}
				}
				
//				List userlist= getSession().createQuery("from UserModel where loginId.office.id=:ofc and salary_type!=0 order by first_name")
//											.setLong("ofc", officeId).list();
//				if(userlist.size()>0){
//					Iterator it=userlist.iterator();
//					while (it.hasNext()) {
//						UserModel user = (UserModel) it.next();
//						
//						List lst=getSession().createQuery("from TransactionModel a join a.transaction_details_list b where a.status=1 and a.office.id=:ofcid and" +
//								" a.transaction_type =:type and a.date between :fromDate and :toDate and b.toAcct.id=:ledger ")
//								.setLong("ofcid", officeId)
//								.setLong("ledger", user.getLedger().getId())
//								.setParameter("fromDate", fromDate)
//								.setParameter("toDate", toDate)
//								.setLong("type", SConstants.PAYROLL_PAYMENTS).list();
//						
//						if(lst.size()>0)
//							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(a.transaction_type,b.toAcct.id, b.toAcct.name,  coalesce(sum(b.amount),0)) " +
//								"from TransactionModel a join a.transaction_details_list b where a.status=1 and a.office.id=:ofcid and" +
//								" a.transaction_type =:type and a.date between :fromDate and :toDate and b.toAcct.id=:ledger ")
//								.setLong("ofcid", officeId)
//								.setLong("ledger", user.getLedger().getId())
//								.setParameter("fromDate", fromDate)
//								.setParameter("toDate", toDate)
//								.setLong("type", SConstants.PAYROLL_PAYMENTS).list());
//						
//					}
//				}
//				
//				if(userlist.size()>0){
//					Iterator it=userlist.iterator();
//					while (it.hasNext()) {
//						UserModel user = (UserModel) it.next();
//						
//						List lst=getSession().createQuery("from EmployeeAdvancePaymentModel a where a.office.id=:ofcid and a.date between :fromDate and :toDate and a.login_id=:ledger")
//								.setLong("ofcid", officeId)
//								.setLong("ledger", user.getLoginId().getId())
//								.setParameter("fromDate", fromDate)
//								.setParameter("fromDate", fromDate)
//								.setParameter("toDate", toDate).list();
//						
//						if(lst.size()>0)
//							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean" +
//									"((select c.transaction_type from TransactionModel c where c.transaction_id=a.transaction_id )," +
//									"a.login_id,(select b.ledger.name from UserModel b where b.loginId.id=a.login_id and b.ledger.office.id=:ofcid)," +
//									"coalesce(sum(a.payment_amount),0)) " +
//								"from EmployeeAdvancePaymentModel a where a.office.id=:ofcid and a.date between :fromDate and :toDate and a.login_id=:ledger")
//								.setLong("ofcid", officeId)
//								.setLong("ledger", user.getLoginId().getId())
//								.setParameter("fromDate", fromDate)
//								.setParameter("fromDate", fromDate)
//								.setParameter("toDate", toDate).list());
//						
//					}
//				}
//				
			}
			else if(type.equals("Income")) {
				
				List list= getSession().createQuery(" from LedgerModel where group.account_class_id=:income and office_id=:ofcid")
						.setLong("ofcid", officeId)
						.setParameter("income", Long.parseLong("3")).list();
				if(list.size()>0){
					Iterator it=list.iterator();
					while (it.hasNext()) {
						LedgerModel ledger = (LedgerModel) it.next();
						
						List lst=getSession().createQuery("from PaymentDepositModel a join a.transaction.transaction_details_list b where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type" +
								" and a.date between :fromDate and :toDate and b.fromAcct.id=:ledger")
								.setLong("ofcid", officeId)
								.setLong("ledger", ledger.getId())
								.setParameter("fromDate", fromDate)
								.setParameter("toDate", toDate)
								.setLong("type", SConstants.INCOME_TRANSACTION).list();
						
						if(lst.size()>0)
							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(b.fromAcct.id, b.fromAcct.name, coalesce(sum(b.amount),0))" +
								" from PaymentDepositModel a join a.transaction.transaction_details_list b where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type" +
								" and a.date between :fromDate and :toDate and b.fromAcct.id=:ledger")
								.setLong("ofcid", officeId)
								.setLong("ledger", ledger.getId())
								.setParameter("fromDate", fromDate)
								.setParameter("toDate", toDate)
								.setLong("type", SConstants.INCOME_TRANSACTION).list());
						
					}
				}
				
			}
			
			else if(type.equals("Stock")) {
				
				List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE).list();
				if(itemsList.size()>0){
					Iterator itr=itemsList.iterator();
					while (itr.hasNext()) {
						ItemModel itemObj = (ItemModel) itr.next();
						
						List lst=getSession().createQuery("from ItemStockModel where item.office.id=:ofcid and date_time between :fromDate and :toDate and balance>0 and item.id=:item")
								.setLong("ofcid", officeId)
								.setLong("item", itemObj.getId())
								.setParameter("fromDate", new Timestamp(fromDate.getTime()))
								.setParameter("toDate",  new Timestamp(toDate.getTime())).list();
						
						if(lst.size()>0)
							result.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(item.id, item.name,coalesce(sum(quantity),0),coalesce(sum(balance),0))" +
								" from ItemStockModel where item.office.id=:ofcid and date_time between :fromDate and :toDate and balance>0 and item.id=:item")
								.setLong("ofcid", officeId)
								.setLong("item", itemObj.getId())
								.setParameter("fromDate", new Timestamp(fromDate.getTime()))
								.setParameter("toDate",  new Timestamp(toDate.getTime())).list());
						
					}
				}
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
		}
		return result;
	}
	
	
	public TransactionModel getTransaction(long id) throws Exception {
		TransactionModel tran = null;
		try {
			begin();
			tran = (TransactionModel) getSession().get(TransactionModel.class,
					id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return tran;
	}
	
	
	public SalaryDisbursalNewModel getSalaryDisbursalNewModel(long id) throws Exception {
		SalaryDisbursalNewModel tran = null;
		try {
			begin();
			tran = (SalaryDisbursalNewModel) getSession().createQuery("from SalaryDisbursalNewModel where transaction_id="+id).uniqueResult();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return tran;
	}
	
}
