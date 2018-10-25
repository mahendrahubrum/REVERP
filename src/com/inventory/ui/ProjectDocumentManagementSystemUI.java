package com.inventory.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.DocumentAccessDao;
import com.inventory.model.DocumentAccessDetailsModel;
import com.inventory.model.DocumentAccessModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class ProjectDocumentManagementSystemUI extends SparkLogic{

	SPanel mainPanel;
	SVerticalLayout mainLayout;
	SHorizontalLayout topLayout;
	SGridLayout gridLayout;
	SHorizontalLayout hlay;
	SFileUpload fileUpload;
	SFileUploder fileUploader;
	SButton backButton;
	SButton createFolderButton;
	STextField addressField;
	FileDownloader downloader;
	
	int PROJECT=0;
	public String DIR=VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/Uploads";
	boolean isBaseFolder=true;
	
	SFormLayout popLay;
	SWindow popWindow;
	STextField folderNameField;
	SButton popDoneBtn;
	ClickListener clickListener;
	ClickListener imageClickListener;
	ClickListener propertiesListener;
//	LayoutClickListener imageClickListener;
	SPopupView pop;
	SVerticalLayout popContentLay;
	
	SFormLayout accessLay;
	SRadioButton accessTypeButton;
	SComboField officComboField;
	SListSelect mainList;
	SButton addButton;
	SButton editButton;
	SButton saveAccessButton;
	
	STable table;
	
	static String TBC_ID = "Id";
	static String TBC_NAME = "Name";
	static String TBC_DOWNLOAD = "Download";
	static String TBC_DELETE = "Delete";
	static String TBC_VIEW = "View";
	
	String[] allHeaders;
	String[] reqHeaders;
	
	SCheckBox viewBox;
	SCheckBox deleteBox;
	SCheckBox downloadBox;
	
	DocumentAccessDao dao;
	UserManagementDao userDao;
	
	@Override
	public SPanel getGUI() {
		setSize(1100, 700);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		try {
			dao=new DocumentAccessDao(); 
			userDao=new UserManagementDao();
			
			allHeaders = new String[] { TBC_ID, TBC_NAME,TBC_VIEW, TBC_DOWNLOAD,TBC_DELETE};
			reqHeaders = new String[] { TBC_NAME,TBC_VIEW, TBC_DOWNLOAD,TBC_DELETE};
					
			DIR=VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/Uploads";
			
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			
			topLayout=new SHorizontalLayout();
			topLayout.setSpacing(true);
			topLayout.setStyleName("addressLayoutStyle");
			
			gridLayout=new SGridLayout();
			gridLayout.setSpacing(true);
			gridLayout.setColumns(8);
			gridLayout.setRows(500);
			hlay=new SHorizontalLayout();
			
			fileUploader = new SFileUploder();
			fileUpload = new SFileUpload(null, fileUploader);
			fileUpload.setButtonCaption(null);
			fileUpload.setImmediate(true);
			fileUpload.setPrimaryStyleName("uploadButtonStyle");
			fileUpload.setDescription("Upload");
//			fileUpload.setHeight("35");
			
			gridLayout.setColumnExpandRatio(0, 1.5f);
			gridLayout.setColumnExpandRatio(1, 1.5f);
			gridLayout.setColumnExpandRatio(2, 1.5f);
			gridLayout.setColumnExpandRatio(3, 1.5f);
			gridLayout.setColumnExpandRatio(4, 1.5f);
			gridLayout.setColumnExpandRatio(5, 1.5f);
			
			backButton=new SButton();
//			backButton.setHeight("35");
//			backButton.setWidth("55");
			backButton.setPrimaryStyleName("backButtonStyle");
			backButton.setDescription("Back");
			
			createFolderButton=new SButton();
			createFolderButton.setPrimaryStyleName("createFolderButtonStyle");
			createFolderButton.setDescription("New Folder");
			
			addressField=new STextField();
			addressField.setStyleName("address_textfield_style");
			addressField.setWidth("800");
			addressField.setReadOnly(true);
			addressField.setDescription("Path");
			addressField.setNewValue("");
			
			popContentLay=new SVerticalLayout();
			pop=new SPopupView(null,popContentLay);
			
			topLayout.addComponent(backButton);
			topLayout.addComponent(createFolderButton);
			topLayout.addComponent(fileUpload);
			topLayout.addComponent(pop);
			topLayout.addComponent(addressField);
			mainLayout.addComponent(topLayout);
			mainLayout.setComponentAlignment(topLayout, Alignment.MIDDLE_LEFT);
			mainLayout.addComponent(gridLayout);
			mainPanel.setContent(mainLayout);
			
			popWindow=new SWindow();
			popWindow.setModal(true);
			popDoneBtn=new SButton("Create");
			popDoneBtn.setClickShortcut(KeyCode.ENTER, null);
			folderNameField=new STextField("Folder Name");
			popLay=new SFormLayout();
			popLay.setMargin(true);
			popLay.setSpacing(true);
			popLay.addComponent(folderNameField);
			popLay.addComponent(popDoneBtn);
			popWindow.center();
			
			accessTypeButton = new SRadioButton("Access Type",200,
					Arrays.asList(new KeyValue(1, "Organization"),
							new KeyValue(2, "Office"), new KeyValue(3, "Users")),
					"intKey", "value");
			accessTypeButton.setValue(3);
			accessTypeButton.setVisible(false);
			accessTypeButton.setHorizontal(true);
			
			mainList=new SListSelect(null,200,100,userDao.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),"id","first_name");
			mainList.setMultiSelect(true);
			
			officComboField=new SComboField("Office",150,new OfficeDao().getAllOfficeName(),"id","name");
			officComboField.setValue(getOfficeID());
//			officComboField.setVisible(false);
			
			addButton=new SButton("Add");
			editButton=new SButton("Update");
			saveAccessButton=new SButton("Save");
			accessLay=new  SFormLayout();
			accessLay.setMargin(true);
			accessLay.setSpacing(true);
			
			table=new STable(null,700,300);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,
					Align.CENTER);
			table.addContainerProperty(TBC_NAME, String.class, null,
					TBC_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_VIEW, String.class, null,
					getPropertyName("View"), null, Align.LEFT);
			table.addContainerProperty(TBC_DELETE, String.class, null,
					getPropertyName("delete"), null, Align.LEFT);
			table.addContainerProperty(TBC_DOWNLOAD, String.class, null,
					getPropertyName("download"), null, Align.LEFT);
			table.setVisibleColumns(reqHeaders);
			table.setSelectable(true);
			
			deleteBox=new SCheckBox("Delete");
			downloadBox=new SCheckBox("Download");
			viewBox=new SCheckBox("View");
			viewBox.setValue(true);
			viewBox.setReadOnly(true);
			
			SHorizontalLayout listLay=new SHorizontalLayout();
			listLay.setSpacing(true);
			listLay.addComponent(officComboField);
			listLay.addComponent(mainList);
			listLay.addComponent(viewBox);
			listLay.addComponent(downloadBox);
			listLay.addComponent(deleteBox);
			listLay.addComponent(addButton);
			listLay.addComponent(editButton);
			editButton.setVisible(false);
			
			accessLay.addComponent(accessTypeButton);
			accessLay.addComponent(table);
			accessLay.addComponent(listLay);
			accessLay.addComponent(saveAccessButton);
			
			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					if (table.getValue() != null) {
							table.removeItem(table.getValue());
				}
				}
			});
			
			accessTypeButton.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
