package com.inventory.payment.bean;

public class PaymentBean {

	String currency;
	String description;
	double amount;
	public PaymentBean(String currency, String description, double amount) {
		super();
		this.currency = currency;
		this.description = description;
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
}
