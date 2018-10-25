package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.dao.CommissionSalaryDao;
import com.inventory.payroll.model.CommissionSalaryModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 21, 2015
 */

@SuppressWarnings("serial")
public class CommissionSalaryUI extends SparkLogic {

	SPanel mainPanel;
	SFormLayout form;
	SHorizontalLayout createLayout;
	SHorizontalLayout buttonLayout;
	SComboField commissonCombo;
	SComboField employeeCombo;
	SDateField dateField;
	STextField paymentField;
	SButton saveButton,updateButton,deleteButton,createNew;
	
	CommissionSalaryDao dao;
	SettingsValuePojo settings;
	WrappedSession session;
	
	@Override
	public SPanel getGUI() {
		try{
			dao=new CommissionSalaryDao();
			session = getHttpSession();

			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
			
			
			mainPanel=new SPanel();
			setSize(400, 245);
			form=new SFormLayout();
			form.setSizeFull();
			form.setSpacing(true);
			form.setMargin(true);
			
			createLayout=new SHorizontalLayout("Commission Salary");
			createLayout.setSpacing(true);
			buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			
			commissonCombo=new SComboField(null, 200);
			commissonCombo.setInputPrompt(getPropertyName("create_new"));
			createNew=new SButton();
			createNew.setStyleName("createNewBtnStyle");
			createNew.setDescription(getPropertyName("create_new"));
			employeeCombo=new SComboField("Employee", 200, 
											new UserManagementDao().getUsersWithFullNameAndCodeUnderOffice(getOfficeID()), "id", "first_name", 
											true, getPropertyName("select"));
			dateField=new SDateField("Date", 100);
			dateField.setValue(getMonthStartDate());
			dateField.setImmediate(true);
			paymentField=new STextField("Salary Amount", 200);
			paymentField.setInputPrompt("Salary Amount");
			paymentField.setValue("0");
			saveButton=new SButton(getPropertyName("save"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			updateButton=new SButton(getPropertyName("update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			updateButton.setVisible(false);
			deleteButton=new SButton(getPropertyName("delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			deleteButton.setVisible(false);
			createLayout.addComponent(commissonCombo);
			createLayout.addComponent(createNew);
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			
			form.addComponent(createLayout);
			form.addComponent(employeeCombo);
			form.addComponent(dateField);
			form.addComponent(paymentField);
			form.addComponent(buttonLayout);
			loadSalary(0);
			
			
			
			
			createNew.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						commissonCombo.setValue((long)0);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			
			
			employeeCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(employeeCombo.getValue()!=null){
							dateField.setValue(null);
							dateField.setValue(getWorkingDate());
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
 			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if(isValid()){
							if(commissonCombo.getValue()==null || commissonCombo.getValue().toString().equals("0")){
								CommissionSalaryModel mdl=new CommissionSalaryModel();
								Calendar calendar=getCalendar();
								calendar.setTime(dateField.getValue());
								calendar.set(Calendar.DATE, 1);
								mdl.setPayment_number(getNextSequence("Commission Payment", getLoginID()));
								mdl.setEmployee(new UserModel((Long)employeeCombo.getValue()));
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setMonth(CommonUtil.getSQLDateFromUtilDate(calendar.getTime()));
								mdl.setSalary(roundNumber(toDouble(paymentField.getValue().toString())));
								UserModel user=new UserManagementDao().getUser((Long)employeeCombo.getValue());
								FinTransaction salary=new FinTransaction();
								salary.addTransaction(	SConstants.CR, 
														settings.getCASH_ACCOUNT(), 
														settings.getSALARY_ACCOUNT(), 
														roundNumber(toDouble(paymentField.getValue().toString())));
								long id=dao.save(mdl, 
												salary.getTransaction(SConstants.COMMISSION_SALARY, CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								loadSalary(id);
								SNotification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			
			commissonCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if(commissonCombo.getValue()!=null && !commissonCombo.getValue().toString().equals("0")){
							CommissionSalaryModel mdl=dao.getCommissionSalaryModel((Long)commissonCombo.getValue());
							if(mdl!=null){
								employeeCombo.setValue(mdl.getEmployee().getId());
								dateField.setValue(mdl.getDate());
								paymentField.setValue(mdl.getSalary()+"");
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}
						}
						else{
							resetAll();
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			
			
			updateButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if(isValid()){
							if(commissonCombo.getValue()!=null && !commissonCombo.getValue().toString().equals("0")){
								CommissionSalaryModel mdl=dao.getCommissionSalaryModel((Long)commissonCombo.getValue());
								if(mdl!=null){
									if(mdl.getPaid_amount()==0){
										Calendar calendar=getCalendar();
										calendar.setTime(dateField.getValue());
										calendar.set(Calendar.DATE, 1);
										mdl.setEmployee(new UserModel((Long)employeeCombo.getValue()));
										mdl.setOffice(new S_OfficeModel(getOfficeID()));
										mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
										mdl.setMonth(CommonUtil.getSQLDateFromUtilDate(calendar.getTime()));
										mdl.setSalary(roundNumber(toDouble(paymentField.getValue().toString())));
										UserModel user=new UserManagementDao().getUser((Long)employeeCombo.getValue());
										FinTransaction sal=new FinTransaction();
										sal.addTransaction(	SConstants.CR, 
																settings.getCASH_ACCOUNT(), 
																settings.getSALARY_ACCOUNT(), 
																roundNumber(toDouble(paymentField.getValue().toString())));
										
										TransactionModel salary=dao.getTransactionModel(mdl.getTransaction_id());
										salary.setTransaction_details_list(sal.getChildList());
										salary.setDate(mdl.getDate());
										salary.setLogin_id(getLoginID());
										
										
										dao.update(mdl, salary);
										loadSalary(mdl.getId());
										SNotification.show(getPropertyName("update_success"), Type.WARNING_MESSAGE);
									}
									else{
										SNotification.show(getPropertyName("data_used_salary_disbursal"));
									}
								}
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try{
						if(commissonCombo.getValue()!=null && !commissonCombo.getValue().toString().equals("0")){
							final CommissionSalaryModel mdl=dao.getCommissionSalaryModel((Long)commissonCombo.getValue());
							if(mdl!=null){
								ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												if(mdl.getPaid_amount()==0){
													dao.delete(mdl);
													loadSalary(0);
													Notification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
												}
												else{
													SNotification.show(getPropertyName("data_used_salary_disbursal"));
												}
												
											} 
											catch (Exception e) {
												Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
			        			        } 
			        			    }
			        			});
							}
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			mainPanel.setContent(form);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return mainPanel;
	}

	
	
	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(employeeCombo.getValue()==null || employeeCombo.getValue().toString().equals("0")){
			valid=false;
			setRequiredError(employeeCombo, getPropertyName("invalid_selection"), true);
		}
		else{
			setRequiredError(employeeCombo, null, false);
		}
		
		if(dateField.getValue()==null){
			valid=false;
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
		}
		else{
			if(dateField.getValue().getTime()>new Date().getTime()){
				valid=false;
				setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			}
			else{
				setRequiredError(dateField, null, false);
			}
		}
		
		if (paymentField.getValue() == null || paymentField.getValue().equals("")) {
			valid=false;
			setRequiredError(paymentField, getPropertyName("invalid_selection"), true);
		} 
		else {
			try {
				if (toDouble(paymentField.getValue().toString()) < 0) {
					valid=false;
					setRequiredError(paymentField, getPropertyName("invalid_selection"), true);
				}
				else{
					setRequiredError(paymentField, null, false);
				}
			} 
			catch (Exception e) {
				valid=false;
				setRequiredError(paymentField, getPropertyName("invalid_selection"), true);
			}
		}
		
		return valid;
	}

	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadSalary(long id){
		List list=new ArrayList();
		try{
			list.add(0, new CommissionSalaryModel(0, getPropertyName("create_new")));
			list.addAll(dao.getAllCommissionSalary(getOfficeID()));
			SCollectionContainer bic=SCollectionContainer.setList(list, "id");
			commissonCombo.setContainerDataSource(bic);
			commissonCombo.setItemCaptionPropertyId("comments");
			commissonCombo.setValue(id);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void resetAll() {
		employeeCombo.setValue(null);
		paymentField.setValue("0");
		dateField.setValue(getWorkingDate());
		saveButton.setVisible(true);
		updateButton.setVisible(false);
		deleteButton.setVisible(false);
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}



	public SComboField getCommissonCombo() {
		return commissonCombo;
	}



	public void setCommissonCombo(SComboField commissonCombo) {
		this.commissonCombo = commissonCombo;
	}


	
	
}
