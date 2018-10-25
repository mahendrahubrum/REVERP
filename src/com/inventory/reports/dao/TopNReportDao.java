package com.inventory.reports.dao;
/**
 *
 */
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.FastSlowMovingItemsReportBean;
import com.inventory.reports.bean.TopNReportBean;
import com.inventory.reports.ui.PurchaseTopNReportUI;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class TopNReportDao extends SHibernate implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	public List<TopNReportBean> getSalesTopNList(long organization_id, long office_id, Date from_date,Date to_date, int report_type) throws Exception{
		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer.append("SELECT id FROM ItemModel  WHERE office.organization.id = :organization_id")
				.append((office_id != 0)?" AND office.id = :office_id" : "");
		List<ItemModel> itemModelArrayList = null;		
		List<TopNReportBean> topNReportList = new ArrayList<TopNReportBean>();
		try{
			begin();
			Query query = getSession().createQuery(queryStringBuffer.toString())
					.setLong("organization_id", organization_id);
			if(office_id != 0){
				query.setLong("office_id", office_id);
			}			
			itemModelArrayList = query.list();
			
			List<TopNReportBean> topNReportTempList = getSalesTopNList(itemModelArrayList, from_date, to_date, report_type);
		//	Iterator<TopNReportBean> topNReportBeanIterator = topNReportTempList.iterator();
			for(TopNReportBean bean : topNReportTempList){				
				if(!topNReportList.contains(bean)){
					topNReportList.add(bean);
				} 
			}	
			
			Collections.sort(topNReportList);
			
			
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally{
			flush();
			close();			
		}
		return topNReportList;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<TopNReportBean> getPurchaseTopNList(long organization_id, long office_id, Date from_date,Date to_date, int report_type) throws Exception{
		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer.append("SELECT id FROM ItemModel  WHERE office.organization.id = :organization_id")
				.append((office_id != 0)?" AND office.id = :office_id" : "");
		List<ItemModel> itemModelArrayList = null;		
		List<TopNReportBean> topNReportList = new ArrayList<TopNReportBean>();
		try{
			begin();
			Query query = getSession().createQuery(queryStringBuffer.toString())
					.setLong("organization_id", organization_id);
			if(office_id != 0){
				query.setLong("office_id", office_id);
			}			
			itemModelArrayList = query.list();
			
			List<TopNReportBean> topNReportTempList = getPurchaseTopNList(itemModelArrayList, from_date, to_date, report_type);
		//	Iterator<TopNReportBean> topNReportBeanIterator = topNReportTempList.iterator();
			for(TopNReportBean bean : topNReportTempList){
				System.out.println("====== "+bean.getItem() +" ======== "+bean.getAmountOrQuantity());
				if(!topNReportList.contains(bean)){
					topNReportList.add(bean);
				} 
			}	
			
			Collections.sort(topNReportList);
			
			
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally{
			flush();
			close();			
		}
		return topNReportList;
	}
	
	@SuppressWarnings("unchecked")
	private List<TopNReportBean> getPurchaseTopNList(List<ItemModel> itemIdList,Date from_date,Date to_date,int report_type) throws Exception {
		List<TopNReportBean> topNReportList = new ArrayList<TopNReportBean>();
		if(report_type == PurchaseTopNReportUI.QUANTITY_WISE){
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qty_in_basic_unit),0)," +
										"0," +
										"b.item.unit.symbol)" +
										
										" FROM PurchaseModel a JOIN a.purchase_details_list b " +
													" WHERE b.item.id IN ( :item_id )" +
													" AND b.grn_id = 0" +
													" AND a.date BETWEEN :from_date AND :to_date" +
													" GROUP BY b.item.id")
													.setParameterList("item_id", itemIdList)
													.setDate("from_date", from_date)
													.setDate("to_date", to_date).list());	
			
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qty_in_basic_unit),0)," +
										"0," +
										"b.item.unit.symbol)" +
										
										" FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
					" WHERE b.item.id IN( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
			topNReportList.addAll( getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qty_in_basic_unit),0)," +
										"1," +
										"b.item.unit.symbol)" +
										
										" FROM PurchaseReturnModel a JOIN a.inventory_details_list b " +
					" WHERE b.item.id IN ( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
		/*	topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.quantity_in_basic_unit),0))" +
										
							" FROM SalesReturnModel a JOIN a.inventory_details_list b " +
					" WHERE b.item.id IN( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	*/
		} else {
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qunatity * b.unit_price),0)," +
										"0," +
										" '' )" +
										
										" FROM PurchaseModel a JOIN a.purchase_details_list b " +
													" WHERE b.item.id IN ( :item_id )" +
													" AND b.grn_id = 0" +
													" AND a.date BETWEEN :from_date AND :to_date" +
													" GROUP BY b.item.id")
													.setParameterList("item_id", itemIdList)
													.setDate("from_date", from_date)
													.setDate("to_date", to_date).list());	
			
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qunatity * b.unit_price),0)," +
										"0," +
										" '' )" +
										
										" FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
					" WHERE b.item.id IN( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
			topNReportList.addAll( getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qunatity * unit_price),0)," +
										"1," +
										" '' )" +
										
										" FROM PurchaseReturnModel a JOIN a.inventory_details_list b " +
					" WHERE b.item.id IN ( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
		/*	topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.unit_price),0))" +
										
							" FROM SalesReturnModel a JOIN a.inventory_details_list b " +
					" WHERE b.item.id IN( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	*/
		}	
		
		return topNReportList;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private List<TopNReportBean> getSalesTopNList(List<ItemModel> itemIdList,Date from_date,Date to_date,int report_type) throws Exception {
		List<TopNReportBean> topNReportList = new ArrayList<TopNReportBean>();
		if(report_type == PurchaseTopNReportUI.QUANTITY_WISE){
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.quantity_in_basic_unit),0)," +
										"0," +
										"b.item.unit.symbol)" +
										
										" FROM SalesModel a JOIN a.inventory_details_list b " +
													" WHERE b.item.id IN ( :item_id )" +
													" AND b.delivery_id = 0" +
													" AND a.date BETWEEN :from_date AND :to_date" +
													" GROUP BY b.item.id")
													.setParameterList("item_id", itemIdList)
													.setDate("from_date", from_date)
													.setDate("to_date", to_date).list());	
			
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qty_in_basic_unit),0)," +
										"0," +
										"b.item.unit.symbol)" +
										
										" FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b " +
					" WHERE b.item.id IN( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
			topNReportList.addAll( getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.quantity_in_basic_unit),0)," +
										"1," +
										"b.item.unit.symbol)" +
										
										" FROM SalesReturnModel a JOIN a.inventory_details_list b " +
					" WHERE b.item.id IN ( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
		
		} else {
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qunatity * unit_price),0)," +
										"0," +
										" '' )" +
										
										" FROM SalesModel a JOIN a.inventory_details_list b " +
													" WHERE b.item.id IN ( :item_id )" +
													" AND b.delivery_id = 0" +
													" AND a.date BETWEEN :from_date AND :to_date" +
													" GROUP BY b.item.id")
													.setParameterList("item_id", itemIdList)
													.setDate("from_date", from_date)
													.setDate("to_date", to_date).list());	
			
			topNReportList.addAll(getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qunatity * unit_price),0)," +
										"0," +
										" '' )" +
										
										" FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b " +
					" WHERE b.item.id IN( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());	
			
			topNReportList.addAll( getSession()
					.createQuery("SELECT new com.inventory.reports.bean.TopNReportBean(b.item.parentId," +
										"b.item.sub_group.id," +
										"b.item.name," +
										"COALESCE(SUM(b.qunatity * unit_price),0)," +
										"1," +
										" '')" +
										
										" FROM SalesReturnModel a JOIN a.inventory_details_list b " +
					" WHERE b.item.id IN ( :item_id)" +
					" AND a.date BETWEEN :from_date AND :to_date"+
					" GROUP BY b.item.id")
					.setParameterList("item_id", itemIdList)
					.setDate("from_date", from_date)
					.setDate("to_date", to_date).list());			
		}	
		
		return topNReportList;
	}

}
