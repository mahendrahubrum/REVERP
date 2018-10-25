package com.inventory.subscription.bean;


public class RentalCustomerLedgerBean {

	long id, sales_no;
	String type,date;
	double sale,cash,period,balance;
	
	public RentalCustomerLedgerBean(long id, long sales_no, String type,
			String date, double sale, double cash) {
		super();
		this.id = id;
		this.sales_no = sales_no;
		this.type = type;
		this.date = date;
		this.sale = sale;
		this.cash = cash;
	}
	
	public RentalCustomerLedgerBean(long id, long sales_no, String type,
			String date, double sale, double cash, double period, double balance) {
		super();
		this.id = id;
		this.sales_no = sales_no;
		this.type = type;
		this.date = date;
		this.sale = sale;
		this.cash = cash;
		this.period = period;
		this.balance = balance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSales_no() {
		return sales_no;
	}
	public void setSales_no(long sales_no) {
		this.sales_no = sales_no;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getSale() {
		return sale;
	}
	public void setSale(double sale) {
		this.sale = sale;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
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
	
}
