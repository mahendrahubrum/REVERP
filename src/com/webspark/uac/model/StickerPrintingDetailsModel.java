package com.webspark.uac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_STICKER_PRINTING_DETAILS)
public class StickerPrintingDetailsModel implements Serializable {

	private static final long serialVersionUID = 5180170079562960641L;

	public StickerPrintingDetailsModel() {
		super();
	}

	public StickerPrintingDetailsModel(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "specification", length = 300)
	private String specification;

	@Column(name = "details", length = 300)
	private String details;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
	@Override
	public boolean equals(Object obj) {
		StickerPrintingDetailsModel model = (StickerPrintingDetailsModel) obj;
		return this.id == model.getId();
	}

}
