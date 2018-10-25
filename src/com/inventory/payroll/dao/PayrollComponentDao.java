package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.List;

import com.inventory.payroll.model.PayrollComponentModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 3, 2013
 */
public class PayrollComponentDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3610037608856072823L;

	public long save(PayrollComponentModel model) throws Exception {
		try {
			begin();
			getSession().save(model);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return model.getId();
	}

	public PayrollComponentModel getComponentModel(long id) throws Exception {
		PayrollComponentModel model = null;
		try {
			begin();
			model = (PayrollComponentModel) getSession().get(
					PayrollComponentModel.class, id);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return model;
	}

	public void update(PayrollComponentModel componentModel) throws Exception {
		try {
			begin();
			getSession().update(componentModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new PayrollComponentModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}

	}

	public List getAllComponents(long officeID) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"Select new com.inventory.payroll.model.PayrollComponentModel(id, concat(name,' ( ', code ,' ) '))"
									+ " from PayrollComponentModel where office.id=:ofc order by name")
					.setParameter("ofc", officeID).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}

}
