package com.inventory.rent.model;

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


@Entity
@Table(name = SConstants.tb_names.RENT_TYPE)
public class RentTypeModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4920420477438797134L;
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "rent_type_name", length = 100)
	private String name;

	@Column(name = "status")
	private long status;

	public RentTypeModel() {
		super();
	}

	public RentTypeModel(long id) {
		super();
		this.id = id;
	}

	public RentTypeModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}
}
