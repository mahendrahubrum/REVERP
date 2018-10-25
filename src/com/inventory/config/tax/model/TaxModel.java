package com.inventory.config.tax.model;

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
 *         Jun 15, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TAX)
public class TaxModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7836534833575363271L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 100)
	private String name;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "tax_type")
	private long tax_type;

	@Column(name = "value_type")
	private long value_type;

	@Column(name = "value")
	private double value;

	@Column(name = "status")
	private long status;

	public TaxModel() {
		super();
	}

	public TaxModel(long id) {
		super();
		this.id = id;
	}

	public TaxModel(long id, String name) {
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

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public long getTax_type() {
		return tax_type;
	}

	public void setTax_type(long tax_type) {
		this.tax_type = tax_type;
	}

	public long getValue_type() {
		return value_type;
	}

	public void setValue_type(long value_type) {
		this.value_type = value_type;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}