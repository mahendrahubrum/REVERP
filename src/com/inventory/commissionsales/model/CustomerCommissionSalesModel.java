package com.inventory.commissionsales.model;

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
 * @author Jinshad PT
 * 
 *         Mar 25 2014
 */

@Entity
@Table(name = SConstants.tb_names.CUSTOMER_COMMISSION_SALES)
public class CustomerCommissionSalesModel implements Serializable {

	private static final long serialVersionUID = -8697152078164261315L;

	public CustomerCommissionSalesModel() {
		super();
	}

	public CustomerCommissionSalesModel(long id, String ref_no) {
		super();
		this.id = id;
		this.ref_no = ref_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "date")
	private Date date;

	@Column(name = "ref_no", length = 50)
	private String ref_no;

	@Column(name = "sales_no")
	private long sales_no;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "container_id")
	private long containerId;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "comments", length = 400)
	private String comments;

	@Column(name = "amount")
	private double amount;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "comn_sal_cust_details_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<CommissionSalesCustomerDetailsModel> details_list = new ArrayList<CommissionSalesCustomerDetailsModel>();

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

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public long getContainerId() {
		return containerId;
	}

	public void setContainerId(long containerId) {
		this.containerId = containerId;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
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

	public List<CommissionSalesCustomerDetailsModel> getDetails_list() {
		return details_list;
	}

	public void setDetails_list(
			List<CommissionSalesCustomerDetailsModel> details_list) {
		this.details_list = details_list;
	}

	public long getSales_no() {
		return sales_no;
	}

	public void setSales_no(long sales_no) {
		this.sales_no = sales_no;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

}
