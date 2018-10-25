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
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_ROLE_OPTION_MAPPING)
public class S_RoleOptionMappingModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5358511425673758784L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "role_id")
	private S_UserRoleModel role_id;

	@OneToOne
	@JoinColumn(name = "option_id")
	private S_OptionModel option_id;

	public S_RoleOptionMappingModel() {
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

	public S_UserRoleModel getRole_id() {
		return role_id;
	}

	public void setRole_id(S_UserRoleModel role_id) {
		this.role_id = role_id;
	}

}
