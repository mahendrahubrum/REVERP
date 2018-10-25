package com.inventory.reports.bean;

public class CommissionStockBean {

	String name,code,unit;
	double balance;
	public CommissionStockBean(String name, String code, String unit,
			double balance) {
		super();
		this.name = name;
		this.code = code;
		this.unit = unit;
		this.balance = balance;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
}
