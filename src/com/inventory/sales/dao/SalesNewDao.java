package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.StockRackMappingModel;
import com.inventory.sales.model.SalesNewInventoryDetailsModel;
import com.inventory.sales.model.SalesNewModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class SalesNewDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7453940143393746135L;

	List resultList = new ArrayList();
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	ItemDao itemDao=new ItemDao();

	public long save(SalesNewModel obj, TransactionModel transaction, double payingAmt, int update_rate_settings)
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
			
			SalesNewInventoryDetailsModel invObj;
			List<SalesNewInventoryDetailsModel> invList = new ArrayList<SalesNewInventoryDetailsModel>();
			Iterator<SalesNewInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				if (invObj.getOrder_id() != 0) {
					
					getSession().createQuery(
									"update SalesNewInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu  where id=:id")
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
				
				invObj.setStock_ids(invObj.getStk_id()+":"+invObj.getQuantity_in_basic_unit());
				
				decreaseRackQty(invObj.getRack_id(), invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				flush();

				comDao.decreaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				if(update_rate_settings!=0)
					comDao.updateConvertionQtyAndRate(update_rate_settings, invObj.getItem().getId(), 
							invObj.getUnit().getId(), obj.getSales_type(), invObj.getUnit_price(),
							(CommonUtil.roundNumber(invObj.getQuantity_in_basic_unit()/invObj.getQunatity())));
				
				invList.add(invObj);
			}

			obj.setInventory_details_list(invList);

			getSession().save(obj);
			
			
			Iterator itr3=obj.getInventory_details_list().iterator();
			while (itr3.hasNext()) {
				invObj=(SalesNewInventoryDetailsModel) itr3.next();
				
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
		}
		return obj.getId();
	}

	public SalesNewModel getSale(long id) throws Exception {
		SalesNewModel pur = null;
		try {
			begin();
			pur = (SalesNewModel) getSession().get(SalesNewModel.class, id);
			Hibernate.initialize(pur.getSales_expense_list());
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
		return pur;
	}

	public void update(SalesNewModel newobj, TransactionModel transaction, double payingAmt)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from SalesNewModel a join a.inventory_details_list b "
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
//					"SalesNewModel where id=" + newobj.getId()).uniqueResult();
//			
//			if(smdl.getPayedAmt()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", smdl.getPayedAmt()).setLong("id", smdl.getCustomer_id()).executeUpdate();
			
			

			// SalesNewModel obj=(SalesNewModel) getSession().get(SalesNewModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from SalesNewModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			SalesNewInventoryDetailsModel invObj;
			List list;
			Iterator<SalesNewInventoryDetailsModel> it = oldLst.iterator();
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
									"update SalesNewInventoryDetailsModel set balance=balance+:qty, quantity_in_basic_unit=quantity_in_basic_unit+:qtybu where id in (:lst)")
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
				comDao.increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());

				increaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				
			}

			// getSession().delete(obj);

			flush();

			// Save

			List<SalesNewInventoryDetailsModel> invList = new ArrayList<SalesNewInventoryDetailsModel>();
			Iterator<SalesNewInventoryDetailsModel> it1 = newobj
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
									"update SalesNewInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
							.executeUpdate();

					/*
					 * getSession().createQuery(
					 * "update SalesNewInventoryDetailsModel set balance=balance-:qty where id=:id"
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

				invObj.setStock_ids(invObj.getStk_id()+":"+invObj.getQuantity_in_basic_unit());
				
				flush();
				
				comDao.decreaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				decreaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());

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
							"delete from SalesNewInventoryDetailsModel where id in (:lst)")
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
				invObj=(SalesNewInventoryDetailsModel) itr3.next();
				
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
			SalesNewModel obj = (SalesNewModel) getSession()
					.get(SalesNewModel.class, id);

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

			SalesNewInventoryDetailsModel invObj;
			List list;
			Iterator<SalesNewInventoryDetailsModel> it = obj
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
									"update SalesNewInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();
				}
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				flush();
				
				comDao.increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				increaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
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
	
	public void cancelSale(long id) throws Exception {
		try {
			begin();
			SalesNewModel obj = (SalesNewModel) getSession()
					.get(SalesNewModel.class, id);
			
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
			
			SalesNewInventoryDetailsModel invObj;
			List list;
			Iterator<SalesNewInventoryDetailsModel> it = obj
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
							"update SalesNewInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
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
				
				comDao.increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				increaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
			}
			
			getSession().createQuery("update SalesNewModel set active=false where id=:id")
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

	public List getAllSalesNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesNewModel(id,cast(sales_number as string) )"
									+ " from SalesNewModel where office.id=:ofc and type<2 and active=true order by sales_number desc")
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
					.createQuery("select new com.inventory.sales.model.SalesOrderModel(a.id,concat(a.sales_order_number, ' ( ', a.date, ' )') )"
									+ " from SalesOrderModel a join a.inventory_details_list b where a.office.id=:ofc and b.balance>0 and a.customer.id=:cust and (status=:sts1 or status=:sts2 or status=:sts3) and a.active=true group by a.id")
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
	
	public long getSalesOrderNumberFromSOID(long id) throws Exception {
		long number=0;
		try {
			begin();
			number = (Long) getSession()
					.createQuery("select sales_order_number from SalesOrderModel where id=:id")
					.setLong("id", id).uniqueResult();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return number;
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
	
	
	public List getAllItems(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel where office.id=:ofc  order by name")
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
	
	
	public List getAllItemsWithRealStck(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			Iterator it = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' ), current_balalnce)"
									+ " from ItemModel where office.id=:ofc order by name")
					.setParameter("ofc", ofc_id).list().iterator();
			
			double bal=0;
			while(it.hasNext()) {
				ItemModel obj=(ItemModel) it.next();
				
				bal=(Double) getSession().createQuery("select coalesce(sum(balance),0) from ItemStockModel where item.id=:itm and status=3")
					.setLong("itm", obj.getId()).uniqueResult();
					
				obj.setName(obj.getName()+(obj.getCurrent_balalnce()-bal));
				resultList.add(obj);
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
	
	
	public List getAllItemsWithAllStocks(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ',current_balalnce ), current_balalnce)"
									+ " from ItemModel where office.id=:ofc order by name")
					.setParameter("ofc", ofc_id).list();
			
//			double bal=0;
//			while(it.hasNext()) {
//				ItemModel obj=(ItemModel) it.next();
//				
////				bal=(Double) getSession().createQuery("select coalesce(sum(balance),0) from ItemStockModel where item.id=:itm")
////					.setLong("itm", obj.getId()).uniqueResult();
////					
////				obj.setName(obj.getName()+(obj.getCurrent_balalnce()-bal));
//				resultList.add(obj);
//			}
			
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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return tran;
	}
	
	
	public List<Object> getAllSalesDetailsForCustomer(long ledgerId,
			Date fromDate, Date last_payable_date) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesNewModel(sales_number,payment_amount,amount)"
									+ " from SalesNewModel where customer.id=:custId and date between :fromDate and :lastdate" +
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
					.createQuery(
							"select new com.inventory.sales.model.SalesNewModel(id,concat(sales_number, '  - ', date ))"
									+ " from SalesNewModel where customer.id=:custId and date between :fromDate and :toDate" +
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
	
	
	
	
	
	
	

	//Added By Anil
	public List<Object> getAllSalesNumbersForSupplier(long officeId, long custId,
			Date fromDate, Date toDate) throws Exception {

		try {
			begin();
			String condition="";
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesNewModel(id,cast(sales_number as string) )"
									+ " from SalesNewModel where customer.id=:custId and date between :fromDate and :toDate and active=true")
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
							"a.customer.name, b.unit_price, a.date)   from SalesNewModel a join a.inventory_details_list b"
									+ " where b.item.id=:itemid and a.active=true and b.unit.id=:unit and a.office.id=:ofc order by a.date desc")
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
	
	
	public long getItemFromStock(long stockId) throws Exception {
		long itemId=0;
		try {
			begin();
				
			itemId = (Long) getSession()
						.createQuery("select item.id from ItemStockModel where id=:id").setParameter("id", stockId).uniqueResult();
			
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
		return itemId;
	}
	
	
	public ItemStockModel getItemFromBarcode(String code) throws Exception {
		List codeList=null;
		ItemStockModel stk=null;
		try {
			begin();
				
			codeList = getSession()
						.createQuery("from ItemStockModel where barcode=:code order by id desc").setParameter("code", code).list();
				
			commit();
			
			if(codeList!=null&&codeList.size()>0)
				stk=(ItemStockModel) codeList.get(0);
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return stk;
	}
	
	
	public void decreaseRackQty(long rack_id, long stk_id, double qty) throws Exception {
		try {
			
			if(rack_id!=0) {
				
				double bal=qty;
				StockRackMappingModel obj;
				
				List lst=getSession().createQuery("select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk and a.balance>0 "
						).setLong("rack", rack_id).setLong("stk", stk_id).list();
				
				if(lst==null || lst.size()<=0) {
					lst=getSession().createQuery("select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk order by a.id desc"
							).setLong("rack", rack_id).setLong("stk", stk_id).list();
					if(lst.size()>0) {
						obj=(StockRackMappingModel) lst.get(0);
						obj.setBalance(obj.getBalance()-bal);
						getSession().update(obj);
					}
				}
				else {
					for(int i=0; i<lst.size();i++) {
						obj=(StockRackMappingModel) lst.get(i);
						
						if(obj.getBalance()>bal) {
							obj.setBalance(CommonUtil.roundNumber(obj.getBalance()-bal));
							getSession().update(obj);
							flush();
							bal=0;
							break;
						}
						else {
							bal=CommonUtil.roundNumber(bal-obj.getBalance());
							obj.setBalance(0);
							getSession().update(obj);
							flush();
						}
						
						if(bal<=0) 
							break;
						
						if(i==(lst.size()-1) && bal>0) {
							obj.setBalance(obj.getBalance()-bal);
							getSession().update(obj);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public void increaseRackQty(long rack_id, long stk_id, double qty) throws Exception {
		try {
			
			if(rack_id!=0) {
				
				List lst=getSession().createQuery("select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk and a.balance!=0"
						).setLong("rack", rack_id).setLong("stk", stk_id).list();
				
				if(lst==null || lst.size()<=0) {
					lst=getSession().createQuery("select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk  order by a.id desc "
							).setLong("rack", rack_id).setLong("stk", stk_id).list();
				}
				
				if(lst.size()>0) {
					StockRackMappingModel obj=(StockRackMappingModel) lst.get(0);
					obj.setBalance(CommonUtil.roundNumber(obj.getBalance()+qty));
					getSession().update(obj);
				}
				
			}
		} catch (Exception e) {
			throw e;
		}
	}



	public List getSalesChartDetails(java.sql.Date fromDate,
			java.sql.Date toDate, long officeID) throws Exception {
		
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.bean.SalesChartBean(date,coalesce(sum(amount),0))" +
							" from SalesNewModel where office.id=:office and date between :fromDate and :toDate" +
									" and active=true group by date order by date asc")
					.setParameter("office", officeID)
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
	}
		return resultList;
	}



	public boolean isSalesNoExists(Object salesId,long salesNo, long officeID) throws Exception {
		boolean flag=false;
		try {
			String condition="";
			if(salesId!=null&&!salesId.equals("")&&(Long)salesId!=0)
				condition=" and id!="+(Long)salesId;
			
			begin();
			resultList = getSession()
					.createQuery("from SalesNewModel where office.id=:office and sales_number=:saleNo and active=true"+condition)
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



	public double getPurchaseRateFromStock(long stockId) throws Exception {
		double rate = 0;
		try {
			begin();
			Object obj =  getSession()
					.createQuery(
							"select COALESCE(rate,0) from ItemStockModel where id=:stockId")
					.setParameter("stockId", stockId).uniqueResult();
			commit();
			if(obj!=null)
				rate=(Double)obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return rate;
	}
	
}
