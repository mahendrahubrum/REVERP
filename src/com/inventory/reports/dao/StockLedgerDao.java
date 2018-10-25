package com.inventory.reports.dao;
/**
 *
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.StockLedgerBean;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class StockLedgerDao extends SHibernate implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<StockLedgerBean> getStockLedger(long officeId, long item_group_id,long item_subgroup_id,
			long item_id, java.sql.Date from_date, java.sql.Date to_date) throws Exception{
		ItemModel itemModel = null;		
		List<StockLedgerBean> stockLedgerList = new ArrayList<StockLedgerBean>();
		try {
			begin();
			Query query = getSession().createQuery(" FROM ItemModel" +
					"  WHERE office.id = :officeId" +
					((item_group_id != 0 ) ? " AND sub_group.group.id = "+item_group_id : "")+
					((item_subgroup_id != 0 ) ? " AND sub_group.id = "+item_subgroup_id : "")+
					((item_id != 0 ) ? " AND id = "+item_id : ""))				
					.setLong("officeId", officeId);
			List<ItemModel> list = query.list();
			Iterator<ItemModel> itemIterator = list.iterator();
			double openingQty = 0;
			while(itemIterator.hasNext()){
				itemModel = itemIterator.next();
				if(from_date.compareTo(itemModel.getOpening_stock_date())>0){
					openingQty += getOpeningQty(itemModel.getId(),from_date,itemModel.getOpening_balance());
				} else if(to_date.compareTo(itemModel.getOpening_stock_date())<0){
					openingQty += getOpeningQty(itemModel.getId(),from_date,0);
				} else {
					openingQty =+ getOpeningQty(itemModel.getId(),from_date,itemModel.getOpening_balance());
				}
			}
			StockLedgerBean bean = new StockLedgerBean();	
				bean.setItem("");
				bean.setLedger("");
				bean.setReceivedQty(0);
				bean.setIssuedQty(0);
				if(item_id!=0){
					bean.setComments("Opening Balance");
					bean.setBalanceQty(openingQty);
					bean.setDateString(getPreviousDate(from_date));
				}else{
					bean.setComments("");
					bean.setBalanceQty(0);
					bean.setDateString("");
				}
				stockLedgerList.add(bean);		
			
				stockLedgerList
					.addAll(getReceivedList(officeId, item_group_id, item_subgroup_id,item_id, from_date, to_date));
				stockLedgerList.addAll(getIssuedList(officeId, item_group_id, item_subgroup_id,item_id, from_date, to_date));
				
				Collections.sort(stockLedgerList, new Comparator<StockLedgerBean>() {
					@Override
				public int compare(final StockLedgerBean object1,
						final StockLedgerBean object2) {
					return object1.getDateString().compareTo(
							object2.getDateString());
				}	
				});
					
				Iterator<StockLedgerBean> itr = stockLedgerList.iterator();
				double balance = 0;
				boolean isFirst = true;
				String prevItem="";
				while(itr.hasNext()){
					bean = itr.next();
					if(isFirst){
						balance = bean.getBalanceQty();
						isFirst = false;
					} else {
						if(prevItem.equals(bean.getItem()))
							balance += bean.getReceivedQty() - bean.getIssuedQty();
						else{
							if(item_id==0){
								balance = bean.getReceivedQty() - bean.getIssuedQty();
								prevItem=bean.getItem();
							}else
								balance += bean.getReceivedQty() - bean.getIssuedQty();
						}
					}
					bean.setBalanceQty(balance);
				}
				
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally{
			flush();
			close();			
		}
		
		return stockLedgerList;
	}

	private String getPreviousDate(Date from_date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(from_date);
		cal.add(Calendar.DATE, -1);
		return CommonUtil.getSQLDateFromUtilDate(cal.getTime()).toString();
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
		
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM StockCreateModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();	
		
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(a.quantity),0) FROM ItemStockModel a " +
				" WHERE a.item.id = :item_id" +
				" AND a.status = :status" +
				" AND a.purchase_type = :purchaseType" +
				" AND a.date_time < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)			
				.setLong("status", SConstants.stock_statuses.TRANSFERRED_STOCK)
				.setInteger("purchaseType", SConstants.stockPurchaseType.STOCK_TRANSFER).uniqueResult();	
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
		
		opening_bal -= (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM StockTransferModel a JOIN a.inventory_details_list b " +
				" WHERE b.stock_id.item.id = :item_id" +
				" AND a.transfer_date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();	
		
		opening_bal -= (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM DisposeItemsModel a JOIN a.item_details_list b " +
				" WHERE b.item.id = :item_id" +
				" AND a.date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date).uniqueResult();
		
		
		return opening_bal;
	}

	@SuppressWarnings("unchecked")
	private List<StockLedgerBean> getIssuedList(long officeId, long item_group_id, long item_subgroup_id,
			long item_id,Date from_date,Date to_date) throws Exception{
		
		List<StockLedgerBean> stockLedgerList = (List<StockLedgerBean>) getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						"a.customer.name," +
						"CONCAT( 'Sales (' ,a.sales_number, ')' )," +
						"0.0," +
					//	"0.0)" +
						"COALESCE(SUM(b.quantity_in_basic_unit),0.0),a.date)" +
						
						" FROM SalesModel a JOIN a.inventory_details_list b " +
												" WHERE b.item.office.id = :officeId" +
												((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
					//	" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
												" AND b.delivery_id = 0" +
												" AND a.date BETWEEN :from_date AND :to_date" +
												" group by a.id,b.item.id")
												.setLong("officeId", officeId)
				//		.setLong("item_group_id", item_group_id)
												.setDate("from_date", from_date)
												.setDate("to_date", to_date).list();	
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						"a.customer.name," +
						"CONCAT( 'Delivery Note (' , a.deliveryNo, ')' )," +
						"0.0," +
					//	"0.0)" +
						"COALESCE(SUM(b.qty_in_basic_unit),0),a.date)" +
						
						" FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b " +
				" WHERE b.item.office.id = :officeId" +
				((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
			//			" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
				" AND a.date BETWEEN :from_date AND :to_date" +
						" group by a.id,b.item.id")
				.setLong("officeId", officeId)
			//			.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());	
		
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						"a.supplier.name," +
						" CONCAT('Purchase Return (', a.return_no, ')' )," +
						"0.0," +
						//"0.0)" +
						"COALESCE(SUM(b.qty_in_basic_unit),0),a.date)" +
						
						" FROM PurchaseReturnModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.office.id = :officeId" +
				((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
			//			" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
				" AND a.date BETWEEN :from_date AND :to_date" +
							" group by a.id,b.item.id")
				.setLong("officeId", officeId)
			//			.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());			
		
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.stock_id.item.name," +
						"cast(a.transfer_date as string)," +
						" ' '," +
						" CONCAT('Stock Transfer (', a.transfer_no, ')' )," +
						"0.0," +
						//"0.0)" +
						"COALESCE(SUM(b.quantity_in_basic_unit),0),a.transfer_date)" +
						
						" FROM StockTransferModel a JOIN a.inventory_details_list b " +
				" WHERE b.stock_id.item.office.id = :officeId" +
				((item_group_id != 0) ? " AND b.stock_id.item.sub_group.group.id = "+item_group_id : "")+
			//			" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.stock_id.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.stock_id.item.id = "+item_id : "")+
				" AND a.transfer_date BETWEEN :from_date AND :to_date" +
							" group by a.id,b.stock_id.item.id")
				.setLong("officeId", officeId)
			//			.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());	
		
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						" ' '," +
						" CONCAT('Stock Dispose' )," +
						"0.0," +
						//"0.0)" +
						"COALESCE(SUM(b.qty_in_basic_unit),0),a.date)" +
						
						" FROM DisposeItemsModel a JOIN a.item_details_list b " +
				" WHERE a.office.id = :officeId" +
				((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
			//			" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
				" AND a.date BETWEEN :from_date AND :to_date" +
							" group by a.id,b.item.id")
				.setLong("officeId", officeId)
			//			.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());	
		
		return stockLedgerList;
	}
	
	@SuppressWarnings("unchecked")
	private List<StockLedgerBean> getReceivedList(long officeId, long item_group_id, long item_subgroup_id, long item_id, Date from_date,Date to_date) throws Exception{
		
		List<StockLedgerBean> stockLedgerList = (List<StockLedgerBean>) getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						"a.supplier.name," +
						"CONCAT( 'Purchase (',  a.purchase_no , ')' )," +
						"COALESCE(SUM(b.qty_in_basic_unit),0)," +					
						"0.0,a.date)" +
						" FROM PurchaseModel a JOIN a.purchase_details_list b " +
						" WHERE b.item.office.id = :officeId" +
						((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+						
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
						" AND b.grn_id = 0" +
						" AND a.date BETWEEN :from_date AND :to_date" +
						" group by a.id,b.item.id")
						.setLong("officeId", officeId)
					//	.setLong("item_group_id", item_group_id)
						.setDate("from_date", from_date)
						.setDate("to_date", to_date).list();	
	
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						"a.supplier.name," +
						"CONCAT('Purchase GRN (' , a.grn_no , ')')," +
						"COALESCE(SUM(b.qty_in_basic_unit),0)," +						
						"0.0,a.date)" +
						 " FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
							" WHERE b.item.office.id = :officeId" +
							((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
			//			" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
							" AND a.date BETWEEN :from_date AND :to_date" +
							" group by a.id,b.item.id")
				.setLong("officeId", officeId)
		//		.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());		
		
		
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						"a.customer.name," +
						" CONCAT('Sales Return (', a.return_no, ')')," +
						"COALESCE(SUM(b.quantity_in_basic_unit),0)," +						
						"0.0,a.date)" +
						"  FROM SalesReturnModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.office.id = :officeId" +
				((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
					//	" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
				" AND a.date BETWEEN :from_date AND :to_date"+
				" group by a.id,b.item.id")
				.setLong("officeId", officeId)
		//		.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());			
		
		
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(b.item.name," +
						"cast(a.date as string)," +
						" ' ' ," +
						" CONCAT('Stock Created (', a.purchase_number, ')')," +
						"COALESCE(SUM(b.qty_in_basic_unit),0)," +						
						"0.0,a.date)" +
						"  FROM StockCreateModel a JOIN a.inventory_details_list b " +
				" WHERE b.item.office.id = :officeId" +
				((item_group_id != 0) ? " AND b.item.sub_group.group.id = "+item_group_id : "")+
					//	" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND b.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND b.item.id = "+item_id : "")+
				" AND a.date BETWEEN :from_date AND :to_date"+
				" group by a.id,b.item.id")
				.setLong("officeId", officeId)
		//		.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());		
		
		stockLedgerList.addAll(getSession()
				.createQuery("SELECT new com.inventory.reports.bean.StockLedgerBean(a.item.name," +
						"cast(a.date_time as string)," +
						" ' ' ," +
						" 'Stock Transfer(R)' ," +
						"COALESCE(SUM(a.quantity),0)," +						
						"0.0,date(a.date_time))" +
						"  FROM ItemStockModel a " +
				" WHERE a.item.office.id = :officeId" +
				" AND a.status = :status" +
				" AND a.purchase_type = :purchaseType" +
				((item_group_id != 0) ? " AND a.item.sub_group.group.id = "+item_group_id : "")+
					//	" AND b.item.sub_group.group.id = :item_group_id" +
						((item_subgroup_id != 0) ? " AND a.item.sub_group.id = "+item_subgroup_id : "")+
						((item_id != 0) ? " AND a.item.id = "+item_id : "")+
				" AND a.date_time BETWEEN :from_date AND :to_date"+
				" group by a.id,a.item.id")
				.setLong("officeId", officeId)				
				.setLong("status", SConstants.stock_statuses.TRANSFERRED_STOCK)
				.setInteger("purchaseType", SConstants.stockPurchaseType.STOCK_TRANSFER)
		//		.setLong("item_group_id", item_group_id)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).list());		
		
		return stockLedgerList;
	}
	


}
