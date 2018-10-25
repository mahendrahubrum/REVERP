package com.inventory.onlineSales.dao;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.onlineSales.model.OnlineCustomerModel;
import com.inventory.onlineSales.model.OnlineSalesOrderModel;
import com.inventory.sales.model.SalesOrderModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 24, 2014
 */
public class ImportOnlineSalesOrderDao extends SHibernate{

	public List getAllOnlineSalesOrders(Long officeId, Long customerId) throws Exception {
		List list=null;
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.onlineSales.model.OnlineSalesOrderModel(id,concat(date,'')) from OnlineSalesOrderModel where onlineCustomer=:cust and status=1").setParameter("cust", customerId).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return list;
	}

	public OnlineCustomerModel getOnlineCustomer(long onlineCustomerID) throws Exception {
		OnlineCustomerModel mdl=null;
		try {
			begin();
			mdl=(OnlineCustomerModel) getSession().get(OnlineCustomerModel.class, onlineCustomerID);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return mdl;
	}
	
	public OnlineSalesOrderModel getOnlineOrder(long onlineOrderId) throws Exception {
		OnlineSalesOrderModel mdl=null;
		try {
			begin();
			mdl=(OnlineSalesOrderModel) getSession().get(OnlineSalesOrderModel.class, onlineOrderId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return mdl;
	}

	public List getOnlineSalesOrderDetails(long orderId) throws Exception {
		
		List list=null;
		try {
			begin();
			list=getSession().createQuery("from OnlineSalesOrderDetailsModel where onlineSalesOrderId=:ordId").setParameter("ordId", orderId).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return list;
	}

	public List getAllOnlineCustomersWithSO() throws Exception {
		List list=null;
		try {
			begin();
			list=getSession().createQuery("from OnlineCustomerModel where id in (select onlineCustomer from OnlineSalesOrderModel where  status=1)").list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return list;
	}

	public long getCustomerIdOfOnlineCustomer(Long custId) throws Exception {
		long id=0;
		try {
			begin();
			Object obj=getSession().createQuery("select customer_id from OnlineCustomerModel where id=:id").setParameter("id", custId).uniqueResult();
			commit();
			if(obj!=null)
				id=(Long) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		
		return id;
	}

	public void updateOnlineCustomer(OnlineCustomerModel custMdl) throws Exception {
		try {
			begin();
			getSession().update(custMdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		
		
	}

	public void saveSalesOrder(List saveList,List updateList) throws Exception {
		try {
			begin();
			
			SalesOrderModel salesOrderModel;
			OnlineSalesOrderModel onlineSalesOrderModel;
			Iterator iter=saveList.iterator();
			while (iter.hasNext()) {
				salesOrderModel = (SalesOrderModel) iter.next();
				getSession().save(salesOrderModel);
			}
			
			iter = updateList.iterator();
			while (iter.hasNext()) {
				onlineSalesOrderModel = (OnlineSalesOrderModel) iter.next();
				onlineSalesOrderModel.setStatus(2);
				getSession().update(onlineSalesOrderModel);
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
	}

	public List<Object> getOnlineSalesOrderDetailsReport(long custId,Date fromDate, Date toDate) throws Exception {
		List list=null;
		try {
			String con="";
			
			if(custId!=0)
				con+=" and onlineCustomer="+custId;
			begin();
			list = getSession()
					.createQuery(
							"from OnlineSalesOrderModel where date between :frm and :to "+con).setParameter("frm", fromDate).setParameter("to", toDate)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return list;
	}

	public List getAllOnlineCustomers() throws Exception {
		List list=null;
		try {
			begin();
			list=getSession().createQuery("from OnlineCustomerModel order by firstName").list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
			
		}finally{
			flush();
			close();
		}
		return list;
	}
	
}
