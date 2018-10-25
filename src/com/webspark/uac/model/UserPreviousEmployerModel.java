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

/**
 * 
 * @author sangeeth
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.USER_PREVIOUS_EMPLOYER)
public class UserPreviousEmployerModel implements Serializable {

	public UserPreviousEmployerModel() {
	
	}
	
	public UserPreviousEmployerModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "user")
	private UserModel user;
	
	@Column(name = "name")
	private String name;

	@Column(name = "place")
	private String place;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "officeId")
	private long officeId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	
}
