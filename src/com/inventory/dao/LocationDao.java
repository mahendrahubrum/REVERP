package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.model.LocationModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
public class LocationDao extends SHibernate implements Serializable{


	public long save(LocationModel mdl) throws Exception {
		try {
			begin();
			getSession().save(mdl);
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
		return mdl.getId();
	}

	public void update(LocationModel mdl) throws Exception {

		try {
			begin();
			getSession().update(mdl);
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

	public void delete(long id) throws Exception {

		try {
			begin();
			LocationModel mdl = (LocationModel) getSession().get(LocationModel.class, id);
			getSession().delete(mdl);
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

	public LocationModel getLocationModel(long id) throws Exception {
		LocationModel mdl = null;
		try {
			begin();
			mdl = (LocationModel) getSession().get(LocationModel.class, id);
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
		return mdl;
	}

	@SuppressWarnings("rawtypes")
	public List getAllLocationModelUnderOrganization(long organizationID) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.LocationModel(id, name)"
								+ " from LocationModel where office.organization.id=:org").setParameter("org", organizationID).list();
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
	
	@SuppressWarnings("rawtypes")
	public List getLocationModelList(long office) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.LocationModel(id, name)"
									+ " from LocationModel where office.id=:office")
					.setParameter("office", office).list();
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
	

}
