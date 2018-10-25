package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.HolidayDao;
import com.inventory.payroll.dao.LeaveDao;
import com.inventory.payroll.dao.LeaveTypeDao;
import com.inventory.payroll.model.LeaveHistoryModel;
import com.inventory.payroll.model.LeaveModel;
import com.inventory.payroll.model.LeaveTypeModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * 
 * @author sangeeth
 * Automobile
 * 22-Jul-2015
 */

@SuppressWarnings("serial")
public class LeaveApplyUI extends SContainerPanel {
	
	private static final String TBL_ID="ID";
	private static final String TBL_DATE="Applied Date";
	private static final String TBL_LEAVE_TYPE_ID="Leave Type Id";
	private static final String TBL_LEAVE_TYPE="Leave Type";
	private static final String TBL_FROM_DATE="From Date";
	private static final String TBL_TO_DATE="To Date";
	private static final String TBL_NO_OF_DAYS="Days";
	private static final String TBL_STATUS_ID="Status Id";
	private static final String TBL_STATUS="Status";
	private static final String TBL_REASON="Reason";
	
	SPanel mainPanel;
	private SComboField userCombo;
	private SComboField leaveTypeCombo;
	private STextField balanceField;
	private SDateField fromDate;
	private SDateField toDate;
	private STextField daysField;
	private SRadioButton fullRadio;
	private SRadioButton sessionRadio;
	private STextArea reasonArea;
	private SComboField applyToCombo;
	
	SHorizontalLayout mainLayout;
	SFormLayout formLayout;
	SFormLayout tableLayout;
	
	private STable table;
	
	private SButton applyButton;
	private SButton cancelButton;
	
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	
	private SVerticalLayout popupLay;
	private SFormLayout popupContlay;
	
	private SLabel leaveDetailsLabel;
	private SLabel leaveStatusLabel;
	private SLabel commentsLabel;
	
	UserManagementDao userDao;
	LeaveDao dao;
	
	Calendar startCalendar;
	Calendar endCalendar;
	
	String yearLeaveString="";

