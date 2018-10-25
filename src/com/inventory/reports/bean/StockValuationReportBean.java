package com.inventory.reports.bean;

/**
 * @author Muhammed shah A
 * 
 * WebSpark.
 *
 * Sep 21, 2013
 */
public class StockValuationReportBean {

	private String item;
	private String location;
	private String quantity;
	private String rate;
	private String date;
	private String currentStock;
	
	public StockValuationReportBean(String item, String location, String quantity,String rate,String date, String currentStock) {
		super();
		this.item = item;
		this.location = location;
		this.quantity = quantity;
		this.rate = rate;
		this.date = date;
		this.currentStock = currentStock;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	

	public String getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(String currentStock) {
		this.currentStock = currentStock;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
	
	
}
