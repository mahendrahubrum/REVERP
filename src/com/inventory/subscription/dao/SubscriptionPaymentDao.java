package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentDetailsModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

public class SubscriptionPaymentDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -3938038134654204399L;

	public long save(SubscriptionPaymentModel paymentModel, TransactionModel transaction,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check) throws Exception {
		try {
			begin();
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			getSession().save(transaction);
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			paymentModel.setTransaction_id(transaction.getTransaction_id());
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().save(paymentModel);
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst).executeUpdate();
					}
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return paymentModel.getId();
	}
	
	public long savePaymentCredit(SubscriptionPaymentModel paymentModel, TransactionModel transaction,TransactionModel credit,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check) throws Exception {
		try {
			begin();
			double amount=0;
			
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			getSession().save(transaction);
			getSession().save(credit);
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			
			SubscriptionPaymentModel paymdl=new SubscriptionPaymentModel();
			Iterator<TransactionDetailsModel> citr = credit.getTransaction_details_list().iterator();
			TransactionDetailsModel ct;
			while (citr.hasNext()) {
				ct = citr.next();
				amount+=ct.getAmount();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getToAcct().getId()).executeUpdate();
				paymdl.setFrom_account(ct.getFromAcct().getId());
				paymdl.setTo_account(ct.getToAcct().getId());
				flush();
			}
			paymentModel.setTransaction_id(transaction.getTransaction_id());
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().save(paymentModel);
			flush();
			long sid=(Long)getSession().createQuery("select subscription.id from SubscriptionPaymentModel where id="+paymentModel.getId()).uniqueResult();
			paymdl.setSubscription(new SubscriptionInModel(sid));
			paymdl.setPayment_date(paymentModel.getPayment_date());
			paymdl.setSubscription_date(paymentModel.getSubscription_date());
			paymdl.setCash_cheque(paymentModel.getCash_cheque());
			
			paymdl.setCheque_date(paymentModel.getCheque_date());
			paymdl.setCheque_number(paymentModel.getCheque_number());
			paymdl.setType(paymentModel.getType());
			paymdl.setPay_credit((long)1);
			paymdl.setAmount_due(amount);
			paymdl.setAmount_paid(amount);
			paymdl.setSpecial_credit_payment(paymentModel.getId());
			paymdl.setTransaction_id(credit.getTransaction_id());
			getSession().save(paymdl);
			
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst).executeUpdate();
					}
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return paymentModel.getId();
	}
	
	public long save(SubscriptionPaymentModel paymentModel, TransactionModel transaction, TransactionModel trans,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check) throws Exception {
		try {
			begin();
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			getSession().save(transaction);
			getSession().save(trans);
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			Iterator<TransactionDetailsModel> acitr = trans.getTransaction_details_list().iterator();
			TransactionDetailsModel tdm;
			while (acitr.hasNext()) {
				tdm = acitr.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getToAcct().getId()).executeUpdate();
				flush();
			}
			paymentModel.setTransaction_id(transaction.getTransaction_id());
			paymentModel.setCredit_transaction(trans.getTransaction_id());
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().save(paymentModel);
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst).executeUpdate();
					}
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return paymentModel.getId();
	}
	
	public long savePaymentCredit(SubscriptionPaymentModel paymentModel, TransactionModel transaction, TransactionModel trans,TransactionModel credit,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check) throws Exception {
		try {
			begin();
			double amount=0;
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			getSession().save(transaction);
			getSession().save(trans);
			getSession().save(credit);
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			Iterator<TransactionDetailsModel> acitr = trans.getTransaction_details_list().iterator();
			TransactionDetailsModel tdm;
			while (acitr.hasNext()) {
				tdm = acitr.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			SubscriptionPaymentModel paymdl=new SubscriptionPaymentModel();
			Iterator<TransactionDetailsModel> citr = credit.getTransaction_details_list().iterator();
			TransactionDetailsModel ct;
			while (citr.hasNext()) {
				ct = citr.next();
				amount+=ct.getAmount();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getToAcct().getId()).executeUpdate();
				paymdl.setFrom_account(ct.getFromAcct().getId());
				paymdl.setTo_account(ct.getToAcct().getId());
				flush();
			}
			
			paymentModel.setTransaction_id(transaction.getTransaction_id());
			paymentModel.setCredit_transaction(trans.getTransaction_id());
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().save(paymentModel);
			flush();
			long sid=(Long)getSession().createQuery("select subscription.id from SubscriptionPaymentModel where id="+paymentModel.getId()).uniqueResult();
			paymdl.setSubscription(new SubscriptionInModel(sid));
			paymdl.setPayment_date(paymentModel.getPayment_date());
			paymdl.setSubscription_date(paymentModel.getSubscription_date());
			paymdl.setCash_cheque(paymentModel.getCash_cheque());
			paymdl.setCheque_date(paymentModel.getCheque_date());
			paymdl.setCheque_number(paymentModel.getCheque_number());
			paymdl.setType(paymentModel.getType());
			paymdl.setPay_credit((long)1);
			paymdl.setAmount_due(amount);
			paymdl.setAmount_paid(amount);
			paymdl.setSpecial_credit_payment(paymentModel.getId());
			paymdl.setTransaction_id(credit.getTransaction_id());
			getSession().save(paymdl);
			
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst).executeUpdate();
					}
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return paymentModel.getId();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(SubscriptionPaymentModel paymentModel, TransactionModel transaction,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check)
			throws Exception {
			
		try {
			begin();
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			
			List old_AcctnotDeletedLst = new ArrayList();
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(tr.getId());
			}

			getSession().update(transaction);
			flush();
			Iterator<TransactionDetailsModel> iter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tdr;
			while (iter.hasNext()) {
				tdr = iter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getToAcct().getId()).executeUpdate();

				flush();

			}
			
			getSession().createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
							.executeUpdate();
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst)
						.executeUpdate();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().update(paymentModel);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updatePaymentCredit(SubscriptionPaymentModel paymentModel, TransactionModel transaction,TransactionModel credit,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check)
			throws Exception {
			
		try {
			begin();
			SubscriptionPaymentModel paymdl=(SubscriptionPaymentModel)getSession().createQuery("from SubscriptionPaymentModel where special_credit_payment="+paymentModel.getId()).uniqueResult();
			double amount=0;
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			
			List old_AcctnotDeletedLst = new ArrayList();
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			
			List ctransList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + credit.getTransaction_id())
					.list();
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(tr.getId());
			}

			getSession().update(transaction);
			flush();
			
			Iterator<TransactionDetailsModel> ctr = ctransList.iterator();
			TransactionDetailsModel ct;
			while (ctr.hasNext()) {
				ct = ctr.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(ct.getId());
			}

			getSession().update(credit);
			flush();
			
			Iterator<TransactionDetailsModel> iter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tdr;
			while (iter.hasNext()) {
				tdr = iter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getToAcct().getId()).executeUpdate();

				flush();

			}
			
			Iterator<TransactionDetailsModel> citr = credit.getTransaction_details_list().iterator();
			TransactionDetailsModel ctm;
			while (citr.hasNext()) {
				ctm = citr.next();
				amount+=ctm.getAmount();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", ctm.getAmount())
						.setLong("id", ctm.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", ctm.getAmount())
						.setLong("id", ctm.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			getSession().createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
							.executeUpdate();
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst)
						.executeUpdate();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().update(paymentModel);
			paymdl.setSpecial_credit_payment(paymentModel.getId());
			paymdl.setPay_credit((long)1);
			paymdl.setAmount_paid(amount);
			getSession().update(paymdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(SubscriptionPaymentModel paymentModel, TransactionModel transaction,TransactionModel trans,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check)
			throws Exception {
			
		try {
			begin();
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			
			List old_AcctnotDeletedLst = new ArrayList();
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			List ftransList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + trans.getTransaction_id())
					.list();
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(tr.getId());
			}
			getSession().update(transaction);
			flush();
			
			Iterator<TransactionDetailsModel> acitr = ftransList.iterator();
			TransactionDetailsModel tdm;
			while (acitr.hasNext()) {
				tdm = acitr.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(tdm.getId());
			}

			getSession().update(trans);
			flush();
			Iterator<TransactionDetailsModel> iter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tdr;
			while (iter.hasNext()) {
				tdr = iter.next();
				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getToAcct().getId()).executeUpdate();

				flush();

			}
			
			Iterator<TransactionDetailsModel> itr = trans.getTransaction_details_list().iterator();
			TransactionDetailsModel td;
			while (itr.hasNext()) {
				td = itr.next();
				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", td.getAmount())
						.setLong("id", td.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", td.getAmount())
						.setLong("id", td.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			getSession().createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
							.executeUpdate();
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst)
						.executeUpdate();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}	
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().update(paymentModel);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updatePaymentCredit(SubscriptionPaymentModel paymentModel, TransactionModel transaction,TransactionModel trans,TransactionModel credit,SubscriptionInModel simdl,SubscriptionCreationModel scmdl,boolean check)
			throws Exception {
			
		try {
			begin();
			double amount=0;
			SubscriptionPaymentModel paymdl=(SubscriptionPaymentModel)getSession().createQuery("from SubscriptionPaymentModel where special_credit_payment="+paymentModel.getId()).uniqueResult();
			List old_notDeletedLst = null;
			if(check){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
				
				SubscriptionPaymentDetailsModel detmdl;
				if(paymentModel.getSubscription_payment_list()!=null && paymentModel.getSubscription_payment_list().size()!=0){
					Iterator itr=paymentModel.getSubscription_payment_list().iterator();
					while(itr.hasNext()){
						detmdl=(SubscriptionPaymentDetailsModel)itr.next();
						if(old_notDeletedLst.contains(detmdl.getId())){
							old_notDeletedLst.remove(detmdl.getId());
						}
					}
				}
			}
			
			List old_AcctnotDeletedLst = new ArrayList();
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			List ftransList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + trans.getTransaction_id())
					.list();
			
			List ctransList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + credit.getTransaction_id())
					.list();
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(tr.getId());
			}
			getSession().update(transaction);
			flush();
			
			Iterator<TransactionDetailsModel> acitr = ftransList.iterator();
			TransactionDetailsModel tdm;
			while (acitr.hasNext()) {
				tdm = acitr.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(tdm.getId());
			}

			getSession().update(trans);
			flush();
			
			Iterator<TransactionDetailsModel> ctr = ctransList.iterator();
			TransactionDetailsModel ct;
			while (ctr.hasNext()) {
				ct = ctr.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", ct.getAmount())
						.setLong("id", ct.getToAcct().getId()).executeUpdate();

				flush();
				old_AcctnotDeletedLst.add(ct.getId());
			}

			getSession().update(credit);
			flush();
			
			Iterator<TransactionDetailsModel> iter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tdr;
			while (iter.hasNext()) {
				tdr = iter.next();
				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdr.getAmount())
						.setLong("id", tdr.getToAcct().getId()).executeUpdate();

				flush();

			}
			
			Iterator<TransactionDetailsModel> itr = trans.getTransaction_details_list().iterator();
			TransactionDetailsModel td;
			while (itr.hasNext()) {
				td = itr.next();
				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", td.getAmount())
						.setLong("id", td.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", td.getAmount())
						.setLong("id", td.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			Iterator<TransactionDetailsModel> citr = credit.getTransaction_details_list().iterator();
			TransactionDetailsModel ctm;
			while (citr.hasNext()) {
				ctm = citr.next();
				amount+=ctm.getAmount();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", ctm.getAmount())
						.setLong("id", ctm.getFromAcct().getId())
						.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", ctm.getAmount())
						.setLong("id", ctm.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			getSession().createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
							.executeUpdate();
			if(check){
				try {
					if(old_notDeletedLst.size()>0){
						getSession().createQuery("delete from SubscriptionPaymentDetailsModel where id in (:lst)")
						.setParameterList("lst", old_notDeletedLst)
						.executeUpdate();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}	
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().update(paymentModel);
			paymdl.setSpecial_credit_payment(paymentModel.getId());
			paymdl.setPay_credit((long)1);
			paymdl.setAmount_paid(amount);
			getSession().update(paymdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	public void deleteSupplierPayment(long paymentId,SubscriptionInModel simdl,SubscriptionCreationModel scmdl) throws Exception {
		try {
			begin();
			List old_notDeletedLst=null;
			SubscriptionPaymentModel paymentModel = (SubscriptionPaymentModel) getSession().get(SubscriptionPaymentModel.class, paymentId);
			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, paymentModel.getTransaction_id());
			if(paymentModel.getCredit_transaction()!=0){
				TransactionModel trans = (TransactionModel) getSession().get(TransactionModel.class, paymentModel.getCredit_transaction());
				Iterator<TransactionDetailsModel> acitr = trans.getTransaction_details_list().iterator();
				TransactionDetailsModel tdm;
				while (acitr.hasNext()) {
					tdm = acitr.next();

					getSession()
							.createQuery(
									"update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", tdm.getAmount())
							.setLong("id", tdm.getFromAcct().getId())
							.executeUpdate();

					getSession()
							.createQuery(
									"update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", tdm.getAmount())
							.setLong("id", tdm.getToAcct().getId()).executeUpdate();

					flush();
				}
				getSession().delete(trans);
				flush();
			}
			
			Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			flush();
			
			List list=getSession().createQuery("from SubscriptionPaymentModel where special_credit_payment="+paymentId).list();
			if(list.size()>0) {
				SubscriptionPaymentModel paymdl=null;
				Iterator it=list.iterator();
				while (it.hasNext()) {
					paymdl=(SubscriptionPaymentModel)it.next();
					TransactionModel trans = (TransactionModel) getSession().get(TransactionModel.class, paymdl.getTransaction_id());
					Iterator<TransactionDetailsModel> acitr = trans.getTransaction_details_list().iterator();
					TransactionDetailsModel tdm;
					while (acitr.hasNext()) {
						tdm = acitr.next();
						getSession().createQuery(
										"update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tdm.getAmount())
								.setLong("id", tdm.getFromAcct().getId())
								.executeUpdate();

						getSession().createQuery(
										"update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tdm.getAmount())
								.setLong("id", tdm.getToAcct().getId()).executeUpdate();

						flush();
					}
					getSession().delete(trans);
					getSession().delete(paymdl);
					flush();
				}
			}
			getSession().clear();
			getSession().update(scmdl);
			getSession().update(simdl);
			getSession().delete(transObj);
			
			if(paymentModel.getSubscription_payment_list().size()!=0){
				old_notDeletedLst = getSession().createQuery(
						"select b.id from SubscriptionPaymentModel a join a.subscription_payment_list b "
								+ "where a.pay_credit=0 and a.id=" + paymentModel.getId()).list();
			}
			getSession().delete(paymentModel);
			if(paymentModel.getSubscription_payment_list().size()!=0){
				try {
					if(old_notDeletedLst.size()>0)
					getSession()
					.createQuery(
							"delete from SubscriptionPaymentDetailsModel where id in (:lst)")
					.setParameterList("lst", old_notDeletedLst)
					.executeUpdate();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	public SubscriptionPaymentModel getPaymentModel(long id) throws Exception
	{
		SubscriptionPaymentModel mdl = null;
		try
		{
			begin();
			mdl=(SubscriptionPaymentModel) getSession().get(SubscriptionPaymentModel.class, id);
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl;
	}
	
	public SubscriptionPaymentModel getCreditPaymentModel(long id) throws Exception
	{
		SubscriptionPaymentModel mdl = null;
		long count=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select count(id) from SubscriptionPaymentModel where special_credit_payment="+id).uniqueResult();
			if(obj!=null)
				count=(Long)obj;
			if(count>0)
				mdl=(SubscriptionPaymentModel)getSession().createQuery("from SubscriptionPaymentModel where special_credit_payment="+id).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl;
	}
	
	public TransactionModel getTransaction(long id) throws Exception {
		TransactionModel tran = null;
		try {
			begin();
			tran = (TransactionModel) getSession().get(TransactionModel.class,
					id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return tran;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllTransactions(long sid)throws Exception{
		List list=null;
		try{
			begin();
			list=getSession().createQuery("from SubscriptionPaymentModel where subscription.id=:sid and pay_credit=0 order by id DESC").setParameter("sid", sid).list();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getLastTransactions(long sid)throws Exception{
		List list=null;
		try{
			begin();
			list=getSession().createQuery("from SubscriptionPaymentModel where subscription.id=:sid and pay_credit=0 order by id DESC limit 1").setParameter("sid", sid).list();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllOutSubscriptions(long officeId,long type) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel" +
					"(a.id,concat(a.subscription_no,' ( ',b.name,' )'))"+
											" from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid " +
											"and a.available=2 and b.rent_status=3 and a.rent_in!=0 and b.account_type=:type order by subscription_no")
											.setParameter("oid", officeId).setParameter("type", type).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return idLIst;	
	}

	@SuppressWarnings({ "rawtypes" })
	public List getAllInSubscriptions(long officeId,long type) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel" +
					"(a.id,concat(a.subscription_no,' ( ',b.name,' )'))"+
											" from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid and a.available!=2 and" +
											" b.account_type=:type order by subscription_no")
											.setParameter("oid", officeId).setParameter("type", type).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return idLIst;	
	}

	@SuppressWarnings({ "rawtypes" })
	public long getCount(long id) throws Exception
	{	
		long count=0;
		try
		{
			begin();
			long value=(Long)getSession().createQuery("select subscription.id from SubscriptionInModel where id=:id")
					.setParameter("id", id).uniqueResult();
			count=(Long)getSession().createQuery("select coalesce(count(subscription.id),0) from SubscriptionInModel where subscription.id=:id")
					.setParameter("id", value).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return count;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public SubscriptionPaymentModel getModel(long sid) throws Exception
	{	SubscriptionPaymentModel mdl=null;
		long count=0;
		try
		{
			begin();
			long value=(Long)getSession().createQuery("select max(id) from SubscriptionPaymentModel where pay_credit=0 and subscription.id=:id  ")
					.setParameter("id", sid).uniqueResult();
			mdl=(SubscriptionPaymentModel)getSession().get(SubscriptionPaymentModel.class, value);
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return mdl;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public SubscriptionInModel getInModel(long sid) throws Exception
	{	SubscriptionInModel mdl=null;
		long count=0;
		try
		{
			begin();
			long id=(Long)getSession().createQuery("select subscription.id from SubscriptionPaymentModel where pay_credit=0 and id="+sid).uniqueResult();
			mdl=(SubscriptionInModel)getSession().createQuery("from SubscriptionInModel where id="+id).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return mdl;	
	}
	
	public SubscriptionInModel getAllInModel(long sid) throws Exception
	{	SubscriptionInModel mdl=null;
		long count=0;
		try
		{
			begin();
			long id=(Long)getSession().createQuery("select subscription.id from SubscriptionPaymentModel where id="+sid).uniqueResult();
			mdl=(SubscriptionInModel)getSession().createQuery("from SubscriptionInModel where id="+id).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return mdl;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public long getAccountType(SubscriptionPaymentModel mdl) throws Exception
	{	long acct=0;
		long count=0;
		try
		{
			begin();
			long id=(Long)getSession().createQuery("select subscription.account_type from SubscriptionPaymentModel where pay_credit=0 and id="+mdl.getId()).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return acct;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public double getAllCreditPayment(SubscriptionInModel mdl) throws Exception{	
		double credit=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select sum(amount_paid) from SubscriptionPaymentModel where pay_credit=1 and subscription.id="+mdl.getId()).uniqueResult();
			if(obj!=null)
				credit=(Double)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return credit;	
	}
	
}
