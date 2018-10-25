package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.model.ItemSubGroupModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class ItemSubGroupDao extends SHibernate implements Serializable{

	List resultList = new ArrayList();

	public long save(ItemSubGroupModel obj) throws Exception {

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

	public void update(ItemSubGroupModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
			commit();

		}catch (Exception e) {
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
			getSession().delete(new ItemSubGroupModel(id));
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
		close();
	}

	public List getAllItemSubGroups() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(ItemSubGroupModel.class)
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

	public List getAllActiveItemSubGroups(long itemGroupId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from ItemSubGroupModel" +
							" where status=:val" +
							((itemGroupId != 0) ? " AND group.id="+itemGroupId : ""))
					.setParameter("val",
							SConstants.statuses.ITEM_SUBGROUP_ACTIVE)
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
	
	
	public List getAllActiveItemSubGroupsUnderOrg(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from ItemSubGroupModel where status=:val and group.organization.id=:org")
					.setParameter("org", organizationId)
					.setParameter("val",
							SConstants.statuses.ITEM_SUBGROUP_ACTIVE).list();
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

	public List getAllItemSubGroupsNames(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.ItemSubGroupModel(id, name)"
									+ " from ItemSubGroupModel where group.organization.id=:org")
					.setParameter("org", organizationId).list();
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

	public List getAllActiveItemSubGroupsNames(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.ItemSubGroupModel(id, name)"
									+ " from ItemSubGroupModel where status=:val and group.organization.id=:org order by name")
					.setParameter("org", organizationId)
					.setParameter("val",
							SConstants.statuses.ITEM_SUBGROUP_ACTIVE).list();
			commit();
		}catch (Exception e) {
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

	public ItemSubGroupModel getItemSubGroup(long id) throws Exception {
		ItemSubGroupModel mod = null;
		try {
			begin();
			mod = (ItemSubGroupModel) getSession().get(ItemSubGroupModel.class,
					id);
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

	public String getIconName(long itmId) throws Exception {
		String str = null;
		try {
			begin();
			str = (String) getSession()
					.createQuery(
							"select icon from ItemSubGroupModel where id=:id")
					.setParameter("id",itmId).uniqueResult();
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
		
		return str;
	}

}
