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
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.LOAN_APPROVAL)
public class LoanApprovalModel implements Serializable{
	
	@Id
	@GeneratedValue
	@Column(name = "id",nullable = false)
	private long id;
	
	@OneToOne
	@JoinColumn(name = "request_id",referencedColumnName="id")
	private LoanRequestModel loanRequest;
	
	@Column(name = "loan_amount")
	private double loanAmount;

	@OneToOne
	@JoinColumn(name = "currency_id")
	public CurrencyModel currency;
	
	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "no_of_installment")
	private double noOfInstallment;
	
	@Column(name = "monthly_charge")
	private double monthlycharge;
	
	@Column(name = "approved_or_rejected_date")
	private Date approvedOrRejectedDate;
	
	@Column(name = "payment_start_date")
	private Date paymentStartDate;
	
	@Column(name = "payment_end_date")
	private Date paymentEndDate;
	
	
	@Column(name = "status",nullable = false)
	public short status;
	
	@Column(name = "transaction_id", columnDefinition ="bigint default 0", nullable=false)
	private long transactionId;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private UserModel approvedOrRejectedBy;
	
	public LoanApprovalModel(){
		super();
	}
	public LoanApprovalModel(long id) {
		super();
		setId(id);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public LoanRequestModel getLoanRequest() {
		return loanRequest;
	}
	public void setLoanRequest(LoanRequestModel loanRequest) {
		this.loanRequest = loanRequest;
	}
	public double getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(double loanAmount) {
		this.loanAmount = loanAmount;
	}
	public double getMonthlycharge() {
		return monthlycharge;
	}
	public void setMonthlycharge(double monthlycharge) {
		this.monthlycharge = monthlycharge;
	}
	public Date getApprovedOrRejectedDate() {
		return approvedOrRejectedDate;
	}
	public void setApprovedOrRejectedDate(Date approvedOrRejectedDate) {
		this.approvedOrRejectedDate = approvedOrRejectedDate;
	}
	public Date getPaymentStartDate() {
		return paymentStartDate;
	}
	public void setPaymentStartDate(Date paymentStartDate) {
		this.paymentStartDate = paymentStartDate;
	}
	public Date getPaymentEndDate() {
		return paymentEndDate;
	}
	public void setPaymentEndDate(Date paymentEndDate) {
		this.paymentEndDate = paymentEndDate;
	}
	public UserModel getApprovedOrRejectedBy() {
		return approvedOrRejectedBy;
	}
	public void setApprovedOrRejectedBy(UserModel approvedOrRejectedBy) {
		this.approvedOrRejectedBy = approvedOrRejectedBy;
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
	public double getNoOfInstallment() {
		return noOfInstallment;
	}
	public void setNoOfInstallment(double noOfInstallment) {
		this.noOfInstallment = noOfInstallment;
	}
	public short getStatus() {
		return status;
	}
	public void setStatus(short status) {
		this.status = status;
	}
	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	
}
