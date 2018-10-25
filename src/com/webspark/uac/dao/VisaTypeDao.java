package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.VisaTypeModel;

/**
 * 
 * @author sangeeth
 * Automobile
 * 10-Jun-2015
 */

@SuppressWarnings("serial")
public class VisaTypeDao extends SHibernate implements Serializable{

	public long save(VisaTypeModel mdl) throws Exception{
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
	
	public VisaTypeModel getVisaTypeModel(long id) throws Exception{
		VisaTypeModel mdl=null;
		try {
			begin();
			mdl=(VisaTypeModel)getSession().get(VisaTypeModel.class, id);
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
	
	public void update(VisaTypeModel mdl) throws Exception{
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
	
	public void delete(VisaTypeModel mdl) throws Exception{
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
	public List getVisaTypeModelList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.webspark.uac.model.VisaTypeModel(id,name) " +
					"from VisaTypeModel where office.id=:office order by id DESC").setParameter("office", office).list();
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
	
	@SuppressWarnings("rawtypes")
	public List getActiveVisaTypeModelList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.webspark.uac.model.VisaTypeModel(id,name) " +
					"from VisaTypeModel where office.id=:office and status=1 order by id DESC").setParameter("office", office).list();
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