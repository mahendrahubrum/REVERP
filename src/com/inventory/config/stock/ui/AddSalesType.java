package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.SalesTypeModel;
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
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OfficeModel;

public class AddSalesType extends SparkLogic {

	long id;

	SPanel pannel;
	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField salestypeListCombo;
	STextField salestypeNameTextField;
	SComboField statusCombo;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	SalesTypeDao objDao = new SalesTypeDao();

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		setSize(480, 255);
		objDao = new SalesTypeDao();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));

			pannel = new SPanel();
			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			pannel.setSizeFull();
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

			list = objDao.getAllSalesTypeNames(getOfficeID());
			SalesTypeModel og = new SalesTypeModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			salestypeListCombo = new SComboField(null, 300, list, "id", "name");
			salestypeListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("SalesTypeModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			salestypeNameTextField = new STextField(
					getPropertyName("sales_type_name"), 300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("sales_type"));
			salLisrLay.addComponent(salestypeListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(salestypeNameTextField);
			form.addComponent(statusCombo);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			pannel.setContent(hLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					salestypeListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (salestypeListCombo.getValue() == null
								|| salestypeListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								SalesTypeModel objModel = new SalesTypeModel();
								objModel.setName(salestypeNameTextField
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
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

			salestypeListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (salestypeListCombo.getValue() != null
										&& !salestypeListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									edit.setVisible(true);
									delete.setVisible(true);
									update.setVisible(false);
									cancel.setVisible(false);

									SalesTypeModel objModel = objDao
											.getSalesType((Long) salestypeListCombo
													.getValue());

									setWritableAll();
									salestypeNameTextField.setValue(objModel
											.getName());
									statusCombo.setValue(objModel.getStatus());
									setReadOnlyAll();

								} else {
									save.setVisible(true);
									edit.setVisible(false);
									delete.setVisible(false);
									update.setVisible(false);
									cancel.setVisible(false);

									setWritableAll();
									salestypeNameTextField.setValue("");
									statusCombo.setValue(null);

								}

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
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
						loadOptions(Long.parseLong(salestypeListCombo
								.getValue().toString()));

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
												id = (Long) salestypeListCombo
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
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						System.out.println("Option :"
								+ salestypeListCombo.getValue());
						if (salestypeListCombo.getValue() != null) {

							if (isValid()) {

								SalesTypeModel objModel = objDao
										.getSalesType((Long) salestypeListCombo
												.getValue());

								objModel.setName(salestypeNameTextField
										.getValue());
								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									objDao.update(objModel);
									loadOptions(objModel.getId());
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
		}

		return pannel;
	}

	public void setReadOnlyAll() {
		salestypeNameTextField.setReadOnly(true);
		statusCombo.setReadOnly(true);

		salestypeNameTextField.focus();
	}

	public void setWritableAll() {
		salestypeNameTextField.setReadOnly(false);
		statusCombo.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllActiveSalesTypeNames(getOfficeID());

			SalesTypeModel sop = new SalesTypeModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			salestypeListCombo.setContainerDataSource(bic);
			salestypeListCombo.setItemCaptionPropertyId("name");

			salestypeListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (salestypeNameTextField.getValue() == null
				|| salestypeNameTextField.getValue().equals("")) {
			setRequiredError(salestypeNameTextField,
					getPropertyName("invalid_data"), true);
			salestypeNameTextField.focus();
			ret = false;
		} else
			setRequiredError(salestypeNameTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
