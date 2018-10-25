package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.SizeModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SizeDao extends SHibernate implements Serializable{


	public long save(SizeModel obj) throws Exception {

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

	public void update(SizeModel obj) throws Exception {

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
			SizeModel mdl=(SizeModel)getSession().get(SizeModel.class, id);
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
	public List getAllSizeModel(long office_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.stock.model.SizeModel(id, name) from SizeModel where office.id=:ofc")
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

	public List getAllActiveSizeModel(long ofc_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.SizeModel(id, name)"
									+ " from SizeModel where status=:val and office.id=:ofc")
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

	public SizeModel getSizeModel(long id) throws Exception {
		SizeModel mod = null;
		try {
			begin();
			mod = (SizeModel) getSession().get(SizeModel.class, id);
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
