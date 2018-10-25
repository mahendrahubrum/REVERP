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
 */

@Entity
@Table(name = SConstants.tb_names.S_ID_GENERATOR_MASTER)
public class S_IDGeneratorSettingsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5484613754613627829L;

	public S_IDGeneratorSettingsModel() {
		super();
	}

	public S_IDGeneratorSettingsModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "id_name", length = 150)
	private String id_name;

	@Column(name = "scope")
	private int scope;

	@Column(name = "initial_value")
	private long initial_value;

	@Column(name = "reset_mode")
	private int reset_mode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getId_name() {
		return id_name;
	}

	public void setId_name(String id_name) {
		this.id_name = id_name;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public long getInitial_value() {
		return initial_value;
	}

	public void setInitial_value(long initial_value) {
		this.initial_value = initial_value;
	}

	public int getReset_mode() {
		return reset_mode;
	}

	public void setReset_mode(int reset_mode) {
		this.reset_mode = reset_mode;
	}

}
