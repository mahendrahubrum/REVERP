package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.ItemDepartmentDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.model.ItemDepartmentModel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.model.ItemGroupModel;
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
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OrganizationModel;

public class ItemGroupPanel extends SContainerPanel {

	private static final long serialVersionUID = 8273188459540712299L;

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField groupListCombo;
	SComboField departmentCombo;
	STextField groupNameTextField;
	STextField codeTextField;
	SComboField statusCombo;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	ItemGroupDao objDao = new ItemGroupDao();

	boolean taxEnable = isTaxEnable();

	SButton newSaleButton;

	private ItemDepartmentDao depDao;

	public ItemGroupPanel() {

		taxEnable = isTaxEnable();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription(getPropertyName("add_new"));

		setSize(500, 300);
		setId("Item Group");
		objDao = new ItemGroupDao();
		depDao = new ItemDepartmentDao();

		try {

			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();


			save = new SButton(getPropertyName("Save"), 80);
			save.setStyleName("savebtnStyle");
			save.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			update = new SButton(getPropertyName("Update"), 80);
			update.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			update.setStyleName("updatebtnStyle");

			delete = new SButton(getPropertyName("Delete"), 78);
			delete.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			delete.setStyleName("deletebtnStyle");

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);
			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			list = objDao.getAllItemGroupsNames(getOrganizationID());
			ItemGroupModel og = new ItemGroupModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			groupListCombo = new SComboField(null, 300, list, "id", "name");
			groupListCombo
					.setInputPrompt(getPropertyName("create_new"));

			List deplist = new ItemDepartmentDao()
					.getAllItemDepartmentNames(getOrganizationID());

			departmentCombo = new SComboField(
					getPropertyName("item_department"), 300, deplist, "id",
					"name");
			departmentCombo
					.setInputPrompt(getPropertyName("select"));

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("ItemGroupModel", "status"),
					"value", "name", true, "Select");

			if (statusCombo.getItemIds() != null)
				statusCombo
						.setValue(statusCombo.getItemIds().iterator().next());

			// statusCombo.setInputPrompt("------------------------------ Select ------------------------------");

			groupNameTextField = new STextField(
					getPropertyName("item_grp_name"), 300, 200, true);
			codeTextField = new STextField(getPropertyName("code"), 300, 50,
					true);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("item_grp"));
			salLisrLay.addComponent(groupListCombo);
			salLisrLay.addComponent(newSaleButton);

			form.addComponent(salLisrLay);
			form.addComponent(groupNameTextField);
			form.addComponent(codeTextField);
			form.addComponent(departmentCombo);
			form.addComponent(statusCombo);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			newSaleButton.addClickListener(new Button.ClickListener() {
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
								ItemGroupModel objModel = new ItemGroupModel();
								objModel.setName(groupNameTextField.getValue());
								objModel.setCode(codeTextField.getValue());
								objModel.setItemDepartment(new ItemDepartmentModel(
										(Long) departmentCombo.getValue()));
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

									ItemGroupModel objModel = objDao
											.getItemGroup((Long) groupListCombo
													.getValue());

									groupNameTextField.setValue(objModel
											.getName());
									codeTextField.setValue(objModel.getCode());
									statusCombo.setValue(objModel.getStatus());
									departmentCombo.setValue(objModel
											.getItemDepartment().getId());



								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);
									groupNameTextField.setValue("");
									codeTextField.setValue("");
									statusCombo.setValue(null);
									departmentCombo.setValue(null);

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

			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI().getCurrent(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) groupListCombo
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
						if (groupListCombo.getValue() != null) {

							if (isValid()) {

								ItemGroupModel objModel = objDao
										.getItemGroup((Long) groupListCombo
												.getValue());

								objModel.setName(groupNameTextField.getValue());
								objModel.setCode(codeTextField.getValue());
								objModel.setOrganization(new S_OrganizationModel(
										getOrganizationID()));
								objModel.setItemDepartment(new ItemDepartmentModel(
										(Long) departmentCombo.getValue()));
								objModel.setStatus((Long) statusCombo
										.getValue());
								try {
									objDao.update(objModel);
									loadOptions(objModel.getId());
									Notification.show(getPropertyName("updated_success"),Type.WARNING_MESSAGE);
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

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllItemGroupsNames(getOrganizationID());

			ItemGroupModel sop = new ItemGroupModel();
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

	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (departmentCombo.getValue() == null
				|| departmentCombo.getValue().equals("")) {
			setRequiredError(departmentCombo,
					getPropertyName("invalid_selection"), true);
			departmentCombo.focus();
			ret = false;
		} else
			setRequiredError(departmentCombo, null, false);

		if (codeTextField.getValue() == null
				|| codeTextField.getValue().equals("")) {
			setRequiredError(codeTextField, getPropertyName("invalid_data"),
					true);
			codeTextField.focus();
			ret = false;
		} else
			setRequiredError(codeTextField, null, false);

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

	public void removeErrorMsgs() {
		statusCombo.setComponentError(null);
		codeTextField.setComponentError(null);
		groupNameTextField.setComponentError(null);
	}

	public void reloadDepartments() {
		try {
			if (departmentCombo.isReadOnly()) {
				Object obj = departmentCombo.getValue();
				departmentCombo.setReadOnly(false);
				list = depDao.getAllItemDepartmentNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				departmentCombo.setContainerDataSource(bic);
				departmentCombo.setItemCaptionPropertyId("name");
				departmentCombo.setValue(obj);
				departmentCombo.setReadOnly(true);
			} else {
				list = depDao.getAllItemDepartmentNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				departmentCombo.setContainerDataSource(bic);
				departmentCombo.setItemCaptionPropertyId("name");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
