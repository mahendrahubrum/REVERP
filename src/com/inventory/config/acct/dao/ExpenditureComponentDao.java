package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.model.ExpenditureComponentModel;
import com.webspark.dao.SHibernate;

public class ExpenditureComponentDao extends SHibernate implements Serializable{

	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -9220322291229208890L;
	List resultList = new ArrayList();
		public long save(ExpenditureComponentModel lm) throws Exception {

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
		
		
		public void Update(ExpenditureComponentModel md) throws Exception {

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
				getSession().delete(new ExpenditureComponentModel(id));
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
				resultList = getSession().createQuery("select new com.inventory.management.model.ExpenditureComponentModel(id,name) FROM ExpenditureComponentModel").list();
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
		
		public List getComponentNames(long ofc_id) throws Exception {
			try {
				
				begin();
				resultList = getSession().createQuery("select new com.inventory.config.acct.model.ExpenditureComponentModel(id,name) FROM ExpenditureComponentModel where office.id="+ofc_id).list();
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
				resultList = getSession().createQuery("from ExpenditureComponentModel where organization_id="+org_id).list();
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
		
		
		
		public ExpenditureComponentModel getComponent(long Id) throws Exception {
			ExpenditureComponentModel lm=null;
			try {
				
				begin();
				lm = (ExpenditureComponentModel)getSession().get(ExpenditureComponentModel.class,Id);
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
