package com.inventory.sales.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;

import com.inventory.sales.model.HoldSalesInventoryDetailsModel;
import com.inventory.sales.model.HoldSalesModel;
import com.webspark.dao.SHibernate;

public class HoldSalesDao extends SHibernate implements Serializable{
	
private List resultList = new ArrayList();
	
	public long save(HoldSalesModel mdl) throws Exception {
		try {
			begin();
			getSession().save(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
		return mdl.getId();
	}	
	
	public void delete(Long id) throws Exception {
		try {
			begin();
			getSession().delete(new HoldSalesModel(id));
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}finally{
		flush();
		close();
		}
	}
	
	public void update(HoldSalesModel mdl) throws Exception {
		try {
			begin();
			List oldList = new ArrayList();
			List oldIdList = new ArrayList();
			oldList = getSession()
						.createQuery(
							"select b from HoldSalesModel a join a.inventory_details_list b where a.id=:id")
					.setParameter("id", mdl.getId()).list();
			
			List<Long> quotationList = new ArrayList<Long>();
			Iterator itr = oldList.iterator();
			while (itr.hasNext()) {
				HoldSalesInventoryDetailsModel det = (HoldSalesInventoryDetailsModel) itr.next();
				oldIdList.add(det.getId());
			}

			List<HoldSalesInventoryDetailsModel> itemsList = new ArrayList<HoldSalesInventoryDetailsModel>();
			List<Long> newQuotationList = new ArrayList<Long>();
			itr = mdl.getInventory_details_list().iterator();
			while (itr.hasNext()) {
				HoldSalesInventoryDetailsModel det = (HoldSalesInventoryDetailsModel) itr.next();
				if (det.getId() != 0){
					oldIdList.remove(det.getId());
				}
				itemsList.add(det);
			}
			mdl.setInventory_details_list(itemsList);
			getSession().clear();
			getSession().update(mdl);
			flush();
			if (oldIdList != null && oldIdList.size() > 0) {
				getSession()
						.createQuery(
								"delete from HoldSalesInventoryDetailsModel where id in (:list)")
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
	
	public HoldSalesModel getHoldSalesModel(long id) throws Exception {
		HoldSalesModel model = null;
		try {
			begin();
			model = (HoldSalesModel) getSession().get(HoldSalesModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	return model;
	}
	
	@SuppressWarnings("rawtypes")
	public List getAllSalesHolded(Date date, long officeID) throws Exception {
		List resultList = new ArrayList();
		try {
			begin();
			resultList = getSession()
					.createQuery(
							" from HoldSalesModel where office.id=:office and date=:dt order by id asc")
					.setParameter("office", officeID)
					.setParameter("dt", date).list();
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
	public boolean isExist(Date date, long officeID,long id) throws Exception {
		List resultList = new ArrayList();
		boolean isExist=false;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							" from HoldSalesModel where office.id=:office and date=:dt and id=:id ")
					.setParameter("office", officeID)
					.setParameter("id", id)
					.setParameter("dt", date).list();
			
			if(resultList.size()>0)
				isExist=true;
			
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
		return isExist;
	}

	
	

}
