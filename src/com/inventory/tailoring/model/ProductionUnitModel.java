package com.inventory.tailoring.model;

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
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 18, 2014
 */

@Entity
@Table(name = SConstants.tb_names.I_PRODUCTION_UNIT)
public class ProductionUnitModel implements Serializable {

	private static final long serialVersionUID = 1759270199552663157L;

	public ProductionUnitModel() {
		super();
	}

	public ProductionUnitModel(long id) {
		super();
		this.id = id;
	}

	public ProductionUnitModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "details", length = 200)
	private String details;

	@Column(name = "name")
	private String name;

	@OneToOne
	@JoinColumn(name = "office")
	private S_OfficeModel office;

	@Column(name = "priority_order" , columnDefinition="integer default 0", nullable=false)
	private int priorityOrder;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
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

	public int getPriorityOrder() {
		return priorityOrder;
	}

	public void setPriorityOrder(int priorityOrder) {
		this.priorityOrder = priorityOrder;
	}
}
