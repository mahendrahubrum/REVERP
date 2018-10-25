package com.inventory.config.unit.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Anil K P
 * 
 *         Jun 24, 2013
 */
public class AddNewUnitUI extends SContainerPanel {

	private SComboField organizationComboField;
	private SComboField unitComboField;
	private STextField unitTextField;
	private STextField symbolTextField;
	private STextArea descriptionTextArea;
	private SComboField statusComboField;

	private SFormLayout formLayout;
	private SHorizontalLayout horizontalLayout;

	private SPanel mainPanel;

	private SButton saveButton;
	private SButton editButton;
	private SButton deleteButton;
	private SButton updateButton;
	private SButton cancelButton;

	private SCollectionContainer bic;
	private UnitDao unitDao;

	SButton newSaleButton;

	public AddNewUnitUI() {

		setSize(500, 400);

		unitDao = new UnitDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription(getPropertyName("add_new"));

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		formLayout = new SFormLayout();
		formLayout.setSizeFull();
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		horizontalLayout = new SHorizontalLayout();
		horizontalLayout.setMargin(true);
		horizontalLayout.setSpacing(true);

		List orgList;
		try {
			orgList = unitDao.getAllOrganizations();
		} catch (Exception e1) {
			orgList = new ArrayList();
			e1.printStackTrace();
		}
		organizationComboField = new SComboField(
				getPropertyName("organization"), 300, orgList, "id", "name");
		organizationComboField.select(getOrganizationID());
		formLayout.addComponent(organizationComboField);
		organizationComboField.setEnabled(false);

		List unitList;
		try {
			unitList = unitDao.getAllUnits(getOrganizationID());
		} catch (Exception e1) {
			unitList = new ArrayList();
			e1.printStackTrace();
		}
		UnitModel model = new UnitModel();
		model.setId(0);
		model.setName("------------------- Create New -------------------");
		unitList.add(0, model);
		unitComboField = new SComboField(null, 300, unitList, "id", "name");
		unitComboField
				.setInputPrompt("------------------- Create New -------------------");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("unit"));
		salLisrLay.addComponent(unitComboField);
		salLisrLay.addComponent(newSaleButton);
		formLayout.addComponent(salLisrLay);

		unitTextField = new STextField(getPropertyName("unit_name"), 300);
		formLayout.addComponent(unitTextField);

		symbolTextField = new STextField(getPropertyName("symbol"), 300);
		formLayout.addComponent(symbolTextField);

		descriptionTextArea = new STextArea(getPropertyName("description"));
		descriptionTextArea.setWidth("300px");
		descriptionTextArea.setHeight("50px");
		formLayout.addComponent(descriptionTextArea);

		List statusList;
		try {
			statusList = new StatusDao().getStatuses("UnitModel", "status");
		} catch (Exception e1) {
			statusList = new ArrayList();
			e1.printStackTrace();
		}
		statusComboField = new SComboField(getPropertyName("status"), 300,
				statusList, "value", "name");
		statusComboField
				.setInputPrompt("------------------- Select -------------------");
		statusComboField.setWidth("300px");
		formLayout.addComponent(statusComboField);

		saveButton = new SButton(getPropertyName("Save"));
		editButton = new SButton(getPropertyName("Edit"));
		deleteButton = new SButton(getPropertyName("Delete"));
		updateButton = new SButton(getPropertyName("Update"));
		cancelButton = new SButton(getPropertyName("Cancel"));

		editButton.setVisible(false);
		deleteButton.setVisible(false);
		updateButton.setVisible(false);
		cancelButton.setVisible(false);

		horizontalLayout.addComponent(saveButton);
		horizontalLayout.addComponent(editButton);
		horizontalLayout.addComponent(deleteButton);
		horizontalLayout.addComponent(updateButton);
		horizontalLayout.addComponent(cancelButton);

		formLayout.addComponent(horizontalLayout);

		mainPanel.setContent(formLayout);

		newSaleButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				unitComboField.setValue((long) 0);
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {

					if (unitComboField.getValue() == null
							|| unitComboField.getValue().toString().equals("0")) {

						if (isValid()) {

							UnitModel unitModel = new UnitModel();

							unitModel.setName(unitTextField.getValue());
							unitModel.setOrganization(new S_OrganizationModel(
									Long.parseLong(organizationComboField
											.getValue().toString())));
							unitModel.setStatus(Long.parseLong(statusComboField
									.getValue().toString()));
							unitModel.setSymbol(symbolTextField.getValue());
							unitModel.setDescription(descriptionTextArea
									.getValue());

							try {

								setWritableAll();
								long id = unitDao.save(unitModel);

								Iterator itt = getUI().getWindows().iterator();
								itt.next();
								getUI().removeWindow((Window) itt.next());

								loadOptions(id);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								setReadOnlyAll();

								saveButton.setVisible(false);
								editButton.setVisible(true);
								deleteButton.setVisible(true);
								updateButton.setVisible(false);
								cancelButton.setVisible(false);

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

		editButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					saveButton.setVisible(false);
					editButton.setVisible(false);
					deleteButton.setVisible(false);
					updateButton.setVisible(true);
					cancelButton.setVisible(true);
					setWritableAll();

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		cancelButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					saveButton.setVisible(false);
					editButton.setVisible(true);
					deleteButton.setVisible(true);
					updateButton.setVisible(false);
					cancelButton.setVisible(false);
					loadOptions(Long.parseLong(unitComboField.getValue()
							.toString()));

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		deleteButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											long id = Long
													.parseLong(unitComboField
															.getValue()
															.toString());
											unitDao.delete(id);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

											loadOptions(0);

											saveButton.setVisible(true);
											editButton.setVisible(false);
											deleteButton.setVisible(false);
											updateButton.setVisible(false);
											cancelButton.setVisible(false);

										} catch (Exception e) {
											Notification.show(
													getPropertyName("Error"),

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

		updateButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if (unitComboField.getValue() != null) {

						if (isValid()) {

							UnitModel unitModel = unitDao
									.getUnit((Long) unitComboField.getValue());

							unitModel.setName(unitTextField.getValue());
							unitModel.setOrganization(new S_OrganizationModel(
									Long.parseLong(organizationComboField
											.getValue().toString())));
							unitModel.setStatus(Long.parseLong(statusComboField
									.getValue().toString()));
							unitModel.setSymbol(symbolTextField.getValue());
							unitModel.setDescription(descriptionTextArea
									.getValue());
							try {
								unitDao.update(unitModel);
								loadOptions(unitModel.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);

								saveButton.setVisible(false);
								editButton.setVisible(true);
								deleteButton.setVisible(true);
								updateButton.setVisible(false);
								cancelButton.setVisible(false);

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

		statusComboField.setValue(statusComboField.getItemIds().iterator()
				.next());

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
				if (saveButton.isVisible())
					saveButton.click();
				else
					updateButton.click();
			}
		});

		unitComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (unitComboField.getValue() != null
							&& !unitComboField.getValue().toString()
									.equals("0")) {

						saveButton.setVisible(false);
						editButton.setVisible(true);
						deleteButton.setVisible(true);
						updateButton.setVisible(false);
						cancelButton.setVisible(false);

						UnitModel unitModel = unitDao
								.getUnit((Long) unitComboField.getValue());

						setWritableAll();
						// organizationComboField.setValue(unitModel
						// .getOrganization().getId());
						unitTextField.setValue(unitModel.getName());
						symbolTextField.setValue(unitModel.getSymbol());
						descriptionTextArea.setValue(unitModel.getDescription());
						statusComboField.setValue(unitModel.getStatus());

						setReadOnlyAll();

					} else {
						saveButton.setVisible(true);
						editButton.setVisible(false);
						deleteButton.setVisible(false);
						updateButton.setVisible(false);
						cancelButton.setVisible(false);

						setWritableAll();
						// organizationComboField.select(getOrganizationID());
						unitTextField.setValue("");
						symbolTextField.setValue("");
						descriptionTextArea.setValue("");
						statusComboField.setValue("");

					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		organizationComboField
				.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChange(ValueChangeEvent event) {

						System.out.println("Changed");
						loadOptions(0);

					}
				});

		setContent(mainPanel);

	}

	public void loadOptions(long id) {
		List testList;
		try {

			testList = unitDao.getAllUnits(Long
					.parseLong(organizationComboField.getValue().toString()));
			UnitModel model = new UnitModel();
			model.setId(0);
			model.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, model);

			unitComboField
					.setInputPrompt("------------------- Create New -------------------");

			bic = SCollectionContainer.setList(testList, "id");
			unitComboField.setContainerDataSource(bic);
			unitComboField.setItemCaptionPropertyId("name");

			unitComboField.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void setWritableAll() {
		organizationComboField.setReadOnly(false);
		unitTextField.setReadOnly(false);
		symbolTextField.setReadOnly(false);
		descriptionTextArea.setReadOnly(false);
		statusComboField.setReadOnly(false);
	}

	protected void setReadOnlyAll() {

		organizationComboField.setReadOnly(true);
		unitTextField.setReadOnly(true);
		symbolTextField.setReadOnly(true);
		descriptionTextArea.setReadOnly(true);
		statusComboField.setReadOnly(true);

		organizationComboField.focus();
	}

	public Boolean isValid() {

		boolean flag = true;

		if (organizationComboField.getValue() == null
				|| organizationComboField.getValue().equals("")) {
			setRequiredError(organizationComboField,
					getPropertyName("invalid_selection"), true);
			flag = false;
		}

		if (unitTextField.getValue() == null
				|| unitTextField.getValue().equals("")) {
			setRequiredError(unitTextField, getPropertyName("invalid_data"),
					true);
			flag = false;
		}
		if (symbolTextField.getValue() == null
				|| symbolTextField.getValue().equals("")) {
			setRequiredError(symbolTextField, getPropertyName("invalid_data"),
					true);
			flag = false;
		}
		if (statusComboField.getValue() == null
				|| statusComboField.getValue().equals("")) {
			setRequiredError(statusComboField,
					getPropertyName("invalid_selection"), true);
			flag = false;
		}
		return flag;
	}

	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
