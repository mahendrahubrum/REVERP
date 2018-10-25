package com.inventory.finance.model;

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
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 30, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_FINANCE_PAYMENT)
public class FinancePaymentModel implements Serializable {

	private static final long serialVersionUID = 2180750237108324484L;

	public FinancePaymentModel() {
		super();
	}

	public FinancePaymentModel(long id) {
		super();
		this.id = id;
	}

	public FinancePaymentModel(long id, String payment_no) {
		super();
		this.id = id;
		this.payment_no = payment_no;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "date")
	private Date date;

	@Column(name = "payment_no")
	private String payment_no;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "description")
	private String description;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "finance_payment_link", joinColumns = { @JoinColumn(name = "payment_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<FinancePaymentDetailsModel> finance_payment_list = new ArrayList<FinancePaymentDetailsModel>();
	
	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPayment_no() {
		return payment_no;
	}

	public void setPayment_no(String payment_no) {
		this.payment_no = payment_no;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<FinancePaymentDetailsModel> getFinance_payment_list() {
		return finance_payment_list;
	}

	public void setFinance_payment_list(
			List<FinancePaymentDetailsModel> finance_payment_list) {
		this.finance_payment_list = finance_payment_list;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
