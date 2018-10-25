package com.inventory.fixedasset.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Muhammed shah A
 * @date Nov 13, 2015
 * @Project REVERP
 */
@Entity
@Table(name = SConstants.tb_names.I_FIXED_ASSET)
public class FixedAssetModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "id",nullable = false)
	private long id;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@Column(name = "name")
	private String name;
	
	@OneToOne
	@JoinColumn(name = "asset_type_group")
	private GroupModel assetTypeGroup;
	
	@Column(name = "depreciation_type")
	private int depreciationType;
	
	@Column(name = "percentage")
	private double percentage;
	
	@Column(name = "calculation_type")
	private int calculationType;
	
	@OneToOne
	@JoinColumn(name = "depreciation_account_id")
	private LedgerModel depreciationAccount;
	
	@Column(name = "is_movable")
	private boolean isMovable;
	
	@OneToOne
	@JoinColumn(name = "account_id")
	private LedgerModel account;
	
	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;
	
	public FixedAssetModel() {
		super();
	}
	public FixedAssetModel(long id) {
		this.id = id;
	}
	public FixedAssetModel(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public UnitModel getUnit() {
		return unit;
	}
	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public GroupModel getAssetTypeGroup() {
		return assetTypeGroup;
	}
	public void setAssetTypeGroup(GroupModel assetTypeGroup) {
		this.assetTypeGroup = assetTypeGroup;
	}
	public int getDepreciationType() {
		return depreciationType;
	}
	public void setDepreciationType(int depreciationType) {
		this.depreciationType = depreciationType;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public int getCalculationType() {
		return calculationType;
	}
	public void setCalculationType(int calculationType) {
		this.calculationType = calculationType;
	}
	public LedgerModel getDepreciationAccount() {
		return depreciationAccount;
	}
	public void setDepreciationAccount(LedgerModel depreciationAccount) {
		this.depreciationAccount = depreciationAccount;
	}
	public boolean isMovable() {
		return isMovable;
	}
	public void setMovable(boolean isMovable) {
		this.isMovable = isMovable;
	}
	public LedgerModel getAccount() {
		return account;
	}
	public void setAccount(LedgerModel account) {
		this.account = account;
	}
	
	
	

}
