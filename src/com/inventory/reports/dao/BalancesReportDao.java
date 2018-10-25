package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class BalancesReportDao extends SHibernate implements Serializable{
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getBalancesReport(Date start_date, Date end_date, long office_id, long organization_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			AcctReportMainBean mainObj;
			AcctReportMainBean secondLayer;
			KeyValue objKeyValue;
			Iterator iter2;
			GroupModel grpObj;
			double gp_current_debits=0,gp_current_credits=0,gp_opening_debits=0,gp_opening_credits=0;
			AcctReportMainBean thirdLayer;
			LedgerModel ledgerObj;
			Object obj_current_debits,obj_current_credits,obj_opening_debits,obj_opening_credits;
			double current_debits=0,current_credits=0,opening_debits=0,opening_credits=0;
			Iterator iter1=SConstants.actClassList.iterator();
			while (iter1.hasNext()) {
				objKeyValue = (KeyValue) iter1.next();
				
				mainObj=new AcctReportMainBean();
				mainObj.setName(objKeyValue.getValue());
				
				mainObj.setType('F');
				mainObj.setId(objKeyValue.getKey());
				
				List firstList=new ArrayList();
				
				resultList.add(mainObj);
				
				iter2=getAllGroupsUnderClass(objKeyValue.getKey(),
												organization_id).iterator();
				while (iter2.hasNext()) {
					
					secondLayer=new AcctReportMainBean();
					
					secondLayer.setType('S');
					
					grpObj = (GroupModel) iter2.next();
					
					secondLayer.setId(grpObj.getId());
					secondLayer.setName(grpObj.getName());
					resultList.add(secondLayer);
					
					gp_current_debits=0;gp_current_credits=0;gp_opening_debits=0;gp_opening_credits=0;
					Iterator iter3=getAllActiveLedgerNamesUnderGroup(grpObj.getId(),office_id).iterator();
					while (iter3.hasNext()) {
						
						thirdLayer=new AcctReportMainBean();
						
						thirdLayer.setType('T');
						
						ledgerObj = (LedgerModel) iter3.next();
						
						thirdLayer.setId(ledgerObj.getId());
						thirdLayer.setName(ledgerObj.getName());
						
						
						obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.toAcct.id=:actid and a.date between :stdt and :enddt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).setParameter("enddt", end_date).uniqueResult();
						obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.fromAcct.id=:actid and a.date between :stdt and :enddt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).setParameter("enddt", end_date).uniqueResult();
						
						obj_opening_debits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.toAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).uniqueResult();
						obj_opening_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.fromAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).uniqueResult();
						
						current_debits=0;current_credits=0;opening_debits=0;opening_credits=0;
						if(obj_current_debits!=null)
							current_debits=(Double) obj_current_debits;
						if(obj_current_credits!=null)
							current_credits=(Double) obj_current_credits;
						if(obj_opening_debits!=null)
							opening_debits=(Double) obj_opening_debits;
						if(obj_opening_credits!=null)
							opening_credits=(Double) obj_opening_credits;
						
						gp_current_debits+=current_debits;
						gp_current_credits+=current_credits;
						gp_opening_debits+=opening_debits;
						gp_opening_credits+=opening_credits;
						thirdLayer.setOpening_credit(CommonUtil.roundNumber(opening_credits));
						thirdLayer.setOpening_debit(CommonUtil.roundNumber(opening_debits));
						thirdLayer.setCurrent_credit(CommonUtil.roundNumber(current_credits));
						thirdLayer.setCurrent_debit(CommonUtil.roundNumber(current_debits));
						thirdLayer.setBalance_credit(CommonUtil.roundNumber(opening_credits+current_credits));
						thirdLayer.setBalance_debit(CommonUtil.roundNumber(opening_debits+current_debits));
						
						resultList.add(thirdLayer);
					}
					
					secondLayer.setOpening_credit(CommonUtil.roundNumber(gp_opening_credits));
					secondLayer.setOpening_debit(CommonUtil.roundNumber(gp_opening_debits));
					secondLayer.setCurrent_credit(CommonUtil.roundNumber(gp_current_credits));
					secondLayer.setCurrent_debit(CommonUtil.roundNumber(gp_current_debits));
					secondLayer.setBalance_credit(CommonUtil.roundNumber(gp_opening_credits+gp_current_credits));
					secondLayer.setBalance_debit(CommonUtil.roundNumber(gp_opening_debits+gp_current_debits));
