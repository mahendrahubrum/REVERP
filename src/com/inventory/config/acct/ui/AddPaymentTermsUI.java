package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.PaymentTermsDao;
import com.inventory.config.stock.model.PaymentTermsModel;
import com.vaadin.annotations.Theme;
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
import com.webspark.dao.StatusDao;
import com.webspark.uac.dao.OrganizationDao;

@Theme("testappstheme")
public class AddPaymentTermsUI extends SparkLogic {

	long id = 0;

	CollectionContainer bic;

	final SFormLayout content;

	SComboField organizations;

	SComboField statusCombo;

	SComboField paymentTerms;
	final STextField paymentTermsName;
	final STextField maxdays;

	final SButton save = new SButton(getPropertyName("Save"));
	final SButton edit = new SButton(getPropertyName("Edit"));
	final SButton delete = new SButton(getPropertyName("Delete"));
	final SButton update = new SButton(getPropertyName("Update"));
	final SButton cancel = new SButton(getPropertyName("Cancel"));

	final HorizontalLayout buttonLayout = new HorizontalLayout();

	PaymentTermsDao ptDao = new PaymentTermsDao();

	SButton createNewButton;

	public AddPaymentTermsUI() throws Exception {

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		setCaption("Add Designation");

		setWidth("480px");
		setHeight("280px");
		content = new SFormLayout();

		// **********************************************************

		List testList = ptDao.getAllPaymentTerms(getOrganizationID());
		PaymentTermsModel sop = new PaymentTermsModel();
		sop.setId(0);
		sop.setName("------------------- Create New -------------------");

		if (testList == null)
			testList = new ArrayList();

		testList.add(0, sop);
		// **********************************************************

		statusCombo = new SComboField(getPropertyName("status"), 300,
				new StatusDao().getStatuses("SalesTypeModel", "status"),
				"value", "name");
		statusCombo
				.setInputPrompt(getPropertyName("select"));

		organizations = new SComboField(getPropertyName("organization"), 300,
				new OrganizationDao().getAllOrganizations(), "id", "name");

		paymentTerms = new SComboField(null, 300, testList, "id", "name");

		paymentTermsName = new STextField(getPropertyName("payment_term"), 300);
		maxdays = new STextField(getPropertyName("maximum_days"), 300);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("payment_term"));
		salLisrLay.addComponent(paymentTerms);
		salLisrLay.addComponent(createNewButton);
		content.addComponent(salLisrLay);
		content.addComponent(paymentTermsName);
		content.addComponent(organizations);
		content.addComponent(maxdays);
		content.addComponent(statusCombo);

		buttonLayout.setSpacing(true);
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

		organizations.setValue(getOrganizationID());

		setContent(content);

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				paymentTerms.setValue((long) 0);
			}
		});

		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (paymentTerms.getValue() == null
							|| paymentTerms.getValue().toString().equals("0")) {

						if (isValid()) {
							PaymentTermsModel lm = new PaymentTermsModel();
							lm.setName(paymentTermsName.getValue());
							lm.setDays(toInt(maxdays.getValue()));
							lm.setOrganization_id((Long) organizations
									.getValue());
							lm.setStatus((Long) statusCombo.getValue());
							try {
								id = ptDao.save(lm);
								loadOptions(id);
								Notification.show(getPropertyName("Success"),
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Notification.show(
										getPropertyName("Error"),
										getPropertyName("issue_occured")
												+ e.getCause(),
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

		paymentTerms.addListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if (paymentTerms.getValue() != null
							&& !paymentTerms.getValue().toString().equals("0")) {

						save.setVisible(false);
						edit.setVisible(true);
						delete.setVisible(true);
						update.setVisible(false);
						cancel.setVisible(false);

						PaymentTermsModel lmd = ptDao
								.getPaymentTerm((Long) paymentTerms.getValue());
						setWritableAll();

						paymentTermsName.setValue(lmd.getName());
						maxdays.setValue(asString(lmd.getDays()));
						organizations.setValue(lmd.getOrganization_id());
						statusCombo.setValue(lmd.getStatus());

						setReadOnlyAll();

					} else {
						save.setVisible(true);
						edit.setVisible(false);
						delete.setVisible(false);
						update.setVisible(false);
						cancel.setVisible(false);

						setWritableAll();
						paymentTermsName.setValue("");
						maxdays.setValue("");
						organizations.setValue(getOrganizationID());
						statusCombo.setValue(null);

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
					loadOptions(Long.parseLong(paymentTerms.getValue()
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
											id = Long.parseLong(paymentTerms
													.getValue().toString());
											ptDao.delete(id);

											loadOptions(0);

											Notification
													.show(getPropertyName("Success"),
															getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

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

					if (paymentTerms.getValue() != null) {

						if (paymentTermsName.getValue() != null
								&& !paymentTermsName.getValue().equals("")
								&& organizations.getValue() != null) {

							PaymentTermsModel lm = ptDao.getPaymentTerm(Long
									.parseLong(paymentTerms.getValue()
											.toString()));

							lm.setName(paymentTermsName.getValue());
							lm.setDays(toInt(maxdays.getValue()));
							lm.setOrganization_id((Long) organizations
									.getValue());
							lm.setStatus((Long) statusCombo.getValue());

							try {
								ptDao.update(lm);
								loadOptions(lm.getId());
								Notification.show(getPropertyName("Success"),
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
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

	}

	public void setReadOnlyAll() {
		paymentTermsName.setReadOnly(true);
		maxdays.setReadOnly(true);
		organizations.setReadOnly(true);
		paymentTermsName.focus();
		statusCombo.setReadOnly(true);
	}

	public void setWritableAll() {
		paymentTermsName.setReadOnly(false);
		maxdays.setReadOnly(false);
		organizations.setReadOnly(false);
		statusCombo.setReadOnly(false);
	}

	public void loadOptions(long id) {
		List testList;
		try {
			testList = ptDao.getAllPaymentTerms(getOrganizationID());

			PaymentTermsModel sop = new PaymentTermsModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			paymentTerms
					.setInputPrompt("------------------- Create New -------------------");

			bic = CollectionContainer.fromBeans(testList, "id");
			paymentTerms.setContainerDataSource(bic);
			paymentTerms.setItemCaptionPropertyId("name");

			paymentTerms.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// **********************************************************

	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (maxdays.getValue() == null || maxdays.getValue().equals("")) {
			setRequiredError(maxdays, getPropertyName("invalid_data"), true);
			maxdays.focus();
			ret = false;
		} else {
			try {
				if (toDouble(maxdays.getValue()) < 0) {
					setRequiredError(maxdays, getPropertyName("invalid_data"),
							true);
					maxdays.focus();
					ret = false;
				} else
					setRequiredError(maxdays, null, false);
			} catch (Exception e) {
				setRequiredError(maxdays, getPropertyName("invalid_data"), true);
				maxdays.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (organizations.getValue() == null
				|| organizations.getValue().equals("")) {
			setRequiredError(organizations,
					getPropertyName("invalid_selection"), true);
			organizations.focus();
			ret = false;
		} else
			setRequiredError(organizations, null, false);

		if (paymentTermsName.getValue() == null
				|| paymentTermsName.getValue().equals("")) {
			setRequiredError(paymentTermsName, getPropertyName("invalid_data"),
					true);
			paymentTermsName.focus();
			ret = false;
		} else
			setRequiredError(paymentTermsName, null, false);

		// TODO Auto-generated method stub
		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
