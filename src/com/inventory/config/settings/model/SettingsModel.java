package com.inventory.config.settings.model;

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
 *         Jul 25, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_SETTINGS)
public class SettingsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -971721990697317957L;

	public SettingsModel() {
		super();
	}

	public SettingsModel(String settings_name, String value) {
		super();
		this.settings_name = settings_name;
		this.value = value;
	}

	public SettingsModel(long id, String settings_name, String value,
			int level, long level_id) {
		super();
		this.id = id;
		this.settings_name = settings_name;
		this.value = value;
		this.level = level;
		this.level_id = level_id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "settings_name")
	private String settings_name;

	@Column(name = "value")
	private String value;

	@Column(name = "level")
	private int level;

	@Column(name = "level_id")
	private long level_id;

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getLevel_id() {
		return level_id;
	}

	public void setLevel_id(long level_id) {
		this.level_id = level_id;
	}
}
