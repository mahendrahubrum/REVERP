package com.inventory.reports.bean;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Feb 11, 2015
 */
public class SalaryDisbursalBean {

	private String name = "";
	private String status = "";
	private double workedDays = 0;
	private double paymentAmount = 0;
	private double advanceAmount = 0;
	private double netAmount = 0;

	
	public SalaryDisbursalBean(String name, String status, double workedDays,
			double paymentAmount, double advanceAmount, double netAmount) {
		super();
		this.name = name;
		this.status = status;
		this.workedDays = workedDays;
		this.paymentAmount = paymentAmount;
		this.advanceAmount = advanceAmount;
		this.netAmount = netAmount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getWorkedDays() {
		return workedDays;
	}

	public void setWorkedDays(double workedDays) {
		this.workedDays = workedDays;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public double getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(double advanceAmount) {
		this.advanceAmount = advanceAmount;
	}

	public double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}

}
