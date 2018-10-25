package com.webspark.mailclient.bean;

import java.util.Date;

public class EmailDetailsBean {

	String subject, details, from;
	long id, message_no;
	Date date;

	public EmailDetailsBean(String subject, String details) {
		super();
		this.subject = subject;
		this.details = details;
	}

	public EmailDetailsBean(String from, String subject, String details) {
		super();
		this.subject = subject;
		this.details = details;
		this.from = from;
	}

	public EmailDetailsBean(long message_no, String from, String subject, String details,
			Date date) {
		super();
		this.subject = subject;
		this.details = details;
		this.from = from;
		this.date = date;
		this.message_no=message_no;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getMessage_no() {
		return message_no;
	}

	public void setMessage_no(long message_no) {
		this.message_no = message_no;
	}

}
