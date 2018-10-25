package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.TransferStockMap;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 23, 2013
 */

public class StockReceiveDao extends SHibernate implements Serializable {

	List resultList = new ArrayList();

	public void receiveStock(List stockList, long transfer_id) throws Exception {

		try {
			
			begin();
			
			ItemStockModel stk;
			
			Iterator it=stockList.iterator();
			while(it.hasNext()){
				stk=(ItemStockModel) it.next();
				getSession().save(stk);
				
				getSession().save(new TransferStockMap(transfer_id, stk));
				
				getSession()
				.createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
				.setParameter("id", stk.getItem().getId())
				.setParameter("qty", stk.getQuantity()).executeUpdate();

				
			}
			
			
			getSession().createQuery("update StockTransferModel set status=2 where id=:id")
								.setLong("id", transfer_id).executeUpdate();
			
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
	
	public List getStockTransfer(long id, long office_id) throws Exception {
		try {
			begin();
			resultList =getSession().createQuery("select b from StockTransferModel a join a.inventory_details_list b where a.id=:id and b.to_office_id=:ofcid")
							.setLong("id", id).setLong("ofcid", office_id).list();
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
	
	
	public List getAllStockTransferNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.stock.model.StockTransferModel(a.id,cast(a.transfer_no as string) )" +
					" from StockTransferModel a where a.from_office.id=:ofc group by a.id").setParameter("ofc", ofc_id).list();
			// join a.inventory_details_list b
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
		ItemStockModel stk=null;
		try {
			begin();
			stk =  (ItemStockModel) getSession().get(ItemStockModel.class, id);
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
	
	public long getStatus(long id) throws Exception {
		long status=0;
		try {
			begin();
			Object sts =   getSession().createQuery("select status from StockTransferModel where id=:id")
								.setLong("id", id).uniqueResult();
			commit();
			if(sts!=null)
				status=(Long) sts;
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
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
			resultList=getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(stock.id, " +
					"concat(stock.id,' : ', stock.item.name, ' : Qty : ', stock.quantity, ' : ', stock.expiry_date) ) from TransferStockMap where trnasfer_id=:tid")
				.setLong("tid", transfer_id).list();
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