//					officComboField.setVisible(false);
					SCollectionContainer cont;
					
					try {
						if ((Integer) accessTypeButton.getValue() == 1) {
							cont = SCollectionContainer.setList(
									new OrganizationDao().getAllOrganizations(),
									"id");
							mainList.setContainerDataSource(cont);
							mainList.setItemCaptionPropertyId("name");
						} else if ((Integer) accessTypeButton.getValue() == 2) {
							cont = SCollectionContainer.setList(
									new OfficeDao().getAllOfficeName(), "id");
							mainList.setContainerDataSource(cont);
							mainList.setItemCaptionPropertyId("name");
						} else {
//							officComboField.setVisible(true);
							officComboField.setValue(getOfficeID());
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			officComboField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(officComboField.getValue()!=null){
						try {
							SCollectionContainer cont = SCollectionContainer.setList(
									userDao
											.getUsersWithFullNameAndCodeUnderOffice((Long) officComboField
													.getValue()), "id");
							mainList.setContainerDataSource(cont);
							mainList.setItemCaptionPropertyId("first_name");
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			});
			
			addButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if (mainList.getValue() != null) {
						try {
							Set mainSet = (Set) mainList.getValue();
							String view="Y";
							String download="Y";
							String delete="Y";
							
							if(!viewBox.getValue())
								view="N";
							if(!downloadBox.getValue())
								download="N";
							if(!deleteBox.getValue())
								delete="N";
							
							Iterator iter =mainSet.iterator();
							
							table.setVisibleColumns(allHeaders);
							while (iter.hasNext()) {
								UserModel mdl=userDao.getUser((Long)iter.next());
								
								if(!table.getItemIds().contains(mdl.getId())){
								
								table.addItem(
										new Object[] {
												mdl.getId(),
												mdl.getFirst_name() + " "
														+ mdl.getMiddle_name()
														+ " "
														+ mdl.getLast_name()
														+ " ( "
														+ mdl.getEmploy_code()
														+ " )", view, download,
												delete }, mdl.getId());
								}
							}
							table.setVisibleColumns(reqHeaders);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						
					try {
						Item item=table.getItem(table.getValue());
						UserModel mdl=userDao.getUser((Long)item.getItemProperty(TBC_ID).getValue());
						officComboField.setValue(mdl.getLoginId().getOffice().getId());
						Set set=new LinkedHashSet();
						set.add(mdl.getId());
						mainList.setReadOnly(false);
						mainList.setValue(set);
						
						if(item.getItemProperty(TBC_VIEW).getValue().toString().equalsIgnoreCase("Y"))
							viewBox.setValue(true);
						else
							viewBox.setValue(false);
				
						if(item.getItemProperty(TBC_DOWNLOAD).getValue().toString().equalsIgnoreCase("Y"))
							downloadBox.setValue(true);
						else
							downloadBox.setValue(false);
						
						if(item.getItemProperty(TBC_DELETE).getValue().toString().equalsIgnoreCase("Y"))
							deleteBox.setValue(true);
						else
							deleteBox.setValue(false);
						
						addButton.setVisible(false);
						editButton.setVisible(true);

						mainList.setReadOnly(true);
						officComboField.setReadOnly(true);
						
					} catch (Exception e) {
						e.printStackTrace();
					}

					}else{
						addButton.setVisible(true);
						editButton.setVisible(false);
						
						downloadBox.setValue(false);
						deleteBox.setValue(false);
						
						mainList.setReadOnly(false);
						officComboField.setReadOnly(false);
					}
				}
			});
			
			editButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if (mainList.getValue() != null) {
						try {
							Set mainSet = (Set) mainList.getValue();
							String view="Y";
							String download="Y";
							String delete="Y";
							
							if(!viewBox.getValue())
								view="N";
							if(!downloadBox.getValue())
								download="N";
							if(!deleteBox.getValue())
								delete="N";
							
							Item item=table.getItem(table.getValue());
							
							Iterator iter =mainSet.iterator();
							
							table.setVisibleColumns(allHeaders);
							UserModel mdl=userDao.getUser((Long)iter.next());
							
							item.getItemProperty(TBC_ID).setValue(mdl.getId());
							item.getItemProperty(TBC_NAME).setValue(mdl.getFirst_name() + " "+ mdl.getMiddle_name()
									+ " "+ mdl.getLast_name()+ " ( "+ mdl.getEmploy_code()+ " )");
							item.getItemProperty(TBC_VIEW).setValue(view);
							item.getItemProperty(TBC_DELETE).setValue(delete);
							item.getItemProperty(TBC_DOWNLOAD).setValue(download);
							
							table.setVisibleColumns(reqHeaders);
							
							table.setValue(null);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			saveAccessButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						DocumentAccessModel mdl=dao.getAccessModel(popWindow.getId());
						List<DocumentAccessDetailsModel>detMdlList=new ArrayList<DocumentAccessDetailsModel>();
						if(table.getItemIds()!=null&&table.getItemIds().size()>0){
//							DocumentAccessDetailsModel detMdl=new DocumentAccessDetailsModel();
							Iterator iter=table.getItemIds().iterator();
							
							while (iter.hasNext()) {
								Item item=table.getItem((Object) iter.next());
								DocumentAccessDetailsModel detMdl=new DocumentAccessDetailsModel();
								detMdl.setUser(toLong(item.getItemProperty(TBC_ID).getValue().toString()));
								detMdl.setDelete(item.getItemProperty(TBC_DELETE).getValue().toString().charAt(0));
								detMdl.setDownload(item.getItemProperty(TBC_DOWNLOAD).getValue().toString().charAt(0));
								detMdl.setView(item.getItemProperty(TBC_VIEW).getValue().toString().charAt(0));
								detMdlList.add(detMdl);
							}
							mdl.setDoc_access_list(detMdlList);
							
							
						}else{
							detMdlList=null;
							mdl.setDoc_access_list(detMdlList);
						}
						dao.update(mdl);
						SNotification.show("Saved Successfully");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			
			backButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						String address=addressField.getValue();
						if(address.contains("/"))
							address=address.substring(0, address.lastIndexOf('/')).trim();
						setAddress(address);
						loadData();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			createFolderButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().addWindow(popWindow);
					popWindow.setCaption("New Folder");
					popWindow.setWidth("300px");
					popWindow.setHeight("180px");
					popWindow.setContent(popLay);
					popWindow.center();
					folderNameField.focus();
				}
			});
			
			popDoneBtn.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					folderNameField.setComponentError(null);
					if(folderNameField.getValue()!=null&&folderNameField.getValue().toString().trim().length()>0){
					if(!folderExists(folderNameField.getValue())){
					createFolder();
					
					DocumentAccessModel mdl=new DocumentAccessModel();
					mdl.setCreator(new UserModel(getUserID()));
					mdl.setFileName(folderNameField.getValue());
					mdl.setFilePath(DIR+getAddress()+"/"+folderNameField.getValue());
					mdl.setDoc_access_list(null);
					mdl.setFileType(1);
					mdl.setOfficeId(getOfficeID());
					
					try {
						dao.save(mdl);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					getUI().removeWindow(popWindow);
					loadData();
					folderNameField.setValue("");
					}else{
						setRequiredError(folderNameField, "Name Already Exists", true);
					}
					}else{
						setRequiredError(folderNameField, "Enter a name", true);
					}
					
				}
			});
			
			clickListener=new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					loadAddress("/"+event.getButton().getId());
					loadData();
				}
			};
			
			propertiesListener=new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getComponent()!=null){
						getUI().addWindow(popWindow);
						popWindow.setCaption("Access");
						popWindow.setWidth("750px");
						popWindow.setHeight("550px");
						popWindow.setContent(accessLay);
						popWindow.center();
						popWindow.setId(event.getComponent().getId());
						
						try {
							DocumentAccessModel mdl=dao.getAccessModel(event.getComponent().getId());
							DocumentAccessDetailsModel detMdl;
							Iterator iter=mdl.getDoc_access_list().iterator();
							UserModel userMdl=null;
							
							table.removeAllItems();
							table.setVisibleColumns(allHeaders);
							while (iter.hasNext()) {
								detMdl = (DocumentAccessDetailsModel) iter.next();
								userMdl=userDao.getUser(detMdl.getUser());
								table.addItem(
										new Object[] {
												userMdl.getId(),
												userMdl.getFirst_name() + " "
														+ userMdl.getMiddle_name()
														+ " "
														+ userMdl.getLast_name()
														+ " ( "
														+ userMdl.getEmploy_code()
														+ " )", detMdl.getView()+"", detMdl.getDownload()+"",
														detMdl.getDelete()+""}, userMdl.getId());
							}
							table.setVisibleColumns(reqHeaders);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			imageClickListener=new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getComponent()!=null){
						SImage image=new SImage(null,new FileResource(new File(event.getComponent().getId())));
						image.setWidth("600");
						image.setHeight("500");
						popContentLay.removeAllComponents();
						popContentLay.addComponent(image);
						pop.setPopupVisible(true);
					}
				}
			};
