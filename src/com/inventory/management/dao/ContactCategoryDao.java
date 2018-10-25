package com.inventory.management.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.management.model.ContactCategoryModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class ContactCategoryDao extends SHibernate implements Serializable{

	List resultList = new ArrayList();

	public long save(ContactCategoryModel obj) throws Exception {

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

	public void update(ContactCategoryModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
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
			getSession().delete(new ContactCategoryModel(id));
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

	public List getAllCategories() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(ContactCategoryModel.class)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}

	public List getAllCategoryNames(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.management.model.ContactCategoryModel(id, name)"
									+ " from ContactCategoryModel where organization_id=:org")
									.setLong("org", org_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}


	public ContactCategoryModel getSalesType(long id) throws Exception {
		ContactCategoryModel mod = null;
		try {
			begin();
			mod = (ContactCategoryModel) getSession().get(ContactCategoryModel.class, id);
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
