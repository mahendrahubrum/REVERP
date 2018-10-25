package com.inventory.reports.bean;

import java.sql.Date;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 16, 2014
 */

public class RentCustomerLedgerReportBean {

	Date date;
	
	Long rentno;
	
	Double cash;
	Double returnAmount;
	Double balanceAmount;
	
	public RentCustomerLedgerReportBean() {
		super();
	}

	public RentCustomerLedgerReportBean(Date date, Long rentno, Double cash,
			Double returnAmount, Double balanceAmount) {
		super();
		this.date = date;
		this.rentno = rentno;
		this.cash = cash;
		this.returnAmount = returnAmount;
		this.balanceAmount = balanceAmount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getRentno() {
		return rentno;
	}

	public void setRentno(Long rentno) {
		this.rentno = rentno;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Double getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(Double returnAmount) {
		this.returnAmount = returnAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	
	
	
}
