package com.inventory.reports.dao;

import java.sql.Date;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 23, 2013
 */

public class ChequeIssueReportDao extends SHibernate {

	private static final long serialVersionUID = 472061415509442579L;

	public List getReportList(Date fromDate, Date toDate, long supplierId,
			long bankId, long officeId,int type) throws Exception {
		List resList = null;
		try {
			begin();

			String con = "";
			String selectStr = "";
			
			if (type == SConstants.SUPPLIER_PAYMENTS) {

				if (supplierId != 0)
					con += " and pm.to_account_id=" + supplierId;
				if (bankId != 0)
					con += " and pm.from_account_id=" + bankId;
				
				con+= " and from_account_id in (select id from LedgerModel c where c.group.id=:bank) ";
				
				selectStr= "(select name from LedgerModel a where a.id=pm.to_account_id),"
						+ " (select name from LedgerModel b where b.id=pm.from_account_id)";

			}else{
				if (bankId != 0)
					con += " and pm.to_account_id="+bankId;
				if (supplierId != 0)
					con += " and pm.from_account_id="+supplierId;
				
				con+= " and to_account_id in (select id from LedgerModel c where c.group.id=:bank) ";
				
				selectStr= "(select name from LedgerModel a where a.id=pm.from_account_id),"
						+ " (select name from LedgerModel b where b.id=pm.to_account_id)";
			}
			
			if (officeId != 0)
				con += " and pm.office.id="+officeId;

			resList = getSession()
					.createQuery(
							"select new com.inventory.reports.bean.ChequeIssueBean(cast (cheque_date as string) ,cast (date as string),"
									+selectStr+", payment_amount,type) "
									+ " from PaymentModel pm where type=:typ and cheque_date between :frmdt and :todt"
									+ con+" order by cheque_date")
//					.setParameter("bank", SConstants.BANK_ACCOUNT_GROUP_ID)
					.setParameter("typ", type)
					.setParameter("frmdt", fromDate)
					.setParameter("todt", toDate).list();
			commit();
		} catch (Exception e) {

			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}

		return resList;
	}

}
