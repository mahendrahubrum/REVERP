package com.inventory.budget.dao;

import java.util.ArrayList;
import java.util.List;

import com.inventory.budget.model.BudgetDefinitionModel;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */
public class BudgetDefinitionDao extends SHibernate {
	public long save(BudgetDefinitionModel mdl) throws Exception {
		try {
			begin();
			getSession().save(mdl);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl.getId();
	}

	public BudgetDefinitionModel getBudgetDefinitionModel(long id)
			throws Exception {
		BudgetDefinitionModel mdl = null;
		try {
			begin();
			mdl = (BudgetDefinitionModel) getSession().get(
					BudgetDefinitionModel.class, id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}

		return mdl;
	}

	public List getAllBudgets() throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession().createQuery("from BudgetDefinitionModel").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}

	public List getAllActiveBudgets(long of_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetDefinitionModel where office_id.id=:offic and status=1")
					.setLong("offic", of_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}

	public List getActiveBudgets() throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession().createQuery(
					"from BudgetDefinitionModel where status=1").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}

	public List getAllBudgets(long of_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetDefinitionModel where office_id.id=:offic")
					.setLong("offic", of_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}

	public void update(BudgetDefinitionModel mdl) throws Exception {
		try {
			begin();
			getSession().update(mdl);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}

	}

	public void delete(BudgetDefinitionModel mdl) throws Exception {
		try {
			begin();
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

	public List getAllDepartments(long id) throws Exception {
		List list = null;
		try {
			list = new ArrayList();
			begin();
			list = getSession()
					.createQuery(
							"select  new com.webspark.uac.model.DepartmentModel(id,name) from DepartmentModel where organization_id =:id order by name")
					.setParameter("id", id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return list;
	}
}
