package com.inventory.reports.bean;

import java.util.Date;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 2, 2014
 */
public class PurchaseRateReportBean {
	String item;
	double quantity;
	String unit;
	double rate;
	Date date;
	int repeat;

	public PurchaseRateReportBean(String item, double quantity, String unit,
			double rate) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.unit = unit;
		this.rate = rate;
	}

	public PurchaseRateReportBean(Date date, String item, double quantity,
			String unit, double rate) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.unit = unit;
		this.rate = rate;
		this.date = date;
	}
	
	public PurchaseRateReportBean(String item, double quantity, String unit,
			double rate, Date date, int repeat) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.unit = unit;
		this.rate = rate;
		this.date = date;
		this.repeat = repeat;
	}

	public PurchaseRateReportBean() {
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
}
