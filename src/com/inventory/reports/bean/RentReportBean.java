package com.inventory.reports.bean;

import java.sql.Date;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 15, 2014
 */
public class RentReportBean {
	
	Date date;
	String office;
	String customer;
	Long rentno;
	String employeeName;
	String items;
	Double amount;
	Double totalpaidamnt;
	Double balanceamount;
	
	public RentReportBean() {
		super();
	}
	
	
	public RentReportBean(Date date, String office, String customer,
			Long rentno, String employeeName, String items, Double amount) {
		super();
		this.date = date;
		this.office = office;
		this.customer = customer;
		this.rentno = rentno;
		this.employeeName = employeeName;
		this.items = items;
		this.amount = amount;
	}

	

	public RentReportBean(Date date, String office, String customer,
			Long rentno, String employeeName, String items, Double amount,
			Double totalpaidamnt) {
		super();
		this.date = date;
		this.office = office;
		this.customer = customer;
		this.rentno = rentno;
		this.employeeName = employeeName;
		this.items = items;
		this.amount = amount;
		this.totalpaidamnt = totalpaidamnt;
	}


	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
	public Long getRentno() {
		return rentno;
	}

	public void setRentno(Long rentno) {
		this.rentno = rentno;
	}

	

	public Double getTotalpaidamnt() {
		return totalpaidamnt;
	}


	public void setTotalpaidamnt(Double totalpaidamnt) {
		this.totalpaidamnt = totalpaidamnt;
	}


	public String getEmployeeName() {
		return employeeName;
	}


	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}


	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	

}
