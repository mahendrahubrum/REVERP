package com.inventory.sales.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 * 
 * @Date Jan 9, 2014
 */

public class SalesReturnNewDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2130771295587292922L;
	List resultList = new ArrayList();
	CommonMethodsDao comDao=new CommonMethodsDao();

	public long returnItems(List<ItemStockModel> itemsList, SalesReturnModel salesReturnModel,
			TransactionModel transactionModel, HashMap<TransactionModel, PurchaseReturnModel> hash) throws Exception {

		long invDetailId = 0;
		
		try {
			
			PurchaseReturnModel purchaseReturnModel;
			TransactionModel purFinTran;
			long itemId=0;
			Iterator itret =hash.keySet().iterator();
			
			begin();
			
			
			getSession().save(transactionModel);
			
			flush();
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter=transactionModel.getTransaction_details_list().iterator();
			while(aciter.hasNext()) {
				tr=aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId())
								.executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
				.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId())
					.executeUpdate();
				
				flush();
				
			}
			salesReturnModel.setTransaction_id(transactionModel.getTransaction_id());
			
//			if(salesReturnModel.getPayment_amount()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", salesReturnModel.getPayment_amount()).setLong("id", salesReturnModel.getCustomer().getId()).executeUpdate();
			
			getSession().save(salesReturnModel);
			
			flush();
			
			
			Iterator<SalesReturnInventoryDetailsModel> it = salesReturnModel
					.getInventory_details_list().iterator();
			
			SalesReturnInventoryDetailsModel invObj;
			while (it.hasNext()) {
				
				invObj = it.next();
				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getGood_stock()+invObj.getStock_quantity()+invObj.getReturned_quantity())
						.executeUpdate();
				flush();

				/*invDetailId = (Long) getSession()
						.createQuery(
								"select b.id from SalesModel a join a.inventory_details_list b"
										+ " where a.id=:ID and b.item.id =:ItemID and a.office.id=:OffID")
						.setParameter("ID", invObj.getOrder_id())
						.setParameter("OffID", salesReturnModel.getOffice().getId())
						.setParameter("ItemID", invObj.getItem().getId())
						.uniqueResult();
				

				getSession()
						.createQuery(
								"update SalesInventoryDetailsModel set balance=:Balance where id=:ID")
						.setParameter("ID", invDetailId)
						.setParameter("Balance", invObj.getBalance())
						.executeUpdate();
				
				flush();*/
				
			}
			
			if(itemsList!=null&&itemsList.size()>0){
				
				Iterator<ItemStockModel> itemItr=itemsList.iterator();
				ItemStockModel stk;
				while (itemItr.hasNext()) {
					stk=itemItr.next();
					stk.setPurchase_id(salesReturnModel.getId());
					getSession().save(stk);
				}
				flush();
			}
			
			//Purchase Return Related============================================
			
			Iterator<TransactionDetailsModel> transactioniter;
			TransactionDetailsModel trd;
			while(itret.hasNext()){
				
				purFinTran=(TransactionModel) itret.next();
				purchaseReturnModel=(PurchaseReturnModel) hash.get(purFinTran);
				getSession().save(purFinTran);
				flush();
				transactioniter = purFinTran
						.getTransaction_details_list().iterator();
				while (transactioniter.hasNext()) {
					trd = transactioniter.next();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", trd.getAmount())
							.setLong("id", trd.getFromAcct().getId())
							.executeUpdate();
					
					getSession()
							.createQuery(
									"update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", trd.getAmount())
							.setLong("id", trd.getToAcct().getId()).executeUpdate();
					
					flush();
					
				}
				
				purchaseReturnModel.setTransaction_id(purFinTran.getTransaction_id());
				
				getSession().save(purchaseReturnModel);
				flush();
				Iterator<PurchaseReturnInventoryDetailsModel> ite = purchaseReturnModel
						.getInventory_details_list().iterator();
				PurchaseReturnInventoryDetailsModel pinvObj;
				while (ite.hasNext()) {
					
					pinvObj = ite.next();
					
					getSession().createQuery("update PurchaseReturnInventoryDetailsModel set order_id=:ordId where id=:id").
							setParameter("ordId",salesReturnModel.getId()).setParameter("id", pinvObj.getId()).executeUpdate();
					
					getSession().createQuery(
									"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", pinvObj.getItem().getId())
							.setParameter("qty", pinvObj.getQty_in_basic_unit())
							.executeUpdate();
					flush();
//					itemId = (Long) getSession()
//							.createQuery(
//									"select b.id from PurchaseModel a join a.inventory_details_list b where a.id=:ID and b.item.id =:ItemID")
//							.setParameter("ID", invObj.getOrder_id())
//							.setParameter("ItemID", invObj.getItem().getId())
//							.uniqueResult();
//
//					getSession()
//							.createQuery(
//									"update PurchaseInventoryDetailsModel set balance=:Balance where id=:ID")
//							.setParameter("ID", itemId)
//							.setParameter("Balance", invObj.getBalance())
//							.executeUpdate();
//
//					flush();
				}
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return salesReturnModel.getId();
	}
	
	
	public void delete(long id) throws Exception {
		try {
			begin();
			
			SalesReturnModel obj = (SalesReturnModel) getSession().get(
					SalesReturnModel.class, id);
			
			
			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
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
			
			flush();
			
			// Transaction Related
			
			SalesReturnInventoryDetailsModel invObj;
			List list;
			Iterator<SalesReturnInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getStock_quantity()+invObj.getGood_stock()+invObj.getReturned_quantity())
						.executeUpdate();
				
				flush();
			}
			
			PurchaseReturnModel pretObj;
			Iterator itr4= getSession().createQuery("select a.id from PurchaseReturnModel a join a.inventory_details_list b where b.order_id=:ret and a.status=2 group by a.id")
							.setLong("ret", id).list().iterator();
			while (itr4.hasNext()) {
				deletePurchReturn((Long) itr4.next());
			}
			
			flush();
			
			getSession().delete(obj);
			
			flush();
			
			getSession().createQuery("delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where status=:sts and purchase_id=:pid)")
									.setLong("sts",SConstants.SALES_RETURN_STOCK_STATUS).setLong("pid", id).executeUpdate();
			
			getSession().createQuery("delete from ItemStockModel where (status=:sts or status=:sts1) and purchase_id=:pid").
			setLong("sts",SConstants.SALES_RETURN_STOCK_STATUS).setLong("sts1",SConstants.stock_statuses.GOOD_STOCK).setLong("pid", id).executeUpdate();
			
			
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
	
	
	
	
	public void cancelReturn(long id) throws Exception {
		try {
			begin();
			
			SalesReturnModel obj = (SalesReturnModel) getSession().get(
					SalesReturnModel.class, id);
			
			
			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
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
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", obj.getPayment_amount()).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			
			getSession().delete(transObj);
			
			flush();
			
			// Transaction Related
			
			SalesReturnInventoryDetailsModel invObj;
			List list;
			Iterator<SalesReturnInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getStock_quantity()+invObj.getGood_stock()+invObj.getReturned_quantity())
						.executeUpdate();
				
				flush();
			}
			
			
			PurchaseReturnModel pretObj;
			Iterator itr4= getSession().createQuery("select a.id from PurchaseReturnModel a join a.inventory_details_list b where b.order_id=:ret and a.status=2 group by a.id")
							.setLong("ret", id).list().iterator();
			while (itr4.hasNext()) {
				deletePurchReturn((Long) itr4.next());
			}
			
			flush();
			
			getSession().createQuery("update SalesReturnModel set active=false where id=:id")
				.setParameter("id", obj.getId()).executeUpdate();
			
			flush();
			
			getSession().createQuery("delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where status=:sts and purchase_id=:pid)")
									.setLong("sts",SConstants.SALES_RETURN_STOCK_STATUS).setLong("pid", id).executeUpdate();
			
			getSession().createQuery("delete from ItemStockModel where (status=:sts or status=:sts1) and purchase_id=:pid").
			setLong("sts",SConstants.SALES_RETURN_STOCK_STATUS).setLong("sts1",SConstants.stock_statuses.GOOD_STOCK).setLong("pid", id).executeUpdate();
			
			
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
	
	
	
	
	public double getPurchaseReturnRate(long id, long item_id) throws Exception {
		double rate=0;
		try {
			begin();
			
			Iterator itr4= getSession().createQuery("select b.unit_price from PurchaseReturnModel a join a.inventory_details_list b where b.order_id=:ret and a.status=2 and a.active=true and b.item.id=:itm")
							.setLong("ret", id).setLong("itm", item_id).list().iterator();
			if(itr4.hasNext()) {
				rate=(Double) itr4.next();
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		}
		flush();
		close();
		return rate;
	}
	
	
	
	
	public long updateReturn(List<ItemStockModel> itemsList, SalesReturnModel salesReturnModel,
			TransactionModel transactionModel, HashMap<TransactionModel, PurchaseReturnModel> hash, long id) throws Exception {
		
		long invDetailId = 0;
		
		try {
			
			begin();
			
			transactionModel.setTransaction_id(salesReturnModel.getId());
			
			// For Delete 
			
			SalesReturnModel obj = (SalesReturnModel) getSession().get(SalesReturnModel.class, id);
			getSession().evict(obj);
			// Transaction Related
			
			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());
			
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				
				flush();
			}
			
			
//			if(obj.getPayment_amount()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", obj.getPayment_amount()).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			
			
			
//			getSession().delete(transObj);
			
			// Transaction Related
			
			SalesReturnInventoryDetailsModel invObj;
			List list;
			Iterator<SalesReturnInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getStock_quantity()+invObj.getGood_stock()+invObj.getReturned_quantity())
						.executeUpdate();
			}
			
			flush();
			
			
			PurchaseReturnModel pretObj;
			Iterator itr4= getSession().createQuery("select a.id from PurchaseReturnModel a join a.inventory_details_list b where b.order_id=:ret and a.status=2 group by a.id")
							.setLong("ret", id).list().iterator();
			while (itr4.hasNext()) {
				deletePurchReturn((Long) itr4.next());
			}
			
			flush();
			
			
