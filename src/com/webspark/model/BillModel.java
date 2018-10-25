package com.webspark.model;

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
 * @Author Anil
 */

@Entity
@Table(name = SConstants.tb_names.BILL)
public class BillModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4064597524305843653L;

	public BillModel() {
		super();
	}

	public BillModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "bill_name_id")
	private BillNameModel bill_name;

	// Value from sconstatns.bills
	@Column(name = "type")
	private int type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public BillNameModel getBill_name() {
		return bill_name;
	}

	public void setBill_name(BillNameModel bill_name) {
		this.bill_name = bill_name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
