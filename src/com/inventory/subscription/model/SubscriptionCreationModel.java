package com.inventory.subscription.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_SUBSCRIPTION_CREATION)
public class SubscriptionCreationModel implements Serializable
{
	public SubscriptionCreationModel() {

	}
	
	public SubscriptionCreationModel(long id)
	{
		super();
		this.id=id;
	}
	
	public SubscriptionCreationModel(long id,String name)
	{
		super();
		this.id=id;
		this.name=name;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="created_date")
	private Date created_date;

	@Column(name="created_by")
	private long created_by;
	
	@Column(name="available",columnDefinition = "bigint default 0", nullable = false)
	private long available;
	
	@Column(name="rent_status",columnDefinition = "bigint default 0", nullable = false)
	private long rent_status;
	
	@OneToOne
	@JoinColumn(name="subscription_type")
	private SubscriptionConfigurationModel subscription_type;
	
	@OneToOne
	@JoinColumn(name="ledger")
	private LedgerModel ledger;
	
	@Column(name="account_type")
	private long account_type;
	
	@Column(name="image")
	private String image;
	
	@Column(name="special",columnDefinition = "bigint default 0", nullable = false)
	private long special;

	public long getSpecial() {
		return special;
	}

	public void setSpecial(long special) {
		this.special = special;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(long created_by) {
		this.created_by = created_by;
	}

	public long getAccount_type() {
		return account_type;
	}

	public void setAccount_type(long account_type) {
		this.account_type = account_type;
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SubscriptionConfigurationModel getSubscription_type() {
		return subscription_type;
	}

	public void setSubscription_type(
			SubscriptionConfigurationModel subscription_type) {
		this.subscription_type = subscription_type;
	}

	public long getAvailable() {
		return available;
	}

	public void setAvailable(long available) {
		this.available = available;
	}

	public long getRent_status() {
		return rent_status;
	}

	public void setRent_status(long rent_status) {
		this.rent_status = rent_status;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}
	
}
