package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 12, 2013
 */
public class ItemReportDao extends SHibernate implements Serializable {

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


	public double getGRVStockBalance(long id,Date date) throws Exception {
		double balance=0;
		try {
			begin();
			
			balance = (Double) getSession()
					.createQuery("select sum(balance) from ItemStockModel where item.id=:id and status=3 and date(date_time)<=:dt").setParameter("dt", date)
					.setParameter("id", id).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return balance;
	}


	public List getStockMappingOfItems(long id,long rackId) throws Exception {
		List list=null;
		try {
			begin();
			
			String condition="";
			if(rackId!=0)
				condition+=" and rack.id="+rackId;
			
			list =  getSession()
					.createQuery(" from StockRackMappingModel where stock.item.id=:id "+condition)
					.setParameter("id", id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}


	public List getAllRacksUnderOffice(long officeID) throws Exception {
		List list=null;
		try {
			begin();
			
			list =  getSession()
					.createQuery(" from RackModel where room.building.office.id=:id")
					.setParameter("id", officeID).list();
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


	public List getItemInventoryDetails(Long itemId,Date toDate) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			
			list .add(getSession()
					.createQuery("select new com.inventory.reports.bean.ItemReportBean(concat('Purchase','(',b.item.unit.symbol,')'),coalesce(sum(b.qty_in_basic_unit),0)) from PurchaseModel a join a.purchase_details_list b  where b.item.id=:item and a.date<=:dt")
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
	
}
