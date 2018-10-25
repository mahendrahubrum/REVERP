package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.inventory.payroll.dao.EmployeeAttendanceListDao;
import com.inventory.payroll.dao.HolidayDao;
import com.inventory.payroll.dao.LeaveDao;
import com.inventory.payroll.dao.LeaveTypeDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class EmployeeAttendanceListUI extends SparkLogic{

	SPanel mainPanel;
	SVerticalLayout mainLayout;
	
	InlineDateField dateField;
	SComboField leaveTypeCombo;
	STable table;
	STable subTable;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	
	Calendar mainCalendar;
	
	static String TBC_SN = "SN";
	static String TBC_UID = "UID";
	static String TBC_USER = "User";
	static String TBC_TOTAL = "Total Days";
	static String TBC_HOLIDAYS = "Holidays";
	static String TBC_OFFICE_HOLIDAYS = "Office Holidays";
	static String TBC_LEAVE = "Leaves";
	static String TBC_WORKING = "Working Days";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	EmployeeAttendanceListDao dao;
	
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		dao=new EmployeeAttendanceListDao();
		setSize(800, 600);
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		
		allHeaders=new Object[]{ TBC_SN, TBC_UID, TBC_USER, TBC_TOTAL, TBC_HOLIDAYS, TBC_OFFICE_HOLIDAYS, TBC_LEAVE, TBC_WORKING };
		
		requiredHeaders=new Object[]{ TBC_SN, TBC_USER, TBC_TOTAL, TBC_HOLIDAYS, TBC_OFFICE_HOLIDAYS, TBC_LEAVE, TBC_WORKING };
		
		mainCalendar=Calendar.getInstance();
		try {
			dateField=new InlineDateField("Month");
			dateField.setImmediate(true);
			dateField.setResolution(Resolution.MONTH);
			SHorizontalLayout topLayout=new SHorizontalLayout();
			topLayout.setSpacing(true);
			topLayout.addComponent(new SFormLayout(dateField));
			
			leaveTypeCombo = new SComboField("Leave Type", 200,new LeaveTypeDao().getLeaveTypeModelList(getOfficeID()),"id","name", true, getPropertyName("select"));
			
			table=new STable();
			table.setWidth("750");
			table.setHeight("400");
			table.setSelectable(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.CENTER);
			table.addContainerProperty(TBC_UID, Long.class, null, TBC_UID, null, Align.CENTER);
			table.addContainerProperty(TBC_USER, String.class, null, TBC_USER, null, Align.LEFT);
			table.addContainerProperty(TBC_TOTAL, Integer.class, null, TBC_TOTAL, null, Align.LEFT);
			table.addContainerProperty(TBC_HOLIDAYS, Integer.class, null, TBC_HOLIDAYS, null, Align.LEFT);
			table.addContainerProperty(TBC_OFFICE_HOLIDAYS, Integer.class, null, TBC_OFFICE_HOLIDAYS, null, Align.LEFT);
			table.addContainerProperty(TBC_LEAVE, Double.class, null, TBC_LEAVE, null, Align.LEFT);
			table.addContainerProperty(TBC_WORKING, Integer.class, null, TBC_WORKING, null, Align.LEFT);
			
			table.setColumnExpandRatio(TBC_SN, .5f);
			table.setColumnExpandRatio(TBC_USER, 1f);
			table.setColumnExpandRatio(TBC_TOTAL, 1f);
			table.setColumnExpandRatio(TBC_HOLIDAYS, 1f);
			table.setColumnExpandRatio(TBC_OFFICE_HOLIDAYS, 1f);
			table.setColumnExpandRatio(TBC_LEAVE, 1f);
			table.setColumnExpandRatio(TBC_WORKING, 1f);
			
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
//			SHorizontalLayout buttonLayout=new SHorizontalLayout();
//			buttonLayout.setSpacing(true);
//			buttonLayout.addComponent(saveButton);
//			buttonLayout.addComponent(updateButton);
//			buttonLayout.addComponent(deleteButton);
			
//			updateButton.setVisible(false);
//			deleteButton.setVisible(false);
			
			mainLayout.addComponent(topLayout);
			mainLayout.addComponent(table);
//			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
//			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			
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
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							EmployeeMarkLeaveUI markLeave = new EmployeeMarkLeaveUI();
							markLeave.setCaption("Mark Leave");
							markLeave.userCombo.setNewValue((Long)item.getItemProperty(TBC_UID).getValue());
							markLeave.dateField.setValue(mainCalendar.getTime());
							markLeave.center();
							getUI().getCurrent().addWindow(markLeave);
//							markLeave.addCloseListener(closeListener);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
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
			List userList=new ArrayList();
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			
			userList=new UserManagementDao().getUsersFromOffice(getOfficeID());
			if(userList.size()>0){
				for(int i=1;i<=userList.size();i++){
					UserModel user=(UserModel)userList.get(i-1);
					allHeaders=new Object[]{ TBC_SN, TBC_UID, TBC_USER, TBC_TOTAL, TBC_HOLIDAYS, TBC_OFFICE_HOLIDAYS, TBC_LEAVE, TBC_WORKING };
					table.addItem(new Object[]{table.getItemIds().size()+1,
												user.getId(),
												user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name(),
												mainCalendar.getActualMaximum(Calendar.DAY_OF_MONTH),
												getHolidaysInMonth(mainCalendar.getTime()),
												getOfficeHolidays(mainCalendar.getTime()),
												getTotalLeavesOfMonth(mainCalendar.getTime(), user.getId()),
												getTotalWorkingDaysOfMonth(mainCalendar.getTime(), user.getId())}, table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(requiredHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean isAddingValid(){
		
		boolean valid=true;
		
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

	
	@SuppressWarnings("rawtypes")
	public int getTotalWorkingDaysOfMonth(Date date, long user){
		int days=0;
		try {
			List weekOffList=new ArrayList();
			S_OfficeModel office=new OfficeDao().getOffice(getOfficeID());
			String[] holidays=office.getHolidays().split(",");
			weekOffList=Arrays.asList(holidays);
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			days=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			for(int i=1;i<=mainCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
				boolean isLeave=new LeaveDao().isLeaveOnDate(CommonUtil.getSQLDateFromUtilDate(cal.getTime()),user);
				List holidayList=new ArrayList();
				holidayList=new HolidayDao().getHolidayModelList(getOfficeID(), CommonUtil.getSQLDateFromUtilDate(cal.getTime()));
				if(isLeave){
					days--;
				}
				else if(holidayList.size()>0 || weekOffList.contains(cal.get(Calendar.DAY_OF_WEEK)+"")){
					days--;
				}
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			days=0;
		}
		return days;
	}
	
	
	public double getTotalLeavesOfMonth(Date date, long user){
		double leave=0;
		try {
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			Date start, end;
			cal.set(Calendar.DAY_OF_MONTH, 1);
			start=cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			end=cal.getTime();
			leave=dao.getLeavesInMonth(CommonUtil.getSQLDateFromUtilDate(start), CommonUtil.getSQLDateFromUtilDate(end), getOfficeID(), user);
		} catch (Exception e) {
			e.printStackTrace();
			leave=0;
		}
		return leave;
	}
	
	
	@SuppressWarnings("rawtypes")
	public int getOfficeHolidays(Date date){
		int holiday=0;
		try {
			List weekOffList=new ArrayList();
			S_OfficeModel office=new OfficeDao().getOffice(getOfficeID());
			String[] holidays=office.getHolidays().split(",");
			weekOffList=Arrays.asList(holidays);
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			for(int i=1;i<=mainCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
				if( weekOffList.contains(cal.get(Calendar.DAY_OF_WEEK)+"") ){
					holiday++;
				}
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			holiday=0;
		}
		return holiday;
	}
	
	
	public int getHolidaysInMonth(Date date){
		int holidays=0;
		try {
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			Date start, end;
			cal.set(Calendar.DAY_OF_MONTH, 1);
			start=cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			end=cal.getTime();
			holidays=dao.getHolidaysInMonth(CommonUtil.getSQLDateFromUtilDate(start), CommonUtil.getSQLDateFromUtilDate(end), getOfficeID());
		} catch (Exception e) {
			e.printStackTrace();
			holidays=0;
		}
		return holidays;
	}
	
}
