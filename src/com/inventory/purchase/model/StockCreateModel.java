package com.inventory.purchase.model;

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
 * @author sangeeth
 * @date 21-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_STOCK_CREATE)
public class StockCreateModel implements Serializable {

	public StockCreateModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	public StockCreateModel(long id) {
		super();
		this.id = id;
	}

	public StockCreateModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "purchase_number")
	private String purchase_number;
	
	@Column(name = "responsible_employee")
	private long responsible_employee;
	
	@Column(name = "ref_no", length = 50)
	private String ref_no;

	@Column(name = "date")
	private Date date;

	@Column(name = "status")
	private long status;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@Column(name = "comments")
	private String comments;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "create__link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<StockCreateDetailsModel> inventory_details_list = new ArrayList<StockCreateDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPurchase_number() {
		return purchase_number;
	}

	public void setPurchase_number(String purchase_number) {
		this.purchase_number = purchase_number;
	}

	public long getResponsible_employee() {
		return responsible_employee;
	}

	public void setResponsible_employee(long responsible_employee) {
		this.responsible_employee = responsible_employee;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<StockCreateDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<StockCreateDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
	}

}
