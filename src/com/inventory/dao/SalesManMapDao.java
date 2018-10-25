package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.inventory.model.SalesManMapModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class SalesManMapDao extends SHibernate implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7646797238538971210L;
	private List resultList=new ArrayList();

	
	
	public void updateOptionsToUser(List<SalesManMapModel> objList, long ofcId, int optionId) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from SalesManMapModel where office_id=:ofc and option_id=:opt")
							.setLong("ofc", ofcId).setInteger("opt", optionId).executeUpdate();
			
			for (SalesManMapModel obj: objList) {
				getSession().save(obj);
			}
			
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
	}
	
	
	public Set<Long> getAllUsers( long ofcId, int optionId) throws Exception {
		Set<Long> users=new HashSet<Long>();
		try {

			begin();
			
			List<Long> lst=getSession().createQuery("select login_id from SalesManMapModel where office_id=:ofc and option_id=:opt")
					.setLong("ofc", ofcId).setInteger("opt", optionId).list();
			
			commit();
			
			if(lst!=null)
				users.addAll(lst);

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return users;
		}
	}

	
	
	
	public boolean isOptionsAvailToUser( long ofcId, int optionId, long login_id) throws Exception {
		boolean avail=false;
		try {
			begin();
			
			long ct=(Long) getSession().createQuery("select count(id) from SalesManMapModel where office_id=:ofc and option_id=:opt")
					.setLong("ofc", ofcId).setInteger("opt", optionId).uniqueResult();
			
			if(ct==0)
				avail= true;
			else {
				long ct1=(Long) getSession().createQuery("select count(id) from SalesManMapModel where option_id=:opt and login_id=:log")
						.setLong("log", login_id).setInteger("opt", optionId).uniqueResult();
				
				if(ct1>0)
					avail= true;
			}
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			return false;
		} finally {
			flush();
			close();
			return avail;
		}
	}
	
	
	public List getUsers( long ofcId, int optionId) throws Exception {
		List lst=null;
		try {
			begin();
			
			List idLst=getSession().createQuery("select b.login_id from SalesManMapModel b where b.office_id=:ofc and b.option_id=:opt")
					.setLong("ofc", ofcId).setInteger("opt", optionId).list();
			
			if(idLst!=null && idLst.size()>0) 
				lst= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) " +
						"from UserModel a where a.id in (:lst)  order by a.first_name")
				.setParameterList("lst", idLst).list();
			else
				lst= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) " +
						"from UserModel a where a.office.id=:ofc and user_role.id>1 order by a.first_name")
						.setLong("ofc", ofcId).list();
			
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
