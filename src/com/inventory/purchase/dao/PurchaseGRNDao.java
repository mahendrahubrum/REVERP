package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.config.stock.model.BatchModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseGRNDetailsModel;
import com.inventory.purchase.model.PurchaseGRNModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PurchaseGRNDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public long save(PurchaseGRNModel mdl) throws Exception {
		try {
			begin();
			List<PurchaseGRNDetailsModel> childList = new ArrayList<PurchaseGRNDetailsModel>();
			List<Long> orderList=new ArrayList<Long>(); 
			Iterator itr=mdl.getGrn_details_list().iterator();
			while (itr.hasNext()) {
				
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				// Update Purchase Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received+:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Purchase Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				childList.add(det);
			}
			
			mdl.setGrn_details_list(childList);
			getSession().save(mdl);
			
			flush();
			itr=mdl.getGrn_details_list().iterator();
			List<PurchaseGRNDetailsModel> newChildList = new ArrayList<PurchaseGRNDetailsModel>();
			while (itr.hasNext()) {
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				ItemStockModel stock=new ItemStockModel();
				
				stock.setExpiry_date(det.getExpiry_date());
				stock.setItem(det.getItem());
				stock.setPurchase_type(SConstants.stockPurchaseType.PURCHASE_GRN);
				stock.setPurchase_id(mdl.getId());
				stock.setInv_det_id(det.getId());
				double rate=0;
				try {
					rate=(det.getQty_in_basic_unit()/det.getQunatity())*(det.getUnit_price()/det.getConversionRate());
				} catch (Exception e) {
					rate=0;
				}
				stock.setQuantity(det.getQty_in_basic_unit());
				stock.setBalance(det.getQty_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(2);
				stock.setManufacturing_date(det.getManufacturing_date());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(det.getGrade_id());
				stock.setItem_tag("");
				stock.setLocation_id(det.getLocation_id());
				getSession().save(stock);
				flush();
				
				BatchModel batch=new BatchModel();
				batch.setItem(det.getItem());
				batch.setManufacturing_date(CommonUtil.getSQLDateFromUtilDate(det.getManufacturing_date()));
				batch.setExpiry_date(CommonUtil.getSQLDateFromUtilDate(det.getExpiry_date()));
				batch.setManufacturer("");
				batch.setRate(rate);
				batch.setDescription("");
				batch.setOffice_id(mdl.getOffice().getId());
				getSession().save(batch);
				flush();
				
				stock=(ItemStockModel) getSession().get(ItemStockModel.class, stock.getId());
				stock.setBatch_id(batch.getId());
				getSession().update(stock);
				
				det.setBatch_id(batch.getId());
				det.setStock_id(stock.getId());
				newChildList.add(det);
			}
			mdl.setGrn_details_list(newChildList);
			getSession().clear();
			getSession().update(mdl);
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
	public List getPurchaseGRNModelList(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("select new com.inventory.purchase.model.PurchaseGRNModel(id,grn_no) from PurchaseGRNModel " +
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
	
	
	public PurchaseGRNModel getPurchaseGRNModel(long id) throws Exception {
		PurchaseGRNModel mdl=null;
		try {
			begin();
			mdl=(PurchaseGRNModel)getSession().get(PurchaseGRNModel.class, id);
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
	
	
	public PurchaseGRNDetailsModel getPurchaseGRNDetailsModel(long id) throws Exception {
		PurchaseGRNDetailsModel mdl=null;
		try {
			begin();
			mdl=(PurchaseGRNDetailsModel)getSession().get(PurchaseGRNDetailsModel.class, id);
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
	public void update(PurchaseGRNModel mdl) throws Exception {
		try {
			begin();
			List oldList=new ArrayList();
			List oldIdList=new ArrayList();
			List<Long> orderList=new ArrayList<Long>();
			List<Long> newOrderList=new ArrayList<Long>();
			oldList=getSession().createQuery("select b from PurchaseGRNModel a join a.grn_details_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				// Update Purchase Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Purchase Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				oldIdList.add(det.getId());
			}
			
			List<PurchaseGRNDetailsModel> childList = new ArrayList<PurchaseGRNDetailsModel>();
			// Updating
			itr=mdl.getGrn_details_list().iterator();
			while (itr.hasNext()) {
				
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				// Update Purchase Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received+:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Purchase Order Parent
				if(det.getOrder_id()!=0){
					if(!newOrderList.contains(det.getOrder_id())) {
						newOrderList.add(det.getOrder_id());
						getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count+1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				if(det.getId()!=0)
					oldIdList.remove(det.getId());
				
				childList.add(det);
			}
			mdl.setGrn_details_list(childList);
			getSession().clear();
			getSession().update(mdl);
			
			flush();
			itr=mdl.getGrn_details_list().iterator();
			List<PurchaseGRNDetailsModel> newChildList = new ArrayList<PurchaseGRNDetailsModel>();
			while (itr.hasNext()) {
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				ItemStockModel stock=null;
				if(det.getStock_id()!=0){
					stock=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
				}
				if(stock==null)
					stock=new ItemStockModel();
				stock.setExpiry_date(det.getExpiry_date());
				stock.setItem(det.getItem());
				stock.setPurchase_type(SConstants.stockPurchaseType.PURCHASE_GRN);
				stock.setPurchase_id(mdl.getId());
				stock.setInv_det_id(det.getId());
				double rate=0;
				try {
					rate=(det.getQty_in_basic_unit()/det.getQunatity())*(det.getUnit_price()/det.getConversionRate());
				} catch (Exception e) {
					rate=0;
				}
				stock.setQuantity(det.getQty_in_basic_unit());
				stock.setBalance(det.getQty_in_basic_unit());
				stock.setRate(rate);
				stock.setStatus(2);
				stock.setManufacturing_date(det.getManufacturing_date());
				stock.setDate_time(CommonUtil.getCurrentDateTime());
				stock.setGradeId(det.getGrade_id());
				stock.setItem_tag("");
				stock.setLocation_id(det.getLocation_id());
				
				if(stock.getId()!=0)
					getSession().update(stock);
				else
					getSession().save(stock);
				flush();
				
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
				
				stock=(ItemStockModel) getSession().get(ItemStockModel.class, stock.getId());
				stock.setBatch_id(batch.getId());
				getSession().update(stock);
				
				det.setBatch_id(batch.getId());
				det.setStock_id(stock.getId());
				newChildList.add(det);
			}
			mdl.setGrn_details_list(newChildList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			
			if(oldIdList.size()>0){
				Iterator it=oldIdList.iterator();
				while (it.hasNext()) {
					long id=(Long)it.next();
					PurchaseGRNDetailsModel det=(PurchaseGRNDetailsModel)getSession().get(PurchaseGRNDetailsModel.class, id);
					
					if(det.getStock_id()!=0){
						ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
						getSession().delete(stck);
					}
					flush();
					
					if(det.getBatch_id()!=0){
						BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
						getSession().delete(batch);
					}
					flush();
					getSession().delete(det);
				}
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
	public void delete(PurchaseGRNModel mdl) throws Exception {
		try {
			begin();
			Iterator itr=mdl.getGrn_details_list().iterator();
			List<Long> orderList=new ArrayList<Long>();
			while (itr.hasNext()) {
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				// Update Purchase Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Purchase Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				if(det.getStock_id()!=0){
					ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
					getSession().delete(stck);
				}
				flush();
				
				if(det.getBatch_id()!=0){
					BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
					getSession().delete(batch);
				}
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
	public void cancel(PurchaseGRNModel mdl) throws Exception {
		try {
			begin();
			Iterator itr=mdl.getGrn_details_list().iterator();
			List<Long> orderList=new ArrayList<Long>();
			while (itr.hasNext()) {
				PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
				
				// Update Purchase Order Child
				if(det.getOrder_child_id()!=0) {
					getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
								.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				}
				flush();
				
				// Update Purchase Order Parent
				if(det.getOrder_id()!=0){
					if(!orderList.contains(det.getOrder_id())) {
						orderList.add(det.getOrder_id());
						getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getOrder_id()).executeUpdate();
					}
				}
				flush();
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
				if(det.getStock_id()!=0){
					ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
					getSession().delete(stck);
				}
				flush();
				
				if(det.getBatch_id()!=0){
					BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
					getSession().delete(batch);
				}
				flush();
			}
			getSession().createQuery("update PurchaseGRNModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	public List getAllDataFromPurchaseOrder(Set<Long> purchaseOrders) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select new com.inventory.purchase.bean.PurchaseGRNBean(a.id, b) from PurchaseOrderModel a " +
								"join a.order_details_list b where a.id in (:list) and active=true order by a.id")
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
	
	
}
