package com.inventory.model;

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
 *         Jun 12, 2013
 */

@Entity
@Table(name = SConstants.tb_names.I_ITEM_SUBGROUP)
public class ItemSubGroupModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2642919949083305614L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "group_id")
	private ItemGroupModel group;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "code", length = 50)
	private String code;

	@Column(name = "status")
	private long status;

	@Column(name = "icon")
	private String icon;

	@Column(name = "item_parent_id" , columnDefinition="bigint default 0", nullable=false)
	private long itemParentId;
	
	public ItemSubGroupModel() {
		super();
	}

	public ItemSubGroupModel(long id) {
		super();
		this.id = id;
	}

	public ItemSubGroupModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemGroupModel getGroup() {
		return group;
	}

	public void setGroup(ItemGroupModel group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getItemParentId() {
		return itemParentId;
	}

	public void setItemParentId(long itemParentId) {
		this.itemParentId = itemParentId;
	}

}