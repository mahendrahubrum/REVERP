package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.sales.model.DeliveryNoteDetailsModel;
import com.inventory.sales.model.DeliveryNoteModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.webspark.dao.SHibernate;

/**
 * @author anil
 * @date 05-Sep-2015
 * @Project REVERP
 */

public class DeliveryNoteDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -7062040600823346431L;

CommonMethodsDao comDao=new CommonMethodsDao();
	@SuppressWarnings("rawtypes")
	public long save(DeliveryNoteModel mdl) throws Exception {
		try {
			begin();
			List<Long> orderList=new ArrayList<Long>(); 
			Iterator itr=mdl.getDelivery_note_details_list().iterator();
			while (itr.hasNext()) {
				
				DeliveryNoteDetailsModel det = (DeliveryNoteDetailsModel) itr.next();
				
				// Update Sales Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold+:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Sales Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				det.setStock_ids(det.getStock_id()+":"+det.getQty_in_basic_unit());
				
				comDao.decreaseStockByStockID(det.getStock_id(), det.getQty_in_basic_unit());
				flush();
			}
			getSession().save(mdl);
			
			flush();
			
			Iterator itr3=mdl.getDelivery_note_details_list().iterator();
			while (itr3.hasNext()) {
				DeliveryNoteDetailsModel det=(DeliveryNoteDetailsModel) itr3.next();
					String[] stks=det.getStock_ids().split(",");
					for (String string : stks) {
						if(string.length()>2)
						getSession().save(new SalesStockMapModel(mdl.getId(), det.getId(), 
											Long.parseLong(string.split(":")[0]),Double.parseDouble(string.split(":")[1]),2));
						flush();
					}
			}
			
			flush();
			
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
	public List getDeliveryNoteModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(id,deliveryNo) from DeliveryNoteModel " +
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
	public List getSalesOrderModelCustomerList(long office, long customer, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.SalesOrderModel(a.id,concat('Order No: ',order_no,', Order Date: ',cast(date as string),', Approximate Amount: ',amount))" +
					" from SalesOrderModel a join a.order_details_list b where a.office.id=:office and a.active=true and a.customer.id=:customer " +
					" and b.quantity_sold<b.qty_in_basic_unit "
					+cdn+" order by a.id DESC")
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
	
	
	@SuppressWarnings("rawtypes")
	public List getSalesOrderModelSalesManList(long office, long salesman, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.SalesOrderModel(a.id,concat('Order No: ',order_no,', Order Date: ',cast(date as string),', Approximate Amount: ',amount))" +
					" from SalesOrderModel  a join a.order_details_list b where a.office.id=:office and a.active=true and a.responsible_employee=:salesman "+
					" and b.quantity_sold<b.qty_in_basic_unit "
					+cdn+" order by a.id DESC")
					.setParameter("office", office).setParameter("salesman", salesman).list();
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
	
	
	public DeliveryNoteModel getDeliveryNoteModel(long id) throws Exception {
		DeliveryNoteModel mdl=null;
		try {
			begin();
			mdl=(DeliveryNoteModel)getSession().get(DeliveryNoteModel.class, id);
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
	
	
	public DeliveryNoteDetailsModel getDeliveryNoteDetailsModel(long id) throws Exception {
		DeliveryNoteDetailsModel mdl=null;
		try {
			begin();
			mdl=(DeliveryNoteDetailsModel)getSession().get(DeliveryNoteDetailsModel.class, id);
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
	public void update(DeliveryNoteModel mdl) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List oldIdList=new ArrayList();
			List<Long> orderList=new ArrayList<Long>();
			List<Long> newOrderList=new ArrayList<Long>();
			oldList=getSession().createQuery("select b from DeliveryNoteModel a join a.delivery_note_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				DeliveryNoteDetailsModel det = (DeliveryNoteDetailsModel) itr.next();
				
				// Update Sales Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold-:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Sales Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				comDao.increaseStockByStockID(det.getStock_id(),  det.getQty_in_basic_unit());
				
				oldIdList.add(det.getId());
			}
			
			List<DeliveryNoteDetailsModel> childList = new ArrayList<DeliveryNoteDetailsModel>();
			// Updating
			itr=mdl.getDelivery_note_details_list().iterator();
			while (itr.hasNext()) {
				
				DeliveryNoteDetailsModel det = (DeliveryNoteDetailsModel) itr.next();
				
				// Update Sales Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold+:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Sales Order Parent
				if(det.getOrder_id()!=0){
					if(!newOrderList.contains(det.getOrder_id())) {
						newOrderList.add(det.getOrder_id());
						getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
								.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
					flush();
					
				det.setStock_ids(det.getStock_id()+":"+det.getQty_in_basic_unit());
				
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				comDao.decreaseStockByStockID(det.getStock_id(), det.getQty_in_basic_unit());
				
				childList.add(det);
			}
			mdl.setDelivery_note_details_list(childList);
			getSession().clear();
			getSession().update(mdl);
			
			flush();
			
			if (oldIdList.size() > 0) {
				getSession().createQuery("delete from DeliveryNoteDetailsModel where id in (:lst)")
						.setParameterList("lst", (Collection) oldIdList).executeUpdate();
			}
			
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=2").setLong("id", mdl.getId()).executeUpdate();
			flush();
			
			
			Iterator itr3=mdl.getDelivery_note_details_list().iterator();
			DeliveryNoteDetailsModel detMdl;
			while (itr3.hasNext()) {
				detMdl=(DeliveryNoteDetailsModel) itr3.next();
				String[] stks=detMdl.getStock_ids().split(",");
				for (String string : stks) {
					if(string.length()>2)
					getSession().save(new SalesStockMapModel(mdl.getId(), detMdl.getId(), Long.parseLong(string.split(":")[0]),
							Double.parseDouble(string.split(":")[1]),2));
				}
				flush();
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
	
	
	@SuppressWarnings({ "rawtypes"})
	public void delete(DeliveryNoteModel mdl) throws Exception {
		try {
			begin();
			Iterator itr=mdl.getDelivery_note_details_list().iterator();
			List<Long> orderList=new ArrayList<Long>();
			while (itr.hasNext()) {
				DeliveryNoteDetailsModel det = (DeliveryNoteDetailsModel) itr.next();
				
				// Update Sales Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold-:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Sales Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				comDao.increaseStockByStockID(det.getStock_id(),  det.getQty_in_basic_unit());
			}
			getSession().delete(mdl);
			flush();
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=2").setLong("id", mdl.getId()).executeUpdate();
			flush();
			
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
	public void cancel(DeliveryNoteModel mdl) throws Exception {
		try {
			begin();
			Iterator itr=mdl.getDelivery_note_details_list().iterator();
			List<Long> orderList=new ArrayList<Long>();
			while (itr.hasNext()) {
				DeliveryNoteDetailsModel det = (DeliveryNoteDetailsModel) itr.next();
				
				// Update Sales Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold-:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Sales Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				comDao.increaseStock(det.getStock_id(),  det.getQty_in_basic_unit());
			}
			getSession().createQuery("update DeliveryNoteModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
			flush();
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=2").setLong("id", mdl.getId()).executeUpdate();
			flush();
			
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
	public List getAllDataFromSalesOrder(Set<Long> salesOrders) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.bean.DeliveryNoteBean(a.id, b) from SalesOrderModel a " +
								"join a.order_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", salesOrders).list();
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
	public List getAllDataFromSalesOrderWithCustomer(Set<Long> salesOrders) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.bean.DeliveryNoteBean(a.id,a.customer.id, b,a) from SalesOrderModel a " +
					"join a.order_details_list b where a.id in (:list) and a.active=true order by a.id")
					.setParameterList("list", salesOrders).list();
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

	public List getAllDeliveryNoteNumbers(long officeID) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(a.id, a.deliveryNo) from DeliveryNoteModel a" +
					" where a.office.id = :office_id and active=true order by a.id")
						.setParameter("office_id", officeID).list();
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


	public List getAllDNNumbersForEmploy(long officeId, long employId,Date from_date, Date to_date) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(a.id, a.deliveryNo) from DeliveryNoteModel a" +
					" where a.office.id = :office_id" +
					" and a.responsible_employee = :employee_id" +
					" and date between :from_date and :to_date and active=true" +
					" order by a.id")
						.setParameter("office_id", officeId)
						.setParameter("employee_id", employId)
						.setParameter("from_date", from_date)
						.setParameter("to_date", to_date).list();
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
