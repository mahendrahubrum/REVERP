package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesOrderDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.IDGeneratorDao;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class SalesOrderDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -6552696986393366887L;
	
	@SuppressWarnings("rawtypes")
	public long save(SalesOrderModel mdl) throws Exception {
		try {
			begin();
			List<SalesOrderDetailsModel> itemsList = new ArrayList<SalesOrderDetailsModel>();
			List<Long> quotationList=new ArrayList<Long>(); 
			Iterator itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				SalesOrderDetailsModel det = (SalesOrderDetailsModel) itr.next();
				
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update QuotationDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update QuotationModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
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
	public List getSalesOrderModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.sales.model.SalesOrderModel(id,order_no) from SalesOrderModel " +
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
	public List getQuotationModelCustomerList(long office, long customer, List lst) throws Exception {
		List list=new ArrayList();
		try {
			
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.QuotationModel(id,concat('Quotation No: ',quotation_no,', Quotation Date: ',cast(date as string),', Approximate Amount: ',amount))" +
					" from QuotationModel where office.id=:office and active=true and customer.id=:customer "+cdn+" order by id DESC")
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
	
	
	public SalesOrderModel getSalesOrderModel(long id) throws Exception {
		SalesOrderModel mdl=null;
		try {
			begin();
			mdl=(SalesOrderModel)getSession().get(SalesOrderModel.class, id);
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
	
	
	public SalesOrderDetailsModel getSalesOrderDetailsModel(long id) throws Exception {
		SalesOrderDetailsModel mdl=null;
		try {
			begin();
			mdl=(SalesOrderDetailsModel)getSession().get(SalesOrderDetailsModel.class, id);
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
	public void update(SalesOrderModel mdl) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List oldIdList=new ArrayList();
			oldList=getSession().createQuery("select b from SalesOrderModel a join a.order_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			List<Long> quotationList=new ArrayList<Long>();
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				SalesOrderDetailsModel det = (SalesOrderDetailsModel) itr.next();
				
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update QuotationDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update QuotationModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
					}
				}
				flush();
				oldIdList.add(det.getId());
			}
			
			List<SalesOrderDetailsModel> itemsList = new ArrayList<SalesOrderDetailsModel>();
			List<Long> newQuotationList=new ArrayList<Long>();
			itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				SalesOrderDetailsModel det = (SalesOrderDetailsModel) itr.next();
				
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update QuotationDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				if(det.getQuotation_id()!=0){
					if(!newQuotationList.contains(det.getQuotation_id())) {
						newQuotationList.add(det.getQuotation_id());
						getSession().createQuery("update QuotationModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
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
				getSession().createQuery("delete from SalesOrderDetailsModel where id in (:list)")
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
	public void delete(SalesOrderModel mdl) throws Exception {
		try {
			begin();
			List<Long> quotationList=new ArrayList<Long>();
			Iterator itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				SalesOrderDetailsModel det = (SalesOrderDetailsModel) itr.next();
				
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update QuotationDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update QuotationModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
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
	public void cancel(SalesOrderModel mdl) throws Exception {
		try {
			begin();
			List<Long> quotationList=new ArrayList<Long>();
			Iterator itr=mdl.getOrder_details_list().iterator();
			while (itr.hasNext()) {
				SalesOrderDetailsModel det = (SalesOrderDetailsModel) itr.next();
				
				if(det.getQuotation_child_id()!=0) {
					getSession().createQuery("update QuotationDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
								.setParameter("id", det.getQuotation_child_id()).executeUpdate();
				}
				flush();
				
				if(det.getQuotation_id()!=0){
					if(!quotationList.contains(det.getQuotation_id())) {
						quotationList.add(det.getQuotation_id());
						getSession().createQuery("update QuotationModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getQuotation_id()).executeUpdate();
					}
				}
				flush();
			}
			getSession().createQuery("update SalesOrderModel set active=false where id="+mdl.getId()).executeUpdate();
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
	public List getAllDataFromQuotation(Set<Long> quotations) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.bean.SalesOrderBean(a.id, b) from QuotationModel a " +
								"join a.quotation_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", quotations).list();
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
	
	

	
	
	public void changeCustomerSOtoRealSO(Set<Long> SOs, long login_id, long office_id, long organization_id) throws Exception {
		try {
			begin();
			
			Iterator<Long> it=SOs.iterator();
			IDGeneratorDao idgenDao=new IDGeneratorDao();
			long so_no;
			while(it.hasNext()) {
				so_no=idgenDao.generateIDWithOutBegin("Sales Order Id", login_id, office_id, organization_id);
				
				getSession().createQuery("update SalesOrderModel set status=:sts,sales_order_number=:son where id=:id and active=true")
					.setLong("id", it.next()).setLong("son", so_no).setLong("sts", SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED)
					.executeUpdate();
			}
			
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
	}
	
	
	List resultList;
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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	
	
	public ItemModel getItem(long id) throws Exception {
		ItemModel itm = null;
		try {
			begin();
			itm = (ItemModel) getSession().get(ItemModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return itm;
		}
	}
	
	
	public List getAllItems(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel where office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return stk;
		}
	}

	public void updateItem(List<SalesInventoryDetailsModel> itemsList) throws Exception {
		SalesInventoryDetailsModel item=null;
		ManufacturingMapModel mapMdl;
		try {
			begin();
			List childList=null;
			Iterator childIter;
			Iterator iter=itemsList.iterator();
			while (iter.hasNext()) {
				item = (SalesInventoryDetailsModel) iter.next();
				
				childList = getSession()
						.createQuery(
								"from ManufacturingMapModel where item.id=:item")
						.setParameter("item", item.getItem().getId()).list();
				childIter=childList.iterator();
				while (childIter.hasNext()) {
					mapMdl=(ManufacturingMapModel) childIter.next();
					
					getSession()
							.createQuery(
									"update ItemModel set reservedQuantity=reservedQuantity+:resqty where id=:id")
							.setParameter("id", mapMdl.getSubItem().getId())
							.setParameter("resqty",
									(item.getQunatity()*mapMdl.getQuantity()))
							.executeUpdate();
				}
			}
			
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
	}
	
	public List<Object> getAllSalesOrderNumbersByDate(long officeId,
			Date fromDate, Date toDate) throws Exception {

		try {
			begin();
			String condition="";
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesOrderModel(id,order_no )"
									+ " from SalesOrderModel where date between :fromDate and :toDate" +
									" and active=true "+condition)//(status=:sts1 or status=:sts2 or status=:sts3)
					.setParameter("fromDate", fromDate)
				//	.setLong("sts1", SConstants.statuses.SALES_ORDER_DIRECT).setLong("sts3", SConstants.statuses.SALES_ORDER_ONLINE_APPROVED).setLong("sts2", SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getAllSalesOrdersForCustomer(long office_id , long customer_id,Date fromDate, Date toDate)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesOrderModel(a.id,a.order_no )"
									+ " from SalesOrderModel a join a.order_details_list b where a.office.id=:ofc" +
									" and a.date between :fromDate and :toDate and a.active=true" +
									" and a.customer.id=:cust" +
									//" and (status=:sts1 or status=:sts2 or status=:sts3)" +
									" group by a.id")
					.setParameter("ofc", office_id)
					//.setLong("sts1", SConstants.statuses.SALES_ORDER_DIRECT).setLong("sts3", SConstants.statuses.SALES_ORDER_ONLINE_APPROVED).setLong("sts2", SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED)
					.setParameter("cust", customer_id).setParameter("fromDate", fromDate).setParameter("toDate", toDate).list();
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
	
	public List getAllSalesOrdersForCustomer(long office_id , long customer_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesOrderModel(a.id,a.order_no )"
									+ " from SalesOrderModel a join a.order_details_list b where a.office.id=:ofc" +
									"  and a.active=true" +
									" and a.customer.id=:cust" +
									" group by a.id")
					.setParameter("ofc", office_id)
					.setParameter("cust", customer_id).list();
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
	
	public List getAllSalesOrdersUnderOffice(long office_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesOrderModel(a.id,a.order_no )"
									+ " from SalesOrderModel a join a.order_details_list b where a.office.id=:ofc" +
									"  and a.active=true" +
									" group by a.id")
					.setParameter("ofc", office_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}return resultList;
	}


}
