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
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.BillNameDao;
import com.webspark.model.BillNameModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddBillNames extends SparkLogic {

	long id = 0;

	SCollectionContainer bic;

	final SFormLayout content;

	SComboField modules;
	final STextField module_name;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	BillNameDao modDao = new BillNameDao();

	SButton createNewButton;

	public AddBillNames() throws Exception {
		setSize(480, 190);

		content = new SFormLayout();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		// **********************************************************

		modules = new SComboField(null, 300);
		modules.setInputPrompt("------------------- Create New -------------------");

		module_name = new STextField(getPropertyName("bill_name"), 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("bill_names"));
		salLisrLay.addComponent(modules);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(module_name);

		loadOptions(0);

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
				modules.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (modules.getValue() == null
							|| modules.getValue().toString().equals("0")) {

						if (isValid()) {
							BillNameModel mod = new BillNameModel();
							mod.setBill_name(module_name.getValue());

							try {
								id = modDao.save(mod);
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

		modules.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (modules.getValue() != null
							&& !modules.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						BillNameModel mod = modDao.getModule(Long
								.parseLong(modules.getValue().toString()));

						setWritableAll();

						module_name.setValue(mod.getBill_name());

						setReadOnlyAll();

					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						module_name.setValue("");

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
					loadOptions(Long.parseLong(modules.getValue().toString()));

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
											id = Long.parseLong(modules
													.getValue().toString());
											modDao.delete(id);

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

					if (modules.getValue() != null) {

						BillNameModel mod = modDao.getModule(Long
								.parseLong(modules.getValue().toString()));

						mod.setBill_name(module_name.getValue());

						try {
							modDao.Update(mod);
							loadOptions(mod.getId());
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
		module_name.setReadOnly(true);

		module_name.focus();
	}

	public void setWritableAll() {
		module_name.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = modDao.getAllModules();

			BillNameModel sop = new BillNameModel();
			sop.setId(0);
			sop.setBill_name("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			modules.setInputPrompt("------------------- Create New -------------------");

			bic = SCollectionContainer.setList(testList, "id");
			modules.setContainerDataSource(bic);
			modules.setItemCaptionPropertyId("bill_name");

			modules.setValue(id);

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
		if (module_name.getValue() != null
				&& !module_name.getValue().equals(""))
			return true;
		else {

			Notification.show(getPropertyName("invalid_data"),
					Type.ERROR_MESSAGE);
			return false;
		}

	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
