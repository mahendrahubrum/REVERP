package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 29, 2015
 */

@Entity
@Table(name = SConstants.tb_names.S_DEPARTMENT_OPTION_MAPPING)
public class S_DepartmentOptionMappingModel implements Serializable {

	private static final long serialVersionUID = 1346860189678421319L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "department_id")
	private DepartmentModel departmentId;

	@OneToOne
	@JoinColumn(name = "option_id")
	private S_OptionModel option_id;

	public S_DepartmentOptionMappingModel() {
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

	public DepartmentModel getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(DepartmentModel departmentId) {
		this.departmentId = departmentId;
	}


}
