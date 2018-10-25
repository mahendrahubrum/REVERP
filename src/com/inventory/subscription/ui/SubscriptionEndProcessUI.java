package com.inventory.subscription.ui;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.process.dao.EndProcessDao;
import com.inventory.subscription.dao.SubscriptionEndProcessDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.biz.FinTransaction;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 26, 2013
 */
public class SubscriptionEndProcessUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;
	
	SettingsValuePojo settings;

	WrappedSession session;
	
	private SButton processButton;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	java.util.Date date;
	OfficeDao ofcDao;
	SubscriptionEndProcessDao dao;

	@Override
	public SPanel getGUI() {

		setSize(340, 180);
		session = getHttpSession();
		date=new java.util.Date();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		ofcDao = new OfficeDao();
		dao = new SubscriptionEndProcessDao();

		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		formLayout = new SFormLayout();
		formLayout.setMargin(true);
		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		try {
			organizationSelect = new SComboField(getPropertyName("organization"), 200,new OrganizationDao().getAllOrganizations(), "id", "name");
			officeSelect = new SComboField(getPropertyName("office"), 200,null, "id", "name");
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
		processButton = new SButton(getPropertyName("rental_day_end"));
		buttonLayout.addComponent(processButton);
		formLayout.addComponent(organizationSelect);
		formLayout.addComponent(officeSelect);
		formLayout.addComponent(buttonLayout);
		organizationSelect.addValueChangeListener(new Property.ValueChangeListener() {
			@SuppressWarnings("unchecked")
			public void valueChange(ValueChangeEvent event) {
				SCollectionContainer bic = null;
				try {
					List lst = new ArrayList();
					lst.add(new S_OfficeModel(0, "ALL"));
					lst.addAll(ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect.getValue()));
					bic = SCollectionContainer.setList(lst, "id");
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				officeSelect.setContainerDataSource(bic);
				officeSelect.setItemCaptionPropertyId("name");
			}
		});

		officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {
					if ((Long) officeSelect.getValue() != 0) {
						S_OfficeModel ofc = ofcDao.getOffice((Long) officeSelect.getValue());
						if (ofc.getWorkingDate().toString().equals(getFinEndDate().toString())) {
							processButton.setCaption("Year End Process");
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		processButton.addClickListener(new ClickListener() {
			@SuppressWarnings("serial")
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {
						ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									@SuppressWarnings({ "unchecked", "rawtypes" })
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												List ofcList = new ArrayList();
												if ((Long) officeSelect.getValue() != 0) {
													ofcList.add(ofcDao.getOffice((Long) officeSelect.getValue()));
												} 
												else {
													ofcList.addAll(ofcDao.getAllOfficesUnderOrg((Long) organizationSelect.getValue()));
												}

												if (ofcList.size() > 0) {
													S_OfficeModel ofc;
													boolean isMonthEnd = false, isYearEnd = false;
													Iterator it = ofcList.iterator();
													while (it.hasNext()) {
														ofc = (S_OfficeModel) it.next();
														isMonthEnd = false;
														isYearEnd = false;
														if (ofc.getWorkingDate().equals(ofc.getFin_end_date())) {
															isYearEnd = true;
														}
														getHttpSession().setAttribute("dayend_office_id", ofc.getId());
														try{
															Calendar cal=Calendar.getInstance();
															System.out.println("Day End Process Started");
															List subscriptionList =new SubscriptionInDao().getPendingSubscription((Long)officeSelect.getValue());
															System.out.println("No of Active Subscriptions "+subscriptionList.size());
															SubscriptionInModel simdl=null;
															long period=0;
															long accountType=0;
															long fromAccountId=0,toAccountId=0,subscriber = 0;
															int diffDays=0;
															int creditDebit=0;
															java.util.Date issueDate=null;
															List<SubscriptionPaymentModel> paymentList=new ArrayList<SubscriptionPaymentModel>();
															double rate=0,amountDue=0,previousDue=0,previousPaid=0;
															if(subscriptionList.size()>0){
																Iterator subitr=subscriptionList.iterator();
																SubscriptionPayment option = new SubscriptionPayment();
																while(subitr.hasNext()){
																	simdl=(SubscriptionInModel)subitr.next();// Individual Subscriptions
																	if(simdl!=null){
																		cal.setTime(simdl.getSubscription_date());
																		period=option.getPeriod(simdl.getId());
																		issueDate=option.findIssueDate(simdl.getId());
																		diffDays=option.calculateDifferenceInDays(simdl.getId());
																		if(diffDays>0){
																			SubscriptionPaymentModel mdl=dao.getLastCreditPayment(simdl);
																			if(mdl!=null){
																				cal.setTime(mdl.getPayment_date());
//																				System.out.println("Payment is Present "+mdl.getPayment_date().toString());
																				int diff=(int)((date.getTime()-mdl.getPayment_date().getTime())/(24*60*60*1000));
																				if(diff>0){
																					System.out.println("No Of Days between last payment date and today ....."+diff);
																					for(int i=0;i<=diff;i++){
																						rate=simdl.getRate();
																						amountDue=option.calculateSubscriptionAmountDue(diffDays, rate, period);
//																							System.out.println("Amount Due "+amountDue);
																						accountType=simdl.getAccount_type();
																						subscriber=simdl.getSubscriber();
																						long inout=0;
																						long rentType=new SubscriptionInDao().getRentType(simdl.getId(), simdl.getSubscription().getSubscription_type().getOfficeId());
																						long type=new SubscriptionInDao().getAvailablilty(simdl.getId(), rentType);
																						if(accountType==1){
																							fromAccountId=subscriber;
//																							toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
																							inout=1;
																							creditDebit=SConstants.DR;
																						}
																						else if(accountType==2){
//																							fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
																							toAccountId=subscriber;
																							inout=0;
																							creditDebit=SConstants.CR;
																						}
																						else if(accountType==3){
																							if(rentType==1){
																								fromAccountId=subscriber;
//																								toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
																								inout=1;
																								creditDebit=SConstants.DR;
																							}
																							else if(rentType==2){
//																								fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
																								toAccountId=subscriber;
																								inout=0;
																								creditDebit=SConstants.CR;
																							}
																							else{
																								if(type==1){
																									fromAccountId=subscriber;
//																									toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
																									inout=1;
																									creditDebit=SConstants.DR;
																								}
																								else if(type==2) {
//																									fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
																									toAccountId=subscriber;
																									inout=0;
																									creditDebit=SConstants.CR;
																								}
																							}
																						}
																						if(amountDue>0){
																							SubscriptionPaymentModel pmdl=new SubscriptionPaymentModel();
																							FinTransaction trans=new FinTransaction();
																							pmdl.setSubscription(new SubscriptionInModel(simdl.getId()));
																							pmdl.setPayment_date(CommonUtil.getSQLDateFromUtilDate(cal.getTime()));
																							pmdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(simdl.getSubscription_date()));
																							pmdl.setCash_cheque(1);
																							pmdl.setFrom_account(fromAccountId);
																							pmdl.setTo_account(toAccountId);
																							pmdl.setCheque_date((Date)session.getAttribute("working_date"));
																							pmdl.setCheque_number("");
																							pmdl.setType(inout);
																							pmdl.setPay_credit(1);
																							pmdl.setAmount_paid(amountDue);
																							pmdl.setAmount_due(amountDue);
																							trans.addTransaction(creditDebit, fromAccountId, toAccountId, amountDue);
																							dao.updateSubscriptionDue(pmdl,trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																																				CommonUtil.getSQLDateFromUtilDate(cal.getTime())));
																							cal.add(Calendar.DATE, 1);
																						}
																						else
																							System.out.println("No Payment Pending");	
																					}
																				}
																				else{
																					System.out.println("All Credit Payments Done");
																				}
																			}
																			else{
																				int diff=(int)((date.getTime()-simdl.getSubscription_date().getTime())/(24*60*60*1000));
																				if(diff>0){
																					System.out.println("No Of Days where no credit payment done ....."+diff);
																					for(int i=0;i<=diff;i++){
//																						System.out.println("Payment is not Present date is "+cal.getTime().toString());
																						rate=simdl.getRate();
																						amountDue=option.calculateSubscriptionAmountDue(diffDays, rate, period);
//																						System.out.println("Amount Due "+amountDue);
																						accountType=simdl.getAccount_type();
																						subscriber=simdl.getSubscriber();
																						long inout=0;
																						long rentType=new SubscriptionInDao().getRentType(simdl.getId(), simdl.getSubscription().getSubscription_type().getOfficeId());
																						long type=new SubscriptionInDao().getAvailablilty(simdl.getId(), rentType);
																						if(accountType==1){
																							fromAccountId=subscriber;
//																							toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
																							inout=1;
																							creditDebit=SConstants.DR;
																						}
																						else if(accountType==2){
//																							fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
																							toAccountId=subscriber;
																							inout=0;
																							creditDebit=SConstants.CR;
																						}
																						else if(accountType==3){
																							if(rentType==1){
																								fromAccountId=subscriber;
//																								toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
																								inout=1;
																								creditDebit=SConstants.DR;
																							}
																							else if(rentType==2){
//																								fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
																								toAccountId=subscriber;
																								inout=0;
																								creditDebit=SConstants.CR;
																							}
																							else{
																								if(type==1){
																									fromAccountId=subscriber;
//																									toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
																									inout=1;
																									creditDebit=SConstants.DR;
																								}
																								else if(type==2) {
//																									fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
																									toAccountId=subscriber;
																									inout=0;
																									creditDebit=SConstants.CR;
																								}
																							}
																						}
																						if(amountDue>0){
																							SubscriptionPaymentModel pmdl=new SubscriptionPaymentModel();
																							FinTransaction trans=new FinTransaction();
																							pmdl.setSubscription(new SubscriptionInModel(simdl.getId()));
																							pmdl.setPayment_date(CommonUtil.getSQLDateFromUtilDate(cal.getTime()));
																							pmdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(simdl.getSubscription_date()));
																							pmdl.setCash_cheque(1);
																							pmdl.setFrom_account(fromAccountId);
																							pmdl.setTo_account(toAccountId);
																							pmdl.setCheque_date((Date)session.getAttribute("working_date"));
																							pmdl.setCheque_number("");
																							pmdl.setType(inout);
																							pmdl.setPay_credit(1);
																							pmdl.setAmount_paid(amountDue);
																							pmdl.setAmount_due(amountDue);
																							trans.addTransaction(creditDebit, fromAccountId, toAccountId, amountDue);
																							dao.updateSubscriptionDue(pmdl,trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																																				CommonUtil.getSQLDateFromUtilDate(cal.getTime())));
																							cal.add(Calendar.DATE, 1);
																						}
																						else
																							System.out.println("No Payment Pending");	
																					}
																				}
																				else{
																					System.out.println("All Credit Payments Done");
																				}
																			}
																		}
																	}
																}// Individual Subscriptions End
															}
															System.out.println("Day End Process Ended");
														}
														catch(Exception e){
															e.printStackTrace();
														}
													}
													Notification.show(getPropertyName("success"),Type.WARNING_MESSAGE);
												} 
												else {
													Notification.show(getPropertyName("no_office_found"),Type.WARNING_MESSAGE);
												}

											} catch (Exception e) {
												Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		mainPanel.setContent(formLayout);
		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());
		return mainPanel;
	}
	@SuppressWarnings("unchecked")
	public void deleteOldFiles(File file,int olderThanDate) {
		Calendar cal=getCalendar();
		cal.add(cal.DAY_OF_MONTH, -olderThanDate);
	    Iterator<File> filesToDelete = org.apache.commons.io.FileUtils.iterateFiles(file, new AgeFileFilter(cal.getTime()), null);
	    while (filesToDelete.hasNext()) {
			File deleteFile = (File) filesToDelete.next();
			deleteFile.delete();
	    }
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
