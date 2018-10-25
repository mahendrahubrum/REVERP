package com.inventory.reports.bean;

public class ItemWiseNoSalesReportBean {
	String item,supplier;
	double stock;
	public ItemWiseNoSalesReportBean(String item, String supplier,
			double stock) {
		super();
		this.item = item;
		this.supplier = supplier;
		this.stock = stock;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public double getStock() {
		return stock;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
}
