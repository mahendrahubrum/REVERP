package com.inventory.reports.bean;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark
 * 
 *         Aug 8, 2013
 */

public class PurchaseReturnReportBean {
	String date = "";
	String supplier = "";
	String office = "";
	String items = "";
	double amount = 0, payment_amt;
	String number;
	String currency;

	public PurchaseReturnReportBean(String date, String supplier,
			String office, String items, double amount) {
		super();
		this.date = date;
		this.supplier = supplier;
		this.office = office;
		this.items = items;
		this.amount = amount;
	}

	public PurchaseReturnReportBean(String date, String supplier,
			String office, String items, double amount, double payment_amt,
			String number) {
		super();
		this.date = date;
		this.supplier = supplier;
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

	public String getsupplier() {
		return supplier;
	}

	public void setsupplier(String supplier) {
		this.supplier = supplier;
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

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
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
