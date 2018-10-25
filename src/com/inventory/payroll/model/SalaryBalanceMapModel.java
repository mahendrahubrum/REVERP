package com.inventory.payroll.model;

import java.io.Serializable;
import java.sql.Date;

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
 * Feb 25, 2015
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_SALARY_BALANCE_MAP)
public class SalaryBalanceMapModel implements Serializable{
	
	public SalaryBalanceMapModel() {

	}
	
	public SalaryBalanceMapModel(long id) {
		super();
		this.id = id;
	}


	public SalaryBalanceMapModel(long employee, long office_id, Date month,
			double balance, long salary_id) {
		super();
		this.employee = employee;
		this.office_id = office_id;
		this.month = month;
		this.balance = balance;
		this.salary_id = salary_id;
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="employee")
	private long employee;
	
	@Column(name="office_id")
	private long office_id;
	
	@Column(name="month")
	private Date month;
	
	@Column(name="balance")
	private double balance;
	
	@Column(name="salary_id")
	private long salary_id;
	
	@Column(name="used_id", columnDefinition="bigint default 0", nullable=false)
	private long used_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getEmployee() {
		return employee;
	}

	public void setEmployee(long employee) {
		this.employee = employee;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getSalary_id() {
		return salary_id;
	}

	public void setSalary_id(long salary_id) {
		this.salary_id = salary_id;
	}

	public long getUsed_id() {
		return used_id;
	}

	public void setUsed_id(long used_id) {
		this.used_id = used_id;
	}
	
}
