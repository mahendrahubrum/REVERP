package com.inventory.reports.bean;

import java.io.Serializable;

public class BankReconciliationReportBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String particulars ;
	private double dr ;
	private double cr ;
	private double balance;
	
	
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public double getDr() {
		return dr;
	}
	public void setDr(double dr) {
		this.dr = dr;
	}
	public double getCr() {
		return cr;
	}
	public void setCr(double cr) {
		this.cr = cr;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	

}
