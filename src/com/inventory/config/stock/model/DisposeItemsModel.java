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
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_DISPOSE_ITEMS_MODEL)
public class DisposeItemsModel implements Serializable{
	
	
	public DisposeItemsModel(long id, Date date) {
		super();
		this.id = id;
		this.date = date;
	}
	
	public DisposeItemsModel(long id,Date date,List<DisposalItemsDetailsModel> itemList) {
		super();
		this.id = id;
		this.date = date;
		this.item_details_list = itemList;
	}

	public DisposeItemsModel(long id) {
		super();
		this.id = id;
	}

	public DisposeItemsModel() {
		super();
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "date")
	private Date date;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "dispose_item_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<DisposalItemsDetailsModel> item_details_list = new ArrayList<DisposalItemsDetailsModel>();

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

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public List<DisposalItemsDetailsModel> getItem_details_list() {
		return item_details_list;
	}

	public void setItem_details_list(
			List<DisposalItemsDetailsModel> item_details_list) {
		this.item_details_list = item_details_list;
	}
	
}
