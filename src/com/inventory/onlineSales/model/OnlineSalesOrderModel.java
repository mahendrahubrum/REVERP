package com.inventory.onlineSales.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 24, 2014
 */
@Entity
@Table(name = SConstants.tb_names.I_ONLINE_SALES_ORDER)
public class OnlineSalesOrderModel implements Serializable {

	private static final long serialVersionUID = -5970859926343097572L;

	public OnlineSalesOrderModel() {
		super();
	}

	public OnlineSalesOrderModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "date")
	private Date date;

	@Column(name = "online_customer_id")
	private long onlineCustomer;

	@Column(name = "status")
	private long status;

	@Column(name = "total_amount")
	private double totalAmount;
	
	@Transient
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


	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getOnlineCustomer() {
		return onlineCustomer;
	}

	public void setOnlineCustomer(long onlineCustomer) {
		this.onlineCustomer = onlineCustomer;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
