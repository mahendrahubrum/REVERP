package com.inventory.reports.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.purchase.model.ItemStockModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 20, 2013
 */
public class StockReportDao extends SHibernate implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2941320768768622710L;

	public List<Object> getRackDetails(long itemId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		List<Object> stockList = null;
		List<Long> stockIdList = new ArrayList<Long>();;
		try {
			begin();
			stockList = getSession()
					.createQuery(
							"from ItemStockModel where item.id=:ItmId")
					.setParameter("ItmId", itemId).list();

			if (stockList.size() > 0) {
				ItemStockModel model;
				for (int i = 0; i < stockList.size(); i++) {
					model=(ItemStockModel)stockList.get(i);
					stockIdList.add(model.getId());
				}
				resultList = getSession()
						.createQuery(
								"from StockRackMappingModel where stock.id in(:stockList)")
						.setParameterList("stockList", stockIdList).list();
			}
			commit();
		} catch (Exception e) {
			resultList = new ArrayList<Object>();
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

}
