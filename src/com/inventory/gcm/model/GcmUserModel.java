package com.inventory.gcm.model;

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
 *         Dec 29, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_GCM_USERS)
public class GcmUserModel implements Serializable {

	private static final long serialVersionUID = 5966854343430472234L;

	public GcmUserModel() {
		super();
	}

	public GcmUserModel(String reg_id, String deviceId) {
		super();
		this.reg_id = reg_id;
		this.deviceId = deviceId;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "reg_id", length = 200)
	private String reg_id;

	@Column(name = "device_id", length = 20)
	private String deviceId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReg_id() {
		return reg_id;
	}

	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
