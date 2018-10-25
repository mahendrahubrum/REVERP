package com.inventory.subscription.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;


@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_SUBSCRIPTION_PAYMENT_DETAILS)
public class SubscriptionPaymentDetailsModel {
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@OneToOne
	@JoinColumn(name="expenditure")
	private SubscriptionExpenditureModel expenditure;
	
	@Column(name="details",length=500)
	private String details;
	
	@Column(name="remarks",length=500)
	private String remarks;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SubscriptionExpenditureModel getExpenditure() {
		return expenditure;
	}

	public void setExpenditure(SubscriptionExpenditureModel expenditure) {
		this.expenditure = expenditure;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	

}
