package com.inventory.config.stock.dao;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.ItemDepartmentModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class ItemDepartmentDao extends SHibernate{

	public List getAllItemDepartmentNames(long organizationID) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("from ItemDepartmentModel where status=:val and organization.id=:org")
					.setParameter("org", organizationID).setParameter("val", SConstants.statuses.ITEM_GROUP_ACTIVE)
					.list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	public List getAllItemDepartmentNamesUnderOrganization(long organizationID) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("from ItemDepartmentModel where  organization.id=:org")
					.setParameter("org", organizationID)
					.list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	List resultList = new ArrayList();

	public long save(ItemDepartmentModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
			commit();

		}  catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj.getId();
	}

	public void update(ItemDepartmentModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
			commit();

		}  catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			ItemDepartmentModel dep=(ItemDepartmentModel) getSession().get(ItemDepartmentModel.class, id);
			getSession().delete(dep);
			commit();

		}  catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public List getAllItemDepartments() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(ItemDepartmentModel.class)
					.list();
			commit();
		}  catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	

	public ItemDepartmentModel getItemDepartment(long id) throws Exception {
		ItemDepartmentModel mod = null;
		try {
			begin();
			mod = (ItemDepartmentModel) getSession().get(ItemDepartmentModel.class, id);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return mod;
	}
	

}
