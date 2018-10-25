package com.inventory.proposal.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.model.ItemModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_CUSTOMER_ENQUIRY)
public class CustomerEnquiryModel implements Serializable {

	private static final long serialVersionUID = 673991452000260834L;

	public CustomerEnquiryModel(long id, String enquiry) {
		super();
		this.id = id;
		this.enquiry = enquiry;
	}

	public CustomerEnquiryModel(long id, String enquiry, long number) {
		super();
		this.id = id;
		this.enquiry = enquiry;
		this.number = number;
	}

	public CustomerEnquiryModel(long id) {
		super();
		this.id = id;
	}

	public CustomerEnquiryModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "enquiry", length = 500)
	private String enquiry;

	@Column(name = "number", columnDefinition = "bigint default 0", nullable = true)
	private long number;

	@Column(name = "paren_req_id", columnDefinition = "bigint default 0", nullable = true)
	private long paren_req_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "delivery_date")
	private Date delivery_date;

	@Column(name = "level")
	private int level;

	@OneToOne
	@JoinColumn(name = "fk_customer_id")
	private LedgerModel customer;

	@OneToOne
	@JoinColumn(name = "fk_item_id")
	private ItemModel item;

	@Column(name = "responsible_employee")
	private long responsible_employee;

	@Column(name = "description", length = 2000)
	private String description;

	@Column(name = "ref_no", length = 50)
	private String ref_no;

	@Column(name = "budget_amount", columnDefinition = "double default 0", nullable = true)
	private double budget_amount;

	@OneToOne
	@JoinColumn(name = "fk_office_id")
	private S_OfficeModel office;

	@Column(name = "status")
	private long status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEnquiry() {
		return enquiry;
	}

	public void setEnquiry(String enquiry) {
		this.enquiry = enquiry;
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

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	public long getResponsible_employee() {
		return responsible_employee;
	}

	public void setResponsible_employee(long responsible_employee) {
		this.responsible_employee = responsible_employee;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getParen_req_id() {
		return paren_req_id;
	}

	public void setParen_req_id(long paren_req_id) {
		this.paren_req_id = paren_req_id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public double getBudget_amount() {
		return budget_amount;
	}

	public void setBudget_amount(double budget_amount) {
		this.budget_amount = budget_amount;
	}

	public Date getDelivery_date() {
		return delivery_date;
	}

	public void setDelivery_date(Date delivery_date) {
		this.delivery_date = delivery_date;
	}

}
