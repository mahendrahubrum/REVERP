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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 17, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_DAILY_QUOTATION)
public class DailyQuotationModel implements Serializable {

	private static final long serialVersionUID = 8620888744615412730L;

	public DailyQuotationModel() {
		super();
	}

	public DailyQuotationModel(long id) {
		super();
		this.id = id;
	}


	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "login")
	private S_LoginModel login;
	
	@Column(name = "date")
	private Date date;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "daily_quotation_link", joinColumns = { @JoinColumn(name = "quotation_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<DailyQuotationDetailsModel> quotation_details_list = new ArrayList<DailyQuotationDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<DailyQuotationDetailsModel> getQuotation_details_list() {
		return quotation_details_list;
	}

	public void setQuotation_details_list(List<DailyQuotationDetailsModel> quotation_details_list) {
		this.quotation_details_list = quotation_details_list;
	}
}
