package com.inventory.payroll.bean;

public class PaySlipBean {

	String user,month,component;
	double payroll,advance,lop,overTime,loan,payment,amount;
	
	public PaySlipBean(String component, double amount) {
		super();
		this.component = component;
		this.amount = amount;
	}
	public PaySlipBean(String user, String month, double payroll,
			double advance, double lop, double overTime, double loan,
			double payment) {
		super();
		this.user = user;
		this.month = month;
		this.payroll = payroll;
		this.advance = advance;
		this.lop = lop;
		this.overTime = overTime;
		this.loan = loan;
		this.payment = payment;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getPayroll() {
		return payroll;
	}
	public void setPayroll(double payroll) {
		this.payroll = payroll;
	}
	public double getAdvance() {
		return advance;
	}
	public void setAdvance(double advance) {
		this.advance = advance;
	}
	public double getLop() {
		return lop;
	}
	public void setLop(double lop) {
		this.lop = lop;
	}
	public double getOverTime() {
		return overTime;
	}
	public void setOverTime(double overTime) {
		this.overTime = overTime;
	}
	public double getLoan() {
		return loan;
	}
	public void setLoan(double loan) {
		this.loan = loan;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
}
