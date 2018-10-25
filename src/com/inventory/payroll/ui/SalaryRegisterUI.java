package com.inventory.payroll.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.bean.PaySlipBean;
import com.inventory.payroll.dao.PayrollEmployeeMapDao;
import com.inventory.payroll.dao.SalaryDisbursalDao;
import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class SalaryRegisterUI extends SparkLogic{

	SPanel mainPanel;
	SVerticalLayout mainLayout;
//	SDateField dateField;
	
	SComboField userCombo;
	InlineDateField fromDateField;
	InlineDateField toDateField;
	
	Calendar startCalendar;
	Calendar endCalendar;
	
	SimpleDateFormat sdf,df,mdf;
	STable table;
	
	SButton printButton;
	SButton printSingleButton;
	
//	SButton saveButton;
//	SButton updateButton;
//	SButton deleteButton;
	
//	SButton updateItemButton;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_UID = "UID";
	static String TBC_USER = "User";
	static String TBC_DATE_ID = "Date Id";
	static String TBC_DATE = "Month";
	static String TBC_PAYROLL = "Payroll";
	static String TBC_ADVANCE = "Employee Advance";
	static String TBC_LOP = "Loss Of Pay";
	static String TBC_OT = "Over Time";
	static String TBC_COMMISSION = "Commission Amount";
	static String TBC_TOTAL = "Total";
	static String TBC_LOAN = "Loan Amount";
	static String TBC_PAYMENT = "Payment Amount";
	static String TBC_STATUS_ID = "Status Id";
	static String TBC_STATUS = "Status";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	SalaryDisbursalDao dao;
	
	private WrappedSession session;
	private SettingsValuePojo settings;
	
	STextField payrollField;
	STextField advanceField;
	STextField lopField;
	STextField overTimeField;
	STextField commissionField;
	STextField loanField;
	STextField totalField;

	ValueChangeListener valueChangeListener;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		setSize(1250, 650);
		mainLayout=new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mdf = new SimpleDateFormat("MMMM");
		sdf = new SimpleDateFormat("hh:mm:a");
		df = new SimpleDateFormat("HH:mm");
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		allHeaders=new Object[]{ TBC_SN, TBC_ID, TBC_UID, TBC_USER, TBC_DATE_ID, TBC_DATE, TBC_PAYROLL, TBC_ADVANCE, 
				TBC_LOP, TBC_OT, TBC_COMMISSION, TBC_TOTAL, TBC_LOAN, TBC_PAYMENT, TBC_STATUS_ID, TBC_STATUS };
		
		requiredHeaders=new Object[]{ TBC_SN, TBC_USER, TBC_DATE, TBC_PAYROLL, TBC_ADVANCE, TBC_LOP, TBC_OT, TBC_COMMISSION, TBC_TOTAL, TBC_LOAN, TBC_PAYMENT, TBC_STATUS };
		
		List<Object> tempList = new ArrayList<Object>();
		Collections.addAll(tempList, requiredHeaders);
		
		if(!settings.isCOMMISSION_SALARY_ENABLED()){
			tempList.remove(TBC_COMMISSION);
		}
		requiredHeaders = tempList.toArray(new String[tempList.size()]);
		
		startCalendar=Calendar.getInstance();
		endCalendar=Calendar.getInstance();
		
		dao=new SalaryDisbursalDao();
		try {
//			dateField=new SDateField("Disbursal Date", 100, getDateFormat(), getWorkingDate());
//			dateField.setImmediate(true);
			
			List userList=new ArrayList();
			userList.add(0, new UserModel(0, getPropertyName("all")));
			userList.addAll(new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), false));
			userCombo=new SComboField(getPropertyName("user"), 200, userList, "id", "first_name", true, getPropertyName("select"));
			
			fromDateField=new InlineDateField("Month");
			fromDateField.setImmediate(true);
			fromDateField.setResolution(Resolution.MONTH);
			toDateField=new InlineDateField("Month");
			toDateField.setImmediate(true);
			toDateField.setResolution(Resolution.MONTH);
			
			SHorizontalLayout topLayout=new SHorizontalLayout();
			topLayout.setSpacing(true);
			topLayout.addComponent(new SFormLayout(fromDateField));
			topLayout.addComponent(new SFormLayout(toDateField));
			topLayout.addComponent(new SFormLayout(userCombo));
			
			printButton = new SButton(getPropertyName("print"), 80);
			printButton.setIcon(new ThemeResource("icons/print.png"));
			printButton.setStyleName("deletebtnStyle");
			
			printSingleButton = new SButton(getPropertyName("print"), 80);
			printSingleButton.setIcon(new ThemeResource("icons/print.png"));
			printSingleButton.setStyleName("deletebtnStyle");
			printSingleButton.setVisible(false);
			
