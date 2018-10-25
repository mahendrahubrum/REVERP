package com.webspark.uac.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.dao.LanguageDao;
import com.webspark.model.AddressModel;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.CountryModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Anil K P
 */

/**
 * @author sangeeth
 * @date 05-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class AddOffice extends SparkLogic {

	SPanel mainPanel;
	SVerticalLayout mainLayout;
	SHorizontalLayout bodyLayout;
	SFormLayout contentLayout;
	
	SComboField officeCombo;
	STextField nameField;
	SComboField organizationCombo;
	SComboField adminUserCombo;
	SComboField currencyCombo;
	SComboField languageCombo;
	SComboField timeZoneCombo;
	SDateField finStartDateField;
	SDateField finEndDateField;
	SDateField workingDateField;
	SAddressField addressField;
	SCheckBox copyLedgerCheck;
	SCheckBox copyItemCheck;
	

	SButton createNewButton;
	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;
	
	WrappedSession session;
	OfficeDao dao;
	
	private static String DEFAULT_IMAGE = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/Images/no_image_hor.png";
	private static String DIR = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
	
	SFileUpload headerUpload;
	SFileUploder headerUploader;
	SFileUpload footerUpload;
	SFileUploder footerUploader;
	SImage headerImage;
	SImage footerImage;
    final String defaultImage="images/default.png";
    boolean headerRemoved=false;
    boolean footerRemoved=false;
    SimpleDateFormat sdf;
    
    SCheckBox sundayBox;
	SCheckBox mondayBox;
	SCheckBox tuesdayBox;
	SCheckBox wednesdayBox;
	SCheckBox thursdayBox;
	SCheckBox fridayBox;
	SCheckBox saturdayBox;
	SGridLayout daylayout;
    
	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		bodyLayout=new SHorizontalLayout();
		bodyLayout.setSpacing(true);
		contentLayout = new SFormLayout();
		contentLayout.setSizeFull();
		session = getHttpSession();
		dao = new OfficeDao();
		sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		setSize(900, 700);
		
		try {
			
			sundayBox=new SCheckBox("Sunday","1");
			mondayBox=new SCheckBox("Monday","2");
			tuesdayBox=new SCheckBox("Tuesday","3");
			wednesdayBox=new SCheckBox("Wednesday","4");
			thursdayBox=new SCheckBox("Thursday","5");
			fridayBox=new SCheckBox("Friday","6");
			saturdayBox=new SCheckBox("Saturday","7");
			 
			daylayout=new SGridLayout("Holidays",4,2);
			daylayout.addComponent(sundayBox);
			daylayout.addComponent(mondayBox);
			daylayout.addComponent(tuesdayBox);
			daylayout.addComponent(wednesdayBox);
			daylayout.addComponent(thursdayBox);
			daylayout.addComponent(fridayBox);
			daylayout.addComponent(saturdayBox);
			
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription((getPropertyName("create_new")));
			officeCombo = new SComboField(null, 300, null, "id", "name",false,getPropertyName("create_new"));
			adminUserCombo = new SComboField(getPropertyName("admin_user"),300,
											new UserManagementDao().getAllLoginsFromOffice(getOfficeID()), "id", "login_name",false, getPropertyName("select"));
			nameField = new STextField(getPropertyName("name"), 300);
			nameField.setRequired(true);
			organizationCombo = new SComboField(getPropertyName("organization"), 300, 
												new OrganizationDao().getAllOrganizations(), "id", "name",true,getPropertyName("select"));
			currencyCombo = new SComboField(getPropertyName("currency"), 300, 
											new CurrencyManagementDao().getlabels(), "id", "name",true,getPropertyName("select"));
			languageCombo = new SComboField(getPropertyName("language"), 300, 
											new LanguageDao().getAllLanguages(), "id", "name",true,getPropertyName("select"));
			timeZoneCombo = new SComboField(getPropertyName("time_zone"), 300);
			timeZoneCombo.setInputPrompt(getPropertyName("select"));
			for (int i = 0; i < TimeZone.getAvailableIDs().length; i++) {
				timeZoneCombo.addItem(TimeZone.getAvailableIDs()[i]);
			}
			finStartDateField = new SDateField((getPropertyName("start_fin_year")), 100,getDateFormat());
			finEndDateField = new SDateField((getPropertyName("end_fin_year")), 100,getDateFormat());
			workingDateField = new SDateField((getPropertyName("working_date")), 100,getDateFormat());
			addressField = new SAddressField(1);
			addressField.setCaption(null);
			
			copyLedgerCheck=new SCheckBox(getPropertyName("copy_ledger"), false);
			copyItemCheck=new SCheckBox(getPropertyName("copy_item"), false);
			
			
			headerUploader = new SFileUploder();
			headerUpload = new SFileUpload(null, headerUploader);
			headerUpload.setImmediate(true);
			headerUpload.setButtonCaption("Upload Header Image");
			
			footerUploader = new SFileUploder();
			footerUpload = new SFileUpload(null, footerUploader);
			footerUpload.setImmediate(true);
			footerUpload.setButtonCaption("Upload Footer Image");
			
			headerImage=new SImage(null, new ThemeResource(defaultImage));
			headerImage.setWidth("650");
			headerImage.setHeight("100");
			headerImage.setImmediate(true);
			
			footerImage=new SImage(null, new ThemeResource(defaultImage));
			footerImage.setWidth("650");
			footerImage.setHeight("100");
			footerImage.setImmediate(true);
			
			SHorizontalLayout headerLayout = new SHorizontalLayout();
			headerLayout.setSpacing(true);
			SHorizontalLayout headerImageLayout = new SHorizontalLayout();
			headerImageLayout.setStyleName("layout_border");
			
			SHorizontalLayout footerLayout = new SHorizontalLayout();
			footerLayout.setSpacing(true);
			SHorizontalLayout footerImageLayout = new SHorizontalLayout();
			footerImageLayout.setStyleName("layout_border");
			
			headerImageLayout.addComponent(headerImage);
			headerLayout.addComponent(headerUpload);
			headerLayout.addComponent(headerImageLayout);
			headerLayout.setComponentAlignment(headerUpload, Alignment.MIDDLE_CENTER);
			
			footerImageLayout.addComponent(footerImage);
			footerLayout.addComponent(footerUpload);
			footerLayout.addComponent(footerImageLayout);
			footerLayout.setComponentAlignment(footerUpload, Alignment.MIDDLE_CENTER);
			
			File file=new File(DEFAULT_IMAGE);
			if(file.exists() && !file.isDirectory()){
				headerImage.setSource(new FileResource(file));
				footerImage.setSource(new FileResource(file));
			}
			
			SHorizontalLayout createNewLayout = new SHorizontalLayout(getPropertyName("office"));
			createNewLayout.setSpacing(true);
			createNewLayout.addComponent(officeCombo);
			createNewLayout.addComponent(createNewButton);
			
			contentLayout.addComponent(createNewLayout);
			contentLayout.addComponent(nameField);
			contentLayout.addComponent(organizationCombo);
			contentLayout.addComponent(adminUserCombo);
			contentLayout.addComponent(currencyCombo);
			contentLayout.addComponent(languageCombo);
			contentLayout.addComponent(timeZoneCombo);
			contentLayout.addComponent(finStartDateField);
			contentLayout.addComponent(finEndDateField);
			contentLayout.addComponent(workingDateField);
			contentLayout.addComponent(copyLedgerCheck);
			contentLayout.addComponent(copyItemCheck);
			contentLayout.addComponent(daylayout);
			
			bodyLayout.addComponent(contentLayout);
			bodyLayout.addComponent(addressField);

			HorizontalLayout buttonLayout = new HorizontalLayout();
			buttonLayout.setSpacing(true);
			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			deleteButton.setVisible(false);
			updateButton.setVisible(false);

			mainLayout.addComponent(bodyLayout);
			mainLayout.addComponent(headerLayout);
			mainLayout.addComponent(footerLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

			mainPanel.setContent(mainLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					officeCombo.setValue((long) 0);
				}
			});
			
			
			headerUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					try{
						if (headerUploader.getFile() != null && headerUploader.getFile().exists()) {
							headerImage.setSource(new FileResource(headerUploader.getFile()));
							headerImage.markAsDirty();
							headerRemoved=true;
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			});
			 
			 
			footerUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					try{
						if (footerUploader.getFile() != null	&& footerUploader.getFile().exists()) {
							footerImage.setSource(new FileResource(footerUploader.getFile()));
							footerImage.markAsDirty();
							footerRemoved=true;
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			});
			

			finStartDateField.setImmediate(true);
			finStartDateField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(finStartDateField.getValue());
						cal.add(Calendar.DAY_OF_MONTH, -1);
						cal.add(Calendar.YEAR, 1);
						finEndDateField.setValue(cal.getTime());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					
					try {
						if (officeCombo.getValue() == null || officeCombo.getValue().toString().equals("0")) {
							if (isValid()) {
								S_OfficeModel objModel = new S_OfficeModel();
								String header="",footer="";
								objModel.setActive('Y');
								objModel.setName(nameField.getValue());
								objModel.setAddress(addressField.getAddress());
								objModel.setCountry(new CountryModel((Long) addressField.getCountryComboField().getValue()));
								objModel.setLanguage((Long)languageCombo.getValue());
								objModel.setCurrency(new CurrencyModel((Long) currencyCombo.getValue()));
								objModel.setOrganization(new S_OrganizationModel((Long) organizationCombo.getValue()));
								objModel.setWorkingDate(CommonUtil.getSQLDateFromUtilDate(workingDateField.getValue()));
								objModel.setFin_start_date(CommonUtil.getSQLDateFromUtilDate(finStartDateField.getValue()));
								objModel.setFin_end_date(CommonUtil.getSQLDateFromUtilDate(finEndDateField.getValue()));
								objModel.setTimezone(asString(timeZoneCombo.getValue()));
								objModel.setHolidays(getHolidyas());
								if(headerRemoved)
									header="header_"+getFileName(getLoginID());
								if(footerRemoved)
									footer="footer_"+getFileName(getLoginID());
								objModel.setHeader(header);
								objModel.setFooter(footer);

								if (adminUserCombo.getValue() != null)
									objModel.setAdmin_user_id((Long) adminUserCombo.getValue());
								else
									objModel.setAdmin_user_id((long)0);

								S_OfficeModel ofc = dao.save(objModel,getOfficeID());
								
								if (headerUploader.getFile() != null && headerUploader.getFile().exists()) {
									saveImageAsPNG(headerUploader.getFile(),DIR.trim()+header.trim());
									headerUploader.deleteFile();
								}
								if (footerUploader.getFile() != null && footerUploader.getFile().exists()) {
									saveImageAsPNG(footerUploader.getFile(),DIR.trim()+footer.trim());
									footerUploader.deleteFile();
								}
								long officeId=ofc.getId();
								
								new IDGeneratorSettingsDao().createIDGenerators(SConstants.scopes.OFFICE_LEVEL,
																				ofc.getOrganization().getId(), 
																				ofc.getId(), 0);
								
								if(copyLedgerCheck.getValue()){
									dao.saveSettings(getOfficeID(), officeId);
									dao.createLedgers(getOfficeID(),officeId);
								}

								if(copyItemCheck.getValue())
									dao.createItems(getOfficeID(),officeId);
								
								loadOptions(ofc.getId());	
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}

				}

			});

			
			officeCombo.addValueChangeListener(new Property.ValueChangeListener() {

				@SuppressWarnings({ "rawtypes", "deprecation" })
				public void valueChange(ValueChangeEvent event) {
					clearErrors();
					try {
						copyItemCheck.setValue(false);
						copyLedgerCheck.setValue(false);
						copyItemCheck.setVisible(false);
						copyLedgerCheck.setVisible(false);
						if (headerUploader.getFile() != null) {
							headerUploader.deleteFile();
						}
						if (footerUploader.getFile() != null) {
							footerUploader.deleteFile();
						}
						File file=new File(DEFAULT_IMAGE);
						if(file.exists() && !file.isDirectory()){
							headerImage.setSource(new FileResource(file));
							footerImage.setSource(new FileResource(file));
						}
						headerRemoved=false;
						footerRemoved=false;
						if (officeCombo.getValue() != null && !officeCombo.getValue().toString().equals("0")) {
							saveButton.setVisible(false);
							deleteButton.setVisible(true);
							updateButton.setVisible(true);
							S_OfficeModel ofc = dao.getOffice((Long) officeCombo.getValue());
							
							nameField.setValue(ofc.getName());
							organizationCombo.setNewValue(ofc.getOrganization().getId());
							adminUserCombo.setValue(ofc.getAdmin_user_id());
							currencyCombo.setValue(ofc.getCurrency().getId());
							languageCombo.setValue(ofc.getLanguage());
							timeZoneCombo.setValue(ofc.getTimezone());
							finStartDateField.setValue(ofc.getFin_start_date());
							finEndDateField.setValue(ofc.getFin_end_date());
							workingDateField.setValue(ofc.getWorkingDate());
							addressField.loadAddress(ofc.getAddress().getId());
							String[] holidays=ofc.getHolidays().split(",");
							
							if(holidays!=null && holidays.length>0){
								for(int i=0;i<holidays.length;i++){
									if(holidays[i].toString().trim().length()>0){
										if(toInt(holidays[i].toString())==SConstants.weekDays.SUNDAY){
											sundayBox.setValue(true);
										}else if(toInt(holidays[i].toString())==SConstants.weekDays.MONDAY){
											mondayBox.setValue(true);
										}else if(toInt(holidays[i].toString())==SConstants.weekDays.TUESDAY){
											tuesdayBox.setValue(true);
										}else if(toInt(holidays[i].toString())==SConstants.weekDays.WEDNESDAY){
											wednesdayBox.setValue(true);
										}else if(toInt(holidays[i].toString())==SConstants.weekDays.THURSDAY){
											thursdayBox.setValue(true);
										}else if(toInt(holidays[i].toString())==SConstants.weekDays.FRIDAY){
											fridayBox.setValue(true);
										}else if(toInt(holidays[i].toString())==SConstants.weekDays.SATURDAY){
											saturdayBox.setValue(true);
										}
									}
								}
							}
							if(ofc.getHeader().length()>0){
								File headerFile=new File(DIR.trim()+ofc.getHeader().trim());
								if(headerFile.exists() && !headerFile.isDirectory())
									headerImage.setSource(new FileResource(headerFile));
							}
							if(ofc.getFooter().length()>0){
								File footerFile=new File(DIR.trim()+ofc.getFooter().trim());
								if(footerFile.exists() && !footerFile.isDirectory())
									footerImage.setSource(new FileResource(footerFile));
							}
							copyItemCheck.setValue(false);
							copyLedgerCheck.setValue(false);
							copyItemCheck.setVisible(false);
							copyLedgerCheck.setVisible(false);
						}
						else{
							saveButton.setVisible(true);
							deleteButton.setVisible(false);
							updateButton.setVisible(false);
							copyItemCheck.setValue(false);
							copyLedgerCheck.setValue(false);
							copyItemCheck.setVisible(true);
							copyLedgerCheck.setVisible(true);
							nameField.setValue("");
							organizationCombo.setValue(null);
							adminUserCombo.setValue(null);
							currencyCombo.setValue(null);
							languageCombo.setValue(null);
							timeZoneCombo.setValue(null);
							finStartDateField.setValue(getWorkingDate());
							workingDateField.setValue(getWorkingDate());
							addressField.setReadOnly(false);
							addressField.clearAll();
							addressField.getCountryComboField().setValue(getCountryID());
							headerImage.setSource(null);
							footerImage.setSource(null);
							SCheckBox check;
							Iterator iter=daylayout.getComponentIterator();
							while (iter.hasNext()) {
								check = (SCheckBox) iter.next();
								check.setValue(false);
							}
							if (getRoleID() > 2) {
								organizationCombo.setReadOnly(true);
							}
							if(file.exists() && !file.isDirectory()){
								headerImage.setSource(new FileResource(file));
								footerImage.setSource(new FileResource(file));
							}
							headerRemoved=false;
							footerRemoved=false;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			deleteButton.addClickListener(new Button.ClickListener() {

				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {

							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										long id = Long.parseLong(officeCombo.getValue().toString());
											/*List list = new ArrayList();
											for (int i = 1; i <=62; i++) {
												list.add(i);
											}
//													new DeleteDao().delete(list, id);
*/											S_OfficeModel office = dao.getOffice(Long.parseLong(officeCombo.getValue().toString()));
											String header="",footer="";
											header=office.getHeader();
											footer=office.getFooter();
											boolean isDeletable=true;
											try {
												isDeletable=dao.isDeletable(id);
											} catch (Exception e) {
												isDeletable=false;
											}
											dao.delete(id, isDeletable);
											if(isDeletable){
												deleteOldFile(header.trim());
												deleteOldFile(footer.trim());
											}
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											loadOptions(0);
										
									} catch (Exception e) {
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			updateButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							String header="",footer="";
							String oldHeader="",oldFooter="";
							S_OfficeModel objModel = dao.getOffice(Long.parseLong(officeCombo.getValue().toString()));
							AddressModel address = addressField.getAddress();
							address.setId(objModel.getAddress().getId());
							
							oldHeader=objModel.getHeader();
							oldFooter=objModel.getFooter();
							
							objModel.setName(nameField.getValue());
							objModel.setOrganization(new S_OrganizationModel((Long) organizationCombo.getValue()));
							if (adminUserCombo.getValue() != null)
								objModel.setAdmin_user_id((Long) adminUserCombo.getValue());
							else
								objModel.setAdmin_user_id((long)0);
							objModel.setCurrency(new CurrencyModel((Long) currencyCombo.getValue()));
							objModel.setLanguage((Long)languageCombo.getValue());
							objModel.setTimezone(asString(timeZoneCombo.getValue()));
							objModel.setFin_start_date(CommonUtil.getSQLDateFromUtilDate(finStartDateField.getValue()));
							objModel.setFin_end_date(CommonUtil.getSQLDateFromUtilDate(finEndDateField.getValue()));
							objModel.setWorkingDate(CommonUtil.getSQLDateFromUtilDate(workingDateField.getValue()));
							objModel.setAddress(address);
							objModel.setCountry(new CountryModel((Long) addressField.getCountryComboField().getValue()));
							objModel.setHolidays(getHolidyas());
							if(headerRemoved)
								header="header_"+getFileName(getLoginID());	
							else
								header=oldHeader;
							if(footerRemoved)
								footer="footer_"+getFileName(getLoginID());	
							else
								footer=oldFooter;
							
							objModel.setHeader(header);
							objModel.setFooter(footer);
							
							dao.update(objModel);
							session.setAttribute("time_zone",objModel.getTimezone());
							
							if (headerUploader.getFile() != null && headerUploader.getFile().exists()) {
								saveImageAsPNG(headerUploader.getFile(),DIR.trim()+header.trim());
								headerUploader.deleteFile();
								deleteOldFile(oldHeader.trim());
							}
							if (footerUploader.getFile() != null && footerUploader.getFile().exists()) {
								saveImageAsPNG(footerUploader.getFile(),DIR.trim()+footer.trim());
								footerUploader.deleteFile();
								deleteOldFile(oldFooter.trim());
							}
							loadOptions(objModel.getId());
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);	
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
					}
				}
			});

			
			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			
			loadOptions(0);
			
			organizationCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(organizationCombo.getValue()!=null && !organizationCombo.getValue().toString().equals("")){
						if((Long)organizationCombo.getValue() == getOrganizationID()){
							copyItemCheck.setValue(false);
							copyLedgerCheck.setValue(false);
							copyItemCheck.setVisible(true);
							copyLedgerCheck.setVisible(true);
						}
						else{
							copyItemCheck.setValue(false);
							copyLedgerCheck.setValue(false);
							copyItemCheck.setVisible(false);
							copyLedgerCheck.setVisible(false);
						}
					}
				}
			});
			organizationCombo.setNewValue(getOrganizationID());
			
			if (getRoleID() > 2) {
				organizationCombo.setReadOnly(true);
			}
			workingDateField.setValue(getWorkingDate());
			finStartDateField.setValue(getWorkingDate());
			addressField.getCountryComboField().setValue(getCountryID());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	protected String getHolidyas() {
		String hDays="";
		SCheckBox check;
		Iterator iter=daylayout.getComponentIterator();
		while (iter.hasNext()) {
			check = (SCheckBox) iter.next();
			if(check.getValue()){
				hDays+=check.getId()+",";
			}
		}
		return hDays;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		List testList = new ArrayList();
		
		try {
			UserModel user=new UserManagementDao().getUser(getUserID());
			testList.add(0, new S_OfficeModel(0, getPropertyName("create_new")));
			if(user.getUser_role().getId()<=2){
				testList.addAll(dao.getAllOfficeNames());
			}
			else{
				testList.addAll(dao.getAllOfficeNamesUnderOrg(getOrganizationID()));
			}
			SCollectionContainer bic = SCollectionContainer.setList(testList, "id");
			officeCombo.setContainerDataSource(bic);
			officeCombo.setItemCaptionPropertyId("name");
			officeCombo.setValue(id);
			officeCombo.setInputPrompt(getPropertyName("create_new"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public String getFileName(long id) {
		String fileName = "";
		Calendar calendar = Calendar.getInstance();
		fileName = id + "_"+ String.valueOf(sdf.format(calendar.getTime())).trim()+ ".png";
		return fileName;
	}
	
	
	public void deleteOldFile(String fileName){
		try{
			if(fileName.length()>0){
				File file=new File(DIR.trim()+fileName.trim());
				if(file.exists() && !file.isDirectory())
					file.delete();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void saveImageAsPNG(File file, String fileName) {
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(file);
			float width = bufferedImage.getWidth(), height = bufferedImage.getHeight();
			if (bufferedImage.getWidth() >= 600) {
//				float div = width / 600;
				width = 1000;
//				if (div > 1)
					height = 100;
			}
			else{
				width = 600;
				height=100;
			}
			BufferedImage newBufferedImage = new BufferedImage((int) width,(int) height, BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,(int) width, (int) height, Color.WHITE, null);
			ImageIO.write(newBufferedImage, "png", new File(fileName));
			Notification.show("Image Updated",Type.TRAY_NOTIFICATION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Boolean isValid() {

		boolean valid = true;

		clearErrors();

		try {

			if (nameField.getValue() == null
					|| nameField.getValue().equals("")) {
				setRequiredError(nameField, getPropertyName("invalid_data"),
						true);
				valid = false;
			}

			if (organizationCombo.getValue() == null
					|| organizationCombo.getValue().equals("")) {
				setRequiredError(organizationCombo,
						getPropertyName("invalid_selection"), true);
				valid = false;
			}

			if (addressField.getCountryComboField().getValue() == null
					|| addressField.getCountryComboField().getValue()
							.equals("")) {
				setRequiredError(addressField,
						getPropertyName("invalid_selection"), true);
				valid = false;
			}

			if (currencyCombo.getValue() == null || currencyCombo.getValue().equals("")) {
				setRequiredError(currencyCombo,
						getPropertyName("invalid_selection"), true);
				valid = false;
			}
			
			if (languageCombo.getValue() == null || languageCombo.getValue().equals("")) {
				setRequiredError(languageCombo,
						getPropertyName("invalid_selection"), true);
				valid = false;
			}

			if (workingDateField.getValue() == null
					|| workingDateField.getValue().equals("")) {
				setRequiredError(workingDateField, getPropertyName("invalid_data"),
						true);
				valid = false;
			}

			if (finStartDateField.getValue() == null
					|| finStartDateField.getValue().equals("")) {
				setRequiredError(finStartDateField, getPropertyName("invalid_data"),
						true);
				valid = false;
			}

			if (finEndDateField.getValue() == null
					|| finEndDateField.getValue().equals("")) {
				setRequiredError(finEndDateField, getPropertyName("invalid_data"),
						true);
				valid = false;
			}

			if (finEndDateField.getValue().before(finStartDateField.getValue())) {
				setRequiredError(finEndDateField,
						getPropertyName("end_date_fin_date"), true);
				valid = false;
			}

			if (timeZoneCombo.getValue() == null
					|| timeZoneCombo.getValue().equals("")) {
				setRequiredError(timeZoneCombo,
						getPropertyName("invalid_selection"), true);
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}
		return valid;

	}

	
	private void clearErrors() {
		nameField.setComponentError(null);
		organizationCombo.setComponentError(null);
		addressField.setComponentError(null);
		currencyCombo.setComponentError(null);
		languageCombo.setComponentError(null);
		workingDateField.setComponentError(null);
		finStartDateField.setComponentError(null);
		finEndDateField.setComponentError(null);
		timeZoneCombo.setComponentError(null);
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
