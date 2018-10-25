package com.inventory.payroll.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.AttendanceDao;
import com.inventory.payroll.dao.EmployeeAttendanceListDao;
import com.inventory.payroll.dao.LeaveDao;
import com.inventory.payroll.dao.LeaveTypeDao;
import com.inventory.payroll.model.AttendanceModel;
import com.inventory.payroll.model.LeaveModel;
import com.inventory.payroll.model.LeaveTypeModel;
import com.inventory.payroll.model.UserLeaveMapModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class EmployeeMarkLeaveUI extends SparkLogic{

	SPanel mainPanel;
	SVerticalLayout mainLayout;
	
	public InlineDateField dateField;
	public SComboField userCombo;
	STable table;
	STable subTable;
	SimpleDateFormat sdf;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	
	Calendar mainCalendar;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_DATE_ID = "Date Id";
	static String TBC_DATE = "Date";
	static String TBC_IN = "In";
	static String TBC_OUT = "Out";
	static String TBC_LEAVE_TYPE = "Leave Type";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	EmployeeAttendanceListDao dao;
	
	@SuppressWarnings({"rawtypes"})
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		dao=new EmployeeAttendanceListDao();
		setSize(800, 600);
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		sdf = new SimpleDateFormat("hh:mm:a");
		allHeaders=new Object[]{ TBC_SN, TBC_ID, TBC_DATE_ID, TBC_DATE, TBC_IN, TBC_OUT, TBC_LEAVE_TYPE };
		
		requiredHeaders=new Object[]{ TBC_SN, TBC_DATE, TBC_IN, TBC_OUT, TBC_LEAVE_TYPE };
		
		mainCalendar=Calendar.getInstance();
		try {
			dateField=new InlineDateField("Month");
			dateField.setImmediate(true);
			dateField.setResolution(Resolution.MONTH);
			
			userCombo=new SComboField("Employee", 150, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), isSuperAdmin()), "id", "first_name", true, "Select");
			userCombo.setReadOnly(true);
			
			table=new STable();
			table.setWidth("750");
			table.setHeight("450");
			table.setSelectable(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DATE_ID, Date.class, null, TBC_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, String.class, null, TBC_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_IN, String.class, null, TBC_IN, null, Align.LEFT);
			table.addContainerProperty(TBC_OUT, String.class, null, TBC_OUT, null, Align.LEFT);
			table.addContainerProperty(TBC_LEAVE_TYPE, SComboField.class, null, TBC_LEAVE_TYPE, null, Align.LEFT);
			
			table.setColumnExpandRatio(TBC_SN, .25f);
			table.setColumnExpandRatio(TBC_DATE, 1f);
			table.setColumnExpandRatio(TBC_IN, 1f);
			table.setColumnExpandRatio(TBC_OUT, 1f);
			table.setColumnExpandRatio(TBC_LEAVE_TYPE, 1.5f);
			
			table.setVisibleColumns(requiredHeaders);
			
			subTable=new STable();
			subTable.setWidth("750");
			subTable.setHeight("400");
			subTable.setSelectable(true);
			
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/save.png"));
			saveButton.setStyleName("saveButtonStyle");
			updateButton = new SButton(getPropertyName("update"), 100, 25);
			updateButton.setIcon(new ThemeResource("icons/update.png"));
			updateButton.setStyleName("updateButtonStyle");
			deleteButton = new SButton(getPropertyName("delete"), 100, 25);
			deleteButton.setIcon(new ThemeResource("icons/delete.png"));
			deleteButton.setStyleName("deleteButtonStyle");
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
//			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			
//			updateButton.setVisible(false);
//			deleteButton.setVisible(false);
			
			mainLayout.addComponent(table);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(dateField.getValue()!=null){
							mainCalendar.setTime(dateField.getValue());
							mainCalendar.set(Calendar.DAY_OF_MONTH, 1);
							mainCalendar.set(Calendar.HOUR_OF_DAY, 0);
							mainCalendar.set(Calendar.MINUTE, 0);
							mainCalendar.set(Calendar.SECOND, 0);
							mainCalendar.set(Calendar.MILLISECOND, 0);
							loadTable(mainCalendar.getTime());
						}
						else
							table.removeAllItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							Iterator itr=table.getItemIds().iterator();
							List<UserLeaveMapModel> leaveList=new ArrayList<UserLeaveMapModel>();
							Date start,end;
							start=mainCalendar.getTime();
							Calendar cal=Calendar.getInstance();
							cal.setTime(mainCalendar.getTime());
							cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
							end=cal.getTime();
							
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								SComboField leaveType=(SComboField)item.getItemProperty(TBC_LEAVE_TYPE).getValue();
								if(!leaveType.isReadOnly()){
									UserLeaveMapModel mdl=null;
									long id=(Long)item.getItemProperty(TBC_ID).getValue();
									if(id!=0)
										mdl=dao.getUserLeaveMapModel(id);
									if(mdl==null)
										mdl=new UserLeaveMapModel();
									mdl.setUserId((Long)userCombo.getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_DATE_ID).getValue()));
									mdl.setLeave_type(new LeaveTypeModel((Long)leaveType.getValue()));
									mdl.setOfficeId(getOfficeID());
									leaveList.add(mdl);
								}
							}
							if(leaveList.size()>0){
								dao.save(leaveList, CommonUtil.getSQLDateFromUtilDate(mainCalendar.getTime()), 
										CommonUtil.getSQLDateFromUtilDate(start), 
										CommonUtil.getSQLDateFromUtilDate(end),
										getOfficeID(),
										(Long)userCombo.getValue());
								SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								Date date=dateField.getValue();
								dateField.setValue(null);
								dateField.setValue(date);
							}
							else{
								SNotification.show("No Data Selected",Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						final Date start;
						final Date end;
						start=mainCalendar.getTime();
						Calendar cal=Calendar.getInstance();
						cal.setTime(mainCalendar.getTime());
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
						end=cal.getTime();
						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										Iterator itr=table.getItemIds().iterator();
										List<UserLeaveMapModel> leaveList=new ArrayList<UserLeaveMapModel>();
										while (itr.hasNext()) {
											Item item = table.getItem(itr.next());
											SComboField leaveType=(SComboField)item.getItemProperty(TBC_LEAVE_TYPE).getValue();
											if(!leaveType.isReadOnly()){
												UserLeaveMapModel mdl=null;
												long id=(Long)item.getItemProperty(TBC_ID).getValue();
												if(id!=0)
													mdl=dao.getUserLeaveMapModel(id);
												if(mdl!=null)
													leaveList.add(mdl);
											}
										}
										if(leaveList.size()>0){
											dao.delete(leaveList, CommonUtil.getSQLDateFromUtilDate(mainCalendar.getTime()),
														CommonUtil.getSQLDateFromUtilDate(start), 
														CommonUtil.getSQLDateFromUtilDate(end),
														getOfficeID(),
														(Long)userCombo.getValue());
											SNotification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											Date date=dateField.getValue();
											dateField.setValue(null);
											dateField.setValue(date);
										}
										else{
											SNotification.show("No Data Selected",Type.WARNING_MESSAGE);
										}
									} 
									catch (Exception e) {
										SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadTable(Date from){
		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			if(userCombo.getValue()!=null){
				Calendar calendar=Calendar.getInstance();
				Calendar startCal=Calendar.getInstance();
				Calendar endCal=Calendar.getInstance();
				UserModel user=new UserManagementDao().getUser((Long)userCombo.getValue());
				table.setCaption(user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name());
				calendar.setTime(mainCalendar.getTime());
				List leaveTypeList=new ArrayList();
				for(int i=1;i<=mainCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
					allHeaders=new Object[]{ TBC_SN, TBC_ID, TBC_DATE_ID, TBC_DATE, TBC_IN, TBC_OUT, TBC_LEAVE_TYPE };
					leaveTypeList.clear();
					long id=0;
					boolean isOnLeave=false;
					startCal.set(Calendar.HOUR_OF_DAY, 0);
					startCal.set(Calendar.MINUTE, 0);
					startCal.set(Calendar.SECOND, 0);
					startCal.set(Calendar.MILLISECOND, 0);
					endCal.set(Calendar.HOUR_OF_DAY, 0);
					endCal.set(Calendar.MINUTE, 0);
					endCal.set(Calendar.SECOND, 0);
					endCal.set(Calendar.MILLISECOND, 0);
					Date date=getWorkingDate();
					date=calendar.getTime();
					
					UserLeaveMapModel leaveMap=new EmployeeAttendanceListDao().getUserLeaveMapModel(CommonUtil.getSQLDateFromUtilDate(calendar.getTime()), 
																									(Long)userCombo.getValue(), getOfficeID());
					

					AttendanceModel attendance=new AttendanceDao().getAttendanceModel(CommonUtil.getSQLDateFromUtilDate(calendar.getTime()),
																						getOfficeID(),
																						(Long)userCombo.getValue());
					
					if(attendance!=null){
						startCal.setTime(attendance.getFirst_in());
						endCal.setTime(attendance.getSecond_out());
					}
					LeaveModel leaveModel=new LeaveDao().getLeaveModelFromDate(CommonUtil.getSQLDateFromUtilDate(calendar.getTime()), 
																				(Long)userCombo.getValue());
					
					isOnLeave=new LeaveDao().isLeaveOnDate(CommonUtil.getSQLDateFromUtilDate(calendar.getTime()), 
															(Long)userCombo.getValue());
					/*if(!isOnLeave){
						leaveTypeList.add(0, new LeaveTypeModel(0, "None"));
					}*/
					leaveTypeList.addAll(new LeaveTypeDao().getLeaveTypeModelList(getOfficeID()));
					
					SComboField leaveTypeComboField=new SComboField(null, 175, leaveTypeList, "id", "name");
					leaveTypeComboField.setValue((long)0);
					
					if(!isOnLeave)
						leaveTypeComboField.setReadOnly(true);
					
					if(leaveModel!=null){
						leaveTypeComboField.setNewValue(leaveModel.getLeave_type().getId());
						leaveTypeComboField.setReadOnly(true);
					}
					
					if(leaveMap!=null){
						id=leaveMap.getId();
						leaveTypeComboField.setNewValue(leaveMap.getLeave_type().getId());
						if(leaveModel!=null)
							leaveTypeComboField.setReadOnly(true);
					}
					
					table.addItem(new Object[]{table.getItemIds().size()+1,
												id,
												date,
												CommonUtil.formatDateToDDMMYYYY(date),
												sdf.format(startCal.getTime()),
												sdf.format(endCal.getTime()),
												leaveTypeComboField}, table.getItemIds().size()+1);
					
					calendar.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
			table.setVisibleColumns(requiredHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public Boolean isValid() {
		boolean valid=true;
		
		if(dateField.getValue()==null){
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(dateField, null, false);
		
		if(table.getItemIds().size()<=0){
			setRequiredError(table, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(table, null, false);
		
		Map<Long, Double > map=new HashMap<Long, Double>();
		try {
			List leaveList=new LeaveTypeDao().getLeaveTypeList(getOfficeID());
			Iterator litr=leaveList.iterator();
			while (litr.hasNext()) {
				LeaveTypeModel leaveType = (LeaveTypeModel) litr.next();
				double leaveBalance=CommonUtil.roundNumber(new LeaveDao().getLeaveOfEmployee((Long) userCombo.getValue(), 
															leaveType.getId(),
															CommonUtil.getSQLDateFromUtilDate(mainCalendar.getTime()),getOfficeID()));
				map.put(leaveType.getId(), roundNumber(leaveBalance));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		table.setComponentError(null);
		Iterator itr=table.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			SComboField leaveType=(SComboField)item.getItemProperty(TBC_LEAVE_TYPE).getValue();
			
			if(!leaveType.isReadOnly()){
				if(leaveType.getValue()==null || leaveType.getValue().toString().equals("")){
					setRequiredError(leaveType, getPropertyName("invalid_selection"), true);
					leaveType.setStyleName("combo_field_error");
					valid=false;
				}
				else{
					leaveType.removeStyleName("combo_field_error");
					setRequiredError(leaveType, null, false);
					try {
						LeaveTypeModel typeMdl=new LeaveTypeDao().getLeaveTypeModel((Long)leaveType.getValue());
						if(typeMdl!=null){
							
							int count=new LeaveDao().getUserLeaveCount((Long)userCombo.getValue(), 
																		getOfficeID(), 
																		(Long)leaveType.getValue(), 
																		(long)mainCalendar.get(Calendar.YEAR));
							
							if(count<=0){
								SNotification.show("Leave Not Allocated fot the year", Type.ERROR_MESSAGE);
								leaveType.setStyleName("combo_field_error");
								setRequiredError(leaveType, getPropertyName("invalid_selection"), true);
								valid=false;
							} else {
								setRequiredError(leaveType, null, false);
								leaveType.setStyleName("combo_field_error");
							}
								
							AttendanceModel attendance=new AttendanceDao().getAttendanceModel(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_DATE_ID).getValue()), 
																								getOfficeID(), 
																								(Long) userCombo.getValue());
							double days=0;
							if(attendance!=null){
								if(attendance.getPresentLeave()==SConstants.attendanceStatus.LEAVE){
									days=1;
								}
								else if(attendance.getPresentLeave()==SConstants.attendanceStatus.HALF_DAY_LEAVE){
									days=0.5;
								}
								double leaveBalance=0;
								if(map.containsKey((Long)leaveType.getValue())){
									leaveBalance=map.get((Long)leaveType.getValue());
								}
								if(leaveBalance!=0){
									if(leaveBalance>=days)
										leaveBalance-=days;
									else{
										leaveType.setStyleName("combo_field_error");
										setRequiredError(leaveType, getPropertyName("invalid_selection"), true);
										valid=false;
									}
									if(map.containsKey((Long)leaveType.getValue())){
										map.remove((Long)leaveType.getValue());
										map.put((Long)leaveType.getValue(), roundNumber(leaveBalance));
									}
								}
								else{
									if(!typeMdl.isLop()){
										leaveType.setStyleName("combo_field_error");
										setRequiredError(leaveType, getPropertyName("invalid_selection"), true);
										valid=false;
									}
									else{
										setRequiredError(leaveType, null, false);
										leaveType.removeStyleName("combo_field_error");
									}
								}
								
							}
						}
						else{
							setRequiredError(leaveType, null, false);
							leaveType.setStyleName("combo_field_error");
						}

					} catch (Exception e) {
						e.printStackTrace();
						leaveType.setStyleName("combo_field_error");
						setRequiredError(leaveType, getPropertyName("invalid_selection"), true);
					}
				}
			}
		}
		
		return valid;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}
	
}
