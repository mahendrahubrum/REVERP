package com.webspark.uac.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.EmployeeDocumentDao;
import com.webspark.uac.dao.EmployeeDocumentMapDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.EmployeeDocumentCategoryModel;
import com.webspark.uac.model.EmployeeDocumentModel;

/**
 * @author sangeeth
 * @date 05-Nov-2015
 * @Project REVERP
 */


@SuppressWarnings("serial")
public class UserDocumentUploadPanel extends SContainerPanel {

	SVerticalLayout mainVertical;
	SFormLayout mainLayout;
	SComboField userCombo;
	
	SButton createNewButton;
	SButton saveButton;
	
	EmployeeDocumentMapDao dao;
	SimpleDateFormat sdf;
	
	public static String DIR = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/EmployeeDocuments/".trim();
	

	@SuppressWarnings("rawtypes")
	public UserDocumentUploadPanel() {
		
		sdf = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
		dao=new EmployeeDocumentMapDao();
		setSize(750, 460);
		mainLayout=new SFormLayout();
		mainLayout.setSpacing(true);
		mainVertical=new SVerticalLayout();
		mainVertical.setSpacing(true);
		mainVertical.setMargin(true);
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Create New");
		
		saveButton = new SButton(getPropertyName("save"), 100, 25);
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		saveButton.setStyleName("savebtnStyle");
		SHorizontalLayout buttonLayout=new SHorizontalLayout();
		buttonLayout.setId("0");
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(saveButton);
		
		SHorizontalLayout createLayout=new SHorizontalLayout("User");
		createLayout.setSpacing(true);
		
		try {
			userCombo=new SComboField(null, 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), isSuperAdmin()), "id", "first_name", true, "Select");
			
