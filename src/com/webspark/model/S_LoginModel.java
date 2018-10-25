package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_LOGIN)
public class S_LoginModel implements Serializable {


	public S_LoginModel() {
		super();
	}

	public S_LoginModel(long id) {
		super();
		this.id = id;
	}
	

	public S_LoginModel(String password) {
		super();
		this.password = password;
	}


	public S_LoginModel(long id, String login_name) {
		super();
		this.id = id;
		this.login_name = login_name;
	}


	@Id
	@GeneratedValue
	@Column(name = "login_id")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "office")
	private S_OfficeModel office;

	@Column(name = "login_name", length = 50)
	private String login_name;

	@Column(name = "password")
	private String password;
	
	@Column(name = "status", columnDefinition = "int default 0", nullable = false)
	private int status;

	@OneToOne
	@JoinColumn(name = "user_type")
	private S_UserRoleModel userType;


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_UserRoleModel getUserType() {
		return userType;
	}

	public void setUserType(S_UserRoleModel userType) {
		this.userType = userType;
	}

}
