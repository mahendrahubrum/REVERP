package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.rent.model.RentDetailsModel;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 24, 2014
 */
public class ItemwiseRentReportDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2230965879537500099L;

private List resultList = new ArrayList();
	
	public RentDetailsModel getRentitemReport(long org_id, long off_id, long customer_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
		RentDetailsModel mdl=null;
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
//			resultList = getSession().createQuery("SELECT a.customer.id, a.date, a.rent_number, a.amount," +
//					"b.quantity_in_basic_unit, b.period, b.item.current_balalnce," +
//					" b.item.opening_balance, b.item.rent_period, b.item.unit.name  from RentDetailsModel a join " +
//					"a.inventory_details_list b where " +condition+ "date between :fdate and :tdate")
//					  .setParameter("fdate", from_date)
//					  .setParameter("tdate", to_date)
//					.list();
//			
//			SELECT a.au_lname, a.au_fname, t.title
//			FROM authors a INNER JOIN titleauthor ta
//			   ON a.au_id = ta.au_id JOIN titles t
//			   ON ta.title_id = t.title_id
//			WHERE t.type = 'trad_cook'
			
			mdl = (RentDetailsModel) getSession().createQuery("from RentDetailsModel " +
					
					
					
							"where " +condition+ "date between :fdate and :tdate")
					  .setParameter("fdate", from_date)
					  .setParameter("tdate", to_date);
					
			        
					commit();
				} catch (Exception e) {
					rollback();
					close();
					e.printStackTrace();
					throw e;
				} finally {
					flush();
					close();
					return mdl;
				}
}
	
}
