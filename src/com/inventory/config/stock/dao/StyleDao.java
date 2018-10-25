package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.StyleModel;
import com.inventory.config.stock.model.StyleModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class StyleDao extends SHibernate implements Serializable{


	public long save(StyleModel obj) throws Exception {

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

	public void update(StyleModel obj) throws Exception {

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
			StyleModel mdl=(StyleModel)getSession().get(StyleModel.class, id);
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
	public List getAllStyleModel(long office_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.stock.model.StyleModel(id, name) from StyleModel where office.id=:ofc")
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

	public List getAllActiveStyleModel(long ofc_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.StyleModel(id, name)"
									+ " from StyleModel where status=:val and office.id=:ofc")
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

	public StyleModel getStyleModel(long id) throws Exception {
		StyleModel mod = null;
		try {
			begin();
			mod = (StyleModel) getSession().get(StyleModel.class, id);
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
