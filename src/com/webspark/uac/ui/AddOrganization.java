package com.webspark.uac.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.dao.SettingsDao;
import com.inventory.config.settings.model.SettingsModel;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
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
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.dao.OptionProjectMapDao;
import com.webspark.model.ProductLicenseModel;
import com.webspark.uac.dao.CountryDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.CountryModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddOrganization extends SparkLogic {

	private static final long serialVersionUID = 7341691542970343793L;

	long id = 0;
	SimpleDateFormat sdf;
	SCollectionContainer bic;

	final SFormLayout content;

	SComboField organizationList;
	final STextField organizationName;
	// final SComboField country;

	SComboField adminUserSelect, projectTypeSelect;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));

	private SFileUpload fileUpload;
	private SFileUploder fileUploder;
	private Image logoImage;

	private SCheckBox trialBox;
	private SDateField expDateField;
	
	STextField vatNumberField;

	private SAddressField address1Field;

	final SGridLayout buttonLayout = new SGridLayout(6,1);

	OrganizationDao ogDao = new OrganizationDao();

	SButton createNewButton;
	
	SHorizontalLayout mainLayout;
	 boolean imageRemoved=false;

	private static String DEFAULT_IMAGE = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/Images/no_image.png";
	private static String DIR = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/OrganizationLogos/";
	
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public AddOrganization() throws Exception {

		setCaption("Add Organization");

		setSize(820, 610);
		content = new SFormLayout();

		mainLayout = new SHorizontalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		fileUploder = new SFileUploder();
		fileUpload = new SFileUpload(getPropertyName("logo") + " : ",fileUploder);
		fileUpload.setButtonCaption(getPropertyName("upload"));
		fileUpload.setImmediate(true);
		fileUpload.setVisible(true);
		sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		 final String defaultImage="images/default.png";
		 logoImage = new Image("", new ThemeResource(defaultImage));
		logoImage.setStyleName("user_photo");
		logoImage.setWidth("200px");
		logoImage.setHeight("200px");
		logoImage.setImmediate(true);
		logoImage.markAsDirty();
		
		File file=new File(DEFAULT_IMAGE);
		if(file.exists() && !file.isDirectory())
			logoImage.setSource(new FileResource(file));
		
		SFormLayout imageLay = new SFormLayout();
		imageLay.setSpacing(true);


		List testList = ogDao.getAllOrganizations();
		S_OrganizationModel og = new S_OrganizationModel();
		og.setId(0);
		og.setName("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, og);

		organizationList = new SComboField(null, 300, testList, "id", "name");
		organizationList
				.setInputPrompt(getPropertyName("create_new"));

		organizationName = new STextField(getPropertyName("org_name"), 300);

		testList = new CountryDao().getCountry();

		adminUserSelect = new SComboField(getPropertyName("admin_user"), 300,
				new UserManagementDao().getAllLogins(), "id", "login_name");

		projectTypeSelect = new SComboField(getPropertyName("project_type"),
				300, new OptionProjectMapDao().getAllProjectTypes(), "id",
				"name");
		vatNumberField = new STextField(getPropertyName("vat_num_label"), 300);

		trialBox = new SCheckBox(getPropertyName("trial_version"));
		expDateField = new SDateField(getPropertyName("expiry_date"), 150, "dd/MM/yyyy", getWorkingDate());
		expDateField.setVisible(false);

		address1Field = new SAddressField(1);
		address1Field.setCaption(null);	

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("organization") + " :");
		salLisrLay.addComponent(organizationList);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(organizationName);
		content.addComponent(adminUserSelect);
		content.addComponent(projectTypeSelect);
		content.addComponent(vatNumberField);
		content.addComponent(trialBox);
		content.addComponent(expDateField);
		
		SVerticalLayout addrlay=new SVerticalLayout(getPropertyName("address"));
		addrlay.addComponent(address1Field);
		content.addComponent(addrlay);

		imageLay.addComponent(logoImage);
		imageLay.addComponent(fileUpload);
		
		mainLayout.addComponent(content);
		mainLayout.addComponent(imageLay);

		buttonLayout.addComponent(save,3,0);
		buttonLayout.addComponent(update,4,0);
		buttonLayout.addComponent(delete,5,0);

		buttonLayout.setSizeFull();
		buttonLayout.setSpacing(true);

		addrlay.addComponent(buttonLayout);

		delete.setVisible(false);
		update.setVisible(false);
		content.setSizeUndefined();

		setContent(mainLayout);

		trialBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (trialBox.getValue())
					expDateField.setVisible(true);
				else {
					expDateField.setVisible(false);
					expDateField.setValue(getWorkingDate());
				}
			}
		});

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				organizationList.setValue((long) 0);
			}
		});

		fileUpload.addFinishedListener(new FinishedListener() {
			
			@Override
			public void uploadFinished(FinishedEvent event) {
				if(fileUploder.getFile()!=null){
					logoImage.setSource(new FileResource(fileUploder.getFile()));
					logoImage.markAsDirty();
					imageRemoved=true;
				}
				
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (organizationList.getValue() == null || organizationList.getValue().toString().equals("0")) {

						if (isValid()) {
							S_OrganizationModel org = new S_OrganizationModel();
							String fileName="";
							org.setName(organizationName.getValue());
							org.setCountry(new CountryModel((Long) address1Field.getCountryComboField().getValue()));
							if (adminUserSelect.getValue() != null)
								org.setAdmin_user_id((Long) adminUserSelect.getValue());
							org.setProject_type((Long) projectTypeSelect.getValue());
							org.setActive('Y');
							org.setAddress(address1Field.getAddress());
							if(imageRemoved)
								fileName=getFileName(getLoginID());
							org.setLogoName(fileName);
							try {
								id = ogDao.save(org);
								if (trialBox.getValue()) {
									ProductLicenseModel licenseModel = new ProductLicenseModel();
									licenseModel.setDetails("");
									licenseModel.setExpiry_date(CommonUtil.getTimestampFromUtilDate(expDateField.getValue()));
									licenseModel.setOrganizationId(id);
									ogDao.setExpiryDate(licenseModel);
								}
								if (fileUploder.getFile() != null) {
									saveImageAsPNG(fileUploder.getFile(),DIR.trim()+fileName.trim());
								}

								new IDGeneratorSettingsDao().createIDGenerators(SConstants.scopes.ORGANIZATION_LEVEL,id, 0, 0);
								List settingsList = new ArrayList();
								SettingsModel objModel;
								objModel = new SettingsModel();
								objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
								objModel.setLevel_id(id);
								objModel.setSettings_name(SConstants.settings.DEFAULT_DATE_SELECTION);
								objModel.setValue("1");
								settingsList.add(objModel);

								objModel = new SettingsModel();
								objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
								objModel.setLevel_id(id);
								objModel.setSettings_name(SConstants.settings.DATE_FORMAT);
								objModel.setValue("dd/MM/yyyy");
								settingsList.add(objModel);
								new SettingsDao().saveGlobalSettings(settingsList, id);
								loadOptions(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

		organizationList.addValueChangeListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (fileUploder.getFile() != null) {
						fileUploder.deleteFile();
					}
					File file=new File(DEFAULT_IMAGE);
					if(file.exists() && !file.isDirectory()){
						logoImage.setSource(new FileResource(file));
					}
					imageRemoved=false;
					if (organizationList.getValue() != null && !organizationList.getValue().toString().equals("0")) {
						save.setVisible(false);
						delete.setVisible(true);
						update.setVisible(true);
						S_OrganizationModel objModel = ogDao.getOrganization((Long) organizationList.getValue());
						address1Field.clearAll();
						organizationName.setValue(objModel.getName());
						adminUserSelect.setValue(objModel.getAdmin_user_id());
						projectTypeSelect.setValue(objModel.getProject_type());
						ProductLicenseModel licModel = ogDao.getProductLicense(objModel.getId());
						if (licModel != null) {
							trialBox.setValue(true);
							expDateField.setValue(new Date(licModel.getExpiry_date().getTime()));
						} else
							trialBox.setValue(false);

						if (objModel.getAddress() != null)
							address1Field.loadAddress(objModel.getAddress().getId());
						
						if(objModel.getLogoName().length()>3){
							File imageFile=new File(DIR.trim()+objModel.getLogoName().trim());
							if(imageFile.exists() && !imageFile.isDirectory())
								logoImage.setSource(new FileResource(imageFile));
						}
					} else {
						save.setVisible(true);
						delete.setVisible(false);
						update.setVisible(false);
						organizationName.setValue("");
						adminUserSelect.setValue(null);
						projectTypeSelect.setValue(null);
						address1Field.clearAll();
						trialBox.setValue(false);
						if(file.exists() && !file.isDirectory()){
							logoImage.setSource(new FileResource(file));
						}
						imageRemoved=false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		delete.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {

								try {
									S_OrganizationModel objModel = ogDao.getOrganization((Long) organizationList.getValue());
									id = (Long) organizationList.getValue();
									File file=new File(DIR.trim()+objModel);
									if(file.exists() && !file.isDirectory())
										file.delete();
									ogDao.delete(id);
									Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
									loadOptions(0);
								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
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

		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if (organizationList.getValue() != null) {

						if (isValid()) {

							S_OrganizationModel op = ogDao.getOrganization((Long) organizationList.getValue());
							String fileName="",oldFileName="";
							oldFileName=op.getLogoName();
							op.setName(organizationName.getValue());
							op.setCountry(new CountryModel((Long) address1Field.getCountryComboField().getValue()));
							op.setAddress(address1Field.getAddress());
							if (adminUserSelect.getValue() != null)
								op.setAdmin_user_id((Long) adminUserSelect.getValue());
							op.setProject_type((Long) projectTypeSelect.getValue());
							if(imageRemoved)
								fileName=getFileName(getLoginID());
							else
								fileName=oldFileName;
							op.setLogoName(fileName);
							try {
								ogDao.update(op);
								if (trialBox.getValue()) {
									ProductLicenseModel licenseModel = new ProductLicenseModel();
									licenseModel.setDetails("");
									licenseModel.setExpiry_date(CommonUtil
											.getTimestampFromUtilDate(expDateField
													.getValue()));
									licenseModel.setOrganizationId(op.getId());
									ogDao.setExpiryDate(licenseModel);
								}

								if (fileUploder.getFile() != null) {
									saveImageAsPNG(fileUploder.getFile(),DIR.trim()+fileName.trim());
									File file=new File(DIR.trim()+oldFileName.trim());
									if(file.exists() && !file.isDirectory())
										file.delete();
								}
								loadOptions(op.getId());
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}

					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

		addShortcutListener(new ShortcutListener("Add New Purchase",
				ShortcutAction.KeyCode.N,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
			@Override
			public void handleAction(Object sender, Object target) {
				loadOptions(0);
			}
		});

	}

	public String getFileName(long id) {
		String fileName = "";
		Calendar calendar = Calendar.getInstance();
		fileName = id + "_"+ String.valueOf(sdf.format(calendar.getTime())).trim()+ ".png";
		return fileName;
	}
	
	public void saveImageAsPNG(File file, String fileName) {

		BufferedImage bufferedImage;

		try {

			// read image file
			bufferedImage = ImageIO.read(file);
			float width = bufferedImage.getWidth(), height = bufferedImage
					.getHeight();
			if (bufferedImage.getWidth() > 200) {
				float div = width / 200;
				width = 200;
				if (div > 1)
					height = height / div;
			}
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		List testList=new ArrayList();
		try {
			testList.add(0, new S_OrganizationModel(0,"Create New"));
			testList.addAll(ogDao.getAllOrganizations());
			organizationList.setInputPrompt(getPropertyName("create_new"));
			bic = SCollectionContainer.setList(testList, "id");
			organizationList.setContainerDataSource(bic);
			organizationList.setItemCaptionPropertyId("name");
			organizationList.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {

		if (organizationName.getValue() == null
				|| organizationName.getValue().equals("")) {
			setRequiredError(organizationName, getPropertyName("invalid_data"),
					true);
			return false;
		} else
			organizationName.setComponentError(null);

		if (projectTypeSelect.getValue() == null
				|| projectTypeSelect.getValue().equals("")) {
			setRequiredError(projectTypeSelect,
					getPropertyName("invalid_selection"), true);
			return false;
		} else
			projectTypeSelect.setComponentError(null);
		if (trialBox.getValue()) {
			if (expDateField.getValue() == null
					|| expDateField.getValue().equals("")) {
				setRequiredError(expDateField, getPropertyName("invalid_data"),
						true);
				return false;
			} else
				expDateField.setComponentError(null);
		}

		if (address1Field.getCountryComboField().getValue() == null
				|| address1Field.getCountryComboField().getValue().equals("")) {
			setRequiredError(address1Field,
					getPropertyName("invalid_selection"), true);
			return false;
		} else
			address1Field.getCountryComboField().setComponentError(null);

		/*
		 * if (adminUserSelect.getValue() == null ||
		 * adminUserSelect.getValue().equals("")) { Notification.show("Invalid",
		 * "Select Administrator..!", Type.ERROR_MESSAGE); return false; }
		 */

		return true;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
