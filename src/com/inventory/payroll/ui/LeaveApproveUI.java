package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.dao.LeaveDao;
import com.inventory.payroll.model.LeaveHistoryModel;
import com.inventory.payroll.model.LeaveModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author anil
 * Automobile
 * 08-Jul-2015
 */

/**
 * @author sangeeth
 * @date 16-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class LeaveApproveUI extends SContainerPanel {

	SPanel mainPanel;
	SFormLayout mainLayout;
	
	private static final String TBL_ID = "ID";
	private static final String TBL_USER_ID = "User Id";
	private static final String TBL_USER = "User";
	private static final String TBL_LOGIN_ID = "Login Id";
	private static final String TBL_DATE = "Applied Date";
	private static final String TBL_LEAVE_TYPE_ID="Leave Type Id";
	private static final String TBL_LEAVE_TYPE="Leave Type";
	private static final String TBL_FROM_DATE = "From Date";
	private static final String TBL_TO_DATE = "To Date";
	private static final String TBL_NO_OF_DAYS = "Days";
	private static final String TBL_STATUS_ID="Status Id";
	private static final String TBL_STATUS = "Status";
	private static final String TBL_REASON = "Reason";

	private STable table;

	private Object[] allHeaders;
	private Object[] visibleHeaders;

	private LeaveDao dao;
	UserManagementDao userDao;

	private SButton approveButton;
	private SButton forwardButton;
	private SButton rejectButton;
	
	private STextArea commentsArea;
	
	
	private SDialogBox dailog;
	private SFormLayout dialogLayout;
	
	private SComboField forwardToField;
	
	private SButton dialogOkButton;
	private SButton dialogCancelButton;

	public SPanel getGUI() {
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		SHorizontalLayout hLay = new SHorizontalLayout();
		hLay.setSpacing(true);

		mainLayout = new SFormLayout();
		mainLayout.setMargin(true);

		SGridLayout btnLay = new SGridLayout(5, 1);
		btnLay.setSpacing(true);

		dao = new LeaveDao();
		userDao=new UserManagementDao();

		try {
			
			allHeaders = new String[] { TBL_ID, TBL_USER_ID, TBL_USER, TBL_LOGIN_ID, TBL_DATE, TBL_LEAVE_TYPE_ID, TBL_LEAVE_TYPE, TBL_FROM_DATE, TBL_TO_DATE, 
										TBL_NO_OF_DAYS, TBL_STATUS_ID, TBL_STATUS, TBL_REASON };
			visibleHeaders = new String[] { TBL_USER, TBL_DATE, TBL_LEAVE_TYPE, TBL_FROM_DATE, TBL_TO_DATE, TBL_NO_OF_DAYS, TBL_STATUS, TBL_REASON };

			table = new STable(null, 800, 300);
			table.addContainerProperty(TBL_ID, Long.class, null, TBL_ID, null,Align.CENTER);
			table.addContainerProperty(TBL_USER_ID, Long.class, null, TBL_USER_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_USER, String.class, null, TBL_USER, null, Align.LEFT);
			table.addContainerProperty(TBL_LOGIN_ID, Long.class, null, TBL_LOGIN_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_DATE, String.class, null, TBL_DATE, null, Align.CENTER);
			table.addContainerProperty(TBL_LEAVE_TYPE_ID, Long.class, null, TBL_LEAVE_TYPE_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_LEAVE_TYPE, String.class, null, TBL_LEAVE_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBL_FROM_DATE, String.class, null, TBL_FROM_DATE, null, Align.CENTER);
			table.addContainerProperty(TBL_TO_DATE, String.class, null, TBL_TO_DATE, null, Align.CENTER);
			table.addContainerProperty(TBL_NO_OF_DAYS, Double.class, null, TBL_NO_OF_DAYS, null, Align.CENTER);
			table.addContainerProperty(TBL_STATUS_ID, Integer.class, null, TBL_STATUS_ID, null, Align.LEFT);
			table.addContainerProperty(TBL_STATUS, String.class, null, TBL_STATUS, null, Align.LEFT);
			table.addContainerProperty(TBL_REASON, String.class, null, TBL_REASON, null, Align.LEFT);
			table.setSelectable(true);

			loadTableData();

			mainLayout.addComponent(table);

			commentsArea = new STextArea("Comments", 500, 50);
			mainLayout.addComponent(commentsArea);
			
			approveButton = new SButton("Approve");
			approveButton.setStyleName("savebtnStyle");
			approveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			forwardButton = new SButton("Forward");
			forwardButton.setStyleName("savebtnStyle");
			forwardButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			rejectButton = new SButton("Reject");
			rejectButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			rejectButton.setStyleName("deletebtnStyle");
			approveButton.setVisible(false);
			rejectButton.setVisible(false);
			forwardButton.setVisible(false);
			btnLay.addComponent(approveButton, 2, 0);
			btnLay.addComponent(forwardButton, 3, 0);
			btnLay.addComponent(rejectButton, 4, 0);
			mainLayout.addComponent(btnLay);
			
			forwardToField = new SComboField("Forward To",200,null,
					"id", "first_name");
			forwardToField.setInputPrompt("--------------Select------------");

			dailog = new SDialogBox("Forward",350,300);
			dailog.center();
			dialogLayout = new SFormLayout();
			
			dialogOkButton = new SButton("OK");
			dialogOkButton.setStyleName("savebtnStyle");
			dialogOkButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			dialogCancelButton = new SButton("Cancel");
			dialogCancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			dialogCancelButton.setStyleName("deletebtnStyle");
			
			hLay.addComponent(dialogOkButton);
			hLay.addComponent(dialogCancelButton);
			
			dialogLayout.addComponent(forwardToField);
			dialogLayout.addComponent(hLay);
			
			dailog.addComponent(dialogLayout);
	
			
			approveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
						
						if(isValid()){
							Item item = table.getItem(table.getValue());
							try {
								LeaveHistoryModel histMdl = new LeaveHistoryModel();
								histMdl.setComments(commentsArea.getValue());
								histMdl.setDate(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()));
								histMdl.setStatus(SConstants.leaveStatus.LEAVE_APPROVED);
								histMdl.setLeave((Long)item.getItemProperty(TBL_ID).getValue());
								histMdl.setLogin(new S_LoginModel(getLoginID()));
								dao.doActionLeave(histMdl, SConstants.leaveStatus.LEAVE_APPROVED);
								loadTableData();
								SNotification.show("Leave Approved", Type.WARNING_MESSAGE);
	//							sendPushAlert(getUserID(), userDao
	//									.getUserIdFromEmployeeId(leave
	//											.getEmployee().getId()),
	//									SConstants.pushAlerts.LEAVE_APPROVE,
	//									"Leave Approved By "
	//											+ empDao.getEmployeeFromUser(getUserID()).getFirst_name());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
				}
			});
			
			
			
			rejectButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
						if (isValid()) {
							Item item = table.getItem(table.getValue());
							try {
								LeaveHistoryModel histMdl = new LeaveHistoryModel();
								histMdl.setComments(commentsArea.getValue());
								histMdl.setDate(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()));
								histMdl.setStatus(SConstants.leaveStatus.LEAVE_REJECTED);
								histMdl.setLeave((Long)item.getItemProperty(TBL_ID).getValue());
								histMdl.setLogin(new S_LoginModel(getLoginID()));
								dao.doActionLeave(histMdl, SConstants.leaveStatus.LEAVE_REJECTED);
								loadTableData();
								SNotification.show("Leave Rejected", Type.WARNING_MESSAGE);
	//							sendPushAlert(getUserID(), userDao
	//									.getUserIdFromEmployeeId(leave
	//											.getEmployee().getId()),
	//									SConstants.pushAlerts.LEAVE_APPROVE,
	//									"Leave Rejected By "
	//											+ empDao.getEmployeeFromUser(getUserID()).getFirst_name());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
				}
			});

			
			
			forwardButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings({"static-access" })
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()) {
							Item item=table.getItem(table.getValue());
							long id=toLong(item.getItemProperty(TBL_ID).getValue().toString());
							List<Long> userList=new ArrayList<Long>();
							userList=dao.getAssignedUsers(id);
							SCollectionContainer bic=SCollectionContainer.setList(userDao.getAllLoginsFromOfficeExcept(getOfficeID(),userList), "id");
							forwardToField.setContainerDataSource(bic);
							forwardToField.setItemCaptionPropertyId("login_name");
							forwardToField.setValue(null);
							getUI().getCurrent().addWindow(dailog);
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			
			dialogOkButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					forwardToField.setComponentError(null);
					if(forwardToField.getValue()!=null&&!forwardToField.getValue().equals("")&&(Long)forwardToField.getValue()!=getUserID()){
						try {
							Item item = table.getItem(table.getValue());
							LeaveHistoryModel histMdl = new LeaveHistoryModel();
							histMdl.setComments(commentsArea.getValue());
							histMdl.setDate(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()));
							histMdl.setStatus(SConstants.leaveStatus.LEAVE_FORWARDED);
							histMdl.setLeave((Long)item.getItemProperty(TBL_ID).getValue());
							histMdl.setLogin(new S_LoginModel((Long)forwardToField.getValue()));
							dao.doActionLeave(histMdl, SConstants.leaveStatus.LEAVE_FORWARDED);
							loadTableData();
							SNotification.show("Leave Forwarded", Type.WARNING_MESSAGE);
							getUI().getCurrent().removeWindow(dailog);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						setRequiredError(forwardToField, getPropertyName("invalid_selection"), true);
					}
				}
			});
			
			
			
			dialogCancelButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().getCurrent().removeWindow(dailog);
				}
			});
			
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						approveButton.setVisible(true);
						rejectButton.setVisible(true);
						forwardButton.setVisible(true);
						Item item=table.getItem(table.getValue());
						int stat=(Integer)item.getItemProperty(TBL_STATUS_ID).getValue();
						long loginId=(Long)item.getItemProperty(TBL_LOGIN_ID).getValue();
						
						approveButton.setVisible(false);
						rejectButton.setVisible(false);
						forwardButton.setVisible(false);
						if(loginId==getLoginID()){
							switch (stat) {
								case SConstants.leaveStatus.LEAVE_APPLIED: 	 approveButton.setVisible(true);
																			 rejectButton.setVisible(true);
																			 forwardButton.setVisible(true);
																			 break;
																			 
								case SConstants.leaveStatus.LEAVE_CANCELED:  approveButton.setVisible(false);
																			 rejectButton.setVisible(false);
																			 forwardButton.setVisible(false);
																			 break;
																			 
								case SConstants.leaveStatus.LEAVE_APPROVED:	 approveButton.setVisible(false);
																			 rejectButton.setVisible(true);
																			 forwardButton.setVisible(false);
																			 break;
																			 
								case SConstants.leaveStatus.LEAVE_REJECTED:	 approveButton.setVisible(false);
																			 rejectButton.setVisible(false);
																			 forwardButton.setVisible(false);
																			 break;
																			 
								case SConstants.leaveStatus.LEAVE_FORWARDED: approveButton.setVisible(true);
																			 rejectButton.setVisible(true);
																			 forwardButton.setVisible(true);
																			 break;
																			 
								default:									 approveButton.setVisible(false);
																			 rejectButton.setVisible(false);
																			 forwardButton.setVisible(false);
																			 break;
							}
						}
						else {
							approveButton.setVisible(false);
							rejectButton.setVisible(false);
							forwardButton.setVisible(false);
						}
					}
					else {
						approveButton.setVisible(false);
						rejectButton.setVisible(false);
						forwardButton.setVisible(false);
					}
				}
			});
	
			
			mainPanel.setContent(mainLayout);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return mainPanel;
	}

	
	protected boolean isValid() {
		table.setComponentError(null);
		if(table.getValue()==null){
			setRequiredError(table, "Select a row", true);
			return false;
		}
		return true;
	}

	
	@SuppressWarnings("rawtypes")
	private void loadTableData() {
		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			List list=new ArrayList();
			list=dao.getAllAppliedLeaves(getLoginID());
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					LeaveModel mdl = (LeaveModel) itr.next();
					table.addItem(new Object[]{
							mdl.getId(),
							mdl.getUser().getId(),
							mdl.getUser().getFirst_name()+" "+mdl.getUser().getMiddle_name()+" "+mdl.getUser().getLast_name()+" [ "+mdl.getUser().getEmploy_code()+"]",
							dao.getEligibleEmployee(mdl.getId()),
							CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
							mdl.getLeave_type().getId(),
							mdl.getLeave_type().getName(),
							CommonUtil.formatDateToDDMMYYYY(mdl.getFrom_date()),
							CommonUtil.formatDateToDDMMYYYY(mdl.getTo_date()),
							roundNumber(mdl.getNo_of_days()),
							mdl.getStatus(),
							getStatusName(mdl.getStatus()),
							mdl.getReason()}, table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(visibleHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private Object getStatusName(int status) {
		String name = "";
		switch (status) {
		case SConstants.leaveStatus.LEAVE_APPLIED:
			name = "Applied";
			break;
		case SConstants.leaveStatus.LEAVE_APPROVED:
			name = "Approved";
			break;
		case SConstants.leaveStatus.LEAVE_REJECTED:
			name = "Rejected";
			break;
		case SConstants.leaveStatus.LEAVE_CANCELED:
			name = "Cancelled";
			break;
		case SConstants.leaveStatus.LEAVE_FORWARDED:
			name = "Forwarded";
			break;

		default:
			name = "Applied";
			break;
		}

		return name;
	}
}
