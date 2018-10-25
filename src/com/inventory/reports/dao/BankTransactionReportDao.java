package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 26, 2013
 */
public class BankTransactionReportDao extends SHibernate  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3714625101386754557L;

	public List getAllTransactions(long officeId, long ledgerId, int type,
			Date fromDate, Date toDate, long group) throws Exception {

		List resList = null;
		try {
			begin();

			String cond = "";
			if (officeId > 0)
				cond += " and a.office.id=" + officeId;

			if (ledgerId > 0)
				cond += " and (b.fromAcct.id=" + ledgerId + " or b.toAcct="
						+ ledgerId + ")";
			
			if(type>0)
				cond+=" and a.transaction_type="+type;

			resList = getSession()
					.createQuery(
							"select distinct a from TransactionModel a join a.transaction_details_list b "
									+ " where a.date between :fromDate and :toDate and (b.fromAcct.group.id=:group or b.toAcct.group.id=:group)"
									+ cond).setParameter("fromDate", fromDate)
					.setParameter("group", group)
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

		return resList;
	}

	public long getBankPaymentId(long trId)throws Exception{
		long id=0;
		try{
			begin();
			Object obj=getSession().createQuery("select id from BankAccountPaymentModel where transaction.id="+trId).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
	public long getBankDepositId(long trId)throws Exception{
		long id=0;
		try{
			begin();
			Object obj=getSession().createQuery("select id from BankAccountDepositModel where transaction.id="+trId).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
		return id;
	}
	
}
