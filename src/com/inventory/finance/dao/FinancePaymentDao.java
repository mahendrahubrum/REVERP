package com.inventory.finance.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.inventory.finance.model.FinanceComponentModel;
import com.inventory.finance.model.FinancePaymentDetailsModel;
import com.inventory.finance.model.FinancePaymentModel;
import com.inventory.reports.bean.FinanceTransactionReportBean;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 30, 2014
 */
public class FinancePaymentDao extends SHibernate{

	private static final long serialVersionUID = 9174639867379789645L;

	public List getAllPaymentNumber(long officeId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"Select new com.inventory.finance.model.FinancePaymentModel(a.id, a.payment_no)"
									+ " from FinancePaymentModel a join a.finance_payment_list b where b.from_account.officeId=:ofc and a.active=true order by a.id desc").setParameter("ofc", officeId).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}

	public FinancePaymentModel getPaymentModel(Long value) throws Exception {
		FinancePaymentModel mdl = null;
		try {
			begin();
			mdl = (FinancePaymentModel) getSession().get(FinancePaymentModel.class, value);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return mdl;
	}

	public void save(FinancePaymentModel objMdl) throws Exception {
		try {
			begin();
			
			FinancePaymentDetailsModel detMdl=null;
			List list=objMdl.getFinance_payment_list();
			Iterator iter=list.iterator();
			while (iter.hasNext()) {
				detMdl = (FinancePaymentDetailsModel) iter.next();
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance-:amnt where id=:id")
					.setParameter("amnt", detMdl.getAmount()).setParameter("id", detMdl.getFrom_account().getId()).executeUpdate();
				
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance+:amnt where id=:id")
					.setParameter("amnt", detMdl.getAmount()).setParameter("id", detMdl.getTo_account().getId()).executeUpdate();
			}
			
			getSession().save(objMdl);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		
	}

	public void updateJounal(FinancePaymentModel objMdl) throws Exception {
		try {
			begin();
			
			FinancePaymentDetailsModel oldDetMdl=null;
			List oldList=getSession().createQuery(
					"select b.id from FinancePaymentModel a join a.finance_payment_list b "
							+ "where a.id=" + objMdl.getId()).list();
			long oldId=0;
			
			Iterator oldIter=oldList.iterator();
			while (oldIter.hasNext()) {
				
				oldId=(Long) oldIter.next();
				oldDetMdl = (FinancePaymentDetailsModel)getSession().get(FinancePaymentDetailsModel.class, oldId) ;
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance+:amnt where id=:id")
					.setParameter("amnt", oldDetMdl.getAmount()).setParameter("id", oldDetMdl.getFrom_account().getId()).executeUpdate();
				
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance-:amnt where id=:id")
					.setParameter("amnt", oldDetMdl.getAmount()).setParameter("id", oldDetMdl.getTo_account().getId()).executeUpdate();
			}
			flush();
			
			
			FinancePaymentDetailsModel detMdl=null;
			List list=objMdl.getFinance_payment_list();
			Iterator iter=list.iterator();
			while (iter.hasNext()) {
				detMdl = (FinancePaymentDetailsModel) iter.next();
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance-:amnt where id=:id")
					.setParameter("amnt", detMdl.getAmount()).setParameter("id", detMdl.getFrom_account().getId()).executeUpdate();
				
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance+:amnt where id=:id")
					.setParameter("amnt", detMdl.getAmount()).setParameter("id", detMdl.getTo_account().getId()).executeUpdate();
			}

			getSession().update(objMdl);
			flush();

			getSession()
					.createQuery(
							"delete from FinancePaymentDetailsModel where id in (:lst)")
					.setParameterList("lst", oldList)
					.executeUpdate();

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		
		
	}

	public void delete(Long value) throws Exception {
		try {
			begin();
			
			FinancePaymentModel objMdl=(FinancePaymentModel) getSession().get(FinancePaymentModel.class, value);
			FinancePaymentDetailsModel oldDetMdl=null;
			List oldList=getSession().createQuery(
					"select b from FinancePaymentModel a join a.finance_payment_list b "
							+ "where a.id=" + objMdl.getId()).list();
			Iterator oldIter=oldList.iterator();
			while (oldIter.hasNext()) {
				
				oldDetMdl = (FinancePaymentDetailsModel)oldIter.next() ;
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance+:amnt where id=:id")
					.setParameter("amnt", oldDetMdl.getAmount()).setParameter("id", oldDetMdl.getFrom_account().getId()).executeUpdate();
				
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance-:amnt where id=:id")
					.setParameter("amnt", oldDetMdl.getAmount()).setParameter("id", oldDetMdl.getTo_account().getId()).executeUpdate();
			}
			flush();
			
			getSession().delete(objMdl);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
	}
	
	public void cancel(Long value) throws Exception {
		try {
			begin();
			
			FinancePaymentModel objMdl=(FinancePaymentModel) getSession().get(FinancePaymentModel.class, value);
			FinancePaymentDetailsModel oldDetMdl=null;
			List oldList=getSession().createQuery(
					"select b from FinancePaymentModel a join a.finance_payment_list b "
							+ "where a.id=" + objMdl.getId()).list();
			Iterator oldIter=oldList.iterator();
			while (oldIter.hasNext()) {
				
				oldDetMdl = (FinancePaymentDetailsModel)oldIter.next() ;
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance+:amnt where id=:id")
				.setParameter("amnt", oldDetMdl.getAmount()).setParameter("id", oldDetMdl.getFrom_account().getId()).executeUpdate();
				
				getSession().createQuery("update FinanceComponentModel set current_balance=current_balance-:amnt where id=:id")
				.setParameter("amnt", oldDetMdl.getAmount()).setParameter("id", oldDetMdl.getTo_account().getId()).executeUpdate();
			}
			flush();
			
			getSession().createQuery("update FinancePaymentModel set active=false where id=:id").setParameter("id", value).executeUpdate();
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			throw e;
			
		} finally {
			flush();
			close();
		}
	}

	public List getFinTransactionReport(long accid, Date fromDate,Date toDate,long officeId) throws Exception {
		
		List list = null;
		try {
			begin();
			
			String condition="";
			if(accid>0){
				condition+=" and b.from_account.id="+accid+" or b.to_account.id ="+accid ;
			}
			
			if(officeId>0)
				condition+=" and b.from_account.officeId="+officeId;
			
			list = getSession()
					.createQuery(
							"Select new com.inventory.reports.bean.FinanceTransactionReportBean(b.from_account.name," +
																								"b.to_account.name ," +
																								"cast(concat(b.amount,' ',b.currency.code) as string)," +
																								"a.date, " +
																								"b.comments,a.id)"
									+ " from FinancePaymentModel a join a.finance_payment_list b where a.date between :frm and :to and a.active=true "+condition+" order by payment_no desc").setParameter("frm", fromDate).setParameter("to", toDate).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List getFinLedgerReport(long accid, Date fromDate,Date toDate,long officeId) throws Exception {
		
		List list = new ArrayList();
		try {
			begin();
			
			FinanceComponentModel comp=null;
			FinanceTransactionReportBean bean=null;
			String condition="";
			if(accid>0){
				condition+=" and id="+accid;
			}
			
			List mainList=getSession().createQuery("from FinanceComponentModel where officeId=:ofc"+condition).setParameter("ofc", officeId).list();
			Iterator iter=mainList.iterator();
			
			while (iter.hasNext()) {
				comp = (FinanceComponentModel) iter.next();
				
				list.addAll(getSession()
						.createQuery(
								"Select new com.inventory.reports.bean.FinanceTransactionReportBean(b.from_account.name," +
																									"a.date," +
																									"b.comments," +
																									"a.payment_no, " +
																									"0.0," +
																									"b.amount," +
																									"0.0 ,"
																									+accid+",a.id)"
										+ " from FinancePaymentModel a join a.finance_payment_list b where a.date between :frm and :to and b.from_account.id=:acc and a.active=true order by payment_no desc")
						.setParameter("acc", comp.getId())
						.setParameter("frm", fromDate)
						.setParameter("to", toDate).list());
				
				list.addAll(getSession()
						.createQuery(
								"Select new com.inventory.reports.bean.FinanceTransactionReportBean(b.to_account.name," +
																									"a.date ," +
																									"b.comments," +
																									"a.payment_no," +
																									"b.amount," +
																									"0.0," +
																									"0.0 ,"
																									+accid+",a.id)"
										+ " from FinancePaymentModel a join a.finance_payment_list b where a.date between :frm and :to and b.to_account.id=:acc and a.active=true order by payment_no desc")
						.setParameter("acc", comp.getId())
						.setParameter("frm", fromDate)
						.setParameter("to", toDate).list());		
				
			}
			
			
			Collections.sort(list, new Comparator<FinanceTransactionReportBean>() {
				@Override
				public int compare(final FinanceTransactionReportBean object1,final FinanceTransactionReportBean object2) {
					int result = object1.getDate().compareTo(object2.getDate());
				     if (result == 0) {
				        result = object1.getFromaccount().compareTo(object2.getFromaccount());
				     }
				     
				     return result;
				}
			});
			
			if (accid > 0) {
				double balance=0;
				double prevBalance=0;
				double opCrBalance=0;
				double opDbBalance=0;
				
				Object crObj = (Double) getSession()
						.createQuery(
								"select sum(b.amount) from FinancePaymentModel a join a.finance_payment_list b where a.date<:date and b.from_account.id=:acc")
						.setParameter("date", fromDate)
						.setParameter("acc", accid)
						.uniqueResult();
				
				Object dbObj = (Double) getSession()
						.createQuery(
								"select sum(b.amount) from FinancePaymentModel a join a.finance_payment_list b where a.date <:date and b.to_account.id=:acc")
						.setParameter("date", fromDate)
								.setParameter("acc", accid)
								.uniqueResult();
			
				if(crObj!=null)
					opCrBalance=(Double) crObj;
				
				if(dbObj!=null)
					opDbBalance=(Double) dbObj;
				
				prevBalance=opDbBalance-opCrBalance;
				
				Iterator subIter = list.iterator();
				
				while (subIter.hasNext()) {
					
					bean = (FinanceTransactionReportBean) subIter.next();
					
					balance=balance+bean.getInwards()-bean.getOutwards();
					bean.setBalance(balance);
					
					prevBalance+=balance;
				}
			}
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}
}
