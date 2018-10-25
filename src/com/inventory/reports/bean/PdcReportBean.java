package com.inventory.reports.bean;

import java.util.Date;

public class PdcReportBean {

	long id;
	String bill, bank, chequeNo, currency;
	Date chequeDate;
	Date issueDate;
	int status;
	double amount, convRate;
	
	public PdcReportBean(long id, String bill, String bank, Date chequeDate) {
		super();
		this.id = id;
		this.bill = bill;
		this.bank = bank;
		this.chequeDate = chequeDate;
	}
	
	public PdcReportBean(String bill, String bank, String chequeNo,
			String currency, Date chequeDate, Date issueDate, int status,
			double amount, double convRate) {
		super();
		this.bill = bill;
		this.bank = bank;
		this.chequeNo = chequeNo;
		this.currency = currency;
		this.chequeDate = chequeDate;
		this.issueDate = issueDate;
		this.status = status;
		this.amount = amount;
		this.convRate = convRate;
	}
	
	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBill() {
		return bill;
	}

	public void setBill(String bill) {
		this.bill = bill;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public double getConvRate() {
		return convRate;
	}

	public void setConvRate(double convRate) {
		this.convRate = convRate;
	}

}
