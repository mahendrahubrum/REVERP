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
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.RoleDao;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddUserRole extends SparkLogic {

	long id = 0;

	CollectionContainer bic;

	final SFormLayout content;

	SComboField roles;
	final STextField role_name;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	RoleDao rolDao = new RoleDao();

	SButton createNewButton;

	public AddUserRole() throws Exception {

		setCaption("Role Management");

		setWidth("470px");
		setHeight("190px");
		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// **********************************************************

		List testList = rolDao.getAllRoles();
		S_UserRoleModel sop = new S_UserRoleModel();
		sop.setId(0);
		sop.setRole_name("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, sop);
		// **********************************************************

		roles = new SComboField(null, 300, testList, "id", "role_name");
		roles.setInputPrompt(getPropertyName("create_new"));

		role_name = new STextField(getPropertyName("role_name"), 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("role"));
		salLisrLay.addComponent(roles);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(role_name);

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
				roles.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (roles.getValue() == null
							|| roles.getValue().toString().equals("0")) {

						if (isValid()) {
							S_UserRoleModel rol = new S_UserRoleModel();
							rol.setRole_name(role_name.getValue());
							rol.setActive('Y');
							try {
								id = rolDao.saveRole(rol);
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

		roles.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (roles.getValue() != null
							&& !roles.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						S_UserRoleModel mod = rolDao.getRole(Long
								.parseLong(roles.getValue().toString()));

						setWritableAll();

						role_name.setValue(mod.getRole_name());

						setReadOnlyAll();

						isValid();

					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						role_name.setValue("");

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
					loadOptions(Long.parseLong(roles.getValue().toString()));

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
											id = Long.parseLong(roles
													.getValue().toString());
											rolDao.delete(id);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

											loadOptions(0);

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

					if (roles.getValue() != null) {

						if (isValid()) {

							S_UserRoleModel mod = rolDao.getRole(Long
									.parseLong(roles.getValue().toString()));

							mod.setRole_name(role_name.getValue());

							try {
								rolDao.Update(mod);
								loadOptions(mod.getId());
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

	}

	public void setReadOnlyAll() {
		role_name.setReadOnly(true);

		role_name.focus();
	}

	public void setWritableAll() {
		role_name.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = rolDao.getAllRoles();

			S_UserRoleModel sop = new S_UserRoleModel();
			sop.setId(0);
			sop.setRole_name("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			roles.setInputPrompt("------------------- Create New -------------------");

			bic = CollectionContainer.fromBeans(testList, "id");
			roles.setContainerDataSource(bic);
			roles.setItemCaptionPropertyId("role_name");

			roles.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (role_name.getValue() != null && !role_name.getValue().equals("")) {
			role_name.setComponentError(null);
			return true;
		} else {
			setRequiredError(role_name, getPropertyName("invalid_data"), true);
			return false;
		}
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
