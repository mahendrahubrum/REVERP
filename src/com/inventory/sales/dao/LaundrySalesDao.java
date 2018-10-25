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
import com.inventory.sales.bean.SalesBean;
import com.inventory.sales.model.LaundrySalesDetailsModel;
import com.inventory.sales.model.LaundrySalesModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 * @Date 14 Feb 2014
 */

public class LaundrySalesDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7453940143393746135L;

	List resultList = new ArrayList();
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	ItemDao itemDao=new ItemDao();

	public long save(LaundrySalesModel obj, TransactionModel transaction, double payingAmt)
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

			LaundrySalesDetailsModel invObj;
			String stockIDs;
			List<LaundrySalesDetailsModel> invList = new ArrayList<LaundrySalesDetailsModel>();
			Iterator<LaundrySalesDetailsModel> it = obj
					.getDetails_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
				.createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
				.setParameter("id", invObj.getItem().getId())
				.setParameter("qty", invObj.getQuantity())
				.executeUpdate();
				
				flush();
				
				invList.add(invObj);
			}

			obj.setDetails_list(invList);

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
	
	

	public LaundrySalesModel getSale(long id) throws Exception {
		LaundrySalesModel pur = null;
		try {
			begin();
			pur = (LaundrySalesModel) getSession().get(LaundrySalesModel.class, id);
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

	public void update(LaundrySalesModel newobj, TransactionModel transaction, double payingAmt)
			throws Exception {
		try {

			begin();

			Object old_notDeletedLst = getSession().createQuery(
					"select b.id from LaundrySalesModel a join a.details_list b "
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
					"LaundrySalesModel where id=" + newobj.getId()).uniqueResult();
			
//			if(smdl.getPayedAmt()!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", smdl.getPayedAmt()).setLong("id", smdl.getCustomer_id()).executeUpdate();
			
			
			
			// LaundrySalesModel obj=(LaundrySalesModel) getSession().get(LaundrySalesModel.class,
			// newobj.getId());

			List oldLst = getSession()
					.createQuery(
							"select b from LaundrySalesModel a join a.details_list b where a.id=:id")
					.setLong("id", newobj.getId()).list();

			LaundrySalesDetailsModel invObj;
			List list;
			Iterator<LaundrySalesDetailsModel> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity())
						.executeUpdate();
				
				flush();

			}

			// getSession().delete(obj);

			flush();

			// Save

			List<LaundrySalesDetailsModel> invList = new ArrayList<LaundrySalesDetailsModel>();
			String stockIDs;
			Iterator<LaundrySalesDetailsModel> it1 = newobj
					.getDetails_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity())
						.executeUpdate();
				
				flush();

				invList.add(invObj);
			}

			newobj.setDetails_list(invList);

			// Transaction Related
			
			flush();

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
			
			getSession().createQuery(
							"delete from LaundrySalesDetailsModel where id in (:lst)")
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
			LaundrySalesModel obj = (LaundrySalesModel) getSession()
					.get(LaundrySalesModel.class, id);

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
			
			if(obj.getPayment_amount()!=0)
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
					.setDouble("amt", obj.getPayment_amount()).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			flush();

			getSession().delete(transObj);
			
			flush();

			// Transaction Related

			LaundrySalesDetailsModel invObj;
			List list;
			Iterator<LaundrySalesDetailsModel> it = obj
					.getDetails_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity())
						.executeUpdate();
				
				flush();
				
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
	
	
	public void cancelSale(long id) throws Exception {
		try {
			begin();
			LaundrySalesModel obj = (LaundrySalesModel) getSession()
					.get(LaundrySalesModel.class, id);

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
			
			if(obj.getPayment_amount()!=0)
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
					.setDouble("amt", obj.getPayment_amount()).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			flush();

			getSession().delete(transObj);
			
			flush();

			// Transaction Related

			LaundrySalesDetailsModel invObj;
			List list;
			Iterator<LaundrySalesDetailsModel> it = obj
					.getDetails_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity())
						.executeUpdate();
				
				flush();
				
			}
			
			getSession().createQuery("update LaundrySalesModel set active=false where id=:id")
			.setParameter("id", obj.getId()).executeUpdate();
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
							"select new com.inventory.sales.model.LaundrySalesModel(id,cast(sales_number as string) )"
									+ " from LaundrySalesModel where office.id=:ofc and type<2 and active=true order by sales_number desc")
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
					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(item.name,' ( ',item.item_code,' ) ', ' Bal: ' , balance, ' ', item.unit.symbol )) from ItemStockModel where item.office.id=:ofc  order by item.name")
									.setLong("ofc", ofc_id).list();
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
			resultList = getSession().createQuery(
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
			
			resultList = getSession().createQuery("select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )' )) from ItemModel where office.id=:ofc").setParameter("ofc", ofc_id).list();
			
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
							"select new com.inventory.sales.model.LaundrySalesModel(sales_number,payment_amount,amount)"
									+ " from LaundrySalesModel where customer.id=:custId and date between :fromDate and :lastdate" +
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
			Date fromDate, Date toDate, boolean isCreateNew) throws Exception {
		
		try {
			
			String criteria="";
			if(isCreateNew)
				criteria=" and payment_done='N'";
			
			
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.LaundrySalesModel(id,concat(sales_number, '  - ', date ,'(',amount-payment_amount-paid_by_payment,')' ))"
									+ " from LaundrySalesModel where customer.id=:custId and date between :fromDate and :toDate" +
									" and status>1 and active=true"+criteria)
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
	
	
	
	
	public List<Object> getAllSalesNumbersByDate(long officeId,
			Date fromDate, Date toDate, String condition1) throws Exception {

		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession().createQuery(
							"select new com.inventory.sales.model.LaundrySalesModel(id,cast(sales_number as string) )"
									+ " from LaundrySalesModel where date between :fromDate and :toDate and active=true"+condition)
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
	
	public List getSalesRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		resultList=null;
		try {
			
			begin();
				
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.bean.SalesInventoryDetailsPojo(" +
							"a.customer.name, b.unit_price, a.date)   from LaundrySalesModel a join a.details_list b"
									+ " where b.item.id=:itemid and b.unit.id=:unit and a.office.id=:ofc and a.active=true order by a.date desc")
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
	
	
}
