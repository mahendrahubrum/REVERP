package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jan 27, 2014
 */

public class GRVSalesOldDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7453940143393746135L;

	List resultList = new ArrayList();
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	ItemDao itemDao=new ItemDao();

	public long save(SalesModel obj, TransactionModel transaction, double payingAmt)
			throws Exception {
		
		try {
			
			begin();
			
			// Transaction Related

			getSession().save(transaction);
			
			flush();

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			
//			if(payingAmt!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", payingAmt).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			
			// Transaction Related

			obj.setTransaction_id(transaction.getTransaction_id());

			SalesInventoryDetailsModel invObj;
			List<SalesInventoryDetailsModel> invList = new ArrayList<SalesInventoryDetailsModel>();
			Iterator<SalesInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					getSession()
							.createQuery(
									"update SalesInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu  where id=:id")
							.setParameter("id", invObj.getId())
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
							.executeUpdate();

					invObj.setId(0);
				}
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				invObj.setStock_ids(invObj.getStock_id()+":"+invObj.getQuantity_in_basic_unit());
				
				
				flush();

				comDao.decreaseStockByStockID(invObj.getStock_id(), invObj.getQuantity_in_basic_unit());
				
				invList.add(invObj);
			}
			
			obj.setInventory_details_list(invList);

			getSession().save(obj);
			
			
			Iterator itr3=obj.getInventory_details_list().iterator();
			while (itr3.hasNext()) {
				invObj=(SalesInventoryDetailsModel) itr3.next();
				
				String[] stks=invObj.getStock_ids().split(",");
				for (String string : stks) {
//					if(string.length()>2)
//					getSession().save(new SalesStockMapModel(obj.getId(), invObj.getId(), Long.parseLong(string.split(":")[0]),
//							Double.parseDouble(string.split(":")[1])));
				}
				flush();
			}
			
			
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
	
	

	public SalesModel getSale(long id) throws Exception {
		SalesModel pur = null;
		try {
			begin();
			pur = (SalesModel) getSession().get(SalesModel.class, id);
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

	public void update(SalesModel newobj, TransactionModel transaction, double payingAmt)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from SalesModel a join a.inventory_details_list b "
							+ "where a.id=" + newobj.getId()).list();

			// Delete

			List old_AcctnotDeletedLst = new ArrayList();

			List AcctDetLst = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id())
					.list();
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = AcctDetLst.iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

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

			}
			
			
