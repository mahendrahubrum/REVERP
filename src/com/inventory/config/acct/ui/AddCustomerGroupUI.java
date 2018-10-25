package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.CustomerGroupDao;
import com.inventory.config.acct.model.CustomerGroupModel;
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
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 9, 2015
 */

public class AddCustomerGroupUI extends SparkLogic {

	private static final long serialVersionUID = 3731972766390277530L;
	SPanel pannel;
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField groupListCombo;
	STextField groupNameTextField;
	STextArea detailsField;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	CustomerGroupDao objDao;

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		setSize(480, 320);
		objDao = new CustomerGroupDao();

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
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);

			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			list = objDao.getAllCustomerGroups(getOfficeID());
			CustomerGroupModel og = new CustomerGroupModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			groupListCombo = new SComboField(null, 300, list, "id", "name");
			groupListCombo
					.setInputPrompt("------------------- Create New -------------------");

			groupNameTextField = new STextField(getPropertyName("group_name"),
					300);
			detailsField = new STextArea(getPropertyName("details"),
					300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("group"));
			salLisrLay.addComponent(groupListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(groupNameTextField);
			form.addComponent(detailsField);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			pannel.setContent(hLayout);
			
			addShortcutListener(new ShortcutListener("Add New Brand",
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

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					groupListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (groupListCombo.getValue() == null
								|| groupListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								CustomerGroupModel objModel = new CustomerGroupModel();
								objModel.setName(groupNameTextField.getValue());
								objModel.setDetails(detailsField
										.getValue());
								objModel.setOfficeId(getOfficeID());
								try {
									objDao.save(objModel);
									loadOptions(objModel.getId());
									Notification.show(
											getPropertyName("Success"),
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

			groupListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (groupListCombo.getValue() != null
										&& !groupListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									CustomerGroupModel objModel = objDao
											.getCustomerGroup((Long) groupListCombo
													.getValue());

									groupNameTextField.setValue(objModel
											.getName());
									detailsField.setValue(objModel
											.getDetails());

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									groupNameTextField.setValue("");
									detailsField.setValue("");

								}

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
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
												objDao.delete((Long) groupListCombo
														.getValue());

												Notification
														.show(getPropertyName("Success"),
														Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
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
								+ groupListCombo.getValue());
						if (groupListCombo.getValue() != null) {

							if (isValid()) {

								CustomerGroupModel objModel = objDao
										.getCustomerGroup((Long) groupListCombo
												.getValue());

								objModel.setName(groupNameTextField.getValue());
								objModel.setDetails(detailsField
										.getValue());
								objModel.setOfficeId(getOfficeID());
								try {
									objDao.update(objModel);
									loadOptions(objModel.getId());
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



		} catch (Exception e) {
		}

		return pannel;
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllCustomerGroups(getOfficeID());

			CustomerGroupModel sop = new CustomerGroupModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			groupListCombo.setContainerDataSource(bic);
			groupListCombo.setItemCaptionPropertyId("name");

			groupListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (groupNameTextField.getValue() == null
				|| groupNameTextField.getValue().equals("")) {
			setRequiredError(groupNameTextField,
					getPropertyName("invalid_data"), true);
			groupNameTextField.focus();
			ret = false;
		} else
			setRequiredError(groupNameTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
