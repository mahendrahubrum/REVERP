package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.DesignationModel;

public class DesignationDao extends SHibernate implements Serializable{

	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -9220322291229208890L;
	List resultList = new ArrayList();
		public long addOption(DesignationModel lm) throws Exception {

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
		
		
		public void Update(DesignationModel md) throws Exception {

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
				getSession().delete(new DesignationModel(id));
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
		
		
		public List getlabels() throws Exception {
			try {

				begin();
				resultList = getSession().createQuery("select new com.webspark.uac.model.DesignationModel(id,name) FROM DesignationModel").list();
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
		
		public List getDesignations(long org_id) throws Exception {
			try {

				begin();
				resultList = getSession().createQuery("select new com.webspark.uac.model.DesignationModel(id,name) FROM DesignationModel where organization_id="+org_id).list();
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
		
		public DesignationModel getselecteditem(long Id) throws Exception {
			DesignationModel lm=null;
			try {
				

				begin();
				lm = (DesignationModel)getSession().get(DesignationModel.class,Id);
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
