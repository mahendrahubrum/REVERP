package com.inventory.budget.dao;

import java.util.List;

import com.inventory.budget.model.BudgetLVMasterModel;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 25, 2014
 */
public class BudgetLVDao extends SHibernate {
	public long save(BudgetLVMasterModel mdl) throws Exception {
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

	public void update(BudgetLVMasterModel mdl) throws Exception {
		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select id from BudgetLVMasterModel where budget_id.id=:budgetid")
					.setParameter("budgetid", mdl.getBudget_id().getId())
					.uniqueResult();

			if (obj != null) {
				mdl.setId((Long) obj);
				getSession().update(mdl);
			} else {
				getSession().save(mdl);
			}
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

	public void delete(BudgetLVMasterModel mdl) throws Exception {
		try {
			begin();
			getSession().delete(mdl);
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

	public List getAllActiveBudgets(long of_id) throws Exception {
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

	public List getbudgetMasterList(long budget_id) throws Exception {
		List ls = null;
		try {
			begin();
			ls = getSession()
					.createQuery(
							"from BudgetLVMasterModel where budget_id.id=:bud")
					.setLong("bud", budget_id).list();
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

	public BudgetLVMasterModel getBudgetLVMasterModel(long id) throws Exception {
		BudgetLVMasterModel mdl = null;
		try {
			begin();
			mdl = (BudgetLVMasterModel) getSession().get(
					BudgetLVMasterModel.class, id);
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

}
