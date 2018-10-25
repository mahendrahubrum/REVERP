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

@Entity
@Table(name=SConstants.tb_names.S_MODULE)
public class S_ModuleModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3888174973664169629L;

	public S_ModuleModel() {
		super();
	}
	
	

	public S_ModuleModel(long id) {
		super();
		this.id = id;
	}



	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="module_name", length=40)
	private String module_name;
	
	@Column(name = "priority_order" , columnDefinition = "int default 0", nullable = false)
	private int priority_order;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public int getPriority_order() {
		return priority_order;
	}

	public void setPriority_order(int priority_order) {
		this.priority_order = priority_order;
	}
	
}
