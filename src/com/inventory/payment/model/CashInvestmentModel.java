package com.inventory.payment.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 22, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_CASH_INVESTMENT)
public class CashInvestmentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6899470793836663469L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "investment_no")
	private long investment_no;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "capital_account_id")
	private long capital_account_id;

	@Column(name = "cash_account_id")
	private long cash_account_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "description", length = 400)
	private String description;

	@Column(name = "type")
	private int type;

	@Column(name = "amount")
	private double amount;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	public CashInvestmentModel() {
		super();
	}

	public CashInvestmentModel(long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInvestment_no() {
		return investment_no;
	}

	public void setInvestment_no(long investment_no) {
		this.investment_no = investment_no;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public long getCapital_account_id() {
		return capital_account_id;
	}

	public void setCapital_account_id(long capital_account_id) {
		this.capital_account_id = capital_account_id;
	}

	public long getCash_account_id() {
		return cash_account_id;
	}

	public void setCash_account_id(long cash_account_id) {
		this.cash_account_id = cash_account_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
