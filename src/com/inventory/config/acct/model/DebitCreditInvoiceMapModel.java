package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 30, 2014
 */

/**
 * 
 * @author sangeeth
 * @date 25-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_DEBIT_CREDIT_INVOICE_MAP)
public class DebitCreditInvoiceMapModel implements Serializable {

	public DebitCreditInvoiceMapModel() {
		super();
	}

	
	public DebitCreditInvoiceMapModel(long id) {
		super();
		this.id = id;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	private int type;
	
	@Column(name = "supplier_customer", columnDefinition = "int default 1", nullable = false)
	private int supplier_customer;
	
	@Column(name = "invoice_id")
	private long invoiceId;

	@Column(name = "payment_id")
	private long paymentId;
	
	@Column(name = "amount")
	private double amount;
	
	@Column(name = "office_id")
	private long office_id;
	
	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public CurrencyModel getCurrencyId() {
		return currencyId;
	}


	public void setCurrencyId(CurrencyModel currencyId) {
		this.currencyId = currencyId;
	}


	public double getConversionRate() {
		return conversionRate;
	}


	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}


	public long getOffice_id() {
		return office_id;
	}


	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}


	public int getSupplier_customer() {
		return supplier_customer;
	}

	public void setSupplier_customer(int supplier_customer) {
		this.supplier_customer = supplier_customer;
	}
	
}
