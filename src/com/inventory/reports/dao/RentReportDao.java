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
 * May 15, 2014
 */
public class RentReportDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4813123106669850615L;

private List resultList = new ArrayList();
	
	public List getRentReport(long org_id, long off_id, long employee_id, int payment_type, long customer_id, long rent_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(employee_id !=0){
			condition += "responsible_person="  +employee_id+ " and "; 
		}
		if(customer_id !=0){
			condition += "customer.id=" +customer_id+ " and ";
		}
		if(rent_id !=0){
			condition += "id=" +rent_id+ " and ";
		}
		if(payment_type !=0){
			condition += "cash_credit_sale=" +payment_type+ " and ";
		}
		
try {
			
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where " +condition+ "date between :fdate and :tdate")
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
	
	public List getRentItemReportwithReturn(long org_id, long off_id, long customer_id, long rent_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(customer_id !=0){
			condition += "customer.id=" +customer_id+ " and ";
		}
		if(rent_id !=0){
			condition += "id=" +rent_id+ " and ";
		}
		
		
try {
			
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where " +condition+ "date between :fdate and :tdate")
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
	
	public List getRentItemReport(long org_id, long off_id, long customer_id,Date from_date, Date to_date, long item_id) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(customer_id !=0){
			condition += "customer.id=" +customer_id+ " and ";
		}
		
		
try {
			
			begin();
			resultList = getSession().createQuery("from RentDetailsModel mm where " +condition+ "date between :fdate and :tdate and mm.inventory_details_list.item.id :=itemid" )
					.setParameter("fdate", from_date)
					  .setParameter("tdate", to_date)
					.setParameter("itemid", item_id)  
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
