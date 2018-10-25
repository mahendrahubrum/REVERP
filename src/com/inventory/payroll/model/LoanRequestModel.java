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
/**
 * 
 * @author Muhammed Shah
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.LOAN_REQUEST)
public class LoanRequestModel implements Serializable{
	@Id
	@GeneratedValue
	@Column(name = "id",nullable = false)
	public long id;
	
	@Column(name = "request_no",nullable = false,length = 10)
	public String requestNo;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	public UserModel user;
	
	@Column(name = "request_date",nullable = false)
	public Date requestDate;
	
	@Column(name = "amount",nullable = false)
	public double amount;
	
	@Column(name = "no_of_installment",nullable = false)
	public double noOfInstallment;
	
	@Column(name = "status",nullable = false)
	public short status;
	
	@OneToOne
	@JoinColumn(name = "currency_id")
	public CurrencyModel currency;
	
	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	public LoanRequestModel() {
		super();		
	}
	public LoanRequestModel(long id) {
		super();
		this.id = id;
	}
	public LoanRequestModel(long id,String requestNo) {
		super();
		this.id = id;
		this.requestNo = requestNo;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public UserModel getUser() {
		return user;
	}
	public void setUser(UserModel user) {
		this.user = user;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	
}
