package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.GroupModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class GroupDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5286771736350067601L;
	List resultList = new ArrayList();

	public long save(GroupModel obj) throws Exception {

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
		}
		return obj.getId();
	}
	
	
	public void update(GroupModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
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
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new GroupModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} 
			flush();
			close();
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getAllGroups() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(GroupModel.class).list();
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
		}
		return resultList;
	}
	
	public List getAllGroupsNames(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id, name)" +
					" from GroupModel where organization.id=:org order by name")
					.setParameter("org", org_id).list();
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
		}
		return resultList;
	}
	
	public List getAllGroupsNamesWithoutParent(long org_id) throws Exception {
		
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id, name)" +
					" from GroupModel where organization.id=:org and level!=1 order by name")
					.setParameter("org", org_id).list();
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
	
	public List getAllActiveGroupsNames(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id, name)" +
					" from GroupModel where status=:val and organization.id=:org  order by name").setParameter("org", org_id)
					.setParameter("val", SConstants.statuses.GROUP_ACTIVE).list();
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
	
	
	
	public List getAllActiveGroups(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("from GroupModel where status=:val and organization.id=:org  order by name").setParameter("org", org_id)
					.setParameter("val", SConstants.statuses.GROUP_ACTIVE).list();
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
	
	
	
	
	
	
	
	public List getAllActiveParentGroupsNames(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id, name)" +
					" from GroupModel where status=1 and organization.id=:org  order by name")
					.setLong("org", org_id).list();
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
	
	
	public GroupModel getGroup(long id) throws Exception {
		GroupModel mod=null;
		try {
			begin();
			mod=(GroupModel) getSession().get(GroupModel.class, id);
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
	
	
	public List getAllGroupsUnderClass(long class_id, long office_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id,name) " +
					" from GroupModel where office.id=:ofcid and status=:sts and account_class_id=:cls")
					.setLong("ofcid", office_id).setLong("sts", 1).setLong("cls", class_id).list();
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
	
	
	public boolean isGroupExist(String name,long orgId) throws Exception {
		try {
			begin();
			Object obj = getSession().createQuery("select id from GroupModel where name=:nm and organization.id=:org")
								.setParameter("nm", name).setParameter("org", orgId).uniqueResult();
			commit();
			
			if(obj==null)
				return false;
			else
				return true;
			
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
	
	
	public List getChilds(long org_id, long gpid) throws Exception {
		
		
		try {
			List list = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id,name) " +
					"from GroupModel where parent_id=:grp and organization.id=:org")
					.setParameter("org", org_id).setLong("grp", gpid).list();
			
			Iterator it1=list.iterator();
			while(it1.hasNext()) {
				
				GroupModel obj=(GroupModel) it1.next();
				resultList.add(obj);
				resultList.addAll(getChilds(org_id, obj.getId()));
				
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		return resultList;
	}


	public List getAllParentGroups() throws Exception {
		List list;
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.config.acct.model.GroupModel(id,name) " +
					"from GroupModel where parent_id=0  order by name").list();
			
				
			commit();			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		flush();
		close();
		return list;
	}


	public long getClassIdOfGroup(Long grpId) throws Exception {
		long id=0;
		try {
			begin();
			id = (Long) getSession().createQuery("select account_class_id " +
					"from GroupModel where id=:id").setLong("id", grpId).uniqueResult();
			
				
			commit();			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		flush();
		close();
		return id;
	}


	public int getLevel(Long grpId) throws Exception {
		int level=0;
		try {
			begin();
			level = (Integer) getSession().createQuery("select level " +
					"from GroupModel where id=:id").setLong("id", grpId).uniqueResult();
			
				
			commit();			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		flush();
		close();
		return level;
	}
	
	
	
	
}
