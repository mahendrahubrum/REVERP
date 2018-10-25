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
 *         Sep 27, 2013
 */
@Entity
@Table(name = SConstants.tb_names.I_EMPLOYEE_WORKING_TIME)
public class EmployeeWorkingTimeModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7846110289451540636L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "employee")
	private UserModel employee;

	@Column(name = "working_time")
	private double working_time;

	@Column(name = "month")
	private Date month;

	public EmployeeWorkingTimeModel() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getWorking_time() {
		return working_time;
	}

	public void setWorking_time(double working_time) {
		this.working_time = working_time;
	}

	public UserModel getEmployee() {
		return employee;
	}

	public void setEmployee(UserModel employee) {
		this.employee = employee;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}
}
