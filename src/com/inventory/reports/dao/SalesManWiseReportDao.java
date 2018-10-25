package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.SalesManWiseReportBean;
import com.inventory.sales.model.SalesModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class SalesManWiseReportDao extends SHibernate implements Serializable {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getSalesManWiseReport(long sales_man, long customer, Date start, Date end, long office)throws Exception {
		
		List resultList=new ArrayList();
		
		try {
			String condition="";
			begin();
			if(sales_man!=0)
				condition+=" and responsible_person="+sales_man;
			if(customer!=0)
				condition+=" and customer.id="+customer;
			UserModel user=(UserModel)getSession().createQuery("from UserModel where loginId.id="+sales_man).uniqueResult();
			List saleList=getSession().createQuery("from SalesModel where date between :start and :end and office.id=:office "+condition+" order by date ASC")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			Iterator itr=saleList.iterator();
			double amount=0,paid=0,balance=0;
			while (itr.hasNext()) {
				SalesModel sale=(SalesModel)itr.next();
				amount+=CommonUtil.roundNumber(sale.getAmount());
				paid+=CommonUtil.roundNumber(sale.getPayment_amount()+ sale.getPaid_by_payment());
				balance+=CommonUtil.roundNumber(sale.getAmount()- sale.getPayment_amount()- sale.getPaid_by_payment());
			}
			String name="";
			if(user!=null)
				name=user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name();
			resultList.add(new SalesManWiseReportBean(	sales_man, 
														name, 
														CommonUtil.roundNumber(amount),
														CommonUtil.roundNumber(paid),
														CommonUtil.roundNumber(balance)));
//			SalesManWiseReportBean
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		return resultList;
	}

	
	
	@SuppressWarnings("rawtypes")
	public List getSalesManList(long office, long salesman)throws Exception {
		
		List resultList=new ArrayList();
		
		try {
			String condition="";
			String condition1="";
			begin();
			if(salesman!=0){
				condition+=" and b.login_id="+salesman;
				condition1+=" and a.loginId.id="+salesman;
			}
				
			
			List idLst=getSession().createQuery("select b.login_id from SalesManMapModel b where b.office_id=:ofc and b.option_id=:opt "+condition)
					.setLong("ofc", salesman).setInteger("opt", 0).list();
			
			if(idLst!=null && idLst.size()>0) 
				resultList= getSession().createQuery("from UserModel a where a.loginId.id in (:lst) and a.loginId.status!=1 "+condition1+" order by a.first_name")
				.setParameterList("lst", idLst).list();
			else
				resultList= getSession().createQuery("from UserModel a where a.loginId.office.id=:ofc and loginId.userType.id>1 and a.loginId.status!=1 "+condition1+" order by a.first_name")
						.setLong("ofc", office).list(); 
			
			
			
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		return resultList;
	}

	
	
	@SuppressWarnings("rawtypes")
	public List getSalesMansSales(long salesman, long office, long customer,Date start, Date end)throws Exception {
		
		List resultList=new ArrayList();
		
		try {String condition="";
			begin();
			if(salesman!=0)
				condition+=" and responsible_person="+salesman;
			if(customer!=0)
			condition+=" and customer.id="+customer;
			resultList=getSession().createQuery("from SalesModel where date between :start and :end and office.id=:office "+condition+" order by date ASC")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
	}



	public List<Object> getSalesManWiseStockReportOld(long officeId, long salseManId,
			Date fromDtae, Date toDate) throws Exception {
		List subList = new ArrayList();
		List resultList = new ArrayList();
		try {
//			String condition = "";
//			if (salseManId != 0)
//				condition += " and responsible_person=" + salseManId;
			ItemModel itemMdl;
			begin();
			
			List purchRetIdList=getSession().createQuery("select id from PurchaseModel where office.id=:ofc and" +
					" responsible_person=:sal ").setParameter("ofc", officeId)
					.setParameter("sal", salseManId).list();
			List saleRetIdList=getSession().createQuery("select id from SalesReturnModel where office.id=:ofc and" +
					" responsible_person=:sal ").setParameter("ofc", officeId)
					.setParameter("sal", salseManId).list();
			
			Iterator itemIter=getSession().createQuery("from ItemModel where office.id=:ofc  order by name").setParameter("ofc", officeId).list().iterator();
			while (itemIter.hasNext()) {
				itemMdl = (ItemModel) itemIter.next();
				if(itemMdl!=null){
					subList = new ArrayList();

					subList.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesManWiseReportBean(b.item.id,1,'',b.item.name,b.item.unit.symbol,sum(b.qty_in_basic_unit),0.0,0.0,0.0,0.0)" +
						" from PurchaseModel a join a.inventory_details_list b " +
						"where a.office.id=:ofc and a.date between :start and :end and a.responsible_person=:salesman and b.item.id=:item and a.office.id=:ofc group by b.item.id")
						.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
						.setParameter("salesman", salseManId).setParameter("item", itemMdl.getId()).list());
					
					double returnQty=0;
					if(purchRetIdList!=null&&purchRetIdList.size()>0)
						returnQty+=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)" +
							" from PurchaseReturnModel a join a.inventory_details_list b " +
							"where a.office.id=:ofc and a.date between :start and :end and b.order_id in (:orderList) and a.status=1 and b.item.id=:item and a.office.id=:ofc group by b.item.id")
							.setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
							.setParameter("item", itemMdl.getId()).setParameterList("orderList", purchRetIdList).uniqueResult();
							
					if(saleRetIdList!=null&&saleRetIdList.size()>0)
						returnQty+=(Double)getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)" +
								" from PurchaseReturnModel a join a.inventory_details_list b " +
								"where a.office.id=:ofc and a.date between :start and :end and  b.order_id in (:orderList) and a.status=2 and b.item.id=:item and a.office.id=:ofc group by b.item.id")
								.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
								.setParameter("item", itemMdl.getId()).setParameterList("orderList", saleRetIdList).uniqueResult();
					
					subList.add(new SalesManWiseReportBean(itemMdl.getId(),2,"",itemMdl.getName(),itemMdl.getUnit().getSymbol(),0.0,returnQty,0.0,0.0,0.0));
				
					subList.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesManWiseReportBean(b.item.id,3,'',b.item.name,b.item.unit.symbol,0.0,0.0,sum(b.quantity_in_basic_unit),0.0,0.0)" +
						" from SalesModel a join a.inventory_details_list b " +
						"where a.office.id=:ofc and a.date between :start and :end and a.responsible_person=:salesman and b.item.id=:item and a.office.id=:ofc group by b.item.id")
						.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
						.setParameter("salesman", salseManId).setParameter("item", itemMdl.getId()).list());
				
					subList.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesManWiseReportBean(b.item.id,4,'',b.item.name,b.item.unit.symbol,0.0,0.0,0.0,sum(b.quantity_in_basic_unit),0.0)" +
						" from SalesReturnModel a join a.inventory_details_list b " +
						"where a.office.id=:ofc and a.date between :start and :end and a.responsible_person=:salesman and b.item.id=:item and a.office.id=:ofc group by b.item.id")
						.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
						.setParameter("salesman", salseManId).setParameter("item", itemMdl.getId()).list());
					resultList.add(subList);
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
		return resultList;
	}

	
	public List<Object> getSalesManWiseStockReport(long officeId, long salseManId,
			Date fromDtae, Date toDate) throws Exception {
		List resultList = new ArrayList();
		try {
//			String condition = "";
//			if (salseManId != 0)
//				condition += " and responsible_person=" + salseManId;
			ItemModel itemMdl;
			begin();
			
			SalesManWiseReportBean mainBean;
			double purchQty=0;
			double purchRetQty=0;
			double saleQty=0;
			double saleRetQty=0;
			
			List purchRetIdList=getSession().createQuery("select id from PurchaseModel where office.id=:ofc and" +
					" responsible_person=:sal ").setParameter("ofc", officeId)
					.setParameter("sal", salseManId).list();
			List saleRetIdList=getSession().createQuery("select id from SalesReturnModel where office.id=:ofc and" +
					" responsible_person=:sal ").setParameter("ofc", officeId)
					.setParameter("sal", salseManId).list();
			
			Iterator itemIter=getSession().createQuery("from ItemModel where office.id=:ofc  order by name").setParameter("ofc", officeId).list().iterator();
			while (itemIter.hasNext()) {
				itemMdl = (ItemModel) itemIter.next();
				if(itemMdl!=null){
					Object purchRetObj1=null;
					Object purchRetObj2=null;
					Object purchObj=null;
					Object saleObj=null;
					Object saleRetObj=null;
					
					purchQty=0;
					purchRetQty=0;
					saleQty=0;
					saleRetQty=0;
					
					 purchObj=getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)" +
						" from PurchaseModel a join a.inventory_details_list b " +
						"where a.office.id=:ofc and a.date between :start and :end and a.responsible_person=:salesman and b.item.id=:item and a.office.id=:ofc group by b.item.id")
						.setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
						.setParameter("salesman", salseManId).setParameter("item", itemMdl.getId()).uniqueResult();
					
					if(purchRetIdList!=null&&purchRetIdList.size()>0)
						purchRetObj1=getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)" +
							" from PurchaseReturnModel a join a.inventory_details_list b " +
							"where a.office.id=:ofc and a.date between :start and :end and b.order_id in (:orderList) and a.status=1 and b.item.id=:item and a.office.id=:ofc group by b.item.id")
							.setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
							.setParameter("item", itemMdl.getId()).setParameterList("orderList", purchRetIdList).uniqueResult();
							
					if(saleRetIdList!=null&&saleRetIdList.size()>0)
						purchRetObj2=(Double)getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)" +
								" from PurchaseReturnModel a join a.inventory_details_list b " +
								"where a.office.id=:ofc and a.date between :start and :end and  b.order_id in (:orderList) and a.status=2 and b.item.id=:item and a.office.id=:ofc group by b.item.id")
								.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
								.setParameter("item", itemMdl.getId()).setParameterList("orderList", saleRetIdList).uniqueResult();
					
					saleObj=getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)" +
						" from SalesModel a join a.inventory_details_list b " +
						"where a.office.id=:ofc and a.date between :start and :end and a.responsible_person=:salesman and b.item.id=:item and a.office.id=:ofc group by b.item.id")
						.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
						.setParameter("salesman", salseManId).setParameter("item", itemMdl.getId()).uniqueResult();
				
					saleRetObj=getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)" +
						" from SalesReturnModel a join a.inventory_details_list b " +
						"where a.office.id=:ofc and a.date between :start and :end and a.responsible_person=:salesman and b.item.id=:item and a.office.id=:ofc group by b.item.id")
						.setParameter("ofc", officeId).setParameter("start", fromDtae).setParameter("end", toDate).setParameter("ofc", officeId)
						.setParameter("salesman", salseManId).setParameter("item", itemMdl.getId()).uniqueResult();
					
					if(purchObj!=null)
						purchQty=(Double) purchObj;					
					if(purchRetObj1!=null)
						purchRetQty=(Double) purchRetObj1;					
					if(purchRetObj2!=null)
						purchRetQty+=(Double) purchRetObj2;					
					if(saleObj!=null)
						saleQty=(Double) saleObj;					
					if(saleRetObj!=null)
						saleRetQty=(Double) saleRetObj;	
					
					if(purchQty!=0||purchRetQty!=0||saleQty!=0||saleRetQty!=0)
						resultList.add(new SalesManWiseReportBean(itemMdl
								.getId(), 0, "", itemMdl.getName(), itemMdl
								.getUnit().getSymbol(), purchQty, purchRetQty,
								saleQty, saleRetQty, purchQty + saleRetQty
										- purchRetQty - saleQty));
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
		return resultList;
	}
	
}
