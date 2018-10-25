package com.inventory.reports.bean;

import java.util.Date;

public class TransactionListingReportBean {
	private String account;
	private String fromOrToAccount;
	private Date date;
	private String docNo;
	private String chequeNo;
	private Date chequeDate;
	private double amount;
	private String remarks;
	private long fromAccountId;
	private int transaction_type;
	private long transactionId;
	private long ledger_id;
	public TransactionListingReportBean() {
		super();
	}
	//Constructor 1
	public TransactionListingReportBean(String account,	 String fromOrToAccount, Date date,
			double amount, String remarks, long fromAccountId, int transaction_type, long transactionId, long ledger_id){
		this.account = account;
		this.fromOrToAccount = fromOrToAccount;
		this.date = date;
		this.amount = amount;
		this.remarks = remarks;
		this.fromAccountId = fromAccountId;
		this.transaction_type = transaction_type;
		this.transactionId = transactionId;
		this.ledger_id = ledger_id;
	}
	//Constructor 2
	public TransactionListingReportBean(String docNo, String chequeNo, Date chequeDate){
		this.docNo = docNo;
		this.chequeNo = chequeNo;
		this.chequeDate = chequeDate;
	}
	//Constructor 3
		public TransactionListingReportBean(String docNo, String chequeNo){
			this.docNo = docNo;
			this.chequeNo = chequeNo;		
		}
	public TransactionListingReportBean(String account,	 String fromOrToAccount, Date date, String docNo,
			 String chequeNo, Date chequeDate, double amount, String remarks){
		this.account = account;
		this.fromOrToAccount = fromOrToAccount;
		this.date = date;
		this.docNo = docNo;
		this.chequeNo = chequeNo;
		this.chequeDate = chequeDate;
		this.amount = amount;
		this.remarks = remarks;
	}
	
	public long getLedger_id() {
		return ledger_id;
	}
	public void setLedger_id(long ledger_id) {
		this.ledger_id = ledger_id;
	}
	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	public int getTransaction_type() {
		return transaction_type;
	}
	public void setTransaction_type(int transaction_type) {
		this.transaction_type = transaction_type;
	}
	public long getFromAccountId() {
		return fromAccountId;
	}
	public void setFromAccountId(long fromAccountId) {
		this.fromAccountId = fromAccountId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getFromOrToAccount() {
		return fromOrToAccount;
	}
	public void setFromOrToAccount(String fromOrToAccount) {
		this.fromOrToAccount = fromOrToAccount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public Date getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