//			getSession().delete(obj);
			
			getSession()
					.createQuery(
							"delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where status=:sts and purchase_id=:pid)")
									.setLong("sts",SConstants.SALES_RETURN_STOCK_STATUS).setLong("pid", id).executeUpdate();
			
			getSession().createQuery("delete from ItemStockModel where (status=:sts or status=:sts1) and purchase_id=:pid").
								setLong("sts",SConstants.SALES_RETURN_STOCK_STATUS).setLong("sts1",SConstants.stock_statuses.GOOD_STOCK).setLong("pid", id).executeUpdate();
			
			flush();
			
			
//			For Save New
			
			
			PurchaseReturnModel purchaseReturnModel;
			TransactionModel purFinTran;
			long itemId=0;
			Iterator itret =hash.keySet().iterator();
			
			getSession().evict(transObj);
			
			getSession().update(transactionModel);
			
			flush();
			
			TransactionDetailsModel tr1;
			Iterator<TransactionDetailsModel> aciter1=transactionModel.getTransaction_details_list().iterator();
			while(aciter1.hasNext()) {
				tr1=aciter1.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", tr1.getAmount()).setLong("id", tr1.getFromAcct().getId())
								.executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
					.setDouble("amt", tr1.getAmount()).setLong("id", tr1.getToAcct().getId()).executeUpdate();
				
				flush();
				
			}
			
