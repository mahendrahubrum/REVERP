package com.inventory.subscription.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class RentalCustomerLedgerReportDao extends SHibernate implements Serializable{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getCustomerLedgerReport(Date start, Date end, long office, long ledger)throws Exception{
		List list=new ArrayList();
		try{
			begin();
			list.addAll(getSession().createQuery("select new com.inventory.subscription.bean.RentalCustomerLedgerBean(a.id,a.sales_number,'Rental'," +
												"cast(a.date as string),a.amount,a.payment_amount) from RentalTransactionModel a  where " +
												"a.office.id=:office and a.date between :start and :end and a.customer.id=:ledger order by a.id")
						.setParameter("office", office)
						.setParameter("start", start)
						.setParameter("end", end)
						.setParameter("ledger", ledger).list());
			list.addAll(getSession().createQuery("select new com.inventory.subscription.bean.RentalCustomerLedgerBean(a.id,a.payment_id,'Receipt'," +
												"cast(a.date as string),0.0,a.payment_amount) from RentalPaymentModel a where " +
												"a.office.id=:office and a.date between :start and :end and a.from_account_id=:ledger order by a.id")
						.setParameter("office", office)
						.setParameter("start", start)
						.setParameter("end", end)
						.setParameter("ledger", ledger).list());
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
		return list;
	}
	
	public double getOpeningBalance(Date start, long office, long ledger)throws Exception{
		double balance=0;
		try{
			begin();
			double sale=0,pay=0;
			Object ob1=getSession().createQuery("select coalesce(sum((b.qunatity*b.unit_price)+b.tax_amount+b.cess_amount-b.discount_amount),0) " +
												"from RentalTransactionModel a join a.inventory_details_list b where " +
												"a.office.id=:office and a.date <:start and a.customer.id=:ledger")
						.setParameter("office", office)
						.setParameter("start", start)
						.setParameter("ledger", ledger).uniqueResult();
			if(ob1!=null)
				sale=(Double)ob1;
			Object ob2=getSession().createQuery("select coalesce(sum(a.payment_amount),0) from RentalPaymentModel a where " +
												"a.office.id=:office and a.date <:start and a.from_account_id=:ledger")
						.setParameter("office", office)
						.setParameter("start", start)
						.setParameter("ledger", ledger).uniqueResult();
			if(ob2!=null)
				pay=(Double)ob2;
			balance=sale-pay;
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
		return balance;
	}
	
}
