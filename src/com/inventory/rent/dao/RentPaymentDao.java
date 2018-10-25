package com.inventory.rent.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.payment.model.PaymentModel;
import com.inventory.rent.model.RentPaymentModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Oct 7, 2014
 */

public class RentPaymentDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -172297986186581970L;

	
	public long saveRentPayment(RentPaymentModel paymentModel)
			
			throws Exception {
		try {
			begin();
			
			getSession().save(paymentModel);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return paymentModel.getId();
		}
	}
	
	public void updateRentPayment(PaymentModel paymentModel,
			TransactionModel transaction, Set<Long> options_selected)
			throws Exception {

		try {
			begin();

			Object oldSID = getSession().createQuery(
					"select sales_ids from PaymentModel where id="
							+ paymentModel.getId()).uniqueResult();

			if (oldSID != null && !oldSID.toString().equals("")) {

				String[] ids = oldSID.toString().split(",");
				for (int i = 0; i < ids.length; i++) {
					getSession()
							.createQuery(
									"update PurchaseModel set payment_done='N' where id=:id")
							.setLong("id", Long.parseLong(ids[i]))
							.executeUpdate();
				}

			}

			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transList.iterator();
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

			getSession().update(transaction);

			Iterator<TransactionDetailsModel> iter = transaction
					.getTransaction_details_list().iterator();
			while (iter.hasNext()) {
				tr = iter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			getSession().update(paymentModel);

			// Added by Jinshad
			Iterator<Long> it = options_selected.iterator();
			while (it.hasNext()) {
				getSession()
						.createQuery(
								"update PurchaseModel set payment_done='Y' where id=:id")
						.setLong("id", it.next()).executeUpdate();
			}

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	public RentPaymentModel getPaymentModel(long id) throws Exception {
		RentPaymentModel mdl=null;
		try 
		{
			begin();
			mdl=(RentPaymentModel) getSession().get(RentPaymentModel.class,id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally
		{
			flush();
			close();
		}
		return mdl;
	}
	
	public List getPayment(long rid)throws Exception
	{
		List list=null;
		try
		{
			begin();
			list=getSession().createQuery("from RentPaymentModel where rent_number=:no").setParameter("no", rid).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return list;
	}
	
	public List getpaymntDetails(long id) throws Exception 
	{
		List lis=null;
		try 
		{
			
			begin();
			lis = getSession().createQuery("from RentPaymentModel where rent_number=:rentid order by date").setParameter("rentid", id).list();
			commit();
		}
		catch (Exception e) 
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
			return lis;
		}
	}
	
	public long getPaymentCount(long rid)throws Exception
	{
		long count=0;
		try 
		{
			begin();
			count=(Long)getSession().createQuery("select count(rent_number) from RentPaymentModel where rent_number=:no").setParameter("no", rid).uniqueResult();
			commit();
		} 
		catch (Exception e) 
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally 
		{
			flush();
			close();
		}
		return count;
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
			return tran;
		}
	}

	public void savePayment(Hashtable<TransactionModel, RentPaymentModel> hashTable)throws Exception
	{
		try
		{
			begin();
			Hashtable<TransactionModel, RentPaymentModel> hash=hashTable;
			Set hashSet=hash.keySet();
			Iterator itr=hashSet.iterator();
			TransactionModel transactionModel=null;
			RentPaymentModel rentPaymentModel=null;
			while(itr.hasNext())
			{
				transactionModel=(TransactionModel)itr.next();
				rentPaymentModel=hash.get(transactionModel);
				if(rentPaymentModel.getId()!=0)
				{
					List transList = getSession().createQuery(
							"select b from TransactionModel a join a.transaction_details_list b "
									+ "where a.id=" + transactionModel.getTransaction_id())
							.list();
					TransactionDetailsModel tr;
					Iterator<TransactionDetailsModel> aciter = transList.iterator();
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
					Iterator<TransactionDetailsModel> iter = transactionModel
							.getTransaction_details_list().iterator();
					while (iter.hasNext()) {
						tr = iter.next();
						getSession()
								.createQuery(
										"update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tr.getAmount())
								.setLong("id", tr.getFromAcct().getId())
								.executeUpdate();
						getSession()
								.createQuery(
										"update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tr.getAmount())
								.setLong("id", tr.getToAcct().getId()).executeUpdate();
						flush();
					}
					getSession().update(transactionModel);
					getSession().update(rentPaymentModel);
				}
				else
				{
					Iterator<TransactionDetailsModel> aciter = transactionModel
							.getTransaction_details_list().iterator();
					TransactionDetailsModel tr;
					while (aciter.hasNext()) {
						tr = aciter.next();
						getSession()
								.createQuery(
										"update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tr.getAmount())
								.setLong("id", tr.getFromAcct().getId())
								.executeUpdate();

						getSession()
								.createQuery(
										"update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tr.getAmount())
								.setLong("id", tr.getToAcct().getId()).executeUpdate();

						flush();
					}
					rentPaymentModel.setTransaction_id(transactionModel.getTransaction_id());
					getSession().save(transactionModel);
					getSession().save(rentPaymentModel);
				}
			}
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
	
	public double getBalanceAmount(long rent_id) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select balance " +
					"from RentPaymentModel where rent_number=:rentno and id = (Select id from RentPaymentModel where rent_number=:rentno order by date)")
					.setParameter("rentno", rent_id).uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public double getPaidAmount(long rent_id) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select SUM(payment_amount)" +
					"from RentPaymentModel where rent_number=:rentno")
					.setParameter("rentno", rent_id).uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}

	@SuppressWarnings("rawtypes")
	public List getAllPaymentByCustomer(long cid)throws Exception
	{
		List list=null;
		try
		{
			begin();
			list=getSession().createQuery("from RentPaymentModel where customer.id=:no order by date DESC").setParameter("no", cid).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return list;
	}

	public static Date getStartDate(long cid)throws Exception
	{
		Date date = null;
		try
		{
			begin();
			date=(Date)getSession().createQuery("select MIN(date) from RentDetailsModel where customer.id=:no order by date ASC").setParameter("no", cid).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return date;
	}
	
	public Date getFirstReturnDate(long rent_id,long childId)throws Exception
	{
		Date date = null;
		try
		{
			begin();
			Object obj = getSession().createQuery("select return_date " +
					"from RentReturnItemDetailModel where rent_number=:rentno and rent_inventory_id =:childId and id = (Select MIN(id) from RentReturnItemDetailModel where rent_inventory_id=:childId)")
					.setParameter("rentno", rent_id).setParameter("childId", childId).uniqueResult();
				if(obj != null){
					date=(Date)obj;
				}
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return date;
	}

	public List getMasterModel(long cid)throws Exception
	{
		List list=null;
		try
		{
			begin();
			list=getSession().createQuery("from RentDetailsModel where customer.id=:no order by date ASC").setParameter("no", cid).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public List getRentDetailsReport(long org_id, long off_id,long customer_id) throws Exception 
	{
		List resultList=new ArrayList();
		String condition = "";
		if(org_id !=0)
		{
			condition += " and office.organization.id= "  +org_id; 
		}
		if(off_id !=0)
		{
			condition +=  " and office.id= " +off_id;
		}
		if(customer_id !=0)
		{
			condition += " and customer.id= " +customer_id;
		}
		try 
		{
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where 1=1 " +condition).list();
			commit();
		}
		catch (Exception e) 
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
			return resultList;
		}
}
	
	public void deletePayment(long paymentId) throws Exception {
		try {
			begin();

			RentPaymentModel mdl = (RentPaymentModel) getSession().get(RentPaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, mdl.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
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

			getSession().delete(transObj);
			getSession().delete(mdl);
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
	
	
}

