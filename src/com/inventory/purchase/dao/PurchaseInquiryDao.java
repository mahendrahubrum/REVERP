package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.purchase.model.PurchaseInquiryDetailsModel;
import com.inventory.purchase.model.PurchaseInquiryModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PurchaseInquiryDao extends SHibernate implements Serializable {

	public long save(PurchaseInquiryModel mdl) throws Exception {
		try {
			begin();
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
	public List getPurchaseInquiryModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseInquiryModel(id,inquiry_no) from PurchaseInquiryModel " +
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
	
	
	public PurchaseInquiryModel getPurchaseInquiryModel(long id) throws Exception {
		PurchaseInquiryModel mdl=null;
		try {
			begin();
			mdl=(PurchaseInquiryModel)getSession().get(PurchaseInquiryModel.class, id);
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
	
	
	public PurchaseInquiryDetailsModel getPurchaseInquiryDetailsModel(long id) throws Exception {
		PurchaseInquiryDetailsModel mdl=null;
		try {
			begin();
			mdl=(PurchaseInquiryDetailsModel)getSession().get(PurchaseInquiryDetailsModel.class, id);
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
	
	
	@SuppressWarnings("rawtypes")
	public void update(PurchaseInquiryModel mdl) throws Exception {
		try {
			begin();
			List oldList=getSession().createQuery("select b.id from PurchaseInquiryModel a join a.inquiry_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			List<PurchaseInquiryDetailsModel> itemsList = new ArrayList<PurchaseInquiryDetailsModel>();
			if(oldList.size()>0){
				Iterator itr=mdl.getInquiry_details_list().iterator();
				while (itr.hasNext()) {
					PurchaseInquiryDetailsModel det = (PurchaseInquiryDetailsModel) itr.next();
					if(det.getId()!=0)
						oldList.remove(det.getId());
					itemsList.add(det);
				}
			}
			mdl.setInquiry_details_list(itemsList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			if(oldList!=null && oldList.size()>0){
				getSession().createQuery("delete from PurchaseInquiryDetailsModel where id in (:list)")
							.setParameterList("list", (Collection)oldList).executeUpdate();
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
	
	
	public void delete(PurchaseInquiryModel mdl) throws Exception {
		try {
			begin();
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
	
	
	public void cancel(PurchaseInquiryModel mdl) throws Exception {
		try {
			begin();
			getSession().createQuery("update PurchaseInquiryModel set active=false where id="+mdl.getId()).executeUpdate();
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

	
}
