package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 * 
 * @Date 13 Feb 2014
 */

@Entity
@Table(name = SConstants.tb_names.S_PROJECT_TYPES)
public class S_ProjectTypeModel implements Serializable {

	private static final long serialVersionUID = -3075986268313427902L;

	public S_ProjectTypeModel() {
		super();
	}

	public S_ProjectTypeModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 40)
	private String name;

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

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

}
