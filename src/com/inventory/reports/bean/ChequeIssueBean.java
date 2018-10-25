package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 18, 2013
 */
public class ChequeIssueBean {

	private String date;
	private String issuedDate;
	private String supplier;
	private String bank;
	private double amount;
	private int type;
	
	public ChequeIssueBean(String date,String issuedDate, String supplier, String bank,
			double amount) {
		super();
		this.date = date;
		this.issuedDate = issuedDate;
		this.supplier = supplier;
		this.bank = bank;
		this.amount = amount;
	}
	
	public ChequeIssueBean(String date,String issuedDate, String supplier, String bank,
			double amount,int type) {
		super();
		this.date = date;
		this.issuedDate = issuedDate;
		this.supplier = supplier;
		this.bank = bank;
		this.amount = amount;
		this.setType(type);
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
}
