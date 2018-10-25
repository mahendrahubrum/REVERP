package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 * WebSpark.
 *
 * Jan 23 2014
 */
public class ActivityLogReportDao extends SHibernate implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1071364669984964064L;
	
	List empUnderList=new ArrayList();
	

	public List getActivityLogs(long emp_id, Timestamp frmDt, Timestamp toDt, long ofc_id, long login_id,boolean isAdmin) throws Exception {
		List lst=null;
		try {
			empUnderList=new ArrayList();
			
			begin();
			
			if(emp_id!=0) {
				lst=getSession().createQuery("select new com.webspark.bean.ReportBean((select b.first_name from UserModel b where b.loginId.id=a.login),a.log,a.date)" +
						" from ActivityLogModel a where a.date between :stdt and :enddt and a.office_id=:ofc and a.login="+emp_id)
								.setParameter("stdt", frmDt).setLong("ofc", ofc_id).setParameter("enddt", toDt).list();
			}
			else {
				if(!isAdmin)
					lst=getSession().createQuery("select new com.webspark.bean.ReportBean((select b.first_name from UserModel b where b.loginId.id=a.login),a.log,a.date) from ActivityLogModel a where a.date between :stdt and :enddt and a.office_id=:ofc and a.login in (:lgns)")
						.setParameter("stdt", frmDt).setLong("ofc", ofc_id).setParameterList("lgns", getEmployeesIDsUnderUser(ofc_id, login_id)).setParameter("enddt", toDt).list();
				else
					lst=getSession().createQuery("select new com.webspark.bean.ReportBean((select b.first_name from UserModel b where b.loginId.id=a.login),a.log,a.date) from ActivityLogModel a where a.date between :stdt and :enddt and a.office_id=:ofc ")
						.setParameter("stdt", frmDt).setLong("ofc", ofc_id).setParameter("enddt", toDt).list();
		
			} 
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return lst;
		}
	}
	
	
	public List getEmployeesIDsUnderUser (long office_id, long login_id) throws Exception {
		try {
			
			List lst=getSession().createQuery("select loginId.id from UserModel "
					+ "where loginId.office.id=:ofc and loginId.userType.id>1 and superior_id=:sup")
					.setLong("sup", login_id).setLong("ofc", office_id).list();
			
			empUnderList.add(login_id);
			if(lst!=null && lst.size()>0) {
				Iterator it1 = lst.iterator();
				while (it1.hasNext()) {
					getEmployeesIDsUnderUser(office_id, (Long)it1.next());
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			return empUnderList;
		}
	}
	
}
