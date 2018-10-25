package com.inventory.management.model;

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
 * @author Jinshad P.T.
 * 
 *         Dec 30, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_TASK_COMPONENT_DETAILS)
public class TaskComponentDetailsModel implements Serializable {

	private static final long serialVersionUID = 1891162286603040155L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "fk_task_component")
	private TaskComponentModel task_component;

	@Column(name = "creater_description", length = 400)
	private String creater_description;

	@Column(name = "description", length = 600)
	private String description;

	@Column(name = "status")
	private long status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TaskComponentModel getTask_component() {
		return task_component;
	}

	public void setTask_component(TaskComponentModel task_component) {
		this.task_component = task_component;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getCreater_description() {
		return creater_description;
	}

	public void setCreater_description(String creater_description) {
		this.creater_description = creater_description;
	}

}
