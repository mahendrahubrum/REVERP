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
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 11, 2014
 */

@Entity
@Table(name = SConstants.tb_names.OFFICE_OPTION_MAPPING)
public class OfficeOptionMappingModel implements Serializable {

	private static final long serialVersionUID = 2606028207497561622L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "option_id")
	private S_OptionModel option_id;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel officeId;

	public OfficeOptionMappingModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_OptionModel getOption_id() {
		return option_id;
	}

	public void setOption_id(S_OptionModel option_id) {
		this.option_id = option_id;
	}

	public S_OfficeModel getOfficeId() {
		return officeId;
	}

	public void setOfficeId(S_OfficeModel officeId) {
		this.officeId = officeId;
	}

}
