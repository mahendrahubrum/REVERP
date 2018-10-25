package com.inventory.subscription.model;

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

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_SUBSCRIPTION_IN)
public class SubscriptionInModel implements Serializable
{
	public SubscriptionInModel() {
		// TODO Auto-generated constructor stub
	}
	
	public SubscriptionInModel(long id)
	{
		super();
		this.id=id;
	}
	
	public SubscriptionInModel(long id,String details)
	{
		super();
		this.id=id;
		this.details=details;
	}
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="subscription_no",columnDefinition="bigint default 0",nullable=false)
	private long subscription_no;
	
	@OneToOne
	@JoinColumn(name="subscription")
	private SubscriptionCreationModel subscription;
	
	@Column(name="subscription_date",nullable=false)
	private Date subscription_date;
	
	@Column(name="quantity")
	private int quantity;
	
	@Column(name="period_type")
	private int period_type;
	
	@Column(name="closing_date",nullable=false)
	private Date closing_date;
	
	@Column(name="return_date",nullable=true)
	private Date return_date;
	
	@Column(name="account_type")
	private long account_type;
	
	@Column(name="lock_status",columnDefinition = "bigint default 0", nullable = false)
	private long lock;
	
	@Column(name="subscriber")
	private long subscriber;
	
	@Column(name="rate")
	private double rate;
	
	@Column(name="period")
	private long period;
	
	@Column(name="details")
	private String details;

	@Column(name="available",columnDefinition = "bigint default 0", nullable = false)
	private long available;
	
	@Column(name="rent_in",columnDefinition = "bigint default 0", nullable = false)
	private long rent_in;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "subscription_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<SubscriptionInventoryDetailsModel> subscription_details_list = new ArrayList<SubscriptionInventoryDetailsModel>();
	
	public List<SubscriptionInventoryDetailsModel> getSubscription_details_list() {
		return subscription_details_list;
	}

	public void setSubscription_details_list(
			List<SubscriptionInventoryDetailsModel> subscription_details_list) {
		this.subscription_details_list = subscription_details_list;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSubscription_no() {
		return subscription_no;
	}

	public void setSubscription_no(long subscription_no) {
		this.subscription_no = subscription_no;
	}

	public SubscriptionCreationModel getSubscription() {
		return subscription;
	}

	public void setSubscription(SubscriptionCreationModel subscription) {
		this.subscription = subscription;
	}

	public Date getSubscription_date() {
		return subscription_date;
	}

	public void setSubscription_date(Date subscription_date) {
		this.subscription_date = subscription_date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPeriod_type() {
		return period_type;
	}

	public void setPeriod_type(int period_type) {
		this.period_type = period_type;
	}

	public Date getClosing_date() {
		return closing_date;
	}

	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}

	public Date getReturn_date() {
		return return_date;
	}

	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}

	public long getAccount_type() {
		return account_type;
	}

	public void setAccount_type(long account_type) {
		this.account_type = account_type;
	}

	public long getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(long subscriber) {
		this.subscriber = subscriber;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public long getAvailable() {
		return available;
	}

	public void setAvailable(long available) {
		this.available = available;
	}

	public long getRent_in() {
		return rent_in;
	}

	public void setRent_in(long rent_in) {
		this.rent_in = rent_in;
	}

	public long getLock() {
		return lock;
	}

	public void setLock(long lock) {
		this.lock = lock;
	}
}
