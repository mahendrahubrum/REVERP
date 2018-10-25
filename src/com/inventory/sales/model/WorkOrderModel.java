package com.inventory.sales.model;

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
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_WORK_ORDER)
public class WorkOrderModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3919299592972139580L;

	public WorkOrderModel() {
		super();
	}

	public WorkOrderModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "work_order_number")
	private long work_order_number;

	@Column(name = "date")
	private Date date;

	@OneToOne
	@JoinColumn(name = "contractor_led_id")
	private LedgerModel contractor;

	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "excise_duty")
	private double excise_duty;

	@Column(name = "amount")
	private double amount;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

//	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JoinTable(name = "work_order_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
//	private List<SalesInventoryDetailsModel> inventory_details_list = new ArrayList<SalesInventoryDetailsModel>();

	@Column(name = "comments")
	private String comments;

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

	public long getWork_order_number() {
		return work_order_number;
	}

	public void setWork_order_number(long work_order_number) {
		this.work_order_number = work_order_number;
	}

	public LedgerModel getContractor() {
		return contractor;
	}

	public void setContractor(LedgerModel contractor) {
		this.contractor = contractor;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

//	public List<SalesInventoryDetailsModel> getInventory_details_list() {
//		return inventory_details_list;
//	}
//
//	public void setInventory_details_list(
//			List<SalesInventoryDetailsModel> inventory_details_list) {
//		this.inventory_details_list = inventory_details_list;
//	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getShipping_charge() {
		return shipping_charge;
	}

	public void setShipping_charge(double shipping_charge) {
		this.shipping_charge = shipping_charge;
	}

	public double getExcise_duty() {
		return excise_duty;
	}

	public void setExcise_duty(double excise_duty) {
		this.excise_duty = excise_duty;
	}

}
