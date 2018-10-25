package com.webspark.mailclient.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.MAILS)
public class MyMailsModel implements Serializable {

	private static final long serialVersionUID = -9173353885245013342L;

	public MyMailsModel(long user_id, long config_id, long folder_id,
			long mail_number, String emails, String subject,
			Timestamp date_time, boolean attachment_avail, boolean unreaded) {
		super();
		this.user_id = user_id;
		this.config_id = config_id;
		this.folder_id = folder_id;
		this.mail_number = mail_number;
		this.emails = emails;
		this.subject = subject;
		this.date_time = date_time;
		this.attachment_avail = attachment_avail;
		this.unreaded = unreaded;
	}

	public MyMailsModel() {
		super();
	}

	public MyMailsModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "user_id")
	private long user_id;

	@Column(name = "config_id")
	private long config_id;

	@Column(name = "folder_id")
	private long folder_id;

	@Column(name = "mail_number")
	private long mail_number;

	@Column(name = "emails", length = 1000)
	private String emails;

	@Column(name = "subject", length = 300)
	private String subject;

	@Transient
	private String details;

	@Column(name = "date_time")
	private Timestamp date_time;

	@Column(name = "attachment_avail")
	private boolean attachment_avail;

	@Column(name = "unreaded")
	private boolean unreaded;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getConfig_id() {
		return config_id;
	}

	public void setConfig_id(long config_id) {
		this.config_id = config_id;
	}

	public long getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(long folder_id) {
		this.folder_id = folder_id;
	}

	public long getMail_number() {
		return mail_number;
	}

	public void setMail_number(long mail_number) {
		this.mail_number = mail_number;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Timestamp getDate_time() {
		return date_time;
	}

	public void setDate_time(Timestamp date_time) {
		this.date_time = date_time;
	}

	public boolean isAttachment_avail() {
		return attachment_avail;
	}

	public void setAttachment_avail(boolean attachment_avail) {
		this.attachment_avail = attachment_avail;
	}

	public boolean isUnreaded() {
		return unreaded;
	}

	public void setUnreaded(boolean unreaded) {
		this.unreaded = unreaded;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
