package com.inventory.config.stock.model;

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
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Dec 27, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_SUPPLIER_QUOTATION)
public class SupplierQuotationModel implements Serializable {
	
	private static final long serialVersionUID = -3682176718562533855L;

	public SupplierQuotationModel() {
		super();
	}

	public SupplierQuotationModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "quotation_number")
	private long quotation_number;

	@Column(name = "login_id")
	private long login_id;
	
	@Column(name = "office_id")
	private long office_id;

	@Column(name = "date")
	private Date date;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "supplier_quotation_link", joinColumns = { @JoinColumn(name = "quotation_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<SupplierQuotationDetailsModel> quotation_details_list = new ArrayList<SupplierQuotationDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<SupplierQuotationDetailsModel> getQuotation_details_list() {
		return quotation_details_list;
	}

	public void setQuotation_details_list(
			List<SupplierQuotationDetailsModel> quotation_details_list) {
		this.quotation_details_list = quotation_details_list;
	}

	public long getQuotation_number() {
		return quotation_number;
	}

	public void setQuotation_number(long quotation_number) {
		this.quotation_number = quotation_number;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}
}
