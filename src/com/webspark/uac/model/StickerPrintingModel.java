package com.webspark.uac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;

@Entity
@Table(name = SConstants.tb_names.I_STICKER_PRINTING)
public class StickerPrintingModel implements Serializable {

	private static final long serialVersionUID = 5180170079562960641L;

	public StickerPrintingModel() {
		super();
	}

	public StickerPrintingModel(long id) {
		super();
		this.id = id;
	}

	public StickerPrintingModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 100)
	private String name;
	
	@Column(name = "item_id", columnDefinition="bigint default 0", nullable = false)
	private long item_id;

	@Column(name = "content", length = 5000)
	private String content;
	
	@Column(name = "sub_content", length = 5000)
	private String sub_content;

	@Column(name = "organization_id")
	private long organization_id;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "i_sticker_link", joinColumns = { @JoinColumn(name = "id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<StickerPrintingDetailsModel> sticker_list = new ArrayList<StickerPrintingDetailsModel>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(long organization_id) {
		this.organization_id = organization_id;
	}

	public String getSub_content() {
		return sub_content;
	}

	public void setSub_content(String sub_content) {
		this.sub_content = sub_content;
	}

	public List<StickerPrintingDetailsModel> getSticker_list() {
		return sticker_list;
	}

	public void setSticker_list(List<StickerPrintingDetailsModel> sticker_list) {
		this.sticker_list = sticker_list;
	}

	public long getItem_id() {
		return item_id;
	}

	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}
	
	
}
