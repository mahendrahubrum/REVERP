package com.webspark.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Anil K P
 */

@Entity
@Table(name = SConstants.tb_names.ACTIVITY_LOG)
public class ActivityLogModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7378951632368007526L;

	public ActivityLogModel() {
		super();
	}

	public ActivityLogModel(long id) {
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

	@Column(name = "log", length = 1000)
	private String log;

	@Column(name = "date")
	private Timestamp date;

	@Column(name = "office_id", columnDefinition = "bigint default 0", nullable = false)
	private long office_id;
	
	@Column(name = "bill_id", columnDefinition = "bigint default 0", nullable = false)
	private long billId;

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

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
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
}
