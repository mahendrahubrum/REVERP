package com.inventory.sales.model;

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

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_SALES_HOLD)
public class HoldSalesModel implements Serializable {
	
	public HoldSalesModel() {
		super();
	}
	
	public HoldSalesModel(long id) {
		super();
		this.id = id;
	}

	public HoldSalesModel(long id, String holdSalesNumber) {
		super();
		this.id = id;
		this.holdSalesNumber = holdSalesNumber;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "holdSalesNumber")
	private String holdSalesNumber;
	
	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "customer_id")
	private LedgerModel customer;
	
	@Column(name = "responsible_employee")
	private long responsible_employee;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "hold_sales_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<HoldSalesInventoryDetailsModel> inventory_details_list = new ArrayList<HoldSalesInventoryDetailsModel>();

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

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public long getResponsible_employee() {
		return responsible_employee;
	}

	public void setResponsible_employee(long responsible_employee) {
		this.responsible_employee = responsible_employee;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public List<HoldSalesInventoryDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<HoldSalesInventoryDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

	public String getHoldSalesNumber() {
		return holdSalesNumber;
	}

	public void setHoldSalesNumber(String holdSalesNumber) {
		this.holdSalesNumber = holdSalesNumber;
	}
	
	

}
