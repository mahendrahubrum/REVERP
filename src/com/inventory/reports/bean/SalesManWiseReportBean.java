package com.inventory.reports.bean;


public class SalesManWiseReportBean {

	long id;
    int type;
	String sales_man,from,to,item;
	private String unit;
	double amount,paid,balance;
	double purchaseQuantity,purchaseReturnQuantity,salesQuantity,salesReturnQuantity;

	public SalesManWiseReportBean() {
	}
	
	public SalesManWiseReportBean(long id, String sales_man, double amount,
			double paid, double balance) {
		super();
		this.id = id;
		this.sales_man = sales_man;
		this.amount = amount;
		this.paid = paid;
		this.balance = balance;
	}
	
	public SalesManWiseReportBean(String sales_man, String from, String to,
			double amount, double paid, double balance) {
		super();
		this.sales_man = sales_man;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.paid = paid;
		this.balance = balance;
	}

	public SalesManWiseReportBean(long id,int type,String sales_man, String item,String unit,double purchaseQuantity,double purchaseReturnQuantity,
			double salesQuantity,double salesReturnQuantity,double balance) {
		super();
		this.sales_man = sales_man;
		this.item = item;
		this.balance = balance;
		this.purchaseQuantity = purchaseQuantity;
		this.purchaseReturnQuantity = purchaseReturnQuantity;
		this.salesQuantity = salesQuantity;
		this.salesReturnQuantity = salesReturnQuantity;
		this.unit = unit;
		this.id =id;
		this.setType(type);
	}
	
	public SalesManWiseReportBean(double purchaseQuantity,double purchaseReturnQuantity,double salesQuantity,double salesReturnQuantity,double balance) {
		super();
		this.balance = balance;
		this.purchaseQuantity = purchaseQuantity;
		this.purchaseReturnQuantity = purchaseReturnQuantity;
		this.salesQuantity = salesQuantity;
		this.salesReturnQuantity = salesReturnQuantity;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSales_man() {
		return sales_man;
	}

	public void setSales_man(String sales_man) {
		this.sales_man = sales_man;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public double getPurchaseQuantity() {
		return purchaseQuantity;
	}

	public void setPurchaseQuantity(double purchaseQuantity) {
		this.purchaseQuantity = purchaseQuantity;
	}

	public double getPurchaseReturnQuantity() {
		return purchaseReturnQuantity;
	}

	public void setPurchaseReturnQuantity(double purchaseReturnQuantity) {
		this.purchaseReturnQuantity = purchaseReturnQuantity;
	}

	public double getSalesQuantity() {
		return salesQuantity;
	}

	public void setSalesQuantity(double salesQuantity) {
		this.salesQuantity = salesQuantity;
	}

	public double getSalesReturnQuantity() {
		return salesReturnQuantity;
	}

	public void setSalesReturnQuantity(double salesReturnQuantity) {
		this.salesReturnQuantity = salesReturnQuantity;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}
