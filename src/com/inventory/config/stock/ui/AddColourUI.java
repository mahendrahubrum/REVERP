package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.BrandDao;
import com.inventory.config.stock.dao.ColourDao;
import com.inventory.config.stock.model.BrandModel;
import com.inventory.config.stock.model.ColourModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.ExpenditurePanel;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author anil
 *
 */

public class AddColourUI extends SparkLogic {

	private static final long serialVersionUID = 1957368753190599474L;
	SPanel pannel;
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField colourCombo;
	STextField colourTextField;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	ColourDao objDao;

	SButton createNewButton;
	
	@Override
	public SPanel getGUI() {

		setSize(580, 255);
		objDao = new ColourDao();

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

			list = objDao.getAllColourModel(getOfficeID());
			ColourModel og = new ColourModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			colourCombo = new SComboField(null, 300, list, "id", "name");
			colourCombo
					.setInputPrompt("------------------- Create New -------------------");

			colourTextField = new STextField(getPropertyName("Colour Name"),
					300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("Colour"));
			salLisrLay.addComponent(colourCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(colourTextField);
			
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
					colourCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (colourCombo.getValue() == null
								|| colourCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								ColourModel objModel = new ColourModel();
								objModel.setName(colourTextField.getValue());
								objModel.setStatus(1);
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

			colourCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (colourCombo.getValue() != null
										&& !colourCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									ColourModel objModel = objDao
											.getColourModel((Long) colourCombo
													.getValue());

									colourTextField.setValue(objModel
											.getName());

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									colourTextField.setValue("");

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
												objDao.delete((Long) colourCombo
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
								+ colourCombo.getValue());
						if (colourCombo.getValue() != null) {

							if (isValid()) {

								ColourModel objModel = objDao
										.getColourModel((Long) colourCombo
												.getValue());

								objModel.setName(colourTextField.getValue());
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
			list = objDao.getAllColourModel(getOfficeID());

			ColourModel sop = new ColourModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			colourCombo.setContainerDataSource(bic);
			colourCombo.setItemCaptionPropertyId("name");

			colourCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (colourTextField.getValue() == null
				|| colourTextField.getValue().equals("")) {
			setRequiredError(colourTextField,
					getPropertyName("invalid_data"), true);
			colourTextField.focus();
			ret = false;
		} else
			setRequiredError(colourTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
