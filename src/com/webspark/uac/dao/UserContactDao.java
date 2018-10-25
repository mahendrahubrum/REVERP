package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserContactModel;
import com.webspark.uac.model.UserFamilyContactModel;

@SuppressWarnings("serial")
public class UserContactDao extends SHibernate implements Serializable{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(List list, List familyList, long user, long office) throws Exception{
		try {
			begin();
			List cntctLst=new ArrayList();
			List fmlyLst=new ArrayList();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				UserContactModel mdl = (UserContactModel) itr.next();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
				cntctLst.add(mdl.getId());
			}
			
			itr=familyList.iterator();
			while (itr.hasNext()) {
				UserFamilyContactModel mdl = (UserFamilyContactModel) itr.next();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
				fmlyLst.add(mdl.getId());
			}
			
			if(cntctLst!=null && cntctLst.size()>0){
				getSession().createQuery("delete from UserContactModel where user.id=:user and officeId=:office and id not in (:list)")
					.setParameter("user", user).setParameter("office", office).setParameterList("list", (Collection)cntctLst).executeUpdate();
				flush();
			}
			if(fmlyLst!=null && fmlyLst.size()>0){
				getSession().createQuery("delete from UserFamilyContactModel where user.id=:user and officeId=:office and id not in (:list)")
					.setParameter("user", user).setParameter("office", office).setParameterList("list", (Collection)fmlyLst).executeUpdate();
				flush();
			}
			commit();
		}
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		finally {
			flush();
			close();
		}
	}
	
	public UserContactModel getUserContactModel(long id) throws Exception {
		UserContactModel qualification = null;
		try {
			begin();
			qualification = (UserContactModel) getSession().get(UserContactModel.class, id);
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
		return qualification;
	}
	
	public UserFamilyContactModel getUserFamilyContactModel(long id) throws Exception {
		UserFamilyContactModel qualification = null;
		try {
			begin();
			qualification = (UserFamilyContactModel) getSession().get(UserFamilyContactModel.class, id);
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
		return qualification;
	}
	
	@SuppressWarnings("rawtypes")
	public List getUserFamilyContactModelList(long user, long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("from UserFamilyContactModel where user.id=:user and officeId=:office").setParameter("user", user).setParameter("office", office).list();
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
	
	@SuppressWarnings("rawtypes")
	public List getUserContactModelList(long user, long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("from UserContactModel where user.id=:user and officeId=:office")
					.setParameter("user", user).setParameter("office", office).list();
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
