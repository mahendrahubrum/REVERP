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
 * @author sangeeth
 * @date 15-Oct-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PDC_PAYMENT_DETAILS)
public class PdcPaymentDetailsModel implements Serializable{
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "type", columnDefinition = "int default 1", nullable = false)
	private int type;
	
	@Column(name = "pdc_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long pdc_id;
	
	@Column(name = "pdc_child_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long pdc_child_id;
	
	@Column(name = "amount" ,columnDefinition ="double default 0", nullable=false)
	private double amount;
	
	@Column(name = "bill_no" ,columnDefinition ="varchar(500) default ''", nullable=false)
	private String bill_no;
	
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
	
	@Column(name = "issue_date")
	private Date issueDate;
	
	@Column(name = "cheque_date")
	private Date chequeDate;
	
	@Column(name = "from_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long from_id;
	
	@Column(name = "to_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long to_id;
	
	@Column(name = "status" ,columnDefinition ="int default 0", nullable=false)
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

	public long getPdc_id() {
		return pdc_id;
	}

	public void setPdc_id(long pdc_id) {
		this.pdc_id = pdc_id;
	}

	public long getPdc_child_id() {
		return pdc_child_id;
	}

	public void setPdc_child_id(long pdc_child_id) {
		this.pdc_child_id = pdc_child_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public CurrencyModel getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(CurrencyModel currencyId) {
		this.currencyId = currencyId;
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

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}

	public long getFrom_id() {
		return from_id;
	}

	public void setFrom_id(long from_id) {
		this.from_id = from_id;
	}

	public long getTo_id() {
		return to_id;
	}

	public void setTo_id(long to_id) {
		this.to_id = to_id;
	}
	
}
