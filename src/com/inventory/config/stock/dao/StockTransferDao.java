package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.transform.ToListResultTransformer;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.StockTransferInventoryDetails;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class StockTransferDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5189208738526903448L;
	List resultList = new ArrayList();
	ItemDao itemDao = new ItemDao();

	public long save(StockTransferModel obj) throws Exception {

		try {
			begin();
			
	//		getSession().clear();
			getSession().save(obj);
			flush();
			StockTransferInventoryDetails invObj;

			Iterator<StockTransferInventoryDetails> it = obj
					.getInventory_details_list().iterator();
			
			ItemModel item = null;
			while (it.hasNext()) {
				invObj = it.next();

				item = getItemInAnotherOfficeTemp(obj.getTo_office().getId(),
						invObj.getStock_id().getItem().getId());
				
				if (item == null) {
					System.out.println("====== NULL ==========");
				} else {
					System.out.println("====== NOT NULL =========="
							+ item.getId());
				}
				
				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance-:qty where id=:id")
						.setLong("id", invObj.getStock_id().getId())
						.setDouble("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id",
								invObj.getStock_id().getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce + :qty where id=:id")
						.setLong("id", item.getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				ItemStockModel itemStockModel = new ItemStockModel();
				itemStockModel.setPurchase_id(obj.getId());
				itemStockModel.setItem(item);
				itemStockModel.setQuantity(invObj.getQuantity_in_basic_unit());
				itemStockModel.setRate(invObj.getStock_id().getRate());
				itemStockModel.setDate_time(CommonUtil.getCurrentDateTime());
				itemStockModel.setManufacturing_date(invObj.getStock_id()
						.getManufacturing_date());
				itemStockModel.setExpiry_date(invObj.getStock_id()
						.getExpiry_date());
				itemStockModel
						.setStatus(SConstants.stock_statuses.TRANSFERRED_STOCK);
				itemStockModel.setBalance(itemStockModel.getQuantity());
				itemStockModel.setBarcode(invObj.getStock_id().getBarcode());
				itemStockModel.setItem_tag(invObj.getStock_id().getItem_tag());
				itemStockModel.setInv_det_id(invObj.getId());

				itemStockModel
						.setPurchase_type(SConstants.stockPurchaseType.STOCK_TRANSFER);
				if(obj.getTo_location()!=null)
					itemStockModel.setLocation_id(obj.getTo_location().getId());
				else
					itemStockModel.setLocation_id(0);
				itemStockModel.setBatch_id(invObj.getStock_id().getBatch_id());
			//	itemStockModel.setRate(rate)

				// item
				getSession().clear();
				getSession().save(itemStockModel);

			}
			
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
			return obj.getId();
		}
	}


	private ItemModel getItemInAnotherOfficeTemp(long new_office_id, long item_id) throws Exception {

		ItemModel obj1 = null;
		
			ItemModel itemModel = (ItemModel)getSession().get(ItemModel.class, item_id);

	

			obj1 = (ItemModel) getSession()
					.createQuery(
							"FROM ItemModel where office.id =:office_id"
									+ " AND parentId = :parent_id"
									+ " AND sub_group.id = :sub_group_id")
					.setParameter("office_id", new_office_id)
					.setParameter("parent_id", itemModel.getParentId())
					.setParameter("sub_group_id",
							itemModel.getSub_group().getId()).uniqueResult();

		

		return obj1;
	
	}


	public ItemModel getItemInAnotherOffice(long new_office_id, long item_id)
			throws Exception {
		ItemModel obj = null;
		try {
			ItemModel itemModel = itemDao.getItem(item_id);

			begin();

			obj = (ItemModel) getSession()
					.createQuery(
							"FROM ItemModel where office.id =:office_id"
									+ " AND parentId = :parent_id"
									+ " AND sub_group.id = :sub_group_id")
					.setParameter("office_id", new_office_id)
					.setParameter("parent_id", itemModel.getParentId())
					.setParameter("sub_group_id",
							itemModel.getSub_group().getId()).uniqueResult();

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		} finally {
			flush();
			close();

		}
		return obj;
	}

	public StockTransferModel getStockTransfer(long id) throws Exception {
		StockTransferModel st = null;
		try {
			begin();
			st = (StockTransferModel) getSession().get(
					StockTransferModel.class, id);
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
			return st;
		}
	}

	public void update(StockTransferModel newobj) throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from StockTransferModel a join a.inventory_details_list b "
							+ "where a.id=" + newobj.getId()).list();

			// Delete

			List oldLst = getSession()
					.createQuery(
							"select b from StockTransferModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			StockTransferInventoryDetails invObj;

			Iterator<StockTransferInventoryDetails> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();

				// For Stock Update
				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance+:qty where id=:id")
						.setLong("id", invObj.getStock_id().getId())
						.setDouble("qty", invObj.getQuantity_in_basic_unit()).executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getStock_id().getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				getSession()
				.createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty " +
						" where id=(SELECT item.id FROM ItemStockModel WHERE inv_det_id=:inv_det_id AND purchase_type=:purchase_type)")
				.setLong("inv_det_id", invObj.getStock_id().getId())
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER)
				.setParameter("qty", invObj.getQuantity_in_basic_unit())
				.executeUpdate();
				
				getSession()
				.createQuery(
						"DELETE FROM ItemStockModel WHERE inv_det_id=:inv_det_id AND purchase_type=:purchase_type")
				.setLong("inv_det_id", invObj.getStock_id().getId())
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER).executeUpdate();

			}

			
