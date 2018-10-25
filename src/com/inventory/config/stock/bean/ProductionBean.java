package com.inventory.config.stock.bean;

public class ProductionBean {

	private int parent_id, child_id;
	private long item_id, unit_id;
	private double quatity, qty_in_basic_unit;
	private String item_name, unit_name, item_code;

	public ProductionBean() {
		super();
	}

	public ProductionBean(int parent_id, int child_id, long item_id,
			long unit_id, double quatity, String item_code, String item_name,
			String unit_name, double qty_in_basic_unit) {
		super();
		this.parent_id = parent_id;
		this.child_id = child_id;
		this.item_id = item_id;
		this.unit_id = unit_id;
		this.quatity = quatity;
		this.item_code = item_code;
		this.item_name = item_name;
		this.unit_name = unit_name;
		this.qty_in_basic_unit=qty_in_basic_unit;
	}

	public long getItem_id() {
		return item_id;
	}

	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}

	public double getQuatity() {
		return quatity;
	}

	public void setQuatity(double quatity) {
		this.quatity = quatity;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public int getChild_id() {
		return child_id;
	}

	public void setChild_id(int child_id) {
		this.child_id = child_id;
	}

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}

	public double getQty_in_basic_unit() {
		return qty_in_basic_unit;
	}

	public void setQty_in_basic_unit(double qty_in_basic_unit) {
		this.qty_in_basic_unit = qty_in_basic_unit;
	}

}
