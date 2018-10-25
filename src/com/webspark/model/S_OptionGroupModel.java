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
import com.webspark.uac.model.S_ModuleModel;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_OPTION_GROUP)
public class S_OptionGroupModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3328072599041928870L;

	public S_OptionGroupModel() {
		super();
	}

	public S_OptionGroupModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "module_id")
	private S_ModuleModel module;

	@Column(name = "option_group_name", length = 40)
	private String option_group_name;

	@Column(name = "priority_order")
	private int priority_order;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public S_ModuleModel getModule() {
		return module;
	}

	public void setModule(S_ModuleModel module) {
		this.module = module;
	}

	public String getOption_group_name() {
		return option_group_name;
	}

	public void setOption_group_name(String option_group_name) {
		this.option_group_name = option_group_name;
	}

	public int getPriority_order() {
		return priority_order;
	}

	public void setPriority_order(int priority_order) {
		this.priority_order = priority_order;
	}

}
