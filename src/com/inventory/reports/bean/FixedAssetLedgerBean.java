package com.inventory.reports.bean;

import java.util.Date;


public class FixedAssetLedgerBean {
	private long id;
	private String group;
	private String fixedAsset;
	private Date date;
	private String particulars;
	private double openingQty;
	private double openingBal;
	private double qty;
	private double unitPrice;
	private double amount;
	private double depPercentage;
	private double depValue;
	private double closingQty;
	private double closingBalance;
	private String currency;
	private String unit;
	public FixedAssetLedgerBean() {
		// TODO Auto-generated constructor stub
	}
	public FixedAssetLedgerBean(long id) {
			this.id = id;
		}

	public FixedAssetLedgerBean(String group) {
		this.group = group;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getDepPercentage() {
		return depPercentage;
	}
	public void setDepPercentage(double depPercentage) {
		this.depPercentage = depPercentage;
	}
	public double getDepValue() {
		return depValue;
	}
	public void setDepValue(double depValue) {
		this.depValue = depValue;
	}
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getFixedAsset() {
		return fixedAsset;
	}

	public void setFixedAsset(String fixedAsset) {
		this.fixedAsset = fixedAsset;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getOpeningQty() {
		return openingQty;
	}

	public void setOpeningQty(double openingQty) {
		this.openingQty = openingQty;
	}

	public double getOpeningBal() {
		return openingBal;
	}

	public void setOpeningBal(double openingBal) {
		this.openingBal = openingBal;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getClosingQty() {
		return closingQty;
	}

	public void setClosingQty(double closingQty) {
		this.closingQty = closingQty;
	}

	public double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object obj) {
		FixedAssetLedgerBean bean = (FixedAssetLedgerBean) obj;
		return this.id == bean.id;
	}
	

}
