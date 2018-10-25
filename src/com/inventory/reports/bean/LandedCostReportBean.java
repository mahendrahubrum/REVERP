package com.inventory.reports.bean;

public class LandedCostReportBean {
	private String billNo;
	private String date;
	private String tag;
	private double amount;
	private String item;
	private double quantity;
	private String unit;
	private double unitPrice;
	private double landedCost;
	private String currency;
	public LandedCostReportBean() {
		//super();
	}
	public LandedCostReportBean(String billNo, String date, String tag, 
			String item, double quantity, String unit, double unitPrice, double amount,
			double landedCost){
		//super();
		this.billNo =  billNo;
		this.date = date;
		this.tag = tag;
		this.amount = amount;
		this.item = item;
		this.quantity = quantity;
		this.unit = unit;
		this.unitPrice = unitPrice;
		this.landedCost = landedCost;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public double getLandedCost() {
		return landedCost;
	}
	public void setLandedCost(double landedCost) {
		this.landedCost = landedCost;
	} 
	
	
}
