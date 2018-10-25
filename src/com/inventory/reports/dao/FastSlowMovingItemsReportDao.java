package com.inventory.reports.dao;
/**
 *
 */
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.FastSlowMovingItemsReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class FastSlowMovingItemsReportDao extends SHibernate implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<FastSlowMovingItemsReportBean> getItemListWithPurchaseAndSale(long organization_id, long office_id, long item_group_id, long item_sub_group_id, Date from_date,Date to_date) throws Exception{
		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer.append(" FROM ItemModel  WHERE office.organization.id = :organization_id")
				.append((office_id != 0)?" AND office.id = :office_id" : "")
				.append((item_group_id != 0)?" AND sub_group.group.id = :item_group_id" : "")
				.append((item_sub_group_id != 0)?" AND sub_group.id = :item_sub_group_id" : "");
				
		
		List<ItemModel> itemModelArrayList = null;		
		List<FastSlowMovingItemsReportBean> movingItemsList = null;
		try {
			begin();
			Query query = getSession().createQuery(queryStringBuffer.toString())
					.setLong("organization_id", organization_id);
			if(office_id != 0){
				query.setLong("office_id", office_id);
			}
			if(item_group_id != 0){
				query.setLong("item_group_id", item_group_id);
			}
			if(item_sub_group_id != 0){
				query.setLong("item_sub_group_id", item_sub_group_id);
			}
			itemModelArrayList = query.list();
		//	flush();
			movingItemsList = new ArrayList<FastSlowMovingItemsReportBean>();
		/*	if(isFirst){
				isFirst = false;
				purchaseGRNDao = new PurchaseGRNDao(); 
				purchaseDao = new PurchaseDao();
			}	*/		
					
			
			double purchasedQty = 0;
			double saledQty = 0;
			for(ItemModel model : itemModelArrayList){				
				purchasedQty = 0;
				saledQty = getSaledQty(model.getId(),from_date,to_date);				
				if(saledQty != 0){
					purchasedQty = getPurchasedQty(model.getId(),from_date,to_date);
				} else {
					continue; 
				}
				
				if(purchasedQty == 0 && saledQty == 0){
					continue;
				}
				FastSlowMovingItemsReportBean bean = new FastSlowMovingItemsReportBean();
				bean.setItemId(model.getId());
				bean.setItemName(model.getName());
				bean.setPurchaseQty(purchasedQty);
				bean.setSaleQty(saledQty);
				bean.setCurrentStock(purchasedQty - saledQty);
				bean.setParentId(model.getParentId());
				bean.setSubGroupId(model.getSub_group().getId());
				if(movingItemsList.contains(bean)){
					for(FastSlowMovingItemsReportBean itr : movingItemsList){
						if(itr.equals(bean)){
							itr.setPurchaseQty(itr.getPurchaseQty()+bean.getPurchaseQty());
							itr.setSaleQty(itr.getSaleQty()+bean.getSaleQty());
							itr.setCurrentStock(itr.getPurchaseQty() - itr.getSaleQty());
							break;
						}
					}
				} else{
					movingItemsList.add(bean);
				}
				
				//no_of_row--;
			}
		//	Collections.sort(movingItemsList);
			commit();
			
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
		
		return movingItemsList;
	}

	private double getSaledQty(long itemId,Date from_date,Date to_date) throws Exception{
		double saledQty = 0;
		saledQty = (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesModel a JOIN a.inventory_details_list b " +
												" WHERE b.item.id = :item_id" +
												" AND b.delivery_id = 0" +
												" AND a.date BETWEEN :from_date AND :to_date")
												.setLong("item_id", itemId)
												.setDate("from_date", from_date)
												.setDate("to_date", to_date).uniqueResult();	
		saledQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		return saledQty;
	}
	@SuppressWarnings("unchecked")
	private double getPurchasedQty(long itemId,Date from_date,Date to_date) throws Exception {
		double purchasedQty = 0;
		/*purchasedQty = (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseModel a JOIN a.purchase_details_list b " +
												" WHERE b.item.id = :item_id" +
												" AND b.grn_id = 0")
												.setLong("item_id", itemId).uniqueResult();	
		purchasedQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
				" WHERE b.item.id = :item_id")
				.setLong("item_id", itemId).uniqueResult();	*/
		
		purchasedQty = (Double)getSession()
				.createQuery("SELECT COALESCE(SUM(quantity),0) FROM ItemStockModel  " +
												" WHERE id IN(SELECT c.stockId FROM SalesModel  a JOIN a.inventory_details_list b,SalesStockMapModel c " +
																			" WHERE b.item.id = :item_id" +
																			" AND b.delivery_id = 0" +
																			" AND a.date BETWEEN :from_date AND :to_date" +
																			" AND c.salesId = a.id" +
																			" AND c.type = :type)")
												.setLong("item_id", itemId)
												.setDate("from_date", from_date)
												.setDate("to_date", to_date)
												.setInteger("type",SConstants.SalesDeliveryType.SALE_TYPE).uniqueResult();
		
		purchasedQty += (Double)getSession()
				.createQuery("SELECT COALESCE(SUM(quantity),0) FROM ItemStockModel  " +
												" WHERE id IN(SELECT c.stockId FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b,SalesStockMapModel c " +
																			" WHERE b.item.id = :item_id" +																		
																			" AND a.date BETWEEN :from_date AND :to_date" +
																			" AND c.salesId = a.id" +
																			" AND c.type = :type)")
												.setLong("item_id", itemId)
												.setDate("from_date", from_date)
												.setDate("to_date", to_date)
												.setInteger("type",SConstants.SalesDeliveryType.DELIVERY_TYPE).uniqueResult();
		
		
		return purchasedQty;
	}


}
