package com.inventory.journal.bean;

public class JournalBean {

	long currency;
	double amount;
	double conv_rate;
	
	public JournalBean(long currency, double amount, double conv_rate) {
		super();
		this.currency = currency;
		this.amount = amount;
		this.conv_rate = conv_rate;
	}
	public long getCurrency() {
		return currency;
	}
	public void setCurrency(long currency) {
		this.currency = currency;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getConv_rate() {
		return conv_rate;
	}
	public void setConv_rate(double conv_rate) {
		this.conv_rate = conv_rate;
	}
}
