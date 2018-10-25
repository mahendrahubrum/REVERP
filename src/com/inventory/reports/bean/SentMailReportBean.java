package com.inventory.reports.bean;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 2, 2014
 */
public class SentMailReportBean {
	String date;
	String contact;
	String subject;
	String content;
	
	public SentMailReportBean() {
	}

	public SentMailReportBean(String date, String contact, String subject,
			String content) {
		super();
		this.date = date;
		this.contact = contact;
		this.subject = subject;
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
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
	
}
