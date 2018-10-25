package com.inventory.config.stock.dao;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.bean.StockBean;
import com.inventory.config.stock.model.ItemDailyRateModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.StockResetDetailsModel;
import com.webspark.dao.SHibernate;

public class ItemStockResetDao extends SHibernate {
	
	CommonMethodsDao comDao=new CommonMethodsDao();

	private static final long serialVersionUID = 6670662479044165686L;

	public void save(List list,Date date) throws Exception {
		try {
			begin();
			
			double cur_qty=0;
			Object objID;
			StockBean obj;
			Iterator<StockBean> it=list.iterator();
			while (it.hasNext()) {
				obj=it.next();
				
				
				cur_qty=(Double) getSession().createQuery("select current_balalnce from ItemModel where id=:id")
						.setLong("id", obj.getItem_id()).uniqueResult();
				
				getSession().createQuery("update ItemModel set current_balalnce=:qty where id=:id")
					.setLong("id", obj.getItem_id()).setDouble("qty", obj.getQuantity()).executeUpdate();
				
				getSession().save(new StockResetDetailsModel(new ItemModel(obj.getItem_id()),
						obj.getQuantity(), date,cur_qty));
				
				getSession().createQuery("update ItemStockModel set balance=0 where item.id=:id")
						.setLong("id", obj.getItem_id()).executeUpdate();
				
				flush();
				
				objID=getSession().createQuery("select max(id) from ItemStockModel where item.id=:itm  and status!=3")
						.setLong("itm", obj.getItem_id()).uniqueResult();
				
				if(objID!=null)
					getSession().createQuery("update ItemStockModel set balance=:qty,quantity=:qty  where id=:stk and status!=3")
						.setParameter("stk", objID).setDouble("qty", obj.getQuantity()).executeUpdate();
				
			}
			
			flush();
			
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public List getAllItems(long ofc_id) throws Exception {

		List resultList = null;
		try {

			begin();
			resultList = getSession().createQuery("from ItemModel where office.id=:ofc and status=1 order by name")
					.setLong("ofc", ofc_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return resultList;
	}

	public ItemDailyRateModel getItemDailyRate(long customerId, Date date,
			long sales_type) throws Exception {
		ItemDailyRateModel rateMdl = null;

		try {
			begin();
			rateMdl=(ItemDailyRateModel)getSession()
					.createQuery(
							"from ItemDailyRateModel where customer_id=:cus and sales_type=:sal and date=:dat")
					.setParameter("cus", customerId).setParameter("sal", sales_type).setParameter("dat", date).uniqueResult();
			
			
			if(rateMdl==null&&customerId!=0){
				rateMdl=(ItemDailyRateModel)getSession()
						.createQuery(
								"from ItemDailyRateModel where customer_id=:cus and sales_type=:sal and date=:dat")
						.setParameter("cus", (long)0).setParameter("sal", sales_type).setParameter("dat", date).uniqueResult();
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return rateMdl;
	}

}
