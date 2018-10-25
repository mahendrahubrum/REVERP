package com.inventory.config.stock.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.inventory.purchase.model.ItemStockModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 8, 2014
 */
public class StockMergeDao extends SHibernate{

	public List getAllStocks(long ofc_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.ItemStockModel(id, concat(item.name,' ( ',item.item_code,' )  Bal : ', balance,' , MFG Date : ', manufacturing_date ))"
									+ " from ItemStockModel where item.office.id=:ofc and balance>0 order by item.name")
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
		}
		return resultList;
	}

	public List getAllStocksExceptSelected(long officeID, Long stockId) throws Exception {
		List resultList=null;
		try {
			begin();
			
			ItemStockModel stkMdl=(ItemStockModel) getSession().get(ItemStockModel.class, stockId);
			
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.ItemStockModel(id, concat(item.name,' ( ',item.item_code,' )  Bal : ', balance,' , MFG Date : ', manufacturing_date ))"
									+ " from ItemStockModel where item.office.id=:ofc and id!=:stk and item.id=:item and balance>0 order by manufacturing_date desc")
					.setParameter("ofc", officeID).setParameter("stk", stockId).setParameter("item", stkMdl.getItem().getId()).list();
			
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

	public void mergeStocks(Long stockId, Set selectedStocks) throws Exception {
		try {
			begin();
			
			double balance=0;
			
			balance= (Double) getSession()
					.createQuery("select sum(balance) from ItemStockModel where id in (:lst)")
					.setParameterList("lst", selectedStocks).uniqueResult();
			
			 getSession()
					.createQuery("update ItemStockModel set balance = balance+:bal where id =:id ")
					.setParameter("id", stockId).setParameter("bal", balance).executeUpdate();
			 
			 getSession()
			 		.createQuery("update ItemStockModel set balance = 0 where id in (:lst)")
			 		.setParameterList("lst", selectedStocks).executeUpdate();
			
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
	
}
