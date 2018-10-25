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
 * @author sangeeth
 * @date 25-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALARY_DISBURSAL)
public class SalaryDisbursalModel implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private UserModel user;

	@Column(name="office_id")
	private long officeId;
	
	@Column(name = "dispursal_date")
	private Date dispursal_date;

	@Column(name = "from_date")
	private Date from_date;

	@Column(name = "to_date")
	private Date to_date;

	@Column(name = "payroll", columnDefinition="double default 0", nullable=false)
	private double payroll;
	
	@Column(name = "advance", columnDefinition="double default 0", nullable=false)
	private double advance;
	
	@Column(name = "lop", columnDefinition="double default 0", nullable=false)
	private double lop;
	
	@Column(name = "overTime", columnDefinition="double default 0", nullable=false)
	private double overTime;
	
	@Column(name = "commission", columnDefinition="double default 0", nullable=false)
	private double commission;
	
	@Column(name = "loan", columnDefinition="double default 0", nullable=false)
	private double loan;
	
	@Column(name = "transaction_id", columnDefinition="bigint default 0", nullable=false)
	private long transactionId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "Salary_disp_detail_link", joinColumns = { @JoinColumn(name = "id") }, inverseJoinColumns = { @JoinColumn(name = "detail_id") })
	private List<SalaryDisbursalDetailsModel> detailsList = new ArrayList<SalaryDisbursalDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public Date getDispursal_date() {
		return dispursal_date;
	}

	public void setDispursal_date(Date dispursal_date) {
		this.dispursal_date = dispursal_date;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public Date getTo_date() {
		return to_date;
	}

	public void setTo_date(Date to_date) {
		this.to_date = to_date;
	}

	public double getPayroll() {
		return payroll;
	}

	public void setPayroll(double payroll) {
		this.payroll = payroll;
	}

	public double getAdvance() {
		return advance;
	}

	public void setAdvance(double advance) {
		this.advance = advance;
	}

	public double getLop() {
		return lop;
	}

	public void setLop(double lop) {
		this.lop = lop;
	}

	public double getOverTime() {
		return overTime;
	}

	public void setOverTime(double overTime) {
		this.overTime = overTime;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public double getLoan() {
		return loan;
	}

	public void setLoan(double loan) {
		this.loan = loan;
	}

	public List<SalaryDisbursalDetailsModel> getDetailsList() {
		return detailsList;
	}

	public void setDetailsList(List<SalaryDisbursalDetailsModel> detailsList) {
		this.detailsList = detailsList;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	
}
