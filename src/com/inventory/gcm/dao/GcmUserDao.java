package com.inventory.gcm.dao;

import java.io.Serializable;
import java.util.List;

import com.inventory.gcm.model.GcmUserModel;
import com.webspark.dao.SHibernate;

public class GcmUserDao extends SHibernate implements Serializable{

	/**
	 * @author Jinshad P.T.
	 * 
	 *         Dec 29, 2014
	 */
	private static final long serialVersionUID = -8101243619593185989L;

	public boolean save(GcmUserModel obj) throws Exception {
		boolean status=false;
		try {
			
			begin();
			
			Object tmp=getSession().createQuery("select id from GcmUserModel where deviceId=:dev")
					.setString("dev", obj.getDeviceId()).uniqueResult();
			
			if(tmp!=null) {
				obj.setId((Long) tmp);
				getSession().update(obj);
			}
			else 
				getSession().save(obj);
			
			commit();
			
			status=true;

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return status;
	}
	
	public List getAllUsers() throws Exception {
		List lst=null;
		try {
			
			begin();
			
			lst=getSession().createQuery("select reg_id from GcmUserModel").list();
			
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return lst;
	}
	
}
