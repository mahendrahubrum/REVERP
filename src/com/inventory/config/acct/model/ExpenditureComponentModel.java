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
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Apr 7, 2014
 */

@Entity
@Table(name = SConstants.tb_names.EXPENDETURE_COMPONENTS)
public class ExpenditureComponentModel implements Serializable {

	private static final long serialVersionUID = -5912586811006506322L;

	public ExpenditureComponentModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public ExpenditureComponentModel(long id) {
		super();
		this.id = id;
	}

	public ExpenditureComponentModel() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 150)
	private String name;

	@Column(name = "ledger_id")
	private long ledger_id;

	@Column(name = "amount")
	private double amount;

	@Column(name = "description", length = 600)
	private String description;

	@Column(name = "status")
	private long status;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getLedger_id() {
		return ledger_id;
	}

	public void setLedger_id(long ledger_id) {
		this.ledger_id = ledger_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

}
