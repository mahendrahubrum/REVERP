package com.inventory.process.bean;

public class EndProcessBean {
	
	private long unit_id;
	private double quantity;
	
	
	
	public EndProcessBean(long unit_id, double quantity) {
		super();
		this.unit_id = unit_id;
		this.quantity = quantity;
	}
	public long getUnit_id() {
		return unit_id;
	}
	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	
	
}
