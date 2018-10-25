package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 26, 2013
 */
public class BankTransactionReportBean {

	private String name;
	private String fromAccount;
	private Double amount;
	private String date;
	private String type;

	public BankTransactionReportBean(String name, String fromAccount, Double amount,
			String date, String type) {
		super();
		this.name = name;
		this.fromAccount = fromAccount;
		this.amount = amount;
		this.date = date;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
