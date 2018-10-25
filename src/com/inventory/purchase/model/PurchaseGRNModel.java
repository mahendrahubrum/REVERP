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

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         Jun 22, 2013
 */

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_PURCHASE_GRN)
public class PurchaseGRNModel implements Serializable {

	public PurchaseGRNModel() {
		super();
	}

	public PurchaseGRNModel(long id, String grn_no) {
		super();
		this.id = id;
		this.grn_no = grn_no;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "grn_no", length = 50)
	private String grn_no;
	
	@Column(name = "ref_no", length = 50)
	private String ref_no;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "supplier_id")
	private LedgerModel supplier;

	@Column(name = "responsible_employee")
	private long responsible_employee;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "comments", length = 400)
	private String comments;

	@Column(name = "amount")
	private double amount;
	
	@Column(name = "currency_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long currencyId;

	@Column(name = "conversion_rate",columnDefinition ="double default 0", nullable=false)
	private double conversionRate;
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;
	
	@Column(name = "lock_count" ,columnDefinition ="double default 0", nullable=false)
	private double lock_count;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "purchase_grn_details_link", joinColumns = { @JoinColumn(name = "grn_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<PurchaseGRNDetailsModel> grn_details_list = new ArrayList<PurchaseGRNDetailsModel>();
	
	@Column(name = "department_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long department_id;
	
	@Column(name = "division_id" ,columnDefinition ="bigint default 0", nullable=false)
	private long division_id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGrn_no() {
		return grn_no;
	}

	public void setGrn_no(String grn_no) {
		this.grn_no = grn_no;
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

	public LedgerModel getSupplier() {
		return supplier;
	}

	public void setSupplier(LedgerModel supplier) {
		this.supplier = supplier;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<PurchaseGRNDetailsModel> getGrn_details_list() {
		return grn_details_list;
	}

	public void setGrn_details_list(List<PurchaseGRNDetailsModel> grn_details_list) {
		this.grn_details_list = grn_details_list;
	}

	public double getLock_count() {
		return lock_count;
	}

	public void setLock_count(double lock_count) {
		this.lock_count = lock_count;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}
	public long getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(long department_id) {
		this.department_id = department_id;
	}

	public long getDivision_id() {
		return division_id;
	}

	public void setDivision_id(long division_id) {
		this.division_id = division_id;
	}
}
