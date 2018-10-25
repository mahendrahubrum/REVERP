package com.inventory.payroll.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 23, 2015
 */


@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALARY_COMMISSION_MAP)
public class SalaryCommissionMapModel implements Serializable {


	public SalaryCommissionMapModel() {
		super();
	}

	
	public SalaryCommissionMapModel(long id) {
		super();
		this.id = id;
	}

	
	public SalaryCommissionMapModel(long commission_id, long salary_id,
			double amount, int type) {
		super();
		this.commission_id = commission_id;
		this.salary_id = salary_id;
		this.amount = amount;
		this.type = type;
	}


	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "commission_id")
	private long commission_id;

	@Column(name = "salary_id")
	private long salary_id;
	
	@Column(name = "amount")
	private double amount;

	@Column(name = "type")
	private int type;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getCommission_id() {
		return commission_id;
	}


	public void setCommission_id(long commission_id) {
		this.commission_id = commission_id;
	}


	public long getSalary_id() {
		return salary_id;
	}


	public void setSalary_id(long salary_id) {
		this.salary_id = salary_id;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}

}
