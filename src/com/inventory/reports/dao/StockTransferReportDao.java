package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import com.inventory.config.stock.model.StockTransferModel;
import com.webspark.dao.SHibernate;

public class StockTransferReportDao extends SHibernate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6785184606071842507L;
	private List resultList = null;

	@SuppressWarnings("finally")
	public List getStockTransferList(long from_office_id, Date from_date, Date to_date, long stock_transfer_id,
			long to_office_id, long from_location_id, long to_location_id) throws Exception {
		try {
			begin();
			StringBuffer hQueryString =new StringBuffer();
			hQueryString.append(" FROM StockTransferModel" +
					" WHERE transfer_date BETWEEN :from_date AND :to_date")
					.append((from_office_id != 0)?" AND from_office.id = :from_office_id":"")
					.append((stock_transfer_id != 0)?" AND id = :stock_transfer_id":"")
					.append((to_office_id != 0)?" AND to_office.id = :to_office_id":"")
					.append((from_location_id != 0)?" AND from_location.id = :from_location_id":"")
					.append((to_location_id != 0)?" AND to_location.id = :to_location_id":"");
			
			Query  query= getSession()
					.createQuery(hQueryString.toString())
					.setDate("from_date", from_date)
					.setDate("to_date", to_date);
			if(from_office_id != 0){
				query.setLong("from_office_id", from_office_id);
			}
			if(to_office_id != 0){
				query.setLong("to_office_id", to_office_id);
			}
			if(from_location_id != 0){
				query.setLong("from_location_id", from_location_id);
			}
			if(to_location_id != 0){
				query.setLong("to_location_id", to_location_id);
			}
			if(stock_transfer_id != 0){
				query.setLong("stock_transfer_id",stock_transfer_id);
			}		
			resultList = query.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	
	@SuppressWarnings("finally")
	public List<StockTransferModel> getAllStockTransferNumbersAsComment(
			long office_id) throws Exception {

		try {
			begin();
			StringBuffer queryStringBuffer = new StringBuffer();
			queryStringBuffer.append("SELECT new com.inventory.config.stock.model.StockTransferModel(id,cast(transfer_no as string) )"
									+ " FROM StockTransferModel")
									.append((office_id != 0)?" WHERE from_office.id=:office_id" : "");
			Query query = getSession().createQuery(queryStringBuffer.toString());
			if(office_id != 0){
				query.setParameter("office_id", office_id);
			}					
			resultList = query.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getLocationModelList(long office_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			StringBuffer queryStringBuffer = new StringBuffer();
			queryStringBuffer.append("SELECT new com.inventory.model.LocationModel(id, name)"
									+ " FROM LocationModel")
									.append((office_id != 0)?" WHERE office.id=:office_id" : "");
			Query query = getSession().createQuery(queryStringBuffer.toString());
			if(office_id != 0){
				query.setParameter("office_id", office_id);
			}					
			resultList = query.list();
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
}
