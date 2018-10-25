package com.webspark.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

/**
 * @Author Jinshad P.T.
 */

@Entity
@Table(name = SConstants.tb_names.S_ID_GENERATOR_VALUES)
public class S_IDValueModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8771391621320498657L;

	@EmbeddedId
	private S_IDValueCompoundKey id_val_comp_key;

	@Column(name = "value")
	private long value;

	public S_IDValueCompoundKey getId_val_comp_key() {
		return id_val_comp_key;
	}

	public void setId_val_comp_key(S_IDValueCompoundKey id_val_comp_key) {
		this.id_val_comp_key = id_val_comp_key;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
