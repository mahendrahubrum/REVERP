package com.webspark.mailclient.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.EMAIL_CONFIGURATION)
public class EmailConfigurationModel implements Serializable {

	private static final long serialVersionUID = 3942987551621541333L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 40)
	private String name;

	@Column(name = "host_name", length = 40)
	private String host_name;

	@Column(name = "user_id")
	private long user_id;

	@Column(name = "username", length = 50)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "max_no_emails")
	private int max_no_emails;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMax_no_emails() {
		return max_no_emails;
	}

	public void setMax_no_emails(int max_no_emails) {
		this.max_no_emails = max_no_emails;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
