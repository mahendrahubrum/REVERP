package com.inventory.sales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesOrderModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class SalesOrderDaoUsingStock extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2413618183245788775L;
	List resultList = new ArrayList();

	public long save(SalesOrderModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
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
			return obj.getId();
		}
	}
	
	public SalesOrderModel getSalesOrder(long order_id) throws Exception {
		SalesOrderModel po=null;
		try {
			begin();
			po = (SalesOrderModel) getSession().get(SalesOrderModel.class, order_id);
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
			return po;
		}
	}
	
	
	public void update(SalesOrderModel obj) throws Exception {
		try {
			
			begin();
				
			Object objLst=getSession().createQuery("select b.id from SalesOrderModel a join a.inventory_details_list b " +
					"where a.id="+obj.getId()).list();
			
			getSession().update(obj);
			flush();
			
			getSession().createQuery("delete from SalesInventoryDetailsModel where id in (:lst)")
			.setParameterList("lst", (Collection) objLst).executeUpdate();
				
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
		}
	}
	
	
	public void delete(long id) throws Exception {
		try {
			begin();
			SalesOrderModel obj=(SalesOrderModel) getSession().get(SalesOrderModel.class, id);
			getSession().delete(obj);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		} 
			flush();
			close();
	}
	
	
	public List getAllSalesOrderNumbersAsRefNo(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.sales.model.SalesOrderModel(id,cast(sales_order_number as string) )" +
					" from SalesOrderModel where office.id=:ofc").setParameter("ofc", ofc_id).list();
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
	
	
	public List getAllItemStocks(long office_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo(" +
					" id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where item.office=:ofc")
					.setLong("ofc", office_id).list();
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
	
	
	public ItemStockModel getItemStocks(long id) throws Exception {
		ItemStockModel stk=null;
		try {
			begin();
			stk =  (ItemStockModel) getSession().get(ItemStockModel.class, id);
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
			return stk;
		}
	}
	
}
