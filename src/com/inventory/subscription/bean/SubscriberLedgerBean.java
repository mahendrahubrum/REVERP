package com.inventory.subscription.bean;

public class SubscriberLedgerBean {
	
	long id;
	String date,particular;
	double amount,periodBalance,balance,credit;
	public SubscriberLedgerBean(long id, String date, String particular,
			double amount, double credit, double periodBalance, double balance) {
		super();
		this.id = id;
		this.date = date;
		this.particular = particular;
		this.amount = amount;
		this.periodBalance = periodBalance;
		this.balance = balance;
		this.credit=credit;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getParticular() {
		return particular;
	}
	public void setParticular(String particular) {
		this.particular = particular;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getPeriodBalance() {
		return periodBalance;
	}
	public void setPeriodBalance(double periodBalance) {
		this.periodBalance = periodBalance;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
