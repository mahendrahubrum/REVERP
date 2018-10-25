package com.inventory.purchase.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.purchase.model.PurchaseOrderDetailsModel;
import com.inventory.purchase.model.PurchaseOrderModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PurchaseOrderDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public long save(PurchaseOrderModel mdl) throws Exception {
		try {
			begin();
			List<PurchaseOrderDetailsModel> itemsList = new ArrayList<PurchaseOrderDetailsModel>();
			List<Long> quotationList=new ArrayList<Long>(); 
			Iterator itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseOrderDetailsModel det = (PurchaseOrderDetailsModel) itr.next();
				
				// Update Purchase Quotation Child
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update PurchaseQuotationDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Quotation Parent
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update PurchaseQuotationModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
					}
				}
				flush();
				
				itemsList.add(det);
			}
			mdl.setOrder_details_list(itemsList);
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
	public List getPurchaseOrderModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseOrderModel(id,order_no) from PurchaseOrderModel " +
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
	public List getPurchaseQuotationModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseQuotationModel(id,concat('Quotation No: ',quotation_no,', Quotation Date: ',cast(date as string),', Approximate Amount: ',amount))" +
					" from PurchaseQuotationModel where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
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
	
	
	public PurchaseOrderModel getPurchaseOrderModel(long id) throws Exception {
		PurchaseOrderModel mdl=null;
		try {
			begin();
			mdl=(PurchaseOrderModel)getSession().get(PurchaseOrderModel.class, id);
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
	
	
	public PurchaseOrderDetailsModel getPurchaseOrderDetailsModel(long id) throws Exception {
		PurchaseOrderDetailsModel mdl=null;
		try {
			begin();
			mdl=(PurchaseOrderDetailsModel)getSession().get(PurchaseOrderDetailsModel.class, id);
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
	public void update(PurchaseOrderModel mdl) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List oldIdList=new ArrayList();
			oldList=getSession().createQuery("select b from PurchaseOrderModel a join a.order_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			List<Long> quotationList=new ArrayList<Long>();
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				PurchaseOrderDetailsModel det = (PurchaseOrderDetailsModel) itr.next();
				
				// Update Purchase Quotation Child
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update PurchaseQuotationDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Quotation Parent
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update PurchaseQuotationModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
					}
				}
				flush();
				oldIdList.add(det.getId());
			}
			
			List<PurchaseOrderDetailsModel> itemsList = new ArrayList<PurchaseOrderDetailsModel>();
			List<Long> newQuotationList=new ArrayList<Long>();
			itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseOrderDetailsModel det = (PurchaseOrderDetailsModel) itr.next();
				
				// Update Purchase Quotation Child
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update PurchaseQuotationDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Quotation Parent
				if(det.getQuotation_id()!=0){
					if(!newQuotationList.contains(det.getQuotation_id())) {
						newQuotationList.add(det.getQuotation_id());
						getSession().createQuery("update PurchaseQuotationModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
					}
				}
				flush();
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				itemsList.add(det);
			}
			mdl.setOrder_details_list(itemsList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			if(oldIdList!=null && oldIdList.size()>0){
				getSession().createQuery("delete from PurchaseOrderDetailsModel where id in (:list)")
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
	public void delete(PurchaseOrderModel mdl) throws Exception {
		try {
			begin();
			List<Long> quotationList=new ArrayList<Long>();
			Iterator itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseOrderDetailsModel det = (PurchaseOrderDetailsModel) itr.next();
				
				// Update Purchase Quotation Child
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update PurchaseQuotationDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Quotation Parent
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update PurchaseQuotationModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
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
	public void cancel(PurchaseOrderModel mdl) throws Exception {
		try {
			begin();
			List<Long> quotationList=new ArrayList<Long>();
			Iterator itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseOrderDetailsModel det = (PurchaseOrderDetailsModel) itr.next();
				
				// Update Purchase Quotation Child
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update PurchaseQuotationDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				// Update Purchase Quotation Parent
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update PurchaseQuotationModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
					}
				}
				flush();
			}
			getSession().createQuery("update PurchaseOrderModel set active=false where id="+mdl.getId()).executeUpdate();
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
	public List getAllDataFromPurchaseQuotation(Set<Long> purchasequotations) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseOrderBean(a.id, b) from PurchaseQuotationModel a " +
								"join a.quotation_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchasequotations).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getPurchaseOrderExpiry(Date start, Date end, long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select a from PurchaseOrderModel a where a.office.id=:office and a.lock_count=0 and a.active=true " +
					" and a.expiryDate is not null and ((a.expiryDate between :start and :end) or a.expiryDate<=:start )")
					.setParameter("office", office).setParameter("start", start).setParameter("end", end).list();
			
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
