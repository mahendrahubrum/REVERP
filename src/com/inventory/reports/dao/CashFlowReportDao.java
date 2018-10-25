package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.model.SalaryDisbursalNewModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 *   Inventory
 *   Nov 21, 2013
 */

/**
 * @author Anil K P
 * @date 20-Oct-2015
 * @Project REVERP
 */

/**
 * @author sangeeth
 * @date 28-Oct-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class CashFlowReportDao extends SHibernate implements Serializable{
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getCashFlowReport(long orgId, long officeId, long ledger, long departmentid, long divisionId, Date start, Date end, SettingsValuePojo settings) throws Exception {
		List<AcctReportMainBean> resultList=new ArrayList<AcctReportMainBean>();
		try {
			begin();
			List transList=new ArrayList();
			List cashList=new ArrayList();
			String cdn="";
			if(ledger!=0)
				cdn=" and id="+ledger;
			cashList=getSession().createQuery("from LedgerModel where office.id=:ofc and group.id=:grp"+cdn)
										.setLong("ofc",officeId).setLong("grp",settings.getCASH_GROUP()).list();
			String condition="";
			if(departmentid!=0)
				condition+=" and b.departmentId="+departmentid;
			if(divisionId!=0)
				condition+=" and b.divisionId="+divisionId;
			
			LedgerModel ledgMdl;
			AcctReportMainBean mainBean;
			AcctReportMainBean repBean=null;
			
			Iterator cashIter=cashList.iterator();
			
			while (cashIter.hasNext()) {
				ledgMdl = (LedgerModel) cashIter.next();
				
				// Contsructor #27
				transList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Cr',a.date,b.amount,a.transaction_type,b.fromAcct.name,b.toAcct.name,b.conversionRate,b.currencyId) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
											"b.fromAcct.id=:led "+condition).setLong("led", ledgMdl.getId()).setDate("stdt", start).setDate("enddt",  end).list());
				
				// Contsructor #27
				transList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Dr',a.date,b.amount,a.transaction_type,b.toAcct.name,b.fromAcct.name,b.conversionRate,b.currencyId) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
											"b.toAcct.id=:led "+condition).setLong("led", ledgMdl.getId()).setDate("stdt", start).setDate("enddt",  end).list());
			}
			
			Iterator iter=transList.iterator();
			
			while (iter.hasNext()) {
				mainBean = (AcctReportMainBean) iter.next();
				if(mainBean!=null){
					repBean=null;
					switch (mainBean.getTransaction_type()) {
					// Contsructor #26
					
						case SConstants.SALES:
												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
														"a.id,a.sales_number,a.comments) from SalesModel a where  a.transaction_id=:trans ")
														.setParameter("trans", mainBean.getId()).uniqueResult();
												break;
						case SConstants.PURCHASE:
												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
														"a.id,a.purchase_no,a.comments)" +
														"from PurchaseModel a where a.transaction_id=:trans")
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
					
					
						}
						if(repBean!=null){
							repBean.setId(mainBean.getId());
							repBean.setRate(mainBean.getRate());
							repBean.setCurrencyId(mainBean.getCurrencyId());
							repBean.setAmount_type(mainBean.getAmount_type());
							repBean.setDate(mainBean.getDate());
							repBean.setAmount(mainBean.getAmount());
							repBean.setTransaction_type(mainBean.getTransaction_type());
							repBean.setName(mainBean.getName());
							repBean.setFrom_or_to(mainBean.getFrom_or_to());
							resultList.add(repBean);
						}
					}
				}
			
				commit();
			
			return resultList;
			
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getCashFlowReportChart(long officeId,Date start, Date end,SettingsValuePojo settings) throws Exception {
		List<AcctReportMainBean> resultList=new ArrayList<AcctReportMainBean>();
		try {
			begin();
			List transList=new ArrayList();
			List cashList=new ArrayList();
			String cdn="";
			
			cashList=getSession().createQuery("from LedgerModel where office.id=:ofc and group.id=:grp"+cdn)
										.setLong("ofc",officeId).setLong("grp",settings.getCASH_GROUP()).list();
			String condition="";
					
			LedgerModel ledgMdl;
			AcctReportMainBean mainBean;
			AcctReportMainBean repBean=null;
			
			Iterator cashIter=cashList.iterator();
			
			while (cashIter.hasNext()) {
				ledgMdl = (LedgerModel) cashIter.next();
				
				// Contsructor #27
				transList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Cr',a.date,b.amount,a.transaction_type,b.fromAcct.name,b.toAcct.name,b.conversionRate,b.currencyId) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
											"b.fromAcct.id=:led "+condition).setLong("led", ledgMdl.getId()).setDate("stdt", start).setDate("enddt",  end).list());
				
				// Contsructor #27
				transList.addAll(getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
						"a.id,'Dr',a.date,b.amount,a.transaction_type,b.toAcct.name,b.fromAcct.name,b.conversionRate,b.currencyId) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt  and " +
											"b.toAcct.id=:led "+condition).setLong("led", ledgMdl.getId()).setDate("stdt", start).setDate("enddt",  end).list());
			}
			
//			Iterator iter=transList.iterator();
//			
//			while (iter.hasNext()) {
//				mainBean = (AcctReportMainBean) iter.next();
//				if(mainBean!=null){
//					repBean=null;
//					switch (mainBean.getTransaction_type()) {
//					// Contsructor #26
//					
//						case SConstants.SALES:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"a.id,a.sales_number,a.comments) from SalesModel a where  a.transaction_id=:trans ")
//														.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//						case SConstants.PURCHASE:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"a.id,a.purchase_no,a.comments)" +
//														"from PurchaseModel a where a.transaction_id=:trans")
//													.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//						case SConstants.JOURNAL:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,remarks)" +" from JournalModel where transaction_id=:trans")
//												.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//						case SConstants.PURCHASE_RETURN:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,return_no,comments)"+" from PurchaseReturnModel where transaction_id=:trans")
//													.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//						case SConstants.SALES_RETURN:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,return_no,comments)"+" from SalesReturnModel where transaction_id=:trans")
//												.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//							
//						case SConstants.BANK_ACCOUNT_PAYMENTS:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from BankAccountPaymentModel where transactionId=:trans")
//													.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//						case SConstants.BANK_ACCOUNT_DEPOSITS:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from BankAccountDepositModel where transactionId=:trans")
//													.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//							
//						case SConstants.CASH_ACCOUNT_DEPOSITS:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from CashAccountDepositModel where transactionId=:trans")
//													.setParameter("trans", mainBean.getId()).uniqueResult();
//												break;
//							
//						case SConstants.CASH_ACCOUNT_PAYMENTS:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from CashAccountPaymentModel where transactionId=:trans")
//													.setParameter("trans",  mainBean.getId()).uniqueResult();
//												break;
//							
//						case SConstants.CREDIT_NOTE:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from CreditNoteModel where transactionId=:trans")
//													.setParameter("trans",  mainBean.getId()).uniqueResult();
//												break;
//							
//						case SConstants.DEBIT_NOTE:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from DebitNoteModel where transactionId=:trans")
//													.setParameter("trans",  mainBean.getId()).uniqueResult();
//												break;
//							
//							
//						case SConstants.PDC_PAYMENT:
//												repBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
//														"id,bill_no,memo)"+" from PdcPaymentModel where transactionId=:trans")
//													.setParameter("trans",  mainBean.getId()).uniqueResult();
//												break;
//					
//					
//						}
//						if(repBean!=null){
//							repBean.setId(mainBean.getId());
//							repBean.setRate(mainBean.getRate());
//							repBean.setCurrencyId(mainBean.getCurrencyId());
//							repBean.setAmount_type(mainBean.getAmount_type());
//							repBean.setDate(mainBean.getDate());
//							repBean.setAmount(mainBean.getAmount());
//							repBean.setTransaction_type(mainBean.getTransaction_type());
//							repBean.setName(mainBean.getName());
//							repBean.setFrom_or_to(mainBean.getFrom_or_to());
//							resultList.add(repBean);
//						}
//					}
//				}
			
				commit();
			
			return transList;
			
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	
	@SuppressWarnings("rawtypes")
	public double getCashFlowOpeningBalance(long officeId, long leder,  Date start_date, SettingsValuePojo settings) throws Exception {
		double openingBalance=0;
		try {
			
			begin();
			List cashList=new ArrayList();
			
			String cdn="";
			if(leder!=0)
				cdn=" and id="+leder;
			cashList=getSession().createQuery("from LedgerModel where office.id=:ofc and group.id=:grp"+cdn)
										.setLong("ofc",officeId).setLong("grp",settings.getCASH_GROUP()).list();
			
			
			Iterator itr=cashList.iterator();
			while (itr.hasNext()) {
				LedgerModel ledger= (LedgerModel) itr.next();
				
				Object objDr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.toAcct.id =:led)").setLong("led", ledger.getId()).setDate("stdt", start_date).uniqueResult();
				
				Object objCr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.fromAcct.id =:led)").setLong("led", ledger.getId()).setDate("stdt", start_date).uniqueResult();
				
				Object ledgOpBalCr=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:cr and date <:stdt " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:stdt and ledger.id=:id)")
						.setLong("id", ledger.getId()).setDate("stdt", start_date).setParameter("cr", SConstants.CR).uniqueResult();
				
				Object ledgOpBalDr=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:dr and date <:stdt " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:stdt and ledger.id=:id)")
						.setLong("id", ledger.getId()).setDate("stdt", start_date).setParameter("dr", SConstants.CR).uniqueResult();;
				
				
				if(objDr!=null)
					openingBalance+=(Double)objDr;
				
				if(objCr!=null)
					openingBalance-=(Double)objCr;
				
				if(ledgOpBalCr!=null)
					openingBalance-=(Double)ledgOpBalCr;
				
				if(ledgOpBalDr!=null)
					openingBalance+=(Double)ledgOpBalDr;
				
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
		return openingBalance;
	}
	

	public long getIdFromTransaction(int type, long tid) throws Exception {
		long id=0;
		Object obj=null;
		try {
			begin();
			switch (type) {
						case SConstants.SALES:
											obj=getSession().createQuery("select id from SalesModel where transaction_id="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
											
						case SConstants.PURCHASE:
											obj=getSession().createQuery("select id from PurchaseModel where transaction_id="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
											
						case SConstants.JOURNAL:
											obj=getSession().createQuery("select id from JournalModel where transaction_id="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
											
						case SConstants.PURCHASE_RETURN:
											obj=getSession().createQuery("select id from PurchaseReturnModel where transaction_id="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
											
						case SConstants.SALES_RETURN:
											obj=getSession().createQuery("select id from SalesReturnModel where transaction_id="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						case SConstants.BANK_ACCOUNT_PAYMENTS:
											obj=getSession().createQuery("select id from BankAccountPaymentModel where transactionId="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
											
						case SConstants.BANK_ACCOUNT_DEPOSITS:
											obj=getSession().createQuery("select id from BankAccountDepositModel where transaction_id="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						case SConstants.CASH_ACCOUNT_DEPOSITS:
											obj=getSession().createQuery("select id from CashAccountDepositModel where transactionId="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						case SConstants.CASH_ACCOUNT_PAYMENTS:
											obj=getSession().createQuery("select id from CashAccountPaymentModel where transactionId="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						case SConstants.CREDIT_NOTE:
											obj=getSession().createQuery("select id from CreditNoteModel where transactionId="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						case SConstants.DEBIT_NOTE:
											obj=getSession().createQuery("select id from DebitNoteModel where transactionId="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						case SConstants.PDC_PAYMENT:
											obj=getSession().createQuery("select id from PdcPaymentModel where transactionId="+tid).uniqueResult();
											if(obj!=null)
												id=(Long)obj;
											break;
			
						default:
											id=0;
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
	
	@SuppressWarnings("unchecked")
	public List getMoneyFlowReport(long officeId, Date start, Date end, SettingsValuePojo settings) throws Exception {
		List<ReportBean> resultList=new ArrayList<ReportBean>();
		/*try {
			begin();
			
			ArrayList arr=new ArrayList();
			arr.add(SConstants.CASH_GROUP);
			arr.add(SConstants.BANK_ACCOUNT_GROUP_ID);
			
			String condition="";
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			String officeCondition="";
			if (officeId != 0) {
				officeCondition += " and a.office.id=" + officeId;
			}
			
			String cashAcc=(String) getSession().createQuery("select name from LedgerModel where id=:id")
					.setLong("id", settings.getCASH_ACCOUNT()).uniqueResult();
			
			resultList.addAll(getSession()
					.createQuery("select new com.webspark.bean.ReportBean(id,'Sales',payment_amount, 0.0, date,customer.name,'"+cashAcc+"','Cash Payment')"
									+ " from SalesModel where payment_amount>0 and date between :stdt and :enddt and active=true "+condition)
									.setParameter("stdt", start).setParameter("enddt", end).list());
			
			resultList.addAll(getSession().createQuery(
							"select new com.webspark.bean.ReportBean(id,'Purchase', 0.0, payment_amount,date,'"+cashAcc+"',supplier.name,'Cash Payment')"
									+ " from PurchaseModel where payment_amount>0 and date between :stdt and :enddt and active=true "+condition)
									.setParameter("stdt", start).setParameter("enddt", end).list());
			
			resultList.addAll(getSession().createQuery(
					"select new com.webspark.bean.ReportBean(id,'Customer Payment',payment_amount, 0.0, date,(select b.name from LedgerModel b where id=a.from_account_id)," +
					"(select b.name from LedgerModel b where id=a.to_account_id),case when cash_or_check=1 then 'Cash Payment' else 'Cheque Payment' end)" +
					" from PaymentModel a where type=:type and active=true and" +
						" payment_amount>0 and date between :stdt and :enddt"+condition)
						.setParameter("stdt", start).setParameter("enddt", end)
						.setParameter("type", SConstants.CUSTOMER_PAYMENTS).list());
			
			
			resultList.addAll(getSession().createQuery(
					"select new com.webspark.bean.ReportBean(id,'Supplier Payment',0.0, payment_amount, date,(select b.name from LedgerModel b where id=a.to_account_id)," +
					"(select b.name from LedgerModel b where id=a.from_account_id),case when cash_or_check=1 then 'Cash Payment' else 'Cheque Payment' end) from PaymentModel a where type=:type and active=true and" +
						" payment_amount>0 and date between :stdt and :enddt "+condition)
						.setParameter("stdt", start).setParameter("enddt", end)
					.setParameter("type", SConstants.SUPPLIER_PAYMENTS).list());
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.id,'Transportation Payments',  0.0,a.payment_amount,a.date," +
					"(select b.name from LedgerModel b where id=a.from_account_id),(select c.name from LedgerModel c where c.id=a.transportation_id)," +
					"case when cash_or_check=1 then 'Cash Payment' else 'Cheque Payment' end)"
					+ " from TransportationPaymentModel a where " +
					"a.date between :stdt and :enddt and a.type=1 and a.active=true  "+officeCondition+"")
					.setParameter("stdt", start).setParameter("enddt", end)
					.list());
			
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Expenditure',0.0, sum(b.amount), a.date,b.fromAcct.name,b.toAcct.name," +
								"case when b.fromAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
									+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
									"date between :stdt and :enddt and a.transaction_type =:type and b.fromAcct.group.id in (:grp) "+officeCondition+" group by a.transaction_id")
									.setParameter("stdt", start).setParameter("enddt", end)
					.setLong("type", SConstants.EXPENDETURE_TRANSACTION).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Income', sum(b.amount), 0.0,a.date,b.toAcct.name,b.fromAcct.name," +
								"case when b.toAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
									+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
									"date between :stdt and :enddt and a.transaction_type =:type and b.toAcct.group.id in (:grp)"+officeCondition+" group by a.transaction_id")
									.setParameter("stdt", start).setParameter("enddt", end)
					.setLong("type", SConstants.INCOME_TRANSACTION).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Bank Account Withdrawal', sum(b.amount), 0.0,a.date,b.fromAcct.name,b.toAcct.name," +
								"case when b.fromAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
								+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
								"date between :stdt and :enddt and a.transaction_type =:type  and b.fromAcct.group.id in (:grp) "+officeCondition+" group by a.transaction_id")
								.setParameter("stdt", start).setParameter("enddt", end)
								.setLong("type", SConstants.BANK_ACCOUNT_PAYMENTS).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Bank Account Deposit', 0.0, sum(b.amount),a.date,b.toAcct.name,b.fromAcct.name," +
								"case when b.toAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
					+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
					"date between :stdt and :enddt and a.transaction_type =:type  and b.toAcct.group.id in (:grp)"+officeCondition+" group by a.transaction_id")
					.setParameter("stdt", start).setParameter("enddt", end).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr)
					.setLong("type", SConstants.BANK_ACCOUNT_DEPOSITS).list());
			
			
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Employee Advance Payments', 0.0, sum(b.amount),a.date,b.fromAcct.name,b.toAcct.name," +
								"case when b.fromAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
					+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
					"date between :stdt and :enddt  and a.transaction_type =:type  and b.fromAcct.group.id in (:grp) "+officeCondition+" group by a.transaction_id")
					.setParameter("stdt", start).setParameter("enddt", end).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr)
					.setLong("type", SConstants.EMPLOYEE_ADVANCE_PAYMENTS).list());
			
//			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Salary Payments',  0.0,sum(b.amount),a.date,'','','')"
//					+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
//					"date between :stdt and :enddt  and a.transaction_type =:type"+officeCondition+" group by a.transaction_id")
//					.setParameter("stdt", start).setParameter("enddt", end)
//					.setLong("type", SConstants.PAYROLL_PAYMENTS).list());
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Journal', 0.0,b.amount,a.date,b.fromAcct.name,b.toAcct.name," +
								"case when b.fromAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
					+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
					"date between :stdt and :enddt and a.transaction_type =:type  and b.fromAcct.group.id in (:grp)"+officeCondition)
					.setParameter("stdt", start).setParameter("enddt", end).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr)
					.setLong("type", SConstants.JOURNAL).list());
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(a.transaction_id,'Journal', b.amount, 0.0,a.date,b.toAcct.name,b.fromAcct.name," +
								"case when b.toAcct.group.id=:cash then 'Cash Payment' else 'Cheque Payment' end)"
					+ " from TransactionModel a join a.transaction_details_list b where a.status=1 and " +
					"date between :stdt and :enddt and a.transaction_type =:type  and b.toAcct.group.id in (:grp)"+officeCondition)
					.setParameter("stdt", start).setParameter("enddt", end).setLong("cash", SConstants.CASH_GROUP).setParameterList("grp", arr)
					.setLong("type", SConstants.JOURNAL).list());
			
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(id,'Investment', amount, 0.0,date,(select b.name from LedgerModel b where id=a.capital_account_id)," +
					"(select b.name from LedgerModel b where id=a.cash_account_id),'Cash Payment') "
						+ " from CashInvestmentModel a where date between :stdt and :enddt and type=1 and active=true and office.id="+officeId)
						.setParameter("stdt", start).setParameter("enddt", end).list());
			
			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(id,'Investment',  0.0,amount,date,(select b.name from LedgerModel b where id=a.cash_account_id)," +
					"(select b.name from LedgerModel b where id=a.capital_account_id),'Cash Payment') "
					+ " from CashInvestmentModel a where date between :stdt and :enddt and  type=2 and active=true and office.id="+officeId)
					.setParameter("stdt", start).setParameter("enddt", end).list());
			
			
			
//			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(id,'Subscription',amount_paid,  0.0,payment_date,subscription.name," +
//					"(select b.name from LedgerModel b where id=a.from_account),case when cash_cheque=1 then 'Cash Payment' else 'Cheque Payment' end) "
//					+ " from SubscriptionPaymentModel a where payment_date between :stdt and :enddt and  subscription.account_type=2  and subscription.subscription_type.officeId="+officeId)
//					.setParameter("stdt", start).setParameter("enddt", end).list());
//			
//			resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(id,'Subscription', 0.0, amount_paid,payment_date," +
//					"(select b.name from LedgerModel b where id=a.from_account),subscription.name,case when cash_cheque=1 then 'Cash Payment' else 'Cheque Payment' end) "
//					+ " from SubscriptionPaymentModel a where payment_date between :stdt and :enddt and  subscription.account_type!=2  and subscription.subscription_type.officeId="+officeId)
//					.setParameter("stdt", start).setParameter("enddt", end).list());
			
			// Expendeture Subscription & Rent Type=1
						String subsSelection=",(select name from LedgerModel where id=from_account),(select name from LedgerModel where id=to_account),";
						resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean" +
								"(a.id,'Subscription'"+subsSelection+"0.0,a.amount_paid,a.payment_date) "
					
								+ " from SubscriptionPaymentModel a where a.payment_date between :stdt and :enddt and a.from_account=:acc " +
								" and a.subscription.subscription.rent_status=0 or a.subscription.subscription.rent_status=1" +
								" and a.pay_credit=0 and a.cash_cheque=1" +
								" and a.subscription.subscription.subscription_type.officeId="+officeId)
								.setParameter("stdt", start)
								.setParameter("enddt", end)
								.setParameter("acc", settings.getCASH_ACCOUNT())
								.list());
						// Income Subscription & Rent Type=1
						resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean" +
								"(a.id,'Subscription'"+subsSelection+"a.amount_paid,0.0,a.payment_date) "
					
								+ " from SubscriptionPaymentModel a where a.payment_date between :stdt and :enddt and a.to_account=:acc " +
								" and a.subscription.subscription.rent_status=0 or a.subscription.subscription.rent_status=2" +
								"and a.pay_credit=0 and a.cash_cheque=1 and" +
								" a.subscription.subscription.subscription_type.officeId="+officeId)
								.setParameter("stdt", start)
								.setParameter("enddt", end)
								.setParameter("acc", settings.getCASH_ACCOUNT())
								.list());
						// Both Rent Type Rent In
						resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean" +
								"(a.id,'Subscription'"+subsSelection+"0.0,a.amount_paid,a.payment_date) "
					
								+ " from SubscriptionPaymentModel a where a.payment_date between :stdt and :enddt and a.from_account=:acc " +
								" and a.subscription.account_type=3 and a.subscription.subscription.rent_status=3 and a.subscription.available=1" +
								" and a.pay_credit=0 and a.cash_cheque=1" +
								" and a.subscription.subscription.subscription_type.officeId="+officeId)
								.setParameter("stdt", start)
								.setParameter("enddt", end)
								.setParameter("acc", settings.getCASH_ACCOUNT())
								.list());
						// Both Rent Type Rent Out
						resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean" +
								"(a.id,'Subscription'"+subsSelection+"a.amount_paid,0.0,a.payment_date) "
								+ " from SubscriptionPaymentModel a where a.payment_date between :stdt and :enddt and a.to_account=:acc " +
								" and a.subscription.account_type=3 and a.subscription.subscription.rent_status=3 or a.subscription.available=2" +
								"and a.pay_credit=0 and a.cash_cheque=1 and" +
								" a.subscription.subscription.subscription_type.officeId="+officeId)
								.setParameter("stdt", start)
								.setParameter("enddt", end)
								.setParameter("acc", settings.getCASH_ACCOUNT())
								.list());
			
//			double inw=(Double) getSession().createQuery("select coalesce(sum(amount),0) "
//					+ " from CashInvestmentModel where date between :stdt and :enddt and type=1 and active=true and office.id="+officeId)
//					.setParameter("stdt", start).setParameter("enddt", end)
//					.uniqueResult();
//			double out=(Double) getSession().createQuery("select coalesce(sum(amount),0) "
//					+ " from CashInvestmentModel where date between :stdt and :enddt and type=2 and active=true and office.id="+officeId)
//					.setParameter("stdt", start).setParameter("enddt", end)
//					.uniqueResult();
			
//			Object date=end;
			
//			if(resultList.size()>0) {
//				ReportBean s=resultList.get(0);
//				date=s.getTrn_date();
//			}
			
//			if(date!=null)
//				resultList.add(new ReportBean("Investment",  inw,out,(java.util.Date)date));
			
			
			commit();
			
		} catch (Exception e) {
			resultList=new ArrayList();
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}*/
		return resultList;
	}
	
	
	
}
