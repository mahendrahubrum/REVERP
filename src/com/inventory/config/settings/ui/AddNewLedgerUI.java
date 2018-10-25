package com.inventory.config.settings.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.model.ItemSubGroupModel;
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
import com.webspark.common.util.SConstants;
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OfficeModel;

public class AddNewLedgerUI extends SparkLogic {

	long id;

	SHorizontalLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	// SComboField ledgerListCombo;
	STextField ledgerNameTextField;
	SComboField groupCombo;
	SComboField statusCombo;

	STextField openingBalanceTextField;

	// SAddressField address1Field;

	SButton save;
	SButton edit;
	SButton delete;
	SButton update;
	SButton cancel;

	GroupDao gpDao;

	List list;
	LedgerDao objDao = new LedgerDao();

	SButton createNewButton;

	public AddNewLedgerUI() {

		setId("Ledger");
		setSize(500, 320);
		objDao = new LedgerDao();
		gpDao = new GroupDao();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Add New Ledger ( or Account)");

			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			form.setSizeFull();

			// address1Field=new SAddressField(2);

			save = new SButton(getPropertyName("save"));
			edit = new SButton(getPropertyName("edit"));
			delete = new SButton(getPropertyName("delete"));
			update = new SButton(getPropertyName("update"));
			cancel = new SButton(getPropertyName("cancel"));

			buttonLayout.addComponent(save);
			buttonLayout.addComponent(edit);
			buttonLayout.addComponent(delete);
			buttonLayout.addComponent(update);
			buttonLayout.addComponent(cancel);
			buttonLayout.setSpacing(true);

			edit.setVisible(false);
			delete.setVisible(false);
			update.setVisible(false);
			cancel.setVisible(false);

			list = objDao.getAllGeneralLedgers(getOfficeID());
			ItemSubGroupModel og = new ItemSubGroupModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			// ledgerListCombo = new SComboField(null, 300, list, "id", "name");
			// ledgerListCombo.setInputPrompt("------------------- Create New -------------------");

			statusCombo = new SComboField(getPropertyName("status"), 300,
					new StatusDao().getStatuses("GroupModel", "status"),
					"value", "name");
			statusCombo
					.setInputPrompt("------------------- Select -------------------");
			statusCombo.setValue((long) 1);

			groupCombo = new SComboField(getPropertyName("group"), 300,
					gpDao.getAllGroupsNames(getOrganizationID()), "id", "name",
					true, "Select");

			ledgerNameTextField = new STextField(
					getPropertyName("ledger_name"), 300);

			openingBalanceTextField = new STextField(
					getPropertyName("opening_balance"), 300);
			openingBalanceTextField.setValue("0");

			// SHorizontalLayout salLisrLay=new SHorizontalLayout("Ledger");
			// salLisrLay.addComponent(ledgerListCombo);
			// salLisrLay.addComponent(createNewButton);
			// form.addComponent(salLisrLay);
			form.addComponent(ledgerNameTextField);
			form.addComponent(groupCombo);
			form.addComponent(statusCombo);
			form.addComponent(openingBalanceTextField);
			// form.addComponent(address1Field);

			// form.setWidth("400");

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			setContent(hLayout);

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {
							LedgerModel objModel = new LedgerModel();
							objModel.setName(ledgerNameTextField.getValue());
							objModel.setGroup(new GroupModel((Long) groupCombo
									.getValue()));
							// objModel.setAddress(new AddressModel(1));
							objModel.setCurrent_balance(Double
									.parseDouble(openingBalanceTextField
											.getValue()));
							objModel.setStatus((Long) statusCombo.getValue());

							objModel.setOffice(new S_OfficeModel(getOfficeID()));
							objModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
							try {
								id = objDao.save(objModel);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

								closeWindow();

							} catch (Exception e) {
								// TODO Auto-generated catch block
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

		} catch (Exception e) {
		}

	}

	public void closeWindow() {
		this.close();
	}

	public void setReadOnlyAll() {
		ledgerNameTextField.setReadOnly(true);
		statusCombo.setReadOnly(true);
		// address1Field.setReadOnly(true);
		groupCombo.setReadOnly(true);
		openingBalanceTextField.setReadOnly(true);
		// address1Field.setReadOnlyAll();

		ledgerNameTextField.focus();
	}

	public void setWritableAll() {
		ledgerNameTextField.setReadOnly(false);
		statusCombo.setReadOnly(false);
		// address1Field.setReadOnly(false);
		groupCombo.setReadOnly(false);
		openingBalanceTextField.setReadOnly(false);
		// address1Field.setWritableAll();
	}

	public Boolean isValid() {

		boolean ret = true;

		/*
		 * if(!address1Field.isValid()){ ret=false; }
		 */

		if (openingBalanceTextField.getValue() == null
				|| openingBalanceTextField.getValue().equals("")) {
			setRequiredError(openingBalanceTextField,
					getPropertyName("enter_opening_balance"), true);
			openingBalanceTextField.focus();
			ret = false;
		} else
			setRequiredError(openingBalanceTextField, null, false);

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("select_status"),
					true);
			statusCombo.focus();
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);

		if (groupCombo.getValue() == null || groupCombo.getValue().equals("")) {
			setRequiredError(groupCombo, getPropertyName("select_group"), true);
			groupCombo.focus();
			ret = false;
		} else
			setRequiredError(groupCombo, null, false);

		if (ledgerNameTextField.getValue() == null
				|| ledgerNameTextField.getValue().equals("")) {
			setRequiredError(ledgerNameTextField,
					getPropertyName("enter_ledger_name"), true);
			ledgerNameTextField.focus();
			ret = false;
		} else
			setRequiredError(ledgerNameTextField, null, false);

		return ret;
	}

	public void removeErrorMsg() {
		openingBalanceTextField.setComponentError(null);
		statusCombo.setComponentError(null);
		groupCombo.setComponentError(null);
		ledgerNameTextField.setComponentError(null);
	}

	public void reloadGroup() {
		try {
			if (groupCombo.isReadOnly()) {
				Object obj = groupCombo.getValue();
				groupCombo.setReadOnly(false);
				list = gpDao.getAllActiveGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupCombo.setContainerDataSource(bic);
				groupCombo.setItemCaptionPropertyId("name");
				groupCombo.setValue(obj);
				groupCombo.setReadOnly(true);
			} else {
				list = gpDao.getAllActiveGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupCombo.setContainerDataSource(bic);
				groupCombo.setItemCaptionPropertyId("name");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
	}

}
