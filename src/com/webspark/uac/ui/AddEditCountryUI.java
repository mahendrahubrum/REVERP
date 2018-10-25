package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.CountryDao;
import com.webspark.uac.model.CountryModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class AddEditCountryUI extends SparkLogic {

	private static final long serialVersionUID = 1853432924948171384L;

	long id = 0;

	CollectionContainer bic;

	final SFormLayout content;

	SComboField countryList;
	final STextField currencyName;
	final SComboField currency;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	CountryDao ogDao = new CountryDao();

	SButton createNewButton;

	WrappedSession session;

	public AddEditCountryUI() throws Exception {

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		setCaption("Add Country");

		session = getHttpSession();

		setWidth("460px");
		setHeight("250px");
		content = new SFormLayout();

		List testList = ogDao.getCountry();
		S_OrganizationModel og = new S_OrganizationModel();
		og.setId(0);
		og.setName("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, og);

		countryList = new SComboField(null, 300, testList, "id", "name");
		countryList
				.setInputPrompt(getPropertyName("create_new"));

		currencyName = new STextField(getPropertyName("country_name"), 300);

		testList = new CurrencyManagementDao().getlabels();

		currency = new SComboField(getPropertyName("currency"), 300, testList,
				"id", "name");
		currency.setInputPrompt(getPropertyName("select"));

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("ad_country"));
		salLisrLay.addComponent(countryList);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(currencyName);
		content.addComponent(currency);

		buttonLayout.addComponent(save);
		buttonLayout.addComponent(edit);
		buttonLayout.addComponent(delete);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(cancel);

		content.addComponent(buttonLayout);

		edit.setVisible(false);
		delete.setVisible(false);
		update.setVisible(false);
		cancel.setVisible(false);
		content.setSizeUndefined();

		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				countryList.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (countryList.getValue() == null
							|| countryList.getValue().toString().equals("0")) {

						if (isValid()) {
							CountryModel objModel = new CountryModel();
							objModel.setName(currencyName.getValue());
							objModel.setCurrency(new CurrencyModel(
									(Long) currency.getValue()));

							try {
								id = ogDao.save(objModel);

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		countryList.addValueChangeListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (countryList.getValue() != null
							&& !countryList.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						CountryModel objModel = ogDao
								.getCountryModel((Long) countryList.getValue());

						setWritableAll();
						currencyName.setValue(objModel.getName());
						currency.setValue(objModel.getCurrency().getId());

						setReadOnlyAll();

					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						currencyName.setValue("");
						currency.setValue(null);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		edit.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					edit.setVisible(false);
					delete.setVisible(false);
					update.setVisible(true);
					cancel.setVisible(true);
					setWritableAll();

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		cancel.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					edit.setVisible(false);
					delete.setVisible(false);
					update.setVisible(false);
					cancel.setVisible(false);
					loadOptions(Long.parseLong(countryList.getValue()
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

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											id = (Long) countryList.getValue();
											ogDao.delete(id);

											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

											loadOptions(0);

										} catch (Exception e) {
											// TODO Auto-generated catch block
											Notification.show(
													getPropertyName("Error"),
													Type.ERROR_MESSAGE);
											e.printStackTrace();
										}

										// Confirmed to continue
										// DO STUFF
									} else {
										// User did not confirm
										// CANCEL STUFF
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
					System.out.println("Option :" + countryList.getValue());
					if (countryList.getValue() != null) {

						if (isValid()) {

							CountryModel objModel = ogDao
									.getCountryModel((Long) countryList
											.getValue());

							objModel.setName(currencyName.getValue());
							objModel.setCurrency(new CurrencyModel(
									(Long) currency.getValue()));

							try {
								ogDao.update(objModel);

								loadOptions(objModel.getId());
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
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

	}

	public void setReadOnlyAll() {
		currencyName.setReadOnly(true);
		currency.setReadOnly(true);

		currencyName.focus();
	}

	public void setWritableAll() {
		currencyName.setReadOnly(false);
		currency.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = ogDao.getCountry();

			S_OrganizationModel sop = new S_OrganizationModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();
			testList.add(0, sop);

			countryList
					.setInputPrompt("------------------- Create New -------------------");

			bic = CollectionContainer.fromBeans(testList, "id");
			countryList.setContainerDataSource(bic);
			countryList.setItemCaptionPropertyId("name");

			countryList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {

		if (currencyName.getValue() == null
				|| currencyName.getValue().equals("")) {
			Notification.show(getPropertyName("invalid_data"),
					getPropertyName("invalid_data"), Type.ERROR_MESSAGE);
			return false;
		}

		if (currency.getValue() == null || currency.getValue().equals("")) {
			Notification.show(getPropertyName("invalid_selection"),
					getPropertyName("invalid_selection"), Type.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
