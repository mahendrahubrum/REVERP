package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Anil
 */

@Entity
@Table(name = SConstants.tb_names.BILL_NAME)
public class BillNameModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7602057160112164348L;

	public BillNameModel() {
		super();
	}

	public BillNameModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "bill_name", length = 100)
	private String bill_name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBill_name() {
		return bill_name;
	}

	public void setBill_name(String bill_name) {
		this.bill_name = bill_name;
	}

}
