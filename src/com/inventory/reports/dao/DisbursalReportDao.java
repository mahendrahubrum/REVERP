package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

public class DisbursalReportDao extends SHibernate {
	
	private List resultList=new ArrayList();
	
	public List getDisbursal(Date start_date, Date end_date, long office_id, long empId, long orgId) throws Exception {
		
		try {
			
			String qry="from SalaryDisbursalModel where dispursal_date between '"+start_date+"' and '"+end_date+"'";
			resultList=new ArrayList();
			
			begin();
			
			if(empId!=0) {
				qry+=" and user.id="+empId;
			}
			else if(office_id!=0) {
				qry+=" and user.loginId.office.id="+office_id;
			}
			else {
				qry+=" and user.loginId.office.organization.id="+orgId;
			}
			
			qry+=" order by dispursal_date,user.first_name";
			
			resultList = getSession().createQuery(qry).list();
			
			commit();
			
			return resultList;
			
		} catch (Exception e) {
			// TODO: handle exception
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	
	
	
	public List getCommissionSalaryDisbursal(Date start_date, Date end_date, long office_id, long empId) throws Exception {
		
		try {
			
			String qry="from CommissionSalaryModel where date between '"+start_date+"' and '"+end_date+"'";
			resultList=new ArrayList();
			
			begin();
			
			if(empId!=0) {
				qry+=" and employee.id="+empId;
			}
			else if(office_id!=0) {
				qry+=" and office.id="+office_id;
			}
			
			qry+=" order by date,employee.first_name";
			
			resultList = getSession().createQuery(qry).list();
			
			commit();
			
			return resultList;
			
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	

	
	public double getTotalAdvancePayment(Date start_date, Date end_date, long office_id, long empId, long orgId) throws Exception {
		
		try {
			double advance=0;
			String qry="select coalesce(sum(amount),0) from EmployeeAdvancePaymentModel where date between '"+start_date+"' and '"+end_date+"'";
			resultList=new ArrayList();
			
			begin();
			
			if(empId!=0) {
				qry+=" and login_id="+empId;
			}
			else if(office_id!=0) {
				qry+=" and office.id="+office_id;
			}
			else {
				qry+=" and office.organization.id="+orgId;
			}
			
			qry+=" order by date";
			
			Object obj = getSession().createQuery(qry).uniqueResult();
			if(obj!=null)
				advance=(Double)obj;
			commit();
			
			return advance;
			
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
		}
		
		
	}
	
	
}