//			if(salesReturnModel.getPayment_amount()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", salesReturnModel.getPayment_amount()).setLong("id", salesReturnModel.getCustomer().getId()).executeUpdate();
			
			
			salesReturnModel.setTransaction_id(transactionModel.getTransaction_id());
			
			flush();
			
			Iterator<SalesReturnInventoryDetailsModel> it2 = salesReturnModel
					.getInventory_details_list().iterator();
			
			SalesReturnInventoryDetailsModel invObj1;
			while (it2.hasNext()) {
				
				invObj1 = it2.next();
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj1.getItem().getId())
//						.setParameter("qty", invObj1.getStock_quantity()+invObj1.getGood_stock()+invObj1.getReturned_quantity())
						.executeUpdate();
				flush();
			}
			
			if(itemsList!=null&&itemsList.size()>0){
				
				Iterator<ItemStockModel> itemItr=itemsList.iterator();
				ItemStockModel stk;
				while (itemItr.hasNext()) {
					stk=itemItr.next();
					stk.setPurchase_id(salesReturnModel.getId());
					getSession().save(stk);
				}
				 
			}
			
			flush();
			//Purchase Return Related============================================
			
			Iterator<TransactionDetailsModel> transactioniter;
			TransactionDetailsModel trd;
			while(itret.hasNext()){
				
				purFinTran=(TransactionModel) itret.next();
				purchaseReturnModel=(PurchaseReturnModel) hash.get(purFinTran);
				getSession().save(purFinTran);
				flush();
				transactioniter = purFinTran
						.getTransaction_details_list().iterator();
				while (transactioniter.hasNext()) {
					trd = transactioniter.next();

					getSession()
							.createQuery(
									"update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", trd.getAmount())
							.setLong("id", trd.getFromAcct().getId())
							.executeUpdate();
					
					getSession()
							.createQuery(
									"update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", trd.getAmount())
							.setLong("id", trd.getToAcct().getId()).executeUpdate();
					
					flush();
					
				}
				
				purchaseReturnModel.setTransaction_id(purFinTran.getTransaction_id());
				
				getSession().save(purchaseReturnModel);
				flush();
				Iterator<PurchaseReturnInventoryDetailsModel> ite = purchaseReturnModel
						.getInventory_details_list().iterator();
				PurchaseReturnInventoryDetailsModel pinvObj;
				while (ite.hasNext()) {
					
					pinvObj = ite.next();
					
					getSession().createQuery("update PurchaseReturnInventoryDetailsModel set order_id=:ordId where id=:id").
					setParameter("ordId",salesReturnModel.getId()).setParameter("id", pinvObj.getId()).executeUpdate();
					
					getSession()
							.createQuery(
									"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", pinvObj.getItem().getId())
							.setParameter("qty", pinvObj.getQty_in_basic_unit())
							.executeUpdate();
					flush();
//					itemId = (Long) getSession()
//							.createQuery(
//									"select b.id from PurchaseModel a join a.inventory_details_list b where a.id=:ID and b.item.id =:ItemID")
//							.setParameter("ID", invObj.getOrder_id())
//							.setParameter("ItemID", invObj.getItem().getId())
//							.uniqueResult();
//
//					getSession()
//							.createQuery(
//									"update PurchaseInventoryDetailsModel set balance=:Balance where id=:ID")
//							.setParameter("ID", itemId)
//							.setParameter("Balance", invObj.getBalance())
//							.executeUpdate();
//
//					flush();
				}
			}
			
			flush();
			
			getSession().update(salesReturnModel);
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			
		} finally {
			flush();
			close();
		}
		return salesReturnModel.getId();
	}
	
	
	
	
	public void deletePurchReturn(long id) throws Exception {

		try {
			
			
			PurchaseReturnModel obj = (PurchaseReturnModel) getSession().get(
					PurchaseReturnModel.class, id);

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
			
			flush();

			// Transaction Related

			PurchaseReturnInventoryDetailsModel invObj;
			List list;
			Iterator<PurchaseReturnInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				/*if (invObj.getOrder_id() != 0) {
					list=null;
					list = getSession()
							.createQuery(
									"select b.id from PurchaseModel a join a.inventory_details_list b  where a.id=:id "
											+ "and b.item.id=:itm and b.unit.id=:un ")
							.setParameter("itm", invObj.getItem().getId())
							.setParameter("id", invObj.getOrder_id())
							.setParameter("un", invObj.getUnit().getId())
							.list();

					getSession()
							.createQuery(
									"update PurchaseInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
							.setParameterList("lst", list)
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();
				}*/

				getSession().createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQty_in_basic_unit())
						.executeUpdate();
				flush();
				
				comDao.increaseStock(invObj.getItem().getId(), invObj.getQty_in_basic_unit() );

			}

			getSession().delete(obj);


		} catch (Exception e) {
			throw e;
		}
		flush();
	
	}
	
	
	
	
	
	
	public double getConvertionRate(long item_id, long unit_id, int sales_type) throws Exception {
		double rate=1;
		try {
			
			Object obj = getSession()
					.createQuery(
							"select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt " +
							" and sales_type=:st").setLong("itm", item_id)
									.setLong("alt", unit_id).setLong("st", sales_type).uniqueResult();
			if(obj!=null)
				rate=(Double) obj;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		return rate;
	}
	

	public SalesReturnModel getSalesReturnModel(long id) throws Exception {
		SalesReturnModel pur = null;
		try {
			begin();
			pur = (SalesReturnModel) getSession().get(SalesReturnModel.class, id);
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
	
	
	public List getAllCreditNotesAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesReturnModel(id,cast(credit_note_no as string) )"
									+ " from SalesReturnModel where office.id=:ofc and active=true order by id desc")
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


	public void updateItemStandardCost(Set<Long> ItemIDs) throws Exception {
		try {
			begin();

			long itemId;
			Object obj;
			Iterator it = ItemIDs.iterator();
			while (it.hasNext()) {
				itemId = (Long) it.next();

				obj = getSession()
						.createQuery(
								"select sum(b.stock_quantity*b.unit_price)/sum(b.stock_quantity)   from SalesReturnModel a join a.inventory_details_list b"
										+ " where b.item.id=:itemid")
						.setParameter("itemid", itemId).uniqueResult();

				if (obj != null) {
					double rate = (Double) obj;
					if (rate > 0) {
						getSession()
								.createQuery(
										"update ItemModel set standard_cost=:cst where id=:id")
								.setDouble("cst", rate).setLong("id", itemId)
								.executeUpdate();
					}
				}

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
	
	
	
	
	
	
	public List getAllDebitNotesOfSupplier(long ofc_id, Date stDt, Date endDt, long sup_id) throws Exception {
		resultList=null;
		try {
			begin();
			resultList = getSession()
					.createQuery("select b from PurchaseReturnModel a join a.inventory_details_list b where a.office.id=:ofc " +
							"and a.date between :stdt and :enddt and a.supplier.id=:sup and a.active=true")
					.setLong("ofc", ofc_id).setParameter("stdt", stDt).setParameter("enddt", endDt)
					.setLong("sup", sup_id).list();
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


	public List getSalesBillNo(long custid,long officeId) throws Exception {
		List list;

		String condition = "";
		if (custid > 0) {
			condition += " and customer.id=" + custid;
		}

		try {
			begin();

			list = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string)) from SalesModel " +
							" where office.id=:ofc and active=true "
									+ condition).setParameter("ofc", officeId).list();

			commit();
		} catch (Exception e) {
			list = new ArrayList();
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}

		return list;
	}


	
	public List getSalesRetrunChartDetails(java.sql.Date fromDate,
			java.sql.Date toDate, long officeID) throws Exception {
		
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.bean.SalesChartBean(date,coalesce(sum(amount),0))" +
							" from SalesReturnModel where office.id=:office and date between :fromDate and :toDate" +
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
	
	
	
	
	
	

}
