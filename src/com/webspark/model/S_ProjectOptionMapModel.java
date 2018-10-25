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
@Table(name = SConstants.tb_names.S_PROJECT_OPTION_MAP)
public class S_ProjectOptionMapModel implements Serializable {

	private static final long serialVersionUID = 1881892667893731091L;

	public S_ProjectOptionMapModel(long id) {
		super();
		this.id = id;
	}

	public S_ProjectOptionMapModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "fk_option_id")
	private S_OptionModel option;

	@OneToOne
	@JoinColumn(name = "fk_project_type_id")
	private S_ProjectTypeModel project_type;

	@Column(name = "class_name", length = 100)
	private String class_name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OptionModel getOption() {
		return option;
	}

	public void setOption(S_OptionModel option) {
		this.option = option;
	}

	public S_ProjectTypeModel getProject_type() {
		return project_type;
	}

	public void setProject_type(S_ProjectTypeModel project_type) {
		this.project_type = project_type;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

}
