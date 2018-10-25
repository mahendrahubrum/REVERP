package com.inventory.expenditureposting.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Apr 10 2014
 */
@Entity
@Table(name = SConstants.tb_names.BATCH_EXPENDITURE_PAYMENT_DETAILS)
public class BatchExpenditurePaymentDetailsModel implements Serializable {

	private static final long serialVersionUID = 3420821069412714190L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "from_account")
	private LedgerModel from_account;

	@OneToOne
	@JoinColumn(name = "to_account")
	private LedgerModel to_account;

	@Column(name = "amount")
	private double amount;

	@Column(name = "real_amount")
	private double real_amount;

	@Column(name = "type")
	private int type;

	@Column(name = "comments")
	private String comments;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LedgerModel getFrom_account() {
		return from_account;
	}

	public void setFrom_account(LedgerModel from_account) {
		this.from_account = from_account;
	}

	public LedgerModel getTo_account() {
		return to_account;
	}

	public void setTo_account(LedgerModel to_account) {
		this.to_account = to_account;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getReal_amount() {
		return real_amount;
	}

	public void setReal_amount(double real_amount) {
		this.real_amount = real_amount;
	}

}
