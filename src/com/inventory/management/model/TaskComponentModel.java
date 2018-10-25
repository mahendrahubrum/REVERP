package com.inventory.management.model;

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
 *         Dec 30, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TASK_COMPONENTS)
public class TaskComponentModel implements Serializable {

	private static final long serialVersionUID = 2256105389568453970L;

	public TaskComponentModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public TaskComponentModel(long id) {
		super();
		this.id = id;
	}

	public TaskComponentModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length=150)
	private String name;

	@Column(name = "organization_id")
	private long organization_id;

	@Column(name = "description", length = 600)
	private String description;

	@Column(name = "status")
	private long status;

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

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(long organization_id) {
		this.organization_id = organization_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

}