//			SalesBean smdl = (SalesBean) getSession().createQuery(
//					"select new com.inventory.sales.bean.SalesBean(customer.id,payment_amount)  from " +
//					"SalesModel where id=" + newobj.getId()).uniqueResult();
//			
//			if(smdl.getPayedAmt()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", smdl.getPayedAmt()).setLong("id", smdl.getCustomer_id()).executeUpdate();
			
			

			// SalesModel obj=(SalesModel) getSession().get(SalesModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from SalesModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			SalesInventoryDetailsModel invObj;
			List list;
			Iterator<SalesInventoryDetailsModel> it = oldLst.iterator();
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
									"update SalesInventoryDetailsModel set balance=balance+:qty, quantity_in_basic_unit=quantity_in_basic_unit+:qtybu where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
							.executeUpdate();
				}

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				
				flush();
				
				// For Stock Update
				comDao.increaseStockByStockID(invObj.getStock_id(), invObj.getQuantity_in_basic_unit());

			}

			// getSession().delete(obj);

			flush();

			// Save

			List<SalesInventoryDetailsModel> invList = new ArrayList<SalesInventoryDetailsModel>();
			Iterator<SalesInventoryDetailsModel> it1 = newobj
					.getInventory_details_list().iterator();
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
									"update SalesInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
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
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				invObj.setStock_ids(invObj.getStock_id()+":"+invObj.getQuantity_in_basic_unit());
				
				flush();
				
				comDao.decreaseStockByStockID(invObj.getStock_id(), invObj.getQuantity_in_basic_unit());
				
				
				invList.add(invObj);
			}

			newobj.setInventory_details_list(invList);

			// Transaction Related

			getSession().update(transaction);
			
			flush();

			Iterator<TransactionDetailsModel> aciter1 = transaction
					.getTransaction_details_list().iterator();
			while (aciter1.hasNext()) {
				tr = aciter1.next();

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
			
			
//			if(payingAmt!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", payingAmt).setLong("id", newobj.getCustomer().getId()).executeUpdate();
			

			// Transaction Related

			getSession().update(newobj);
			flush();

			getSession()
					.createQuery(
							"delete from SalesInventoryDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

			getSession()
					.createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
					.executeUpdate();
			
			
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id")
			.setLong("id", newobj.getId()).executeUpdate();

			flush();
			
			Iterator itr3=newobj.getInventory_details_list().iterator();
			while (itr3.hasNext()) {
				invObj=(SalesInventoryDetailsModel) itr3.next();
				
				String[] stks=invObj.getStock_ids().split(",");
				for (String string : stks) {
//					if(string.length()>2)
//					getSession().save(new SalesStockMapModel(newobj.getId(), invObj.getId(), Long.parseLong(string.split(":")[0]),
//							Double.parseDouble(string.split(":")[1])));
				}
				flush();
			}
			
			

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
			SalesModel obj = (SalesModel) getSession()
					.get(SalesModel.class, id);

			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

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
			
//			if(obj.getPayment_amount()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", obj.getPayment_amount()).setLong("id", obj.getCustomer().getId()).executeUpdate();
			

			getSession().delete(transObj);
			
			flush();

			// Transaction Related

			SalesInventoryDetailsModel invObj;
			List list;
			Iterator<SalesInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
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
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				
				flush();
				
				comDao.increaseStockByStockID(invObj.getStock_id(), invObj.getQuantity_in_basic_unit());

			}

			getSession().delete(obj);
			
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id")
			.setLong("id", obj.getId()).executeUpdate();
			
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
	
	
	
	public void cancel(long id) throws Exception {
		try {
			begin();
			SalesModel obj = (SalesModel) getSession()
					.get(SalesModel.class, id);

			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

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
			
//			if(obj.getPayment_amount()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", obj.getPayment_amount()).setLong("id", obj.getCustomer().getId()).executeUpdate();
			

			getSession().delete(transObj);
			
			flush();

			// Transaction Related

			SalesInventoryDetailsModel invObj;
			List list;
			Iterator<SalesInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
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
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				
				flush();
				
				comDao.increaseStockByStockID(invObj.getStock_id(), invObj.getQuantity_in_basic_unit());

			}

			getSession().createQuery("update SalesModel set active=false where id=:id")
			.setParameter("id", obj.getId()).executeUpdate();
			
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id")
			.setLong("id", obj.getId()).executeUpdate();
			
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
	

	public List getAllGRVSalesNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel where office.id=:ofc and active=true and type=2 order by id desc")
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
									+ " from SalesOrderModel a join a.inventory_details_list b where a.office.id=:ofc and active=true and b.balance>0 and a.customer.id=:cust and (status=:sts1 or status=:sts2 or status=:sts3) group by a.id")
					.setParameter("ofc", office_id).setLong("sts1", SConstants.statuses.SALES_ORDER_DIRECT).setLong("sts2", SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED).setLong("sts3", SConstants.statuses.SALES_ORDER_ONLINE_APPROVED)
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
									+ "b.tax.id, b.tax_amount, b.tax_percentage, b.qunatity, b.unit_price, b.discount_amount, b.balance , b.cess_amount , b.quantity_in_basic_unit)"
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
									+ " id, concat(item.name,  ' Bal: ' , balance )) from ItemStockModel where item.id=:itm and quantity>0")
