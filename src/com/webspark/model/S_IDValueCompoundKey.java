package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @Author Jinshad P.T.
 */

public class S_IDValueCompoundKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 603696467564351918L;

	@OneToOne
	@JoinColumn(name="mast_id")
	private S_IDGeneratorSettingsModel mast_id;

	@Column(name = "login_id" , nullable=true)
	private long login_id;

	@Column(name = "office_id", nullable=true)
	private long office_id;

	@Column(name = "organization_id", nullable=true)
	private long organization_id;

	public S_IDGeneratorSettingsModel getMast_id() {
		return mast_id;
	}

	public void setMast_id(S_IDGeneratorSettingsModel mast_id) {
		this.mast_id = mast_id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(long organization_id) {
		this.organization_id = organization_id;
	}

}
