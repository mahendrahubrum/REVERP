package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author sangeeth
 * @date 28-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_BANK_DETAILS_INVOICE_MAP)
public class BankDetailsInvoiceMapModel implements Serializable {

	public BankDetailsInvoiceMapModel() {
		
	}
	
	public BankDetailsInvoiceMapModel(int type, long office_id, long paymentId, long paymentDetailsId, long invoice_id, int payment_type) {
		super();
		this.type = type;
		this.office_id = office_id;
		this.paymentId = paymentId;
		this.paymentDetailsId = paymentDetailsId;
		this.invoice_id = invoice_id;
		this.payment_type=payment_type;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	private int type;
	
	@Column(name = "office_id")
	private long office_id;
	
	@Column(name = "payment_id")
	private long paymentId;
	
	@Column(name = "payment_details_id")
	private long paymentDetailsId;

	@Column(name = "invoice_id")
	private long invoice_id;
	
	@Column(name = "payment_type",columnDefinition ="int default 0", nullable=false)
	private int payment_type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(long invoice_id) {
		this.invoice_id = invoice_id;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public long getPaymentDetailsId() {
		return paymentDetailsId;
	}

	public void setPaymentDetailsId(long paymentDetailsId) {
		this.paymentDetailsId = paymentDetailsId;
	}

	public int getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(int payment_type) {
		this.payment_type = payment_type;
	}
	
}