package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.inventory.model.OfficeAllocationModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class OfficeAllocationDao extends SHibernate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7646797238538971210L;
	private List resultList=new ArrayList();

	public void updateOptionsToUser(List<OfficeAllocationModel> objList, long userId) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from OfficeAllocationModel where login_id=:ofc")
							.setLong("ofc", userId).executeUpdate();
			
			for (OfficeAllocationModel obj: objList) {
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
	
	
	public Set<Long> getAllOffices( long loginId) throws Exception {
		Set<Long> users=new HashSet<Long>();
		try {

			begin();
			
			List<Long> lst=getSession().createQuery("select office_id from OfficeAllocationModel where login_id=:log")
					.setLong("log", loginId).list();
			
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
			
			long ct=(Long) getSession().createQuery("select count(id) from OfficeAllocationModel where office_id=:ofc and option_id=:opt")
					.setLong("ofc", ofcId).setInteger("opt", optionId).uniqueResult();
			
			
			if(ct==0)
				avail= true;
			else {
				long ct1=(Long) getSession().createQuery("select count(id) from OfficeAllocationModel where option_id=:opt and login_id=:log")
						.setLong("log", login_id).setInteger("opt", optionId).uniqueResult();
				
				if(ct1>0)
					avail= true;
			}
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			flush();
			close();
			return avail;
		}
	}
	
	
	
	
	
}
