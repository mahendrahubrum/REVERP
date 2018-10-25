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
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.DesignationDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.DesignationModel;

@Theme("testappstheme")
public class DesignationUI extends SparkLogic {

	long id = 0;

	CollectionContainer bic;

	final SFormLayout content;

	SComboField organizations;

	SComboField designations;
	final STextField designation_name;
	final STextArea description;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	DesignationDao desDao = new DesignationDao();

	SButton createNewButton;

	public DesignationUI() throws Exception {

		setCaption("Add Designation");

		setWidth("500px");
		setHeight("300px");
		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// **********************************************************

		// **********************************************************

		organizations = new SComboField(getPropertyName("organization"), 300,
				new OrganizationDao().getAllOrganizations(), "id", "name");

		organizations.setValue(getOrganizationID());

		designations = new SComboField(null, 300, null, "id", "name");

		loadOptions(0);

		designation_name = new STextField(getPropertyName("designation_name"),
				300);
		description = new STextArea(getPropertyName("description"), 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("designation"));
		salLisrLay.addComponent(designations);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(designation_name);
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
				designations.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {

						if (designation_name.getValue() != null
								&& !designation_name.getValue().equals("")
								&& organizations.getValue() != null) {
							DesignationModel lm = new DesignationModel();
							lm.setName(designation_name.getValue());
							lm.setDescription(description.getValue());
							lm.setOrganization_id((Long) organizations
									.getValue());

							try {
								id = desDao.addOption(lm);
								loadOptions(id);
								Notification.show(getPropertyName("Success"),
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(
										getPropertyName("Error"),
										getPropertyName("issue_occured")
												+ e.getCause(),
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

		designations.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (designations.getValue() != null
							&& !designations.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						DesignationModel lmd = desDao.getselecteditem(Long
								.parseLong(designations.getValue().toString()));
						setWritableAll();

						designation_name.setValue(lmd.getName());
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
						designation_name.setValue("");
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
					loadOptions(Long.parseLong(designations.getValue()
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
											id = Long.parseLong(designations
													.getValue().toString());
											desDao.delete(id);

											loadOptions(0);

											Notification
													.show(getPropertyName("Success"),
															getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

										} catch (Exception e) {
											// TODO Auto-generated catch block
											Notification
													.show(getPropertyName("Error"),
															getPropertyName("issue_occured")
																	+ e.getCause(),
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

					if (designations.getValue() != null) {

						if (isValid()) {

							DesignationModel op = desDao.getselecteditem(Long
									.parseLong(designations.getValue()
											.toString()));

							op.setName(designation_name.getValue());
							op.setDescription(description.getValue());
							op.setOrganization_id((Long) organizations
									.getValue());

							try {
								desDao.Update(op);
								loadOptions(op.getId());
								Notification.show(getPropertyName("Success"),
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(
										getPropertyName("Error"),
										getPropertyName("issue_occured")
												+ e.getCause(),
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
		designation_name.setReadOnly(true);
		description.setReadOnly(true);
		organizations.setReadOnly(true);
		designation_name.focus();
	}

	public void setWritableAll() {
		designation_name.setReadOnly(false);
		description.setReadOnly(false);
		organizations.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = desDao.getDesignations((Long) organizations.getValue());

			DesignationModel sop = new DesignationModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			designations
					.setInputPrompt("------------------- Create New -------------------");

			bic = CollectionContainer.fromBeans(testList, "id");
			designations.setContainerDataSource(bic);
			designations.setItemCaptionPropertyId("name");

			designations.setValue(id);

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

		if (designation_name.getValue() == null
				|| designation_name.getValue().equals("")) {
			setRequiredError(designation_name, getPropertyName("invalid_data"),
					true);
			designation_name.focus();
			ret = false;
		} else
			setRequiredError(designation_name, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