//			updateItemButton = new SButton(null, "Update Item");
//			updateItemButton.setStyleName("updateItemBtnStyle");
			
			table=new STable();
			table.setWidth("1175");
			table.setHeight("450");
			table.setSelectable(true);
			
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.CENTER);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UID, Long.class, null, TBC_UID, null, Align.CENTER);
			table.addContainerProperty(TBC_USER, String.class, null, TBC_USER, null, Align.LEFT);
			table.addContainerProperty(TBC_DATE_ID, Date.class, null, TBC_DATE_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_DATE, String.class, null, TBC_DATE, null, Align.LEFT);
			table.addContainerProperty(TBC_PAYROLL, Double.class, null, TBC_PAYROLL, null, Align.RIGHT);
			table.addContainerProperty(TBC_ADVANCE, Double.class, null, TBC_ADVANCE, null, Align.RIGHT);
			table.addContainerProperty(TBC_LOP, Double.class, null, TBC_LOP, null, Align.RIGHT);
			table.addContainerProperty(TBC_OT, Double.class, null, TBC_OT, null, Align.RIGHT);
			table.addContainerProperty(TBC_COMMISSION, Double.class, null, TBC_COMMISSION, null, Align.RIGHT);
			table.addContainerProperty(TBC_TOTAL, Double.class, null, TBC_TOTAL, null, Align.RIGHT);
			table.addContainerProperty(TBC_LOAN, Double.class, null, TBC_LOAN, null, Align.RIGHT);
			table.addContainerProperty(TBC_PAYMENT, Double.class, null, TBC_PAYMENT, null, Align.RIGHT);
			table.addContainerProperty(TBC_STATUS_ID, Boolean.class, null, TBC_STATUS_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_STATUS, String.class, null, TBC_STATUS, null, Align.LEFT);
			
			table.setColumnExpandRatio(TBC_SN, .25f);
			table.setColumnExpandRatio(TBC_USER, 1f);
			table.setColumnExpandRatio(TBC_DATE, 1f);
			table.setColumnExpandRatio(TBC_PAYROLL, 1f);
			table.setColumnExpandRatio(TBC_ADVANCE, 1f);
			table.setColumnExpandRatio(TBC_LOP, 1f);
			table.setColumnExpandRatio(TBC_OT, 1f);
			table.setColumnExpandRatio(TBC_TOTAL, 1f);
			table.setColumnExpandRatio(TBC_LOAN, 1f);
			table.setColumnExpandRatio(TBC_PAYMENT, 1f);
			table.setColumnExpandRatio(TBC_STATUS, 1f);
			table.setFooterVisible(true);
			table.setVisibleColumns(requiredHeaders);
			table.setColumnFooter(TBC_DATE, getPropertyName("total"));
			calculateTableTotal();
			
			
			SVerticalLayout mainItemLayout=new SVerticalLayout();
			mainItemLayout.setStyleName("po_border");
			mainItemLayout.setSpacing(true);
			mainItemLayout.setMargin(true);
			
			SGridLayout itemLayout1=new SGridLayout(8, 3);
			itemLayout1.setSpacing(true);

			payrollField=new STextField("Payroll", 150);
			advanceField=new STextField("Advance Paid", 150);
			lopField=new STextField("Loss Of Pay", 150);
			overTimeField=new STextField("Over Time Amount", 150);
			commissionField=new STextField("Commission", 150);
			loanField=new STextField("Loan Amount", 150);
			totalField=new STextField("Payment Amount", 150);
			totalField.setReadOnly(true);
			
			payrollField.setStyleName("textfield_align_right");
			advanceField.setStyleName("textfield_align_right");
			lopField.setStyleName("textfield_align_right");
			overTimeField.setStyleName("textfield_align_right");
			commissionField.setStyleName("textfield_align_right");
			loanField.setStyleName("textfield_align_right");
			totalField.setStyleName("textfield_align_right");
			
			payrollField.setImmediate(true);
			advanceField.setImmediate(true);
			lopField.setImmediate(true);
			overTimeField.setImmediate(true);
			commissionField.setImmediate(true);
			loanField.setImmediate(true);
			totalField.setImmediate(true);
			
			itemLayout1.addComponent(payrollField);
			itemLayout1.addComponent(advanceField);
			itemLayout1.addComponent(lopField);
			itemLayout1.addComponent(overTimeField);
			if(settings.isCOMMISSION_SALARY_ENABLED())
				itemLayout1.addComponent(commissionField);
			itemLayout1.addComponent(loanField);
			itemLayout1.addComponent(totalField);
