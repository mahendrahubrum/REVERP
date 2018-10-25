package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.model.GroupModel;
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
import com.webspark.Components.SSelectionField;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.DivisionModel;

@Theme("testappstheme")
public class DivisionUI extends SparkLogic {

	private static final long serialVersionUID = 5049226945190697981L;

	SCollectionContainer bic;

	final SFormLayout content;

	SComboField divisions;
	final STextField division_name;
	final STextArea description;	
	SComboField parentListCombo;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));

	SComboField organizations;

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	DivisionDao desDao;

	SButton createNewButton;

	@SuppressWarnings("deprecation")
	public DivisionUI() throws Exception {

		setSize(500, 400);
		content = new SFormLayout();
		
		desDao=new DivisionDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// **********************************************************

		organizations = new SComboField(getPropertyName("organization"), 300,
				new OrganizationDao().getAllOrganizations(), "id", "name");

		// **********************************************************

		divisions = new SComboField(null, 300, null, "id", "name", false,
				"Create New");

		division_name = new STextField(getPropertyName("Division_name"),
				300);
		description = new STextArea(getPropertyName("description"), 300);
		parentListCombo = new SComboField(getPropertyName("parent_group"), 300);
		loadParentGroups((long)0);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("Divisions"));
		salLisrLay.addComponent(divisions);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(division_name);
		content.addComponent(organizations);
		content.addComponent(description);
		content.addComponent(parentListCombo);
		
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(save);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);

		content.addComponent(buttonLayout);

		delete.setVisible(false);
		update.setVisible(false);
		content.setSizeUndefined();

		setContent(content);

		addShortcutListener(new ShortcutListener("Add New",
				ShortcutAction.KeyCode.N,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
			@Override
			public void handleAction(Object sender, Object target) {
				loadOptions(0);
				loadParentGroups((long)0);
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

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				divisions.setValue((long) 0);
			}
		});

		organizations.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {
					bic = SCollectionContainer.setList(new UserManagementDao()
							.getAllLoginsFromOrg((Long) organizations
									.getValue()), "id");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {

						if (division_name.getValue() != null
								&& !division_name.getValue().equals("")
								&& organizations.getValue() != null) {
							int level=0;
							if((Long)parentListCombo.getValue()!=0){
								level=desDao.getLevel((Long)parentListCombo.getValue())+1;
							}
							DivisionModel lm = new DivisionModel();
							lm.setName(division_name.getValue());
							lm.setDescription(description.getValue());
							lm.setOrganization_id((Long) organizations
									.getValue());
							lm.setLevel(level);
							lm.setParent_id((Long) parentListCombo
									.getValue());

							try {
								 desDao.save(lm);
								 loadParentGroups(lm.getParent_id());
								loadOptions(lm.getId());
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

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

		divisions.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (divisions.getValue() != null
							&& !divisions.getValue().toString().equals("0")) {

						save.setVisible(false);
						delete.setVisible(true);
						update.setVisible(true);

						DivisionModel lmd = desDao.getDivision(Long
								.parseLong(divisions.getValue().toString()));

						division_name.setValue(lmd.getName());
						description.setValue(lmd.getDescription());
						parentListCombo.setValue(lmd.getParent_id());

						organizations.setValue(lmd.getOrganization_id());

						isValid();
					} else {
						save.setVisible(true);
						delete.setVisible(false);
						update.setVisible(false);

						division_name.setValue("");
						description.setValue("");
						organizations.setValue(getOrganizationID());
						parentListCombo.setValue((long)0);

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

		delete.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if(!desDao.isChildExists(Long.parseLong(divisions
							.getValue().toString()))){
					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											desDao.delete(Long.parseLong(divisions
													.getValue().toString()));

											loadOptions(0);
											loadParentGroups(0);
											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

										} catch (Exception e) {
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});
					}else{
						Notification
						.show(getPropertyName("cannot_delete. Child Exists"),
								Type.ERROR_MESSAGE);
					}

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

					if (divisions.getValue() != null) {

						if (isValid()) {

							DivisionModel op = desDao.getDivision(Long
									.parseLong(divisions.getValue()
											.toString()));
							int level=0;
							if((Long)parentListCombo.getValue()!=0){
								level=desDao.getLevel((Long)parentListCombo.getValue())+1;
							}
							op.setName(division_name.getValue());
							op.setDescription(description.getValue());
							op.setLevel(level);
							op.setParent_id((Long) parentListCombo
									.getValue());
							op.setOrganization_id((Long) organizations
									.getValue());

							try {
								desDao.Update(op);
								loadParentGroups(op.getParent_id());
								loadOptions(op.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
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


		organizations.setValue(getOrganizationID());
		loadOptions(0);

		if (isSuperAdmin() || isSystemAdmin()) {
			organizations.setEnabled(true);
		} else
			organizations.setEnabled(false);

	}

	public void loadParentGroups(long id) {
		List testList=null;
		try {
			testList = desDao
					.getDivisions(getOrganizationID());
			GroupModel sop = new GroupModel();
			sop.setId(0);
			sop.setName("NONE");
			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			bic = SCollectionContainer.setList(testList, "id");
			parentListCombo.setContainerDataSource(bic);
			parentListCombo.setItemCaptionPropertyId("name");
			parentListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = desDao.getDivisions((Long) organizations.getValue());

			DivisionModel sop = new DivisionModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			divisions
					.setInputPrompt("------------------- Create New -------------------");

			bic = SCollectionContainer.setList(testList, "id");
			divisions.setContainerDataSource(bic);
			divisions.setItemCaptionPropertyId("name");

			divisions.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// **********************************************************

	}

	@Override
	public SPanel getGUI() {
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

		if (division_name.getValue() == null
				|| division_name.getValue().equals("")) {
			setRequiredError(division_name, getPropertyName("invalid_data"),
					true);
			division_name.focus();
			ret = false;
		} else
			setRequiredError(division_name, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
