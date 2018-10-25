package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ManualTradingMasterModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 4, 2013
 */

public class ManualTradingEntryDao extends SHibernate implements Serializable{

	List resultList = new ArrayList();

	CommonMethodsDao comDao = new CommonMethodsDao();

	public void save(List<ManualTradingMasterModel> list, long ofc_id, Date date) throws Exception {

		try {

			begin();
			
			
			
			List lst=getSession().createQuery("from ManualTradingMasterModel where date=:dt and office_id=:ofc")
					.setParameter("dt", date).setLong("ofc", ofc_id).list();
			
			Iterator<ManualTradingMasterModel> it1 =lst.iterator();
			while (it1.hasNext()) {
				getSession().delete(it1.next());
				flush();
			}
			
			
			Iterator<ManualTradingMasterModel> it =list.iterator();
			while (it.hasNext()) {
				getSession().save(it.next());
				flush();
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

	public List getDetails(long ofc_id, Date date) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("from ManualTradingMasterModel where date=:dt and office_id=:ofc")
								.setParameter("dt", date).setLong("ofc", ofc_id).list();
			
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
	
	
	public String getUnitNameFromID(long id) throws Exception {
		String symb="";
		try {
			begin();
			Object obj = getSession().createQuery("select symbol from UnitModel where id=:id")
							.setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				symb=(String) obj;
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return symb;
		}
	}
	
	
	public String getCustomerNameFromID(long id) throws Exception {
		String cust = "";
		try {
			begin();
			Object obj = getSession().createQuery("select name from CustomerModel where id=:id")
					.setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				cust=(String) obj;
			
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return cust;
		}
	}
	
	
	public String getSupplierNameFromID(long id) throws Exception {
		String cust = "";
		try {
			begin();
			Object obj = getSession().createQuery("select name from SupplierModel where id=:id")
					.setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				cust=(String) obj;
			
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return cust;
		}
	}
	
	
	
	
	
	
	
	

}
