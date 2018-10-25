package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.model.BuildingModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class BuildingDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4660593496135564813L;
	List resultList = new ArrayList();

	public long save(BuildingModel obj) throws Exception {

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

	public void update(BuildingModel obj) throws Exception {

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
			getSession().delete(new BuildingModel(id));
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

	public List getAllBuildingNames() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery(
					"select new com.inventory.model.BuildingModel(id, name)"
							+ " from BuildingModel").list();
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

	public List getAllActiveBuildingNames() throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.BuildingModel(id, name)"
									+ " from BuildingModel where status=:val")
					.setParameter("val", SConstants.statuses.BUILDING_ACTIVE)
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

	public List getAllActiveBuildingNamesUnderOffice(long office_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.BuildingModel(id, name)"
									+ " from BuildingModel where status=:val and office.id=:ofc")
					.setLong("ofc", office_id)
					.setParameter("val", SConstants.statuses.BUILDING_ACTIVE)
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

	public BuildingModel getBuilding(long id) throws Exception {
		BuildingModel mod = null;
		try {
			begin();
			mod = (BuildingModel) getSession().get(BuildingModel.class, id);
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

	public List getAllBuildingNamesUnderOrganization(long organizationID)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.BuildingModel(id, name)"
									+ " from BuildingModel where office.organization.id=:org")
					.setParameter("org", organizationID)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	
	public List getAllBuildingNamesUnderOffice(long ofc)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.BuildingModel(id, name)"
									+ " from BuildingModel where office.id=:ofc")
					.setParameter("ofc", ofc)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	

}
