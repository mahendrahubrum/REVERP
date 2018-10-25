package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.S_StatusModel;

public class StatusDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6732148298562756427L;
	List resultList = new ArrayList();

	public long saveStatus(S_StatusModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	
	public void Update(S_StatusModel sts) throws Exception {

		try {

			begin();
			getSession().update(sts);
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
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new S_StatusModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
			
		} 
			flush();
			close();
		
	}
	
	
	public List getAllStatuses()
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_StatusModel").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	

	public List getStatuses(String modelName, String fieldName)
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_StatusModel WHERE model_name=:mdl and field_name=:fld")
					.setParameter("mdl", modelName)
					.setParameter("fld", fieldName).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public S_StatusModel getStatus(long stsId) throws Exception {
		S_StatusModel opt=null;
		try {
			begin();
			opt=(S_StatusModel) getSession().get(S_StatusModel.class, stsId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return opt;
		}
	}
	
	
	
	
	
}
