package com.inventory.subscription.bean;

public class SubscriptionListBean{
	
	private String accountType;
	private String name;
	private String creationDate;
	private String subscriptionDate;
	private String subscriptionType;
	private String subscriber;
	private String rate;
	private String period;
	private double cash;
	private double credit;
	
	public SubscriptionListBean(String accountType,String name,
			String creationDate,String subscriptionDate,
			String subscriptionType,String subscriber,
			String rate,String period,double cash,double credit){
		super();
		this.accountType=accountType;
		this.name=name;
		this.creationDate=creationDate;
		this.subscriptionDate=subscriptionDate;
		this.subscriptionType=subscriptionType;
		this.subscriber=subscriber;
		this.rate=rate;
		this.period=period;
		this.cash=cash;
		this.credit=credit;
	}
	
	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
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
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getSubscriptionDate() {
		return subscriptionDate;
	}
	public void setSubscriptionDate(String subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}
	public String getSubscriptionType() {
		return subscriptionType;
	}
	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	
}
