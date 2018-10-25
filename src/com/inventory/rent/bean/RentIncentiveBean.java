package com.inventory.rent.bean;

import java.util.Date;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Jun 26, 2014
 */

public class RentIncentiveBean {

	String employee;
	Double rent;
	Double incentive;
	Double totalamt;
	Long rentid;
	Date date;
	Double balance;
	
	
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	
	public Double getIncentive() {
		return incentive;
	}
	public void setIncentive(Double incentive) {
		this.incentive = incentive;
	}
	public Double getTotalamt() {
		return totalamt;
	}
	public void setTotalamt(Double totalamt) {
		this.totalamt = totalamt;
	}
	
	
	public Long getRentid() {
		return rentid;
	}
	public void setRentid(Long rentid) {
		this.rentid = rentid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getRent() {
		return rent;
	}
	public void setRent(Double rent) {
		this.rent = rent;
	}
	public RentIncentiveBean() {
		super();
	}
	public RentIncentiveBean(String employee, Double rent, Double incentive,
			Double totalamt) {
		super();
		this.employee = employee;
		this.rent = rent;
		this.incentive = incentive;
		this.totalamt = totalamt;
	}
	public RentIncentiveBean(String employee, Double rent) {
		super();
		this.employee = employee;
		this.rent = rent;
	}
	public RentIncentiveBean(String employee, Double rent, Double incentive,
			Double totalamt, Long rentid, Date date, Double balance) {
		super();
		this.employee = employee;
		this.rent = rent;
		this.incentive = incentive;
		this.totalamt = totalamt;
		this.rentid = rentid;
		this.date = date;
		this.balance = balance;
	}
	
	
	
}
