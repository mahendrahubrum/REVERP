package com.inventory.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.dao.BuildingDao;
import com.inventory.dao.RackDao;
import com.inventory.dao.RoomDao;
import com.inventory.model.RackModel;
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

public class RackPanel extends SContainerPanel {

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField rackListCombo;
	STextField rackNumberTextField;
	STextArea descriptionTextField;
	SComboField buildingCombo;
	SComboField roomCombo;
	SComboField statusCombo;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	RackDao objDao = new RackDao();

	@SuppressWarnings("deprecation")
	public RackPanel() {

		setId("Rack");
		setSize(480, 360);
		objDao = new RackDao();

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

			list = objDao.getAllRacksUnderOffice(getOfficeID());
			RackModel og = new RackModel();
			og.setId(0);
			og.setRack_number("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			rackListCombo = new SComboField(getPropertyName("rack_no"), 300,
					list, "id", "rack_number");
			rackListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("RackModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			if (statusCombo.getItemIds() != null)
				statusCombo
						.setValue(statusCombo.getItemIds().iterator().next());

			rackNumberTextField = new STextField(getPropertyName("rack_no"),
					300);
			descriptionTextField = new STextArea(
					getPropertyName("description"), 300);

			buildingCombo = new SComboField(
					getPropertyName("building"),
					300,
					new BuildingDao()
							.getAllActiveBuildingNamesUnderOffice(getOfficeID()),
					"id", "name");
			buildingCombo
					.setInputPrompt(getPropertyName("select"));

			roomCombo = new SComboField(getPropertyName("room"), 300, null,
					"id", "room_number");
			roomCombo
					.setInputPrompt(getPropertyName("select"));

			form.addComponent(rackListCombo);
			form.addComponent(rackNumberTextField);
			form.addComponent(descriptionTextField);
			form.addComponent(buildingCombo);
			form.addComponent(roomCombo);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (rackListCombo.getValue() == null
								|| rackListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								RackModel objModel = new RackModel();
								objModel.setRack_number(rackNumberTextField
										.getValue());
								objModel.setDescription(descriptionTextField
										.getValue());
								objModel.setRoom(new RoomModel((Long) roomCombo
										.getValue()));
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

			buildingCombo.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (buildingCombo.getValue() != null) {
							if (roomCombo.isReadOnly()) {
								Object temp = roomCombo.getValue();
								roomCombo.setReadOnly(false);
								list = new RoomDao()
										.getAllRoomNamesFromBuilding((Long) buildingCombo
												.getValue());
								bic = CollectionContainer.fromBeans(list, "id");
								roomCombo.setContainerDataSource(bic);
								roomCombo
										.setItemCaptionPropertyId("room_number");
								roomCombo.setValue(temp);
								roomCombo.setReadOnly(true);
							} else {
								list = new RoomDao()
										.getAllRoomNamesFromBuilding((Long) buildingCombo
												.getValue());
								bic = CollectionContainer.fromBeans(list, "id");
								roomCombo.setContainerDataSource(bic);
								roomCombo
										.setItemCaptionPropertyId("room_number");
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

			rackListCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (rackListCombo.getValue() != null
								&& !rackListCombo.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							RackModel objModel = objDao
									.getRack((Long) rackListCombo.getValue());

							setWritableAll();
							rackNumberTextField.setValue(objModel
									.getRack_number());
							descriptionTextField.setValue(objModel
									.getDescription());
							buildingCombo.setValue(objModel.getRoom()
									.getBuilding().getId());
							roomCombo.setValue(objModel.getRoom().getId());
							statusCombo.setValue(objModel.getStatus());
							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							rackNumberTextField.setValue("");
							descriptionTextField.setValue("");
							roomCombo.setValue(null);
							statusCombo.setValue(null);

							setDefaultValues();

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
						loadOptions(Long.parseLong(rackListCombo.getValue()
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
												id = (Long) rackListCombo
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
								+ rackListCombo.getValue());
						if (rackListCombo.getValue() != null) {

							if (isValid()) {

								RackModel objModel = objDao
										.getRack((Long) rackListCombo
												.getValue());

								objModel.setRack_number(rackNumberTextField
										.getValue());
								objModel.setDescription(descriptionTextField
										.getValue());
								objModel.setRoom(new RoomModel((Long) roomCombo
										.getValue()));
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

			setDefaultValues();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setReadOnlyAll() {
		rackNumberTextField.setReadOnly(true);
		descriptionTextField.setReadOnly(true);
		roomCombo.setReadOnly(true);
		statusCombo.setReadOnly(true);
		buildingCombo.setReadOnly(true);

		rackNumberTextField.focus();
	}

	public void setWritableAll() {
		rackNumberTextField.setReadOnly(false);
		descriptionTextField.setReadOnly(false);
		roomCombo.setReadOnly(false);
		statusCombo.setReadOnly(false);
		buildingCombo.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllRacksUnderOffice(getOfficeID());

			RackModel sop = new RackModel();
			sop.setId(0);
			sop.setRack_number("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			rackListCombo.setContainerDataSource(bic);
			rackListCombo.setItemCaptionPropertyId("rack_number");

			rackListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDefaultValues() {
		Iterator itr = buildingCombo.getItemIds().iterator();
		if (itr.hasNext()) {
			buildingCombo.setValue(itr.next());
		}

		itr = null;
		itr = roomCombo.getItemIds().iterator();
		if (itr.hasNext()) {
			roomCombo.setValue(itr.next());
		}

		itr = null;
		itr = statusCombo.getItemIds().iterator();
		if (itr.hasNext()) {
			statusCombo.setValue(itr.next());
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

		if (roomCombo.getValue() == null || roomCombo.getValue().equals("")) {
			setRequiredError(roomCombo, getPropertyName("invalid_selection"),
					true);
			roomCombo.focus();
			ret = false;
		} else
			setRequiredError(roomCombo, null, false);

		if (rackNumberTextField.getValue() == null
				|| rackNumberTextField.getValue().equals("")) {
			setRequiredError(rackNumberTextField,
					getPropertyName("invalid_data"), true);
			rackNumberTextField.focus();
			ret = false;
		} else
			setRequiredError(rackNumberTextField, null, false);

		return ret;
	}

	public void removeErrorMsg() {
		statusCombo.setComponentError(null);
		roomCombo.setComponentError(null);
		rackNumberTextField.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
