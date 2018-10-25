package com.inventory.config.stock.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 12, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_PAYMENT_TERMS)
public class PaymentTermsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4981867665535875855L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "organization_id")
	private long organization_id;

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "days", length = 100)
	private int days;

	@Column(name = "status")
	private long status;

	public PaymentTermsModel() {
		super();
	}

	public PaymentTermsModel(long id) {
		super();
		this.id = id;
	}

	public PaymentTermsModel(long id, String name) {
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

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(long organization_id) {
		this.organization_id = organization_id;
	}

}
