package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemTransferInventoryDetails;
import com.inventory.config.stock.model.ItemTransferModel;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class ItemTransferDao extends SHibernate implements Serializable{

	List resultList = new ArrayList();

	CommonMethodsDao comDao = new CommonMethodsDao();

	public long save(ItemTransferModel obj) throws Exception {

		try {

			begin();
			
			ItemTransferInventoryDetails invObj;
			
			Iterator<ItemTransferInventoryDetails> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				
				comDao.decreaseStockByStockID(invObj.getStk_id(),
						invObj.getQuantity_in_basic_unit());
				invObj.setStock_id("");

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
			}

			// Add Stock Here

			getSession().save(obj);
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
		return obj.getId();
	}

	public ItemTransferModel getItemTransfer(long id) throws Exception {
		ItemTransferModel st = null;
		try {
			begin();
			st = (ItemTransferModel) getSession().get(ItemTransferModel.class,
					id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return st;
		}
	}

	public void update(ItemTransferModel newobj) throws Exception {
		try {
			
			begin();
			
			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from ItemTransferModel a join a.inventory_details_list b "
							+ "where a.id=" + newobj.getId()).list();
			
			// Delete
			
			List oldLst = getSession()
					.createQuery(
							"select b from ItemTransferModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();
			
			ItemTransferInventoryDetails invObj;
			
			Iterator<ItemTransferInventoryDetails> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				comDao.increaseStockByStockID(invObj.getStk_id(),
						invObj.getQuantity_in_basic_unit());
				
				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				flush();
				
			}
			
			
			
			// Save
			
			Iterator<ItemTransferInventoryDetails> it1 = newobj
					.getInventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();
				
				comDao.decreaseStockByStockID(invObj.getStk_id(),
						invObj.getQuantity_in_basic_unit());
				invObj.setStock_id("");
				
				flush();
				
				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
			}
			
			getSession().update(newobj);
			flush();
			
			getSession()
					.createQuery(
							"delete from ItemTransferInventoryDetails where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();
			
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

	public void delete(long id) throws Exception {
		try {
			begin();

			ItemTransferModel obj = (ItemTransferModel) getSession().get(
					ItemTransferModel.class, id);
			
			ItemTransferInventoryDetails invObj;

			Iterator<ItemTransferInventoryDetails> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				// For Stock Update
				// getSession().createQuery("update ItemStockModel set balance=balance+:qty where id=:id")
				// .setLong("id", invObj.getStock_id()).setDouble("qty",
				// invObj.getQunatity()).executeUpdate();

				comDao.increaseStockByStockID(invObj.getStk_id(),
						invObj.getQuantity_in_basic_unit());

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
			}

			getSession().delete(obj);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}

	public List getAllStockTransferNumbersAsComment(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemTransferModel(id,cast(transfer_no as string) )"
									+ " from ItemTransferModel where office.id=:ofc")
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
