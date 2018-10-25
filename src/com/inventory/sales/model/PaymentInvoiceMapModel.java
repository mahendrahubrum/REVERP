package com.inventory.sales.model;

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
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PAYMENT_INVOICE_MAP)
public class PaymentInvoiceMapModel implements Serializable {

	public PaymentInvoiceMapModel() {
		super();
	}

	
	public PaymentInvoiceMapModel(long id) {
		super();
		this.id = id;
	}
	
	public PaymentInvoiceMapModel(long invoiceId, long paymentId, int type) {
		super();
		this.invoiceId = invoiceId;
		this.paymentId = paymentId;
		this.type = type;
	}
	
	public PaymentInvoiceMapModel(long invoiceId, long paymentId, int type, double amount) {
		super();
		this.invoiceId = invoiceId;
		this.paymentId = paymentId;
		this.type = type;
		this.amount = amount;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	private int type;
	
	@Column(name = "payment_type",columnDefinition ="int default 0", nullable=false)
	private int payment_type;
	
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
	
	@Column(name = "cheque" ,columnDefinition ="boolean default false", nullable=false)
	private boolean cheque;
	
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


	public boolean isCheque() {
		return cheque;
	}


	public void setCheque(boolean cheque) {
		this.cheque = cheque;
	}


	public int getPayment_type() {
		return payment_type;
	}


	public void setPayment_type(int payment_type) {
		this.payment_type = payment_type;
	}

}
