/**
 * 
 */
package com.inventory.reports.dao;

import java.sql.Date;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author anil
 *
 * 10-Nov-2016
 *
 * WebSpark
 */
public class SalarySlipDao extends SHibernate{
	
	public List getSalarySlip(long employeeId,Date frmDate,Date toDate) throws Exception {
		List list=null;
		try {
			begin();
			//Constructor 58
			list=getSession().createQuery("select new com.webspark.bean.ReportBean(b.component.office.currency.code,b.component.name,b.amount)" +
					" from SalaryDisbursalNewModel a join a.detailsList b where a.employ.id=:emp and a.month between :frm and :to order by b.component.id")
						.setParameter("emp", employeeId).setParameter("frm",frmDate).setParameter("to",toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
		return list;
	}
	

}
