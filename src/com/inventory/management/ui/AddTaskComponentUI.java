package com.inventory.management.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.management.dao.TaskComponentDao;
import com.inventory.management.model.TaskComponentModel;
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
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.OrganizationDao;

@Theme("testappstheme")
public class AddTaskComponentUI extends SparkLogic {

	long id = 0;

	CollectionContainer bic;

	final SFormLayout content;

	SComboField organizations;

	SComboField taskComponents;
	final STextField component_name;
	final STextArea description;

	final SButton save = new SButton(getPropertyName("save"));
	final SButton edit = new SButton(getPropertyName("edit"));
	final SButton delete = new SButton(getPropertyName("delete"));
	final SButton update = new SButton(getPropertyName("update"));
	final SButton cancel = new SButton(getPropertyName("cancel"));

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	TaskComponentDao objDao = new TaskComponentDao();

	SButton createNewButton;

	public AddTaskComponentUI() throws Exception {

		setWidth("500px");
		setHeight("300px");
		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Create new");

		// **********************************************************

		// **********************************************************

		organizations = new SComboField(getPropertyName("organization"), 300,
				new OrganizationDao().getAllOrganizations(), "id", "name");

		organizations.setValue(getOrganizationID());

		taskComponents = new SComboField(null, 300, null, "id", "name");

		loadOptions(0);

		component_name = new STextField(getPropertyName("component_name"), 300);
		description = new STextArea(getPropertyName("description"), 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("task_components"));
		salLisrLay.addComponent(taskComponents);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(component_name);
		content.addComponent(organizations);
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

		if (isSuperAdmin() || isSystemAdmin()) {
			organizations.setEnabled(true);
		} else
			organizations.setEnabled(false);

		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				taskComponents.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {

						if (component_name.getValue() != null
								&& !component_name.getValue().equals("")
								&& organizations.getValue() != null) {
							TaskComponentModel lm = new TaskComponentModel();
							lm.setName(component_name.getValue());
							lm.setDescription(description.getValue());
							lm.setOrganization_id((Long) organizations
									.getValue());

							try {
								id = objDao.save(lm);
								loadOptions(id);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(getPropertyName("error"),
										Type.WARNING_MESSAGE);
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

		taskComponents.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (taskComponents.getValue() != null
							&& !taskComponents.getValue().toString()
									.equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						TaskComponentModel lmd = objDao.getComponent(Long
								.parseLong(taskComponents.getValue().toString()));
						setWritableAll();

						component_name.setValue(lmd.getName());
						description.setValue(lmd.getDescription());
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
						component_name.setValue("");
						description.setValue("");
						organizations.setValue(getOrganizationID());

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
					loadOptions(Long.parseLong(taskComponents.getValue()
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

					ConfirmDialog.show(getUI(), "Are you sure..?",
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											id = Long.parseLong(taskComponents
													.getValue().toString());
											objDao.delete(id);

											loadOptions(0);

											Notification
													.show(getPropertyName("save_success"),
															Type.WARNING_MESSAGE);

										} catch (Exception e) {
											// TODO Auto-generated catch block
											Notification.show(
													getPropertyName("error"),
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

					if (taskComponents.getValue() != null) {

						if (isValid()) {

							TaskComponentModel op = objDao.getComponent(Long
									.parseLong(taskComponents.getValue()
											.toString()));

							op.setName(component_name.getValue());
							op.setDescription(description.getValue());
							op.setOrganization_id((Long) organizations
									.getValue());

							try {
								objDao.Update(op);
								loadOptions(op.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("error"),
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
		component_name.setReadOnly(true);
		description.setReadOnly(true);
		organizations.setReadOnly(true);
		component_name.focus();
	}

	public void setWritableAll() {
		component_name.setReadOnly(false);
		description.setReadOnly(false);
		organizations.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = objDao
					.getComponentNames((Long) organizations.getValue());

			TaskComponentModel sop = new TaskComponentModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			taskComponents
					.setInputPrompt("------------------- Create New -------------------");

			bic = CollectionContainer.fromBeans(testList, "id");
			taskComponents.setContainerDataSource(bic);
			taskComponents.setItemCaptionPropertyId("name");

			taskComponents.setValue(id);

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
					getPropertyName("select_organization"), true);
			organizations.focus();
			ret = false;
		} else
			setRequiredError(organizations, null, false);

		if (component_name.getValue() == null
				|| component_name.getValue().equals("")) {
			setRequiredError(component_name, getPropertyName("enter_name"),
					true);
			component_name.focus();
			ret = false;
		} else
			setRequiredError(component_name, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
