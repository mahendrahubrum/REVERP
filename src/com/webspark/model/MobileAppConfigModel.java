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
@Table(name = SConstants.tb_names.I_MOBILE_APP_CONFIG)
public class MobileAppConfigModel implements Serializable {


	private static final long serialVersionUID = -6207963458637857644L;

	public MobileAppConfigModel() {
		super();
	}

	public MobileAppConfigModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "settings_name")
	private String settings_name;

	@Column(name = "option_id")
	private long option_id;

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

	public long getOption_id() {
		return option_id;
	}

	public void setOption_id(long option_id) {
		this.option_id = option_id;
	}

}
