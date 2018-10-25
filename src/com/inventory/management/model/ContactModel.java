package com.inventory.management.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 30, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_CONTACTS)
public class ContactModel implements Serializable {

	private static final long serialVersionUID = 8041664033662092526L;

	public ContactModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public ContactModel(long id) {
		super();
		this.id = id;
	}

	public ContactModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "contact_person", length = 100)
	private String contact_person;

	@Column(name = "type")
	private int type;

	@OneToOne
	@JoinColumn(name = "fk_category_id")
	private ContactCategoryModel category;

	@Column(name = "date")
	private Date date;

	@Column(name = "mobile", length = 50)
	private String mobile;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "fax", length = 50)
	private String fax;

	@Column(name = "website", length = 50)
	private String website;

	@Column(name = "location", length = 80)
	private String location;

	@Column(name = "address", length = 500)
	private String address;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "fk_login_id")
	private S_LoginModel login;

	@Column(name = "added_by")
	private long added_by;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ContactCategoryModel getCategory() {
		return category;
	}

	public void setCategory(ContactCategoryModel category) {
		this.category = category;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public long getAdded_by() {
		return added_by;
	}

	public void setAdded_by(long added_by) {
		this.added_by = added_by;
	}
}
