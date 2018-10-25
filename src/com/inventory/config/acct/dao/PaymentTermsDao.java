package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.PaymentTermsModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class PaymentTermsDao extends SHibernate implements Serializable {

	List resultList = new ArrayList();

	public long save(PaymentTermsModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}

	public void update(PaymentTermsModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new PaymentTermsModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		}
		flush();
		close();
	}

	public List getAllActivePaymentTerms(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.PaymentTermsModel(id, name)"
									+ " from PaymentTermsModel where status=:val and organization_id=:org")
					.setParameter("org", org_id).setParameter("val", SConstants.statuses.PAYMENT_TERMS_ACTIVE)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getAllPaymentTerms(long org_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.PaymentTermsModel(id, name)"
									+ " from PaymentTermsModel where organization_id=:org")
					.setParameter("org", org_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	

	public PaymentTermsModel getPaymentTerm(long id) throws Exception {
		PaymentTermsModel mod = null;
		try {
			begin();
			mod = (PaymentTermsModel) getSession().get(PaymentTermsModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}

}
