package com.hotel.config.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.hotel.config.dao.TableDao;
import com.hotel.config.model.TableModel;
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
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 23-Sep-2015
 */
public class AddTableUI extends SparkLogic{
	
	private static final long serialVersionUID = -1937820253006235912L;
	
	SPanel panel;
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField tableListCombo;
	SComboField employeeListCombo;
	STextField tableNoTextField;
	STextField chairsField;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	TableDao objDao;

	SButton createNewButton;
	@Override
	public SPanel getGUI() {
		setSize(480, 350);
		objDao = new TableDao();

		try {

			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));

			panel = new SPanel();
			hLayout = new SHorizontalLayout();
			// vLayout=new SVerticalLayout();
			form = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			panel.setSizeFull();
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

			list = objDao.getAllTables(getOfficeID());
			TableModel og = new TableModel();
			og.setId(0);
			og.setTableNo("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			tableListCombo = new SComboField(null, 300, list, "id", "tableNo");
			tableListCombo
					.setInputPrompt("------------------- Create New -------------------");

			tableNoTextField = new STextField(getPropertyName("Table No"),
					300);
			chairsField = new STextField(getPropertyName("No of chairs"),
					300);
			chairsField.setValue("0");
			chairsField.setStyleName("textfield_align_right");
			employeeListCombo = new SComboField(
					getPropertyName("employee"),
					300,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name");
			employeeListCombo
					.setInputPrompt("------------------------- Select --------------------------");

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("Table"));
			salLisrLay.addComponent(tableListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(tableNoTextField);
			form.addComponent(employeeListCombo);
			form.addComponent(chairsField);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			panel.setContent(hLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					tableListCombo.setValue(null);
				}
			});
			
			addShortcutListener(new ShortcutListener("Add New",
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

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (tableListCombo.getValue() == null
								|| tableListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								TableModel objModel = new TableModel();
								objModel.setTableNo(tableNoTextField.getValue());
								objModel.setNo_of_chairs(toInt(chairsField.getValue()));
								objModel.setOffice(new S_OfficeModel(getOfficeID()));
								objModel.setEmployee(new UserModel((Long)employeeListCombo.getValue()));
								objModel.setStatus(SConstants.tableStatus.AVAILABLE);;
								try {
									objDao.save(objModel);
									loadOptions(objModel.getId());
									Notification.show(
											getPropertyName("Success"),
											getPropertyName("save_success"),
											Type.WARNING_MESSAGE);

								} catch (Exception e) {
									Notification.show(getPropertyName("Error"),
											getPropertyName("issue_occured"),
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

			tableListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (tableListCombo.getValue() != null
										&& !tableListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									TableModel objModel = objDao
											.getTable((Long) tableListCombo
													.getValue());

									tableNoTextField.setValue(objModel.getTableNo());
									chairsField.setValue(objModel.getNo_of_chairs()+"");
									employeeListCombo.setValue(objModel
											.getEmployee().getId());

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									tableNoTextField.setValue("");
									chairsField.setValue("0");
									employeeListCombo.setValue(null);

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
												objDao.delete((Long) tableListCombo
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
								+ tableListCombo.getValue());
						if (tableListCombo.getValue() != null) {

							if (isValid()) {

								TableModel objModel = objDao
										.getTable((Long) tableListCombo
												.getValue());

								objModel.setTableNo(tableNoTextField.getValue());
								objModel.setNo_of_chairs(toInt(chairsField.getValue()));
								objModel.setEmployee(new UserModel((Long)employeeListCombo.getValue()));
//								objModel.setStatus(SConstants.tableStatus.AVAILABLE);
								
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

		return panel;
	}

	public void loadOptions(long id) {
		List testList;
		try {
			list = objDao.getAllTables(getOfficeID());

			TableModel sop = new TableModel();
			sop.setId(0);
			sop.setTableNo("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			tableListCombo.setContainerDataSource(bic);
			tableListCombo.setItemCaptionPropertyId("tableNo");

			tableListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		chairsField.setComponentError(null);


		if (tableNoTextField.getValue() == null
				|| tableNoTextField.getValue().equals("")) {
			setRequiredError(tableNoTextField,
					getPropertyName("invalid_data"), true);
			tableNoTextField.focus();
			ret = false;
		} else
			setRequiredError(tableNoTextField, null, false);
		
		if (employeeListCombo.getValue() == null
				|| employeeListCombo.getValue().equals("")) {
			setRequiredError(employeeListCombo,
					getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(employeeListCombo, null, false);

		try {
			if (toInt(chairsField.getValue().toString()) < 0) {
				setRequiredError(chairsField,
						getPropertyName("invalid_data"), true);
				chairsField.focus();
				ret = false;
			}
		} catch (Exception e) {
			setRequiredError(chairsField, getPropertyName("invalid_data"),
					true);
			chairsField.focus();
			ret = false;
		}

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
