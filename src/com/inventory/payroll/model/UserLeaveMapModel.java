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

/**
 * @author sangeeth
 * @date 19-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_USER_LEAVE_MAP)
public class UserLeaveMapModel implements Serializable {

	public UserLeaveMapModel() {
		
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="userId")
	private long userId;
	
	@Column(name="date")
	private Date date;
	
	@OneToOne
	@JoinColumn(name="leave_type")
	private LeaveTypeModel leave_type;
	
	@Column(name="office_id")
	private long officeId;
	
	@Column(name = "loss_of_pay", columnDefinition="boolean default false", nullable=false)
	private boolean lossOfPay;

	@Column(name = "no_days", columnDefinition="double default 0", nullable=false)
	private double noOfDays;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public LeaveTypeModel getLeave_type() {
		return leave_type;
	}

	public void setLeave_type(LeaveTypeModel leave_type) {
		this.leave_type = leave_type;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public boolean isLossOfPay() {
		return lossOfPay;
	}

	public void setLossOfPay(boolean lossOfPay) {
		this.lossOfPay = lossOfPay;
	}

	public double getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(double noOfDays) {
		this.noOfDays = noOfDays;
	}
	
}
