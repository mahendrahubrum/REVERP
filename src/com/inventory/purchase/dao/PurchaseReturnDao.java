package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PurchaseReturnDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public long save(PurchaseReturnModel mdl, TransactionModel transaction) throws Exception {
		try {
			begin();
			List<PurchaseReturnInventoryDetailsModel> childList = new ArrayList<PurchaseReturnInventoryDetailsModel>();
			List<Long> purchaseList=new ArrayList<Long>(); 
			
			getSession().save(transaction);
			
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tdm;
			
			while (aciter.hasNext()) {
				tdm = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
				
			}

			mdl.setTransaction_id(transaction.getTransaction_id());
			
			Iterator itr=mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				
				PurchaseReturnInventoryDetailsModel det = (PurchaseReturnInventoryDetailsModel) itr.next();
				
				if(det.getPurchase_id()!=0){
					
					if(!purchaseList.contains(det.getPurchase_id())) {
						purchaseList.add(det.getPurchase_id());
						getSession().createQuery("update PurchaseModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getPurchase_id()).executeUpdate();
						flush();
					}
					
					if(det.getPurchase_child_id()!=0) {
						getSession().createQuery("update PurchaseInventoryDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getPurchase_child_id()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0)
					new CommonMethodsDao().decreaseStockByStockID(det.getStock_id(), CommonUtil.roundNumber(det.getQty_in_basic_unit()), false);
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				childList.add(det);
			}
			mdl.setInventory_details_list(childList);
			getSession().save(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl.getId();
	}

	
	@SuppressWarnings("rawtypes")
	public List getPurchaseReturnModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseReturnModel(id, return_no) from PurchaseReturnModel " +
					"where office.id=:office and active=true order by id DESC").setParameter("office", office).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	public PurchaseReturnModel getPurchaseReturnModel(long id) throws Exception {
		PurchaseReturnModel mdl=null;
		try {
			begin();
			mdl=(PurchaseReturnModel)getSession().get(PurchaseReturnModel.class, id);
			Hibernate.initialize(mdl.getPurchase_return_expense_list());
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	public TransactionModel getTransactionModel(long id) throws Exception {
		TransactionModel mdl=null;
		try {
			begin();
			mdl=(TransactionModel)getSession().get(TransactionModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	public ItemStockModel getItemStockModel(long id) throws Exception {
		ItemStockModel mdl=null;
		try {
			begin();
			mdl=(ItemStockModel)getSession().get(ItemStockModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	public PurchaseReturnInventoryDetailsModel getPurchaseReturnInventoryDetailsModel(long id) throws Exception {
		PurchaseReturnInventoryDetailsModel mdl=null;
		try {
			begin();
			mdl=(PurchaseReturnInventoryDetailsModel)getSession().get(PurchaseReturnInventoryDetailsModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(PurchaseReturnModel mdl, TransactionModel transaction) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List<Long> purchaseList=new ArrayList<Long>();
			List<Long> newPurchaseList=new ArrayList<Long>();
			List transactionList=new ArrayList();
			
			List<Long> transactionOldList=new ArrayList<Long>();
			List expenseList=new ArrayList();
			List oldIdList=new ArrayList();
			
			oldList=getSession().createQuery("select b from PurchaseReturnModel a join a.inventory_details_list b where a.id=:id")
								.setParameter("id", mdl.getId()).list();
			
			expenseList=getSession().createQuery("select b.id from PurchaseReturnModel a join a.purchase_return_expense_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			transactionList=getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransaction_id()).list();
			
			Iterator transItr=transactionList.iterator();
			
			while (transItr.hasNext()) {
				TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				transactionOldList.add(tdm.getId());
				flush();
				
			}
			
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				PurchaseReturnInventoryDetailsModel det = (PurchaseReturnInventoryDetailsModel) itr.next();
				
				if(det.getPurchase_id()!=0){
					
					if(!purchaseList.contains(det.getPurchase_id())) {
						purchaseList.add(det.getPurchase_id());
						getSession().createQuery("update PurchaseModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_id()).executeUpdate();
						flush();
					}
					
					if(det.getPurchase_child_id()!=0) {
						getSession().createQuery("update PurchaseInventoryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_child_id()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0)
					new CommonMethodsDao().increaseStockByStockID(det.getStock_id(), CommonUtil.roundNumber(det.getQty_in_basic_unit()), false);
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				oldIdList.add(det.getId());
			}
			
			// Updating
			
			getSession().update(transaction);
			
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tdm;
			
			while (aciter.hasNext()) {
				tdm = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
				
			}
			
			List<PurchaseReturnInventoryDetailsModel> childList = new ArrayList<PurchaseReturnInventoryDetailsModel>();
			itr=mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				PurchaseReturnInventoryDetailsModel det = (PurchaseReturnInventoryDetailsModel) itr.next();
				
				if(det.getPurchase_id()!=0){
					
					if(!newPurchaseList.contains(det.getPurchase_id())) {
						newPurchaseList.add(det.getPurchase_id());
						getSession().createQuery("update PurchaseModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getPurchase_id()).executeUpdate();
						flush();
					}
					
					if(det.getPurchase_child_id()!=0) {
						getSession().createQuery("update PurchaseInventoryDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getPurchase_child_id()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0)
					new CommonMethodsDao().decreaseStockByStockID(det.getStock_id(), CommonUtil.roundNumber(det.getQty_in_basic_unit()), false);
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				childList.add(det);
			}
			mdl.setInventory_details_list(childList);
			getSession().clear();
			flush();
			getSession().update(mdl);
			
			if(oldIdList.size()>0){
				Iterator it=oldIdList.iterator();
				while (it.hasNext()) {
					long id=(Long)it.next();
					PurchaseReturnInventoryDetailsModel det=(PurchaseReturnInventoryDetailsModel)getSession().get(PurchaseReturnInventoryDetailsModel.class, id);
					getSession().delete(det);
					flush();
				}
			}
			
			if(transactionOldList.size()>0){
				
				getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) transactionOldList).executeUpdate();
				
			}
			
			if(expenseList.size()>0){
				
				getSession().createQuery("delete from PurchaseExpenseDetailsModel where id in (:lst)")
				.setParameterList("lst", (Collection) expenseList).executeUpdate();
				
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings({ "rawtypes"})
	public void delete(PurchaseReturnModel mdl) throws Exception {
		try {
			begin();
			List<Long> purchaseList=new ArrayList<Long>();
			
			TransactionModel transaction=(TransactionModel)getSession().get(TransactionModel.class, mdl.getTransaction_id());
			
			if(transaction!=null){
				Iterator transItr=transaction.getTransaction_details_list().iterator();
				
				while (transItr.hasNext()) {
					TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

					flush();
					
				}
				getSession().delete(transaction);
			}
			
			flush();
			
			Iterator itr=mdl.getInventory_details_list().iterator();
			
			while (itr.hasNext()) {
				PurchaseReturnInventoryDetailsModel det = (PurchaseReturnInventoryDetailsModel) itr.next();
				
				if(det.getPurchase_id()!=0){
					
					if(!purchaseList.contains(det.getPurchase_id())) {
						purchaseList.add(det.getPurchase_id());
						getSession().createQuery("update PurchaseModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_id()).executeUpdate();
						flush();
					}
					
					if(det.getPurchase_child_id()!=0) {
						getSession().createQuery("update PurchaseInventoryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_child_id()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0)
					new CommonMethodsDao().increaseStockByStockID(det.getStock_id(), CommonUtil.roundNumber(det.getQty_in_basic_unit()), false);
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
			}
			getSession().delete(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void cancel(PurchaseReturnModel mdl) throws Exception {
		try {
			begin();
			List<Long> purchaseList=new ArrayList<Long>();
			
			TransactionModel transaction=(TransactionModel)getSession().get(TransactionModel.class, mdl.getTransaction_id());
			
			Iterator transItr=transaction.getTransaction_details_list().iterator();
			
			while (transItr.hasNext()) {
				TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
							.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
							.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

				flush();
				
			}
			
			getSession().delete(transaction);
			
			flush();
			
			Iterator itr=mdl.getInventory_details_list().iterator();
			
			while (itr.hasNext()) {
				PurchaseReturnInventoryDetailsModel det = (PurchaseReturnInventoryDetailsModel) itr.next();
				
				if(det.getPurchase_id()!=0){
					
					if(!purchaseList.contains(det.getPurchase_id())) {
						purchaseList.add(det.getPurchase_id());
						getSession().createQuery("update PurchaseModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_id()).executeUpdate();
						flush();
					}
					
					if(det.getPurchase_child_id()!=0) {
						getSession().createQuery("update PurchaseInventoryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_child_id()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0)
					new CommonMethodsDao().increaseStockByStockID(det.getStock_id(), CommonUtil.roundNumber(det.getQty_in_basic_unit()), false);
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
			}
			getSession().createQuery("update PurchaseReturnModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}

	
	@SuppressWarnings("rawtypes")
	public List getPurchaseModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseModel(id,concat('Purchase No: ',purchase_no,', Date: ',cast(date as string)))" +
					" from PurchaseModel where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("supplier", supplier).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getPurchaseModelItemsSupplierList(long office, long supplier, Set<Long> purchase, List idList) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(purchase!=null && purchase.size()>0)
				cdn+=" and a.id in "+purchase.toString().replace('[', '(').replace(']', ')');
			
			if(idList!=null && idList.size()>0)
				cdn+=" and b.id not in "+idList.toString().replace('[', '(').replace(']', ')');
			
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseModel(b.id,concat('Purchase No: ',a.purchase_no,', Date: ',cast(a.date as string)," +
					"' Item: ',b.item.name, ' ', b.qunatity, ' ',b.unit.symbol))" +
					" from PurchaseModel a join a.purchase_details_list b where a.office.id=:office and a.active=true and a.supplier.id=:supplier "+cdn+" order by a.id DESC")
					.setParameter("office", office).setParameter("supplier", supplier).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getAllDataFromPurchase(Set<Long> purchase) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseReturnBean(a.id, b,a) from PurchaseModel a " +
								"join a.purchase_details_list b where b.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchase).list();
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
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getPurchaseGRNModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseGRNModel(id,concat('GRN No: ',grn_no,', GRN Date: ',cast(date as string),', Amount: ',amount))" +
					" from PurchaseGRNModel where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("supplier", supplier).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getAllDataFromPurchaseGRN(Set<Long> purchaseGRN) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseBean(a.id, b) from PurchaseGRNModel a " +
								"join a.grn_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchaseGRN).list();
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
		return list;
	}
	
}
