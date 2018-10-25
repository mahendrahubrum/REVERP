package com.inventory.dao;

import java.util.List;
import java.util.Set;

import com.inventory.model.DocumentAccessModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 30-Jul-2015
 */
public class DocumentAccessDao extends SHibernate{

	public List getAllSelections(Set ids, int type) throws Exception {
		List resultList;
		try {
			begin();

			if(type==1){
				resultList = getSession()
						.createQuery("from S_OrganizationModel where id in (:ids)").setParameterList("ids", ids).list();
			}else if(type==2){
				resultList = getSession()
						.createQuery("from S_OfficeModel where id in (:ids)").setParameterList("ids", ids).list();
			}else{
				resultList = getSession()
						.createQuery(
								" from UserModel where where id in (:ids)").setParameterList("ids", ids).list();
			}

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

	public DocumentAccessModel getAccessModel(long id) throws Exception {
		
		DocumentAccessModel mdl;
		try {
			begin();

			mdl= (DocumentAccessModel) getSession().get(DocumentAccessModel.class,id);

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
		return mdl;
	}


	public DocumentAccessModel getAccessModel(String filePath) throws Exception {
		
		DocumentAccessModel mdl;
		try {
			begin();

			mdl = (DocumentAccessModel) getSession()
					.createQuery(
							"from DocumentAccessModel where filePath=:filePath")
					.setParameter("filePath", filePath).uniqueResult();

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
		return mdl;
	}
	
	public void save(DocumentAccessModel mdl) throws Exception {
		try {
			begin();

			getSession().save(mdl);

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

	public void delete(String path) throws Exception {
		try {
			begin();
			DocumentAccessModel mdl=(DocumentAccessModel) getSession().createQuery(
					"from DocumentAccessModel where filePath=:filePath")
			.setParameter("filePath", path).uniqueResult();
			getSession().delete(mdl);
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

	public void update(DocumentAccessModel mdl) throws Exception {
		try {
			begin();
			List oldDelList = getSession()
					.createQuery(
							"select b.id from DocumentAccessModel a join a.doc_access_list b where a.id=:id")
					.setParameter("id", mdl.getId()).list();
			
//			getSession().createQuery("delete from DocumentAccessModel where id =:id").setParameter("id", mdl.getId()).executeUpdate();
			getSession().update(mdl);
			flush();
			if(oldDelList!=null && oldDelList.size()>0)
				getSession().createQuery("delete from DocumentAccessDetailsModel where id in (:ids)").setParameterList("ids", oldDelList).executeUpdate();
			
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

	public List getAccessUserIds(long id, String access) throws Exception {
		List resultList;
		try {
			begin();
			if(access.equalsIgnoreCase("view"))
			resultList = getSession()
					.createQuery(
							"select b.user from DocumentAccessModel a join a.doc_access_list b where a.id =:id and b.view='Y'")
					.setParameter("id", id).list();
			
			
			else if(access.equalsIgnoreCase("download"))
				resultList = getSession()
						.createQuery(
								"select b.user from DocumentAccessModel a join a.doc_access_list b where a.id =:id and b.download='Y'")
						.setParameter("id", id).list();
			
			else
				resultList = getSession()
				.createQuery(
						"select b.user from DocumentAccessModel a join a.doc_access_list b where a.id =:id and b.delete='Y'")
				.setParameter("id", id).list();
	
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
}
