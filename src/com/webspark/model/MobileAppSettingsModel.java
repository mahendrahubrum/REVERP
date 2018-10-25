package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Anil K P
 * 
 */

@Entity
@Table(name = SConstants.tb_names.I_MOBILE_APP_SETTINGS)
public class MobileAppSettingsModel implements Serializable {


	private static final long serialVersionUID = -4486003514815015111L;

	public MobileAppSettingsModel() {
		super();
	}

	public MobileAppSettingsModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "settings_name")
	private String settings_name;

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
