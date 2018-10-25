
package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.dao.EmployeeWorkingTimeDao;
import com.inventory.payroll.dao.PayrollComponentDao;
import com.inventory.payroll.dao.PayrollEmployeeMapDao;
import com.inventory.payroll.model.PayrollComponentModel;
import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 5, 2013
 */

/**
 * @author sangeeth
 * Hotel
 * 28-Jan-2016
 */

@SuppressWarnings("serial")
public class PayrollEmployeeMapUI extends SparkLogic {

	private SComboField employeeComboField;
	private SDateField dateField;
	private STable table;
	private SComboField componentCombo;
	private STextField valueField;
	private STextField amountField;

	private SButton addItemButton;
	private SButton updateItemButton;
	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;

	private Object[] allHeaders;
	private Object[] requiredHeaders;

	private static final String TBL_ID = "id";
	private static final String TBL_COMPONENTS = "Component";
	private static final String TBL_ACTION = "Action";
	private static final String TBL_TYPE_ID = "Type Id";
	private static final String TBL_TYPE = "Type";
	private static final String TBL_TYPE_VALUE = "Value";
	private static final String TBL_VALUE = "Net Amount";

	private PayrollEmployeeMapDao dao;

	private SettingsValuePojo settings;
	private WrappedSession session;

	private EmployeeWorkingTimeDao workDao;
	
	List<Long> tableList;

