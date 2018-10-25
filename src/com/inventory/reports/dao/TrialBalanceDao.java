package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.bean.TrialBalanceBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class TrialBalanceDao extends SHibernate implements Serializable{
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getTrialBalance( Date end_date, long office_id, long organization_id) throws Exception {
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
								"where b.toAcct.id=:actid and a.date <= :enddt").setParameter("actid", ledgerObj.getId())
								.setParameter("enddt", end_date).uniqueResult();
						obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.fromAcct.id=:actid and a.date <=:enddt").setParameter("actid", ledgerObj.getId())
								.setParameter("enddt", end_date).uniqueResult();
						
						obj_opening_debits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.toAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", end_date).uniqueResult();
						obj_opening_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
								"where b.fromAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
									.setParameter("stdt", end_date).uniqueResult();
						
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
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	
	private int id=1;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List showTrialBalance( Date end_date, long office_id, long organization_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			List classList =SConstants.account_parent_groups.classList;	
			
			TrialBalanceBean mainBean=null;
			Iterator classIter=classList.iterator();
			Object obj_current_debits,obj_current_credits;
			double current_debits=0,current_credits=0;
			KeyValue classVal;
			while (classIter.hasNext()) {
				classVal = (KeyValue) classIter.next();
				current_debits=0;
				current_credits=0;
				
				obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.toAcct.group.account_class_id=:actid and a.date <=:enddt  and b.toAcct.office.id=:ofc").setParameter("actid", classVal.getKey())
							.setParameter("enddt", end_date).setParameter("ofc", office_id).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.group.account_class_id=:actid and a.date <=:enddt and b.fromAcct.office.id=:ofc").setParameter("actid", classVal.getKey())
							.setParameter("enddt", end_date).setParameter("ofc", office_id).uniqueResult();
				
				if(obj_current_debits!=null)
					current_debits=(Double) obj_current_debits;
				if(obj_current_credits!=null)
					current_credits=(Double) obj_current_credits;
				
				mainBean=new TrialBalanceBean();
				mainBean.setLedgerId(classVal.getKey());
				mainBean.setLedgerName(classVal.getValue());
				mainBean.setDebitAmount(CommonUtil.roundNumber(current_debits));
				mainBean.setCreditAmount(CommonUtil.roundNumber(current_credits));
				mainBean.setType(0);
				mainBean.setLevel(0);
				mainBean.setId(id++);
				mainBean.setParentId(0);
				mainBean.setClassId(classVal.getKey());
				resultList.add(mainBean);
				
				resultList.addAll(getGroupUnderClass(mainBean.getLedgerId(),end_date,office_id,mainBean.getId(),organization_id));
			}
			
			commit();
			
			return resultList;
			
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
	}
	
	List parentIdList=new ArrayList();
	public List getGroupUnderClass(long classId,Date end_date,long officeId,long parentId, long organizationId){
		List list=new ArrayList();
		List groupIdsList=new ArrayList();
		
		try {
			List grpList=getSession().createQuery("from GroupModel where parent_id=0 and account_class_id=:cls and organization.id=:org order by name")
					.setParameter("cls", classId).setParameter("org", organizationId).list();
			
			GroupModel grpMdl;
			KeyValue parentKeyVal;
			Iterator grpIter=grpList.iterator();
			TrialBalanceBean mainBean=null;
			while (grpIter.hasNext()) {
				grpMdl = (GroupModel) grpIter.next();
				double[] balances=getGroupBalance(grpMdl.getId(), end_date, officeId,organizationId);
				mainBean=new TrialBalanceBean();
				mainBean.setLedgerId(grpMdl.getId());
				mainBean.setLedgerName(grpMdl.getName());
				mainBean.setDebitAmount(CommonUtil.roundNumber(balances[0]));
				mainBean.setCreditAmount(CommonUtil.roundNumber(balances[1]));
				mainBean.setType(1);
				mainBean.setLevel(grpMdl.getLevel());
				mainBean.setId(id++);
				mainBean.setParentId(parentId);
				mainBean.setClassId(classId);
				list.add(mainBean);
				parentKeyVal=new KeyValue(grpMdl.getId(),mainBean.getId());
				groupIdsList.add(parentKeyVal);
				
				list.addAll(getLedgersUnderGroup(grpMdl.getId(), end_date, mainBean.getId(),officeId,mainBean.getClassId()));
			}
			if(groupIdsList.size()>0)
				list.addAll(getGroupUnderParent(groupIdsList,mainBean.getId(),end_date,officeId, organizationId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List getGroupUnderParent(List idList,long parentId,Date date,long officeId,long organizationId){
		boolean continueFlag=true;
		List list=new ArrayList();
		List groupIdsList=new ArrayList();
		List grpList=null;
		try {
			
			if(idList!=null&&idList.size()>0){
//			List grpList=getSession().createQuery("from GroupModel where parent_id in (:prnt)")
//					.setParameterList("prnt", idList).list();
			
//			if(grpList!=null&&grpList.size()>0){
			GroupModel grpMdl;
			KeyValue parentKeyVal;
			KeyValue currentKeyVal;
			Iterator subIter;
			Iterator grpIter=idList.iterator();
			TrialBalanceBean mainBean=null;
				while (grpIter.hasNext()) {
					currentKeyVal=(KeyValue) grpIter.next();
					grpList=getSession().createQuery("from GroupModel where parent_id =:prnt  order by name")
							.setParameter("prnt", currentKeyVal.getKey()).list();
					
					subIter=grpList.iterator();
					while (subIter.hasNext()) {
						grpMdl = (GroupModel) subIter.next();
						
						double[] balances = getGroupBalance(grpMdl.getId(),	date, officeId,organizationId);
						mainBean = new TrialBalanceBean();
						mainBean.setLedgerId(grpMdl.getId());
						mainBean.setLedgerName(grpMdl.getName());
						mainBean.setDebitAmount(CommonUtil.roundNumber(balances[0]));
						mainBean.setCreditAmount(CommonUtil.roundNumber(balances[1]));
						mainBean.setType(1);
						mainBean.setLevel(grpMdl.getLevel());
						mainBean.setId(id++);
						mainBean.setParentId(currentKeyVal.getLongValue());
						mainBean.setClassId(grpMdl.getAccount_class_id());
						list.add(mainBean);
						list.addAll(getLedgersUnderGroup(grpMdl.getId(), date, mainBean.getId(),officeId,mainBean.getClassId()));
						parentKeyVal=new KeyValue(grpMdl.getId(),mainBean.getId());
						groupIdsList.add(parentKeyVal);
					}
				}
				if(groupIdsList.size()>0)
					list.addAll(getGroupUnderParent(groupIdsList,mainBean.getId(),date,officeId, organizationId));
			}
//			}
//			}else{
//				continueFlag=false;
//			}
//			if(continueFlag){
//				TrialBalanceBean subBean;
//				Iterator grpIter=list.iterator();
//				while (grpIter.hasNext()) {
//					subBean=(TrialBalanceBean) grpIter.next();
//					list.addAll(getGroupUnderParent(subBean.getLedgerId(), end_date, officeId, subBean.getParentId()));
//				}
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	private List getLedgersUnderGroup(long groupId,Date date,long parentId,long officeId,long classId) {
		List list=new ArrayList();
		try {
			List ledgerList=getSession().createQuery("from LedgerModel where group.id=:prnt and office.id=:ofc order by name")
					.setParameter("ofc", officeId).setParameter("prnt", groupId).list();
			Iterator grpIter=ledgerList.iterator();
			Object obj_current_debits,obj_current_credits;
			double current_credits=0,current_debits=0;
			TrialBalanceBean mainBean=null;
			LedgerModel ledg;
			while (grpIter.hasNext()) {
				ledg=(LedgerModel) grpIter.next();
						
				obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.toAcct.id=:actid and a.date <=:enddt").setParameter("actid", ledg.getId())
							.setParameter("enddt", date).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.id=:actid and a.date <=:enddt").setParameter("actid", ledg.getId())
							.setParameter("enddt", date).uniqueResult();
				current_credits=0;current_debits=0;
				if(obj_current_debits!=null)
					current_debits=(Double) obj_current_debits;
				if(obj_current_credits!=null)
					current_credits=(Double) obj_current_credits;
				
				mainBean = new TrialBalanceBean();
				mainBean.setLedgerId(ledg.getId());
				mainBean.setLedgerName(ledg.getName());
				mainBean.setDebitAmount(CommonUtil.roundNumber(current_debits));
				mainBean.setCreditAmount(CommonUtil.roundNumber(current_credits));
				mainBean.setType(3);
				mainBean.setLevel(-1);
				mainBean.setId(id++);
				mainBean.setParentId(parentId);
				mainBean.setClassId(classId);
				
				list.add(mainBean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	double credits=0;
	double debits=0;
	
	private void getGroupWiseBalance(List grpList,Date endDate, long officeId){
		try {
			
			List grpIdsList=new ArrayList();
			
			if(grpList.size()>0){
			List resList=getSession().createQuery("from GroupModel where parent_id in (:prnt) ")
					.setParameterList("prnt", grpList).list();
			
			if(resList!=null&&resList.size()>0){
			
				Iterator grpIter=resList.iterator();
				Object obj_current_debits,obj_current_credits;
				double current_credits=0,current_debits=0;
				GroupModel grpMdl;
				while (grpIter.hasNext()) {
					grpMdl = (GroupModel) grpIter.next();
					
					obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.toAcct.group.id=:actid and a.date <=:enddt  and b.toAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
								.setParameter("enddt", endDate).setParameter("ofc", officeId).uniqueResult();
					obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.fromAcct.group.id=:actid and a.date <=:enddt and b.fromAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
								.setParameter("enddt", endDate).setParameter("ofc", officeId).uniqueResult();
					
					if(obj_current_debits!=null)
						current_debits+=(Double) obj_current_debits;
					if(obj_current_credits!=null)
						current_credits+=(Double) obj_current_credits;
					
					if(!grpIdsList.contains(grpMdl.getId()))
						grpIdsList.add(grpMdl.getId());
					
				}
				debits+=current_debits;
				credits+=current_credits;
			}
			if(grpList!=null&&grpList.size()>0)
				getGroupWiseBalance(grpIdsList, endDate, officeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private double[] getGroupBalance(long groupId,Date endDate, long officeId,long organizationId){
		double[] lis=new double[2];
		try {
				Object obj_current_debits,obj_current_credits;
				double current_credits=0,current_debits=0;
				GroupModel grpMdl = (GroupModel) getSession().get(GroupModel.class,groupId);
					
				obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.toAcct.group.id=:actid and a.date <=:enddt  and b.toAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
							.setParameter("enddt", endDate).setParameter("ofc", officeId).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.fromAcct.group.id=:actid and a.date <=:enddt and b.fromAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
							.setParameter("enddt", endDate).setParameter("ofc", officeId).uniqueResult();
					
				if(obj_current_debits!=null)
					current_debits=(Double) obj_current_debits;
				if(obj_current_credits!=null)
					current_credits=(Double) obj_current_credits;
				
				
				List grpIdList=new ArrayList();
				grpIdList.add(grpMdl.getId());
				credits=0;debits=0;
				getGroupWiseBalance(grpIdList, endDate, officeId);
					
				lis[0]=current_debits+debits;
				lis[1]=current_credits+credits;
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lis;
	}
	
	
	
	private double[] getGroupWiseBalance(long groupId,Date endDate, long officeId,long organizationId){
		double[] lis=new double[2];
		try {
			
			List resList=getSession().createQuery("from GroupModel where parent_id=:prnt or id=:prnt order by name")
					.setParameter("prnt", groupId).list();
			
//			int level=(Integer) getSession().createQuery("select max(level) from GroupModel where organization.id=:org")
//					.setParameter("org", organizationId).uniqueResult();
//			List resList=new ArrayList();
//			for(int i=0;i<=level;i++){
//				resList.addAll(getSession().createQuery("from GroupModel where parent_id=:prnt or id=:prnt and level=:level")
//						.setParameter("prnt", groupId).setParameter("level", level).list());
//				
//			}
			
			if(resList!=null&&resList.size()>0){
				
				Iterator grpIter=resList.iterator();
				Object obj_current_debits,obj_current_credits;
				double current_credits=0,current_debits=0;
				GroupModel grpMdl;
				while (grpIter.hasNext()) {
					grpMdl = (GroupModel) grpIter.next();
					
					System.out.println(grpMdl.getName());
					
					obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.toAcct.group.id=:actid and a.date <=:enddt  and b.toAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
							.setParameter("enddt", endDate).setParameter("ofc", officeId).uniqueResult();
					obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.fromAcct.group.id=:actid and a.date <=:enddt and b.fromAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
							.setParameter("enddt", endDate).setParameter("ofc", officeId).uniqueResult();
					
					if(obj_current_debits!=null)
						current_debits+=(Double) obj_current_debits;
					if(obj_current_credits!=null)
						current_credits+=(Double) obj_current_credits;
					
				}
				lis[0]=current_debits;
				lis[1]=current_credits;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lis;
	}
	
	
	
	public List getGroupUnderParent(long groupId,Date end_date,long officeId,long parentId,long organizationId){
		boolean continueFlag=true;
		List list=new ArrayList();
		try {
			List grpList=getSession().createQuery("from GroupModel where parent_id=:prnt order by name")
					.setParameter("prnt", groupId).list();
			
			if(grpList!=null&&grpList.size()>0){
			GroupModel grpMdl;
			Iterator grpIter=grpList.iterator();
			TrialBalanceBean mainBean=null;
				while (grpIter.hasNext()) {
					grpMdl = (GroupModel) grpIter.next();
					double[] balances = getGroupWiseBalance(grpMdl.getId(),	end_date, officeId,organizationId);
					mainBean = new TrialBalanceBean();
					mainBean.setLedgerId(grpMdl.getId());
					mainBean.setLedgerName(grpMdl.getName());
					mainBean.setDebitAmount(CommonUtil.roundNumber(balances[0]));
					mainBean.setCreditAmount(CommonUtil.roundNumber(balances[1]));
					mainBean.setType(1);
					mainBean.setLevel(grpMdl.getLevel());
					mainBean.setId(id++);
					mainBean.setParentId(parentId);
					list.add(mainBean);
				}
			}else{
				continueFlag=false;
			}
//			if(continueFlag){
//				TrialBalanceBean subBean;
//				Iterator grpIter=list.iterator();
//				while (grpIter.hasNext()) {
//					subBean=(TrialBalanceBean) grpIter.next();
//					list.addAll(getGroupUnderParent(subBean.getLedgerId(), end_date, officeId, subBean.getParentId()));
//				}
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List showTrialBalanceForGroup(Date end_date, long office_id, long organization_id,long id) throws Exception {
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
						"where b.toAcct.id=:actid and a.date <=:enddt").setParameter("actid", ledgerObj.getId())
							.setParameter("enddt", end_date).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.id=:actid and a.date <=:enddt").setParameter("actid", ledgerObj.getId())
							.setParameter("enddt", end_date).uniqueResult();
				
				obj_opening_debits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.toAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
							.setParameter("stdt", end_date).uniqueResult();
				obj_opening_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.id=:actid and a.date<:stdt").setParameter("actid", ledgerObj.getId())
							.setParameter("stdt", end_date).uniqueResult();
				
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
					" from LedgerModel where group.id=:grpid and status=:val and office.id=:ofc order by name")
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
					" from GroupModel where organization.id=:org_id and status=:sts and account_class_id=:cls order by name")
					.setLong("org_id", org_id).setLong("sts", 1).setLong("cls", class_id).list();
			
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
	}
	

}
