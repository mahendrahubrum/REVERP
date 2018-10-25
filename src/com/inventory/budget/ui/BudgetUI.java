package com.inventory.budget.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.budget.dao.BudgetDao;
import com.inventory.budget.dao.BudgetDefinitionDao;
import com.inventory.budget.model.BudgetDefinitionModel;
import com.inventory.budget.model.BudgetModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */
public class BudgetUI extends SparkLogic {

	private static final long serialVersionUID = 1L;

	private SComboBox jobCombo;
	private STextField jobName;
	private SComboBox budgetdefCombo;
	private SDateField startDate;
	private SDateField endDate;
	private STextField amount;
	private STextArea notes;
	private SHorizontalLayout createNewbtnLayout;
	private SHorizontalLayout buttonLayout;
	private SButton save;
	private SButton update;
	private SButton delete;
	private SButton createNew;
	private SNativeSelect status;
	BudgetDao dao;
	BudgetDefinitionDao budgetDao;

	@Override
	public SPanel getGUI() {
		dao = new BudgetDao();
		budgetDao = new BudgetDefinitionDao();
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("600");
		setHeight("600");
		panel.setContent(layout);
		jobCombo = new SComboField(null, 200);
		loadBudget((long) 0);
		jobCombo.setValue(null);
		jobName = new STextField(getPropertyName("job_name"), 200);
		try {
			budgetdefCombo = new SComboField(
					getPropertyName("budget_definition"), 200);

			List list = budgetDao.getAllActiveBudgets(getOfficeID());
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			budgetdefCombo.setContainerDataSource(bic);
			budgetdefCombo.setItemCaptionPropertyId("budgetname");
			budgetdefCombo.setInputPrompt(getPropertyName("select"));
			System.out.println("Budget Definition : " + list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startDate = new SDateField(getPropertyName("start_date"), 200);
		startDate.setValue(CommonUtil.getCurrentSQLDate());
		endDate = new SDateField(getPropertyName("end_date"), 200);
		endDate.setValue(CommonUtil.getCurrentSQLDate());
		amount = new STextField(getPropertyName("amount"), 200);
		notes = new STextArea(getPropertyName("notes"));
		notes.setWidth("400");
		notes.setHeight("200");

		createNewbtnLayout = new SHorizontalLayout(getPropertyName("budget"));
		buttonLayout = new SHorizontalLayout();

		createNew = new SButton();
		createNew.setStyleName("createNewBtnStyle");
		createNew.setDescription("Add new");

		save = new SButton(getPropertyName("save"));
		update = new SButton(getPropertyName("update"));
		delete = new SButton(getPropertyName("delete"));
		List lis = new ArrayList();
		lis.add(new KeyValue((long) 1, "Active"));
		lis.add(new KeyValue((long) 2, "Inactive"));
		status = new SNativeSelect(getPropertyName("status"), 200, lis, "key",
				"value");
		status.setValue((long) 1);

		update.setVisible(false);
		delete.setVisible(false);

		createNew.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				budgetdefCombo.setInputPrompt(getPropertyName("select"));
				endDate.setValue(CommonUtil.getCurrentSQLDate());
				startDate.setValue(CommonUtil.getCurrentSQLDate());
				amount.setValue("");
				notes.setValue("");
				status.setValue((long) 1);
				jobName.setValue("");
				loadBudget((long) 0);
			}
		});

