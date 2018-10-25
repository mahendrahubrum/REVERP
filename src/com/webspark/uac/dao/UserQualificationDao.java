package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserQualificationModel;

@SuppressWarnings("serial")
public class UserQualificationDao extends SHibernate implements Serializable{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(List list, long user, long office) throws Exception{
		try {
			begin();
			List cntctLst=new ArrayList();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				UserQualificationModel mdl = (UserQualificationModel) itr.next();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
				cntctLst.add(mdl.getId());
			}
			if(cntctLst!=null && cntctLst.size()>0){
				getSession().createQuery("delete from UserQualificationModel where user.id=:user and office.id=:office and id not in (:list)")
					.setParameter("user", user).setParameter("office", office).setParameterList("list", (Collection)cntctLst).executeUpdate();
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
	
	public UserQualificationModel getUserQualificationModel(long id) throws Exception {
		UserQualificationModel qualification = null;
		try {
			begin();
			qualification = (UserQualificationModel) getSession().get(UserQualificationModel.class, id);
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
	public List getUserQualificationList(long user, long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("from UserQualificationModel where user.id=:user and office.id=:office")
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
