package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.DeliveryNoteDetailsModel;
import com.inventory.sales.model.DeliveryNoteModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class DeliveryNoteDaoUsingStock extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6267911671210961239L;

	List resultList = new ArrayList();
	
	ItemDao itemDao=new ItemDao();

	public long save(DeliveryNoteModel obj)
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

			List<DeliveryNoteDetailsModel> invList = new ArrayList<DeliveryNoteDetailsModel>();
			Iterator<DeliveryNoteDetailsModel> it = obj
					.getDelivery_note_details_list().iterator();
			DeliveryNoteDetailsModel invObj;
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					getSession()
							.createQuery(
									"update DeliveryNoteDetailsModel set balance=balance-:qty where id=:id")
							.setParameter("id", invObj.getId())
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();

					getSession()
							.createQuery(
									"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", invObj.getItem().getId())
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();

					invObj.setId(0);
				}

				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance-:qty where id=:id")
						/*.setLong("id", invObj.getStock_id())*/
						.setDouble("qty", invObj.getQunatity()).executeUpdate();

				invList.add(invObj);
			}

			obj.setDelivery_note_details_list(invList);

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

	public DeliveryNoteModel getDeliveryNote(long id) throws Exception {
		DeliveryNoteModel pur = null;
		try {
			begin();
			pur = (DeliveryNoteModel) getSession().get(DeliveryNoteModel.class, id);
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

	public void update(DeliveryNoteModel newobj)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from DeliveryNoteModel a join a.inventory_details_list b "
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

			// DeliveryNoteModel obj=(DeliveryNoteModel) getSession().get(DeliveryNoteModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from DeliveryNoteModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			Iterator<DeliveryNoteDetailsModel> it = oldLst.iterator();
			DeliveryNoteDetailsModel invObj;
			List list;
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					getSession().createQuery(
									"update SalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();
				}

				getSession().createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQunatity())
						.executeUpdate();

				// For Stock Update
				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance+:qty where id=:id")
						/*.setLong("id", invObj.getStock_id())*/
						.setDouble("qty", invObj.getQunatity()).executeUpdate();

			}

			// getSession().delete(obj);

			flush();

			// Save

			List<DeliveryNoteDetailsModel> invList = new ArrayList<DeliveryNoteDetailsModel>();
			Iterator<DeliveryNoteDetailsModel> it1 = newobj
					.getDelivery_note_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					getSession()
							.createQuery(
									"update SalesInventoryDetailsModel set balance=balance-:qty where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();

					/*
					 * getSession().createQuery(
					 * "update SalesInventoryDetailsModel set balance=balance-:qty where id=:id"
					 * ) .setParameter("id", invObj.getId()).setParameter("qty",
					 * invObj.getQunatity()) .executeUpdate();
					 */

					invObj.setId(0);
				}

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQunatity())
						.executeUpdate();

				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance-:qty where id=:id")
						/*.setLong("id", invObj.getStock_id())*/
						.setDouble("qty", invObj.getQunatity()).executeUpdate();

				invList.add(invObj);
			}

			newobj.setDelivery_note_details_list(invList);

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
			DeliveryNoteModel obj = (DeliveryNoteModel) getSession()
					.get(DeliveryNoteModel.class, id);

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

			Iterator<DeliveryNoteDetailsModel> it = obj
					.getDelivery_note_details_list().iterator();
			DeliveryNoteDetailsModel invObj;
			List list;
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					getSession()
							.createQuery(
									"update SalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();
				}

				getSession()
						.createQuery(
								"update ItemStockModel set balance=balance+:qty where id=:id")
						/*.setLong("id", invObj.getStock_id())*/
						.setDouble("qty", invObj.getQunatity()).executeUpdate();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQunatity())
						.executeUpdate();

			}

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

	public List getAllDeliveryNoteNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.DeliveryNoteModel(id,cast(delivery_order_number as string) )"
									+ " from DeliveryNoteModel where office.id=:ofc")
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

	public List getAllSalesOrdersForCustomer(long customer_id, long office_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesOrderModel(a.id,cast(a.sales_order_number as string) )"
									+ " from SalesOrderModel a join a.inventory_details_list b where a.office.id=:ofc and b.balance!=0 and a.customer.id=:cust group by a.id")
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

	public List getAllItemsFromSalesOrders(Set<Long> SOs) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo(a.id, b.id,  b.item.id, b.item.item_code, b.item.name, b.unit.id, b.unit.symbol, "
									+ "b.tax.id, b.tax_amount, b.tax_percentage, b.qunatity, b.unit_price, b.discount_amount, b.balance , b.cess_amount , b.manufacturing_date, b.expiry_date, b.stock_id)"
									+ " from SalesOrderModel a join a.inventory_details_list b where a.id in (:ids) and b.balance!=0")
					.setParameterList("ids", SOs).list();
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

	public List getItemStocksOfItem(long item_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat('Stk : ', id,' : ', item.name, ' Exp : ' , expiry_date, ' Bal: ' , balance )) from ItemStockModel where item.id=:itm and quantity>0")
					.setLong("itm", item_id).list();
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
		ItemStockModel stk = null;
		try {
			begin();
			stk = (ItemStockModel) getSession().get(ItemStockModel.class, id);
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

	@SuppressWarnings("finally")
	public List getAllItemStocks() throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where quantity>0")
					.list();
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
	
	
	public List getAllItemStocks(long ofice_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel " +
									"where item.office.id=:ofc").setLong("ofc", ofice_id)
					.list();
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

	@SuppressWarnings("finally")
	public TransactionModel getTransaction(long id) throws Exception {
		TransactionModel tran = null;
		try {
			begin();
			tran = (TransactionModel) getSession().get(TransactionModel.class,
					id);
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
			return tran;
		}
	}

	
	public List<Object> getAllDNNumbersForEmploy(long officeId, long empId,
			Date fromDate, Date toDate) throws Exception {

		try {
			begin();
			String condition="";
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.DeliveryNoteModel(id,cast(delivery_order_number as string) )"
									+ " from DeliveryNoteModel where employ.id=:empId and date>=:fromDate and date<=:toDate")
					.setParameter("empId", empId)
					.setParameter("fromDate", fromDate)
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

}
