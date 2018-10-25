package com.inventory.budget.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Apr 25, 2014
 */

@Entity
@Table(name = SConstants.tb_names.BUDGETLV_CHILD)
public class BudgetLVChildModel implements Serializable{

	private static final long serialVersionUID = -5775027168799289267L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "from_date")
	private Date from_date;
	
	@Column(name = "to_date")
	private Date to_date;
	
	@Column(name = "person_name")
	private String person_name;
	
	public String getPerson_name() {
		return person_name;
	}

	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}

	@Column(name = "reference_no")
	private String ref_no;
	
	@Column(name = "amount")
	private double amount;

	public BudgetLVChildModel() {
		super();
	}

	public BudgetLVChildModel(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
