package com.inventory.reports.bean;

import java.io.Serializable;



public class ExceptionReportBean implements Serializable{	
	//private Date date;
	private String item;
	private String ledger;
	
	private String firstNo;
	private String firstDate;
	private double firstQty;
	
	private String secondNo;
	private String secondDate;
	private double secondQty;
		
	private double balanceQty;
	
/*	private long parentId;
	private long subGroupId;
	
	public boolean equals(Object obj){
		PurchaseExceptionReportBean bean = (PurchaseExceptionReportBean)obj;
		if(this.parentId == bean.parentId && this.subGroupId == bean.subGroupId){
			return true;
		}
		return false;		
	}*/
	
	
	public ExceptionReportBean() {
		super();
	}
	public ExceptionReportBean(String item, String supplier, String firstNo, String firstDate, 
			double firstQty, String secondNo, String secondDate, double secondQty) {
		super();
		System.out.println("==========HI INVOKED================");
		this.item = item;
		this.ledger = supplier;
		
		this.firstNo = firstNo;
		this.firstDate = firstDate;
		this.firstQty = firstQty;
		
		this.secondNo = secondNo;
		this.secondDate = secondDate;
		this.secondQty = secondQty;
		
		this.balanceQty = firstQty - secondQty;
		
	}
	

	public String getItem() {
		return item;
	}


	public void setItem(String item) {
		this.item = item;
	}


	


	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public String getFirstNo() {
		return firstNo;
	}


	public void setFirstNo(String firstNo) {
		this.firstNo = firstNo;
	}


	public String getFirstDate() {
		return firstDate;
	}


	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}


	public double getFirstQty() {
		return firstQty;
	}


	public void setFirstQty(double firstQty) {
		this.firstQty = firstQty;
	}


	public String getSecondNo() {
		return secondNo;
	}


	public void setSecondNo(String secondNo) {
		this.secondNo = secondNo;
	}


	public String getSecondDate() {
		return secondDate;
	}


	public void setSecondDate(String secondDate) {
		this.secondDate = secondDate;
	}


	public double getSecondQty() {
		return secondQty;
	}


	public void setSecondQty(double secondQty) {
		this.secondQty = secondQty;
	}


	public double getBalanceQty() {
		return balanceQty;
	}


	public void setBalanceQty(double balanceQty) {
		this.balanceQty = balanceQty;
	}

/*
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(long subGroupId) {
		this.subGroupId = subGroupId;
	}
	*/
	
	
		
	
}
