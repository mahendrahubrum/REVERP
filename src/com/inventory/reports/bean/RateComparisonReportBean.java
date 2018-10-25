package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 2, 2014
 */
public class RateComparisonReportBean {
	String item;
	String unit;
	double supplierRate;
	String supplier;
	double employeeRate;
	String employee;
	String date;
	
	public RateComparisonReportBean() {
	}
	public RateComparisonReportBean(String item, String unit,
			double supplierRate, String supplier, double employeeRate,
			String employee, String date) {
		super();
		this.item = item;
		this.unit = unit;
		this.supplierRate = supplierRate;
		this.supplier = supplier;
		this.employeeRate = employeeRate;
		this.employee = employee;
		this.date = date;
	}
	
	public RateComparisonReportBean(String item, String unit,
			double employeeRate, String employee, String date) {
		super();
		this.item = item;
		this.unit = unit;
		this.employeeRate = employeeRate;
		this.employee = employee;
		this.date = date;
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
	public double getSupplierRate() {
		return supplierRate;
	}
	public void setSupplierRate(double supplierRate) {
		this.supplierRate = supplierRate;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public double getEmployeeRate() {
		return employeeRate;
	}
	public void setEmployeeRate(double employeeRate) {
		this.employeeRate = employeeRate;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
