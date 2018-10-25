package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 12, 2013
 */
public class ContainerReportDao extends SHibernate implements  Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6241049407965920098L;
	
	CommonMethodsDao methodsDao=new CommonMethodsDao();

	public List<Object> getSalesDetails(long suplID, Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> list = null;
		
		try {
			
			begin();
			
			String condition = "";
//			if (salesNo != 0) {
//				condition += " and number=" + salesNo;
//			}
			if (suplID != 0) {
				condition += " and supplier.id=" + suplID;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession().createQuery("from CommissionPurchaseModel where issue_date between :fromDate and :toDate "
									+ condition).setParameter("fromDate", fromDate).setParameter("toDate", toDate).list();
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}
	
	
	
}
