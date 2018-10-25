package com.inventory.transaction.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 24, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TRANSACTIONS)
public class TransactionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3640213386766804316L;

	public TransactionModel() {
		super();
	}

	public TransactionModel(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	@Id
	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "status")
	private long status;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "transaction_type")
	private int transaction_type;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "transaction_details_link", joinColumns = { @JoinColumn(name = "transaction_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<TransactionDetailsModel> transaction_details_list = new ArrayList<TransactionDetailsModel>();

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public List<TransactionDetailsModel> getTransaction_details_list() {
		return transaction_details_list;
	}

	public void setTransaction_details_list(
			List<TransactionDetailsModel> transaction_details_list) {
		this.transaction_details_list = transaction_details_list;
	}

	public int getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(int transaction_type) {
		this.transaction_type = transaction_type;
	}

}
