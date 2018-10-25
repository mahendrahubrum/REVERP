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
import com.webspark.model.S_LoginModel;


/**
 * @author sangeeth
 * @date 12-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_LEAVE_HISTORY)
public class LeaveHistoryModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "comments", length = 300)
	private String comments;

	@Column(name = "date")
	private Date date;
	
	@Column(name = "status")
	private int status;
	
	@OneToOne
	@JoinColumn(name="login")
	private S_LoginModel login;
	
	@Column(name="leave_id")
	private long leave;

	public LeaveHistoryModel(long id) {
		super();
		this.id = id;
	}

	public LeaveHistoryModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public long getLeave() {
		return leave;
	}

	public void setLeave(long leave) {
		this.leave = leave;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}
	
}
