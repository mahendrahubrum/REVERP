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
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author anil
 * @date 10-Sep-2015
 * @Project REVERP
 */

/**
 * 
 * @author sangeeth
 * @date 22-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class SalesReturnDao extends SHibernate implements Serializable{

	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	
	ItemDao itemDao=new ItemDao();

	
	public long save(SalesReturnModel mdl, TransactionModel transaction) throws Exception {
		
		try {
			
			begin();
			getSession().save(transaction);
			List<Long> salesList=new ArrayList<Long>(); 
			
			flush();

			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				TransactionDetailsModel tr = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			mdl.setTransaction_id(transaction.getTransaction_id());
			
			List<SalesReturnInventoryDetailsModel> itemList = new ArrayList<SalesReturnInventoryDetailsModel>();
			
			Iterator<SalesReturnInventoryDetailsModel> it = mdl.getInventory_details_list().iterator();
			
			while (it.hasNext()) {
				SalesReturnInventoryDetailsModel det = it.next();
				
				if(det.getSales_id()!=0){
					
					if(det.getSales_id()!=0){
						if(!salesList.contains(det.getSales_id())) {
							salesList.add(det.getSales_id());
							getSession().createQuery("update SalesModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getSales_id()).executeUpdate();
							flush();
						}
					}
					
					if(det.getSales_child_id()!=0) {
						getSession().createQuery("update SalesInventoryDetailsModel a set a.lock_count=(a.lock_count+1), " +
												" a.quantity_returned=(a.quantity_returned+:qty) where a.id=:id")
									.setParameter("id", det.getSales_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
				flush();
				
				getSession().save(mdl);
				
				ItemStockModel stock=null;
				if(det.getStock_id()==0){
					stock=new ItemStockModel();
				}
				else{
					stock=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
				}
				stock.setExpiry_date(mdl.getDate());
				stock.setItem(det.getItem());
				stock.setPurchase_type(SConstants.stockPurchaseType.SALES_RETURN);
				stock.setPurchase_id(mdl.getId());
				stock.setInv_det_id(det.getId());
				stock.setBarcode(det.getBarcode());
				double rate = 0;
				try {
					rate=(det.getQuantity_in_basic_unit()/det.getQunatity())*((det.getUnit_price()/det.getConversionRate())+det.getTaxAmount()+det.getCessAmount()-det.getDiscount());
				} catch (Exception e) {
					rate = 0;
				}
				stock.setQuantity(det.getQuantity_in_basic_unit());
				stock.setBalance(det.getQuantity_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(SConstants.stock_statuses.GOOD_STOCK);
				stock.setManufacturing_date(mdl.getDate());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(det.getGrade_id());
				stock.setItem_tag("");
				stock.setLocation_id(det.getLocation_id());
				if(det.getStock_id()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				flush();
				
				det.setStock_id(stock.getId());
				itemList.add(det);
			}
			mdl.setInventory_details_list(itemList);
			
			flush();
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
		return mdl.getId();
	}
	
	
	public SalesReturnModel getSalesReturnModel(long id) throws Exception {
		SalesReturnModel pur = null;
		try {
			begin();
			pur = (SalesReturnModel) getSession().get(SalesReturnModel.class, id);
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

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(SalesReturnModel mdl, TransactionModel transaction) throws Exception {
		try {

			begin();

			List oldList=new ArrayList();
			
			List<Long> salesList=new ArrayList<Long>();
			List<Long> newSalesList=new ArrayList<Long>();
			
			List transactionList=new ArrayList();
			
			List<Long> transactionOldList=new ArrayList<Long>();
			List expenseList=new ArrayList();
			List oldIdList=new ArrayList();
			
			oldList=getSession().createQuery("select b from SalesReturnModel a join a.inventory_details_list b where a.id=:id")
								.setParameter("id", mdl.getId()).list();
			
			expenseList=getSession().createQuery("select b.id from SalesReturnModel a join a.sales_expense_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			transactionList=getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransaction_id()).list();
			
			Iterator transItr=transactionList.iterator();
			while (transItr.hasNext()) {
				TransactionDetailsModel tr = (TransactionDetailsModel) transItr.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
				transactionOldList.add(tr.getId());
			}
			
			Iterator itr=oldList.iterator();
			
			while (itr.hasNext()) {
				SalesReturnInventoryDetailsModel det = (SalesReturnInventoryDetailsModel) itr.next();
				
				if(det.getSales_id()!=0){
					
					if(det.getSales_id()!=0){
						if(!salesList.contains(det.getSales_id())) {
							salesList.add(det.getSales_id());
							getSession().createQuery("update SalesModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getSales_id()).executeUpdate();
							flush();
						}
					}
					
					if(det.getSales_child_id()!=0) {
						getSession().createQuery("update SalesInventoryDetailsModel a set a.lock_count=(a.lock_count-1), " +
												" a.quantity_returned=(a.quantity_returned-:qty) where a.id=:id")
									.setParameter("id", det.getSales_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
				flush();
				
				oldIdList.add(det.getId());
			}
			
			getSession().update(transaction);
			
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			
			while (aciter.hasNext()) {
				TransactionDetailsModel tdm = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			List<SalesReturnInventoryDetailsModel> childList = new ArrayList<SalesReturnInventoryDetailsModel>();
			itr=mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				
				SalesReturnInventoryDetailsModel det = (SalesReturnInventoryDetailsModel) itr.next();
				
				if(det.getSales_id()!=0){
					
					if(det.getSales_id()!=0){
						if(!newSalesList.contains(det.getSales_id())) {
							newSalesList.add(det.getSales_id());
							getSession().createQuery("update SalesModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getSales_id()).executeUpdate();
							flush();
						}
					}
					
					if(det.getSales_child_id()!=0) {
						getSession().createQuery("update SalesInventoryDetailsModel a set a.lock_count=(a.lock_count+1), " +
												" a.quantity_returned=(a.quantity_returned+:qty) where a.id=:id")
									.setParameter("id", det.getSales_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
				flush();
			
				ItemStockModel stock=null;
				if(det.getStock_id()==0){
					stock=new ItemStockModel();
					stock.setBalance(det.getQuantity_in_basic_unit());
				}
				else{
					stock=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
				}
				stock.setExpiry_date(mdl.getDate());
				stock.setItem(det.getItem());
				stock.setPurchase_type(SConstants.stockPurchaseType.SALES_RETURN);
				stock.setPurchase_id(mdl.getId());
				stock.setInv_det_id(det.getId());
				stock.setBarcode(det.getBarcode());
				double rate = 0;
				try {
					rate=(det.getQuantity_in_basic_unit()/det.getQunatity())*((det.getUnit_price()/det.getConversionRate())+det.getTaxAmount()+det.getCessAmount()-det.getDiscount());
				} catch (Exception e) {
					rate = 0;
				}
				stock.setQuantity(det.getQuantity_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(SConstants.stock_statuses.GOOD_STOCK);
				stock.setManufacturing_date(mdl.getDate());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(det.getGrade_id());
				stock.setItem_tag("");
				stock.setLocation_id(det.getLocation_id());
				if(det.getStock_id()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				flush();
				det.setStock_id(stock.getId());
				
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				childList.add(det);
			}
			mdl.setInventory_details_list(childList);
			getSession().clear();
			flush();
			getSession().update(mdl);
			flush();
			if(oldIdList.size()>0){
				Iterator it=oldIdList.iterator();
				while (it.hasNext()) {
					long id=(Long)it.next();
					SalesReturnInventoryDetailsModel det = (SalesReturnInventoryDetailsModel) getSession().get(SalesReturnInventoryDetailsModel.class, id);
					
					if(det.getStock_id()!=0){
						ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
						getSession().delete(stck);
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
				getSession().createQuery("delete from SalesExpenseDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) expenseList).executeUpdate();
			}
			flush();
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	
	@SuppressWarnings("rawtypes")
	public void delete(long id) throws Exception {
		try {
			begin();
			SalesReturnModel mdl = (SalesReturnModel) getSession().get(SalesReturnModel.class, id);
			
			List<Long> salesList=new ArrayList<Long>();

			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, mdl.getTransaction_id());

			Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				TransactionDetailsModel tr = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);
			
			flush();
			
			Iterator itr=mdl.getInventory_details_list().iterator();
			
			while (itr.hasNext()) {
				SalesReturnInventoryDetailsModel det = (SalesReturnInventoryDetailsModel) itr.next();
				
				if(det.getSales_id()!=0){
					
					if(det.getSales_id()!=0){
						if(!salesList.contains(det.getSales_id())) {
							salesList.add(det.getSales_id());
							getSession().createQuery("update SalesModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getSales_id()).executeUpdate();
							flush();
						}
					}
					
					if(det.getSales_child_id()!=0) {
						getSession().createQuery("update SalesInventoryDetailsModel a set a.lock_count=(a.lock_count-1), " +
												" a.quantity_returned=(a.quantity_returned-:qty) where a.id=:id")
									.setParameter("id", det.getSales_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0){
					ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
					getSession().delete(stck);
					flush();
				}
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
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
	public void cancel(long id) throws Exception {
		try {
			begin();
			List<Long> salesList=new ArrayList<Long>();
			SalesReturnModel mdl = (SalesReturnModel) getSession().get(SalesReturnModel.class, id);
			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, mdl.getTransaction_id());
			
			Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				TransactionDetailsModel tr = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				
				flush();
			}
			
			
			getSession().delete(transObj);
			
			flush();
			
			Iterator itr=mdl.getInventory_details_list().iterator();
			
			while (itr.hasNext()) {
				SalesReturnInventoryDetailsModel det = (SalesReturnInventoryDetailsModel) itr.next();
				
				if(det.getSales_id()!=0){
					
					if(det.getSales_id()!=0){
						if(!salesList.contains(det.getSales_id())) {
							salesList.add(det.getSales_id());
							getSession().createQuery("update SalesModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getSales_id()).executeUpdate();
							flush();
						}
					}
					
					if(det.getSales_child_id()!=0) {
						getSession().createQuery("update SalesInventoryDetailsModel a set a.lock_count=(a.lock_count-1), " +
												" a.quantity_returned=(a.quantity_returned-:qty) where a.id=:id")
									.setParameter("id", det.getSales_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0){
					ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
					getSession().delete(stck);
					flush();
				}
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
				flush();
			}
			getSession().createQuery("update SalesReturnModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	public List getAllSalesReturnModelList(long ofc_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.sales.model.SalesReturnModel(id,return_no)"
									+ " from SalesReturnModel where office.id=:ofc and active=true order by id desc")
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
	
	
	@SuppressWarnings("rawtypes")
	public List getSalesModelCustomerList(long office, long customer, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.SalesModel(id,concat('Sales No: ',sales_number,', Date: ',cast(date as string)))" +
					" from SalesModel where office.id=:office and active=true and customer.id=:customer "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("customer", customer).list();
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
	public List getSalesModelItemsCustomerList(long office, long customer, Set<Long> sales, List idList) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(sales!=null && sales.size()>0)
				cdn+=" and a.id in "+sales.toString().replace('[', '(').replace(']', ')');
			
			if(idList!=null && idList.size()>0)
				cdn+=" and b.id not in "+idList.toString().replace('[', '(').replace(']', ')');
			
			list=getSession().createQuery("select new com.inventory.sales.model.SalesModel(b.id,concat('Sales No: ',a.sales_number,', Date: ',cast(a.date as string)," +
					"' Item: ',b.item.name, ' ', b.qunatity, ' ',b.unit.symbol))" +
					" from SalesModel a join a.inventory_details_list b where a.office.id=:office and a.active=true and a.customer.id=:customer and " +
					" b.quantity_returned < b.quantity_in_basic_unit "+cdn+" order by a.id DESC")
					.setParameter("office", office).setParameter("customer", customer).list();
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
	public List getAllDataFromSales(Set<Long> sales) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.bean.SalesReturnBean(a.id, b) from SalesModel a " +
								"join a.inventory_details_list b where b.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", sales).list();
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
	public List getDeliveryNoteModelCustomerList(long office, long customer, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(id,concat('Delivery No: ',deliveryNo,', Date: ',cast(date as string),', Amount: ',amount))" +
					" from DeliveryNoteModel where office.id=:office and active=true and customer.id=:customer "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("customer", customer).list();
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
	public List getAllDataFromDeliveryNote(Set<Long> delNote) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.sales.bean.SalesBean(a.id, b) from DeliveryNoteModel a " +
								"join a.delivery_note_details_list b where a.id in (:list) and a.active=true order by a.id")
								.setParameterList("list", delNote).list();
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
	public List getSalesRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		List resultList = new ArrayList();
		try {
			
			begin();
				
			resultList = getSession()
					.createQuery("select new com.inventory.sales.bean.SalesInventoryDetailsPojo(" +
							"a.customer.name, b.unit_price, a.date)   from SalesReturnModel a join a.inventory_details_list b"
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

	
	public SalesReturnInventoryDetailsModel getSaleInventoryDetailsModel(long id) throws Exception {
		SalesReturnInventoryDetailsModel detMdl;
		try {
			begin();
			detMdl=(SalesReturnInventoryDetailsModel) getSession().get(SalesReturnInventoryDetailsModel.class, id);
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
		return detMdl;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> getAllSalesDetailsForCustomer(long ledgerId, Date fromDate, Date last_payable_date) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesReturnModel(sales_number,payment_amount,amount)"
									+ " from SalesReturnModel where customer.id=:custId and active=true and date between :fromDate and :lastdate" +
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
		}
		return resultList;
	}
	
	
	@SuppressWarnings("rawtypes")
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
	
	
	@SuppressWarnings("rawtypes")
	public List getPurchaseRateHistory(long item_id, long unit_id, long office_id) throws Exception {
		List resultList = new ArrayList();
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
		}
		return resultList;
	}

	
	@SuppressWarnings("rawtypes")
	public List getSalesChartDetails(java.sql.Date fromDate, java.sql.Date toDate, long officeID) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.bean.SalesChartBean(date,coalesce(sum(amount),0))" +
							" from SalesReturnModel where office.id=:office and active=true and date between :fromDate and :toDate" +
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


	public String getReason(long reasonId) throws Exception {
		String name="";
		try {
			begin();
			if(reasonId!=0)
				name = (String) getSession()
						.createQuery("select name from ReasonModel where id=:id").setLong("id", reasonId).uniqueResult();
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
		return name;
	}

	
	
}
