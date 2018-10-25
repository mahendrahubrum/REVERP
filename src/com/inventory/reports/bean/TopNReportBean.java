package com.inventory.reports.bean;

import java.io.Serializable;


public class TopNReportBean implements Serializable,Comparable<TopNReportBean>{
	private long parentId;
	private long subGroupId;
	private String item;
	private double amountOrQuantity;
	private String unit;
	/**
	 * For Addition 0 - PurchaseModel,PurchaseGRNModel 
	 * For Substraction 1 - PurchaseReturnModel
	 * 
	 */
	private int calculation_type;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean equals(Object obj){
		TopNReportBean bean = (TopNReportBean)obj;
		if(this.parentId == bean.parentId && this.subGroupId == bean.subGroupId){
	//	if(true){
		/*	System.out.println("========this ===="+this.amountOrQuantity);
			System.out.println("========bean ===="+bean.amountOrQuantity);
		*/
			if(calculation_type == 0){
				bean.amountOrQuantity += this.amountOrQuantity;
			} else {
				bean.amountOrQuantity -= this.amountOrQuantity;
			}
			/*if(this.unit.equals(bean.getUnit())){
				
			} else {
				
			}*/
			
			
		//	this.amountOrQuantityString = this.amountOrQuantity+"";
			return true;
		}
		return false;		
	}
	
	public TopNReportBean() {
		super();
	}
	public TopNReportBean(long parentId, long subGroupId, String item, double amountOrQuantity, int calculation_type,String unit) {
		super();
		this.parentId = parentId;
		this.subGroupId = subGroupId;
		this.item = item;
		this.amountOrQuantity = amountOrQuantity;
		this.calculation_type = calculation_type;
		this.unit = unit;
	}
	


	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getCalculation_type() {
		return calculation_type;
	}

	public void setCalculation_type(int calculation_type) {
		this.calculation_type = calculation_type;
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

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public double getAmountOrQuantity() {
		return amountOrQuantity;
	}

	public void setAmountOrQuantity(double amountOrQuantity) {
		this.amountOrQuantity = amountOrQuantity;
	}

	@Override
	public int compareTo(TopNReportBean obj) {
		return (int) (obj.amountOrQuantity - this.amountOrQuantity);
	}
	
	
}
