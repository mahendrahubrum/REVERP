package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 18, 2013
 */
public class DailyQuotationBean {

	private String date;
	private String supplier;
	private String items;
	private String unit;
	private double rate;
	private String employee;
	private String country;
	long login;
	
	public DailyQuotationBean(String date,String employee, String supplier, String items,
			String unit, double rate) {
		super();
		this.date = date;
		this.supplier = supplier;
		this.items = items;
		this.unit = unit;
		this.rate = rate;
		this.employee = employee;
	}
	public DailyQuotationBean(String date,String employee, String supplier, String items,
			String unit, double rate,String country) {
		super();
		this.date = date;
		this.supplier = supplier;
		this.items = items;
		this.unit = unit;
		this.rate = rate;
		this.employee = employee;
		this.country = country;
	}
	
	public DailyQuotationBean(String date,String employee, String supplier, String items,
			String unit, double rate,String country,long login) {
		super();
		this.date = date;
		this.supplier = supplier;
		this.items = items;
		this.unit = unit;
		this.rate = rate;
		this.employee = employee;
		this.country = country;
		this.login=login;
	}
	
	public long getLogin() {
		return login;
	}
	public void setLogin(long login) {
		this.login = login;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
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
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
}
