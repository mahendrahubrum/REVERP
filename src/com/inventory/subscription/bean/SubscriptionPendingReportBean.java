package com.inventory.subscription.bean;

public class SubscriptionPendingReportBean {
	
	String name,rental,sdate,rdate;
	double total,paid,credit,balance;
	public SubscriptionPendingReportBean(String name, String rental,
			String sdate, String rdate, double total, double paid,
			double credit, double balance) {
		super();
		this.name = name;
		this.rental = rental;
		this.sdate = sdate;
		this.rdate = rdate;
		this.total = total;
		this.paid = paid;
		this.credit = credit;
		this.balance = balance;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRental() {
		return rental;
	}
	public void setRental(String rental) {
		this.rental = rental;
	}
	public String getSdate() {
		return sdate;
	}
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	public String getRdate() {
		return rdate;
	}
	public void setRdate(String rdate) {
		this.rdate = rdate;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}

}
