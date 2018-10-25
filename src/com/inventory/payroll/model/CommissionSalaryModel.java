package com.inventory.payroll.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 21, 2015
 */

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_COMMISSION_SALARY)
public class CommissionSalaryModel implements Serializable {
	
	public CommissionSalaryModel() {
	
	}
	
	public CommissionSalaryModel(long id) {
		super();
		this.id = id;
	}

	public CommissionSalaryModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="payment_number")
	private long payment_number;
	
	@Column(name="transaction_id")
	private long transaction_id;
	
	@OneToOne
	@JoinColumn(name="employee")
	private UserModel employee;
	
	@OneToOne
	@JoinColumn(name="office")
	private S_OfficeModel office;
	
	@Column(name="date")
	private Date date;
	
	@Column(name="month")
	private Date month;
	
	@Column(name="salary")
	private double salary;
	
	@Column(name="paid_amount", columnDefinition ="double default 0", nullable=false)
	private double paid_amount;
	
	@Transient
	private String comments;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPayment_number() {
		return payment_number;
	}

	public void setPayment_number(long payment_number) {
		this.payment_number = payment_number;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public UserModel getEmployee() {
		return employee;
	}

	public void setEmployee(UserModel employee) {
		this.employee = employee;
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

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getPaid_amount() {
		return paid_amount;
	}

	public void setPaid_amount(double paid_amount) {
		this.paid_amount = paid_amount;
	}
	
	
}
