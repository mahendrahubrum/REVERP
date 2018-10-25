package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.BillNameModel;

public class BillNameDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8101243619593185989L;
	List resultList = new ArrayList();

	public long save(BillNameModel obj) throws Exception {

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
	
	
	public void Update(BillNameModel sts) throws Exception {

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
			getSession().delete(new BillNameModel(id));
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
	
	
	public List getAllModules()
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM BillNameModel").list();
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
	

	
	
	public BillNameModel getModule(long stsId) throws Exception {
		BillNameModel mod=null;
		try {
			begin();
			mod=(BillNameModel) getSession().get(BillNameModel.class, stsId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}
	
	
	
	
	
}
