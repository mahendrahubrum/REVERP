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
 * @Author Jinshad P.T.
 * @Date Jan 6 2013
 */

@Entity
@Table(name = SConstants.tb_names.S_LOGIN_HISTORY)
public class S_LoginHistoryModel implements Serializable {

	private static final long serialVersionUID = 1177626947294637998L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "loginID")
	private long login_id;

	@Column(name = "logged_in_time")
	private Timestamp logged_in_time;

	@Column(name = "logged_out_time")
	private Timestamp logged_out_time;

	@Column(name = "active")
	private char active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public Timestamp getLogged_in_time() {
		return logged_in_time;
	}

	public void setLogged_in_time(Timestamp logged_in_time) {
		this.logged_in_time = logged_in_time;
	}

	public Timestamp getLogged_out_time() {
		return logged_out_time;
	}

	public void setLogged_out_time(Timestamp logged_out_time) {
		this.logged_out_time = logged_out_time;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

}
