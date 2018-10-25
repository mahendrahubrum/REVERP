package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.bean.ReportBean;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * WebSpark.
 * Apr 7, 2014
 */
public class LossSalesDao extends SHibernate{

	CommonMethodsDao comDao=new CommonMethodsDao();
	
	public List getLossSalesReport(long officeId, long itemId, Date fromDate, Date toDate) throws Exception {
		
		List list = null;
		List itemList = new ArrayList();
		Iterator mainIter;
		Iterator it;
		SalesModel salObj;
		SalesInventoryDetailsModel invObj;
		ReportBean rptModel;
		Object obj,obj1;
		long stockId,itemID;
		double saledValue,purchasedValue,purchase_amt;
		String[] stks;
		int count=0;
		ItemStockModel stkObj=null;
//		UnitModel unitObj=null;
		
		List reportList = new ArrayList();
		try {
			begin();
			if(itemId==0){
				itemList.addAll(getSession().createQuery("select id from ItemModel where office.id=:ofc").setParameter("ofc", officeId).list());
			}else{
				itemList.add(itemId);
			}
			
			Iterator iter=itemList.iterator();
			
			while (iter.hasNext()) {
				itemID = (Long) iter.next();
				
				list = getSession().createQuery(
								"select a from SalesModel a join a.inventory_details_list b" +
								" where a.date between :from and :to and a.office.id=:ofc and b.item.id = :item and a.active=true" +
								" group by a.id order by a.sales_number desc,a.date desc ")
						.setParameter("ofc", officeId)
						.setParameter("from", fromDate).setParameter("to", toDate)
						.setParameter("item", itemID).list();
				
				mainIter=list.iterator();
				 
				while (mainIter.hasNext()) {
					salObj = (SalesModel) mainIter.next();
					
					
					it=salObj.getInventory_details_list().iterator();
					
					
					while (it.hasNext()) {
						rptModel=new ReportBean();
						invObj=(SalesInventoryDetailsModel) it.next();
						saledValue=0; purchasedValue=0; 
						
						if (itemID != 0) {
							if (itemID == invObj.getItem().getId()) {

								rptModel.setItem_name(invObj.getItem().getName());
								rptModel.setNumber(salObj.getId()); // Sales Id
								rptModel.setDate(salObj.getDate() + "");
								rptModel.setId(Long.parseLong(salObj.getSales_number()));
								rptModel.setQuantity(invObj.getQunatity());
								stockId = 0;
								purchase_amt = 0;
								count = 0;
								if (invObj.getStock_ids().length() > 0) {
									stks = invObj.getStock_ids().toString()
											.split(",");
									for (int i = 0; i < stks.length; i++) {
										try {

											stockId = Long.parseLong(stks[i]
													.split(":")[0]);

											obj = getSession().createQuery("from ItemStockModel where id="+ stockId).uniqueResult();
											if (obj != null) {
												stkObj=(ItemStockModel) obj;
													purchase_amt += stkObj.getRate();
												count++;
											}

										} catch (Exception e) {
										}
									}
								}

								rptModel.setRate(invObj.getUnit_price());
								rptModel.setAmount(purchase_amt/count);
								
								saledValue += (invObj.getQunatity() * invObj
										.getUnit_price());
								purchasedValue += (purchase_amt / count)
										* invObj.getQuantity_in_basic_unit();

							}
						}else{
							
							rptModel.setItem_name(invObj.getItem().getName());
							rptModel.setDate(salObj.getDate()+"");
							rptModel.setId(Long.parseLong(salObj.getSales_number()));
							rptModel.setNumber(salObj.getId()); // Sales Id
							rptModel.setQuantity(invObj.getQunatity());
							
							stockId=0;purchase_amt=0;
							count=0;
							if(invObj.getStock_ids().length()>0) {
								stks=invObj.getStock_ids().toString().split(",");
								for (int i = 0; i < stks.length; i++) {
									try {
									
									stockId=Long.parseLong(stks[i].split(":")[0]);
									
									obj=getSession().createQuery(
											"select rate from ItemStockModel where id="+stockId).uniqueResult();
									if(obj!=null) {
										purchase_amt+=(Double)obj;
										count++;
									}
								} catch (Exception e) {
								}
							}
						}
						rptModel.setRate(invObj.getUnit_price());
						rptModel.setAmount(purchase_amt/count);
						saledValue+=(invObj.getQunatity()*invObj.getUnit_price());
						
						purchasedValue+=(purchase_amt/count)*invObj.getQuantity_in_basic_unit();
							
						}
						
						if(purchasedValue>saledValue) {
							rptModel.setInwards(saledValue);
							rptModel.setOutwards(purchasedValue);
							rptModel.setProfit(purchasedValue-saledValue);
							reportList.add(rptModel);
						}
					
					}
				}
				
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} 
		flush();
		close();

		return reportList;
	}

	
}
