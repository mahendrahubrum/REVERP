package com.inventory.management.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.management.model.TaskComponentModel;
import com.webspark.dao.SHibernate;

public class TaskComponentDao extends SHibernate implements Serializable{

	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -9220322291229208890L;
	List resultList = new ArrayList();
		public long save(TaskComponentModel lm) throws Exception {

			try {

				begin();
				getSession().save(lm);
				commit();

			} catch (Exception e) {
				rollback();
				close();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				flush();
				close();
				return lm.getId();		}
		}
		
		
		public void Update(TaskComponentModel md) throws Exception {

			try {

				begin();
				getSession().update(md);
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
				getSession().delete(new TaskComponentModel(id));
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
		
		
		public List getComponentNames() throws Exception {
			try {
				
				begin();
				resultList = getSession().createQuery("select new com.inventory.management.model.TaskComponentModel(id,name) FROM TaskComponentModel").list();
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
		
		public List getComponentNames(long org_id) throws Exception {
			try {
				
				begin();
				resultList = getSession().createQuery("select new com.inventory.management.model.TaskComponentModel(id,name) FROM TaskComponentModel where organization_id="+org_id).list();
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
		
		
		public List getAllComponents(long org_id) throws Exception {
			try {
				begin();
				resultList = getSession().createQuery("from TaskComponentModel where organization_id="+org_id).list();
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
		
		
		
		public TaskComponentModel getComponent(long Id) throws Exception {
			TaskComponentModel lm=null;
			try {
				
				begin();
				lm = (TaskComponentModel)getSession().get(TaskComponentModel.class,Id);
				commit();

			} catch (Exception e) {
				rollback();
				close();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				flush();
				close();
				return lm;
			}
		}

}
