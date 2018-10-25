package com.inventory.subscription.bean;

public class ItemWiseRentalReportBean {

	long id,sales_no;
	String date,item,ledger;
	double quantity,rate,total;
	
	public ItemWiseRentalReportBean(long id, String date, String item,
			String ledger, double quantity, double rate, double total,long sales_no) {
		super();
		this.id = id;
		this.date = date;
		this.item = item;
		this.ledger = ledger;
		this.quantity = quantity;
		this.rate = rate;
		this.total = total;
		this.sales_no=sales_no;
	}
	public long getSales_no() {
		return sales_no;
	}
	public void setSales_no(long sales_no) {
		this.sales_no = sales_no;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
}
