package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 * WebSpark.
 *
 * Jan 23 2014
 */
public class ItemWiseStockAuditingReportDao extends SHibernate implements Serializable {
	
	private static final long serialVersionUID = -1071364669984964064L;
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	
	public List<Object> getItemWiseStockAuditingReport(long itmId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		
		
		try {
			
			Date bfrDate=new Date(fromDate.getTime()-86400000);
			
			
			begin();
			
			double open_stk=0, purchaseQty=0, saleQty=0, saleRtnQty, purchaseRtnQty=0, closing_stk=0,stock_value=0;
			
			if (itmId != 0) {
				
				ItemModel itmObj = (ItemModel) getSession().get(ItemModel.class, itmId);
				
				open_stk=getItemBalanceAtDate(itmObj.getId(), bfrDate);
				
				saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from SalesModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
		
				purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
				
				saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.returned_quantity+b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
		
				purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
				
				closing_stk=getItemBalanceAtDate(itmObj.getId(), toDate);
//				stock_value=getItemStockValue(itmObj.getId(), toDate);
				
				
				if(purchaseQty==0 && purchaseRtnQty==0 && saleQty==0 && saleRtnQty==0 ) {
					
				}
				else
					resultList.add(new ReportBean(	itmObj.getName(),
													CommonUtil.roundNumber(open_stk),
													CommonUtil.roundNumber(purchaseQty),
													CommonUtil.roundNumber(purchaseRtnQty),
													CommonUtil.roundNumber(saleQty),
													CommonUtil.roundNumber(saleRtnQty),
													CommonUtil.roundNumber(closing_stk),
													itmObj.getUnit().getSymbol()));
				
			}
			else {
				
				List itmList = getSession()
						.createQuery("select new com.inventory.config.stock.model.ItemModel(id,unit.symbol,name)"
									+ " from ItemModel where office.id=:ofc order by name")
										.setParameter("ofc", officeId).list();
				
				ReportBean rptModel;
				Iterator itr1=itmList.iterator();
				while (itr1.hasNext()) {
					ItemModel itmObj = (ItemModel) itr1.next();
					rptModel=new ReportBean();
					rptModel.setClient_name(itmObj.getName());
					
					open_stk=getItemBalanceAtDate(itmObj.getId(), bfrDate);
					
					saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
							+ " from SalesModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
			
					purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
							+ " from PurchaseModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
					
					saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.returned_quantity+b.stock_quantity),0)"
							+ " from SalesReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
			
					purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
							+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
					
					closing_stk=getItemBalanceAtDate(itmObj.getId(), toDate);
//					stock_value=getItemStockValue(itmObj.getId(), toDate);
					if(purchaseQty==0 && purchaseRtnQty==0 && saleQty==0 && saleRtnQty==0 ) {
						
					}
					else
						resultList.add(new ReportBean(	itmObj.getName(),
														CommonUtil.roundNumber(open_stk),
														CommonUtil.roundNumber(purchaseQty),
														CommonUtil.roundNumber(purchaseRtnQty),
														CommonUtil.roundNumber(saleQty),
														CommonUtil.roundNumber(saleRtnQty),
														CommonUtil.roundNumber(closing_stk),
														itmObj.getItem_code()));
				}
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	public List<Object> getItemWiseStockReport(long itmId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		
		
		try {
			
			Date bfrDate=new Date(fromDate.getTime()-86400000);
			
			
			begin();
			
			double open_stk=0, purchaseQty=0, saleQty=0, saleRtnQty, purchaseRtnQty=0, closing_stk=0,stock_value=0,sale_rate=0,purchase_rate=0;
			
			if (itmId != 0) {
				
				ItemModel itmObj = (ItemModel) getSession().get(ItemModel.class, itmId);
				
				open_stk=getItemBalanceAtDate(itmObj.getId(), bfrDate);
				
				saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from SalesModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
				
				sale_rate=(Double) getSession().createQuery("select coalesce(avg(b.unit_price),0)"
						+ " from SalesModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
		
				purchase_rate=(Double) getSession().createQuery("select coalesce(avg(b.unit_price),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
				
				
				purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
				
				
				saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.returned_quantity+b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
		
				purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
						.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
				
				closing_stk=getItemBalanceAtDate(itmObj.getId(), toDate);
				stock_value=getItemStockValue(itmObj.getId(), toDate);
				
				
				if(purchaseQty==0 && purchaseRtnQty==0 && saleQty==0 && saleRtnQty==0 ) {
					
				}
				else
					resultList.add(new ReportBean(	itmObj.getId(),
													itmObj.getName(),
													CommonUtil.roundNumber(open_stk),
													CommonUtil.roundNumber(purchaseQty),
													CommonUtil.roundNumber(purchaseRtnQty),
													CommonUtil.roundNumber(saleQty),
													CommonUtil.roundNumber(saleRtnQty),
													CommonUtil.roundNumber(closing_stk),
													itmObj.getUnit().getSymbol(),
													Math.abs(stock_value),sale_rate,purchase_rate));
				
			}
			else {
				
				List itmList = getSession()
						.createQuery("select new com.inventory.config.stock.model.ItemModel(id,unit.symbol,name)"
									+ " from ItemModel where office.id=:ofc order by name")
										.setParameter("ofc", officeId).list();
				
				ReportBean rptModel;
				Iterator itr1=itmList.iterator();
				while (itr1.hasNext()) {
					ItemModel itmObj = (ItemModel) itr1.next();
					rptModel=new ReportBean();
					rptModel.setClient_name(itmObj.getName());
					
					open_stk=getItemBalanceAtDate(itmObj.getId(), bfrDate);
					
					saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
							+ " from SalesModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
			
					sale_rate=(Double) getSession().createQuery("select coalesce(avg(b.unit_price),0)"
							+ " from SalesModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
			
					purchase_rate=(Double) getSession().createQuery("select coalesce(avg(b.unit_price),0)"
							+ " from PurchaseModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
					
					purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
							+ " from PurchaseModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
					
					saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.returned_quantity+b.stock_quantity),0)"
							+ " from SalesReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
			
					purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
							+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date between :fdt and :tdt and b.item.id=:itm and a.active=true")
							.setParameter("fdt", fromDate).setParameter("tdt", toDate).setLong("itm", itmObj.getId()).uniqueResult();
					
					closing_stk=getItemBalanceAtDate(itmObj.getId(), toDate);
					stock_value=getItemStockValue(itmObj.getId(), toDate);
					if(purchaseQty==0 && purchaseRtnQty==0 && saleQty==0 && saleRtnQty==0 ) {
						
					}
					else
						resultList.add(new ReportBean(	itmObj.getId(),
														itmObj.getName(),
														CommonUtil.roundNumber(open_stk),
														CommonUtil.roundNumber(purchaseQty),
														CommonUtil.roundNumber(purchaseRtnQty),
														CommonUtil.roundNumber(saleQty),
														CommonUtil.roundNumber(saleRtnQty),
														CommonUtil.roundNumber(closing_stk),
														itmObj.getItem_code(),
														Math.abs(stock_value),sale_rate,purchase_rate));
				}
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	public double getItemBalanceAtDate(long item_id, Date date) throws Exception {
		double balance=0;
		try {
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			
			if(obj!=null) {
				
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
								+ " from SalesModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.returned_quantity+b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from DeliveryNoteModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double itmTransfredQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from ItemTransferModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double itmReceivedQty=(Double) getSession().createQuery("select coalesce(sum(stock.quantity),0)"
						+ " from TransferStockMap where stock.manufacturing_date>:stdt and stock.manufacturing_date<=:dt and stock.item.id=:itm")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double resetedQty=(Double) getSession().createQuery("select reseted_quantity from StockResetDetailsModel where id=(select max(id) from StockResetDetailsModel where date=:dt and item.id=:itm)")
						.setParameter("dt", obj).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(resetedQty)+CommonUtil.roundNumber(purchaseQty)-
						CommonUtil.roundNumber(saleQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(deliveryNoteQty)
						-CommonUtil.roundNumber(itmTransfredQty)+CommonUtil.roundNumber(itmReceivedQty);
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id")
						.setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
								+ " from SalesModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
								.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.returned_quantity+b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from DeliveryNoteModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double itmTransfredQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from ItemTransferModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double itmReceivedQty=(Double) getSession().createQuery("select coalesce(sum(stock.quantity),0)"
						+ " from TransferStockMap where stock.manufacturing_date<=:dt and stock.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double resetedQty=(Double) getSession().createQuery("select coalesce(sum(-(balance_before_reset-reseted_quantity)),0)"
						+ " from StockResetDetailsModel where date<=:dt and item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)-
						CommonUtil.roundNumber(saleQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(deliveryNoteQty)
						-CommonUtil.roundNumber(itmTransfredQty)+CommonUtil.roundNumber(itmReceivedQty)
						+CommonUtil.roundNumber(resetedQty);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		return CommonUtil.roundNumber(balance);
		
	}
	
	
	
	public double getItemStockValue(long item_id, Date date) throws Exception {
		double balance=0;
		try {
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			
			if(obj!=null) {
				
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
								+ " from SalesModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from DeliveryNoteModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				List list=getSession().createQuery("select distinct(b.stock_id)"
						+ " from ItemTransferModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).list();
				
				
				
				double itmTransfredQty=0;
				
				if(list.size()>0){
					Iterator itr=list.iterator();
					while (itr.hasNext()) {
						String stck=(String)itr.next();
						ItemStockModel stock=(ItemStockModel)getSession().get(ItemStockModel.class,Long.parseLong(stck));
						itmTransfredQty+=stock.getQuantity()*stock.getRate();
					}
				}
				
				double itmReceivedQty=(Double) getSession().createQuery("select coalesce(sum(stock.quantity*stock.rate),0)"
						+ " from TransferStockMap where stock.manufacturing_date>:stdt and stock.manufacturing_date<=:dt and stock.item.id=:itm")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double resetedQty=0;
				
				Object objID=getSession().createQuery("select max(id) from ItemStockModel where item.id=:itm  and status!=3")
						.setLong("itm", item_id).uniqueResult();
				if(objID!=null){
					ItemStockModel stock=(ItemStockModel)getSession().get(ItemStockModel.class, (Long)objID);
					resetedQty=stock.getQuantity()*stock.getRate();
				}
				
				balance=CommonUtil.roundNumber(resetedQty)+CommonUtil.roundNumber(purchaseQty)-
						CommonUtil.roundNumber(saleQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(deliveryNoteQty)
						-CommonUtil.roundNumber(itmTransfredQty)+CommonUtil.roundNumber(itmReceivedQty)+CommonUtil.roundNumber(stockCreate);
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id")
						.setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
								+ " from SalesModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
								.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from PurchaseModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0)"
						+ " from DeliveryNoteModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity*b.unit_price/b.conversionRate),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				List list=getSession().createQuery("select distinct(b.stock_id)"
						+ " from ItemTransferModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).list();
				
				double itmTransfredQty=0;
				
				if(list.size()>0){
					Iterator itr=list.iterator();
					while (itr.hasNext()) {
						String stck=(String)itr.next();
						ItemStockModel stock=(ItemStockModel)getSession().get(ItemStockModel.class,Long.parseLong(stck));
						itmTransfredQty+=stock.getQuantity()*stock.getRate();
					}
				}
				
				
				double itmReceivedQty=(Double) getSession().createQuery("select coalesce(sum(stock.quantity*stock.rate),0)"
						+ " from TransferStockMap where stock.manufacturing_date<=:dt and stock.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				double resetedQty=0;
				
				Object objID=getSession().createQuery("select max(id) from ItemStockModel where item.id=:itm  and status!=3")
						.setLong("itm", item_id).uniqueResult();
				if(objID!=null){
					ItemStockModel stock=(ItemStockModel)getSession().get(ItemStockModel.class, (Long)objID);
					resetedQty=stock.getQuantity()*stock.getRate();
				}
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)-
						CommonUtil.roundNumber(saleQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(deliveryNoteQty)
						-CommonUtil.roundNumber(itmTransfredQty)+CommonUtil.roundNumber(itmReceivedQty)
						+CommonUtil.roundNumber(resetedQty)+CommonUtil.roundNumber(stockCreate);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		return CommonUtil.roundNumber(balance);
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getSalePurchaseDetails(long item, long office, Date start, Date end) throws Exception{
		List resultList=new ArrayList();
		try{
			begin();
			List saleList=getSession().createQuery("select a from SalesModel a join a.inventory_details_list b where a.date between :start and :end" +
					" and a.office.id=:office and b.item.id=:item")
					.setParameter("start", start).setParameter("end", end).setParameter("item", item).setParameter("office", office).list();
			if(saleList.size()>0){
				Iterator itr=saleList.iterator();
				while (itr.hasNext()) {
					SalesModel mdl=(SalesModel)itr.next();
					ReportBean bean=new ReportBean();
					bean.setId(mdl.getId());
					bean.setNumber((long)0);
					bean.setTitle("Sales");
					bean.setPaymentNo(mdl.getSales_number()+"");
					bean.setPurchase((double)0);
					bean.setPurchaseQty((double)0);
					Iterator it=mdl.getInventory_details_list().iterator();
					while (it.hasNext()) {
						SalesInventoryDetailsModel det=(SalesInventoryDetailsModel)it.next();
						if(det.getItem().getId()==item){
							bean.setSale(CommonUtil.roundNumber(det.getUnit_price()));
							bean.setSalesQty(CommonUtil.roundNumber(det.getQuantity_in_basic_unit()));
							bean.setItem_name(det.getItem().getName());
						}
						else{
							continue;
						}
					}
					resultList.add(bean);
				}
			}
			List purchaseList=getSession().createQuery("select a from PurchaseModel a join a.inventory_details_list b where a.date between :start and :end" +
					" and a.office.id=:office and b.item.id=:item")
					.setParameter("start", start).setParameter("end", end).setParameter("item", item).setParameter("office", office).list();
			if(purchaseList.size()>0){
				Iterator itr=purchaseList.iterator();
				while (itr.hasNext()) {
					PurchaseModel mdl=(PurchaseModel)itr.next();
					ReportBean bean=new ReportBean();
					bean.setId((long)0);
					bean.setNumber(mdl.getId());
					bean.setTitle("Purchase");
					bean.setPaymentNo(mdl.getPurchase_no()+"");
					bean.setSale((double)0);
					bean.setSalesQty((double)0);
					Iterator it=mdl.getPurchase_details_list().iterator();
					while (it.hasNext()) {
						PurchaseInventoryDetailsModel det=(PurchaseInventoryDetailsModel)it.next();
						if(det.getItem().getId()==item){
							bean.setPurchase(CommonUtil.roundNumber(det.getUnit_price()));
							bean.setPurchaseQty(CommonUtil.roundNumber(det.getQty_in_basic_unit()));
							bean.setItem_name(det.getItem().getName());
						}
						else{
							continue;
						}
					}
					resultList.add(bean);
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
