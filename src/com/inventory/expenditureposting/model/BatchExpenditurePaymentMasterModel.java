package com.inventory.expenditureposting.model;

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
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Aug 3, 2013
 */

@Entity
@Table(name = SConstants.tb_names.BATCH_EXPENDITURE_PAYMENT_MASTER)
public class BatchExpenditurePaymentMasterModel implements Serializable {

	private static final long serialVersionUID = 2841579076377740806L;
	
	public BatchExpenditurePaymentMasterModel() {
		super();
	}
	
	public BatchExpenditurePaymentMasterModel(long id) {
		super();
		this.id = id;
	}

	public BatchExpenditurePaymentMasterModel(long id, String exp_transaction_ids) {
		super();
		this.id = id;
		this.exp_transaction_ids = exp_transaction_ids;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "group_id")
	private long group_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "number")
	private long number;

	@Column(name = "exp_transaction_ids")
	private String exp_transaction_ids;

	@Column(name = "office_id")
	private long office_id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "batch_expenditure_payment_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<BatchExpenditurePaymentDetailsModel> details_list = new ArrayList<BatchExpenditurePaymentDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

	public List<BatchExpenditurePaymentDetailsModel> getDetails_list() {
		return details_list;
	}

	public void setDetails_list(
			List<BatchExpenditurePaymentDetailsModel> details_list) {
		this.details_list = details_list;
	}

	public long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}


	public String getExp_transaction_ids() {
		return exp_transaction_ids;
	}

	public void setExp_transaction_ids(String exp_transaction_ids) {
		this.exp_transaction_ids = exp_transaction_ids;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
