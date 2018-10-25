package com.inventory.reports.bean;

import java.sql.Date;

public class RentDueBean {

	String month,date;
	double rentDue,balanceDue,payment,due;
	
	public RentDueBean() {
		// TODO Auto-generated constructor stub
	}
	public RentDueBean(String month,double rentDue,double payment,double balanceDue){
		super();
		this.month=month;
		this.rentDue=rentDue;
		this.balanceDue=balanceDue;
		this.payment=payment;
	}
	
	public RentDueBean(String month, double due, double rentDue ,String date,
			double payment, double balanceDue) {
		super();
		this.month = month;
		this.date = date;
		this.rentDue = rentDue;
		this.balanceDue = balanceDue;
		this.payment = payment;
		this.due = due;
	}
	public RentDueBean(String month, double due, double rentDue,
			double payment, double balanceDue) {
		super();
		this.month = month;
		this.rentDue = rentDue;
		this.balanceDue = balanceDue;
		this.payment = payment;
		this.due = due;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getDue() {
		return due;
	}
	public void setDue(double due) {
		this.due = due;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getRentDue() {
		return rentDue;
	}
	public void setRentDue(double rentDue) {
		this.rentDue = rentDue;
	}
	public double getBalanceDue() {
		return balanceDue;
	}
	public void setBalanceDue(double balanceDue) {
		this.balanceDue = balanceDue;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	
	
}
