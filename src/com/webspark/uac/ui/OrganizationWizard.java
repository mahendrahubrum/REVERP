package com.webspark.uac.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.dao.SettingsDao;
import com.inventory.config.settings.model.SettingsModel;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SEncryption;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.model.CurrencyModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OrganizationWizardDao;
import com.webspark.uac.dao.RoleDao;
import com.webspark.uac.model.CountryModel;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DesignationModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;
import com.webspark.uac.model.S_UserRoleModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 * 
 * @Date 8 Nov 2013
 */

@Theme("testappstheme")
public class OrganizationWizard extends SparkLogic {

	long id = 0;

	SCollectionContainer bic;

	SFormLayout organizationForm;
	STextField organizationName;
	// SComboField country;
	SButton save = new SButton(getPropertyName("Save"));

	SFormLayout officeForm;
	STextField office_name;
	SComboField currency;
	SDateField finStartDate;
	SDateField finEndDate;
	SDateField workingDate;
	SAddressField officeAddressField;

	SFormLayout departmentForm;
	STextField department_name;

	SFormLayout designationForm;
	STextField designation_name;

	SFormLayout userForm;
	STextField user_name;
	STextField employ_code;
	SPasswordField password;
	SPasswordField confirmpassword;
	SComboField type;
	STextField first_name;
	SNativeSelect genderSelect;
	SNativeSelect salaryTypeSelect;
	SAddressField address;

	private SAddressField address1Field;

	SHorizontalLayout buttonLayout = new SHorizontalLayout();

	OrganizationWizardDao objDao = new OrganizationWizardDao();

	Wizard wizard;

	public OrganizationWizard() throws Exception {

		setSize(900, 600);

		wizard = new Wizard();

		organizationForm = new SFormLayout();
		organizationName = new STextField(getPropertyName("org_name"), 300);
		// country = new SComboField("Country :", 300, new
		// CountryDao().getCountry(), "id", "name");
		// country.setInputPrompt("-------------------------- Select --------------------------");
		address1Field = new SAddressField(3);

		organizationForm.setMargin(true);
		organizationForm.setWidth("280px");
		organizationForm.setHeight("200px");
		organizationForm.addComponent(organizationName);
		organizationForm.addComponent(address1Field);
		// organizationForm.addComponent(country);

		// For Office Details

		officeForm = new SFormLayout();
		officeForm.setMargin(true);
		officeForm.setWidth("280px");
		officeForm.setHeight("200px");
		workingDate = new SDateField(getPropertyName("working_date"), 100,
				getDateFormat());
		finStartDate = new SDateField(getPropertyName("start_fin_year"), 100,
				getDateFormat());
		finEndDate = new SDateField(getPropertyName("end_fin_year"), 100,
				getDateFormat());
		officeAddressField = new SAddressField(2);
		officeAddressField.setCaption(getPropertyName("address"));
		hideAddressComponents(officeAddressField);
		office_name = new STextField(getPropertyName("office_name"), 300);
		currency = new SComboField(getPropertyName("currency") + " :", 300,
				new CurrencyManagementDao().getlabels(), "id", "name");
		currency.setInputPrompt("-----------------Select-----------------");

		currency.setValue(getCurrencyID());

		officeForm.addComponent(office_name);
		officeForm.addComponent(currency);
		officeForm.addComponent(finStartDate);
		officeForm.addComponent(finEndDate);
		officeForm.addComponent(workingDate);
		officeForm.addComponent(officeAddressField);

		finStartDate.setValue(getFinStartDate());
		finEndDate.setValue(getFinEndDate());
		workingDate.setValue(new Date());

		departmentForm = new SFormLayout();
		departmentForm.setMargin(true);
		department_name = new STextField(getPropertyName("Department_name"));
		departmentForm.addComponent(department_name);

		designationForm = new SFormLayout();
		designationForm.setMargin(true);
		designation_name = new STextField(getPropertyName("designation_name"));
		designationForm.addComponent(designation_name);

		userForm = new SFormLayout();
		userForm.setMargin(true);
		salaryTypeSelect = new SNativeSelect(getPropertyName("salary_type"),
				160, SConstants.payroll.salaryTypes, "intKey", "value");
		employ_code = new STextField(getPropertyName("employ_code"), 160);
		user_name = new STextField(getPropertyName("login_name") + " :", 160);
		password = new SPasswordField("Password :", 160);
		confirmpassword = new SPasswordField(
				getPropertyName("confirm_password"), 160);
		type = new SComboField(getPropertyName("role"), 160,
				new RoleDao().getAllRoles(), "id", "role_name");
		type.setInputPrompt("-----------------Select-----------------");
		first_name = new STextField(getPropertyName("first_name"), 160);
		genderSelect = new SNativeSelect(getPropertyName("gender"), 160,
				SConstants.genderOptions, "charKey", "value", true);
		genderSelect.setValue((char) 'M');
		address = new SAddressField(5);
		hideAddressComponents(address);
		salaryTypeSelect.setValue(2);
		address.setCaption(getPropertyName("address"));

		userForm.addComponent(employ_code);
		userForm.addComponent(first_name);
		userForm.addComponent(type);
		userForm.addComponent(genderSelect);
		userForm.addComponent(salaryTypeSelect);
		userForm.addComponent(user_name);
		userForm.addComponent(password);
		userForm.addComponent(confirmpassword);
		userForm.addComponent(address);

		buttonLayout.addComponent(save);

		organizationForm.setSizeUndefined();

		address1Field.getCountryComboField().setValue(getCountryID());
		address1Field.setCaption(getPropertyName("address"));
		officeAddressField.getCountryComboField().setValue(getCountryID());
		address.getCountryComboField().setValue(getCountryID());

		WizardStep organizationStep = new WizardStep() {
			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub

				return true;
			}

			@Override
			public boolean onAdvance() {
				// TODO Auto-generated method stub
				if (isValid()) {
					office_name.focus();
					return true;
				} else
					return false;
			}

			@Override
			public Component getContent() {
				// TODO Auto-generated method stub
				return organizationForm;
			}

			@Override
			public String getCaption() {
				// TODO Auto-generated method stub
				return getPropertyName("organization");
			}
		};

