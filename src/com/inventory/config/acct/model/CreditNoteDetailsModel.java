/**
 * 
 */
package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

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
@Table(name = SConstants.tb_names.I_CREDIT_NOTE_DETAILS)
public class CreditNoteDetailsModel implements Serializable{
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "supplier_customer", columnDefinition = "int default 1", nullable = false)
	private int supplier_customer;
	
	@OneToOne
	@JoinColumn(name = "account")
	private LedgerModel account;
	
	@Column(name = "bill_no" ,columnDefinition ="bigint default 0", nullable=false)
	private long bill_no;

	@Column(name = "amount" ,columnDefinition ="double default 0", nullable=false)
	private double amount;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long departmentId;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long divisionId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LedgerModel getAccount() {
		return account;
	}

	public void setAccount(LedgerModel account) {
		this.account = account;
	}

	public long getBill_no() {
		return bill_no;
	}

	public void setBill_no(long bill_no) {
		this.bill_no = bill_no;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
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

	public int getSupplier_customer() {
		return supplier_customer;
	}

	public void setSupplier_customer(int supplier_customer) {
		this.supplier_customer = supplier_customer;
	}
	
}
