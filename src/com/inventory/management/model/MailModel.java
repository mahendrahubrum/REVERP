package com.inventory.management.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 30, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_MAIL)
public class MailModel implements Serializable {

	private static final long serialVersionUID = -4092309905682533003L;

	public MailModel(long id) {
		super();
		this.id = id;
	}

	public MailModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "contact_id")
	private long contact_id;

	@Column(name = "date")
	private Timestamp date;

	@Column(name = "subject", length = 600)
	private String subject;
	
	@Column(name = "content", length = 5000)
	private String content;
	
	@Column(name = "status")
	private long status;

	//login id
	@Column(name = "send_by")
	private long send_by;
	
	@Column(name = "attachment", length = 500)
	private String attachment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getSend_by() {
		return send_by;
	}

	public void setSend_by(long send_by) {
		this.send_by = send_by;
	}

	public long getContact_id() {
		return contact_id;
	}

	public void setContact_id(long contact_id) {
		this.contact_id = contact_id;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
}
