package com.inventory.sales.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.sales.dao.TailoringItemSpecDao;
import com.inventory.sales.model.TailoringItemSpecModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Feb 14, 2014
 */
public class TailoringItemSpecificationUI extends SparkLogic {

	private static final long serialVersionUID = 8227728399834565772L;

	CollectionContainer bic;

	SFormLayout content;

	SComboField specSelect;
	STextField name;
	STextArea description;
	STextField price;

	SComboField organizations;

	HorizontalLayout buttonLayout;

	TailoringItemSpecDao desDao;

	SButton createNewButton;

	SRadioButton typeButton;

	SButton save;
	SButton delete;
	SButton update;

	@Override
	public SPanel getGUI() {

		SPanel pan = new SPanel();
		pan.setSizeFull();

		save = new SButton(getPropertyName("Save"));
		delete = new SButton(getPropertyName("Delete"));
		update = new SButton(getPropertyName("Update"));

		setSize(500, 400);
		content = new SFormLayout();
		content.setMargin(true);

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Create new");

		buttonLayout = new HorizontalLayout();

		desDao = new TailoringItemSpecDao();

		try {
			organizations = new SComboField(getPropertyName("organization"),
					300, new OrganizationDao().getAllOrganizations(), "id",
					"name");
			organizations.setValue(getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}

		specSelect = new SComboField(null, 300, null, "id", "name", false,
				"Create New");
		loadSpecs((long) 0);

		name = new STextField(getPropertyName("name"), 300);
		description = new STextArea(getPropertyName("description"), 300);
		price = new STextField("Price :", 300);
		price.setValue("0");

		typeButton = new SRadioButton(null, 200,
				SConstants.tailoring.tailoringTypes, "key", "value");
		typeButton.setValue(SConstants.tailoring.TYPE_CHECKBOX);
		typeButton.setStyleName("radio_horizontal");

		content.addComponent(organizations);
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("specification"));
		salLisrLay.addComponent(specSelect);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(name);
		content.addComponent(typeButton);
		content.addComponent(price);
		content.addComponent(description);

		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(save);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);

		content.addComponent(buttonLayout);

		delete.setVisible(false);
		update.setVisible(false);

		setContent(content);

		typeButton.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (toLong(typeButton.getValue().toString()) == SConstants.tailoring.TYPE_CHECKBOX) {
					price.setVisible(true);
				} else {
					price.setValue("0");
					price.setVisible(false);
				}
			}
		});

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				specSelect.setValue(null);
			}
		});

		organizations.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				loadSpecs((long) 0);

			}
		});

		save.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {

						TailoringItemSpecModel lm = new TailoringItemSpecModel();
						lm.setName(name.getValue());
						lm.setDetails(description.getValue());
						lm.setOrganization((Long) organizations.getValue());
						lm.setPrice(toDouble(price.getValue()));
						lm.setType(toLong(typeButton.getValue().toString()));

						try {
							desDao.save(lm);
							loadSpecs(lm.getId());
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						} catch (Exception e) {
							Notification.show(getPropertyName("error"),
									Type.WARNING_MESSAGE);
							e.printStackTrace();
						}
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

		delete.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											desDao.delete(Long
													.parseLong(specSelect
															.getValue()
															.toString()));

											loadSpecs(0);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

										} catch (Exception e) {
											Notification.show(
													getPropertyName("error"),
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

		update.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (specSelect.getValue() != null) {

						if (isValid()) {

							TailoringItemSpecModel lm = desDao
									.getItemSpecModel(Long.parseLong(specSelect
											.getValue().toString()));

							lm.setName(name.getValue());
							lm.setDetails(description.getValue());
							lm.setOrganization((Long) organizations.getValue());
							lm.setPrice(toDouble(price.getValue()));
							lm.setType(toLong(typeButton.getValue().toString()));

							try {
								desDao.Update(lm);
								loadSpecs(lm.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("error"),
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

		specSelect.addValueChangeListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (specSelect.getValue() != null
							&& !specSelect.getValue().toString().equals("0")) {

						save.setVisible(false);
						delete.setVisible(true);
						update.setVisible(true);

						TailoringItemSpecModel lmd = desDao
								.getItemSpecModel(Long.parseLong(specSelect
										.getValue().toString()));

						name.setValue(lmd.getName());
						description.setValue(lmd.getDetails());
						price.setValue(asString(lmd.getPrice()));
						typeButton.setValue(lmd.getType());

						isValid();
					} else {
						save.setVisible(true);
						delete.setVisible(false);
						update.setVisible(false);

						name.setValue("");
						description.setValue("");
						price.setValue("0");
						typeButton.setValue(SConstants.tailoring.TYPE_CHECKBOX);
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		pan.setContent(content);
		return pan;
	}

	protected void loadSpecs(long id) {
		SCollectionContainer bic = null;
		try {

			List testList = desDao.getAllSpec((Long) organizations.getValue());

			TailoringItemSpecModel sop = new TailoringItemSpecModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			bic = SCollectionContainer.setList(testList, "id");
			specSelect.setContainerDataSource(bic);
			specSelect.setItemCaptionPropertyId("name");
			specSelect
					.setInputPrompt("------------------- Create New -------------------");
			if (id > 0)
				specSelect.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		boolean ret = true;

		price.setComponentError(null);

		if (name.getValue() == null || name.getValue().equals("")) {
			setRequiredError(name, getPropertyName("invalid_data"), true);
			name.focus();
			ret = false;
		} else
			setRequiredError(name, null, false);

		if (price.getValue() == null || price.getValue().equals("")) {
			setRequiredError(price, getPropertyName("invalid_data"), true);
			price.focus();
			ret = false;
		} else {
			try {
				if (toDouble(price.getValue()) < 0) {
					setRequiredError(price, getPropertyName("invalid_data"),
							true);
					price.focus();
					ret = false;
				}
			} catch (Exception e) {
				setRequiredError(price, getPropertyName("invalid_data"), true);
				price.focus();
				ret = false;
			}

		}

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
