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
import com.webspark.model.AddressModel;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_ORGANIZATION)
public class S_OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262875219444725691L;

	public S_OrganizationModel() {
		super();
	}

	public S_OrganizationModel(long id) {
		super();
		this.id = id;
	}

	public S_OrganizationModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "org_name", length = 100)
	private String name;

	@OneToOne
	@JoinColumn(name = "country_id")
	private CountryModel country;

	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;

	@Column(name = "admin_user_id")
	private long admin_user_id;

	@Column(name = "active")
	private char active;

	@Column(name = "project_type")
	private long project_type;
	
	@Column(name = "logo_name", columnDefinition="varchar(500) default '' ", nullable=false)
	private String logoName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CountryModel getCountry() {
		return country;
	}

	public void setCountry(CountryModel country) {
		this.country = country;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

	public long getAdmin_user_id() {
		return admin_user_id;
	}

	public void setAdmin_user_id(long admin_user_id) {
		this.admin_user_id = admin_user_id;
	}

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

	public long getProject_type() {
		return project_type;
	}

	public void setProject_type(long project_type) {
		this.project_type = project_type;
	}

	public String getLogoName() {
		return logoName;
	}

	public void setLogoName(String logoName) {
		this.logoName = logoName;
	}
	
}
