package com.inventory.reports.bean;

import java.io.Serializable;
import java.util.Date;

public class ItemWiseReportBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private Date date;
	private String supplierOrCustomer;
	private long itemId;
	private String item;
	private double quantity;
	private String unit;
//	private double unitPrice;
	private double total;
	public ItemWiseReportBean(long id, Date date, String supplierOrCustomer, long itemId,String item, double quantity, String unit) {
		this.id = id;
		this.date = date;
		this.supplierOrCustomer = supplierOrCustomer;
		this.itemId = itemId;
		this.item = item;
		this.quantity = quantity;
		this.unit = unit;
	//	this.unitPrice = unitPrice;		
	}
	
	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

//	public double getUnitPrice() {
//		return unitPrice;
//	}
//
//	public void setUnitPrice(double unitPrice) {
//		this.unitPrice = unitPrice;
//	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSupplierOrCustomer() {
		return supplierOrCustomer;
	}
	public void setSupplierOrCustomer(String supplierOrCustomer) {
		this.supplierOrCustomer = supplierOrCustomer;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	

}
