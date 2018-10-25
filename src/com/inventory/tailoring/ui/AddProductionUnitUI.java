package com.inventory.tailoring.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.tailoring.dao.ProductionUnitDao;
import com.inventory.tailoring.model.ProductionUnitModel;
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
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 18, 2014
 */

public class AddProductionUnitUI extends SparkLogic {

	private static final long serialVersionUID = 8230683954885799402L;
	
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField prodUnitListCombo;
	STextField prodNameTextField;
	STextArea prodDetailsTextArea;
	STextField priorityOrder;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	ProductionUnitDao objDao;

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		SPanel panel = new SPanel();
		
		setSize(510, 340);
		objDao = new ProductionUnitDao();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));

			hLayout = new SHorizontalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			panel.setSizeFull();
			form.setSizeFull();

			save = new SButton(getPropertyName(getPropertyName("save")));
			delete = new SButton(getPropertyName(getPropertyName("delete")));
			update = new SButton(getPropertyName(getPropertyName("update")));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(delete);

			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			list = objDao.getAllProductionUnits(getOfficeID());
			ProductionUnitModel og = new ProductionUnitModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			prodUnitListCombo = new SComboField(null, 300, list, "id", "name");
			prodUnitListCombo
					.setInputPrompt(getPropertyName("create_new"));

			prodNameTextField = new STextField(getPropertyName(getPropertyName("production_unit_name")),
					300);
			prodDetailsTextArea = new STextArea(getPropertyName(getPropertyName("details")),
					300);
			priorityOrder=new STextField(getPropertyName(getPropertyName("priority_order")),300);
			priorityOrder.setValue("0");

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName(getPropertyName("production_unit")));
			salLisrLay.addComponent(prodUnitListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(prodNameTextField);
			form.addComponent(prodDetailsTextArea);
			form.addComponent(priorityOrder);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			panel.setContent(hLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					prodUnitListCombo.setValue(null);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (prodUnitListCombo.getValue() == null
								|| prodUnitListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								ProductionUnitModel objModel = new ProductionUnitModel();
								objModel.setName(prodNameTextField.getValue());
								objModel.setDetails(prodDetailsTextArea
										.getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
								objModel.setPriorityOrder(toInt(priorityOrder.getValue().toString()));
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

			prodUnitListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (prodUnitListCombo.getValue() != null
										&& !prodUnitListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									ProductionUnitModel objModel = objDao
											.getBrand((Long) prodUnitListCombo
													.getValue());

									prodNameTextField.setValue(objModel
											.getName());
									prodDetailsTextArea.setValue(objModel
											.getDetails());
									priorityOrder.setValue(objModel.getPriorityOrder()+"");

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									prodNameTextField.setValue("");
									prodDetailsTextArea.setValue("");
									priorityOrder.setValue("0");
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
												objDao.delete((Long) prodUnitListCombo
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
						if (prodUnitListCombo.getValue() != null) {

							if (isValid()) {

								ProductionUnitModel objModel = objDao
										.getBrand((Long) prodUnitListCombo
												.getValue());

								objModel.setName(prodNameTextField.getValue());
								objModel.setDetails(prodDetailsTextArea
										.getValue());
								objModel.setPriorityOrder(toInt(priorityOrder.getValue().toString()));
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

		} catch (Exception e) {
		}

		return panel;
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllProductionUnits(getOfficeID());

			ProductionUnitModel sop = new ProductionUnitModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			prodUnitListCombo.setContainerDataSource(bic);
			prodUnitListCombo.setItemCaptionPropertyId("name");

			prodUnitListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (prodNameTextField.getValue() == null
				|| prodNameTextField.getValue().equals("")) {
			setRequiredError(prodNameTextField,
					getPropertyName("invalid_data"), true);
			prodNameTextField.focus();
			ret = false;
		} else
			setRequiredError(prodNameTextField, null, false);
		
		try {
			
			if (toInt(priorityOrder.getValue().toString())<0) {
				setRequiredError(priorityOrder,
						getPropertyName("invalid_data"), true);
				priorityOrder.focus();
				ret = false;
			} else
				setRequiredError(priorityOrder, null, false);
			
		} catch (Exception e) {
			setRequiredError(priorityOrder,
					getPropertyName("invalid_data"), true);
			priorityOrder.focus();
			ret = false;
		}
		
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
