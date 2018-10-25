package com.webspark.bean;


public class ExpenseTransactionBean {

	long fromId,toId;
	long currencyId;
	double conversionRate, amount;
	
	
	public ExpenseTransactionBean() {
		
	}

	public ExpenseTransactionBean(long fromId, long toId, long currencyId,
			double conversionRate, double amount) {
		super();
		this.fromId = fromId;
		this.toId = toId;
		this.currencyId = currencyId;
		this.conversionRate = conversionRate;
		this.amount = amount;
	}

	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public long getToId() {
		return toId;
	}

	public void setToId(long toId) {
		this.toId = toId;
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
