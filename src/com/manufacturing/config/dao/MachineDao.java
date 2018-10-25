package com.manufacturing.config.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.manufacturing.config.model.MachineModel;
import com.webspark.dao.SHibernate;
/**
 * 
 * @author Muhammed shah
 *
 */



@SuppressWarnings("serial")
public class MachineDao extends SHibernate implements Serializable{

	public long save(MachineModel model) throws Exception {
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

	public MachineModel getMachineModel(long id) throws Exception {
		MachineModel model = null;
		try {
			begin();
			model = (MachineModel) getSession().get(
					MachineModel.class, id);
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

	
	public void update(MachineModel componentModel) throws Exception {
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
			MachineModel model = (MachineModel) getSession().get(
					MachineModel.class, id);
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
	public List getAllMachineList(long officeId) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("SELECT new com.manufacturing.config.model.MachineModel(id, machineName)" +
								" FROM MachineModel WHERE office.id = :officeid ORDER BY machineName")
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
	public List getMachineListByStatus(long officeId,int status) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("SELECT new com.manufacturing.config.model.MachineModel(id, machineName)" +
					"FROM MachineModel WHERE status = :status AND office.id = :officeid order by machineName")
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
