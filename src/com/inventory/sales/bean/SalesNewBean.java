package com.inventory.sales.bean;

public class SalesNewBean {

	private long id, customer_id;
	private double payedAmt;
	
	

	public SalesNewBean(long customer_id, double payedAmt) {
		super();
		this.customer_id = customer_id;
		this.payedAmt = payedAmt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public double getPayedAmt() {
		return payedAmt;
	}

	public void setPayedAmt(double payedAmt) {
		this.payedAmt = payedAmt;
	}

}