		jobCombo.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				if (jobCombo.getValue() != null
						&& !jobCombo.getValue().equals("")
						&& !jobCombo.getValue().toString().equals("0")) {

					try {
						BudgetModel mdl = dao.getBudgetModel((Long) jobCombo
								.getValue());

						if (mdl != null) {

							jobCombo.setValue(mdl.getJobName());
							jobName.setValue(mdl.getJobName());
							status.setValue(mdl.getStatus());
							notes.setValue(mdl.getNotes());
							amount.setValue(Double.toString(mdl.getAmount()));
							startDate.setValue(mdl.getStartDate());
							endDate.setValue(mdl.getEndDate());
							status.setValue(mdl.getStatus());
							budgetdefCombo.setValue(mdl.getBudgetDef_id()
									.getId());

							save.setVisible(false);
							update.setVisible(true);
							delete.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {

					budgetdefCombo.setValue(null);
					endDate.setValue(CommonUtil.getCurrentSQLDate());
					startDate.setValue(CommonUtil.getCurrentSQLDate());
					amount.setValue("");
					notes.setValue("");
					status.setValue((long) 1);
					jobName.setValue("");

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
						BudgetModel mdl = new BudgetModel();
						mdl.setBudgetDef_id(new BudgetDefinitionModel(
								(Long) budgetdefCombo.getValue()));
						mdl.setJobName(jobName.getValue());
						mdl.setNotes(notes.getValue());
						mdl.setStartDate(CommonUtil
								.getSQLDateFromUtilDate(startDate.getValue()));
						mdl.setEndDate(CommonUtil
								.getSQLDateFromUtilDate(endDate.getValue()));
						mdl.setAmount(toDouble(amount.getValue()));
						mdl.setStatus((Long) status.getValue());
						mdl.setOffice_id(new S_OfficeModel(getOfficeID()));
						dao.save(mdl);
						loadBudget(mdl.getId());

						SNotification.show(getPropertyName("update_success"),
								Type.TRAY_NOTIFICATION);

					} catch (Exception e) {
						SNotification
								.show("Unable to save", Type.ERROR_MESSAGE);
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
						BudgetModel mdl = dao.getBudgetModel((Long) jobCombo
								.getValue());
						if (mdl != null) {
							mdl.setBudgetDef_id(new BudgetDefinitionModel(
									(Long) budgetdefCombo.getValue()));
							mdl.setJobName(jobName.getValue());
							mdl.setNotes(notes.getValue());
							mdl.setStartDate(CommonUtil
									.getSQLDateFromUtilDate(startDate
											.getValue()));
							mdl.setEndDate(CommonUtil
									.getSQLDateFromUtilDate(endDate.getValue()));
							mdl.setAmount(toDouble(amount.getValue()));
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
									BudgetModel mdl;

									try {

										mdl = dao
												.getBudgetModel((Long) jobCombo
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

		createNewbtnLayout.addComponent(jobCombo);
		createNewbtnLayout.addComponent(createNew);
		layout.addComponent(createNewbtnLayout);
		layout.addComponent(jobName);
		layout.addComponent(budgetdefCombo);
		layout.addComponent(startDate);
		layout.addComponent(endDate);
		layout.addComponent(amount);
		layout.addComponent(notes);
		layout.addComponent(status);
		layout.addComponent(buttonLayout);
		buttonLayout.addComponent(save);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);

		return panel;
	}

	private void loadBudget(long id) {
		try {
			List list = dao.getAllBudgets(getOfficeID());
			if (list == null)
				list = new ArrayList();
			list.add(0, new BudgetModel(0,
					"------------Create New-------------"));
			jobCombo.setInputPrompt(getPropertyName("create_new"));

			System.out.println(list);
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			jobCombo.setContainerDataSource(bic);
			jobCombo.setItemCaptionPropertyId("jobName");

			jobCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		jobName.setComponentError(null);
		boolean flag = true;
		if (jobName.getValue() == null || jobName.getValue().equals("")) {
			setRequiredError(jobName, getPropertyName("enter_valid_name"), true);
			flag = false;
		} else
			budgetdefCombo.setComponentError(null);

		if (budgetdefCombo.getValue() == null
				|| budgetdefCombo.getValue().equals("")) {
			setRequiredError(budgetdefCombo,
					getPropertyName("select_budget_definition"), true);
			flag = false;
		} else
			budgetdefCombo.setComponentError(null);

		if (amount.getValue() == null || amount.getValue().equals("")) {
			setRequiredError(amount, getPropertyName("enter_amount"), true);
			flag = false;
		} else {
			try {
				toDouble(amount.getValue());
				amount.setComponentError(null);
			} catch (Exception e) {
				setRequiredError(amount, getPropertyName("enter_valid_amount"),
						true);
				flag = false;
				// TODO: handle exception
			}
		}

		return flag;
	}

	public SComboBox getJobCombo() {
		return jobCombo;
	}

	public void setJobCombo(SComboBox jobCombo) {
		this.jobCombo = jobCombo;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
