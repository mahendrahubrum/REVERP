package com.inventory.reports.bean;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 28, 2013
 */
public class DeliveryNoteReportBean {

	String date="";
	String customer="";
	String salesNo="";
	String office="";
	String items="";
	double amount=0;
	String currency;

	public DeliveryNoteReportBean(String date, String customer, String salesNo,
			String office, String items, double amount) {
		super();
		this.date = date;
		this.customer = customer;
		this.salesNo = salesNo;
		this.office = office;
		this.items = items;
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSalesNo() {
		return salesNo;
	}

	public void setSalesNo(String salesNo) {
		this.salesNo = salesNo;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
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
}
