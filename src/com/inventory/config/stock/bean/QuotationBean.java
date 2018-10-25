package com.inventory.config.stock.bean;

public class QuotationBean {

	private long id;
	private String number;

	public QuotationBean(long id, String number) {
		super();
		this.id = id;
		this.number = number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
