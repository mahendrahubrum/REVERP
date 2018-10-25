package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.S_ProjectOptionMapModel;

/**
 * @Author Jinshad P.T.
 */

public class OptionProjectMapDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6915101401905952362L;
	List resultList = new ArrayList();

	public long save(S_ProjectOptionMapModel obj) throws Exception {

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
	
	
	public void update(S_ProjectOptionMapModel sts) throws Exception {

		try {

			begin();
			getSession().update(sts);
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
			getSession().delete(new S_ProjectOptionMapModel(id));
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
	
	
	public List getAllProjectOptionMaps(long project_type) throws Exception {

		try {
			
			begin();
			resultList = getSession().createQuery(
							"from S_ProjectOptionMapModel where project_type.id=:prj")
							.setLong("prj", project_type).list();
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
	

	
	public S_ProjectOptionMapModel getOptionGroup(long stsId) throws Exception {
		S_ProjectOptionMapModel mod=null;
		try {
			begin();
			mod=(S_ProjectOptionMapModel) getSession().get(S_ProjectOptionMapModel.class, stsId);
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
	
	
	public List getAllProjectTypes() throws Exception {

		try {
			resultList=null;
			begin();
			resultList = getSession()
					.createQuery("from S_ProjectTypeModel where status=1").list();
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
	
	
	public List getAllOptions() throws Exception {
		try {
			resultList=null;
			begin();
			resultList = getSession().createQuery("FROM S_OptionModel").list();
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
	
	public String getOptionsClass(long optId) throws Exception {
		String class_name="";
		try {
			begin();
			class_name = (String) getSession().createQuery("select class_name from S_OptionModel where option_id=:id")
								.setLong("id", optId).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return class_name;
		}
	}
	
	
	
	
	
}
