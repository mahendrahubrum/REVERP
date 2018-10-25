package com.inventory.reports.bean;

import java.util.Comparator;



public class StockTransactionAnalysisReportBean{	
	private String itemName;
	private double openingQty;
	private double receivedQty;
	private double issuedQty;
	private double balanceQty;
//	private long itemId;
	private long parentId;
	private long subGroupId;
	
	public boolean equals(Object obj){
		StockTransactionAnalysisReportBean bean = (StockTransactionAnalysisReportBean)obj;
		if(this.parentId == bean.parentId && this.subGroupId == bean.subGroupId){
			return true;
		}
		return false;		
	}
	
	public StockTransactionAnalysisReportBean() {
		super();
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getOpeningQty() {
		return openingQty;
	}

	public void setOpeningQty(double openingQty) {
		this.openingQty = openingQty;
	}

	public double getReceivedQty() {
		return receivedQty;
	}

	public void setReceivedQty(double receivedQty) {
		this.receivedQty = receivedQty;
	}

	public double getIssuedQty() {
		return issuedQty;
	}

	public void setIssuedQty(double issuedQty) {
		this.issuedQty = issuedQty;
	}

	public double getBalanceQty() {
		return balanceQty;
	}

	public void setBalanceQty(double balanceQty) {
		this.balanceQty = balanceQty;
	}

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
	
	
	
		
	
}