//			itemLayout1.addComponent(updateItemButton);
			
//			itemLayout1.setComponentAlignment(updateItemButton, Alignment.MIDDLE_CENTER);
//			mainItemLayout.addComponent(itemLayout1);
			
			/*saveButton = new SButton(getPropertyName("save"), 100, 25);
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
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);*/
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			
			buttonLayout.addComponent(printButton);
			buttonLayout.addComponent(printSingleButton);
			
//			updateButton.setVisible(false);
//			deleteButton.setVisible(false);
			
			mainLayout.addComponent(topLayout);
			mainLayout.addComponent(table);
//			mainLayout.addComponent(mainItemLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			
			valueChangeListener=new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateTotal();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			resetItems();
			payrollField.addValueChangeListener(valueChangeListener);
			advanceField.addValueChangeListener(valueChangeListener);
			lopField.addValueChangeListener(valueChangeListener);
			overTimeField.addValueChangeListener(valueChangeListener);
			commissionField.addValueChangeListener(valueChangeListener);
			loanField.addValueChangeListener(valueChangeListener);

			
			fromDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(fromDateField.getValue()!=null){
							startCalendar.setTime(fromDateField.getValue());
							startCalendar.set(Calendar.DAY_OF_MONTH, 1);
							startCalendar.set(Calendar.HOUR_OF_DAY, 0);
							startCalendar.set(Calendar.MINUTE, 0);
							startCalendar.set(Calendar.SECOND, 0);
							startCalendar.set(Calendar.MILLISECOND, 0);
							if(userCombo.getValue()!=null)
								loadTable((Long)userCombo.getValue());
						}
						else
							table.removeAllItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			fromDateField.setValue(getWorkingDate());
			
			
			toDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(toDateField.getValue()!=null){
							endCalendar.setTime(toDateField.getValue());
							endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
							endCalendar.set(Calendar.HOUR_OF_DAY, 0);
							endCalendar.set(Calendar.MINUTE, 0);
							endCalendar.set(Calendar.SECOND, 0);
							endCalendar.set(Calendar.MILLISECOND, 0);
							if(userCombo.getValue()!=null)
								loadTable((Long)userCombo.getValue());
						}
						else
							table.removeAllItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			fromDateField.setValue(getWorkingDate());
			
			
			userCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(userCombo.getValue()!=null){
						loadTable((Long)userCombo.getValue());
					}
					else
						table.removeAllItems();
				}
			});
			userCombo.setValue((long)0);
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						resetItems();
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							if(item!=null){
								printButton.setVisible(false);
								printSingleButton.setVisible(true);
							}
							else{
								printButton.setVisible(true);
								printSingleButton.setVisible(false);
							}
							/*payrollField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_PAYROLL).getValue())+"");
							advanceField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_ADVANCE).getValue())+"");
							lopField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_LOP).getValue())+"");
							overTimeField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_OT).getValue())+"");
							commissionField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_COMMISSION).getValue())+"");
							loanField.setNewValue(roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue())+"");
							
							advanceField.setReadOnly(true);
							lopField.setReadOnly(true);
							overTimeField.setReadOnly(true);
							commissionField.setReadOnly(true);
							loanField.setReadOnly(true);*/
						}
						else{
							printButton.setVisible(true);
							printSingleButton.setVisible(false);
//							resetItems();
						}
						calculateTotal();
						calculateTableTotal();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			printButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						Iterator itr=table.getItemIds().iterator();
						List reportList=new ArrayList();
						while (itr.hasNext()) {
							Item item = table.getItem(itr.next());
							PaySlipBean bean=new PaySlipBean(item.getItemProperty(TBC_USER).getValue().toString(), 
															item.getItemProperty(TBC_DATE).getValue().toString(), 
															roundNumber((Double)item.getItemProperty(TBC_PAYROLL).getValue()),
															roundNumber((Double)item.getItemProperty(TBC_ADVANCE).getValue()), 
															roundNumber((Double)item.getItemProperty(TBC_LOP).getValue()), 
															roundNumber((Double)item.getItemProperty(TBC_OT).getValue()), 
															roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue()),
															roundNumber((Double)item.getItemProperty(TBC_PAYMENT).getValue()));
							reportList.add(bean);
						}
						if(reportList.size()>0){
							HashMap<String, Object> map = new HashMap<String, Object>();
							String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
							map.put("REPORT_TITLE_LABEL", getPropertyName("salary_register"));
							map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("salary_register")+" "+getPropertyName("from")+" "+
															CommonUtil.formatDateToDDMMYYYY(startCalendar.getTime())+" "+
															getPropertyName("to")+" "+CommonUtil.formatDateToDDMMYYYY(endCalendar.getTime()));
							
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("USER_LABEL", getPropertyName("user"));
							map.put("MONTH_LABEL", getPropertyName("month"));
							map.put("PAYROLL_LABEL", getPropertyName("payroll"));
							map.put("ADVANCE_LABEL", getPropertyName("advance"));
							map.put("LOP_LABEL", getPropertyName("loss_of_pay"));
							map.put("OVER_TIME_LABEL", getPropertyName("over_time"));
							map.put("LOAN_LABEL", getPropertyName("loan"));
							map.put("PAYMENT_LABEL", getPropertyName("payment"));
							map.put("TOTAL_LABEL", getPropertyName("total"));
							
							S_OfficeModel office = new OfficeDao().getOffice(getOfficeID());
							try {
								File headFile=new File(rootPath.trim()+office.getHeader().trim());
								if(headFile.exists()&& !headFile.isDirectory())
									map.put("HEADER", rootPath.trim()+office.getHeader().trim());
								else
									map.put("HEADER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("HEADER", rootPath.trim()+"blank.png");
							}
							
							try {
								File footFile=new File(rootPath.trim()+office.getFooter().trim());
								if(footFile.exists()&& !footFile.isDirectory())
									map.put("FOOTER", rootPath.trim()+office.getFooter().trim());
								else
									map.put("FOOTER", rootPath.trim()+"blank.png");
							} catch (Exception e1) {
								map.put("FOOTER", rootPath.trim()+"blank.png");
							}
							
							Report report = new Report(getLoginID());
							report.setJrxmlFileName("Pay_Slip_Print");
							report.setReportFileName("Salary Register");
							report.setReportType(Report.PDF);
							report.createReport(reportList, map);
							report.print();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
			
			printSingleButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							List reportList=new ArrayList();
							if(item!=null){
								List list=new ArrayList();
								list=new PayrollEmployeeMapDao().getPayRollMap((Long)item.getItemProperty(TBC_UID).getValue(), getOfficeID());
								Iterator it=list.iterator();
								while (it.hasNext()) {
									PayrollEmployeeMapModel map = (PayrollEmployeeMapModel) it.next();
									PaySlipBean bean=new PaySlipBean(map.getComponent().getName(), roundNumber(map.getValue()));
									reportList.add(bean);
								}
								if(reportList.size()>0){
									HashMap<String, Object> map = new HashMap<String, Object>();
									String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"images/";
									map.put("REPORT_TITLE_LABEL", getPropertyName("salary_register"));
									map.put("REPORT_SUB_TITLE_LABEL", getPropertyName("salary_register")+" "+getPropertyName("of")+" "+
																		item.getItemProperty(TBC_USER).getValue().toString()+" "+
																		getPropertyName("of")+" "+item.getItemProperty(TBC_DATE).getValue().toString());
									map.put("SL_NO_LABEL", getPropertyName("sl_no"));
									map.put("USER_LABEL", getPropertyName("user"));
									map.put("MONTH_LABEL", getPropertyName("month"));
									map.put("PAYROLL_LABEL", getPropertyName("payroll"));
									map.put("ADVANCE_LABEL", getPropertyName("advance"));
									map.put("LOP_LABEL", getPropertyName("loss_of_pay"));
									map.put("OVER_TIME_LABEL", getPropertyName("over_time"));
									map.put("LOAN_LABEL", getPropertyName("loan"));
									map.put("PAYMENT_LABEL", getPropertyName("payment"));
									map.put("TOTAL_LABEL", getPropertyName("total"));
									
									map.put("payroll", roundNumber((Double)item.getItemProperty(TBC_PAYROLL).getValue()));
									map.put("advance", roundNumber((Double)item.getItemProperty(TBC_ADVANCE).getValue()));
									map.put("lop", roundNumber((Double)item.getItemProperty(TBC_LOP).getValue()));
									map.put("overTime", roundNumber((Double)item.getItemProperty(TBC_OT).getValue()));
									map.put("loan", roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue()));
									map.put("payment", roundNumber((Double)item.getItemProperty(TBC_PAYMENT).getValue()));
									
									S_OfficeModel office = new OfficeDao().getOffice(getOfficeID());
									try {
										File headFile=new File(rootPath.trim()+office.getHeader().trim());
										if(headFile.exists()&& !headFile.isDirectory())
											map.put("HEADER", rootPath.trim()+office.getHeader().trim());
										else
											map.put("HEADER", rootPath.trim()+"blank.png");
									} catch (Exception e1) {
										map.put("HEADER", rootPath.trim()+"blank.png");
									}
									
									try {
										File footFile=new File(rootPath.trim()+office.getFooter().trim());
										if(footFile.exists()&& !footFile.isDirectory())
											map.put("FOOTER", rootPath.trim()+office.getFooter().trim());
										else
											map.put("FOOTER", rootPath.trim()+"blank.png");
									} catch (Exception e1) {
										map.put("FOOTER", rootPath.trim()+"blank.png");
									}
									
									Report report = new Report(getLoginID());
									report.setJrxmlFileName("Pay_Slip_Single_Print");
									report.setReportFileName("Salary Register");
									report.setReportType(Report.PDF);
									report.createReport(reportList, map);
									report.print();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			/*updateItemButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							if(table.getValue()!=null){
								Item item=table.getItem(table.getValue());
								item.getItemProperty(TBC_PAYROLL).setValue(roundNumber(toDouble(payrollField.getValue().toString())));
								item.getItemProperty(TBC_ADVANCE).setValue(roundNumber(toDouble(advanceField.getValue().toString())));
								item.getItemProperty(TBC_LOP).setValue(roundNumber(toDouble(lopField.getValue().toString())));
								item.getItemProperty(TBC_OT).setValue(roundNumber(toDouble(overTimeField.getValue().toString())));
								item.getItemProperty(TBC_COMMISSION).setValue(roundNumber(toDouble(commissionField.getValue().toString())));
								item.getItemProperty(TBC_LOAN).setValue(roundNumber(toDouble(loanField.getValue().toString())));
								item.getItemProperty(TBC_TOTAL).setValue(roundNumber(toDouble(totalField.getValue().toString())));
								item.getItemProperty(TBC_PAYMENT).setValue(roundNumber(toDouble(totalField.getValue().toString())- toDouble(loanField.getValue().toString())));
								table.setValue(null);
							}
						}
						calculateTableTotal();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});*/
			
			
			/*saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						if(isValid()){
							Hashtable<TransactionModel, SalaryDisbursalModel> hash = new Hashtable<TransactionModel, SalaryDisbursalModel>();
							Iterator itr=table.getItemIds().iterator();
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								long id=(Long)item.getItemProperty(TBC_ID).getValue();
								List<SalaryDisbursalDetailsModel> childList=new ArrayList<SalaryDisbursalDetailsModel>();
								
								SalaryDisbursalModel mdl=null;
								if(id!=0)
									mdl=dao.getSalaryDisbursalModel(id);
								if(mdl==null)
									mdl=new SalaryDisbursalModel();
								mdl.setUser(new UserModel((Long)item.getItemProperty(TBC_UID).getValue()));
								mdl.setOfficeId(getOfficeID());
								mdl.setDispursal_date(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setFrom_date(CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()));
								mdl.setTo_date(CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime()));
								mdl.setPayroll(roundNumber((Double)item.getItemProperty(TBC_PAYROLL).getValue()));
								mdl.setAdvance(roundNumber((Double)item.getItemProperty(TBC_ADVANCE).getValue()));
								mdl.setLop(roundNumber((Double)item.getItemProperty(TBC_LOP).getValue()));
								mdl.setOverTime(roundNumber((Double)item.getItemProperty(TBC_OT).getValue()));
								mdl.setCommission(roundNumber((Double)item.getItemProperty(TBC_COMMISSION).getValue()));
								mdl.setLoan(roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue()));
								
								double payroll=0;
								
								payroll=roundNumber((Double)item.getItemProperty(TBC_PAYROLL).getValue());
								
								FinTransaction transaction=new FinTransaction();
								
								transaction.addTransaction(SConstants.DR,
															settings.getSALARY_PAYABLE_ACCOUNT(),
															settings.getSALARY_ACCOUNT(),
															roundNumber((Double)item.getItemProperty(TBC_PAYMENT).getValue()),
															"",
															getCurrencyID(),
															(double)1);
								
								if((Double)item.getItemProperty(TBC_LOAN).getValue()>0){
									transaction.addTransaction(SConstants.CR,
																settings.getSALARY_PAYABLE_ACCOUNT(),
																settings.getSALARY_LOAN_ACCOUNT(),
																roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue()),
																"",
																getCurrencyID(),
																(double)1);
									
									transaction.addTransaction(SConstants.DR,
																settings.getSALARY_LOAN_ACCOUNT(),
																settings.getCASH_ACCOUNT(),
																roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue()),
																"",
																getCurrencyID(),
																(double)1);
								}
								
								if((Double)item.getItemProperty(TBC_ADVANCE).getValue()>0){
									transaction.addTransaction(SConstants.CR,
																settings.getSALARY_PAYABLE_ACCOUNT(),
																settings.getSALARY_ADVANCE_ACCOUNT(),
																roundNumber((Double)item.getItemProperty(TBC_ADVANCE).getValue()),
																"",
																getCurrencyID(),
																(double)1);
								}
								
								transaction.addTransaction(SConstants.DR,
															settings.getCASH_ACCOUNT(),
															settings.getSALARY_PAYABLE_ACCOUNT(),
															roundNumber((Double)item.getItemProperty(TBC_PAYMENT).getValue()),
															"",
															getCurrencyID(),
															(double)1);
								
								List list=new ArrayList();
								list=new PayrollEmployeeMapDao().getPayRollMap((Long)item.getItemProperty(TBC_UID).getValue(), getOfficeID());
								Iterator it=list.iterator();
								while (it.hasNext()) {
									PayrollEmployeeMapModel map = (PayrollEmployeeMapModel) it.next();
									SalaryDisbursalDetailsModel det=new SalaryDisbursalDetailsModel();
									det.setComponent(new PayrollComponentModel(map.getComponent().getId()));
									double value=map.getValue();
									double amount=0;
									
									if(payroll==0)
										amount=0;
									else if(value>payroll)
										amount=payroll;
									else if(value<=payroll)
										amount=value;
									
									det.setAmount(roundNumber(amount));
									childList.add(det);
									payroll-=amount;
								}
								mdl.setDetailsList(childList);
								TransactionModel trans=null;
								if(mdl.getTransactionId()!=0)
									trans=new PaymentDao().getTransaction(mdl.getTransactionId());
								
								if(trans==null){
									trans=transaction.getTransaction(SConstants.PAYROLL_PAYMENTS, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								}
								else{
									trans.setTransaction_details_list(transaction.getChildList());
									trans.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									trans.setLogin_id(getLoginID());
								}
								hash.put(trans, mdl);
							}
							if(hash.size()>0){
								dao.save(hash, CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()), getOfficeID());
								loadTable();
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});*/
			
			
			/*deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											dao.delete(CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()), getOfficeID());
											loadTable();
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
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
			});*/
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void loadTable(long userId){
		
		try {
			List userList=new ArrayList();
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			
			if(userCombo.getValue()!=null){
				
				Calendar monthStart=Calendar.getInstance();
				Calendar monthEnd=Calendar.getInstance();
				monthStart.setTime(startCalendar.getTime());
				monthEnd.setTime(startCalendar.getTime());
				
				monthStart.set(Calendar.DAY_OF_MONTH, 1);
				monthStart.set(Calendar.HOUR_OF_DAY, 0);
				monthStart.set(Calendar.MINUTE, 0);
				monthStart.set(Calendar.SECOND, 0);
				monthStart.set(Calendar.MILLISECOND, 0);
				
				while (monthEnd.getTime().compareTo(endCalendar.getTime())<=0) {
					
					monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
					monthEnd.set(Calendar.HOUR_OF_DAY, 0);
					monthEnd.set(Calendar.MINUTE, 0);
					monthEnd.set(Calendar.SECOND, 0);
					monthEnd.set(Calendar.MILLISECOND, 0);
					
					userList=new UserManagementDao().getUsersFromOffice(getOfficeID(),userId,CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()),
							CommonUtil.getSQLDateFromUtilDate(endCalendar.getTime()));
					if(userList.size()>0){
						for(int i=1;i<=userList.size();i++){
							UserModel user=(UserModel)userList.get(i-1);
							long id=0;
							double payroll=0, advance=0, lop=0, overTime=0, commission=0, loan=0, total=0, payment=0;
							boolean isPaid=false;
							String statusString="Not paid";
							
							SalaryDisbursalModel mdl=null;
							mdl=dao.getSalaryDisbursalModel(user.getId(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(monthStart.getTime()), 
									CommonUtil.getSQLDateFromUtilDate(monthEnd.getTime()));
							
							if(mdl!=null){
								id=mdl.getId();
								payroll=CommonUtil.roundNumber(mdl.getPayroll());
								advance=CommonUtil.roundNumber(mdl.getAdvance());
								lop=CommonUtil.roundNumber(mdl.getLop());
								overTime=CommonUtil.roundNumber(mdl.getOverTime());
								if(settings.isCOMMISSION_SALARY_ENABLED())
									commission=CommonUtil.roundNumber(mdl.getCommission());
								loan=CommonUtil.roundNumber(mdl.getLoan());
								total=CommonUtil.roundNumber(payroll+ commission+ overTime- lop- advance);
							}
							payment=CommonUtil.roundNumber(total- loan);
							
							if(id!=0)
								isPaid=true;
							if(isPaid)
								statusString="Paid";
							
							if(!isPaid)
								continue;
							
							table.addItem(new Object[]{table.getItemIds().size()+1,
														id,
														user.getId(),
														user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name(),
														monthStart.getTime(),
														mdf.format(monthStart.getTime()),
														roundNumber(payroll),
														roundNumber(advance),
														roundNumber(lop),
														roundNumber(overTime),
														roundNumber(commission),
														roundNumber(total),
														roundNumber(loan),
														roundNumber(payment),
														isPaid,
														statusString},table.getItemIds().size()+1);
						}
					}
					monthStart.add(Calendar.MONTH, 1);
					monthEnd.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
			table.setVisibleColumns(requiredHeaders);
			calculateTableTotal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void calculateTotal() {
		
		double payroll=0,advance=0,lop=0,overTime=0,commission=0,total=0, loan=0;
		
		try {
			payroll=toDouble(payrollField.getValue().toString().trim());
		} catch (Exception e) {
			payroll=0;
		}
		
		try {
			advance=toDouble(advanceField.getValue().toString().trim());
		} catch (Exception e) {
			advance=0;
		}
		
		try {
			lop=toDouble(lopField.getValue().toString().trim());
		} catch (Exception e) {
			lop=0;
		}
		
		try {
			overTime=toDouble(overTimeField.getValue().toString().trim());
		} catch (Exception e) {
			overTime=0;
		}
		
		if(settings.isCOMMISSION_SALARY_ENABLED()){
			try {
				commission=toDouble(commissionField.getValue().toString().trim());
			} catch (Exception e) {
				commission=0;
			}
		}
		
		try {
			loan=toDouble(loanField.getValue().toString().trim());
		} catch (Exception e) {
			loan=0;
		}
		
		try {
			total=payroll- advance- lop+ overTime+ commission/*-loan*/;
		} catch (Exception e) {
			total=0;
		}
		
		if(total<loan)
			loan=0;
		loanField.setNewValue(roundNumber(loan)+"");
		
		totalField.setNewValue(roundNumber(total)+"");
	}
	
	
	@SuppressWarnings("rawtypes")
	public void calculateTableTotal() {
		
		double payroll=0,advance=0,lop=0,overTime=0,commission=0,total=0, loan=0, payment=0;
		
		Iterator itr=table.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			payroll+=roundNumber((Double)item.getItemProperty(TBC_PAYROLL).getValue());
			advance+=roundNumber((Double)item.getItemProperty(TBC_ADVANCE).getValue());
			lop+=roundNumber((Double)item.getItemProperty(TBC_LOP).getValue());
			overTime+=roundNumber((Double)item.getItemProperty(TBC_OT).getValue());
			commission+=roundNumber((Double)item.getItemProperty(TBC_COMMISSION).getValue());
			total+=roundNumber((Double)item.getItemProperty(TBC_TOTAL).getValue());
			loan+=roundNumber((Double)item.getItemProperty(TBC_LOAN).getValue());
			payment+=roundNumber((Double)item.getItemProperty(TBC_PAYMENT).getValue());
		}
		table.setColumnFooter(TBC_PAYROLL, roundNumber(payroll)+"");
		table.setColumnFooter(TBC_ADVANCE, roundNumber(advance)+"");
		table.setColumnFooter(TBC_LOP, roundNumber(lop)+"");
		table.setColumnFooter(TBC_OT, roundNumber(overTime)+"");
		table.setColumnFooter(TBC_COMMISSION, roundNumber(commission)+"");
		table.setColumnFooter(TBC_TOTAL, roundNumber(total)+"");
		table.setColumnFooter(TBC_LOAN, roundNumber(loan)+"");
		table.setColumnFooter(TBC_PAYMENT, roundNumber(payment)+"");
	}
	
	
	public void resetItems(){
		advanceField.setReadOnly(false);
		lopField.setReadOnly(false);
		overTimeField.setReadOnly(false);
		commissionField.setReadOnly(false);
		loanField.setReadOnly(false);
		
		payrollField.setNewValue("0.0");
		advanceField.setNewValue("0.0");
		lopField.setNewValue("0.0");
		overTimeField.setNewValue("0.0");
		commissionField.setNewValue("0.0");
		loanField.setNewValue("0.0");
		
		calculateTotal();
	}
	
	
	public boolean isAddingValid(){
		boolean valid=true;
		
		try {
			if(toDouble(payrollField.getValue().toString().trim())<0){
				valid=false;
				setRequiredError(payrollField, getPropertyName("invalid_data"), true);
			}
			else
				setRequiredError(payrollField, null, false);
		} catch (Exception e) {
			valid=false;
			setRequiredError(payrollField, getPropertyName("invalid_data"), true);
		}
		
		try {
			if(toDouble(totalField.getValue().toString().trim())<0){
				valid=false;
				setRequiredError(totalField, getPropertyName("invalid_data"), true);
			}
			else
				setRequiredError(totalField, null, false);
		} catch (Exception e) {
			valid=false;
			setRequiredError(totalField, getPropertyName("invalid_data"), true);
		}
		
		return valid;
	}
	
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		
		/*if(dateField.getValue()==null){
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(dateField, null, false);*/
		
		if(table.getItemIds().size()<=0){
			setRequiredError(table, getPropertyName("invalid_selection"), true);
			valid=false;
		}
		else
			setRequiredError(table, null, false);
		
		/*if(settings.getCASH_ACCOUNT()==0 || settings.getSALARY_LOAN_ACCOUNT()==0 || 
			settings.getSALARY_PAYABLE_ACCOUNT()==0 || settings.getSALARY_ADVANCE_ACCOUNT()==0 || settings.getSALARY_ACCOUNT()==0) {
			valid=false;
			SNotification.show("Account Settings Not Set", Type.ERROR_MESSAGE);
		}*/
		
		return valid;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
