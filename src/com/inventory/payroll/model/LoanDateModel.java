package com.inventory.payroll.model;

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


/**
 * @author sangeeth
 * @date 25-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_LOAN_DATE)
public class LoanDateModel implements Serializable {

	public LoanDateModel(long id) {
		super();
		this.id = id;
	}

	public LoanDateModel() {
		super();
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name="loan_id")
	private LoanApprovalModel loan;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "amount", columnDefinition="double default 0", nullable=false)
	private double amount;
	
	@OneToOne
	@JoinColumn(name = "currency_id")
	public CurrencyModel currency;
	
	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "office", columnDefinition="bigint default 0", nullable=false)
	private long officeId;
	
	@Column(name = "payment_date")
	private Date payment_date;
	
	@Column(name = "loan_status")
	private int loanStatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LoanApprovalModel getLoan() {
		return loan;
	}

	public void setLoan(LoanApprovalModel loan) {
		this.loan = loan;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public Date getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}

	public int getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(int loanStatus) {
		this.loanStatus = loanStatus;
	}
	
}
