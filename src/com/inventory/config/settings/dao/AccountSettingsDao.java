package com.inventory.config.settings.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.model.AccountSettingsModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 31, 2013
 */

public class AccountSettingsDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9138927977101793063L;
	List resultList = new ArrayList();

	
	public List getAccountSettings(long office_id) throws Exception {
		
		try {
			
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.settings.model.AccountSettingsModel(" +
					"settings_name, value) from AccountSettingsModel where office_id=:ofcid")
							.setParameter("ofcid", office_id).list();
			commit();
			
			return resultList;

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
	
 	public void saveAccountSettings(List settingsList, long office_id) throws Exception {

		try {

			begin();
			
			
			long ct=(Long) getSession().createQuery("select count(id) from AccountSettingsModel where office_id=:ofcid")
										.setParameter("ofcid", office_id).uniqueResult();
			
			if(ct!=settingsList.size()) {
			
				getSession().createQuery("delete from AccountSettingsModel where office_id=:ofcid")
								.setParameter("ofcid", office_id).executeUpdate();
				
				Iterator it=settingsList.iterator();
				while(it.hasNext()) {
					getSession().save(it.next());
				}
			}
			else {
				AccountSettingsModel obj;
				Iterator it=settingsList.iterator();
				while(it.hasNext()) {
					obj=(AccountSettingsModel) it.next();
					getSession().createQuery("update AccountSettingsModel set value=:val where settings_name=:name and office_id=:ofc")
					.setParameter("val", obj.getValue()).setParameter("ofc", office_id).setParameter("name", obj.getSettings_name()).executeUpdate();
				}
				
				
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

	public List getAllLedgersOfType(long officeID, int type) throws Exception {
		List list=null;
		try {
			
			begin();
			
			list=getSession().createQuery("from LedgerModel where office.id=:ofcid and type=:type")
							.setParameter("ofcid", officeID).setParameter("type", type).list();
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
		return list;
	}
 
 	
}
