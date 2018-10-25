package com.inventory.fixedasset.model;

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
import com.webspark.uac.model.S_OfficeModel;

@Entity
@Table (name = SConstants.tb_names.I_FIXED_ASSET_DEPRECIATION_MAIN)
public class FixedAssetDepreciationMainModel implements Serializable{
	
public FixedAssetDepreciationMainModel() {
	
}
	/**
	 * 
	 */
	public FixedAssetDepreciationMainModel(long id, String depreciationNo) {
		this.id = id;
		this.depreciationNo = depreciationNo;
	}
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column (name = "id")
	private long id;	
	
	@Column(name = "depreciationNo")
	private String depreciationNo;
	
	@OneToOne
	@JoinColumn (name = "office_id")
	private S_OfficeModel office;
	
	@OneToOne
	@JoinColumn (name = "login_id")
	private S_LoginModel login;
	
	@Column(name = "date")
	private Date date;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "i_fixed_asset_depreciation_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "depreciation_id") })
	private List<FixedAssetDepreciationModel> fixed_asset_depreciation_list = new ArrayList<FixedAssetDepreciationModel>();
	
	@Column(name = "transaction_id")
	private long transactionId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<FixedAssetDepreciationModel> getFixed_asset_depreciation_list() {
		return fixed_asset_depreciation_list;
	}

	public void setFixed_asset_depreciation_list(
			List<FixedAssetDepreciationModel> fixed_asset_depreciation_list) {
		this.fixed_asset_depreciation_list = fixed_asset_depreciation_list;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getDepreciationNo() {
		return depreciationNo;
	}

	public void setDepreciationNo(String depreciationNo) {
		this.depreciationNo = depreciationNo;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}
	
	

}
