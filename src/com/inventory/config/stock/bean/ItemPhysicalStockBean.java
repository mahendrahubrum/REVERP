package com.inventory.config.stock.bean;

public class ItemPhysicalStockBean {

	String name,unit;
	double current,physical,diff,val;
	
	public ItemPhysicalStockBean(String name, String unit, double current,
			double physical, double diff, double val) {
		super();
		this.name = name;
		this.unit = unit;
		this.current = current;
		this.physical = physical;
		this.diff = diff;
		this.val = val;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getCurrent() {
		return current;
	}
	public void setCurrent(double current) {
		this.current = current;
	}
	public double getPhysical() {
		return physical;
	}
	public void setPhysical(double physical) {
		this.physical = physical;
	}
	public double getDiff() {
		return diff;
	}
	public void setDiff(double diff) {
		this.diff = diff;
	}
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}
	
}
