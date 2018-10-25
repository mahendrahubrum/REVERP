package com.inventory.finance.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.finance.dao.FinanceComponentDao;
import com.inventory.finance.model.FinanceComponentModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 29, 2014
 */
public class FinanceComponentUI extends SparkLogic{

	private static final long serialVersionUID = 2239063005001837237L;

	private SComboField officeField;
	private SComboField componentComboField;
	private STextField nameField;
	private STextField balanceField;
	private STextArea descriptionField;
	private SNativeSelect status;
	private SLabel currBalance;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;

	private FinanceComponentDao dao;

	@SuppressWarnings("unchecked")
	@Override
	public SPanel getGUI() {

		setSize(370, 420);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout formLayout = new SFormLayout();
		formLayout.setMargin(true);

		SHorizontalLayout buttonFormLayout = new SHorizontalLayout();
		buttonFormLayout.setSpacing(true);

		dao = new FinanceComponentDao();
		
		try {
		officeField=new SComboField(getPropertyName("office"),200,new OfficeDao().getAllOfficeNamesUnderOrg(getOrganizationID()),"id","name");
		officeField.setValue(getOfficeID());

		componentComboField = new SComboField(getPropertyName("component"), 200);
		componentComboField
				.setInputPrompt(getPropertyName("create_new"));

		nameField = new STextField(getPropertyName("name"), 200);
		balanceField = new STextField(getPropertyName("opening_balance"), 200);
		balanceField.setValue("0");
		List lis=new ArrayList();
		lis.add(new KeyValue(SConstants.statuses.FINANCE_COMPONENT_ACTIVE, "Active"));
		lis.add(new KeyValue(SConstants.statuses.FINANCE_COMPONENT_INACTIVE, "Inactive"));
		status = new SNativeSelect(getPropertyName("status"), 100,lis, "intKey", "value");
		status.setNullSelectionAllowed(false);
		status.select(1);
		
		currBalance=new SLabel(getPropertyName("current_balance")+" : ");
		currBalance.setVisible(false);

		descriptionField = new STextArea(getPropertyName("description"), 200,100);

		loadComponents((long) 0);
		
		formLayout.addComponent(officeField);
		formLayout.addComponent(componentComboField);
		formLayout.addComponent(nameField);
		formLayout.addComponent(descriptionField);
		formLayout.addComponent(balanceField);
		formLayout.addComponent(status);
		formLayout.addComponent(currBalance);

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
		
		
		formLayout.addShortcutListener(new ShortcutListener("Save",
				ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (saveButton.isVisible()) {
					saveButton.click();
				} else {
					updateButton.click();
				}
			}
		});
		
		officeField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				loadComponents((long)0);
			}
		});
		
		componentComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (componentComboField.getValue() != null
						&& !componentComboField.getValue().equals("")
						&& !componentComboField.getValue().toString()
								.equals("0")) {

					try {
						FinanceComponentModel componentModel = dao
								.getComponentModel(toLong(componentComboField
										.getValue().toString()));

						if (componentModel != null) {

							nameField.setValue(componentModel.getName());
							descriptionField.setValue(componentModel.getDescription());
							status.setValue(componentModel.getStatus());
							balanceField.setNewValue(componentModel.getOpening_balance()+"");
							currBalance.setVisible(true);
							currBalance.setValue(componentModel.getCurrent_balance()+"");
							balanceField.setReadOnly(true);

							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					nameField.setValue("");
					descriptionField.setValue("");
					status.setValue(1);
					balanceField.setReadOnly(false);
					balanceField.setValue("0");
					currBalance.setVisible(false);
					currBalance.setValue("0");

					saveButton.setVisible(true);
					updateButton.setVisible(false);
					deleteButton.setVisible(false);
				}
				nameField.setComponentError(null);
			}
		});
		
		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {


						FinanceComponentModel componentModel = new FinanceComponentModel();
						componentModel.setName(nameField.getValue().toString());
						componentModel.setDescription(descriptionField.getValue().toString());
						componentModel.setStatus(toInt(status.getValue()
								.toString()));
						componentModel.setOpening_balance(toDouble(balanceField.getValue()));
						componentModel.setOfficeId((Long)officeField.getValue());
						componentModel.setCurrent_balance(toDouble(balanceField.getValue()));

						dao.save(componentModel);

						loadComponents(componentModel.getId());

						SNotification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
						SNotification
								.show(getPropertyName("issue_occured"), Type.ERROR_MESSAGE);
						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
					}

				}
			}
		});

		updateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					try {
						FinanceComponentModel componentModel = dao
								.getComponentModel(toLong(componentComboField
										.getValue().toString()));
						if (componentModel != null) {


							componentModel.setName(nameField.getValue().toString());
							componentModel.setDescription(descriptionField.getValue().toString());
							componentModel.setStatus(toInt(status.getValue()
									.toString()));
							componentModel.setOpening_balance(toDouble(balanceField.getValue()));
							componentModel.setOfficeId((Long)officeField.getValue());
							
							dao.update(componentModel);

							loadComponents(componentModel.getId());

							SNotification.show(getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("issue_occured"),
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
				ConfirmDialog.show(getUI(),  getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.delete(toLong(componentComboField
												.getValue().toString()));

										SNotification.show(
												getPropertyName("deleted_success"),
												Type.WARNING_MESSAGE);
										loadComponents(0);
										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);

									} catch (Exception e) {
										Notification.show(getPropertyName("Error"), getPropertyName("issue_occured")+e.getCause(),Type.ERROR_MESSAGE);
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

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		panel.setContent(formLayout);
		
		return panel;
	}

	private void loadComponents(long id) {
		List<Object> list = new ArrayList<Object>();
		try {
			FinanceComponentModel model = new FinanceComponentModel();
			model.setId(0);
			model.setName("---------------Create New---------------");
			list.add(0, model);
			list.addAll(dao.getAllComponents((Long)officeField.getValue()));
			SCollectionContainer container = SCollectionContainer.setList(list,
					"id");
			componentComboField.setContainerDataSource(container);
			componentComboField.setItemCaptionPropertyId("name");

			if (id > 0) {
				componentComboField.setValue(id);
			} else {
				componentComboField.setValue(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		boolean valid = true;
		nameField.setComponentError(null);
		if (nameField.getValue() == null || nameField.getValue().equals("")) {
			setRequiredError(nameField, getPropertyName("invalid_data"), true);
			valid = false;
		} 
		
		if (balanceField.getValue() == null || balanceField.getValue().equals("")) {
			setRequiredError(balanceField, getPropertyName("invalid_data"), true);
			valid = false;
		}else{
			try {
				toDouble(balanceField.getValue());
			} catch (Exception e) {
				setRequiredError(balanceField, getPropertyName("invalid_data"), true);
				valid = false;
			}
		}
		return valid;
	}
	@Override
	public Boolean getHelp() {
		return null;
	}

}
