package com.inventory.purchase.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import com.inventory.config.stock.model.BatchModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseOrderModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PurchaseDao extends SHibernate implements Serializable {

	
	@SuppressWarnings("rawtypes")
	public long save(PurchaseModel mdl, TransactionModel transaction) throws Exception {
		try {
			begin();
			List<PurchaseInventoryDetailsModel> childList = new ArrayList<PurchaseInventoryDetailsModel>();
			List<Long> orderList=new ArrayList<Long>(); 
			List<Long> grnList=new ArrayList<Long>(); 
			
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
			
			getSession().save(mdl);
			
			Iterator itr=mdl.getPurchase_details_list().iterator();
			while (itr.hasNext()) {
				
				PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
				
				if(det.getGrn_id()!=0){
					
					// Update Purchase GRN Parent
					if(det.getGrn_id()!=0){	
						if(!grnList.contains(det.getGrn_id())) {
							grnList.add(det.getGrn_id());
							getSession().createQuery("update PurchaseGRNModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
										.setParameter("id", det.getGrn_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase GRN Child
					if(det.getGrn_child_id()!=0) {	
						getSession().createQuery("update PurchaseGRNDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getGrn_child_id()).executeUpdate();
						flush();
					}
					
				}
				else{
					
					// Update Purchase Order Parent
					if(det.getOrder_id()!=0){
						if(!orderList.contains(det.getOrder_id())) {
							orderList.add(det.getOrder_id());
							getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
										.setParameter("id", det.getOrder_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase Order Child
					if(det.getOrder_child_id()!=0) {
						getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received+:qty where id=:id")
									.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getGrn_id()==0){
					// Update Item Balance
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
								.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
					flush();
				}
				
				double rate = 0;
				try {
					rate=(det.getQty_in_basic_unit()/det.getQunatity())*((det.getUnit_price()/det.getConversionRate())+det.getTaxAmount()+det.getCessAmount()-det.getDiscount());
				} catch (Exception e) {
					rate = 0;
				}
				
				
				BatchModel batch=null;
				if(det.getBatch_id()!=0){
					batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
				}
				if(batch==null)
					batch=new BatchModel();
				batch.setItem(det.getItem());
				batch.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate(det.getManufacturing_date()));
				batch.setExpiry_date(CommonUtil.getSQLDateFromUtilDate(det.getExpiry_date()));
				batch.setManufacturer("");
				batch.setRate(rate);
				batch.setDescription("");
				batch.setOffice_id(mdl.getOffice().getId());
				if(batch.getId()!=0)
					getSession().update(batch);
				else
					getSession().save(batch);
				
				flush();

				
				ItemStockModel stock=null;
				if(det.getStock_id()==0){
					stock=new ItemStockModel();
				}
				else{
					stock=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
				}
				stock.setExpiry_date(det.getExpiry_date());
				stock.setItem(det.getItem());
				if(det.getGrn_id()==0){
					stock.setPurchase_type(SConstants.stockPurchaseType.PURCHASE);
					stock.setPurchase_id(mdl.getId());
					stock.setInv_det_id(det.getId());
				}
				stock.setBarcode(det.getBarcode());
				stock.setQuantity(det.getQty_in_basic_unit());
				stock.setBalance(det.getQty_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(2);
				stock.setManufacturing_date(det.getManufacturing_date());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(det.getGrade_id());
				stock.setItem_tag("");
				stock.setLocation_id(det.getLocation_id());
				stock.setBatch_id(batch.getId());
				if(det.getStock_id()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				
				flush();
				
				det.setBatch_id(batch.getId());
				det.setStock_id(stock.getId());
				childList.add(det);
			}
			flush();
			mdl.setPurchase_details_list(childList);
			
			flush();
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
	public List getPurchaseModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseModel(id,purchase_no) from PurchaseModel " +
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
	
	
	public PurchaseModel getPurchaseModel(long id) throws Exception {
		PurchaseModel mdl=null;
		try {
			begin();
			mdl=(PurchaseModel)getSession().get(PurchaseModel.class, id);
			Hibernate.initialize(mdl.getPurchase_expense_list());
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
	
	
	public PurchaseInventoryDetailsModel getPurchaseInventoryDetailsModel(long id) throws Exception {
		PurchaseInventoryDetailsModel mdl=null;
		try {
			begin();
			mdl=(PurchaseInventoryDetailsModel)getSession().get(PurchaseInventoryDetailsModel.class, id);
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
	public void update(PurchaseModel mdl, TransactionModel transaction) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List oldIdList=new ArrayList();
			List<Long> orderList=new ArrayList<Long>();
			List<Long> newOrderList=new ArrayList<Long>();
			List<Long> grnList=new ArrayList<Long>(); 
			List<Long> newGrnList=new ArrayList<Long>();
			
			List expenseList=new ArrayList();
			
			List transactionList=new ArrayList();
			List<Long> transactionOldList=new ArrayList<Long>();
			
			oldList=getSession().createQuery("select b from PurchaseModel a join a.purchase_details_list b where a.id=:id")
								.setParameter("id", mdl.getId()).list();
			
			expenseList=getSession().createQuery("select b.id from PurchaseModel a join a.purchase_expense_list b where a.id=:id")
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
				PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
				
				if(det.getGrn_id()!=0){
					
					// Update Purchase GRN Parent
					if(det.getGrn_id()!=0){	
						if(!grnList.contains(det.getGrn_id())) {
							grnList.add(det.getGrn_id());
							getSession().createQuery("update PurchaseGRNModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getGrn_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase GRN Child
					if(det.getGrn_child_id()!=0) {	
						getSession().createQuery("update PurchaseGRNDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getGrn_child_id()).executeUpdate();
						flush();
					}
					
				}
				else{
					
					// Update Purchase Order Parent
					if(det.getOrder_id()!=0){
						if(!orderList.contains(det.getOrder_id())) {
							orderList.add(det.getOrder_id());
							getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getOrder_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase Order Child
					if(det.getOrder_child_id()!=0) {
						getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
									.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				if(det.getGrn_id()==0){	
					// Update Item Balance
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
								.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
					flush();
				}
				
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
			
			List<PurchaseInventoryDetailsModel> childList = new ArrayList<PurchaseInventoryDetailsModel>();
			itr=mdl.getPurchase_details_list().iterator();
			while (itr.hasNext()) {
				
				PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
				
				if(det.getGrn_id()!=0){
					
					// Update Purchase GRN Parent
					if(det.getGrn_id()!=0){	
						if(!newGrnList.contains(det.getGrn_id())) {
							newGrnList.add(det.getGrn_id());
							getSession().createQuery("update PurchaseGRNModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
										.setParameter("id", det.getGrn_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase GRN Child
					if(det.getGrn_child_id()!=0) {	
						getSession().createQuery("update PurchaseGRNDetailsModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getGrn_child_id()).executeUpdate();
						flush();
					}
					
				}
				else{
					
					// Update Purchase Order Parent
					if(det.getOrder_id()!=0){
						if(!newOrderList.contains(det.getOrder_id())) {
							newOrderList.add(det.getOrder_id());
							getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
										.setParameter("id", det.getOrder_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase Order Child
					if(det.getOrder_child_id()!=0) {
						getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received+:qty where id=:id")
									.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getGrn_id()==0){	
					// Update Item Balance
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
								.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
					flush();
				}

				double rate = 0;
				try {
					rate=(det.getQty_in_basic_unit()/det.getQunatity())*((det.getUnit_price()/det.getConversionRate())+det.getTaxAmount()+det.getCessAmount()-det.getDiscount());
				} catch (Exception e) {
					rate = 0;
				}
				
				
				BatchModel batch=null;
				if(det.getBatch_id()!=0){
					batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
				}
				if(batch==null)
					batch=new BatchModel();
				batch.setItem(det.getItem());
				batch.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate(det.getManufacturing_date()));
				batch.setExpiry_date(CommonUtil.getSQLDateFromUtilDate(det.getExpiry_date()));
				batch.setManufacturer("");
				batch.setRate(rate);
				batch.setDescription("");
				batch.setOffice_id(mdl.getOffice().getId());
				if(batch.getId()!=0)
					getSession().update(batch);
				else
					getSession().save(batch);
				
				flush();

				
				ItemStockModel stock=null;
				if(det.getStock_id()==0){
					stock=new ItemStockModel();
				}
				else{
					stock=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
				}
				stock.setExpiry_date(det.getExpiry_date());
				stock.setItem(det.getItem());
				if(det.getGrn_id()==0){
					stock.setPurchase_type(SConstants.stockPurchaseType.PURCHASE);
					stock.setPurchase_id(mdl.getId());
					stock.setInv_det_id(det.getId());
				}
				stock.setBarcode(det.getBarcode());
				stock.setQuantity(det.getQty_in_basic_unit());
				stock.setBalance(det.getQty_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(2);
				stock.setManufacturing_date(det.getManufacturing_date());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(det.getGrade_id());
				stock.setItem_tag("");
				stock.setLocation_id(det.getLocation_id());
				stock.setBatch_id(batch.getId());
				if(det.getStock_id()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				
				flush();
				
				det.setBatch_id(batch.getId());
				det.setStock_id(stock.getId());
				
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				childList.add(det);
			}
			mdl.setPurchase_details_list(childList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			
			if(oldIdList.size()>0){
				Iterator it=oldIdList.iterator();
				while (it.hasNext()) {
					long id=(Long)it.next();
					PurchaseInventoryDetailsModel det=(PurchaseInventoryDetailsModel)getSession().get(PurchaseInventoryDetailsModel.class, id);
					if(det.getStock_id()!=0){
						ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
						getSession().delete(stck);
						flush();
					}
					if(det.getBatch_id()!=0){
						BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
						getSession().delete(batch);
						flush();
					}
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
	public void delete(PurchaseModel mdl) throws Exception {
		try {
			begin();
			List<Long> orderList=new ArrayList<Long>();
			List<Long> grnList=new ArrayList<Long>(); 
			
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
			
			Iterator itr=mdl.getPurchase_details_list().iterator();
			
			while (itr.hasNext()) {
				PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
				
				if(det.getGrn_id()!=0){
					
					// Update Purchase GRN Parent
					if(det.getGrn_id()!=0){	
						if(!grnList.contains(det.getGrn_id())) {
							grnList.add(det.getGrn_id());
							getSession().createQuery("update PurchaseGRNModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getGrn_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase GRN Child
					if(det.getGrn_child_id()!=0) {	
						getSession().createQuery("update PurchaseGRNDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getGrn_child_id()).executeUpdate();
						flush();
					}
					
				}
				else{
					
					// Update Purchase Order Parent
					if(det.getOrder_id()!=0){
						if(!orderList.contains(det.getOrder_id())) {
							orderList.add(det.getOrder_id());
							getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getOrder_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase Order Child
					if(det.getOrder_child_id()!=0) {
						getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
									.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getGrn_id()==0){
					
					if(det.getStock_id()!=0){
						ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
						getSession().delete(stck);
						flush();
					}
					
					if(det.getBatch_id()!=0){
						BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
						getSession().delete(batch);
						flush();
					}
					
					// Update Item Balance
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
								.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
					flush();
				}
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
	public void cancel(PurchaseModel mdl) throws Exception {
		try {
			begin();
			List<Long> orderList=new ArrayList<Long>();
			List<Long> grnList=new ArrayList<Long>(); 
			
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
			
			Iterator itr=mdl.getPurchase_details_list().iterator();
			
			while (itr.hasNext()) {
				PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
				
				if(det.getGrn_id()!=0){
					
					// Update Purchase GRN Parent
					if(det.getGrn_id()!=0){	
						if(!grnList.contains(det.getGrn_id())) {
							grnList.add(det.getGrn_id());
							getSession().createQuery("update PurchaseGRNModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getGrn_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase GRN Child
					if(det.getGrn_child_id()!=0) {	
						getSession().createQuery("update PurchaseGRNDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getGrn_child_id()).executeUpdate();
						flush();
					}
					
				}
				else{
					
					// Update Purchase Order Parent
					if(det.getOrder_id()!=0){
						if(!orderList.contains(det.getOrder_id())) {
							orderList.add(det.getOrder_id());
							getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getOrder_id()).executeUpdate();
							flush();
						}
					}
					
					// Update Purchase Order Child
					if(det.getOrder_child_id()!=0) {
						getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
									.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getGrn_id()==0){
					
					if(det.getStock_id()!=0){
						ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
						getSession().delete(stck);
						flush();
					}
					
					if(det.getBatch_id()!=0){
						BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
						getSession().delete(batch);
						flush();
					}
					
					// Update Item Balance
					getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
								.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
					flush();
					
				}
				
			}
			getSession().createQuery("update PurchaseModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	public List getPurchaseOrderModelSupplierList(long office, long supplier, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseOrderModel(id,concat('Order No: ',order_no,', Order Date: ',cast(date as string),', Approximate Amount: ',amount))" +
					" from PurchaseOrderModel where office.id=:office and active=true and supplier.id=:supplier "+cdn+" order by id DESC")
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
	public List getAllDataFromPurchaseOrder(Set<Long> purchaseOrders) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseGRNBean(a.id, b) from PurchaseOrderModel a " +
								"join a.order_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", purchaseOrders).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public String getBarcodeFromStock(long itemId) throws Exception {
		List list;
		String code="";
		try {
			begin();
			list = getSession().createQuery("select barcode from ItemStockModel where item.id=:item and barcode!=null order by id desc limit 0")
					.setParameter("item", itemId).list();
			commit();
			if(list!=null&&list.size()>0)
				if(list.get(0)!=null)
					code=list.get(0).toString();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return code;
	}

	
	@SuppressWarnings("rawtypes")
	public List getPurchaseModelOfSupplier(long office, long supplier) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseModel(id,purchase_no) from PurchaseModel " +
					"where office.id=:office and supplier.id=:supplier and active=true order by id DESC")
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


	public List<PurchaseOrderModel> getAllPurchaseOrdersForSupplier(
			long supplier_id, long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseOrderModel(a.id,concat(a.order_no, '  - ', a.supplier.name, ' - ',a.date) )"
									+ " from PurchaseOrderModel a join a.order_details_list b" +
									" where a.office.id=:ofc and a.supplier.id=:sup and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", office_id)
									.setParameter("sup", supplier_id)
									.setParameter("from_date", fromDate)
									.setParameter("to_date", toDate).list();
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


	public List<PurchaseOrderModel> getAllPurchaseOrdersForOffice(
			long officeId, Date fromDate, Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
			
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseOrderModel(a.id,concat(a.order_no, '  - ', a.supplier.name, ' - ',a.date) )"
									+ " from PurchaseOrderModel a join a.order_details_list b" +
									" where a.office.id=:ofc and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", officeId)
									//.setParameter("sup", supplier_id)
									.setParameter("from_date", fromDate)
									.setParameter("to_date", toDate).list();
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
	
	public List getAllPurchaseGRNNumbersForSupplier(long ofc_id, long supplierId,
			Date fromDate, Date toDate, String condition1) throws Exception {
		List resultList = null;
		try {
			begin();

			String condition = condition1;
			if (ofc_id != 0) {
				condition += " and office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseGRNModel(id,grn_no )"
									+ " from PurchaseGRNModel where supplier.id=:suppId and date>=:fromDate and date<=:toDate  "
									+ condition)
					.setParameter("suppId", supplierId)
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
	
	public List getAllPurchaseGRNNumbersFromDate(long ofc_id,
			Date fromDate, Date toDate, String condition1) throws Exception {
		List resultList = null;
		try {
			begin();

			String condition = condition1;
			if (ofc_id != 0) {
				condition += " and office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery("select new com.inventory.purchase.model.PurchaseGRNModel(id, grn_no )"
									+ " from PurchaseGRNModel where date between :fromDate and :toDate  "
									+ condition)
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

	public List getAllPurchaseNumbersForSupplier(long ofc_id, long supplierId,
			Date fromDate, Date toDate, String condition1) throws Exception {
		List resultList = null;
		try {
			begin();

			String condition = condition1;
			if (ofc_id != 0) {
				condition += " and office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseModel(id,cast(purchase_no as string) )"
									+ " from PurchaseModel where supplier.id=:suppId and date>=:fromDate and date<=:toDate  "
									+ condition)
					.setParameter("suppId", supplierId)
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
	
	
	
	public List getAllPurchaseNumbersFromDate(long ofc_id,
			Date fromDate, Date toDate, String condition1) throws Exception {
		List resultList = null;
		try {
			begin();

			String condition = condition1;
			if (ofc_id != 0) {
				condition += " and office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseModel(id,cast(purchase_no as string) )"
									+ " from PurchaseModel where date between :fromDate and :toDate  "
									+ condition)
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
	
	public List getPurchaseChartDetails(java.sql.Date fromDate,
			java.sql.Date toDate, long officeID) throws Exception {
		List resultList = null;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.bean.SalesChartBean(date,coalesce(sum(amount),0))" +
							" from PurchaseModel where office.id=:office and date between :fromDate and :toDate" +
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
	
	public List getPurchaseRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		List resultList=null;
		try {
			
			begin();
				
				resultList = getSession()
						.createQuery(
								"select new com.inventory.purchase.bean.InventoryDetailsPojo(" +
								"a.supplier.name, b.unit_price, a.date)   from PurchaseModel a join a.purchase_details_list b"
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
