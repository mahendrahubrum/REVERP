package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.inventory.payroll.model.EmployeeWorkingTimeModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 6, 2013
 */
public class EmployeeWorkingTimeDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5665517232067048837L;

	public List getEmployees(long orgId) throws Exception {

		List list = null;
		try {
			begin();
			list = getSession().createQuery("from UserModel where office.organization.id=:ofc and salary_type!=0 and user_role.id!=1 order by first_name")
					.setParameter("ofc", orgId).list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	public List getEmployeesUnderOffice(long officeId) throws Exception {
		
		List list = null;
		try {
			begin();
			list = getSession().createQuery("from UserModel where office.id=:ofc and salary_type!=0 and user_role.id!=1 order by first_name")
									.setParameter("ofc", officeId).list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}

	public void save(Vector<EmployeeWorkingTimeModel> modelVector)
			throws Exception {
		try {
			begin();

			EmployeeWorkingTimeModel model;
			EmployeeWorkingTimeModel deleteTimeModel;
			for (int i = 0; i < modelVector.size(); i++) {
				
				model =modelVector.get(i);
				deleteTimeModel = (EmployeeWorkingTimeModel) getSession()
						.createQuery(
								"from EmployeeWorkingTimeModel where month=:mont and employee.id=:id")
						.setParameter("mont", model.getMonth())
						.setParameter("id", model.getEmployee().getId())
						.uniqueResult();
				if (deleteTimeModel != null) {
					getSession().delete(deleteTimeModel);
				}
				getSession().save(model);
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

	}

	public double getWorkingTime(java.sql.Date month, long empId)
			throws Exception {
		double time = 0;
		try {
			begin();

			Object obj = getSession()
					.createQuery(
							"select working_time from EmployeeWorkingTimeModel where month=:mont and employee.id=:id")
					.setParameter("mont", month).setParameter("id", empId)
					.uniqueResult();
			commit();

			if (obj != null)
				time = (Double) obj;
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return time;
	}

	public boolean isPresent(java.sql.Date month, long id) throws Exception {
		boolean present = false;
		try {
			begin();

			List list = getSession()
					.createQuery(
							"from EmployeeWorkingTimeModel where month=:mont and employee.id=:id")
					.setParameter("mont", month).setParameter("id", id).list();
			commit();

			if (list != null && list.size() > 0)
				present = true;
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return present;
	}
	
	public List getEmployeesWithLoginID(long orgId) throws Exception {

		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(loginId.id,first_name)  from UserModel where loginId.office.organization.id=:ofc and salary_type!=0 "
									+" order by first_name")
					.setParameter("ofc", orgId).list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	public List getEmployeesUnderOfficeWithLoginID(long officeId) throws Exception {
		
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(loginId.id,first_name) from UserModel where loginId.office.id=:ofc and salary_type!=0 "
									+" order by first_name")
									.setParameter("ofc", officeId).list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
}