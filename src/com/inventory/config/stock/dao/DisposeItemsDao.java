package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.DisposalItemsDetailsModel;
import com.inventory.config.stock.model.DisposeItemsModel;
import com.inventory.config.stock.model.ItemModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class DisposeItemsDao extends SHibernate implements Serializable{

	ItemDao itemDao=new ItemDao();
	CommonMethodsDao commonMtdDao=new CommonMethodsDao();
	
	public long save(DisposeItemsModel obj) throws Exception {
		try {
			begin();
			
			List<DisposalItemsDetailsModel> itemsList = new ArrayList<DisposalItemsDetailsModel>();
			Iterator itr = obj.getItem_details_list().iterator();
			while (itr.hasNext()) {
				DisposalItemsDetailsModel det = (DisposalItemsDetailsModel) itr
						.next();
				itemsList.add(det);
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
                			.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				
				commonMtdDao.decreaseStockByStockID(det.getStockId(), det.getQty_in_basic_unit(),false);
			}
			obj.setItem_details_list(itemsList);
			getSession().save(obj);
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
		return obj.getId();
	}
	
	
	
	public void update(DisposeItemsModel objModel)
			throws Exception {

		try {

			begin();
			
			List oldList = new ArrayList();
			List oldIdList = new ArrayList();
			oldList = getSession()
					.createQuery(
							"select b from DisposeItemsModel a join a.item_details_list b where a.id=:id")
					.setParameter("id", objModel.getId()).list();
			Iterator itr = oldList.iterator();
			while (itr.hasNext()) {
				DisposalItemsDetailsModel det = (DisposalItemsDetailsModel) itr.next();
				oldIdList.add(det.getId());
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
    						.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				commonMtdDao.increaseStockByStockID(det.getStockId(), det.getQty_in_basic_unit(),false);
			}

			List<DisposalItemsDetailsModel> itemsList = new ArrayList<DisposalItemsDetailsModel>();
			itr = objModel.getItem_details_list().iterator();
			while (itr.hasNext()) {
				DisposalItemsDetailsModel det = (DisposalItemsDetailsModel) itr.next();
				if (det.getId() != 0){
					oldIdList.remove(det.getId());
				}
				itemsList.add(det);
				
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
                			.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				
				commonMtdDao.decreaseStockByStockID(det.getStockId(), det.getQty_in_basic_unit(),false);
			}
			objModel.setItem_details_list(itemsList);
			getSession().clear();
			getSession().update(objModel);
			flush();
			if (oldIdList != null && oldIdList.size() > 0) {
				getSession()
						.createQuery(
								"delete from DisposalItemsDetailsModel where id in (:list)")
						.setParameterList("list", (Collection) oldIdList)
						.executeUpdate();
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
			
			DisposeItemsModel obj=(DisposeItemsModel) getSession().get(DisposeItemsModel.class, id);
			
			List<DisposalItemsDetailsModel> itemsList = new ArrayList<DisposalItemsDetailsModel>();
			Iterator itr = obj.getItem_details_list().iterator();
			while (itr.hasNext()) {
				DisposalItemsDetailsModel det = (DisposalItemsDetailsModel) itr.next();
				itemsList.add(det);
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
                			.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				commonMtdDao.increaseStockByStockID(det.getStockId(), det.getQty_in_basic_unit(),false);
			}
			
			getSession().delete(obj);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} 
			flush();
			close();
	}
	
	public DisposalItemsDetailsModel getDisposalItemsDetailsModel(long id) throws Exception {
		DisposalItemsDetailsModel mdl = null;
		try {
			begin();
			mdl = (DisposalItemsDetailsModel) getSession().get(DisposalItemsDetailsModel.class, id);
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
	
	public DisposeItemsModel getDisposeItemsModel(long id) throws Exception {
		DisposeItemsModel cust = null;
		try {
			begin();
			cust = (DisposeItemsModel) getSession().get(DisposeItemsModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}return cust;
	}
	
	public long getDisposeItemsModelId(Date dt, long ofc_id) throws Exception {
		long mdlId=0;
		try {
			begin();
			Object obj = getSession().createQuery("select id from DisposeItemsModel where date=:date and office.id=:ofc")
					.setParameter("date",dt)
					.setParameter("ofc",ofc_id).uniqueResult();
			if(obj!=null)
				mdlId=(Long) obj;
			
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
		return mdlId;
	}
	
	
	public List getAllDisposalItemsForReport(long officeId, Date from_date,	Date to_date) throws Exception{
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.DisposeItemsModel(id) from DisposeItemsModel where office.id=:office and date between :from_date and :to_date order by date")
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
		return resultList;
	}
	
	
	public List getAllItemsRealStckWithAffectType(long ofc_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			Iterator it = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' ), current_balalnce)"
									+ " from ItemModel where (office.id=:ofc and affect_type=1) or (office.id=:ofc and affect_type=2) order by name")
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
		}
		return itm;
	}
	
	
}
