package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Oct 28, 2013
 */
public class UnderCreditPeriodBean {
	
	private String name;
	private String salesNo;
	private double totalAmount;
	private double balanceAmount;
	
	public UnderCreditPeriodBean(String name, String salesNo,
			double totalAmount, double balanceAmount) {
		super();
		this.name = name;
		this.salesNo = salesNo;
		this.totalAmount = totalAmount;
		this.balanceAmount = balanceAmount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSalesNo() {
		return salesNo;
	}
	public void setSalesNo(String salesNo) {
		this.salesNo = salesNo;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

}
