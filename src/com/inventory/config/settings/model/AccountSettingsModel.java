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
 *         Jul 31, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_ACCOUNT_SETTINGS)
public class AccountSettingsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8023344094106288683L;

	public AccountSettingsModel() {
		super();
	}

	public AccountSettingsModel(String settings_name, String value) {
		super();
		this.settings_name = settings_name;
		this.value = value;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "settings_name")
	private String settings_name;

	@Column(name = "value")
	private String value;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "type", columnDefinition="integer default 1",nullable=false)
	private long type;

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

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

}
