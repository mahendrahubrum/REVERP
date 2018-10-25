package com.inventory.reports.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author sangeeth
 *
 */
@SuppressWarnings("serial")
public class ItemTreeViewDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public List getAllActiveItems(long officeId, long itemId, long itemSubgroupId, long groupId, long org_id) throws Exception {

		List resultList = new ArrayList();
		
		String condition = "";
		if (itemId != 0) {
			condition += " and id=" + itemId;
		}
		if (itemSubgroupId != 0) {
			condition += " and sub_group.id=" + itemSubgroupId;
		}
		if (groupId != 0) {
			condition += " and sub_group.group.id=" + groupId;
		}
		if (officeId != 0) {
			condition += " and office.id=" + officeId;
		}
		else
			condition += " and office.organization.id=" + org_id;
		
		try {
			begin();
			resultList = getSession().createQuery("from ItemModel  where status=:sts" + condition+" order by sub_group.group.name")
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE).list();
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
