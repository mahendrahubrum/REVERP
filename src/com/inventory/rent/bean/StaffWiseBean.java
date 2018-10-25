package com.inventory.rent.bean;

public class StaffWiseBean {
	
	String staff,customer,month,date;
	double totalRent,payment,balance,due;
	
	
	public double getDue() {
		return due;
	}


	public void setDue(double due) {
		this.due = due;
	}


	public StaffWiseBean() {
		// TODO Auto-generated constructor stub
	}
	
	
	public StaffWiseBean(String staff, String customer, String month,
			String date, double totalRent, double payment, double balance,double due) {
		super();
		this.staff = staff;
		this.customer = customer;
		this.month = month;
		this.date = date;
		this.totalRent = totalRent;
		this.payment = payment;
		this.balance = balance;
		this.due=due;
	}
	public String getStaff() {
		return staff;
	}
	public void setStaff(String staff) {
		this.staff = staff;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getTotalRent() {
		return totalRent;
	}
	public void setTotalRent(double totalRent) {
		this.totalRent = totalRent;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	

}