//			imageClickListener=new LayoutClickListener() {
//				
//				@Override
//				public void layoutClick(LayoutClickEvent event) {
//					if(event.getComponent()!=null){
//						SImage image=new SImage(null,new FileResource(new File(event.getComponent().getId())));
//						image.setWidth("600");
//						image.setHeight("500");
//						popContentLay.removeAllComponents();
//						popContentLay.addComponent(image);
//						pop.setPopupVisible(true);
//					}
//				}
//			};

			
			fileUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					try{
						if (fileUploader.getFile() != null	&& fileUploader.getFile().exists()) {
							hlay.setId(fileUploader.getFile().getAbsolutePath());
							String name=fileUploader.getFile().getName();
							String dir=DIR+getAddress();
							if(dir.length()>0 && hlay.getId().length()>0){
								try{
									File sou=new File(hlay.getId());
									File dest=new File(dir+"/"+name);
									if(!dest.exists())
										FileUtils.moveFile(sou, dest);
									else
										SNotification.show("File Exists", Type.TRAY_NOTIFICATION);
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
							hlay.setId("");
							loadData();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			});
			loadAddress("/");
			loadData();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	
	protected boolean folderExists(String name) {
		
		File folder=new File(DIR+getAddress());
		
		File[] listOfFiles = folder.listFiles();
		for(File file:listOfFiles){
			if(name.equalsIgnoreCase(file.getName()))
					return true;
		}
		return false;
	}


	protected void createFolder() {
		File folder=new File(DIR+getAddress()+"/"+folderNameField.getValue().toString());
		folder.mkdir();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadData(){
		
			
			gridLayout.removeAllComponents();
			
			int col = 0;
			int row = 0;
			File projectFile = new File(DIR+getAddress());
		
		if(projectFile.exists()){
			File[] listOfFiles = projectFile.listFiles();
			List fileList=new ArrayList(Arrays.asList(listOfFiles));
			Collections.sort(fileList, new Comparator<File>() {
			    public int compare(File f1, File f2) {
			        return f1.getName().compareTo(f2.getName());
			    }
			});
			
			DocumentAccessModel accessMdl;
			List accessUserList=null;
			
			File file;
				for (int i = 0; i < fileList.size(); i++) {
					try {
					file = (File) fileList.get(i);
					
					final SHorizontalLayout lay = new SHorizontalLayout();
					lay.setSpacing(false);
					final SVerticalLayout verlay = new SVerticalLayout();
					
					SVerticalLayout btnLay = new SVerticalLayout();
					btnLay.setSpacing(true);

					SButton closeButton = new SButton();
					closeButton
							.setPrimaryStyleName("deleteFileButtonStyle");
					
					if (file.isDirectory()) {
						
						accessMdl=dao.getAccessModel(file.getPath());
						
						if(accessMdl.getOfficeId()==getOfficeID()){
						
//						if(accessMdl!=null)
							accessUserList=dao.getAccessUserIds(accessMdl.getId(),"view");
						
						if (accessMdl.getCreator().getId() == getUserID()||accessUserList==null||accessUserList.size()<=0
								|| accessUserList.contains(getUserID())) {

							SButton propertiesButton = new SButton();
							propertiesButton
									.setPrimaryStyleName("propertiesButtonStyle");
							propertiesButton
									.addClickListener(propertiesListener);
							propertiesButton.setId(file.getPath());

							SButton button = new SButton();
							button.setPrimaryStyleName("folderButtonStyle");
							button.setHeight("80");
							button.setId(file.getName());
							SHTMLLabel label = new SHTMLLabel(null,"<font size='2' color='black'><center>"
											+ file.getName()+ "</center></font>");
							label.setStyleName("newlabel");
							label.setWidth("100px");
							verlay.addComponent(button);
							verlay.setComponentAlignment(button,
									Alignment.TOP_CENTER);
							verlay.setId(file.getAbsolutePath());
							verlay.addComponent(label);
							verlay.setComponentAlignment(label,
									Alignment.BOTTOM_CENTER);

							button.addClickListener(clickListener);
							lay.addComponent(verlay);
							lay.addComponent(btnLay);

							gridLayout.addComponent(lay, col, row);
							col++;
							if (col >= gridLayout.getColumns()) {
								col = 0;
								row++;
							}
							
							if(dao.getAccessUserIds(accessMdl.getId(),"delete").contains(getUserID())||accessMdl.getCreator().getId() == getUserID())
								btnLay.addComponent(closeButton);
							if(accessMdl.getCreator().getId() == getUserID())
								btnLay.addComponent(propertiesButton);

						}
					}
					} else {

						boolean isImage = false;
						if (file.getName().startsWith("."))
							continue;
						BufferedImage imge = null;
						try {
							imge = ImageIO.read(file);
							if (imge != null) {
								isImage = true;
							} else
								isImage = false;
						} catch (Exception e) {
							e.printStackTrace();
							isImage = false;
						}
					
						SButton button = null;
						SImage image = null;
						String imageName = "images/no_image.png";
						if (isImage) {
							image = new SImage(null, new ThemeResource(
									imageName));
							image.setSource(new FileResource(new File(file
									.getAbsolutePath())));
							image.setWidth("80");
							image.setHeight("80");
							image.setImmediate(true);
							verlay.addComponent(image);
							verlay.setComponentAlignment(image,
									Alignment.TOP_CENTER);
							image.setDescription(file.getName());
							verlay.setId(file.getAbsolutePath());
							// lay.addLayoutClickListener(imageClickListener);

						} else {
							button = new SButton();
							button.setPrimaryStyleName("fileButtonStyle");
							button.setHeight("80");
							verlay.addComponent(button);
							verlay.setComponentAlignment(button,
									Alignment.TOP_CENTER);
							verlay.setId(file.getAbsolutePath());
							button.setDescription(file.getName());
						}

						lay.addComponent(verlay);
						lay.addComponent(btnLay);
						
						SButton downloadButton = new SButton();
						downloadButton
								.setPrimaryStyleName("downloadFileButtonStyle");

						SButton viewButton = new SButton();
						viewButton.setPrimaryStyleName("viewFileButtonStyle");
						viewButton.addClickListener(imageClickListener);

						downloader = new FileDownloader(new FileResource(file));
						downloader.extend(downloadButton);
						
						viewButton.setId(verlay.getId());
						
						btnLay.addComponent(viewButton);
						btnLay.addComponent(downloadButton);
						btnLay.addComponent(closeButton);
						
						SHTMLLabel label = new SHTMLLabel(null,
								"<font size='2' color='black'><center>"
										+ file.getName() + "</center></font>");
						label.setStyleName("newlabel");
						label.setWidth("100px");
						verlay.addComponent(label);
						verlay.setComponentAlignment(label,
								Alignment.BOTTOM_CENTER);
						gridLayout.addComponent(lay, col, row);
						col++;
						if (col >= gridLayout.getColumns()) {
							col = 0;
							row++;
						}
					}
					closeButton.addClickListener(new ClickListener() {

						@SuppressWarnings("static-access")
						@Override
						public void buttonClick(ClickEvent event) {
							ConfirmDialog.show(getUI().getCurrent(),
									"File Will Be Deleted",
									new ConfirmDialog.Listener() {
										public void onClose(
												ConfirmDialog dialog) {
											if (dialog.isConfirmed()) {

												try {
													if (verlay.getId()
															.length() > 0) {
														File file = new File(
																verlay.getId());
														
														if(file.isDirectory())
															dao.delete(file.getPath());
														
														if (file.exists())
															FileUtils.forceDelete(file);
														
														
														Notification
																.show("Deleted Successfully",
																		Type.WARNING_MESSAGE);
														loadData();
													}

												} catch (Exception e) {
													e.printStackTrace();
													SNotification
															.show("Unable To Delete",
																	Type.ERROR_MESSAGE);
												}
											}
										}
									});
						}
					});
					}
					
					catch(Exception e){
						e.printStackTrace();
					}	}
					
		}
	
	}

	
	public void backButtonVisiblity(){
		if(!isBaseFolder)
			backButton.setVisible(true);
		else
			backButton.setVisible(false);
	}
	
	
	public void loadAddress(String address){
		String currAddress=addressField.getValue();
		if(currAddress.equals("/"))
			currAddress="";
		if(address.equals("/"))
			address="";
		addressField.setNewValue(currAddress+address);
	}
	
	public void setAddress(String address){
		addressField.setNewValue(address);
	}
	
	public String getAddress(){
		return addressField.getValue();
	}
	
	@Override
	public Boolean isValid() {
		return null;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}
}