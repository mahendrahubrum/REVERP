package com.inventory.payroll.model;

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

@Entity
@Table(name = SConstants.tb_names.I_EMPLOYEE_STATUS)
public class EmployeeStatusModel {
	@Id
	@Column (name = "id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private UserModel user;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "status")
	private int status;
	
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
