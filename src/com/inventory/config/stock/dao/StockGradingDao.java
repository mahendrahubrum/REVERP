package com.inventory.config.stock.dao;

import java.util.Iterator;
import java.util.List;

import com.inventory.purchase.model.ItemStockModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jun 4, 2014
 */
public class StockGradingDao extends SHibernate{

	public ItemStockModel getStock(Long stockId) throws Exception {
		ItemStockModel stkMdl=null;
		try {
			begin();
			stkMdl=(ItemStockModel) getSession().get(ItemStockModel.class, stockId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
		return stkMdl;
	}

	public List getItemStockList(long office_id,long itemId) throws Exception {
		List resultList;
		String condition="";
		if(itemId!=0){
			condition+=" and a.item.id="+itemId;
		}
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(" +
					" a.id, concat(a.item.name,' Bal: ' , a.balance,' ', a.item.unit.symbol)) " +
					"from ItemStockModel a, GradeModel b where a.balance>0 " +
					" and a.item.office.id=:ofc and a.gradeId in (b.id,0) "+condition+ " order by a.item.name").setLong("ofc", office_id)
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

	public void save(List stockList, ItemStockModel oldStk) throws Exception {
		try {
			begin();
			ItemStockModel newMdl;
			Iterator iter=stockList.iterator();
			while (iter.hasNext()) {
				newMdl = (ItemStockModel) iter.next();
				getSession().save(newMdl);
			}
			
			getSession().update(oldStk);
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
