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
@Table(name = SConstants.tb_names.I_PROPOSALS_SENT_TO_CUSTOMERS)
public class ProposalsSentToCustomersModel implements Serializable {
	
	private static final long serialVersionUID = 8782885187747232842L;

	public ProposalsSentToCustomersModel(long id, String head) {
		super();
		this.id = id;
		this.head = head;
	}

	public ProposalsSentToCustomersModel(long id) {
		super();
		this.id = id;
	}

	public ProposalsSentToCustomersModel() {
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

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "fk_customer_id")
	private LedgerModel customer;

	@OneToOne
	@JoinColumn(name = "fk_supplier_proposal_id")
	private SupplierProposalReceiptionModel supplier_proposal;

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

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public SupplierProposalReceiptionModel getSupplier_proposal() {
		return supplier_proposal;
	}

	public void setSupplier_proposal(
			SupplierProposalReceiptionModel supplier_proposal) {
		this.supplier_proposal = supplier_proposal;
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

}
