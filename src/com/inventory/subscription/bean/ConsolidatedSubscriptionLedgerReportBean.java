package com.inventory.subscription.bean;

public class ConsolidatedSubscriptionLedgerReportBean {

	String name;
	double opening,cash,credit,balance,current;
	private long ledgerId=0;
	
	public ConsolidatedSubscriptionLedgerReportBean(String name,
			double opening, double cash, double credit, double balance,
			double current) {
		super();
		this.name = name;
		this.opening = opening;
		this.cash = cash;
		this.credit = credit;
		this.balance = balance;
		this.current = current;
	}
	
	public ConsolidatedSubscriptionLedgerReportBean(long ledgerId,String name,
			double opening, double cash, double credit, double balance,
			double current) {
		super();
		this.name = name;
		this.opening = opening;
		this.cash = cash;
		this.credit = credit;
		this.balance = balance;
		this.current = current;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getOpening() {
		return opening;
	}
	public void setOpening(double opening) {
		this.opening = opening;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
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
	public double getCurrent() {
		return current;
	}
	public void setCurrent(double current) {
		this.current = current;
	}

	public long getLedgerId() {
		return ledgerId;
	}

	public void setLedgerId(long ledgerId) {
		this.ledgerId = ledgerId;
	}
}
