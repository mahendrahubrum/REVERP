package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.EmployeeDocumentCategoryModel;

@SuppressWarnings("serial")
public class EmployeeDocumentDao extends SHibernate implements Serializable {

	public long save(EmployeeDocumentCategoryModel mdl) throws Exception {
		try {
			begin();
			getSession().save(mdl);
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
		return mdl.getId();
	}

	public long update(EmployeeDocumentCategoryModel mdl) throws Exception {
		try {
			begin();
			getSession().update(mdl);
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
		return mdl.getId();
	}

	public long delete(EmployeeDocumentCategoryModel mdl) throws Exception {
		try {
			begin();
			getSession().delete(mdl);
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
		return mdl.getId();

	}

	@SuppressWarnings({ "rawtypes"})
	public List getAllDocuments(long orgId) throws Exception
	{	
		List idLIst=null;
		try
		{
			begin();
			idLIst=getSession().createQuery("select new com.webspark.uac.model.EmployeeDocumentCategoryModel(id,name)"+
											" from EmployeeDocumentCategoryModel where org_id=:oid order by name").setParameter("oid", orgId).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}		
		return idLIst;	
	}
	
	public EmployeeDocumentCategoryModel getEmployeeDocumentModel(long id) throws Exception
	{
		EmployeeDocumentCategoryModel mdl = null;
		try
		{
			begin();
			mdl=(EmployeeDocumentCategoryModel) getSession().get(EmployeeDocumentCategoryModel.class, id);
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return mdl;
	}
	
	public String getDocumentName(long id) throws Exception
	{
		String document = null;
		try
		{
			begin();
			document=(String) getSession().createQuery("select name from EmployeeDocumentCategoryModel where id="+id).uniqueResult();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally
		{
			flush();
			close();
		}
		return document;
	}
	
}
