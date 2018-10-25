package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.StockAuditingReportBean;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 18, 2014
 */
public class StockAuditingReportDao extends SHibernate implements Serializable {
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	private static final long serialVersionUID = 7550361275087510643L;
	
	public List getStockAuditingReport(long itemId,long custId, long salesId,
			long supplierId, long purchaseId, Date fromDate, Date toDate,
			Long officeId) throws Exception {
		
		List reportList=new ArrayList();
		try {
			String saleCon="";
			String purchaseCon="";
			
			if(custId!=0)
				saleCon=" and a.customer.id="+custId;
			if(salesId!=0)
				saleCon=" and a.id="+salesId;
			if(supplierId!=0)
				purchaseCon=" and a.supplier.id="+supplierId;
			if(purchaseId!=0)
				purchaseCon=" and a.id="+purchaseId;
			
			begin();
			
			List itemList=null;
			List salesList=null;
			List purchaseList=null;
			StockAuditingReportBean saleBean=null;
			StockAuditingReportBean bean=null;
			StockAuditingReportBean purchaseBean=null;
			
			Iterator purchIterator;
			Iterator salesIterator;
			Iterator subIter;
			
			PurchaseModel purchMdl;
			SalesModel salesMdl;
			SalesInventoryDetailsModel saleInvDetMdl;
			
			long stockId;
			double qty;
			String[] stks;
			Object obj;
			long purchase=0;
			
			ItemModel itemMdl=null;
			ItemStockModel stockMdl=null;
			String itemCond="";
			if(itemId!=0)
				itemCond=" and id="+itemId;
			
			itemList=getSession().createQuery("from ItemModel where office.id=:ofc "+itemCond+" order by name").setParameter("ofc", officeId).list();
			Iterator it=itemList.iterator();
			
			while (it.hasNext()) {
				itemMdl = (ItemModel) it.next();
					
				salesList=getSession().createQuery("select new com.inventory.reports.bean.StockAuditingReportBean" +
						"(cast (a.date as string),b.stock_ids,cast (a.sales_number as string),a.customer.name,b.qunatity,b.unit_price,a.id,b.unit.id,b.unit.symbol )" +
						" from SalesModel a join a.inventory_details_list b where a.office.id=:ofc" +
						" and a.date between :from and :to and b.item.id=:item and a.active=true"+saleCon+" order by a.id desc")
						.setParameter("ofc", officeId)
						.setParameter("item", itemMdl.getId())
						.setParameter("from", fromDate).setParameter("to", toDate).list();
				
				salesIterator=salesList.iterator();
				while (salesIterator.hasNext()) {
					saleBean= (StockAuditingReportBean) salesIterator.next();
					stockId=0;qty=0;
					
					if(saleBean.getStockIds().charAt(saleBean.getStockIds().length()-1)!=',')
						saleBean.setStockIds(saleBean.getStockIds()+",");
						
					stks=saleBean.getStockIds().split(",");
						for (int i = 0; i < stks.length; i++) {
						try {

							stockId = Long.parseLong(stks[i].split(":")[0]);
							qty = Double.parseDouble(stks[i].split(":")[1]);
							
							stockMdl=(ItemStockModel) getSession().get(ItemStockModel.class, stockId);
							
							if(stockMdl!=null){

							purchaseList =  getSession()
									.createQuery(
											"select new com.inventory.reports.bean.StockAuditingReportBean" +
											"(a.id,cast (a.date as string),cast (a.purchase_number as string),a.supplier.name,b.qunatity,b.unit_price,b.unit.id,b.unit.symbol,b.id)"
											+ " from PurchaseModel a join a.inventory_details_list b where"
											+ " a.id =:purchId and b.item.id=:item and a.active=true and b.id=:invId"
											+ purchaseCon).setParameter("purchId",stockMdl.getPurchase_id())
									.setParameter("invId",stockMdl.getInv_det_id())
									.setParameter("item", itemMdl.getId())
									.list();
							
							purchIterator=purchaseList.iterator();
								while (purchIterator.hasNext()) {
									purchaseBean = (StockAuditingReportBean) purchIterator
											.next();

									if (purchaseBean != null) {
										bean = new StockAuditingReportBean();
										bean.setItem(itemMdl.getName());

										bean.setPurchaseDate(purchaseBean
												.getPurchaseDate());
										bean.setPurchaseNo(purchaseBean
												.getPurchaseNo());
										bean.setPurchaseQuantity(purchaseBean
												.getPurchaseQuantity());

										// bean.setPurchaseRate(purchaseBean.getPurchaseRate());

										bean.setPurchaseNormalRate(purchaseBean
												.getPurchaseRate());
										bean.setPurchaseRate(purchaseBean
												.getPurchaseRate()
												* comDao.getUnitConvertionValue(
														itemMdl.getId(),
														purchaseBean
																.getPurch_unit(),
														saleBean.getSales_unit()));
										bean.setPurchaseId(purchaseBean.getPurchaseId());
										bean.setSupplier(purchaseBean
												.getSupplier());
										bean.setPurchaseUnit(purchaseBean
												.getPurchaseUnit());

										bean.setSalesDate(saleBean
												.getSalesDate());
										bean.setSalesNo(saleBean.getSalesNo());
										bean.setSalesQuantity(saleBean
												.getSalesQuantity());
										bean.setSalesRate(saleBean
												.getSalesRate());
										bean.setCustomer(saleBean.getCustomer());
										bean.setSaleUnit(saleBean.getSaleUnit());

										bean.setSalesReturnQuantity(0);
										bean.setPurchaseReturnQuantity(0);
										bean.setProfit((bean.getSalesRate() - bean
												.getPurchaseRate())
												* bean.getSalesQuantity()
												* comDao.getUnitConvertionValue(
														itemMdl.getId(),
														saleBean.getPurch_unit(),
														purchaseBean
																.getSales_unit()));
										bean.setSaleId(saleBean.getSaleId());

										reportList.add(bean);
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							}
						}
					
				}
				
				
				
//				purchaseList=getSession().createQuery("select a from PurchaseModel a join a.inventory_details_list b where a.office.id=:ofc" +
//						" and a.date between :from and :to and b.item.id=:item"+purchaseCon)
//						.setParameter("ofc", officeId)
//						.setParameter("item", itemMdl.getId())
//						.setParameter("from", fromDate).setParameter("to", toDate).list();
				
				
			}
			
			
			
			
			
			
			
//			purchIterator=purchaseList.iterator();
//			while (purchIterator.hasNext()) {
//				purchMdl= (PurchaseModel) purchIterator.next();
//				bean=new StockAuditingReportBean();
//				bean.setItem(itemMdl.getName());
//			}
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			 e.printStackTrace();
			 throw e;
		}
		flush();
		close();
		
		return reportList;
	}

	public List getPurchaseStockAuditingReport(long itemId, long custId,
			long salesId, long supplierId, long purchaseId,
			Date fromDate, Date toDate, Long officeId) throws Exception {
		
		
		List reportList=new ArrayList();
		try {
			String purchaseCon="";
			String saleCon="";
			if(custId!=0)
				saleCon=" and a.customer.id="+custId;
			if(salesId!=0)
				saleCon=" and a.id="+salesId;
			if(supplierId!=0)
				purchaseCon=" and a.supplier.id="+supplierId;
			if(purchaseId!=0)
				purchaseCon=" and a.id="+purchaseId;
			
			begin();
			
			List itemList=null;
			List salesList=null;
			List purchaseList=null;
			List mapList=null;
			List stockList=null;
			StockAuditingReportBean saleBean=null;
			StockAuditingReportBean bean=null;
			StockAuditingReportBean purchaseBean=null;
			
			Iterator purchIterator;
			Iterator salesIterator;
			Iterator subIter;
			Iterator stockIter;
			Iterator mapIter;
			
			PurchaseModel purchMdl;
			ItemStockModel itemStockModel;
			SalesModel salesMdl;
			SalesStockMapModel mapMdl;
			
			double qty;
			
			ItemModel itemMdl=null;
			String itemCond="";
			if(itemId!=0)
				itemCond=" and id="+itemId;
			
			itemList=getSession().createQuery("from ItemModel where office.id=:ofc "+itemCond+" order by name").setParameter("ofc", officeId).list();
			Iterator it=itemList.iterator();
			
			while (it.hasNext()) {
				itemMdl = (ItemModel) it.next();
					
				purchaseList=getSession().createQuery("select new com.inventory.reports.bean.StockAuditingReportBean" +
						"(a.id,cast (a.date as string),cast (a.purchase_number as string),a.supplier.name,b.qunatity,b.unit_price,b.unit.id,b.unit.symbol, b.id)" +
						" from PurchaseModel a join a.inventory_details_list b where a.office.id=:ofc" +
						" and a.date between :from and :to and b.item.id=:item and a.active=true"+purchaseCon+" order by a.id desc")
						.setParameter("ofc", officeId)
						.setParameter("item", itemMdl.getId())
						.setParameter("from", fromDate).setParameter("to", toDate).list();
				
				purchIterator=purchaseList.iterator();
				while (purchIterator.hasNext()) {
					purchaseBean= (StockAuditingReportBean) purchIterator.next();
					
					stockList = getSession()
							.createQuery(
									"from ItemStockModel where purchase_id=:purch and inv_det_id=:inv")
							.setParameter("purch", purchaseBean.getPurchaseId()).setLong("inv", purchaseBean.getInv_id())
							.list();
					stockIter=stockList.iterator();
						while (stockIter.hasNext()) {
						try {
							itemStockModel=(ItemStockModel) stockIter.next();
							
							if(itemStockModel!=null){
								
							mapList=getSession().createQuery("from SalesStockMapModel where stockId=:stk").setParameter("stk", itemStockModel.getId()).list();
							
							mapIter=mapList.iterator();
							while (mapIter.hasNext()) {
									qty=0;
								mapMdl = (SalesStockMapModel) mapIter.next();
								
								qty=mapMdl.getQuantity();
							salesList =  getSession()
									.createQuery("select new com.inventory.reports.bean.StockAuditingReportBean" +
											"(a.id,cast (a.date as string),cast (a.sales_number as string),a.customer.name,b.qunatity,b.unit_price,b.unit.id,b.unit.symbol,b.id)"
											+ " from SalesModel a join a.inventory_details_list b where"
											+ " a.id =:sal and b.item.id=:item and a.active=true and b.id=:invId"+saleCon)
									.setParameter("sal",mapMdl.getSalesId()).setParameter("invId",mapMdl.getSalesInventoryId())
									.setParameter("item", itemMdl.getId()).list();
							
							salesIterator=salesList.iterator();
						while (salesIterator.hasNext()) {
							saleBean = (StockAuditingReportBean) salesIterator.next();
								
							if(saleBean!=null){
								bean=new StockAuditingReportBean();
								bean.setItem(itemMdl.getName());
									
								bean.setPurchaseDate(purchaseBean.getPurchaseDate());
								bean.setPurchaseNo(purchaseBean.getPurchaseNo());
								bean.setPurchaseQuantity(purchaseBean.getPurchaseQuantity());
	//							bean.setPurchaseRate(purchaseBean.getPurchaseRate());
								bean.setPurchaseRate(purchaseBean.getPurchaseRate());
								bean.setSupplier(purchaseBean.getSupplier());
								bean.setPurchaseUnit(purchaseBean.getPurchaseUnit());
								bean.setPurchaseId(purchaseBean.getPurchaseId());
								
								bean.setSalesDate(saleBean.getPurchaseDate());
								bean.setSalesNo(saleBean.getPurchaseNo());
								bean.setSalesQuantity(saleBean.getPurchaseQuantity());
								bean.setSalesNormalRate(saleBean.getPurchaseRate());
								bean.setSalesRate(saleBean.getPurchaseRate()*comDao.getUnitConvertionValue(itemMdl.getId() ,saleBean.getPurch_unit(),purchaseBean.getPurch_unit()));
								bean.setCustomer(saleBean.getSupplier());
								bean.setSaleUnit(saleBean.getPurchaseUnit());
								
								bean.setSalesReturnQuantity(0);
								bean.setPurchaseReturnQuantity(0);
								bean.setProfit((bean.getSalesRate()-bean.getPurchaseRate())*bean.getSalesQuantity()*comDao.getUnitConvertionValue(itemMdl.getId() ,purchaseBean.getPurch_unit(),saleBean.getPurch_unit()));
								bean.setSaleId(saleBean.getPurchaseId());
									
								reportList.add(bean);
							}
							}
							}
							}
						} catch (Exception e) {
							e.printStackTrace();
							}
						}
					
				}
				
				
				
//				purchaseList=getSession().createQuery("select a from PurchaseModel a join a.inventory_details_list b where a.office.id=:ofc" +
//						" and a.date between :from and :to and b.item.id=:item"+purchaseCon)
//						.setParameter("ofc", officeId)
//						.setParameter("item", itemMdl.getId())
//						.setParameter("from", fromDate).setParameter("to", toDate).list();
				
				
			}
			
			
			
			
			
			
//			purchIterator=purchaseList.iterator();
//			while (purchIterator.hasNext()) {
//				purchMdl= (PurchaseModel) purchIterator.next();
//				bean=new StockAuditingReportBean();
//				bean.setItem(itemMdl.getName());
//			}
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			 e.printStackTrace();
			 throw e;
		}
		flush();
		close();
		
		return reportList;
	
		
		
	}
	
}
