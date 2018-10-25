package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.S_SYSTEM_SETTINGS)
public class S_SystemSettingsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3551595263993980895L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "settings_name", length = 30)
	private String settings_name;

	@Column(name = "value", length = 30)
	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSettings_name() {
		return settings_name;
	}

	public void setSettings_name(String settings_name) {
		this.settings_name = settings_name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
