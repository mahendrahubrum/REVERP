package com.inventory.reports.bean;

public class PeriodicalAnalysisReportBean {
	private long itemId;
	private String item;
	// private int monthId;
	private String month;
	private int slNo;
	private double opening;
	private double purchaseOrSale;
	private double amount;
	private String currency;

	public PeriodicalAnalysisReportBean(long itemId, String item) {
		this.itemId = itemId;
		this.item = item;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getSlNo() {
		return slNo;
	}

	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}

	/*
	 * public int getMonthId() { return monthId; } public void setMonthId(int
	 * monthId) { this.monthId = monthId; }
	 */
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	/*
	 * public int getYear() { return year; } public void setYear(int year) {
	 * this.year = year; }
	 */
	public double getOpening() {
		return opening;
	}

	public void setOpening(double opening) {
		this.opening = opening;
	}

	public double getPurchaseOrSale() {
		return purchaseOrSale;
	}

	public void setPurchaseOrSale(double purchaseOrSale) {
		this.purchaseOrSale = purchaseOrSale;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
