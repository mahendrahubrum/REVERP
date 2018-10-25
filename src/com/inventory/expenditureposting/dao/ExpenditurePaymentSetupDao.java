package com.inventory.expenditureposting.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.PaymentDepositModel;
import com.inventory.expenditureposting.model.BatchExpenditurePaymentMasterModel;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupModel;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 *
 * Jul 11, 2013
 */
public class ExpenditurePaymentSetupDao extends SHibernate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5329377737432540916L;
	List resultList=new ArrayList();
	
	public List getAllSetups(long office_id) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.expenditureposting.model.ExpenditurePaymentSetupModel(id,group_name)" +
					" from ExpenditurePaymentSetupModel where status=1 and office_id=:ofcid")
						.setLong("ofcid", office_id).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getAllPaymentHistories(long office_id) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.expenditureposting.model.BatchExpenditurePaymentMasterModel(id,cast(number as string))" +
					" from BatchExpenditurePaymentMasterModel where office_id=:ofcid order by id desc")
						.setLong("ofcid", office_id).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public long save(ExpenditurePaymentSetupModel obj) throws Exception {
		try {
			begin();
			
			getSession().save(obj);
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	
	public void update(ExpenditurePaymentSetupModel obj) throws Exception {
		try {
			
			begin();
			
			List old_notDeletedLst = getSession().createQuery(
					"select b.id from ExpenditurePaymentSetupModel a join a.details_list b "
							+ "where a.id=" + obj.getId()).list();
			
			getSession().update(obj);
			
			flush();
			
			getSession().createQuery("delete from ExpenditurePaymentSetupDetailsModel where id in (:lst)")
				.setParameterList("lst", (Collection) old_notDeletedLst).executeUpdate();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	public void delete(long id) throws Exception {
		
		try {
			
			begin();
			
			getSession().delete(getSession().get(ExpenditurePaymentSetupModel.class, id));
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	
	public ExpenditurePaymentSetupModel getSetup(long id) throws Exception {
		ExpenditurePaymentSetupModel obj=null;
		try {
			
			begin();
			
			obj=(ExpenditurePaymentSetupModel) getSession().get(ExpenditurePaymentSetupModel.class, id);
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	public BatchExpenditurePaymentMasterModel getBatchHistory(long id) throws Exception {
		BatchExpenditurePaymentMasterModel obj=null;
		try {
			
			begin();
			
			obj=(BatchExpenditurePaymentMasterModel) getSession().get(BatchExpenditurePaymentMasterModel.class, id);
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	
	public long payExpenses(List expList, Hashtable<EmployeeAdvancePaymentModel, TransactionModel> advncPayList,
					BatchExpenditurePaymentMasterModel objMdl) throws Exception {
		try {
			String ids="";
			
			begin();
			
			Iterator<TransactionDetailsModel> aciter;
			PaymentDepositModel paymentObj;
			TransactionDetailsModel trnDet=null;
			Iterator itr1=expList.iterator();
			while (itr1.hasNext()) {
				paymentObj = (PaymentDepositModel) itr1.next();
				getSession().save(paymentObj.getTransaction());
				getSession().save(paymentObj);
				ids+=paymentObj.getId()+",";
				flush();
				aciter=null;
				aciter = paymentObj.getTransaction()
						.getTransaction_details_list().iterator();
				while (aciter.hasNext()) {
					trnDet = aciter.next();

					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", trnDet.getAmount()).setLong("id", trnDet.getFromAcct().getId())
							.executeUpdate();

					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", trnDet.getAmount())
							.setLong("id", trnDet.getToAcct().getId()).executeUpdate();
				}
				flush();
			}
			
			ids+=" ; ";
			
			EmployeeAdvancePaymentModel payObj; 
			TransactionModel trnObj=null;
			Iterator<EmployeeAdvancePaymentModel> itr2=advncPayList.keySet().iterator();
			while (itr2.hasNext()) {
				payObj = (EmployeeAdvancePaymentModel) itr2.next();
				trnObj=advncPayList.get(payObj);
				
				getSession().save(trnObj);
				payObj.setTransaction_id(trnObj.getTransaction_id());
				
				getSession().save(payObj);
				ids+=payObj.getId()+",";
				flush();
				aciter=null;
				aciter = trnObj.getTransaction_details_list().iterator();
				while (aciter.hasNext()) {
					trnDet = aciter.next();

					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", trnDet.getAmount()).setLong("id", trnDet.getFromAcct().getId())
							.executeUpdate();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", trnDet.getAmount())
							.setLong("id", trnDet.getToAcct().getId()).executeUpdate();
				}
				flush();
			}
			
			objMdl.setExp_transaction_ids(ids);
			getSession().save(objMdl);
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		flush();
		close();
		return objMdl.getId();
	}
	
	
	public long getLoginIDFromLledger(long ledgerID) throws Exception{
		
		long ledg=0;
		try {
			begin();
			ledg= (Long) getSession().createQuery("select loginId.id from UserModel where ledger.id=:id").setParameter("id", ledgerID).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return ledg;
	}
	
	
	
	public void deleteExpenses(String[] expIDs, String[] advIDs,
			BatchExpenditurePaymentMasterModel objMdl) throws Exception {
		try {
			
			begin();
			
			for (String id : expIDs) {
				if(id.length()!=0)
					deleteExpendetureTrans(Long.parseLong(id));
			}
			
			for (String paymentId : advIDs) {
				if(paymentId.length()!=0)
					deleteAdvancePay(Long.parseLong(paymentId));
			}
			
			getSession().delete(objMdl);
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		flush();
		close();
	}

	
	
	public void deleteExpendetureTrans(long id) throws Exception {
		try {
			
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
			
			flush();

		} catch (Exception e) {
			throw e;
		} 
	}
	
	
	public void deleteAdvancePay(long paymentId) throws Exception {
		try {
			
			EmployeeAdvancePaymentModel paymentModel = (EmployeeAdvancePaymentModel) getSession().get(
					EmployeeAdvancePaymentModel.class, paymentId);

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, paymentModel.getTransaction_id());

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
			getSession().delete(paymentModel);
			
			flush();

		} catch (Exception e) {
			throw e;
		} 
	}
	
	
	public List getPaymentHistoryReport(long office_id, long grp_id, Date frmdt, Date todt) throws Exception {
		try {
			begin();
			String criteria="";
			if(grp_id!=0)
				criteria+=" and a.group_id="+grp_id;
			// Constructor 46
			resultList=getSession().createQuery("select new com.webspark.bean.ReportBean(a.number, " +
					"(select c.group_name from ExpenditurePaymentSetupModel c where  c.id=a.group_id)," +
					"a.date,b.from_account.name,b.to_account.name,b.amount,b.real_amount,b.type,b.comments,a.id)" +
					" from BatchExpenditurePaymentMasterModel a join a.details_list b where a.office_id=:ofcid and a.date between :frm and :to"+criteria)
						.setLong("ofcid", office_id).setParameter("frm", frmdt)
						.setParameter("to", todt).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
}
