package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.model.ItemGroupModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class ItemGroupDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5644408375556149597L;
	List resultList = new ArrayList();

	public long save(ItemGroupModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
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
		return obj.getId();
	}

	public void update(ItemGroupModel obj) throws Exception {

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
			getSession().delete(new ItemGroupModel(id));
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
	}

	public List getAllItemGroups() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(ItemGroupModel.class)
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

	public List getAllItemGroupsNames() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery(
					"select new com.inventory.model.ItemGroupModel(id, name)"
							+ " from ItemGroupModel").list();
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

	public List getAllActiveItemGroupsNames() throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.ItemGroupModel(id, name)"
									+ " from ItemGroupModel where status=:val")
					.setParameter("val", SConstants.statuses.ITEM_GROUP_ACTIVE)
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

	public ItemGroupModel getItemGroup(long id) throws Exception {
		ItemGroupModel mod = null;
		try {
			begin();
			mod = (ItemGroupModel) getSession().get(ItemGroupModel.class, id);
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
		return mod;
	}

	public List getAllItemGroupsNames(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.ItemGroupModel(id, name)"
									+ " from ItemGroupModel where organization.id=:org and  status=:val")
					.setParameter("org", organizationId).setParameter("val", SConstants.statuses.ITEM_GROUP_ACTIVE).list();
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
	
	
	
	public List getAllItemGroups(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("from ItemGroupModel where status=:val and organization.id=:org")
					.setParameter("org", organizationId).setParameter("val", SConstants.statuses.ITEM_GROUP_ACTIVE)
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
	
	
	public List getAllActiveItemGroupsNames(long organizationId)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.ItemGroupModel(id, name)"
									+ " from ItemGroupModel where status=:val and organization.id=:org")
					.setParameter("org", organizationId)
					.setParameter("val", SConstants.statuses.ITEM_GROUP_ACTIVE)
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

}
