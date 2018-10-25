package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.purchase.model.PurchaseQuotationDetailsModel;
import com.inventory.purchase.model.PurchaseQuotationModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PurchaseQuotationDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public long save(PurchaseQuotationModel mdl) throws Exception {
		try {
			begin();
			List<PurchaseQuotationDetailsModel> itemsList = new ArrayList<PurchaseQuotationDetailsModel>();
			List<Long> inquiryList=new ArrayList<Long>(); 
			Iterator itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseQuotationDetailsModel det = (PurchaseQuotationDetailsModel) itr.next();
				
				// Update Purchase Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update PurchaseInquiryDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update PurchaseInquiryModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
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
	public List getPurchaseQuotationModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseQuotationModel(id,quotation_no) from PurchaseQuotationModel " +
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
	public List getPurchaseInquiryModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseInquiryModel(id,concat('Inquiry No: ',inquiry_no,', Inquiry Date: ',cast(date as string),', Approximate Amount: ',amount)) from PurchaseInquiryModel " +
					"where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("supplier", supplier).list();
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
	
	
	public PurchaseQuotationModel getPurchaseQuotationModel(long id) throws Exception {
		PurchaseQuotationModel mdl=null;
		try {
			begin();
			mdl=(PurchaseQuotationModel)getSession().get(PurchaseQuotationModel.class, id);
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
	
	
	public PurchaseQuotationDetailsModel getPurchaseQuotationDetailsModel(long id) throws Exception {
		PurchaseQuotationDetailsModel mdl=null;
		try {
			begin();
			mdl=(PurchaseQuotationDetailsModel)getSession().get(PurchaseQuotationDetailsModel.class, id);
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
	public void update(PurchaseQuotationModel mdl) throws Exception {
		try {
			begin();
			List oldList=getSession().createQuery("select b from PurchaseQuotationModel a join a.quotation_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			List oldIdList=new ArrayList();
			List<Long> inquiryList=new ArrayList<Long>();
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				PurchaseQuotationDetailsModel det = (PurchaseQuotationDetailsModel) itr.next();
				
				// Update Purchase Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update PurchaseInquiryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update PurchaseInquiryModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
				oldIdList.add(det.getId());
			}
			
			List<PurchaseQuotationDetailsModel> itemsList = new ArrayList<PurchaseQuotationDetailsModel>();
			List<Long> newInquiryList=new ArrayList<Long>();
			
			itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseQuotationDetailsModel det = (PurchaseQuotationDetailsModel) itr.next();
				
				// Update Purchase Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update PurchaseInquiryDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!newInquiryList.contains(det.getInquiry_id())) {
						newInquiryList.add(det.getInquiry_id());
						getSession().createQuery("update PurchaseInquiryModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
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
				getSession().createQuery("delete from PurchaseQuotationDetailsModel where id in (:list)")
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
	public void delete(PurchaseQuotationModel mdl) throws Exception {
		try {
			begin();
			List<Long> inquiryList=new ArrayList<Long>();
			Iterator itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseQuotationDetailsModel det = (PurchaseQuotationDetailsModel) itr.next();
				
				// Update Purchase Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update PurchaseInquiryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update PurchaseInquiryModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
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
	public void cancel(PurchaseQuotationModel mdl) throws Exception {
		try {
			begin();
			List<Long> inquiryList=new ArrayList<Long>();
			Iterator itr=mdl.getQuotation_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseQuotationDetailsModel det = (PurchaseQuotationDetailsModel) itr.next();
				
				// Update Purchase Inquiry Child
				if(det.getInquiry_child_id()!=0) {
					getSession().createQuery("update PurchaseInquiryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getInquiry_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Inquiry Parent
				if(det.getInquiry_id()!=0){
					if(!inquiryList.contains(det.getInquiry_id())) {
						inquiryList.add(det.getInquiry_id());
						getSession().createQuery("update PurchaseInquiryModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getInquiry_id()).executeUpdate();
					}
				}
				flush();
			}
			getSession().createQuery("update PurchaseQuotationModel set active=false where id="+mdl.getId()).executeUpdate();
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
	public List getAllDataFromPurchaseInquiry(Set<Long> purchaseInquiries) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseQuotationBean(a.id, b) from PurchaseInquiryModel a " +
								"join a.inquiry_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchaseInquiries).list();
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
