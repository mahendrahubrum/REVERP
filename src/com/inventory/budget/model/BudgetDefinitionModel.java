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

/**
 * @author Aswathy April 24,2014
 */

@Entity
@Table(name = SConstants.tb_names.BUDGET_DEFINITION)
public class BudgetDefinitionModel implements Serializable {

	private static final long serialVersionUID = -904682243999229335L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "budget_name", length = 100)
	private String budgetname;

	@Column(name = "budget_desc", length = 1000)
	private String budgetdesc;

	@Column(name = "start_date")
	private Date startdate;

	@Column(name = "end_date")
	private Date enddate;

	@Column(name = "intervals")
	private long interval;

	@Column(name = "interval_duration")
	private String intervalduration;

	@Column(name = "status")
	private long status;

	@Column(name = "department", columnDefinition = "bigint default 0", nullable = false)
	private long department;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office_id;

	public S_OfficeModel getOffice_id() {
		return office_id;
	}

	public void setOffice_id(S_OfficeModel office_id) {
		this.office_id = office_id;
	}

	public BudgetDefinitionModel() {
		super();
	}

	public BudgetDefinitionModel(long id) {
		super();
		this.id = id;
	}

	public BudgetDefinitionModel(long id, String budgetname) {
		super();
		this.id = id;
		this.budgetname = budgetname;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBudgetname() {
		return budgetname;
	}

	public void setBudgetname(String budgetname) {
		this.budgetname = budgetname;
	}

	public String getBudgetdesc() {
		return budgetdesc;
	}

	public void setBudgetdesc(String budgetdesc) {
		this.budgetdesc = budgetdesc;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public String getIntervalduration() {
		return intervalduration;
	}

	public void setIntervalduration(String intervalduration) {
		this.intervalduration = intervalduration;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getDepartment() {
		return department;
	}

	public void setDepartment(long department) {
		this.department = department;
	}

}
