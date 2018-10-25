package com.inventory.process.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.process.model.EndProcessModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ConfigureEndProcessDao extends SHibernate implements Serializable{
	
	private List resultList = new ArrayList();
	
	public List getAllProcessModel(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from EndProcessModel where office.id=:ofc order by name")
					.setParameter("ofc", ofc_id)
					.list();
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
	
	public long save(EndProcessModel model) throws Exception {
		try {
			begin();
			getSession().save(model);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}	return model.getId();
	}
	
	public void update(EndProcessModel model) throws Exception {
		try {
			begin();
			getSession().update(model);
			commit();
	
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		}finally{
			flush();
			close();
		}
	}
	
	public void delete(long id) throws Exception {
		EndProcessModel model=null;
		try {
			model=(EndProcessModel) getSession().get(EndProcessModel.class, id);
			begin();
			getSession().delete(model);
			commit();
	
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
	}

	public EndProcessModel getEndProcessModel(long id) throws Exception {
		EndProcessModel mdl = null;
		try {
			begin();
			mdl = (EndProcessModel) getSession().get(EndProcessModel.class, id);
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
	
	
}
