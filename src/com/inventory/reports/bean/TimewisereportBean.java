package com.inventory.reports.bean;

import java.util.List;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 28, 2014
 */
public class TimewisereportBean {

	String item;
	double quantity;
	double period;
	String status;
	double discount;
	double netprice;
	double amount;
	String type;
	String particulars;
	List subList;
	double payed;
	
	public double getPayed() {
		return payed;
	}
	public void setPayed(double payed) {
		this.payed = payed;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getPeriod() {
		return period;
	}
	public void setPeriod(double period) {
		this.period = period;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getNetprice() {
		return netprice;
	}
	public void setNetprice(double netprice) {
		this.netprice = netprice;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public TimewisereportBean() {
		super();
	}
	public TimewisereportBean(String item, double quantity, double period,
			String status, double discount, double netprice, double amount,
			String type) {
		super();
		this.item = item;
		this.quantity = quantity;
		this.period = period;
		this.status = status;
		this.discount = discount;
		this.netprice = netprice;
		this.amount = amount;
		this.type = type;
	}
	
	
	
	
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public List getSubList() {
		return subList;
	}
	public void setSubList(List subList) {
		this.subList = subList;
	}
	
	
	
	public TimewisereportBean( String particulars, double amount, double payed) {
		super();
		this.amount = amount;
		this.particulars = particulars;
		this.payed = payed;
	}
	public TimewisereportBean(double amount, String particulars, List subList,
			double payed) {
		super();
		this.amount = amount;
		this.particulars = particulars;
		this.subList = subList;
		this.payed = payed;
	}
//	public AcctReportMainBean(String particulars, double amount, double payed,
//			double returned, double balance, List subList) {
//		super();
//		this.particulars = particulars;
//		this.amount = amount;
//		this.payed = payed;
//		this.returned = returned;
//		this.balance = balance;
//		this.subList = subList;
//	}
	
}
