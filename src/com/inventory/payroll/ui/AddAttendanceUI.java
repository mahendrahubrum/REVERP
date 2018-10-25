package com.inventory.payroll.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.AttendanceDao;
import com.inventory.payroll.dao.HolidayDao;
import com.inventory.payroll.dao.LeaveDao;
import com.inventory.payroll.dao.OverTimeDao;
import com.inventory.payroll.model.AttendanceModel;
import com.inventory.payroll.model.LeaveModel;
import com.inventory.payroll.model.OverTimeModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.STimeField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class AddAttendanceUI extends SparkLogic{

	SPanel mainPanel;
	SVerticalLayout mainLayout;
	SDateField dateField;

	SComboField employeeCombo;
	STimeField firstTimeField;
	STimeField secondTimeField;
	STextField totalField;
	STimeField overTimeField;
	SComboField overTimeCombo;
	
	SRadioButton presentRadio;
	SRadioButton sessionRadio;
	SCheckBox checkAllCheck;
	
	SimpleDateFormat sdf,df;
	Calendar totalCalendar;
	
	STable table;
	Calendar fromCalendar;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	
	SButton updateItemButton;
	
	Calendar mainCalendar;
	
	static String TBC_SELECT = "#";
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_UID = "UID";
	static String TBC_USER = "User";
	static String TBC_DATE_ID = "Date Id";
	static String TBC_DATE = "Date";
	static String TBC_FIRST_START_ID = "First In Id";
	static String TBC_FIRST_START = "First In";
	static String TBC_FIRST_FINISH_ID = "First Out Id";
	static String TBC_FIRST_FINISH = "First Out";
	static String TBC_SECOND_START_ID = "Second In Id";
	static String TBC_SECOND_START = "Second In";
	static String TBC_SECOND_FINISH_ID = "Second Out Id";
	static String TBC_SECOND_FINISH = "Second Out";
	static String TBC_TOTAL = "Total";
	static String TBC_OVERTIME_ID = "Overtime Id";
	static String TBC_OVERTIME = "Overtime";
	static String TBC_OVERTIME_IN_ID = "Over Time In Id";
	static String TBC_OVERTIME_OUT_ID = "Over Time Out Id";
	static String TBC_OVERTIME_HOURS = "Over Time Hour";
	static String TBC_WORKING_ID = "Working Day Id";
	static String TBC_WORKING = "Working Day";
	static String TBC_STATUS_ID = "Status Id";
	static String TBC_SESSION_ID = "Session Id";
	static String TBC_STATUS = "Status";
	static String TBC_LEAVE_ID = "Leave Id";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	AttendanceDao dao;
	
	
	@SuppressWarnings({"rawtypes", "unchecked" })
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		setSize(1250, 650);
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		sdf = new SimpleDateFormat("hh:mm:a");
		df = new SimpleDateFormat("HH:mm");
		totalCalendar=Calendar.getInstance();
		
		allHeaders=new Object[]{TBC_SELECT, TBC_SN, TBC_ID,TBC_UID, TBC_USER, TBC_DATE_ID, TBC_DATE, TBC_FIRST_START_ID, TBC_FIRST_START, 
				TBC_FIRST_FINISH_ID, TBC_FIRST_FINISH, TBC_SECOND_START_ID, TBC_SECOND_START, TBC_SECOND_FINISH_ID, TBC_SECOND_FINISH, 
				TBC_TOTAL, TBC_OVERTIME_ID, TBC_OVERTIME, TBC_OVERTIME_IN_ID, TBC_OVERTIME_OUT_ID, TBC_OVERTIME_HOURS, TBC_WORKING_ID, TBC_WORKING, 
				TBC_STATUS_ID, TBC_SESSION_ID, TBC_STATUS, TBC_LEAVE_ID};
		
		requiredHeaders=new Object[]{TBC_SELECT, TBC_SN, TBC_USER, TBC_DATE, TBC_FIRST_START, TBC_FIRST_FINISH, TBC_SECOND_START, 
				TBC_SECOND_FINISH, TBC_TOTAL, TBC_OVERTIME, TBC_OVERTIME_HOURS, TBC_WORKING, TBC_STATUS };
		
		mainCalendar=Calendar.getInstance();
		mainCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mainCalendar.set(Calendar.MINUTE, 0);
		mainCalendar.set(Calendar.SECOND, 0);
		mainCalendar.set(Calendar.MILLISECOND, 0);
		dao=new AttendanceDao();
		try {
			dateField=new SDateField("Date", 100);
			dateField.setImmediate(true);
			SHorizontalLayout topLayout=new SHorizontalLayout();
			topLayout.setSpacing(true);
			topLayout.addComponent(new SFormLayout(dateField));
			fromCalendar=getCalendar();
			
			employeeCombo=new SComboField("Employee", 150, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), isSuperAdmin()), "id", "first_name", true, "Select");
			
			firstTimeField=new STimeField("First");
			secondTimeField=new STimeField("Second");
			
			totalField=new STextField("Total",100);
			totalField.setReadOnly(true);

			overTimeField=new STimeField("Over Time", "Start", "End");
			
			List list=new ArrayList();
			list.add(0, new OverTimeModel(0, "None"));
			list.addAll(new OverTimeDao().getOverTimeModelList(getOfficeID()));
			overTimeCombo=new SComboField("Over Time", 125, list, "id", "description", true, "Select");
			
			List<KeyValue> presentList = new ArrayList<KeyValue>();
			presentList=Arrays.asList(new KeyValue(SConstants.attendanceStatus.PRESENT, "Present"),
					new KeyValue(SConstants.attendanceStatus.LEAVE, "Leave"), new KeyValue(SConstants.attendanceStatus.HALF_DAY_LEAVE, "Half Day Leave"));
			
			presentRadio=new SRadioButton(null, 250, presentList, "intKey", "value");
			sessionRadio=new SRadioButton(null, 200, Arrays.asList(new KeyValue(SConstants.attendanceStatus.FIRST_HALF, "First Half"),
																	new KeyValue(SConstants.attendanceStatus.SECOND_HALF, "Second Half")),"intKey", "value");
			presentRadio.setHorizontal(true);
			sessionRadio.setHorizontal(true);
			sessionRadio.setVisible(false);
			updateItemButton = new SButton(null, "Update Item");
			updateItemButton.setStyleName("updateItemBtnStyle");
			
			checkAllCheck=new SCheckBox("Select All", false);
			table=new STable();
			table.setWidth("1175");
			table.setHeight("250");
			table.setSelectable(true);
			
			table.addContainerProperty(TBC_SELECT, SCheckBox.class, null, TBC_SELECT, null, Align.CENTER);
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UID, Long.class, null, TBC_UID, null, Align.CENTER);
			table.addContainerProperty(TBC_USER, String.class, null, TBC_USER, null, Align.LEFT);
			table.addContainerProperty(TBC_DATE_ID, Date.class, null, TBC_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, String.class, null, TBC_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_FIRST_START_ID, Date.class, null, TBC_FIRST_START_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_FIRST_START, String.class, null, TBC_FIRST_START, null, Align.LEFT);
			table.addContainerProperty(TBC_FIRST_FINISH_ID, Date.class, null, TBC_FIRST_FINISH_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_FIRST_FINISH, String.class, null, TBC_FIRST_FINISH, null, Align.LEFT);
			table.addContainerProperty(TBC_SECOND_START_ID, Date.class, null, TBC_SECOND_START_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_SECOND_START, String.class, null, TBC_SECOND_START, null, Align.LEFT);
			table.addContainerProperty(TBC_SECOND_FINISH_ID, Date.class, null, TBC_SECOND_FINISH_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_SECOND_FINISH, String.class, null, TBC_SECOND_FINISH, null, Align.LEFT);
			table.addContainerProperty(TBC_TOTAL, String.class, null, TBC_TOTAL, null, Align.LEFT);
			table.addContainerProperty(TBC_OVERTIME_ID, Long.class, null, TBC_OVERTIME_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_OVERTIME, String.class, null, TBC_OVERTIME, null, Align.LEFT);
			table.addContainerProperty(TBC_OVERTIME_IN_ID, Date.class, null, TBC_OVERTIME_IN_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_OVERTIME_OUT_ID, Date.class, null, TBC_OVERTIME_OUT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_OVERTIME_HOURS, String.class, null, TBC_OVERTIME_HOURS, null, Align.LEFT);
			table.addContainerProperty(TBC_WORKING_ID, Boolean.class, null, TBC_WORKING_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_WORKING, String.class, null, TBC_WORKING, null, Align.LEFT);
			table.addContainerProperty(TBC_STATUS_ID, Integer.class, null, TBC_STATUS_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_SESSION_ID, Integer.class, null, TBC_SESSION_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_STATUS, String.class, null, TBC_STATUS, null, Align.LEFT);
			table.addContainerProperty(TBC_LEAVE_ID, Long.class, null, TBC_LEAVE_ID, null, Align.LEFT);
			
			table.setColumnExpandRatio(TBC_SELECT, .5f);
			table.setColumnExpandRatio(TBC_SN, .5f);
			table.setColumnExpandRatio(TBC_USER, 1f);
			table.setColumnExpandRatio(TBC_DATE, 0.90f);
			table.setColumnExpandRatio(TBC_FIRST_START, 0.90f);
			table.setColumnExpandRatio(TBC_FIRST_FINISH, 0.90f);
			table.setColumnExpandRatio(TBC_SECOND_START, 0.90f);
			table.setColumnExpandRatio(TBC_SECOND_FINISH, 0.90f);
			table.setColumnExpandRatio(TBC_TOTAL, 0.75f);
			table.setColumnExpandRatio(TBC_OVERTIME, 1f);
			table.setColumnExpandRatio(TBC_OVERTIME_HOURS, 1f);
			table.setColumnExpandRatio(TBC_WORKING, 1f);
			table.setColumnExpandRatio(TBC_STATUS, 1f);
			
			table.setVisibleColumns(requiredHeaders);
			
			SVerticalLayout mainItemLayout=new SVerticalLayout();
			mainItemLayout.setStyleName("po_border");
			mainItemLayout.setSpacing(true);
			mainItemLayout.setMargin(true);
			
			SGridLayout itemLayout1=new SGridLayout(8, 3);
			itemLayout1.setSpacing(true);
			
			SGridLayout itemLayout2=new SGridLayout(18, 3);
			itemLayout2.setSpacing(true);
			
			SGridLayout itemLayout3=new SGridLayout(18, 3);
			itemLayout3.setSpacing(true);

