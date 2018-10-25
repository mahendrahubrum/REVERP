package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.ReasonDao;
import com.inventory.config.stock.model.ReasonModel;
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
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author anil
 * @date 13-Jun-2016
 * @Project REVERP
 */

public class AddReasonUI extends SparkLogic {

	private static final long serialVersionUID = -567010017158345761L;
	SPanel pannel;
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField reasonCombo;
	STextField reasonTextField;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	ReasonDao objDao;

	SButton createNewButton;
	
	@Override
	public SPanel getGUI() {

		setSize(580, 255);
		objDao = new ReasonDao();

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

			list = objDao.getAllReasonModel(getOfficeID());
			ReasonModel og = new ReasonModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			reasonCombo = new SComboField(null, 300, list, "id", "name");
			reasonCombo
					.setInputPrompt("------------------- Create New -------------------");

			reasonTextField = new STextField(getPropertyName("Reason"),
					300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("Reason"));
			salLisrLay.addComponent(reasonCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(reasonTextField);
			
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
					reasonCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (reasonCombo.getValue() == null
								|| reasonCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								ReasonModel objModel = new ReasonModel();
								objModel.setName(reasonTextField.getValue());
								objModel.setOffice(new S_OfficeModel(
										getOfficeID()));
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

			reasonCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (reasonCombo.getValue() != null
										&& !reasonCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									ReasonModel objModel = objDao
											.getReasonModel((Long) reasonCombo
													.getValue());

									reasonTextField.setValue(objModel
											.getName());

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									reasonTextField.setValue("");

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
												objDao.delete((Long) reasonCombo
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
								+ reasonCombo.getValue());
						if (reasonCombo.getValue() != null) {

							if (isValid()) {

								ReasonModel objModel = objDao
										.getReasonModel((Long) reasonCombo
												.getValue());

								objModel.setName(reasonTextField.getValue());
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
			list = objDao.getAllReasonModel(getOfficeID());

			ReasonModel sop = new ReasonModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			reasonCombo.setContainerDataSource(bic);
			reasonCombo.setItemCaptionPropertyId("name");

			reasonCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (reasonTextField.getValue() == null
				|| reasonTextField.getValue().equals("")) {
			setRequiredError(reasonTextField,
					getPropertyName("invalid_data"), true);
			reasonTextField.focus();
			ret = false;
		} else
			setRequiredError(reasonTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
