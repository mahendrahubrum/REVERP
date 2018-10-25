package com.webspark.uac.model;

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

/**
 * 
 * @author sangeeth
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.USER_QUALIFICATION)
public class UserQualificationModel implements Serializable {

	public UserQualificationModel() {
	
	}
	
	public UserQualificationModel(long id) {
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
	
	@OneToOne
	@JoinColumn(name = "qualification")
	private QualificationModel qualification;

	@Column(name = "year")
	private Date year;
	
	@Column(name = "description")
	private String description;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

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

	public QualificationModel getQualification() {
		return qualification;
	}

	public void setQualification(QualificationModel qualification) {
		this.qualification = qualification;
	}

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}
	
	
}
