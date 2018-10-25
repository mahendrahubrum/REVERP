package com.inventory.finance.dao;

import java.util.List;

import com.inventory.finance.model.FinanceComponentModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 29, 2014
 */
public class FinanceComponentDao  extends SHibernate{

	private static final long serialVersionUID = 6835629216075566502L;

	public long save(FinanceComponentModel componentModel) throws Exception {
		try {
			begin();
			getSession().save(componentModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return componentModel.getId();
		
	}

	public FinanceComponentModel getComponentModel(long id) throws Exception {
		FinanceComponentModel model = null;
		try {
			begin();
			model = (FinanceComponentModel) getSession().get(
					FinanceComponentModel.class, id);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return model;
	}

	public void update(FinanceComponentModel componentModel) throws Exception {
		try {
			begin();
			getSession().update(componentModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		
	}

	public void delete(long id) throws Exception {
		try {
			begin();
			FinanceComponentModel cmp=(FinanceComponentModel) getSession().get(FinanceComponentModel.class, id); 
			getSession().delete(cmp);
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
	
	public List getAllComponents(long officeId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"Select new com.inventory.finance.model.FinanceComponentModel(id, name)"
									+ " from FinanceComponentModel where officeId=:ofc order by name").setParameter("ofc", officeId).list();
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

	public List getAllActiveComponents(long officeId) throws Exception {
		List list = null;
		try {
			
			String condition="";
			if(officeId>0)
				condition=" and officeId="+officeId;
			
			begin();
			list = getSession()
					.createQuery(
							"Select new com.inventory.finance.model.FinanceComponentModel(id, name)"
									+ " from FinanceComponentModel where status=:sts "+condition+" order by name").setParameter("sts", SConstants.statuses.FINANCE_COMPONENT_ACTIVE).list();
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

	public double getCurrentBalance(Long id) throws Exception {
		double balance=0;
		try {
			
			begin();
			balance = (Double) getSession()
					.createQuery(
							"select current_balance"
									+ " from FinanceComponentModel where id=:id").setParameter("id",id).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return balance;
	}

}
