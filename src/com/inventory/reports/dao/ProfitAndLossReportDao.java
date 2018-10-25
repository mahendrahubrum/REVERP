package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.BalanceSheetBean;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author anil
 * @date 04-Nov-2015
 * @Project REVERP
 */

public class ProfitAndLossReportDao extends SHibernate implements Serializable {
	private static final long serialVersionUID = 1747854391264580012L;

	private List resultList=new ArrayList();
	
	WrappedSession session = new SessionUtil().getHttpSession();
	SettingsValuePojo settings = (SettingsValuePojo) session.getAttribute("settings");
	
	
	private int id=1;
	public List getProfitAndLoss(long classId,Date fromDate,Date todate,long officeId, long organizationId){
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
				double[] balances=getGroupBalance(grpMdl.getId(),fromDate, todate, officeId,organizationId);
				mainBean=new BalanceSheetBean();
				mainBean.setLedgerId(grpMdl.getId());
				mainBean.setLedgerName(grpMdl.getName());
				mainBean.setAmount(Math.abs(CommonUtil.roundNumber(balances[0]-balances[1])));
				mainBean.setType(1);
				mainBean.setLevel(grpMdl.getLevel());
				mainBean.setId(id++);
				mainBean.setParentId(0);
				list.add(mainBean);
				parentKeyVal=new KeyValue(grpMdl.getId(),mainBean.getId());
				groupIdsList.add(parentKeyVal);
				
				list.addAll(getLedgersUnderGroup(grpMdl.getId(),fromDate, todate, mainBean.getId(),officeId));
			}
			if(groupIdsList.size()>0)
				list.addAll(getGroupUnderParent(groupIdsList,mainBean.getId(),fromDate,todate,officeId, organizationId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List getGroupUnderParent(List idList,long parentId,Date fromDate,Date toDate,long officeId,long organizationId){
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
						
						double[] balances = getGroupBalance(grpMdl.getId(),fromDate,toDate, officeId,organizationId);
						mainBean = new BalanceSheetBean();
						mainBean.setLedgerId(grpMdl.getId());
						mainBean.setLedgerName(grpMdl.getName());
						mainBean.setAmount(Math.abs(CommonUtil.roundNumber(balances[0]-balances[1])));
						mainBean.setType(1);
						mainBean.setLevel(grpMdl.getLevel());
						mainBean.setId(id++);
						mainBean.setParentId(currentKeyVal.getLongValue());
						list.add(mainBean);
						list.addAll(getLedgersUnderGroup(grpMdl.getId(),fromDate, toDate, mainBean.getId(),officeId));
						parentKeyVal=new KeyValue(grpMdl.getId(),mainBean.getId());
						groupIdsList.add(parentKeyVal);
					}
				}
				if(groupIdsList.size()>0)
					list.addAll(getGroupUnderParent(groupIdsList,mainBean.getId(),fromDate, toDate,officeId, organizationId));
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
	
	private List getLedgersUnderGroup(long groupId,Date fromDate,Date toDate,long parentId,long officeId) {
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
						"where b.toAcct.id=:actid and a.date between :frm and :enddt").setParameter("actid", ledg.getId())
						.setParameter("frm", fromDate).setParameter("enddt", toDate).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
						"where b.fromAcct.id=:actid and a.date between :frm and :enddt").setParameter("actid", ledg.getId())
							.setParameter("frm", fromDate).setParameter("enddt", toDate).uniqueResult();
				current_credits=0;current_debits=0;
				if(obj_current_debits!=null)
					current_debits=(Double) obj_current_debits;
				if(obj_current_credits!=null)
					current_credits=(Double) obj_current_credits;
				
				mainBean = new BalanceSheetBean();
				mainBean.setLedgerId(ledg.getId());
				mainBean.setLedgerName(ledg.getName());
				mainBean.setAmount(Math.abs(CommonUtil.roundNumber(current_debits-current_credits)));
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
	