	public SPanel getGUI() {
		
		userDao=new UserManagementDao();
		dao=new LeaveDao();
		
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		
		mainLayout= new SHorizontalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		
		formLayout = new SFormLayout();
		formLayout.setSpacing(true);
		
		tableLayout = new SFormLayout();
		tableLayout.setSpacing(true);
		
		startCalendar=getCalendar();
		endCalendar=getCalendar();

		SHorizontalLayout buttonLayount = new SHorizontalLayout();
		buttonLayount.setSpacing(true);

		
		popupLay=new SVerticalLayout();				
		popupContlay=new SFormLayout();
		popupLay.setSpacing(true);
		popupLay.setMargin(true);
		popupContlay.setSpacing(true);
		popupContlay.setMargin(true);
		
		allHeaders=new Object[]{TBL_ID, TBL_DATE, TBL_LEAVE_TYPE_ID, TBL_LEAVE_TYPE, TBL_FROM_DATE, TBL_TO_DATE, TBL_NO_OF_DAYS, 
								TBL_STATUS_ID, TBL_STATUS, TBL_REASON};
		visibleHeaders=new Object[]{TBL_DATE, TBL_LEAVE_TYPE, TBL_FROM_DATE, TBL_TO_DATE, TBL_NO_OF_DAYS, TBL_STATUS, TBL_REASON};

		try {
			userCombo = new SComboField(getPropertyName("user"),200,userDao.getUsersWithFullNameAndCodeFromOffice(getOfficeID(), false),"id", "first_name", true, getPropertyName("select"));
			leaveTypeCombo = new SComboField("Leave Type", 200,new LeaveTypeDao().getLeaveTypeModelList(getOfficeID()),"id","name", true, getPropertyName("select"));
			balanceField = new STextField("Leave Balance");
			balanceField.setNewValue("0");
			balanceField.setStyleName("textfield_align_right");
			balanceField.setReadOnly(true);
			fromDate = new SDateField("From Date", 100, getDateFormat());
			toDate = new SDateField("To Date", 100, getDateFormat());
			daysField = new STextField("No of Days");
			daysField.setImmediate(true);
			daysField.setStyleName("textfield_align_right");
			daysField.setReadOnly(true);
			fullRadio=new SRadioButton(null, 200, Arrays.asList(new KeyValue((long)1, "Full Day"),new KeyValue((long)2, "Half Day")),"key", "value");
			fullRadio.setHorizontal(true);
			fullRadio.setVisible(false);
			sessionRadio=new SRadioButton(null, 200, Arrays.asList(new KeyValue((long)1, "First Half"),new KeyValue((long)2, "Second Half")),"key", "value");
			sessionRadio.setHorizontal(true);
			sessionRadio.setVisible(false);
			reasonArea = new STextArea("Reason", 200, 50);
			applyToCombo=new SComboField("Apply To",200,userDao.getAllLoginsFromOfficeExcept(getOfficeID(),null),"id","login_name", true, getPropertyName("select"));
			fromDate.setImmediate(true);
			toDate.setImmediate(true);
			
			formLayout.addComponent(userCombo);
			formLayout.addComponent(leaveTypeCombo);
			formLayout.addComponent(balanceField);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(daysField);
			formLayout.addComponent(fullRadio);
			formLayout.addComponent(sessionRadio);
			formLayout.addComponent(reasonArea);
			formLayout.addComponent(applyToCombo);
			
			table=new STable(null,795,350);
			table.addContainerProperty(TBL_ID, Long.class, null,TBL_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_DATE, String.class, null,TBL_DATE, null, Align.CENTER);
			table.addContainerProperty(TBL_LEAVE_TYPE_ID, Long.class, null,TBL_LEAVE_TYPE_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_LEAVE_TYPE, String.class, null,TBL_LEAVE_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBL_FROM_DATE, String.class, null,TBL_FROM_DATE, null, Align.CENTER);
			table.addContainerProperty(TBL_TO_DATE, String.class, null,TBL_TO_DATE, null, Align.CENTER);
			table.addContainerProperty(TBL_NO_OF_DAYS, Double.class, null,TBL_NO_OF_DAYS, null, Align.CENTER);
			table.addContainerProperty(TBL_STATUS_ID, Integer.class, null,TBL_STATUS_ID, null, Align.LEFT);
			table.addContainerProperty(TBL_STATUS, String.class, null,TBL_STATUS, null, Align.LEFT);
			table.addContainerProperty(TBL_REASON, String.class, null,TBL_REASON, null, Align.LEFT);
			table.setVisibleColumns(visibleHeaders);
			table.setSelectable(true);
			
			applyButton = new SButton("Apply", 75);
			applyButton.setStyleName("savebtnStyle");
			applyButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			cancelButton = new SButton(getPropertyName("cancel"), 85);
			cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			cancelButton.setStyleName("deletebtnStyle");
			cancelButton.setVisible(false);
			
			buttonLayount.addComponent(applyButton);
			formLayout.addComponent(buttonLayount);
			
			tableLayout.addComponent(popupLay);
			tableLayout.addComponent(table);
			tableLayout.addComponent(cancelButton);
			
			mainLayout.addComponent(formLayout);
			mainLayout.addComponent(tableLayout);
			mainPanel.setContent(mainLayout);
			
			leaveDetailsLabel=new SLabel("Leave Details : ");
			leaveStatusLabel=new SLabel("Leave Status : ");
			commentsLabel=new SLabel("Approver's Comments : " );
			leaveDetailsLabel.setStyleName(Reindeer.LABEL_H2);		
			leaveStatusLabel.setStyleName(Reindeer.LABEL_H2);		
			commentsLabel.setStyleName(Reindeer.LABEL_H2);		
			
			
			userCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(userCombo.getValue()!=null){
						loadTableData();
					}
				}
			});
			
			
			fromDate.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(fromDate.getValue()!=null){
						startCalendar.setTime(fromDate.getValue());
						startCalendar.set(Calendar.HOUR_OF_DAY, 0);
						startCalendar.set(Calendar.MINUTE, 0);
						startCalendar.set(Calendar.SECOND, 0);
						startCalendar.set(Calendar.MILLISECOND, 0);
						calculateDays();
						loadLeaveBalance();
						loadSessionRadio();
					}
				}
			});
			
			
			toDate.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(toDate.getValue()!=null){
						endCalendar.setTime(toDate.getValue());
						endCalendar.set(Calendar.HOUR_OF_DAY, 0);
						endCalendar.set(Calendar.MINUTE, 0);
						endCalendar.set(Calendar.SECOND, 0);
						endCalendar.set(Calendar.MILLISECOND, 0);
						calculateDays();
						loadLeaveBalance();
						loadSessionRadio();
					}
				}
			});
			
			
			fullRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(fullRadio.getValue()!=null){
						if((Long)fullRadio.getValue()==1)
							sessionRadio.setVisible(false);
						else if((Long)fullRadio.getValue()==2)
							sessionRadio.setVisible(true);
						sessionRadio.setValue(null);
						sessionRadio.setValue(SConstants.leaveStatus.FIRST_HALF);
					}
				}
			});
			
			
			sessionRadio.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(sessionRadio.getValue()!=null){
						if(sessionRadio.isVisible()){
							daysField.setNewValue("0.5");
						}
						else{
							calculateDays();
						}
					}
					else{
						calculateDays();
					}
				}
			});
			
			fromDate.setValue(getWorkingDate());
			toDate.setValue(getWorkingDate());
			fullRadio.setValue(SConstants.leaveStatus.FULL_DAY);
			
			
			applyButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isLeaveAllocated()){
							if(isValid()){
								LeaveModel leave=new LeaveModel();
								leave.setUser(new UserModel((Long)userCombo.getValue()));
								leave.setLeave_type(new LeaveTypeModel((Long)leaveTypeCombo.getValue()));
								leave.setReason(reasonArea.getValue());
								leave.setFrom_date(CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()));
								leave.setTo_date(CommonUtil.getSQLDateFromUtilDate(toDate.getValue()));
								leave.setNo_of_days(roundNumber(toDouble(daysField.getValue())));
								leave.setDate(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()));
								leave.setStatus(SConstants.leaveStatus.LEAVE_APPLIED);
								if(fullRadio.isVisible())
									leave.setFull_half((Long)fullRadio.getValue());
								else
									leave.setFull_half(SConstants.leaveStatus.NONE);
								if(sessionRadio.isVisible())
									leave.setFirst_second((Long)sessionRadio.getValue());
								else
									leave.setFirst_second(SConstants.leaveStatus.NONE);								
								leave.setAppliedToLogin((Long)applyToCombo.getValue());
								leave.setAppliedByLogin(getLoginID());
								leave.setDaysInYear(yearLeaveString.toString().trim());
								LeaveTypeModel leaveType=new LeaveTypeDao().getLeaveTypeModel((Long)leaveTypeCombo.getValue());
								double balance=roundNumber(toDouble(balanceField.getValue().toString()));
								if(leaveType.isLop()){
									if(balance<=0)
										leave.setLossOfPay(false);
									else
										leave.setLossOfPay(true);
								}
								LeaveHistoryModel histMdl=new LeaveHistoryModel();
								histMdl.setComments(leave.getReason());
								histMdl.setDate(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()));
								histMdl.setStatus(leave.getStatus());
								histMdl.setLogin(new S_LoginModel((Long)applyToCombo.getValue()));
								
								dao.save(leave,histMdl,getOfficeID());
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								loadTableData();
//								sendPushAlert(getUserID(), histMdl
//												.getEmployee().getId(),
//										SConstants.pushAlerts.LEAVE_APPLY,
//										"Leave Request From "
//												+ empDao.getEmployeeFromUser(getUserID()).getFirst_name());
								Object obj=userCombo.getValue();
								userCombo.setValue(null);
								userCombo.setValue(obj);
								leaveTypeCombo.setValue(null);
								fromDate.setValue(getWorkingDate());
								toDate.setNewValue(getWorkingDate());
								fullRadio.setValue(SConstants.leaveStatus.FULL_DAY);
								sessionRadio.setValue(SConstants.leaveStatus.FIRST_HALF);
								reasonArea.setValue("");
								applyToCombo.setValue(null);
							}
						}
						else
							SNotification.show("Leave Not Available. Set Role Leave Mapping", Type.ERROR_MESSAGE);
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
					
				}
			});

			
			table.addValueChangeListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						Item item=table.getItem(table.getValue());
						long status=toLong(item.getItemProperty(TBL_STATUS_ID).getValue().toString());
						
						if(status==SConstants.leaveStatus.LEAVE_APPLIED || status==SConstants.leaveStatus.LEAVE_APPROVED || status==SConstants.leaveStatus.LEAVE_FORWARDED)
							cancelButton.setVisible(true);
						else
							cancelButton.setVisible(false);
						
						leaveDetailsLabel.setValue("Applied on "+item.getItemProperty(TBL_DATE).getValue()+" for "+item.getItemProperty(TBL_NO_OF_DAYS).getValue()+" days.");
						leaveStatusLabel.setValue("Present Leave Status : "+item.getItemProperty(TBL_STATUS).getValue());
						try {
							commentsLabel.setValue(dao.getApproverComments((Long)item.getItemProperty(TBL_ID).getValue()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						popupContlay.addComponent(leaveDetailsLabel);
						popupContlay.addComponent(commentsLabel);
						SPopupView pop = new SPopupView("", popupContlay);
						pop.setWidth("250px");
						popupLay.removeAllComponents();
						popupLay.addComponent(pop);
						pop.setHideOnMouseOut(false);
						pop.setPopupVisible(true);
						
					}else{
						cancelButton.setVisible(false);
						leaveDetailsLabel.setValue("");
						commentsLabel.setValue("");
						popupLay.removeAllComponents();
					}
				}
			});
			
			
			cancelButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					if(table.getValue()!=null){
						ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										Item item=table.getItem(table.getValue());
//										LeaveModel leave=dao.getLeaveModel((Long)item.getItemProperty(TBL_ID).getValue());
//										if()
										dao.cancelLeave((Long)item.getItemProperty(TBL_ID).getValue(),
												        CommonUtil.getSQLDateFromUtilDate(getWorkingDate()), getLoginID());
										Notification.show(getPropertyName("cancel_success"),Type.WARNING_MESSAGE);
										loadTableData();
									} catch (Exception e) {
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
					}
				}
			});
			
			
			leaveTypeCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					loadLeaveBalance();
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
						applyButton.click();
				}
			});
			
			
		} catch (Exception e) {
			Notification.show("Only Employees can access this option",Type.WARNING_MESSAGE);
		}

		return mainPanel;
	}
	
	
	private void loadSessionRadio(){
		if(fromDate.getValue()!=null && toDate.getValue()!=null){
			if(fromDate.getValue().compareTo(toDate.getValue())==0)
				fullRadio.setVisible(true);
			else
				fullRadio.setVisible(false);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void calculateDays(){
		try {
			if(fromDate.getValue()!=null && toDate.getValue()!=null){
				if(isDateValid()){
					yearLeaveString="";
					double days=((endCalendar.getTime().getTime()-startCalendar.getTime().getTime())/(60*60*24*1000))+1;
					
					int daysInYear=0, previousYear=0;
					Calendar diffCal=getCalendar();
					diffCal.setTime(startCalendar.getTime());
					previousYear=diffCal.get(Calendar.YEAR);
					
					Calendar cal=getCalendar();
					cal.setTime(startCalendar.getTime());
					
					double itdays=days;
					for(int i=1;i<=itdays;i++){
						List holidayList=new ArrayList();
						List weekOffList=new ArrayList();
						
						if(diffCal.get(Calendar.YEAR)!=previousYear){
							previousYear=diffCal.get(Calendar.YEAR);
							yearLeaveString+=daysInYear+",";
							daysInYear=0;
						}
						
						S_OfficeModel office=new OfficeDao().getOffice(getOfficeID());
						holidayList=new HolidayDao().getHolidayModelList(getOfficeID(), CommonUtil.getSQLDateFromUtilDate(cal.getTime()));
						String[] holidays=office.getHolidays().split(",");
						if(holidays.length>0)
							weekOffList=Arrays.asList(holidays);
						if(holidayList.size()>0 || weekOffList.contains(cal.get(Calendar.DAY_OF_WEEK)+"") ){
							days-=1;
						}
						else{
							daysInYear+=1;
						}
						cal.add(Calendar.DAY_OF_MONTH, 1);
						diffCal.add(Calendar.DAY_OF_MONTH, 1);
					}
					yearLeaveString+=daysInYear+",";
//					System.out.println("Days In Year "+yearLeaveString);
					daysField.setNewValue(roundNumber(days)+"");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void loadLeaveBalance() {
		if (isLeaveValid()) {
			try {
				balanceField.setNewValue(CommonUtil.roundNumber(dao.getLeaveOfEmployee((Long) userCombo.getValue(), 
															(Long) leaveTypeCombo.getValue(),
															CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),getOfficeID()))+"");
				
			} catch (Exception e) {
				balanceField.setNewValue("0");
				e.printStackTrace();
			}
		}else{
			balanceField.setNewValue("0");
		}
	}
	
	
	private boolean isLeaveValid(){
		boolean valid=true;
		
		if(leaveTypeCombo.getValue()==null || leaveTypeCombo.getValue().equals("")){
			valid=false;
		}
		
		if(userCombo.getValue()==null || userCombo.getValue().equals("")){
			valid=false;
		}
		
		if(fromDate.getValue()==null || fromDate.getValue().equals("")){
			valid=false;
		}
		
		return valid;
	}
	
	
	private boolean isLeaveAllocated(){
		boolean valid=true;
		try {
			if(leaveTypeCombo.getValue()!=null && userCombo.getValue()!=null && toDate.getValue()!=null && fromDate.getValue()!=null){
				Calendar calendar=Calendar.getInstance();
				
				if(fromDate.getValue()!=null){
					calendar.setTime(fromDate.getValue());
					int count=dao.getUserLeaveCount((Long)userCombo.getValue(), getOfficeID(), (Long)leaveTypeCombo.getValue(), (long)calendar.get(Calendar.YEAR));
					if(count<=0)
						valid=false;
				}
				else
					valid=false;
				
				if(toDate.getValue()!=null){
					calendar.setTime(toDate.getValue());
					int count2=dao.getUserLeaveCount((Long)userCombo.getValue(), getOfficeID(), (Long)leaveTypeCombo.getValue(), (long)calendar.get(Calendar.YEAR));
					if(count2<=0)
						valid=false;
				}
				else
					valid=false;
					
			}
		} catch (Exception e) {
			valid=false;
		}
		return valid;
	}
	
	
	protected boolean isValid() {
		
		userCombo.setComponentError(null);
		applyToCombo.setComponentError(null);
		leaveTypeCombo.setComponentError(null);
		reasonArea.setComponentError(null);
		daysField.setComponentError(null);
		fromDate.setComponentError(null);
		toDate.setComponentError(null);
		sessionRadio.setComponentError(null);
		fullRadio.setComponentError(null);
		
		boolean flag=true;
		if(leaveTypeCombo.getValue()==null||leaveTypeCombo.getValue().equals("")){
			setRequiredError(leaveTypeCombo, getPropertyName("invalid_selection"), true);
			flag=false;
		}
		
		try {
			if(leaveTypeCombo.getValue()!=null){
				LeaveTypeModel leaveType=new LeaveTypeDao().getLeaveTypeModel((Long)leaveTypeCombo.getValue());
				try {
					if(toDouble(balanceField.getValue().toString())<=0){
						if(!leaveType.isLop()){
							flag=false;
							setRequiredError(leaveTypeCombo, getPropertyName("invalid_selection"), true);
						}
					}
				} catch (Exception e) {
					flag=false;
					setRequiredError(leaveTypeCombo, getPropertyName("invalid_selection"), true);
				}
			}
		} catch (Exception e2) {
			flag=false;
			setRequiredError(leaveTypeCombo, getPropertyName("invalid_selection"), true);
		}
		
		if(userCombo.getValue()==null||userCombo.getValue().equals("")){
			setRequiredError(userCombo, getPropertyName("invalid_selection"), true);
			flag=false;
		}
		
		if(fromDate.getValue()==null){
			setRequiredError(fromDate, getPropertyName("invalid_selection"), true);
			flag=false;
		}
		
		if(toDate.getValue()==null){
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			flag=false;
		}
		if(fromDate.getValue()!=null && toDate.getValue()!=null){
			if(fromDate.getValue().getTime()>toDate.getValue().getTime()){
				setRequiredError(fromDate, getPropertyName("invalid_selection"), true);
				flag=false;
			}
		}
		
		if(sessionRadio.isVisible()){
			if(sessionRadio.getValue()==null){
				setRequiredError(sessionRadio, getPropertyName("invalid_selection"), true);
				flag=false;
			}
		}
		if(fullRadio.isVisible()){
			if(fullRadio.getValue()==null){
				setRequiredError(fullRadio, getPropertyName("invalid_selection"), true);
				flag=false;
			}
		}
		if(reasonArea.getValue()==null||reasonArea.getValue().equals("")){
			setRequiredError(reasonArea, getPropertyName("invalid_data"), true);
			flag=false;
		}
		if(applyToCombo.getValue()==null||applyToCombo.getValue().equals("")){
			setRequiredError(applyToCombo, getPropertyName("invalid_selection"), true);
			flag=false;
		}
		try {
			if(applyToCombo.getValue()!=null && userCombo.getValue()!=null){
				UserModel user=userDao.getUser((Long)userCombo.getValue());
				if(user.getLoginId()!=null){
					if((Long)applyToCombo.getValue()==user.getLoginId().getId()){
						setRequiredError(applyToCombo, getPropertyName("invalid_selection"), true);
						flag=false;
					}
				}
			}
		} catch (Exception e1) {
			flag=false;
			setRequiredError(applyToCombo, getPropertyName("invalid_selection"), true);
		}
		if((Long)applyToCombo.getValue()==getUserID()){
			setRequiredError(applyToCombo, getPropertyName("invalid_selection"), true);
			flag=false;
		}
		
		if(daysField.getValue()==null||daysField.getValue().equals("")){
			setRequiredError(daysField, getPropertyName("invalid_data"), true);
			flag=false;
		}else{
			try {
				if(toDouble(daysField.getValue().toString())<=0){
					setRequiredError(daysField, getPropertyName("invalid_data"), true);
					flag=false;
				}
			} catch (Exception e) {
				setRequiredError(daysField, getPropertyName("invalid_selection"), true);
				flag=false;
			}
		}
		return flag;
	}
	
	
	public boolean isDateValid() {
		boolean flag=true;
		fromDate.setComponentError(null);
		if(fromDate.getValue()!=null && toDate.getValue()!=null){
			if(fromDate.getValue().getTime()>toDate.getValue().getTime()){
				setRequiredError(fromDate, "Select Date", true);
				flag=false;
			}
		}
		return flag;
	}

	
	@SuppressWarnings("rawtypes")
	private void loadTableData() {
		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			List list = new ArrayList();
			list = dao.getAllLeaves((Long)userCombo.getValue());
			if(list.size()>0){
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					LeaveModel mdl = (LeaveModel) iter.next();
					table.addItem(new Object[] { mdl.getId(),
												CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
												mdl.getLeave_type().getId(),
												mdl.getLeave_type().getName(),
												CommonUtil.formatDateToDDMMYYYY(mdl.getFrom_date()),
												CommonUtil.formatDateToDDMMYYYY(mdl.getTo_date()),
												roundNumber(mdl.getNo_of_days()),
												mdl.getStatus(),
												getStatusName(mdl.getStatus()),
												mdl.getReason()}, table.getItemIds().size() + 1);
				}
			}
			else
				SNotification.show("No Leave Data Available", Type.TRAY_NOTIFICATION);
			table.setVisibleColumns(visibleHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	
	private Object getStatusName(int status) {
		String name="";
		switch (status) {
			case SConstants.leaveStatus.LEAVE_APPLIED:
				name="Applied";
				break;
			case SConstants.leaveStatus.LEAVE_APPROVED:
				name="Approved";
				break;
			case SConstants.leaveStatus.LEAVE_REJECTED:
				name="Rejected";
				break;
			case SConstants.leaveStatus.LEAVE_CANCELED:
				name="Cancelled";
				break;
			case SConstants.leaveStatus.LEAVE_FORWARDED:
				name="Forwarded";
				break;
	
			default:
				name="Applied";
				break;
		}
		return name;
	}
}

