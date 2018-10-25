package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.ItemPhysicalStockModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class ItemPhysicalStockDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public List getAllItems(long ofc_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery(
							"from ItemModel where office.id=:ofc order by name").setParameter("ofc", ofc_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public void save(ItemPhysicalStockModel mdl) throws Exception{
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
			ItemPhysicalStockModel map=null;
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					map=(ItemPhysicalStockModel)itr.next();
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
	
	public void delete(long office,Date date) throws Exception{
		try{
			begin();
			getSession().createQuery("delete from ItemPhysicalStockModel where office=:office and date=:date")
						.setParameter("office", office).setParameter("date", date).executeUpdate();
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
	
	public ItemPhysicalStockModel getItemPhysicalStockModel(long office,Date date,long item) throws Exception {
		ItemPhysicalStockModel mdl=null;
		try{
			begin();
			mdl=(ItemPhysicalStockModel)getSession().createQuery("from ItemPhysicalStockModel where office=:office and date=:date and item.id=:item")
								.setParameter("office", office).setParameter("date", date).setParameter("item", item).uniqueResult();
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
	
}
