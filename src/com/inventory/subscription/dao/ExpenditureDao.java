package com.inventory.subscription.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.subscription.model.SubscriptionExpenditureModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ExpenditureDao extends SHibernate implements Serializable {
	
	public long save(SubscriptionExpenditureModel mdl) throws Exception {

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
	
	public long update(SubscriptionExpenditureModel mdl) throws Exception {

		try {

			begin();
			getSession().update(mdl);
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
	
	public void delete(SubscriptionExpenditureModel mdl) throws Exception {

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
	
	public SubscriptionExpenditureModel getModel(long id) throws Exception {
		SubscriptionExpenditureModel mdl;
		try {
			begin();
			mdl=(SubscriptionExpenditureModel)getSession().get(SubscriptionExpenditureModel.class, id);
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

	@SuppressWarnings("rawtypes")
	public List getAllExpenditures(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=(List)getSession().createQuery("select new com.inventory.subscription.model.SubscriptionExpenditureModel(id,name) " +
					"from SubscriptionExpenditureModel where office.id=:office").setParameter("office", office).list();
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
		return list;
	}
	
}
