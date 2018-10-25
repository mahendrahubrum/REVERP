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
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 15, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_GROUP)
public class GroupModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8085170156214721418L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 100)
	private String name;

	@OneToOne
	@JoinColumn(name = "organization")
	private S_OrganizationModel organization;

	@Column(name = "account_class_id")
	private long account_class_id;

	@Column(name = "parent_id")
	private long parent_id;

	@Column(name = "status")
	private long status;

	@Column(name = "level")
	private int level;
	
	@Column(name = "ledger_parent_id" , columnDefinition="bigint default 0", nullable=false)
	private long ledgerParentId;

	public GroupModel() {
		super();
	}

	public GroupModel(long id) {
		super();
		this.id = id;
	}

	public GroupModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
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

	public S_OrganizationModel getOrganization() {
		return organization;
	}

	public void setOrganization(S_OrganizationModel organization) {
		this.organization = organization;
	}

	public long getAccount_class_id() {
		return account_class_id;
	}

	public void setAccount_class_id(long account_class_id) {
		this.account_class_id = account_class_id;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getParent_id() {
		return parent_id;
	}

	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getLedgerParentId() {
		return ledgerParentId;
	}

	public void setLedgerParentId(long ledgerParentId) {
		this.ledgerParentId = ledgerParentId;
	}

}