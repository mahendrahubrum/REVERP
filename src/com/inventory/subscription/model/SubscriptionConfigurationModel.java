package com.inventory.subscription.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name=SConstants.tb_names.I_SUBSCRIPTION_CONFIGURATION)
public class SubscriptionConfigurationModel implements Serializable
{
	private static final long serialVersionUID = -1843428651677770076L;
	
	public SubscriptionConfigurationModel() {
		// TODO Auto-generated constructor stub
	}
	public SubscriptionConfigurationModel(long id) {
		super();
		this.id=id;
	}
	public SubscriptionConfigurationModel(long id,String name){
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
	
	@Column(name="account_type")
	private long account_type;
	
	@Column(name="status")
	private long status;
	
	@Column(name="officeId")
	private long officeId;
	
	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getAccount_type() {
		return account_type;
	}

	public void setAccount_type(long account_type) {
		this.account_type = account_type;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
