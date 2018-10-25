package com.inventory.config.stock.model;

/**
 * @author anil
 * @date 13-Jun-2016
 * @Project REVERP
 */
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


@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_REASON)
public class ReasonModel implements Serializable{

	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "office")
	private S_OfficeModel office;

	@Column(name = "name", length = 100)
	private String name;

	public ReasonModel() {
		super();
	}

	public ReasonModel(long id) {
		super();
		this.id = id;
	}

	public ReasonModel(long id, String name) {
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
}
