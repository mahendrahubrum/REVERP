package com.inventory.reports.bean;

public class ItemReportBean {

	private String name = "";
	private String code = "";
	private String unit = "";
	private double current_quantity = 0, grv_quantity = 0;
	private String subgroup = "";
	private String purchaseType = "";
	private String rack = "";
	private String stock = "";
	private String manufacturingDate = "";
	private String expiryDate = "";
	private String currency = "";
	private double rate = 0;
	private double qtyInExtraUnit = 0;
	
	public ItemReportBean() {
		super();
	}

	public ItemReportBean(double rate) {
		super();
		this.rate = rate;
	}

	public ItemReportBean(String name, double rate) {
		super();
		this.name = name;
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getCurrent_quantity() {
		return current_quantity;
	}

	public void setCurrent_quantity(double current_quantity) {
		this.current_quantity = current_quantity;
	}

	public String getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(String subgroup) {
		this.subgroup = subgroup;
	}

	public double getGrv_quantity() {
		return grv_quantity;
	}

	public void setGrv_quantity(double grv_quantity) {
		this.grv_quantity = grv_quantity;
	}

	public String getRack() {
		return rack;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getManufacturingDate() {
		return manufacturingDate;
	}

	public void setManufacturingDate(String manufacturingDate) {
		this.manufacturingDate = manufacturingDate;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
	}

	public double getQtyInExtraUnit() {
		return qtyInExtraUnit;
	}

	public void setQtyInExtraUnit(double qtyInExtraUnit) {
		this.qtyInExtraUnit = qtyInExtraUnit;
	}
}
