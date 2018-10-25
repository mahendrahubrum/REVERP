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
import com.inventory.reports.bean.StockTransactionAnalysisReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class StockTransactionAnalysisReportDao extends SHibernate implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<StockTransactionAnalysisReportBean> getItemListWithPurchaseAndSale(long organization_id, long office_id, long item_group_id, long item_sub_group_id, Date from_date,Date to_date) throws Exception{
		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer.append(" FROM ItemModel  WHERE office.organization.id = :organization_id")
				.append((office_id != 0)?" AND office.id = :office_id" : "")
				.append((item_group_id != 0)?" AND sub_group.group.id = :item_group_id" : "")
				.append((item_sub_group_id != 0)?" AND sub_group.id = :item_sub_group_id" : "")
				.append(" ORDER BY name");
				
		
		List<ItemModel> itemModelArrayList = null;		
		List<StockTransactionAnalysisReportBean> stockTransactionAnalysisReportList = null;
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
			stockTransactionAnalysisReportList = new ArrayList<StockTransactionAnalysisReportBean>();		
			
			double receivedQty = 0;
			double issuedQty = 0;
			double openingQty = 0;
			for(ItemModel model : itemModelArrayList){		
				issuedQty = getIssuedQty(model.getId(),from_date,to_date);				
				receivedQty = getReceivedQty(model.getId(),from_date,to_date);
				if(from_date.compareTo(model.getOpening_stock_date())>0){
					openingQty = getOpeningQty(model.getId(),from_date,model.getOpening_balance());
				} else if(to_date.compareTo(model.getOpening_stock_date())<0){
					openingQty = getOpeningQty(model.getId(),from_date,0);
				} else {
					openingQty = getOpeningQty(model.getId(),from_date,0);
					receivedQty+=model.getOpening_balance();
				}
				
				
				
				
				if(openingQty == 0 && receivedQty == 0 && issuedQty == 0){
					continue;
				}
				StockTransactionAnalysisReportBean bean = new StockTransactionAnalysisReportBean();		
				bean.setItemName(model.getName());
				bean.setOpeningQty(openingQty);
				bean.setReceivedQty(receivedQty);
				bean.setIssuedQty(issuedQty);
				bean.setBalanceQty(openingQty+receivedQty - issuedQty);
				bean.setParentId(model.getParentId());
				bean.setSubGroupId(model.getSub_group().getId());
				if(stockTransactionAnalysisReportList.contains(bean)){
					for(StockTransactionAnalysisReportBean itr : stockTransactionAnalysisReportList){
						if(itr.equals(bean)){
							itr.setOpeningQty(itr.getOpeningQty()+bean.getOpeningQty());
							itr.setReceivedQty(itr.getReceivedQty()+bean.getReceivedQty());
							itr.setIssuedQty(itr.getIssuedQty()+bean.getIssuedQty());
							itr.setBalanceQty(itr.getOpeningQty()+itr.getReceivedQty() - itr.getIssuedQty());
							break;
						}
					}
				} else{
					stockTransactionAnalysisReportList.add(bean);
				}
				
				//no_of_row--;
			}
			System.out.println("===== COUNT ============= "+stockTransactionAnalysisReportList.size());
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
		
		return stockTransactionAnalysisReportList;
	}

	private double getOpeningQty(long itemId, Date from_date,double opening_bal) throws Exception{
	//	double receivedQty = 0;	
		
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseModel a JOIN a.purchase_details_list b " +
												" WHERE b.item.id = :item_id" +
												" AND b.grn_id = 0" +
												" AND a.date < :from_date")
												.setLong("item_id", itemId)
												.setDate("from_date", from_date).uniqueResult();	
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();	
		
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesReturnModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();	
		//==================================================================================================
//		double saledQty = 0;
		opening_bal -= (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesModel a JOIN a.inventory_details_list b " +
												" WHERE b.item.id = :item_id" +
												" AND b.delivery_id = 0" +
												" AND a.date < :from_date")
												.setLong("item_id", itemId)
												.setDate("from_date", from_date).uniqueResult();	
		opening_bal -= (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();	
		
		opening_bal -= (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseReturnModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();	
		return opening_bal;
	}

	private double getIssuedQty(long itemId,Date from_date,Date to_date) throws Exception{
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
		
		saledQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseReturnModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		
		saledQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM StockTransferModel a JOIN a.inventory_details_list b " +
				" WHERE b.stock_id.item.id = :item_id" +
				" AND a.transfer_date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		return saledQty;
	}
	@SuppressWarnings("unchecked")
	private double getReceivedQty(long itemId,Date from_date,Date to_date) throws Exception {
		double receivedQty = 0;	
		
		receivedQty = (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseModel a JOIN a.purchase_details_list b " +
												" WHERE b.item.id = :item_id" +
												" AND b.grn_id = 0" +
												" AND a.date BETWEEN :from_date AND :to_date")
												.setLong("item_id", itemId)
												.setDate("from_date", from_date)
												.setDate("to_date", to_date).uniqueResult();	
		receivedQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		
		receivedQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesReturnModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		
		receivedQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(a.quantity),0) FROM ItemStockModel a " +
				" WHERE a.item.id = :item_id" +
				" AND DATE(a.date_time) BETWEEN :from_date AND :to_date" +
				" AND a.purchase_type = :purchase_type")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date)
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER).uniqueResult();	
		
		
		return receivedQty;
	}


}
