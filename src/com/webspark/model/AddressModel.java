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
import com.webspark.uac.model.CountryModel;

/**
 * @Author Anil K P
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.ADDRESS)
public class AddressModel implements Serializable {

	public AddressModel() {
		super();
	}

	public AddressModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "address_id")
	private long id;

	@OneToOne
	@JoinColumn(name = "country_id")
	private CountryModel country;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "mobile", length = 15)
	private String mobile;

	@Column(name = "email", length = 250)
	private String email;

	@Column(name = "address_area", length = 5000, columnDefinition="varchar(5000) default '' ")
	private String address_area;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CountryModel getCountry() {
		return country;
	}

	public void setCountry(CountryModel country) {
		this.country = country;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress_area() {
		return address_area;
	}

	public void setAddress_area(String address_area) {
		this.address_area = address_area;
	}

}
