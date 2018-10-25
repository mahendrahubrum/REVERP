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

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_OPTION)
public class S_OptionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3216092746260263028L;

	public S_OptionModel() {
		super();
	}

	public S_OptionModel(long option_id) {
		super();
		this.option_id = option_id;
	}

	public S_OptionModel(long option_id, String option_name) {
		super();
		this.option_id = option_id;
		this.option_name = option_name;
	}

	@Id
	@GeneratedValue
	@Column(name = "option_id")
	private long option_id;

	@Column(name = "active")
	private char active;

	@OneToOne
	@JoinColumn(name = "option_group")
	private S_OptionGroupModel group;

	@Column(name = "option_name", length = 200)
	private String option_name;

	@Column(name = "class_name", length = 100)
	private String class_name;

	@Column(name = "tool_tip", length = 200)
	private String tool_tip;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "priority_order")
	private int priority_order;

	@Column(name = "is_create_new", columnDefinition = "boolean default false", nullable = false)
	private boolean create;

	@Column(name = "analytics_class_name")
	private String analyticsClassName;

	public long getOption_id() {
		return option_id;
	}

	public void setOption_id(long option_id) {
		this.option_id = option_id;
	}

	public String getOption_name() {
		return option_name;
	}

	public void setOption_name(String option_name) {
		this.option_name = option_name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getTool_tip() {
		return tool_tip;
	}

	public void setTool_tip(String tool_tip) {
		this.tool_tip = tool_tip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

	public S_OptionGroupModel getGroup() {
		return group;
	}

	public void setGroup(S_OptionGroupModel group) {
		this.group = group;
	}

	public int getPriority_order() {
		return priority_order;
	}

	public void setPriority_order(int priority_order) {
		this.priority_order = priority_order;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public String getAnalyticsClassName() {
		return analyticsClassName;
	}

	public void setAnalyticsClassName(String analyticsClassName) {
		this.analyticsClassName = analyticsClassName;
	}

}
