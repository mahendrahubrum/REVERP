package com.manufacturing.config.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.manufacturing.config.model.MouldModel;
import com.webspark.dao.SHibernate;
/**
 * 
 * @author Muhammed shah
 *
 */



@SuppressWarnings("serial")
public class MouldDao extends SHibernate implements Serializable{

	public long save(MouldModel model) throws Exception {
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

	public MouldModel getMouldModel(long id) throws Exception {
		MouldModel model = null;
		try {
			begin();
			model = (MouldModel) getSession().get(
					MouldModel.class, id);
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

	
	public void update(MouldModel componentModel) throws Exception {
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
			MouldModel model = (MouldModel) getSession().get(
					MouldModel.class, id);
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
	public List getAllMouldList(long officeId) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("SELECT new com.manufacturing.config.model.MouldModel(id, mouldName)" +
								" FROM MouldModel WHERE office.id = :officeid ORDER BY mouldName")
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
	public List getMouldListByStatus(long officeId,int status) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("SELECT new com.manufacturing.config.model.AddMouldModel(id, mouldName)" +
					"FROM MouldModel WHERE status = :status AND office.id = :officeid order by mouldName")
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
