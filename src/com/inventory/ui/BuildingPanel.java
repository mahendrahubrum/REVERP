package com.inventory.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.BuildingDao;
import com.inventory.model.BuildingModel;
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
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OfficeModel;

public class BuildingPanel extends SContainerPanel {

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField buildingListCombo;
	STextField buildingNameTextField;
	STextArea descriptionTextField;
	// SComboField officeCombo;
	SComboField statusCombo;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	BuildingDao objDao = new BuildingDao();

	public BuildingPanel() {
		setId("Building");
		setSize(480, 360);
		objDao = new BuildingDao();

		try {

			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

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
			buttonLayout.setSpacing(true);

			edit.setVisible(false);
			delete.setVisible(false);
			update.setVisible(false);
			cancel.setVisible(false);

			list = objDao.getAllBuildingNamesUnderOffice(getOfficeID());
			BuildingModel og = new BuildingModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			buildingListCombo = new SComboField(
					getPropertyName("ad_building_name"), 300, list, "id",
					"name");
			buildingListCombo
					.setInputPrompt(getPropertyName("create_new"));

			buildingNameTextField = new STextField(
					getPropertyName("ad_building_name"), 300);
			descriptionTextField = new STextArea(
					getPropertyName("description"), 300);

			// officeCombo=new SComboField("Office", 300, new
			// OfficeDao().getAllOfficeNamesUnderOrg(getOrganizationID()),
			// "id","name");
			// officeCombo.setInputPrompt("------------------- Select -------------------");

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("BuildingModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			if (statusCombo.getItemIds() != null)
				statusCombo
						.setValue(statusCombo.getItemIds().iterator().next());

			form.addComponent(buildingListCombo);
			form.addComponent(buildingNameTextField);
			form.addComponent(descriptionTextField);
			// form.addComponent(officeCombo);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			// officeCombo.setValue(getOfficeID());
			//
			// if(isSuperAdmin() || isSystemAdmin() || isOrganizationAdmin()) {
			// officeCombo.setEnabled(true);
			// }
			// else
			// officeCombo.setEnabled(false);

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (buildingListCombo.getValue() == null
								|| buildingListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								BuildingModel objModel = new BuildingModel();
								objModel.setName(buildingNameTextField
										.getValue());
								objModel.setDescription(descriptionTextField
										.getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									id = objDao.save(objModel);
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

			buildingListCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (buildingListCombo.getValue() != null
								&& !buildingListCombo.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							BuildingModel objModel = objDao
									.getBuilding((Long) buildingListCombo
											.getValue());

							setWritableAll();
							buildingNameTextField.setValue(objModel.getName());
							descriptionTextField.setValue(objModel
									.getDescription());
							// officeCombo.setValue(objModel.getOffice().getId());
							statusCombo.setValue(objModel.getStatus());
							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							buildingNameTextField.setValue("");
							descriptionTextField.setValue("");
							statusCombo.setValue(null);

							if (statusCombo.getItemIds() != null)
								statusCombo.setValue(statusCombo.getItemIds()
										.iterator().next());

						}
						removeErrorMsg();
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
						loadOptions(Long.parseLong(buildingListCombo.getValue()
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
												id = (Long) buildingListCombo
														.getValue();
												objDao.delete(id);

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
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
						System.out.println("Option :"
								+ buildingListCombo.getValue());
						if (buildingListCombo.getValue() != null) {

							if (isValid()) {

								BuildingModel objModel = objDao
										.getBuilding((Long) buildingListCombo
												.getValue());

								objModel.setName(buildingNameTextField
										.getValue());
								objModel.setDescription(descriptionTextField
										.getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									objDao.update(objModel);
									loadOptions(objModel.getId());
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

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setReadOnlyAll() {
		buildingNameTextField.setReadOnly(true);
		descriptionTextField.setReadOnly(true);
		// officeCombo.setReadOnly(true);
		statusCombo.setReadOnly(true);

		buildingNameTextField.focus();
	}

	public void setWritableAll() {
		buildingNameTextField.setReadOnly(false);
		descriptionTextField.setReadOnly(false);
		// officeCombo.setReadOnly(false);
		statusCombo.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllBuildingNamesUnderOffice(getOfficeID());

			BuildingModel sop = new BuildingModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			buildingListCombo.setContainerDataSource(bic);
			buildingListCombo.setItemCaptionPropertyId("name");

			buildingListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		// if(officeCombo.getValue()==null ||
		// officeCombo.getValue().equals("")){
		// setRequiredError(officeCombo, "Please Select a Group",true);
		// officeCombo.focus();
		// ret=false;
		// }
		// else
		// setRequiredError(officeCombo, null,false);

		if (buildingNameTextField.getValue() == null
				|| buildingNameTextField.getValue().equals("")) {
			setRequiredError(buildingNameTextField,
					getPropertyName("invalid_data"), true);
			buildingNameTextField.focus();
			ret = false;
		} else
			setRequiredError(buildingNameTextField, null, false);

		return ret;
	}

	public void removeErrorMsg() {
		statusCombo.setComponentError(null);
		// officeCombo.setComponentError(null);
		buildingNameTextField.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
