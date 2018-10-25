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
@Table(name = SConstants.tb_names.I_ROOM)
public class RoomModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4536125273059085756L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "building_id")
	private BuildingModel building;

	@Column(name = "room_number", length = 30)
	private String room_number;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "status")
	private long status;

	public RoomModel() {
		super();
	}

	public RoomModel(long id) {
		super();
		this.id = id;
	}

	public RoomModel(long id, String room_number) {
		super();
		this.id = id;
		this.room_number = room_number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BuildingModel getBuilding() {
		return building;
	}

	public void setBuilding(BuildingModel building) {
		this.building = building;
	}

	public String getRoom_number() {
		return room_number;
	}

	public void setRoom_number(String room_number) {
		this.room_number = room_number;
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

}
