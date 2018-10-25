package com.inventory.config.stock.dao;

import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemComboDetailsModel;
import com.inventory.config.stock.model.ItemComboModel;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 30, 2014
 */
public class ItemComboCreationDao extends SHibernate{

	CommonMethodsDao comDao=new CommonMethodsDao(); 
			
	public long save(ItemComboModel objModel,String barcode) throws Exception {
		ItemStockModel stock = new ItemStockModel();
		try {
			begin();
			
			getSession().save(objModel);
			
			
			stock.setExpiry_date(objModel.getDate());
			stock.setItem(objModel.getItem());
			stock.setPurchase_id(0);
			stock.setInv_det_id(0);
			stock.setQuantity(objModel.getQuantity());
			stock.setBalance(objModel.getQuantity());
			stock.setRate(objModel.getUnitPrice());
			stock.setStatus(2);
			stock.setManufacturing_date(objModel.getDate());
			stock.setDate_time(CommonUtil.getCurrentDateTime());
			getSession().save(stock);
			
			if(barcode==null||barcode.trim().length()<=0){
				stock.setBarcode(stock.getId()+"");
			}else{
				stock.setBarcode(barcode);
			}
			
			getSession()
					.createQuery(
							"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
					.setParameter("id", objModel.getItem().getId())
					.setParameter("qty", objModel.getQuantity())
					.executeUpdate();
			
			flush();
			
			ItemComboDetailsModel detMdl;
			Iterator ite=objModel.getItem_combo_details_list().iterator();
			while (ite.hasNext()) {
				detMdl= (ItemComboDetailsModel) ite.next();
				comDao.decreaseStock(detMdl.getItem().getId(), detMdl.getQty_in_basic_unit()*objModel.getQuantity());

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", detMdl.getItem().getId())
						.setParameter("qty", detMdl.getQty_in_basic_unit()*objModel.getQuantity())
						.executeUpdate();
				
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
		return stock.getId();
		
	}

	public void release(long itemId,double quantity) throws Exception {
		try {
			begin();
			
			getSession()
					.createQuery(
							"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
					.setParameter("id", itemId)
					.setParameter("qty", quantity)
					.executeUpdate();
			
			flush();
			
			ItemComboModel objModel=(ItemComboModel) getSession().createQuery(
							"from ItemComboModel where item.id=:item and id=(select max(id) from ItemComboModel where item.id=:item)")
					.setParameter("item", itemId).uniqueResult();
			
			comDao.decreaseStock(objModel.getItem().getId(),quantity);
			
			ItemComboDetailsModel detMdl;
			Iterator ite=objModel.getItem_combo_details_list().iterator();
			while (ite.hasNext()) {
				detMdl= (ItemComboDetailsModel) ite.next();
				comDao.increaseStock(detMdl.getItem().getId(), detMdl.getQty_in_basic_unit()*quantity);

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", detMdl.getItem().getId())
						.setParameter("qty", detMdl.getQty_in_basic_unit()*quantity)
						.executeUpdate();
				
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

	public ItemComboModel getItemComboModel(long itemId) throws Exception {
		ItemComboModel mdl;
		try {
			begin();
			mdl = (ItemComboModel) getSession()
					.createQuery(
							"from ItemComboModel where item.id=:item and id=(select max(id) from ItemComboModel where item.id=:item)")
					.setParameter("item", itemId).uniqueResult();
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

	public List getAllComboItems(long officeID) throws Exception {
		List list=null;
		try {
			begin();
			list =  getSession()
					.createQuery(
							"select distinct new com.inventory.config.stock.model.ItemModel(a.item.id,concat(a.item.name,' ( ', a.item.item_code ,' )  Bal: ' , a.item.current_balalnce)) from ItemComboModel a where a.item.office.id=:ofc and  a.item.current_balalnce!=0 order by a.item.name")
					.setParameter("ofc", officeID).list();
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

	public boolean isItemStockExists(long itemId) throws Exception {
		List list=null;
		try {
			begin();
			list =  getSession()
					.createQuery(
							"from ItemStockModel where item.id=:itemId and balance!=0")
					.setParameter("itemId", itemId).list();
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
		if(list!=null&&list.size()>0)
			return true;
		else
			return false;
	}

	public List getCatalogNo(long officeID) throws Exception {
		List list=null;
		try {
			begin();
			list =  getSession()
					.createQuery(
							"select distinct new com.inventory.purchase.model.PurchaseModel(id,catalogNo)" +
							" from PurchaseModel where office.id=:ofc and catalogNo!='' and catalogNo != null order by id desc")
					.setParameter("ofc", officeID).list();
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

	public List getPurchaseFromCatalogNo(String catalogNo,long officeId) throws Exception {
		List list=null;
		try {
			begin();
			list =  getSession()
					.createQuery(
							" from PurchaseModel where office.id=:ofc and catalogNo=:cat")
					.setParameter("ofc", officeId).setParameter("cat", catalogNo).list();
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

	public ItemStockModel getStock(long stockID) throws Exception {
		ItemStockModel stk;
		try {
			begin();
			stk = (ItemStockModel) getSession()
					.get(ItemStockModel.class, stockID);
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
}
