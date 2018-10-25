package com.inventory.payroll.dao;

import java.io.Serializable;

import com.inventory.payroll.model.EmployeeStatusModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserModel;

@SuppressWarnings("serial")
public class EmployeeStatusUpdationDao extends SHibernate implements Serializable{
	public long save(EmployeeStatusModel model) throws Exception {
		try {
			begin();
			if(model.getUser().getLoginId()!=null)
				getSession().update(model.getUser().getLoginId());
			getSession().update(model.getUser());
			getSession().save(model);
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
		return model.getUser().getId();
	}
	public void update(EmployeeStatusModel model) throws Exception{
		try {
			if(model.getUser().getLoginId()!=null)
				getSession().update(model.getUser().getLoginId());
			getSession().update(model.getUser());
			getSession().update(model);
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
	
	public EmployeeStatusModel getEmployeeStatusModel(long id) throws Exception {
		EmployeeStatusModel model = null;
		try {
			begin();
			model = (EmployeeStatusModel) getSession().get(
					EmployeeStatusModel.class, id);
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
	
	public void delete(long id) throws Exception {

		try {
			begin();
			EmployeeStatusModel model = (EmployeeStatusModel) getSession().get(
					EmployeeStatusModel.class, id);
			UserModel userModel = model.getUser();
			userModel.setStatus(SConstants.EmployeeStatus.ACTIVE);
			if(userModel.getLoginId()!=null){
				userModel.getLoginId().setStatus(0);
				getSession().update(userModel.getLoginId());
			}
			getSession().update(userModel);
			getSession().delete(model);
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
}
