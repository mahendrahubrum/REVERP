package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class DayBookDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -652923603119519095L;
	private List resultList=new ArrayList();
	
	
	public List getDayBook(Date start_date, long office_id, long org_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();	
			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"id,'Purchase Order',order_no, date, supplier.name, amount,ref_no,comments ) from PurchaseOrderModel where date =:stdt and active=true " +
//										" and office.id=:ofcid").setLong("ofcid", office_id).setDate("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Purchase',purchase_no, date, supplier.name, amount,'',comments ) from PurchaseModel where date =:stdt and active=true " +
										" and office.id=:ofcid").setLong("ofcid", office_id).setDate("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Purchase Return',return_no,date,supplier.name , amount,ref_no,comments)"
					+ " from PurchaseReturnModel where active=true and " +
					"date =:stdt and office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id).list());
			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"id,'Sales Order',order_no, date, customer.name,amount,ref_no,comments)" +
//					" from SalesOrderModel where date=:stdt and active=true" +
//										" and office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Delivery Note',deliveryNo, date, customer.name, amount,ref_no,comments)" +
					" from DeliveryNoteModel where date=:stdt and active=true" +
										" and office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sale',sales_number, date, customer.name, amount,'',comments) from SalesModel where date=:stdt and active=true" +
										" and office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Sales Return',return_no,date,customer.name , amount,ref_no,comments)"
					+ " from SalesReturnModel where status=1 and active=true and date =:stdt and office.id=:ofcid")
					.setParameter("stdt", start_date).setLong("ofcid", office_id).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Journal',a.bill_no,a.date,b.ledger.name, sum(b.amount), a.ref_no,a.bill_no)" +
					" from JournalModel a join a.journal_details_list b" +
									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());

			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Bank Acct. Payment',a.bill_no,a.date,a.bankAccount.name, b.amount, a.ref_no,a.bill_no)" +
					" from BankAccountPaymentModel a join a.bank_account_payment_list b" +
						" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
							.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Bank Acct. Deposit',a.bill_no,a.date,a.bankAccount.name, sum(b.amount), a.ref_no,a.bill_no)" +
					" from BankAccountDepositModel a join a.bank_account_deposit_list b" +
									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Employee Advance Payment',cast(a.payment_id as string),a.date," +
					"a.user.first_name," +
					"a.amount, '',a.description)" +
					" from EmployeeAdvancePaymentModel a where a.date=:stdt and a.active=true and " +
										" a.office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"id,'Salary Disbursal',cast(id as string),dispursal_date,user.first_name,(payroll + overtime + commission - lop - advance - loan ),'','')" +
					" from SalaryDisbursalModel where dispursal_date=:stdt and " +
										" officeId=:officeId").setDate("stdt", start_date).setLong("officeId", office_id).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Cash Acct. Deposit',a.bill_no,a.date,a.cashAccount.name, sum(b.amount), a.ref_no,a.bill_no)" +
					" from CashAccountDepositModel a join a.cash_account_deposit_list b" +
									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Cash Acct. Payment',a.bill_no,a.date,a.cashAccount.name, sum(b.amount), a.ref_no,a.bill_no)" +
					" from CashAccountPaymentModel a join a.cash_account_payment_list b" +
									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Credit Note',a.bill_no,a.date,a.ledger.name, sum(b.amount), a.ref_no,a.memo)" +
					" from CreditNoteModel a join a.credit_note_list b" +
									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Debit Note',a.bill_no,a.date,a.ledger.name, sum(b.amount), a.ref_no,a.memo)" +
					" from DebitNoteModel a join a.debit_note_list b" +
									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"a.id,'PDC Payment',a.bill_no,a.date,a.ledger.name, sum(b.amount), a.ref_no,a.memo)" +
//					" from PdcPaymentModel a join a.pdc_payment_list b" +
//									" where a.office_id=:ofcid and a.active=true and a.date =:stdt group by a.id")
//					.setLong("ofcid", office_id).setParameter("stdt", start_date).list());
//			
//=========================================================================================================
//			try {
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"id,'Commission Sale', cast(number as string), received_date, supplier.name, net_sale,'',details ) from CommissionSalesModel where received_date =:stdt " +
//										" and office.id=:ofcid").setLong("ofcid", office_id).setDate("stdt", start_date).list());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//			"id,'Supplier Payment',cast(payment_id as string),date,(select name from LedgerModel where id=to_account_id)," +
//			"payment_amount, '',description, case when cash_or_check=1 then '' else cast(cheque_date as string) end ) from PaymentModel where date =:stdt and " +
//								" type=:typ and active=true and office.id=:ofcid").setLong("ofcid", office_id).setDate("stdt", start_date)
//								.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).list());
			
//			try {
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//						"a.id,'Customer Commission Sale',cast(a.sales_no as string),a.date, '', a.amount ,a.ref_no,a.comments)" +
//						" from CustomerCommissionSalesModel a join a.details_list b where a.date =:stdt and a.office.id=:ofcid group by a.id" +
//											"").setDate("stdt", start_date).setLong("ofcid", office_id).list());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"id,'Customer Payment',cast(payment_id as string),date,(select name from LedgerModel where id=from_account_id),payment_amount, '',description, case when cash_or_check=1 then '' else cast(cheque_date as string) end) from PaymentModel where date=:stdt and active=true and " +
//										"type=:typ and office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id)
//										.setParameter("typ", SConstants.CUSTOMER_PAYMENTS).list());
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"id,'Transportation Payment',cast(payment_id as string),date,(select name from LedgerModel where id=transportation_id),payment_amount, '',description, case when cash_or_check=1 then '' else cast(cheque_date as string) end) from TransportationPaymentModel where date =:stdt and " +
//										" active=true  and office.id=:ofcid").setDate("stdt", start_date).setLong("ofcid", office_id).list());
//			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"a.id,'Expenditure Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo)" +
//					" from PaymentDepositModel a join a.transaction.transaction_details_list b" +
//									" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date =:stdt group by a.id")
//					.setLong("ofcid", office_id).setParameter("stdt", start_date).setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
//			
//			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//					"a.id,'Income Transaction',cast(a.bill_no as string),a.date,b.toAcct.name, sum(b.amount), a.ref_no,a.memo) from PaymentDepositModel a join a.transaction.transaction_details_list b" +
//									" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date =:stdt group by a.id")
//					.setLong("ofcid", office_id).setParameter("stdt", start_date).setLong("type", SConstants.INCOME_TRANSACTION).list());
			
//			try {
//				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(id,'Subscription', " +
//						"'',payment_date, subscription.name,COALESCE(sum(amount_paid),0),'','') "
//						+ " from SubscriptionPaymentModel  where payment_date =:stdt and subscription.subscription_type.officeId="+office_id+" group by subscription.id")
//						.setParameter("stdt", start_date).list());
//			} catch (Exception e) {
//			}
			
			
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
	
	
	public SalaryDisbursalNewModel getSalaryDesbursal(long id) throws Exception {
		SalaryDisbursalNewModel model = null;
		try {
			begin();
			model = (SalaryDisbursalNewModel) getSession().get(
					SalaryDisbursalNewModel.class, id);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return model;
	}
	
	
	
}