//			itemLayout1.addComponent(employeeCombo);
			
			itemLayout2.addComponent(presentRadio);
			itemLayout2.addComponent(sessionRadio);
			
			itemLayout2.setComponentAlignment(presentRadio, Alignment.BOTTOM_CENTER);
			itemLayout2.setComponentAlignment(sessionRadio, Alignment.BOTTOM_CENTER);
			
			itemLayout2.addComponent(firstTimeField);
			itemLayout2.addComponent(secondTimeField);
			itemLayout2.addComponent(totalField);
			
			itemLayout3.addComponent(overTimeCombo);
			itemLayout3.addComponent(overTimeField);
			itemLayout3.addComponent(updateItemButton);
			
			itemLayout2.setComponentAlignment(totalField, Alignment.BOTTOM_CENTER);
			itemLayout3.setComponentAlignment(updateItemButton, Alignment.BOTTOM_CENTER);
			itemLayout3.setComponentAlignment(overTimeCombo, Alignment.BOTTOM_CENTER);
			itemLayout3.setComponentAlignment(overTimeField, Alignment.MIDDLE_CENTER);
			
			mainItemLayout.addComponent(itemLayout1);
			mainItemLayout.addComponent(itemLayout2);
			mainItemLayout.addComponent(itemLayout3);
			
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
			
			mainLayout.addComponent(topLayout);
			mainLayout.addComponent(checkAllCheck);
			mainLayout.addComponent(table);
			mainLayout.addComponent(mainItemLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			calculateTotalHours();
			
			
			checkAllCheck.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(checkAllCheck.getValue()!=null){
						Iterator itr=table.getItemIds().iterator();
						while (itr.hasNext()) {
							Item item = table.getItem(itr.next());
							SCheckBox box=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
							box.setValue(checkAllCheck.getValue());
						}
					}
				}
			});
			
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(dateField.getValue()!=null){
							mainCalendar.setTime(dateField.getValue());
							mainCalendar.set(Calendar.HOUR_OF_DAY, 0);
							mainCalendar.set(Calendar.MINUTE, 0);
							mainCalendar.set(Calendar.SECOND, 0);
							mainCalendar.set(Calendar.MILLISECOND, 0);
							firstTimeField.startTime.setValue(mainCalendar.getTime());
							firstTimeField.endTime.setValue(mainCalendar.getTime());
							secondTimeField.startTime.setValue(mainCalendar.getTime());
							secondTimeField.endTime.setValue(mainCalendar.getTime());
							fromCalendar.setTime(dateField.getValue());
							loadTable(fromCalendar.getTime());
						}
						else
							table.removeAllItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dateField.setValue(getWorkingDate());

			
			
			firstTimeField.startTime.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					calculateTotalHours();
					isOverTimeValid();
				}
			});
			
			
			firstTimeField.endTime.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					secondTimeField.setNewValue(firstTimeField.endTime.getValue(), firstTimeField.endTime.getValue());
					isOverTimeValid();
					calculateTotalHours();
				}
			});
			
			
			secondTimeField.startTime.addValueChangeListener(new ValueChangeListener() {
	
				@Override
				public void valueChange(ValueChangeEvent event) {
					isOverTimeValid();
					if(isSecondTimeValid())
						calculateTotalHours();
				}
			});
			
			
			secondTimeField.endTime.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					isOverTimeValid();
					if(isSecondTimeValid())
						calculateTotalHours();
				}
			});
			
			
			overTimeCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(overTimeCombo.getValue()==null || overTimeCombo.getValue().toString().equals("0")){
							overTimeField.setReadOnly(true);
						}
						else{
							overTimeField.setReadOnly(false);
						}
						overTimeField.resetAll();
						isOverTimeValid();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			overTimeCombo.setValue((long)0);
			
			
			presentRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(presentRadio.getValue()!=null){
							switch ((Integer)presentRadio.getValue()) {
								case SConstants.attendanceStatus.PRESENT:
																		sessionRadio.setValue(SConstants.attendanceStatus.FIRST_HALF);
																		sessionRadio.setVisible(false);
																		firstTimeField.setReadOnly(false);
																		secondTimeField.setReadOnly(false);
																		overTimeCombo.setReadOnly(false);
																		overTimeCombo.setNewValue(null);
																		overTimeCombo.setNewValue((long)0);
																		firstTimeField.resetAll();
																		secondTimeField.resetAll();
																		overTimeField.resetAll();
																		break;	
																		
								case SConstants.attendanceStatus.LEAVE:
																		sessionRadio.setValue(SConstants.attendanceStatus.FIRST_HALF);
																		sessionRadio.setVisible(false);
																		firstTimeField.setReadOnly(true);
																		secondTimeField.setReadOnly(true);
//																		overTimeField.setReadOnly(true);
																		overTimeCombo.setReadOnly(true);
																		overTimeCombo.setNewValue(null);
																		overTimeCombo.setNewValue((long)0);
																		firstTimeField.resetAll();
																		secondTimeField.resetAll();
																		overTimeField.resetAll();
																		break;
									
								case SConstants.attendanceStatus.HALF_DAY_LEAVE:
																		sessionRadio.setValue(null);
																		sessionRadio.setValue(SConstants.attendanceStatus.FIRST_HALF);
																		sessionRadio.setVisible(true);
																		break;

								default:
																		break;
							}
						}
						else{
							sessionRadio.setValue(SConstants.attendanceStatus.FIRST_HALF);
							sessionRadio.setVisible(false);
							firstTimeField.setReadOnly(false);
							secondTimeField.setReadOnly(false);
//							overTimeField.setReadOnly(false);
							overTimeCombo.setReadOnly(false);
							overTimeCombo.setNewValue(null);
							overTimeCombo.setNewValue((long)0);
							firstTimeField.resetAll();
							secondTimeField.resetAll();
							overTimeField.resetAll();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			presentRadio.setValue(SConstants.attendanceStatus.PRESENT);
			
			
			sessionRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(sessionRadio.getValue()!=null){
							switch ((Integer)sessionRadio.getValue()) {
								case SConstants.attendanceStatus.FIRST_HALF:
																		firstTimeField.setReadOnly(true);
																		secondTimeField.setReadOnly(false);
																		firstTimeField.resetAll();
																		secondTimeField.resetAll();
																		overTimeField.resetAll();
																		break;	
																		
								case SConstants.attendanceStatus.SECOND_HALF:
																		firstTimeField.setReadOnly(false);
																		secondTimeField.setReadOnly(true);
																		firstTimeField.resetAll();
																		secondTimeField.resetAll();
																		overTimeField.resetAll();
																		break;
	
								default:
																		break;
							}
						}
						else{
							firstTimeField.setReadOnly(false);
							secondTimeField.setReadOnly(false);
//							overTimeField.setReadOnly(false);
							overTimeCombo.setReadOnly(false);
							overTimeCombo.setNewValue(null);
							overTimeCombo.setNewValue((long)0);
							firstTimeField.resetAll();
							secondTimeField.resetAll();
							overTimeField.resetAll();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			sessionRadio.setValue(SConstants.attendanceStatus.FIRST_HALF);
			
			
			overTimeField.startTime.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					overTimeField.isValid();
					isOverTimeValid();
				}
			});
			
			
			overTimeField.endTime.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					overTimeField.isValid();
					isOverTimeValid();
				}
			});
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						resetItems();
						if(table.getValue()!=null){
							presentRadio.setReadOnly(false);
							sessionRadio.setReadOnly(false);
							Item item=table.getItem(table.getValue());
							employeeCombo.setNewValue((Long)item.getItemProperty(TBC_UID).getValue());
							employeeCombo.setReadOnly(true);
							Calendar cal=getCalendar();
							cal.setTime(fromCalendar.getTime());
							boolean isLeave=false;
							boolean isHalfLeave=false;
							
							LeaveModel leaveModel=new LeaveDao().getLeaveModelFromDate(CommonUtil.getSQLDateFromUtilDate(fromCalendar.getTime()),(Long)item.getItemProperty(TBC_UID).getValue());
							if(leaveModel!=null){
								if(leaveModel.getStatus()==SConstants.leaveStatus.LEAVE_APPLIED ||
									leaveModel.getStatus()==SConstants.leaveStatus.LEAVE_APPROVED || 
									leaveModel.getStatus()==SConstants.leaveStatus.LEAVE_FORWARDED){
									if(leaveModel.getNo_of_days()<1 && leaveModel.getNo_of_days()>0){
										isHalfLeave=true;
									}
									else{
										isLeave=true;
									}
								}
							}
							if((Integer)item.getItemProperty(TBC_STATUS_ID).getValue()!=0)
								presentRadio.setValue((Integer)item.getItemProperty(TBC_STATUS_ID).getValue());
							else
								presentRadio.setValue(SConstants.attendanceStatus.PRESENT);
							sessionRadio.setValue((Integer)item.getItemProperty(TBC_SESSION_ID).getValue());
							firstTimeField.setNewValue((Date)item.getItemProperty(TBC_FIRST_START_ID).getValue(), (Date)item.getItemProperty(TBC_FIRST_FINISH_ID).getValue());
							secondTimeField.setNewValue((Date)item.getItemProperty(TBC_SECOND_START_ID).getValue(), (Date)item.getItemProperty(TBC_SECOND_FINISH_ID).getValue());
							overTimeCombo.setValue((Long)item.getItemProperty(TBC_OVERTIME_ID).getValue());
							overTimeField.setNewValue((Date)item.getItemProperty(TBC_OVERTIME_IN_ID).getValue(), (Date)item.getItemProperty(TBC_OVERTIME_OUT_ID).getValue());
							if(isLeave)
								presentRadio.setReadOnly(true);
							else if(isHalfLeave){
								sessionRadio.setReadOnly(true);
								presentRadio.setReadOnly(true);
							}
								
						}
						else
							resetItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			updateItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							if(table.getValue()!=null){
								Item item=table.getItem(table.getValue());
		
								Calendar calendar=Calendar.getInstance();
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								calendar.set(Calendar.MILLISECOND, 0);
								calendar.add(Calendar.HOUR_OF_DAY, calculateHour(firstTimeField.endTime.getValue(), firstTimeField.startTime.getValue()));
								calendar.add(Calendar.HOUR_OF_DAY, calculateHour(secondTimeField.endTime.getValue(), secondTimeField.startTime.getValue()));
								
								item.getItemProperty(TBC_DATE_ID).setValue(dateField.getValue());
								item.getItemProperty(TBC_DATE).setValue(CommonUtil.formatDateToDDMMYYYY(dateField.getValue()));
								
								item.getItemProperty(TBC_FIRST_START_ID).setValue(firstTimeField.startTime.getValue());
								item.getItemProperty(TBC_FIRST_START).setValue(sdf.format(firstTimeField.startTime.getValue()));
								
								item.getItemProperty(TBC_FIRST_FINISH_ID).setValue(firstTimeField.endTime.getValue());
								item.getItemProperty(TBC_FIRST_FINISH).setValue(sdf.format(firstTimeField.endTime.getValue()));
								
								item.getItemProperty(TBC_SECOND_START_ID).setValue(secondTimeField.startTime.getValue());
								item.getItemProperty(TBC_SECOND_START).setValue(sdf.format(secondTimeField.startTime.getValue()));
								
								item.getItemProperty(TBC_SECOND_FINISH_ID).setValue(secondTimeField.endTime.getValue());
								item.getItemProperty(TBC_SECOND_FINISH).setValue(sdf.format(secondTimeField.endTime.getValue()));
								
								item.getItemProperty(TBC_TOTAL).setValue(df.format(calendar.getTime()));
								item.getItemProperty(TBC_OVERTIME_ID).setValue((Long)overTimeCombo.getValue());
								item.getItemProperty(TBC_OVERTIME).setValue(overTimeCombo.getItemCaption((Long)overTimeCombo.getValue()));
								
								item.getItemProperty(TBC_OVERTIME_IN_ID).setValue(overTimeField.startTime.getValue());
								item.getItemProperty(TBC_OVERTIME_OUT_ID).setValue(overTimeField.endTime.getValue());
								
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								calendar.set(Calendar.MILLISECOND, 0);
								calendar.add(Calendar.HOUR_OF_DAY, calculateHour(overTimeField.endTime.getValue(), overTimeField.startTime.getValue()));
								
								item.getItemProperty(TBC_OVERTIME_HOURS).setValue(df.format(calendar.getTime()));
								
								item.getItemProperty(TBC_STATUS_ID).setValue((Integer)presentRadio.getValue());
								item.getItemProperty(TBC_SESSION_ID).setValue((Integer)sessionRadio.getValue());
								item.getItemProperty(TBC_STATUS).setValue(getStatus((Integer)presentRadio.getValue(), (Integer)sessionRadio.getValue()));
								
								table.setValue(null);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							Iterator itr=table.getItemIds().iterator();
							
							List<AttendanceModel> attendanceList=new ArrayList<AttendanceModel>();
							AttendanceModel mdl=null;
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
								SCheckBox box=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
								if(box.getValue()){
									mdl=dao.getAttendanceModel(id);
									if(mdl==null)
										mdl=new AttendanceModel();
									mdl.setUserId((Long)item.getItemProperty(TBC_UID).getValue());
									mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									mdl.setFirst_in(CommonUtil.getTimestampFromUtilDate((Date)item.getItemProperty(TBC_FIRST_START_ID).getValue()));
									mdl.setFirst_out(CommonUtil.getTimestampFromUtilDate((Date)item.getItemProperty(TBC_FIRST_FINISH_ID).getValue()));
									mdl.setSecond_in(CommonUtil.getTimestampFromUtilDate((Date)item.getItemProperty(TBC_SECOND_START_ID).getValue()));
									mdl.setSecond_out(CommonUtil.getTimestampFromUtilDate((Date)item.getItemProperty(TBC_SECOND_FINISH_ID).getValue()));
									if((Integer)item.getItemProperty(TBC_STATUS_ID).getValue()!=0)
										mdl.setPresentLeave((Integer)item.getItemProperty(TBC_STATUS_ID).getValue());
									else
										mdl.setPresentLeave(SConstants.attendanceStatus.LEAVE);
									mdl.setSessionLeave((Integer)item.getItemProperty(TBC_SESSION_ID).getValue());
									mdl.setWorkingDay((Boolean)item.getItemProperty(TBC_WORKING_ID).getValue());
									mdl.setOvertime((Long)item.getItemProperty(TBC_OVERTIME_ID).getValue());
									mdl.setOver_time_in(CommonUtil.getTimestampFromUtilDate((Date)item.getItemProperty(TBC_OVERTIME_IN_ID).getValue()));
									mdl.setOver_time_out(CommonUtil.getTimestampFromUtilDate((Date)item.getItemProperty(TBC_OVERTIME_OUT_ID).getValue()));
									mdl.setOfficeId(getOfficeID());
									mdl.setLeaveId((Long)item.getItemProperty(TBC_LEAVE_ID).getValue());
									attendanceList.add(mdl);
								}
								else
									continue;
							}
							if(attendanceList.size()>0){
								dao.save(attendanceList);
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
						if(isValid()){
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											Iterator itr=table.getItemIds().iterator();
											List<AttendanceModel> attendanceList=new ArrayList<AttendanceModel>();
											AttendanceModel mdl=null;
											while (itr.hasNext()) {
												Item item = table.getItem(itr.next());
												long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
												SCheckBox box=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
												if(box.getValue()){
													if(id!=0){
														mdl=dao.getAttendanceModel(id);
														if(!mdl.isBlocked())
															attendanceList.add(mdl);
													}
												}
												else
													continue;
											}
											if(attendanceList.size()>0){
												dao.delete(attendanceList);
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
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (updateItemButton.isVisible())
						updateItemButton.click();
				}
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void loadTable(Date from){
		
		try {
			checkAllCheck.setValue(false);
			List userList=new ArrayList();
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			userList=new UserManagementDao().getUsersFromOffice(getOfficeID());
			if(userList.size()>0){
				for(int i=1;i<=userList.size();i++){
					UserModel user=(UserModel)userList.get(i-1);
					long leaveId=0;
					long id=0,overTime=0;
					boolean isHoliday=false;
					int present=0, session=1;
					String workingString="", overTimeString="None";
					Date date,firstStart,firstFinish,secondStart,secondFinish,overHourIn,overHourOut;
					
					Calendar cal=getCalendar();
					date=from;
					cal.setTime(date);
					
					firstStart=mainCalendar.getTime();
					firstFinish=mainCalendar.getTime();
					secondStart=mainCalendar.getTime();
					secondFinish=mainCalendar.getTime();
					overHourIn=mainCalendar.getTime();
					overHourOut=mainCalendar.getTime();
					
					List holidayList=new ArrayList();
					List weekOffList=new ArrayList();
					S_OfficeModel office=new OfficeDao().getOffice(getOfficeID());
					holidayList=new HolidayDao().getHolidayModelList(getOfficeID(), CommonUtil.getSQLDateFromUtilDate(cal.getTime()));
					String[] holidays=office.getHolidays().split(",");
					weekOffList=Arrays.asList(holidays);
					if(holidayList.size()>0 || weekOffList.contains(cal.get(Calendar.DAY_OF_WEEK)+"") ){
						present=SConstants.attendanceStatus.LEAVE;
						isHoliday=true;
					}
					LeaveModel leaveModel=new LeaveDao().getLeaveModelFromDate(CommonUtil.getSQLDateFromUtilDate(fromCalendar.getTime()),user.getId());
					if(leaveModel!=null){
						leaveId=leaveModel.getId();
						if(leaveModel.getStatus()==SConstants.leaveStatus.LEAVE_APPLIED || 
							leaveModel.getStatus()==SConstants.leaveStatus.LEAVE_APPROVED || 
							leaveModel.getStatus()==SConstants.leaveStatus.LEAVE_FORWARDED) {
							if(leaveModel.getNo_of_days()<1){
								present=SConstants.attendanceStatus.HALF_DAY_LEAVE;
								if(leaveModel.getFull_half()==SConstants.leaveStatus.SECOND_HALF){
									session=SConstants.attendanceStatus.SECOND_HALF;
								}
								else
									session=SConstants.attendanceStatus.FIRST_HALF;
							}
							else{
								present=SConstants.attendanceStatus.LEAVE;
							}
						}
					}
					AttendanceModel attendance=dao.getAttendanceModel(CommonUtil.getSQLDateFromUtilDate(date), getOfficeID(), user.getId());
					if(attendance!=null){
						id=attendance.getId();
						date=attendance.getDate();
						firstStart=attendance.getFirst_in();
						firstFinish=attendance.getFirst_out();
						secondStart=attendance.getSecond_in();
						secondFinish=attendance.getSecond_out();
						present=attendance.getPresentLeave();
						session=attendance.getSessionLeave();
						overTime=attendance.getOvertime();
						isHoliday=attendance.isWorkingDay();
						if(overTime!=0)
							overTimeString=new OverTimeDao().getOverTimeModel(overTime).getDescription();
						overHourIn=attendance.getOver_time_in();
						overHourOut=attendance.getOver_time_out();
						leaveId=attendance.getLeaveId();
					}
					
					Calendar calendar=Calendar.getInstance();
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					calendar.add(Calendar.HOUR_OF_DAY, calculateHour(firstFinish, firstStart));
					calendar.add(Calendar.HOUR_OF_DAY, calculateHour(secondFinish, secondStart));
					
					Calendar ocalendar=Calendar.getInstance();
					ocalendar.set(Calendar.HOUR_OF_DAY, 0);
					ocalendar.set(Calendar.MINUTE, 0);
					ocalendar.set(Calendar.SECOND, 0);
					ocalendar.set(Calendar.MILLISECOND, 0);
					ocalendar.add(Calendar.HOUR_OF_DAY, calculateHour(overHourOut, overHourIn));
					
					if(isHoliday)
						workingString="Holiday";
					else
						workingString="Working Day";
					
					table.addItem(new Object[]{new SCheckBox(),
												table.getItemIds().size()+1,
												id,
												user.getId(),
												user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name(),
												date,
												CommonUtil.formatDateToDDMMYYYY(date),
												firstStart,
												sdf.format(firstStart),
												firstFinish,
												sdf.format(firstFinish),
												secondStart,
												sdf.format(secondStart),
												secondFinish,
												sdf.format(secondFinish),
												df.format(calendar.getTime()),
												overTime,
												overTimeString,
												overHourIn,
												overHourOut,
												df.format(ocalendar.getTime()),
												isHoliday,
												workingString,
												present, 
												session,
												getStatus(present, session),
												leaveId},table.getItemIds().size()+1);
					}
			}
			table.setVisibleColumns(requiredHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void resetItems(){
		presentRadio.setReadOnly(false);
		sessionRadio.setReadOnly(false);
		checkAllCheck.setValue(false);
		employeeCombo.setReadOnly(false);
		employeeCombo.setValue(null);
		presentRadio.setValue(null);
		presentRadio.setValue(SConstants.attendanceStatus.PRESENT);
	}
	
	
	public boolean isAddingValid(){
		
		boolean valid=true;
		
		if(!isSecondTimeValid())
			valid=false;
		if(!firstTimeField.isValid())
			valid=false;
		if(!secondTimeField.isValid())
			valid=false;
		if(!overTimeField.isValid())
			valid=false;
		if(!isOverTimeValid())
			valid=false;
		
		if(overTimeCombo.getValue()==null || overTimeCombo.getValue().toString().equals("")) {
			setRequiredError(overTimeCombo, "Invalid Selection", true);
			valid=false;
		}
		else
			setRequiredError(overTimeCombo, null, false);
		
		return valid;
	}
	
	
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
		
		return valid;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}
	
	
	public boolean isOverTimeValid(){
		boolean valid=true;
		
		if(overTimeCombo.getValue()!=null && !overTimeCombo.getValue().toString().equals("0")) {
			
			if((!firstTimeField.isReadOnly()) && (!secondTimeField.isReadOnly())){
				
				if(overTimeField.startTime.getValue().getTime()<firstTimeField.startTime.getValue().getTime()){
					setRequiredError(overTimeField.startTime, "Invalid Selection", true);
					valid=false;
				}
				else
					setRequiredError(overTimeField.startTime, null, false);
				
				if(secondTimeField.endTime.getValue().getTime()<overTimeField.endTime.getValue().getTime()){
					setRequiredError(overTimeField.endTime, "Invalid Selection", true);
					valid=false;
				}
				else
					setRequiredError(overTimeField.endTime, null, false);
				
			}
			else if(!firstTimeField.isReadOnly()){
				
				if(overTimeField.startTime.getValue().getTime()<firstTimeField.startTime.getValue().getTime()){
					setRequiredError(overTimeField.startTime, "Invalid Selection", true);
					valid=false;
				}
				else
					setRequiredError(overTimeField.startTime, null, false);
				
				if(firstTimeField.endTime.getValue().getTime()<overTimeField.endTime.getValue().getTime()){
					setRequiredError(overTimeField.endTime, "Invalid Selection", true);
					valid=false;
				}
				else
					setRequiredError(overTimeField.endTime, null, false);
				
			}
			else{
				
				if(overTimeField.startTime.getValue().getTime()<secondTimeField.startTime.getValue().getTime()){
					setRequiredError(overTimeField.startTime, "Invalid Selection", true);
					valid=false;
				}
				else
					setRequiredError(overTimeField.startTime, null, false);
				
				if(secondTimeField.endTime.getValue().getTime()<overTimeField.endTime.getValue().getTime()){
					setRequiredError(overTimeField.endTime, "Invalid Selection", true);
					valid=false;
				}
				else
					setRequiredError(overTimeField.endTime, null, false);
				
			}
		}
		return valid;
	}
	
	
	public boolean isSecondTimeValid(){
		boolean valid=true;
		if(secondTimeField.startTime.getValue().getTime()<firstTimeField.endTime.getValue().getTime()){
			setRequiredError(secondTimeField.startTime, "Invalid Selection", true);
			valid=false;
		}
		else
			setRequiredError(secondTimeField.startTime, null, false);
		
		if(secondTimeField.endTime.getValue().getTime()<firstTimeField.endTime.getValue().getTime()){
			setRequiredError(secondTimeField.endTime, "Invalid Selection", true);
			valid=false;
		}
		else
			setRequiredError(secondTimeField.endTime, null, false);
		return valid;
	}
	
	
	public void calculateTotalHours(){
		totalCalendar.set(Calendar.HOUR_OF_DAY, 0);
		totalCalendar.set(Calendar.MINUTE, 0);
		totalCalendar.set(Calendar.SECOND, 0);
		totalCalendar.set(Calendar.MILLISECOND, 0);
		if(firstTimeField.isValid()){
			totalCalendar.add(Calendar.MINUTE, firstTimeField.getMinutes());
		}
		if(secondTimeField.isValid()){
			totalCalendar.add(Calendar.MINUTE, secondTimeField.getMinutes());
		}
		totalField.setNewValue(df.format(totalCalendar.getTime()));
	}
	
	
	public int calculateHour(Date end , Date start){
		int hour=0;
		try {
			hour = (int) ((end.getTime()-start.getTime())/(1000*60*60));
		} catch (Exception e) {
			hour=0;
		}
		return hour;
	}
	
	
	public String getStatus(int present, int session){
		String status="";
		switch (present) {
			case SConstants.attendanceStatus.PRESENT:
													status="Present";
													break;
			case SConstants.attendanceStatus.LEAVE:
													status="Leave";
													break;
			case SConstants.attendanceStatus.HALF_DAY_LEAVE:
															if(session==SConstants.attendanceStatus.FIRST_HALF)
																status="First Half Leave";
															else
																status="Second Half Leave";
															break;

		default:									status="Not Available";
													break;
		}
		return status;
	}

}
