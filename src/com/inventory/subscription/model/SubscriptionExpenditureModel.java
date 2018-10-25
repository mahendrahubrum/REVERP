package com.inventory.subscription.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.I_SUBSCRIPTION_EXPENDETURE)
public class SubscriptionExpenditureModel implements Serializable {
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="name")
	private String name;
	
	@OneToOne
	@JoinColumn(name="office")
	private S_OfficeModel office;

	@Column(name="type",columnDefinition="bigint default 0", nullable=false)
	private long type;
	
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
	
	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public SubscriptionExpenditureModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public SubscriptionExpenditureModel(long id) {
		super();
		this.id = id;
	}
	
	public SubscriptionExpenditureModel() {
	}

}
