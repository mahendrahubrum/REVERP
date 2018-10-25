package com.inventory.reports.bean;

import java.util.Comparator;



public class FastSlowMovingItemsReportBean{
	public class FastMovingItemsComparator implements Comparator<FastSlowMovingItemsReportBean>{

		@Override
		public int compare(FastSlowMovingItemsReportBean o1,
				FastSlowMovingItemsReportBean o2) {		
			return (int) (o1.getCurrentStock() - o2.getCurrentStock());
		}
		
	}

	public class SlowMovingItemsComparator implements Comparator<FastSlowMovingItemsReportBean>{

		@Override
		public int compare(FastSlowMovingItemsReportBean o1,
				FastSlowMovingItemsReportBean o2) {		
			return (int) (o2.getCurrentStock() - o1.getCurrentStock());
		}
		
	}
	
	private String itemName;
	private double purchaseQty;
	private double saleQty;
	private double currentStock;
	private long itemId;
	private long parentId;
	private long subGroupId;
	
	public boolean equals(Object obj){
		FastSlowMovingItemsReportBean bean = (FastSlowMovingItemsReportBean)obj;
		if(this.parentId == bean.parentId && this.subGroupId == bean.subGroupId){
			return true;
		}
		return false;		
	}
	
	public FastSlowMovingItemsReportBean() {
		super();
	}
	
	
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public double getPurchaseQty() {
		return purchaseQty;
	}
	public void setPurchaseQty(double purchaseQty) {
		this.purchaseQty = purchaseQty;
	}
	public double getSaleQty() {
		return saleQty;
	}
	public void setSaleQty(double saleQty) {
		this.saleQty = saleQty;
	}
	public double getCurrentStock() {
		return currentStock;
	}
	public void setCurrentStock(double currentStock) {
		this.currentStock = currentStock;
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
