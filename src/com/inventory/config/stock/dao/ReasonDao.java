package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.ReasonModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ReasonDao extends SHibernate implements Serializable{


	public long save(ReasonModel obj) throws Exception {

		try {
			begin();
			getSession().save(obj);
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
		return obj.getId();
	}

	public void update(ReasonModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
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

	public void delete(long id) throws Exception {

		try {
			begin();
			ReasonModel mdl=(ReasonModel)getSession().get(ReasonModel.class, id);
			getSession().delete(mdl);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}

	@SuppressWarnings("rawtypes")
	public List getAllReasonModel(long office_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.stock.model.ReasonModel(id, name) from ReasonModel where office.id=:ofc")
					.setParameter("ofc", office_id).list();
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

	public List getAllActiveReasonModel(long ofc_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ReasonModel(id, name)"
									+ " from ReasonModel where status=:val and office.id=:ofc")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.SALES_TYPE_ACTIVE)
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

	public ReasonModel getReasonModel(long id) throws Exception {
		ReasonModel mod = null;
		try {
			begin();
			mod = (ReasonModel) getSession().get(ReasonModel.class, id);
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
		return mod;
	}
}
