package com.inventory.process.model;

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

/**
 * @author Jinshad P.T.
 * 
 *         Sep 25, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_END_PROCESS)
public class EndProcessModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1842094990783480761L;
	
	public EndProcessModel() {
		super();
	}

	public EndProcessModel(long id) {
		super();
		this.id = id;
	}

	public EndProcessModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "process_name", length = 100)
	private String name;

	@Column(name = "class_name", length = 150)
	private String class_name;

	@Column(name = "type", length = 15)
	private String type;

	@Column(name = "status")
	private long status;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}
	
	

}
