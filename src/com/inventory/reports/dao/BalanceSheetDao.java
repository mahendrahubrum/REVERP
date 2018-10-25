package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.bean.BalanceSheetBean;
import com.inventory.reports.bean.BalanceSheetBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class BalanceSheetDao extends SHibernate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4757127499492624783L;
	private List resultList=new ArrayList();
	
	public List getBalanceSheetOld( Date end_date, long office_id, long organization_id) throws Exception {
		
		try {
			
			resultList=new ArrayList();
			
			begin();
			
			Iterator iter1=SConstants.balanceSheetClassList.iterator();
			double asset_total=0, liability_total=0;
			KeyValue objKeyValue;
			AcctReportMainBean mainObj;
			Iterator iter2;
			double classTotal=0;
			AcctReportMainBean secondLayer;
			GroupModel grpObj;
			Object obj_total_debits;
			Object obj_total_credits;
			double current_debits=0,current_credits=0,opening_debits=0,opening_credits=0;
			while (iter1.hasNext()) {
				objKeyValue = (KeyValue) iter1.next();
				
				mainObj=new AcctReportMainBean();
				mainObj.setName(objKeyValue.getValue());
				mainObj.setType('F');
				resultList.add(mainObj);
				
//				List firstList=new ArrayList();
				
				iter2=getAllGroupsUnderClass(objKeyValue.getKey(),
						organization_id).iterator();
				classTotal=0;
				while (iter2.hasNext()) {
					
					secondLayer=new AcctReportMainBean();
					
					grpObj = (GroupModel) iter2.next();
					
					secondLayer.setId(grpObj.getId());
					secondLayer.setName(grpObj.getName());
					secondLayer.setType('S');
					
					obj_total_debits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where (b.toAcct.group.id=:gpid or b.toAcct.group.parent_id=:gpid) and a.date<=:enddt and b.toAcct.office.id=:ofc").setParameter("gpid", grpObj.getId())
								.setParameter("enddt", end_date).setLong("ofc", office_id).uniqueResult();
					obj_total_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where (b.fromAcct.group.id=:gpid or b.fromAcct.group.parent_id=:gpid) and a.date<=:enddt and b.toAcct.office.id=:ofc").setParameter("gpid", grpObj.getId())
								.setParameter("enddt", end_date).setLong("ofc", office_id).uniqueResult();
					
					current_debits=0;current_credits=0;opening_debits=0;opening_credits=0;
					if(obj_total_debits!=null)
						opening_debits=(Double) obj_total_debits;
					if(obj_total_credits!=null)
						opening_credits=(Double) obj_total_credits;
						
						
					secondLayer.setAmount(CommonUtil.roundNumber(opening_debits-opening_credits));
					
					classTotal+=opening_debits-opening_credits;
					
					resultList.add(secondLayer);
					
				}
				
				secondLayer=new AcctReportMainBean();
				secondLayer.setId(0);
				secondLayer.setType('M');
				
				if(objKeyValue.getKey()==1)
					secondLayer.setName("Total Assets");
				else
					secondLayer.setName("Total Liabilities");
				secondLayer.setAmount(CommonUtil.roundNumber(classTotal));
				resultList.add(secondLayer);
				
				
//				mainObj.setSubList(firstList);
				
				mainObj.setAmount(CommonUtil.roundNumber(classTotal));
				
				
				
				if(objKeyValue.getKey()==1){
					asset_total=classTotal;
				}
				else {
					liability_total=classTotal;
				}
				
			}
			
			
			
			mainObj=new AcctReportMainBean();
			mainObj.setName("Calculated Return");
			mainObj.setAmount(CommonUtil.roundNumber(asset_total-liability_total));
			mainObj.setType('M');
			resultList.add(mainObj);
			
			mainObj=new AcctReportMainBean();
			mainObj.setName("Total Liabilities and Equities");
			mainObj.setAmount(CommonUtil.roundNumber(asset_total));
			mainObj.setType('M');
			resultList.add(mainObj);
			
			
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
	
	
	private int id=1;
	public List getBalanceSheet(long classId,Date date,long officeId, long organizationId){
		List list=new ArrayList();
		List groupIdsList=new ArrayList();
		
		try {
			List grpList=getSession().createQuery("from GroupModel where parent_id=0 and account_class_id=:cls and organization.id=:org order by name")
					.setParameter("cls", classId).setParameter("org", organizationId).list();
			
			GroupModel grpMdl;
			KeyValue parentKeyVal;
			Iterator grpIter=grpList.iterator();
			BalanceSheetBean mainBean=null;
			while (grpIter.hasNext()) {
				grpMdl = (GroupModel) grpIter.next();
				double[] balances=getGroupBalance(grpMdl.getId(), date, officeId,organizationId);
				mainBean=new BalanceSheetBean();
				mainBean.setLedgerId(grpMdl.getId());
				mainBean.setLedgerName(grpMdl.getName());
				mainBean.setAmount(CommonUtil.roundNumber(balances[0]-balances[1]));
				mainBean.setType(1);
				mainBean.setLevel(grpMdl.getLevel());
				mainBean.setId(id++);
				mainBean.setParentId(0);
				list.add(mainBean);
				parentKeyVal=new KeyValue(grpMdl.getId(),mainBean.getId());
				groupIdsList.add(parentKeyVal);
				
				list.addAll(getLedgersUnderGroup(grpMdl.getId(), date, mainBean.getId(),officeId));
			}
			if(groupIdsList.size()>0)
				list.addAll(getGroupUnderParent(groupIdsList,mainBean.getId(),date,officeId, organizationId));
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
			BalanceSheetBean mainBean=null;
				while (grpIter.hasNext()) {
					currentKeyVal=(KeyValue) grpIter.next();
					grpList=getSession().createQuery("from GroupModel where parent_id =:prnt  order by name")
							.setParameter("prnt", currentKeyVal.getKey()).list();
					
					subIter=grpList.iterator();
					while (subIter.hasNext()) {
						grpMdl = (GroupModel) subIter.next();
						
						double[] balances = getGroupBalance(grpMdl.getId(),	date, officeId,organizationId);
						mainBean = new BalanceSheetBean();
						mainBean.setLedgerId(grpMdl.getId());
						mainBean.setLedgerName(grpMdl.getName());
						mainBean.setAmount(CommonUtil.roundNumber(balances[0]-balances[1]));
						mainBean.setType(1);
						mainBean.setLevel(grpMdl.getLevel());
						mainBean.setId(id++);
						mainBean.setParentId(currentKeyVal.getLongValue());
						list.add(mainBean);
						list.addAll(getLedgersUnderGroup(grpMdl.getId(), date, mainBean.getId(),officeId));
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
//				BalanceSheetBean subBean;
//				Iterator grpIter=list.iterator();
//				while (grpIter.hasNext()) {
//					subBean=(BalanceSheetBean) grpIter.next();
//					list.addAll(getGroupUnderParent(subBean.getLedgerId(), end_date, officeId, subBean.getParentId()));
//				}
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	private List getLedgersUnderGroup(long groupId,Date date,long parentId,long officeId) {
		List list=new ArrayList();
		try {
			List ledgerList=getSession().createQuery("from LedgerModel where group.id=:prnt and office.id=:ofc  order by name")
					.setParameter("ofc", officeId).setParameter("prnt", groupId).list();
			Iterator grpIter=ledgerList.iterator();
			Object obj_current_debits,obj_current_credits;
			double current_credits=0,current_debits=0;
			BalanceSheetBean mainBean=null;
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
				
				mainBean = new BalanceSheetBean();
				mainBean.setLedgerId(ledg.getId());
				mainBean.setLedgerName(ledg.getName());
				mainBean.setAmount(CommonUtil.roundNumber(current_debits-current_credits));
				mainBean.setType(3);
				mainBean.setLevel(-1);
				mainBean.setId(id++);
				mainBean.setParentId(parentId);
				
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
	
	public List getAllActiveLedgerNamesUnderGroup(long group_id) throws Exception {
		List lst=null;
		try {
			lst = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)" +
					" from LedgerModel where group.id=:grpid and status=:val  order by name")
					.setParameter("grpid", group_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
		
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
	}
	
	
	public List getAllGroupsUnderClass(long class_id, long organization_id) throws Exception {
		List lst=null;
		try {
			lst = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id,name) " +
					" from GroupModel where organization.id=:organization_id and status=:sts and account_class_id=:cls  order by name")
					.setLong("organization_id", organization_id).setLong("sts", 1).setLong("cls", class_id).list();
			
			return lst;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
	}
	

}
