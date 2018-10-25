package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payroll.dao.LoanApprovalDao;
import com.inventory.payroll.dao.LoanRequestDao;
import com.inventory.payroll.model.LoanApprovalModel;
import com.inventory.payroll.model.LoanRequestModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
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
public class LoanApprovalUI extends SparkLogic {

	private SPanel mainPanel;
	private LoanApprovalDao dao;
	private LoanRequestDao reqDao;
	private SFormLayout mainLayout;
	
	private SHorizontalLayout buttonLayout;
	private SButton createNewButton;
	
	private SComboField requestNoCombo;
	private SComboField employeeCombo;
	private SDateField requestDateField;
	private SCurrencyField loanAmountField;
	private STextField installmentField;
	private STextField monthlyAmountField;
	private SDateField approveOrRejectDateField;
	private SDateField paymentStartDateField;
	private SDateField paymentEndDateField;
	private SComboField approvedByCombo;
	private SButton approveButton;
	private SButton rejectButton;
	private STextField monthlyChargeField;
	private STextField totalMonthlyPaymentField;
	private STextField statusField;
	
	Date previousDate;
	
	WrappedSession session;
	SettingsValuePojo settings;
	
	@Override
	public SPanel getGUI() {
		setSize(600, 550);
		reqDao = new LoanRequestDao();
		dao = new LoanApprovalDao();
		
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		previousDate=new Date();
		previousDate=getWorkingDate();
		
		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		
		mainLayout = new SFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		
		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		
		try {
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));
			requestNoCombo = new SComboField(null, 200);
			employeeCombo = new SComboField(getPropertyName("employee"), 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(),false), "id", "first_name", true, getPropertyName("select"));
			employeeCombo.setReadOnly(true);
			requestDateField = new SDateField(getPropertyName("request_date"), 100, getDateFormat(),getWorkingDate());
			requestDateField.setReadOnly(true);
			loanAmountField = new SCurrencyField(getPropertyName("loan_amount"), 140, getWorkingDate());
			loanAmountField.setReadOnly(true);
			loanAmountField.setStyleName("textfield_align_right");
			installmentField = new STextField(getPropertyName("no_of_installment"), 200);
			installmentField.setReadOnly(true);
			installmentField.setImmediate(true);
			installmentField.setStyleName("textfield_align_right");
			monthlyAmountField = new STextField(getPropertyName("monthly_amount"), 200);
			monthlyAmountField.setStyleName("textfield_align_right");
			monthlyAmountField.setReadOnly(true);
			monthlyAmountField.setImmediate(true);
			monthlyAmountField.setNewValue("0.0");
			monthlyChargeField = new STextField(getPropertyName("monthly_charge"), 200);
			monthlyChargeField.setStyleName("textfield_align_right");
			totalMonthlyPaymentField = new STextField(getPropertyName("total_monthly_payment"), 200);
			totalMonthlyPaymentField.setStyleName("textfield_align_right");
			totalMonthlyPaymentField.setReadOnly(true);
			totalMonthlyPaymentField.setImmediate(true);
			totalMonthlyPaymentField.setNewValue("0.0");
			approveOrRejectDateField = new SDateField(getPropertyName("approve_or_reject_date"), 100, getDateFormat());
			paymentStartDateField = new SDateField(getPropertyName("payment_start_date"), 100, getDateFormat());
			paymentEndDateField = new SDateField(getPropertyName("payment_end_date"), 100, getDateFormat());
			paymentEndDateField.setReadOnly(true);
			approvedByCombo = new SComboField(getPropertyName("approved_or_rejected_by"), 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(),false), "id", "first_name", true, getPropertyName("select"));
			statusField = new STextField(getPropertyName("status"), 200);
			statusField.setReadOnly(true);
			statusField.setNewValue(getStatus(0));
			
			approveButton = new SButton(getPropertyName("approve"));
			approveButton.setStyleName("savebtnStyle");
			approveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			rejectButton = new SButton(getPropertyName("reject"));
			rejectButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			rejectButton.setStyleName("deletebtnStyle");
			
			SHorizontalLayout salLisrLay = new SHorizontalLayout(getPropertyName("request_no"));
			salLisrLay.addComponent(requestNoCombo);
			salLisrLay.addComponent(createNewButton);
			
			buttonLayout.addComponent(approveButton);
			buttonLayout.addComponent(rejectButton);

			mainLayout.addComponent(salLisrLay);
			mainLayout.addComponent(employeeCombo);
			mainLayout.addComponent(requestDateField);
			mainLayout.addComponent(loanAmountField);
			mainLayout.addComponent(installmentField);
			mainLayout.addComponent(monthlyAmountField);
			mainLayout.addComponent(monthlyChargeField);
			mainLayout.addComponent(totalMonthlyPaymentField);
			mainLayout.addComponent(approveOrRejectDateField);
			mainLayout.addComponent(paymentStartDateField);
			mainLayout.addComponent(paymentEndDateField);
			mainLayout.addComponent(approvedByCombo);
			mainLayout.addComponent(statusField);

			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);

			loadOptions(0);
			
			
			approveOrRejectDateField.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(approveOrRejectDateField.getValue()!=null){
						if(previousDate.getTime()!=approveOrRejectDateField.getValue().getTime()){
							final long id=(Long)loanAmountField.currencySelect.getValue();
							if((Long)loanAmountField.currencySelect.getValue()!=getCurrencyID()){
								ConfirmDialog.show(getUI().getCurrent().getCurrent(), "Are You Sure ? Update Currency Rate Accordingly.",new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (!dialog.isConfirmed()) {
											approveOrRejectDateField.setValue(previousDate);
										}
										previousDate=approveOrRejectDateField.getValue();
										loanAmountField.setCurrencyDate(previousDate);
										loanAmountField.currencySelect.setValue(id);
									}
								});
							}
							loanAmountField.currencySelect.setNewValue(null);
							loanAmountField.currencySelect.setNewValue(id);
						}
					}
				}
			});
			approveOrRejectDateField.setValue(getWorkingDate());
			
			
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					requestNoCombo.setValue(null);
				}
			});

			
			
			loanAmountField.amountField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					calculateMonthlyAmount();
				}
			});
			loanAmountField.setNewValue(getCurrencyID(), 0.0);
			
			

			installmentField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					calculateMonthlyAmount();
				}
			});
			installmentField.setNewValue("0.0");
			
			
			monthlyChargeField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					calculateMonthlyAmount();
				}
			});
			monthlyChargeField.setNewValue("0.0");
			
			
			paymentStartDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(paymentStartDateField.getValue()!=null){
							loadToDate(paymentStartDateField.getValue());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			paymentStartDateField.setValue(getWorkingDate());
			
			
			approveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							if(settings.getCASH_ACCOUNT()!=0 && settings.getSALARY_LOAN_ACCOUNT()!=0) {
								LoanRequestModel reqMdl=reqDao.getLoanRequestModel((Long)requestNoCombo.getValue());
								LoanApprovalModel mdl=null;
								mdl=dao.getLoanApprovalModelByRequestId((Long)requestNoCombo.getValue());
								if(mdl==null)
									mdl=new LoanApprovalModel();
								
								mdl.setLoanRequest(reqMdl);
								mdl.setLoanAmount(roundNumber(loanAmountField.getValue()));
								mdl.setCurrency(new CurrencyModel(loanAmountField.getCurrency()));
								mdl.setConversionRate(roundNumber(loanAmountField.getConversionRate()));
								mdl.setNoOfInstallment(toDouble(installmentField.getValue()));
								mdl.setMonthlycharge(toDouble(monthlyChargeField.getValue()));
								mdl.setApprovedOrRejectedDate(CommonUtil.getSQLDateFromUtilDate(approveOrRejectDateField.getValue()));
								mdl.setPaymentStartDate(CommonUtil.getSQLDateFromUtilDate(paymentStartDateField.getValue()));
								mdl.setPaymentEndDate(CommonUtil.getSQLDateFromUtilDate(paymentEndDateField.getValue()));
								mdl.setApprovedOrRejectedBy(new UserModel((Long)approvedByCombo.getValue()));
								mdl.setStatus(SConstants.statuses.LOAN_APPROVED);
								reqMdl.setStatus(SConstants.statuses.LOAN_APPROVED);
								
								FinTransaction trans=new FinTransaction();
								
								trans.addTransaction(SConstants.DR,
													settings.getCASH_ACCOUNT(),
													settings.getSALARY_LOAN_ACCOUNT(),
													roundNumber(loanAmountField.getValue()/loanAmountField.getConversionRate()), 
													"",
													loanAmountField.getCurrency(), 
													roundNumber(loanAmountField.getConversionRate()));
								
								TransactionModel transaction=null;
								
								if(mdl.getTransactionId()!=0){
									transaction= new PurchaseDao().getTransactionModel(mdl.getTransactionId());
									transaction.setTransaction_details_list(trans.getChildList());
									transaction.setDate(mdl.getApprovedOrRejectedDate());
									transaction.setLogin_id(getLoginID());
								}
								else
									transaction=trans.getTransaction(SConstants.SALARY_LOAN, CommonUtil.getSQLDateFromUtilDate(approveOrRejectDateField.getValue()));
								
								long id=dao.save(mdl, reqMdl, transaction);
								
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
								saveActivity(getOptionId(),"Loan Request No : "
												+ mdl.getLoanRequest().getRequestNo()
												+ ", Approved By : "+ mdl.getApprovedOrRejectedBy().getFirst_name()
												+ ", Loan Amount : "+ mdl.getLoanAmount()+ ", No. of Ins : "
												+ mdl.getNoOfInstallment()
												+ ", Monthly Charge : "+ mdl.getMonthlycharge(),id);
								loadOptions(reqMdl.getId());
							}
							else
								SNotification.show("Account Settings Not Set", Type.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			requestNoCombo.addValueChangeListener(new ValueChangeListener() {
					
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						loanAmountField.currencySelect.setReadOnly(true);
						loanAmountField.setNewValue(getCurrencyID(),0.0);
						previousDate=getWorkingDate();
						approveOrRejectDateField.setValue(getWorkingDate());
						loanAmountField.setCurrencyDate(getWorkingDate());
						
						employeeCombo.setNewValue(null);
						installmentField.setNewValue("0.0");
						
						monthlyAmountField.setNewValue("0.0");
						monthlyChargeField.setNewValue("0.0");
						totalMonthlyPaymentField.setNewValue("0.0");
						approveOrRejectDateField.setValue(getWorkingDate());
						paymentStartDateField.setValue(null);
						paymentStartDateField.setValue(getWorkingDate());
						statusField.setNewValue(getStatus(0));
						approvedByCombo.setValue(null);
						approveButton.setVisible(true);
						rejectButton.setVisible(true);
						
						if (requestNoCombo.getValue() != null && !requestNoCombo.getValue().toString().equals("")) {
							LoanRequestModel reqMdl=reqDao.getLoanRequestModel((Long)requestNoCombo.getValue());
							LoanApprovalModel mdl=null;
							mdl=dao.getLoanApprovalModelByRequestId((Long)requestNoCombo.getValue());
							employeeCombo.setNewValue(reqMdl.getUser().getId());
							requestDateField.setNewValue(reqMdl.getRequestDate());
							loanAmountField.setNewValue(reqMdl.getCurrency().getId(), roundNumber(reqMdl.getAmount()));
							installmentField.setNewValue(roundNumber(reqMdl.getNoOfInstallment())+"");
							paymentStartDateField.setValue(null);
							paymentStartDateField.setValue(getWorkingDate());
							if(mdl!=null){
								previousDate=mdl.getApprovedOrRejectedDate();
								loanAmountField.setCurrencyDate(mdl.getApprovedOrRejectedDate());
								loanAmountField.conversionField.setNewValue(roundNumber(mdl.getConversionRate())+"");
								monthlyChargeField.setNewValue(roundNumber(mdl.getMonthlycharge())+"");
								approveOrRejectDateField.setValue(mdl.getApprovedOrRejectedDate());
								paymentStartDateField.setValue(null);
								paymentStartDateField.setValue(mdl.getPaymentStartDate());
								paymentEndDateField.setNewValue(mdl.getPaymentEndDate());
								approvedByCombo.setValue(mdl.getApprovedOrRejectedBy().getId());
								statusField.setNewValue(getStatus(mdl.getStatus()));
							}
							if(reqMdl.getStatus()==SConstants.statuses.LOAN_APPLY){
								approveButton.setVisible(true);
								rejectButton.setVisible(true);
							}
							else if(reqMdl.getStatus()==SConstants.statuses.LOAN_APPROVED){
								approveButton.setVisible(false);
								rejectButton.setVisible(true);
							}
							else{
								approveButton.setVisible(false);
								rejectButton.setVisible(false);
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			rejectButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							LoanRequestModel reqMdl=reqDao.getLoanRequestModel((Long)requestNoCombo.getValue());
							LoanApprovalModel mdl=null;
							mdl=dao.getLoanApprovalModelByRequestId((Long)requestNoCombo.getValue());
							if(mdl==null)
								mdl=new LoanApprovalModel();
							
							mdl.setLoanRequest(reqMdl);
							mdl.setLoanAmount(roundNumber(loanAmountField.getValue()));
							mdl.setCurrency(new CurrencyModel(loanAmountField.getCurrency()));
							mdl.setConversionRate(roundNumber(loanAmountField.getConversionRate()));
							mdl.setNoOfInstallment(toDouble(installmentField.getValue()));
							mdl.setMonthlycharge(toDouble(monthlyChargeField.getValue()));
							mdl.setApprovedOrRejectedDate(CommonUtil.getSQLDateFromUtilDate(approveOrRejectDateField.getValue()));
							mdl.setPaymentStartDate(CommonUtil.getSQLDateFromUtilDate(paymentStartDateField.getValue()));
							mdl.setPaymentEndDate(CommonUtil.getSQLDateFromUtilDate(paymentEndDateField.getValue()));
							mdl.setApprovedOrRejectedBy(new UserModel((Long)approvedByCombo.getValue()));
							mdl.setStatus(SConstants.statuses.LOAN_REJECTED);
							reqMdl.setStatus(SConstants.statuses.LOAN_REJECTED);
							
							TransactionModel transaction=null;
							
							long id=dao.save(mdl, reqMdl, transaction);
							
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							saveActivity(getOptionId(),"Loan Request No : "
											+ mdl.getLoanRequest().getRequestNo()
											+ ", Approved By : "+ mdl.getApprovedOrRejectedBy().getFirst_name()
											+ ", Loan Amount : "+ mdl.getLoanAmount()+ ", No. of Ins : "
											+ mdl.getNoOfInstallment()
											+ ", Monthly Charge : "+ mdl.getMonthlycharge(),id);
							loadOptions(reqMdl.getId());
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}

	
	public void loadToDate(Date date){
		double months=0;
		try {
			months=toDouble(installmentField.getValue().toString());
		} catch (Exception e) {
			e.printStackTrace();
			months=0;
		}
		if(months!=0)
			months-=1;
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, (int)months);
		paymentEndDateField.setNewValue(calendar.getTime());
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadOptions(long id) {
		List list = new ArrayList();
		try {
			list.addAll(reqDao.getAllLoanRequestList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			requestNoCombo.setContainerDataSource(bic);
			requestNoCombo.setItemCaptionPropertyId("requestNo");
			requestNoCombo.setInputPrompt(getPropertyName("select"));
			requestNoCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void calculateMonthlyAmount() {
		double monthlyAmount = 0,charge=0;
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
				monthlyAmount=CommonUtil.roundNumber(loanAmount/installment);
			} catch (Exception e1) {
				monthlyAmount=0.0;
			}
		}
		else
			monthlyAmount=0.0;
		
		monthlyAmountField.setNewValue(CommonUtil.roundNumber(monthlyAmount)+"");
		try {
			charge=toDouble(monthlyChargeField.getValue().trim());
		} catch (Exception e1) {
			charge=0;
		}
		totalMonthlyPaymentField.setNewValue(roundNumber(monthlyAmount+charge)+"");
	}

	
	@Override
	public Boolean isValid() {
		boolean valid = true;
		if (requestNoCombo.getValue() == null || requestNoCombo.getValue().equals("")) {
			setRequiredError(requestNoCombo, getPropertyName("invalid_selection"), true);
			valid = false;
		} else 
			setRequiredError(requestNoCombo, null, false);
		
		if (employeeCombo.getValue() == null || employeeCombo.getValue().equals("")) {
			setRequiredError(employeeCombo, getPropertyName("invalid_selection"), true);
			valid = false;
		} else 
			setRequiredError(employeeCombo, null, false);

		try {
			if (loanAmountField.getValue()<= 0) {
				setRequiredError(loanAmountField,getPropertyName("invalid_data"), true);
				valid = false;
			} else 
				setRequiredError(loanAmountField, null, false);
		} catch (Exception e) {
			setRequiredError(loanAmountField,getPropertyName("invalid_data"), true);
			valid = false;
		}

		try {
			if (toDouble(installmentField.getValue().trim()) <= 0) {
				setRequiredError(installmentField,getPropertyName("invalid_data"), true);
				valid = false;
			} else 
				setRequiredError(installmentField, null, false);
			
		} catch (NumberFormatException e) {
			setRequiredError(installmentField,getPropertyName("invalid_data"), true);
			valid = false;
		}

		try {
			if (toDouble(monthlyAmountField.getValue().trim()) <= 0) {
				setRequiredError(monthlyAmountField,getPropertyName("invalid_data"), true);
				valid = false;
			} else {
				setRequiredError(monthlyAmountField, null, false);
			}
		} catch (NumberFormatException e) {
			setRequiredError(monthlyAmountField,getPropertyName("invalid_data"), true);
			valid = false;
		}
		try {
			if (toDouble(monthlyChargeField.getValue().trim()) < 0) {
				setRequiredError(monthlyChargeField, getPropertyName("invalid_data"), true);
				valid = false;
			} else {
				setRequiredError(monthlyChargeField, null, false);
			}
		} catch (Exception e) {
			setRequiredError(monthlyChargeField,getPropertyName("invalid_data"), true);
			valid = false;
		}
		try {
			if (toDouble(totalMonthlyPaymentField.getValue().trim()) <= 0) {
				setRequiredError(totalMonthlyPaymentField,getPropertyName("invalid_data"), true);
				valid = false;
			} else {
				setRequiredError(totalMonthlyPaymentField, null, false);
			}
		} catch (NumberFormatException e) {
			setRequiredError(totalMonthlyPaymentField,getPropertyName("invalid_data"), true);
			valid = false;
		}
		if (approveOrRejectDateField.getValue() == null || approveOrRejectDateField.getValue().equals("")) {
			setRequiredError(approveOrRejectDateField,getPropertyName("invalid_data"), true);
			valid = false;
		} else 
			setRequiredError(approveOrRejectDateField, null, false);
		
		if (paymentStartDateField.getValue() == null || paymentStartDateField.getValue().equals("")) {
			setRequiredError(paymentStartDateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else 
			setRequiredError(paymentStartDateField, null, false);
		
		if (paymentEndDateField.getValue() == null || paymentEndDateField.getValue().equals("")) {
			setRequiredError(paymentEndDateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else 
			setRequiredError(paymentEndDateField, null, false);
		
		if(requestDateField.getValue()!=null && approveOrRejectDateField.getValue()!=null){
			if(requestDateField.getValue().compareTo(approveOrRejectDateField.getValue())>0){
				setRequiredError(approveOrRejectDateField, getPropertyName("invalid_data"), true);
				valid = false;
			}
			else 
				setRequiredError(approveOrRejectDateField, null, false);
		}
		
		if(paymentStartDateField.getValue()!=null && approveOrRejectDateField.getValue()!=null){
			if(approveOrRejectDateField.getValue().compareTo(paymentStartDateField.getValue())>0){
				setRequiredError(paymentStartDateField, getPropertyName("invalid_data"), true);
				valid = false;
			}
			else 
				setRequiredError(paymentStartDateField, null, false);
		}
		
		if (approvedByCombo.getValue() == null || approvedByCombo.getValue().equals("")) {
			setRequiredError(approvedByCombo,getPropertyName("invalid_selection"), true);
			valid = false;
		} 
		else if(approvedByCombo.getValue()!=null && employeeCombo.getValue()!=null){
			if (approvedByCombo.getValue().toString().trim().equalsIgnoreCase(employeeCombo.getValue().toString().trim())) {
				setRequiredError(approvedByCombo,getPropertyName("invalid_selection"), true);
				valid = false;
			}
			else
				setRequiredError(approvedByCombo, null, false);
		}
		else {
			setRequiredError(approvedByCombo, null, false);
		}
		
		return valid;
	}

	
	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
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
