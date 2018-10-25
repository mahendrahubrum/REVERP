package com.inventory.config.unit.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class UnitDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4045660356158761284L;
	List resultList = new ArrayList();

	public List getAllUnits() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery(
					"select new com.inventory.config.unit.model.UnitModel(id, symbol)"
							+ " from UnitModel").list();
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

	public List getAllUnits(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.unit.model.UnitModel(id,name, symbol)"
									+ " from UnitModel where organization.id=:OrganizationId")
					.setParameter("OrganizationId", organizationId).list();
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

	public List getAllActiveUnits(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.unit.model.UnitModel(id, symbol)" +
					" from UnitModel where status=:val and organization.id=:org")
					.setParameter("val", SConstants.statuses.RACK_ACTIVE).setLong("org", org_id).list();
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

	public UnitModel getUnit(long id) throws Exception {
		UnitModel mod = null;
		try {
			begin();
			mod = (UnitModel) getSession().get(UnitModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}
	

	public long save(UnitModel unitModel) throws Exception {
		long model;
		try {
			
			begin();
			model = (Long) getSession().save(unitModel);
			commit();
			
		} catch (Exception e) {
			model = 0;
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return model;
	}

	public List getAllOrganizations() throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.S_OrganizationModel(id,name) FROM S_OrganizationModel"
									+ " where active='Y'").list();
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

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new UnitModel(id));
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
	
	public List getAllActiveUnitsFromOrg(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("from UnitModel where status=:val and organization.id=:org")
					.setParameter("val", SConstants.statuses.RACK_ACTIVE).setLong("org", org_id).list();
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
	
	

	public void update(UnitModel unitModel) throws Exception {
		try {

			begin();
			getSession().update(unitModel);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

}