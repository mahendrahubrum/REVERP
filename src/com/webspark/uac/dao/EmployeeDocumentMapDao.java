package com.webspark.uac.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.EmployeeDocumentModel;

@SuppressWarnings("serial")
public class EmployeeDocumentMapDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public void save(List list) throws Exception{
		try{
			begin();
			EmployeeDocumentModel mdl;
			Iterator itr=list.iterator();
			while(itr.hasNext()){
				mdl=(EmployeeDocumentModel)itr.next();
				getSession().save(mdl);
				flush();
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
	
	
	@SuppressWarnings("rawtypes")
	public void update(List list) throws Exception{
		try{
			begin();
			EmployeeDocumentModel mdl;
			Iterator itr=list.iterator();
			while(itr.hasNext()){
				mdl=(EmployeeDocumentModel)itr.next();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
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
	
	
	@SuppressWarnings("rawtypes")
	public void delete(List list) throws Exception{
		try{
			begin();
			EmployeeDocumentModel mdl;
			Iterator itr=list.iterator();
			while(itr.hasNext()){
				mdl=(EmployeeDocumentModel)itr.next();
				getSession().delete(mdl);
				flush();
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
	
	
	public EmployeeDocumentModel getEmployeeDocumentModel(long id) throws Exception{
		EmployeeDocumentModel mdl=null;
		try{
			begin();
			mdl=(EmployeeDocumentModel)getSession().get(EmployeeDocumentModel.class, id);
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
	public List getDocumentList(long user, long did) throws Exception{
		List list=new ArrayList();
		try{
			begin();
			list=getSession().createQuery("from EmployeeDocumentModel where employee_id="+user+" and document.id="+did).list();
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
		return list;
	}
	
	
	public EmployeeDocumentModel getEmployeeDocumentModel(long user, long did, long office) throws Exception{
		EmployeeDocumentModel mdl=null;
		try{
			begin();
			mdl=(EmployeeDocumentModel)getSession().createQuery("from EmployeeDocumentModel where employee_id="+user+
													" and document.id="+did+" and office_id="+office).uniqueResult();
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
	public List getDocumentCategoryList(long id) throws Exception{
		List list=new ArrayList();
		try{
			begin();
			list=getSession().createQuery("from EmployeeDocumentCategoryModel where org_id="+id).list();
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
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getExpiryList(Date start,Date end,long id) throws Exception{
		List list=new ArrayList();
		try{
			begin();
			list=getSession().createQuery("select a from EmployeeDocumentModel a where a.expiry between :start and :end and a.document.id="+id)
					.setParameter("start", start).setParameter("end", end).list();
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
		return list;
	}
	
}
