package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.BrandDao;
import com.inventory.config.stock.model.BrandModel;
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
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */

public class AddBrandUI extends SparkLogic {

	private static final long serialVersionUID = 3731972766390277530L;
	SPanel pannel;
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField brandListCombo;
	STextField brandNameTextField;
	STextField brandCodeTextField;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	BrandDao objDao;

	SButton createNewButton;
	
	@Override
	public SPanel getGUI() {

		setSize(580, 255);
		objDao = new BrandDao();

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

			list = objDao.getAllBrands(getOrganizationID());
			BrandModel og = new BrandModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			brandListCombo = new SComboField(null, 300, list, "id", "name");
			brandListCombo
					.setInputPrompt("------------------- Create New -------------------");

			brandNameTextField = new STextField(getPropertyName("brand_name"),
					300);
			brandCodeTextField = new STextField(getPropertyName("brand_code"),
					300);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("brand"));
			salLisrLay.addComponent(brandListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(brandNameTextField);
			form.addComponent(brandCodeTextField);
			
			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			pannel.setContent(hLayout);
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					brandListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (brandListCombo.getValue() == null
								|| brandListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								BrandModel objModel = new BrandModel();
								objModel.setName(brandNameTextField.getValue());
								objModel.setBrandCode(brandCodeTextField
										.getValue());
								objModel.setOrganization(new S_OrganizationModel(
										getOrganizationID()));
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

			brandListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (brandListCombo.getValue() != null
										&& !brandListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									BrandModel objModel = objDao
											.getBrand((Long) brandListCombo
													.getValue());

									brandNameTextField.setValue(objModel
											.getName());
									brandCodeTextField.setValue(objModel
											.getBrandCode());

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									brandNameTextField.setValue("");
									brandCodeTextField.setValue("");

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
												objDao.delete((Long) brandListCombo
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
								+ brandListCombo.getValue());
						if (brandListCombo.getValue() != null) {

							if (isValid()) {

								BrandModel objModel = objDao
										.getBrand((Long) brandListCombo
												.getValue());

								objModel.setName(brandNameTextField.getValue());
								objModel.setBrandCode(brandCodeTextField
										.getValue());
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

		return pannel;
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllBrands(getOrganizationID());

			BrandModel sop = new BrandModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			brandListCombo.setContainerDataSource(bic);
			brandListCombo.setItemCaptionPropertyId("name");

			brandListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (brandCodeTextField.getValue() == null
				|| brandCodeTextField.getValue().equals("")) {
			setRequiredError(brandCodeTextField,
					getPropertyName("invalid_data"), true);
			brandCodeTextField.focus();
			ret = false;
		} else
			setRequiredError(brandCodeTextField, null, false);

		if (brandNameTextField.getValue() == null
				|| brandNameTextField.getValue().equals("")) {
			setRequiredError(brandNameTextField,
					getPropertyName("invalid_data"), true);
			brandNameTextField.focus();
			ret = false;
		} else
			setRequiredError(brandNameTextField, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
