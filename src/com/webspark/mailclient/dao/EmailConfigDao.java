package com.webspark.mailclient.dao;

import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.mailclient.model.EmailConfigurationModel;

/**
 * @author Jinshad
 * 
 * @Date 11 Mar 2014
 * 
 */

public class EmailConfigDao extends SHibernate {

	public long save(EmailConfigurationModel mdl) throws Exception {
		try {
			begin();
			
			if(mdl.getId()==0)
				getSession().save(mdl);
			else
				getSession().update(mdl);
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl.getId();
	}
	
	
	public long saveAndDeleteAllMails(EmailConfigurationModel mdl) throws Exception {
		try {
			begin();
			
			if(mdl.getId()==0)
				getSession().save(mdl);
			else
				getSession().update(mdl);
			
			 getSession().createQuery("delete from MyMailsModel where user_id=:usr").setLong("usr", mdl.getUser_id()).executeUpdate();
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl.getId();
	}
	
	
	public EmailConfigurationModel getEmailConfiguration(long user_id) throws Exception {
		EmailConfigurationModel mdl=null;
		try {
			
			begin();
			Object obj= getSession().createQuery("from EmailConfigurationModel where user_id=:usr")
								.setLong("usr", user_id).uniqueResult();
			commit();
			
			if(obj!=null)
				mdl=(EmailConfigurationModel) obj;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl;
	}
	
	
	public List getEmailConfigurations(long user_id) throws Exception {
		List lst=null;
		try {
			
			begin();
			lst= getSession().createQuery("from EmailConfigurationModel where user_id=:usr")
								.setLong("usr", user_id).list();
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return lst;
	}
	
	
	
	
	
	

}
