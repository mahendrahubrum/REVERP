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
import com.webspark.model.S_LoginModel;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.USER)
public class UserModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private long id;
	
	@Column(name = "loginEnabled",columnDefinition="boolean default true", nullable=false)
	private boolean loginEnabled;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel loginId;
	
	@OneToOne
	@JoinColumn(name = "user_role")
	private S_UserRoleModel user_role;
	
	@OneToOne
	@JoinColumn(name = "office")
	private S_OfficeModel office;

	@Column(name = "employ_code", length = 50)
	private String employ_code;

	@Column(name = "first_name", length = 150)
	private String first_name;

	@Column(name = "middle_name", length = 150)
	private String middle_name;

	@Column(name = "last_name", length = 150)
	private String last_name;
	
	@Column(name = "gender")
	private char gender;

	@Column(name = "birth_date")
	private Date birth_date;

	@Column(name = "marital_status")
	private char marital_status;

	@OneToOne
	@JoinColumn(name = "department_id")
	private DepartmentModel department;

	@OneToOne
	@JoinColumn(name = "designation_id")
	private DesignationModel designation;

	@Column(name = "joining_date")
	private Date joining_date;

	@Column(name = "salary_type", length = 1)
	private int salary_type;

	@Column(name = "effective_date")
	private Date effective_date;

	@Column(name = "salary_effective_date")
	private Date salary_effective_date;

	@Column(name = "superior_id")
	private long superior_id;
	
	@Column(name = "joinStatus",columnDefinition="bigint default 1",nullable=false)
	private long joinStatus;
	
	@Column(name = "reJoinStatus",columnDefinition="bigint default 1",nullable=false)
	private long reJoinStatus;
	
	@Column(name = "visaType",columnDefinition="bigint default 0",nullable=false)
	private long visaType;

	@OneToOne
	@JoinColumn(name = "address_id")
	private AddressModel address;
	
	@OneToOne
	@JoinColumn(name = "work_address")
	private AddressModel work_address;
	
	@OneToOne
	@JoinColumn(name = "local_address")
	private AddressModel local_address;
	
	
	@Column(name = "job_title",columnDefinition="varchar(200) default '' ",nullable=false)
	private String job_title;
	
	@Column(name = "height",columnDefinition="double default 0",nullable=false)
	private double height;
	
	@Column(name = "weight",columnDefinition="double default 0",nullable=false)
	private double weight;
	
	@Column(name = "user_image",columnDefinition="varchar(1000) default '' ",nullable=false)
	private String user_image;
	
	@Column(name = "companyAccomodation",columnDefinition="boolean default false", nullable=false)
	private boolean companyAccomodation;
	
	@Column(name = "familyStatus",columnDefinition="boolean default true", nullable=false)
	private boolean familyStatus;
	
	@Column(name = "familyCountry",columnDefinition="boolean default true", nullable=false)
	private boolean familyCountry;
	
	@Column(name = "ticketStatus",columnDefinition="bigint default 1", nullable=false)
	private long ticketStatus;
	
	@Column(name = "familyTicket",columnDefinition="boolean default true", nullable=false)
	private boolean familyTicket;
	
	@Column(name = "visa_company",columnDefinition="varchar(200) default '' ",nullable=false)
	private String visa_company;
	
	@Column(name = "status",columnDefinition="int default 1",nullable=false)
	private int status;
	
	public UserModel(long id) {
		super();
		this.id = id;
	}

	public UserModel(long id, String first_name) {
		super();
		this.id = id;
		this.first_name = first_name;
	}

	public UserModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public S_LoginModel getLoginId() {
		return loginId;
	}

	public void setLoginId(S_LoginModel loginId) {
		this.loginId = loginId;
	}

	public String getFirst_name() {
		return first_name;
	}

	public long getJoinStatus() {
		return joinStatus;
	}

	public void setJoinStatus(long joinStatus) {
		this.joinStatus = joinStatus;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public Date getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(Date birth_date) {
		this.birth_date = birth_date;
	}

	public char getMarital_status() {
		return marital_status;
	}

	public void setMarital_status(char marital_status) {
		this.marital_status = marital_status;
	}

	public DepartmentModel getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentModel department) {
		this.department = department;
	}

	public DesignationModel getDesignation() {
		return designation;
	}

	public void setDesignation(DesignationModel designation) {
		this.designation = designation;
	}

	public Date getJoining_date() {
		return joining_date;
	}

	public void setJoining_date(Date joining_date) {
		this.joining_date = joining_date;
	}

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSalary_type() {
		return salary_type;
	}

	public void setSalary_type(int salary_type) {
		this.salary_type = salary_type;
	}

	public Date getEffective_date() {
		return effective_date;
	}

	public void setEffective_date(Date effective_date) {
		this.effective_date = effective_date;
	}

	public Date getSalary_effective_date() {
		return salary_effective_date;
	}

	public void setSalary_effective_date(Date salary_effective_date) {
		this.salary_effective_date = salary_effective_date;
	}

	public String getEmploy_code() {
		return employ_code;
	}

	public void setEmploy_code(String employ_code) {
		this.employ_code = employ_code;
	}

	public long getSuperior_id() {
		return superior_id;
	}

	public void setSuperior_id(long superior_id) {
		this.superior_id = superior_id;
	}

	public long getVisaType() {
		return visaType;
	}

	public void setVisaType(long visaType) {
		this.visaType = visaType;
	}

	public boolean isLoginEnabled() {
		return loginEnabled;
	}

	public void setLoginEnabled(boolean loginEnabled) {
		this.loginEnabled = loginEnabled;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public S_UserRoleModel getUser_role() {
		return user_role;
	}

	public void setUser_role(S_UserRoleModel user_role) {
		this.user_role = user_role;
	}

	public AddressModel getWork_address() {
		return work_address;
	}

	public void setWork_address(AddressModel work_address) {
		this.work_address = work_address;
	}

	public AddressModel getLocal_address() {
		return local_address;
	}

	public void setLocal_address(AddressModel local_address) {
		this.local_address = local_address;
	}

	public String getJob_title() {
		return job_title;
	}

	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getUser_image() {
		return user_image;
	}

	public void setUser_image(String user_image) {
		this.user_image = user_image;
	}

	public boolean isCompanyAccomodation() {
		return companyAccomodation;
	}

	public void setCompanyAccomodation(boolean companyAccomodation) {
		this.companyAccomodation = companyAccomodation;
	}

	public boolean isFamilyStatus() {
		return familyStatus;
	}

	public void setFamilyStatus(boolean familyStatus) {
		this.familyStatus = familyStatus;
	}

	public boolean isFamilyCountry() {
		return familyCountry;
	}

	public void setFamilyCountry(boolean familyCountry) {
		this.familyCountry = familyCountry;
	}

	public boolean isFamilyTicket() {
		return familyTicket;
	}

	public void setFamilyTicket(boolean familyTicket) {
		this.familyTicket = familyTicket;
	}

	public long getReJoinStatus() {
		return reJoinStatus;
	}

	public void setReJoinStatus(long reJoinStatus) {
		this.reJoinStatus = reJoinStatus;
	}

	public long getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(long ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getVisa_company() {
		return visa_company;
	}

	public void setVisa_company(String visa_company) {
		this.visa_company = visa_company;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
