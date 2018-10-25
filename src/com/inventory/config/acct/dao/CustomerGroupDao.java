package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.model.CustomerGroupModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 9, 2015
 */
public class CustomerGroupDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -1529122180362551900L;
	List resultList = new ArrayList();

	public List getAllCustomerGroups(long officeId) throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from CustomerGroupModel  where officeId=:org order by name")
					.setParameter("org", officeId).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public CustomerGroupModel getCustomerGroup(long CustomerGroupId) throws Exception {
		
		CustomerGroupModel bm=null;
		try {
			
			begin();
			bm = (CustomerGroupModel) getSession()
					.get(CustomerGroupModel.class, CustomerGroupId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return bm;
	}
	
	public void save(CustomerGroupModel bm) throws Exception {
		
		try {
			
			begin();
			getSession().save(bm);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	public void update(CustomerGroupModel bm) throws Exception {
		
		try {
			
			begin();
			getSession().update(bm);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	public void delete(long CustomerGroupId) throws Exception {
		
		
		try {
			CustomerGroupModel bm=null;
			begin();
			bm = (CustomerGroupModel) getSession().get(CustomerGroupModel.class, CustomerGroupId);
			getSession().delete(bm);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

}
