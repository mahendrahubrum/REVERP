package com.inventory.sales.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.finance.model.PaymentModeModel;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author anil
 * @date 02-Sep-2015
 * @Project REVERP
 */

@Entity
@Table(name = SConstants.tb_names.I_SALES_PAYMENT_MODE_DETAILS)
public class SalesPaymentModeDetailsModel implements Serializable {

	private static final long serialVersionUID = -8907443675790642641L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "payment_mode_id")
	private PaymentModeModel paymentMode;
	
	@Column(name = "amount",columnDefinition ="double default 0", nullable=false)
	private double amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PaymentModeModel getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentModeModel paymentMode) {
		this.paymentMode = paymentMode;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
