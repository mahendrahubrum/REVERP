package com.inventory.rent.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.rent.model.RentPaymentModel;
import com.inventory.rent.model.RentReturnItemDetailModel;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * Jun 10, 2014
 */
public class RentItemReturnDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -829160787335028797L;
	
	List resultList = new ArrayList();
	
	public long saveRentItemReturn(RentReturnItemDetailModel itemreturnModel)
	
			throws Exception {
		try {
			begin();
			
			getSession().save(itemreturnModel);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return itemreturnModel.getId();
		}
	}
	
	
	
public long saveRentItemReturnList(RentReturnItemDetailModel itemreturnModel)
	
			throws Exception {
		try {
			begin();
			
			getSession().save(itemreturnModel);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
			return itemreturnModel.getId();
		}
	}



public Long getdaysbetween(Date from_date, Date to_date) throws Exception {
	long days=0;
try {
		
		begin();
		days = (Long) getSession().createQuery("DATEDIFF (day, fdate, tdate) as NumberOfDays")
				  .setParameter("fdate", from_date)
				  .setParameter("tdate", to_date)
				.uniqueResult();
		        
				commit();
			} catch (Exception e) {
				rollback();
				close();
				e.printStackTrace();
				throw e;
			} finally {
				flush();
				close();
				return days;
			}
}

	

}
