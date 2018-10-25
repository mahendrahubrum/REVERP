package com.inventory.payroll.model;

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
 * @author sangeeth
 * @date 25-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALARY_DISBURSAL_DETAILS)
public class SalaryDisbursalDetailsModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "component_id")
	private PayrollComponentModel component;
	
	@Column(name = "amount")
	private double amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PayrollComponentModel getComponent() {
		return component;
	}

	public void setComponent(PayrollComponentModel component) {
		this.component = component;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
