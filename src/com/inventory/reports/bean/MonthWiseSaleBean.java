package com.inventory.reports.bean;




public class MonthWiseSaleBean {
	
	int type;
	String particular;
	double amount;
	
	long id;
	String sales,date,customer,item,month;
	
	
	public MonthWiseSaleBean(double amount, long id, String sales, String date,
			String customer, String item,int type) {
		super();
		this.amount = amount;
		this.id = id;
		this.sales = sales;
		this.date = date;
		this.customer = customer;
		this.item = item;
		this.type=type;
	}
	
	public MonthWiseSaleBean(int type, String particular, double amount) {
		super();
		this.type = type;
		this.particular = particular;
		this.amount = amount;
	}
	
	public MonthWiseSaleBean(String particular, double amount) {
		super();
		this.particular = particular;
		this.amount = amount;
	}
	public MonthWiseSaleBean(String particular, double amount,String month) {
		super();
		this.particular = particular;
		this.amount = amount;
		this.month = month;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getParticular() {
		return particular;
	}
	public void setParticular(String particular) {
		this.particular = particular;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSales() {
		return sales;
	}
	public void setSales(String sales) {
		this.sales = sales;
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
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
	
	
}
