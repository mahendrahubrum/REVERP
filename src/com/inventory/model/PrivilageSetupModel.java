package com.inventory.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 12, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_PRIVILAGE_SETUP)
public class PrivilageSetupModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5582036661047990104L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "option_id")
	private int option_id;

	public PrivilageSetupModel() {
		super();
	}

	public PrivilageSetupModel(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public int getOption_id() {
		return option_id;
	}

	public void setOption_id(int option_id) {
		this.option_id = option_id;
	}

}
