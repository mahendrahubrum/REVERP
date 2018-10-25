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
 *         Dec 20, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_OFFICE_ALLOCATION)
public class OfficeAllocationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3897781368301785820L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "office_id")
	private long office_id;

	public OfficeAllocationModel() {
		super();
	}

	public OfficeAllocationModel(long id) {
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

}
