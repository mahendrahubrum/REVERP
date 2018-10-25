package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.bean.SalesBean;
import com.inventory.sales.model.TailoringSalesInventoryDetailsModel;
import com.inventory.sales.model.TailoringSalesModel;
import com.inventory.tailoring.model.ProductionUnitModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 1, 2013
 */

public class TailoringSalesDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = 7453940143393746135L;

	List resultList = new ArrayList();
	
//	CommonMethodsDao comDao=new CommonMethodsDao();
	
	ItemDao itemDao=new ItemDao();

	public long save(TailoringSalesModel obj, TransactionModel transaction, double payingAmt)
			throws Exception {

		try {

			begin();

			// Transaction Related

			getSession().save(transaction);

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
			
			
			if(payingAmt!=0)
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
					.setDouble("amt", payingAmt).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			
			
			// Transaction Related

			obj.setTransaction_id(transaction.getTransaction_id());

			TailoringSalesInventoryDetailsModel invObj;
			String stockIDs;
			List<TailoringSalesInventoryDetailsModel> invList = new ArrayList<TailoringSalesInventoryDetailsModel>();
			Iterator<TailoringSalesInventoryDetailsModel> it = obj
					.getTailoring_inventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					getSession()
							.createQuery(
									"update TailoringSalesInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu  where id=:id")
							.setParameter("id", invObj.getId())
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
							.executeUpdate();

					invObj.setId(0);
				}
				
				getSession()
				.createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
				.setParameter("id", invObj.getItem().getId())
				.setParameter("qty", invObj.getQuantity_in_basic_unit())
				.executeUpdate();
				
				

//				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
//				invObj.setStock_ids(stockIDs);
				
				invList.add(invObj);
			}

			obj.setTailoring_inventory_details_list(invList);

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
	
	

	public TailoringSalesModel getSale(long id) throws Exception {
		TailoringSalesModel pur = null;
		try {
			begin();
			pur = (TailoringSalesModel) getSession().get(TailoringSalesModel.class, id);
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

	public void update(TailoringSalesModel newobj, TransactionModel transaction, double payingAmt)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from TailoringSalesModel a join a.tailoring_inventory_details_list b "
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
			
			
			SalesBean smdl = (SalesBean) getSession().createQuery(
					"select new com.inventory.sales.bean.SalesBean(customer.id,payment_amount)  from " +
					"TailoringSalesModel where id=" + newobj.getId()).uniqueResult();
			
//			if(smdl.getPayedAmt()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", smdl.getPayedAmt()).setLong("id", smdl.getCustomer_id()).executeUpdate();
			
			
			
			// TailoringSalesModel obj=(TailoringSalesModel) getSession().get(TailoringSalesModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from TailoringSalesModel a join a.tailoring_inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			TailoringSalesInventoryDetailsModel invObj;
			List list;
			Iterator<TailoringSalesInventoryDetailsModel> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.tailoring_inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					
					getSession()
							.createQuery(
									"update TailoringSalesInventoryDetailsModel set balance=balance+:qty, quantity_in_basic_unit=quantity_in_basic_unit+:qtybu where id in (:lst)")
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

				// For Stock Update
//				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());

			}

			// getSession().delete(obj);

			flush();

			// Save

			List<TailoringSalesInventoryDetailsModel> invList = new ArrayList<TailoringSalesInventoryDetailsModel>();
			String stockIDs;
			Iterator<TailoringSalesInventoryDetailsModel> it1 = newobj.getTailoring_inventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.tailoring_inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();
					
					
					getSession()
							.createQuery(
									"update TailoringSalesInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
							.executeUpdate();

					/*
					 * getSession().createQuery(
					 * "update TailoringSalesInventoryDetailsModel set balance=balance-:qty where id=:id"
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

//				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				
//				invObj.setStock_ids(stockIDs);

				invList.add(invObj);
			}

			newobj.setTailoring_inventory_details_list(invList);

			// Transaction Related
			
			flush();

			getSession().update(transaction);

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
			
			
			if(payingAmt!=0)
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
					.setDouble("amt", payingAmt).setLong("id", newobj.getCustomer().getId()).executeUpdate();
			

			// Transaction Related

			getSession().update(newobj);
			flush();

			getSession().createQuery(
							"delete from TailoringSalesInventoryDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

			getSession().createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
					.executeUpdate();

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
			TailoringSalesModel obj = (TailoringSalesModel) getSession()
					.get(TailoringSalesModel.class, id);

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

			getSession().delete(transObj);

			// Transaction Related

			TailoringSalesInventoryDetailsModel invObj;
			List list;
			Iterator<TailoringSalesInventoryDetailsModel> it = obj
					.getTailoring_inventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.tailoring_inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					getSession()
							.createQuery(
									"update TailoringSalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
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
				
//				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());

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
	
	public void cancel(long id) throws Exception {
		try {
			begin();
			TailoringSalesModel obj = (TailoringSalesModel) getSession()
					.get(TailoringSalesModel.class, id);

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

			getSession().delete(transObj);

			// Transaction Related

			TailoringSalesInventoryDetailsModel invObj;
			List list;
			Iterator<TailoringSalesInventoryDetailsModel> it = obj
					.getTailoring_inventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					list = getSession()
							.createQuery(
									"select b.id from SalesOrderModel a join a.tailoring_inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					getSession()
							.createQuery(
									"update TailoringSalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
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
				
//				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());

			}
			obj.setActive(false);
			getSession().update(obj);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}
	
	
	public List getAllSalesNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.TailoringSalesModel(id,cast(sales_number as string) )"
									+ " from TailoringSalesModel where office.id=:ofc and type<2 and active=true order by sales_number desc")
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
									+ " from SalesOrderModel a join a.inventory_details_list b where a.office.id=:ofc and b.balance>0 and a.customer.id=:cust and (status=:sts1 or status=:sts2 or status=:sts3) group by a.id")
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
									+ " from SalesOrderModel a join a.tailoring_inventory_details_list b where a.id in (:ids) and b.balance!=0")
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
	
	
	
	public List getAllItemsWithRealStck(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			resultList= getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )' ))"
									+ " from ItemModel where office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
			
//			double bal=0;
//			while(it.hasNext()) {
//				ItemModel obj=(ItemModel) it.next();
//				
//				bal=(Double) getSession().createQuery("select coalesce(sum(balance),0) from ItemStockModel where item.id=:itm and status=3")
//					.setLong("itm", obj.getId()).uniqueResult();
//					
//				obj.setName(obj.getName()+(obj.getCurrent_balalnce()-bal));
//				resultList.add(obj);
//			}
			
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
							"select new com.inventory.sales.model.TailoringSalesModel(sales_number,payment_amount,amount)"
									+ " from TailoringSalesModel where customer.id=:custId and date between :fromDate and :lastdate" +
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
							"select new com.inventory.sales.model.TailoringSalesModel(id,concat(sales_number, '  - ', date ))"
									+ " from TailoringSalesModel where customer.id=:custId and date between :fromDate and :toDate" +
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
							"select new com.inventory.sales.model.TailoringSalesModel(id,cast(sales_number as string) )"
									+ " from TailoringSalesModel where date between :fromDate and :toDate and active=true "+condition)
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
			Date fromDate, Date toDate, String condition1) throws Exception {

		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.TailoringSalesModel(id,cast(sales_number as string) )"
									+ " from TailoringSalesModel where customer.id=:custId and date between :fromDate and :toDate and active=true "+condition)
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
						.createQuery(
								"select new com.inventory.sales.bean.SalesInventoryDetailsPojo(" +
								"a.customer.name, b.unit_price, a.date)   from TailoringSalesModel a join a.tailoring_inventory_details_list b"
										+ " where b.item.id=:itemid and b.unit.id=:unit and a.office.id=:ofc and active=true  order by a.date desc")
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



	public String getSpecString(long id) throws Exception {
		String str="";
		try {
			
			begin();
				
			str = (String) getSession()
						.createQuery(
								"select stock_ids from TailoringSalesInventoryDetailsModel where id=:id")
										.setLong("id", id).uniqueResult();
				
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
		return str;
	}



	public String getSpecFieldString(long id) throws Exception {
		String str="";
		try {
			
			begin();
				
			str = (String) getSession()
						.createQuery(
								"select spec_field_ids from TailoringSalesInventoryDetailsModel where id=:id")
										.setLong("id", id).uniqueResult();
				
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
		return str;
	}

	public List<Object> getAllSalesIDsForCustomer(long ledgerId, Date fromDate,
			Date toDate, boolean isCreateNew) throws Exception {

		try {

			String criteria = "";
			if (isCreateNew)
				criteria = " and payment_done='N'";

			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.TailoringSalesModel(id,concat(sales_number, ' - ', date ))"
									+ " from TailoringSalesModel where customer.id=:custId and date between :fromDate and :toDate"
									+ " and status>1 and active=true " + criteria)
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
		}
		return resultList;
	}



	public void updateProductionUnit(long salesId,long prodUnitId) throws Exception {
		try {

			begin();
			TailoringSalesModel sale=(TailoringSalesModel) getSession().get(TailoringSalesModel.class, salesId);
			sale.setProductionUnit(new ProductionUnitModel(prodUnitId));
			getSession().update(sale);
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



	public long getSalesIdFromBarcode(String barcode, long officeID) throws Exception {
		long id=0;
		try {
			begin();
			List lst = getSession()
					.createQuery(
							"select id from TailoringSalesModel where barcode=:barc and office.id=:ofc")
					.setParameter("barc", barcode)
					.setParameter("ofc", officeID).list();
			
			commit();
			
			if(lst!=null&&lst.size()>0){
				id=(Long) lst.get(0);
			}
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return id;
	}

}
