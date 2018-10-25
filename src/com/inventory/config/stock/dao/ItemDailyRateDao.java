package com.inventory.config.stock.dao;

import java.sql.Date;
import java.util.List;

import com.inventory.config.stock.model.ItemDailyRateModel;
import com.webspark.dao.SHibernate;

public class ItemDailyRateDao extends SHibernate {

	private static final long serialVersionUID = 6670662479044165686L;

	public long save(ItemDailyRateModel mdl) throws Exception {
		try {
			begin();
			
			Object obj=getSession().createQuery("from ItemDailyRateModel where customer_id=:cus and sales_type=:sal and date=:dat")
					.setParameter("cus", mdl.getCustomer_id()).setParameter("sal", mdl.getSales_type()).setParameter("dat",mdl.getDate()).uniqueResult();
			
			if(obj!=null){
				ItemDailyRateModel oldMdl=(ItemDailyRateModel) obj;
				getSession().delete(oldMdl);
			}
			
			getSession().save(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl.getId();
	}

	public List getAllItemUnitDetails(long item_id, long sales_type)
			throws Exception {

		List resultList = null;
		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from ItemUnitMangementModel"
									+ " where item.id=:itm and sales_type=:typ")
					.setLong("itm", item_id).setLong("typ", sales_type).list();
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
