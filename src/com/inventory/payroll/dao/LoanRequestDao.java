package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.payroll.model.LoanRequestModel;
import com.webspark.dao.SHibernate;
/**
 * 
 * @author Muhammed shah
 *
 */

/**
 * 
 * @author sangeeth
 * @date 23-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class LoanRequestDao extends SHibernate implements Serializable{

	public long save(LoanRequestModel model) throws Exception {
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

	public LoanRequestModel getLoanRequestModel(long id) throws Exception {
		LoanRequestModel model = null;
		try {
			begin();
			model = (LoanRequestModel) getSession().get(
					LoanRequestModel.class, id);
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

	
	public void update(LoanRequestModel componentModel) throws Exception {
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
			LoanRequestModel model = (LoanRequestModel) getSession().get(
					LoanRequestModel.class, id);
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
	public List getAllLoanRequestList(long officeId) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("Select new com.inventory.payroll.model.LoanRequestModel(id, requestNo)" +
								" from LoanRequestModel where user.office.id = :officeid order by requestNo")
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
	public List getLoanRequestModelList(long officeId,short status) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.payroll.model.LoanRequestModel(id, requestNo) " +
					"from LoanRequestModel where status = :status AND user.office.id = :officeid order by requestNo")
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
	
	
	public LoanRequestModel getLoanRequestModelByRequestId(long id) throws Exception {
		LoanRequestModel model = null;
		try {
			begin();
			model = (LoanRequestModel) getSession()
					.createQuery(" from LoanRequestModel where id = :id")
					.setParameter("id", id).uniqueResult();
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
	
	
	public LoanRequestModel getLoanRequestModelByRequestIdAndStatus(long id,short status) throws Exception {
		LoanRequestModel model = null;
		try {
			begin();
			model = (LoanRequestModel) getSession()
					.createQuery(" from LoanRequestModel where id = :id AND status = :status")
					.setParameter("id", id)
					.setParameter("status", status).uniqueResult();
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

}
