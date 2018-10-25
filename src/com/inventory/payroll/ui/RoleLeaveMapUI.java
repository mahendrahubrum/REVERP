package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.LeaveTypeDao;
import com.inventory.payroll.dao.RoleLeaveMapDao;
import com.inventory.payroll.model.LeaveTypeModel;
import com.inventory.payroll.model.RoleLeaveMapModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.RoleDao;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @author sangeeth
 * @date 11-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class RoleLeaveMapUI extends SparkLogic {

	private static final String TBL_ID = "Id";
	private static final String TBL_LEAVE_TYPE_ID = "Leave Id";
	private static final String TBL_LEAVE_TYPE = "Leave Type";
	private static final String TBL_COUNT = "No/Year";

	private SComboField roleCombo;
	private SComboField leaveTypeCombo;
	private STextField countField;

	private SButton saveButton;
	private SButton deleteButton;

	private SButton addButton;
	private SButton removeButton;
	private SButton updateButton;

	private STable table;

	private Object[] allHeaders;
	private Object[] reqHeaders;
	
	private RoleLeaveMapDao dao;
	private LeaveTypeDao leaveDao;
	
	InlineDateField year;

	@Override
	public SPanel getGUI() {

		SPanel pan = new SPanel();
		setSize(625, 450);
		pan.setSizeFull();

		SFormLayout lay = new SFormLayout();
		lay.setMargin(true);
//		lay.setWidth("600px");
//		lay.setStyleName("layout_bordered");

		SHorizontalLayout btnLayout = new SHorizontalLayout();
		btnLayout.setSpacing(true);

		SGridLayout btnGrid = new SGridLayout(6, 1);
		btnGrid.setSizeFull();
		SGridLayout desigGrid = new SGridLayout(5, 1);
		desigGrid.setSpacing(true);
		SGridLayout grid = new SGridLayout(10, 1);
		grid.setSpacing(true);

		pan.setContent(lay);
		
		dao=new RoleLeaveMapDao();
		leaveDao=new LeaveTypeDao();
		

		allHeaders = new Object[] {	TBL_ID, TBL_LEAVE_TYPE_ID, TBL_LEAVE_TYPE, TBL_COUNT };
		reqHeaders = new Object[] { TBL_LEAVE_TYPE, TBL_COUNT };

		try {
			
			year=new InlineDateField();
			year.setResolution(Resolution.YEAR);
			
			leaveTypeCombo = new SComboField(null, 150,leaveDao.getLeaveTypeModelList(getOfficeID()), "id", "name");
			leaveTypeCombo.setInputPrompt("--------------Select----------------");
			roleCombo = new SComboField(null, 150,new RoleDao().getAllRoles(),"id", "role_name");
			roleCombo.setInputPrompt("--------------Select----------------");
			
			countField = new STextField(null, 75);
			countField.setValue("0.0");
			countField.setStyleName("textfield_align_right");

			addButton = new SButton("Add");
			updateButton = new SButton("Update");
			removeButton = new SButton("Remove");
			updateButton.setVisible(false);
			removeButton.setVisible(false);

			desigGrid.addComponent(new SLabel("Year"), 0, 0);
			desigGrid.addComponent(year, 2, 0);
			desigGrid.addComponent(new SLabel("Designation"), 3, 0);
			desigGrid.addComponent(roleCombo, 4, 0);
			
			grid.addComponent(new SLabel("Leave Type"), 3, 0);
			grid.addComponent(leaveTypeCombo, 4, 0);
			grid.addComponent(new SLabel("Count/ Year"), 5, 0);
			grid.addComponent(countField, 6, 0);
			grid.addComponent(addButton, 7, 0);
			grid.addComponent(updateButton, 8, 0);
			grid.addComponent(removeButton, 9, 0);

			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			
			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");

			btnLayout.addComponent(saveButton);
			btnLayout.addComponent(deleteButton);
			
			btnGrid.addComponent(btnLayout,3,0);

			table = new STable(null, 550, 250);

			table.addContainerProperty(TBL_ID, Long.class, null, TBL_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_LEAVE_TYPE_ID, Long.class, null, TBL_LEAVE_TYPE_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_LEAVE_TYPE, String.class, null, TBL_LEAVE_TYPE, null, Align.LEFT);
			table.addContainerProperty(TBL_COUNT, Double.class, null, TBL_COUNT, null, Align.CENTER);

			table.setVisibleColumns(reqHeaders);
			table.setEditable(false);
			table.setSelectable(true);

			lay.addComponent(desigGrid);
			lay.addComponent(table);
			lay.addComponent(grid);
			lay.addComponent(btnGrid);

			addButton.addClickListener(new ClickListener() {

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					if (isAddingValid()) {
						table.setVisibleColumns(allHeaders);
						Iterator itr=table.getItemIds().iterator();
						Item itm=null;
						boolean alreadyAdded=false;
						while (itr.hasNext()) {
							itm = table.getItem(itr.next());
							if (itm.getItemProperty(TBL_LEAVE_TYPE_ID).getValue().toString().equals(leaveTypeCombo.getValue().toString())) {
								itm.getItemProperty(TBL_COUNT).setValue(roundNumber(toDouble(countField.getValue().toString())));
								alreadyAdded=true;
								break;
							}
						}
						if(!alreadyAdded){
							table.addItem(new Object[] {(long)0,
														(Long) leaveTypeCombo.getValue(),
														leaveTypeCombo.getItemCaption(leaveTypeCombo.getValue()),
														roundNumber(toDouble(countField.getValue()))},table.getItemIds().size()+1);
						}
						table.setVisibleColumns(reqHeaders);
						leaveTypeCombo.setNewValue(null);
						countField.setValue("0.0");
					}
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						leaveTypeCombo.setReadOnly(false);
						Item item=table.getItem(table.getValue());
						leaveTypeCombo.setNewValue(toLong(item.getItemProperty(TBL_LEAVE_TYPE_ID).getValue().toString()));
						countField.setValue(roundNumber((Double)item.getItemProperty(TBL_COUNT).getValue())+"");
						leaveTypeCombo.setReadOnly(true);
						addButton.setVisible(false);
						updateButton.setVisible(true);
						removeButton.setVisible(true);
					}
					else{
						leaveTypeCombo.setReadOnly(false);
						countField.setValue("0.0");
						addButton.setVisible(true);
						updateButton.setVisible(false);
						removeButton.setVisible(false);
					}
				}
			});
			
			
			updateButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
				if(isAddingValid()){
					Item itm=table.getItem(table.getValue());
					itm.getItemProperty(TBL_COUNT).setValue(toDouble(countField.getValue().toString()));
					itm.getItemProperty(TBL_LEAVE_TYPE_ID).setValue(toLong(leaveTypeCombo.getValue().toString()));
					itm.getItemProperty(TBL_LEAVE_TYPE).setValue(leaveTypeCombo.getItemCaption(leaveTypeCombo.getValue()));
					leaveTypeCombo.setReadOnly(false);
					leaveTypeCombo.setNewValue(null);
					countField.setValue("0.0");
					table.setValue(null);
				}
				}
			});

			
			removeButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					table.removeItem(table.getValue());
					leaveTypeCombo.setReadOnly(false);
					leaveTypeCombo.setValue(null);
					countField.setValue("0.0");
				}
			});
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							Iterator itr=table.getItemIds().iterator();
							
							List list=new ArrayList();
							
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								RoleLeaveMapModel mdl=null;
								if((Long)item.getItemProperty(TBL_ID).getValue()!=0)
									mdl=dao.getRoleLeaveMapModel((Long)item.getItemProperty(TBL_ID).getValue());
								if(mdl==null)	
									mdl=new RoleLeaveMapModel();
								mdl.setRole(new S_UserRoleModel((Long)roleCombo.getValue()));
								mdl.setLeave_type(new LeaveTypeModel((Long)item.getItemProperty(TBL_LEAVE_TYPE_ID).getValue()));
								mdl.setValue(roundNumber((Double)item.getItemProperty(TBL_COUNT).getValue()));
								mdl.setYear(getYear());
								mdl.setOfficeId(getOfficeID());
								list.add(mdl);
							}
							dao.save(list,(Long)roleCombo.getValue(), getYear(), getOfficeID());
							loadData();
							SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
		
			
			roleCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					
						loadData();
				}
			});
			
			
			deleteButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(table.getItemIds().size()>0){
						ConfirmDialog.show(getUI().getCurrent(), "Are you sure?",new ConfirmDialog.Listener() {
							@Override
							public void onClose(ConfirmDialog arg0) {
								if(arg0.isConfirmed()){
									try {
										dao.delete((Long)roleCombo.getValue(), getYear(), getOfficeID());
										loadData();
										SNotification.show("Deleted Successfully",Type.WARNING_MESSAGE);
									} catch (Exception e) {
										e.printStackTrace();
										SNotification.show("Unable to delete",Type.ERROR_MESSAGE);
									}
								}
							}
						});
					}else{
						setRequiredError(table, "No data to delete", true);
					}
				}
			});
			
			
			year.setImmediate(true);
			year.addListener(new Listener() {
				
				@Override
				public void componentEvent(Event event) {
					
					loadData();
				}
			});

			
			addShortcutListener(new ShortcutListener("Add Leave", ShortcutAction.KeyCode.ENTER,null) {
				
				@Override
				public void handleAction(Object sender, Object target) {
					if(addButton.isVisible())
						addButton.click();
					else
						updateButton.click();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pan;
	}

	
	protected long getYear() {
		Calendar cal = Calendar.getInstance();
	    cal.setTime(year.getValue());
		return cal.get(Calendar.YEAR);
	}

	
	@SuppressWarnings("rawtypes")
	protected void loadData() {
		RoleLeaveMapModel map=null;
		try {
			table.removeAllItems();
			if(roleCombo.getValue()!=null&&!roleCombo.getValue().equals("")){
				table.setVisibleColumns(allHeaders);
				List lis=dao.getRoleMap((Long)roleCombo.getValue(), getYear(), getOfficeID());
				Iterator iter=lis.iterator();
				while (iter.hasNext()) {
					map =  (RoleLeaveMapModel) iter.next();
					table.addItem(new Object[] {map.getId(),
												map.getLeave_type().getId(),
												map.getLeave_type().getName(),
												map.getValue()},table.getItemIds().size()+1);
				}
				table.setVisibleColumns(reqHeaders);
			}
			leaveTypeCombo.setReadOnly(false);
			leaveTypeCombo.setValue(null);
			countField.setValue("0.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	protected boolean isAddingValid() {
		countField.setComponentError(null);
		leaveTypeCombo.setComponentError(null);
		
		boolean valid=true;
		if(leaveTypeCombo.getValue()==null||leaveTypeCombo.getValue().equals("")){
			setRequiredError(leaveTypeCombo, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		if(countField.getValue()==null||countField.getValue().equals("")){
			setRequiredError(countField, getPropertyName("invalid_data"), true);
			valid=false;
		}else{
			try{
				if(toDouble(countField.getValue().toString())<0){
					setRequiredError(countField, getPropertyName("invalid_data"), true);
					valid=false;
				}
			}catch (Exception e){
				setRequiredError(countField, getPropertyName("invalid_data"), true);
				valid=false;
			}
		}
		
		return valid;
	}

	
	@Override
	public Boolean isValid() {
		table.setComponentError(null);
		roleCombo.setComponentError(null);
		boolean valid=true;
		if(table.getItemIds().size()<=0){
			setRequiredError(table, "Cannot be empty", true);
			valid=false;
		}
		if(roleCombo.getValue()==null||roleCombo.getValue().equals("")){
			setRequiredError(roleCombo, "Select a designation", true);
			valid=false;
		}
		
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
