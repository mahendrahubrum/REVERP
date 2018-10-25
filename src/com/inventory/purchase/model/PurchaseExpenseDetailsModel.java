package com.inventory.purchase.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author anil
 * @date 02-Sep-2015
 * @Project REVERP
 */

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PURCHASE_EXPENSE_DETAILS)
public class PurchaseExpenseDetailsModel implements Serializable {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "clearingAgent")
	private boolean clearingAgent;
	
	@Column(name = "ledger_id")
	private long ledger_id;
	
	@Column(name = "transaction_type")
	private int transaction_type;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;

	@Column(name = "amount")
	private double amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isClearingAgent() {
		return clearingAgent;
	}

	public void setClearingAgent(boolean clearingAgent) {
		this.clearingAgent = clearingAgent;
	}

	public long getLedger_id() {
		return ledger_id;
	}

	public void setLedger_id(long ledger_id) {
		this.ledger_id = ledger_id;
	}

	public int getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(int transaction_type) {
		this.transaction_type = transaction_type;
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
