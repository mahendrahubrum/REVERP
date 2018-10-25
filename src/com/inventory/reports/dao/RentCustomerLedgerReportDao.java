package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
import com.inventory.rent.model.RentPaymentModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.bean.TimewisereportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;



/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 16, 2014
 */
public class RentCustomerLedgerReportDao extends SHibernate implements Serializable{

	Calendar calendar;
	int leap=0;
	boolean issueLeap=false,returnLeap=false,currentLeap=false;
	long currentTime=0,issueTime=0,returnTime=0,diffTime=0;
	int issueYear,issueMonth,issueDay,issueMaxDays,issueBalanceDays;
	int currentYear,currentMonth,currentDay,currentMaxDays,currentBalanceDays;
	Calendar issueCalendar, returnCalendar;
	
	private static final long serialVersionUID = -5056109495741681L;

	private List resultList = new ArrayList();
	
	public List getRentCustomerReport(long org_id, long off_id,long customer_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(customer_id !=0){
			condition += "customer.id=" +customer_id+ " and ";
		}
		
		
		try {
			
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where " +condition+ "date between :fdate and :tdate")
					  .setParameter("fdate", from_date)
					  .setParameter("tdate", to_date)
					.list();
			        
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
	
	public Date getReturnedDate(long id) throws Exception
	{
		Date date=null;
		try
		{
			begin();
			Object obj=getSession().createQuery("select returned_date from RentInventoryDetailsModel where returned_status=:status and id=:id").
			setParameter("status", "Returned").setParameter("id", id).uniqueResult();
			date=(Date)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return date;
	}
	
	public List getRentDetailsReport(long org_id, long off_id,long customer_id) throws Exception 
	{
		String condition = "";
		if(org_id !=0)
		{
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0)
		{
			condition += "office.id=" +off_id+ " and ";
		}
		if(customer_id !=0)
		{
			condition += "customer.id=" +customer_id;
		}
		try 
		{
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where " +condition).list();
			commit();
		}
		catch (Exception e) 
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally 
		{
			flush();
			close();
		}
		return resultList;
	}
	
	public List getStaffWiseRentDetailsReport(long org_id, long off_id,long staff_id,long customer_id,Date date) throws Exception 
	{
		String condition = "";
		if(org_id !=0)
		{
			condition += " and office.organization.id="  +org_id; 
		}
		if(off_id !=0)
		{
			condition += " and office.id=" +off_id;
		}
		if(staff_id !=0)
		{
			condition += " and responsible_person=" +staff_id;
		}
		if(customer_id !=0)
		{
			condition += " and customer.id=" +customer_id;
		}
		try 
		{
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where 1=1 and month <=:date" +condition).setParameter("date", date).list();
			commit();
		}
		catch (Exception e) 
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally 
		{
			flush();
			close();
		}
		return resultList;
	}

	public String getStaffNameFromLoginID(long id)throws Exception{
		String staff=null;
		try{
			begin();
			staff=(String)getSession().createQuery("selct first_name from UserModel where loginId.id=:id").setParameter("id", id).uniqueResult();
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
		return staff;
	}
	
	public String getCustomerNameFromLedgerID(long id)throws Exception{
		String customer=null;
		try{
			begin();
			Object obj=getSession().createQuery("selct name from LedgerModel where id=:id").setParameter("id", id).uniqueResult();
			if(obj!=null)
				customer=(String)obj;
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
		return customer;
	}
	
	public List getRentCustomerReportfortimewise(long org_id, long off_id,long customer_id,long rent_id, Date from_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(customer_id !=0){
			condition += "customer.id=" +customer_id+ " and ";
		}
		
		
		
try {
			
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where " +condition+ "id=:rent and"+
					" date >= :fdate")
					
					  .setParameter("fdate", from_date)
					  .setLong("rent", rent_id)
					  
					.list();
			        
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
	
	public List getCustomerledgerReport(long org_id, long off_id,long customer_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(customer_id !=0){
			condition += "customer.id=" +customer_id+ " and ";
		}
		
		
try {
			
			begin();
			resultList = getSession().createQuery("from RentPaymentModel where " +condition+ "date between :fdate and :tdate")
					  .setParameter("fdate", from_date)
					  .setParameter("tdate", to_date)
					.list();
			        
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
	
	public List getCustomerLedgerReport(Date date,int interval, int noOfintrvls, long office_id, long customer) throws Exception {
		try {
			List<Double> intrvalList;
			Calendar cal=Calendar.getInstance();
			Date start_date;
			TimewisereportBean rptObj;
			boolean valid=false;
			
			resultList=new ArrayList();
			
			begin();
			Iterator itr = getSession().createQuery(
							"select new com.inventory.rent.model.RentDetailsModel(a.id,cast(a.rent_number as string))"
									+ " from RentDetailsModel a where a.customer.id=:cus order by a.rent_number desc")
									.setLong("cus", customer)
									.list().iterator();
			RentDetailsModel obj;
			
			double sale=0, cash=0, ret=0, todayBal=0, openingBal=0;
			while(itr.hasNext()) {
				
				cal.setTime(date);
				start_date=date;
				
				obj=(RentDetailsModel) itr.next();
				
				sale=0; cash=0; ret=0;todayBal=0;openingBal=0;
				
				sale=(Double) getSession().createQuery("select coalesce(sum(amount-totalpaidamt),0) from RentDetailsModel where date <=:stdt and customer.id=:led and id=:rent" +
											"").setParameter("led", customer)
											.setParameter("rent", obj.getId())
											.setDate("stdt", start_date).uniqueResult();
				valid=false;
				if(sale!=0 || cash!=0 || ret!=0)
					valid=true;
				rptObj=new TimewisereportBean( Long.toString(obj.getRent_number()),obj.getAmount(), obj.getTotalpaidamt());
				sale=0;cash=0;ret=0;
				intrvalList=new ArrayList<Double>();
				for (int i = 0; i < noOfintrvls; i++) 
				{
					cal.add(Calendar.DAY_OF_MONTH, interval);
					start_date=new Date(cal.getTime().getTime());
					sale=(Double) getSession().createQuery("select coalesce(sum(amount-totalpaidamt),0) from RentDetailsModel where date <=:stdt and customer.id=:led and id=:rent " +
							"").setParameter("led", customer)
							.setParameter("rent", obj.getId()).setDate("stdt", start_date).uniqueResult();
					intrvalList.add(sale-cash-ret);
					if(sale!=0 || cash!=0 || ret!=0)
						valid=true;
				}
				rptObj.setSubList(intrvalList);
				if(valid)
					resultList.add(rptObj);
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getRentDetails(Date date,long customer)throws Exception
	{
		List rentList = null;
		Calendar cal=Calendar.getInstance();
		Date startDate=null;
		try
		{
			begin();
			cal.setTime(date);
			startDate=date;
			rentList = getSession().createQuery("select a from RentDetailsModel a where a.date<=:date and a.customer.id=:cid")
					.setParameter("date", startDate).setParameter("cid", customer).list();
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return rentList;
		
	}
	
	public double getReturnedItemsAmount(long id,Date start,Date end) throws Exception
	{
		double amount=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select total_amount from RentReturnItemDetailModel where id=( select MAX(id) from RentReturnItemDetailModel where rent_inventory_id=:id and return_date between :start and :end)").
						setParameter("id", id).setParameter("start", start).setParameter("end", end).uniqueResult();
			
			if(obj!=null)
				amount=(Double)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return amount;
	}
	
	public double getTotalPaidAmountTillDate(long id,Date start,Date end) throws Exception
	{
		double amount=0;
		try
		{
			begin();
			Object obj=getSession().createQuery("select coalesce(sum(payment_amount),0) from RentPaymentModel where customer.id=:id and date between :start and :end ").
			setParameter("id", id).setParameter("start", start).setParameter("end", end).uniqueResult();
			if(obj!=null)
			amount=(Double)obj;
			commit();
		}
		catch(Exception e)
		{
			rollback();
			close();
			e.printStackTrace();
		}
		finally
		{
			flush();
			close();
		}
		return amount;
	}
	
}