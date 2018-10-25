package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Apr 28, 2014
 */
public class BudgetReportDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -1678539174481931571L;

	private List resultList = new ArrayList();
	
	public List getBudgetReport(long org_id, long off_id, long budgetdef_id, long budget_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office_id.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office_id.id=" +off_id+ " and ";
		}
		if(budgetdef_id !=0){
			condition += "budget_id.budgetDef_id.id=" +budgetdef_id+ " and ";
		}
		if(budget_id !=0){
			condition += "budget_id.id=" +budget_id+ " and ";
		}
		
try {
			
			begin();
			resultList = getSession().createQuery("from BudgetLVMasterModel where " +condition+ "date between :fdate and :tdate")
					  .setParameter("fdate", from_date)
					  .setParameter("tdate", to_date)
					.list();
			        
					commit();
				} catch (Exception e) {
					rollback();
					close();
					e.printStackTrace();
					throw e;
				} finally {
					flush();
					close();
					return resultList;
				}
}
}
