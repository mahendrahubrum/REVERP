package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.AcctReportMainBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class LedgerViewDao extends SHibernate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1502795200451828408L;
	private List resultList=new ArrayList();
	
	@SuppressWarnings("unchecked")
	public List getLedgerViewOld(Date start_date, Date end_date, long office_id, long ledger_id) throws Exception {
		
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			// Contsructor #9
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Cr',a.date,b.amount,a.transaction_type,b.fromAcct.name,b.toAcct.name) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
										"b.fromAcct.id=:led").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			// Contsructor #9
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Dr',a.date,b.amount,a.transaction_type,b.toAcct.name,b.fromAcct.name) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
										"b.toAcct.id=:led").setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			commit();
			
		
			
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	@SuppressWarnings("unchecked")
	public List getLedgerView(Date start_date, Date end_date, long office_id, long ledger_id,long departmentid,long divisionId) throws Exception {
		
		try {
			
			resultList=new ArrayList();
			List transList=new ArrayList();
			
			String condition="";
			if(departmentid!=0)
				condition+=" and b.departmentId="+departmentid;
			if(divisionId!=0)
				condition+=" and b.divisionId="+divisionId;
			
			begin();
			
			// Contsructor #9
			transList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Cr',a.date,b.amount,a.transaction_type,b.fromAcct.name,b.toAcct.name) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
										"b.fromAcct.id=:led "+condition).setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			// Contsructor #9
			transList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Dr',a.date,b.amount,a.transaction_type,b.toAcct.name,b.fromAcct.name) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
										"b.toAcct.id=:led "+condition).setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
		
			
			AcctReportMainBean mainBean;
			AcctReportMainBean repBean=null;
			Iterator iter=transList.iterator();
			
			while (iter.hasNext()) {
				mainBean = (AcctReportMainBean) iter.next();
			if(mainBean!=null){
					repBean=null;
			switch (mainBean.getTransaction_type()) {
			// Contsructor #26
			
			case SConstants.SALES:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,sales_number,comments)" +
						" from SalesModel a where  a.transaction_id=:trans ")
					.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
			case SConstants.PURCHASE:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,purchase_no,comments)" +
						"from PurchaseModel where transaction_id=:trans")
					.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
			case SConstants.JOURNAL:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,remarks)" +" from JournalModel where transaction_id=:trans")
				.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
			case SConstants.PURCHASE_RETURN:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,return_no,comments)"+" from PurchaseReturnModel where transaction_id=:trans")
					.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
			case SConstants.SALES_RETURN:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,return_no,comments)"+" from SalesReturnModel where transaction_id=:trans")
				.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.BANK_ACCOUNT_PAYMENTS:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from BankAccountPaymentModel where transactionId=:trans")
					.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
			case SConstants.BANK_ACCOUNT_DEPOSITS:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from BankAccountDepositModel where transactionId=:trans")
					.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.CASH_ACCOUNT_DEPOSITS:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from CashAccountDepositModel where transactionId=:trans")
					.setParameter("trans", mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.CASH_ACCOUNT_PAYMENTS:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from CashAccountPaymentModel where transactionId=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.CREDIT_NOTE:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from CreditNoteModel where transactionId=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.DEBIT_NOTE:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from DebitNoteModel where transactionId=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.PDC_PAYMENT:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,bill_no,memo)"+" from PdcPaymentModel where transactionId=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.EMPLOYEE_ADVANCE_PAYMENTS:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,cast(payment_id as string) ,description)"+" from EmployeeAdvancePaymentModel where transaction_id=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.SALARY_LOAN:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,loanRequest.requestNo ,' ')"+" from LoanApprovalModel where transactionId=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
			case SConstants.PAYROLL_PAYMENTS:
				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"id,' ' ,' ')"+" from SalaryDisbursalModel where transactionId=:trans")
					.setParameter("trans",  mainBean.getId()).uniqueResult();
				break;
				
//			case SConstants.FIXED_ASSET:
//				repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//						"id,' ' ,' ')"+" from SalaryDisbursalModel where transactionId=:trans")
//						.setParameter("trans",  mainBean.getId()).uniqueResult();
//				break;
				
				
				
