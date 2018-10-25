package com.inventory.commissionsales.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.commissionsales.model.CommissionPurchaseDetailsModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.purchase.model.CommissionStockModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

public class CommissionPurchaseDao extends SHibernate{

	public CommissionPurchaseModel getPurchase(long id) throws Exception {
		CommissionPurchaseModel pur = null;
		try {
			begin();
			pur = (CommissionPurchaseModel) getSession().get(CommissionPurchaseModel.class, id);
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
	
	public List getAllActivePurchaseNos(long ofc_id) throws Exception {
		List resultList;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.commissionsales.model.CommissionPurchaseModel(id, cast(number as string))"
									+ " from CommissionPurchaseModel where office.id=:ofc order by id desc")
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

	public long save(CommissionPurchaseModel obj, TransactionModel transaction) throws Exception {

		try {

			begin();

			getSession().save(transaction);
			
			flush();

			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();

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
			

			obj.setTransaction_id(transaction.getTransaction_id());

			List<CommissionPurchaseDetailsModel> invList = new ArrayList<CommissionPurchaseDetailsModel>();
			Iterator<CommissionPurchaseDetailsModel> it = obj
					.getCommission_purchase_list().iterator();
			CommissionPurchaseDetailsModel invObj;
			while (it.hasNext()) {
				invObj = it.next();

				if (invObj.getOrder_id() != 0) {

					getSession()
							.createQuery(
									"update CommissionPurchaseDetailsModel set balance=balance-:qty where id=:id")
							.setParameter("id", invObj.getId())
							.setParameter("qty", invObj.getQunatity())
							.executeUpdate();
					
					
//					double conv_rate=getConvertionRate(invObj.getItem().getId(), invObj.getUnit().getId(), 0);
					
					invObj.setId(0);
				}
				
				getSession()
					.createQuery(
							"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
					.setParameter("id", invObj.getItem().getId())
					.setParameter("qty", invObj.getQty_in_basic_unit())
					.executeUpdate();
				
				flush();
				

				invList.add(invObj);
			}

			obj.setCommission_purchase_list(invList);
			
			getSession().save(obj);
			
			flush();

			Iterator<CommissionPurchaseDetailsModel> it1 = obj
					.getCommission_purchase_list().iterator();
			CommissionStockModel stock;
			double rate;
			String code;
			while (it1.hasNext()) {
				invObj = it1.next();
				stock = new CommissionStockModel();
				stock.setExpiry_date(obj.getReceived_date());
				stock.setItem(invObj.getItem());
				stock.setPurchase_id(obj.getId());
				stock.setInv_det_id(invObj.getId());
				
				rate=0;
//				double conv_rate=getConvertionRate(invObj.getItem().getId(), invObj.getUnit().getId(), 0);
				rate=invObj.getQunatity()*invObj.getUnit_price()/invObj.getQty_in_basic_unit();
				
				stock.setQuantity(invObj.getQty_in_basic_unit());
				stock.setBalance(invObj.getQty_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(1);
				stock.setManufacturing_date(obj.getReceived_date());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(invObj.getGradeId());
				stock.setItem_tag(invObj.getItem_tag());
				getSession().save(stock);
					stock.setBarcode(stock.getId()+"");
//				getSession().update(stock);
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
			return obj.getId();
		}
	}
	
	public void update(CommissionPurchaseModel newobj, TransactionModel transaction) throws Exception {
		
		try {
			
			CommissionPurchaseModel newOldData=newobj;
			
			begin();
			
			List old_notDeletedLst = getSession().createQuery(
					"select b.id from CommissionPurchaseModel a join a.commission_purchase_list b "
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
			
			
			CommissionPurchaseModel oldPurcObj=(CommissionPurchaseModel) getSession().get(CommissionPurchaseModel.class, newobj.getId());
			getSession().evict(oldPurcObj);
			
			CommissionPurchaseDetailsModel invObj;
			List list;
			Iterator<CommissionPurchaseDetailsModel> it = oldPurcObj.getCommission_purchase_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
//				double conv_rate=getConvertionRate(invObj.getItem().getId(), invObj.getUnit().getId(), 0);

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQty_in_basic_unit())
						.executeUpdate();
				
				
				flush();
						
			}
			
			// Save
			
			List<CommissionPurchaseDetailsModel> invList = new ArrayList<CommissionPurchaseDetailsModel>();
			Iterator<CommissionPurchaseDetailsModel> it1 = newobj
					.getCommission_purchase_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();

				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
					.setParameter("id", invObj.getItem().getId()).setParameter("qty", invObj.getQty_in_basic_unit())
					.executeUpdate();
				
				
				if(invObj.getId()!=0)
					old_notDeletedLst.remove(invObj.getId());
				
				flush();

				invList.add(invObj);
			}

			newobj.setCommission_purchase_list(invList);

			// getSession().save(obj);

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
			
			// Transaction Related

			getSession().update(newobj);
			
			flush();

			getSession().createQuery(
					"delete from StockRackMappingModel where stock.id in (select id from ItemStockModel where purchase_id=:purId)")
					.setLong("purId", newobj.getId()).executeUpdate();
			
			getSession().createQuery("delete from ItemStockModel where purchase_id=:pid and blocked=false")
					.setLong("pid", newobj.getId()).executeUpdate();
			
			flush();
			
			Iterator<CommissionPurchaseDetailsModel> it2 = newobj
					.getCommission_purchase_list().iterator();
			CommissionStockModel stock;
			double rate;
			String code;
			while (it2.hasNext()) {
				invObj = it2.next();
				
				if(invObj.getId()!=0) {
					Object obj=getSession().createQuery("from CommissionStockModel where purchase_id=:pid and inv_det_id=:invdet").setLong("invdet", invObj.getId())
							.setLong("pid", newobj.getId()).uniqueResult();
					if(obj!=null)
						stock=(CommissionStockModel) obj;
					else
						stock = new CommissionStockModel();
				}
				else
					stock = new CommissionStockModel();
				
				stock.setExpiry_date(newobj.getReceived_date());
				stock.setItem(invObj.getItem());
				stock.setPurchase_id(newobj.getId());
				stock.setInv_det_id(invObj.getId());
//				double conv_rate=getConvertionRate(invObj.getItem().getId(), invObj.getUnit().getId(), 0);
				rate=0;
				rate=invObj.getQunatity()*invObj.getUnit_price()/invObj.getQty_in_basic_unit();
				
				stock.setQuantity(invObj.getQty_in_basic_unit());
				stock.setBalance(invObj.getQty_in_basic_unit());
				stock.setRate(rate);
				
				stock.setStatus(2);
				stock.setManufacturing_date(newobj.getReceived_date());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(invObj.getGradeId());
				stock.setItem_tag(invObj.getItem_tag());
				
				if(stock.getId()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				
					stock.setBarcode(stock.getId()+"");
//				getSession().update(stock);
			}

			try {
				if(old_notDeletedLst.size()>0)
				getSession()
				.createQuery(
						"delete from CommissionPurchaseDetailsModel where id in (:lst)")
				.setParameterList("lst", old_notDeletedLst)
				.executeUpdate();
			} catch (Exception e) {
				System.out.println();
			}
			
			getSession()
					.createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) old_AcctnotDeletedLst)
					.executeUpdate();

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
			CommissionPurchaseModel obj = (CommissionPurchaseModel) getSession().get(
					CommissionPurchaseModel.class, id);

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

			CommissionPurchaseDetailsModel invObj;
			List list;
			Iterator<CommissionPurchaseDetailsModel> it = obj
					.getCommission_purchase_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQty_in_basic_unit())
						.executeUpdate();
				
				flush();

			}

			getSession().delete(obj);
			
			flush();

			getSession()
					.createQuery(
							"delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where purchase_id=:purId)")
					.setLong("purId", obj.getId()).executeUpdate();

			getSession()
					.createQuery(
							"delete from CommissionStockModel where purchase_id=:pid")
					.setLong("pid", obj.getId()).executeUpdate();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}
	
	public void cancel(long id) throws Exception {

		try {
			begin();
			CommissionPurchaseModel obj = (CommissionPurchaseModel) getSession().get(
					CommissionPurchaseModel.class, id);

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

			CommissionPurchaseDetailsModel invObj;
			List list;
			Iterator<CommissionPurchaseDetailsModel> it = obj
					.getCommission_purchase_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQty_in_basic_unit())
						.executeUpdate();
				
				flush();

			}

			getSession().createQuery("update CommissionPurchaseModel set active=false where id =:id").setParameter("id", obj.getId()).executeUpdate();
			
			flush();

			getSession()
					.createQuery(
							"delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where purchase_id=:purId)")
					.setLong("purId", obj.getId()).executeUpdate();

			getSession()
					.createQuery(
							"delete from CommissionStockModel where purchase_id=:pid")
					.setLong("pid", obj.getId()).executeUpdate();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}

	public List getSalesRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		List resultList=null;
		try {
			
			begin();
				
				resultList = getSession()
						.createQuery(
								"select new com.inventory.sales.bean.SalesInventoryDetailsPojo(" +
								"a.customer.name, b.unit_price, a.date)   from CommissionSalesNewModel a join a.commission_sales_list b"
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
	
	public List getPurchaseRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		List resultList=null;
		try {
			
			begin();
				
				resultList = getSession()
						.createQuery(
								"select new com.inventory.purchase.bean.InventoryDetailsPojo(" +
								"a.supplier.name, b.unit_price, a.date)   from CommissionPurchaseModel a join a.commission_purchase_list b"
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
			return resultList;
		}
	}
}
