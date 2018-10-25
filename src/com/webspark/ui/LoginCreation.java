package com.webspark.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SEncryption;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.dao.LoginCreationDao;
import com.webspark.dao.LoginOptionMappingDao;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.RoleDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class LoginCreation extends SparkLogic {

	private static final long serialVersionUID = -21930048209807994L;

	long id = 0;

	CollectionContainer bic;

	WrappedSession session;

	private SFormLayout content;

	private SComboField nameList;
	private STextField login_name;
	private SPasswordField password;
	private SPasswordField confirmPassword;
	private SComboField organization;
	private SComboField office;
	private SComboField type;

	private SButton save;
	private SButton edit;
	private SButton delete;
	private SButton update;
	private SButton cancel;

	private HorizontalLayout buttonLayout;

	private LoginCreationDao lcdObj;
	private LoginOptionMappingDao lomd;

	private long userType = 0;

	OfficeDao ofcDao;
	IDGeneratorSettingsDao idgDao;

	SButton createNewButton;

	public void setReadOnlyAll() {
		login_name.setReadOnly(true);
		password.setReadOnly(true);
		confirmPassword.setReadOnly(true);
		office.setReadOnly(true);
		organization.setReadOnly(true);
		type.setReadOnly(true);
	}

	public void setWritableAll() {
		login_name.setReadOnly(false);
		password.setReadOnly(false);
		confirmPassword.setReadOnly(false);
		office.setReadOnly(false);
		organization.setReadOnly(false);
		type.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			userType = lcdObj.getUser(getLoginID()).getUserType().getId();
			if (userType == 1 || userType == 2) {
				testList = lcdObj.getUsers();
			} else {
				testList = lcdObj.getActiveUsers(getOrganizationID());
			}
			S_LoginModel sop = new S_LoginModel();
			sop.setId(0);
			sop.setLogin_name("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			nameList.setInputPrompt(getPropertyName("create_new"));

			bic = CollectionContainer.fromBeans(testList, "id");
			nameList.setContainerDataSource(bic);
			nameList.setItemCaptionPropertyId("login_name");

			nameList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public SPanel getGUI() {
		setSize(500, 340);

		ofcDao = new OfficeDao();
		idgDao = new IDGeneratorSettingsDao();

		SPanel panel = new SPanel();
		panel.setSizeFull();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create new");

			session = getHttpSession();

			content = new SFormLayout();
			lcdObj = new LoginCreationDao();
			lomd = new LoginOptionMappingDao();

			buttonLayout = new HorizontalLayout();

			List testList = null;
			userType = lcdObj.getUser(getLoginID()).getUserType().getId();
			if (userType == 1 || userType == 2) {
				testList = lcdObj.getUsers();
			} else {
				testList = lcdObj.getActiveUsers(getOrganizationID());
			}
			S_LoginModel sop = new S_LoginModel();
			sop.setId(0);
			sop.setLogin_name("------------------- Create New -------------------");
			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			nameList = new SComboField(null, 300, testList, "id", "login_name");
			nameList.setInputPrompt(getPropertyName("create_new"));

			login_name = new STextField(getPropertyName("login_name"), 300);
			password = new SPasswordField(getPropertyName("password"), 300);
			confirmPassword = new SPasswordField(
					getPropertyName("confirm_password"), 300);

			organization = new SComboField(getPropertyName("organization"),
					300, new OrganizationDao().getAllOrganizations(), "id",
					"name");
			organization.setValue(getOrganizationID());
			office = new SComboField(getPropertyName("office"), 300,
					ofcDao.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			office.setValue(getOfficeID());
			type = new SComboField(getPropertyName("role"), 300,
					new RoleDao().getAllRoles(), "id", "role_name");

			organization
					.setInputPrompt(getPropertyName("select"));
			office.setInputPrompt(getPropertyName("select"));
			type.setInputPrompt(getPropertyName("select"));

			if (userType == 1 || userType == 2) {
				organization.setVisible(true);
			} else {
				organization.setVisible(false);
			}

			content.setMargin(true);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("login"));
			salLisrLay.addComponent(nameList);
			salLisrLay.addComponent(createNewButton);
			content.addComponent(salLisrLay);
			content.addComponent(login_name);
			content.addComponent(password);
			content.addComponent(confirmPassword);
			content.addComponent(organization);
			content.addComponent(office);
			content.addComponent(type);

			save = new SButton(getPropertyName("Save"));
			edit = new SButton(getPropertyName("Edit"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));
			cancel = new SButton(getPropertyName("Cancel"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(edit);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(cancel);

			content.addComponent(buttonLayout);

			edit.setVisible(false);
			delete.setVisible(false);
			update.setVisible(false);
			cancel.setVisible(false);

			panel.setContent(content);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					nameList.setValue((long) 0);
				}
			});

			organization.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (organization.getValue() != null
							&& !organization.getValue().equals("")) {
						loadOffices(toLong(organization.getValue().toString()));
					}
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (nameList.getValue() == null
								|| nameList.getValue().toString().equals("0")) {

							if (isValid() && isNotExist()) {

								S_LoginModel log = new S_LoginModel();
								log.setLogin_name(login_name.getValue());
								log.setPassword(SEncryption.encrypt(password
										.getValue()));
								log.setOffice(new S_OfficeModel((Long) office
										.getValue()));
								log.setUserType(new S_UserRoleModel(toLong(type
										.getValue().toString())));

								try {
									log = lcdObj.save(log);

									id = log.getId();

									loadOptions(id);
									Notification.show(
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

									S_OfficeModel ofcObj = ofcDao.getOffice(log
											.getOffice().getId());

									idgDao.createIDGenerators(
											SConstants.scopes.LOGIN_LEVEL,
											ofcObj.getOrganization().getId(),
											ofcObj.getId(), id);

								} catch (Exception e) {
									Notification.show(getPropertyName("error"),
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

			nameList.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					clearErrors();

					try {
						if (nameList.getValue() != null
								&& !nameList.getValue().toString().equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							S_LoginModel log = lcdObj.getUser(Long
									.parseLong(nameList.getValue().toString()));

							setWritableAll();
							login_name.setValue(log.getLogin_name());
							password.setValue(SEncryption.decrypt(log
									.getPassword()));
							confirmPassword.setValue(SEncryption.decrypt(log
									.getPassword()));
							organization.setValue(log.getOffice()
									.getOrganization().getId());
							office.setValue(log.getOffice().getId());
							type.setValue(log.getUserType().getId());

							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							login_name.setValue("");
							password.setValue("");
							confirmPassword.setValue("");
							organization.setValue(getOrganizationID());
							office.setValue(getOfficeID());
							type.setValue(null);

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			edit.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(true);
						cancel.setVisible(true);
						setWritableAll();

						session.setAttribute("oldValue", login_name.getValue());

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			cancel.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);
						loadOptions(Long.parseLong(nameList.getValue()
								.toString()));

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = Long.parseLong(nameList
														.getValue().toString());
												lcdObj.delete(id);

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}

										}
									}
								});

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						boolean notExist = false;
						if (session.getAttribute("oldValue").toString()
								.equals(login_name.getValue())) {
							notExist = true;
						} else {
							notExist = isNotExist();
						}

						boolean needRoleChange = false;

						if (isValid() && notExist) {

							S_LoginModel log = lcdObj.getUser(Long
									.parseLong(nameList.getValue().toString()));

							if (log.getUserType().getId() != (Long) type
									.getValue())
								needRoleChange = true;

							log.setLogin_name(login_name.getValue());
							log.setPassword(SEncryption.encrypt(password
									.getValue()));
							log.setOffice(new S_OfficeModel((Long) office
									.getValue()));
							log.setUserType(new S_UserRoleModel(toLong(type
									.getValue().toString())));

							try {
								lcdObj.update(log);

								if (needRoleChange) {
									lomd.updateOptionsToUserFromRole(
											log.getId(), (Long) type.getValue());
								}
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
								loadOptions(log.getId());
							} catch (Exception e) {
								Notification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}

	protected void loadOffices(long orgId) {
		List testList;
		try {

			testList = ofcDao.getAllOfficeNamesUnderOrg(orgId);

			if (testList == null)
				testList = new ArrayList();

			office.setInputPrompt("------------------- Create New -------------------");

			SCollectionContainer bic = SCollectionContainer.setList(testList,
					"id");
			office.setContainerDataSource(bic);
			office.setItemCaptionPropertyId("name");

			office.setValue(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean valid = true;
		clearErrors();
		try {

			if (login_name.getValue() == null
					|| login_name.getValue().equals("")) {
				setRequiredError(login_name, getPropertyName("invalid_data"),
						true);
				valid = false;
			}

			if (password.getValue() == null || password.getValue().equals("")) {
				setRequiredError(password, getPropertyName("invalid_data"),
						true);
				valid = false;
			}

			if (confirmPassword.getValue() == null
					|| confirmPassword.getValue().equals("")) {
				setRequiredError(confirmPassword,
						getPropertyName("invalid_data"), true);
				valid = false;
			}

			if (!password.getValue().toString()
					.equals(confirmPassword.getValue().toString())) {
				setRequiredError(confirmPassword,
						getPropertyName("invalid_data"), true);
				valid = false;
			}

			if (office.getValue() == null || office.getValue().equals("")) {
				setRequiredError(office, getPropertyName("invalid_selection"),
						true);
				valid = false;
			}
			if (organization.getValue() == null
					|| organization.getValue().equals("")) {
				setRequiredError(organization,
						getPropertyName("invalid_selection"), true);
				valid = false;
			}

			if (type.getValue() == null || type.getValue().equals("")) {
				setRequiredError(type, getPropertyName("invalid_selection"),
						true);
				valid = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}
		return valid;
	}

	private void clearErrors() {
		login_name.setComponentError(null);
		password.setComponentError(null);
		confirmPassword.setComponentError(null);
		organization.setComponentError(null);
		office.setComponentError(null);
		type.setComponentError(null);
	}

	public Boolean isNotExist() {

		try {

			if (lcdObj.isAlreadyExist(login_name.getValue())) {
				setRequiredError(login_name, getPropertyName("invalid_data"),
						true);
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
