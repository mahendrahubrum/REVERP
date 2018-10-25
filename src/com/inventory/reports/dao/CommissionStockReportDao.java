package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionPurchaseDetailsModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.CommissionStockModel;
import com.inventory.reports.bean.CommissionStockBean;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 12, 2013
 */
public class CommissionStockReportDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8373259263374673101L;

	public double getItemBalance(long id) throws Exception {
		double balance=0;

		try {
			begin();
			
			balance = (Double) getSession()
					.createQuery(
							" select sum(balance) from ItemStockModel where item.id=:id ")
					.setParameter("id", id).uniqueResult();
			commit();

		} catch (Exception e) {
			balance=0;
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return balance;
	}


	@SuppressWarnings("unchecked")
	public List getItemInventoryDetails(Long itemId,Date toDate) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			
			list .add(getSession()
					.createQuery("select new com.inventory.reports.bean.ItemReportBean(concat('Purchase','(',b.item.unit.symbol,')'),coalesce(sum(b.qty_in_basic_unit),0)) from PurchaseModel a join a.inventory_details_list b  where b.item.id=:item and a.date<=:dt")
					.setParameter("item",itemId).setParameter("dt",toDate).uniqueResult());
			list .add(getSession()
					.createQuery("select new com.inventory.reports.bean.ItemReportBean(concat('Sales','(',b.item.unit.symbol,')'),coalesce(sum(b.quantity_in_basic_unit),0)) from SalesModel a join a.inventory_details_list b  where b.item.id=:item and a.date<=:dt")
					.setParameter("item",itemId).setParameter("dt",toDate).uniqueResult());
			list .add(getSession()
					.createQuery("select new com.inventory.reports.bean.ItemReportBean(concat('Purchase Return','(',b.item.unit.symbol,')'),coalesce(sum(b.qty_in_basic_unit),0)) from PurchaseReturnModel a join a.inventory_details_list b  where b.item.id=:item and a.date<=:dt")
					.setParameter("item",itemId).setParameter("dt",toDate).uniqueResult());
			list .add(getSession()
					.createQuery("select new com.inventory.reports.bean.ItemReportBean(concat('Sales Return','(',b.item.unit.symbol,')'),coalesce(sum(b.quantity_in_basic_unit),0)) from SalesReturnModel a join a.inventory_details_list b  where b.item.id=:item and a.date<=:dt")
					.setParameter("item",itemId).setParameter("dt",toDate).uniqueResult());
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
		return list;
	}

	
	public List getTopSaledItems(java.sql.Date fromDate,
			java.sql.Date toDate, long officeID) throws Exception {
		
		List resultList=null;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.bean.SalesChartBean(coalesce(sum(b.quantity_in_basic_unit),0) ,concat(b.item.name,'(',b.item.unit.symbol,')'))" +
							" from SalesModel a join a.inventory_details_list b  where a.office.id=:office and a.date between :fromDate and :toDate" +
									" and a.active=true group by b.item.id order by sum(b.quantity_in_basic_unit) desc").setMaxResults(5)
					.setParameter("office", officeID)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	
	@SuppressWarnings("unchecked")
	public List getCommissionStockReport(long item,Date from,Date to,long office) throws Exception {
		List resultList=new ArrayList();
		try{
			CommissionStockBean bean;
			List purchaseList=new ArrayList();
			List childList=new ArrayList();
			List itemList=new ArrayList();
			ItemModel mdl=null;
			CommissionPurchaseModel purmdl=null;
			CommissionPurchaseDetailsModel detmdl=null;
			CommissionStockModel stk=null;
			double balance=0;
			begin();
			String condition="";
			if(item!=0){
				condition+=" and b.item.id="+item;
				itemList=getSession().createQuery("from ItemModel where office.id="+office+"and id="+item+" order by id").list();
			}
			else{
				itemList=getSession().createQuery("from ItemModel where office.id="+office+" order by id").list();
			}
			if(itemList.size()>0){
				for(int i=0;i<itemList.size();i++){
					mdl=(ItemModel)itemList.get(i);
					System.out.println("Item name "+mdl.getName());
					if(item!=0){
						purchaseList=getSession().createQuery("select a from CommissionPurchaseModel a join a.commission_purchase_list b where a.received_date between" +
								" :start and :end and a.office.id=:ofc "+condition+"order by b.item.id")
								.setParameter("ofc", office)
								.setParameter("start", from)
								.setParameter("end", to).list();
					}
					else{
						purchaseList=getSession().createQuery("select a from CommissionPurchaseModel a join a.commission_purchase_list b where a.received_date between" +
								" :start and :end and a.office.id=:ofc and b.item.id=:item order by b.item.id")
								.setParameter("item", mdl.getId())
								.setParameter("ofc", office)
								.setParameter("start", from)
								.setParameter("end", to).list();
					}
					if(purchaseList.size()>0){
						balance=0;
						Iterator pitr=purchaseList.iterator();
						while(pitr.hasNext()){
							purmdl=(CommissionPurchaseModel)pitr.next();
							childList=getSession().createQuery("select b from CommissionPurchaseModel a join a.commission_purchase_list b where" +
									" a.id="+purmdl.getId()).list();
							Iterator citr=childList.iterator();
							while(citr.hasNext()){
								detmdl=(CommissionPurchaseDetailsModel)citr.next();
								stk=(CommissionStockModel)getSession().createQuery("from CommissionStockModel where purchase_id=:pid and inv_det_id=:did and item.id=:item").setParameter("item", mdl.getId()).setParameter("pid", purmdl.getId()).setParameter("did", detmdl.getId()).uniqueResult();
								if(stk!=null)
									balance+=stk.getBalance();
							}
						}
						bean=new CommissionStockBean(mdl.getName(), mdl.getItem_code(), mdl.getUnit().getSymbol(), balance);
						resultList.add(bean);
					}
				}
			}
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return resultList;
	}
	
}
