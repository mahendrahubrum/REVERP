package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.dao.PayrollComponentDao;
import com.inventory.payroll.model.PayrollComponentModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 3, 2013
 */

/**
 * @author sangeeth
 * @date 20-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class PayrollComponentUI extends SparkLogic {

	private SComboField componentComboField;
	private STextField nameField;
	private STextField codeField;
	private SNativeSelect actionSelect;
	private SNativeSelect typeSelect;
	private SComboField otherComponentComboField;
	private STextField valueField;
	private SCheckBox commissonCheckBox;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;

	private PayrollComponentDao dao;

	private long componentId;

	private SComboField ledgerComboField;
	
	private WrappedSession session;
	private SettingsValuePojo settings;

	
	@Override
	public SPanel getGUI() {

		setSize(370, 400);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		SFormLayout formLayout = new SFormLayout();
		formLayout.setMargin(true);

		SHorizontalLayout buttonFormLayout = new SHorizontalLayout();
		buttonFormLayout.setSpacing(true);

		dao = new PayrollComponentDao();

		componentComboField = new SComboField(getPropertyName("component"), 200);
		componentComboField
				.setInputPrompt(getPropertyName("create_new"));

		nameField = new STextField(getPropertyName("name"), 200);
		codeField = new STextField(getPropertyName("code"), 200);

		actionSelect = new SNativeSelect(getPropertyName("property1"), 150,
				SConstants.payroll.action, "key", "value");
		actionSelect.setNullSelectionAllowed(false);
		actionSelect.select((long) 1);

		typeSelect = new SNativeSelect(getPropertyName("property2"), 150,
				SConstants.payroll.type, "key", "value");
		typeSelect.setNullSelectionAllowed(false);
		typeSelect.select((long) 1);

		valueField = new STextField(getPropertyName("value"), 150);
		commissonCheckBox=new SCheckBox(getPropertyName("commission_salary"), false);

		otherComponentComboField = new SComboField(
				getPropertyName("percentage_of"), 200);
		otherComponentComboField
				.setInputPrompt(getPropertyName("select"));

		loadComponents((long) 0);

		try {
			ledgerComboField = new SComboField(getPropertyName("ledger"), 200,new LedgerDao().getAllActiveGeneralLedgerOnly(getOfficeID()),"id", "name");
			ledgerComboField.setInputPrompt(getPropertyName("select"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		formLayout.addComponent(componentComboField);
		formLayout.addComponent(nameField);
		formLayout.addComponent(codeField);
		formLayout.addComponent(actionSelect);
		formLayout.addComponent(typeSelect);
		if(settings.isCOMMISSION_SALARY_ENABLED())
			formLayout.addComponent(commissonCheckBox);
		formLayout.addComponent(valueField);
		formLayout.addComponent(otherComponentComboField);
		formLayout.addComponent(ledgerComboField);

		saveButton = new SButton(getPropertyName("Save"));
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		updateButton = new SButton(getPropertyName("Update"));
		updateButton.setVisible(false);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		deleteButton = new SButton(getPropertyName("Delete"));
		deleteButton.setVisible(false);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		buttonFormLayout.addComponent(saveButton);
		buttonFormLayout.addComponent(updateButton);
		buttonFormLayout.addComponent(deleteButton);

		formLayout.addComponent(buttonFormLayout);

		panel.setContent(formLayout);

		formLayout.addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (saveButton.isVisible()) {
					saveButton.click();
				} else {
					updateButton.click();
				}
			}
		});

		
		componentComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (componentComboField.getValue() != null && !componentComboField.getValue().equals("") && !componentComboField.getValue().toString()
								.equals("0")) {

					try {
						PayrollComponentModel componentModel = dao.getComponentModel(toLong(componentComboField.getValue().toString()));
						if (componentModel != null) {

							nameField.setValue(componentModel.getName());
							codeField.setValue(componentModel.getCode());
							actionSelect.setValue(componentModel.getAction());
							typeSelect.setValue(componentModel.getType());
							valueField.setValue(componentModel.getValue() + "");
							ledgerComboField.setValue(componentModel.getLedger().getId());
							commissonCheckBox.setValue(componentModel.isCommissionEnabled());
							if (componentModel.getType() == SConstants.payroll.PERCENTAGE) {
								otherComponentComboField.setValue(componentModel.getParent_id());
								otherComponentComboField.setVisible(true);
							} else {
								otherComponentComboField.setValue(null);
								otherComponentComboField.setVisible(false);
							}
							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					nameField.setValue("");
					codeField.setValue("");
					actionSelect.setValue((long) 1);
					typeSelect.setValue((long) 1);
					valueField.setValue("");
					otherComponentComboField.setValue(null);
					otherComponentComboField.setVisible(true);
					ledgerComboField.setValue(null);
					commissonCheckBox.setValue(false);
					saveButton.setVisible(true);
					updateButton.setVisible(false);
					deleteButton.setVisible(false);
				}
				removeErrorMsg();
			}
		});

		
		typeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				otherComponentComboField.setValue(null);
				if (toLong(typeSelect.getValue().toString()) == SConstants.payroll.FIXED) {
					otherComponentComboField.setVisible(false);
				} else {
					otherComponentComboField.setVisible(true);
				}
			}
		});

		
		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {
						long parentId = 0;

						if (toLong(typeSelect.getValue().toString()) == SConstants.payroll.PERCENTAGE) {
							if (otherComponentComboField.getValue() != null && !otherComponentComboField.getValue().equals("")) {
								parentId = toLong(otherComponentComboField.getValue().toString());
							}
						}
						PayrollComponentModel componentModel = new PayrollComponentModel();
						componentModel.setName(nameField.getValue().toString());
						componentModel.setCode(codeField.getValue().toString());
						componentModel.setType(toLong(typeSelect.getValue().toString()));
						componentModel.setAction(toLong(actionSelect.getValue().toString()));
						componentModel.setParent_id(parentId);
						componentModel.setCommissionEnabled(commissonCheckBox.getValue());
						componentModel.setValue(toDouble(valueField.getValue().toString()));
						componentModel.setOffice(new S_OfficeModel(getOfficeID()));
						componentModel.setLedger(new LedgerModel((Long) ledgerComboField.getValue()));
						componentId = dao.save(componentModel);
						loadComponents(componentId);
						Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
					}
				}
			}
		});

		
		updateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					try {
						PayrollComponentModel componentModel = dao
								.getComponentModel(toLong(componentComboField
										.getValue().toString()));
						if (componentModel != null) {

							long parentId = 0;

							if (toLong(typeSelect.getValue().toString()) == SConstants.payroll.PERCENTAGE) {
								if (otherComponentComboField.getValue() != null && !otherComponentComboField.getValue().equals("")) {
									parentId = toLong(otherComponentComboField
											.getValue().toString());
								}
							}

							componentModel.setName(nameField.getValue()
									.toString());
							componentModel.setCode(codeField.getValue()
									.toString());
							componentModel.setType(toLong(typeSelect.getValue()
									.toString()));
							componentModel.setAction(toLong(actionSelect
									.getValue().toString()));
							componentModel.setParent_id(parentId);
							componentModel.setValue(toDouble(valueField
									.getValue().toString()));
							componentModel.setCommissionEnabled(commissonCheckBox.getValue());
							componentModel.setOffice(new S_OfficeModel(
									getOfficeID()));
							componentModel.setLedger(new LedgerModel(
									(Long) ledgerComboField.getValue()));

							dao.update(componentModel);

							loadComponents(componentModel.getId());

							if (componentModel.getParent_id() != 0)
								otherComponentComboField
										.setValue(componentModel.getParent_id());

							Notification.show(
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
					}
					saveButton.setVisible(false);
					updateButton.setVisible(true);
					deleteButton.setVisible(true);
				}
			}
		});
		
		
		deleteButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.delete(toLong(componentComboField.getValue().toString()));

										SNotification.show(
												"Deleted Successfully",
												Type.WARNING_MESSAGE);
										loadComponents(0);
										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);

									} 
									catch (Exception e) {
										Notification.show(
												getPropertyName("Error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
										saveButton.setVisible(false);
										updateButton.setVisible(true);
										deleteButton.setVisible(true);
									}
								}
							}
						});
			}
		});

		return panel;
	}

	
	@SuppressWarnings("unchecked")
	private void loadComponents(long id) {
		List<Object> list = new ArrayList<Object>();
		try {
			PayrollComponentModel model = new PayrollComponentModel();
			model.setId(0);
			model.setName("---------------Create New---------------");
			list.add(0, model);
			list.addAll(dao.getAllComponents(getOfficeID()));
			SCollectionContainer container = SCollectionContainer.setList(list,
					"id");
			componentComboField.setContainerDataSource(container);
			componentComboField.setItemCaptionPropertyId("name");

			otherComponentComboField.setContainerDataSource(container);
			otherComponentComboField.setItemCaptionPropertyId("name");
			otherComponentComboField.setValue(null);
			componentComboField.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {

		boolean valid = true;

		if (nameField.getValue() == null || nameField.getValue().equals("")) {
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			nameField.setComponentError(null);
		}
		if (codeField.getValue() == null || codeField.getValue().equals("")) {
			setRequiredError(codeField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			codeField.setComponentError(null);
		}
		if (valueField.getValue() == null || valueField.getValue().equals("")) {
			setRequiredError(valueField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			valueField.setComponentError(null);
		}
		if (ledgerComboField.getValue() == null
				|| ledgerComboField.getValue().equals("")) {
			setRequiredError(ledgerComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			ledgerComboField.setComponentError(null);
		}

		try {
			Double.parseDouble(valueField.getValue().toString());
		} catch (Exception e) {
			setRequiredError(valueField, getPropertyName("invalid_data"), true);
			valid = false;
		}
		if (toLong(typeSelect.getValue().toString()) == SConstants.payroll.PERCENTAGE) {
			if (otherComponentComboField.getValue() == null
					|| otherComponentComboField.getValue().equals("")) {
				setRequiredError(otherComponentComboField,
						getPropertyName("invalid_selection"), true);
				valid = false;
			} else {
				otherComponentComboField.setComponentError(null);
			}
		}
		return valid;
	}

	
	public void removeErrorMsg() {
		nameField.setComponentError(null);
		codeField.setComponentError(null);
		valueField.setComponentError(null);
		typeSelect.setComponentError(null);
		otherComponentComboField.setComponentError(null);
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
