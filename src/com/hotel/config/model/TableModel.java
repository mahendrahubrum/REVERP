package com.hotel.config.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 23-Sep-2015
 */

@Entity
@Table(name = SConstants.tb_names.H_TABLE)
public class TableModel implements Serializable {


	private static final long serialVersionUID = 1802596440969394122L;

	public TableModel() {
		super();
	}

	public TableModel(long id) {
		super();
		this.id = id;
	}

	public TableModel(long id, String name) {
		super();
		this.id = id;
		this.tableNo = name;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "table_no", length = 200)
	private String tableNo;
	
	@Column(name = "status")
	private int status;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@Column(name = "no_of_chairs")
	private int no_of_chairs;
	
	@OneToOne
	@JoinColumn(name = "employee")
	private UserModel employee;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTableNo() {
		return tableNo;
	}

	public void setTableNo(String tableNo) {
		this.tableNo = tableNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public int getNo_of_chairs() {
		return no_of_chairs;
	}

	public void setNo_of_chairs(int no_of_chairs) {
		this.no_of_chairs = no_of_chairs;
	}

	public UserModel getEmployee() {
		return employee;
	}

	public void setEmployee(UserModel employee) {
		this.employee = employee;
	}

}
