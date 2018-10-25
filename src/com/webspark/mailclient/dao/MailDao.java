package com.webspark.mailclient.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.mailclient.model.MyMailsModel;

/**
 * @author Jinshad
 * 
 * @Date 11 Mar 2014
 * 
 */

public class MailDao extends SHibernate {
	
	public void save(List mailList) throws Exception {
		try {
			begin();
			
			Iterator it=mailList.iterator();
			while (it.hasNext()) {
				getSession().save(it.next());
				flush();
			}
			
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
	
	
	public void deleteMails(Collection selectedItems) throws Exception {
		try {
			begin();
			
			Iterator it=selectedItems.iterator();
			while (it.hasNext()) {
				getSession().delete(new MyMailsModel((Long) it.next()));
				flush();
			}
			
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
	
	
	
	
	
	
	public List getEmails(long user_id, long config_id, long folder_id) throws Exception {
		List lst=null;
		try {
			
			begin();
			lst= getSession().createQuery("from MyMailsModel where user_id=:usr " +
					"and config_id=:cnfid and folder_id=:fldid order by mail_number desc").setLong("cnfid", config_id)
					.setLong("fldid", folder_id).setLong("usr", user_id).list();
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
	
	
	public List getAllEmails(long user_id, String email) throws Exception {
		List lst=null;
		try {
			
			begin();
			lst= getSession().createQuery("from MyMailsModel where user_id=:usr " +
					"and emails like :email order by date_time desc").setString("email", "%"+email+"%").
					setLong("usr", user_id).list();
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
	
	public List getAllEmailsByFolder(long user_id, String email, long folder_id) throws Exception {
		List lst=null;
		try {
			
			begin();
			lst= getSession().createQuery("from MyMailsModel where user_id=:usr " +
					"and emails like :email and folder_id=:fld order by date_time desc")
					.setString("email", "%"+email+"%").setLong("fld", folder_id).
					setLong("usr", user_id).list();
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
	
	
	public long getLastMailNumber(long user_id, long config_id, long folder_id) throws Exception {
		long mail_no=0;
		try {
			
			begin();
			Object obj= getSession().createQuery("select max(mail_number) from MyMailsModel where user_id=:usr " +
					"and config_id=:cnfid and folder_id=:fldid").setLong("cnfid", config_id)
					.setLong("fldid", folder_id).setLong("usr", user_id).uniqueResult();
			commit();
			
			if(obj!=null)
				mail_no=(Long)obj;
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mail_no;
		}
	}
	
	
	
	public boolean isMailExist(long user_id, long mail_no, long config_id, long folder_id) throws Exception {
		boolean avail=false;
		try {
			
			begin();
			Object obj= getSession().createQuery("select count(id) from MyMailsModel where user_id=:usr and mail_number=:mno " +
					"and config_id=:cnfid and folder_id=:fldid").setLong("mno", mail_no).setLong("cnfid", config_id)
					.setLong("fldid", folder_id).setLong("usr", user_id).uniqueResult();
			commit();
			
			if(obj!=null) {
				if((Long)obj>0)
					avail=true;
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return avail;
		}
	}
	
	
	public List getEmailReport(long user_id, String email, long folder_id, Timestamp frDt, Timestamp toDt) throws Exception {
		List lst=null;
		try {
			String criteria="";
			if(folder_id!=0)
				criteria+=" and folder_id="+folder_id;
			
			begin();
			
			if(!email.equals("ALL"))
				lst= getSession().createQuery("from MyMailsModel where user_id=:usr and date_time between :stdt and :enddt " +criteria+
						" and emails like :email order by date_time desc").setString("email", "%"+email+"%")
						.setParameter("stdt", frDt).setParameter("enddt", toDt)
						.setLong("usr", user_id).list();
			else
				lst= getSession().createQuery("from MyMailsModel where user_id=:usr and date_time between :stdt and :enddt " +criteria+
						" order by date_time desc").setParameter("stdt", frDt).setParameter("enddt", toDt)
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
	
	
	
	public void markAsReaded(long id) throws Exception {
		long mail_no=0;
		try {
			
			begin();
			
			Object obj= getSession().createQuery("update MyMailsModel set unreaded=false where id=:id").setLong("id", id)
							.executeUpdate();
			
			commit();
			
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	

}
