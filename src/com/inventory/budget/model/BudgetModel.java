package com.inventory.budget.model;

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

@Entity
@Table(name = SConstants.tb_names.BUDGET)

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Apr 24, 2014
 */



public class BudgetModel implements Serializable{

	private static final long serialVersionUID = 8803811779122824277L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "job_name",length = 100)
	private String jobName;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "end_Date")
	private Date endDate;
	
	@Column(name="amount")
	private double amount;
	
	@Column(name="notes", length = 1000)
	private String notes;
	
	@Column(name = "status")
	private long status;
	
	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	@OneToOne
	@JoinColumn(name= "budgetdef_id")
	private BudgetDefinitionModel budgetDef_id;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office_id;

	
	public S_OfficeModel getOffice_id() {
		return office_id;
	}

	public void setOffice_id(S_OfficeModel office_id) {
		this.office_id = office_id;
	}

	public BudgetModel(long id, String jobName) {
		super();
		this.id = id;
		this.jobName = jobName;
	}

	public BudgetModel(long id) {
		super();
		this.id = id;
	}

	public BudgetModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public BudgetDefinitionModel getBudgetDef_id() {
		return budgetDef_id;
	}

	public void setBudgetDef_id(BudgetDefinitionModel budgetDef_id) {
		this.budgetDef_id = budgetDef_id;
	}

	
	
	
}
