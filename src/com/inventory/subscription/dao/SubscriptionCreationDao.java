package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.subscription.model.SubscriptionCreationModel;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

public class SubscriptionCreationDao extends SHibernate implements Serializable 
{
	private static final long serialVersionUID = 8706779853811582775L;
	
	public long save(SubscriptionCreationModel mdl) throws Exception
	{
		try
		{
			begin();
//			getSession().save(mdl.getAddress());
			getSession().save(mdl.getLedger());
			getSession().save(mdl);
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
		return mdl.getId();
	}
	
	public long update(SubscriptionCreationModel mdl) throws Exception
	{
		try
		{
			begin();
			if(mdl.getLedger().getId()!=0){
//				getSession().update(mdl.getLedger().getAddress());
//				flush();
				getSession().update(mdl.getLedger());
			}
			else{
//				getSession().save(mdl.getLedger().getAddress());
//				flush();
				getSession().save(mdl.getLedger());
			}
			flush();
			getSession().update(mdl);
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
		return mdl.getId();
	}
	
	public void delete(SubscriptionCreationModel mdl) throws Exception
	{
		try
		{
			begin();
			if(mdl!=null){
				PaymentDepositModel pdmdl=null;
				TransactionDetailsModel tdm=null;
				TransactionModel trans=null;
				List plist=getSession().createQuery("from PaymentDepositModel where subscription!=0  and subscription=:subscription and office_id="
							+mdl.getSubscription_type().getOfficeId()).setParameter("subscription", mdl.getId()).list();
				if(plist.size()>0) {
					Iterator pitr=plist.iterator();
					while (pitr.hasNext()) {
						pdmdl=(PaymentDepositModel)pitr.next();
						trans=(TransactionModel)getSession().get(TransactionModel.class, pdmdl.getTransaction().getTransaction_id());
						Iterator titr=trans.getTransaction_details_list().iterator();
						while (titr.hasNext()) {
							tdm=(TransactionDetailsModel)titr.next();
							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tdm.getAmount())
								.setLong("id", tdm.getFromAcct().getId()).executeUpdate();

							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tdm.getAmount())
								.setLong("id", tdm.getToAcct().getId()).executeUpdate();
							flush();
						}
						getSession().delete(trans);
						getSession().delete(pdmdl);
					}
					flush();
				}
//				getSession().delete(mdl.getLedger().getAddress());
				getSession().delete(mdl.getLedger());
			}
			getSession().delete(mdl);
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
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getAllSubscriptions(long officeId,long loginId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionCreationModel(id,name)"+
											" from SubscriptionCreationModel where subscription_type.officeId=:oid order by name")
											.setParameter("oid", officeId).list();
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
	public List getAllSubscriptionsReport(long officeId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel" +
					"(a.id,concat(a.subscription_no,' ( ',b.name,' )'))"+
					" from SubscriptionInModel a join a.subscription b where b.subscription_type.officeId=:oid " +
					"order by subscription_no")
					.setParameter("oid", officeId).list();
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
	
	public SubscriptionCreationModel getCreationModel(long id) throws Exception
	{
		SubscriptionCreationModel mdl = null;
		try
		{
			begin();
			mdl=(SubscriptionCreationModel) getSession().get(SubscriptionCreationModel.class, id);
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
	
	public long getGreatestID(long sid,long ofc) throws Exception
	{
		long ids = 0;
		try
		{
			begin();
			SubscriptionInModel mdl=(SubscriptionInModel) getSession().get(SubscriptionInModel.class, sid);
			ids=(Long)getSession().createQuery("select MAX(id) from SubscriptionInModel where account_type=:typ " +
					"and subscription.rent_status=:rent and subscription.subscription_type.officeId=:office")
					.setParameter("office", ofc)
					.setParameter("typ", mdl.getAccount_type()).setParameter("rent", mdl.getSubscription().getRent_status()).uniqueResult();
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
		return ids;	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public List getSubscriptions(long type,long ofc) throws Exception{
		List resultList=null;
		try{
			begin();
//			resultList=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel(id,details)"+
//												"from SubscriptionInModel where account_type=:type order by subscription.name").setParameter("type", type).list();
			
			resultList=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionCreationModel(id,name)"+
					" from SubscriptionCreationModel where account_type=:typ and  subscription_type.officeId=:ofc order by name")
					.setParameter("ofc", ofc).setParameter("typ", type).list();
			
			/*resultList=getSession().createQuery("select new com.inventory.subscription.model.SubscriptionInModel" +
					"(a.id,concat(a.subscription_no,' ( ',b.name,' )'))"+
					" from SubscriptionInModel a join a.subscription b where a.account_type=:type " +
					"order by subscription_no")
					.setParameter("type", type).list();*/
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
		return resultList;
	}
	
	@SuppressWarnings({ })
	public Long getRentType(long creation,long office) throws Exception
	{	
		long status=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select rent_status from SubscriptionCreationModel where id=:id and subscription_type.officeId=:office")
					.setParameter("office", office).setParameter("id", creation).uniqueResult();
			if(obj!=null)
				status=(Long)obj;
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
		return status;	
	}
	
	public String getImageName(long itmId) throws Exception {
		String str = "";
		try {
			begin();
			str = (String) getSession().createQuery("select image from SubscriptionCreationModel where id=:id").setParameter("id",itmId).uniqueResult();
			System.out.println("Str "+str);
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
		return str;
	}
	
}
