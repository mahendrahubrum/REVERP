package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.WorkOrderModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class WorkOrderDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4718243203783193865L;

	List resultList = new ArrayList();
	
	ItemDao itemDao=new ItemDao();
	CommonMethodsDao comDao=new CommonMethodsDao();

	public long save(WorkOrderModel obj)
			throws Exception {

		try {

			begin();

			// Transaction Related

			/*getSession().save(transaction);

			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				TransactionDetailsModel tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}

			// Transaction Related

			obj.setTransaction_id(transaction.getTransaction_id());*/

			List<SalesInventoryDetailsModel> invList = new ArrayList<SalesInventoryDetailsModel>();
			SalesInventoryDetailsModel invObj;
			String stockIDs;
//			Iterator<SalesInventoryDetailsModel> it = obj
//					.getInventory_details_list().iterator();
//			while (it.hasNext()) {
//				invObj = it.next();
//
//				if (invObj.getOrder_id() != 0) {
//
//					getSession()
//							.createQuery(
//									"update SalesInventoryDetailsModel set balance=balance-:qty where id=:id")
//							.setParameter("id", invObj.getId())
//							.setParameter("qty", invObj.getQunatity())
//							.executeUpdate();
//
//					invObj.setId(0);
//				}
//				
//				
//				getSession()
//					.createQuery(
//							"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
//					.setParameter("id", invObj.getItem().getId())
//					.setParameter("qty", invObj.getQunatity())
//					.executeUpdate();
//
//				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
//				
//				invObj.setStock_ids(stockIDs);
//				
//				
//				invList.add(invObj);
//			}
//
//			obj.setInventory_details_list(invList);

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

	public WorkOrderModel getWorkOrder(long id) throws Exception {
		WorkOrderModel pur = null;
		try {
			begin();
			pur = (WorkOrderModel) getSession().get(WorkOrderModel.class, id);
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
			return pur;
		}
	}

	public void update(WorkOrderModel newobj)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from WorkOrderModel a join a.inventory_details_list b "
							+ "where a.id=" + newobj.getId()).list();

			// Delete

			/*List old_AcctnotDeletedLst = new ArrayList();

			List AcctDetLst = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			Iterator<TransactionDetailsModel> aciter = AcctDetLst.iterator();
			while (aciter.hasNext()) {
				TransactionDetailsModel tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

				old_AcctnotDeletedLst.add(tr.getId());

			}*/

			// WorkOrderModel obj=(WorkOrderModel) getSession().get(WorkOrderModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from WorkOrderModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			SalesInventoryDetailsModel invObj;
			Iterator<SalesInventoryDetailsModel> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQunatity())
						.executeUpdate();

				// For Stock Update
				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());

			}

			// getSession().delete(obj);

			flush();

			// Save

			String stockIDs;
			List<SalesInventoryDetailsModel> invList = new ArrayList<SalesInventoryDetailsModel>();
//			Iterator<SalesInventoryDetailsModel> it1 = newobj
//					.getInventory_details_list().iterator();
//			while (it1.hasNext()) {
//				invObj = it1.next();
//
//				getSession()
//						.createQuery(
//								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
//						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getQunatity())
//						.executeUpdate();
//
//				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
//				
//				invObj.setStock_ids(stockIDs);
//				
//				invList.add(invObj);
//			}

//			newobj.setInventory_details_list(invList);

			// Transaction Related

//			getSession().update(transaction);

			/*Iterator<TransactionDetailsModel> aciter1 = transaction
					.getTransaction_details_list().iterator();
			while (aciter1.hasNext()) {
				TransactionDetailsModel tr = aciter1.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();

			}*/

			// Transaction Related

			getSession().update(newobj);
			flush();

			getSession()
					.createQuery(
							"delete from SalesInventoryDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

			/*getSession()
					.createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
					.executeUpdate();*/

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
			WorkOrderModel obj = (WorkOrderModel) getSession()
					.get(WorkOrderModel.class, id);

			// Transaction Related

			/*TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				TransactionDetailsModel tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);*/

			// Transaction Related

			SalesInventoryDetailsModel invObj;
//			Iterator<SalesInventoryDetailsModel> it = obj
//					.getInventory_details_list().iterator();
//			while (it.hasNext()) {
//				invObj = it.next();
//
//				getSession()
//						.createQuery(
//								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getQunatity())
//						.executeUpdate();
//				
//				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
//
//			}

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

	public List getAllWorkOrderNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.WorkOrderModel(id,cast(work_order_number as string) )"
									+ " from WorkOrderModel where office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
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
	
	
	
	public List getAllWorkOrderNumbersOfContractorAsComment(long cont_id, Date stdt, Date enddt) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.sales.model.WorkOrderModel(id,concat(work_order_number, '  ( ', date, ' )') )"
									+ " from WorkOrderModel where contractor.id=:cont and date between :stdt and :enddt")
										.setParameter("stdt", stdt).setParameter("enddt", enddt).setParameter("cont", cont_id).list();
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
	
	
	
	public List getAllWorkOrderNumbersOfContractorAsComment(Date stdt, Date enddt, long office_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.sales.model.WorkOrderModel(id,concat(work_order_number, '  ( ', date, ' )') )"
									+ " from WorkOrderModel where office.id=:ofc and date between :stdt and :enddt")
										.setParameter("stdt", stdt).setParameter("enddt", enddt).setParameter("ofc", office_id).list();
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
	
	


	public ItemModel getItem(long id) throws Exception {
		ItemModel itm = null;
		try {
			begin();
			itm = (ItemModel) getSession().get(ItemModel.class, id);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	

}
