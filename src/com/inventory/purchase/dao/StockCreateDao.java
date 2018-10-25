package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.config.stock.model.BatchModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.StockCreateDetailsModel;
import com.inventory.purchase.model.StockCreateModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

/**
 * @author sangeeth
 * @date 21-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class StockCreateDao extends SHibernate implements Serializable{

	
	@SuppressWarnings("rawtypes")
	public List getStockCreateModelList(long ofc_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.purchase.model.StockCreateModel(id,purchase_number) "
									+ " from StockCreateModel where office.id=:ofc and active=true order by id desc").setParameter("ofc", ofc_id).list();
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

	
	public StockCreateModel getStockCreateModel(long id) throws Exception {
		StockCreateModel pur = null;
		try {
			begin();
			pur = (StockCreateModel) getSession().get(StockCreateModel.class, id);
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
		return pur;
	}
	
	
	public StockCreateDetailsModel getStockCreateDetailsModel(long id) throws Exception {
		StockCreateDetailsModel pur = null;
		try {
			begin();
			pur = (StockCreateDetailsModel) getSession().get(StockCreateDetailsModel.class, id);
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
		return pur;
	}

	
	@SuppressWarnings("rawtypes")
	public long save(StockCreateModel mdl) throws Exception {
		
		try {
			begin();
			List<StockCreateDetailsModel> childList = new ArrayList<StockCreateDetailsModel>();
			Iterator itr = mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				StockCreateDetailsModel det = (StockCreateDetailsModel)itr.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
					.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				
				flush();
				
				double rate = 0;
				try {
					rate = (det.getQty_in_basic_unit() / det.getQunatity())
							* ((det.getUnit_price() / det.getConversionRate())
									+ det.getTaxAmount() + det.getCessAmount() - det
										.getDiscount());
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
				stock.setPurchase_type(SConstants.stockPurchaseType.STOCK_CREATE);
				stock.setPurchase_id(mdl.getId());
				stock.setInv_det_id(det.getId());
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
			mdl.setInventory_details_list(childList);
			getSession().save(mdl);
			flush();
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
		return mdl.getId();
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(StockCreateModel mdl) throws Exception {
		
		try {
			begin();
			List oldList=new ArrayList();
			List oldIdList=new ArrayList();
			
			List<StockCreateDetailsModel> childList = new ArrayList<StockCreateDetailsModel>();
			
			oldList=getSession().createQuery("select b from StockCreateModel a join a.inventory_details_list b where a.id=:id")
								.setParameter("id", mdl.getId()).list();

			Iterator itr=oldList.iterator();
			while (itr.hasNext()) {
				StockCreateDetailsModel det = (StockCreateDetailsModel)itr.next();

				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				oldIdList.add(det.getId());
			}
			
			itr = mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				
				StockCreateDetailsModel det = (StockCreateDetailsModel)itr.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				
				flush();
				
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
				stock.setPurchase_type(SConstants.stockPurchaseType.STOCK_CREATE);
				stock.setPurchase_id(mdl.getId());
				stock.setInv_det_id(det.getId());
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
			mdl.setInventory_details_list(childList);
			getSession().clear();
			getSession().update(mdl);
			flush();
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
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void delete(StockCreateModel mdl) throws Exception {
		try {
			begin();
			Iterator itr = mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				
				StockCreateDetailsModel det = (StockCreateDetailsModel)itr.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				
				flush();
				
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
			}
			getSession().delete(mdl);
			flush();
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
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void cancel(StockCreateModel mdl) throws Exception {
		try {
			begin();
			Iterator itr = mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				
				StockCreateDetailsModel det = (StockCreateDetailsModel)itr.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
				.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				
				flush();
				
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
			}
			getSession().createQuery("update StockCreateModel set active=false where id="+mdl.getId());
			flush();
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
	
	
	
	

	public void delete(long id) throws Exception {
		try {
			begin();
			StockCreateModel obj = (StockCreateModel) getSession().get(
					StockCreateModel.class, id);

			// Transaction Related

			StockCreateDetailsModel invObj;
			List list;
			Iterator<StockCreateDetailsModel> it = obj
					.getInventory_details_list().iterator();
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

			getSession().createQuery(
							"delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where purchase_id=:purId and status=5)")
					.setLong("purId", obj.getId()).executeUpdate();

			getSession()
					.createQuery(
							"delete from ItemStockModel where purchase_id=:pid and status=5")
					.setLong("pid", obj.getId()).executeUpdate();

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
	
	
	public void cancel(long id) throws Exception {
		try {
			begin();
			StockCreateModel obj = (StockCreateModel) getSession().get(
					StockCreateModel.class, id);


			StockCreateDetailsModel invObj;
			List list;
			Iterator<StockCreateDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQty_in_basic_unit())
						.executeUpdate();
				
				flush();
			}
			
			getSession().createQuery("update StockCreateModel set active=false where id =:id").setParameter("id", obj.getId()).executeUpdate();
			
			flush();
			
			getSession()
					.createQuery(
							"delete from StockRackMappingModel where stock.id in (select "
									+ " id from ItemStockModel where purchase_id=:purId and status=5)")
					.setLong("purId", obj.getId()).executeUpdate();
			
			getSession()
					.createQuery("delete from ItemStockModel where purchase_id=:pid and status=5")
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

	

	@SuppressWarnings("rawtypes")
	public List getAllPurchaseOrdersForSupplier(long supplier_id, long office_id) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.purchase.model.PurchaseOrderModel(a.id,concat(a.purchase_order_number, '   : ' , a.date) )"
									+ " from PurchaseOrderModel a join a.inventory_details_list b where a.office.id=:ofc and b.balance>0 and a.supplier.id=:sup and a.active=true group by a.id")
					.setParameter("ofc", office_id)
					.setParameter("sup", supplier_id).list();
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
								"select sum(b.qunatity*b.unit_price)/sum(b.qunatity)   from StockCreateModel a join a.inventory_details_list b"
										+ " where b.item.id=:itemid and a.active=true")
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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return itm;
		}
	}
	
	
	public String getBarcode(long purchaseId, long itemId, long inv_det_id) throws Exception {
		String res ="";
		try {
			begin();
			
			List lst = getSession()
					.createQuery(
							"select barcode from ItemStockModel where purchase_id=:purId and item.id=:item and inv_det_id=:invd and status=5")
							.setLong("invd", inv_det_id)
					.setParameter("purId", purchaseId)
					.setParameter("item", itemId).list();
			
			if(lst!=null && lst.size()>0)
				res=(String) lst.get(0);
			
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
		return res;
	}
	


	public ItemStockModel getStockFromInvDetailsId(long invDetailsId) throws Exception {
		ItemStockModel mdl;
		try {
			begin();
			mdl = (ItemStockModel) getSession()
					.createQuery("from ItemStockModel where inv_det_id=:invDetailsId and status=5")
					.setParameter("invDetailsId", invDetailsId).uniqueResult();
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
		return mdl;
	}



	public String getBarcodeFromStock(long itemId) throws Exception {
		List list;
		String code="";
		try {
			begin();
			list = getSession()
					.createQuery("select barcode from ItemStockModel where item.id=:item and barcode!=null order by id desc limit 0")
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



	public boolean isBarcodeExists(Long itemId,String barcode) throws Exception {
		List<Long> list;
		boolean flag=false;
		try {
			begin();
			
			list = getSession()
					.createQuery("select item.id from ItemStockModel where barcode=:barcode order by id desc ")
					.setParameter("barcode", barcode).list();
			commit();
			
			if(list!=null&&list.size()>0)
				if(!list.contains(itemId))
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

}
