package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.ItemCustomerBarcodeMapModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author anil
 * @date 06-Nov-2015
 * @Project REVERP
 */
@SuppressWarnings("serial")
public class ItemCustomerBarcodeMappingDao extends SHibernate implements Serializable{
	
	public ItemCustomerBarcodeMapModel getMappingModel(long itemId,long customerId) throws Exception {
		ItemCustomerBarcodeMapModel mdl=null;
		try{
			begin();
			mdl=(ItemCustomerBarcodeMapModel)getSession().createQuery("from ItemCustomerBarcodeMapModel where customerId=:cust and itemId=:itm")
							.setParameter("cust", customerId).setParameter("itm", itemId).uniqueResult();
			commit();
		}
		catch(Exception e){
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
	
	public void save(ItemCustomerBarcodeMapModel mdl) throws Exception{
		try{
			begin();
			getSession().save(mdl);
			commit();
		}
		catch(Exception e){
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
	public void update(List list) throws Exception{
		try{
			begin();
			ItemCustomerBarcodeMapModel map=null;
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					map=(ItemCustomerBarcodeMapModel)itr.next();
					if(map.getId()!=0){
						getSession().update(map);
					}
					else{
						getSession().save(map);
					}
					flush();
				}
			}
			commit();
		}
		catch(Exception e){
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
	
	public void delete(ItemCustomerBarcodeMapModel mdl) throws Exception{
		try{
			begin();
			getSession().delete(mdl);
			commit();
		}
		catch(Exception e){
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

	public List getMappingModelList(Long itemId) throws Exception {
		List list;
		try{
			begin();
			list=getSession().createQuery("from ItemCustomerBarcodeMapModel where itemId=:itm")
				.setParameter("itm", itemId).list();
			commit();
		}
		catch(Exception e){
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
	
}
