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

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_LOGIN_OPTION_MAPPING)
public class S_LoginOptionMappingModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3831087911507226453L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "option_id")
	private S_OptionModel option_id;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login_id;

	@Column(name = "active")
	private char active;

	public S_LoginOptionMappingModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OptionModel getOption_id() {
		return option_id;
	}

	public void setOption_id(S_OptionModel option_id) {
		this.option_id = option_id;
	}

	public S_LoginModel getLogin_id() {
		return login_id;
	}

	public void setLogin_id(S_LoginModel login_id) {
		this.login_id = login_id;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

}
