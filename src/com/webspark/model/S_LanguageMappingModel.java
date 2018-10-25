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

@SuppressWarnings("serial")
@Entity
@Table(name=SConstants.tb_names.S_LANGUAGE_MAPPING)
public class S_LanguageMappingModel implements Serializable {

	public S_LanguageMappingModel() {
		
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private long id;
	
	@Column(name="type")
	private long type;
	
	@OneToOne
	@JoinColumn(name="language")
	private S_LanguageModel language;
	
	@Column(name="option_id")
	private long option;
	
	@Column(name="name")
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public S_LanguageModel getLanguage() {
		return language;
	}

	public void setLanguage(S_LanguageModel language) {
		this.language = language;
	}

	public long getOption() {
		return option;
	}

	public void setOption(long option) {
		this.option = option;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
