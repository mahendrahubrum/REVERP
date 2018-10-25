package com.inventory.budget.dao;

import java.util.List;

import com.inventory.budget.model.BudgetModel;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */
public class BudgetDao extends SHibernate {
	public long save(BudgetModel mdl) throws Exception {
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
		return mdl.getId();
	}

	public BudgetModel getBudgetModel(long id) throws Exception {
		BudgetModel mdl = null;
		try {
			begin();
			mdl = (BudgetModel) getSession().get(BudgetModel.class, id);
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
			ls = getSession().createQuery("from BudgetModel").list();
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

	public List getAllActiveBudgets(long off_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetModel where office_id.id=:offic and status=1")
					.setLong("offic", off_id).list();
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

	public List getAllActiveBudgetsUnderActiveBudgetDefinition(long off_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetModel where office_id.id=:offic and status=1 and budgetDef_id.status = 1 ")
					.setLong("offic", off_id).list();
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
	public List getAllActiveBudgetsBudgetDefinitionAndDepartment(long off_id,
			long depId) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetModel where office_id.id=:offic and status=1 and budgetDef_id.status = 1 and budgetDef_id.department in (0,:depid) order by jobName ")
							.setLong("depid", depId).setLong("offic", off_id).list();
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

	public List getAllBudgets(long off_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery("from BudgetModel where office_id.id=:offic")
					.setLong("offic", off_id).list();
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

	public List getActiveBudgetsUnderActiveBudgetDefinition() throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetModel where status=1 and budgetDef_id.status = 1")
					.list();
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

	public void update(BudgetModel mdl) throws Exception {
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

	public void delete(BudgetModel mdl) throws Exception {
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

	public List getAllbudgetUnderBudgtDef(long bdgtdef_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetModel where budgetDef_id.id=:budgtDef and status=1")
					.setLong("budgtDef", bdgtdef_id).list();
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

}
