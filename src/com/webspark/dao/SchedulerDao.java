package com.webspark.dao;

import java.util.List;

import com.webspark.common.util.SConstants;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 13, 2015
 */
public class SchedulerDao extends SHibernate{

	public List getAllAlertEnabledOfficeSettings(long orgId) throws Exception{
		List list;
		try {
			begin();
			list=getSession().createQuery("from S_OfficeModel where organization.id=:org").setParameter("org", orgId).list();
			
//			list=getSession().createQuery("from SettingsModel where level=:lev and level_id in (:list)")
//					.setParameterList("list",officeList).setParameter("lev",SConstants.scopes.OFFICE_LEVEL_GENERAL).list();
			
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
		return list;
	}

	public List getAllAlertEnabledOrganizations() throws Exception {
		List list;
		try {
			begin();
			list=getSession().createQuery("from SettingsModel where settings_name=:sett and value!=null and value!='' and level=:lev")
					.setParameter("sett", SConstants.settings.APPLICATION_EMAIL).setParameter("lev",SConstants.scopes.SYSTEM_LEVEL).list();
			
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
		return list;
	}
	
	
	public String getOfficeName(long id) throws Exception {
		String name="";
		try {
			begin();
			Object obj= getSession().createQuery("select name from S_OfficeModel where id=:id")
					.setLong("id", id).uniqueResult();
			if(obj!=null)
				name=(String) obj;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return name;
		}
	}
}
