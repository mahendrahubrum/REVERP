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
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 24, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_ITEM_DAILY_RATE)
public class ItemDailyRateModel implements Serializable {

	private static final long serialVersionUID = 8992225097194787331L;

	public ItemDailyRateModel() {
		super();
	}

	public ItemDailyRateModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "customer_id")
	private long customer_id;

	@Column(name = "sales_type")
	private long sales_type;

	@Column(name = "login_id")
	private long login_id;

	@Column(name = "office_id")
	private long office_id;

	@Column(name = "date")
	private Date date;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "daily_rate_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "rate_details_id") })
	private List<ItemDailyRateDetailModel> daily_rate_list = new ArrayList<ItemDailyRateDetailModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public List<ItemDailyRateDetailModel> getDaily_rate_list() {
		return daily_rate_list;
	}

	public void setDaily_rate_list(
			List<ItemDailyRateDetailModel> daily_rate_list) {
		this.daily_rate_list = daily_rate_list;
	}

	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(long office_id) {
		this.office_id = office_id;
	}

}
