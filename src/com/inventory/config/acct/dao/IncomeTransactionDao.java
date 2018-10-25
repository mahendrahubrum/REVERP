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
 * WebSpark.
 *
 * Oct 1, 2013
 */
public class IncomeTransactionDao extends SHibernate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7692723509474348465L;
	List resultList = new ArrayList();

	
	public List getAllIncomeAsRefNoList(long office_id) throws Exception {
		try {
			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.PaymentDepositModel(id,cast(bill_no as string))"
									+ " from PaymentDepositModel where status=1 and office_id=:ofcid and type=:type and active=true")
					.setLong("ofcid", office_id).setLong("type", SConstants.INCOME_TRANSACTION).list();

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

	
	public PaymentDepositModel getIncomeTransaction(Long id)
			throws Exception {
		PaymentDepositModel obj = null;
		try {
			begin();

			obj = (PaymentDepositModel) getSession().get(
					PaymentDepositModel.class, id);

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

	
	public List getAllLedgersUnderType(long officeID, long income)
			throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("from LedgerModel where group.account_class_id=:income and office_id=:ofcid")
					.setLong("ofcid", officeID)
					.setParameter("income", income).list();

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
			getSession().update(objMdl);

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
			
			getSession().delete(
					getSession().get(TransactionModel.class,
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
			
			getSession().delete(
					getSession().get(TransactionModel.class,
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List showIncomeReport(long toAcct, long income, Date fromDt, Date toDt, long office_id)throws Exception {
		
		List reslist=new ArrayList();
		String condition="";
		String cdn="";
		try {
			begin();
			if(toAcct!=0){
				condition+=" and b.toAcct.id="+toAcct;
			}
			
			if(income!=0){
				cdn+=" and id="+income;
			}
			List list= getSession().createQuery("from LedgerModel where group.account_class_id=:income and office_id=:ofcid"+cdn)
					.setLong("ofcid", office_id).setParameter("income", Long.parseLong("3")).list();
			if(list.size()>0){
				Iterator it=list.iterator();
				while (it.hasNext()) {
					LedgerModel ledger = (LedgerModel) it.next();
					List lsts=getSession().createQuery("select a from PaymentDepositModel a join a.transaction.transaction_details_list b" +
											" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
											"a.date between :stdt and :enddt "+condition+" and b.fromAcct.id=:ledger group by a.id")
										.setLong("ofcid", office_id)
										.setLong("ledger", ledger.getId())
										.setParameter("stdt", fromDt)
										.setParameter("enddt", toDt)
										.setLong("type", SConstants.INCOME_TRANSACTION).list();
					if(lsts.size()>0)
						reslist.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(b.fromAcct.id, b.fromAcct.name, coalesce(sum(b.amount),0)) " +
								"from PaymentDepositModel a join a.transaction.transaction_details_list b" +
								" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
								"a.date between :stdt and :enddt "+condition+" and b.fromAcct.id=:ledger")
								.setLong("ofcid", office_id)
								.setLong("ledger", ledger.getId())
								.setParameter("stdt", fromDt)
								.setParameter("enddt", toDt)
								.setLong("type", SConstants.INCOME_TRANSACTION).list());
					
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
		return reslist;
	}
	
	
	@SuppressWarnings("unchecked")
	public List getIncomeReport(long toAcct, long expId, Date fromDt, Date toDt, long office_id) throws Exception {
		try {
			begin();
			resultList=new ArrayList();
			List lst=null;
			
			String condition="";
			
			if(toAcct!=0){
				condition+=" and b.toAcct.id="+toAcct;
			}
			
			if(expId!=0){
				condition+=" and b.fromAcct.id="+expId;
			}
			
			lst = getSession().createQuery("select a from PaymentDepositModel a join a.transaction.transaction_details_list b" +
											" where a.status=1 and a.office_id=:ofcid and a.active=true and a.type =:type and " +
											"a.date between :stdt and :enddt "+condition+" group by a.id")
											.setLong("ofcid", office_id)
											.setParameter("stdt", fromDt)
											.setParameter("enddt", toDt)
											.setLong("type", SConstants.INCOME_TRANSACTION).list();
			
			commit();
			
			double amount=0;
			String details="", tooAcct="";
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
					if(toAcct!=0){
						if(obj2.getToAcct().getId()!=toAcct)
							continue;
					}
					details+=obj2.getToAcct().getName()+" : "+obj2.getAmount()+" , ";
					tooAcct=obj2.getFromAcct().getName();
					amount+=obj2.getAmount();
				}
				//Constructor 45
				resultList.add(new ReportBean(obj.getId(),String.valueOf(obj.getBill_no()),obj.getDate(), details, tooAcct,amount));;
				
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
