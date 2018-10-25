package com.inventory.reports.bean;

public class ManufacturingProfitBean {

	String item,date;
	long id,number;
	double purchase,sale,expense;
	
	public ManufacturingProfitBean() {
	}
	public ManufacturingProfitBean(long id,long number,String item, double purchase, double sale,
			double expense,String date) {
		super();
		this.id=id;
		this.number=number;
		this.item = item;
		this.purchase = purchase;
		this.sale = sale;
		this.expense = expense;
		this.date=date;
	}
	
	public ManufacturingProfitBean(String item, double purchase, double sale,
			double expense) {
		super();
		this.item = item;
		this.purchase = purchase;
		this.sale = sale;
		this.expense = expense;
	}
	public ManufacturingProfitBean(long id,String item, double purchase, double sale,
			double expense,String date) {
		super();
		this.id=id;
		this.item = item;
		this.purchase = purchase;
		this.sale = sale;
		this.expense = expense;
		this.date=date;
	}
	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getPurchase() {
		return purchase;
	}
	public void setPurchase(double purchase) {
		this.purchase = purchase;
	}
	public double getSale() {
		return sale;
	}
	public void setSale(double sale) {
		this.sale = sale;
	}
	public double getExpense() {
		return expense;
	}
	public void setExpense(double expense) {
		this.expense = expense;
	}
	
	
}
