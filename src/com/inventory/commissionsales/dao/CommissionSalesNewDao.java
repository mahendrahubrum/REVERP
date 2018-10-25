package com.inventory.commissionsales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionSalesDetailsNewModel;
import com.inventory.commissionsales.model.CommissionSalesNewModel;
import com.inventory.purchase.model.CommissionStockModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 5, 2014
 */

public class CommissionSalesNewDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = 4477228104694404271L;
	List resultList = new ArrayList();

	public List getAllSalesNumbersAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.commissionsales.model.CommissionSalesNewModel(id,cast(sales_number as string) )"
									+ " from CommissionSalesNewModel where office.id=:ofc and type<2 and active=true order by id desc")
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
		}
		return resultList;
	}
	

	public CommissionSalesNewModel getSale(long id) throws Exception {
		CommissionSalesNewModel pur = null;
		try {
			begin();
			pur = (CommissionSalesNewModel) getSession().get(CommissionSalesNewModel.class, id);
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
	
	public List getAllItemsWithRealStck(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			resultList= getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ', current_balalnce ), current_balalnce)"
									+ " from ItemModel where office.id=:ofc order by name")
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
		}
		return resultList;
	}


	public List getSalesRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		resultList=null;
		try {
			
			begin();
				
			resultList = getSession()
					.createQuery("select new com.inventory.sales.bean.SalesInventoryDetailsPojo(" +
							"a.customer.name, b.unit_price, a.date)   from CommissionSalesNewModel a join a.commission_sales_list b"
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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return tran;
		}
	}
	
	public CommissionStockModel getItemStocks(long id) throws Exception {
		CommissionStockModel stk = null;
		try {
			begin();
			stk = (CommissionStockModel) getSession().get(CommissionStockModel.class, id);
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
		return stk;
	}


	public CommissionStockModel getItemFromBarcode(String code) throws Exception {
		List codeList=null;
		CommissionStockModel stk=null;
		try {
			begin();
				
			codeList = getSession()
						.createQuery("from CommissionStockModel where barcode=:code order by id desc").setParameter("code", code).list();
				
			commit();
			
			if(codeList!=null&&codeList.size()>0)
				stk=(CommissionStockModel) codeList.get(0);
			
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


	
	public boolean isSalesNoExists(Object salesId,long salesNo, long officeID) throws Exception {
		boolean flag=false;
		try {
			String condition="";
			if(salesId!=null&&!salesId.equals("")&&(Long)salesId!=0)
				condition=" and id!="+(Long)salesId;
			
			begin();
			resultList = getSession()
					.createQuery("from CommissionSalesNewModel where office.id=:office and sales_number=:saleNo and active=true"+condition)
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
	
	
	public long save(CommissionSalesNewModel obj, TransactionModel transaction)
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
			
			
			
			// Transaction Related
			
			obj.setTransaction_id(transaction.getTransaction_id());
			
			CommissionSalesDetailsNewModel invObj;
			List<CommissionSalesDetailsNewModel> invList = new ArrayList<CommissionSalesDetailsNewModel>();
			Iterator<CommissionSalesDetailsNewModel> it = obj
					.getCommission_sales_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				if (invObj.getOrder_id() != 0) {
					
					getSession().createQuery(
									"update CommissionSalesDetailsNewModel set balance=balance-:qty, quantity_in_basic_unit=quantity_in_basic_unit-:qtybu  where id=:id")
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
				
//				decreaseRackQty(invObj.getRack_id(), invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				flush();

				decreaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				invList.add(invObj);
			}

			obj.setCommission_sales_list(invList);

			getSession().save(obj);
			
			
//			Iterator itr3=obj.getCommission_sales_list().iterator();
//			while (itr3.hasNext()) {
//				invObj=(CommissionSalesDetailsNewModel) itr3.next();
//				
//				String[] stks=invObj.getStock_ids().split(",");
//				for (String string : stks) {
//					if(string.length()>2)
//					getSession().save(new SalesStockMapModel(obj.getId(), invObj.getId(), Long.parseLong(string.split(":")[0]),
//							Double.parseDouble(string.split(":")[1])));
//				}
//				flush();
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
			return obj.getId();
		}
	}
	
	public void update(CommissionSalesNewModel newobj, TransactionModel transaction, double payingAmt)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from CommissionSalesNewModel a join a.commission_sales_list b "
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
			
			
			List oldLst = getSession()
					.createQuery(
							"select b from CommissionSalesNewModel a join a.commission_sales_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			CommissionSalesDetailsNewModel invObj;
			List list;
			Iterator<CommissionSalesDetailsNewModel> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				
				flush();
				
				// For Stock Update
				increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());

//				increaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
				
			}

			// getSession().delete(obj);

			flush();

			// Save

			List<CommissionSalesDetailsNewModel> invList = new ArrayList<CommissionSalesDetailsNewModel>();
			Iterator<CommissionSalesDetailsNewModel> it1 = newobj
					.getCommission_sales_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();

				invObj.setStock_ids(invObj.getStk_id()+":"+invObj.getQuantity_in_basic_unit());
				
				flush();
				
				decreaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
//				decreaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());

				invList.add(invObj);
			}

			newobj.setCommission_sales_list(invList);

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
			
			
			getSession().update(newobj);
			flush();

			getSession()
					.createQuery(
							"delete from CommissionSalesDetailsNewModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_notDeletedLst)
					.executeUpdate();

			getSession()
					.createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
					.executeUpdate();
			
			
			
//			getSession().createQuery("delete from SalesStockMapModel where salesId=:id")
//					.setLong("id", newobj.getId()).executeUpdate();

//			flush();
			
//			Iterator itr3=newobj.getInventory_details_list().iterator();
//			while (itr3.hasNext()) {
//				invObj=(SalesInventoryDetailsModel) itr3.next();
//				
//				String[] stks=invObj.getStock_ids().split(",");
//				for (String string : stks) {
//					if(string.length()>2)
//					getSession().save(new SalesStockMapModel(newobj.getId(), invObj.getId(), Long.parseLong(string.split(":")[0]),
//							Double.parseDouble(string.split(":")[1])));
//				}
//				flush();
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
		}
	}

	public void delete(long id) throws Exception {
		try {
			begin();
			CommissionSalesNewModel obj = (CommissionSalesNewModel) getSession()
					.get(CommissionSalesNewModel.class, id);

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

			CommissionSalesDetailsNewModel invObj;
			List list;
			Iterator<CommissionSalesDetailsNewModel> it = obj
					.getCommission_sales_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				flush();
				
				increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
//				increaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
			}
			
			getSession().delete(obj);
			
			
//			getSession().createQuery("delete from SalesStockMapModel where salesId=:id")
//				.setLong("id", obj.getId()).executeUpdate();
			
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}
	
	public void cancelSale(long id) throws Exception {
		try {
			begin();
			CommissionSalesNewModel obj = (CommissionSalesNewModel) getSession()
					.get(CommissionSalesNewModel.class, id);
			
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
			
			CommissionSalesDetailsNewModel invObj;
			List list;
			Iterator<CommissionSalesDetailsNewModel> it = obj
					.getCommission_sales_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				
				getSession()
				.createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				flush();
				
				increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
//				increaseRackQty(invObj.getRack_id(),invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
			}
			
			getSession().createQuery("update CommissionSalesNewModel set active=false where id=:id")
				.setParameter("id", obj.getId()).executeUpdate();
			
//			getSession().createQuery("delete from SalesStockMapModel where salesId=:id")
//			.setLong("id", obj.getId()).executeUpdate();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}

	
	public void decreaseStockByStockID(long stk_id, double qty_in_basic_unit)
			throws Exception {
		if (stk_id != 0) {
			getSession()
					.createQuery(
							"update CommissionStockModel set balance=balance-:qty, blocked=true  where id=:id")
					.setLong("id", stk_id).setDouble("qty", qty_in_basic_unit)
					.executeUpdate();
		}

	}
	
	public void increaseStockByStockID(long stk_id, double qty_in_basic_unit)
			throws Exception {
		
		if (stk_id != 0) {

			CommissionStockModel obj = (CommissionStockModel) getSession().get(
					CommissionStockModel.class, stk_id);
			if (obj != null) {
				obj.setBalance(obj.getBalance() + qty_in_basic_unit);

				if (obj.getBalance() < obj.getQuantity()) {
					obj.setBlocked(true);
				} else {
					obj.setBlocked(false);
				}
				getSession().update(obj);
			}
		}
	}


	@SuppressWarnings("finally")
	public List getStocks(long item_id, boolean isUseTag) throws Exception {
		try {

			resultList = new ArrayList();
			
			String tag_crit="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";

			begin();
			resultList
					.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from CommissionStockModel where item.id=:itm and balance>0  order by id")
							.setLong("itm", item_id).list());

			if (resultList == null || resultList.size() <= 0) {

				Object obj = getSession()
						.createQuery(
								"select max(id) from CommissionStockModel where item.id=:itm ")
						.setLong("itm", item_id).uniqueResult();

				if (obj != null)
					resultList
							.addAll(getSession()
									.createQuery(
											"select new com.inventory.purchase.bean.InventoryDetailsPojo("
													+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol )" +
															",status) from CommissionStockModel where id=:id")
									.setLong("id", (Long) obj).list());

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
		return resultList;
	}


		
	public Long getDefaultStockToSelect(long item_id) throws Exception {
		long stk_id=0;
		try {
			
			begin();
			stk_id = (Long) getSession()
					.createQuery(
							"select coalesce(min(id),0) from CommissionStockModel where item.id=:itm and balance>0 and status=1")
					.setLong("itm", item_id).uniqueResult();

			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return stk_id;
		}
		
	}
	
	
}
