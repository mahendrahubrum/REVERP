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
 * @date 13-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_LEAVE_DATE)
public class LeaveDateModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "user_id", columnDefinition="bigint default 0", nullable=false)
	private long userId;
	
	@OneToOne
	@JoinColumn(name="leave_id")
	private LeaveModel leave;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "days")
	private double days;
	
	@Column(name = "office", columnDefinition="bigint default 0", nullable=false)
	private long officeId;
	
	@Column(name = "attendance", columnDefinition="boolean default false", nullable=false)
	private boolean attendance;
	
	public LeaveDateModel(long id) {
		super();
		this.id = id;
	}

	public LeaveDateModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LeaveModel getLeave() {
		return leave;
	}

	public void setLeave(LeaveModel leave) {
		this.leave = leave;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getDays() {
		return days;
	}

	public void setDays(double days) {
		this.days = days;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public boolean isAttendance() {
		return attendance;
	}

	public void setAttendance(boolean attendance) {
		this.attendance = attendance;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
}
