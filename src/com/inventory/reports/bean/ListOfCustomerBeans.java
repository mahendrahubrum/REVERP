package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Sep 21, 2013
 */
public class ListOfCustomerBeans {

	private String name;
	private String code;
	private String address;
	private String currency;
	
	public ListOfCustomerBeans(String name, String code, String address,
			String currency) {
		super();
		this.name = name;
		this.code = code;
		this.address = address;
		this.currency = currency;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
}
