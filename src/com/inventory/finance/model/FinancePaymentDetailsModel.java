package com.inventory.finance.model;

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
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 30, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_FINANCE_PAYMENT_DETAILS)
public class FinancePaymentDetailsModel implements Serializable {

	private static final long serialVersionUID = 3420821069412714190L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "from_account")
	private FinanceComponentModel from_account;
	
	@OneToOne
	@JoinColumn(name = "to_account")
	private FinanceComponentModel to_account;
	
	@OneToOne
	@JoinColumn(name = "currency")
	private CurrencyModel currency;

	@Column(name = "amount")
	private double amount;

	@Column(name = "comments")
	private String comments;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public FinanceComponentModel getFrom_account() {
		return from_account;
	}

	public void setFrom_account(FinanceComponentModel from_account) {
		this.from_account = from_account;
	}

	public FinanceComponentModel getTo_account() {
		return to_account;
	}

	public void setTo_account(FinanceComponentModel to_account) {
		this.to_account = to_account;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
