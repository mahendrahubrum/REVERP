package com.inventory.reports.bean;

public class ItemDetailsReportBean {
	
	private int slNo=0;
	private long itemId=0;
	private String name="";
	private String code="";
	private double inwards=0;
	private double outwards=0;
	private double balance=0;
	
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
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
	public double getInwards() {
		return inwards;
	}
	public void setInwards(double inwards) {
		this.inwards = inwards;
	}
	public double getOutwards() {
		return outwards;
	}
	public void setOutwards(double outwards) {
		this.outwards = outwards;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
}
