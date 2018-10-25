package com.inventory.sales.bean;

import java.util.Date;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 20, 2014
 */
public class SalesChartBean {

	private Date date;
	private double amount;
	private String name;
	private String unit;
	
	public SalesChartBean() {
		super();
	}

	public SalesChartBean(Date date, double amount) {
		this.date = date;
		this.amount = amount;
	}
	
	
	
	public SalesChartBean(double amount, String name) {
		super();
		this.amount = amount;
		this.name = name;
	}
	public SalesChartBean(String unit, String name) {
		super();
		this.unit = unit;
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
}
