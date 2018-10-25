/**
 * 
 */
package com.inventory.management.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.management.dao.TaskComponentDao;
import com.inventory.management.dao.TasksDao;
import com.inventory.management.model.TaskComponentDetailsModel;
import com.inventory.management.model.TaskComponentModel;
import com.inventory.management.model.TasksAssignedUsersModel;
import com.inventory.management.model.TasksModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupDateField;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * @author Jinshad P.T.
 * 
 * @Date Jan 3 2014
 */
public class AddTasksUI extends SparkLogic {

	private static final long serialVersionUID = -1830134231919939537L;

	private SComboField tasksComboField;
	private STextField titleField;
	private STextArea descriptionField;
	private SNativeSelect status;
	// private SComboField assignedToComboField;

	private SPopupDateField starttimeField;
	private SPopupDateField endtimeField;
	private SPopupDateField actualcompletiontimeField;
	private WrappedSession session;
	private TasksDao daoObj;

	private SVerticalLayout verticalLayout;

	private SRadioButton modeRadio;

	private SButton save;

	// private SButton accept;

	private SButton update;
	private SButton delete;
	private boolean isUpdate;

	private SCheckBox allCompletedCheckBox;

	private SHorizontalLayout mainHorizontalLayout;

	// private TaskChartUI taskChartUI;

	private STokenField assignUsersSelection;

	Iterator iterCom = null;

	static String TSJ_ENABLE = "ENABLE";
	static String TSJ_SN = "SN";
	static String TSJ_COMPONENT_DETAILS_ID = "Comp Det ID";
	static String TSJ_COMPONENT_NAME = "Name";
	static String TSJ_CREATER_DESCRIPTION = "Creator Desc";
	static String TSJ_DESCRIPTION = "Employee Desc";
	static String TSJ_STATUS_ID = "Status ID";
	static String TSJ_STATUS = "Status";

	STable componentsTable;

	STextField editTitleTextField;
	STextArea editDescTextArea, createrDescTextArea;

	TaskComponentDao compDao;
	SButton tablUpdateBtn, tablCompleteBtn;

	SFormLayout editForm;

	TaskComponentModel comObj;
	TaskComponentDetailsModel detailsObj;

	SPopupView pop;
	SHorizontalLayout popupContainer;

