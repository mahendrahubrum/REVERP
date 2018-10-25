package com.webspark.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.REPORT_ISSUE)
public class ReportIssueModel implements Serializable {

	private static final long serialVersionUID = -4057149207096608044L;

	public ReportIssueModel() {
		super();
	}

	public ReportIssueModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "login_id")
	private long login;

	@Column(name = "option_id")
	private long option;

	@Column(name = "issue", length = 300)
	private String issue;

	@Column(name = "date")
	private Timestamp date;

	@Column(name = "office_id", columnDefinition = "bigint default 0", nullable = false)
	private long office_id;
	
	@Column(name = "to_user", columnDefinition = "bigint default 0", nullable = false)
	private long to_user;

	@Column(name = "bill_id", columnDefinition = "bigint default 0", nullable = false)
	private long billId;

	@Column(name = "status", columnDefinition = "int default 1", nullable = false)
	private long status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLogin() {
		return login;
	}

	public void setLogin(long login) {
		this.login = login;
	}

	public long getOption() {
		return option;
	}

	public void setOption(long option) {
		this.option = option;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getBillId() {
		return billId;
	}

	public void setBillId(long billId) {
		this.billId = billId;
	}

	public long getTo_user() {
		return to_user;
	}

	public void setTo_user(long to_user) {
		this.to_user = to_user;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}
}
