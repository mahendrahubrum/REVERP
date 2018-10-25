package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 30, 2013
 */
public class ExpendetureTransactionDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9198751225071472584L;
	List resultList = new ArrayList();

	public List getAllExpendetureAsRefNoList(long office_id) throws Exception {
		try {
			begin();
			
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.PaymentDepositModel(id,cast(bill_no as string))"
									+ " from PaymentDepositModel where status=1 and office_id=:ofcid and type =:type and active=true order by id desc")
					.setLong("ofcid", office_id).setLong("type", SConstants.EXPENDETURE_TRANSACTION).list();

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
		return resultList;
	}
	
	public List getAllSubscriptionExpendetureAsBillNoList(long office_id) throws Exception {
		try {
			begin();
			
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.PaymentDepositModel(id,cast(bill_no as string))"
									+ " from PaymentDepositModel where status=1 and office_id=:ofcid and type =:type and active=true and subscription!=0 order by id desc")
					.setLong("ofcid", office_id).setLong("type", SConstants.RENTAL_EXPENDETURE).list();

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
		return resultList;
	}

	public PaymentDepositModel getExpendetureTransaction(Long id)
			throws Exception {
		PaymentDepositModel obj = null;
		try {
			begin();

			obj = (PaymentDepositModel) getSession().get(PaymentDepositModel.class, id);

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
		return obj;
	}

	public List getAllLedgersUnderType(long officeID, long expense)
			throws Exception {
		try {
			begin();

			resultList = getSession()
					.createQuery(
							" from LedgerModel where group.account_class_id=:expense and office_id=:ofcid")
					.setLong("ofcid", officeID)
					.setParameter("expense", expense).list();

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
		return resultList;
	}
	
	
	public List getAllDirectAddedLedgersUnderType(long officeID, long expense)
			throws Exception {
		try {
			begin();

			resultList = getSession()
					.createQuery(
							" from LedgerModel where group.account_class_id=:expense and office_id=:ofcid and type=:typ")
					.setLong("ofcid", officeID).setInteger("typ", SConstants.LEDGER_ADDED_DIRECTLY)
					.setParameter("expense", expense).list();

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
		return resultList;
	}
	

	public long save(PaymentDepositModel objMdl) throws Exception {
		try {
			begin();
			
			getSession().save(objMdl.getTransaction());
			
			TransactionDetailsModel trnDet=null;
			Iterator aciter = objMdl.getTransaction()
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				trnDet = (TransactionDetailsModel) aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", trnDet.getAmount()).setLong("id", trnDet.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", trnDet.getAmount())
						.setLong("id", trnDet.getToAcct().getId()).executeUpdate();
			}
			
			getSession().save(objMdl);

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
		return objMdl.getId();
	}

	@SuppressWarnings("unchecked")
	public void update(PaymentDepositModel objMdl) throws Exception {
		try {
			begin();
			List old_notDeletedLst=new ArrayList();
			List transList = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + objMdl.getTransaction().getTransaction_id())
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
				
				old_notDeletedLst.add(tr.getId());
				
			}
			
			getSession().update(objMdl.getTransaction());
			flush();
			
			Iterator<TransactionDetailsModel> iter = objMdl.getTransaction()
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
			getSession().update(objMdl);

			getSession()
					.createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

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

	public void delete(Long id) throws Exception {
		try {
			begin();
			PaymentDepositModel jm = (PaymentDepositModel) getSession().get(
					PaymentDepositModel.class, id);
			
			Iterator<TransactionDetailsModel> aciter = jm.getTransaction().getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery(
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
			
			
			getSession().delete(getSession().get(TransactionModel.class,
							jm.getTransaction().getTransaction_id()));
			getSession().delete(jm);

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
	
	
	public void cancel(Long id) throws Exception {
		try {
			begin();
			PaymentDepositModel jm = (PaymentDepositModel) getSession().get(
					PaymentDepositModel.class, id);
			
			Iterator<TransactionDetailsModel> aciter = jm.getTransaction().getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery(
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
			
			
			getSession().delete(getSession().get(TransactionModel.class,
							jm.getTransaction().getTransaction_id()));
//			getSession().delete(jm);
			
			jm.setActive(false);
			jm.setTransaction(null);

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
	
	
	@SuppressWarnings("unchecked")
	public List getExpendetureReport(long fromActId, long expId, Date fromDt, Date toDt, long office_id) throws Exception {
		try {
			begin();
			resultList=new ArrayList();
			List lst=null;
			
			String condition="";
			
			if(fromActId!=0){
				condition+=" and b.fromAcct.id="+fromActId;
			}
			
			if(expId!=0){
				condition+=" and b.toAcct.id="+expId;
			}
			
			lst = getSession().createQuery("select a from PaymentDepositModel a join a.transaction.transaction_details_list b" +
											" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
											"a.date between :stdt and :enddt "+condition+" group by a.id")
											.setLong("ofcid", office_id)
											.setParameter("stdt", fromDt)
											.setParameter("enddt", toDt)
											.setLong("type", SConstants.EXPENDETURE_TRANSACTION).list();
			
			commit();
			
			double amount=0;
			String details="", toAcct="";
			PaymentDepositModel obj;
			TransactionDetailsModel obj2;
			Iterator it=lst.iterator();
			Iterator it2;
			while (it.hasNext()) {
				obj=(PaymentDepositModel) it.next();
				amount=0;
				details="";
				it2=obj.getTransaction().getTransaction_details_list().iterator();
				while (it2.hasNext()) {
					obj2 = (TransactionDetailsModel) it2.next();
					details+=obj2.getFromAcct().getName()+" : "+obj2.getAmount()+" , ";
					toAcct=obj2.getToAcct().getName();
					amount+=obj2.getAmount();
				}
				//Constructor 45
				resultList.add(new ReportBean(obj.getId(),String.valueOf(obj.getBill_no()),obj.getDate(), details, toAcct,amount));;
				
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List showExpendetureReport(long fromActId, long expId, long salesMan, Date fromDt, Date toDt, long office_id,int cash_cheque) throws Exception {
		List lst = new ArrayList();
		try {
			begin();
			resultList=new ArrayList();
			
			String condition="";
			String cdn="";
			if(fromActId!=0){
				condition+=" and b.fromAcct.id="+fromActId;
			}
			if(salesMan!=0){
				condition+=" and b.narration="+salesMan;
			}
			if(expId!=0){
				cdn+=" and id="+expId;
			}
			List list= getSession().createQuery("from LedgerModel where group.account_class_id=:expense and office_id=:ofcid and type=:typ "+cdn)
									.setLong("ofcid", office_id)
									.setInteger("typ", SConstants.LEDGER_ADDED_DIRECTLY)
									.setParameter("expense", Long.parseLong("4")).list();
			if(list.size()>0){
				Iterator it=list.iterator();
				while (it.hasNext()) {
					LedgerModel ledger = (LedgerModel) it.next();
					List lsts=getSession().createQuery("from PaymentDepositModel a join a.transaction.transaction_details_list b" +
											" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
											"a.date between :stdt and :enddt "+condition+" and b.toAcct.id=:ledger and a.cash_or_check=:cash_cheque  group by a.id")
										.setLong("ofcid", office_id)
										.setLong("ledger", ledger.getId())
										.setParameter("stdt", fromDt)
										.setParameter("enddt", toDt)
										.setParameter("cash_cheque", cash_cheque)
										.setLong("type", SConstants.EXPENDETURE_TRANSACTION).list();
//					System.out.println("List "+lsts.size());
					if(lsts.size()>0)
						lst.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(b.toAcct.id, b.toAcct.name, coalesce(sum(b.amount),0)) from PaymentDepositModel a join a.transaction.transaction_details_list b" +
								" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
								"a.date between :stdt and :enddt "+condition+" and b.toAcct.id=:ledger and a.cash_or_check=:cash_cheque")
								.setLong("ofcid", office_id)
								.setLong("ledger", ledger.getId())
								.setParameter("stdt", fromDt)
								.setParameter("enddt", toDt)
								.setParameter("cash_cheque", cash_cheque)
								.setLong("type", SConstants.EXPENDETURE_TRANSACTION).list());
					
					//Constructor 52
//					System.out.println("List "+lst.size());
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
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	public List getSubscriptionExpendetureReport(long fromActId, long expId, Date fromDt, Date toDt, long office_id,long rental) throws Exception {
		try {
			begin();
			resultList=new ArrayList();
			List lst=null;
			
			String condition="";
			if(fromActId!=0)
				condition+=" and b.fromAcct.id="+fromActId;
			if(expId!=0)
				condition+=" and b.toAcct.id="+expId;
			if(rental!=0)
				condition+=" and a.subscription="+rental;
			
			lst = getSession()
				.createQuery("select a from PaymentDepositModel a join a.transaction.transaction_details_list b" +
								" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and a.date between :stdt and :enddt"+condition+" group by a.id")
				.setLong("ofcid", office_id).setParameter("stdt", fromDt).setParameter("enddt", toDt).setLong("type", SConstants.RENTAL_EXPENDETURE).list();
			
			commit();
			
			double amount=0;
			String details="", toAcct="",vehilce="";
			PaymentDepositModel obj;
			TransactionDetailsModel obj2;
			Iterator it=lst.iterator();
			Iterator it2;
			while (it.hasNext()) {
				obj=(PaymentDepositModel) it.next();
				amount=0;
				details="";
				it2=obj.getTransaction().getTransaction_details_list().iterator();
				while (it2.hasNext()) {
					obj2 = (TransactionDetailsModel) it2.next();
					details+=obj2.getFromAcct().getName()+" : "+obj2.getAmount()+" , ";
					toAcct=obj2.getToAcct().getName();
					amount+=obj2.getAmount();
					vehilce=obj2.getNarration();
				}
				//Constructor 49
				resultList.add(new ReportBean(obj.getId(),String.valueOf(obj.getBill_no()),obj.getDate(), details, toAcct,amount,vehilce));;
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}

}
