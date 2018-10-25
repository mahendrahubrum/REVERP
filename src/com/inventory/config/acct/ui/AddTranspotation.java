package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.TranspotationDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.TranspotationModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.dao.StatusDao;
import com.webspark.model.AddressModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */

@SuppressWarnings("serial")
public class AddTranspotation extends SparkLogic {

	long id;

	SPanel mainPanel;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField contractorListCombo;
	STextField contractorNameTextField;
	
	SComboField statusCombo;

	SAddressField address1Field;

	SButton save;
	SButton delete;
	SButton update;

	SButton createNewButton;

	STextField contractorCodeTextField;
	// SNativeSelect salesType;
	// SNativeSelect payment_terms;
	STextField credit_limitTextField;
	STextField max_credit_periodTextField;
	STextArea description;
	SCheckBox subscription;

	List list;
	TranspotationDao objDao;

	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(850, 550);
		objDao = new TranspotationDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {

			contractorCodeTextField = new STextField(
					getPropertyName("transportation_code"), 250);

			credit_limitTextField = new STextField(
					getPropertyName("credit_limit"), 250);
			max_credit_periodTextField = new STextField(
					getPropertyName("max_credit_period"), 250, "0");
			description = new STextArea(getPropertyName("description"), 250);
			subscription=new SCheckBox(getPropertyName("enable_rental"));

			hLayout = new SHorizontalLayout();
			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();
			form.setSizeFull();

			address1Field = new SAddressField(1);

			save = new SButton(getPropertyName("Save"));
			delete = new SButton(getPropertyName("Delete"));
			update = new SButton(getPropertyName("Update"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(update);
			buttonLayout.setSpacing(true);

			delete.setVisible(false);
			update.setVisible(false);

			contractorListCombo = new SComboField(null, 250);

			loadOptions(0);

			statusCombo = new SComboField(getPropertyName("status"), 250,
					new StatusDao().getStatuses("GroupModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt(getPropertyName("select"));

			// groupCombo = new SComboField("Group", 250, new
			// GroupDao().getAllGroupsNames(getOrganizationID()), "id", "name"
			// , true, "Select");

			contractorNameTextField = new STextField(
					getPropertyName("transportation_name"), 250);


			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("transportation"));
			salLisrLay.addComponent(contractorListCombo);
			salLisrLay.addComponent(createNewButton);

			form.addComponent(salLisrLay);
			form.addComponent(contractorCodeTextField);
			form.addComponent(contractorNameTextField);
			form.addComponent(statusCombo);

			form.addComponent(credit_limitTextField);
			form.addComponent(max_credit_periodTextField);
			form.addComponent(description);
			form.addComponent(subscription);

			hLayout.addComponent(form);
			hLayout.addComponent(address1Field);

			address1Field.setCaption(null);
			hLayout.setMargin(true);
			hLayout.setSpacing(true);

			vLayout.addComponent(hLayout);
			vLayout.addComponent(buttonLayout);

			vLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

			mainPanel.setContent(vLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					contractorListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (contractorListCombo.getValue() == null
								|| contractorListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {

								if (!objDao.isCodeExists(getOfficeID(),
										contractorCodeTextField.getValue(), 0)) {

									LedgerModel objModel = new LedgerModel();
									objModel.setName(contractorNameTextField
											.getValue());
//									objModel.setGroup(new GroupModel(
//											SConstants.TRANSPORTATION_GROUP_ID));
									
									objModel.setCurrent_balance(0);
									objModel.setStatus((Long) statusCombo
											.getValue());

									objModel.setOffice(new S_OfficeModel(
											getOfficeID()));

									TranspotationModel contractor = new TranspotationModel();
									contractor.setAddress(address1Field
											.getAddress());
									contractor.setName(contractorNameTextField
											.getValue());
									contractor
											.setCredit_limit(toDouble(credit_limitTextField
													.getValue()));
									contractor.setMax_credit_period(toInt(max_credit_periodTextField
													.getValue()));
									contractor.setCode(contractorCodeTextField
											.getValue());
									contractor.setDescription(description
											.getValue());
									
									if(subscription.getValue())
									{
										contractor.setSubscription(toLong("1"));
									}
									else
									{
										contractor.setSubscription(toLong("0"));
									}

									contractor.setLedger(objModel);

									try {
										id = objDao.save(contractor);
										loadOptions(id);
										Notification
												.show(getPropertyName("Success"),
														getPropertyName("save_success"),
														Type.WARNING_MESSAGE);

									} catch (Exception e) {
										// TODO Auto-generated catch block
										Notification.show(
												getPropertyName("Error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
									setRequiredError(contractorCodeTextField,
											null, false);

								} else {
									setRequiredError(contractorCodeTextField,
											getPropertyName("code_exist"), true);
								}
							}
						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			contractorListCombo.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					try {
						rmvError();
						if (contractorListCombo.getValue() != null
								&& !contractorListCombo.getValue().toString()
										.equals("0")) {

							save.setVisible(false);
							delete.setVisible(true);
							update.setVisible(true);

							TranspotationModel contractorModel = objDao
									.getTranspotation((Long) contractorListCombo
											.getValue());
							LedgerModel objModel = contractorModel.getLedger();

							contractorNameTextField.setValue(objModel.getName());
							address1Field.loadAddress(contractorModel.getAddress().getId());
							
							statusCombo.setValue(objModel.getStatus());

							contractorCodeTextField.setValue(contractorModel
									.getCode());
							credit_limitTextField.setValue(""
									+ contractorModel.getCredit_limit());
							description.setValue(""
									+ contractorModel.getDescription());
							max_credit_periodTextField.setValue(""
									+ contractorModel.getMax_credit_period());
							
							if(contractorModel.getSubscription()==0)
							{
								subscription.setValue(false);
							}
							else if(contractorModel.getSubscription()==1)
							{
								subscription.setValue(true);
							}


						} else {
							save.setVisible(true);
							delete.setVisible(false);
							update.setVisible(false);

							contractorNameTextField.setValue("");
							statusCombo.setValue(null);
							address1Field.clearAll();
							contractorCodeTextField.setValue("");
							credit_limitTextField.setValue("0.0");
							description.setValue("");
							max_credit_periodTextField.setValue("0");
							setDefaultValues();

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

			delete.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) contractorListCombo
														.getValue();
												objDao.delete(id);

												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
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
						System.out.println("Option :"
								+ contractorListCombo.getValue());
						if (contractorListCombo.getValue() != null) {

							if (isValid()) {

								if (!objDao.isCodeExists(getOfficeID(),
										contractorCodeTextField.getValue(),
										(Long) contractorListCombo.getValue())) {

									TranspotationModel contractor = objDao
											.getTranspotation((Long) contractorListCombo
													.getValue());
									LedgerModel objModel = contractor
											.getLedger();
									AddressModel addr = address1Field
											.getAddress();
									addr.setId(contractor.getAddress().getId());

									objModel.setName(contractorNameTextField
											.getValue());
//									objModel.setGroup(new GroupModel(
//											SConstants.TRANSPORTATION_GROUP_ID));
									
									objModel.setCurrent_balance(objModel
											.getCurrent_balance());
									objModel.setStatus((Long) statusCombo
											.getValue());

									objModel.setOffice(new S_OfficeModel(
											getOfficeID()));
									contractor.setAddress(addr);
									contractor.setName(contractorNameTextField
											.getValue());
									contractor
											.setCredit_limit(toDouble(credit_limitTextField
													.getValue()));
									contractor
											.setMax_credit_period(toInt(max_credit_periodTextField
													.getValue()));
									contractor.setCode(contractorCodeTextField
											.getValue());
									contractor.setDescription(description
											.getValue());
									
									if(subscription.getValue())	{
										contractor.setSubscription(toLong("1"));
									}
									else{
										contractor.setSubscription(toLong("0"));
									}

									contractor.setLedger(objModel);

									try {
										objDao.update(contractor);
										loadOptions(contractor.getId());
										Notification
												.show(getPropertyName("Success"),
														getPropertyName("update_success"),
														Type.WARNING_MESSAGE);
									} catch (Exception e) {
										Notification.show(
												getPropertyName("Error"),

												Type.ERROR_MESSAGE);
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									setRequiredError(contractorCodeTextField,
											null, false);
								} else {
									setRequiredError(contractorCodeTextField,
											getPropertyName("code_exist"), true);
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

			setDefaultValues();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return mainPanel;

	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllTranspotationNames(getOfficeID());

			TranspotationModel sop = new TranspotationModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			contractorListCombo.setContainerDataSource(bic);
			contractorListCombo.setItemCaptionPropertyId("name");

			contractorListCombo.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (!address1Field.isValid()) {
			ret = false;
		}

		if (max_credit_periodTextField.getValue() == null
				|| max_credit_periodTextField.getValue().equals("")) {
			setRequiredError(max_credit_periodTextField,
					getPropertyName("invalid_data"), true);
			max_credit_periodTextField.focus();
			ret = false;
		} else {
			try {
				if (toInt(max_credit_periodTextField.getValue()) < 0) {
					setRequiredError(max_credit_periodTextField,
							getPropertyName("invalid_data"), true);
					max_credit_periodTextField.focus();
					ret = false;
				} else
					setRequiredError(max_credit_periodTextField, null, false);
			} catch (Exception e) {
				setRequiredError(max_credit_periodTextField,
						getPropertyName("invalid_data"), true);
				max_credit_periodTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (credit_limitTextField.getValue() == null
				|| credit_limitTextField.getValue().equals("")) {
			setRequiredError(credit_limitTextField,
					getPropertyName("invalid_data"), true);
			credit_limitTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(credit_limitTextField.getValue()) < 0) {
					setRequiredError(credit_limitTextField,
							getPropertyName("invalid_data"), true);
					credit_limitTextField.focus();
					ret = false;
				} else
					setRequiredError(credit_limitTextField, null, false);
			} catch (Exception e) {
				setRequiredError(credit_limitTextField,
						getPropertyName("invalid_data"), true);
				credit_limitTextField.focus();
				ret = false;
				// TODO: handle exception
			}
		}

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);
		
		if (contractorNameTextField.getValue() == null
				|| contractorNameTextField.getValue().equals("")) {
			setRequiredError(contractorNameTextField,
					getPropertyName("invalid_data"), true);
			contractorNameTextField.focus();
			ret = false;
		} else
			setRequiredError(contractorNameTextField, null, false);

		if (contractorCodeTextField.getValue() == null
				|| contractorCodeTextField.getValue().equals("")) {
			setRequiredError(contractorCodeTextField,
					getPropertyName("invalid_data"), true);
			contractorCodeTextField.focus();
			ret = false;
		} else
			setRequiredError(contractorCodeTextField, null, false);

		return ret;
	}

	public void rmvError() {
		address1Field.getCountryComboField().setComponentError(null);
		max_credit_periodTextField.setComponentError(null);
		credit_limitTextField.setComponentError(null);
		statusCombo.setComponentError(null);
		contractorNameTextField.setComponentError(null);
		contractorCodeTextField.setComponentError(null);
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaultValues() {
		try {
			Iterator it = null;

			it = statusCombo.getItemIds().iterator();
			if (it.hasNext())
				statusCombo.setValue(it.next());

			credit_limitTextField.setValue("0.0");
			address1Field.getCountryComboField().setValue(getCountryID());

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
