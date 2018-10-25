package com.hotel.service.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.hotel.service.model.HotelSalesInventoryDetailsModel;
import com.hotel.service.model.HotelSalesModel;
import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.bean.SalesBean;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */

public class HotelSalesDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7453940143393746135L;

	List resultList = new ArrayList();
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	ItemDao itemDao=new ItemDao();

	public long save(HotelSalesModel obj, TransactionModel transaction, double payingAmt)
			throws Exception {

		try {

			begin();
			
			if(obj.getCash_pay_id()!=null)
				getSession().save(obj.getCash_pay_id());
			
			
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
			
			
//			if(payingAmt!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", payingAmt).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			
			
			// Transaction Related

			obj.setTransaction_id(transaction.getTransaction_id());

			HotelSalesInventoryDetailsModel invObj;
			String stockIDs;
			List<HotelSalesInventoryDetailsModel> invList = new ArrayList<HotelSalesInventoryDetailsModel>();
			Iterator<HotelSalesInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					getSession()
							.createQuery(
									"update HotelSalesInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu  where id=:id")
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
				
				

				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				invObj.setStock_ids(stockIDs);
				
				invList.add(invObj);
			}

			obj.setInventory_details_list(invList);

			getSession().save(obj);
			
			flush();
			
			getSession().createQuery("update TableModel set status=:sts where id=:id")
			.setParameter("id", obj.getTableId()).setParameter("sts",SConstants.tableStatus.AWAITING_CLEANING).executeUpdate();
			
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
	
	
	public long saveHold(HotelSalesModel obj) throws Exception {
		
		try {
			
			begin();
			
			obj.setTransaction_id(0);
			obj.setStatus(3);

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
	
	
	
	public long updateHold(HotelSalesModel obj) throws Exception {
		
		try {
			
			begin();
			getSession().update(obj);
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
	
	
	
	
	

	public HotelSalesModel getSale(long id) throws Exception {
		HotelSalesModel pur = null;
		try {
			begin();
			pur = (HotelSalesModel) getSession().get(HotelSalesModel.class, id);
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

	public void update(HotelSalesModel newobj, TransactionModel transaction, double payingAmt,boolean trans_exist)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from HotelSalesModel a join a.inventory_details_list b "
							+ "where a.id=" + newobj.getId()).list();
			
			if(newobj.getCash_pay_id()!=null) {
				if(newobj.getCash_pay_id().getId()!=0)
					getSession().update(newobj.getCash_pay_id());
				else
					getSession().save(newobj.getCash_pay_id());
			}
				
			

			// Delete

			List old_AcctnotDeletedLst = new ArrayList();
			TransactionDetailsModel tr;
			List AcctDetLst=null;
			if(trans_exist) {
				AcctDetLst = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "
							+ "where a.id=" + transaction.getTransaction_id()).list();
			
				
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
			}
			
			SalesBean smdl = (SalesBean) getSession().createQuery(
					"select new com.inventory.sales.bean.SalesBean(customer.id,payment_amount)  from " +
					"HotelSalesModel where id=" + newobj.getId()).uniqueResult();
			
//			if(smdl.getPayedAmt()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", smdl.getPayedAmt()).setLong("id", smdl.getCustomer_id()).executeUpdate();
			
			

			// HotelSalesModel obj=(HotelSalesModel) getSession().get(HotelSalesModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from HotelSalesModel a join a.inventory_details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			HotelSalesInventoryDetailsModel invObj;
			List list;
			Iterator<HotelSalesInventoryDetailsModel> it = oldLst.iterator();
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
									"update HotelSalesInventoryDetailsModel set balance=balance+:qty, quantity_in_basic_unit=quantity_in_basic_unit+:qtybu where id in (:lst)")
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
				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());

			}

			// getSession().delete(obj);

			flush();

			// Save

			List<HotelSalesInventoryDetailsModel> invList = new ArrayList<HotelSalesInventoryDetailsModel>();
			String stockIDs;
			Iterator<HotelSalesInventoryDetailsModel> it1 = newobj
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
									"update HotelSalesInventoryDetailsModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity()).setParameter("qtybu", invObj.getQuantity_in_basic_unit())
							.executeUpdate();

					/*
					 * getSession().createQuery(
					 * "update HotelSalesInventoryDetailsModel set balance=balance-:qty where id=:id"
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

				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				
				invObj.setStock_ids(stockIDs);

				invList.add(invObj);
			}

			newobj.setInventory_details_list(invList);

			// Transaction Related

			if(trans_exist)
				getSession().update(transaction);
			else
				getSession().save(transaction);
			
			newobj.setTransaction_id(transaction.getTransaction_id());

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

			getSession().createQuery(
							"delete from HotelSalesInventoryDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

			if(old_AcctnotDeletedLst!=null && old_AcctnotDeletedLst.size()>0)
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
	
	
	public void updateAsConfirm(HotelSalesModel newobj) throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from HotelSalesModel a join a.inventory_details_list b where a.id=" + newobj.getId()).list();
			
			if(newobj.getCash_pay_id()!=null) {
				if(newobj.getCash_pay_id().getId()!=0)
					getSession().update(newobj.getCash_pay_id());
				else
					getSession().save(newobj.getCash_pay_id());
			}
				
			
			// Delete
			

//			List oldLst = getSession()
//					.createQuery(
//							"select b from HotelSalesModel a join a.inventory_details_list b where a.id=:id")
//					.setLong("id", newobj.getId()).list();

			// getSession().delete(obj);

			flush();

			// Save

			HotelSalesInventoryDetailsModel invObj;
			List<HotelSalesInventoryDetailsModel> invList = new ArrayList<HotelSalesInventoryDetailsModel>();
			String stockIDs;
			Iterator<HotelSalesInventoryDetailsModel> it1 = newobj
					.getInventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				
				invObj.setStock_ids(stockIDs);

				invList.add(invObj);
			}

			newobj.setInventory_details_list(invList);

			// Transaction Related


			// Transaction Related

			getSession().update(newobj);
			flush();

			getSession().createQuery(
							"delete from HotelSalesInventoryDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
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
	
	
	
	public long saveAsConfirm(HotelSalesModel newobj) throws Exception {
		try {

			begin();

			
			// Save

			HotelSalesInventoryDetailsModel invObj;
			List<HotelSalesInventoryDetailsModel> invList = new ArrayList<HotelSalesInventoryDetailsModel>();
			String stockIDs;
			Iterator<HotelSalesInventoryDetailsModel> it1 = newobj
					.getInventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				
				invObj.setStock_ids(stockIDs);

				invList.add(invObj);
			}

			newobj.setInventory_details_list(invList);

			getSession().save(newobj);
			
			getSession().createQuery("update TableModel set status=:sts where id=:id")
			.setParameter("id", newobj.getTableId()).setParameter("sts",SConstants.tableStatus.AWAITING_CLEANING).executeUpdate();

			commit();
			
			return newobj.getId();

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
			HotelSalesModel obj = (HotelSalesModel) getSession()
					.get(HotelSalesModel.class, id);
			
			if(obj.getCash_pay_id()!=null)
				getSession().delete(obj.getCash_pay_id());
				
			// Transaction Related
			
			if(obj.getStatus()==1) {

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
				
				
				flush();
	
				getSession().delete(transObj);
				
			
			}

			// Transaction Related
			
			if(obj.getStatus()==1 || obj.getStatus()==2) {

				HotelSalesInventoryDetailsModel invObj;
				List list;
				Iterator<HotelSalesInventoryDetailsModel> it = obj
						.getInventory_details_list().iterator();
				while (it.hasNext()) {
					invObj = it.next();
	
					getSession()
							.createQuery(
									"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", invObj.getItem().getId())
							.setParameter("qty", invObj.getQuantity_in_basic_unit())
							.executeUpdate();
					
					comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
					
				}
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
	
	
	
	
	public void cancelOrder(long id) throws Exception {
		try {
			begin();
			HotelSalesModel obj = (HotelSalesModel) getSession()
					.get(HotelSalesModel.class, id);
			
			if(obj.getCash_pay_id()!=null)
				getSession().delete(obj.getCash_pay_id());
				
			// Transaction Related
			
			if(obj.getStatus()==1) {

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
				
				
				flush();
	
				getSession().delete(transObj);
				
			
			}

			// Transaction Related
			
			if(obj.getStatus()==1 || obj.getStatus()==2) {

				HotelSalesInventoryDetailsModel invObj;
				List list;
				Iterator<HotelSalesInventoryDetailsModel> it = obj
						.getInventory_details_list().iterator();
				while (it.hasNext()) {
					invObj = it.next();
	
					getSession()
							.createQuery(
									"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", invObj.getItem().getId())
							.setParameter("qty", invObj.getQuantity_in_basic_unit())
							.executeUpdate();
					
					comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
					
				}
			}
			obj.setStatus(4);
			getSession().update(obj);
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
							"select new com.hotel.service.model.HotelSalesModel(id,cast(sales_number as string) )"
									+ " from HotelSalesModel where office.id=:ofc and status<4 order by sales_number desc")
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
	
	
	
	public List getAllSalesItems(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel where office.id=:ofc and (affect_type=1 or affect_type=2)")
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
							"select new com.hotel.service.model.HotelSalesModel(sales_number,payment_amount,amount)"
									+ " from HotelSalesModel where customer.id=:custId and date between :fromDate and :lastdate" +
									" and status=2")
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
							"select new com.hotel.service.model.HotelSalesModel(id,concat(sales_number, '  - ', date ))"
									+ " from HotelSalesModel where customer.id=:custId and date between :fromDate and :toDate" +
									" and status>1")
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
							"select new com.hotel.service.model.HotelSalesModel(id,cast(sales_number as string) )"
									+ " from HotelSalesModel where date between :fromDate and :toDate"+condition)
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
							"select new com.hotel.service.model.HotelSalesModel(id,cast(sales_number as string) )"
									+ " from HotelSalesModel where customer.id=:custId and date between :fromDate and :toDate"+condition)
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
								"a.customer.name, b.unit_price, a.date)   from HotelSalesModel a join a.inventory_details_list b"
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


	public List<Object> getAllSalesNumbersOfTable(long officeId, long tableId,
			Date fromDate, Date toDate,	String condition1) throws Exception {
		resultList=null;
		try {
			
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.HotelSalesModel(id,cast(sales_number as string) )"
									+ " from HotelSalesModel where tableId=:tabl and date between :fromDate and :toDate  and (type=0 or type=1) and  active=true"+condition)
					.setParameter("tabl", tableId)
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
	

	
	public List<Object> getSalesDetailsReport(long salesId, long tableID,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List list = null;
		try {
			begin();
			String condition="";
			if (salesId != 0) {
				condition += " and id=" + salesId;
			}
			if (tableID != 0) {
				condition += " and tableId=" + tableID;
			}
			list = getSession()
					.createQuery(
							"from HotelSalesModel where date>=:fromDate and date<=:toDate   and (type=0 or type=1)"
									+condition+ " and office.id=:ofc")
					.setParameter("ofc", officeId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}
	
	CommonMethodsDao methodsDao = new CommonMethodsDao();
	@SuppressWarnings("unchecked")
	public List<Object> getItemWiseSalesDetails(long itemID, 
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getSItemWise(fromDate, toDate, condition1
						+ condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getTotal();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						rptObj.setDescription(methodsDao.getItemBalanceAtDate(
								itemObj.getId(), toDate)
								+ " "
								+ itemObj.getUnit().getSymbol());
						resultList.add(rptObj);
					}
				}
			} else {

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = getSItemWise(fromDate, toDate, condition1
							+ condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getTotal();
							rptObj.setTotal(sum);
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setDescription(methodsDao
									.getItemBalanceAtDate(itemObj.getId(),
											toDate)
									+ " " + itemObj.getUnit().getSymbol());
							resultList.add(rptObj);
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}	
	
	@SuppressWarnings("unchecked")
	public List<Object> showItemWiseSalesDetails(long itemID,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = showSItemWise(fromDate, toDate, condition1
						+ condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getTotal();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						rptObj.setDescription(methodsDao.getItemBalanceAtDate(
								itemObj.getId(), toDate)
								+ " "
								+ itemObj.getUnit().getSymbol());
						resultList.add(rptObj);
					}
				}
			} else {

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = showSItemWise(fromDate, toDate, condition1
							+ condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getTotal();
							rptObj.setTotal(sum);
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setDescription(methodsDao
									.getItemBalanceAtDate(itemObj.getId(),
											toDate)
									+ " " + itemObj.getUnit().getSymbol());
							resultList.add(rptObj);
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> showItemWiseSalesDetailsConsolidated(long itemID, Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			begin();
			String condition1 = " and a.office.id=" + officeId;
			String cdn="";
			if (itemID != 0) {
				cdn+=" and a.id="+itemID;
			}
			List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts"+cdn)
										.setParameter("ofc", officeId)
										.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
										.list();
			if(itemsList.size()>0){
				Iterator itr=itemsList.iterator();
				while (itr.hasNext()) {
					ItemModel itemObj = (ItemModel) itr.next();
					
					List list=getSession().createQuery(" from HotelSalesModel a join a.inventory_details_list b where date between :fromDate and :toDate and b.item.id=:item"
							+ condition1)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate)
							.setParameter("item", itemObj.getId()).list();
					if(list.size()>0)
						resultList.addAll(getSession().createQuery(
								"select new com.webspark.bean.ReportBean(b.item.id,b.item.name, coalesce(sum(b.quantity_in_basic_unit),0))" +
								" from HotelSalesModel a join a.inventory_details_list b where date between :fromDate and :toDate and b.item.id=:item"
								+ condition1 + " order by a.date")
						.setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate)
						.setParameter("item", itemObj.getId()).list());
				}
			}
			commit();	
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	public List showSItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			// Constr 42
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(a.id,b.item.name, a.customer, b.qunatity, b.quantity_in_basic_unit,a.date,b.unit.symbol,b.unit_price,a.sales_number) " +
							"from HotelSalesModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}

	public List getSItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.name, a.customer, b.qunatity, b.quantity_in_basic_unit,a.date,b.unit.symbol,b.unit_price,a.sales_number) " +
							"from HotelSalesModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}


	public List getAllSalesItemNames(long ofc_id, long sub_gp_id) throws Exception {
		resultList=null;
		try {
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, name)"
									+ " from ItemModel where office.id=:ofc and affect_type=2 and sub_group.id=:sub")
					.setLong("ofc", ofc_id).setLong("sub", sub_gp_id).list();
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
	

	public List getAllItemNamesUnderType(long ofc_id, long sub_gp_id,List affType) throws Exception {
		resultList=null;
		try {
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, name)"
									+ " from ItemModel where office.id=:ofc and affect_type in (:aff) and sub_group.id=:sub")
					.setLong("ofc", ofc_id).setLong("sub", sub_gp_id).setParameterList("aff", affType).list();
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
	
	public List getAllItemSubGroupsNamesWithSalesOnly(long organizationId,long officeID) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select a.sub_group "
									+ " from ItemModel a where a.sub_group.status=:val and (a.affect_type=2 or a.affect_type=1) and" +
									" a.sub_group.group.organization.id=:org and a.office.id=:ofc order by a.sub_group.name")
					.setParameter("org", organizationId)
					.setParameter("ofc", officeID)
					.setParameter("val",
							SConstants.statuses.ITEM_SUBGROUP_ACTIVE).list();
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
