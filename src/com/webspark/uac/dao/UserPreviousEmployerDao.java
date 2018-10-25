package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserPreviousEmployerModel;

@SuppressWarnings("serial")
public class UserPreviousEmployerDao extends SHibernate implements Serializable{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(List list, long user, long office) throws Exception{
		try {
			begin();
			List cntctLst=new ArrayList();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				UserPreviousEmployerModel mdl = (UserPreviousEmployerModel) itr.next();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
				cntctLst.add(mdl.getId());
			}
			
			if(cntctLst!=null && cntctLst.size()>0){
				getSession().createQuery("delete from UserPreviousEmployerModel where user.id=:user and officeId=:office and id not in (:list)")
					.setParameter("user", user).setParameter("office", office).setParameterList("list", (Collection)cntctLst).executeUpdate();
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
	
	public UserPreviousEmployerModel getUserPreviousEmployerModel(long id) throws Exception {
		UserPreviousEmployerModel qualification = null;
		try {
			begin();
			qualification = (UserPreviousEmployerModel) getSession().get(UserPreviousEmployerModel.class, id);
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
	public List getUserPreviousEmployerModel(long user, long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("from UserPreviousEmployerModel where user.id=:user and officeId=:office").setParameter("user", user).setParameter("office", office).list();
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
