package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.DebitCreditInvoiceMapModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.sales.model.PaymentInvoiceMapModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class CustomerSupplierLedgerReportDao extends SHibernate implements Serializable{
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getCustomerLedgerReportOldLogic(Date start, Date end, long office_id, long customer) throws Exception {
		List resultList=new ArrayList();	
		try {
			
			begin();
			
			
			String condition="";
			if(customer!=0)
				condition+=" and a.ledger.id="+customer;

			List ledgerList=new ArrayList();
			
			ledgerList= getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(a.ledger.id, a.name)"
									+ " from CustomerModel a where a.ledger.status=:val and  a.ledger.office.id=:ofc "+condition+" order by a.name")
									.setLong("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
			
			LedgerModel obj;
			Object object;
			
			List bankPaymentList=new ArrayList();
			List cashPaymentList=new ArrayList();
			
			List bankPaymentTodayList=new ArrayList();
			List cashPaymentTodayList=new ArrayList();
			
			List bankPaymentOpeningList=new ArrayList();
			List cashPaymentOpeningList=new ArrayList();
			
			List invoiceList=new ArrayList();
			List invoiceTodayList=new ArrayList();
			List invoiceOpeningList=new ArrayList();
			
			List transactionList=new ArrayList();
			
			List debitNoteList=new ArrayList();
			List creditNoteList=new ArrayList();
			
			List debitNoteTodayList=new ArrayList();
			List creditNoteTodayList=new ArrayList();
			
			List debitNoteOpeningList=new ArrayList();
			List creditNoteOpeningList=new ArrayList();
			
			Iterator titr;
			
			double sale=0, cash=0, ret=0, todayBal=0, openingBal=0;
			
			// Bank Deposit List
			bankPaymentList=getSession().createQuery("select id from BankAccountDepositModel where date between :start and :end and " +
										"active=true and office_id=:office_id")
										.setLong("office_id", office_id).setDate("start", start).setDate("end", end).list();
			
			// Cash Deposit List
			cashPaymentList=getSession().createQuery("select id from CashAccountDepositModel where date between :start and :end and " +
										"active=true and office_id=:office_id")
										.setLong("office_id", office_id).setDate("start", start).setDate("end", end).list();
			
			// Bank Deposit List Today Balance
			bankPaymentTodayList=getSession().createQuery("select id from BankAccountDepositModel where date <= :end and " +
										"active=true and office_id=:office_id")
										.setLong("office_id", office_id).setDate("end", CommonUtil.getCurrentSQLDate()).list();
			
			// Cash Deposit List Today Balance
			cashPaymentTodayList=getSession().createQuery("select id from CashAccountDepositModel where date <= :end and " +
										"active=true and office_id=:office_id")
										.setLong("office_id", office_id).setDate("end", CommonUtil.getCurrentSQLDate()).list();
			
			// Bank Deposit List Opening Balance
			bankPaymentOpeningList=getSession().createQuery("select id from BankAccountDepositModel where date < :start and " +
										"active=true and office_id=:office_id")
										.setLong("office_id", office_id).setDate("start", start).list();
			
			// Cash Deposit List Opening Balance
			cashPaymentOpeningList=getSession().createQuery("select id from CashAccountDepositModel where date < :start and " +
										"active=true and office_id=:office_id")
										.setLong("office_id", office_id).setDate("start", start).list();
			
			Iterator itr =ledgerList.iterator();
			
			while(itr.hasNext()) {
				
				obj=(LedgerModel) itr.next();
				
				sale=0; cash=0; ret=0; todayBal=0; openingBal=0;
				
				// Sales Amount
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesModel where date between :start and :end and active=true and customer.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					sale+=CommonUtil.roundNumber((Double)object);
				
				// Sales Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesModel where date <= :end and active=true and customer.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal+=CommonUtil.roundNumber((Double)object);
				
				// Sales Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesModel where date < :start and active=true and customer.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal+=CommonUtil.roundNumber((Double)object);
				
				
				
				// Sales Payment Amount
				object=getSession().createQuery("select coalesce(sum((payment_amount/paymentConversionRate)),0) from SalesModel where " +
										" date between :start and :end and active=true and customer.id=:ledger")
										.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Sales Payment Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((payment_amount/paymentConversionRate)),0) from SalesModel where " +
						" date <= :end and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Sales Payment Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((payment_amount/paymentConversionRate)),0) from SalesModel where " +
						" date < :start and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Sales Return Amount
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
										" from SalesReturnModel where date between :start and :end and active=true and customer.id=:ledger")
											.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					ret+=CommonUtil.roundNumber((Double)object);
				
				// Sales Return Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesReturnModel where date <= :end and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Sales Return Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesReturnModel where date < :start and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);

				
				
				// Debit Note List
				debitNoteList=getSession().createQuery("select id from DebitNoteModel where supplier_customer=:type and date between" +
														" :start and :end and active=true and office_id=:office_id and ledger.id=:ledger")
														.setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
														.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).list();

				// Credit Note List
				creditNoteList=getSession().createQuery("select id from CreditNoteModel where supplier_customer=:type and date between" +
														" :start and :end and active=true and office_id=:office_id and ledger.id=:ledger")
														.setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
														.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).list();
				
				// Debit Note List Today Balance
				debitNoteTodayList=getSession().createQuery("select id from DebitNoteModel where supplier_customer=:type and date <= :end " +
						" and active=true and office_id=:office_id and ledger.id=:ledger")
						.setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).list();
				
				// Credit Note List Today Balance
				creditNoteTodayList=getSession().createQuery("select id from CreditNoteModel where supplier_customer=:type and date <= :end" +
						" and active=true and office_id=:office_id and ledger.id=:ledger")
						.setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).list();
				
				// Debit Note List Opening Balance
				debitNoteOpeningList=getSession().createQuery("select id from DebitNoteModel where supplier_customer=:type and date < " +
						" :start and active=true and office_id=:office_id and ledger.id=:ledger")
						.setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("start", start).list();
				
				// Credit Note List Opening Balance
				creditNoteOpeningList=getSession().createQuery("select id from CreditNoteModel where supplier_customer=:type and date <" +
						" :start and active=true and office_id=:office_id and ledger.id=:ledger")
						.setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("start", start).list();
				
				
				
				// Invoice List
				invoiceList=getSession().createQuery("select id from SalesModel where date between :start and :end and active=true and " +
									"customer.id=:ledger").setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).list();
				
				// Invoice List Today Balance
				invoiceTodayList=getSession().createQuery("select id from SalesModel where date <= :end and active=true and " +
						"customer.id=:ledger").setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).list();
				
				// Invoice List Opening Balance
				invoiceOpeningList=getSession().createQuery("select id from SalesModel where date < :start and active=true and " +
						"customer.id=:ledger").setLong("ledger", obj.getId()).setDate("start", start).list();

				
				
				if(bankPaymentList.size()>0 && invoiceList.size()>0) {
					
					// Bank Account Deposit
					transactionList=getSession().createQuery("from PaymentInvoiceMapModel where type=:type and payment_type=:payment_type " +
												" and office_id=:office and invoiceId in (:invList) and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameterList("invList", invoiceList)
												.setParameterList("payList", bankPaymentList)
												.setParameter("payment_type", SConstants.BANK_ACCOUNT_DEPOSITS)
												.setParameter("type", SConstants.SALES).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) titr.next();
						cash+=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				if(bankPaymentTodayList.size()>0 && invoiceTodayList.size()>0) {
					
					// Bank Account Deposit Today Balance
					transactionList=getSession().createQuery("from PaymentInvoiceMapModel where type=:type and payment_type=:payment_type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameterList("invList", invoiceTodayList)
												.setParameterList("payList", bankPaymentTodayList)
												.setParameter("payment_type", SConstants.BANK_ACCOUNT_DEPOSITS)
												.setParameter("type", SConstants.SALES).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) titr.next();
						todayBal-=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				
				
				if(bankPaymentOpeningList.size()>0 && invoiceOpeningList.size()>0) {
					
					// Bank Account Deposit Opening Balance
					transactionList=getSession().createQuery("from PaymentInvoiceMapModel where type=:type and payment_type=:payment_type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameterList("invList", invoiceOpeningList)
												.setParameterList("payList", bankPaymentOpeningList)
												.setParameter("payment_type", SConstants.BANK_ACCOUNT_DEPOSITS)
												.setParameter("type", SConstants.SALES).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) titr.next();
						openingBal-=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				
				if(cashPaymentList.size()>0 && invoiceList.size()>0) {
					
					// Cash Account Deposit
					transactionList=getSession().createQuery("from PaymentInvoiceMapModel where type=:type and payment_type=:payment_type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameterList("invList", invoiceList)
												.setParameterList("payList", cashPaymentList)
												.setParameter("payment_type", SConstants.CASH_ACCOUNT_DEPOSITS)
												.setParameter("type", SConstants.SALES).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) titr.next();
						cash+=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				if(cashPaymentTodayList.size()>0 && invoiceTodayList.size()>0) {
				
					// Cash Account Deposit Today Balance
					transactionList=getSession().createQuery("from PaymentInvoiceMapModel where type=:type and payment_type=:payment_type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameterList("invList", invoiceTodayList)
												.setParameterList("payList", cashPaymentTodayList)
												.setParameter("payment_type", SConstants.CASH_ACCOUNT_DEPOSITS)
												.setParameter("type", SConstants.SALES).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) titr.next();
						todayBal-=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				if(cashPaymentOpeningList.size()>0 && invoiceOpeningList.size()>0) {
					
					// Cash Account Deposit Opening Balance
					transactionList=getSession().createQuery("from PaymentInvoiceMapModel where type=:type and payment_type=:payment_type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameterList("invList", invoiceOpeningList)
												.setParameterList("payList", cashPaymentOpeningList)
												.setParameter("payment_type", SConstants.CASH_ACCOUNT_DEPOSITS)
												.setParameter("type", SConstants.SALES).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) titr.next();
						openingBal-=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				if(debitNoteList.size()>0 && invoiceList.size()>0) {
					
					// Debit Note
					transactionList=getSession().createQuery("from DebitCreditInvoiceMapModel where supplier_customer=:cusSup and type=:type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER)
												.setParameterList("invList", invoiceList)
												.setParameterList("payList", debitNoteList)
												.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) titr.next();
						sale+=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				
				if(debitNoteTodayList.size()>0 && invoiceTodayList.size()>0) {
					
					// Debit Note Today Balance
					transactionList=getSession().createQuery("from DebitCreditInvoiceMapModel where supplier_customer=:cusSup and type=:type " +
							" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
							.setParameter("office", office_id)
							.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER)
							.setParameterList("invList", invoiceTodayList)
							.setParameterList("payList", debitNoteTodayList)
							.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) titr.next();
						todayBal+=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				if(debitNoteOpeningList.size()>0 && invoiceOpeningList.size()>0) {

					// Debit Note Opening Balance
					transactionList=getSession().createQuery("from DebitCreditInvoiceMapModel where supplier_customer=:cusSup and type=:type " +
							" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
							.setParameter("office", office_id)
							.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER)
							.setParameterList("invList", invoiceOpeningList)
							.setParameterList("payList", debitNoteOpeningList)
							.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) titr.next();
						openingBal+=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				

				
				if(creditNoteList.size()>0 && invoiceList.size()>0) {
					
					// Credit Note
					transactionList=getSession().createQuery("from DebitCreditInvoiceMapModel where supplier_customer=:cusSup and type=:type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER)
												.setParameterList("invList", invoiceList)
												.setParameterList("payList", creditNoteList)
												.setParameter("type", SConstants.creditDebitNote.CREDIT).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) titr.next();
						cash+=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				if(creditNoteTodayList.size()>0 && invoiceTodayList.size()>0) {
					
					// Credit Note Today Balance
					transactionList=getSession().createQuery("from DebitCreditInvoiceMapModel where supplier_customer=:cusSup and type=:type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER)
												.setParameterList("invList", invoiceTodayList)
												.setParameterList("payList", creditNoteTodayList)
												.setParameter("type", SConstants.creditDebitNote.CREDIT).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) titr.next();
						todayBal-=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
				
				
				if(creditNoteOpeningList.size()>0 && invoiceOpeningList.size()>0) {
					
					// Credit Note Opening Balance
					transactionList=getSession().createQuery("from DebitCreditInvoiceMapModel where supplier_customer=:cusSup and type=:type " +
												" and office_id=:office and invoiceId in (:invList)  and paymentId in (:payList) ")
												.setParameter("office", office_id)
												.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER)
												.setParameterList("invList", invoiceOpeningList)
												.setParameterList("payList", creditNoteOpeningList)
												.setParameter("type", SConstants.creditDebitNote.CREDIT).list();
					
					titr=transactionList.iterator();
					while (titr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) titr.next();
						openingBal-=CommonUtil.roundNumber(map.getAmount()/map.getConversionRate());
					}
					
				}
					
				
				// PDC
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate between :start and :end and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.SALES)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				
				// PDC Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate <= :end and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.SALES)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				
				// PDC Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate < :start and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.SALES)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("start", start).uniqueResult();
				
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				Object ledgOpBalCr=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:cr and date <:start " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:start and ledger.id=:id)")
						.setLong("id", obj.getId()).setDate("start", start).setParameter("cr", SConstants.CR).uniqueResult();
				
				Object ledgOpBalDr=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:dr and date <:start " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:start and ledger.id=:id)")
						.setLong("id", obj.getId()).setDate("start", start).setParameter("dr", SConstants.CR).uniqueResult();
				
				if(ledgOpBalDr!=null)
					openingBal+=(Double)ledgOpBalDr;
				
				if(ledgOpBalCr!=null)
					openingBal-=(Double)ledgOpBalCr;
				
				// Contsructor #13
				// if(!(sale==0 && cash==0 && ret==0))
					resultList.add(new AcctReportMainBean(obj.getName(), 
															CommonUtil.roundNumber(sale), 
															CommonUtil.roundNumber(cash),
															CommonUtil.roundNumber(ret),
															CommonUtil.roundNumber(sale-cash-ret),
															CommonUtil.roundNumber(todayBal),
															CommonUtil.roundNumber(openingBal)));

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
		return resultList;
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getCustomerLedgerReport(Date start, Date end, long office_id, long customer, long salesMan) throws Exception {
		List resultList=new ArrayList();	
		try {
			
			begin();
			
			
			String condition="";
			if(customer!=0)
				condition+=" and a.ledger.id="+customer;
			if(salesMan!=0)
				condition+=" and a.responsible_person="+salesMan;

			List ledgerList=new ArrayList();
			
			ledgerList= getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(a.ledger.id, a.name)"
									+ " from CustomerModel a where a.ledger.status=:val and  a.ledger.office.id=:ofc "+condition+" order by a.name")
									.setLong("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
			
			LedgerModel obj;
			Object object;
			
			double sale=0, cash=0, ret=0, todayBal=0, openingBal=0;
			double opening_bal = 0;
			
			Iterator itr =ledgerList.iterator();
			
			while(itr.hasNext()) {
				
				obj=(LedgerModel) itr.next();
				
				sale=0; cash=0; ret=0; todayBal=0; openingBal=0;
				
				
				Object objDr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.toAcct.id =:led)").setLong("led", obj.getId()).setDate("stdt", start).uniqueResult();
				Object objCr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.fromAcct.id =:led)").setLong("led", obj.getId()).setDate("stdt", start).uniqueResult();
				
				
				if(objDr!=null)
					opening_bal+=(Double)objDr;
				
				if(objCr!=null)
					opening_bal-=(Double)objCr;
				
				
				
				// Sales Amount
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesModel where date between :start and :end and active=true and customer.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					sale+=CommonUtil.roundNumber((Double)object);
				
				// Sales Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesModel where date <= :end and active=true and customer.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal+=CommonUtil.roundNumber((Double)object);
				
				// Sales Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesModel where date < :start and active=true and customer.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal+=CommonUtil.roundNumber((Double)object);
				
				
				
				// Sales Payment Amount
				object=getSession().createQuery("select coalesce(sum((payment_amount/conversionRate)),0) from SalesModel where " +
										" date between :start and :end and active=true and customer.id=:ledger")
										.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Sales Payment Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((payment_amount/conversionRate)),0) from SalesModel where " +
						" date <= :end and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Sales Payment Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((payment_amount/conversionRate)),0) from SalesModel where " +
						" date < :start and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Sales Return Amount
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
										" from SalesReturnModel where date between :start and :end and active=true and customer.id=:ledger")
											.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					ret+=CommonUtil.roundNumber((Double)object);
				
				// Sales Return Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesReturnModel where date <= :end and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Sales Return Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from SalesReturnModel where date < :start and active=true and customer.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);


				
				// Debit Note
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from DebitNoteModel a join a.debit_note_list b" +
						" where a.supplier_customer=:type and a.date between :start and :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				
				
				if(object!=null)
					sale+=CommonUtil.roundNumber((Double)object);
				
				// Debit Note Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from DebitNoteModel a join a.debit_note_list b" +
						" where a.supplier_customer=:type and a.date <= :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal+=CommonUtil.roundNumber((Double)object);
				
				// Debit Note Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from DebitNoteModel a join a.debit_note_list b" +
						" where a.supplier_customer=:type and a.date < :start and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal+=CommonUtil.roundNumber((Double)object);
				
				
				
				// Credit Note
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CreditNoteModel a join a.credit_note_list b" +
						" where a.supplier_customer=:type and a.date between :start and :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Credit Note Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CreditNoteModel a join a.credit_note_list b" +
						" where a.supplier_customer=:type and a.date <= :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Credit Note Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CreditNoteModel a join a.credit_note_list b" +
						" where a.supplier_customer=:type and a.date < :start and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.CUSTOMER)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Bank Deposit
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from BankAccountDepositModel a join " +
						" a.bank_account_deposit_list b where b.account.id=:ledger and a.date between :start and :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Bank Deposit Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from BankAccountDepositModel a join " +
						" a.bank_account_deposit_list b where b.account.id=:ledger and a.date <= :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Bank Deposit Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from BankAccountDepositModel a join " +
						" a.bank_account_deposit_list b where b.account.id=:ledger and a.date < :start and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Cash Deposit
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CashAccountDepositModel a join " +
						" a.cash_account_deposit_list b where b.account.id=:ledger and a.date between :start and :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Cash Deposit Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CashAccountDepositModel a join " +
						" a.cash_account_deposit_list b where b.account.id=:ledger and a.date <= :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Cash Deposit Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CashAccountDepositModel a join " +
						" a.cash_account_deposit_list b where b.account.id=:ledger and a.date < :start and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// PDC
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate between :start and :end and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.SALES)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				
				// PDC Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate <= :end and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.SALES)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				
				// PDC Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate < :start and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.SALES)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("start", start).uniqueResult();
				
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				object=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:cr and date <:start " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:start and ledger.id=:id)")
						.setLong("id", obj.getId()).setDate("start", start).setParameter("cr", SConstants.CR).uniqueResult();
				
				if(object!=null)
					openingBal-=(Double)object;
				
				object=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:dr and date <:start " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:start and ledger.id=:id)")
						.setLong("id", obj.getId()).setDate("start", start).setParameter("dr", SConstants.CR).uniqueResult();
				
				if(object!=null)
					openingBal+=(Double)object;
				
				// Contsructor #29
				// if(!(sale==0 && cash==0 && ret==0))
					resultList.add(new AcctReportMainBean(obj.getId(),
														obj.getName(), 
														CommonUtil.roundNumber(sale), 
														CommonUtil.roundNumber(cash),
														CommonUtil.roundNumber(ret),
														CommonUtil.roundNumber(sale-cash-ret),
														CommonUtil.roundNumber(todayBal),
														CommonUtil.roundNumber(opening_bal)));

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
		return resultList;
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getSupplierLedgerReport(Date start, Date end, long office_id, long supplier) throws Exception {
		List resultList=new ArrayList();	
		try {
			
			begin();
			
			
			String condition="";
			if(supplier!=0)
				condition+=" and a.ledger.id="+supplier;

			List ledgerList=new ArrayList();
			
			ledgerList= getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(a.ledger.id, a.name)"
									+ " from SupplierModel a where a.ledger.status=:val and  a.ledger.office.id=:ofc "+condition+" order by a.name")
									.setLong("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
			
			LedgerModel obj;
			Object object;
			
			double sale=0, cash=0, ret=0, todayBal=0, openingBal=0;
			double opening_bal = 0;
			
			Iterator itr =ledgerList.iterator();
			
			while(itr.hasNext()) {
				
				obj=(LedgerModel) itr.next();
				
				sale=0; cash=0; ret=0; todayBal=0; openingBal=0;
				
				Object objDr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.toAcct.id =:led)").setLong("led", obj.getId()).setDate("stdt", start).uniqueResult();
				Object objCr=getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.fromAcct.id =:led)").setLong("led", obj.getId()).setDate("stdt", start).uniqueResult();
				
				
				if(objDr!=null)
					opening_bal-=(Double)objDr;
				
				if(objCr!=null)
					opening_bal+=(Double)objCr;
				
				
				
				
				
				
				// Purchase Amount
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from PurchaseModel where date between :start and :end and active=true and supplier.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					sale+=CommonUtil.roundNumber((Double)object);
				
				// Purchase Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from PurchaseModel where date <= :end and active=true and supplier.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal+=CommonUtil.roundNumber((Double)object);
				
				// Purchase Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from PurchaseModel where date < :start and active=true and supplier.id=:ledger")
							.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal+=CommonUtil.roundNumber((Double)object);
				
				
				
				// Purchase Payment Amount
				object=getSession().createQuery("select coalesce(sum((paymentAmount/conversionRate)),0) from PurchaseModel where " +
										" date between :start and :end and active=true and supplier.id=:ledger")
										.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Purchase Payment Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((paymentAmount/paymentConversionRate)),0) from PurchaseModel where " +
						" date <= :end and active=true and supplier.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Purchase Payment Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((paymentAmount/paymentConversionRate)),0) from PurchaseModel where " +
						" date < :start and active=true and supplier.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Purchase Return Amount
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
										" from PurchaseReturnModel where date between :start and :end and active=true and supplier.id=:ledger")
											.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					ret+=CommonUtil.roundNumber((Double)object);
				
				// Purchase Return Amount Today Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from PurchaseReturnModel where date <= :end and active=true and supplier.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Purchase Return Amount Opening Balance
				object=getSession().createQuery("select coalesce(sum((((amount-expenseAmount)+(expenseAmount-expenseCreditAmount))/conversionRate)),0) " +
						" from PurchaseReturnModel where date < :start and active=true and supplier.id=:ledger")
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);


				
				// Debit Note
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from DebitNoteModel a join a.debit_note_list b" +
						" where a.supplier_customer=:type and a.date between :start and :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.SUPPLIER)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					sale+=CommonUtil.roundNumber((Double)object);
				
				// Debit Note Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from DebitNoteModel a join a.debit_note_list b" +
						" where a.supplier_customer=:type and a.date <= :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.SUPPLIER)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal+=CommonUtil.roundNumber((Double)object);
				
				// Debit Note Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from DebitNoteModel a join a.debit_note_list b" +
						" where a.supplier_customer=:type and a.date < :start and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.SUPPLIER)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal+=CommonUtil.roundNumber((Double)object);
				
				
				
				// Credit Note
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CreditNoteModel a join a.credit_note_list b" +
						" where a.supplier_customer=:type and a.date between :start and :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.SUPPLIER)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Credit Note Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CreditNoteModel a join a.credit_note_list b" +
						" where a.supplier_customer=:type and a.date <= :end and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.SUPPLIER)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Credit Note Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CreditNoteModel a join a.credit_note_list b" +
						" where a.supplier_customer=:type and a.date < :start and a.active=true and a.office_id=:office_id " +
						"and a.ledger.id=:ledger").setLong("office_id", office_id).setLong("type", SConstants.creditDebitNote.SUPPLIER)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Bank Payment
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from BankAccountPaymentModel a join " +
						" a.bank_account_payment_list b where b.account.id=:ledger and a.date between :start and :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Bank Payment Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from BankAccountPaymentModel a join " +
						" a.bank_account_payment_list b where b.account.id=:ledger and a.date <= :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Bank Payment Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from BankAccountPaymentModel a join " +
						" a.bank_account_payment_list b where b.account.id=:ledger and a.date < :start and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// Cash Payment
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CashAccountPaymentModel a join " +
						" a.cash_account_payment_list b where b.account.id=:ledger and a.date between :start and :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).setDate("end", end).uniqueResult();
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				// Cash Payment Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CashAccountPaymentModel a join " +
						" a.cash_account_payment_list b where b.account.id=:ledger and a.date <= :end and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				// Cash Payment Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0) from CashAccountPaymentModel a join " +
						" a.cash_account_payment_list b where b.account.id=:ledger and a.date < :start and a.active=true " +
						" and a.office_id=:office_id").setLong("office_id", office_id)
						.setLong("ledger", obj.getId()).setDate("start", start).uniqueResult();
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				// PDC
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate between :start and :end and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.PURCHASE)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("start", start).setDate("end", end).uniqueResult();
				
				if(object!=null)
					cash+=CommonUtil.roundNumber((Double)object);
				
				
				// PDC Today Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate <= :end and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.PURCHASE)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("end", CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				if(object!=null)
					todayBal-=CommonUtil.roundNumber((Double)object);
				
				
				// PDC Opening Balance
				object=getSession().createQuery("select coalesce(sum(b.amount/b.conversionRate),0)  from PdcModel a join a.pdc_list b " +
									" where b.paymentDate < :start and a.active=true and b.account.id=:ledger and b.type=:type" +
									" and b.status=:status")
									.setLong("ledger", obj.getId()).setParameter("type", SConstants.PURCHASE)
									.setParameter("status", SConstants.PDCStatus.APPROVED)
									.setDate("start", start).uniqueResult();
				
				if(object!=null)
					openingBal-=CommonUtil.roundNumber((Double)object);
				
				
				
				object=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:cr and date <:start " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:start and ledger.id=:id)")
						.setLong("id", obj.getId()).setDate("start", start).setParameter("cr", SConstants.CR).uniqueResult();
				
				if(object!=null)
					openingBal-=(Double)object;
				
				object=getSession().createQuery("select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:dr and date <:start " +
						" and id=(select max(id) from LedgerOpeningBalanceModel where date <:start and ledger.id=:id)")
						.setLong("id", obj.getId()).setDate("start", start).setParameter("dr", SConstants.CR).uniqueResult();
				
				if(object!=null)
					openingBal+=(Double)object;
				
				
				// Contsructor #29
				// if(!(sale==0 && cash==0 && ret==0))
					resultList.add(new AcctReportMainBean(obj.getId(),
														obj.getName(), 
														CommonUtil.roundNumber(sale), 
														CommonUtil.roundNumber(cash),
														CommonUtil.roundNumber(ret),
														CommonUtil.roundNumber(sale-cash-ret),
														CommonUtil.roundNumber(todayBal),
														CommonUtil.roundNumber(opening_bal)));

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
		return resultList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List getSupplierLedgerReportOld(Date start_date, Date end_date, long office_id, long supplId) throws Exception {
		List resultList=new ArrayList();
		try {
			
			begin();
			
			String condition="";
			if(supplId!=0)
				condition+=" and ledger.id="+supplId;
			
			
			Iterator itr = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
									+ " from SupplierModel where ledger.office.id=:ofc and ledger.status=:val "+condition+" order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			LedgerModel obj;
			double purchase=0, cash=0, ret=0, todayBal=0,openingBal=0;
			while(itr.hasNext()) {
				
				obj=(LedgerModel) itr.next();
				
				purchase=0; cash=0; ret=0;todayBal=0;openingBal=0;
				
				purchase = (Double)getSession().createQuery("select coalesce(sum(amount),0) from PurchaseModel where date between :stdt and :enddt and supplier.id=:led and active=true " +
										"").setLong("led", obj.getId()).setDate("stdt", start_date).setDate("enddt", end_date).uniqueResult();
				cash = (Double)getSession().createQuery("select coalesce(sum(payment_amount),0) from PurchaseModel where date between :stdt and :enddt and supplier.id=:led and active=true " +
						"").setLong("led", obj.getId()).setDate("stdt", start_date).setDate("enddt", end_date).uniqueResult();
			
				cash+=(Double)getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date between :stdt and :enddt and active=true and " +
											"to_account_id=:led and type=:typ").setLong("led", obj.getId()).
											setDate("stdt", start_date).setDate("enddt", end_date)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				ret=(Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
						+ " from PurchaseReturnModel where date between :stdt and :enddt and active=true and supplier.id=:led")
						.setParameter("stdt", start_date).setParameter("enddt", end_date).setLong("led", obj.getId())
						.uniqueResult();
				
				
				
				
				todayBal+= (Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where date<=:stdt  and active=true and supplier.id=:led " +
						"").setLong("led", obj.getId()).setDate("stdt", CommonUtil.getCurrentSQLDate()).uniqueResult();

				
				todayBal-=(Double)getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date<=:stdt and " +
											"to_account_id=:led and type=:typ and active=true").setLong("led", obj.getId()).
											setDate("stdt", CommonUtil.getCurrentSQLDate())
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				
				todayBal-=(Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
						+ " from PurchaseReturnModel where date<=:stdt and active=true and supplier.id=:led")
						.setParameter("stdt", CommonUtil.getCurrentSQLDate()).setLong("led", obj.getId())
						.uniqueResult();
				
				
//				openingBal+=obj.getOpening_balance();
				
				openingBal+= (Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0) from PurchaseModel where date<:stdt and supplier.id=:led and active=true " +
						"").setLong("led", obj.getId()).setDate("stdt", start_date).uniqueResult();

				
				openingBal-=(Double)getSession().createQuery("select coalesce(sum(payment_amount),0) from PaymentModel where date<:stdt and " +
											"to_account_id=:led and type=:typ and active=true").setLong("led", obj.getId()).
											setDate("stdt", start_date)
											.setParameter("typ", SConstants.SUPPLIER_PAYMENTS).uniqueResult();
				
				
				openingBal-=(Double)getSession().createQuery("select coalesce(sum(amount-payment_amount),0)"
						+ " from PurchaseReturnModel where date<:stdt and supplier.id=:led and active=true")
						.setParameter("stdt", start_date).setLong("led", obj.getId())
						.uniqueResult();
				
				
				if(!(purchase==0 && cash==0 && ret==0))
					resultList.add(new AcctReportMainBean(obj.getName(), purchase, cash, ret, purchase-cash-ret, todayBal,openingBal));

				
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
	
	
	public List getTransportationReport(Date start_date, Date end_date, long office_id,long transId) throws Exception {
		List resultList=new ArrayList();
		try {
			
			begin();
			String condition="";
			if(transId!=0)
				condition+=" and ledger.id="+transId;
			
			Iterator itr = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
									+ " from TranspotationModel where ledger.office.id=:ofc and active=true and ledger.status=:val "+condition+" order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list().iterator();
			
			LedgerModel obj;
			double credit=0, cash=0, ret=0, todayBal=0,openingBal=0;
			while(itr.hasNext()) {
				
				obj=(LedgerModel) itr.next();
				
				credit=0; cash=0; ret=0;todayBal=0;openingBal=0;
				
				
				credit=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt and " +
											"b.fromAcct.id=:led").setLong("led", obj.getId()).setDate("stdt", start_date).setDate("enddt", end_date).uniqueResult();
				
				
				cash= (Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date between :stdt and :enddt and " +
											"b.toAcct.id=:led").setLong("led", obj.getId()).setDate("stdt", start_date).setDate("enddt", end_date).uniqueResult();
				
				
				todayBal+=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
						"b.fromAcct.id=:led").setLong("led", obj.getId()).setDate("stdt",  CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				
				todayBal-= (Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <=:stdt and " +
						"b.toAcct.id=:led").setLong("led", obj.getId()).setDate("stdt",  CommonUtil.getCurrentSQLDate()).uniqueResult();
				
				
//				openingBal+=obj.getOpening_balance();
				openingBal+=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.fromAcct.id=:led").setLong("led", obj.getId()).setDate("stdt",  start_date).uniqueResult();

				openingBal-=(Double) getSession().createQuery("select coalesce(sum(b.amount),0) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and " +
						"b.toAcct.id=:led").setLong("led", obj.getId()).setDate("stdt",  start_date).uniqueResult();
				
				
				if(!(credit==0 && cash==0 && cash==0))
					resultList.add(new AcctReportMainBean(obj.getName(), credit, cash, 0, credit-cash, todayBal,openingBal));
				
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
		List resultList=new ArrayList();
		double op_bal=0;
		
		try {
			
			
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
