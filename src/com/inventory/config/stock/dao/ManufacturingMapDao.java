package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.purchase.model.ItemStockModel;
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
public class ManufacturingMapDao extends SHibernate implements Serializable{
	

	private static final long serialVersionUID = -1278477782011592891L;

	List resultList = new ArrayList();

	CommonMethodsDao comDao = new CommonMethodsDao();

	public void save(List list,long itemId) throws Exception {
		try {
			
			begin();
			
			getSession()
			.createQuery(
					"Delete from ManufacturingMapModel where item.id=:id")
					.setParameter("id", itemId).executeUpdate();
			
			ManufacturingMapModel objModel;
			
			Iterator it = list.iterator();
			while (it.hasNext()) {
				objModel=(ManufacturingMapModel) it.next();
				getSession().save(objModel);
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
	
	public void delete(long itemId) throws Exception {
		try {
			begin();

			getSession()
					.createQuery(
							"Delete from ManufacturingMapModel where item.id=:id")
					.setParameter("id", itemId).executeUpdate();
			flush();

			flush();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}

	public List getProductionDetails(long itemId)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("from ManufacturingMapModel where item.id=:id")
					.setParameter("id", itemId).list();
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
