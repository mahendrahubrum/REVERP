package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 5, 2013
 */
public class PayrollEmployeeMapDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1461244562295948504L;
	private static final int NO_SALARY = 0;

	public List getEmployees(long org) throws Exception {
		List resultList = null;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) from UserModel "
									+ "where "
									+ "loginId.userType.active='Y' and loginId.office.organization.id=:ofc and salary_type!="
									+ NO_SALARY + "order by first_name")
					.setLong("ofc", org).list();
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
		return resultList;
	}

	
	@SuppressWarnings("rawtypes")
	public void save(List list) throws Exception {

		try {
			begin();
			if(list!=null){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					PayrollEmployeeMapModel model = (PayrollEmployeeMapModel)itr.next();
					getSession().save(model);
				}
			}
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

	
	@SuppressWarnings("rawtypes")
	public List getPayRollMap(long empId, long office) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("from PayrollEmployeeMapModel where employee.id=:emp and employee.office.id=:office order by component.name")
									.setParameter("emp", empId).setParameter("office", office).list();
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
		return resultList;
	}

	
	@SuppressWarnings("rawtypes")
	public void update(List list, long office, long employee) throws Exception {
		try {
			begin();
			List deleteList=new ArrayList();
			deleteList=getSession().createQuery("from PayrollEmployeeMapModel where employee.id=:employee and employee.office.id=:office")
					.setParameter("office", office).setParameter("employee", employee).list();
			if(deleteList.size()>0){
				Iterator itr=deleteList.iterator();
				while (itr.hasNext()) {
					PayrollEmployeeMapModel model = (PayrollEmployeeMapModel)itr.next();
					getSession().delete(model);
				}
			}
			flush();
			if(list!=null){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					PayrollEmployeeMapModel model = (PayrollEmployeeMapModel)itr.next();
					getSession().save(model);
				}
			}
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

	
	@SuppressWarnings("rawtypes")
	public void delete(long office, long employee) throws Exception {
		try {
			begin();
			List deleteList=new ArrayList();
			deleteList=getSession().createQuery("from PayrollEmployeeMapModel where employee.id=:employee and employee.office.id=:office")
					.setParameter("office", office).setParameter("employee", employee).list();
			if(deleteList.size()>0){
				Iterator itr=deleteList.iterator();
				while (itr.hasNext()) {
					PayrollEmployeeMapModel model = (PayrollEmployeeMapModel)itr.next();
					getSession().delete(model);
				}
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

}
