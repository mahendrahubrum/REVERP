package com.inventory.payroll.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author sangeeth
 * @date 17-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_ATTENDANCE)
public class AttendanceModel implements Serializable {

	public AttendanceModel() {
		
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="userId")
	private long userId;
	
	@Column(name="date")
	private Date date;
	
	@Column(name="first_in")
	private Timestamp first_in;
	
	@Column(name="first_out")
	private Timestamp first_out;
	
	@Column(name="second_in")
	private Timestamp second_in;
	
	@Column(name="second_out")
	private Timestamp second_out;
	
	@Column(name="present_leave", columnDefinition="int default 1", nullable=false)
	private int presentLeave;
	
	@Column(name="session_leave", columnDefinition="int default 1", nullable=false)
	private int sessionLeave;
	
	@Column(name="working_day", columnDefinition="boolean default true", nullable=false)
	private boolean workingDay;
	
	@Column(name="officeId")
	private long officeId;
	
	@Column(name="overtime")
	private long overtime;
	
	@Column(name="over_time_in")
	private Timestamp over_time_in;
	
	@Column(name="over_time_out")
	private Timestamp over_time_out;
	
	@Column(name="leave_id", columnDefinition="bigint default 0", nullable=false)
	private long leaveId;
	
	@Column(name="blocked", columnDefinition="boolean default false", nullable=false)
	private boolean blocked;

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

	public Timestamp getFirst_in() {
		return first_in;
	}

	public void setFirst_in(Timestamp first_in) {
		this.first_in = first_in;
	}

	public Timestamp getFirst_out() {
		return first_out;
	}

	public void setFirst_out(Timestamp first_out) {
		this.first_out = first_out;
	}

	public Timestamp getSecond_in() {
		return second_in;
	}

	public void setSecond_in(Timestamp second_in) {
		this.second_in = second_in;
	}

	public Timestamp getSecond_out() {
		return second_out;
	}

	public void setSecond_out(Timestamp second_out) {
		this.second_out = second_out;
	}

	public int getPresentLeave() {
		return presentLeave;
	}

	public void setPresentLeave(int presentLeave) {
		this.presentLeave = presentLeave;
	}

	public int getSessionLeave() {
		return sessionLeave;
	}

	public void setSessionLeave(int sessionLeave) {
		this.sessionLeave = sessionLeave;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public long getOvertime() {
		return overtime;
	}

	public void setOvertime(long overtime) {
		this.overtime = overtime;
	}

	public boolean isWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(boolean workingDay) {
		this.workingDay = workingDay;
	}

	public long getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(long leaveId) {
		this.leaveId = leaveId;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public Timestamp getOver_time_in() {
		return over_time_in;
	}

	public void setOver_time_in(Timestamp over_time_in) {
		this.over_time_in = over_time_in;
	}

	public Timestamp getOver_time_out() {
		return over_time_out;
	}

	public void setOver_time_out(Timestamp over_time_out) {
		this.over_time_out = over_time_out;
	}
	
}
