package com.inventory.sales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.sales.model.QuotationDetailsModel;
import com.inventory.sales.model.QuotationModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class QuotationDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public long save(QuotationModel mdl) throws Exception {
		try {
			begin();
			List<QuotationDetailsModel> itemsList = new ArrayList<QuotationDetailsModel>();
			List<Long> inquiryList=new ArrayList<Long>(); 
			Iterator itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				QuotationDetailsModel det = (QuotationDetailsModel) itr.next();
				
				// Update sales Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update SalesInquiryDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update sales Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update SalesInquiryModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
				
				itemsList.add(det);
			}
			mdl.setQuotation_details_list(itemsList);
			getSession().save(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl.getId();
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getQuotationModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.sales.model.QuotationModel(id,quotation_no) from QuotationModel " +
					"where office.id=:office and active=true order by id DESC").setParameter("office", office).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getSalesInquiryModelCustomerList(long office, long customer, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.SalesInquiryModel(id,concat('Inquiry No: ',inquiry_no,', Inquiry Date: ',cast(date as string),', Approximate Amount: ',amount)) from SalesInquiryModel " +
					"where office.id=:office and active=true and customer.id=:customer "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("customer", customer).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	public QuotationModel getQuotationModel(long id) throws Exception {
		QuotationModel mdl=null;
		try {
			begin();
			mdl=(QuotationModel)getSession().get(QuotationModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	public QuotationDetailsModel getQuotationDetailsModel(long id) throws Exception {
		QuotationDetailsModel mdl=null;
		try {
			begin();
			mdl=(QuotationDetailsModel)getSession().get(QuotationDetailsModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(QuotationModel mdl) throws Exception {
		try {
			begin();
			List oldList=getSession().createQuery("select b from QuotationModel a join a.quotation_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			List oldIdList=new ArrayList();
			List<Long> inquiryList=new ArrayList<Long>();
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				QuotationDetailsModel det = (QuotationDetailsModel) itr.next();
				
				// Update sales Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update SalesInquiryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update sales Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update SalesInquiryModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
				oldIdList.add(det.getId());
			}
			
			List<QuotationDetailsModel> itemsList = new ArrayList<QuotationDetailsModel>();
			List<Long> newInquiryList=new ArrayList<Long>();
			
			itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				QuotationDetailsModel det = (QuotationDetailsModel) itr.next();
				
				// Update sales Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update SalesInquiryDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update sales Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!newInquiryList.contains(det.getInquiry_id())) {
						newInquiryList.add(det.getInquiry_id());
						getSession().createQuery("update SalesInquiryModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
				
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				itemsList.add(det);
			}
			mdl.setQuotation_details_list(itemsList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			if(oldIdList!=null && oldIdList.size()>0){
				getSession().createQuery("delete from QuotationDetailsModel where id in (:list)")
							.setParameterList("list", (Collection)oldIdList).executeUpdate();
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void delete(QuotationModel mdl) throws Exception {
		try {
			begin();
			List<Long> inquiryList=new ArrayList<Long>();
			Iterator itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				QuotationDetailsModel det = (QuotationDetailsModel) itr.next();
				
				// Update sales Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update SalesInquiryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update sales Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update SalesInquiryModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
			}
			getSession().delete(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void cancel(QuotationModel mdl) throws Exception {
		try {
			begin();
			List<Long> inquiryList=new ArrayList<Long>();
			Iterator itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				QuotationDetailsModel det = (QuotationDetailsModel) itr.next();
				
				// Update sales Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update SalesInquiryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update sales Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update SalesInquiryModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
			}
			getSession().createQuery("update QuotationModel set active=false where id="+mdl.getId()).executeUpdate();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}

	
	@SuppressWarnings("rawtypes")
	public List getAllDataFromSalesInquiry(Set<Long> salesInquiries) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.bean.SalesQuotationBean(a.id, b) from SalesInquiryModel a " +
								"join a.sales_inquiry_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", salesInquiries).list();
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
		return list;
	}
	
	
}
