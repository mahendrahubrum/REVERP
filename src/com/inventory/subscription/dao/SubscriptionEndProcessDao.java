package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transaction;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.process.model.FinancialYearsModel;
import com.inventory.process.model.ItemClosingStockModel;
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
import com.webspark.dao.SHibernate;
import com.webspark.model.S_IDGeneratorSettingsModel;
import com.webspark.model.S_IDValueCompoundKey;
import com.webspark.model.S_IDValueModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 25, 2013
 */

public class SubscriptionEndProcessDao extends SHibernate implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9194732019184774037L;
	List resultList=new ArrayList();
	
	
	@SuppressWarnings("rawtypes")
	public void updateSubscriptionDue(SubscriptionPaymentModel mdl,TransactionModel transaction) throws Exception {
		try {
			begin();
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
//				flush();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			System.out.println("Transaction Id "+transaction.getTransaction_id());
			mdl.setTransaction_id(transaction.getTransaction_id());
			getSession().save(mdl);
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
	
	public SubscriptionPaymentModel getLastCreditPayment(SubscriptionInModel mdl) throws Exception {
		SubscriptionPaymentModel spmdl=null;
		try {
			begin();
			
			spmdl=(SubscriptionPaymentModel)getSession().createQuery("from SubscriptionPaymentModel" +
					" where pay_credit=1 and subscription.id=:id and id=( select max(id) from " +
					"SubscriptionPaymentModel where pay_credit=1 and subscription.id=:id )").setParameter("id", mdl.getId()).uniqueResult();
			
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
		return spmdl;
	}
	
}
