package com.inventory.tailoring.report.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 22, 2014
 */
public class TailoringSalesReportDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6241049407965920098L;

	CommonMethodsDao methodsDao = new CommonMethodsDao();

	public List<Object> getSalesDetails(long salesId, long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (salesId != 0) {
				condition += " and id=" + salesId;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							"from TailoringSalesModel where date>=:fromDate and date<=:toDate   and (type=0 or type=1)"
									+ condition
									+ " and office.organization.id=:orgId")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
