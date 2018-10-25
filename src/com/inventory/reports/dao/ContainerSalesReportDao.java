package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Mar 26, 2014
 */
public class ContainerSalesReportDao extends SHibernate implements Serializable {

	private static final long serialVersionUID = 5313659691928223443L;
	
	WrappedSession session = new SessionUtil().getHttpSession();

	SettingsValuePojo settings = (SettingsValuePojo) session.getAttribute("settings");

	public List getAllContainerNumbers(Long officeId) throws Exception {
		List resultList;
		try {
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.commissionsales.model.CommissionSalesModel(id,contr_no) from CommissionSalesModel where office.id=:id")
					.setParameter("id", officeId).list();
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
	
	
	public List<Object> getContainerSalesReport(long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			

			String condition1 = " and a.office.id="+officeId, condition2="";
				
			if (custId  != 0) {
				condition2 += " and b.customer.id=" + custId;
				
//				begin();
//				LedgerModel itemObj=(LedgerModel) getSession().get(LedgerModel.class, custId);
//				commit();
				
				resultList=getSItemWise(fromDate, toDate, condition1+condition2);
				
			}
			else {
				
				begin();
				List itemsList= getSession()
						.createQuery(
								" from LedgerModel  where office.id=:ofc and group.id=:typ")
						.setParameter("ofc", officeId)
						.setParameter("typ",settings.getCUSTOMER_GROUP())
						.list();
				
				commit();
				
				LedgerModel ledg=null;
				
				Iterator it=itemsList.iterator();
				while(it.hasNext()) {
					ledg=(LedgerModel) it.next();
					
					condition2 = " and b.customer.id=" + ledg.getId();
					resultList.addAll(getSItemWise(fromDate, toDate, condition1+condition2));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	
	public List getSItemWise(Date fromDate, Date toDate, String condition) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery("select new com.webspark.bean.ReportBean" +
					"(b.customer.name, cast (a.sales_no as string) ,cast(a.date as string),b.unit.symbol, (select contr_no from CommissionSalesModel where id=a.containerId),b.qunatity,(b.qunatity*b.unit_price))" +
					" from CustomerCommissionSalesModel a join a.details_list b" +
					" where date between :fromDate and :toDate "
							+ condition+" order by a.date")
			.setParameter("fromDate", fromDate)
			.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} 

		return list;
	}
	
	public List<Object> getCommissionSalesReport(long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition = "";
				
			if (custId  != 0) 
				condition += " and customer.id=" + custId;
				
				begin();
				resultList= getSession()
						.createQuery(
								" from CommissionSalesNewModel  where office.id=:ofc and date between :frmdate and :todate "+condition)
						.setParameter("ofc", officeId)
						.setParameter("frmdate",fromDate)
						.setParameter("todate",toDate)
						.list();
				
				commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
}
