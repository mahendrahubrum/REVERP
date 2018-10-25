package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.SupplierModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 13, 2013
 */
public class PurchaseReturnReportDao extends SHibernate implements  Serializable{

	public List<Object> getReturnDetails(long supplierid, long itemId,long officeId,
			Date fromDate, Date toDate) throws Exception {
		List<Object> list = null;
		try {
			begin();

			String condition = "";
			String query = "";

			if (supplierid != 0) {
				condition += " and a.supplier.id=" + supplierid;
			}
			
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}

			if (itemId == 0) {
				query = " from PurchaseReturnModel a where a.date>=:fromDate and a.date<=:toDate and a.active=true "
						+ condition;
			} else {
				query = " select distinct a from PurchaseReturnModel a join a.inventory_details_list b where a.date>=:fromDate and a.date<=:toDate and a.active=true and b.item.id="
						+ itemId + condition;
			}

			list = getSession().createQuery(query)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			list=new ArrayList<Object>();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> showReturnDetails(long supplierid, long itemId,long officeId, Date fromDate, Date toDate) throws Exception {
		List list = new ArrayList();
		try {
			begin();

			String condition = "";
			String query = "";
			String cdn = "";
			if (supplierid != 0) {
//				condition += " and a.supplier.id=" + supplierid;
				cdn+=" and ledger.id="+supplierid;
			}
			
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}

			if (itemId != 0) {
				condition+=" and b.item.id="+itemId;
				query+=" join a.inventory_details_list b ";
			} 
			
			List customerList=getSession().createQuery("from SupplierModel where ledger.office.id=:ofc and ledger.status=:val "+cdn+" order by name")
					.setParameter("ofc", officeId)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
			if(customerList.size()>0){
				Iterator cusitr=customerList.iterator();
				while (cusitr.hasNext()) {
					SupplierModel cust=(SupplierModel)cusitr.next();
					
					List lst=getSession().createQuery("from PurchaseReturnModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and a.active=true and a.supplier.id=:ledger"+ condition)
							.setParameter("fromDate", fromDate)
							.setParameter("ledger", cust.getLedger().getId())
							.setParameter("toDate", toDate).list();
					
					if(lst.size()>0)
						list.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesReportBean(a.supplier.id, a.supplier.name ," +
								"coalesce(sum(a.amount/conversionRate),0))" +
								"from PurchaseReturnModel a "+query+" where a.date between :fromDate and :toDate and a.active=true and a.supplier.id=:ledger"+condition)
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
				}
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			list=new ArrayList<Object>();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}
}
