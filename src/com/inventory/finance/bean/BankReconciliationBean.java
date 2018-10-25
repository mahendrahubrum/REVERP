package com.inventory.finance.bean;

import java.io.Serializable;
import java.util.Date;

import com.webspark.common.util.CommonUtil;

public class BankReconciliationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id ;	
	private String dateString ;
	private Date date;
	private String billNo ;
	private String particulars ;
	private double dr ;
	private double cr ;
	private String chequeNo ;
	private String chequeDate;
	private String remarks;
	private String division;
	private String dept;
	private long divisionId;
	private long deptId;
	private long transactionId ;
	private long transactionAccountId ;
	private long invoiceId;
	private Date clearingDate;
	private String comments;
	private int transaction_type;
	//Constructor 1
	public BankReconciliationBean(Date date, String particulars,
			double dr, double cr,String remarks, long transactionId, long transferAccountId, int transaction_type) {
		this.date = date;
		this.particulars = particulars;
		this.dr = dr;
		this.cr = cr;
		this.transactionAccountId = transferAccountId;
		this.transactionId = transactionId;
		this.dateString = CommonUtil.formatDateToCommonFormat(date);
		this.transaction_type = transaction_type;
		this.remarks = remarks;
	}
	//Constructor 2
	public BankReconciliationBean(long id, Date date, String particulars,
			double dr, double cr,  long transactionId, long transferAccountId, 
			long invoiceId,  Date clearingDate, String comments, int transaction_type) {
		this.id = id;
		this.date = date;	
		this.particulars = particulars;
		this.dr = dr;
		this.cr = cr;	
		this.transactionAccountId = transferAccountId;
		this.transactionId = transactionId;
		this.invoiceId = invoiceId;
		this.dateString = CommonUtil.formatDateToCommonFormat(date);
		this.clearingDate = clearingDate;
		this.comments = comments;		
		this.transaction_type = transaction_type;
	}
	
	//Constructor 3	
	public BankReconciliationBean( String billNo, String chequeNo, String chequeDate,
			long invoiceId, long divisionId, long deptId) {
		this.billNo = billNo;		
		this.chequeNo = chequeNo;
		this.chequeDate = chequeDate;
		this.invoiceId = invoiceId;	
		this.divisionId = divisionId;
		this.deptId = deptId;
	}
	//Constructor 4
	public BankReconciliationBean( String billNo, String chequeNo, String chequeDate,long invoiceId) {
		this.billNo = billNo;		
		this.chequeNo = chequeNo;
		this.chequeDate = chequeDate;
		this.invoiceId = invoiceId;			
	}


	public long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(long divisionId) {
		this.divisionId = divisionId;
	}

	public long getDeptId() {
		return deptId;
	}

	public void setDeptId(long deptId) {
		this.deptId = deptId;
	}

	public int getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(int transaction_type) {
		this.transaction_type = transaction_type;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	public Date getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(Date clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getDr() {
		return dr;
	}

	public void setDr(double dr) {
		this.dr = dr;
	}

	public double getCr() {
		return cr;
	}

	public void setCr(double cr) {
		this.cr = cr;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}


	public long getTransactionAccountId() {
		return transactionAccountId;
	}

	public void setTransactionAccountId(long transactionAccountId) {
		this.transactionAccountId = transactionAccountId;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

	
	

}
