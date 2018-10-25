package com.inventory.budget.model;

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
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Apr 25, 2014
 */
@Entity
@Table(name = SConstants.tb_names.BUDGETLV_MASTER)
public class BudgetLVMasterModel implements Serializable{

	private static final long serialVersionUID = 8366632181125131100L;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "total_amt")
	private double total_amt;
	
	@OneToOne
	@JoinColumn(name = "budget_id")
	private BudgetModel budget_id;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office_id;
	
	@Column(name = "bank")
	private long bank;

	public long getBank() {
		return bank;
	}

	public void setBank(long bank) {
		this.bank = bank;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "budget_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "child_id") })
	private List<BudgetLVChildModel> inventory_details_list = new ArrayList<BudgetLVChildModel>();
	
	public BudgetLVMasterModel() {
		super();
	}

	public BudgetLVMasterModel(long id) {
		super();
		this.id = id;
	}

	
	public List<BudgetLVChildModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<BudgetLVChildModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(double total_amt) {
		this.total_amt = total_amt;
	}

	public BudgetModel getBudget_id() {
		return budget_id;
	}

	public void setBudget_id(BudgetModel budget_id) {
		this.budget_id = budget_id;
	}

	public S_OfficeModel getOffice_id() {
		return office_id;
	}

	public void setOffice_id(S_OfficeModel office_id) {
		this.office_id = office_id;
	}
	
	
	
	
	

}
