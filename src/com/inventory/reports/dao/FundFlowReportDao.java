package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.AcctReportMainBean;
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
public class FundFlowReportDao extends SHibernate implements Serializable{
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getFundFlowReport(long orgId, long officeId, long ledger, long departmentid, long divisionId, Date start, Date end, SettingsValuePojo settings) throws Exception {
		List<AcctReportMainBean> resultList=new ArrayList<AcctReportMainBean>();
		try {
			begin();
			List transList=new ArrayList();
			List cashList=new ArrayList();
			String cdn="",cdn1="";
			if(ledger!=0){
				cdn=" and id="+ledger;
				cdn1=" and a.ledger.id="+ledger;
			}
				
			cashList=getSession().createQuery("from LedgerModel where office.id=:ofc and group.id=:grp"+cdn)
										.setLong("ofc",officeId).setLong("grp",settings.getCASH_GROUP()).list();
			cashList.addAll(getSession().createQuery("select a.ledger from BankAccountModel a where a.ledger.office.id=:ofc"+cdn1).setLong("ofc",officeId).list());
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

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public double getFundFlowOpeningBalance(long officeId, long leder,  Date start_date, SettingsValuePojo settings) throws Exception {
		double openingBalance=0;
		try {
			
			begin();
			List cashList=new ArrayList();
			
			String cdn="",cdn1="";
			if(leder!=0){
				cdn=" and id="+leder;
				cdn1=" and a.ledger.id="+leder;
			}
				
			cashList=getSession().createQuery("from LedgerModel where office.id=:ofc and group.id=:grp"+cdn)
										.setLong("ofc",officeId).setLong("grp",settings.getCASH_GROUP()).list();
			cashList.addAll(getSession().createQuery("select a.ledger from BankAccountModel a where a.ledger.office.id=:ofc"+cdn1).setLong("ofc",officeId).list());
			
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
						.setLong("id", ledger.getId()).setDate("stdt", start_date).setParameter("dr", SConstants.CR).uniqueResult();
				
				
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
	
	
	@SuppressWarnings("rawtypes")
	public List getBankLedgers(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(a.ledger.id, a.ledger.name)" +
					" from BankAccountModel a where a.ledger.office.id=:ofc").setLong("ofc",office).list();
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
		return resultList;
	}
	
}
