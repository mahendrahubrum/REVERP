package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.ClearingAgentDao;
import com.inventory.config.acct.model.ClearingAgentModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SAddressField;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.model.AddressModel;
import com.webspark.uac.model.S_OfficeModel;

public class AddClearingAgent extends SparkLogic {

	private static final long serialVersionUID = 7098775208333873168L;

	long id;

	SPanel mainPanel;

	SHorizontalLayout hLayout;
	SVerticalLayout vLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField clearingAgentListCombo;
	STextField clearingAgentNameField;
	SAddressField address1Field;
	STextField clearingAgentCodeTextField;
	STextArea description;

	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;

	SButton createNewButton;
	
	WrappedSession session;
	SettingsValuePojo settings;
	
	ClearingAgentDao objDao;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();

		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		setSize(800, 450);
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		if(settings.getCLEARING_AGENT_GROUP()!=0){
		
		objDao = new ClearingAgentDao();

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		try {
			clearingAgentCodeTextField = new STextField(
					getPropertyName("clearing_agent_code"), 250);
			description = new STextArea(getPropertyName("description"), 250, 30);

			description = new STextArea(getPropertyName("description"), 250, 80);
			hLayout = new SHorizontalLayout();
			vLayout = new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			address1Field = new SAddressField(1);
			
			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			buttonLayout.setSpacing(true);

			deleteButton.setVisible(false);
			updateButton.setVisible(false);

			List list = objDao.getAllClearingAgentsNames(getOfficeID());
			ClearingAgentModel og = new ClearingAgentModel();
			og.setId(0);
			og.setName(getPropertyName("create_new"));
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			clearingAgentListCombo = new SComboField(null, 250, list, "id", "name");
			clearingAgentListCombo
					.setInputPrompt(getPropertyName("create_new"));

			clearingAgentNameField = new STextField(
					getPropertyName("clearing_agent_name"), 250, "");

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("clearing_agent"));
			salLisrLay.addComponent(clearingAgentListCombo);
			salLisrLay.addComponent(createNewButton);

			form.addComponent(salLisrLay);
			form.addComponent(clearingAgentNameField);
			form.addComponent(clearingAgentCodeTextField);

			form.addComponent(description);
			
			hLayout.addComponent(form);
			hLayout.addComponent(address1Field);
			
			SVerticalLayout addLay = new SVerticalLayout();
			addLay.setSpacing(true);
			
			addLay.addComponent(address1Field);
			
			hLayout.addComponent(addLay);
			

			address1Field.setCaption(null);
			hLayout.setMargin(true);
			hLayout.setSpacing(true);
//			vLayout.setSizeFull();
			vLayout.addComponent(hLayout);
			vLayout.addComponent(buttonLayout);
			vLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
			// form.addComponent(grid);

			mainPanel.setContent(vLayout);
			
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
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					clearingAgentListCombo.setValue((long) 0);
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

							if (isValid()) {

									LedgerModel objModel = new LedgerModel();
									objModel.setName(clearingAgentNameField.getValue());
									objModel.setGroup(new GroupModel(settings.getCLEARING_AGENT_GROUP()));
									objModel.setCurrent_balance(0);
									objModel.setStatus(SConstants.statuses.LEDGER_ACTIVE);
									objModel.setOffice(new S_OfficeModel(getOfficeID()));
									objModel.setType(SConstants.LEDGER_ADDED_INDIRECTLY);

									ClearingAgentModel clearingAgent = new ClearingAgentModel();

									clearingAgent.setName(clearingAgentNameField.getValue());
									clearingAgent.setAgent_code(clearingAgentCodeTextField.getValue());
									clearingAgent.setDetails(description.getValue());
									clearingAgent.setAddress(address1Field.getAddress());
									clearingAgent.setLedger(objModel);

									try {
										id = objDao.save(clearingAgent);

										loadOptions(id);
										Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);

									} catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
									setRequiredError(clearingAgentCodeTextField,null, false);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
			
			
			clearingAgentListCombo.addValueChangeListener(new ValueChangeListener() {

				@SuppressWarnings("rawtypes")
				public void valueChange(ValueChangeEvent event) {

					try {
						if (clearingAgentListCombo.getValue() != null && !clearingAgentListCombo.getValue().toString().equals("0")) {
							saveButton.setVisible(false);
							deleteButton.setVisible(true);
							updateButton.setVisible(true);
							
							ClearingAgentModel clearingAgentModel = objDao.getClearingAgent((Long) clearingAgentListCombo.getValue());
							LedgerModel objModel = clearingAgentModel.getLedger();
							clearingAgentNameField.setValue(objModel.getName());
							clearingAgentCodeTextField.setValue(clearingAgentModel.getAgent_code());
							description.setValue(""+ clearingAgentModel.getDetails());
							address1Field.loadAddress(clearingAgentModel.getAddress().getId());
						}
						else{
							saveButton.setVisible(true);
							deleteButton.setVisible(false);
							updateButton.setVisible(false);
							
							clearingAgentNameField.setValue("");
							clearingAgentCodeTextField.setValue("");
							description.setValue("");
							address1Field.clearAll();
							address1Field.getCountryComboField().setValue(getCountryID());
						}
						removeErrorMsgs();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			deleteButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										id = (Long) clearingAgentListCombo.getValue();
										objDao.delete(id);
										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadOptions(0);
									} catch (Exception e) {
										Notification.show(getPropertyName("Error"), Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			updateButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
							if (isValid()) {

									ClearingAgentModel clearingAgent = objDao.getClearingAgent((Long) clearingAgentListCombo.getValue());
									LedgerModel objModel = clearingAgent.getLedger();
									AddressModel addr = address1Field.getAddress();
									addr.setId(clearingAgent.getAddress().getId());
									
									objModel.setName(clearingAgentNameField.getValue());
									objModel.setGroup(new GroupModel(settings.getCLEARING_AGENT_GROUP()));
									objModel.setStatus(SConstants.statuses.LEDGER_ACTIVE);
									objModel.setOffice(new S_OfficeModel(getOfficeID()));
									objModel.setType(SConstants.LEDGER_ADDED_INDIRECTLY);
									
									clearingAgent.setName(clearingAgentNameField.getValue());
									clearingAgent.setAgent_code(clearingAgentCodeTextField.getValue());
									clearingAgent.setDetails(description.getValue());
									clearingAgent.setAddress(addr);
									clearingAgent.setLedger(objModel);
									
									try {
										objDao.update(clearingAgent);
										loadOptions(clearingAgent.getId());
										Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
									} catch (Exception e) {
										Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			loadOptions(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		}else{
			SNotification.show("Set the clearing agent group in account settings",Type.ERROR_MESSAGE);
		}
		return mainPanel;

	}


	public void loadOptions(long id) {
		List list;
		try {
			list = objDao.getAllClearingAgentsNames(getOfficeID());
			ClearingAgentModel sop = new ClearingAgentModel();
			sop.setId(0);
			sop.setName(getPropertyName("create_new"));
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			clearingAgentListCombo.setContainerDataSource(bic);
			clearingAgentListCombo.setItemCaptionPropertyId("name");

			clearingAgentListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isValid() {

		boolean ret = true;

		if (!address1Field.isValid()) {
			ret = false;
		}

		if (clearingAgentNameField.getValue() == null
				|| clearingAgentNameField.getValue().equals("")) {
			setRequiredError(clearingAgentNameField,
					getPropertyName("invalid_data"), true);
			clearingAgentNameField.focus();
			ret = false;
		} else
			setRequiredError(clearingAgentNameField, null, false);

		return ret;
	}

	public void removeErrorMsgs() {
		address1Field.getCountryComboField().setComponentError(null);
		clearingAgentNameField.setComponentError(null);
	}

	public Boolean getHelp() {
		return null;
	}

	protected boolean isValidEmail(String value) {
		boolean ret = true;
		if (value == null || value.equals("")) {
			ret = false;
		} else {
			try {
				InternetAddress emailAddr = new InternetAddress(value);
				emailAddr.validate();
			} catch (Exception ex) {
				ret = false;
			}
		}
		return ret;
	}

}
