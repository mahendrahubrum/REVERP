package com.inventory.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.webspark.common.util.SConstants;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 30-Jul-2015
 */

@Entity
@Table(name = SConstants.tb_names.I_DOCUMENT_ACCESS)
public class DocumentAccessModel implements Serializable {

	private static final long serialVersionUID = 2779549824168428514L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "file_type")
	private int fileType;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_path")
	private String filePath;
	
	@OneToOne
	@JoinColumn(name = "creator")
	private UserModel creator;

	@OneToMany(fetch=FetchType.EAGER ,cascade=CascadeType.ALL)
	@JoinTable(name="i_doc_access_link" , joinColumns={@JoinColumn(name="master_id")}, inverseJoinColumns={@JoinColumn(name="details_id")})
	private List<DocumentAccessDetailsModel> doc_access_list=new ArrayList<DocumentAccessDetailsModel>();
	
	@Column(name = "office_id")
	private long officeId;

	public DocumentAccessModel() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public UserModel getCreator() {
		return creator;
	}

	public void setCreator(UserModel creator) {
		this.creator = creator;
	}

	public List<DocumentAccessDetailsModel> getDoc_access_list() {
		return doc_access_list;
	}

	public void setDoc_access_list(List<DocumentAccessDetailsModel> doc_access_list) {
		this.doc_access_list = doc_access_list;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}
}
