package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;

/**
 * @author anil
 * @date 03-Sep-2015
 * @Project REVERP
 */

@Entity
@Table(name = SConstants.tb_names.I_CLEARING_AGENT)
public class ClearingAgentModel implements Serializable {
	
	private static final long serialVersionUID = 8743992386074251444L;

	public ClearingAgentModel() {

	}
	
	public ClearingAgentModel(long id) {
		super();
		this.id = id;
	}

	public ClearingAgentModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "agent_code", length = 200)
	private String agent_code;
	
	@Column(name = "details")
	private String details;
	
	@OneToOne
	@JoinColumn(name = "ledger_id")
	private LedgerModel ledger;

	@OneToOne
	@JoinColumn(name = "address")
	private AddressModel address;
	
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

	public String getAgent_code() {
		return agent_code;
	}

	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public LedgerModel getLedger() {
		return ledger;
	}

	public void setLedger(LedgerModel ledger) {
		this.ledger = ledger;
	}

	public AddressModel getAddress() {
		return address;
	}

	public void setAddress(AddressModel address) {
		this.address = address;
	}
}