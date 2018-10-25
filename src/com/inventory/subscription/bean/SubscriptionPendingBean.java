package com.inventory.subscription.bean;

public class SubscriptionPendingBean {
	
	private String accountType;
	private String name;
	private String subscriptionDate;
	private String subscriber;
	private String period;
	private double rate;
	private double paid;
	private double pending;
	private double balance;
	public SubscriptionPendingBean(String accountType, String name,
			String subscriptionDate, String subscriber, String period,
			double rate, double paid, double pending) {
		super();
		this.accountType = accountType;
		this.name = name;
		this.subscriptionDate = subscriptionDate;
		this.subscriber = subscriber;
		this.period = period;
		this.rate = rate;
		this.paid = paid;
		this.pending = pending;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubscriptionDate() {
		return subscriptionDate;
	}
	public void setSubscriptionDate(String subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public double getPending() {
		return pending;
	}
	public void setPending(double pending) {
		this.pending = pending;
	}
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	

}