//			case SConstants.SUPPLIER_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from PaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.CUSTOMER_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from PaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
		
//			case SConstants.EXPENDETURE_TRANSACTION:
//				id=(Long) getSession().createQuery("select id from PaymentDepositModel where transaction.transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.INCOME_TRANSACTION:
//				id=(Long) getSession().createQuery("select id from PaymentDepositModel where transaction.transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.CONTRACTOR_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from PaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.TRANSPORTATION_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from TransportationPaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.EMPLOYEE_ADVANCE_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from EmployeeAdvancePaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.PAYROLL_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from SalaryDisbursalNewModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.INVESTMENT:
//				id=(Long) getSession().createQuery("select id from CashInvestmentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.COMMISSION_PURCHASE:
//				id=(Long) getSession().createQuery("select id from CommissionPurchaseModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.COMMISSION_SALES:
//				id=(Long) getSession().createQuery("select id from CommissionSalesNewModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.RENT_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from RentPaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.SUBSCRIPTION_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from SubscriptionPaymentModel where transaction_id=:trans")
//				.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.COMMISSION_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from CommissionPaymentModel where transaction_id=:trans")
//				.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.TRANSPORTATION_EXPENDITUE:
//				id=(Long) getSession().createQuery("select id from SubscriptionExpenditureModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.RENTAL_TRANSACTION:
//				id=(Long) getSession().createQuery("select id from RentalTransactionModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.RENTAL_PAYMENTS:
//				id=(Long) getSession().createQuery("select id from RentalPaymentModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
//			case SConstants.COMMISSION_SALARY:
//				id=(Long) getSession().createQuery("select id from CommissionSalaryModel where transaction_id=:trans")
//					.setParameter("trans", mainBean.getId()).uniqueResult();
//				break;
				
				
			}
			if(repBean!=null){
			repBean.setId(mainBean.getId());
			repBean.setAmount_type(mainBean.getAmount_type());
			repBean.setDate(mainBean.getDate());
			repBean.setAmount(mainBean.getAmount());
			repBean.setTransaction_type(mainBean.getTransaction_type());
			repBean.setName(mainBean.getName());
			repBean.setFrom_or_to(mainBean.getFrom_or_to());
			resultList.add(repBean);
			}
			
			flush();
				}
		
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	
	public List getAllActiveLedgerNamesUnderGroup(long group_id) throws Exception {
		List lst=null;
		try {
			lst = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)" +
					" from LedgerModel where group.id=:grpid and status=:val")
					.setParameter("grpid", group_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
		
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
	}
	
	
	public List getAllGroupsUnderClass(long class_id, long office_id) throws Exception {
		List lst=null;
		try {
			lst = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id,name) " +
					" from GroupModel where office.id=:ofcid and status=:sts and account_class_id=:cls")
					.setLong("ofcid", office_id).setLong("sts", 1).setLong("cls", class_id).list();
			
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
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
			
			Object ledgOpBalCr=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:cr and date <:stdt " +
					" and id=(select max(id) from LedgerOpeningBalanceModel where date <:stdt and ledger.id=:id)")
					.setLong("id", ledger_id).setDate("stdt", start_date).setParameter("cr", SConstants.CR).uniqueResult();
			
			Object ledgOpBalDr=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:dr and date <:stdt " +
					" and id=(select max(id) from LedgerOpeningBalanceModel where date <:stdt and ledger.id=:id)")
					.setLong("id", ledger_id).setDate("stdt", start_date).setParameter("dr", SConstants.CR).uniqueResult();;
			
			
			if(objDr!=null)
				op_bal+=(Double)objDr;
			
			if(objCr!=null)
				op_bal-=(Double)objCr;
			
			if(ledgOpBalCr!=null)
				op_bal-=(Double)ledgOpBalCr;
			
			if(ledgOpBalDr!=null)
				op_bal+=(Double)ledgOpBalDr;
			
			
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
	
	
	
	public double getLedgerBalance(Date start_date, long ledger_id) throws Exception {
		double op_bal=0;
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			Object objDr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
					"b.toAcct.id =:led)").setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			Object objCr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
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
	
	
	
	@SuppressWarnings("unchecked")
	public List getTransportationPaymentReport(Date start_date, Date end_date, long office_id, 
			long ledger_id, boolean useToDt) throws Exception {
		
		try {
			
			String dat="a.date";
			String dat1="a.date";
			if(useToDt){
				dat="a.to_date";
				dat1="a.date";
			}
				
			
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Cr',a.date,a.payment_amount,0, (select b.name from LedgerModel b where b.id=a.transportation_id)) from TransportationPaymentModel a where "+dat+" between :stdt and :enddt  and " +
										"a.transportation_id=:led and a.type=2 and a.office.id=:office").setLong("led", ledger_id).setLong("office", office_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Dr',a.date,a.payment_amount,0, (select b.name from LedgerModel b where b.id=a.transportation_id)) from TransportationPaymentModel a where "+dat+" between :stdt and :enddt  and " +
										"a.transportation_id=:led and a.type=1 and a.office.id=:office").setLong("led", ledger_id).setLong("office", office_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Dr',a.date,a.amount,0, customer.name) from RentalTransactionModel a where a.date between :stdt and :enddt  and " +
										"a.customer.id=:led and a.rent_type=1 and a.office.id=:office").setLong("led", ledger_id).setLong("office", office_id).setDate("stdt", start_date).setDate("enddt", end_date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
					"a.id,'Cr',a.date,a.payment_amount,0, (select b.name from LedgerModel b where b.id=a.from_account_id)) from RentalPaymentModel a where "+dat1+" between :stdt and :enddt  and " +
										"a.from_account_id=:led and a.type=:typ and a.office.id=:office").setParameter("typ", SConstants.RENTAL_PAYMENTS).setLong("led", ledger_id).setLong("office", office_id).setDate("stdt", start_date).setDate("enddt", end_date).list());

			
			commit();
			
			return resultList;
			
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
		
		
	}
	
	
	/*public List getTransportationLedgerView(Date start_date, Date end_date, List ledger_ids) throws Exception {
		try {
			resultList=new ArrayList();
			
			begin();
			
			if(ledger_ids.size()>0) {
			
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Cr',a.date,b.amount,a.transaction_type,b.fromAcct.name) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt and " +
											"b.fromAcct.id in (:led)").setParameterList("led", ledger_ids).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Dr',a.date,b.amount,a.transaction_type,b.toAcct.name) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt and " +
											"b.toAcct.id in (:led)").setParameterList("led", ledger_ids).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
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
		
		
	}*/
	
	
	@SuppressWarnings("unchecked")
	public List getTransportationLedgerView(Date start_date, Date end_date, List ledger_ids, boolean isToDat,long office) throws Exception {
		try {
			resultList=new ArrayList();
			
			String dat="a.date";
			String dat1="a.date";
			if(isToDat)
				dat="a.to_date";
			
			begin();
			
			if(ledger_ids.size()>0) {
				
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Cr',a.date,a.payment_amount,0, (select b.name from LedgerModel b where b.id=a.transportation_id)) from TransportationPaymentModel a where "+dat+" between :stdt and :enddt  and " +
											"a.transportation_id in (:led) and a.type=2 and a.office.id=:office").setLong("office", office).setParameterList("led", ledger_ids).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Dr',a.date,a.payment_amount,0, (select b.name from LedgerModel b where b.id=a.transportation_id)) from TransportationPaymentModel a where "+dat+" between :stdt and :enddt  and " +
											"a.transportation_id in (:led) and a.type=1 and a.office.id=:office").setLong("office", office).setParameterList("led", ledger_ids).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Dr',a.date,a.amount,0, customer.name) from RentalTransactionModel a where a.date between :stdt and :enddt  and " +
											"a.customer.id in (:led) and a.rent_type=1 and a.office.id=:office").setParameterList("led", ledger_ids).setLong("office", office).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
				resultList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Cr',a.date,a.payment_amount,0, (select b.name from LedgerModel b where b.id=a.from_account_id)) from RentalPaymentModel a where "+dat1+" between :stdt and :enddt  and " +
											"a.from_account_id in (:led) and a.type=:typ and a.office.id=:office").setParameter("typ", SConstants.RENTAL_PAYMENTS).setParameterList("led", ledger_ids).setLong("office", office).setDate("stdt", start_date).setDate("enddt", end_date).list());
				
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

	public long getIdFromTransaction(long transId, int transaction_type) throws Exception {
		long id=0;
		try{
			
		begin();
		switch (transaction_type) {
		
		case SConstants.SALES:
			id=(Long) getSession().createQuery("select id from SalesModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.PURCHASE:
			id=(Long) getSession().createQuery("select id from PurchaseModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.JOURNAL:
			id=(Long) getSession().createQuery("select id from JournalModel where transaction_id=:trans")
			.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.PURCHASE_RETURN:
			id=(Long) getSession().createQuery("select id from PurchaseReturnModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.SALES_RETURN:
			id=(Long) getSession().createQuery("select id from SalesReturnModel where transaction_id=:trans")
			.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.SUPPLIER_PAYMENTS:
			id=(Long) getSession().createQuery("select id from PaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.CUSTOMER_PAYMENTS:
			id=(Long) getSession().createQuery("select id from PaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.BANK_ACCOUNT_PAYMENTS:
			id=(Long) getSession().createQuery("select id from BankAccountPaymentModel where transactionId=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.BANK_ACCOUNT_DEPOSITS:
			id=(Long) getSession().createQuery("select id from BankAccountDepositModel where transactionId=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.EXPENDETURE_TRANSACTION:
			id=(Long) getSession().createQuery("select id from PaymentDepositModel where transaction.transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.INCOME_TRANSACTION:
			id=(Long) getSession().createQuery("select id from PaymentDepositModel where transaction.transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.CONTRACTOR_PAYMENTS:
			id=(Long) getSession().createQuery("select id from PaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.TRANSPORTATION_PAYMENTS:
			id=(Long) getSession().createQuery("select id from TransportationPaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.EMPLOYEE_ADVANCE_PAYMENTS:
			id=(Long) getSession().createQuery("select id from EmployeeAdvancePaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.PAYROLL_PAYMENTS:
			id=(Long) getSession().createQuery("select id from SalaryDisbursalNewModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.INVESTMENT:
			id=(Long) getSession().createQuery("select id from CashInvestmentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.COMMISSION_PURCHASE:
			id=(Long) getSession().createQuery("select id from CommissionPurchaseModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.COMMISSION_SALES:
			id=(Long) getSession().createQuery("select id from CommissionSalesNewModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.RENT_PAYMENTS:
			id=(Long) getSession().createQuery("select id from RentPaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.SUBSCRIPTION_PAYMENTS:
			id=(Long) getSession().createQuery("select id from SubscriptionPaymentModel where transaction_id=:trans")
			.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.COMMISSION_PAYMENTS:
			id=(Long) getSession().createQuery("select id from CommissionPaymentModel where transaction_id=:trans")
			.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.TRANSPORTATION_EXPENDITUE:
			id=(Long) getSession().createQuery("select id from SubscriptionExpenditureModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.RENTAL_TRANSACTION:
			id=(Long) getSession().createQuery("select id from RentalTransactionModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.RENTAL_PAYMENTS:
			id=(Long) getSession().createQuery("select id from RentalPaymentModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		case SConstants.COMMISSION_SALARY:
			id=(Long) getSession().createQuery("select id from CommissionSalaryModel where transaction_id=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
			
		case SConstants.CASH_ACCOUNT_DEPOSITS:
			id=(Long) getSession().createQuery("select id from CashAccountDepositModel where transactionId=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
			
		case SConstants.CASH_ACCOUNT_PAYMENTS:
			id=(Long) getSession().createQuery("select id from CashAccountPaymentModel where transactionId=:trans")
				.setParameter("trans", transId).uniqueResult();
			break;
		}
		
			commit();
			
			
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
	return id;
	}
}