	@Override
	public SPanel getGUI() {
		setSize(900, 620);

		tablUpdateBtn = new SButton("Update");
		tablCompleteBtn = new SButton("Update & Complete");
		createrDescTextArea = new STextArea("Creator Description", 400, 100);
		editDescTextArea = new STextArea("Employee Description", 400, 150);
		editTitleTextField = new STextField("Component Name", 400);
		editTitleTextField.setReadOnly(true);
		popupContainer = new SHorizontalLayout();
		editForm = new SFormLayout(editTitleTextField, createrDescTextArea,
				editDescTextArea, tablUpdateBtn, tablCompleteBtn);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		compDao = new TaskComponentDao();

		assignUsersSelection = new STokenField(getPropertyName("assign_to"));

		componentsTable = new STable(null, 800, 200);

		componentsTable.setSizeFull();
		componentsTable.setSelectable(true);
		// componentsTable.setMultiSelect(true);

		componentsTable.setWidth("670px");
		componentsTable.setHeight("180px");

		componentsTable.addContainerProperty(TSJ_ENABLE, SCheckBox.class, null,
				"", null, Align.CENTER);
		componentsTable.addContainerProperty(TSJ_SN, Integer.class, null, "#",
				null, Align.CENTER);
		componentsTable.addContainerProperty(TSJ_COMPONENT_DETAILS_ID,
				Long.class, null, TSJ_COMPONENT_DETAILS_ID, null, Align.CENTER);
		componentsTable.addContainerProperty(TSJ_COMPONENT_NAME, String.class,
				null, getPropertyName("name"), null, Align.LEFT);
		componentsTable.addContainerProperty(TSJ_CREATER_DESCRIPTION,
				String.class, null, getPropertyName("creator_desc"), null,
				Align.CENTER);
		componentsTable.addContainerProperty(TSJ_DESCRIPTION, String.class,
				null, getPropertyName("employee_desc"), null, Align.CENTER);
		componentsTable.addContainerProperty(TSJ_STATUS_ID, Long.class, null,
				TSJ_STATUS_ID, null, Align.CENTER);
		componentsTable.addContainerProperty(TSJ_STATUS, String.class, null,
				getPropertyName("status"), null, Align.CENTER);

		componentsTable.setColumnExpandRatio(TSJ_SN, (float) .5);
		componentsTable.setColumnExpandRatio(TSJ_COMPONENT_NAME, 2);

		componentsTable
				.setColumnExpandRatio(TSJ_CREATER_DESCRIPTION, (float) 2);
		componentsTable.setColumnExpandRatio(TSJ_DESCRIPTION, (float) 3);
		componentsTable.setColumnExpandRatio(TSJ_STATUS, 1);

		allCompletedCheckBox = new SCheckBox(getPropertyName("completed_only"), false);

		allCompletedCheckBox.setVisible(false);

		List modes = null;

		if (isCreatePrivileaged()) {
			modes = Arrays.asList(new KeyValue((int) 1, getPropertyName("created_me")),
					new KeyValue((int) 2, getPropertyName("assigned_me")));
		} else {
			modes = Arrays.asList(new KeyValue((int) 2, getPropertyName("assigned_me")));
		}

		modeRadio = new SRadioButton(getPropertyName("mode"), 300, modes,
				"intKey", "value");
		modeRadio.setHorizontal(true);

		session = getHttpSession();
		daoObj = new TasksDao();

		try {

			List userList = new UserManagementDao()
					.getAllLoginsFromOrg(getOrganizationID());

			SCollectionContainer bic = SCollectionContainer.setList(userList,
					"id");
			assignUsersSelection.setContainerDataSource(bic);
			assignUsersSelection.setTokenCaptionPropertyId("login_name");

			assignUsersSelection.setWidth("650");
			assignUsersSelection.setInputWidth("200");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mainHorizontalLayout = new SHorizontalLayout();

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		verticalLayout = new SVerticalLayout();
		verticalLayout.setMargin(true);
		verticalLayout.setSpacing(true);

		SFormLayout personalLayout = new SFormLayout();
		personalLayout.setStyleName("layout_border");

		tasksComboField = new SComboField(getPropertyName("tasks"), 200);
		tasksComboField
				.setInputPrompt("-------------- Create New ---------------");

		titleField = new STextField(getPropertyName("title"), 670);
		descriptionField = new STextArea(getPropertyName("description"), 670,
				40);

		starttimeField = new SPopupDateField(getPropertyName("start_time"));
		starttimeField.setValue(new Date());
		endtimeField = new SPopupDateField(getPropertyName("end_time"));
		endtimeField.setValue(new Date());
		actualcompletiontimeField = new SPopupDateField(
				getPropertyName("actual_completion_time"));
		actualcompletiontimeField.setValue(new Date());

		List list = Arrays.asList(new KeyValue((long) 1, "Created"),
				new KeyValue((long) 2, "Completed"), new KeyValue((long) 3,
						"Partially Completed"));
		status = new SNativeSelect(getPropertyName("status"), 200, list, "key",
				"value");
		status.setValue((long) 1);
		status.setReadOnly(true);
		personalLayout.addComponent(titleField);
		personalLayout.addComponent(componentsTable);
		personalLayout.addComponent(popupContainer);

		personalLayout.addComponent(descriptionField);

		// personalLayout.addComponent(assignedToComboField);
		personalLayout.addComponent(assignUsersSelection);

		assignUsersSelection.setStyleName(STokenField.STYLE_TOKENFIELD);
		assignUsersSelection
				.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);

		personalLayout.addComponent(starttimeField);
		personalLayout.addComponent(endtimeField);

		personalLayout.addComponent(actualcompletiontimeField);

		personalLayout.addComponent(status);

		mainHorizontalLayout.addComponent(personalLayout);
		// mainHorizontalLayout.addComponent(taskChartUI);;

		// accept = new SButton("Complete");
		save = new SButton(getPropertyName("save"), "ENTER");
		update = new SButton(getPropertyName("update"), "ENTER");
		update.setVisible(false);
		delete = new SButton(getPropertyName("delete"), "ALT+DEL");
		delete.setVisible(false);
		delete.setClickShortcut(KeyCode.DELETE, ModifierKey.ALT);

		// accept.setVisible(false);

		// buttonLayout.addComponent(accept);
		buttonLayout.addComponent(save);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);

		pop = new SPopupView("", editForm);

		verticalLayout.addComponent(new SFormLayout(modeRadio,
				allCompletedCheckBox, tasksComboField));
		verticalLayout.addComponent(mainHorizontalLayout);

		buttonLayout.setStyleName("addtask_btnLayout");

		verticalLayout.addComponent(buttonLayout);
		verticalLayout.setComponentAlignment(buttonLayout,
				Alignment.MIDDLE_LEFT);
		verticalLayout.setComponentAlignment(mainHorizontalLayout,
				Alignment.TOP_LEFT);

		panel.setContent(verticalLayout);

		verticalLayout.setStyleName("common_page_style");
		// verticalLayout.setWidth("00");

		status.setNewValue((long) 1);

		// starttimeField.setVisible(false);
		// endtimeField.setVisible(false);
		// actualcompletiontimeField.setVisible(false);

		tablUpdateBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (componentsTable.getValue() != null) {
					Item itm = componentsTable.getItem(componentsTable
							.getValue());

					itm.getItemProperty(TSJ_CREATER_DESCRIPTION).setValue(
							createrDescTextArea.getValue());
					itm.getItemProperty(TSJ_DESCRIPTION).setValue(
							editDescTextArea.getValue());

					pop.setPopupVisible(false);
				}
			}
		});

		tablCompleteBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (componentsTable.getValue() != null) {
					Item itm = componentsTable.getItem(componentsTable
							.getValue());
					itm.getItemProperty(TSJ_DESCRIPTION).setValue(
							editDescTextArea.getValue());
					itm.getItemProperty(TSJ_STATUS_ID).setValue((long) 2);
					itm.getItemProperty(TSJ_STATUS).setValue("Completed");
					pop.setPopupVisible(false);

				}
			}
		});

		componentsTable.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {

					if (componentsTable.getValue() != null) {
						Item itm = componentsTable.getItem(componentsTable
								.getValue());

						editTitleTextField.setNewValue(itm.getItemProperty(
								TSJ_COMPONENT_NAME).getValue()
								+ "");
						createrDescTextArea.setNewValue(itm.getItemProperty(
								TSJ_CREATER_DESCRIPTION).getValue()
								+ "");
						editDescTextArea.setValue(itm.getItemProperty(
								TSJ_DESCRIPTION).getValue()
								+ "");

						if (toInt(modeRadio.getValue().toString()) == 2) {
							if ((Long) itm.getItemProperty(TSJ_STATUS_ID)
									.getValue() == 2) {
								tablCompleteBtn.setVisible(false);
								tablUpdateBtn.setVisible(false);
							} else {
								tablCompleteBtn.setVisible(true);
								tablUpdateBtn.setVisible(true);
							}

							createrDescTextArea.setReadOnly(true);
						} else {
							tablCompleteBtn.setVisible(false);

							if ((Long) itm.getItemProperty(TSJ_STATUS_ID)
									.getValue() == 2) {
								tablUpdateBtn.setVisible(false);
							} else {
								tablUpdateBtn.setVisible(true);
							}

							createrDescTextArea.setReadOnly(false);
						}

						popupContainer.addComponent(pop);
						pop.setPopupVisible(true);
						pop.setHideOnMouseOut(false);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		modeRadio.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				tasksComboField.setValue(null);
				loadList((long) 0);
				loadTableData();
				// taskChartUI.setVisible(false);

				// manageAcceptBtn();
			}
		});

		assignUsersSelection.setNewTokensAllowed(false);
		/*
		 * assignUsersSelection.addListener(new ValueChangeListener() { public
		 * void valueChange(ValueChangeEvent event) { System.out.println("dgs");
		 * Set tokens = (Set)event.getProperty().getValue();
		 * assignUsersSelection.setComponentError(null);
		 * 
		 * 
		 * } });
		 */

		allCompletedCheckBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				if (toInt(modeRadio.getValue().toString()) == 2) {

					List list = null;
					try {

						if (allCompletedCheckBox.getValue() == true)
							list = daoObj
									.getAllTasksListAssignedToUserByStatus(
											getLoginID(), 2);
						else
							list = daoObj
									.getAllTasksListAssignedToUser(getLoginID());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CollectionContainer bic = CollectionContainer.fromBeans(
							list, "id");
					tasksComboField.setContainerDataSource(bic);
					tasksComboField.setItemCaptionPropertyId("title");
					tasksComboField
							.setInputPrompt("-------------- Select ---------------");

				}

				// manageAcceptBtn();
			}
		});

		modeRadio.setValue(modeRadio.getItemIds().iterator().next());
		/*
		 * verticalLayout.addShortcutListener(new ShortcutListener("Save",
		 * ShortcutAction.KeyCode.ENTER, null) {
		 * 
		 * @Override public void handleAction(Object sender, Object target) {
		 * 
		 * if (save.isVisible()) save.click(); else update.click(); } });
		 */

		verticalLayout.addShortcutListener(new ShortcutListener(
				"Add New Purchase", ShortcutAction.KeyCode.N,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
			@Override
			public void handleAction(Object sender, Object target) {
				tasksComboField.setValue((long) 0);
			}
		});

		save.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				isUpdate = false;
				if (isValid()) {
					try {

						List<TasksAssignedUsersModel> users = new ArrayList();
						if (assignUsersSelection.getValue() != null) {
							Iterator it = ((Set<Long>) assignUsersSelection
									.getValue()).iterator();
							while (it.hasNext()) {
								users.add(new TasksAssignedUsersModel(
										new S_LoginModel((Long) it.next())));
							}
						}

						Item itm;
						List<TaskComponentDetailsModel> componentDet = new ArrayList<TaskComponentDetailsModel>();
						Iterator itr2 = componentsTable.getItemIds().iterator();
						long comp_id;
						while (itr2.hasNext()) {
							comp_id = (Long) itr2.next();
							detailsObj = new TaskComponentDetailsModel();
							itm = componentsTable.getItem(comp_id);
							if (itm != null) {
								if (((SCheckBox) itm
										.getItemProperty(TSJ_ENABLE).getValue())
										.getValue() == true) {
									detailsObj.setStatus(1);
									detailsObj.setCreater_description(itm
											.getItemProperty(
													TSJ_CREATER_DESCRIPTION)
											.getValue()
											+ "");
									detailsObj.setDescription(itm
											.getItemProperty(TSJ_DESCRIPTION)
											.getValue()
											+ "");
									detailsObj
											.setTask_component(new TaskComponentModel(
													comp_id));
									componentDet.add(detailsObj);
								}
							}
						}

						TasksModel conModel = new TasksModel();

						conModel.setTitle(titleField.getValue().toString());
						conModel.setDescription(descriptionField.getValue()
								.toString());
						conModel.setStatus((Long) status.getValue());
						conModel.setCreated_by(new S_LoginModel(getLoginID()));
						conModel.setStart_time(new Timestamp(starttimeField
								.getValue().getTime()));
						conModel.setEnd_time(new Timestamp(endtimeField
								.getValue().getTime()));
						conModel.setActual_completion_time(new Timestamp(
								actualcompletiontimeField.getValue().getTime()));
						long hours = TimeUnit.MILLISECONDS
								.toHours(actualcompletiontimeField.getValue()
										.getTime()
										- starttimeField.getValue().getTime());

						conModel.setDate(CommonUtil.getCurrentSQLDate());
						conModel.setHours_taken((int) hours);

						conModel.setComponentDetailsList(componentDet);

						conModel.setAssignedList(users);

						long id = daoObj.save(conModel);

						saveActivity(getOptionId(),
								"Task " + conModel.getTitle()
										+ " is created by " + getLoginName());

						SNotification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);

						loadList(id);

					} catch (Exception e) {
						SNotification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

		update.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				isUpdate = true;
				if (isValid()) {
					try {

						if (toInt(modeRadio.getValue().toString()) == 2) {

							try {

								List<TasksAssignedUsersModel> users = new ArrayList();
								if (assignUsersSelection.getValue() != null) {
									Iterator it = ((Set<Long>) assignUsersSelection
											.getValue()).iterator();
									while (it.hasNext()) {
										users.add(new TasksAssignedUsersModel(
												new S_LoginModel((Long) it
														.next())));
									}
								}

								boolean isComplete = true, isPartiallyComplete = false;
								Item itm;
								List<TaskComponentDetailsModel> componentDet = new ArrayList<TaskComponentDetailsModel>();
								Iterator itr2 = componentsTable.getItemIds()
										.iterator();
								long comp_id;
								while (itr2.hasNext()) {
									comp_id = (Long) itr2.next();
									detailsObj = new TaskComponentDetailsModel();
									itm = componentsTable.getItem(comp_id);
									if (itm != null) {
										if ((Long) itm.getItemProperty(
												TSJ_STATUS_ID).getValue() == 1)
											isComplete = false;
										else if ((Long) itm.getItemProperty(
												TSJ_STATUS_ID).getValue() == 2)
											isPartiallyComplete = true;
										detailsObj
												.setCreater_description(itm
														.getItemProperty(
																TSJ_CREATER_DESCRIPTION)
														.getValue()
														+ "");
										detailsObj.setDescription(itm
												.getItemProperty(
														TSJ_DESCRIPTION)
												.getValue()
												+ "");
										detailsObj.setStatus((Long) itm
												.getItemProperty(TSJ_STATUS_ID)
												.getValue());
										detailsObj
												.setTask_component(new TaskComponentModel(
														comp_id));
										componentDet.add(detailsObj);
									}
								}

								TasksModel conModel = daoObj
										.getTasksModel((Long) tasksComboField
												.getValue());

								conModel.setTitle(titleField.getValue()
										.toString());
								conModel.setDescription(descriptionField
										.getValue().toString());

								if (conModel.getStatus() == 1
										|| conModel.getStatus() == 3) {
									if (isComplete)
										conModel.setStatus(2);
									else if (isPartiallyComplete)
										conModel.setStatus(3);
								} else
									conModel.setStatus((Long) status.getValue());

								conModel.setCreated_by(new S_LoginModel(
										getLoginID()));
								conModel.setStart_time(new Timestamp(
										starttimeField.getValue().getTime()));
								conModel.setEnd_time(new Timestamp(endtimeField
										.getValue().getTime()));
								conModel.setActual_completion_time(new Timestamp(
										actualcompletiontimeField.getValue()
												.getTime()));
								long hours = TimeUnit.MILLISECONDS
										.toHours(actualcompletiontimeField
												.getValue().getTime()
												- starttimeField.getValue()
														.getTime());
								conModel.setHours_taken((int) hours);

								conModel.setComponentDetailsList(componentDet);
								conModel.setAssignedList(users);

								daoObj.update(conModel);

								saveActivity(getOptionId(),
										"Task " + conModel.getTitle()
												+ " is updated by "
												+ getLoginName());

								SNotification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);

								loadList(conModel.getId());

							} catch (Exception e) {
								SNotification.show(getPropertyName("error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

						} else {

							List<TasksAssignedUsersModel> users = new ArrayList();
							if (assignUsersSelection.getValue() != null) {
								Iterator it = ((Set<Long>) assignUsersSelection
										.getValue()).iterator();
								while (it.hasNext()) {
									users.add(new TasksAssignedUsersModel(
											new S_LoginModel((Long) it.next())));
								}
							}

							Item itm;
							List<TaskComponentDetailsModel> componentDet = new ArrayList<TaskComponentDetailsModel>();
							Iterator itr2 = componentsTable.getItemIds()
									.iterator();
							long comp_id;
							while (itr2.hasNext()) {
								comp_id = (Long) itr2.next();
								detailsObj = new TaskComponentDetailsModel();
								itm = componentsTable.getItem(comp_id);
								if (itm != null) {
									if (((SCheckBox) itm.getItemProperty(
											TSJ_ENABLE).getValue()).getValue() == true) {
										detailsObj
												.setCreater_description(itm
														.getItemProperty(
																TSJ_CREATER_DESCRIPTION)
														.getValue()
														+ "");
										detailsObj.setDescription(itm
												.getItemProperty(
														TSJ_DESCRIPTION)
												.getValue()
												+ "");
										detailsObj.setStatus((Long) itm
												.getItemProperty(TSJ_STATUS_ID)
												.getValue());
										detailsObj
												.setTask_component(new TaskComponentModel(
														comp_id));
										componentDet.add(detailsObj);
									}
								}
							}

							TasksModel conModel = daoObj
									.getTasksModel((Long) tasksComboField
											.getValue());

							conModel.setTitle(titleField.getValue().toString());
							conModel.setDescription(descriptionField.getValue()
									.toString());
							conModel.setStatus((Long) status.getValue());
							conModel.setCreated_by(new S_LoginModel(
									getLoginID()));
							conModel.setStart_time(new Timestamp(starttimeField
									.getValue().getTime()));
							conModel.setEnd_time(new Timestamp(endtimeField
									.getValue().getTime()));
							conModel.setActual_completion_time(new Timestamp(
									actualcompletiontimeField.getValue()
											.getTime()));
							long hours = TimeUnit.MILLISECONDS
									.toHours(actualcompletiontimeField
											.getValue().getTime()
											- starttimeField.getValue()
													.getTime());
							conModel.setHours_taken((int) hours);

							conModel.setComponentDetailsList(componentDet);
							conModel.setAssignedList(users);

							daoObj.update(conModel);

							saveActivity(getOptionId(),
									"Task " + conModel.getTitle()
											+ " is updated by "
											+ getLoginName());

							SNotification.show(
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);

							loadList(conModel.getId());
						}

					} catch (Exception e) {
						SNotification.show(getPropertyName("error"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

		/*
		 * accept.addClickListener(new ClickListener() {
		 * 
		 * @Override public void buttonClick(ClickEvent event) { isUpdate =
		 * true; if (isValid()) { try {
		 * 
		 * TasksModel conModel = daoObj .getTasksModel((Long) tasksComboField
		 * .getValue());
		 * 
		 * conModel.setTitle(titleField.getValue().toString());
		 * conModel.setDescription(descriptionField.getValue() .toString());
		 * conModel.setStatus(2); // conModel.setCreated_by(new
		 * S_LoginModel(getLoginID())); // conModel.setStart_time(new
		 * Timestamp(starttimeField // .getValue().getTime())); //
		 * conModel.setEnd_time(new Timestamp(endtimeField //
		 * .getValue().getTime())); conModel.setActual_completion_time(new
		 * Timestamp( actualcompletiontimeField.getValue() .getTime())); long
		 * hours = TimeUnit.MILLISECONDS .toHours(actualcompletiontimeField
		 * .getValue().getTime() - starttimeField.getValue() .getTime());
		 * conModel.setHours_taken((int) hours);
		 * 
		 * daoObj.update(conModel); SNotification.show("Successfully Updated",
		 * Type.WARNING_MESSAGE);
		 * 
		 * loadList(conModel.getId());
		 * 
		 * 
		 * } catch (Exception e) { SNotification.show("Unable to update",
		 * Type.ERROR_MESSAGE); e.printStackTrace(); } } } });
		 */

		delete.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				isUpdate = false;
				ConfirmDialog.show(getUI().getCurrent(), "Are you sure?",
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog arg0) {
								if (arg0.isConfirmed()) {
									try {

										daoObj.delete((Long) tasksComboField
												.getValue());

										saveActivity(getOptionId(), "Task "
												+ titleField.getValue()
												+ " is deleted by "
												+ getLoginName());

										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										loadList((long) 0);
									} catch (Exception e) {
										SNotification.show(
												getPropertyName("error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		tasksComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearError();
				if (tasksComboField.getValue() != null
						&& !tasksComboField.getValue().equals("")
						&& !tasksComboField.getValue().toString().equals("0")) {
					try {

						TasksModel conModel = daoObj
								.getTasksModel(toLong(tasksComboField
										.getValue().toString()));

						// taskChartUI.drawChart(new
						// Date(conModel.getStart_time().getTime()),
						// new Date(conModel.getEnd_time().getTime()),
						// new
						// Date(conModel.getActual_completion_time().getTime()));
						// if (toInt(modeRadio.getValue().toString()) == 3)
						// taskChartUI.setVisible(true);
						// else
						// taskChartUI.setVisible(false);

						titleField.setNewValue(conModel.getTitle());
						descriptionField.setNewValue(conModel.getDescription());
						starttimeField.setNewValue(new Date(conModel
								.getStart_time().getTime()));
						endtimeField.setNewValue(new Date(conModel
								.getEnd_time().getTime()));
						actualcompletiontimeField.setNewValue(new Date(conModel
								.getActual_completion_time().getTime()));
						status.setNewValue(conModel.getStatus());

						Set<Long> usersSet = new HashSet<Long>();
						iterCom = conModel.getAssignedList().iterator();
						while (iterCom.hasNext()) {
							usersSet.add(((TasksAssignedUsersModel) iterCom
									.next()).getUser().getId());
						}

						assignUsersSelection.setNewValue(usersSet);

						if (conModel.getStatus() == 2) {
							// accept.setVisible(false);
							update.setVisible(false);
							descriptionField.setReadOnly(true);
						} else {
							// accept.setVisible(true);
							update.setVisible(true);
							descriptionField.setReadOnly(false);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					status.setNewValue((long) 1);
				}

				manageComponents();
				// manageAcceptBtn();

				loadTableData();
			}
		});

		loadTableData();

		return panel;
	}

	public void loadTableData() {

		try {
			componentsTable.removeAllItems();
			componentsTable.setVisibleColumns(new String[] { TSJ_ENABLE,
					TSJ_SN, TSJ_COMPONENT_DETAILS_ID, TSJ_COMPONENT_NAME,
					TSJ_CREATER_DESCRIPTION, TSJ_DESCRIPTION, TSJ_STATUS_ID,
					TSJ_STATUS });

			if (((Integer) modeRadio.getValue() == 1)) {

				int ct = 1;
				Iterator itr = compDao.getAllComponents(getOrganizationID())
						.iterator();
				while (itr.hasNext()) {
					comObj = (TaskComponentModel) itr.next();

					componentsTable.addItem(new Object[] { new SCheckBox(), ct,
							(long) 0, comObj.getName(),
							comObj.getDescription(), "", (long) 1, "Created" },
							comObj.getId());

					ct++;
				}

				if (((Long) tasksComboField.getValue()) != 0) {
					Item itm;
					Iterator itr2 = daoObj.getAssigedComponentsForTask(
							(Long) tasksComboField.getValue()).iterator();
					while (itr2.hasNext()) {
						detailsObj = (TaskComponentDetailsModel) itr2.next();
						itm = componentsTable.getItem(detailsObj
								.getTask_component().getId());
						if (itm != null) {
							((SCheckBox) itm.getItemProperty(TSJ_ENABLE)
									.getValue()).setValue(true);
							itm.getItemProperty(TSJ_COMPONENT_DETAILS_ID)
									.setValue(detailsObj.getId());
							itm.getItemProperty(TSJ_CREATER_DESCRIPTION)
									.setValue(
											detailsObj.getCreater_description());
							itm.getItemProperty(TSJ_DESCRIPTION).setValue(
									detailsObj.getDescription());
							if (detailsObj.getStatus() == 2) {
								itm.getItemProperty(TSJ_STATUS).setValue(
										"Completed");
							} else {
								itm.getItemProperty(TSJ_STATUS).setValue(
										"Created");
							}
							itm.getItemProperty(TSJ_STATUS_ID).setValue(
									detailsObj.getStatus());
						}
					}
				}

				componentsTable.setVisibleColumns(new String[] { TSJ_ENABLE,
						TSJ_SN, TSJ_COMPONENT_NAME, TSJ_CREATER_DESCRIPTION,
						TSJ_DESCRIPTION, TSJ_STATUS });

			} else {

				int ct = 1;
				// Iterator
				// itr=compDao.getAllComponents(getOrganizationID()).iterator();
				/*
				 * while (itr.hasNext()) {
				 * 
				 * }
				 */

				if (((Long) tasksComboField.getValue()) != 0) {
					Item itm;
					Iterator itr2 = daoObj.getAssigedComponentsForTask(
							(Long) tasksComboField.getValue()).iterator();
					while (itr2.hasNext()) {
						detailsObj = (TaskComponentDetailsModel) itr2.next();

						comObj = detailsObj.getTask_component();

						if (detailsObj.getStatus() == 1) {
							componentsTable
									.addItem(
											new Object[] {
													new SCheckBox(null, true),
													ct,
													detailsObj.getId(),
													comObj.getName(),
													detailsObj
															.getCreater_description(),
													detailsObj.getDescription(),
													detailsObj.getStatus(),
													"Created" }, comObj.getId());
						} else {
							componentsTable
									.addItem(
											new Object[] {
													new SCheckBox(null, true),
													ct,
													detailsObj.getId(),
													comObj.getName(),
													detailsObj
															.getCreater_description(),
													detailsObj.getDescription(),
													detailsObj.getStatus(),
													"Completed" }, comObj
													.getId());
						}

						ct++;

						/*
						 * 
						 * 
						 * itm=componentsTable.getItem(detailsObj.getTask_component
						 * ().getId()); if(itm!=null) {
						 * ((SCheckBox)itm.getItemProperty
						 * (TSJ_ENABLE).getValue()).setValue(true);
						 * itm.getItemProperty
						 * (TSJ_COMPONENT_DETAILS_ID).setValue
						 * (detailsObj.getId());
						 * itm.getItemProperty(TSJ_DESCRIPTION
						 * ).setValue(detailsObj.getDescription());
						 * if(detailsObj.getStatus()==2) {
						 * itm.getItemProperty(TSJ_STATUS
						 * ).setValue("Completed"); } else {
						 * itm.getItemProperty(TSJ_STATUS).setValue("Created");
						 * }
						 * itm.getItemProperty(TSJ_STATUS_ID).setValue(detailsObj
						 * .getStatus()); }
						 */
					}
				}

				componentsTable.setVisibleColumns(new String[] { TSJ_SN,
						TSJ_COMPONENT_NAME, TSJ_CREATER_DESCRIPTION,
						TSJ_DESCRIPTION, TSJ_STATUS });

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * private void manageAcceptBtn() {
	 * 
	 * if(toInt(modeRadio.getValue().toString())==2) { if
	 * (tasksComboField.getValue() != null &&
	 * !tasksComboField.getValue().equals("") &&
	 * !tasksComboField.getValue().toString().equals("0")) {
	 * if(toLong(status.getValue().toString())==1) { accept.setVisible(true); }
	 * else { accept.setVisible(false); } } else { accept.setVisible(false); } }
	 * else { accept.setVisible(false); }
	 * 
	 * }
	 */

	private void manageComponents() {
		try {
			List<Object> list = null;

			if (toInt(modeRadio.getValue().toString()) == 1) {

				allCompletedCheckBox.setVisible(false);
				allCompletedCheckBox.setValue(false);

				assignUsersSelection.setReadOnly(false);
				assignUsersSelection.setVisible(true);
				starttimeField.setReadOnly(false);
				endtimeField.setReadOnly(false);
				titleField.setReadOnly(false);
				descriptionField.setReadOnly(false);
				// status.setReadOnly(false);

				if (tasksComboField.getValue() != null
						&& !tasksComboField.getValue().equals("")
						&& !tasksComboField.getValue().toString().equals("0")) {
					save.setVisible(false);
					update.setVisible(true);
					delete.setVisible(true);
				} else {
					titleField.setValue("");
					descriptionField.setValue("");
					status.setValue("");
					starttimeField.setValue(new Date());
					assignUsersSelection.setValue(null);
					save.setVisible(true);
					update.setVisible(false);
					delete.setVisible(false);
				}

			} else if (toInt(modeRadio.getValue().toString()) == 2) {

				allCompletedCheckBox.setVisible(true);
				allCompletedCheckBox.setValue(false);

				if (tasksComboField.getValue() == null
						|| tasksComboField.getValue().equals("")
						|| tasksComboField.getValue().toString().equals("0")) {
					titleField.setNewValue("");
					descriptionField.setNewValue("");
					// status.setNewValue("");
					starttimeField.setNewValue(new Date());
					assignUsersSelection.setNewValue(null);

					// update.setVisible(false);
				} else {

					// descriptionField.setReadOnly(true);

					// update.setVisible(true);
				}

				save.setVisible(false);
				assignUsersSelection.setReadOnly(true);
				assignUsersSelection.setVisible(false);
				starttimeField.setReadOnly(true);
				endtimeField.setReadOnly(true);
				titleField.setReadOnly(true);

				save.setVisible(false);

				delete.setVisible(false);

				// status.setReadOnly(true);
				// accept.setVisible(true);
				// update.setVisible(true);
				// delete.setVisible(true);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void setOptionValue(long id) {
		modeRadio.setValue(2);
		tasksComboField.setValue(id);
	}

	private void loadList(long id) {
		try {
			List<Object> list = null;

			if (toInt(modeRadio.getValue().toString()) == 1) {

				list = daoObj.getAllTasksListCreatedByUser(getLoginID());

				TasksModel con = new TasksModel();
				con.setId(0);
				con.setTitle("-------------- Create New ---------------");
				if (list == null)
					list = new ArrayList();
				list.add(0, con);

				CollectionContainer bic = CollectionContainer.fromBeans(list,
						"id");
				tasksComboField.setContainerDataSource(bic);
				tasksComboField.setItemCaptionPropertyId("title");
				tasksComboField
						.setInputPrompt("-------------- Create New ---------------");

			} else if (toInt(modeRadio.getValue().toString()) == 2) {

				list = daoObj.getAllTasksListAssignedToUser(getLoginID());

				CollectionContainer bic = CollectionContainer.fromBeans(list,
						"id");
				tasksComboField.setContainerDataSource(bic);
				tasksComboField.setItemCaptionPropertyId("title");
				tasksComboField
						.setInputPrompt("-------------- Select ---------------");

			}

			manageComponents();

			tasksComboField.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		clearError();
		boolean valid = true;

		if (status.getValue() == null || status.getValue().equals("")) {
			setRequiredError(status, getPropertyName("select_status"), true);
			valid = false;
		} else
			setRequiredError(status, null, false);

		if (actualcompletiontimeField.getValue() == null
				|| actualcompletiontimeField.getValue().equals("")) {
			setRequiredError(actualcompletiontimeField,
					getPropertyName("select_status"), true);
			valid = false;
		} else
			setRequiredError(actualcompletiontimeField, null, false);

		if (endtimeField.getValue() == null
				|| endtimeField.getValue().equals("")) {
			setRequiredError(endtimeField, getPropertyName("select_date_time"),
					true);
			valid = false;
		} else
			setRequiredError(endtimeField, null, false);

		if (starttimeField.getValue() == null
				|| starttimeField.getValue().equals("")) {
			setRequiredError(starttimeField,
					getPropertyName("select_date_time"), true);
			valid = false;
		} else
			setRequiredError(starttimeField, null, false);

		if (assignUsersSelection.getValue() == null
				|| assignUsersSelection.getValue().equals("")) {
			setRequiredError(assignUsersSelection,
					getPropertyName("select_user"), true);
			valid = false;
		} else {

			if (((Set<Long>) assignUsersSelection.getValue()).size() <= 0) {
				setRequiredError(assignUsersSelection,
						getPropertyName("select_user"), true);
				valid = false;
			}

			setRequiredError(assignUsersSelection, null, false);
		}

		if (titleField.getValue() == null || titleField.getValue().equals("")) {
			setRequiredError(titleField, getPropertyName("enter_title"), true);
			valid = false;
		} else
			setRequiredError(titleField, null, false);

		if (valid) {
			boolean added = false;
			if ((Integer) modeRadio.getValue() == 1) {
				Iterator itr2 = componentsTable.getItemIds().iterator();
				long comp_id;
				while (itr2.hasNext()) {
					if (((SCheckBox) componentsTable.getItem(itr2.next())
							.getItemProperty(TSJ_ENABLE).getValue()).getValue() == true) {
						added = true;
					}
				}
			} else
				added = true;

			if (!added) {
				setRequiredError(componentsTable,
						getPropertyName("check_task_components"), true);
				valid = false;
			} else
				setRequiredError(componentsTable, null, false);

		}

		return valid;
	}

	private void clearError() {
		titleField.setComponentError(null);
		descriptionField.setComponentError(null);
	}

	public boolean isCreatePrivileaged() {
		return (Boolean) getHttpSession().getAttribute("task_add_enabled");
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public SComboField getConnecComboField() {
		return tasksComboField;
	}

	public void setConnecComboField(SComboField tasksComboField) {
		this.tasksComboField = tasksComboField;
	}

}
