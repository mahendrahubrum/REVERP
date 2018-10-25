package com.inventory.sales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.sales.model.SalesInquiryDetailsModel;
import com.inventory.sales.model.SalesInquiryModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class SalesInquiryDao extends SHibernate implements Serializable {

	public long save(SalesInquiryModel mdl) throws Exception {
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
	public List getSalesInquiryModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.sales.model.SalesInquiryModel(id,inquiry_no) from SalesInquiryModel " +
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
	
	
	public SalesInquiryModel getSalesInquiryModel(long id) throws Exception {
		SalesInquiryModel mdl=null;
		try {
			begin();
			mdl=(SalesInquiryModel)getSession().get(SalesInquiryModel.class, id);
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
	
	
	public SalesInquiryDetailsModel getSalesInquiryDetailsModel(long id) throws Exception {
		SalesInquiryDetailsModel mdl=null;
		try {
			begin();
			mdl=(SalesInquiryDetailsModel)getSession().get(SalesInquiryDetailsModel.class, id);
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
	public void update(SalesInquiryModel mdl) throws Exception {
		try {
			begin();
			List oldList=getSession().createQuery("select b.id from SalesInquiryModel a join a.sales_inquiry_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			List<SalesInquiryDetailsModel> itemsList = new ArrayList<SalesInquiryDetailsModel>();
			if(oldList.size()>0){
				Iterator itr=mdl.getSales_inquiry_details_list().iterator();
				while (itr.hasNext()) {
					SalesInquiryDetailsModel det = (SalesInquiryDetailsModel) itr.next();
					if(det.getId()!=0)
						oldList.remove(det.getId());
					itemsList.add(det);
				}
			}
			mdl.setSales_inquiry_details_list(itemsList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			if(oldList!=null && oldList.size()>0){
				getSession().createQuery("delete from SalesInquiryDetailsModel where id in (:list)")
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
	
	
	public void delete(SalesInquiryModel mdl) throws Exception {
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
	
	
	public void cancel(SalesInquiryModel mdl) throws Exception {
		try {
			begin();
			getSession().createQuery("update SalesInquiryModel set active=false where id="+mdl.getId()).executeUpdate();
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
