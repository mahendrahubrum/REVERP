package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.LoanRequestDao;
import com.inventory.payroll.model.LoanRequestModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author sangeeth
 * @date 23-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class LoanRequestUI extends SparkLogic {

	private SPanel mainPanel;
	private SFormLayout mainLayout;
	private SHorizontalLayout buttonLayout;
	
	private SComboField requestNoCombo;
	private SComboField employeeCombo;
	private SDateField dateField;
	private SCurrencyField loanAmountField;
	private STextField installmentField;
	private STextField monthlyAmountField;
	private STextField statusField;
	private LoanRequestDao dao;
	
	Date previousDate;
	
	private SButton saveButton;
	private SButton deleteButton;
	private SButton updateButton;
	private SButton createNewButton;
	
	@Override
	public SPanel getGUI() {
		setSize(500, 400);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		
		previousDate=new Date();
		previousDate=getWorkingDate();
		
		mainLayout = new SFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		dao = new LoanRequestDao();
		
		try {
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));
			requestNoCombo = new SComboField(null, 200);
			employeeCombo = new SComboField(getPropertyName("employee"), 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(),false), "id", "first_name", true, getPropertyName("select"));
			dateField = new SDateField(getPropertyName("request_date"), 100, getDateFormat()) ;
			loanAmountField = new SCurrencyField(getPropertyName("loan_amount"), 140, getWorkingDate());
			loanAmountField.setStyleName("textfield_align_right");
			loanAmountField.setImmediate(true);
			installmentField = new STextField(getPropertyName("no_of_installment"),200);
			installmentField.setStyleName("textfield_align_right");
			installmentField.setImmediate(true);
			monthlyAmountField = new STextField(getPropertyName("monthly_amount"), 200);
			monthlyAmountField.setReadOnly(true);
			monthlyAmountField.setStyleName("textfield_align_right");
			statusField = new STextField(getPropertyName("status"), 200);
			statusField.setReadOnly(true);
			statusField.setNewValue(getStatus(0));
			
			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			
			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			
			updateButton = new SButton(getPropertyName("update"), 80);
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");
			
			deleteButton = new SButton(getPropertyName("delete"), 78);
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);
			deleteButton.setVisible(false);
			updateButton.setVisible(false);
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout(getPropertyName("request_no"));
			salLisrLay.addComponent(requestNoCombo);
			salLisrLay.addComponent(createNewButton);
			
			mainLayout.addComponent(salLisrLay);
			mainLayout.addComponent(employeeCombo);
			mainLayout.addComponent(dateField);
			mainLayout.addComponent(loanAmountField);
			mainLayout.addComponent(installmentField);
			mainLayout.addComponent(monthlyAmountField);
			mainLayout.addComponent(statusField);

			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			loadOptions(0);
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					requestNoCombo.setValue((long)0);
				}
			});
			
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(dateField.getValue()!=null){
						if(previousDate.getTime()!=dateField.getValue().getTime()){
							final long id=(Long)loanAmountField.currencySelect.getValue();
							if((Long)loanAmountField.currencySelect.getValue()!=getCurrencyID()){
								ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are You Sure ? Update Currency Rate Accordingly.",new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (!dialog.isConfirmed()) {
											dateField.setValue(previousDate);
										}
										previousDate=dateField.getValue();
										loanAmountField.setCurrencyDate(previousDate);
										loanAmountField.currencySelect.setValue(id);
									}
								});
							}
							loanAmountField.currencySelect.setValue(null);
							loanAmountField.currencySelect.setValue(id);
						}
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			
			loanAmountField.amountField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {

					calculateMonthlyAmount();

				}

			});
			loanAmountField.setValue(getCurrencyID(), 0.0);
			
			
			installmentField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {

					calculateMonthlyAmount();

				}

			});
			installmentField.setValue("0.0");
			
			
			saveButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							if (requestNoCombo.getValue() == null || requestNoCombo.getValue().toString().equals("0")) {
								LoanRequestModel mdl=new LoanRequestModel();
								mdl.setRequestNo(getNextSequence("Loan_Request_Id",getLoginID()) + "");
								mdl.setUser(new UserModel((Long)employeeCombo.getValue()));
								mdl.setRequestDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setAmount(roundNumber(loanAmountField.getValue()));
								mdl.setCurrency(new CurrencyModel(loanAmountField.getCurrency()));
								mdl.setConversionRate(roundNumber(loanAmountField.getConversionRate()));
								mdl.setNoOfInstallment(toDouble(installmentField.getValue()));
								mdl.setStatus(SConstants.statuses.LOAN_APPLY);
								long id = dao.save(mdl);
								SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),"Loan Request No : "+ mdl.getRequestNo()+ ", Employee : "
												+ employeeCombo.getItemCaption(employeeCombo.getValue())
												+ ", Amount : " + mdl.getAmount()+",No. of Ins : "+mdl.getNoOfInstallment(), id);
								loadOptions(id);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.WARNING_MESSAGE);
					}
				}
			});
			
			
			requestNoCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						loanAmountField.setCurrency(getCurrencyID());
						previousDate=getWorkingDate();
						dateField.setValue(getWorkingDate());
						loanAmountField.setCurrencyDate(getWorkingDate());
						if (requestNoCombo.getValue() != null && !requestNoCombo.getValue().toString().equals("0")) {
							LoanRequestModel mdl=dao.getLoanRequestModel((Long)requestNoCombo.getValue());
							employeeCombo.setValue(mdl.getUser().getId());
							previousDate=mdl.getRequestDate();
							dateField.setValue(mdl.getRequestDate());
							loanAmountField.setCurrencyDate(mdl.getRequestDate());
							loanAmountField.setValue(mdl.getCurrency().getId(), roundNumber(mdl.getAmount()));
							loanAmountField.conversionField.setNewValue(roundNumber(mdl.getConversionRate())+"");
							installmentField.setValue(roundNumber(mdl.getNoOfInstallment())+"");
							statusField.setNewValue(getStatus(mdl.getStatus()));
							if(mdl.getStatus()==SConstants.statuses.LOAN_APPLY){
								saveButton.setVisible(false);
								updateButton.setVisible(true);
								deleteButton.setVisible(true);
							}
							else{
								saveButton.setVisible(false);
								updateButton.setVisible(false);
								deleteButton.setVisible(false);
							}
						}
						else{
							employeeCombo.setValue(null);
							loanAmountField.currencySelect.setReadOnly(false);
							loanAmountField.setCurrency(getCurrencyID());
							previousDate=getWorkingDate();
							dateField.setValue(getWorkingDate());
							loanAmountField.setCurrencyDate(getWorkingDate());
							loanAmountField.setValue(0.0);
							installmentField.setValue("0.0");
							statusField.setNewValue(getStatus(0));
							saveButton.setVisible(true);
							updateButton.setVisible(false);
							deleteButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			updateButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							if (requestNoCombo.getValue() != null && !requestNoCombo.getValue().toString().equals("0")) {
								LoanRequestModel mdl=dao.getLoanRequestModel((Long)requestNoCombo.getValue());
								mdl.setUser(new UserModel((Long)employeeCombo.getValue()));
								mdl.setRequestDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setAmount(roundNumber(loanAmountField.getValue()));
								mdl.setCurrency(new CurrencyModel(loanAmountField.getCurrency()));
								mdl.setConversionRate(roundNumber(loanAmountField.getConversionRate()));
								mdl.setNoOfInstallment(toDouble(installmentField.getValue()));
								mdl.setStatus(SConstants.statuses.LOAN_APPLY);
								dao.update(mdl);
								SNotification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),"Loan Request No : "+ mdl.getRequestNo()+ ", Employee : "
												+ employeeCombo.getItemCaption(employeeCombo.getValue())
												+ ", Amount : " + mdl.getAmount()+",No. of Ins : "+mdl.getNoOfInstallment(), mdl.getId());
								loadOptions(mdl.getId());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						SNotification.show(getPropertyName("error"), Type.WARNING_MESSAGE);
					}
				}
			});
			
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					ConfirmDialog.show(getUI(),getPropertyName("are_you_sure"), new ConfirmDialog.Listener() {
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {

								try {
									dao.delete(toLong(requestNoCombo.getValue()+""));
									SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);										
									loadOptions(0);

								} catch (Exception e) {
									SNotification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}
						}
					});	
				}
			});
			
			
			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.add(0, new LoanRequestModel(0,getPropertyName("create_new")));
			list.addAll(dao.getAllLoanRequestList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			requestNoCombo.setContainerDataSource(bic);
			requestNoCombo.setItemCaptionPropertyId("requestNo");
			requestNoCombo.setInputPrompt(getPropertyName("create_new"));
			requestNoCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (employeeCombo.getValue() == null || employeeCombo.getValue().equals("")) {
			setRequiredError(employeeCombo, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(employeeCombo, null, false);
		}
		
		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			valid = false;
		} else {
			setRequiredError(dateField, null, false);
		}
		
		try{
			if (loanAmountField.getValue()<=0) {
				setRequiredError(loanAmountField, getPropertyName("invalid_data"),true);
				valid = false;
			} else {
				setRequiredError(loanAmountField, null, false);
			}
		}catch(NumberFormatException e){			
			setRequiredError(loanAmountField, getPropertyName("invalid_data"),true);
			valid = false;
		}
		
		try{
			if (toDouble(installmentField.getValue().trim())<=0) {
				setRequiredError(installmentField, getPropertyName("invalid_data"),true);
				valid = false;
			} else {
				double installment=toDouble(installmentField.getValue().toString().trim());
				installmentField.setValue(""+(int)installment);
				setRequiredError(installmentField, null, false);
			}
		}catch(NumberFormatException e){			
			setRequiredError(installmentField, getPropertyName("invalid_data"),true);
			valid = false;
		}
		
		try{
			if (toDouble(monthlyAmountField.getValue().trim())<=0) {
				setRequiredError(monthlyAmountField, getPropertyName("invalid_data"),true);
				valid = false;
			} else {
				setRequiredError(monthlyAmountField, null, false);
			}
		}catch(NumberFormatException e){			
			setRequiredError(monthlyAmountField, getPropertyName("invalid_data"),true);
			valid = false;
		}
		
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

	
	private void calculateMonthlyAmount() {
		double loanAmount=0, installment=0;
		try {
			loanAmount=loanAmountField.getValue();
		} catch (Exception e1) {
			loanAmount=0;
		}
		
		try {
			installment=toDouble(installmentField.getValue().trim());
		} catch (Exception e1) {
			installment=0;
		}
		if(loanAmount!=0 && installment!=0){
			try {
				monthlyAmountField.setNewValue(CommonUtil.roundNumber(loanAmount/installment)+"");
			} catch (Exception e1) {
				monthlyAmountField.setNewValue("0");
			}
		}
		else
			monthlyAmountField.setNewValue("0");
	}
	
	
	private String getStatus(int stat) {
		String status="";
		switch (stat) {
		case SConstants.statuses.LOAN_APPLY : 	status="Applied";
												break;
		case SConstants.statuses.LOAN_APPROVED : status="Approved";
												break;
		case SConstants.statuses.LOAN_REJECTED : status="Rejected";
												break;
		default:								status="Applied";
												break;
		}	
		return status;
	}
	
	
	
}