			createLayout.addComponent(userCombo);
			createLayout.addComponent(createNewButton);
			createLayout.setId("0");
			mainLayout.addComponent(createLayout);
			loadDocumentLayouts();
			mainVertical.addComponent(mainLayout);
			mainVertical.addComponent(buttonLayout);
			mainVertical.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			setContent(mainVertical);
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					userCombo.setValue(null);
				}
			});
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						userCombo.setComponentError(null);
						if(userCombo.getValue()!=null && !userCombo.getValue().toString().equals("")){
							List<EmployeeDocumentModel> map=new ArrayList<EmployeeDocumentModel>();
							Iterator cmpItr=mainLayout.iterator();
							while (cmpItr.hasNext()) {
								SHorizontalLayout horizontalLayout = (SHorizontalLayout) cmpItr.next();
								if(horizontalLayout.getId().equals("0"))
									continue;
								else{
									EmployeeDocumentCategoryModel docCat=new EmployeeDocumentDao().getEmployeeDocumentModel(toLong(horizontalLayout.getId()));
									EmployeeDocumentModel mdl=null;
									Iterator layItr=horizontalLayout.iterator();
									int count=0;
									while (layItr.hasNext()) {
										Component component = (Component) layItr.next();
										String fileName="";
										count++;
										if(count==1){
											SDateField dateField = (SDateField)horizontalLayout.getComponent(horizontalLayout.getComponentIndex(component));
											if(!dateField.getId().equals("0"))
												mdl=dao.getEmployeeDocumentModel(toLong(dateField.getId().trim()));
											if(mdl==null)
												mdl=new EmployeeDocumentModel();
											
											mdl.setExpiry(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
										}
										else if(count==3){
											SVerticalLayout verticalLayout = (SVerticalLayout)horizontalLayout.getComponent(horizontalLayout.getComponentIndex(component));
											Iterator vitr=verticalLayout.iterator();
//											int ct=1;
											while (vitr.hasNext()) {
												SHorizontalLayout hLayout = (SHorizontalLayout) vitr.next();
												int c=0;
												Iterator hitr=hLayout.iterator();
												while (hitr.hasNext()) {
													c++;
													if(c==1){
														SButtonLink link = (SButtonLink) hitr.next();
														String documentName=link.getCaption()+"_"+getFileName();
														fileName+=documentName+",";
														saveFile(documentName, link.getId());
													}
													else
														break;
													
												}
//												ct++;
											}
											mdl.setDocument(new EmployeeDocumentCategoryModel(docCat.getId()));
											mdl.setEmployee_id((Long)userCombo.getValue());
											mdl.setFilename(fileName);
											mdl.setOffice_id(getOfficeID());
											map.add(mdl);
										}
										else
											continue;
									}
								}
							}
							dao.update(map);
							Notification.show(getPropertyName("update_success"), Type.WARNING_MESSAGE);
							Object obj=userCombo.getValue();
							userCombo.setValue(null);
							userCombo.setValue(obj);
						}
						else
							setRequiredError(userCombo, getPropertyName("invalid_selection"), true);
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			userCombo.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						List removeList=new ArrayList();
						Iterator cmpItr=mainLayout.iterator();
						while (cmpItr.hasNext()) {
							SHorizontalLayout horizontalLayout = (SHorizontalLayout) cmpItr.next();
							if(!horizontalLayout.getId().equals("0"))
								removeList.add(horizontalLayout);
						}
						cmpItr=removeList.iterator();
						while (cmpItr.hasNext()) {
							Component component = (Component) cmpItr.next();
							mainLayout.removeComponent(component);
						}
						if(userCombo.getValue()!=null && !userCombo.getValue().toString().equals("")){
							loadDocumentLayouts();
						}
						else{
							loadDocumentLayouts();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("rawtypes")
	public void loadDocumentLayouts(){
		try {
			List documentList=new ArrayList();
			documentList=new EmployeeDocumentDao().getAllDocuments(getOrganizationID());
			if(documentList.size()>0){
				Iterator docItr=documentList.iterator();
				while (docItr.hasNext()) {
					final EmployeeDocumentCategoryModel docCat = (EmployeeDocumentCategoryModel) docItr.next();
					
					// Main Horizontal Layout for Each Document Category 
					SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout(docCat.getName());
					mainHorizontalLayout.setId(docCat.getId()+"");
					mainHorizontalLayout.setSpacing(true);
					mainHorizontalLayout.setMargin(true);
					mainHorizontalLayout.setStyleName("po_border");
					long id=0;
					String fileNames="";
					Date date=getWorkingDate();
					if(userCombo.getValue()!=null && !userCombo.getValue().toString().equals("")){
						EmployeeDocumentModel empDoc=dao.getEmployeeDocumentModel((Long)userCombo.getValue(), docCat.getId(), getOfficeID());
						if(empDoc!=null){
							id=empDoc.getId();
							fileNames=empDoc.getFilename();
							date=empDoc.getExpiry();
						}
					}
					SDateField dateField=new SDateField(getPropertyName("expiry_date"), 100, getDateFormat(), date);
					dateField.setId(id+"");
					final SFileUploder documentUploader = new SFileUploder();
					SFileUpload documentUpload = new SFileUpload(null, documentUploader);
					documentUpload.setImmediate(true);
					documentUpload.setButtonCaption("Attach Documents");
					
					final SVerticalLayout mainDocumentLayout = new SVerticalLayout();
					mainDocumentLayout.setSpacing(true);
					mainDocumentLayout.setStyleName("documentAttachlayout");
					
					if(fileNames.toString().trim().length()>5){
						String name[]=fileNames.split(",");
						String fileName="";
						if(name.length>0){
							for(int i=0;i<name.length;i++){
								fileName=name[i];
								File file=new File(DIR.trim()+fileName);
								if(file.exists()&& !file.isDirectory()){
									
									final SHorizontalLayout documentLayout=new SHorizontalLayout();
									documentLayout.setSpacing(true);
									int count=1;
									Iterator itr=mainDocumentLayout.iterator();
									while (itr.hasNext()) {
										SHorizontalLayout obj = (SHorizontalLayout) itr.next();
										if(obj!=null)
											count++;
									}
									
									SButtonLink fileNameLink = new SButtonLink();
									fileNameLink.setId(file.getAbsolutePath());
									FileResource fileResource = new FileResource(file.getAbsoluteFile());
									FileDownloader downloader = new FileDownloader(fileResource);
									downloader.extend(fileNameLink);
									fileNameLink.setImmediate(true);
									fileNameLink.setCaption(docCat.getName()+"_"+count);
									
									final SButton removeButton=new SButton();
									removeButton.setDescription(getPropertyName("delete"));
									removeButton.setPrimaryStyleName("removeItemButtonStyle");
									removeButton.setId(file.getAbsolutePath());
									
									removeButton.addClickListener(new ClickListener() {
										
										@Override
										public void buttonClick(ClickEvent event) {
											
											ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
												public void onClose(ConfirmDialog dialog) {
													if (dialog.isConfirmed()) {
														try {
															File file=new File(removeButton.getId());
															if(file.exists() && !file.isDirectory())
																file.delete();
															mainDocumentLayout.removeComponent(documentLayout);
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												}
											});
										}
									});
									
									documentLayout.addComponent(fileNameLink);
									documentLayout.addComponent(removeButton);
									documentLayout.setId(removeButton.getId());
									
									mainDocumentLayout.addComponent(documentLayout);
								}
							}
						}
					}
					
					documentUpload.addSucceededListener(new SucceededListener() {

						@Override
						public void uploadSucceeded(SucceededEvent event) {
							try{
								if (documentUploader.getFile() != null) {
									
									final SHorizontalLayout documentLayout=new SHorizontalLayout();
									documentLayout.setSpacing(true);
									int count=1;
									
									Iterator itr=mainDocumentLayout.iterator();
									while (itr.hasNext()) {
										SHorizontalLayout obj = (SHorizontalLayout) itr.next();
										if(obj!=null)
											count++;
									}
									
									SButtonLink fileNameLink = new SButtonLink();
									fileNameLink.setId(documentUploader.getFile().getAbsolutePath());
									FileResource fileResource = new FileResource(documentUploader.getFile().getAbsoluteFile());
									FileDownloader downloader = new FileDownloader(fileResource);
									downloader.extend(fileNameLink);
									fileNameLink.setImmediate(true);
									fileNameLink.setCaption(docCat.getName()+"_"+count);
									
									final SButton removeButton=new SButton();
									removeButton.setDescription(getPropertyName("delete"));
									removeButton.setPrimaryStyleName("removeItemButtonStyle");
									removeButton.setId(documentUploader.getFile().getAbsolutePath());
									
									removeButton.addClickListener(new ClickListener() {
										
										@Override
										public void buttonClick(ClickEvent event) {
											
											ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
												public void onClose(ConfirmDialog dialog) {
													if (dialog.isConfirmed()) {
														try {
															File file=new File(removeButton.getId());
															if(file.exists() && !file.isDirectory())
																file.delete();
															mainDocumentLayout.removeComponent(documentLayout);
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												}
											});
										}
									});
									
									documentLayout.addComponent(fileNameLink);
									documentLayout.addComponent(removeButton);
									documentLayout.setId(removeButton.getId());
									
									mainDocumentLayout.addComponent(documentLayout);
									
								}
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
					});
					
					mainHorizontalLayout.addComponent(dateField);
					mainHorizontalLayout.addComponent(documentUpload);
					mainHorizontalLayout.addComponent(mainDocumentLayout);
					mainHorizontalLayout.setComponentAlignment(documentUpload, Alignment.MIDDLE_CENTER);
					mainLayout.addComponent(mainHorizontalLayout);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getFileName() {
		String fileName = "";
		Calendar calendar = Calendar.getInstance();
		fileName = String.valueOf(sdf.format(calendar.getTime())).trim();
		return fileName;
	}
	
	
	public void saveFile(String fileName, String orginalFile) {

		try {
			try {
				File tempFile=new File(orginalFile);
				File newFile=new File(DIR.trim()+fileName);
				if(tempFile.exists() && !tempFile.isDirectory())
					tempFile.renameTo(newFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}