//									+ " id, concat('Stk : ', id,' : ', item.name, ' Exp : ' , expiry_date, ' Bal: ' , balance )) from ItemStockModel where item.id=:itm and quantity>0")
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
	
	
	

	@SuppressWarnings("finally")
	public List getAllItemStocks(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ', ' Bal: ' , balance, ' ', item.unit.symbol )) from ItemStockModel where item.office.id=:ofc  order by item.name")
//									+ " id, concat(item.name,' ( ',item.item_code,' ) ','Stk : ', id, ' Bal: ' , balance,' : ',  ' Exp : ' , expiry_date )) from ItemStockModel where quantity>0")
									.setLong("ofc", ofc_id)
					.list();
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
	
	
	public List<Object> getAllSalesDetailsForCustomer(long ledgerId,
			Date fromDate, Date last_payable_date) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(sales_number,payment_amount,amount)"
									+ " from SalesModel where customer.id=:custId and date between :fromDate and :lastdate" +
									" and status=2 and active=true")
					.setParameter("custId", ledgerId)
					.setParameter("fromDate", fromDate)
					.setParameter("lastdate", last_payable_date).list();
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
	
	
	
	public List<Object> getAllSalesIDsForCustomer(long ledgerId,
			Date fromDate, Date toDate) throws Exception {
		
		try {
			
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.sales.model.SalesModel(id,concat(sales_number, '  - ', date ))"
									+ " from SalesModel where customer.id=:custId and date between :fromDate and :toDate" +
									" and status>1 and active=true")
					.setParameter("custId", ledgerId)
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
	
	
	
	
	
	public List<Object> getAllSalesNumbersByDate(long officeId,
			Date fromDate, Date toDate, String condition1) throws Exception {

		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel where date between :fromDate and :toDate  and type=2 "+condition)
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
	
	

	//Added By Anil
	public List<Object> getAllGRVSalesNumbersForSupplier(long officeId, long custId,
			Date fromDate, Date toDate,String condition1) throws Exception {

		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel where customer.id=:custId and date between :fromDate and :toDate and type=2"+condition)
					.setParameter("custId", custId)
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

	public long getMaxValue(long ofcId) throws Exception {
		long max=0;
		try {
			begin();
			String condition="";
			Object obj = getSession()
					.createQuery(
							"select max(id) from ItemStockModel where item.office.id=:ofc")
					.setParameter("ofc", ofcId).uniqueResult();
			commit();
			
			if(obj!=null)
				max=(Long) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return max;
		}
	}
	
	
	public long getMaxCustomerId(long ofcId) throws Exception {
		long max=0;
		try {
			begin();
			String condition="";
			Object obj = getSession()
					.createQuery(
							"select max(id) from CustomerModel where ledger.office.id=:ofc")
					.setParameter("ofc", ofcId).uniqueResult();
			commit();
			
			if(obj!=null)
				max=(Long) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return max;
		}
	}
	
	
	
	public List getSalesRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		resultList=null;
		try {
			
			begin();
			
			resultList = getSession()
					.createQuery("select new com.inventory.sales.bean.SalesInventoryDetailsPojo(" +
							"a.customer.name, b.unit_price, a.date)   from SalesModel a join a.inventory_details_list b"
									+ " where b.item.id=:itemid and b.unit.id=:unit and a.office.id=:ofc order by a.date desc")
									.setLong("itemid", item_id).setLong("ofc", office_id).setLong("unit", unit_id).list();
				
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
	
	
	
	public List getAllItems(long ofc_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			begin();
			Iterator it = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' ))"
									+ " from ItemModel where office.id=:ofc order by name")
					.setParameter("ofc", ofc_id).list().iterator();
			
			double bal=0;
			while(it.hasNext()) {
				ItemModel obj=(ItemModel) it.next();
				
				bal=(Double) getSession().createQuery("select coalesce(sum(balance),0) from ItemStockModel where item.id=:itm and status=3")
					.setLong("itm", obj.getId()).uniqueResult();
					
//				if(bal>0) {
					obj.setName(obj.getName()+bal);
					resultList.add(obj);
//				}
			}
			
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
	public List getStocks(long item_id) throws Exception {
		try {
			resultList=new ArrayList();
			
			begin();
			
			resultList.addAll(getSession().createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3  order by id")
									.setLong("itm", item_id).list());
			
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
	
	
	
	public boolean isSalesNoExists(Object salesId,long salesNo, long officeID) throws Exception {
		boolean flag=false;
		try {
			String condition="";
			if(salesId!=null&&!salesId.equals("")&&(Long)salesId!=0)
				condition=" and id!="+(Long)salesId;
			
			begin();
			resultList = getSession()
					.createQuery("from SalesModel where office.id=:office and sales_number=:saleNo and active=true"+condition)
					.setParameter("office", officeID)
					.setParameter("saleNo", salesNo).list();
			commit();
			
			if(resultList!=null&&resultList.size()>0)
				flag=true;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return flag;
	}
	
	
	
	
	
	

}
