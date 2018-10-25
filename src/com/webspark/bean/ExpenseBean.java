package com.webspark.bean;


public class ExpenseBean {

	long ledger;
	boolean isClearingAgent;
	int transactionType;
	long currencyId;
	double conversionRate, amount;
	
	
	public ExpenseBean() {
		
	}

	
	public ExpenseBean(boolean isClearingAgent, long ledger, int transactionType, long currencyId, double conversionRate, double amount) {
		super();
		this.ledger = ledger;
		this.isClearingAgent = isClearingAgent;
		this.transactionType = transactionType;
		this.currencyId = currencyId;
		this.conversionRate = conversionRate;
		this.amount = amount;
	}


	public ExpenseBean(long currencyId, double amount, double conversionRate) {
		super();
		this.currencyId = currencyId;
		this.amount = amount;
		this.conversionRate = conversionRate;
		
	}


	public long getLedger() {
		return ledger;
	}


	public void setLedger(long ledger) {
		this.ledger = ledger;
	}


	public boolean isClearingAgent() {
		return isClearingAgent;
	}


	public void setClearingAgent(boolean isClearingAgent) {
		this.isClearingAgent = isClearingAgent;
	}


	public int getTransactionType() {
		return transactionType;
	}


	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}


	public long getCurrencyId() {
		return currencyId;
	}


	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}


	public double getConversionRate() {
		return conversionRate;
	}


	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}
	
}
