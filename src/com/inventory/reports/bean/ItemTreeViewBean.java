package com.inventory.reports.bean;

public class ItemTreeViewBean {

	String group,subgroup,item,unit;
	double quantity;
	boolean newGroup,newSubGroup;
	
	public ItemTreeViewBean(String group, String subgroup, String item,
			String unit, double quantity, boolean newGroup, boolean newSubGroup) {
		super();
		this.group = group;
		this.subgroup = subgroup;
		this.item = item;
		this.unit = unit;
		this.quantity = quantity;
		this.newGroup = newGroup;
		this.newSubGroup = newSubGroup;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getSubgroup() {
		return subgroup;
	}
	public void setSubgroup(String subgroup) {
		this.subgroup = subgroup;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public boolean isNewGroup() {
		return newGroup;
	}
	public void setNewGroup(boolean newGroup) {
		this.newGroup = newGroup;
	}
	public boolean isNewSubGroup() {
		return newSubGroup;
	}
	public void setNewSubGroup(boolean newSubGroup) {
		this.newSubGroup = newSubGroup;
	}
	
}