		WizardStep officeStep = new WizardStep() {

			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				office_name.focus();
				return true;
			}

			@Override
			public boolean onAdvance() {
				// TODO Auto-generated method stub
				if (isOfficeValid()) {
					department_name.focus();
					return true;
				} else
					return false;
			}

			@Override
			public Component getContent() {
				// TODO Auto-generated method stub
				return officeForm;
			}

			@Override
			public String getCaption() {
				// TODO Auto-generated method stub
				return getPropertyName("office");
			}
		};

		WizardStep departmentStep = new WizardStep() {
			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				Notification.show(getPropertyName("next"),
						getPropertyName("next"), Type.WARNING_MESSAGE);
				return true;
			}

			@Override
			public boolean onAdvance() {
				// TODO Auto-generated method stub
				boolean isValid = true;
				if (department_name.getValue().equals("")) {
					setRequiredError(department_name,
							getPropertyName("enter_dept_name"), true);
					isValid = false;
					department_name.focus();
				} else {
					setRequiredError(department_name, null, false);
					designation_name.focus();
				}
				return isValid;

			}

			@Override
			public Component getContent() {
				// TODO Auto-generated method stub
				return departmentForm;
			}

			@Override
			public String getCaption() {
				// TODO Auto-generated method stub
				return getPropertyName("department");
			}
		};

		WizardStep designationStep = new WizardStep() {
			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean onAdvance() {
				// TODO Auto-generated method stub
				boolean isValid = true;
				if (designation_name.getValue().equals("")) {
					setRequiredError(designation_name,
							getPropertyName("enter_the_desig_name"), true);
					isValid = false;
				} else {
					setRequiredError(designation_name, null, false);
					employ_code.focus();
				}
				return isValid;
			}

			@Override
			public Component getContent() {
				// TODO Auto-generated method stub
				return designationForm;
			}

			@Override
			public String getCaption() {
				// TODO Auto-generated method stub
				return getPropertyName("designation");
			}
		};

		WizardStep userStep = new WizardStep() {
			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean onAdvance() {
				// TODO Auto-generated method stub
				boolean isValid = true;
				if (isUserValid()) {
					save();
				} else
					isValid = false;

				return isValid;
			}

			@Override
			public Component getContent() {
				// TODO Auto-generated method stub
				return userForm;
			}

			@Override
			public String getCaption() {
				// TODO Auto-generated method stub
				return getPropertyName("admin_user");
			}
		};

		wizard.addStep(organizationStep);
		wizard.addStep(officeStep);
		wizard.addStep(departmentStep);
		wizard.addStep(designationStep);
		wizard.addStep(userStep);

		setContent(wizard);

		addShortcutListener(new ShortcutListener("Submit Item",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {

				if (wizard.getNextButton().isEnabled())
					wizard.getNextButton().click();
				else
					wizard.getFinishButton().click();
			}
		});

		/*
		 * save.addClickListener(new Button.ClickListener() { public void
		 * buttonClick(ClickEvent event) { try {
		 * 
		 * 
		 * if (isValid()) { S_OrganizationModel org = new S_OrganizationModel();
		 * org.setName(organizationName.getValue()); org.setCountry(new
		 * CountryModel((Long) country.getValue()));
		 * 
		 * org.setActive('Y');
		 * 
		 * try { id = objDao.save(org);
		 * 
		 * new IDGeneratorSettingsDao().createIDGenerators(SConstants.scopes.
		 * ORGANIZATION_LEVEL , id, 0, 0);
		 * 
		 * 
		 * List settingsList=new ArrayList(); SettingsModel objModel;
		 * 
		 * objModel=new SettingsModel();
		 * objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
		 * objModel.setLevel_id(id);
		 * objModel.setSettings_name(SConstants.settings
		 * .DEFAULT_DATE_SELECTION); objModel.setValue("1");
		 * settingsList.add(objModel);
		 * 
		 * objModel=new SettingsModel();
		 * objModel.setLevel(SConstants.scopes.SYSTEM_LEVEL);
		 * objModel.setLevel_id(id);
		 * objModel.setSettings_name(SConstants.settings.DATE_FORMAT);
		 * objModel.setValue("dd/MM/yyyy"); settingsList.add(objModel); new
		 * SettingsDao().saveGlobalSettings(settingsList, id);
		 * 
		 * 
		 * 
		 * 
		 * Notification.show("Success", "Saved Successfully..!",
		 * Type.WARNING_MESSAGE);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * Notification.show("Error", "Issue occures on Saving..!",
		 * Type.WARNING_MESSAGE); e.printStackTrace(); } }
		 * 
		 * } catch (NumberFormatException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * }
		 * 
		 * });
		 */

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

	public void save() {

		S_OrganizationModel org = new S_OrganizationModel();
		org.setName(organizationName.getValue());
		org.setCountry(new CountryModel((Long) address1Field
				.getCountryComboField().getValue()));
		org.setActive('Y');
		org.setAddress(address1Field.getAddress());

		DepartmentModel depModel = new DepartmentModel();
		depModel.setName(department_name.getValue());
		// depModel.setOrganization_id();

		DesignationModel desModel = new DesignationModel();
		desModel.setName(designation_name.getValue());
		// desModel.setOrganization_id();

		try {

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

			S_OfficeModel objOfcModel = new S_OfficeModel();

			objOfcModel.setActive('Y');
			objOfcModel.setAddress(officeAddressField.getAddress());
			objOfcModel.setCountry(new CountryModel((Long) officeAddressField
					.getCountryComboField().getValue()));
			objOfcModel.setCurrency(new CurrencyModel((Long) currency
					.getValue()));
			objOfcModel.setName(office_name.getValue());
			objOfcModel.setWorkingDate(CommonUtil
					.getSQLDateFromUtilDate(workingDate.getValue()));
			objOfcModel.setFin_start_date(CommonUtil
					.getSQLDateFromUtilDate(finStartDate.getValue()));
			objOfcModel.setFin_end_date(CommonUtil
					.getSQLDateFromUtilDate(finEndDate.getValue()));

			S_LoginModel log = new S_LoginModel();
			log.setLogin_name(user_name.getValue());
			log.setPassword(SEncryption.encrypt(password.getValue()));
			// log.setOffice(new S_OfficeModel((Long)office.getValue()));
			log.setUserType(new S_UserRoleModel((Long) type.getValue()));

			UserModel userObj = new UserModel();

			// userObj.setDesignation(new
			// DesignationModel((Long)designation.getValue()));
			userObj.setFirst_name(first_name.getValue());
			userObj.setMiddle_name("");
			userObj.setLast_name("");
			userObj.setLoginId(log);
			userObj.setAddress(address.getAddress());

			userObj.setBirth_date(CommonUtil.getCurrentSQLDate());
			// userObj.setDepartment(new
			// DepartmentModel((Long)department.getValue()));
			userObj.setGender((Character) genderSelect.getValue());
			userObj.setJoining_date(CommonUtil.getCurrentSQLDate());
			userObj.setMarital_status('U');

			userObj.setEffective_date(CommonUtil.getCurrentSQLDate());
			userObj.setSalary_effective_date(CommonUtil.getCurrentSQLDate());
			userObj.setSalary_type((Integer) salaryTypeSelect.getValue());
			userObj.setEmploy_code(employ_code.getValue());


			UserModel userModel = objDao.save(org, depModel, desModel,
					objOfcModel, userObj);

			long org_id = userModel.getLoginId().getOffice().getOrganization()
					.getId();

			new SettingsDao().saveGlobalSettings(settingsList, org_id);

			new IDGeneratorSettingsDao().createIDGenerators(
					SConstants.scopes.ORGANIZATION_LEVEL, org_id, 0, 0);

			new IDGeneratorSettingsDao().createIDGenerators(
					SConstants.scopes.LOGIN_LEVEL, org_id, userModel
							.getLoginId().getOffice().getId(), userModel
							.getLoginId().getId());

			new IDGeneratorSettingsDao().createIDGenerators(
					SConstants.scopes.OFFICE_LEVEL, org_id, userModel
							.getLoginId().getOffice().getId(), 0);

			this.close();

			Notification.show(getPropertyName("save_success"),
					Type.WARNING_MESSAGE);
		} catch (Exception e) {
			// TODO: handle exception

			Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);

		}

	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean isValid = true;

		if (address1Field.getCountryComboField().getValue() == null
				|| address1Field.getCountryComboField().getValue().equals("")) {
			setRequiredError(address1Field,
					getPropertyName("invalid_selection"), true);
			isValid = false;
			address1Field.getCountryComboField().focus();
		} else
			setRequiredError(address1Field, "", false);

		if (organizationName.getValue() == null
				|| organizationName.getValue().equals("")) {
			setRequiredError(organizationName, getPropertyName("invalid_data"),
					true);
			isValid = false;
			organizationName.focus();
		} else
			setRequiredError(organizationName, getPropertyName("invalid_data"),
					false);

		return isValid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void hideAddressComponents(SAddressField officeAddressField) {

		officeAddressField.getAddressArea().setVisible(false);
		officeAddressField.getPhoneTextField().setVisible(false);
		officeAddressField.getMobileTextField().setVisible(false);
		officeAddressField.getEmailTextField().setVisible(false);

	}

	public Boolean isOfficeValid() {

		boolean isValid = true;

		isValid = officeAddressField.isValid();

		if (workingDate.getValue() == null || workingDate.getValue().equals("")) {
			setRequiredError(workingDate, getPropertyName("invalid_selection"),
					true);
			isValid = false;
			workingDate.focus();
		} else
			setRequiredError(workingDate, null, false);

		if (finEndDate.getValue() == null || finEndDate.getValue().equals("")) {
			setRequiredError(finEndDate, getPropertyName("invalid_selection"),
					true);
			isValid = false;
			finEndDate.focus();
		} else {
			setRequiredError(finEndDate, null, false);

			try {
				if (finEndDate.getValue().before(finStartDate.getValue())) {
					setRequiredError(finEndDate,
							getPropertyName("edate_greaterthan_sdate"), true);
					isValid = false;
					finEndDate.focus();
				}
			} catch (Exception e) {
				isValid = false;
			}
		}

		if (finStartDate.getValue() == null
				|| finStartDate.getValue().equals("")) {
			setRequiredError(finStartDate,
					getPropertyName("invalid_selection"), true);
			isValid = false;
			finStartDate.focus();
		} else
			setRequiredError(finStartDate, null, false);

		if (currency.getValue() == null || currency.getValue().equals("")) {
			setRequiredError(currency, getPropertyName("invalid_selection"),
					true);
			isValid = false;
			currency.focus();
		} else
			setRequiredError(currency, null, false);

		if (office_name.getValue() == null || office_name.getValue().equals("")) {
			setRequiredError(office_name, getPropertyName("invalid_data"), true);
			isValid = false;
			office_name.focus();
		} else
			setRequiredError(office_name, null, false);

		return isValid;
	}

	public Boolean isUserValid() {
		// TODO Auto-generated method stub
		boolean ret = true;
		try {

			if (!address.isValid())
				ret = false;

			if (confirmpassword.getValue() == null
					|| confirmpassword.getValue().equals("")) {
				setRequiredError(confirmpassword,
						getPropertyName("confirm_password"), true);
				ret = false;
				confirmpassword.focus();
			} else
				setRequiredError(confirmpassword,
						getPropertyName("confirm_password"), false);

			if (!confirmpassword.getValue().equals(password.getValue())) {
				setRequiredError(confirmpassword,
						getPropertyName("passwords_are_different"), true);
				ret = false;
				confirmpassword.focus();
			} else
				setRequiredError(confirmpassword, null, false);

			if (password.getValue() == null || password.getValue().equals("")) {
				setRequiredError(password, getPropertyName("password"), true);
				ret = false;
				password.focus();
			} else
				setRequiredError(password, "Password", false);

			if (user_name.getValue() == null || user_name.getValue().equals("")) {
				setRequiredError(user_name, getPropertyName("login_name"), true);
				ret = false;
				user_name.focus();
			} else
				setRequiredError(user_name, getPropertyName("login_name"),
						false);

			if (type.getValue() == null || type.getValue().equals("")) {
				setRequiredError(type, getPropertyName("invalid_selection"),
						true);
				ret = false;
				type.focus();
			} else
				setRequiredError(type, null, false);

			if (genderSelect.getValue() == null
					|| genderSelect.getValue().equals("")) {
				setRequiredError(genderSelect,
						getPropertyName("invalid_selection"), true);
				ret = false;
				genderSelect.focus();
			} else
				setRequiredError(genderSelect, null, false);

			if (first_name.getValue() == null
					|| first_name.getValue().equals("")) {
				setRequiredError(first_name, getPropertyName("invalid_data"),
						true);
				ret = false;
				first_name.focus();
			} else
				setRequiredError(first_name, getPropertyName("invalid_data"),
						false);

			if (employ_code.getValue() == null
					|| employ_code.getValue().equals("")) {
				setRequiredError(employ_code, getPropertyName("invalid_data"),
						true);
				ret = false;
				employ_code.focus();
			} else
				setRequiredError(employ_code, null, false);

			return ret;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

}
