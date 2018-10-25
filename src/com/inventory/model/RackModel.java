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
@Table(name = SConstants.tb_names.I_RACK)
public class RackModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6417797120825054002L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "room_id")
	private RoomModel room;

	@Column(name = "rack_number", length = 30)
	private String rack_number;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "status")
	private long status;

	public RackModel() {
		super();
	}

	public RackModel(long id) {
		super();
		this.id = id;
	}

	public RackModel(long id, String rack_number) {
		super();
		this.id = id;
		this.rack_number = rack_number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public RoomModel getRoom() {
		return room;
	}

	public void setRoom(RoomModel room) {
		this.room = room;
	}

	public String getRack_number() {
		return rack_number;
	}

	public void setRack_number(String rack_number) {
		this.rack_number = rack_number;
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