package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingDetailsModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.config.stock.model.ManufacturingModel;
import com.inventory.config.stock.model.ManufacturingStockMap;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 25, 2014
 */
public class ManufacturingDao extends SHibernate implements Serializable{
	
	private static final long serialVersionUID = -7537620312517654733L;

	List resultList = new ArrayList();

	CommonMethodsDao comDao = new CommonMethodsDao();

	public void save(List list) throws Exception {
		try {
			
			begin();
			
			List childList=null;
			ManufacturingModel objModel;
			ManufacturingDetailsModel detailsMdl;
			ManufacturingMapModel mapMdl;
			Iterator it = list.iterator();
			Iterator it2;
			double quantity=0;
			String stockIds="";
			ManufacturingStockMap mapStkMdl=null;
			
			List salDetList;
			SalesInventoryDetailsModel salDetMdl;
			
			while (it.hasNext()) {
				objModel=(ManufacturingModel) it.next();
				getSession().save(objModel);
				
				childList=objModel.getManufacturing_details_list();
				
				it2=childList.iterator();
				while (it2.hasNext()) {
					detailsMdl =  (ManufacturingDetailsModel) it2.next();
					
					stockIds=comDao.decreaseStock(detailsMdl.getItem().getId(), detailsMdl.getQuantityInBasicUnit());
					
					if (objModel.getSalesOrderId() != 0) {

						salDetList =  getSession()
								.createQuery(
										"select b from SalesOrderModel a"
												+ " join a.inventory_details_list b where a.id=:id and b.item.id=:item")
								.setParameter("id", objModel.getSalesOrderId())
								.setParameter("item", objModel.getItem().getId())
								.list();
						if (salDetList != null && salDetList.size() > 0) {
							salDetMdl=(SalesInventoryDetailsModel) salDetList.get(0);
							
							mapMdl=(ManufacturingMapModel) getSession().createQuery(" from ManufacturingMapModel" +
									" where item.id=:item and subItem.id=:subItem")
									.setParameter("item", salDetMdl.getItem().getId())
									.setParameter("subItem", detailsMdl.getItem().getId()).list().get(0);
							
							if(objModel.getQuantity()>salDetMdl.getQunatity())
								quantity=salDetMdl.getQunatity();
							else
								quantity=objModel.getQuantity();
							
							getSession()
									.createQuery(
											"update ItemModel set reservedQuantity=reservedQuantity-:resqty  where id=:id")
									.setParameter("id",	detailsMdl.getItem().getId())
									.setParameter("resqty",	mapMdl.getQuantity()*quantity).executeUpdate();
						}
					}
					getSession()
							.createQuery(
									"update ItemModel set current_balalnce=current_balalnce-:qty  where id=:id")
							.setParameter("id", detailsMdl.getItem().getId())
							.setParameter("qty", detailsMdl.getQuantityInBasicUnit())
							.executeUpdate();
					
					
					if(stockIds.trim().length()>0){
						String[] array=stockIds.substring(0,stockIds.length()-1).split(",");
						if(array!=null&&array.length>0){
							for(String str:array){
								System.out.println("Updating manufacturing stock map here ");
								mapStkMdl=new ManufacturingStockMap();
								mapStkMdl.setManufacturing_detail_id(detailsMdl.getId());
								mapStkMdl.setStock_id(Long.parseLong(str.substring(0,str.indexOf(':'))));
								getSession().save(mapStkMdl);
							}
						}
					}
					
					flush();
				}
				
				ItemStockModel	stock = new ItemStockModel();
				stock.setExpiry_date(objModel.getDate());
				stock.setItem(objModel.getItem());
				stock.setPurchase_id(0);
				stock.setInv_det_id(0);
				
				stock.setQuantity(objModel.getQty_in_basic_unit());
				stock.setBalance(objModel.getQty_in_basic_unit());
				stock.setRate(objModel.getItem().getRate());
				stock.setStatus(SConstants.stock_statuses.MANUFACTURED_STOCK);
				stock.setManufacturing_date(objModel.getDate());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(0);
				stock.setItem_tag("");
				getSession().save(stock);
				
				stock.setBarcode(stock.getId()+"");		
				
				objModel.setStockId(stock.getId());
//				comDao.increaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
				.setParameter("id", objModel.getItem().getId())
				.setParameter("qty", objModel.getQty_in_basic_unit())
				.executeUpdate();
				
				flush();
			}
			
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
	}
	
	public void delete(long manufacNo,Date date,long officeId) throws Exception {
		try {
			begin();
			
			ManufacturingModel objModel;
			ManufacturingDetailsModel detailsMdl;
			ManufacturingMapModel mapMdl;
			SalesInventoryDetailsModel salDetMdl;
			double quantity=0;
			
			Iterator it2;
			Iterator it;
			List mainList=getSession().createQuery("from ManufacturingModel where date=:date and manufacturing_no=:mno and office.id=:ofc")
					.setParameter("date", date).setParameter("mno", manufacNo).setParameter("ofc", officeId).list();
			it=mainList.iterator();
			List childList=null;
			
			List salDetList;
			
			while (it.hasNext()) {
				
				objModel=(ManufacturingModel) it.next();
				
//				comDao.decreaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", objModel.getItem().getId()).setParameter("qty", objModel.getQty_in_basic_unit())
							.executeUpdate();
				
				childList=objModel.getManufacturing_details_list();
				
				it2=childList.iterator();
				while (it2.hasNext()) {
					detailsMdl =  (ManufacturingDetailsModel) it2.next();
					
					comDao.increaseStock(detailsMdl.getItem().getId(), detailsMdl.getQuantityInBasicUnit());
					
					
					if (objModel.getSalesOrderId() != 0) {

						salDetList =  getSession()
								.createQuery(
										"select b from SalesOrderModel a"
												+ " join a.inventory_details_list b where a.id=:id and b.item.id=:item")
								.setParameter("id", objModel.getSalesOrderId())
								.setParameter("item", objModel.getItem().getId())
								.list();
						if (salDetList != null && salDetList.size() > 0) {
							
							salDetMdl=(SalesInventoryDetailsModel) salDetList.get(0);
							
							mapMdl=(ManufacturingMapModel) getSession().createQuery(" from ManufacturingMapModel" +
									" where item.id=:item and subItem.id=:subItem")
									.setParameter("item", salDetMdl.getItem().getId())
									.setParameter("subItem", detailsMdl.getItem().getId()).list().get(0);
							
							if(objModel.getQuantity()>salDetMdl.getQunatity())
								quantity=salDetMdl.getQunatity();
							else
								quantity=objModel.getQuantity();
							
							getSession()
									.createQuery(
											"update ItemModel set reservedQuantity=reservedQuantity+:resqty  where id=:id")
									.setParameter("id",	detailsMdl.getItem().getId())
									.setParameter("resqty",	mapMdl.getQuantity()*quantity).executeUpdate();
						}
					}
					
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", detailsMdl.getItem().getId()).setParameter("qty", detailsMdl.getQuantityInBasicUnit())
								.executeUpdate();
					
					
					getSession()
							.createQuery(
									"delete from ManufacturingStockMap where manufacturing_detail_id=:detId ")
							.setParameter("detId", detailsMdl.getId())
							.executeUpdate();
					
					flush();
				
				}
				getSession().delete(objModel);
				
				ItemStockModel stk=(ItemStockModel) getSession().get(ItemStockModel.class, objModel.getStockId());
				
				getSession().delete(stk);
				
				flush();
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(List list, long manufacNo,long officeId) throws Exception {
		try {
			
			begin();
//			Deleting Old
			ManufacturingModel objModel;
			ManufacturingDetailsModel detModel;
			ManufacturingMapModel mapMdl;
			SalesInventoryDetailsModel salDetMdl;
			
			double quantity=0;
			
			Iterator it2;
			List childList=null;
			List salDetList;
			
			List oldDeleteList=getSession()
					.createQuery("select id from ManufacturingModel where manufacturing_no=:pn and office.id=:officeId")
					.setParameter("pn", manufacNo).setParameter("officeId", officeId).list();
			
			Iterator<ManufacturingModel> it = getSession()
					.createQuery("from ManufacturingModel where manufacturing_no=:pn  and office.id=:officeId")
					.setParameter("pn", manufacNo).setParameter("officeId", officeId).list().iterator();
			
			while (it.hasNext()) {
				
				objModel=it.next();
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", objModel.getItem().getId()).setParameter("qty", objModel.getQty_in_basic_unit())
							.executeUpdate();
				
				childList=objModel.getManufacturing_details_list();
				it2=childList.iterator();
				while (it2.hasNext()) {
					
					detModel =  (ManufacturingDetailsModel) it2.next();
					
					comDao.increaseStock(detModel.getItem().getId(),  detModel.getQuantityInBasicUnit());
					
					// Updating sales order
					if (objModel.getSalesOrderId() != 0) {

						salDetList =  getSession()
								.createQuery(
										"select b from SalesOrderModel a"
												+ " join a.inventory_details_list b where a.id=:id and b.item.id=:item")
								.setParameter("id", objModel.getSalesOrderId())
								.setParameter("item", objModel.getItem().getId())
								.list();
						if (salDetList != null && salDetList.size() > 0) {
							
							salDetMdl=(SalesInventoryDetailsModel) salDetList.get(0);
							
							mapMdl=(ManufacturingMapModel) getSession().createQuery(" from ManufacturingMapModel" +
									" where item.id=:item and subItem.id=:subItem")
									.setParameter("item", salDetMdl.getItem().getId())
									.setParameter("subItem", detModel.getItem().getId()).list().get(0);
							
							if(objModel.getQuantity()>salDetMdl.getQunatity())
								quantity=salDetMdl.getQunatity();
							else
								quantity=objModel.getQuantity();
							
							getSession().createQuery("update ItemModel set reservedQuantity=reservedQuantity+:resqty  where id=:id")
									.setParameter("id",	detModel.getItem().getId()).setParameter("resqty",	mapMdl.getQuantity()*quantity).executeUpdate();
						}
					}
					// Updating sales order
					
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", detModel.getItem().getId()).setParameter("qty", detModel.getQuantityInBasicUnit())
								.executeUpdate();
					
					
					getSession().createQuery("delete from ManufacturingStockMap where manufacturing_detail_id=:detId ")
							.setParameter("detId", detModel.getId()).executeUpdate();
					
					flush();
				}
				
//				getSession().delete(objModel);
				flush();
			}
			
			flush();
			
			
//			Saving New
			
			String stockIds="";
			ManufacturingStockMap mapStkMdl=null;
			
			
			objModel=null;
			detModel=null;
			salDetList=null;
			it2=null;
			quantity=0;
			it = list.iterator();
			while (it.hasNext()) {
				
				objModel=(ManufacturingModel) it.next();
				
				if(objModel.getId()!=0)
					oldDeleteList.remove(objModel.getId());
				getSession().clear();
				if(objModel.getId()!=0)
					getSession().update(objModel);
				else
					getSession().save(objModel);
				flush();
				childList=objModel.getManufacturing_details_list();
				
				it2=childList.iterator();
				
				while (it2.hasNext()) {
					
					detModel =  (ManufacturingDetailsModel) it2.next();
					
					stockIds=comDao.decreaseStock(detModel.getItem().getId(), detModel.getQuantityInBasicUnit());
					
					
					if (objModel.getSalesOrderId() != 0) {

						salDetList =  getSession()
								.createQuery("select b from SalesOrderModel a"
												+ " join a.inventory_details_list b where a.id=:id and b.item.id=:item")
								.setParameter("id", objModel.getSalesOrderId())
								.setParameter("item", objModel.getItem().getId())
								.list();
						
						if (salDetList != null && salDetList.size() > 0) {
							
							salDetMdl=(SalesInventoryDetailsModel) salDetList.get(0);
							
							mapMdl=(ManufacturingMapModel) getSession().createQuery(" from ManufacturingMapModel" +
									" where item.id=:item and subItem.id=:subItem")
									.setParameter("item", salDetMdl.getItem().getId())
									.setParameter("subItem", detModel.getItem().getId()).list().get(0);
							
							if(objModel.getQuantity()>salDetMdl.getQunatity())
								quantity=salDetMdl.getQunatity();
							else
								quantity=objModel.getQuantity();
							
							getSession()
									.createQuery(
											"update ItemModel set reservedQuantity=reservedQuantity-:resqty where id=:id")
									.setParameter("id",	detModel.getItem().getId())
									.setParameter("resqty",	mapMdl.getQuantity()*quantity).executeUpdate();
						}
					}
					
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", detModel.getItem().getId()).setParameter("qty", detModel.getQuantityInBasicUnit())
						.executeUpdate();
					
					if(stockIds.trim().length()>0){
						String[] array=stockIds.substring(0,stockIds.length()-1).split(",");
						if(array!=null&&array.length>0){
							for(String str:array){
								mapStkMdl=new ManufacturingStockMap();
								mapStkMdl.setManufacturing_detail_id(detModel.getId());
								mapStkMdl.setStock_id(Long.parseLong(str.substring(0,str.indexOf(':'))));
								getSession().save(mapStkMdl);
							}
						}
					}
					
					
					flush();
				}
				
//				comDao.increaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
				.setParameter("id", objModel.getItem().getId())
				.setParameter("qty", objModel.getQty_in_basic_unit())
				.executeUpdate();
				
				flush();
				
				
				ItemStockModel stock;
				
				if(objModel.getId()!=0) {
					Object obj=getSession().createQuery("from ItemStockModel where id=:pid ")
							.setLong("pid",objModel.getStockId()).uniqueResult();
					if(obj!=null)
						stock=(ItemStockModel) obj;
					else
						stock = new ItemStockModel();
				}
				else
					stock = new ItemStockModel();
				
				stock.setExpiry_date(objModel.getDate());
				stock.setItem(objModel.getItem());
				stock.setPurchase_id(0);
				stock.setInv_det_id(0);
				stock.setQuantity(objModel.getQty_in_basic_unit());
				stock.setBalance(objModel.getQty_in_basic_unit());
				stock.setRate(objModel.getItem().getRate());
				
				stock.setStatus(SConstants.stock_statuses.MANUFACTURED_STOCK);
				stock.setManufacturing_date(objModel.getDate());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(0);
				stock.setItem_tag("");
				
				if(stock.getId()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				
				
				if(stock.getBarcode()==null||stock.getBarcode().trim().length()<=0){
					stock.setBarcode(stock.getId()+"");
				}
				
				objModel.setStockId(stock.getId());
				
//				objModel=(ManufacturingModel) getSession().merge(objModel);
				
				getSession().update(objModel);
				/*if(objModel.getId()!=0)
					getSession().update(objModel);
				else
					getSession().save(objModel);*/	
				flush();
				
				
			}
			
			if (oldDeleteList != null) {
				it = oldDeleteList.iterator();
				ManufacturingModel manufacturingModel;
				while (it.hasNext()) {
					manufacturingModel = (ManufacturingModel)getSession().get(ManufacturingModel.class, it
							.next());
					getSession().delete(manufacturingModel);

				}
			}
			
			
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
	}


	public List getAllProductionNumbers(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select distinct manufacturing_no from ManufacturingModel where office.id=:ofc order by manufacturing_no desc")
					.setParameter("ofc", ofc_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getProductionDetails(long manufacNo,long office)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("from ManufacturingModel where manufacturing_no=:id and office.id=:office")
					.setParameter("id", manufacNo).setParameter("office", office).list();
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
	
	public List getManufacturingMapDetails(long item)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("from ManufacturingMapModel where item.id=:id")
					.setParameter("id", item).list();
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
	

	public ItemStockModel getItemStocks(long id) throws Exception {
		ItemStockModel stk = null;
		try {
			begin();
			stk = (ItemStockModel) getSession().get(ItemStockModel.class, id);
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
		return stk;
	}

	public List getAllActiveItemsWithAppendingItemCode(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ',item_code,' ) ',' Bal: ' , current_balance))"
									+ " from ItemModel  where office.id=:ofc and status=:sts")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
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

	public List getItemForManufacturing(Long parentItem) throws Exception {
		List childList;
		try {
			begin();
			childList=getSession().createQuery("from ManufacturingMapModel where item.id=:item order by subItem.name").setParameter("item", parentItem).list();
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
		return childList;
	}

	
	
	
	public List getManufacturingReport(long itemID, Date fromDate,Date toDate, long officeID) throws Exception {
		try {
			begin();
			String cond="";
			if(itemID!=0)
				cond+=" and item.id="+itemID;

			resultList = getSession()
					.createQuery(" from ManufacturingModel  where office.id=:ofc  and date between :frm and :to"+cond+" order by date desc, item.name" ).setParameter("frm", fromDate)
				.setParameter("to", toDate).setParameter("ofc", officeID)
					.list();
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
	
	
	
	public List getRawMaterialConsumptionReport(Date fromDate, Date toDate,
			Long officeId, List rawList) throws Exception {
		
		List list=new ArrayList();
		try {
			
			begin();

			Iterator iter = rawList.iterator();
			ItemModel item;

			while (iter.hasNext()) {
				item = (ItemModel) iter.next();

				list.addAll(getSession()
						.createQuery(
								" select new com.webspark.bean.ReportBean(cast (a.date as string),b.item.name,b.unit.symbol,sum(b.quantity))"
										+ " from ManufacturingModel a join a.manufacturing_details_list b  where a.office.id=:ofc and a.date between :frm and :to"
										+ " and b.item.id=:subItem group by b.item.name order by date desc, b.item.name")
						.setParameter("frm", fromDate)
						.setParameter("to", toDate)
						.setParameter("ofc", officeId)
						.setParameter("subItem", item.getId()).list());
			}

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
	

	public ManufacturingModel getManufacturingModel(long id) throws Exception {
		ManufacturingModel stk = null;
		try {
			begin();
			stk = (ManufacturingModel) getSession().get(ManufacturingModel.class, id);
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
		return stk;
	}
}
