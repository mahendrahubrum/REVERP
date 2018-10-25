package com.webspark.uac.ui;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SEncryption;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.dao.LoginOptionMappingDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.EmployeeDocumentMapDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.QualificationDao;
import com.webspark.uac.dao.RoleDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.dao.VisaTypeDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DesignationModel;
import com.webspark.uac.model.QualificationModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_UserRoleModel;
import com.webspark.uac.model.UserModel;
import com.webspark.uac.model.VisaTypeModel;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class UserManagementPanel extends SContainerPanel {

	long id = 0;
	SCollectionContainer bic;

	CommonUtil objUtil;

	SHorizontalLayout horizontalLayout;
	SVerticalLayout verticalLayout;

	SFormLayout contentLeft;
	SFormLayout contentCenter;
	SFormLayout contentRight;
	SFormLayout loginLayout;
	SVerticalLayout rightLayout;
	SimpleDateFormat sdf;
	SCheckBox enableLogin;
//	FileDownloader downloader;
//	FileResource fileResource;
	
	SComboField userCombo, superiorComboField;
	
	STextField user_name;
	STextField employCodeField;

	SPasswordField password;
	SPasswordField confirmpassword;
	SComboField designation;

	SComboField organization;

	SComboField office;
	
	SComboField userRoleCombo;

	STextField firstNameField;
	STextField middle_name;
	STextField last_name;
	SNativeSelect genderSelect;
	SDateField birthDate;
	SNativeSelect maritalStatusSelect;

	SNativeSelect salaryTypeSelect;

	SComboField department;

	SRadioButton joinRadio;
	SRadioButton joinStatusRadio;
	SDateField joinDate;

	SDateField effectiveDate;
	SDateField salaryEffectiveDate;

	SDateField dateField;
	ClickListener listener;
	
	HorizontalLayout buttonLayout = null;

	UserManagementDao umObj;
	EmployeeDocumentMapDao dao;
	LoginOptionMappingDao lomd;
	IDGeneratorSettingsDao idsetDao;


	OfficeDao ofcDao;
	DepartmentDao depDao;

	SButton createNewButton;
	SWindow userWindow;
	SButton replaceUserButton;
	SFormLayout form;
	SComboField usersList;
	SComboField visaCombo;
	private static String IMAGE_DIRECTORY = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/EmployeePhotos/";
	private SButton loadAllButton;
	static int count=0;
	WrappedSession session;
	
	SHorizontalLayout mainImageLayout;
	@SuppressWarnings("rawtypes")
	List imageList;
	SFileUpload imageUpload;
	SFileUploder imageUploader;
	SCheckBox selectBox;
	SButton removeButton;
	
	
	STextField jobTitleField;
	STextField heightField;
	STextField weightField;
	SRadioButton accomodationRadio;
	SRadioButton familyStatusRadio;
	SRadioButton familyCountryRadio;
	SRadioButton ticketRadio;
	SRadioButton familyTicketRadio;
	STextField visaCompanyField;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UserManagementPanel() {
		imageList = new ArrayList();
		mainImageLayout=new SHorizontalLayout();
		mainImageLayout.setSpacing(true);
		mainImageLayout.setStyleName("user_image_border");
		mainImageLayout.setHeight("125");
		imageUploader = new SFileUploder();
		imageUpload = new SFileUpload(null, imageUploader);
		imageUpload.setImmediate(true);
		imageUpload.setButtonCaption("Upload Image");
		removeButton=new SButton("Remove Image");
		SHorizontalLayout attachLayout=new SHorizontalLayout();
		attachLayout.setSpacing(true);
		attachLayout.addComponent(imageUpload);
		attachLayout.addComponent(removeButton);
		
		jobTitleField=new STextField("Job Title", 160);
		heightField=new STextField("Height", 160);
		weightField=new STextField("Weight", 160);
		accomodationRadio=new SRadioButton("Company Accomodation", 160, 
											Arrays.asList(new KeyValue((long)1, "Yes"),new KeyValue((long)2, "No")), 
											"key", "value");
		accomodationRadio.setHorizontal(true);
		accomodationRadio.setValue((long)1);
		
		
		familyStatusRadio=new SRadioButton("Family Status", 160, 
											Arrays.asList(new KeyValue((long)1, "Yes"),new KeyValue((long)2, "No")), 
											"key", "value");
		familyStatusRadio.setHorizontal(true);
		familyStatusRadio.setValue((long)1);
		
		familyCountryRadio=new SRadioButton("Family in Country", 160, 
											Arrays.asList(new KeyValue((long)1, "Yes"),new KeyValue((long)2, "No")), 
											"key", "value");
		familyCountryRadio.setHorizontal(true);
		familyCountryRadio.setValue((long)1);
		
		ticketRadio=new SRadioButton("Ticket Type", 160, 
										Arrays.asList(new KeyValue((long)1, "One Way"),new KeyValue((long)2, "Two Way")), 
										"key", "value");
		ticketRadio.setHorizontal(true);
		ticketRadio.setValue((long)1);
		
		familyTicketRadio=new SRadioButton("Family Ticket", 160, 
										Arrays.asList(new KeyValue((long)1, "Yes"),new KeyValue((long)2, "No")), 
										"key", "value");
		familyTicketRadio.setHorizontal(true);
		familyTicketRadio.setValue((long)1);
		
		visaCompanyField=new STextField("Visa Company", 160);
		
		
		ofcDao = new OfficeDao();
		idsetDao = new IDGeneratorSettingsDao();
		depDao = new DepartmentDao();
		umObj = new UserManagementDao();
		lomd = new LoginOptionMappingDao();
		objUtil = new CommonUtil();
		dao=new EmployeeDocumentMapDao();
		buttonLayout = new HorizontalLayout();
		session = getHttpSession();
		setSize(1000, 620);
		sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		final SButton saveButton = new SButton(getPropertyName("Save"));
		final SButton deleteButton = new SButton(getPropertyName("Delete"));
		final SButton updateButton = new SButton(getPropertyName("Update"));
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		saveButton.setStyleName("savebtnStyle");
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		SPanel pan = new SPanel();
		pan.setSizeFull();
		try {

			horizontalLayout = new SHorizontalLayout();
			verticalLayout = new SVerticalLayout();

			contentLeft = new SFormLayout();
			contentCenter = new SFormLayout();
			contentRight = new SFormLayout();
			loginLayout = new SFormLayout();
			rightLayout=new SVerticalLayout();

			effectiveDate = new SDateField(getPropertyName("effective_date"),100);
			effectiveDate.setValue(getWorkingDate());
			
			salaryEffectiveDate = new SDateField(getPropertyName("salary_effective_date"), 100);
			salaryEffectiveDate.setValue(getWorkingDate());

			salaryTypeSelect = new SNativeSelect(
					getPropertyName("salary_type"), 160,
					SConstants.payroll.salaryTypes, "intKey", "value");

			
			userWindow = new SWindow();
			userWindow.setWidth("350");
			userWindow.setHeight("170");
			usersList = new SComboField("Replace with", 200);
			
			List visaList=new ArrayList();
			visaList.add(0,new VisaTypeModel(0, "None"));
			visaList.addAll(new VisaTypeDao().getActiveVisaTypeModelList(getOfficeID()));
			visaCombo=new SComboField("Visa", 160, visaList, "id", "name", true, "Select");
			visaCombo.setValue((long)0);
			replaceUserButton=new SButton("Replace User");
			form = new SFormLayout();
			form.setSpacing(true);
			form.setMargin(true);
			form.setSizeFull();
			form.addComponent(usersList);
			form.addComponent(replaceUserButton);
			userWindow.setContent(form);
			userWindow.center();
			userWindow.setModal(true);
			
			List testList = umObj.getAllSuperiorLogins(getOfficeID());
			if (testList == null)
				testList = new ArrayList();

			testList.add(0, new S_LoginModel(0, "none"));

			superiorComboField = new SComboField(getPropertyName("superior"),
					160, testList, "id", "login_name");
			superiorComboField.setValue((long) 0);

			organization = new SComboField(getPropertyName("organization"),
					160, new OrganizationDao().getAllOrganizations(), "id",
					"name", true, "Select");

			loadAllButton = new SButton();
			loadAllButton.setStyleName("loadAllBtnStyle");
			loadAllButton.setId("ALL");

			userCombo = new SComboField(null, 160, null, "id", "first_name");
			userCombo.setInputPrompt("--------- Create New ------------");

			employCodeField = new STextField(getPropertyName("Code"), 160,true);

			user_name = new STextField(getPropertyName("login_name"), 160,true);
			enableLogin=new SCheckBox(getPropertyName("enable_login"), false);
			password = new SPasswordField(getPropertyName("password") + " :",
					160);
			confirmpassword = new SPasswordField(
					getPropertyName("confirm_password"), 160);

			designation = new SComboField(getPropertyName("designation"), 160,
					null, "id", "name",true);
			office = new SComboField(getPropertyName("office") + " :", 160,
					null, "id", "name",true);
			
			List qualiList=new ArrayList();
			qualiList.add(0, new QualificationModel(0, "Others"));
			qualiList.addAll(new QualificationDao().getActiveQualificationModelList(getOfficeID()));
			userRoleCombo = new SComboField(getPropertyName("role"), 160,
					new RoleDao().getAllRoles(), "id", "role_name",true);

			designation
					.setInputPrompt("-----------------Select-----------------");
			office.setInputPrompt("-----------------Select-----------------");
			userRoleCombo.setInputPrompt("-----------------Select-----------------");

			firstNameField = new STextField(getPropertyName("first_name"), 160);
			middle_name = new STextField(getPropertyName("middle_name"), 160);
			last_name = new STextField(getPropertyName("last_name"), 160);
			genderSelect = new SNativeSelect(getPropertyName("gender"), 160,
					SConstants.genderOptions, "charKey", "value", true);
			birthDate = new SDateField(getPropertyName("birth_date"));
			
			List list=new ArrayList();
			list=Arrays.asList(new KeyValue((long)1, "Joining"), new KeyValue((long)2, "Re-Joining"));
			joinRadio=new SRadioButton(null, 200, list, "key", "value");
			joinRadio.setValue((long)1);
			joinRadio.setHorizontal(true);
			joinRadio.setImmediate(true);
			joinStatusRadio=new SRadioButton(null, 200, 
								Arrays.asList(new KeyValue((long)1, "After Leave"), new KeyValue((long)2, "After Termination/Resignation")), 
								"key", "value");
			joinStatusRadio.setValue((long)1);
			joinStatusRadio.setHorizontal(true);
			joinStatusRadio.setImmediate(true);
			joinStatusRadio.setVisible(false);
			
			joinDate = new SDateField(getPropertyName("join_date"), 100);
			joinDate.setValue(getWorkingDate());
			maritalStatusSelect = new SNativeSelect(
					getPropertyName("marital_status"), 160,
					SConstants.maritalStatusOptions, "charKey", "value", true);
			department = new SComboField(getPropertyName("department"), 160,
					null, "id", "name", true, "Select");

//			address = new SAddressField(2);

			contentLeft.setMargin(true);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					(getPropertyName("user")));
			salLisrLay.addComponent(userCombo);
			salLisrLay.addComponent(createNewButton);
			salLisrLay.addComponent(loadAllButton);
			contentLeft.addComponent(salLisrLay);
			contentLeft.addComponent(employCodeField);
			contentLeft.addComponent(firstNameField);
			contentLeft.addComponent(middle_name);
			contentLeft.addComponent(last_name);
			contentLeft.addComponent(jobTitleField);
			contentLeft.addComponent(genderSelect);
			contentLeft.addComponent(birthDate);
			contentLeft.addComponent(heightField);
			contentLeft.addComponent(weightField);
			contentLeft.addComponent(maritalStatusSelect);

			STextField lb = new STextField(" ");
			lb.setValue(" ");
			lb.setReadOnly(true);
			contentCenter.addComponent(lb);

			// contentCenter.addComponent(maritalStatusSelect);

			if (getRoleID() == 1) {
				contentCenter.addComponent(organization);
			}

			contentCenter.addComponent(office);
			contentCenter.addComponent(department);
			contentCenter.addComponent(designation);
			contentCenter.addComponent(userRoleCombo);
			contentCenter.addComponent(salaryTypeSelect);
			contentCenter.addComponent(superiorComboField);
			contentCenter.addComponent(accomodationRadio);
			contentCenter.addComponent(familyStatusRadio);
			contentCenter.addComponent(familyCountryRadio);
			contentCenter.addComponent(familyTicketRadio);
			

			salaryTypeSelect.setValue(2);

			STextField lb1 = new STextField("");
			lb1.setValue("");
			lb1.setReadOnly(true);

			contentRight.addComponent(joinRadio);
			contentRight.addComponent(joinStatusRadio);
			contentRight.addComponent(joinDate);
			contentRight.addComponent(effectiveDate);
			contentRight.addComponent(salaryEffectiveDate);
			contentRight.addComponent(visaCombo);
			contentRight.addComponent(visaCompanyField);
			contentRight.addComponent(ticketRadio);
			contentRight.addComponent(enableLogin);
			loginLayout.addComponent(user_name);
			loginLayout.addComponent(password);
			loginLayout.addComponent(confirmpassword);
			loginLayout.setVisible(false);
			
			rightLayout.addComponent(contentRight);
			rightLayout.addComponent(loginLayout);

			horizontalLayout.addComponent(contentLeft);
			horizontalLayout.addComponent(contentCenter);
			horizontalLayout.addComponent(rightLayout);

			horizontalLayout.setSpacing(true);

			verticalLayout.addComponent(horizontalLayout);
			verticalLayout.addComponent(attachLayout);
			verticalLayout.addComponent(mainImageLayout);
			verticalLayout.setMargin(true);
			
			
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);

			verticalLayout.addComponent(buttonLayout);
			verticalLayout.setComponentAlignment(buttonLayout,Alignment.MIDDLE_CENTER);
//			verticalLayout.setComponentAlignment(mainDocumentLayout,Alignment.MIDDLE_CENTER);
			verticalLayout.setSpacing(true);
			deleteButton.setVisible(false);
			updateButton.setVisible(false);
			// contentLeft.setSizeUndefined();
			setContent(verticalLayout);
//			pan.setContent(verticalLayout);
			
			loadOptions(0);
			
			addShortcutListener(new ShortcutListener("Add New User", ShortcutAction.KeyCode.N, new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});

			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			imageUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					try{
						if (imageUploader.getFile() != null) {
							Image file = null;
							try {
								file = ImageIO.read(imageUploader.getFile());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(file!=null){
								selectBox=new SCheckBox();
								SHorizontalLayout uploadLayout=new SHorizontalLayout();
								uploadLayout.addComponent(selectBox);
								SImage image=new SImage();
								image.setWidth("115");
								image.setHeight("100");
								image.setSource(new FileResource(imageUploader.getFile()));
								uploadLayout.addComponent(image);
								uploadLayout.setId(imageUploader.getFile().getAbsolutePath());
								image.setId(imageUploader.getFile().getName());
								mainImageLayout.addComponent(uploadLayout);
								imageList.add(imageUploader.getFile());
							}
							else
								SNotification.show("Attach Image",Type.WARNING_MESSAGE);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			removeButton.addClickListener(new ClickListener() {

				@SuppressWarnings({ "deprecation"})
				@Override
				public void buttonClick(ClickEvent event) {
					if (imageUploader.getFile() != null) 
						imageUploader.deleteFile();
					SHorizontalLayout imageLayout;
					SCheckBox check = null;
					Iterator citr=mainImageLayout.getComponentIterator();
					List removeList=new ArrayList();
					List removeItemList = new ArrayList();
					while(citr.hasNext()){
						Component component=(Component)citr.next();
						imageLayout=(SHorizontalLayout)component;
						check=(SCheckBox)imageLayout.getComponent(0);
						if(check.getValue()){
							removeList.add(imageLayout.getId());
							removeItemList.add(imageLayout);
						}
					}
					if(removeList.size()>0){
						Iterator itr=removeList.iterator();
						while (itr.hasNext()) {
							String name= (String) itr.next();
							File file=new File(name);
							try {
								if(imageList.contains(file))
									imageList.remove(file);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							try {
								if(file.exists())
									file.delete();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					Iterator it=removeItemList.iterator();
					while(it.hasNext()){
						imageLayout=(SHorizontalLayout)it.next();
						mainImageLayout.removeComponent(imageLayout);
					}
				}
			});
			

			enableLogin.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(enableLogin.getValue()!=null){
						if(enableLogin.getValue()){
							loginLayout.setVisible(true);
						}
						else{
							loginLayout.setVisible(false);
						}
					}
					else{
						loginLayout.setVisible(false);
						user_name.setValue("");
						password.setValue("");
						confirmpassword.setValue("");
					}
				}
			});
			
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					userCombo.setValue((long) 0);
				}
			});

			
			loadAllButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (loadAllButton.getId().equals("ALL"))
							loadAllButton.setId("CURRENT");
						else
							loadAllButton.setId("ALL");

						loadOptions((long) 0);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			joinRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					joinStatusRadio.setVisible(false);
					if(joinRadio.getValue()!=null){
						if((Long)joinRadio.getValue()==1){
							joinDate.setCaption("Join Date");
						}
						else if((Long)joinRadio.getValue()==2){
							joinDate.setCaption("Rejoin Date");
							joinStatusRadio.setVisible(true);
						}
					}
					else{
						joinDate.setCaption("Join Date");
						joinStatusRadio.setVisible(false);
					}
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings({ "static-access", "deprecation"})
				public void buttonClick(ClickEvent event) {
					try {

						if (userCombo.getValue() == null || userCombo.getValue().toString().equals("0")) {

							boolean valid=true;
							
							if(enableLogin.getValue()){
								if (isValid() && isNotExist() && isCodeNotExist()){
									valid=true;
								}
								else
									valid=false;
							}
							else{
								if (isValid() && isCodeNotExist()){
									valid=true;
								}
								else
									valid=false;
							}
							
							if (valid) {
								UserModel userObj = new UserModel();
								
								String imageName="";
								List list=new ArrayList();
								Iterator itr=mainImageLayout.getComponentIterator();
								while (itr.hasNext()) {
									Component component = (Component) itr.next();
									SHorizontalLayout itemLayout=(SHorizontalLayout)component;
									SImage image=(SImage)itemLayout.getComponent(1);
									list.add(image.getId());
								}
								imageName=getFileName(list);
								
								userObj.setEmploy_code(employCodeField.getValue());
								userObj.setFirst_name(firstNameField.getValue());
								userObj.setMiddle_name(middle_name.getValue());
								userObj.setLast_name(last_name.getValue());
								userObj.setJob_title(jobTitleField.getValue());
								userObj.setGender((Character) genderSelect.getValue());
								userObj.setBirth_date(objUtil.getSQLDateFromUtilDate(birthDate.getValue()));
								userObj.setHeight(roundNumber(toDouble(heightField.getValue().toString())));
								userObj.setWeight(roundNumber(toDouble(weightField.getValue().toString())));
								userObj.setMarital_status((Character) maritalStatusSelect.getValue());
								userObj.setOffice(new S_OfficeModel((Long) office.getValue()));
								userObj.setDepartment(new DepartmentModel((Long) department.getValue()));
								userObj.setDesignation(new DesignationModel((Long) designation.getValue()));
								userObj.setUser_role(new S_UserRoleModel((Long) userRoleCombo.getValue()));
								userObj.setSalary_type((Integer) salaryTypeSelect.getValue());
								userObj.setSuperior_id((Long) superiorComboField.getValue());
								userObj.setStatus(SConstants.EmployeeStatus.ACTIVE);
								
								if((Long)accomodationRadio.getValue()==1)
									userObj.setCompanyAccomodation(true);
								else
									userObj.setCompanyAccomodation(false);
								
								if((Long)familyStatusRadio.getValue()==1)
									userObj.setFamilyStatus(true);
								else
									userObj.setFamilyStatus(false);
								
								if((Long)familyCountryRadio.getValue()==1)
									userObj.setFamilyCountry(true);
								else
									userObj.setFamilyCountry(false);
								
								if((Long)familyTicketRadio.getValue()==1)
									userObj.setFamilyTicket(true);
								else
									userObj.setFamilyTicket(false);
								
								userObj.setJoinStatus((Long)joinRadio.getValue());
								userObj.setReJoinStatus((Long)joinStatusRadio.getValue());
								userObj.setJoining_date(objUtil.getSQLDateFromUtilDate(joinDate.getValue()));
								userObj.setEffective_date(objUtil.getSQLDateFromUtilDate(effectiveDate.getValue()));
								userObj.setSalary_effective_date(objUtil.getSQLDateFromUtilDate(salaryEffectiveDate.getValue()));
								userObj.setVisaType((Long)visaCombo.getValue());
								userObj.setVisa_company(visaCompanyField.getValue());
								userObj.setTicketStatus((Long)ticketRadio.getValue());
								
								userObj.setLoginEnabled(enableLogin.getValue());
								if(enableLogin.getValue()){
									S_LoginModel log = new S_LoginModel();
									log.setLogin_name(user_name.getValue());
									log.setPassword(SEncryption.encrypt(password.getValue()));
									log.setOffice(new S_OfficeModel((Long) office.getValue()));
									log.setUserType(new S_UserRoleModel((Long) userRoleCombo.getValue()));
									userObj.setLoginId(log);
								}
								else{
									userObj.setLoginId(null);
								}
								
								userObj.setAddress(null);
								userObj.setWork_address(null);
								userObj.setLocal_address(null);
								
								userObj.setUser_image(imageName);
								
								try {
									userObj = umObj.save(userObj);
									saveImageAsPNG(imageName);
									id = userObj.getId();
									loadOptions(id);
									Notification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);
									if(userObj.getLoginId()!=null){
										S_OfficeModel ofc = ofcDao.getOffice(userObj.getOffice().getId());
										idsetDao.createIDGenerators(SConstants.scopes.LOGIN_LEVEL, 
																	ofc.getOrganization().getId(),
																	ofc.getId(), 
																	userObj.getLoginId().getId());
									}
									
								} catch (Exception e) {
									Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			
			organization.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {

						bic = SCollectionContainer.setList(ofcDao
								.getAllOfficeNamesUnderOrg((Long) organization
										.getValue()), "id");
						office.setContainerDataSource(bic);
						office.setItemCaptionPropertyId("name");

						SCollectionContainer bic1 = SCollectionContainer.setList(
								umObj.getAllDesignationsUnderOrg((Long) organization
										.getValue()), "id");
						designation.setContainerDataSource(bic1);
						designation.setItemCaptionPropertyId("name");

						SCollectionContainer bic2 = SCollectionContainer.setList(depDao.getDepartmentsUnderOrg((Long) organization.getValue()), "id");
						department.setContainerDataSource(bic2);
						department.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			organization.setValue(getOrganizationID());

			
			userCombo.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (userCombo.getValue() != null && !userCombo.getValue().toString().equals("0")) {

							saveButton.setVisible(false);
							deleteButton.setVisible(true);
							updateButton.setVisible(true);

							UserModel user = umObj.getUser(Long.parseLong(userCombo.getValue().toString()));
							
							employCodeField.setValue(user.getEmploy_code());
							firstNameField.setValue(user.getFirst_name());
							middle_name.setValue(user.getMiddle_name());
							last_name.setValue(user.getLast_name());
							jobTitleField.setValue(user.getJob_title());
							genderSelect.setValue(user.getGender());
							birthDate.setValue(user.getBirth_date());
							heightField.setValue(roundNumber(user.getHeight())+"'");
							weightField.setValue(roundNumber(user.getWeight())+"'");
							maritalStatusSelect.setValue(user.getMarital_status());
							organization.setValue(user.getOffice().getOrganization().getId());
							office.setValue(user.getOffice().getId());
							department.setValue(user.getDepartment().getId());
							designation.setValue(user.getDesignation().getId());
							userRoleCombo.setValue(user.getUser_role().getId());
							salaryTypeSelect.setValue(user.getSalary_type());
							superiorComboField.setNewValue(user.getSuperior_id());
							
							if(user.isCompanyAccomodation())
								accomodationRadio.setValue((long)1);
							else
								accomodationRadio.setValue((long)2);
							
							if(user.isFamilyStatus())
								familyStatusRadio.setValue((long)1);
							else
								familyStatusRadio.setValue((long)2);
							
							if(user.isFamilyCountry())
								familyCountryRadio.setValue((long)1);
							else
								familyCountryRadio.setValue((long)2);
							
							if(user.isFamilyTicket())
								familyTicketRadio.setValue((long)1);
							else
								familyTicketRadio.setValue((long)2);
							
							joinRadio.setValue(user.getJoinStatus());
							joinStatusRadio.setValue(user.getReJoinStatus());
							joinDate.setValue(user.getJoining_date());
							effectiveDate.setValue(user.getEffective_date());
							salaryEffectiveDate.setValue(user.getSalary_effective_date());
							visaCombo.setValue(user.getVisaType());
							visaCompanyField.setValue(user.getVisa_company());
							ticketRadio.setValue(user.getTicketStatus());
							
							enableLogin.setValue(user.isLoginEnabled());
							if(user.getLoginId()!=null){
								user_name.setValue(user.getLoginId().getLogin_name());
								password.setValue(SEncryption.decrypt(user.getLoginId().getPassword()));
								confirmpassword.setValue(SEncryption.decrypt(user.getLoginId().getPassword()));
								session.setAttribute("userName", user.getLoginId().getLogin_name());
							}
							session.setAttribute("oldCode", user.getEmploy_code());
							String file=user.getUser_image();
							String[] fileArray = file.split(",");
							for (int i = 0; i < fileArray.length; i++) {
								File imageFile=new File(IMAGE_DIRECTORY.trim()+fileArray[i].trim());
								if(imageFile.exists() && !imageFile.isDirectory()){
									SHorizontalLayout uploadLayout=new SHorizontalLayout();
									selectBox=new SCheckBox();
									uploadLayout.addComponent(selectBox);
									SImage image=new SImage();
									image.setWidth("115");
									image.setHeight("100");
									image.setSource(new FileResource(imageFile));
									uploadLayout.addComponent(image);
									uploadLayout.setId(imageFile.getAbsolutePath());
									image.setId(imageFile.getName());
									mainImageLayout.addComponent(uploadLayout);
								}
							}
							setReadOnlyAll();
						}
						else{
							imageList.clear();
							mainImageLayout.removeAllComponents();
							if (imageUploader.getFile() != null)
								imageUploader.deleteFile();
							saveButton.setVisible(true);
							deleteButton.setVisible(false);
							updateButton.setVisible(false);
							setWritableAll();
							
							employCodeField.setValue("");
							firstNameField.setValue("");
							middle_name.setValue("");
							last_name.setValue("");
							jobTitleField.setValue("");
							genderSelect.setValue(null);
							heightField.setValue("0");
							weightField.setValue("0");
							maritalStatusSelect.setValue(null);
							department.setValue(null);
							designation.setValue(null);
							userRoleCombo.setValue(null);
							salaryTypeSelect.setValue(null);
							
							accomodationRadio.setValue(null);
							familyStatusRadio.setValue(null);
							familyCountryRadio.setValue(null);
							familyTicketRadio.setValue(null);
							accomodationRadio.setValue(null);
							joinRadio.setValue(null);
							joinStatusRadio.setValue(null);

							visaCompanyField.setValue("");
							ticketRadio.setValue(null);
							
							enableLogin.setValue(false);
							user_name.setValue("");
							password.setValue("");
							confirmpassword.setValue("");
							session.setAttribute("userName", null);
							session.setAttribute("oldCode", null);
							setDefaultValues();
						}
						removeErrorMsg();
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			deleteButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("static-access")
				public void buttonClick(ClickEvent event) {
					try {
						UserModel mdl = umObj.getUser((Long)userCombo.getValue());
						id = Long.parseLong(userCombo.getValue().toString());
						List userList=new ArrayList();
						if (loadAllButton.getId().equals("ALL"))
							userList = umObj.getUsersWithFullNameAndCodeFromOrgExcept(getOrganizationID(), mdl);
						else 
							userList = umObj.getUsersWithFullNameAndCodeUnderOfficeExcept(getOfficeID(), mdl);
						SCollectionContainer bic=SCollectionContainer.setList(userList, "id");
						usersList.setContainerDataSource(bic);
						usersList.setItemCaptionPropertyId("first_name");
						ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are you sure?",new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										getUI().getCurrent().addWindow(userWindow);
									} 
									catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			replaceUserButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if (usersList.getValue() != null && !usersList.getValue().toString().equals("0")) {
							long id=Long.parseLong(userCombo.getValue().toString());
							UserModel user = umObj.getUser(Long.parseLong(userCombo.getValue().toString()));
							
							String file=user.getUser_image().trim();
							if(file.length()>3){
								String[] fileArray = file.split(",");
								for (int i = 0; i < fileArray.length; i++) {
									File imageFile=new File(IMAGE_DIRECTORY.trim()+fileArray[i].trim());
									try {
										if(imageFile.exists() && !imageFile.isDirectory()){
											imageFile.delete();
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							
							long subid = Long.parseLong(usersList.getValue().toString());
							umObj.deleteUser(id, subid);
							getUI().getCurrent().removeWindow(userWindow);
							loadOptions(0);
							Notification.show(getPropertyName("Success"),getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			updateButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings({ "static-access", "deprecation"})
				public void buttonClick(ClickEvent event) {
					try {
						boolean notExist = false;
						boolean valid = true;
						boolean isLoginNew=false;
						boolean codeNotExist = false;
						if (session.getAttribute("oldCode").toString().equals(employCodeField.getValue())) {
							codeNotExist = true;
						} else {
							codeNotExist = isCodeNotExist();
						}
						if(enableLogin.getValue()){
							if(session.getAttribute("userName")!=null){
								if (session.getAttribute("userName").toString().equals(user_name.getValue())) {
									notExist = true;
								} else {
									notExist = isNotExist();
								}
							}
							else
								notExist = true;
							
							if (isValid() && notExist && codeNotExist) {
								valid=true;
							}
							else
								valid=false;
						}
						else{
							if (isValid() && codeNotExist) {
								valid=true;
							}
							else
								valid=false;
						}

						if (valid) {
							UserModel userObj = umObj.getUser(Long.parseLong(userCombo.getValue().toString()));
							
							String imageName="";
							List list=new ArrayList();
							Iterator itr=mainImageLayout.getComponentIterator();
							while (itr.hasNext()) {
								Component component = (Component) itr.next();
								SHorizontalLayout itemLayout=(SHorizontalLayout)component;
								SImage image=(SImage)itemLayout.getComponent(1);
								list.add(image.getId());
							}
							imageName=getFileName(list);
							
							userObj.setEmploy_code(employCodeField.getValue());
							userObj.setFirst_name(firstNameField.getValue());
							userObj.setMiddle_name(middle_name.getValue());
							userObj.setLast_name(last_name.getValue());
							userObj.setJob_title(jobTitleField.getValue());
							userObj.setGender((Character) genderSelect.getValue());
							userObj.setBirth_date(objUtil.getSQLDateFromUtilDate(birthDate.getValue()));
							userObj.setHeight(roundNumber(toDouble(heightField.getValue().toString())));
							userObj.setWeight(roundNumber(toDouble(weightField.getValue().toString())));
							userObj.setMarital_status((Character) maritalStatusSelect.getValue());
							userObj.setOffice(new S_OfficeModel((Long) office.getValue()));
							userObj.setDepartment(new DepartmentModel((Long) department.getValue()));
							userObj.setDesignation(new DesignationModel((Long) designation.getValue()));
							userObj.setUser_role(new S_UserRoleModel((Long) userRoleCombo.getValue()));
							userObj.setSalary_type((Integer) salaryTypeSelect.getValue());
							userObj.setSuperior_id((Long) superiorComboField.getValue());
							
							if((Long)accomodationRadio.getValue()==1)
								userObj.setCompanyAccomodation(true);
							else
								userObj.setCompanyAccomodation(false);
							
							if((Long)familyStatusRadio.getValue()==1)
								userObj.setFamilyStatus(true);
							else
								userObj.setFamilyStatus(false);
							
							if((Long)familyCountryRadio.getValue()==1)
								userObj.setFamilyCountry(true);
							else
								userObj.setFamilyCountry(false);
							
							if((Long)familyTicketRadio.getValue()==1)
								userObj.setFamilyTicket(true);
							else
								userObj.setFamilyTicket(false);
							
							userObj.setJoinStatus((Long)joinRadio.getValue());
							userObj.setReJoinStatus((Long)joinStatusRadio.getValue());
							userObj.setJoining_date(objUtil.getSQLDateFromUtilDate(joinDate.getValue()));
							userObj.setEffective_date(objUtil.getSQLDateFromUtilDate(effectiveDate.getValue()));
							userObj.setSalary_effective_date(objUtil.getSQLDateFromUtilDate(salaryEffectiveDate.getValue()));
							userObj.setVisaType((Long)visaCombo.getValue());
							userObj.setVisa_company(visaCompanyField.getValue());
							userObj.setTicketStatus((Long)ticketRadio.getValue());
							
							userObj.setLoginEnabled(enableLogin.getValue());
							
							S_LoginModel log=null;
							if(userObj.getLoginId()!=null){
								log=userObj.getLoginId();
								if(enableLogin.getValue()){
									log.setLogin_name(user_name.getValue());
									log.setPassword(SEncryption.encrypt(password.getValue()));
									log.setOffice(new S_OfficeModel((Long) office.getValue()));
									log.setUserType(new S_UserRoleModel((Long) userRoleCombo.getValue()));
									log.setStatus(0);
									userObj.setLoginId(log);
								}
								else{
									log.setStatus(1);
									userObj.setLoginId(log);
								}
							}
							else{
								if(enableLogin.getValue()){
									log = new S_LoginModel();
									log.setLogin_name(user_name.getValue());
									log.setPassword(SEncryption.encrypt(password.getValue()));
									log.setOffice(new S_OfficeModel((Long) office.getValue()));
									log.setUserType(new S_UserRoleModel((Long) userRoleCombo.getValue()));
									log.setStatus(0);
									userObj.setLoginId(log);
									isLoginNew=true;
								}
								else{
									userObj.setLoginId(null);
								}
							}
							String file=userObj.getUser_image();
							String[] fileArray = file.split(",");
							for (int i = 0; i < fileArray.length; i++) {
								File imageFile=new File(IMAGE_DIRECTORY.trim()+fileArray[i].trim());
								if(imageFile.exists() && !imageFile.isDirectory())
									imageName+=fileArray[i].trim();
							}	
							userObj.setUser_image(imageName);
							try {
								long id=umObj.update(userObj);
								saveImageAsPNG(imageName);
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								
								if(userObj.getLoginId()!=null){
									if(isLoginNew){
										S_OfficeModel ofc = ofcDao.getOffice(userObj.getOffice().getId());
										idsetDao.createIDGenerators(SConstants.scopes.LOGIN_LEVEL, 
																	ofc.getOrganization().getId(),
																	ofc.getId(), 
																	userObj.getLoginId().getId());
									}
								}
								loadOptions(id);
							} catch (Exception e) {
								Notification.show("Error",Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			setDefaultValues();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void setReadOnlyAll() {
		if (getRoleID() == 1) {
			organization.setReadOnly(true);
		}
	}
	
	
	public void setWritableAll() {
		if (getRoleID() == 1) {
			organization.setReadOnly(false);
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		List testList;
		List supList;
		try {

			if (loadAllButton.getId().equals("ALL")) {
				if (isSuperAdmin()) {
					testList = umObj.getUsersWithFullNameAndCodeFromOrg(getOrganizationID());
				} else {
					testList = umObj.getUsersWithFullNameAndCodeWithoutSuperAdminFromOrg(getOrganizationID());
				}

				supList = umObj.getAllSuperiorLoginsFromOrganization(getOrganizationID());
				loadAllButton.setDescription(getPropertyName("load_users_under_this_ofc"));
			} 
			else {
				testList = umObj.getUsersWithFullNameAndCodeUnderOffice(getOfficeID());

				supList = umObj.getAllSuperiorLogins(getOfficeID());

				loadAllButton.setDescription(getPropertyName("load_users_under_all_ofc"));
			}

			UserModel sop = new UserModel();
			sop.setId(0);
			sop.setFirst_name("------------ Create New -------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			boolean readOnlyUser=userCombo.isReadOnly();
			userCombo.setReadOnly(false);
			userCombo.setInputPrompt("------------ Create New -------------");

			bic = SCollectionContainer.setList(testList, "id");
			userCombo.setContainerDataSource(bic);
			userCombo.setItemCaptionPropertyId("first_name");

			if (id != 0)
				userCombo.setValue(id);
			else
				userCombo.setValue(null);
			
			userCombo.setReadOnly(readOnlyUser);

			if (supList == null)
				supList = new ArrayList();

			supList.add(0, new S_LoginModel(0, "none"));

			bic = SCollectionContainer.setList(supList, "id");

			boolean readOnly = superiorComboField.isReadOnly();
			superiorComboField.setReadOnly(false);
			superiorComboField.setContainerDataSource(bic);
			superiorComboField.setItemCaptionPropertyId("login_name");
			superiorComboField.setNewValue((long) 0);
			superiorComboField.setReadOnly(readOnly);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public Boolean isValid() {
		boolean ret = true;
		try {
			
			if (firstNameField.getValue() == null || firstNameField.getValue().equals("")) {
				setRequiredError(firstNameField, getPropertyName("invalid_data"), true);
				ret = false;
				firstNameField.focus();
			} else
				setRequiredError(firstNameField, null,false);
			
			if (jobTitleField.getValue() == null || jobTitleField.getValue().equals("")) {
				setRequiredError(jobTitleField, getPropertyName("invalid_data"), true);
				ret = false;
				jobTitleField.focus();
			} else
				setRequiredError(jobTitleField, null,false);

			if (employCodeField.getValue() == null || employCodeField.getValue().equals("")) {
				setRequiredError(employCodeField, getPropertyName("invalid_data"), true);
				ret = false;
				employCodeField.focus();
			} else
				setRequiredError(employCodeField, null, false);
			
			try {
				if(toDouble(heightField.getValue().toString())<0)
					heightField.setValue("0");
			} catch (Exception e) {
				heightField.setValue("0");
			}
			
			try {
				if(toDouble(weightField.getValue().toString())<0)
					weightField.setValue("0");
			} catch (Exception e) {
				weightField.setValue("0");
			}
			
			if(enableLogin.getValue()){
				if (confirmpassword.getValue() == null || confirmpassword.getValue().equals("")) {
					setRequiredError(confirmpassword, getPropertyName("confirm_password"), true);
					ret = false;
					confirmpassword.focus();
				} else
					setRequiredError(confirmpassword,getPropertyName("confirm_password"), false);

				if (!confirmpassword.getValue().equals(password.getValue())) {
					setRequiredError(confirmpassword, getPropertyName("passwords_are_different"), true);
					ret = false;
					confirmpassword.focus();
				} else
					setRequiredError(confirmpassword, null, false);

				if (password.getValue() == null || password.getValue().equals("")) {
					setRequiredError(password, getPropertyName("password"), true);
					ret = false;
					password.focus();
				} else
					setRequiredError(password, getPropertyName("password"), false);

				if (user_name.getValue() == null || user_name.getValue().equals("")) {
					setRequiredError(user_name, getPropertyName("login_name"), true);
					ret = false;
					user_name.focus();
				} else
					setRequiredError(user_name, getPropertyName("login_name"),false);
			}
			
			if (salaryEffectiveDate.getValue() == null || salaryEffectiveDate.getValue().equals("")) {
				setRequiredError(salaryEffectiveDate, getPropertyName("invalid_selection"), true);
				ret = false;
				salaryEffectiveDate.focus();
			} else
				setRequiredError(salaryEffectiveDate, null, false);

			if (effectiveDate.getValue() == null || effectiveDate.getValue().equals("")) {
				setRequiredError(effectiveDate, getPropertyName("invalid_selection"), true);
				ret = false;
				effectiveDate.focus();
			} else
				setRequiredError(effectiveDate, null, false);

			if (joinDate.getValue() == null || joinDate.getValue().equals("")) {
				setRequiredError(joinDate, getPropertyName("invalid_selection"), true);
				ret = false;
				joinDate.focus();
			} else
				setRequiredError(joinDate, null, false);

			if (userRoleCombo.getValue() == null || userRoleCombo.getValue().equals("")) {
				setRequiredError(userRoleCombo, getPropertyName("invalid_selection"), true);
				ret = false;
				userRoleCombo.focus();
			} else
				setRequiredError(userRoleCombo, null, false);

			if (designation.getValue() == null || designation.getValue().equals("")) {
				setRequiredError(designation, getPropertyName("invalid_selection"), true);
				ret = false;
				designation.focus();
			} else
				setRequiredError(designation, null, false);

			if (department.getValue() == null || department.getValue().equals("")) {
				setRequiredError(department, getPropertyName("invalid_selection"), true);
				ret = false;
				department.focus();
			} else
				setRequiredError(department, null, false);
			
			if (visaCombo.getValue() == null || visaCombo.getValue().equals("")) {
				setRequiredError(visaCombo, getPropertyName("invalid_selection"), true);
				ret = false;
				visaCombo.focus();
			} else
				setRequiredError(visaCombo, null, false);

			if (office.getValue() == null || office.getValue().equals("")) {
				setRequiredError(office, getPropertyName("invalid_selection"),true);
				ret = false;
				office.focus();
			} else
				setRequiredError(office, null, false);

			if (maritalStatusSelect.getValue() == null || maritalStatusSelect.getValue().equals("")) {
				setRequiredError(maritalStatusSelect,getPropertyName("invalid_selection"), true);
				ret = false;
				maritalStatusSelect.focus();
			} else
				setRequiredError(maritalStatusSelect, null, false);

			if (birthDate.getValue() == null || birthDate.getValue().equals("")) {
				setRequiredError(birthDate,getPropertyName("invalid_selection"), true);
				ret = false;
				birthDate.focus();
			} else
				setRequiredError(birthDate, null, false);

			if (genderSelect.getValue() == null || genderSelect.getValue().equals("")) {
				setRequiredError(genderSelect, getPropertyName("invalid_selection"), true);
				ret = false;
				genderSelect.focus();
			} else
				setRequiredError(genderSelect, null, false);
			
			if (superiorComboField.getValue() == null)
				superiorComboField.setNewValue((long) 0);

			return ret;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	public void removeErrorMsg() {
		confirmpassword.setComponentError(null);
		password.setComponentError(null);
		user_name.setComponentError(null);
		salaryEffectiveDate.setComponentError(null);
		effectiveDate.setComponentError(null);
		joinDate.setComponentError(null);
		userRoleCombo.setComponentError(null);
		designation.setComponentError(null);
		department.setComponentError(null);
		office.setComponentError(null);
		maritalStatusSelect.setComponentError(null);
		birthDate.setComponentError(null);
		genderSelect.setComponentError(null);
		firstNameField.setComponentError(null);
		employCodeField.setComponentError(null);
	}
	
	
	public Boolean isNotExist() {
		try {

			user_name.setComponentError(null);
			if (umObj.isAlreadyExist(user_name.getValue())) {
				setRequiredError(user_name,getPropertyName("User Name Already Exists"), true);
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	public Boolean isCodeNotExist() {

		try {
			employCodeField.setComponentError(null);
			if (umObj.isCodeAlreadyExist(employCodeField.getValue(), getOfficeID())) {
				setRequiredError(employCodeField,
						getPropertyName("Code Already exists"), true);
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	public void saveImageAsPNG(File file, String fileName) {

		BufferedImage bufferedImage;

		try {

			// read image file
			bufferedImage = ImageIO.read(file);
			float width = bufferedImage.getWidth(), height = bufferedImage
					.getHeight();
			if (bufferedImage.getWidth() > 100) {
				float div = width / 100;
				width = 100;
				if (div > 1)
					height = height / div;
			}

			// create a blank, RGB, same width and height, and a white
			// background
			BufferedImage newBufferedImage = new BufferedImage((int) width,
					(int) height, BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					(int) width, (int) height, Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "png", new File(fileName));

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	
	
	@SuppressWarnings("rawtypes")
	public void setDefaultValues() {
		try {
			Iterator it = null;

			genderSelect.setValue('M');
			birthDate.setValue(getWorkingDate());
			maritalStatusSelect.setValue('U');
			organization.setValue(getOrganizationID());
			office.setValue(getOfficeID());

			it = department.getItemIds().iterator();
			if (it.hasNext())
				department.setValue(it.next());

			it = designation.getItemIds().iterator();
			if (it.hasNext())
				designation.setValue(it.next());

			it = userRoleCombo.getItemIds().iterator();
			if (it.hasNext())
				userRoleCombo.setValue(it.next());

			salaryTypeSelect.setValue(SConstants.payroll.MONTHLY_SALARY);
			superiorComboField.setNewValue(0);

			joinDate.setValue(getWorkingDate());
			effectiveDate.setValue(getWorkingDate());
			salaryEffectiveDate.setValue(getWorkingDate());
			visaCombo.setValue((long)0);
			familyCountryRadio.setValue((long)1);
			familyStatusRadio.setValue((long)1);
			accomodationRadio.setValue((long)1);
			familyTicketRadio.setValue((long)1);
			accomodationRadio.setValue((long)1);
			joinRadio.setValue((long)1);
			joinStatusRadio.setValue((long)1);
			ticketRadio.setValue((long)1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Boolean getHelp() {
		return null;
	}
	
	
	public SComboField getBillNoFiled() {
		return userCombo;
	}
	
	
	public String getFileExtension(String file){
		String ext="";
		try{
			ext=file.substring(file.lastIndexOf('.'),file.length());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ext;
	}

	
	@SuppressWarnings("rawtypes")
	public void saveImageAsPNG(String fileName) {

		File file = null;
		String singleFile = "";
		String[] fileArray = fileName.split(",");
		BufferedImage bufferedImage;
		try {
			int i = 0;
			Iterator iter = imageList.iterator();
			while (iter.hasNext()) {
				singleFile = IMAGE_DIRECTORY + fileArray[i].replace(',', ' ').trim();

				if (!new File(singleFile).isDirectory() && fileArray[i].trim().length() > 0) {
					file = (File) iter.next();
					bufferedImage = ImageIO.read(file);
					float width = bufferedImage.getWidth(), height = bufferedImage.getHeight();
					
					if (bufferedImage.getWidth() > 500) {
						float div = width / 500;
						width = 500;
						if (div > 1)
							height = height / div;
					}
					BufferedImage newBufferedImage = new BufferedImage((int) width, (int) height,BufferedImage.TYPE_INT_RGB);
					newBufferedImage.createGraphics().drawImage(bufferedImage,0, 0, (int) width, (int) height, Color.WHITE, null);
					ImageIO.write(newBufferedImage, "png", new File(singleFile));
					i++;
				}
			}
			Iterator itr = imageList.iterator();
			while (itr.hasNext()) {
				File sfile = (File) itr.next();
				try {
					if(sfile.exists())
						sfile.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			imageList.clear();
			
		} catch (IOException e) {
			e.printStackTrace();

		}

	}
	
	
	@SuppressWarnings("rawtypes")
	protected String getFileName(List imageList) {
		Calendar cal = Calendar.getInstance();
		String fileName = "";
		for (int i = 0; i < imageList.size(); i++) {
			fileName += i + String.valueOf(sdf.format(cal.getTime())).trim()+".png,";
		}
		return fileName;
	}
	
	
	protected String getFileName() {
		Calendar cal = Calendar.getInstance();
		String fileName = "";
		fileName += String.valueOf(sdf.format(cal.getTime())).trim()+ ".png ,";
		return fileName;
	}
	
}
