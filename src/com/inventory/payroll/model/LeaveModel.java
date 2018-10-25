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
 * @author sangeeth
 * @date 12-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_LEAVE)
public class LeaveModel implements Serializable {

	public LeaveModel(long id) {
		super();
		this.id = id;
	}
	
	public LeaveModel() {
		super();
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name="user")
	private UserModel user;
	
	@OneToOne
	@JoinColumn(name="leave_type")
	private LeaveTypeModel leave_type;
	
	@Column(name = "reason", length = 300)
	private String reason;

	@Column(name = "from_date")
	private Date from_date;
	
	@Column(name = "to_date")
	private Date to_date;
	
	@Column(name = "no_of_days")
	private double no_of_days;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "full_half")
	private long full_half;
	
	@Column(name = "first_second")
	private long first_second;
	
	@Column(name = "applied_to")
	private long appliedToLogin;
	
	@Column(name = "applied_by")
	private long appliedByLogin;
	
	@Column(name = "loss_of_pay", columnDefinition="boolean default false", nullable=false)
	private boolean lossOfPay;
	
	@Column(name = "days_year", length = 500, columnDefinition="varchar(500) default ''", nullable=false)
	private String daysInYear;
	
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public Date getTo_date() {
		return to_date;
	}

	public void setTo_date(Date to_date) {
		this.to_date = to_date;
	}

	public double getNo_of_days() {
		return no_of_days;
	}

	public void setNo_of_days(double no_of_days) {
		this.no_of_days = no_of_days;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getFull_half() {
		return full_half;
	}

	public void setFull_half(long full_half) {
		this.full_half = full_half;
	}

	public long getFirst_second() {
		return first_second;
	}

	public void setFirst_second(long first_second) {
		this.first_second = first_second;
	}

	public long getAppliedToLogin() {
		return appliedToLogin;
	}

	public void setAppliedToLogin(long appliedToLogin) {
		this.appliedToLogin = appliedToLogin;
	}

	public long getAppliedByLogin() {
		return appliedByLogin;
	}

	public void setAppliedByLogin(long appliedByLogin) {
		this.appliedByLogin = appliedByLogin;
	}

	public boolean isLossOfPay() {
		return lossOfPay;
	}

	public void setLossOfPay(boolean lossOfPay) {
		this.lossOfPay = lossOfPay;
	}

	public String getDaysInYear() {
		return daysInYear;
	}

	public void setDaysInYear(String daysInYear) {
		this.daysInYear = daysInYear;
	}
	
}
