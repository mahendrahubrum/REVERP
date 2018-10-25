package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.ItemDepartmentDao;
import com.inventory.config.stock.model.ItemDepartmentModel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
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
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.STextField;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         May 13, 2014
 */

public class ItemDepartmentPanel extends SContainerPanel {

	private static final long serialVersionUID = 4588560540244241744L;

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField depListCombo;
	STextField depNameTextField;
	STextField codeTextField;
	SComboField statusCombo;

	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;

	List list;
	ItemDepartmentDao objDao;

	boolean taxEnable = isTaxEnable();

	SButton newSaleButton;

	@SuppressWarnings("serial")
	public ItemDepartmentPanel() {

		taxEnable = isTaxEnable();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription(getPropertyName("add_new"));

		setSize(530, 300);
		setId("Item Department");
		objDao = new ItemDepartmentDao();

		try {

			hLayout = new SHorizontalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			saveButton = new SButton(getPropertyName("Save"), 80);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("Update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			deleteButton = new SButton(getPropertyName("Delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			buttonLayout.setSpacing(true);

			deleteButton.setVisible(false);
			updateButton.setVisible(false);

			list = objDao.getAllItemDepartmentNamesUnderOrganization(getOrganizationID());
			ItemDepartmentModel og = new ItemDepartmentModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			depListCombo = new SComboField(null, 300, list, "id", "name");
			depListCombo
					.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					SConstants.statuses.status, "key", "value");
			statusCombo.setValue((long) 1);

			if (statusCombo.getItemIds() != null)
				statusCombo
						.setValue(statusCombo.getItemIds().iterator().next());

			// statusCombo.setInputPrompt("------------------------------ Select ------------------------------");

			depNameTextField = new STextField(
					getPropertyName("item_department_name"), 300, 200, true);
			codeTextField = new STextField(getPropertyName("code"), 300, 50,
					true);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("item_department"));
			salLisrLay.addComponent(depListCombo);
			salLisrLay.addComponent(newSaleButton);

			form.addComponent(salLisrLay);
			form.addComponent(depNameTextField);
			form.addComponent(codeTextField);
			form.addComponent(statusCombo);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);
			
			addShortcutListener(new ShortcutListener("Add New",
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
			
			newSaleButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					depListCombo.setValue((long) 0);
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (depListCombo.getValue() == null
								|| depListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								ItemDepartmentModel objModel = new ItemDepartmentModel();
								objModel.setName(depNameTextField.getValue());
								objModel.setCode(codeTextField.getValue());
								objModel.setOrganization(new S_OrganizationModel(
										getOrganizationID()));
								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									id = objDao.save(objModel);
									loadOptions(id);
									Notification.show(
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

								} catch (Exception e) {
									Notification.show(getPropertyName("unable_to_save"),
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

			depListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (depListCombo.getValue() != null
										&& !depListCombo.getValue().toString()
												.equals("0")) {

									saveButton.setVisible(false);
									deleteButton.setVisible(true);
									updateButton.setVisible(true);

									ItemDepartmentModel objModel = objDao
											.getItemDepartment((Long) depListCombo
													.getValue());

									depNameTextField.setValue(objModel
											.getName());
									codeTextField.setValue(objModel.getCode());
									statusCombo.setValue(objModel.getStatus());


								} else {
									saveButton.setVisible(true);
									deleteButton.setVisible(false);
									updateButton.setVisible(false);
									depNameTextField.setValue("");
									codeTextField.setValue("");
									statusCombo.setValue(null);

									if (statusCombo.getItemIds() != null)
										statusCombo
												.setValue(statusCombo
														.getItemIds()
														.iterator().next());
								}

								removeErrorMsgs();

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			deleteButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("serial")
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI().getCurrent(),getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										id = (Long) depListCombo.getValue();
										objDao.delete(id);
										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadOptions(0);
									} catch (Exception e) {
										Notification.show(getPropertyName("delete_unable"),Type.ERROR_MESSAGE);
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
						System.out.println("Option :" + depListCombo.getValue());
						if (depListCombo.getValue() != null) {

							if (isValid()) {

								ItemDepartmentModel objModel = objDao
										.getItemDepartment((Long) depListCombo
												.getValue());

								objModel.setName(depNameTextField.getValue());
								objModel.setCode(codeTextField.getValue());
								objModel.setOrganization(new S_OrganizationModel(
										getOrganizationID()));
								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									objDao.update(objModel);
									Notification.show(getPropertyName("updated_success"),Type.WARNING_MESSAGE);
									loadOptions(objModel.getId());
								} catch (Exception e) {
									Notification.show(getPropertyName("unable_to_update"),Type.ERROR_MESSAGE);
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

	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllItemDepartmentNamesUnderOrganization(getOrganizationID());

			ItemDepartmentModel sop = new ItemDepartmentModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			depListCombo.setContainerDataSource(bic);
			depListCombo.setItemCaptionPropertyId("name");

			depListCombo.setValue(id);

		} catch (Exception e) {
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

		if (codeTextField.getValue() == null
				|| codeTextField.getValue().equals("")) {
			setRequiredError(codeTextField, getPropertyName("invalid_data"),
					true);
			codeTextField.focus();
			ret = false;
		} else
			setRequiredError(codeTextField, null, false);

		if (depNameTextField.getValue() == null
				|| depNameTextField.getValue().equals("")) {
			setRequiredError(depNameTextField, getPropertyName("invalid_data"),
					true);
			depNameTextField.focus();
			ret = false;
		} else
			setRequiredError(depNameTextField, null, false);

		return ret;
	}

	public void removeErrorMsgs() {
		statusCombo.setComponentError(null);
		codeTextField.setComponentError(null);
		depNameTextField.setComponentError(null);
	}

}
