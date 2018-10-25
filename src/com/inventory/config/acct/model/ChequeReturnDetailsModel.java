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
 * @date 26-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_CHEQUE_RETURN_DETAILS)
public class ChequeReturnDetailsModel implements Serializable{

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "type", columnDefinition = "int default 1", nullable = false)
	private int type;
	
	@Column(name = "bill_no" ,columnDefinition ="bigint default 0", nullable=false)
	private long bill_no;
	
	@Column(name = "bill" ,columnDefinition ="varchar(500) default ''", nullable=false)
	private String bill;
	
	@Column(name = "pay_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long pay_id;
	
	@Column(name = "pay_child_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long pay_child_id;
	
	@Column(name = "returned" ,columnDefinition ="boolean default true", nullable=false)
	private boolean returned;

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
	
	@Column(name = "cheque_date")
	private Date chequeDate;
	
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

	public long getBill_no() {
		return bill_no;
	}

	public void setBill_no(long bill_no) {
		this.bill_no = bill_no;
	}

	public long getPay_id() {
		return pay_id;
	}

	public void setPay_id(long pay_id) {
		this.pay_id = pay_id;
	}

	public long getPay_child_id() {
		return pay_child_id;
	}

	public void setPay_child_id(long pay_child_id) {
		this.pay_child_id = pay_child_id;
	}

	public boolean isReturned() {
		return returned;
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
	}

	public String getBill() {
		return bill;
	}

	public void setBill(String bill) {
		this.bill = bill;
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

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	
}
