package com.hotel.service.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.hotel.config.model.TableModel;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */

@Entity
@Table(name = SConstants.tb_names.H_CUSTOMER_BOOKING_MODEL)
public class CustomerBookingModel implements Serializable {

	private static final long serialVersionUID = -2434524118649280977L;

	public CustomerBookingModel() {
		super();
	}

	public CustomerBookingModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "customer_name")
	private String customer_name;
	
	@Column(name = "customer_phone")
	private String customer_phone;
	
	@Column(name = "customer_address")
	private String customer_address;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "employee")
	private long employee;
	
	@OneToOne
	@JoinColumn(name = "table_no")
	private TableModel tableNo;
	
	@Column(name = "adults")
	private int adults;
	
	@Column(name = "childs")
	private int childs;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getCustomer_phone() {
		return customer_phone;
	}

	public void setCustomer_phone(String customer_phone) {
		this.customer_phone = customer_phone;
	}

	public String getCustomer_address() {
		return customer_address;
	}

	public void setCustomer_address(String customer_address) {
		this.customer_address = customer_address;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getEmployee() {
		return employee;
	}

	public void setEmployee(long employee) {
		this.employee = employee;
	}

	public int getAdults() {
		return adults;
	}

	public void setAdults(int adults) {
		this.adults = adults;
	}

	public int getChilds() {
		return childs;
	}

	public void setChilds(int childs) {
		this.childs = childs;
	}

	public TableModel getTableNo() {
		return tableNo;
	}

	public void setTableNo(TableModel tableNo) {
		this.tableNo = tableNo;
	}


}
