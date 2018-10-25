package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.subscription.bean.ConsolidatedSubscriptionLedgerReportBean;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ConsolidatedSubscriptionLedgerReportDao extends SHibernate implements Serializable{

	 @SuppressWarnings({ "rawtypes", "unchecked" })
	public List getConsolidatedLedgerReport(long account,long office,Date start,Date end)throws Exception{
		 List resultList=new ArrayList();
		 LedgerModel ledger=null;
		 Iterator actitr=null;
		 long id=0;
		 try {
			 begin();
			 if(account==2) {
				 actitr=getSession().createQuery("select ledger.id from CustomerModel where subscription=1 order by name").list().iterator();
			 }
			 else if(account==3){
				 actitr=getSession().createQuery("select ledger.id from TranspotationModel where subscription=1 order by name").list().iterator();
			 }
			 while (actitr.hasNext()) {
				 id=(Long)actitr.next();
				 ledger=(LedgerModel)getSession().get(LedgerModel.class, id);
				 double cash=0,credit=0,balance=0,ocash=0,ocredit=0,opening=0,ccash=0,ccredit=0,current=0;
				 if(account==2) {
					 Object caObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date " +
					 		"between :start and :end and pay_credit=0 and from_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
					 		.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
					 if(caObj!=null)
						 cash=(Double)caObj;
					 
					 Object crObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date " +
						 		"between :start and :end and pay_credit=1 and to_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
					 if(crObj!=null)
						 credit=(Double)crObj;

					 List list=getSession().createQuery("from SubscriptionPaymentModel where payment_date " +
						 		"between :start and :end and pay_credit=0 and from_account=:ledger and credit_transaction!=0 and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("ledger", ledger.getId()).list();
					 if(list.size()>0) {
						 Iterator it=list.iterator();
						 SubscriptionPaymentModel spmdl=null;
						 while (it.hasNext()) {
							spmdl=(SubscriptionPaymentModel)it.next();
							TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
							List transList=tran.getTransaction_details_list();
							Iterator tritr=transList.iterator();
							while (tritr.hasNext()) {
								TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
								credit+=det.getAmount();
							}
						}
					 }
					 balance=cash-credit;
					 // Opening Balance
					 
					 Object ocaObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
					 		"payment_date <:start and pay_credit=0 and from_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", start).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
						 if(ocaObj!=null)
							 ocash=(Double)ocaObj;
						 
					 Object ocrObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
					 		"payment_date <:start and pay_credit=1 and to_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", start).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
					 if(ocrObj!=null)
						 ocredit=(Double)ocrObj;
					 
					 List olist=getSession().createQuery("from SubscriptionPaymentModel where payment_date <:start and pay_credit=0 " +
					 		"and from_account=:ledger and credit_transaction!=0 and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", start).setParameter("office", office).setParameter("ledger", ledger.getId()).list();
					 if(olist.size()>0) {
						 Iterator it=olist.iterator();
						 SubscriptionPaymentModel spmdl=null;
						 while (it.hasNext()) {
							spmdl=(SubscriptionPaymentModel)it.next();
							TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
							List transList=tran.getTransaction_details_list();
							Iterator tritr=transList.iterator();
							while (tritr.hasNext()) {
								TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
								ocredit+=det.getAmount();
							}
						}
					 }
					 opening=ocash-ocredit;
					 
					 
					 // Current Balance
					 
					 Object ccaObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
					 		"payment_date <:start and pay_credit=0 and from_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", CommonUtil.getCurrentSQLDate()).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
						 if(ccaObj!=null)
							 ccash=(Double)ccaObj;
						 
					 Object ccrObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
					 		"payment_date <:start and pay_credit=1 and to_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", CommonUtil.getCurrentSQLDate()).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
					 if(ccrObj!=null)
						 ccredit=(Double)ccrObj;
					 
					 List clist=getSession().createQuery("from SubscriptionPaymentModel where payment_date <:start and pay_credit=0 " +
					 		"and from_account=:ledger and credit_transaction!=0 and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=2")
						 		.setParameter("start", CommonUtil.getCurrentSQLDate()).setParameter("office", office).setParameter("ledger", ledger.getId()).list();
					 if(clist.size()>0) {
						 Iterator it=clist.iterator();
						 SubscriptionPaymentModel spmdl=null;
						 while (it.hasNext()) {
							spmdl=(SubscriptionPaymentModel)it.next();
							TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
							List transList=tran.getTransaction_details_list();
							Iterator tritr=transList.iterator();
							while (tritr.hasNext()) {
								TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
								ccredit+=det.getAmount();
							}
						}
					 }
					 current=ccash-ccredit;
					 
					 resultList.add(new ConsolidatedSubscriptionLedgerReportBean(ledger.getId(),ledger.getName(), opening, cash, credit,(opening-balance), current));
				 }
				 else if(account==3){// Transaportation
					 
					 Object caObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date " +
						 		"between :start and :end and pay_credit=0 and to_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
						 		.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
						 if(caObj!=null)
							 cash=(Double)caObj;
						 
						 Object crObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where payment_date " +
							 		"between :start and :end and pay_credit=1 and from_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
						 if(crObj!=null)
							 credit=(Double)crObj;

						 List list=getSession().createQuery("from SubscriptionPaymentModel where payment_date " +
							 		"between :start and :end and pay_credit=0 and to_account=:ledger and credit_transaction!=0 and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", start).setParameter("end", end).setParameter("office", office).setParameter("ledger", ledger.getId()).list();
						 if(list.size()>0) {
							 Iterator it=list.iterator();
							 SubscriptionPaymentModel spmdl=null;
							 while (it.hasNext()) {
								spmdl=(SubscriptionPaymentModel)it.next();
								TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
								List transList=tran.getTransaction_details_list();
								Iterator tritr=transList.iterator();
								while (tritr.hasNext()) {
									TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
									credit+=det.getAmount();
								}
							}
						 }
						 balance=cash-credit;
						 // Opening Balance
						 
						 Object ocaObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
						 		"payment_date <:start and pay_credit=0 and to_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", start).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
							 if(ocaObj!=null)
								 ocash=(Double)ocaObj;
							 
						 Object ocrObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
						 		"payment_date <:start and pay_credit=1 and from_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", start).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
						 if(ocrObj!=null)
							 ocredit=(Double)ocrObj;
						 
						 List olist=getSession().createQuery("from SubscriptionPaymentModel where payment_date <:start and pay_credit=0 " +
						 		"and to_account=:ledger and credit_transaction!=0 and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", start).setParameter("office", office).setParameter("ledger", ledger.getId()).list();
						 if(olist.size()>0) {
							 Iterator it=olist.iterator();
							 SubscriptionPaymentModel spmdl=null;
							 while (it.hasNext()) {
								spmdl=(SubscriptionPaymentModel)it.next();
								TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
								List transList=tran.getTransaction_details_list();
								Iterator tritr=transList.iterator();
								while (tritr.hasNext()) {
									TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
									ocredit+=det.getAmount();
								}
							}
						 }
						 opening=ocash-ocredit;
						 
						 
						 // Current Balance
						 
						 Object ccaObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
						 		"payment_date <:start and pay_credit=0 and to_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", CommonUtil.getCurrentSQLDate()).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
							 if(ccaObj!=null)
								 ccash=(Double)ccaObj;
							 
						 Object ccrObj=getSession().createQuery("select coalesce(sum(amount_paid),0) from SubscriptionPaymentModel where " +
						 		"payment_date <:start and pay_credit=1 and from_account=:ledger and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", CommonUtil.getCurrentSQLDate()).setParameter("office", office).setParameter("ledger", ledger.getId()).uniqueResult();
						 if(ccrObj!=null)
							 ccredit=(Double)ccrObj;
						 
						 List clist=getSession().createQuery("from SubscriptionPaymentModel where payment_date <:start and pay_credit=0 " +
						 		"and to_account=:ledger and credit_transaction!=0 and subscription.subscription.subscription_type.officeId=:office and subscription.account_type=3")
							 		.setParameter("start", CommonUtil.getCurrentSQLDate()).setParameter("office", office).setParameter("ledger", ledger.getId()).list();
						 if(clist.size()>0) {
							 Iterator it=clist.iterator();
							 SubscriptionPaymentModel spmdl=null;
							 while (it.hasNext()) {
								spmdl=(SubscriptionPaymentModel)it.next();
								TransactionModel tran=(TransactionModel)getSession().get(TransactionModel.class, spmdl.getTransaction_id());
								List transList=tran.getTransaction_details_list();
								Iterator tritr=transList.iterator();
								while (tritr.hasNext()) {
									TransactionDetailsModel det=(TransactionDetailsModel)tritr.next();
									ccredit+=det.getAmount();
								}
							}
						 }
						 current=ccash-ccredit;
						 
						 resultList.add(new ConsolidatedSubscriptionLedgerReportBean(ledger.getId(),ledger.getName(), opening, cash, credit, (opening-balance), current));
				 }
			}
			 commit();
		 }
		 catch(Exception e) {
			 rollback();
			 close();
			 e.printStackTrace();
			 throw e;
		 }
		 finally {
			 flush();
			 close();
		 }
		 return resultList;
	 }
	
}
