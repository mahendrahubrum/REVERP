package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.inventory.reports.bean.TransactionListingReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class TransactionListingReportDao extends SHibernate {
	private StringBuffer queryBuffer;
	public TransactionListingReportDao() {
		super();
	}

	public List<TransactionListingReportBean> getTransactionListingReportDetails(
			long ledger_id, int transactionType, Date from_date, Date to_date,long office_id) {
		List<TransactionListingReportBean> mainBeanList = null;
		mainBeanList = new ArrayList<TransactionListingReportBean>();
		queryBuffer = new StringBuffer();

		try {
			mainBeanList.addAll(getTransactionDetails(ledger_id,
					transactionType, from_date, to_date,office_id));
			loadDetails(mainBeanList);
			Collections.sort(mainBeanList, new Comparator<TransactionListingReportBean>() {

				@Override
				public int compare(TransactionListingReportBean o1,
						TransactionListingReportBean o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainBeanList;
	}

	private void loadDetails(List<TransactionListingReportBean> beanList) throws Exception {

		Iterator<TransactionListingReportBean> itr = beanList.iterator();
		while (itr.hasNext()) {
			TransactionListingReportBean bean = itr.next();
			switch (bean.getTransaction_type()) {
			case SConstants.BANK_ACCOUNT_DEPOSITS:
				getBankDepositDetails(bean);
				break;
			case SConstants.BANK_ACCOUNT_PAYMENTS:
				getBankPaymentDetails(bean);
				break;
			case SConstants.SALES: 
				getSalesDetails(bean);
				break;
			case SConstants.PURCHASE: 
				getPurchaseDetails(bean);
				break;
			case SConstants.CASH_ACCOUNT_DEPOSITS:
				getCashAccountDepositsDetails(bean); 
				break; 
			case SConstants.CASH_ACCOUNT_PAYMENTS:
				getCashAccountPaymentDetails(bean); 
				break;
			case SConstants.CREDIT_NOTE: 
				getCreditNoteDetails(bean);
			    break;
			case SConstants.DEBIT_NOTE: 
				getDebitNoteDetails(bean);
				break; 
			case SConstants.PDC_PAYMENT:
				 getPDCPaymentDetails(bean);
				 break;
			}
		}

	}

	private void getPDCPaymentDetails(TransactionListingReportBean bean)  throws Exception{
		TransactionListingReportBean model = null;
		
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"
						+ "b.chequeNo,"
						+ "b.chequeDate)"
						
						+ " FROM PdcPaymentModel a JOIN a.pdc_payment_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.from_id = :from_account_id"
						+ " AND b.to_id = :to_account_id");
		
		model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("from_account_id",bean.getAmount() < 0 ? bean.getFromAccountId() : bean.getLedger_id())
				.setLong("to_account_id", bean.getAmount() < 0 ? bean.getLedger_id() : bean.getFromAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getDebitNoteDetails(TransactionListingReportBean bean)  throws Exception{
		TransactionListingReportBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"
						+ " ' ')"
						+ " FROM DebitNoteModel a JOIN a.debit_note_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
				model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", bean.getLedger_id())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getCreditNoteDetails(TransactionListingReportBean bean)  throws Exception{
		TransactionListingReportBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"						
						+ " ' ')"
						+ " FROM CreditNoteModel a JOIN a.credit_note_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", bean.getLedger_id())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getCashAccountPaymentDetails(TransactionListingReportBean bean) throws Exception{

		TransactionListingReportBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"
						+ " ' ')"
						+ " FROM CashAccountPaymentModel a JOIN a.cash_account_payment_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		
		model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", bean.getLedger_id())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	
		
	}

	private void getCashAccountDepositsDetails(TransactionListingReportBean bean) throws Exception{		
		TransactionListingReportBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"
						+ " ' ')"
						+ " FROM CashAccountDepositModel a JOIN a.cash_account_deposit_list b"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		
		model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", bean.getLedger_id())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}
		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	
		
	}

	private void getPurchaseDetails(TransactionListingReportBean bean) throws Exception{
		TransactionListingReportBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.purchase_no,"
						+ "a.chequeNo,"
						+ "a.chequeDate)"
						
						+ " FROM PurchaseModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");
		
		model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
		
	}

	private void getSalesDetails(TransactionListingReportBean bean) throws Exception {
		// Constructor 4
		TransactionListingReportBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.sales_number,"
						+ "a.chequeNo,"
						+ "a.chequeDate)"
						
						+ " FROM SalesModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");		

		model = (TransactionListingReportBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}
		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getBankPaymentDetails(TransactionListingReportBean bean) throws Exception {
		TransactionListingReportBean model = null;
		// System.out.println("=========== QUERY============"+queryBuffer.toString());
		if (bean.getAmount() > 0) {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"
							+ "b.chequeNo,"
							+ "b.chequeDate)"

							+ " FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b"
							+ " WHERE a.bankAccount.id = :bank_account_id"
							+ // to account id
							" AND b.account.id = :from_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");

			model = (TransactionListingReportBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id",  bean.getLedger_id())
					.setLong("from_account_id", bean.getFromAccountId())
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		} else {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(b.bill_no,"
							+ "b.chequeNo,"
							+ "b.chequeDate)"

							+ " FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b"
							+ " WHERE b.account.id = :bank_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");

			model = (TransactionListingReportBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id",  bean.getLedger_id())
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		}
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}
		} else {
			System.out.println("=========== MODEL IS NULL1==========");
		}

		queryBuffer.delete(0, queryBuffer.length());
		// bean.setComments(comments);

	}

	private void getBankDepositDetails(TransactionListingReportBean bean) throws Exception {
		TransactionListingReportBean model = null;
		System.out.println("=========== QUERY============"
				+ queryBuffer.toString());
		if (bean.getAmount() < 0) {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(a.bill_no,"
							+ "b.chequeNo,"
							+ "b.chequeDate)"

							+ " FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b"
							+ " WHERE a.bankAccount.id = :bank_account_id"
							+ // to account id
							" AND b.account.id = :from_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");

			model = (TransactionListingReportBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id", bean.getLedger_id())
					.setLong("from_account_id", bean.getFromAccountId())
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		} else {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean(b.bill_no,"
							+ "b.chequeNo,"
							+ "b.chequeDate)"

							+ " FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b"
							+ " WHERE b.account.id = :bank_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");

			model = (TransactionListingReportBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id",  bean.getLedger_id())
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		}
		if (model != null) {
			bean.setDocNo(model.getDocNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate());
			}
		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<TransactionListingReportBean> getTransactionDetails(
			long ledger_id, int transaction_type, Date from_date, Date to_date,long office_id)
			throws Exception {
		List bankAccountIdList = null;
		if(ledger_id == 0){
			bankAccountIdList = getSession().createQuery("select ledger.id" +
					" from BankAccountModel where ledger.office.id=:ofc and ledger.status=:val")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
		}
		
		List<TransactionListingReportBean> list = new ArrayList<TransactionListingReportBean>();
		
		queryBuffer.delete(0, queryBuffer.length());
		// Constructor 1
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean( b.toAcct.name,"
						+ "b.fromAcct.name,"
						+ "a.date,"
						+ "-b.amount,"
						+ " ' ',"
						+ " b.fromAcct.id,"
						+ "a.transaction_type,"
						+ "a.transaction_id,"
						+ " b.toAcct.id )"
						+ " FROM TransactionModel a  JOIN a.transaction_details_list b"
						+ " WHERE a.date BETWEEN :from_date AND :to_date"
						+ " AND a.transaction_type = :transaction_type")
				.append(ledger_id != 0 ? " AND b.toAcct.id = :ledger_id"
						: " AND b.toAcct.id IN( :ledger_id)");
		
		Query query = getSession().createQuery(queryBuffer.toString())
				.setDate("from_date", from_date).setDate("to_date", to_date)				
				.setInteger("transaction_type", transaction_type);
		if(ledger_id != 0){
			query.setLong("ledger_id", ledger_id);
		}else{			
			query.setParameterList("ledger_id", bankAccountIdList);
		}
		list.addAll(query.list());

		queryBuffer.delete(0, queryBuffer.length());
		// Constructor 1
		queryBuffer
				.append("SELECT new com.inventory.reports.bean.TransactionListingReportBean( b.fromAcct.name,"
						+ "b.toAcct.name,"
						+ "a.date,"
						+ "b.amount,"
						+ " ' ',"
						+ " b.toAcct.id,"
						+ "a.transaction_type,"
						+ "a.transaction_id,"
						+ "b.fromAcct.id )"
						+ " FROM TransactionModel a  JOIN a.transaction_details_list b"
						+ " WHERE a.date BETWEEN :from_date AND :to_date"
						+ " AND a.transaction_type = :transaction_type")
				.append(ledger_id != 0 ? " AND b.fromAcct.id = :ledger_id"
						: " AND b.fromAcct.id IN( :ledger_id)");
		
		query = getSession().createQuery(queryBuffer.toString())
				.setDate("from_date", from_date).setDate("to_date", to_date)				
				.setInteger("transaction_type", transaction_type);
		if(ledger_id != 0){
			query.setLong("ledger_id", ledger_id);
		}else{			
			query.setParameterList("ledger_id", bankAccountIdList);
		}
		list.addAll(query.list());

	/*	list.addAll(getSession().createQuery(queryBuffer.toString())
				.setDate("from_date", from_date).setDate("to_date", to_date)
				.setLong("ledger_id", ledger_id)
				.setInteger("transaction_type", transaction_type).list());*/

		queryBuffer.delete(0, queryBuffer.length());
		return list;
	}

}
