package com.inventory.reports.bean;

import java.sql.Date;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 19, 2014
 */
public class ItemwisrRentDetailBean {
	
	Date date;
	Long rentno;
	String customer;
	String item;
	String basicUnit;
	Double totalQty;
	Double notreturnedqty;
	Double period;
	String periodtype;
	
	
	
	
	public ItemwisrRentDetailBean() {
		super();
	}
	
	
	
	public ItemwisrRentDetailBean(Date date, Long rentno, String customer,
			String item, String basicUnit, Double totalQty,
			Double notreturnedqty, Double period, String periodtype) {
		super();
		this.date = date;
		this.rentno = rentno;
		this.customer = customer;
		this.item = item;
		this.basicUnit = basicUnit;
		this.totalQty = totalQty;
		this.notreturnedqty = notreturnedqty;
		this.period = period;
		this.periodtype = periodtype;
	}



	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getRentno() {
		return rentno;
	}
	public void setRentno(Long rentno) {
		this.rentno = rentno;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getBasicUnit() {
		return basicUnit;
	}
	public void setBasicUnit(String basicUnit) {
		this.basicUnit = basicUnit;
	}
	public Double getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(Double totalQty) {
		this.totalQty = totalQty;
	}
	public Double getNotreturnedqty() {
		return notreturnedqty;
	}
	public void setNotreturnedqty(Double notreturnedqty) {
		this.notreturnedqty = notreturnedqty;
	}
	public Double getPeriod() {
		return period;
	}
	public void setPeriod(Double period) {
		this.period = period;
	}
	public String getPeriodtype() {
		return periodtype;
	}
	public void setPeriodtype(String periodtype) {
		this.periodtype = periodtype;
	}
	
	
	
	
	
	
	
	
	

}
