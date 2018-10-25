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
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 3, 2013
 */
/**
 * @author sangeeth
 * Hotel
 * 28-Jan-2016
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PAYROLL_EMPLOYEE_MAP)
public class PayrollEmployeeMapModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "employee_id")
	private UserModel employee;

	@OneToOne
	@JoinColumn(name = "component_id")
	private PayrollComponentModel component;

	@Column(name = "value")
	private double value;
	
	@Column(name = "type_value",columnDefinition ="double default 0", nullable=false)
	private double typeValue;

	@Column(name = "type")
	private long type;

	@Column(name = "date")
	private Date date;

	public PayrollEmployeeMapModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserModel getEmployee() {
		return employee;
	}

	public void setEmployee(UserModel employee) {
		this.employee = employee;
	}

	public PayrollComponentModel getComponent() {
		return component;
	}

	public void setComponent(PayrollComponentModel component) {
		this.component = component;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(double typeValue) {
		this.typeValue = typeValue;
	}
	
}