	private void getGroupWiseBalance(List grpList,Date fromDate,Date toDate, long officeId){
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
							"where b.toAcct.group.id=:actid and a.date between :frm and :enddt  and b.toAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
									.setParameter("frm", fromDate).setParameter("enddt", toDate).setParameter("ofc", officeId).uniqueResult();
					obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.fromAcct.group.id=:actid and a.date between :frm and :enddt  and b.fromAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
								.setParameter("frm", fromDate).setParameter("enddt", toDate).setParameter("ofc", officeId).uniqueResult();
					
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
				getGroupWiseBalance(grpIdsList,fromDate, toDate, officeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private double[] getGroupBalance(long groupId,Date fromDate,Date toDate, long officeId,long organizationId){
		double[] lis=new double[2];
		try {
				Object obj_current_debits,obj_current_credits;
				double current_credits=0,current_debits=0;
				GroupModel grpMdl = (GroupModel) getSession().get(GroupModel.class,groupId);
					
				obj_current_debits= getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.toAcct.group.id=:actid and a.date between :frm and :enddt   and b.toAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
							.setParameter("frm", fromDate).setParameter("enddt", toDate).setParameter("ofc", officeId).uniqueResult();
				obj_current_credits=(Double) getSession().createQuery("select sum(b.amount) from TransactionModel a join a.transaction_details_list b " +
							"where b.fromAcct.group.id=:actid and a.date between :frm and :enddt  and b.fromAcct.office.id=:ofc").setParameter("actid", grpMdl.getId())
							.setParameter("frm", fromDate).setParameter("enddt", toDate).setParameter("ofc", officeId).uniqueResult();
					
				if(obj_current_debits!=null)
					current_debits=(Double) obj_current_debits;
				if(obj_current_credits!=null)
					current_credits=(Double) obj_current_credits;
				
				
				List grpIdList=new ArrayList();
				grpIdList.add(grpMdl.getId());
				credits=0;debits=0;
				getGroupWiseBalance(grpIdList, fromDate,toDate, officeId);
					
				lis[0]=current_debits+debits;
				lis[1]=current_credits+credits;
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lis;
	}

	public BalanceSheetBean getAllSalesDetails(Date fromDate,Date toDate, long officeId, long organizationIDb) {
		BalanceSheetBean mainBean=new BalanceSheetBean();
		
		try {
			double retAmount=0;
			double discountAmount=0;
			double amount=(Double) getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from SalesModel where date between :frmDt and :toDt and office.id=:ofc")
					.setParameter("ofc", officeId).setParameter("frmDt", fromDate).setParameter("toDt", toDate).uniqueResult();
			try {
				retAmount=(Double) getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from SalesReturnModel where date between :frmDt and :toDt and office.id=:ofc")
					.setParameter("ofc", officeId).setParameter("frmDt", fromDate).setParameter("toDt", toDate).uniqueResult();
			} catch (Exception e) {}
			try {
				discountAmount=new LedgerDao().getLedgerBalanceBetweenDates(fromDate, toDate, settings.getSALES_DESCOUNT_ACCOUNT());
			} catch (Exception e) {}
			
			mainBean = new BalanceSheetBean();
			mainBean.setLedgerId(0);
			mainBean.setLedgerName("Net Sales");
			mainBean.setAmount(CommonUtil.roundNumber(Math.abs(amount-retAmount+discountAmount)));
			mainBean.setType(0);
			mainBean.setLevel(-1);
			mainBean.setId(id++);
			mainBean.setParentId(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainBean;
	}
	
	public BalanceSheetBean getAllPurchaseDetails(Date fromDate,Date toDate, long officeId, long organizationID) {
		BalanceSheetBean mainBean=new BalanceSheetBean();
		try {
			double retAmount=0,discountAmount=0;
			double amount=(Double) getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from PurchaseModel where date between :frmDt and :toDt and office.id=:ofc")
					.setParameter("ofc", officeId).setParameter("frmDt", fromDate).setParameter("toDt", toDate).uniqueResult();
			try {
				retAmount=(Double) getSession().createQuery("select coalesce(sum(amount/conversionRate),0) from PurchaseReturnModel where date between :frmDt and :toDt and office.id=:ofc")
					.setParameter("ofc", officeId).setParameter("frmDt", fromDate).setParameter("toDt", toDate).uniqueResult();
			} catch (Exception e) {}
			
			try {
				discountAmount=new LedgerDao().getLedgerBalanceBetweenDates(fromDate, toDate, settings.getPURCHASE_DESCOUNT_ACCOUNT());
			} catch (Exception e) {}
			
			
			mainBean = new BalanceSheetBean();
			mainBean.setLedgerId(0);
			mainBean.setLedgerName("Net Purchases");
			mainBean.setAmount(CommonUtil.roundNumber(amount-retAmount+discountAmount));
			mainBean.setType(0);
			mainBean.setLevel(-1);
			mainBean.setId(id++);
			mainBean.setParentId(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainBean;
	}

	public BalanceSheetBean getStockValue(Date fromDate,Date toDate,long officeId, long organizationID,Date finStartDate) {
		
		BalanceSheetBean mainBean=new BalanceSheetBean();
		CommonMethodsDao dao=new CommonMethodsDao();
		double amount=0;

		try {
			List itemList=getSession().createQuery("select id from ItemModel where office.id=:ofc").setParameter("ofc", officeId).list();
			Iterator iter=itemList.iterator();
			while (iter.hasNext()) {
				amount+=dao.getItemValueBetweenDatesInFinancialYear((Long) iter.next(), 0, fromDate,toDate,finStartDate);
			}
			mainBean = new BalanceSheetBean();
			mainBean.setLedgerId(0);
			mainBean.setLedgerName("Stock Value");
			mainBean.setAmount(CommonUtil.roundNumber(Math.abs(amount)));
			mainBean.setType(0);
			mainBean.setLevel(-1);
			mainBean.setId(id++);
			mainBean.setParentId(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainBean;
	}
	
	public BalanceSheetBean getStockValueTillDate(Date toDate,long officeId, long organizationID) {
		
		BalanceSheetBean mainBean=new BalanceSheetBean();
		CommonMethodsDao dao=new CommonMethodsDao();
		double amount=0;

		try {
			List itemList=getSession().createQuery("select id from ItemModel where office.id=:ofc").setParameter("ofc", officeId).list();
			Iterator iter=itemList.iterator();
			while (iter.hasNext()) {
				amount+=dao.getItemValueTillDate((Long) iter.next(), 0,toDate);
			}
			mainBean = new BalanceSheetBean();
			mainBean.setLedgerId(0);
			mainBean.setLedgerName("Stock Value");
			mainBean.setAmount(CommonUtil.roundNumber(Math.abs(amount)));
			mainBean.setType(0);
			mainBean.setLevel(-1);
			mainBean.setId(id++);
			mainBean.setParentId(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainBean;
	}
}
