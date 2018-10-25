package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 */

/**
 * @author sangeeth
 * @date 06-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.INVOICE_FORMAT)
public class InvoiceFormatModel implements Serializable {


	public InvoiceFormatModel() {
		super();
	}

	public InvoiceFormatModel(long id, String invocieFormat) {
		super();
		this.id = id;
		this.invocieFormat = invocieFormat;
	}

	public InvoiceFormatModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "country_id")
	private long id;

	@Column(name = "invoice_format", length = 250)
	private String invocieFormat;
	
	@Column(name = "id_format")
	private long idFormat;
	
	@Column(name = "office")
	private long office;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInvocieFormat() {
		return invocieFormat;
	}

	public void setInvocieFormat(String invocieFormat) {
		this.invocieFormat = invocieFormat;
	}

	public long getIdFormat() {
		return idFormat;
	}

	public void setIdFormat(long idFormat) {
		this.idFormat = idFormat;
	}

	public long getOffice() {
		return office;
	}

	public void setOffice(long office) {
		this.office = office;
	}
	
}
