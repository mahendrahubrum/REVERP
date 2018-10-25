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
import com.inventory.config.stock.model.ItemCustomerBarcodeMapModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.GrvSalesInventoryDetailsModel;
import com.inventory.sales.model.GrvSalesModel;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
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
 * @date 23-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class GrvSalesDao extends SHibernate implements Serializable{

	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	
	ItemDao itemDao=new ItemDao();
	
	
	@SuppressWarnings("rawtypes")
	public long save(GrvSalesModel mdl, TransactionModel transaction, double payingAmt, int update_rate_settings)
			throws Exception {
		
		try {
			begin();
			
			getSession().save(transaction);
			
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
			
//			if(payingAmt!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", payingAmt).setLong("id", mdl.getCustomer().getId()).executeUpdate();
			
			
			
			List<GrvSalesInventoryDetailsModel> invList = new ArrayList<GrvSalesInventoryDetailsModel>();
			Iterator<GrvSalesInventoryDetailsModel> it = mdl.getInventory_details_list().iterator();
			while (it.hasNext()) {
				GrvSalesInventoryDetailsModel det = it.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();

				comDao.decreaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());
	
				flush();
				
				det.setStock_ids(det.getStock_id()+":"+det.getQuantity_in_basic_unit());
				
//				if(update_rate_settings!=0)
//					comDao.updateConvertionQtyAndRate(update_rate_settings, det.getItem().getId(), det.getUnit().getId(), mdl.getSales_type(), 
//							det.getUnit_price(), (CommonUtil.roundNumber(det.getQuantity_in_basic_unit()/det.getQunatity())));
				
				invList.add(det);
			}

			mdl.setInventory_details_list(invList);
			getSession().save(mdl);
			flush();
			
			Iterator itr3=mdl.getInventory_details_list().iterator();
			while (itr3.hasNext()) {
				GrvSalesInventoryDetailsModel det=(GrvSalesInventoryDetailsModel) itr3.next();
				String[] stks=det.getStock_ids().split(",");
				for (String string : stks) {
					if(string.length()>2)
					getSession().save(new SalesStockMapModel(mdl.getId(), det.getId(), 
										Long.parseLong(string.split(":")[0]),Double.parseDouble(string.split(":")[1]),1));
					flush();
				}
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
		return mdl.getId();
	}

	
	public GrvSalesModel getSalesModel(long id) throws Exception {
		GrvSalesModel pur = null;
		try {
			begin();
			pur = (GrvSalesModel) getSession().get(GrvSalesModel.class, id);
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
	public void update(GrvSalesModel mdl, TransactionModel transaction, double payingAmt)
			throws Exception {
		try {

			begin();

			List oldList=new ArrayList();

			List transactionList=new ArrayList();
			
			List<Long> transactionOldList=new ArrayList<Long>();
			List oldIdList=new ArrayList();
			List expenseList=new ArrayList();
			
			oldList=getSession().createQuery("select b from GrvSalesModel a join a.inventory_details_list b where a.id=:id")
								.setParameter("id", mdl.getId()).list();
			
			expenseList=getSession().createQuery("select b.id from GrvSalesModel a join a.sales_expense_list b where a.id=:id")
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
				GrvSalesInventoryDetailsModel det = (GrvSalesInventoryDetailsModel) itr.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();

				comDao.increaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());
			
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
			
			List<GrvSalesInventoryDetailsModel> childList = new ArrayList<GrvSalesInventoryDetailsModel>();
			itr=mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				
				GrvSalesInventoryDetailsModel det = (GrvSalesInventoryDetailsModel) itr.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();

				comDao.decreaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());

				flush();
				
				det.setStock_ids(det.getStock_id()+":"+det.getQuantity_in_basic_unit());
				
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				childList.add(det);
			}
			mdl.setInventory_details_list(childList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			
			if (oldIdList.size() > 0) {
				getSession().createQuery("delete from GrvSalesInventoryDetailsModel where id in (:lst)")
						.setParameterList("lst", (Collection) oldIdList).executeUpdate();
			}
			
			if(transactionOldList.size()>0){
				getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) transactionOldList).executeUpdate();
			}
			
			if(expenseList.size()>0){
				getSession().createQuery("delete from SalesExpenseDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) expenseList).executeUpdate();
			}
			
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=1").setLong("id", mdl.getId()).executeUpdate();
			flush();
			
			Iterator itr3=mdl.getInventory_details_list().iterator();
			GrvSalesInventoryDetailsModel detMdl;
			while (itr3.hasNext()) {
				detMdl=(GrvSalesInventoryDetailsModel) itr3.next();
				String[] stks=detMdl.getStock_ids().split(",");
				for (String string : stks) {
					if(string.length()>2)
					getSession().save(new SalesStockMapModel(mdl.getId(), detMdl.getId(), Long.parseLong(string.split(":")[0]),
							Double.parseDouble(string.split(":")[1]),1));
				}
				flush();
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
	}

	
	public void delete(long id) throws Exception {
		try {
			begin();
			GrvSalesModel mdl = (GrvSalesModel) getSession().get(GrvSalesModel.class, id);
			
			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, mdl.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);
			
			flush();

			Iterator<GrvSalesInventoryDetailsModel> it = mdl.getInventory_details_list().iterator();
			while (it.hasNext()) {
				GrvSalesInventoryDetailsModel det = it.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();

				comDao.increaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());
			
				flush();
				
			}
			getSession().delete(mdl);
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
	
	
	public void cancelSale(long id) throws Exception {
		try {
			begin();
			
			GrvSalesModel mdl = (GrvSalesModel) getSession().get(GrvSalesModel.class, id);
			
			// Transaction Related
			
			TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, mdl.getTransaction_id());
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();
				
				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				
				flush();
			}
			
			getSession().delete(transObj);
			flush();
			
			GrvSalesInventoryDetailsModel det;
			Iterator<GrvSalesInventoryDetailsModel> it = mdl.getInventory_details_list().iterator();
			while (it.hasNext()) {
				det = it.next();

				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();

				comDao.increaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());

				flush();
				
			}
			getSession().createQuery("update GrvSalesModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
			getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=1").setLong("id", mdl.getId()).executeUpdate();
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
	}

	
	@SuppressWarnings("rawtypes")
	public List getAllSalesNumbersAsComment(long ofc_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.GrvSalesModel(id,cast(sales_number as string) )"
									+ " from GrvSalesModel where office.id=:ofc and active=true order by sales_number desc")
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
	public List getDeliveryNoteModelSalesManList(long office, long salesman, List lst) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(lst!=null && lst.size()>0)
				cdn+=" and id not in "+lst.toString().replace('[', '(').replace(']', ')');
			list=getSession().createQuery("select new com.inventory.sales.model.DeliveryNoteModel(id,concat('Delivery No: ',deliveryNo,', Date: ',cast(date as string),', Amount: ',amount))" +
					" from DeliveryNoteModel where office.id=:office and active=true and responsible_employee=:salesman "+cdn+" order by id DESC")
					.setParameter("office", office).setParameter("salesman", salesman).list();
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
							"a.customer.name, b.unit_price, a.date)   from GrvSalesModel a join a.inventory_details_list b"
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

	
	public GrvSalesInventoryDetailsModel getSaleInventoryDetailsModel(long id) throws Exception {
		GrvSalesInventoryDetailsModel detMdl;
		try {
			begin();
			detMdl=(GrvSalesInventoryDetailsModel) getSession().get(GrvSalesInventoryDetailsModel.class, id);
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> getAllSalesDetailsForCustomer(long ledgerId, Date fromDate, Date last_payable_date) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.GrvSalesModel(sales_number,payment_amount,amount)"
									+ " from GrvSalesModel where customer.id=:custId and date between :fromDate and :lastdate" +
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
							" from GrvSalesModel where office.id=:office and active=true and date between :fromDate and :toDate" +
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

	
	@SuppressWarnings("rawtypes")
	public List getAllSalesNumbersOfCustomer(long office, long customer) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.GrvSalesModel(id,cast(sales_number as string) )"
									+ " from GrvSalesModel where office.id=:office and active=true and customer.id=:customer and  active=true order by sales_number desc")
					.setParameter("office", office).setParameter("customer", customer).list();
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
	public List getAllSalesNumbersForCustomer(long officeId,long customerId, Date from_date,	Date to_date, String condition1) throws Exception{
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.GrvSalesModel(id,cast(sales_number as string) )"
									+ " from GrvSalesModel where office.id=:office and active=true and customer.id=:customer and  active=true" +
									" and date between :from_date and :to_date" +
									condition1+
									" order by sales_number desc")
									.setParameter("office", officeId)
									.setParameter("customer", customerId)
									.setParameter("from_date", from_date)
									.setParameter("to_date", to_date).list();
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
	public List getAllSalesNumbersByDate(long officeId,Date from_date, Date to_date,String condition1) throws Exception{
		List resultList = new ArrayList();
			try {
				begin();
				resultList = getSession()
						.createQuery(
								"select new com.inventory.sales.model.GrvSalesModel(id,sales_number)"
										+ " from GrvSalesModel where office.id=:office and  active=true" +
										" and date between :from_date and :to_date" +
										condition1+
										" order by sales_number desc")
										.setParameter("office", officeId)
										.setParameter("from_date", from_date)
										.setParameter("to_date", to_date).list();
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
			return resultList;}


	@SuppressWarnings("rawtypes")
	public ItemStockModel getNewItemStock(String code,long custId) throws Exception {
		List codeList=null;
		ItemStockModel stk=null;
		try {
			begin();
			ItemCustomerBarcodeMapModel mapMdl=null;
			List lis =  getSession()
					.createQuery(" from ItemCustomerBarcodeMapModel where customerId=:cust and barcode=:barc")
					.setParameter("barc", code).setParameter("cust", custId)
					.list();
			if(lis!=null&&lis.size()>0)
				mapMdl=(ItemCustomerBarcodeMapModel) lis.get(0);
			
			if(mapMdl!=null){
				codeList = getSession()
						.createQuery("from ItemStockModel where item.id=:itm order by id desc").setParameter("itm",mapMdl.getItemId()).list();
			}
			
			if(codeList!=null&&codeList.size()>0)
				stk=(ItemStockModel) codeList.get(0);
			
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
	
	
	public List getAllItemsWithRealStck(long ofc_id) throws Exception {
		List resultList=new ArrayList();
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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			
		}
		return resultList;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object> getAllSalesIDsForCustomer(long ledgerId,
			Date fromDate, Date toDate, boolean isCreateNew) throws Exception {
		List resultList=null;
		try {
			String criteria="";
			if(isCreateNew)
				criteria=" and payment_done='N'";
			
			begin();
			
			resultList = getSession().createQuery("select new com.inventory.sales.model.GrvSalesModel(id,concat(sales_number, '  - ', date,'(',amount-payment_amount-paid_by_payment,')' ))"
									+ " from GrvSalesModel where customer.id=:custId and date between :fromDate and :toDate" +
									" and status in (2,3) and active=true "+criteria+" order by (amount-payment_amount-paid_by_payment-discount_amount) ASC ")
					.setParameter("custId", ledgerId).setParameter("fromDate", fromDate)
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
	
	
	@SuppressWarnings("rawtypes")
	public String getSalesOrderSales(long sales, long office) throws Exception {
		String purchaseId="";
		try {
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select distinct b.order_id from GrvSalesModel a join a.inventory_details_list b where a.id=:id and a.office.id=:office")
							.setParameter("id", sales).setParameter("office", office).list();
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					long id = (Long) itr.next();
					if(id!=0){
						SalesOrderModel mdl=(SalesOrderModel) getSession().get(SalesOrderModel.class, id);
						purchaseId+=mdl.getOrder_no()+", ";
					}
				}
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return purchaseId;
	}
	
	public TransactionModel getTransaction(long transId) throws Exception {
		TransactionModel tranMdl;
		try {
			begin();
			tranMdl=(TransactionModel) getSession().get(TransactionModel.class, transId);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return tranMdl;
	}
	
	public List<Object> getAllGRVSalesNumbersForSupplier(long officeId, long custId,
			Date fromDate, Date toDate,String condition1) throws Exception {
List resultList=null;
		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel where customer.id=:custId and date between :fromDate and :toDate and type=2"+condition)
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
}