//					secondLayer.setSubList(secondList);
					firstList.add(secondLayer);
					
				}
				
//				resultList.add(mainObj);
				
			}
			
			commit();
			
			return resultList;
			
		} catch (Exception e) {
			// TODO: handle exception
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List showBalancesReport(Date start_date, Date end_date, long office_id, long organization_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			AcctReportMainBean mainObj;
			AcctReportMainBean secondLayer;
			KeyValue objKeyValue;
			Iterator iter2;
			GroupModel grpObj;
			double gp_current_debits=0,gp_current_credits=0,gp_opening_debits=0,gp_opening_credits=0;
			AcctReportMainBean thirdLayer;
			LedgerModel ledgerObj;
			Object obj_current_debits,obj_current_credits,obj_opening_debits,obj_opening_credits;
			double current_debits=0,current_credits=0,opening_debits=0,opening_credits=0;
			Iterator iter1=SConstants.actClassList.iterator();
			while (iter1.hasNext()) {
				objKeyValue = (KeyValue) iter1.next();
				
				mainObj=new AcctReportMainBean();
				mainObj.setName(objKeyValue.getValue());
				
				mainObj.setType('F');
				mainObj.setId(objKeyValue.getKey());
				
				List firstList=new ArrayList();
				
//				resultList.add(mainObj);
				
				iter2=getAllGroupsUnderClass(objKeyValue.getKey(), organization_id).iterator();
				while (iter2.hasNext()) {
					
					secondLayer=new AcctReportMainBean();
					
					secondLayer.setType('S');
					
					grpObj = (GroupModel) iter2.next();
					
					secondLayer.setId(grpObj.getId());
					secondLayer.setName(grpObj.getName());
					
					gp_current_debits=0;gp_current_credits=0;gp_opening_debits=0;gp_opening_credits=0;
					Iterator iter3=getAllActiveLedgerNamesUnderGroup(grpObj.getId(),office_id).iterator();
					while (iter3.hasNext()) {
						
						thirdLayer=new AcctReportMainBean();
						
						thirdLayer.setType('T');
						
						ledgerObj = (LedgerModel) iter3.next();
						
						thirdLayer.setId(ledgerObj.getId());
						thirdLayer.setName(ledgerObj.getName());
						
						
						obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.toAcct.id=:actid and a.date between :stdt and :enddt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).setParameter("enddt", end_date).uniqueResult();
						obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.fromAcct.id=:actid and a.date between :stdt and :enddt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).setParameter("enddt", end_date).uniqueResult();
						
						obj_opening_debits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.toAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).uniqueResult();
						obj_opening_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.fromAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", start_date).uniqueResult();
						
						current_debits=0;current_credits=0;opening_debits=0;opening_credits=0;
						if(obj_current_debits!=null)
							current_debits=(Double) obj_current_debits;
						if(obj_current_credits!=null)
							current_credits=(Double) obj_current_credits;
						if(obj_opening_debits!=null)
							opening_debits=(Double) obj_opening_debits;
						if(obj_opening_credits!=null)
							opening_credits=(Double) obj_opening_credits;
						
						gp_current_debits+=current_debits;
						gp_current_credits+=current_credits;
						gp_opening_debits+=opening_debits;
						gp_opening_credits+=opening_credits;
						thirdLayer.setOpening_credit(CommonUtil.roundNumber(opening_credits));
						thirdLayer.setOpening_debit(CommonUtil.roundNumber(opening_debits));
						thirdLayer.setCurrent_credit(CommonUtil.roundNumber(current_credits));
						thirdLayer.setCurrent_debit(CommonUtil.roundNumber(current_debits));
						thirdLayer.setBalance_credit(CommonUtil.roundNumber(opening_credits+current_credits));
						thirdLayer.setBalance_debit(CommonUtil.roundNumber(opening_debits+current_debits));
//						resultList.add(thirdLayer);
					}
//					secondLayer.setOpening_credit(CommonUtil.roundNumber(gp_opening_credits));
//					secondLayer.setOpening_debit(CommonUtil.roundNumber(gp_opening_debits));
//					secondLayer.setCurrent_credit(CommonUtil.roundNumber(gp_current_credits));
//					secondLayer.setCurrent_debit(CommonUtil.roundNumber(gp_current_debits));
					secondLayer.setBalance_credit(CommonUtil.roundNumber(gp_opening_credits+gp_current_credits));
					secondLayer.setBalance_debit(CommonUtil.roundNumber(gp_opening_debits+gp_current_debits));
					resultList.add(secondLayer);
//					secondLayer.setSubList(secondList);
					
//					firstList.add(secondLayer);
					
				}
				
