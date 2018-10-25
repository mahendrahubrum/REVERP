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

import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T. WebSpark. Apr 24, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_SUPPLIER_QUOTATION_REQUEST)
public class SupplierQuotationRequestModel implements Serializable {

	private static final long serialVersionUID = -3235783368329388929L;

	public SupplierQuotationRequestModel(long id, String head) {
		super();
		this.id = id;
		this.head = head;
	}

	public SupplierQuotationRequestModel(long id) {
		super();
		this.id = id;
	}

	public SupplierQuotationRequestModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "head", length = 500)
	private String head;

	@Column(name = "content", length = 2000)
	private String content;

	@Column(name = "date")
	private Date date;

	@Column(name = "suppliers", length = 200)
	private String suppliers;

	@OneToOne
	@JoinColumn(name = "fk_enquiry_id")
	private CustomerEnquiryModel enquiry;

	@OneToOne
	@JoinColumn(name = "fk_sendBy_id")
	private S_LoginModel sendBy;

	@OneToOne
	@JoinColumn(name = "fk_office_id")
	private S_OfficeModel office;

	@Column(name = "status")
	private long status;

	@Column(name = "budget_amount")
	private double budget_amount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}

	public CustomerEnquiryModel getEnquiry() {
		return enquiry;
	}

	public void setEnquiry(CustomerEnquiryModel enquiry) {
		this.enquiry = enquiry;
	}

	public S_LoginModel getSendBy() {
		return sendBy;
	}

	public void setSendBy(S_LoginModel sendBy) {
		this.sendBy = sendBy;
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

	public double getBudget_amount() {
		return budget_amount;
	}

	public void setBudget_amount(double budget_amount) {
		this.budget_amount = budget_amount;
	}

}