/*
			// Save

			Iterator<StockTransferInventoryDetails> it1 = newobj
					.getInventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance-:qty where id=:id")
						.setLong("id", invObj.getStock_id())
						.setDouble("qty", invObj.getQunatity()).executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
			}
*/
			saveDetails(newobj);
			//flush();

		/*	getSession()
			.createQuery(
					"delete from com.inventory.config.stock.model.StockTransferModel.inventory_details_list where id=:id")
			.setParameter("id", newobj.getId())
			.executeUpdate();
			
			getSession()
					.createQuery(
							"delete from StockTransferInventoryDetails where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();*/
			
			flush();
			if(oldLst.size()>0){
				getSession()
				.createQuery(
						"delete from StockTransferInventoryDetails where id in (:lst)")
				.setParameterList("lst", (Collection) old_notDeletedLst)
				.executeUpdate();
			}

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
		}
	}

	private void saveDetails(StockTransferModel obj) throws Exception {


	/*	try {*/

			StockTransferInventoryDetails invObj;

			Iterator<StockTransferInventoryDetails> it = obj.getInventory_details_list().iterator();
			ItemModel item = null;
			while (it.hasNext()) {
				invObj = it.next();

				item = getItemInAnotherOfficeTemp(obj.getTo_office().getId(),
						invObj.getStock_id().getItem().getId());
				if (item == null) {
					System.out.println("====== NULL ==========");
				} else {
					System.out.println("====== NOT NULL =========="
							+ item.getId());
				}
				
				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance-:qty where id=:id")
						.setLong("id", invObj.getStock_id().getId())
						.setDouble("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id",
								invObj.getStock_id().getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce + :qty where id=:id")
						.setLong("id", item.getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				ItemStockModel itemStockModel = new ItemStockModel();
				itemStockModel.setPurchase_id(obj.getId());
				itemStockModel.setItem(item);
				itemStockModel.setQuantity(invObj.getQuantity_in_basic_unit());
				itemStockModel.setRate(item.getRate());
				itemStockModel.setDate_time(CommonUtil.getCurrentDateTime());
				itemStockModel.setManufacturing_date(invObj.getStock_id()
						.getManufacturing_date());
				itemStockModel.setExpiry_date(invObj.getStock_id()
						.getExpiry_date());
				itemStockModel
						.setStatus(SConstants.stock_statuses.TRANSFERRED_STOCK);
				itemStockModel.setBalance(itemStockModel.getQuantity());
				itemStockModel.setBarcode(invObj.getStock_id().getBarcode());
				itemStockModel.setItem_tag(invObj.getStock_id().getItem_tag());
				itemStockModel.setInv_det_id(invObj.getId());

				itemStockModel
						.setPurchase_type(SConstants.stockPurchaseType.STOCK_TRANSFER);
				if(obj.getTo_location()!=null)
					itemStockModel.setLocation_id(obj.getTo_location().getId());
				else
					itemStockModel.setLocation_id(0);
				itemStockModel.setBatch_id(invObj.getStock_id().getBatch_id());

				// item
				getSession().save(itemStockModel);

			}
			getSession().clear();
			getSession().update(obj);
		//	commit();

		/*} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			//return obj.getId();
		}*/
	
		
	}

	public void delete(long id) throws Exception {
		try {
			begin();

			StockTransferModel obj = (StockTransferModel) getSession().get(
					StockTransferModel.class, id);

			StockTransferInventoryDetails invObj;
			Iterator<StockTransferInventoryDetails> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				

	//			invObj = it.next();

				// For Stock Update
				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance+:qty where id=:id")
						.setLong("id", invObj.getStock_id().getId())
						.setDouble("qty", invObj.getQuantity_in_basic_unit()).executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getStock_id().getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				getSession()
				.createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty " +
						" where id=(SELECT item.id FROM ItemStockModel WHERE inv_det_id=:inv_det_id AND purchase_type=:purchase_type)")
				.setLong("inv_det_id", invObj.getId())
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER)
				.setParameter("qty", invObj.getQuantity_in_basic_unit())
				.executeUpdate();
				
				getSession()
				.createQuery(
						"DELETE FROM ItemStockModel WHERE inv_det_id=:inv_det_id AND purchase_type=:purchase_type")
				.setLong("inv_det_id", invObj.getId())
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER).executeUpdate();

			
			}

			getSession().delete(obj);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		}
		flush();
		close();
	}

	public List<StockTransferModel> getAllStockTransferNumbersAsComment(
			long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.StockTransferModel(id,cast(transfer_no as string) )"
									+ " from StockTransferModel where from_office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
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

	/*
	 * public List getItemStocks() throws Exception { try { begin(); resultList
	 * = getSession().createQuery(
	 * "select new com.inventory.purchase.bean.InventoryDetailsPojo(" +
	 * " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where quantity>0"
	 * ) .list(); commit(); } catch (Exception e) { rollback(); close(); // TODO
	 * Auto-generated catch block e.printStackTrace(); throw e; } finally {
	 * flush(); close(); return resultList; } }
	 */

	public List getItemStockList(long office_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where quantity>0 "
									+ " and item.office.id=:ofc")
					.setLong("ofc", office_id).list();
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

	public List getItemStockListByLocationWIse(long locationId)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' " +
									", expiry_date )) from ItemStockModel where quantity>0 "
									+ " and location_id=:location_id")
					.setLong("location_id", locationId).list();
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

	public List getItemStockListByLocationAndItemWIse(long location_id,
			long item_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : '" +
									" , expiry_date )) from ItemStockModel where quantity>0 "
									+ " and location_id=:location_id"
									+ " and item.id = :item_id")
					.setLong("location_id", location_id)
					.setLong("item_id", item_id).list();
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
