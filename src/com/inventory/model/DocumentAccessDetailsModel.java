package com.inventory.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 30-Jul-2015
 */

@Entity
@Table(name = SConstants.tb_names.I_DOCUMENT_ACCESS_DETAILS)
public class DocumentAccessDetailsModel implements Serializable {

	private static final long serialVersionUID = 8925011782566283987L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name="user")
	private long user;
	
	@Column(name="download_access")
	private char download;
	
	@Column(name="delete_access")
	private char delete;
	
	@Column(name="view_access")
	private char view;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public char getDownload() {
		return download;
	}

	public void setDownload(char download) {
		this.download = download;
	}

	public char getDelete() {
		return delete;
	}

	public void setDelete(char delete) {
		this.delete = delete;
	}

	public char getView() {
		return view;
	}

	public void setView(char view) {
		this.view = view;
	}
}
