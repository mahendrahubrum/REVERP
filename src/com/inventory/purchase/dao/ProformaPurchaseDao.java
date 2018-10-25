package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import com.inventory.purchase.model.ProformaPurchaseModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ProformaPurchaseDao extends SHibernate implements Serializable {

	public long save(ProformaPurchaseModel mdl) throws Exception {
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
	public List getProformaPurchaseModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.ProformaPurchaseModel(id,proforma_no) from ProformaPurchaseModel " +
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
	
	
	public ProformaPurchaseModel getProformaPurchaseModel(long id) throws Exception {
		ProformaPurchaseModel mdl=null;
		try {
			begin();
			mdl=(ProformaPurchaseModel)getSession().get(ProformaPurchaseModel.class, id);
			Hibernate.initialize(mdl.getProforma_purchase_expense_list());
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
	
	
	@SuppressWarnings({ "rawtypes" })
	public void update(ProformaPurchaseModel mdl) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List expenseList=new ArrayList();
			
			oldList=getSession().createQuery("select b.id from ProformaPurchaseModel a join a.proforma_purchase_details_list b where a.id=:id")
								.setParameter("id", mdl.getId()).list();
			
			expenseList=getSession().createQuery("select b.id from ProformaPurchaseModel a join a.proforma_purchase_expense_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			getSession().update(mdl);
			
			flush();
			
			if(oldList.size()>0){
				
				getSession().createQuery("delete from ProformaPurchaseInventoryDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) oldList).executeUpdate();
				
			}
			
			if(expenseList.size()>0){
				
				getSession().createQuery("delete from ProformaPurchaseExpenseDetailsModel where id in (:lst)")
				.setParameterList("lst", (Collection) expenseList).executeUpdate();
				
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
	
	
	public void delete(ProformaPurchaseModel mdl) throws Exception {
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
	
	
	public void cancel(ProformaPurchaseModel mdl) throws Exception {
		try {
			begin();
			getSession().createQuery("update ProformaPurchaseModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	public List getPurchaseOrderModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseOrderModel(id,concat('Order No: ',order_no,', Order Date: ',cast(date as string),', Approximate Amount: ',amount))" +
					" from PurchaseOrderModel where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
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
	
	
	@SuppressWarnings("rawtypes")
	public List getAllDataFromPurchaseOrder(Set<Long> purchaseOrders) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseGRNBean(a.id, b) from PurchaseOrderModel a " +
								"join a.order_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchaseOrders).list();
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
	public List getPurchaseGRNModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseGRNModel(id,concat('GRN No: ',grn_no,', GRN Date: ',cast(date as string),', Amount: ',amount))" +
					" from PurchaseGRNModel where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
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
	
	
	@SuppressWarnings("rawtypes")
	public List getAllDataFromPurchaseGRN(Set<Long> purchaseGRN) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseBean(a.id, b) from PurchaseGRNModel a " +
								"join a.grn_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchaseGRN).list();
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
