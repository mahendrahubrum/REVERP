package com.inventory.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.BuildingDao;
import com.inventory.dao.RoomDao;
import com.inventory.model.BuildingModel;
import com.inventory.model.RoomModel;
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

public class RoomPanel extends SContainerPanel {

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField roomListCombo;
	STextField roomNumberTextField;
	STextArea descriptionTextField;
	SComboField buildingCombo;
	SComboField statusCombo;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	RoomDao objDao;
	BuildingDao bldDao;

	public RoomPanel() {
		setId("Room");
		setSize(480, 360);
		objDao = new RoomDao();
		bldDao = new BuildingDao();

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

			list = objDao.getAllRoomNamesUnderOffice(getOfficeID());
			RoomModel og = new RoomModel();
			og.setId(0);
			og.setRoom_number("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			roomListCombo = new SComboField(getPropertyName("room_no"), 300,
					list, "id", "room_number");
			roomListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("RoomModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			if (statusCombo.getItemIds() != null)
				statusCombo
						.setValue(statusCombo.getItemIds().iterator().next());

			roomNumberTextField = new STextField(getPropertyName("room_no"),
					300);
			descriptionTextField = new STextArea(
					getPropertyName("description"), 300);

			buildingCombo = new SComboField(getPropertyName("building"), 300,
					bldDao.getAllActiveBuildingNamesUnderOffice(getOfficeID()),
					"id", "name");
			buildingCombo
					.setInputPrompt(getPropertyName("select"));

			form.addComponent(roomListCombo);
			form.addComponent(roomNumberTextField);
			form.addComponent(descriptionTextField);
			form.addComponent(buildingCombo);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (roomListCombo.getValue() == null
								|| roomListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								RoomModel objModel = new RoomModel();
								objModel.setRoom_number(roomNumberTextField
										.getValue());
								objModel.setDescription(descriptionTextField
										.getValue());
								objModel.setBuilding(new BuildingModel(
										(Long) buildingCombo.getValue()));
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

			roomListCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (roomListCombo.getValue() != null
								&& !roomListCombo.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							RoomModel objModel = objDao
									.getRoom((Long) roomListCombo.getValue());

							setWritableAll();
							roomNumberTextField.setValue(objModel
									.getRoom_number());
							descriptionTextField.setValue(objModel
									.getDescription());
							buildingCombo.setValue(objModel.getBuilding()
									.getId());
							statusCombo.setValue(objModel.getStatus());
							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							roomNumberTextField.setValue("");
							descriptionTextField.setValue("");
							buildingCombo.setValue(null);
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
						loadOptions(Long.parseLong(roomListCombo.getValue()
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
												id = (Long) roomListCombo
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
								+ roomListCombo.getValue());
						if (roomListCombo.getValue() != null) {

							if (isValid()) {

								RoomModel objModel = objDao
										.getRoom((Long) roomListCombo
												.getValue());

								objModel.setRoom_number(roomNumberTextField
										.getValue());
								objModel.setDescription(descriptionTextField
										.getValue());
								objModel.setBuilding(new BuildingModel(
										(Long) buildingCombo.getValue()));
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
		roomNumberTextField.setReadOnly(true);
		descriptionTextField.setReadOnly(true);
		buildingCombo.setReadOnly(true);
		statusCombo.setReadOnly(true);

		roomNumberTextField.focus();
	}

	public void setWritableAll() {
		roomNumberTextField.setReadOnly(false);
		descriptionTextField.setReadOnly(false);
		buildingCombo.setReadOnly(false);
		statusCombo.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllRoomNamesUnderOffice(getOfficeID());

			RoomModel sop = new RoomModel();
			sop.setId(0);
			sop.setRoom_number("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			roomListCombo.setContainerDataSource(bic);
			roomListCombo.setItemCaptionPropertyId("room_number");

			roomListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadBuilding() {
		try {
			if (buildingCombo.isReadOnly()) {
				Object obj = buildingCombo.getValue();
				buildingCombo.setReadOnly(false);
				list = bldDao
						.getAllActiveBuildingNamesUnderOffice(getOfficeID());
				bic = CollectionContainer.fromBeans(list, "id");
				buildingCombo.setContainerDataSource(bic);
				buildingCombo.setItemCaptionPropertyId("name");
				buildingCombo.setValue(obj);
				buildingCombo.setReadOnly(true);
			} else {
				list = bldDao
						.getAllActiveBuildingNamesUnderOffice(getOfficeID());
				bic = CollectionContainer.fromBeans(list, "id");
				buildingCombo.setContainerDataSource(bic);
				buildingCombo.setItemCaptionPropertyId("name");
			}

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

		if (buildingCombo.getValue() == null
				|| buildingCombo.getValue().equals("")) {
			setRequiredError(buildingCombo,
					getPropertyName("invalid_selection"), true);
			buildingCombo.focus();
			ret = false;
		} else
			setRequiredError(buildingCombo, null, false);

		if (roomNumberTextField.getValue() == null
				|| roomNumberTextField.getValue().equals("")) {
			setRequiredError(roomNumberTextField,
					getPropertyName("invalid_data"), true);
			roomNumberTextField.focus();
			ret = false;
		} else
			setRequiredError(roomNumberTextField, null, false);

		return ret;
	}

	public void removeErrorMsg() {
		statusCombo.setComponentError(null);
		buildingCombo.setComponentError(null);
		roomNumberTextField.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
