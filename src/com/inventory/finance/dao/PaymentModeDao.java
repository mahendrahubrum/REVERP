package com.inventory.finance.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.finance.model.PaymentModeModel;
import com.manufacturing.config.model.MouldModel;
import com.webspark.dao.SHibernate;
/**
 * 
 * @author Muhammed shah
 *
 */



@SuppressWarnings("serial")
public class PaymentModeDao extends SHibernate implements Serializable{

	public long save(PaymentModeModel model) throws Exception {
		try {
			begin();
			getSession().save(model);
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
		return model.getId();
	}

	public PaymentModeModel getPaymentModeModel(long id) throws Exception {
		PaymentModeModel model = null;
		try {
			begin();
			model = (PaymentModeModel) getSession().get(
					PaymentModeModel.class, id);
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
		return model;
	}

	
	public void update(PaymentModeModel componentModel) throws Exception {
		try {
			begin();
			getSession().update(componentModel);
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
			PaymentModeModel model = (PaymentModeModel) getSession().get(
					PaymentModeModel.class, id);
			getSession().delete(model);
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
	
	
	@SuppressWarnings("rawtypes")
	public List getAllPaymentModeList(long officeId) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("SELECT new com.inventory.finance.model.PaymentModeModel(id, description)" +
								" FROM PaymentModeModel WHERE office.id = :officeid ORDER BY description")
								.setParameter("officeid", officeId).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getPaymentModeListByStatus(long officeId,int status) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("SELECT new com.inventory.finance.model.PaymentModeModel(id, description)" +
					"FROM PaymentModeModel WHERE status = :status AND office.id = :officeid order by description")
					.setParameter("status", status)
					.setParameter("officeid", officeId).list();
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
	
	@SuppressWarnings("rawtypes")
	public List getPaymentModeDetailsListByStatus(long officeId,int status) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("FROM PaymentModeModel WHERE status = :status AND office.id = :officeid order by description")
					.setParameter("status", status)
					.setParameter("officeid", officeId).list();
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
	
	
//	public LoanRequestModel getLoanRequestModelByRequestId(long id) throws Exception {
//		LoanRequestModel model = null;
//		try {
//			begin();
//			model = (LoanRequestModel) getSession()
//					.createQuery(" from LoanRequestModel where id = :id")
//					.setParameter("id", id).uniqueResult();
//			commit();
//
//		} catch (Exception e) {
//			rollback();
//			close();
//			e.printStackTrace();
//			throw e;
//		} finally {
//			flush();
//			close();
//		}
//		return model;
//	}
//	
//	
//	public LoanRequestModel getLoanRequestModelByRequestIdAndStatus(long id,short status) throws Exception {
//		LoanRequestModel model = null;
//		try {
//			begin();
//			model = (LoanRequestModel) getSession()
//					.createQuery(" from LoanRequestModel where id = :id AND status = :status")
//					.setParameter("id", id)
//					.setParameter("status", status).uniqueResult();
//			commit();
//
//		} catch (Exception e) {
//			rollback();
//			close();
//			e.printStackTrace();
//			throw e;
//		} finally {
//			flush();
//			close();
//		}
//		return model;
//	}

}
