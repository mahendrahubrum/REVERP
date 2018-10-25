package com.inventory.finance.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.finance.bean.BankReconciliationBean;
import com.inventory.finance.model.BankRecociliationModel;
import com.inventory.reports.bean.BankReconciliationReportBean;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DivisionModel;

public class BankReconciliationDao extends SHibernate {
	private HashMap<Long, String> divisionHashMap;
	private HashMap<Long, String> departmentHashMap;
	private StringBuffer queryBuffer;

	public long save(BankRecociliationModel model) throws Exception {
		try {
			begin();
			getSession().save(model);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return model.getId();
	}

	public TransactionModel getTransactionModel(long id) throws Exception {
		TransactionModel mdl = null;
		try {
			begin();
			mdl = (TransactionModel) getSession().get(TransactionModel.class,
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
		return mdl;
	}

	public List<BankReconciliationReportBean> getBankReconciliationReportDetails(SparkLogic spark,
			long ledger_id, Date from_date, Date to_date)
			throws Exception {
		if(queryBuffer == null ){
			queryBuffer = new StringBuffer();
		}
		List<BankReconciliationReportBean> mainBeanList = new ArrayList<BankReconciliationReportBean>();
		double balance = getOpeningBalance(from_date, ledger_id);
		BankReconciliationReportBean reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("balance_as_per_our_books"));
		reportBean.setCr(0);
		reportBean.setDr(0);
		reportBean.setBalance(balance);
		
		mainBeanList.add(reportBean);		
		mainBeanList.add(getEmptyBean());
		
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("add"));
		reportBean.setCr(0);
		reportBean.setDr(0);
		reportBean.setBalance(0);
		
		mainBeanList.add(reportBean);
		mainBeanList.add(getEmptyBean());
		
		double totalCr = getUnclearedCredits(ledger_id, from_date, to_date);		
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("uncleared_credits"));
		reportBean.setCr(totalCr);
		reportBean.setDr(0);
		reportBean.setBalance(0);
		
		mainBeanList.add(reportBean);
		
		
		List<BankReconciliationBean> clearedList = getClearedDetails(ledger_id, from_date, to_date);
		Collections.sort(clearedList,
				new Comparator<BankReconciliationBean>() {

					@Override
					public int compare(BankReconciliationBean o1,
							BankReconciliationBean o2) {
						int i = o1.getDate().compareTo(o2.getDate());
						if (i != 0) {
							return i;
						}

						return (int) (o1.getInvoiceId() - o2.getInvoiceId());
					}
				});
		
		for(BankReconciliationBean bean : clearedList){
			if(bean.getCr() == 0){
				continue;
			}
			totalCr += bean.getCr();
			
			reportBean = new BankReconciliationReportBean();
			reportBean.setParticulars(bean.getClearingDate()+" - "+bean.getParticulars());
			reportBean.setCr(bean.getCr());
			reportBean.setDr(0);
			reportBean.setBalance(0);
			
			mainBeanList.add(reportBean);
		}
		mainBeanList.add(getEmptyBean());
		balance += totalCr;
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("total"));
		reportBean.setCr(totalCr);
		reportBean.setDr(0);
		reportBean.setBalance(balance);
		
