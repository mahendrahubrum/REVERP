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
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T. WebSpark. Apr 24, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_SUPPLIER_PROPOSAL_RECEIPTION)
public class SupplierProposalReceiptionModel implements Serializable {

	private static final long serialVersionUID = -3235783368329388929L;

	public SupplierProposalReceiptionModel(long id, String head) {
		super();
		this.id = id;
		this.head = head;
	}

	public SupplierProposalReceiptionModel(long id) {
		super();
		this.id = id;
	}

	public SupplierProposalReceiptionModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "number")
	private long number;

	@Column(name = "head", length = 500)
	private String head;

	@Column(name = "content", length = 2000)
	private String content;

	@Column(name = "amount")
	private double amount;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "fk_supplier_id")
	private LedgerModel supplier;

	@OneToOne
	@JoinColumn(name = "fk_request_id")
	private SupplierQuotationRequestModel request;

	@OneToOne
	@JoinColumn(name = "fk_sendBy_id")
	private S_LoginModel sendBy;

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

	public LedgerModel getSupplier() {
		return supplier;
	}

	public void setSupplier(LedgerModel supplier) {
		this.supplier = supplier;
	}

	public SupplierQuotationRequestModel getRequest() {
		return request;
	}

	public void setRequest(SupplierQuotationRequestModel request) {
		this.request = request;
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

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
