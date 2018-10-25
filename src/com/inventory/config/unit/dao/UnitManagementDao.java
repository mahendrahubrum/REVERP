package com.inventory.config.unit.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class UnitManagementDao extends SHibernate implements Serializable{

	List resultList = new ArrayList();


	public void save(List list, long item_id) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from ItemUnitMangementModel"
							+ " where item.id=:itm").setLong("itm", item_id).executeUpdate();
			
			Iterator it=list.iterator();
			while(it.hasNext()) {
				getSession().save(it.next());
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	
	public List getAllItemUnitDetails(long item_id) throws Exception {
		
		try {
			
			begin();
			resultList = getSession()
					.createQuery(
							"from ItemUnitMangementModel"
									+ " where item.id=:itm").setLong("itm", item_id).list();
			commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	

}