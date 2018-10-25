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
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 11, 2014
 */

@Entity
@Table(name = SConstants.tb_names.ORGANIZATION_OPTION_MAPPING)
public class OrganizationOptionMappingModel implements Serializable {

	private static final long serialVersionUID = -589662261305433300L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "option_id")
	private S_OptionModel option_id;

	@OneToOne
	@JoinColumn(name = "organization_id")
	private S_OrganizationModel organizationId;

	public OrganizationOptionMappingModel() {
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

	public S_OrganizationModel getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(S_OrganizationModel organizationId) {
		this.organizationId = organizationId;
	}

}
