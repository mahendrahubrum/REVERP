package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
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
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.DepartmentModel;

@Theme("testappstheme")
public class DepartmentUI extends SparkLogic {

	long id = 0;

	CollectionContainer bic;

	final SFormLayout content;

	SComboField adminUserSelect;

	SComboField departments;
	final STextField department_name;
	final STextArea description;	

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	SComboField organizations;

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	DepartmentDao desDao = new DepartmentDao();

	SButton createNewButton;

	@SuppressWarnings("deprecation")
	public DepartmentUI() throws Exception {

		setCaption("Add Department");

		setWidth("500px");
		setHeight("340px");
		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// **********************************************************

		organizations = new SComboField(getPropertyName("organization"), 300,
				new OrganizationDao().getAllOrganizations(), "id", "name");

		// **********************************************************

		departments = new SComboField(null, 300, null, "id", "name", false,
				"Create New");

		adminUserSelect = new SComboField(getPropertyName("admin_user"), 300,
				null, "id", "login_name");

		department_name = new STextField(getPropertyName("Department_name"),
				300);
		description = new STextArea(getPropertyName("description"), 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("departments"));
		salLisrLay.addComponent(departments);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(department_name);
		content.addComponent(organizations);
		content.addComponent(adminUserSelect);
		content.addComponent(description);

		buttonLayout.setSpacing(true);
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
		content.setSizeUndefined();

		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				departments.setValue((long) 0);
			}
		});

		organizations.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(new UserManagementDao()
							.getAllLoginsFromOrg((Long) organizations
									.getValue()), "id");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adminUserSelect.setContainerDataSource(bic);
				adminUserSelect.setItemCaptionPropertyId("login_name");

			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {

						if (department_name.getValue() != null
								&& !department_name.getValue().equals("")
								&& organizations.getValue() != null) {
							DepartmentModel lm = new DepartmentModel();
							lm.setName(department_name.getValue());
							lm.setDescription(description.getValue());
							lm.setOrganization_id((Long) organizations
									.getValue());
							if (adminUserSelect.getValue() != null)
								lm.setAdmin_user_id((Long) adminUserSelect
										.getValue());
							else
								lm.setAdmin_user_id(0);

							try {
								id = desDao.save(lm);
								loadOptions(id);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		departments.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (departments.getValue() != null
							&& !departments.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						DepartmentModel lmd = desDao.getDepartment(Long
								.parseLong(departments.getValue().toString()));
						setWritableAll();

						department_name.setValue(lmd.getName());
						description.setValue(lmd.getDescription());

						adminUserSelect.setValue(lmd.getAdmin_user_id());

						organizations.setValue(lmd.getOrganization_id());

						setReadOnlyAll();
						isValid();
					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						department_name.setValue("");
						description.setValue("");
						organizations.setValue(getOrganizationID());
						adminUserSelect.setValue(getLoginID());

					}

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
					loadOptions(Long.parseLong(departments.getValue()
							.toString()));

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
											id = Long.parseLong(departments
													.getValue().toString());
											desDao.delete(id);

											loadOptions(0);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

										} catch (Exception e) {
											// TODO Auto-generated catch block
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}

										// Confirmed to continue
										// DO STUFF
									} else {
										// User did not confirm
										// CANCEL STUFF
									}
								}
							});

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (departments.getValue() != null) {

						if (isValid()) {

							DepartmentModel op = desDao.getDepartment(Long
									.parseLong(departments.getValue()
											.toString()));

							op.setName(department_name.getValue());
							op.setDescription(description.getValue());

							op.setOrganization_id((Long) organizations
									.getValue());

							if (adminUserSelect.getValue() != null)
								op.setAdmin_user_id((Long) adminUserSelect
										.getValue());
							else
								op.setAdmin_user_id(0);


							try {
								desDao.Update(op);
								loadOptions(op.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

		addShortcutListener(new ShortcutListener("Save",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (save.isVisible())
					save.click();
				else
					update.click();
			}
		});

		organizations.setValue(getOrganizationID());
		loadOptions(0);

		if (isSuperAdmin() || isSystemAdmin()) {
			organizations.setEnabled(true);
		} else
			organizations.setEnabled(false);

	}

	public void setReadOnlyAll() {
		department_name.setReadOnly(true);
		description.setReadOnly(true);
		organizations.setReadOnly(true);
		adminUserSelect.setReadOnly(true);

		department_name.focus();
	}

	public void setWritableAll() {
		department_name.setReadOnly(false);
		description.setReadOnly(false);
		organizations.setReadOnly(false);
		adminUserSelect.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = desDao.getDepartments((Long) organizations.getValue());

			DepartmentModel sop = new DepartmentModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			departments
					.setInputPrompt("------------------- Create New -------------------");

			bic = CollectionContainer.fromBeans(testList, "id");
			departments.setContainerDataSource(bic);
			departments.setItemCaptionPropertyId("name");

			departments.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// **********************************************************

	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {
		boolean ret = true;

		if (organizations.getValue() == null
				|| organizations.getValue().equals("")) {
			setRequiredError(organizations,
					getPropertyName("invalid_selection"), true);
			organizations.focus();
			ret = false;
		} else
			setRequiredError(organizations, null, false);

		if (department_name.getValue() == null
				|| department_name.getValue().equals("")) {
			setRequiredError(department_name, getPropertyName("invalid_data"),
					true);
			department_name.focus();
			ret = false;
		} else
			setRequiredError(department_name, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
