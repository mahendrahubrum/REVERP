package com.inventory.payroll.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 5, 2013
 */

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 25, 2015
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALARY_DISBURSAL_NEW)
public class SalaryDisbursalNewModel implements Serializable {


	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "employ_id")
	private UserModel employ;

	@Column(name = "disbursal_date")
	private Date dispursal_date;

	@Column(name = "month")
	private Date month;
	
	@Column(name = "status")
	private long status;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "commission_amount", columnDefinition="double default 0",nullable=false)
	private double commission_amount;
	
	@Column(name = "previous_amount", columnDefinition="double default 0",nullable=false)
	private double previous_amount;
	
	@Column(name = "paid_amount", columnDefinition="double default 0",nullable=false)
	private double paid_amount;
	
	@Column(name = "advance_payed")
	private double advance_payed;
	
	@Column(name = "total_salary")
	private double total_salary;
	
	@Column(name = "balance_amount", columnDefinition="double default 0",nullable=false)
	private double balance_amount;
	
	@Column(name = "over_time", columnDefinition="double default 0",nullable=false)
	private double over_time;
	
	@Column(name = "rate_over_time", columnDefinition="double default 0",nullable=false)
	private double rate_over_time;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "Salary_disb_detail_link_new", joinColumns = { @JoinColumn(name = "id") }, inverseJoinColumns = { @JoinColumn(name = "detail_id") })
	private List<SalaryDisbursalDetailsModel> detailsList = new ArrayList<SalaryDisbursalDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserModel getEmploy() {
		return employ;
	}

	public void setEmploy(UserModel employ) {
		this.employ = employ;
	}

	public Date getDispursal_date() {
		return dispursal_date;
	}

	public void setDispursal_date(Date dispursal_date) {
		this.dispursal_date = dispursal_date;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}
	
	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public double getCommission_amount() {
		return commission_amount;
	}

	public void setCommission_amount(double commission_amount) {
		this.commission_amount = commission_amount;
	}

	public double getPrevious_amount() {
		return previous_amount;
	}

	public void setPrevious_amount(double previous_amount) {
		this.previous_amount = previous_amount;
	}

	public double getPaid_amount() {
		return paid_amount;
	}

	public void setPaid_amount(double paid_amount) {
		this.paid_amount = paid_amount;
	}

	public double getAdvance_payed() {
		return advance_payed;
	}

	public void setAdvance_payed(double advance_payed) {
		this.advance_payed = advance_payed;
	}

	public double getTotal_salary() {
		return total_salary;
	}

	public void setTotal_salary(double total_salary) {
		this.total_salary = total_salary;
	}

	public double getBalance_amount() {
		return balance_amount;
	}

	public void setBalance_amount(double balance_amount) {
		this.balance_amount = balance_amount;
	}

	public List<SalaryDisbursalDetailsModel> getDetailsList() {
		return detailsList;
	}

	public void setDetailsList(List<SalaryDisbursalDetailsModel> detailsList) {
		this.detailsList = detailsList;
	}

	public double getOver_time() {
		return over_time;
	}

	public void setOver_time(double over_time) {
		this.over_time = over_time;
	}

	public double getRate_over_time() {
		return rate_over_time;
	}

	public void setRate_over_time(double rate_over_time) {
		this.rate_over_time = rate_over_time;
	}
	
}
