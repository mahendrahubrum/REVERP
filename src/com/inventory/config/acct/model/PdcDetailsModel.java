/**
 * 
 */
package com.inventory.config.acct.model;

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
 * @author Anil
 * @date 13-Aug-2015
 * @Project REVERP
 */

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PDC_DETAILS)
public class PdcDetailsModel implements Serializable{
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "type", columnDefinition = "int default 1", nullable = false)
	private int type;
	
	@OneToOne
	@JoinColumn(name = "account")
	private LedgerModel account;
	
	@Column(name = "buttonVisible", columnDefinition = "boolean default false", nullable = false)
	private boolean buttonVisible;
	
	@Column(name = "bill_no" ,columnDefinition ="varchar(500) default ''", nullable=false)
	private String bill_no;

	@Column(name = "amount" ,columnDefinition ="double default 0", nullable=false)
	private double amount;
	
	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long departmentId;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long divisionId;
	
	@Column(name = "cheque_no" ,columnDefinition ="varchar(500) default ''", nullable=false)
	private String chequeNo;
	
	@Column(name = "bank_name" ,columnDefinition ="varchar(500) default ''", nullable=false)
	private String bankName;
	
	@Column(name = "issue_date")
	private Date issueDate;
	
	@Column(name = "from_date")
	private Date fromDate;
	
	@Column(name = "to_date")
	private Date toDate;
	
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "status" ,columnDefinition ="int default 1", nullable=false)
	private int status;
	
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

	public LedgerModel getAccount() {
		return account;
	}

	public void setAccount(LedgerModel account) {
		this.account = account;
	}

	public boolean isButtonVisible() {
		return buttonVisible;
	}

	public void setButtonVisible(boolean buttonVisible) {
		this.buttonVisible = buttonVisible;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	public long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(long divisionId) {
		this.divisionId = divisionId;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public CurrencyModel getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(CurrencyModel currencyId) {
		this.currencyId = currencyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
}
