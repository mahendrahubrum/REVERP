package com.inventory.sales.bean;


public class PaymentInvoiceBean {

	int type,supplier_customer;
	long invoiceId,paymentId,office,currencyId;
	double amount,conversionRate;
	boolean isCheque;
	
	public PaymentInvoiceBean(int type, long invoiceId, long paymentId,
			long office, long currencyId, double amount, double conversionRate) {
		super();
		this.type = type;
		this.invoiceId = invoiceId;
		this.paymentId = paymentId;
		this.office = office;
		this.currencyId = currencyId;
		this.amount = amount;
		this.conversionRate = conversionRate;
	}
	public PaymentInvoiceBean(int type, long invoiceId, long paymentId,
			long office, long currencyId, double amount, double conversionRate, boolean isCheque) {
		super();
		this.type = type;
		this.invoiceId = invoiceId;
		this.paymentId = paymentId;
		this.office = office;
		this.currencyId = currencyId;
		this.amount = amount;
		this.conversionRate = conversionRate;
		this.isCheque = isCheque;
	}
	public PaymentInvoiceBean(int type, int supplier_customer, long invoiceId, long paymentId,
			long office, long currencyId, double amount, double conversionRate) {
		super();
		this.type = type;
		this.supplier_customer=supplier_customer;
		this.invoiceId = invoiceId;
		this.paymentId = paymentId;
		this.office = office;
		this.currencyId = currencyId;
		this.amount = amount;
		this.conversionRate = conversionRate;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}
	public long getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}
	public long getOffice() {
		return office;
	}
	public void setOffice(long office) {
		this.office = office;
	}
	public long getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getConversionRate() {
		return conversionRate;
	}
	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}
	public int getSupplier_customer() {
		return supplier_customer;
	}
	public void setSupplier_customer(int supplier_customer) {
		this.supplier_customer = supplier_customer;
	}
	public boolean isCheque() {
		return isCheque;
	}
	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}
	
}
