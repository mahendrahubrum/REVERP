package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.GradeDao;
import com.inventory.config.stock.model.GradeModel;
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
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         May 8, 2014
 */

public class AddGradeUI extends SparkLogic {

	private static final long serialVersionUID = 3892992191783995112L;
	SPanel pannel;
	SHorizontalLayout hLayout;
	SFormLayout form;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField gradeListCombo;
	STextField gradeNameTextField;
	STextField gradeCodeTextField;
	STextField percentageField;
	STextArea descriptionArea;

	SButton save;
	SButton delete;
	SButton update;

	List list;
	GradeDao objDao;

	SButton createNewButton;

	@Override
	public SPanel getGUI() {

		setSize(480, 400);
		objDao = new GradeDao();

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

			list = objDao.getAllGrades(getOfficeID());
			GradeModel og = new GradeModel();
			og.setId(0);
			og.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);

			gradeListCombo = new SComboField(null, 300, list, "id", "name");
			gradeListCombo
					.setInputPrompt("------------------- Create New -------------------");

			gradeNameTextField = new STextField(getPropertyName("grade_name"),
					300);
			gradeCodeTextField = new STextField(getPropertyName("grade_code"),
					300);
			percentageField = new STextField(getPropertyName("percentage"), 300);
			percentageField.setValue("0");
			percentageField.setStyleName("textfield_align_right");
			descriptionArea = new STextArea(getPropertyName("description"),
					300, 50);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("grade"));
			salLisrLay.addComponent(gradeListCombo);
			salLisrLay.addComponent(createNewButton);
			form.addComponent(salLisrLay);
			form.addComponent(gradeNameTextField);
			form.addComponent(gradeCodeTextField);
			form.addComponent(percentageField);
			form.addComponent(descriptionArea);

			form.addComponent(buttonLayout);

			hLayout.addComponent(form);
			hLayout.setMargin(true);

			pannel.setContent(hLayout);

			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					gradeListCombo.setValue((long) 0);
				}
			});

			save.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (gradeListCombo.getValue() == null
								|| gradeListCombo.getValue().toString()
										.equals("0")) {

							if (isValid()) {
								GradeModel objModel = new GradeModel();
								objModel.setName(gradeNameTextField.getValue());
								objModel.setCode(gradeCodeTextField.getValue());
								objModel.setDescription(descriptionArea
										.getValue());
								objModel.setPercentage(toDouble(percentageField
										.getValue()));
								objModel.setOfficeId(getOfficeID());
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

			gradeListCombo
					.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {
								if (gradeListCombo.getValue() != null
										&& !gradeListCombo.getValue()
												.toString().equals("0")) {

									save.setVisible(false);
									delete.setVisible(true);
									update.setVisible(true);

									GradeModel objModel = objDao
											.getGrade((Long) gradeListCombo
													.getValue());

									gradeNameTextField.setValue(objModel
											.getName());
									gradeCodeTextField.setValue(objModel
											.getCode());
									descriptionArea.setValue(objModel
											.getDescription());
									percentageField.setValue(objModel
											.getPercentage() + "");

								} else {
									save.setVisible(true);
									delete.setVisible(false);
									update.setVisible(false);

									gradeNameTextField.setValue("");
									gradeCodeTextField.setValue("");
									descriptionArea.setValue("");
									percentageField.setValue("0");

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
												objDao.delete((Long) gradeListCombo
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
								+ gradeListCombo.getValue());
						if (gradeListCombo.getValue() != null) {

							if (isValid()) {

								GradeModel objModel = objDao
										.getGrade((Long) gradeListCombo
												.getValue());

								objModel.setName(gradeNameTextField.getValue());
								objModel.setCode(gradeCodeTextField.getValue());
								objModel.setDescription(descriptionArea
										.getValue());
								objModel.setPercentage(toDouble(percentageField
										.getValue()));
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

			addShortcutListener(new ShortcutListener("Add New Grade",
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
			list = objDao.getAllGrades(getOfficeID());

			GradeModel sop = new GradeModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			gradeListCombo.setContainerDataSource(bic);
			gradeListCombo.setItemCaptionPropertyId("name");

			gradeListCombo.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		percentageField.setComponentError(null);

		if (gradeCodeTextField.getValue() == null
				|| gradeCodeTextField.getValue().equals("")) {
			setRequiredError(gradeCodeTextField,
					getPropertyName("invalid_data"), true);
			gradeCodeTextField.focus();
			ret = false;
		} else
			setRequiredError(gradeCodeTextField, null, false);

		if (gradeNameTextField.getValue() == null
				|| gradeNameTextField.getValue().equals("")) {
			setRequiredError(gradeNameTextField,
					getPropertyName("invalid_data"), true);
			gradeNameTextField.focus();
			ret = false;
		} else
			setRequiredError(gradeNameTextField, null, false);

		try {
			if (toDouble(percentageField.getValue().toString()) < 0
					|| toDouble(percentageField.getValue().toString()) > 100) {
				setRequiredError(percentageField,
						getPropertyName("invalid_data"), true);
				percentageField.focus();
				ret = false;
			}
		} catch (Exception e) {
			setRequiredError(percentageField, getPropertyName("invalid_data"),
					true);
			percentageField.focus();
			ret = false;
		}

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