//				resultList.add(mainObj);
				
			}
			
			commit();
			
			return resultList;
			
		} catch (Exception e) {
			// TODO: handle exception
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List showBalancesReportForGroup(Date start_date, Date end_date, long office_id, long organization_id,long id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			AcctReportMainBean secondLayer;
			GroupModel grpObj;
			AcctReportMainBean thirdLayer;
			LedgerModel ledgerObj;
			Object obj_current_debits,obj_current_credits,obj_opening_debits,obj_opening_credits;
			double current_debits=0,current_credits=0,opening_debits=0,opening_credits=0;
			Iterator iter1=SConstants.actClassList.iterator();
					
			secondLayer=new AcctReportMainBean();
			
			secondLayer.setType('S');
			
			grpObj = (GroupModel) getSession().get(GroupModel.class, id);
			
			secondLayer.setId(grpObj.getId());
			secondLayer.setName(grpObj.getName());
			
			Iterator iter3=getAllActiveLedgerNamesUnderGroup(grpObj.getId(),office_id).iterator();
			while (iter3.hasNext()) {
				
				thirdLayer=new AcctReportMainBean();
				
				thirdLayer.setType('T');
				
				ledgerObj = (LedgerModel) iter3.next();
				
				thirdLayer.setId(ledgerObj.getId());
				thirdLayer.setName(ledgerObj.getName());
				
				
				obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.toAcct.id=:actid and a.date between :stdt and :enddt").setParameter("actid", ledgerObj.getId())
							.setParameter("stdt", start_date).setParameter("enddt", end_date).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.id=:actid and a.date between :stdt and :enddt").setParameter("actid", ledgerObj.getId())
							.setParameter("stdt", start_date).setParameter("enddt", end_date).uniqueResult();
				
				obj_opening_debits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.toAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
							.setParameter("stdt", start_date).uniqueResult();
				obj_opening_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
							.setParameter("stdt", start_date).uniqueResult();
				
				current_debits=0;current_credits=0;opening_debits=0;opening_credits=0;
				if(obj_current_debits!=null)
					current_debits=(Double) obj_current_debits;
				if(obj_current_credits!=null)
					current_credits=(Double) obj_current_credits;
				if(obj_opening_debits!=null)
					opening_debits=(Double) obj_opening_debits;
				if(obj_opening_credits!=null)
					opening_credits=(Double) obj_opening_credits;
				
				thirdLayer.setOpening_credit(CommonUtil.roundNumber(opening_credits));
				thirdLayer.setOpening_debit(CommonUtil.roundNumber(opening_debits));
				thirdLayer.setCurrent_credit(CommonUtil.roundNumber(current_credits));
				thirdLayer.setCurrent_debit(CommonUtil.roundNumber(current_debits));
				thirdLayer.setBalance_credit(CommonUtil.roundNumber(opening_credits+current_credits));
				thirdLayer.setBalance_debit(CommonUtil.roundNumber(opening_debits+current_debits));
				resultList.add(thirdLayer);
			}
			commit();
			
			return resultList;
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	

	
	@SuppressWarnings("rawtypes")
	public List getAllActiveLedgerNamesUnderGroup(long group_id, long office_id) throws Exception {
		List lst=null;
		try {
			lst = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)" +
					" from LedgerModel where group.id=:grpid and status=:val and office.id=:ofc")
					.setParameter("grpid", group_id).setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
		
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public List getAllGroupsUnderClass(long class_id, long org_id) throws Exception {
		List lst=null;
		try {
			lst = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id,name) " +
					" from GroupModel where organization.id=:org_id and status=:sts and account_class_id=:cls")
					.setLong("org_id", org_id).setLong("sts", 1).setLong("cls", class_id).list();
			
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
	}
	

}
