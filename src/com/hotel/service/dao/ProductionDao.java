package com.hotel.service.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hotel.service.model.ProductionDetailsModel;
import com.hotel.service.model.ProductionModel;
import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */

public class ProductionDao extends SHibernate implements Serializable{
	
	List resultList = new ArrayList();

	CommonMethodsDao comDao = new CommonMethodsDao();

	public void save(List list) throws Exception {
		try {
			
			begin();
			
			ProductionModel objModel;
			ProductionDetailsModel detObj;
			Iterator it2;
			Iterator it = list.iterator();
			while (it.hasNext()) {
				objModel=(ProductionModel) it.next();
				getSession().save(objModel);
				
				it2=objModel.getDetails_list().iterator();
				while (it2.hasNext()) {
					detObj =  (ProductionDetailsModel) it2.next();
					
					comDao.decreaseStock(detObj.getItem().getId(), detObj.getQty_in_basic_unit());
					
					getSession().createQuery(
							"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
					.setParameter("id", detObj.getItem().getId())
					.setParameter("qty", detObj.getQty_in_basic_unit())
					.executeUpdate();
					flush();
				}
				
				comDao.increaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
//				getSession().createQuery(
//						"update ItemModel set current_balalnce=0 where id=:id")
//							.setParameter("id", objModel.getItem().getId())
//							.executeUpdate();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
				.setParameter("id", objModel.getItem().getId()).setParameter("qty", objModel.getQty_in_basic_unit())
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
	
	public void delete(long production_no) throws Exception {
		try {
			begin();
			
			ProductionModel objModel;
			ProductionDetailsModel detObj;
			
			Iterator it2;
			
			Iterator<ProductionModel> it = getSession()
					.createQuery("from ProductionModel where production_no=:pn")
					.setParameter("pn", production_no).list().iterator();
			
			while (it.hasNext()) {
				
				objModel=it.next();
				
				comDao.decreaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", objModel.getItem().getId()).setParameter("qty", objModel.getQty_in_basic_unit())
							.executeUpdate();
				
				it2=objModel.getDetails_list().iterator();
				while (it2.hasNext()) {
					
					detObj =  (ProductionDetailsModel) it2.next();
					
					comDao.increaseStock(detObj.getItem().getId(), detObj.getQty_in_basic_unit());
					
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", detObj.getItem().getId()).setParameter("qty", detObj.getQty_in_basic_unit())
								.executeUpdate();
					flush();
				
				}
				
				getSession().delete(objModel);
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


	public void update(List list, long production_no) throws Exception {
		try {
			
			begin();
			
			
//			Deleting Old
			
			ProductionModel objModel;
			ProductionDetailsModel detObj;
			
			Iterator it2;
			
			Iterator<ProductionModel> it = getSession()
					.createQuery("from ProductionModel where production_no=:pn")
					.setParameter("pn", production_no).list().iterator();
			
			while (it.hasNext()) {
				
				objModel=it.next();
				
				comDao.decreaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", objModel.getItem().getId()).setParameter("qty", objModel.getQty_in_basic_unit())
							.executeUpdate();
				
				it2=objModel.getDetails_list().iterator();
				while (it2.hasNext()) {
					
					detObj =  (ProductionDetailsModel) it2.next();
					
					comDao.increaseStock(detObj.getItem().getId(), detObj.getQty_in_basic_unit());
					
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", detObj.getItem().getId()).setParameter("qty", detObj.getQty_in_basic_unit())
								.executeUpdate();
					flush();
				
				}
				
				getSession().delete(objModel);
				flush();
			}
			
			
			
			flush();
			
			
//			Saving New
			
			
			objModel=null;
			detObj=null;
			it2=null;
			it = list.iterator();
			while (it.hasNext()) {
				objModel=(ProductionModel) it.next();
				getSession().save(objModel);
				
				it2=objModel.getDetails_list().iterator();
				while (it2.hasNext()) {
					detObj =  (ProductionDetailsModel) it2.next();
					
					comDao.decreaseStock(detObj.getItem().getId(), detObj.getQty_in_basic_unit());
					
					getSession().createQuery(
							"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
					.setParameter("id", detObj.getItem().getId())
					.setParameter("qty", detObj.getQty_in_basic_unit())
					.executeUpdate();
					flush();
				}
				
				comDao.increaseStock(objModel.getItem().getId(), objModel.getQty_in_basic_unit());
				
//				getSession().createQuery(
//						"update ItemModel set current_balalnce=0 where id=:id")
//							.setParameter("id", objModel.getItem().getId())
//							.executeUpdate();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
				.setParameter("id", objModel.getItem().getId()).setParameter("qty", objModel.getQty_in_basic_unit())
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


	public List getAllProductionNumbers(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select distinct production_no from ProductionModel where office.id=:ofc order by id desc")
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
	
	public List getProductionDetails(long production_no)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("from ProductionModel where production_no=:pn")
					.setParameter("pn", production_no).list();
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
	
	
	

	/*
	 * public List getItemStocks() throws Exception { try { begin(); resultList
	 * = getSession().createQuery(
	 * "select new com.inventory.purchase.bean.InventoryDetailsPojo(" +
	 * " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where quantity>0"
	 * ) .list(); commit(); } catch (Exception e) { rollback(); close(); // TODO
	 * Auto-generated catch block e.printStackTrace(); throw e; } finally {
	 * flush(); close(); return resultList; } }
	 */

	/*
	 * public List getItemStockList(long office_id) throws Exception { try {
	 * begin(); resultList = getSession().createQuery(
	 * "select new com.inventory.purchase.bean.InventoryDetailsPojo(" +
	 * " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where quantity>0 "
	 * + " and item.office.id=:ofc").setLong("ofc", office_id) .list();
	 * commit(); } catch (Exception e) { rollback(); close(); // TODO
	 * Auto-generated catch block e.printStackTrace(); throw e; } finally {
	 * flush(); close(); return resultList; } }
	 */

	public ItemStockModel getItemStocks(long id) throws Exception {
		ItemStockModel stk = null;
		try {
			begin();
			stk = (ItemStockModel) getSession().get(ItemStockModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return stk;
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}

}
