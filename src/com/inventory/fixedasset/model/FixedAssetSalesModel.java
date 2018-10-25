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

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_FIXED_ASSET_SALES)
public class FixedAssetSalesModel implements Serializable{
	
	public FixedAssetSalesModel() {

	}
	
	public FixedAssetSalesModel(long id, String assetNo) {
		this.id = id;
		this.assetSalesNo = assetNo;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column(name = "asset_sales_no")
	private String assetSalesNo;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "ref_no")
	private String ref_no;
	
	@Column(name = "customer")
	private String customer;
	
	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;
	
	@Column(name = "cash_or_cheque")
	private long cashOrCheque;
	
	@Column(name = "payingAmount")
	private double payingAmount;
	
	@Column(name = "net_price")
	private double netPrice;
	
	@Column(name = "currency_id")
	private long currencyId;
	
	@Column(name = "conversion_rate")
	private double conversionRate;
	
	@Column(name = "transaction_id")
	private long transactionId;
	
	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "i_fixed_asset_sales_details_link", joinColumns = { @JoinColumn(name = "asset_sales_id") }, inverseJoinColumns = { @JoinColumn(name = "asset_sales_detail_id") })
	private List<FixedAssetSalesDetailsModel> fixed_asset_sales_details_list = new ArrayList<FixedAssetSalesDetailsModel>();

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

	

	public long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getCashOrCheque() {
		return cashOrCheque;
	}

	public void setCashOrCheque(long cashOrCheque) {
		this.cashOrCheque = cashOrCheque;
	}

	public double getPayingAmount() {
		return payingAmount;
	}

	public void setPayingAmount(double payingAmount) {
		this.payingAmount = payingAmount;
	}

	public double getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(double netPrice) {
		this.netPrice = netPrice;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	

	public String getAssetSalesNo() {
		return assetSalesNo;
	}

	public void setAssetSalesNo(String assetSalesNo) {
		this.assetSalesNo = assetSalesNo;
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

	

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public List<FixedAssetSalesDetailsModel> getFixed_asset_sales_details_list() {
		return fixed_asset_sales_details_list;
	}

	public void setFixed_asset_sales_details_list(
			List<FixedAssetSalesDetailsModel> fixed_asset_sales_details_list) {
		this.fixed_asset_sales_details_list = fixed_asset_sales_details_list;
	}

	
}
