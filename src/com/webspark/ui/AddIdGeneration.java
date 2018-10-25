package com.webspark.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.model.S_IDGeneratorSettingsModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddIdGeneration extends SparkLogic {

	long id = 0;

	SCollectionContainer bic;

	final SFormLayout content;

	SComboField id_list;
	final STextField id_name;
	final SComboField scope;
	final STextField initial_value;
	final SNativeSelect resetModeSelect;

	final SButton save = new SButton(getPropertyName("save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	IDGeneratorSettingsDao idDao = new IDGeneratorSettingsDao();

	SButton createNewButton;

	public AddIdGeneration() throws Exception {

		setWidth("360px");
		setHeight("290px");

		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// **********************************************************

		List testList = idDao.getAllIDGenerators();
		S_IDGeneratorSettingsModel sop = new S_IDGeneratorSettingsModel();
		sop.setId(0);
		sop.setId_name("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, sop);
		// **********************************************************

		id_list = new SComboField(null, 200);
		id_list.setInputPrompt(getPropertyName("create_new"));

		bic = SCollectionContainer.setList(testList, "id");
		id_list.setContainerDataSource(bic);
		id_list.setItemCaptionPropertyId("id_name");

		id_name = new STextField(getPropertyName("ID_name"), 200);
		// scope=new SComboBox("Scope :", Constants.scopes.scopeList,300);//new
		// SComboField("Scope :",300,scopeList);

		scope = new SComboField(getPropertyName("scope"), 200,
				SConstants.scopes.scopeList, "key", "value");
		scope.setInputPrompt(getPropertyName("select"));
		scope.setNullSelectionAllowed(false);

		initial_value = new STextField(getPropertyName("initial_val"), 200);
		resetModeSelect = new SNativeSelect(getPropertyName("reset_mode"), 200,
				SConstants.resetModeList, "intKey", "value");

		id_list.setImmediate(true);
		id_list.setNullSelectionAllowed(false);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("IDs"));
		salLisrLay.addComponent(id_list);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(id_name);
		content.addComponent(scope);
		content.addComponent(initial_value);
		content.addComponent(resetModeSelect);

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

		resetModeSelect.setValue(4);

		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				id_list.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (id_list.getValue() == null
							|| id_list.getValue().toString().equals("0")) {

						if (id_name.getValue() != null
								&& !id_name.getValue().equals("")) {
							S_IDGeneratorSettingsModel sid = new S_IDGeneratorSettingsModel();
							sid.setId_name(id_name.getValue());
							sid.setScope(Integer.parseInt(String.valueOf(scope
									.getValue())));
							sid.setInitial_value(Long.parseLong(initial_value
									.getValue().toString()));
							sid.setReset_mode((Integer) resetModeSelect
									.getValue());

							try {
								id = idDao.saveIDGeneratorSettings(sid);
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

		id_list.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (id_list.getValue() != null
							&& !id_list.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						S_IDGeneratorSettingsModel sid = idDao
								.getIDSettings(Long.parseLong(id_list
										.getValue().toString()));

						setWritableAll();

						id_name.setValue(sid.getId_name());
						scope.setValue((long) sid.getScope());
						initial_value.setValue(String.valueOf(sid
								.getInitial_value()));
						resetModeSelect.setValue(sid.getReset_mode());

						setReadOnlyAll();

					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						id_name.setValue("");
						scope.setValue(null);
						initial_value.setValue("0");
						resetModeSelect.setValue(4);

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
					loadOptions(Long.parseLong(id_list.getValue().toString()));

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
											id = Long.parseLong(id_list
													.getValue().toString());
											idDao.delete(id);

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

					if (id_list.getValue() != null) {

						S_IDGeneratorSettingsModel sid = idDao
								.getIDSettings(Long.parseLong(id_list
										.getValue().toString()));

						sid.setId_name(id_name.getValue());
						sid.setScope(Integer.parseInt(String.valueOf(scope
								.getValue())));
						sid.setInitial_value(Long.parseLong(initial_value
								.getValue().toString()));
						sid.setReset_mode((Integer) resetModeSelect.getValue());

						try {
							idDao.Update(sid);
							loadOptions(sid.getId());
						} catch (Exception e) {
							Notification.show(
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		id_name.setReadOnly(true);
		scope.setReadOnly(true);
		initial_value.setReadOnly(true);
		resetModeSelect.setReadOnly(true);

		id_name.focus();
	}

	public void setWritableAll() {
		id_name.setReadOnly(false);
		scope.setReadOnly(false);
		initial_value.setReadOnly(false);
		resetModeSelect.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {

			testList = idDao.getAllIDGenerators();
			S_IDGeneratorSettingsModel sid = new S_IDGeneratorSettingsModel();
			sid.setId(0);
			sid.setId_name("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sid);

			id_list.setInputPrompt("------------------- Create New -------------------");

			bic = SCollectionContainer.setList(testList, "id");
			id_list.setContainerDataSource(bic);
			id_list.setItemCaptionPropertyId("id_name");

			id_list.setValue(id);

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
