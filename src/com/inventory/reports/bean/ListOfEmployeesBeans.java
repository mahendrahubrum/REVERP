package com.inventory.reports.bean;

public class ListOfEmployeesBeans {
	private String name;
	private String code;
	private String address;
	private String designation;
	private String department;
	private String gender;
	private String maritalStatus;
	private String dob;
	private String joiningDate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}
	public ListOfEmployeesBeans(String name, String code, String address,
			String designation, String department, String gender,
			String maritalStatus, String dob, String joiningDate) {
		super();
		this.name = name;
		this.code = code;
		this.address = address;
		this.designation = designation;
		this.department = department;
		this.gender = gender;
		this.maritalStatus = maritalStatus;
		this.dob = dob;
		this.joiningDate = joiningDate;
	}
	
	
}
