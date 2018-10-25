package com.inventory.reports.bean;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark
 * 
 *         Aug 13, 2013
 */

public class SalesReturnReportBean {
	String date = "";
	String customer = "";
	String office = "";
	String items = "";
	String number;
	double amount = 0, payment_amt;
	String currency;
	String paymentAmountCurrency;

	public SalesReturnReportBean(String date, String customer, String office,
			String items, double amount) {
		super();
		this.date = date;
		this.setCustomer(customer);
		this.office = office;
		this.items = items;
		this.amount = amount;
	}
	
	public SalesReturnReportBean(String date, String customer, String office,
			String items, double amount, double payment_amt, String number) {
		super();
		this.date = date;
		this.setCustomer(customer);
		this.office = office;
		this.items = items;
		this.amount = amount;
		this.payment_amt = payment_amt;
		this.number = number;
		
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPaymentAmountCurrency() {
		return paymentAmountCurrency;
	}

	public void setPaymentAmountCurrency(String paymentAmountCurrency) {
		this.paymentAmountCurrency = paymentAmountCurrency;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public double getPayment_amt() {
		return payment_amt;
	}

	public void setPayment_amt(double payment_amt) {
		this.payment_amt = payment_amt;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
