package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.webspark.dao.SHibernate;

public class ItemDetailsReportDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2329602428602502238L;

	public List<Object> getItemDetails(long itemId, Date fromDate, Date toDate)
			throws Exception {
		
		List<Object>list=null;
		try {
			begin();
			list=getSession().createQuery("Select id,item_code,name from ItemModel").list();
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return null;
	}

}
