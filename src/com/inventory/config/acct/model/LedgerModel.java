package com.inventory.config.acct.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 15, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_LEDGER)
public class LedgerModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8253727891141924631L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@OneToOne
	@JoinColumn(name = "group_id")
//	@Cascade(CascadeType.DELETE)
	private GroupModel group;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "status")
	private long status;

	@Column(name = "type")
	private int type;

	@Column(name = "current_balance")
	private double current_balance;

	@Column(name = "parent_id", columnDefinition = "bigint default 0", nullable = false)
	private long parentId;

	public LedgerModel() {
		super();
	}

	public LedgerModel(long id) {
		super();
		this.id = id;
	}

	public LedgerModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public LedgerModel(String name) {
		super();
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

	public GroupModel getGroup() {
		return group;
	}

	public void setGroup(GroupModel group) {
		this.group = group;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public double getCurrent_balance() {
		return current_balance;
	}

	public void setCurrent_balance(double current_balance) {
		this.current_balance = current_balance;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

}