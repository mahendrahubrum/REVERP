package com.inventory.reports.bean;

import java.util.Date;


/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 26, 2013
 */
public class FinanceTransactionReportBean {

	private String fromaccount;
	private String toaccount;
	private String amount;
	private Date date;
	private String description;
	private String paymentNo;
	private double inwards;
	private double outwards;
	private double balance;
	private int isAll;
	long id;
	
	public FinanceTransactionReportBean(String fromaccount, String toaccount,
			String amount, Date date, String description) {
		super();
		this.fromaccount = fromaccount;
		this.toaccount = toaccount;
		this.amount = amount;
		this.date = date;
		this.description = description;
	}
	public FinanceTransactionReportBean(String fromaccount, String toaccount,
			String amount, Date date, String description,long id) {
		super();
		this.fromaccount = fromaccount;
		this.toaccount = toaccount;
		this.amount = amount;
		this.date = date;
		this.description = description;
		this.id=id;
	}
	
	
	public FinanceTransactionReportBean(String fromaccount, Date date,
			String description, String paymentNo, double inwards,
			double outwards, double balance,int isAll) {
		super();
		this.fromaccount = fromaccount;
		this.date = date;
		this.description = description;
		this.paymentNo = paymentNo;
		this.inwards = inwards;
		this.outwards = outwards;
		this.balance = balance;
		this.isAll = isAll;
	}
	public FinanceTransactionReportBean(String fromaccount, Date date,
			String description, String paymentNo, double inwards,
			double outwards, double balance,int isAll,long id) {
		super();
		this.fromaccount = fromaccount;
		this.date = date;
		this.description = description;
		this.paymentNo = paymentNo;
		this.inwards = inwards;
		this.outwards = outwards;
		this.balance = balance;
		this.isAll = isAll;
		this.id=id;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getFromaccount() {
		return fromaccount;
	}
	public void setFromaccount(String fromaccount) {
		this.fromaccount = fromaccount;
	}
	public String getToaccount() {
		return toaccount;
	}
	public void setToaccount(String toaccount) {
		this.toaccount = toaccount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}



	public String getPaymentNo() {
		return paymentNo;
	}



	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}



	public double getInwards() {
		return inwards;
	}



	public void setInwards(double inwards) {
		this.inwards = inwards;
	}



	public double getOutwards() {
		return outwards;
	}



	public void setOutwards(double outwards) {
		this.outwards = outwards;
	}



	public double getBalance() {
		return balance;
	}



	public void setBalance(double balance) {
		this.balance = balance;
	}


	public int getIsAll() {
		return isAll;
	}


	public void setIsAll(int isAll) {
		this.isAll = isAll;
	}
	

}
