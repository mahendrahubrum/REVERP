package com.inventory.subscription.bean;

public class SubscriptionTransactionBean{
	
	private String date;
	private String subscriber;
	private String rental;
	private double rate;
	private double cash;
	private double credit;
	public SubscriptionTransactionBean(String date, String subscriber,
			String rental, double rate, double cash, double credit) {
		super();
		this.date = date;
		this.subscriber = subscriber;
		this.rental = rental;
		this.rate = rate;
		this.cash = cash;
		this.credit = credit;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	public String getRental() {
		return rental;
	}
	public void setRental(String rental) {
		this.rental = rental;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
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
}
