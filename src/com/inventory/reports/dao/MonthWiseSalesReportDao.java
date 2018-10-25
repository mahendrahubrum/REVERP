package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.MonthWiseSaleBean;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 18, 2015
 */

@SuppressWarnings("serial")
public class MonthWiseSalesReportDao extends SHibernate implements Serializable {
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getMonthlySaleDetails(long customer,Date start,Date end,long office) throws Exception {
		
		List resultList=new ArrayList();
		
		try {
			String condition="";
			begin();
			if(customer!=0)
				condition+=" and customer.id="+customer;
			List saleList=getSession().createQuery("from SalesModel where date between :start and :end "+condition+" and office.id=:office and (type=0 or type=1)")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			if(saleList.size()>0){
				Iterator itr=saleList.iterator();
				double amount=0;
				while (itr.hasNext()) {
					SalesModel mdl=(SalesModel)itr.next();
					amount+=CommonUtil.roundNumber(mdl.getAmount());
				}
				resultList.add(new MonthWiseSaleBean(SConstants.SALES, "Sales", CommonUtil.roundNumber(amount)));
			}
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
		
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getMonthlyPurchaseDetails(long supplier,Date start,Date end,long office) throws Exception {
		
		List resultList=new ArrayList();
		
		try {
			begin();
			String condition="";
			if(supplier!=0)
				condition+=" and supplier.id="+supplier;
			List saleList=getSession().createQuery("from PurchaseModel where date between :start and :end "+condition+" and office.id=:office")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			if(saleList.size()>0){
				Iterator itr=saleList.iterator();
				double amount=0;
				while (itr.hasNext()) {
					PurchaseModel mdl=(PurchaseModel)itr.next();
					amount+=CommonUtil.roundNumber(mdl.getAmount());
				}
				resultList.add(new MonthWiseSaleBean(SConstants.PURCHASE, "Purchase", CommonUtil.roundNumber(amount)));
			}
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getDetailedMonthlySalesDetails(long customer,Date start,Date end,long office)  throws Exception {
		
		List resultList=new ArrayList();
		
		try {
			String condition="";
			begin();
			if(customer!=0)
				condition+=" and customer.id="+customer;
			List saleList=getSession().createQuery("from SalesModel where date between :start and :end "+condition+" and office.id=:office and (type=0 or type=1)")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			if(saleList.size()>0){
				Iterator itr=saleList.iterator();
				while (itr.hasNext()) {
					SalesModel mdl=(SalesModel)itr.next();
					String item="";
					Iterator it=mdl.getInventory_details_list().iterator();
					while (it.hasNext()) {
						SalesInventoryDetailsModel det=(SalesInventoryDetailsModel)it.next();
						item+=det.getItem().getName()+" (Quantity : "+CommonUtil.roundNumber(det.getQunatity())+
														" , Rate : "+CommonUtil.roundNumber(det.getUnit_price())+"), ";
					}
					
					resultList.add(new MonthWiseSaleBean(CommonUtil.roundNumber(mdl.getAmount()),
														mdl.getId(),
														mdl.getSales_number()+"",
														CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
														mdl.getCustomer().getName(),
														item,
														SConstants.SALES));
				}
				
			}
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getDetailedMonthlyPurchaseDetails(long supplier,Date start,Date end,long office)  throws Exception {
		
		List resultList=new ArrayList();
		
		try {
			String condition="";
			begin();
			if(supplier!=0)
				condition+=" and supplier.id="+supplier;
			List saleList=getSession().createQuery("from PurchaseModel where date between :start and :end "+condition+" and office.id=:office")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
			if(saleList.size()>0){
				Iterator itr=saleList.iterator();
				while (itr.hasNext()) {
					PurchaseModel mdl=(PurchaseModel)itr.next();
					String item="";
					Iterator it=mdl.getPurchase_details_list().iterator();
					while (it.hasNext()) {
						PurchaseInventoryDetailsModel det=(PurchaseInventoryDetailsModel)it.next();
						item+=det.getItem().getName()+" (Quantity : "+CommonUtil.roundNumber(det.getQunatity())+
								" , Rate : "+CommonUtil.roundNumber(det.getUnit_price())+"), ";
					}
					resultList.add(new MonthWiseSaleBean(CommonUtil.roundNumber(mdl.getAmount()),
														mdl.getId(),
														mdl.getPurchase_no()+"",
														CommonUtil.formatDateToDDMMYYYY(mdl.getDate()),
														mdl.getSupplier().getName(),
														item,
														SConstants.PURCHASE));
				}
			}
			commit();
		}
		catch(Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
}
