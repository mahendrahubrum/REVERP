package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.payroll.model.OverTimeModel;
import com.webspark.dao.SHibernate;

/**
 * @author sangeeth
 * @date 17-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class OverTimeDao extends SHibernate implements Serializable{

	public long save(OverTimeModel mdl) throws Exception{
		try {
			begin();
			getSession().save(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl.getId();
	}
	
	public OverTimeModel getOverTimeModel(long id) throws Exception{
		OverTimeModel mdl=null;
		try {
			begin();
			mdl=(OverTimeModel)getSession().get(OverTimeModel.class, id);
			commit();
		} catch (Exception e) {
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
	
	public void update(OverTimeModel mdl) throws Exception{
		try {
			begin();
			getSession().update(mdl);
			commit();
		} catch (Exception e) {
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
	
	public void delete(OverTimeModel mdl) throws Exception{
		try {
			begin();
			getSession().delete(mdl);
			commit();
		} catch (Exception e) {
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
	
	@SuppressWarnings("rawtypes")
	public List getOverTimeModelList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.inventory.payroll.model.OverTimeModel(id,description) " +
					"from OverTimeModel where office.id=:office order by id DESC").setParameter("office", office).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return resultList;
	}
	
}