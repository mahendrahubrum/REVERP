package com.inventory.subscription.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.subscription.dao.SubscriptionConfigurationDao;
import com.inventory.subscription.dao.SubscriptionCreationDao;
import com.inventory.subscription.model.SubscriptionConfigurationModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;
import com.webspark.uac.model.CountryModel;
import com.webspark.uac.model.S_OfficeModel;

public class CreateSubscription extends SparkLogic 
{
	private static final long serialVersionUID = 4933607204036137737L;
	SPanel mainPanel;
	SFormLayout form;
	SVerticalLayout mainLayout;
	SHorizontalLayout subscriptionLayout,buttonLayout,imageLayout,imageButtonLayout;
	SComboField subscriptionCombo,subscriptionTypeCombo;/*,periodCombo,subscriberCombo;*/
	STextField nameField,quantityField;
	SRadioButton accountRadio,rentRadio;
	SDateField createdDate;
	SButton save,update,delete,createNew;
	SubscriptionCreationDao scdao;
	SubscriptionConfigurationDao dao;
	SFileUpload imageUpload;
	SFileUploder uploader;
	SButton remove;
	
	SImage vehicleImage;
	String vehicleImageName = "";
	String updateFilename = "";
	String dirFile = "";
	String dir;
	String odir;
	SLabel vehicle;
	SVerticalLayout image;
	SCheckBox imageSelectBox;
	SCheckBox specialBox;
	SHorizontalLayout hrl;
	WrappedSession session;
	boolean removed=false;
	private SimpleDateFormat sdf;
	@SuppressWarnings("rawtypes")
	private List imageList;
	
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI()
	{
		try
		{
			form=new SFormLayout();
			mainPanel=new SPanel();
			mainPanel.setSizeFull();
			setSize(420, 340);
			image=new SVerticalLayout();
			image.setStyleName("layout_scroll");
			dir = VaadinServlet.getCurrent().getServletContext().getRealPath("/")
					+ "VAADIN/themes/testappstheme/VehicleImages/";
			odir=VaadinServlet.getCurrent().getServletContext().getRealPath("/").toString();
			session = getHttpSession();
			imageList = new ArrayList();
			vehicleImageName = odir+"no_image.png";
			vehicle=new SLabel("Vehicle Pictures", 110);
			sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
			/*****************************************************************************************************///Initialization
			scdao=new SubscriptionCreationDao();
			dao=new SubscriptionConfigurationDao();
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			imageLayout=new SHorizontalLayout();
			imageLayout.setSpacing(true);
			subscriptionLayout=new SHorizontalLayout(getPropertyName("rental_item"));
			subscriptionLayout.setSpacing(true);
			imageButtonLayout=new SHorizontalLayout();
			imageButtonLayout.setSpacing(true);
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			subscriptionCombo=new SComboField(null, 175);
			subscriptionCombo.setRequired(true);
			reloadSubscriptionCombo(0);
			nameField=new STextField(getPropertyName("rental_item_name"), 200);
			nameField.setRequired(true);
			nameField.setInputPrompt(getPropertyName("rental_item_name"));
			quantityField=new STextField(getPropertyName("quantity"), 200);
			quantityField.setRequired(true);
			quantityField.setValue("1");
			quantityField.setInputPrompt(getPropertyName("quantity"));
			accountRadio = new SRadioButton(getPropertyName("account_type"), 200, SConstants.rentalList, "key", "value");
			accountRadio.setValue((long) 2);
			accountRadio.setHorizontal(true);
			specialBox=new SCheckBox(getPropertyName("special_rental"), false);
			rentRadio = new SRadioButton(getPropertyName("rent_type"), 200, SConstants.rentalTypeList, "key", "value");
			rentRadio.setValue((long) 1);
			rentRadio.setHorizontal(true);
			createdDate=new SDateField(getPropertyName("creation_date"), 200);
			createdDate.setRequired(true);
			createdDate.setValue(getWorkingDate());
			subscriptionTypeCombo=new SComboField(getPropertyName("rental_type"), 200);
			subscriptionTypeCombo.setRequired(true);
			subscriptionTypeCombo.setInputPrompt(getPropertyName("select"));
			reloadSubscriptionTypeCombo(0, toLong(accountRadio.getValue().toString()));
			uploader = new SFileUploder();
			imageUpload = new SFileUpload("", uploader);
			imageUpload.setImmediate(true);
			remove=new SButton(getPropertyName("remove"));
			save=new SButton(getPropertyName("save"));
			update=new SButton(getPropertyName("update"));
			update.setVisible(false);
			delete=new SButton(getPropertyName("delete"));
			delete.setVisible(false);
			createNew=new SButton();
			createNew.setStyleName("createNewBtnStyle");
			createNew.setDescription(getPropertyName("create_new"));
			imageButtonLayout.addComponent(imageUpload);
			imageButtonLayout.addComponent(remove);
			remove.setVisible(true);
			imageButtonLayout.setComponentAlignment(imageUpload,Alignment.BOTTOM_LEFT);
			imageButtonLayout.setComponentAlignment(remove,Alignment.BOTTOM_RIGHT);
			/*****************************************************************************************************///Adding to Layout
			subscriptionLayout.addComponent(subscriptionCombo);
			subscriptionLayout.addComponent(createNew);
			imageLayout.addComponent(vehicle);
			SVerticalLayout layout=new  SVerticalLayout();
			layout.addComponent(image);
			layout.addComponent(imageButtonLayout);
			imageLayout.addComponent(layout);
			imageLayout.setStyleName("layout_border");
			imageLayout.setComponentAlignment(vehicle, Alignment.MIDDLE_LEFT);
			imageLayout.setComponentAlignment(layout, Alignment.MIDDLE_RIGHT);
			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			
			/*****************************************************************************************************///Adding to Main Layout
			form.addComponent(subscriptionLayout);
			form.addComponent(nameField);
//			form.addComponent(quantityField);
			form.addComponent(createdDate);
			form.addComponent(specialBox);
			form.addComponent(accountRadio);
			form.addComponent(rentRadio);
			form.addComponent(imageLayout);
			form.addComponent(subscriptionTypeCombo);
			form.addComponent(buttonLayout);
			
			form.setSpacing(true);
			form.setMargin(true);
			rentRadio.setVisible(false);
			imageLayout.setVisible(false);
			mainPanel.setContent(form);
			
			/*****************************************************************************************************///Listeners
			createNew.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try{
						subscriptionCombo.setValue((long)0);
						reloadSubscriptionTypeCombo(0,2);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			specialBox.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(specialBox.getValue()==true) {
							accountRadio.setValue((long)3);
							accountRadio.setReadOnly(true);
							SCollectionContainer bic=SCollectionContainer.setList(SConstants.specialRentalTypeList, "key");
							rentRadio.setContainerDataSource(bic);
							rentRadio.setItemCaptionPropertyId("value");
							rentRadio.setValue((long)1);
						}
						else {
							accountRadio.setReadOnly(false);
							accountRadio.setValue((long)2);
							SCollectionContainer bic=SCollectionContainer.setList(SConstants.rentalTypeList, "key");
							rentRadio.setContainerDataSource(bic);
							rentRadio.setItemCaptionPropertyId("value");
							rentRadio.setValue((long)1);
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			accountRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					subscriptionTypeCombo.removeAllItems();
					long type=toLong(accountRadio.getValue().toString());
					reloadSubscriptionTypeCombo(0, type);
					if(type==(long)1){
						rentRadio.setVisible(false);
						imageLayout.setVisible(false);
						rentRadio.setValue((long)1);
						setSize(420, 340);
						center();
						if (uploader.getFile() != null && uploader.getFile().exists()) {
							uploader.deleteFile();
							vehicleImage.setSource(new FileResource(new File("")));
							vehicleImage.markAsDirty();
							remove.setVisible(true);
						}
						reloadSubscriptionTypeCombo((long)0,type);
					}
					else if(type==(long)2){
						rentRadio.setVisible(false);
						imageLayout.setVisible(false);
						rentRadio.setValue((long)1);
						setSize(420, 340);
						center();
						if (uploader.getFile() != null && uploader.getFile().exists()) {
							uploader.deleteFile();
							vehicleImage.setSource(new FileResource(new File("")));
							vehicleImage.markAsDirty();
							remove.setVisible(true);
						}
						reloadSubscriptionTypeCombo((long)0,type);
					}
					else if(type==(long)3){
						rentRadio.setVisible(true);
						imageLayout.setVisible(true);
						rentRadio.setValue((long)1);
						setSize(420, 750);
						center();
						reloadSubscriptionTypeCombo((long)0,type);
					}
					
				}
			});
			
			imageUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					try{
						if (uploader.getFile() != null	&& uploader.getFile().exists()) {
							hrl=new SHorizontalLayout();
							vehicleImage = new SImage(null, new FileResource(uploader.getFile()));
							vehicleImage.setWidth("50");
							vehicleImage.setHeight("50");
							vehicleImage.setImmediate(true);
							vehicleImage.markAsDirty();
							imageSelectBox = new SCheckBox();
							hrl.addComponent(imageSelectBox);
							hrl.addComponent(vehicleImage);
							image.addComponent(hrl);
							imageList.add(uploader.getFile());
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			});
			
			remove.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (uploader.getFile() != null) {
							uploader.deleteFile();
						}
						SHorizontalLayout hor;
						SCheckBox check = null;
						List remList = new ArrayList();
						Iterator iter = image.iterator();
						while (iter.hasNext()) {
							hor = (SHorizontalLayout) iter.next();
							check = (SCheckBox) hor.getComponent(0);
							if (check.getValue()) {
								remList.add(hor);
							}
						}
						Image imag;
						iter = remList.iterator();
						while (iter.hasNext()) {
							hor = ((SHorizontalLayout) iter.next());
							image.removeComponent(hor);
							imag = (Image) hor.getComponent(1);
							imageList.remove(((FileResource) imag.getSource())
									.getSourceFile());
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			save.addClickListener(new ClickListener() 
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try
					{
//						String filename;
						subscriptionCombo.setComponentError(null);
						nameField.setComponentError(null);
						createdDate.setComponentError(null);
						subscriptionTypeCombo.setComponentError(null);
						SubscriptionCreationModel mdl;
						long type=toLong(accountRadio.getValue().toString());
						if(isValid())
						{
							accountRadio.setReadOnly(false);
//							filename =nameField.getValue().toString()+getFileName();
							String fileName = "";
							if (imageList.size() > 0) {
								fileName = getFileName();
							}
							long id=toLong(subscriptionTypeCombo.getValue().toString());
							mdl=new SubscriptionCreationModel();
							LedgerModel ledger = new LedgerModel();
							AddressModel adr=new AddressModel();
	        				adr.setCountry(new CountryModel(getCountryID()));
	        				ledger.setName(nameField.getValue().toString());
//	        				ledger.setGroup(new GroupModel(SConstants.INDIRECT_EXPENSE_GROUP_ID));
//	        				ledger.setAddress(adr);
	        				ledger.setOffice(new S_OfficeModel(getOfficeID()));
	        				ledger.setCurrent_balance(toDouble(asString(0)));
//	        				ledger.setOpening_balance(toDouble(asString(0)));
	        				ledger.setStatus((long)1);
							
							mdl.setName(nameField.getValue().toString());
							mdl.setAccount_type(type);
//							mdl.setQuantity(toDouble(quantityField.getValue().toString()));
							mdl.setCreated_date(CommonUtil.getSQLDateFromUtilDate(createdDate.getValue()));
							mdl.setSubscription_type(new SubscriptionConfigurationModel(id));
							mdl.setCreated_by(getLoginID());
							mdl.setLedger(ledger);
							mdl.setAvailable((long)0);
							if(type==(long)3){
								mdl.setRent_status(toLong(rentRadio.getValue().toString()));
							}
							else{
								mdl.setRent_status((long)0);
							}
							if(type==(long)3){
//								if (uploader.getFile() != null && uploader.getFile().exists()) {
									mdl.setImage(fileName);
//								}
							}
							if(specialBox.getValue()==true) {
								mdl.setSpecial((long)1);
							}
							else {
								mdl.setSpecial((long)0);
							}
							long sid=scdao.save(mdl);
							if (image.getComponentCount() > 0) {

								saveImageAsPNG(fileName);
							}
							reloadSubscriptionCombo(sid);
							SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			subscriptionCombo.addValueChangeListener(new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					try{
						accountRadio.setReadOnly(false);
						subscriptionCombo.setComponentError(null);
						nameField.setComponentError(null);
						createdDate.setComponentError(null);
						subscriptionTypeCombo.setComponentError(null);
						save.setVisible(false);
						update.setVisible(true);
						delete.setVisible(true);
						SubscriptionCreationModel mdl;
						if (uploader.getFile() != null)
							uploader.deleteFile();

						imageList.clear();
						image.removeAllComponents();
						if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0")){
							mdl=scdao.getCreationModel(toLong(subscriptionCombo.getValue().toString()));
							if(mdl!=null){
								if(mdl.getAvailable()==0){
									nameField.setValue(mdl.getName());
//									quantityField.setValue(mdl.getQuantity()+"");
									accountRadio.setValue(mdl.getAccount_type());
									if(mdl.getSpecial()==1) {
										specialBox.setValue(true);
										accountRadio.setReadOnly(true);
									}
									else {
										accountRadio.setReadOnly(false);
										specialBox.setValue(false);
									}
									long type=toLong(accountRadio.getValue().toString());
									createdDate.setValue(mdl.getCreated_date());
									subscriptionTypeCombo.setValue(mdl.getSubscription_type().getId());
									if(type==(long)1){
										rentRadio.setVisible(false);
										imageLayout.setVisible(false);
										rentRadio.setValue((long)1);
										setSize(420, 340);
										center();
									}
									else if(type==(long)2){
										rentRadio.setVisible(false);
										imageLayout.setVisible(false);
										rentRadio.setValue((long)1);
										setSize(420, 340);
										center();
									}
									else if(type==(long)3){
										rentRadio.setVisible(true);
										imageLayout.setVisible(true);
										if(mdl.getRent_status()!=0)
											rentRadio.setValue(mdl.getRent_status());
										else
											rentRadio.setValue((long)1);
										setSize(420, 750);
										center();
										try{
											String file = scdao.getImageName((Long)subscriptionCombo.getValue());
											if (file != null && file.trim().length() > 0) {
												String[] fileArray = file.split(",");
												String dir = VaadinServlet.getCurrent()
														.getServletContext()
														.getRealPath("/")
														+ "VAADIN/themes/testappstheme/VehicleImages/";
												File imgFile = null;

												for (int i = 0; i < fileArray.length; i++) {
													SHorizontalLayout imgLay = new SHorizontalLayout();

													imgFile = new File(dir
															+ fileArray[i].replace(',',
																	' ').trim());
													vehicleImage = new SImage(null,new FileResource(imgFile));
													vehicleImage.setStyleName("user_photo");
													vehicleImage.setWidth("50");
													vehicleImage.setHeight("50");
													vehicleImage.markAsDirty();
													imageSelectBox = new SCheckBox();
													imgLay.addComponent(imageSelectBox);
													imgLay.addComponent(vehicleImage);
													image.addComponent(imgLay);
													imageList.add(imgFile);
												}

											}
										}
										catch(Exception e){
											e.printStackTrace();
										}
									}
								}
								else{
									SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
								}
							}	
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
						else{
							SCollectionContainer bic=SCollectionContainer.setList(SConstants.rentalTypeList, "key");
							rentRadio.setContainerDataSource(bic);
							rentRadio.setItemCaptionPropertyId("value");
							rentRadio.setValue((long)1);
							accountRadio.setReadOnly(false);
							specialBox.setValue(false);
							subscriptionCombo.setValue((long)0);
							nameField.setValue("");
//							quantityField.setValue("1");
							createdDate.setValue(getWorkingDate());
							subscriptionTypeCombo.setValue((long)1);
							accountRadio.setValue((long)2);
							save.setVisible(true);
							delete.setVisible(false);
							update.setVisible(false);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			
			update.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event) 
				{
					try
					{
						subscriptionCombo.setComponentError(null);
						nameField.setComponentError(null);
						createdDate.setComponentError(null);
						subscriptionTypeCombo.setComponentError(null);
						SubscriptionCreationModel mdl;
						LedgerModel ledger=null;
						AddressModel adr = null ;
						String oldName;
						if(isValid())
						{
							if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0"))
							{
								accountRadio.setReadOnly(false);
								long id=toLong(subscriptionTypeCombo.getValue().toString());
								long type=toLong(accountRadio.getValue().toString());
								mdl=scdao.getCreationModel(toLong(subscriptionCombo.getValue().toString()));
								if(mdl.getAvailable()==0){
									oldName = mdl.getImage();
									if(mdl.getLedger()!=null){
										ledger=mdl.getLedger();
//										if(ledger.getAddress()!=null){
//											adr=new AddressModel();
//					        				adr.setId(mdl.getLedger().getAddress().getId());
//					        				adr.setCountry(new CountryModel(getCountryID()));
//										}
									}
									else{
										ledger=new LedgerModel();
				        				adr=new AddressModel();
				        				adr.setCountry(new CountryModel(getCountryID()));
									}
									ledger.setName(nameField.getValue().toString());
//			        				ledger.setGroup(new GroupModel(SConstants.INDIRECT_EXPENSE_GROUP_ID));
//			        				ledger.setAddress(adr);
			        				ledger.setStatus((long)1);
			        				ledger.setOffice(new S_OfficeModel(getOfficeID()));
									
									
									mdl.setName(nameField.getValue().toString());
									mdl.setAccount_type(toLong(accountRadio.getValue().toString()));
									mdl.setCreated_date(CommonUtil.getSQLDateFromUtilDate(createdDate.getValue()));
									mdl.setSubscription_type(new SubscriptionConfigurationModel(id));
									mdl.setCreated_by(getLoginID());
//									mdl.setQuantity(toDouble(quantityField.getValue().toString()));
									mdl.setLedger(ledger);
									mdl.setAvailable((long)0);
									String fileName = "";
									if (imageList.size() > 0) {
										fileName = getFileName();
									}
									mdl.setImage(fileName);
									if(type==(long)3){
										mdl.setRent_status(toLong(rentRadio.getValue().toString()));
									}
									else{
										mdl.setRent_status((long)0);
									}
									if(specialBox.getValue()==true) {
										mdl.setSpecial((long)1);
									}
									else {
										mdl.setSpecial((long)0);
									}
									long uid=scdao.update(mdl);
									if (imageLayout.getComponentCount() > 0) {
										saveImageAsPNG(fileName);
									}
									deleteImage(oldName);
									reloadSubscriptionCombo(uid);
									SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								}
								else {
									SNotification.show(getPropertyName("item_not_available"),Type.WARNING_MESSAGE);
								}
							}
							else {
								setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			delete.addClickListener(new ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event){
					try
					{
						subscriptionCombo.setComponentError(null);
						nameField.setComponentError(null);
						createdDate.setComponentError(null);
						subscriptionTypeCombo.setComponentError(null);
						final SubscriptionCreationModel mdl;
						if(subscriptionCombo.getValue() != null && !subscriptionCombo.getValue().toString().equals("0"))
						{
							mdl=scdao.getCreationModel(toLong(subscriptionCombo.getValue().toString()));
							ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											updateFilename=mdl.getImage();
											scdao.delete(mdl);
											deleteImage(updateFilename);
											reloadSubscriptionCombo(0);
											reloadSubscriptionTypeCombo(0,toLong(accountRadio.getValue().toString()));
											SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
										} 
										catch (Exception e) {
											Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
		        			        } 
		        			    }
		        			});
							
						}
						else
						{
							setRequiredError(subscriptionCombo, getPropertyName("invalid_selection"), true);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainPanel;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reloadSubscriptionCombo(long id)
	{
		List idList=null;
		try
		{
			idList = new ArrayList();
			idList.add(0,new SubscriptionCreationModel(0, "-----------Create New-----------"));
			idList.addAll(scdao.getAllSubscriptions(getOfficeID(),getLoginID()));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionCombo.setContainerDataSource(bic);
			subscriptionCombo.setItemCaptionPropertyId("name");
			subscriptionCombo.setValue(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reloadSubscriptionTypeCombo(long id,long type)
	{
		List idList=null;
		try{
			idList = new ArrayList();
			idList.addAll(dao.getSubscriptionTypes(getOfficeID(),type));
			SCollectionContainer bic = SCollectionContainer.setList(idList, "id");
			subscriptionTypeCombo.setContainerDataSource(bic);
			subscriptionTypeCombo.setItemCaptionPropertyId("name");
			subscriptionTypeCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		
		if(nameField.getValue().toString().equals("") || nameField.getValue()==null){
			valid=false;
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
		}
		else{
			setRequiredError(nameField, null, false);
		}
		
		if(createdDate.getValue() == null){
			valid=false;
			setRequiredError(createdDate, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(createdDate, null, false);
		}
		
		if(subscriptionTypeCombo.getValue() == null || subscriptionTypeCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(subscriptionTypeCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(subscriptionTypeCombo, null, false);
		}
		
		if (quantityField.getValue() == null || quantityField.getValue().toString().equals("") || quantityField.getValue().toString().equals("0")) {
			quantityField.setValue("1");
		} 
		else {
			try {
				if (toDouble(quantityField.getValue().toString()) <= 0) {
					quantityField.setValue("1");
				} 
			} 
			catch (Exception e) {
				quantityField.setValue("1");
			}
		}
		
		return valid;
	}
	
	@SuppressWarnings("rawtypes")
	public void saveImageAsPNG(String fileName) {

		File file = null;
		String singleFile = "";
		String[] fileArray = fileName.split(",");
		BufferedImage bufferedImage;

		String dir = VaadinServlet.getCurrent().getServletContext()
				.getRealPath("/")
				+ "VAADIN/themes/testappstheme/VehicleImages/";

		try {
			int i = 0;
			Iterator iter = imageList.iterator();
			while (iter.hasNext()) {
				singleFile = dir + fileArray[i].replace(',', ' ').trim();

				if (!new File(singleFile).isDirectory()
						&& fileArray[i].trim().length() > 0) {
					file = (File) iter.next();
					bufferedImage = ImageIO.read(file);
					float width = bufferedImage.getWidth(), height = bufferedImage
							.getHeight();
					if (bufferedImage.getWidth() > 500) {
						float div = width / 500;
						width = 500;
						if (div > 1)
							height = height / div;
					}

					BufferedImage newBufferedImage = new BufferedImage(
							(int) width, (int) height,
							BufferedImage.TYPE_INT_RGB);
					newBufferedImage.createGraphics().drawImage(bufferedImage,
							0, 0, (int) width, (int) height, Color.WHITE, null);

					ImageIO.write(newBufferedImage, "png", new File(singleFile));
					i++;
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	
	protected String getFileName() {

		Calendar cal = Calendar.getInstance();

		String fileName = "";

		for (int i = 0; i < imageList.size(); i++) {
			fileName += i + String.valueOf(sdf.format(cal.getTime())).trim()
					+ ".png ,";
		}
		return fileName;
	}
	
	/*private void deleteImage(long itmId) {

		try {
			String fileName = scdao.getImageName(itmId);
			if(fileName!=null) {
				String[] fileArray = fileName.split(",");
				for (int i = 0; i < fileArray.length; i++) {
					String file = VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")
							+ "VAADIN/themes/testappstheme/VehicleImages/".trim()
							+ fileArray[i].trim();
					File f = new File(file);
					if (f.exists()) {
						f.delete();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	private void deleteImage(String fileName) {

		try {
			String[] fileArray = fileName.split(",");
			for (int i = 0; i < fileArray.length; i++) {
				String file = VaadinServlet.getCurrent().getServletContext()
						.getRealPath("/")
						+ "VAADIN/themes/testappstheme/VehicleImages/".trim()
						+ fileArray[i].trim();
				File f = new File(file);
				if (f.exists()) {
					f.delete();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