		mainBeanList.add(reportBean);
		mainBeanList.add(getEmptyBean());
		//========================================================================
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("less"));
		reportBean.setCr(0);
		reportBean.setDr(0);
		reportBean.setBalance(0);
		
		mainBeanList.add(reportBean);
		mainBeanList.add(getEmptyBean());
		
		double totalDr = getUnclearedDebits(ledger_id, from_date, to_date);		
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("uncleared_debits"));
		reportBean.setCr(0);
		reportBean.setDr(totalDr);
		reportBean.setBalance(0);
		
		mainBeanList.add(reportBean);
		
		
		for(BankReconciliationBean bean : clearedList){
			if(bean.getDr() == 0){
				continue;
			}
			totalDr += bean.getDr();
			
			reportBean = new BankReconciliationReportBean();
			reportBean.setParticulars(bean.getClearingDate()+" - "+bean.getParticulars());
			reportBean.setCr(0);
			reportBean.setDr(bean.getDr());
			reportBean.setBalance(0);
			
			mainBeanList.add(reportBean);
		}
		mainBeanList.add(getEmptyBean());
		balance -= totalDr;
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("total"));
		reportBean.setCr(0);
		reportBean.setDr(totalDr);
		reportBean.setBalance(balance);
		
		mainBeanList.add(reportBean);
		mainBeanList.add(getEmptyBean());
		
		reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars(spark.getPropertyName("balance_as_per_book_statement"));
		reportBean.setCr(0);
		reportBean.setDr(0);
		reportBean.setBalance(balance);
		
		mainBeanList.add(reportBean);

		return mainBeanList;
	}

	private BankReconciliationReportBean getEmptyBean() {
		BankReconciliationReportBean reportBean = new BankReconciliationReportBean();
		reportBean.setParticulars("");
		reportBean.setCr(0);
		reportBean.setDr(0);
		reportBean.setBalance(0);
		return reportBean;
	}

	private double getUnclearedCredits(long ledger_id, Date from_date,
			Date to_date) throws Exception{
		double totalCr;
		queryBuffer
		.append("SELECT COALESCE(SUM(b.amount),0.0)"
				+ " FROM TransactionModel a  JOIN a.transaction_details_list b"
				+ " WHERE a.date BETWEEN :from_date AND :to_date"
				+ " AND b.fromAcct.id = :ledger_id")
		.append(" AND a.transaction_id NOT IN (SELECT c.transaction.id FROM BankRecociliationModel c"
				+ " WHERE c.ledger.id = b.fromAcct.id"
				+ " AND c.transafer_ledger.id = b.toAcct.id"
				+ " AND c.transaction.id = a.transaction_id)");

				totalCr = (Double)getSession().createQuery(queryBuffer.toString())
						.setDate("from_date", from_date).setDate("to_date", to_date)
						.setLong("ledger_id", ledger_id).uniqueResult();

				queryBuffer.delete(0, queryBuffer.length());
		return totalCr;
	}
	private double getUnclearedDebits(long ledger_id, Date from_date,
			Date to_date) throws Exception{
		double totalCr;
		queryBuffer
		.append("SELECT COALESCE(SUM(b.amount),0.0)"
				+ " FROM TransactionModel a  JOIN a.transaction_details_list b"
				+ " WHERE a.date BETWEEN :from_date AND :to_date"
				+ " AND b.toAcct.id = :ledger_id")
		.append(" AND a.transaction_id NOT IN (SELECT c.transaction.id FROM BankRecociliationModel c"
				+ " WHERE c.ledger.id = b.toAcct.id"
				+ " AND c.transafer_ledger.id = b.fromAcct.id"
				+ " AND c.transaction.id = a.transaction_id)");

				totalCr = (Double)getSession().createQuery(queryBuffer.toString())
						.setDate("from_date", from_date).setDate("to_date", to_date)
						.setLong("ledger_id", ledger_id).uniqueResult();

				queryBuffer.delete(0, queryBuffer.length());
		return totalCr;
	}

	public List<BankReconciliationBean> getBankReconciliationDetails(
			long ledger_id, Date from_date, Date to_date, int status)
			throws Exception {
		List<BankReconciliationBean> mainBeanList = null;
		List<BankReconciliationBean> clearedList = null;
		try {
			begin();
			mainBeanList = new ArrayList<BankReconciliationBean>();
			clearedList = new ArrayList<BankReconciliationBean>();
			queryBuffer = new StringBuffer();

			getDivisionAndDepartmentDetails();
			if (status == SConstants.BankReconciliationStatus.ALL) {
				mainBeanList.addAll(getTransactionDetails(ledger_id, from_date,
						to_date));

				// Iterator<BankReconciliationBean> itr;
				loadOtherDetails(ledger_id, mainBeanList, false);
				clearedList.addAll(getClearedDetails(ledger_id, from_date,
						to_date));
				loadOtherDetails(ledger_id, clearedList, true);

				mainBeanList.addAll(clearedList);
				/*
				 * itr = clearedList.iterator(); while (itr.hasNext()) {
				 * BankReconciliationBean bean = itr.next(); switch
				 * (bean.getTransaction_type()) { case
				 * SConstants.BANK_ACCOUNT_DEPOSITS: getBankDepositDetails(bean,
				 * ledger_id,true); break; case
				 * SConstants.BANK_ACCOUNT_PAYMENTS:
				 * getBankPaymentDetails(bean,ledger_id,true); } }
				 */

			} else if (status == SConstants.BankReconciliationStatus.CLEARED) {

				clearedList.addAll(getClearedDetails(ledger_id, from_date,
						to_date));
				loadOtherDetails(ledger_id, clearedList, true);
				mainBeanList.addAll(clearedList);
			} else {
				mainBeanList.addAll(getTransactionDetails(ledger_id, from_date,
						to_date));
				loadOtherDetails(ledger_id, mainBeanList, false);
			}

			Collections.sort(mainBeanList,
					new Comparator<BankReconciliationBean>() {

						@Override
						public int compare(BankReconciliationBean o1,
								BankReconciliationBean o2) {
							int i = o1.getDate().compareTo(o2.getDate());
							if (i != 0) {
								return i;
							}

							return (int) (o1.getInvoiceId() - o2.getInvoiceId());
						}
					});

			/*
			 * Collections.sort(list, new Comparator<BankReconciliationBean>() {
			 * 
			 * @Override public int compare(BankReconciliationBean o1,
			 * BankReconciliationBean o2) { return (int) (o1.getInvoiceId()-
			 * o2.getInvoiceId()); } });
			 */

			// ===========================================================================================

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return mainBeanList;
	}

	public void update(BankRecociliationModel componentModel) throws Exception {
		try {
			begin();
			getSession().update(componentModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public BankRecociliationModel getBankRecociliationModel(long id)
			throws Exception {
		BankRecociliationModel model = null;
		try {
			begin();
			model = (BankRecociliationModel) getSession().get(
					BankRecociliationModel.class, id);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return model;
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new BankRecociliationModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}

	}

	private void loadOtherDetails(long ledger_id,
			List<BankReconciliationBean> beanList, boolean isCleared)
			throws Exception {
		Iterator<BankReconciliationBean> itr = beanList.iterator();
		while (itr.hasNext()) {
			BankReconciliationBean bean = itr.next();
			switch (bean.getTransaction_type()) {
			case SConstants.BANK_ACCOUNT_DEPOSITS:
				System.out
						.println("============= BANK_ACCOUNT_DEPOSITS ==CR==="
								+ bean.getCr() + "==DR==" + bean.getDr());
				getBankDepositDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.BANK_ACCOUNT_PAYMENTS:
				System.out
						.println("============= BANK_ACCOUNT_PAYMENTS ==CR==="
								+ bean.getCr() + "==DR==" + bean.getDr());
				getBankPaymentDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.SALES:
				System.out.println("============= SALES ==CR===" + bean.getCr()
						+ "==DR==" + bean.getDr());
				getSalesDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.PURCHASE:
				System.out.println("============= PURCHASE ==CR==="
						+ bean.getCr() + "==DR==" + bean.getDr());
				getPurchaseDetails(bean, ledger_id, isCleared);
				break;
			// case SConstants.JOURNAL:
			// getJournalDetails(bean, ledger_id, isCleared);
			// break;
			// case SConstants.PURCHASE_RETURN:
			// getPurchaseReturnDetails(bean, ledger_id, isCleared);
			// break;
			// case SConstants.SALES_RETURN:
			// getSalesReturnDetails(bean, ledger_id, isCleared);
			// break;
			case SConstants.CASH_ACCOUNT_DEPOSITS:
				System.out
						.println("============= CASH_ACCOUNT_DEPOSITS ==CR==="
								+ bean.getCr() + "==DR==" + bean.getDr());
				getCashAccountDepositsDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.CASH_ACCOUNT_PAYMENTS:
				System.out
						.println("============= CASH_ACCOUNT_PAYMENTS ==CR==="
								+ bean.getCr() + "==DR==" + bean.getDr());
				getCashAccountPaymentDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.CREDIT_NOTE:
				System.out.println("============= CREDIT_NOTE ==CR==="
						+ bean.getCr() + "==DR==" + bean.getDr());
				getCreditNoteDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.DEBIT_NOTE:
				System.out.println("============= DEBIT_NOTE ==CR==="
						+ bean.getCr() + "==DR==" + bean.getDr());
				getDebitNoteDetails(bean, ledger_id, isCleared);
				break;
			case SConstants.PDC_PAYMENT:
				System.out.println("============= PDC_PAYMENT ==CR==="
						+ bean.getCr() + "==DR==" + bean.getDr());
				getPDCPaymentDetails(bean, ledger_id, isCleared);
				break;

			}
		}
	}

	private void getPDCPaymentDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		BankReconciliationBean model = null;
		// System.out.println("=========== QUERY============"+queryBuffer.toString());

		// Constructor 3
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
						+ "b.chequeNo,"
						+ "case when b.chequeDate is not null then cast(b.chequeDate as string) else ' ' end,"
						+ "b.id,"
						+ "b.divisionId,"
						+ "b.departmentId)"
						+ " FROM PdcPaymentModel a JOIN a.pdc_payment_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.from_id = :from_account_id"
						+ " AND b.to_id = :to_account_id");
		if (isCleared) {
			queryBuffer
					.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = :from_account_id"
							+ " AND c.transafer_ledger.id = :to_account_id"
							+ " AND c.transaction.id = a.transactionId)");
		} else {
			queryBuffer
					.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = :from_account_id"
							+ " AND c.transafer_ledger.id = :to_account_id"
							+ " AND c.transaction.id = a.transactionId)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong(
						"from_account_id",
						bean.getDr() != 0 ? bean.getTransactionAccountId()
								: ledger_id)
				.setLong(
						"to_account_id",
						bean.getDr() != 0 ? ledger_id : bean
								.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();

		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}

		/*
		 * if(model.getBank_account_deposit_list() == null){ System.out.println(
		 * "=========== MODEL .getBank_account_deposit_list() IS NULL=========="
		 * ); } else { System.out.println(
		 * "=========== MODEL .getBank_account_deposit_list() IS NOT NULL=========="
		 * ); }
		 */
		queryBuffer.delete(0, queryBuffer.length());
		// bean.setComments(comments);

	}

	private void getDebitNoteDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 3
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
						+ " ' ',"
						+ " ' ',"
						+ "b.id,"
						+ "b.divisionId,"
						+ "b.departmentId)"
						+ " FROM DebitNoteModel a JOIN a.debit_note_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		if (isCleared) {
			queryBuffer
					.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getCreditNoteDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 3
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
						+ " ' ',"
						+ " ' ',"
						+ "b.id,"
						+ "b.divisionId,"
						+ "b.departmentId)"
						+ " FROM CreditNoteModel a JOIN a.credit_note_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		if (isCleared) {
			queryBuffer
					.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getCashAccountPaymentDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 3
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
						+ " ' ',"
						+ " ' ',"
						+ "b.id,"
						+ "b.divisionId,"
						+ "b.departmentId)"
						+ " FROM CashAccountPaymentModel a JOIN a.cash_account_payment_list b"
						+ " WHERE a.transactionId = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		if (isCleared) {
			queryBuffer
					.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getCashAccountDepositsDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 3
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
						+ " ' ',"
						+ " ' ',"
						+ "b.id,"
						+ "b.divisionId,"
						+ "b.departmentId)"
						+ " FROM CashAccountDepositModel a JOIN a.cash_account_deposit_list b"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date"
						+ " AND b.account.id = :bank_account_id");
		if (isCleared) {
			queryBuffer
					.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = b.id"
							+ " AND c.ledger.id = b.account.id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getSalesReturnDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 4
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.return_no,"
						+ "a.chequeNo,"
						+ "case when a.chequeDate is not null then cast(a.chequeDate as string) else ' ' end,"
						+ "a.id)"
						+ " FROM SalesReturnModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");
		if (isCleared) {
			queryBuffer
					.append(" AND a.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND a.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			// bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			// bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getPurchaseReturnDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 4
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.return_no,"
						+ "a.chequeNo,"
						+ "case when a.chequeDate is not null then cast(a.chequeDate as string) else ' ' end,"
						+ "a.id)"
						+ " FROM PurchaseReturnModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");
		if (isCleared) {
			queryBuffer
					.append(" AND a.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND a.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			// bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			// bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getJournalDetails(BankReconciliationBean bean, long ledger_id,
			boolean isCleared) throws Exception {
		// Constructor 4
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.ref_no,"
						+ "a.chequeNo,"
						+ "case when a.chequeDate is not null then cast(a.chequeDate as string) else ' ' end,"
						+ "a.id)"
						+ " FROM JournalModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");
		if (isCleared) {
			queryBuffer
					.append(" AND a.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND a.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			// bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			// bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getPurchaseDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		// Constructor 4
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.purchase_no,"
						+ "a.chequeNo,"
						+ "case when a.chequeDate is not null then cast(a.chequeDate as string) else ' ' end,"
						+ "a.id)"
						+ " FROM PurchaseModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");
		if (isCleared) {
			queryBuffer
					.append(" AND a.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND a.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			// bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			// bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getSalesDetails(BankReconciliationBean bean, long ledger_id,
			boolean isCleared) throws Exception {
		// Constructor 4
		BankReconciliationBean model = null;
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.sales_number,"
						+ "a.chequeNo,"
						+ "case when a.chequeDate is not null then cast(a.chequeDate as string) else ' ' end,"
						+ "a.id)"
						+ " FROM SalesModel a"
						+ " WHERE a.transaction_id = :transaction_id"
						+ " AND a.date = :tran_date");
		if (isCleared) {
			queryBuffer
					.append(" AND a.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		} else {
			queryBuffer
					.append(" AND a.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
							+ " WHERE c.invoiceId = a.id"
							+ " AND c.ledger.id = :bank_account_id"
							+ " AND c.transafer_ledger.id = :transfer_account_id"
							+ " AND c.transaction.id = a.transaction_id)");
		}

		model = (BankReconciliationBean) getSession()
				.createQuery(queryBuffer.toString())
				.setLong("bank_account_id", ledger_id)
				.setLong("transfer_account_id", bean.getTransactionAccountId())
				.setLong("transaction_id", bean.getTransactionId())
				.setDate("tran_date",
						CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
				.uniqueResult();
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			// bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			// bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}
		queryBuffer.delete(0, queryBuffer.length());
	}

	private void getBankPaymentDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		BankReconciliationBean model = null;
		// System.out.println("=========== QUERY============"+queryBuffer.toString());
		if (bean.getCr() != 0) {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
							+ "b.chequeNo,"
							+ "case when b.chequeDate is not null then cast(b.chequeDate as string) else ' ' end,"
							+ "b.id,"
							+ "b.divisionId,"
							+ "b.departmentId)"
							+ " FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b"
							+ " WHERE a.bankAccount.id = :bank_account_id"
							+ // to account id
							" AND b.account.id = :from_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");
			if (isCleared) {
				queryBuffer
						.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = a.bankAccount.id"
								+ " AND c.transafer_ledger.id = b.account.id"
								+ " AND c.transaction.id = a.transactionId)");
			} else {
				queryBuffer
						.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = a.bankAccount.id"
								+ " AND c.transafer_ledger.id = b.account.id"
								+ " AND c.transaction.id = a.transactionId)");
			}

			model = (BankReconciliationBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id", ledger_id)
					.setLong("from_account_id", bean.getTransactionAccountId())
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		} else {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
							+ "b.chequeNo,"
							+ "case when b.chequeDate is not null then cast(b.chequeDate as string) else ' ' end,"
							+ "b.id,"
							+ "b.divisionId,"
							+ "b.departmentId)"
							+ " FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b"
							+ " WHERE b.account.id = :bank_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");

			if (isCleared) {
				queryBuffer
						.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = b.account.id"
								+ " AND c.transafer_ledger.id = a.bankAccount.id"
								+ " AND c.transaction.id = a.transactionId)");
			} else {
				queryBuffer
						.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = b.account.id"
								+ " AND c.transafer_ledger.id = a.bankAccount.id"
								+ " AND c.transaction.id = a.transactionId)");
			}
			model = (BankReconciliationBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id", ledger_id)
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		}
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL1==========");
		}

		/*
		 * if(model.getBank_account_deposit_list() == null){ System.out.println(
		 * "=========== MODEL .getBank_account_deposit_list() IS NULL=========="
		 * ); } else { System.out.println(
		 * "=========== MODEL .getBank_account_deposit_list() IS NOT NULL=========="
		 * ); }
		 */
		queryBuffer.delete(0, queryBuffer.length());
		// bean.setComments(comments);

	}

	@SuppressWarnings("unchecked")
	private List<BankReconciliationBean> getClearedDetails(long ledger_id,
			Date from_date, Date to_date) throws Exception {
		List<BankReconciliationBean> clearedList = new ArrayList<BankReconciliationBean>();
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.id, "
						+ "a.transaction.date,"// + "cast(a.date as string),"
						+ "a.transafer_ledger.name,"
						+ "case when a.amount < 0 then abs(a.amount) else 0.0 end,"
						+ "case when a.amount > 0 then a.amount else 0.0 end,"
						+ " a.transaction.id,"
						+ " a.transafer_ledger.id,"
						+ "a.invoiceId,"
						+ "a.clearing_date,"
						+ "a.comments,"
						+ " a.transaction.transaction_type)"
						+ " FROM BankRecociliationModel a"
						+ " WHERE a.transaction.date BETWEEN :from_date AND :to_date"
						+ " AND a.ledger.id = :ledger_id");
		clearedList.addAll(getSession().createQuery(queryBuffer.toString())
				.setDate("from_date", from_date).setDate("to_date", to_date)
				.setLong("ledger_id", ledger_id).list());

		queryBuffer.delete(0, queryBuffer.length());
		return clearedList;
	}

	@SuppressWarnings("unchecked")
	private void getDivisionAndDepartmentDetails() throws Exception {

		List<DivisionModel> divisionList = getSession().createQuery(
				"FROM DivisionModel").list();
		divisionHashMap = new HashMap<Long, String>();
		divisionHashMap.put((long) 0, "None");
		for (DivisionModel model : divisionList) {
			divisionHashMap.put(model.getId(), model.getName());
		}

		List<DepartmentModel> departmentList = getSession().createQuery(
				"FROM DepartmentModel").list();
		departmentHashMap = new HashMap<Long, String>();
		departmentHashMap.put((long) 0, "None");
		for (DepartmentModel model : departmentList) {
			departmentHashMap.put(model.getId(), model.getName());
		}

	}

	private void getBankDepositDetails(BankReconciliationBean bean,
			long ledger_id, boolean isCleared) throws Exception {
		BankReconciliationBean model = null;
		System.out.println("=========== QUERY============"
				+ queryBuffer.toString());
		if (bean.getDr() != 0) {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
							+ "b.chequeNo,"
							+ "case when b.chequeDate is not null then cast(b.chequeDate as string) else ' ' end,"
							+ "b.id,"
							+ "b.divisionId,"
							+ "b.departmentId)"
							+ " FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b"
							+ " WHERE a.bankAccount.id = :bank_account_id"
							+ // to account id
							" AND b.account.id = :from_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");
			if (isCleared) {
				queryBuffer
						.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = a.bankAccount.id"
								+ " AND c.transafer_ledger.id = b.account.id"
								+ " AND c.transaction.id = a.transactionId)");
			} else {
				queryBuffer
						.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = a.bankAccount.id"
								+ " AND c.transafer_ledger.id = b.account.id"
								+ " AND c.transaction.id = a.transactionId)");
			}

			model = (BankReconciliationBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id", ledger_id)
					.setLong("from_account_id", bean.getTransactionAccountId())
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		} else {
			// Constructor 3
			queryBuffer
					.append("SELECT new com.inventory.finance.bean.BankReconciliationBean(a.bill_no,"
							+ "b.chequeNo,"
							+ "case when b.chequeDate is not null then cast(b.chequeDate as string) else ' ' end,"
							+ "b.id,"
							+ "b.divisionId,"
							+ "b.departmentId)"
							+ " FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b"
							+ " WHERE b.account.id = :bank_account_id"
							+ " AND a.transactionId = :transaction_id"
							+ " AND a.date = :tran_date");

			if (isCleared) {
				queryBuffer
						.append(" AND b.id IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = b.account.id"
								+ " AND c.transafer_ledger.id = a.bankAccount.id"
								+ " AND c.transaction.id = a.transactionId)");
			} else {
				queryBuffer
						.append(" AND b.id NOT IN (SELECT c.invoiceId FROM BankRecociliationModel c"
								+ " WHERE c.invoiceId = b.id"
								+ " AND c.ledger.id = b.account.id"
								+ " AND c.transafer_ledger.id = a.bankAccount.id"
								+ " AND c.transaction.id = a.transactionId)");
			}
			model = (BankReconciliationBean) getSession()
					.createQuery(queryBuffer.toString())
					.setLong("bank_account_id", ledger_id)
					.setLong("transaction_id", bean.getTransactionId())
					.setDate("tran_date",
							CommonUtil.getSQLDateFromUtilDate(bean.getDate()))
					.uniqueResult();
		}
		if (model != null) {
			bean.setBillNo(model.getBillNo());
			bean.setChequeNo(model.getChequeNo());
			if (model.getChequeDate() != null) {
				bean.setChequeDate(model.getChequeDate().toString());
			}
			bean.setRemarks("");
			bean.setDivision(divisionHashMap.get(model.getDivisionId()));
			bean.setDept(departmentHashMap.get(model.getDeptId()));
			bean.setInvoiceId(model.getInvoiceId());

		} else {
			System.out.println("=========== MODEL IS NULL==========");
		}

		/*
		 * if(model.getBank_account_deposit_list() == null){ System.out.println(
		 * "=========== MODEL .getBank_account_deposit_list() IS NULL=========="
		 * ); } else { System.out.println(
		 * "=========== MODEL .getBank_account_deposit_list() IS NOT NULL=========="
		 * ); }
		 */
		queryBuffer.delete(0, queryBuffer.length());
		// bean.setComments(comments);

	}

	@SuppressWarnings("unchecked")
	private List<BankReconciliationBean> getTransactionDetails(long ledger_id,
			Date from_date, Date to_date) throws Exception {
		List<BankReconciliationBean> list = new ArrayList<BankReconciliationBean>();
		queryBuffer.delete(0, queryBuffer.length());
		// Constructor 1
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean( "
						+ "a.date,"// + "cast(a.date as string),"
						+ " b.fromAcct.name,"
						+ "b.amount,"
						+ "0.0,"

						+ " ' ',"
						+ " a.transaction_id,"
						+ " b.fromAcct.id,"
						+ "a.transaction_type )"
						+ " FROM TransactionModel a  JOIN a.transaction_details_list b"
						+ " WHERE a.date BETWEEN :from_date AND :to_date"
						+ " AND b.toAcct.id = :ledger_id" /*
														 * +
														 * " AND a.transaction_type = 8"
														 */);
		queryBuffer
				.append(" AND a.transaction_id NOT IN (SELECT c.transaction.id FROM BankRecociliationModel c"
						+ " WHERE c.ledger.id = b.toAcct.id"
						+ " AND c.transafer_ledger.id = b.fromAcct.id"
						+ " AND c.transaction.id = a.transaction_id)");

		list.addAll(getSession().createQuery(queryBuffer.toString())
				.setDate("from_date", from_date).setDate("to_date", to_date)
				.setLong("ledger_id", ledger_id).list());

		queryBuffer.delete(0, queryBuffer.length());
		// Constructor 1
		queryBuffer
				.append("SELECT new com.inventory.finance.bean.BankReconciliationBean( "
						+ "a.date,"// + "cast(a.date as string),"
						+ " b.toAcct.name,"
						+ "0.0,"
						+ "b.amount,"
						+ " ' ',"
						+ " a.transaction_id,"
						+ " b.toAcct.id,"
						+ " a.transaction_type)"
						+ " FROM TransactionModel a  JOIN a.transaction_details_list b"
						+ " WHERE a.date BETWEEN :from_date AND :to_date"
						+ " AND b.fromAcct.id = :ledger_id" /*
															 * +
															 * " AND a.transaction_type = 8"
															 */);
		queryBuffer
				.append(" AND a.transaction_id NOT IN (SELECT c.transaction.id FROM BankRecociliationModel c"
						+ " WHERE c.ledger.id = b.fromAcct.id"
						+ " AND c.transafer_ledger.id = b.toAcct.id"
						+ " AND c.transaction.id = a.transaction_id)");

		list.addAll(getSession().createQuery(queryBuffer.toString())
				.setDate("from_date", from_date).setDate("to_date", to_date)
				.setLong("ledger_id", ledger_id).list());

		queryBuffer.delete(0, queryBuffer.length());
		return list;
	}

	public double getOpeningBalance(Date start_date, long ledger_id)
			throws Exception {
		double op_bal = 0;
		try {

			// ArrayList resultList = new ArrayList();

			begin();

			Object objDr = getSession()
					.createQuery(
							"select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and "
									+ "b.toAcct.id =:led)")
					.setLong("led", ledger_id).setDate("stdt", start_date)
					.uniqueResult();
			Object objCr = getSession()
					.createQuery(
							"select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and "
									+ "b.fromAcct.id =:led)")
					.setLong("led", ledger_id).setDate("stdt", start_date)
					.uniqueResult();

			Object ledgOpBalCr = getSession()
					.createQuery(
							"select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:cr and date <:stdt "
									+ " and id=(select max(id) from LedgerOpeningBalanceModel where date <:stdt and ledger.id=:id)")
					.setLong("id", ledger_id).setDate("stdt", start_date)
					.setParameter("cr", SConstants.CR).uniqueResult();

			Object ledgOpBalDr = getSession()
					.createQuery(
							"select amount from LedgerOpeningBalanceModel where ledger.id=:id and type=:dr and date <:stdt "
									+ " and id=(select max(id) from LedgerOpeningBalanceModel where date <:stdt and ledger.id=:id)")
					.setLong("id", ledger_id).setDate("stdt", start_date)
					.setParameter("dr", SConstants.CR).uniqueResult();
			;

			if (objDr != null)
				op_bal += (Double) objDr;

			if (objCr != null)
				op_bal -= (Double) objCr;

			if (ledgOpBalCr != null)
				op_bal -= (Double) ledgOpBalCr;

			if (ledgOpBalDr != null)
				op_bal += (Double) ledgOpBalDr;

			commit();

			return op_bal;

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}

	}
}
