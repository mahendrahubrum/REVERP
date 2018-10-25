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
import com.webspark.model.AddressModel;
import com.webspark.model.CurrencyModel;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_OFFICE)
public class S_OfficeModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6291388745656948832L;

	public S_OfficeModel() {
		super();
	}

	public S_OfficeModel(long id) {
		super();
		this.id = id;
	}

	public S_OfficeModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "office_id")
	private long id;

	@Column(name = "office_name", length = 100)
	private String name;

	@OneToOne
	@JoinColumn(name = "organization")
	private S_OrganizationModel organization;

	@OneToOne
	@JoinColumn(name = "country")
	private CountryModel country;

	@OneToOne
	@JoinColumn(name = "currency_id")
	private CurrencyModel currency;

	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;

	@Column(name = "active")
	private char active;

	@Column(name = "fin_start_date")
	private Date fin_start_date;

	@Column(name = "fin_end_date")
	private Date fin_end_date;

	@Column(name = "working_date")
	private Date workingDate;

	@Column(name = "admin_user_id")
	private long admin_user_id;
	
	@Column(name = "language",columnDefinition="bigint default 1",nullable=false)
	private long language;
	
	@Column(name = "timezone", length = 200)
	private String timezone;	
	
	@Column(name = "header",columnDefinition="varchar(500) default ''",nullable=false)
    private String header;
    
    @Column(name = "footer",columnDefinition="varchar(500) default ''",nullable=false)
    private String footer;
	
    @Column(name = "holidays", length = 100,columnDefinition="varchar(100) default ''",nullable=false)
	private String holidays;
    
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public long getLanguage() {
		return language;
	}

	public void setLanguage(long language) {
		this.language = language;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OrganizationModel getOrganization() {
		return organization;
	}

	public void setOrganization(S_OrganizationModel organization) {
		this.organization = organization;
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

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

	public Date getWorkingDate() {
		return workingDate;
	}

	public void setWorkingDate(Date workingDate) {
		this.workingDate = workingDate;
	}

	public CurrencyModel getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyModel currency) {
		this.currency = currency;
	}

	public Date getFin_start_date() {
		return fin_start_date;
	}

	public void setFin_start_date(Date fin_start_date) {
		this.fin_start_date = fin_start_date;
	}

	public Date getFin_end_date() {
		return fin_end_date;
	}

	public void setFin_end_date(Date fin_end_date) {
		this.fin_end_date = fin_end_date;
	}

	public long getAdmin_user_id() {
		return admin_user_id;
	}

	public void setAdmin_user_id(long admin_user_id) {
		this.admin_user_id = admin_user_id;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getHolidays() {
		return holidays;
	}

	public void setHolidays(String holidays) {
		this.holidays = holidays;
	}
	
}
