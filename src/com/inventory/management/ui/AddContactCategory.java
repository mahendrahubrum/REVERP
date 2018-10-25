package com.inventory.management.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.management.dao.ContactCategoryDao;
import com.inventory.management.model.ContactCategoryModel;
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
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;

public class AddContactCategory extends SparkLogic {

	long id;

	SPanel pannel;
	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField categoryListCombo;
	STextField categoryNameTextField;
	SRadioButton typeRadio;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	ContactCategoryDao objDao;

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		setSize(480, 255);
		objDao = new ContactCategoryDao();

		try {

			typeRadio = new SRadioButton(getPropertyName("type"), 200,
					Arrays.asList(new KeyValue((int) 1, getPropertyName("supplier")),
							new KeyValue((int) 2, getPropertyName("customer"))), "intKey",
					"value");

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create new");

			pannel = new SPanel();
			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			pannel.setSizeFull();
			form.setSizeFull();

			save = new SButton(getPropertyName("save"));
			edit = new SButton(getPropertyName("edit"));
			delete = new SButton(getPropertyName("delete"));
			update = new SButton(getPropertyName("update"));
			cancel = new SButton(getPropertyName("cancel"));

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

			list = objDao.getAllCategoryNames(getOrganizationID());
			ContactCategoryModel og = new ContactCategoryModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			categoryListCombo = new SComboField(null, 300, list, "id", "name");
			categoryListCombo
					.setInputPrompt(getPropertyName("create_new"));

			categoryNameTextField = new STextField(
					getPropertyName("category_name"), 300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("category"));
			salLisrLay.addComponent(categoryListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(categoryNameTextField);
			form.addComponent(typeRadio);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			pannel.setContent(hLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					categoryListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (categoryListCombo.getValue() == null
								|| categoryListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								ContactCategoryModel objModel = new ContactCategoryModel();
								objModel.setName(categoryNameTextField
										.getValue());
								objModel.setType((Integer) typeRadio.getValue());
								objModel.setOrganization_id(getOrganizationID());
								try {
									id = objDao.save(objModel);
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

			categoryListCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (categoryListCombo.getValue() != null
								&& !categoryListCombo.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							ContactCategoryModel objModel = objDao
									.getSalesType((Long) categoryListCombo
											.getValue());

							setWritableAll();
							categoryNameTextField.setValue(objModel.getName());
							typeRadio.setValue(objModel.getType());
							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							categoryNameTextField.setValue("");
							typeRadio.setValue(null);

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
						loadOptions(Long.parseLong(categoryListCombo.getValue()
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

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) categoryListCombo
														.getValue();
												objDao.delete(id);

												Notification
														.show(getPropertyName("save_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												Notification
														.show(getPropertyName("error"),
																Type.WARNING_MESSAGE);
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
								+ categoryListCombo.getValue());
						if (categoryListCombo.getValue() != null) {

							if (isValid()) {

								ContactCategoryModel objModel = objDao
										.getSalesType((Long) categoryListCombo
												.getValue());

								objModel.setName(categoryNameTextField
										.getValue());
								objModel.setType((Integer) typeRadio.getValue());
								try {
									objDao.update(objModel);
									loadOptions(objModel.getId());
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

		} catch (Exception e) {
			// TODO: handle exception
		}

		return pannel;
	}

	public void setReadOnlyAll() {
		categoryNameTextField.setReadOnly(true);
		typeRadio.setReadOnly(true);

		categoryNameTextField.focus();
	}

	public void setWritableAll() {
		categoryNameTextField.setReadOnly(false);
		typeRadio.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllCategoryNames(getOrganizationID());

			ContactCategoryModel sop = new ContactCategoryModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			categoryListCombo.setContainerDataSource(bic);
			categoryListCombo.setItemCaptionPropertyId("name");

			categoryListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (typeRadio.getValue() == null || typeRadio.getValue().equals("")) {
			setRequiredError(typeRadio, getPropertyName("select_type"), true);
			typeRadio.focus();
			ret = false;
		} else
			setRequiredError(typeRadio, null, false);

		if (categoryNameTextField.getValue() == null
				|| categoryNameTextField.getValue().equals("")) {
			setRequiredError(categoryNameTextField,
					getPropertyName("enter_name"), true);
			categoryNameTextField.focus();
			ret = false;
		} else
			setRequiredError(categoryNameTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
