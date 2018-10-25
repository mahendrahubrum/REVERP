package com.webspark.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.REVIEW)
public class ReviewModel implements Serializable {

	private static final long serialVersionUID = -7740892178724688054L;

	public ReviewModel() {
		super();
	}

	public ReviewModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "login_id")
	private long login;

	@Column(name = "option_id")
	private long option;

	@Column(name = "date")
	private Timestamp date;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "title",length=100)
	private String title;
	
	@Column(name = "file_name",length=50)
	private String fileName;
	
	@Column(name = "details",length=400)
	private String details;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLogin() {
		return login;
	}

	public void setLogin(long login) {
		this.login = login;
	}

	public long getOption() {
		return option;
	}

	public void setOption(long option) {
		this.option = option;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
