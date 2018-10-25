package com.inventory.process.biz;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.process.dao.EndProcessDao;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.subscription.ui.SubscriptionPayment;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;

public class SubscriptionPaymentProcess implements EndProcessInterface {

	@Override
	public void process() {
		try{
			WrappedSession session =new SessionUtil().getHttpSession();
					System.out.println("Day End Process Started");
					List subscriptionList =new SubscriptionInDao().getPendingSubscription((Long) session.getAttribute("office_id"));
					System.out.println("No of Active Subscriptions "+subscriptionList.size());
					SettingsValuePojo settings = null;
					if (session.getAttribute("settings") != null)
						settings = (SettingsValuePojo) session.getAttribute("settings");
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
							simdl=(SubscriptionInModel)subitr.next();
							if(simdl!=null){
								period=option.getPeriod(simdl.getId());
								issueDate=option.findIssueDate(simdl.getId());
								diffDays=option.calculateDifferenceInDays(simdl.getId());
								rate=simdl.getRate();
								amountDue=option.calculateSubscriptionAmountDue(diffDays, rate, period);
//								System.out.println("Amount Due "+amountDue);
								accountType=simdl.getAccount_type();
								subscriber=simdl.getSubscriber();
								long inout=0;
								long rentType=new SubscriptionInDao().getRentType(simdl.getId(), simdl.getSubscription().getSubscription_type().getOfficeId());
								long type=new SubscriptionInDao().getAvailablilty(simdl.getId(), rentType);
								if(accountType==1){
									fromAccountId=subscriber;
//									toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
									inout=1;
									creditDebit=SConstants.DR;
								}
								else if(accountType==2){
//									fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
									toAccountId=subscriber;
									inout=0;
									creditDebit=SConstants.CR;
								}
								else if(accountType==3){
									if(rentType==1){
										fromAccountId=subscriber;
//										toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
										inout=1;
										creditDebit=SConstants.DR;
									}
									else if(rentType==2){
//										fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
										toAccountId=subscriber;
										inout=0;
										creditDebit=SConstants.CR;
									}
									else{
										if(type==1){
											fromAccountId=subscriber;
//											toAccountId=settings.getCASH_PAYABLE_ACCOUNT();
											inout=1;
											creditDebit=SConstants.DR;
										}
										else if(type==2) {
//											fromAccountId=settings.getCASH_RECEIVABLE_ACCOUNT();
											toAccountId=subscriber;
											inout=0;
											creditDebit=SConstants.CR;
										}
									}
								}
								if(amountDue>0){
									SubscriptionPaymentModel mdl=new SubscriptionPaymentModel();
									TransactionModel transaction=null;
									FinTransaction trans=new FinTransaction();
									System.out.println("Subscription In Model id "+simdl.getId());
									mdl.setSubscription(new SubscriptionInModel(simdl.getId()));
									mdl.setPayment_date((Date)session.getAttribute("working_date"));
									mdl.setSubscription_date(CommonUtil.getSQLDateFromUtilDate(simdl.getSubscription_date()));
									mdl.setCash_cheque(1);
									mdl.setFrom_account(fromAccountId);
									mdl.setTo_account(toAccountId);
									mdl.setCheque_date((Date)session.getAttribute("working_date"));
									mdl.setCheque_number("");
									mdl.setType(inout);
									mdl.setPay_credit(1);
									mdl.setAmount_paid(amountDue);
									mdl.setAmount_due(amountDue);
									trans.addTransaction(creditDebit, fromAccountId, toAccountId, amountDue);
									new EndProcessDao().updateSubscriptionDue(mdl,
																				trans.getTransaction(SConstants.SUBSCRIPTION_PAYMENTS,
																				(Date)session.getAttribute("working_date")));
								}
								else
									System.out.println("No Payment Pending");	
							}
						}
					}
					System.out.println("Day End Process Ended");
//			new EndProcessDao().updateSubscriptionDue((Long) session.getAttribute("office_id"), 
//					(Date)session.getAttribute("working_date"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
