package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.ItemWiseReportBean;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ItemWiseReportDao extends SHibernate implements Serializable{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemwisePurchaseInquiryDetails(long officeId,Date fromDate, Date toDate, long suppliertId, long itemId, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			StringBuffer query = new StringBuffer();
			HashMap<Long, Double> totalItemHashMap = new HashMap<Long, Double>();
			query.append("SELECT new com.inventory.reports.bean.ItemWiseReportBean(a.id," +
					"a.date," +
					"a.supplier.name," +
					"b.item.id," +
					"b.item.name," +
					"b.qty_in_basic_unit," +
					"b.unit.symbol)" +
					
					" FROM PurchaseInquiryModel a JOIN a.inquiry_details_list b"
					+ " WHERE a.date BETWEEN :fromDate AND :toDate" +
					" AND a.active=true " +
					" AND a.office.id = :office_id");
			if(itemId != 0){
				query.append(" AND b.item.id = "+itemId);
			}
			if(suppliertId != 0){
				query.append(" AND a.supplier.id = "+suppliertId);
			}
			query.append(condition	+ " ORDER BY a.date,b.item.name,a.supplier.name");
			list = getSession()
					.createQuery(query.toString()	)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("office_id", officeId).list();
			Iterator<ItemWiseReportBean> itr = list.iterator();
		
			while(itr.hasNext()){
				ItemWiseReportBean bean = itr.next();
				if(totalItemHashMap.containsKey(bean.getItemId())){
					totalItemHashMap.put(bean.getItemId(), totalItemHashMap.get(bean.getItemId()) + bean.getQuantity());					
				} else {
					totalItemHashMap.put(bean.getItemId(), bean.getQuantity());
				}
				bean.setTotal(totalItemHashMap.get(bean.getItemId()));
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemwisePurchaseQuotationDetails(long officeId,Date fromDate, Date toDate, long suppliertId, long itemId, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			StringBuffer query = new StringBuffer();
			HashMap<Long, Double> totalItemHashMap = new HashMap<Long, Double>();
			query.append("SELECT new com.inventory.reports.bean.ItemWiseReportBean(a.id," +
					"a.date," +
					"a.supplier.name," +
					"b.item.id," +
					"b.item.name," +
					"b.qty_in_basic_unit," +
					"b.unit.symbol)" +
					
					" FROM PurchaseQuotationModel a JOIN a.quotation_details_list b"
					+ " WHERE a.date BETWEEN :fromDate AND :toDate" +
					" AND a.active=true " +
					" AND a.office.id = :office_id");
			if(itemId != 0){
				query.append(" AND b.item.id = "+itemId);
			}
			if(suppliertId != 0){
				query.append(" AND a.supplier.id = "+suppliertId);
			}
			query.append(condition	+ " ORDER BY a.date,b.item.name,a.supplier.name");
			list = getSession()
					.createQuery(query.toString()	)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("office_id", officeId).list();
			Iterator<ItemWiseReportBean> itr = list.iterator();
		
			while(itr.hasNext()){
				ItemWiseReportBean bean = itr.next();
				if(totalItemHashMap.containsKey(bean.getItemId())){
					totalItemHashMap.put(bean.getItemId(), totalItemHashMap.get(bean.getItemId()) + bean.getQuantity());					
				} else {
					totalItemHashMap.put(bean.getItemId(), bean.getQuantity());
				}
				bean.setTotal(totalItemHashMap.get(bean.getItemId()));
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemwisePurchaseGRNDetails(long officeId,Date fromDate, Date toDate, long suppliertId, long itemId, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			StringBuffer query = new StringBuffer();
			HashMap<Long, Double> totalItemHashMap = new HashMap<Long, Double>();
			query.append("SELECT new com.inventory.reports.bean.ItemWiseReportBean(a.id," +
					"a.date," +
					"a.supplier.name," +
					"b.item.id," +
					"b.item.name," +
					"b.qty_in_basic_unit," +
					"b.unit.symbol)" +
					
					" FROM PurchaseGRNModel a JOIN a.grn_details_list b"
					+ " WHERE a.date BETWEEN :fromDate AND :toDate" +
					" AND a.active=true " +
					" AND a.office.id = :office_id");
			if(itemId != 0){
				query.append(" AND b.item.id = "+itemId);
			}
			if(suppliertId != 0){
				query.append(" AND a.supplier.id = "+suppliertId);
			}
			query.append(condition	+ " ORDER BY a.date,b.item.name,a.supplier.name");
			list = getSession()
					.createQuery(query.toString()	)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("office_id", officeId).list();
			Iterator<ItemWiseReportBean> itr = list.iterator();
		
			while(itr.hasNext()){
				ItemWiseReportBean bean = itr.next();
				if(totalItemHashMap.containsKey(bean.getItemId())){
					totalItemHashMap.put(bean.getItemId(), totalItemHashMap.get(bean.getItemId()) + bean.getQuantity());					
				} else {
					totalItemHashMap.put(bean.getItemId(), bean.getQuantity());
				}
				bean.setTotal(totalItemHashMap.get(bean.getItemId()));
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemwiseSalesInquiryDetails(long officeId,Date fromDate, Date toDate, long customerId, long itemId, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			StringBuffer query = new StringBuffer();
			HashMap<Long, Double> totalItemHashMap = new HashMap<Long, Double>();
			query.append("SELECT new com.inventory.reports.bean.ItemWiseReportBean(a.id," +
					"a.date," +
					"a.customer.name," +
					"b.item.id," +
					"b.item.name," +
					"b.qty_in_basic_unit," +
					"b.unit.symbol)" +
					
					" FROM SalesInquiryModel a JOIN a.sales_inquiry_details_list b"
					+ " WHERE a.date BETWEEN :fromDate AND :toDate" +
					" AND a.active=true " +
					" AND a.office.id = :office_id");
			if(itemId != 0){
				query.append(" AND b.item.id = "+itemId);
			}
			if(customerId != 0){
				query.append(" AND a.customer.id = "+customerId);
			}
			query.append(condition	+ " ORDER BY a.date,b.item.name,a.customer.name");
			list = getSession()
					.createQuery(query.toString()	)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("office_id", officeId).list();
			Iterator<ItemWiseReportBean> itr = list.iterator();
		
			while(itr.hasNext()){
				ItemWiseReportBean bean = itr.next();
				if(totalItemHashMap.containsKey(bean.getItemId())){
					totalItemHashMap.put(bean.getItemId(), totalItemHashMap.get(bean.getItemId()) + bean.getQuantity());					
				} else {
					totalItemHashMap.put(bean.getItemId(), bean.getQuantity());
				}
				bean.setTotal(totalItemHashMap.get(bean.getItemId()));
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemwiseDeliveryNoteDetails(long officeId,Date fromDate, Date toDate, long customerId, long itemId, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			StringBuffer query = new StringBuffer();
			HashMap<Long, Double> totalItemHashMap = new HashMap<Long, Double>();
			query.append("SELECT new com.inventory.reports.bean.ItemWiseReportBean(a.id," +
					"a.date," +
					"a.customer.name," +
					"b.item.id," +
					"b.item.name," +
					"b.qty_in_basic_unit," +
					"b.unit.symbol)" +
					
					" FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b"
					+ " WHERE a.date BETWEEN :fromDate AND :toDate" +
					" AND a.active=true " +
					" AND a.office.id = :office_id");
			if(itemId != 0){
				query.append(" AND b.item.id = "+itemId);
			}
			if(customerId != 0){
				query.append(" AND a.customer.id = "+customerId);
			}
			query.append(condition	+ " ORDER BY a.date,b.item.name,a.customer.name");
			list = getSession()
					.createQuery(query.toString()	)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("office_id", officeId).list();
			Iterator<ItemWiseReportBean> itr = list.iterator();
		
			while(itr.hasNext()){
				ItemWiseReportBean bean = itr.next();
				if(totalItemHashMap.containsKey(bean.getItemId())){
					totalItemHashMap.put(bean.getItemId(), totalItemHashMap.get(bean.getItemId()) + bean.getQuantity());					
				} else {
					totalItemHashMap.put(bean.getItemId(), bean.getQuantity());
				}
				bean.setTotal(totalItemHashMap.get(bean.getItemId()));
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemwiseQuotationDetails(long officeId,Date fromDate, Date toDate, long customerId, long itemId, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			StringBuffer query = new StringBuffer();
			HashMap<Long, Double> totalItemHashMap = new HashMap<Long, Double>();
			query.append("SELECT new com.inventory.reports.bean.ItemWiseReportBean(a.id," +
					"a.date," +
					"a.customer.name," +
					"b.item.id," +
					"b.item.name," +
					"b.qty_in_basic_unit," +
					"b.unit.symbol)" +
					
					" FROM QuotationModel a JOIN a.quotation_details_list b"
					+ " WHERE a.date BETWEEN :fromDate AND :toDate" +
					" AND a.active=true " +
					" AND a.office.id = :office_id");
			if(itemId != 0){
				query.append(" AND b.item.id = "+itemId);
			}
			if(customerId != 0){
				query.append(" AND a.customer.id = "+customerId);
			}
			query.append(condition	+ " ORDER BY a.date,b.item.name,a.customer.name");
			list = getSession()
					.createQuery(query.toString()	)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("office_id", officeId).list();
			Iterator<ItemWiseReportBean> itr = list.iterator();
		
			while(itr.hasNext()){
				ItemWiseReportBean bean = itr.next();
				if(totalItemHashMap.containsKey(bean.getItemId())){
					totalItemHashMap.put(bean.getItemId(), totalItemHashMap.get(bean.getItemId()) + bean.getQuantity());					
				} else {
					totalItemHashMap.put(bean.getItemId(), bean.getQuantity());
				}
				bean.setTotal(totalItemHashMap.get(bean.getItemId()));
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
}
