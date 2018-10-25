package com.inventory.subscription.bean;


public class SubscriptionLedgerBean {
	
	private String subscription;
	String paymentDate,rent;
	double amountPaid,period,balance;
	String cash;
	
	public SubscriptionLedgerBean(String subscription, String paymentDate,
			String rent, double amountPaid,double period,double balance,String cash) {
		super();
		this.subscription = subscription;
		this.paymentDate = paymentDate;
		this.rent = rent;
		this.amountPaid = amountPaid;
		this.period=period;
		this.balance=balance;
		this.cash=cash;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public double getPeriod() {
		return period;
	}
	public void setPeriod(double period) {
		this.period = period;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getSubscription() {
		return subscription;
	}
	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getRent() {
		return rent;
	}
	public void setRent(String rent) {
		this.rent = rent;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	
	
	
}
