package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author anil
 * @date 20-Nov-2015
 * @Project REVERP
 */
public class ItemExpiryReportDao extends SHibernate implements  Serializable{

	private static final long serialVersionUID = -2657260698954876738L;

	public List getAllExpiredItems(long officeId, long itemId,
			long itemSubgroupId, long groupId, long locationId, long org_id,Date date) throws Exception {
		
		List resultList=null;

		String condition = "";
		if (itemId != 0) {
			condition += " and item.id=" + itemId;
		}
		if (itemSubgroupId != 0) {
			condition += " and item.sub_group.id=" + itemSubgroupId;
		}
		if (groupId != 0) {
			condition += " and item.sub_group.group.id=" + groupId;
		}
		if (locationId != 0) {
			condition += " and location_id=" + locationId;
		}
		if (officeId != 0) {
			condition += " and item.office.id=" + officeId;
		}
		else
			condition += " and item.office.organization.id=" + org_id;
		
		
		try {
			begin();
			resultList = getSession().createQuery("from ItemStockModel  where expiry_date<:date and balance>0 and purchase_id!=0" + condition+" order by id desc")
					.setParameter("date",date).list();
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

}
