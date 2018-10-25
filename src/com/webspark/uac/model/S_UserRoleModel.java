package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_USER_ROLE)
public class S_UserRoleModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1946894966395826608L;

	public S_UserRoleModel() {
		super();
	}

	public S_UserRoleModel(long id) {
		super();
		this.id = id;
	}

	public S_UserRoleModel(long id, String role_name) {
		super();
		this.id = id;
		this.role_name = role_name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "role_name", length = 40)
	private String role_name;

	@Column(name = "active")
	private char active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

}
