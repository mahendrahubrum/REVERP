package com.inventory.reports.bean;

import java.sql.Date;

/**
 * @author sangeeth
 * 
 *         WebSpark.
 * 
 *         Sep 02, 2014
 */
public class RentItemReportBean {

	String issueDate,finalDate;
	
	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	Date date;
	String office;
	String customer;
	Long rentno;
	String employeeName;
	String items;
	String tinno;
	Date returndate;
	Double amount;
	Double period;
	String returnDate;

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	Double paidamount;
	Double balanceamount;

	Double quantity;
	Double returnQty;
	Double taxAmount;
	Double taxPercentage;
	Double returnItem;
	Double returnRent;
	
	Double unitPrice;
	Long id;

	String unit;
	Double paid;

	public Double getPaid() {
		return paid;
	}

	public RentItemReportBean(String date, String items, String returndate,
			Double amount, Double period, Double quantity, Double unitPrice,Double returnItem,Double returnRent,String returnDate) {
		super();
		this.issueDate = date;
		this.items = items;
		this.finalDate = returndate;
		this.amount = amount;
		this.period = period;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.returnItem=returnItem;
		this.returnRent=returnRent;
		this.returnDate=returnDate;
	}

	public Double getReturnItem() {
		return returnItem;
	}

	public void setReturnItem(Double returnItem) {
		this.returnItem = returnItem;
	}

	public Double getReturnRent() {
		return returnRent;
	}

	public void setReturnRent(Double returnRent) {
		this.returnRent = returnRent;
	}

	public Double getPeriod() {
		return period;
	}

	public void setPeriod(Double period) {
		this.period = period;
	}

	public void setPaid(Double paid) {
		this.paid = paid;
	}

	public RentItemReportBean(String items, Double amount, Double paidamount,
			Double quantity, Double taxAmount, Double taxPercentage,
			Double unitPrice, String unit) {
		super();
		this.items = items;
		this.amount = amount;
		this.paidamount = paidamount;
		this.quantity = quantity;
		this.taxAmount = taxAmount;
		this.taxPercentage = taxPercentage;
		this.unitPrice = unitPrice;
		this.unit = unit;
	}

	public RentItemReportBean(Date date, String office, String customer,
			Long rentno, String employeeName, String items, Date returndate,
			Double amount, Double paidamount, Double balanceamount) {
		super();
		this.date = date;
		this.office = office;
		this.customer = customer;
		this.rentno = rentno;
		this.employeeName = employeeName;
		this.items = items;
		this.returndate = returndate;
		this.amount = amount;
		this.paidamount = paidamount;
		this.balanceamount = balanceamount;
	}

	public RentItemReportBean(Date date, String office, String customer,
			Long rentno, String employeeName, String items) {
		super();
		this.date = date;
		this.office = office;
		this.customer = customer;
		this.rentno = rentno;
		this.employeeName = employeeName;
		this.items = items;
	}

	public RentItemReportBean(Long rentno, Double quantity, Double returnQty) {
		super();
		this.rentno = rentno;
		this.quantity = quantity;
		this.returnQty = returnQty;
	}

	public RentItemReportBean(Long rentno, Double quantity, Double returnQty,
			Long id) {
		super();
		this.rentno = rentno;
		this.quantity = quantity;
		this.returnQty = returnQty;
		this.id = id;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Double getTaxPercentage() {
		return taxPercentage;
	}

	public String getTinno() {
		return tinno;
	}

	public void setTinno(String tinno) {
		this.tinno = tinno;
	}

	public void setTaxPercentage(Double taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public RentItemReportBean() {
		super();
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(Double returnQty) {
		this.returnQty = returnQty;
	}

	public Double getPaidamount() {
		return paidamount;
	}

	public void setPaidamount(Double paidamount) {
		this.paidamount = paidamount;
	}

	public Double getBalanceamount() {
		return balanceamount;
	}

	public void setBalanceamount(Double balanceamount) {
		this.balanceamount = balanceamount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Long getRentno() {
		return rentno;
	}

	public void setRentno(Long rentno) {
		this.rentno = rentno;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getReturndate() {
		return returndate;
	}

	public void setReturndate(Date returndate) {
		this.returndate = returndate;
	}

	public RentItemReportBean(Date date, String office, String customer,
			Long rentno, String employeeName, String items, String tinno,
			Date returndate, Double amount, Double paidamount,
			Double balanceamount, Double quantity, Double returnQty,
			Double taxAmount, Double taxPercentage, Double unitPrice, Long id,
			String unit, Double paid,Double returnItem,Double returnRent) {
		super();
		this.date = date;
		this.office = office;
		this.customer = customer;
		this.rentno = rentno;
		this.employeeName = employeeName;
		this.items = items;
		this.tinno = tinno;
		this.returndate = returndate;
		this.amount = amount;
		this.paidamount = paidamount;
		this.balanceamount = balanceamount;
		this.quantity = quantity;
		this.returnQty = returnQty;
		this.taxAmount = taxAmount;
		this.taxPercentage = taxPercentage;
		this.unitPrice = unitPrice;
		this.id = id;
		this.unit = unit;
		this.paid = paid;
		this.returnItem=returnItem;
		this.returnRent=returnRent;
	}

}
