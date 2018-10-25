package com.inventory.survey.model;

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
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date 20 Feb 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_SURVEY)
public class SurveyModel implements Serializable {

	private static final long serialVersionUID = -6024811483396092407L;

	public SurveyModel() {
		super();
	}

	public SurveyModel(long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "email", length = 50)
	private String email;

	@Column(name = "mobile", length = 20)
	private String mobile;

	@Column(name = "telephone", length = 20)
	private String telephone;

	@Column(name = "fax", length = 20)
	private String fax;

	@Column(name = "contact_person", length = 15)
	private String contact_person;

	@Column(name = "company", length = 15)
	private String company;

	@Column(name = "activity", length = 15)
	private String activity;

	@Column(name = "accounting_software", length = 15)
	private String accounting_software;

	@Column(name = "cheque_writer", length = 15)
	private String cheque_writer;

	@Column(name = "reminders", length = 15)
	private String reminders;

	@Column(name = "website", length = 15)
	private String website;

	@Column(name = "cctv", length = 15)
	private String cctv;

	@Column(name = "time_attendance", length = 15)
	private String time_attendance;

	@Column(name = "bulk_email", length = 15)
	private String bulk_email;

	@Column(name = "bulk_sms", length = 15)
	private String bulk_sms;

	@Column(name = "computer", length = 15)
	private String computer;

	@Column(name = "printer", length = 15)
	private String printer;

	@Column(name = "annual_maintanance_contact", length = 15)
	private String annual_maintanance_contact;

	@Column(name = "flyer", length = 30)
	private String flyer;

	@Column(name = "description", length = 400)
	private String description;

	@Column(name = "date")
	private Date date;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "fk_office")
	private S_OfficeModel office;

	@Column(name = "login_id")
	private long login_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getContact_person() {
		return contact_person;
	}

	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getAccounting_software() {
		return accounting_software;
	}

	public void setAccounting_software(String accounting_software) {
		this.accounting_software = accounting_software;
	}

	public String getCheque_writer() {
		return cheque_writer;
	}

	public void setCheque_writer(String cheque_writer) {
		this.cheque_writer = cheque_writer;
	}

	public String getReminders() {
		return reminders;
	}

	public void setReminders(String reminders) {
		this.reminders = reminders;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCctv() {
		return cctv;
	}

	public void setCctv(String cctv) {
		this.cctv = cctv;
	}

	public String getTime_attendance() {
		return time_attendance;
	}

	public void setTime_attendance(String time_attendance) {
		this.time_attendance = time_attendance;
	}

	public String getBulk_email() {
		return bulk_email;
	}

	public void setBulk_email(String bulk_email) {
		this.bulk_email = bulk_email;
	}

	public String getBulk_sms() {
		return bulk_sms;
	}

	public void setBulk_sms(String bulk_sms) {
		this.bulk_sms = bulk_sms;
	}

	public String getComputer() {
		return computer;
	}

	public void setComputer(String computer) {
		this.computer = computer;
	}

	public String getPrinter() {
		return printer;
	}

	public void setPrinter(String printer) {
		this.printer = printer;
	}

	public String getAnnual_maintanance_contact() {
		return annual_maintanance_contact;
	}

	public void setAnnual_maintanance_contact(String annual_maintanance_contact) {
		this.annual_maintanance_contact = annual_maintanance_contact;
	}

	public String getFlyer() {
		return flyer;
	}

	public void setFlyer(String flyer) {
		this.flyer = flyer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

}
