package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 27, 2013
 */
public class ReorderReportBean {

	private String itemName;
	private String itemCode;
	private double reorderLevel;
	private double currentBalance;
	public ReorderReportBean(String itemName, String itemCode,
			double reorderLevel, double currentBalance) {
		super();
		this.itemName = itemName;
		this.itemCode = itemCode;
		this.reorderLevel = reorderLevel;
		this.currentBalance = currentBalance;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public double getReorderLevel() {
		return reorderLevel;
	}
	public void setReorderLevel(double reorderLevel) {
		this.reorderLevel = reorderLevel;
	}
	public double getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}
}
