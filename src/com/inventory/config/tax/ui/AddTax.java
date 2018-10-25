package com.inventory.config.tax.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
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
import com.webspark.common.util.SConstants;
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OfficeModel;

public class AddTax extends SparkLogic {

	long id;

	SPanel pannel;
	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField taxListCombo;
	STextField taxNameTextField;

	SComboField taxTypeCombo;
	SComboField valueTypeCombo;

	STextField valueTextField;
	SComboField statusCombo;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	List list;
	TaxDao objDao = new TaxDao();

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		setSize(470, 345);
		objDao = new TaxDao();

		try {

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

			list = objDao.getAllTaxes(getOfficeID());
			TaxModel og = new TaxModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			taxListCombo = new SComboField(null, 300, list, "id", "name");
			taxListCombo
					.setInputPrompt(getPropertyName("create_new"));

			taxTypeCombo = new SComboField(getPropertyName("tax_type"), 300,
					SConstants.tax.taxTypes, "key", "value", true, getPropertyName("select"));
			valueTypeCombo = new SComboField(getPropertyName("value_type"),
					300, SConstants.tax.taxValueTypes, "key", "value", true,
					getPropertyName("select"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("TaxModel", "status"), "value",
					"name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			taxNameTextField = new STextField(getPropertyName("tax_name"), 300);

			valueTextField = new STextField(getPropertyName("value"), 300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("tax"));
			salLisrLay.addComponent(taxListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(taxNameTextField);
			form.addComponent(taxTypeCombo);
			form.addComponent(valueTypeCombo);
			form.addComponent(valueTextField);
			form.addComponent(statusCombo);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			pannel.setContent(hLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					taxListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (taxListCombo.getValue() == null
								|| taxListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								TaxModel objModel = new TaxModel();
								objModel.setName(taxNameTextField.getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setTax_type((Long) taxTypeCombo
										.getValue());
								objModel.setValue_type((Long) valueTypeCombo
										.getValue());
								objModel.setStatus((Long) statusCombo
										.getValue());
								objModel.setValue(Double
										.parseDouble(valueTextField.getValue()));
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

			taxListCombo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						if (taxListCombo.getValue() != null
								&& !taxListCombo.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							edit.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							TaxModel objModel = objDao
									.getTax((Long) taxListCombo.getValue());

							setWritableAll();
							taxNameTextField.setValue(objModel.getName());
							taxTypeCombo.setValue(objModel.getTax_type());
							valueTypeCombo.setValue(objModel.getValue_type());
							statusCombo.setValue(objModel.getStatus());
							valueTextField.setValue("" + objModel.getValue());

							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							taxNameTextField.setValue("");
							statusCombo.setValue(null);
							taxTypeCombo.setValue(null);
							valueTypeCombo.setValue(null);
							valueTextField.setValue("");
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
						loadOptions(Long.parseLong(taxListCombo.getValue()
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
												id = (Long) taxListCombo
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
						System.out.println("Option :" + taxListCombo.getValue());
						if (taxListCombo.getValue() != null) {

							if (isValid()) {

								TaxModel objModel = objDao
										.getTax((Long) taxListCombo.getValue());

								objModel.setName(taxNameTextField.getValue());
								objModel.setStatus((Long) statusCombo
										.getValue());
								objModel.setTax_type((Long) taxTypeCombo
										.getValue());
								objModel.setValue_type((Long) valueTypeCombo
										.getValue());
								objModel.setValue(Double
										.parseDouble(valueTextField.getValue()));

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
		taxNameTextField.setReadOnly(true);
		statusCombo.setReadOnly(true);
		taxTypeCombo.setReadOnly(true);
		valueTypeCombo.setReadOnly(true);
		valueTextField.setReadOnly(true);

		taxNameTextField.focus();
	}

	public void setWritableAll() {
		taxNameTextField.setReadOnly(false);
		statusCombo.setReadOnly(false);
		taxTypeCombo.setReadOnly(false);
		valueTypeCombo.setReadOnly(false);
		valueTextField.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllTaxes(getOfficeID());

			TaxModel sop = new TaxModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			taxListCombo.setContainerDataSource(bic);
			taxListCombo.setItemCaptionPropertyId("name");

			taxListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("select_status"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (valueTextField.getValue() == null
				|| valueTextField.getValue().equals("")) {
			setRequiredError(valueTextField, getPropertyName("enter_value"),
					true);
			valueTextField.focus();
			ret = false;
		} else {

			try {
				Double.parseDouble(valueTextField.getValue());
				setRequiredError(valueTextField, null, false);

			} catch (Exception e) {

				setRequiredError(valueTextField,
						getPropertyName("enter_value"), true);
				valueTextField.focus();
				ret = false;

				// TODO: handle exception
			}

		}

		if (valueTypeCombo.getValue() == null
				|| valueTypeCombo.getValue().equals("")) {
			setRequiredError(valueTypeCombo,
					getPropertyName("select_value_type"), true);
			valueTypeCombo.focus();
			ret = false;
		} else
			setRequiredError(valueTypeCombo, null, false);

		if (taxTypeCombo.getValue() == null
				|| taxTypeCombo.getValue().equals("")) {
			setRequiredError(taxTypeCombo, getPropertyName("select_tax_type"),
					true);
			taxTypeCombo.focus();
			ret = false;
		} else
			setRequiredError(taxTypeCombo, null, false);

		if (taxNameTextField.getValue() == null
				|| taxNameTextField.getValue().equals("")) {
			setRequiredError(taxNameTextField,
					getPropertyName("enter_tax_name"), true);
			taxNameTextField.focus();
			ret = false;
		} else
			setRequiredError(taxNameTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
