package com.inventory.budget.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.budget.dao.BudgetDefinitionDao;
import com.inventory.budget.model.BudgetDefinitionModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */
public class BudgetDefinitionUI extends SparkLogic {

	private static final long serialVersionUID = 8518182654331561151L;

	private SComboBox budgetCombo;
	private SComboBox departmentCombo;
	private STextField nameTextfield;
	private STextArea descriptionTextarea;
	private SDateField startDate;
	private SDateField endDate;
	private SNativeSelect intervalType;
	private STextField intervalTextField;
	private SNativeSelect status;
	private SButton save;
	private SButton update;
	private SButton delete;

	private SButton createNew;
	BudgetDefinitionDao dao;
	private SCollectionContainer bic;
	private CollectionContainer depc;
	DepartmentDao depDao;
	List depList = null;

	@Override
	public SPanel getGUI() {
		dao = new BudgetDefinitionDao();
		depList = new ArrayList();
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("400");
		setHeight("500");
		depDao = new DepartmentDao();
		panel.setContent(layout);

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		SHorizontalLayout newbtnLayout = new SHorizontalLayout(
				getPropertyName("budget_definition"));
		budgetCombo = new SComboField(null, 200);
		depList = loadDepartment(0);
		try {
			departmentCombo = new SComboField("Department", 200, depList, "id",
					"name");
			departmentCombo.setValue((long) 0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		loadBudget((long) 0);
		// loadOptions(getOrganizationID());
		budgetCombo.setValue(null);
		nameTextfield = new STextField(getPropertyName("budget_def_name"), 200);
		descriptionTextarea = new STextArea(getPropertyName("description"), 200);
		startDate = new SDateField(getPropertyName("start_date"), 200);
		startDate.setValue(CommonUtil.getCurrentSQLDate());
		endDate = new SDateField(getPropertyName("end_date"), 200);
		endDate.setValue(CommonUtil.getCurrentSQLDate());
		intervalType = new SNativeSelect(getPropertyName("interval"), 200,
				SConstants.budgetTypes, "key", "value");
		intervalType.setValue((long) 1);
		intervalTextField = new STextField(getPropertyName("duration"), 200);
		List lis = new ArrayList();
		lis.add(new KeyValue((long) 1, "Active"));
		lis.add(new KeyValue((long) 2, "Inactive"));
		status = new SNativeSelect(getPropertyName("status"), 200, lis, "key",
				"value");
		status.setValue((long) 1);

		save = new SButton(getPropertyName("save"));

		delete = new SButton(getPropertyName("delete"));
		update = new SButton(getPropertyName("update"));

		createNew = new SButton();
		createNew.setStyleName("createNewBtnStyle");
		createNew.setDescription("Add new");

		buttonLayout.addComponent(save);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);

		delete.setVisible(false);
		update.setVisible(false);

		newbtnLayout.addComponent(budgetCombo);
		newbtnLayout.addComponent(createNew);

		layout.addComponent(newbtnLayout);

		layout.addComponent(nameTextfield);
		layout.addComponent(descriptionTextarea);
		layout.addComponent(startDate);
		layout.addComponent(endDate);
		layout.addComponent(intervalType);
		layout.addComponent(intervalTextField);
		layout.addComponent(departmentCombo);
		layout.addComponent(status);
		layout.addComponent(buttonLayout);

		createNew.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				budgetCombo.setValue((long) 0);
				loadBudget((long) 0);
				nameTextfield.setValue("");
				descriptionTextarea.setValue("");
				intervalTextField.setValue("");
				endDate.setValue(CommonUtil.getCurrentSQLDate());
				startDate.setValue(CommonUtil.getCurrentSQLDate());
				intervalType.setValue((long) 1);
			}
		});

		budgetCombo.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				if (budgetCombo.getValue() != null
						&& !budgetCombo.getValue().equals("")
						&& !budgetCombo.getValue().toString().equals("0")) {

					try {
						BudgetDefinitionModel mdl = dao
								.getBudgetDefinitionModel((Long) budgetCombo
										.getValue());

						if (mdl != null) {

							budgetCombo.setValue(mdl.getBudgetname());
							nameTextfield.setValue(mdl.getBudgetname());
							status.setValue(mdl.getStatus());
							descriptionTextarea.setValue(mdl.getBudgetdesc());
							startDate.setValue(mdl.getStartdate());
							endDate.setValue(mdl.getEnddate());
							intervalTextField.setValue(mdl
									.getIntervalduration());
							departmentCombo.setValue(mdl.getDepartment());
							intervalType.setValue(mdl.getInterval());
							save.setVisible(false);
							update.setVisible(true);
							delete.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {

					nameTextfield.setValue("");
					status.setValue((long) 1);
					descriptionTextarea.setValue("");
					intervalTextField.setValue("");
					endDate.setValue(CommonUtil.getCurrentSQLDate());
					startDate.setValue(CommonUtil.getCurrentSQLDate());
					intervalType.setValue((long) 1);
					departmentCombo.setValue((long) 0);
					save.setVisible(true);
					update.setVisible(false);
					delete.setVisible(false);
				}
			}

		});

		save.addClickListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if (isValid()) {

					try {
						BudgetDefinitionModel mdl = new BudgetDefinitionModel();
						mdl.setBudgetname(nameTextfield.getValue());
						mdl.setBudgetdesc(descriptionTextarea.getValue());
						mdl.setStartdate(CommonUtil
								.getSQLDateFromUtilDate(startDate.getValue()));
						mdl.setEnddate(CommonUtil
								.getSQLDateFromUtilDate(endDate.getValue()));
						mdl.setInterval((Long) intervalType.getValue());
						mdl.setDepartment((Long) departmentCombo.getValue());
						mdl.setIntervalduration(intervalTextField.getValue());
						mdl.setStatus((Long) status.getValue());
						mdl.setOffice_id(new S_OfficeModel(getOfficeID()));
						dao.save(mdl);
						loadBudget(mdl.getId());

						SNotification.show(getPropertyName("save_success"),
								Type.TRAY_NOTIFICATION);

					} catch (Exception e) {
						SNotification.show(getPropertyName("unable_to_save"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}

			}

		});

		update.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				if (isValid()) {

					try {
						BudgetDefinitionModel mdl = dao
								.getBudgetDefinitionModel((Long) budgetCombo
										.getValue());
						if (mdl != null) {
							mdl.setBudgetname(nameTextfield.getValue());
							mdl.setBudgetdesc(descriptionTextarea.getValue());
							mdl.setStartdate(CommonUtil
									.getSQLDateFromUtilDate(startDate
											.getValue()));
							mdl.setEnddate(CommonUtil
									.getSQLDateFromUtilDate(endDate.getValue()));
							mdl.setInterval((Long) intervalType.getValue());
							mdl.setIntervalduration(intervalTextField
									.getValue());
							mdl.setDepartment((Long) departmentCombo.getValue());
							mdl.setStatus((Long) status.getValue());
							mdl.setOffice_id(new S_OfficeModel(getOfficeID()));
							dao.update(mdl);
							loadBudget(mdl.getId());

							SNotification.show(
									getPropertyName("update_success"),
									Type.TRAY_NOTIFICATION);

							save.setVisible(false);
							update.setVisible(true);
							delete.setVisible(true);

						}
					} catch (Exception e) {
						SNotification.show(getPropertyName("update_unable"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

		delete.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI().getCurrent(),
						getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog arg0) {
								if (arg0.isConfirmed()) {
									BudgetDefinitionModel mdl;

									try {

										mdl = dao
												.getBudgetDefinitionModel((Long) budgetCombo
														.getValue());
										dao.delete((mdl));
										loadBudget((long) 0);
										SNotification
												.show(getPropertyName("deleted_success"),
														Type.TRAY_NOTIFICATION);

									}

									catch (Exception e) {
										SNotification
												.show(getPropertyName("delete_unable"),
														Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}

						});

			}
		});

		return panel;
	}

	private void loadBudget(long id) {
		try {
			List list = dao.getAllBudgets(getOfficeID());
			if (list == null)
				list = new ArrayList();
			list.add(0, new BudgetDefinitionModel(0,
					"------------Create New-------------"));
			budgetCombo.setInputPrompt(getPropertyName("create_new"));

			System.out.println(list);
			bic = SCollectionContainer.setList(list, "id");
			budgetCombo.setContainerDataSource(bic);
			budgetCombo.setItemCaptionPropertyId("budgetname");

			budgetCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		nameTextfield.setComponentError(null);
		boolean flag = true;
		if (nameTextfield.getValue() == null
				|| nameTextfield.getValue().equals("")) {
			setRequiredError(nameTextfield,
					getPropertyName("enter_valid_name"), true);
			flag = false;
		}

		return flag;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public List loadDepartment(long id) {
		List list = new ArrayList();
		DepartmentModel dMdl = new DepartmentModel(0, "All");
		list.add(0, dMdl);
		try {
			list.addAll(dao.getAllDepartments(getOrganizationID()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
