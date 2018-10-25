package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.ManufacturingDetailsModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.config.stock.model.ManufacturingModel;
import com.inventory.config.stock.model.ManufacturingStockMap;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.ManufacturingProfitBean;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ManufacturingProfitReportDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public List getSalesWiseProfitReport(long item,Date start,Date end,long office)throws Exception{
		List resultList=new ArrayList();
		try{
			begin();
			List mainsalesList=new ArrayList();
			List salesList=new ArrayList();
			List stockList=new ArrayList();
			List mapstock=new ArrayList();
			List mapstockList=new ArrayList();
			SalesModel mdl=null;
			SalesInventoryDetailsModel smdl=null;
			SalesStockMapModel smap=null;
			ManufacturingModel mmdl=null;
			ManufacturingDetailsModel mdmdl=null;
			ManufacturingStockMap msmap=null;
			ManufacturingMapModel mapmdl=null;
			ManufacturingProfitBean bean;
			ItemStockModel stkmdl=null;
			double totalSale=0;
			double totalPurchase=0;
			double expense=0;
			String condition="";
			if(item!=0)
				condition+=" and b.item.id="+item;
			else
				condition+=" and b.item.affect_type="+4;
			mainsalesList=getSession().createQuery("select a from SalesModel a join a.inventory_details_list b where 1=1 and a.date between :start and :end and a.office.id=:ofc"+condition+" order by b.item.id")
					.setParameter("ofc", office).setParameter("start", start).setParameter("end", end).list();
			Iterator sitr=mainsalesList.iterator();
			while(sitr.hasNext()){
				mdl=(SalesModel)sitr.next();
				salesList=getSession().createQuery("select b from SalesModel a join a.inventory_details_list b where a.id=:sid"+condition)
						.setParameter("sid", mdl.getId()).list();
				if(salesList.size()>0){
					Iterator itr=salesList.iterator();
					while(itr.hasNext()){ // Sales Inventory Details Model
						smdl=(SalesInventoryDetailsModel)itr.next();
						totalPurchase=0;
						totalSale=smdl.getUnit_price()*smdl.getQuantity_in_basic_unit();
						stockList=getSession().createQuery("from SalesStockMapModel where salesInventoryId="+smdl.getId()).list(); // Stock Id List
						if(stockList.size()>0){
							Iterator stockItr=stockList.iterator();
							while(stockItr.hasNext()){ // Sales Stock Map Model
								smap=(SalesStockMapModel)stockItr.next();
								if(smap!=null){
									mmdl=(ManufacturingModel)getSession().createQuery("from ManufacturingModel a where a.stockId=:stk and a.item.id="+smdl.getItem().getId())
											.setParameter("stk", smap.getStockId()).uniqueResult(); // Manufacturing Model
									if(mmdl!=null){
										expense=mmdl.getExpense()/mmdl.getQty_in_basic_unit();
										mapstock=getSession().createQuery("select b from ManufacturingModel a join a.manufacturing_details_list b" +
												" where a.id="+mmdl.getId()).list(); // Manufacturing Details Model
										Iterator mstkitr=mapstock.iterator();
										while(mstkitr.hasNext()){
											mdmdl=(ManufacturingDetailsModel)mstkitr.next();
											mapstockList=getSession().createQuery("from ManufacturingStockMap where manufacturing_detail_id="+mdmdl.getId()).list();
											double rate=0;
											for(int i=0;i<mapstockList.size();i++){
												msmap=(ManufacturingStockMap)mapstockList.get(i);
												stkmdl=(ItemStockModel)getSession().get(ItemStockModel.class, msmap.getStock_id());
												mapmdl=(ManufacturingMapModel)getSession().createQuery("from ManufacturingMapModel where item.id=:id and subItem.id="+stkmdl.getItem().getId())
														.setParameter("id", mmdl.getItem().getId()).list().get(0);
												rate+=((mapmdl.getQty_in_basic_unit()/mapmdl.getMaster_quantity())*stkmdl.getRate());
											}
											totalPurchase+=rate/mapstockList.size();
										}
									}
								}
							}  // Sales Stock Map Model
						}
//						bean=new ManufacturingProfitBean(mdl.getSales_number(),mdl.getId(),smdl.getItem().getName(), (totalPurchase*smdl.getQuantity_in_basic_unit()), totalSale, (expense*smdl.getQuantity_in_basic_unit()), mdl.getDate().toString());
						bean=new ManufacturingProfitBean();
						
						resultList.add(bean);
					} // Sales Inventory Details Model
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
	
	@SuppressWarnings("rawtypes")
	public List getItemWiseProfitReport(long item,Date start,Date end,long office)throws Exception{
		List resultList=new ArrayList();
		try{
			begin();
			List mainsalesList=new ArrayList();
			List salesList=new ArrayList();
			List stockList=new ArrayList();
			List mapstock=new ArrayList();
			List mapstockList=new ArrayList();
			List itemList=new ArrayList();
			SalesModel mdl=null;
			SalesInventoryDetailsModel smdl=null;
			SalesStockMapModel smap=null;
			ManufacturingModel mmdl=null,mmmdl=null;
			ManufacturingDetailsModel mdmdl=null;
			ManufacturingStockMap msmap=null;
			ManufacturingMapModel mapmdl=null;
			ManufacturingProfitBean bean;
			ItemStockModel stkmdl=null;
			double totalSale=0;
			double totalPurchase=0;
			double expense=0;
			String condition="";
			String cdn="";
			if(item!=0){
				condition+=" and b.item.id="+item;
				cdn+=" and item.id="+item;
			}
			else{
				condition+=" and b.item.affect_type="+4;
				cdn+=" and item.affect_type="+4;
			}
			itemList=getSession().createQuery("from ManufacturingModel where 1=1"+cdn).list();
			for(int j=0;j<itemList.size();j++){
				double sale=0,pur=0,exp=0;
				mmmdl=(ManufacturingModel)itemList.get(j);
				if(mmmdl!=null){
					if(item!=0){
						mainsalesList=getSession().createQuery("select a from SalesModel a join a.inventory_details_list b" +
								" where 1=1 and a.date between :start and :end and a.office.id=:ofc"+condition+" order by b.item.id")
								.setParameter("ofc", office).setParameter("start", start).setParameter("end", end).list();
					}
					else{
						mainsalesList=getSession().createQuery("select a from SalesModel a join a.inventory_details_list b" +
								" where 1=1 and a.date between :start and :end and a.office.id=:ofc and b.item.id=:id order by b.item.id")
								.setParameter("ofc", office)
								.setParameter("start", start)
								.setParameter("id", mmmdl.getItem().getId())
								.setParameter("end", end).list();
					}
					if(mainsalesList.size()>0){
						Iterator sitr=mainsalesList.iterator();
						while(sitr.hasNext()){
							mdl=(SalesModel)sitr.next();
							salesList=getSession().createQuery("select b from SalesModel a join a.inventory_details_list b where a.id=:sid"+condition)
									.setParameter("sid", mdl.getId()).list();
							if(salesList.size()>0){
								Iterator itr=salesList.iterator();
								while(itr.hasNext()){ // Sales Inventory Details Model
									smdl=(SalesInventoryDetailsModel)itr.next();
									totalPurchase=0;
									totalSale=smdl.getUnit_price()*smdl.getQuantity_in_basic_unit();
									stockList=getSession().createQuery("from SalesStockMapModel where salesInventoryId="+smdl.getId()).list(); // Stock Id List
									if(stockList.size()>0){
										Iterator stockItr=stockList.iterator();
										while(stockItr.hasNext()){ // Sales Stock Map Model
											smap=(SalesStockMapModel)stockItr.next();
											if(smap!=null){
												mmdl=(ManufacturingModel)getSession().createQuery("from ManufacturingModel a where a.stockId=:stk and a.item.id="+smdl.getItem().getId())
														.setParameter("stk", smap.getStockId()).uniqueResult(); // Manufacturing Model
												if(mmdl!=null){
													expense=mmdl.getExpense()/mmdl.getQty_in_basic_unit();
													mapstock=getSession().createQuery("select b from ManufacturingModel a join a.manufacturing_details_list b" +
															" where a.id="+mmdl.getId()).list(); // Manufacturing Details Model
													Iterator mstkitr=mapstock.iterator();
													while(mstkitr.hasNext()){
														mdmdl=(ManufacturingDetailsModel)mstkitr.next();
														mapstockList=getSession().createQuery("from ManufacturingStockMap where manufacturing_detail_id="+mdmdl.getId()).list();
														double rate=0;
														for(int i=0;i<mapstockList.size();i++){
															msmap=(ManufacturingStockMap)mapstockList.get(i);
															stkmdl=(ItemStockModel)getSession().get(ItemStockModel.class, msmap.getStock_id());
															mapmdl=(ManufacturingMapModel)getSession().createQuery("from ManufacturingMapModel where item.id=:id and subItem.id="+stkmdl.getItem().getId())
																	.setParameter("id", mmdl.getItem().getId()).list().get(0);
															rate+=((mapmdl.getQty_in_basic_unit()/mapmdl.getMaster_quantity())*stkmdl.getRate());
														}
														totalPurchase+=rate/mapstockList.size();
													}
												}
											}
										}  // Sales Stock Map Model
									}
									sale+=totalSale;
									exp+=(expense*smdl.getQuantity_in_basic_unit());
									pur+=(totalPurchase*smdl.getQuantity_in_basic_unit());
								} // Sales Inventory Details Model
							}
						}
					}
				}
				if(mainsalesList.size()>0){
					bean=new ManufacturingProfitBean(smdl.getItem().getName(), pur, sale, exp);
					resultList.add(bean);
				}
				else{
					
				}
				// print Here
				
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
