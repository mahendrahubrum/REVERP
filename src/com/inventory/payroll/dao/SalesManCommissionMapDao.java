package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.SalesManCommissionMapModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SalesManCommissionMapDao extends SHibernate implements Serializable{
	
	public SalesManCommissionMapModel getSalesManCommissionMapModel(long id) throws Exception {
		SalesManCommissionMapModel mdl=null;
		try{
			begin();
			mdl=(SalesManCommissionMapModel)getSession().get(SalesManCommissionMapModel.class, id);
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	public SalesManCommissionMapModel getSalesManCommissionMapModel(long user, long office) throws Exception {
		SalesManCommissionMapModel mdl=null;
		try{
			begin();
			mdl=(SalesManCommissionMapModel)getSession().createQuery("from SalesManCommissionMapModel where userId=:user and officeId=:office").setParameter("user", user).setParameter("office", office).uniqueResult();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}

	
	@SuppressWarnings("rawtypes")
	public void update(List list) throws Exception{
		try{
			begin();
			SalesManCommissionMapModel map=null;
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					map=(SalesManCommissionMapModel)itr.next();
					if(map.getId()!=0){
						getSession().update(map);
					}
					else{
						getSession().save(map);
					}
					flush();
				}
			}
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
}