	@Override
	public SPanel getGUI() {
		setSize(650, 450);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		tableList=new ArrayList<Long>();
		
		try {
			SVerticalLayout formLayout = new SVerticalLayout();
			formLayout.setMargin(true);

			SHorizontalLayout componentLayout = new SHorizontalLayout();
			componentLayout.setSpacing(true);

			SHorizontalLayout employeeLayout = new SHorizontalLayout();
			employeeLayout.setSpacing(true);

			SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
			buttonHorizontalLayout.setSpacing(true);

			SGridLayout gridLayout = new SGridLayout();
			gridLayout.setSizeFull();
			gridLayout.setRows(1);
			gridLayout.setColumns(7);

			SGridLayout buttonGridLayout = new SGridLayout();
			buttonGridLayout.setRows(1);
			buttonGridLayout.setSizeFull();
			buttonGridLayout.setColumns(10);

			dao = new PayrollEmployeeMapDao();
			workDao = new EmployeeWorkingTimeDao();

			session = getHttpSession();

			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
			
			allHeaders = new String[] { TBL_ID, TBL_COMPONENTS,TBL_ACTION, TBL_TYPE_ID, TBL_TYPE, TBL_TYPE_VALUE, TBL_VALUE };
			requiredHeaders = new String[] { TBL_COMPONENTS, TBL_TYPE, TBL_TYPE_VALUE, TBL_VALUE };

			employeeComboField = new SComboField(getPropertyName("employee"), 200);
			employeeComboField
					.setInputPrompt(getPropertyName("select"));
			loadEmployee(0);

			dateField = new SDateField(getPropertyName("date"), 100);
			dateField.setValue(getWorkingDate());

			employeeLayout.addComponent(new SFormLayout(employeeComboField));
			employeeLayout.addComponent(new SFormLayout(dateField));

			table = new STable(null, 500, 200);
			table.setSelectable(true);
			table.addContainerProperty(TBL_ID, Long.class, null, "id", null,Align.CENTER);
			table.addContainerProperty(TBL_ACTION, Long.class, null, TBL_ACTION, null,Align.CENTER);
			table.addContainerProperty(TBL_COMPONENTS, String.class, null,getPropertyName("component"), null, Align.LEFT);
			table.addContainerProperty(TBL_TYPE_ID, Long.class, null, TBL_TYPE_ID, null,Align.RIGHT);
			table.addContainerProperty(TBL_TYPE, String.class, null, TBL_TYPE, null,Align.LEFT);
			table.addContainerProperty(TBL_TYPE_VALUE, Double.class, null, TBL_TYPE_VALUE, null, Align.RIGHT);
			table.addContainerProperty(TBL_VALUE, Double.class, null, TBL_VALUE, null, Align.RIGHT);
			table.setFooterVisible(true);
			table.setColumnFooter(TBL_COMPONENTS, getPropertyName("total"));
			loadTableTotal();
			
			table.setVisibleColumns(requiredHeaders);
			gridLayout.addComponent(table, 2, 0);
			gridLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);

			componentCombo = new SComboField(getPropertyName("component"), 150);
			componentCombo.setInputPrompt(getPropertyName("select"));
			componentCombo.focus();
			loadComponents(0);

			valueField = new STextField(getPropertyName("value"), 75);
			valueField.setImmediate(true);
			amountField = new STextField(getPropertyName("amount"), 75);
			amountField.setImmediate(true);
			amountField.setReadOnly(true);

			addItemButton = new SButton(null, getPropertyName("add"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("Update"));
			updateItemButton.setVisible(false);
			updateItemButton.setStyleName("updateItemBtnStyle");

			componentLayout.addComponent(new SFormLayout(componentCombo));
			componentLayout.addComponent(new SFormLayout(valueField));
			componentLayout.addComponent(new SFormLayout(amountField));
			componentLayout.addComponent(new SFormLayout(addItemButton));
			componentLayout.addComponent(new SFormLayout(updateItemButton));

			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updateButton = new SButton(getPropertyName("Update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			updateButton.setVisible(false);

			deleteButton = new SButton(getPropertyName("Delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			deleteButton.setVisible(false);

			buttonHorizontalLayout.addComponent(saveButton);
			buttonHorizontalLayout.addComponent(updateButton);
			buttonHorizontalLayout.addComponent(deleteButton);
			buttonGridLayout.addComponent(buttonHorizontalLayout, 5, 0);

			formLayout.addComponent(employeeLayout);
			formLayout.addComponent(gridLayout);
			formLayout.addComponent(componentLayout);
			formLayout.addComponent(buttonGridLayout);

			panel.setContent(formLayout);

			
			valueField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						double value=0;
						double amount=0;
						try {
							value=toDouble(valueField.getValue().trim());
						} catch (Exception e) {
							value=0;
						}
						if (componentCombo.getValue() != null && !componentCombo.getValue().equals("")) {
							PayrollComponentModel mdl=new PayrollComponentDao().getComponentModel(toLong(componentCombo.getValue().toString().trim()));
							if(mdl.getType()==SConstants.payroll.FIXED){
								amount=value;
							}
							else{
								if(mdl.getParent_id()!=0){
									Iterator itr=table.getItemIds().iterator();
									while (itr.hasNext()) {
										Item item = table.getItem(itr.next());
										double amt=0;
										if((Long)item.getItemProperty(TBL_ID).getValue()!=mdl.getParent_id())
											continue;
										else{
											amt=(Double)item.getItemProperty(TBL_VALUE).getValue();
											amount=roundNumber(amt*value/100);
										}
									}
								}
							}
						}
						else{
							amount=value;
						}
						valueField.setValue(roundNumber(value)+"");
						amountField.setNewValue(roundNumber(amount)+"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			valueField.setValue("0.0");
			
			
			addShortcutListener(new ShortcutListener("Add", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible()) {
						addItemButton.click();
					} else {
						updateItemButton.click();
					}
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Delete", ShortcutAction.KeyCode.DELETE, null) {

				@Override
				public void handleAction(Object sender, Object target) {
					if (table.getValue() != null) {
						table.removeItem(table.getValue());
					}
				}
			});

			
			final Action actionDelete = new Action("Delete");

			
			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					deleteItem();
				}

			});
			
			
			addItemButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						boolean isAddble=true;
						if (isSelectionValid(0)) {
							clearErrors();
							PayrollComponentModel mdl=new PayrollComponentDao().getComponentModel(toLong(componentCombo.getValue().toString().trim()));
							if(mdl.getParent_id()!=0){
								if(!tableList.contains(mdl.getParent_id()))
									isAddble=false;
							}
							if(isAddble){
								table.setVisibleColumns(allHeaders);
								table.addItem(new Object[] {toLong(componentCombo.getValue().toString().trim()),
															componentCombo.getItemCaption(componentCombo.getValue()),
															mdl.getAction(),
															mdl.getType(),
															getType(mdl.getType()),
															roundNumber(toDouble(valueField.getValue().toString().trim())) ,
															roundNumber(toDouble(amountField.getValue().toString().trim()))}, table.getItemIds().size() + 1);
								if(mdl.getId()!=0){
									if(!tableList.contains(mdl.getId()))
										tableList.add(mdl.getId());
								}
								table.setVisibleColumns(requiredHeaders);
								loadTableTotal();
								componentCombo.setValue(null);
								valueField.setValue("0.0");
								addItemButton.setVisible(true);
								updateItemButton.setVisible(false);
								componentCombo.focus();
							}
							else
								SNotification.show("Add Parent Payroll Component", Type.WARNING_MESSAGE);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (table.getValue() != null) {
						Item item = table.getItem(table.getValue());
						componentCombo.setValue(toLong(item.getItemProperty(TBL_ID).toString()));
						valueField.setValue(item.getItemProperty(TBL_TYPE_VALUE).toString());
						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
						componentCombo.focus();
					} else {
						componentCombo.setValue(null);
						valueField.setValue("0.0");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						componentCombo.focus();
					}
				}
			});
			
			
			updateItemButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						if (isSelectionValid(toLong(componentCombo.getValue().toString()))) {
							if (table.getValue() != null) {
								boolean isAddble=true;
								Item item = table.getItem(table.getValue());
								PayrollComponentModel mdl=new PayrollComponentDao().getComponentModel(toLong(componentCombo.getValue().toString().trim()));
								if(tableList.contains((Long)item.getItemProperty(TBL_ID).getValue()))
									tableList.remove((Long)item.getItemProperty(TBL_ID).getValue());
								if(mdl.getParent_id()!=0){
									if(!tableList.contains(mdl.getParent_id()))
										isAddble=false;
								}
								if(isAddble){
									item.getItemProperty(TBL_ID).setValue(toLong(componentCombo.getValue().toString()));
									item.getItemProperty(TBL_COMPONENTS).setValue(componentCombo.getItemCaption(componentCombo.getValue()));
									item.getItemProperty(TBL_ACTION).setValue(mdl.getAction());
									item.getItemProperty(TBL_TYPE_ID).setValue(mdl.getType());
									item.getItemProperty(TBL_TYPE).setValue(getType(mdl.getType()));
									item.getItemProperty(TBL_TYPE_VALUE).setValue(roundNumber(toDouble(valueField.getValue().toString())));
									item.getItemProperty(TBL_VALUE).setValue(roundNumber(toDouble(amountField.getValue().toString())));
									if(mdl.getId()!=0){
										if(!tableList.contains(mdl.getId()))
											tableList.add(mdl.getId());
									}
									table.setValue(null);
									loadTableTotal();
								}
								else
									SNotification.show("Add Parent Payroll Component", Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			componentCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {

					try {
						double value=0;
						clearErrors();
						if (componentCombo.getValue() != null && !componentCombo.getValue().equals("")) {
							PayrollComponentModel mdl = new PayrollComponentDao().getComponentModel(toLong(componentCombo.getValue().toString()));
							value=mdl.getValue();
							if (mdl.getType() == SConstants.payroll.PERCENTAGE) {
								valueField.setCaption(getPropertyName("percentage"));
							} else {
								valueField.setCaption(getPropertyName("Value"));
							}
						}
						valueField.setValue(roundNumber(value)+"");
						valueField.focus();
						valueField.selectAll();
						if (componentCombo.getValue() != null && !table.getItemIds().isEmpty() && table.getValue() != null) {
							if (toLong(componentCombo.getValue().toString()) != toLong(table.getItem(table.getValue()).getItemProperty(TBL_ID).toString()))
								table.select(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			employeeComboField.addValueChangeListener(new ValueChangeListener() {

				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {

						table.removeAllItems();
						table.setVisibleColumns(allHeaders);
						componentCombo.setValue(null);
						valueField.setValue("0.0");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						componentCombo.focus();
						tableList.clear();
						if (employeeComboField.getValue() != null && !employeeComboField.getValue().equals("")) {

							List list = dao.getPayRollMap((Long)employeeComboField.getValue(), getOfficeID());
							if (list != null && list.size() != 0) {
								Iterator iterator = list.iterator();
								PayrollEmployeeMapModel model;
								while (iterator.hasNext()) {
									model = (PayrollEmployeeMapModel) iterator.next();
									dateField.setValue(model.getDate());
									table.addItem(new Object[] {model.getComponent().getId(),
																model.getComponent().getName()+ "("+ model.getComponent().getCode() + ")",
																model.getComponent().getAction(),
																model.getType(),
																getType(model.getType()),
																roundNumber(model.getTypeValue()),
																roundNumber(model.getValue())}, table.getItemIds().size()+1);
									tableList.add(model.getComponent().getId());
								}
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
								loadTableTotal();
							} else {
								saveButton.setVisible(true);
								updateButton.setVisible(false);
								deleteButton.setVisible(false);
							}
						}
						table.setVisibleColumns(requiredHeaders);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			saveButton.addClickListener(new ClickListener() {

				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						List list=new ArrayList();
						list = getEmployeeMapList();
						if (list.size() > 0) {
							try {
								dao.save(list);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								Object obj=employeeComboField.getValue();
								employeeComboField.setValue(null);
								employeeComboField.setValue(obj);
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
							}
						}
					}
				}

			});

			
			updateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						try {
							List list=new ArrayList();
							list = getEmployeeMapList();
							dao.update(list, getOfficeID(), (Long)employeeComboField.getValue());
							SNotification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							Object obj=employeeComboField.getValue();
							employeeComboField.setValue(null);
							employeeComboField.setValue(obj);
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);						}
					}
				}
			});

			
			deleteButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (employeeComboField.getValue() != null && !employeeComboField.getValue().equals("")) {
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											dao.delete(getOfficeID(), (Long)employeeComboField.getValue());
											SNotification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											Object obj=employeeComboField.getValue();
											employeeComboField.setValue(null);
											employeeComboField.setValue(obj);
										} catch (Exception e) {
											e.printStackTrace();
											Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
										}
									}
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),
								Type.WARNING_MESSAGE);
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	
	@SuppressWarnings("rawtypes")
	protected boolean isSelectionValid(long id) {
		clearErrors();
		boolean valid = true;
		if (componentCombo.getValue() == null || componentCombo.getValue().equals("")) {
			valid = false;
			setRequiredError(componentCombo,getPropertyName("invalid_selection"), true);
		} 
		else {
			Iterator itr = table.getItemIds().iterator();
			while (itr.hasNext()) {
				Item item = table.getItem(itr.next());
				if (toLong(componentCombo.getValue().toString().trim()) != id) {
					if (toLong(componentCombo.getValue().toString().trim()) == (Long)item.getItemProperty(TBL_ID).getValue()) {
						valid = false;
						setRequiredError(componentCombo,getPropertyName("already_added"), true);
					}
				}
			}
		}
		
		if (valueField.getValue() == null || valueField.getValue().equals("")) {
			valid = false;
			setRequiredError(valueField, getPropertyName("invalid_data"), true);
		}

		try {
			toDouble(valueField.getValue());
		} catch (Exception e) {
			valid = false;
			setRequiredError(valueField, getPropertyName("invalid_data"), true);
		}

		return valid;
	}

	
	@SuppressWarnings("rawtypes")
	public void loadTableTotal(){
		double total=0;
		Iterator itr = table.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			if(toLong(item.getItemProperty(TBL_ACTION).getValue().toString())==SConstants.payroll.ADDITION)
				total+=roundNumber(toDouble(item.getItemProperty(TBL_VALUE).getValue().toString()));
			else
				total-=roundNumber(toDouble(item.getItemProperty(TBL_VALUE).getValue().toString()));
			
		}
		table.setColumnFooter(TBL_VALUE, roundNumber(total)+"");
	}
	
	
	private void clearErrors() {
		componentCombo.setComponentError(null);
		valueField.setComponentError(null);
		table.setComponentError(null);
		employeeComboField.setComponentError(null);
		dateField.setComponentError(null);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getEmployeeMapList() {
		List list=new ArrayList();
		Iterator itr = table.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			PayrollEmployeeMapModel model = new PayrollEmployeeMapModel();
			model.setEmployee(new UserModel(toLong(employeeComboField.getValue().toString())));
			model.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
			model.setComponent(new PayrollComponentModel(toLong(item.getItemProperty(TBL_ID).getValue().toString())));
			model.setValue(roundNumber(toDouble(item.getItemProperty(TBL_VALUE).getValue().toString())));
			model.setTypeValue(roundNumber(toDouble(item.getItemProperty(TBL_TYPE_VALUE).getValue().toString())));
			model.setType(toLong(item.getItemProperty(TBL_TYPE_ID).getValue().toString()));
			list.add(model);
		}
		return list;
	}

	
	@SuppressWarnings("unchecked")
	private void loadEmployee(int id) {
		List<Object> list = new ArrayList<Object>();
		try {
			if (settings.isSHOW_ALL_EMPLOYEES_ON_PAYROLL())
				list = workDao.getEmployees(getOrganizationID());
			else
				list = workDao.getEmployeesUnderOffice(getOfficeID());
			SCollectionContainer container = SCollectionContainer.setList(list,"id");
			employeeComboField.setContainerDataSource(container);
			employeeComboField.setItemCaptionPropertyId("first_name");

			if (id > 0) {
				employeeComboField.setValue(id);
			} else {
				employeeComboField.setValue(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	private void loadComponents(long id) {

		List<Object> list = new ArrayList<Object>();
		try {
			list.addAll(new PayrollComponentDao().getAllComponents(getOfficeID()));
			SCollectionContainer container = SCollectionContainer.setList(list,
					"id");
			componentCombo.setContainerDataSource(container);
			componentCombo.setItemCaptionPropertyId("name");

			if (id > 0) {
				componentCombo.setValue(id);
			} else {
				componentCombo.setValue(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {

		clearErrors();
		boolean valid = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("invalid_data"), true);
			valid = false;
		}

		if (employeeComboField.getValue() == null
				|| employeeComboField.getValue().equals("")) {
			setRequiredError(employeeComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
		if (dateField.getValue() == null) {
			setRequiredError(dateField, getPropertyName("invalid_data"), true);
			valid = false;
		}
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	public void deleteItem(){
		if(table.getValue()!=null){
			Object obj=table.getValue();
			Item item=table.getItem(obj);
			if(tableList.contains((Long)item.getItemProperty(TBL_ID).getValue()))
				tableList.remove((Long)item.getItemProperty(TBL_ID).getValue());
			table.removeItem(obj);
		}
	}
	
	
	public String getType(long type){
		String typ="Fixed";
		switch (Integer.parseInt(type+"")) {
				case 1:	typ="Percentage";				
						break;
				case 2:	typ="Fixed";				
						break;
				default: typ="Fixed";
						break;
		}
		return typ;
	}
	
	
}
