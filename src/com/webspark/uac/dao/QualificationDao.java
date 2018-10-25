package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.QualificationModel;

/**
 * 
 * @author sangeeth
 * Automobile
 * 10-Jun-2015
 */

@SuppressWarnings("serial")
public class QualificationDao extends SHibernate implements Serializable{

	public long save(QualificationModel mdl) throws Exception{
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
	
	public QualificationModel getQualificationModel(long id) throws Exception{
		QualificationModel mdl=null;
		try {
			begin();
			mdl=(QualificationModel)getSession().get(QualificationModel.class, id);
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
	
	public void update(QualificationModel mdl) throws Exception{
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
	
	public void delete(QualificationModel mdl) throws Exception{
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
	public List getQualificationModelList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.webspark.uac.model.QualificationModel(id,name) " +
					"from QualificationModel where office.id=:office order by id DESC").setParameter("office", office).list();
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
	public List getActiveQualificationModelList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.webspark.uac.model.QualificationModel(id,name) " +
					"from QualificationModel where office.id=:office and status=1 order by id DESC").setParameter("office", office).list();
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