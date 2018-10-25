package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.ItemReceiveModel;
import com.inventory.config.stock.model.TransferStockMap;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 23, 2013
 */

public class ItemReceiveDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 123191947508333572L;
	List resultList = new ArrayList();

	public void receiveStock(List stockList, List receiveList, long transfer_id) throws Exception {

		try {

			begin();
			
			ItemStockModel stk;
			Iterator it = stockList.iterator();
			while (it.hasNext()) {
				stk = (ItemStockModel) it.next();
				getSession().save(stk);
				stk.setBarcode(stk.getId()+"");

				getSession().save(new TransferStockMap(transfer_id, stk));

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", stk.getItem().getId())
						.setParameter("qty", stk.getQuantity()).executeUpdate();

			}
			
			ItemReceiveModel recMolde;
			Iterator recItr = receiveList.iterator();
			while (recItr.hasNext()) {
				recMolde = (ItemReceiveModel) recItr.next();
				getSession().save(recMolde);
			}

			getSession()
					.createQuery(
							"update ItemTransferModel set status=2 where id=:id")
					.setLong("id", transfer_id).executeUpdate();

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

	public List getStockTransfer(long id, long office_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select b from ItemTransferModel a join a.inventory_details_list b where a.id=:id and b.to_office_id=:ofcid")
					.setLong("id", id).setLong("ofcid", office_id).list();
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

	public List getAllStockTransferNumbersAsComment(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemTransferModel(a.id,cast(a.transfer_no as string) )"
									+ " from ItemTransferModel a join a.inventory_details_list b where b.to_office_id=:ofc group by a.id")
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
			return stk;
		}
	}

	public long getStatus(long id) throws Exception {
		long status = 0;
		try {
			begin();
			Object sts = getSession()
					.createQuery(
							"select status from ItemTransferModel where id=:id")
					.setLong("id", id).uniqueResult();
			commit();
			if (sts != null)
				status = (Long) sts;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return status;
		}
	}

	public List getStockListOfTransfer(long transfer_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.ItemStockModel(stock.id, "
									+ "concat(stock.id,' : ', stock.item.name, ' : Qty : ', stock.quantity, ' : ', stock.expiry_date) ) from TransferStockMap where trnasfer_id=:tid")
					.setLong("tid", transfer_id).list();
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

	public ItemReceiveModel getReceiveModel(long foreignItem, Long itemTransId) throws Exception {
		ItemReceiveModel recMdl=null;
		try {
			begin();
			recMdl = (ItemReceiveModel) getSession()
					.createQuery(
							"from ItemReceiveModel where transferId=:tid and foreignItemId=:item")
					.setLong("tid", itemTransId).setLong("item", foreignItem).uniqueResult();
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
		return recMdl;
	}
}
