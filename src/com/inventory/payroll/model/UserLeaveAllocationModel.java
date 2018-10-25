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
import com.webspark.uac.model.UserModel;


/**
 * @author sangeeth
 * @date 11-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_USER_LEAVE_ALLOCATION)
public class UserLeaveAllocationModel implements Serializable {

	public UserLeaveAllocationModel(long id) {
		super();
		this.id = id;
	}

	public UserLeaveAllocationModel() {
		super();
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "user")
	private UserModel user;

	@OneToOne
	@JoinColumn(name = "leave_type")
	private LeaveTypeModel leave_type;

	@Column(name = "leave_available")
	private double leave_available;
	
	@Column(name = "leave_taken")
	private double leave_taken;
	
	@Column(name = "carry_forward", columnDefinition="double default 0", nullable=false)
	private double carry_forward;
	
	@Column(name = "year")
	private long year;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public LeaveTypeModel getLeave_type() {
		return leave_type;
	}

	public void setLeave_type(LeaveTypeModel leave_type) {
		this.leave_type = leave_type;
	}

	public double getLeave_available() {
		return leave_available;
	}

	public void setLeave_available(double leave_available) {
		this.leave_available = leave_available;
	}

	public double getLeave_taken() {
		return leave_taken;
	}

	public void setLeave_taken(double leave_taken) {
		this.leave_taken = leave_taken;
	}

	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	public double getCarry_forward() {
		return carry_forward;
	}

	public void setCarry_forward(double carry_forward) {
		this.carry_forward = carry_forward;
	}
	
}
