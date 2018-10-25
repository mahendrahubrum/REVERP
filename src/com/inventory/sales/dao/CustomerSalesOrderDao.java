package com.inventory.sales.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.inventory.sales.model.SalesOrderModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 24, 2013
 */
public class CustomerSalesOrderDao extends SHibernate {

	private static final long serialVersionUID = 8042921882902110138L;

	public List getBillNumbers(long loginID, long officeId) throws Exception {
		List resultList=null;
		try {
			List statusList = new ArrayList();
			statusList.add(SConstants.statuses.SALES_ORDER_CUSTOMER_CREATED);
			statusList.add(SConstants.statuses.SALES_ORDER_CUSTOMER_SUBMITTED);
			statusList.add(SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED);
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesOrderModel(id,ref_no)"
									+ " from SalesOrderModel where office.id=:ofc and login.id=:login and status in (:sts) order by id desc")
					.setParameter("ofc", officeId)
					.setParameter("login", loginID)
					.setParameterList("sts", statusList).list();
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

	public long getLedgerFromLogin(long loginID) throws Exception {
		long id = 0;
		try {
			begin();
			Object ob = getSession()
					.createQuery(
							"select ledger.id from CustomerModel where login_id=:login")
					.setParameter("login", loginID).uniqueResult();
			commit();
			if (ob != null)
				id = (Long) ob;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();

		}
		return id;
	}

	public long save(SalesOrderModel obj) throws Exception {

		try {

			begin();
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
			return obj.getId();
		}
	}

	public void update(SalesOrderModel obj) throws Exception {
		try {

			begin();

			Object objLst = getSession().createQuery(
					"select b.id from SalesOrderModel a join a.inventory_details_list b "
							+ "where a.id=" + obj.getId()).list();

			getSession().update(obj);
			flush();

			getSession()
					.createQuery(
							"delete from SalesInventoryDetailsModel where id in (:lst)")
					.setParameterList("lst", (Collection) objLst)
					.executeUpdate();

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
			SalesOrderModel obj = (SalesOrderModel) getSession().get(
					SalesOrderModel.class, id);
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

	public SalesOrderModel getSalesOrder(long order_id) throws Exception {
		SalesOrderModel po = null;
		try {
			begin();
			po = (SalesOrderModel) getSession().get(SalesOrderModel.class,
					order_id);
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
		return po;
	}

	public List getAllActiveUnits(long itemId, long customer, Date date,
			long salesType) throws Exception {
		List resultList=null;
		try {
			List unitList = null;
			begin();
			unitList = getSession()
					.createQuery(
							"select b.unit from "
									+ " ItemDailyRateModel a join a.daily_rate_list b where a.customer_id=:cus and a.sales_type=:sal and a.date=:dat and b.item=:itm")
					.setParameter("cus", customer).setParameter("itm", itemId)
					.setParameter("sal", salesType).setParameter("dat", date)
					.list();

			if (unitList == null || unitList.size() == 0) {
				unitList = getSession()
						.createQuery(
								"select b.unit from "
										+ "ItemDailyRateModel a join a.daily_rate_list b where a.customer_id=:cus and a.sales_type=:sal and a.date=:dat and b.item=:itm")
						.setParameter("cus", (long) 0)
						.setParameter("sal", salesType)
						.setParameter("dat", date).setParameter("itm", itemId)
						.list();
			}
			if(unitList!=null&&unitList.size()>0){
			resultList = getSession()
					.createQuery("from UnitModel where id in (:unitList)")
					.setParameterList("unitList", unitList).list();
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

	public List getAllActiveItemsFromOfc(long customer, Date date,
			long salesType) throws Exception {
		List resultList=null;
		try {
			List itemList = null;
			begin();
			itemList = getSession()
					.createQuery(
							"select b.item  from ItemDailyRateModel a join a.daily_rate_list b"
									+ " where a.customer_id=:cus and a.sales_type=:sal and a.date=:dat")
					.setParameter("cus", customer)
					.setParameter("sal", salesType).setParameter("dat", date)
					.list();

			if (itemList == null || itemList.size() == 0) {
				itemList = getSession()
						.createQuery(
								"select b.item from ItemDailyRateModel a join a.daily_rate_list b"
										+ " where a.customer_id=:cus and a.sales_type=:sal and a.date=:dat")
						.setParameter("cus", (long) 0)
						.setParameter("sal", salesType)
						.setParameter("dat", date).list();
			}
			if(itemList!=null&&itemList.size()>0){
			resultList = getSession()
					.createQuery("from ItemModel where id in (:idList) order by name")
					.setParameterList("idList", itemList).list();
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

	public long getAllActiveSalesTypeNames(long customer, Date date)
			throws Exception {
		
		long salesType=0;
		try {
			List itemList = null;
			begin();
			itemList = getSession()
					.createQuery(
							"select sales_type"
									+ " from ItemDailyRateModel where customer_id=:cus  and date=:dat")
					.setParameter("cus", customer).setParameter("dat", date)
					.list();
			if (itemList == null || itemList.size() == 0) {
				itemList = getSession()
						.createQuery(
								"select sales_type"
										+ " from ItemDailyRateModel where customer_id=:cus  and date=:dat")
						.setParameter("cus", (long) 0)
						.setParameter("dat", date).list();
			}
			if(itemList!=null&&itemList.size()>0){
				Object obj=itemList.get(0);
				salesType=(Long) obj;
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
		return salesType;
	}

	public double getItemPrice(long customerLedgerId, Date date,
			long salesType, long item, long unit) throws Exception {
		double rate = 0;
		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select b.rate from ItemDailyRateModel a join a.daily_rate_list b"
									+ " where a.customer_id=:cus and a.sales_type=:sal and a.date=:dat and b.unit=:unit and b.item=:itm")
					.setParameter("cus", customerLedgerId)
					.setParameter("sal", salesType)
					.setParameter("dat", date).setParameter("itm", item)
					.setParameter("unit", unit).uniqueResult();

			if (obj == null) {
				Object obj1 = getSession()
						.createQuery(
								"select b.rate from ItemDailyRateModel a join a.daily_rate_list b"
										+ " where a.customer_id=:cus and a.sales_type=:sal and a.date=:dat and b.unit=:unit and b.item=:itm")
						.setParameter("cus", (long) 0)
						.setParameter("sal", salesType)
						.setParameter("dat", date)
						.setParameter("itm", item).setParameter("unit", unit)
						.uniqueResult();
				if (obj1 != null)
					rate = (Double) obj1;

			} else
				rate = (Double) obj;

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
		return rate;
	}

}
