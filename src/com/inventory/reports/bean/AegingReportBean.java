package com.inventory.reports.bean;

import java.util.List;

public class AegingReportBean {
	private long id;
	private String particulars;
	private double openingBalance;
	private List<Double> periodWiseAmountList;
	private double closingBalance;
	private int creditPeriod;
	private double unAdjustAmount;
	//private long supp
	public AegingReportBean() {
		
	}
	public AegingReportBean(long id, double closingBalance) {
		this.id = id;
		this.closingBalance = closingBalance;
	}
	public AegingReportBean(long id, int creditPeriod){
		this.id = id;
		this.creditPeriod = creditPeriod;
	}
	
	
	public double getUnAdjustAmount() {
		return unAdjustAmount;
	}
	public void setUnAdjustAmount(double unAdjustAmount) {
		this.unAdjustAmount = unAdjustAmount;
	}
	public int getCreditPeriod() {
		return creditPeriod;
	}
	public void setCreditPeriod(int creditPeriod) {
		this.creditPeriod = creditPeriod;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public double getOpeningBalance() {
		return openingBalance;
	}
	public void setOpeningBalance(double openingBalance) {
		this.openingBalance = openingBalance;
	}
	public List<Double> getPeriodWiseAmountList() {
		return periodWiseAmountList;
	}
	public void setPeriodWiseAmountList(List<Double> periodWiseAmountList) {
		this.periodWiseAmountList = periodWiseAmountList;
	}
	
	
}
