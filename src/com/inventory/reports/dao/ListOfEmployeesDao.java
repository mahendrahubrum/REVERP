package com.inventory.reports.dao;

import java.io.Serializable;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 25, 2013
 */
public class ListOfEmployeesDao extends SHibernate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2385486353698059304L;

	public List getAllUsers(long officeId, long departmentId, long desigId, long orgId)
			throws Exception {
		List resultList = null;
		try {
			begin();

			String condition="";
			if(officeId!=0)
			condition=" and loginId.office.id="+officeId;
			
			if(departmentId!=0)
				condition=" and department.id="+departmentId;
			
			if(desigId!=0)
				condition=" and designation.id="+desigId;
			
			resultList = getSession()
					.createQuery(" from UserModel where loginId.office.organization.id=:org "+condition+" order by first_name").setParameter("org", orgId).list();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
}
